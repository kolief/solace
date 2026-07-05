package net.solace.loader.events;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.GameState;
import net.runelite.api.NPC;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.account.GameAccount;
import net.solace.api.containers.NpcContainer;
import net.solace.api.containers.PlayerContainer;
import net.solace.api.domain.actors.IActor;
import net.solace.api.domain.game.IClient;
import net.solace.api.events.ExperienceGained;
import net.solace.api.events.InventoryChanged;
import net.solace.api.events.ItemObtained;
import net.solace.api.game.IGame;
import net.solace.api.game.IWorlds;
import org.jboss.aerogear.security.otp.Totp;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Singleton
public class EventManager {
    private final int[] itemIdCache = new int[28];
    private final int[] itemQuantityCache = new int[28];
    private final Map<Skill, Integer> previousExp = new HashMap<>(23);

    @Inject
    private IClient client;

    @Inject
    private IGame game;

    @Inject
    private PlayerContainer playerContainer;

    @Inject
    private NpcContainer npcContainer;

    @Inject
    private EventBus eventBus;

    @Inject
    private IWorlds worlds;

    @Inject
    @Named("rsAccount")
    @Nullable
    private String rsAccount;

    @Inject
    @Named("world")
    @Nullable
    private Integer world;

    public void init() {
        if (rsAccount != null) {
            var account = parseAccount(rsAccount);
            if (account != null) {
                game.setGameAccount(account);
            }
        }

        setGameAccount(client.getGameState());

        if (world != null) {
            var first = worlds.getFirst(world);
            if (first != null) {
                client.changeWorld(first);
            }
        }
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged event) {
        setGameAccount(event.getGameState());

        if (event.getGameState() != GameState.LOGGED_IN && event.getGameState() != GameState.LOADING) {
            previousExp.clear();
        }
    }

    private void setGameAccount(GameState gameState) {
        var gameAccount = game.getGameAccount();

        if (gameAccount != null) {
            if (gameAccount.isJagexLauncher()) {
                if (!client.isOAuthCredentialsSet()) {
                    client.setSessionId(gameAccount.getUsername());
                    client.setCharacterId(gameAccount.getPassword());
                    client.setDisplayName(gameAccount.getDisplayName());
                }

                client.setLoginIndex(10);
            }

            switch (gameState) {
                case LOGIN_SCREEN:
                    if (gameAccount.getUsername() != null && gameAccount.getPassword() != null) {
                        client.setUsername(gameAccount.getUsername());
                        client.setPassword(gameAccount.getPassword());
                    } else {
                        log.warn("No username or password set for game account");
                    }

                    break;
                case LOGIN_SCREEN_AUTHENTICATOR:
                    if (gameAccount.getAuth() != null) {
                        client.setOtp(new Totp(gameAccount.getAuth()).now());
                    } else {
                        log.warn("No authenticator set for game account");
                    }

                    break;
            }
        }
    }

    @Subscribe
    public void onItemContainerChanged(ItemContainerChanged event) {
        var startMs = System.currentTimeMillis();
        var callbacks = client.getWrapped().getCallbacks();
        if (callbacks == null) {
            return;
        }

        if (event.getContainerId() != InventoryID.INV) {
            return;
        }

        var changedContainer = event.getItemContainer();
        // Force update inventory widget
        client.runScript(6009, 9764864, 28, 1, -1);

        var items = changedContainer.getItems();
        for (var i = 0; i < 28; i++) {
            var oldId = itemIdCache[i];
            var oldStack = itemQuantityCache[i];
            var newId = items.length <= i ? -1 : items[i].getId();
            var newStack = items.length <= i ? 0 : items[i].getQuantity();

            itemIdCache[i] = newId;
            itemQuantityCache[i] = newStack;


            if (oldId == newId) {
                if (oldStack > newStack) {
                    var inventoryChanged = new InventoryChanged(InventoryChanged.ChangeType.ITEM_REMOVED, newId, Math.abs(oldStack - newStack));
                    callbacks.postDeferred(inventoryChanged);
                    continue;
                }

                if (oldStack < newStack) {
                    var amount = Math.abs(oldStack - newStack);
                    var inventoryChanged = new InventoryChanged(InventoryChanged.ChangeType.ITEM_ADDED, newId, amount);
                    callbacks.postDeferred(inventoryChanged);
                    callbacks.postDeferred(new ItemObtained(newId, amount));
                    continue;
                }
            }

            if (oldId != newId) {
                if (oldId > 0) {
                    var itemRemoved = new InventoryChanged(InventoryChanged.ChangeType.ITEM_REMOVED, oldId, oldStack);
                    callbacks.postDeferred(itemRemoved);
                }

                if (newId > 0 && oldId != 0) {
                    var itemAdded = new InventoryChanged(InventoryChanged.ChangeType.ITEM_ADDED, newId, newStack);
                    callbacks.postDeferred(itemAdded);
                    callbacks.postDeferred(new ItemObtained(newId, newStack));
                }
            }
        }

        log.trace("[EventManager] onItemContainerChanged took {} ms", System.currentTimeMillis() - startMs);
    }

    @Subscribe
    public void onStatChanged(StatChanged e) {
        var skill = e.getSkill();
        var previous = previousExp.get(skill);
        var exp = client.getSkillExperience(skill);
        if (previous == null) {
            previous = exp;
            previousExp.put(skill, exp);
        }

        var skillLevel = client.getRealSkillLevel(skill);
        if (exp > previous) {
            var gained = exp - previous;

            var experienceGained = new ExperienceGained(
                    skill,
                    gained,
                    exp,
                    skillLevel
            );

            eventBus.post(experienceGained);
            previousExp.put(skill, exp);
        }
    }

    @Subscribe
    public void onInteractingChanged(InteractingChanged e) {
        var actor = e.getSource();
        var target = e.getTarget();
        var source = createOrUpdateActor(actor);
        var targ = getCachedActor(target);
        var event = new net.solace.api.events.InteractingChanged(source, targ);
        eventBus.post(event);
    }


    private IActor getCachedActor(Actor rlActor) {
        if (rlActor == null) {
            return null;
        }

        if (rlActor instanceof Player) {
            return playerContainer.get(((Player) rlActor).getId());
        } else if (rlActor instanceof NPC) {
            return npcContainer.get(((NPC) rlActor).getIndex());
        }

        return null;
    }

    private IActor createOrUpdateActor(Actor rlActor) {
        if (rlActor == null) {
            return null;
        }

        if (rlActor instanceof Player) {
            return playerContainer.create((Player) rlActor);
        } else if (rlActor instanceof NPC) {
            return npcContainer.create((NPC) rlActor);
        }

        return null;
    }

    private GameAccount parseAccount(String accountString) {
        var parts = accountString.split(":");

        boolean isKeyValueFormat = parts.length > 0 &&
                parts[0].toLowerCase().startsWith("username=");

        if (isKeyValueFormat) {
            return parseKeyValueFormat(parts);
        } else {
            return parseLegacyFormat(parts);
        }
    }

    private GameAccount parseKeyValueFormat(String[] parts) {
        String username = null, password = null, auth = null, displayName = null, bankPin = null;

        for (String part : parts) {
            if (!part.contains("=")) {
                continue;
            }

            var keyValue = part.split("=", 2);
            if (keyValue.length != 2) {
                continue;
            }

            String key = keyValue[0].toLowerCase().trim();
            String value = keyValue[1].trim();

            switch (key) {
                case "username":
                    username = value;
                    break;
                case "password":
                    password = value;
                    break;
                case "display":
                    displayName = value;
                    break;
                case "auth":
                    auth = value;
                    break;
                case "pin":
                    bankPin = value;
                    break;
            }
        }

        if (username == null || password == null) {
            return null;
        }

        var account = new GameAccount(username, password);
        if (displayName != null) {
            account.setDisplayName(displayName);
        }

        if (auth != null) {
            account.setAuth(auth);
        }

        if (bankPin != null) {
            account.setBankPin(bankPin);
        }

        return account;
    }

    private GameAccount parseLegacyFormat(String[] parts) {
        if (parts.length < 2) {
            return null;
        }

        String username = parts[0].trim();
        String password = parts[1].trim();
        var account = new GameAccount(username, password);

        if (parts.length >= 3) {
            account.setAuth(parts[2].trim());
        }

        return account;
    }
}
