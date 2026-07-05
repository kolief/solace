package net.solace.loader.plugins.arceuuslibrary.domain;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.GameState;
import net.runelite.api.MenuAction;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.events.AnimationChanged;
import net.solace.sdk.entities.Players;
import net.solace.sdk.game.Client;
import net.solace.sdk.widgets.Dialog;
import net.solace.sdk.widgets.Widgets;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.solace.loader.plugins.arceuuslibrary.util.MovementHelper.lastFloor;

@Slf4j
@Singleton
public class LibraryListener {

    private static final int REGION = 6459;
    private static final Pattern BOOK_EXTRACTOR = Pattern.compile("'<col=0000ff>(.*)</col>'");
    private static final Pattern TAG_MATCHER = Pattern.compile("(<[^>]*>)");
    private WorldPoint lastBookshelfClick = null;
    private WorldPoint lastBookshelfAnimatedOn = null;

    @Inject
    private Library library;

    @Inject
    public LibraryListener() {
    }

    @Subscribe
    private void onGameStateChanged(GameStateChanged e) {
        if (library.isShouldHop() && e.getGameState() == GameState.HOPPING) {
            library.setShouldHop(false);
        }

        if (e.getGameState() == GameState.LOGIN_SCREEN) {
            library.reset();
        }
    }

    @Subscribe
    public void onMenuOptionClicked(MenuOptionClicked e) {
        if (e.getMenuAction() == MenuAction.GAME_OBJECT_FIRST_OPTION) {
            lastBookshelfClick = new WorldPoint(
                    e.getParam0() + Client.getTopLevelWorldView().getBaseX(),
                    e.getParam1() + Client.getTopLevelWorldView().getBaseY(),
                    Client.getPlane());
            log.debug("Clicked on bookshelf at {}", lastBookshelfClick);
        }
    }

    @Subscribe
    public void onAnimationChanged(AnimationChanged e) {
        if (e.getActor().equals(Players.getLocal()) && e.getActor().getAnimation() == AnimationID.LOOKING_INTO) {
            lastBookshelfAnimatedOn = lastBookshelfClick;
            log.debug("Animated on bookshelf at {}", lastBookshelfAnimatedOn);
        }
    }

    @Subscribe
    public void onChatMessage(ChatMessage e) {
        if (e.getType() == ChatMessageType.GAMEMESSAGE) {
            if (lastBookshelfAnimatedOn != null && e.getMessage().equals("You don't find anything useful here.")) {
                library.mark(lastBookshelfAnimatedOn, null);
                lastBookshelfAnimatedOn = null;
            }

            if (e.getMessage().contains("You hear the shifting of books due to a mysterious force...or are you just hearing things?")) {
                library.reset();
            }
        }
    }

    @Subscribe
    public void onGameTick(GameTick event) {
        boolean inRegion = LibraryPosition.getRegion(Players.getLocal().getWorldLocation()) == REGION;

        if (lastFloor != Players.getLocal().getPlane()) {
            lastFloor = Players.getLocal().getPlane();
        }

        if (!inRegion) return;

        if (lastBookshelfAnimatedOn != null && Dialog.isOpen()) {
            Widget itemDialog = Widgets.get(193, 0);
            if (itemDialog != null) {
                Book book = Book.byId(getWidget().getItemId());

                if (book != null) {
                    library.mark(lastBookshelfAnimatedOn, book);
                    lastBookshelfAnimatedOn = null;
                }
            }
        }

        var npcDialog = Widgets.get(WidgetInfo.DIALOG_NPC_NAME.getPackedId());

        if (npcDialog != null && Customer.getByName(npcDialog.getText()) != null && !npcDialog.getText().equals(Customer.HORPHIS.getName())) {
            Customer customer = Customer.getByName(npcDialog.getText());
            if (customer != null) {
                Widget npcDialogTest = Widgets.get(231, 6);
                String text = npcDialogTest.getText();
                Matcher m = BOOK_EXTRACTOR.matcher(text);
                if (m.find()) {
                    String bookName = TAG_MATCHER.matcher(m.group(1).replace("<br>", " ")).replaceAll("");
                    Book book = Book.byName(bookName);
                    if (book == null) {
                        return;
                    }

                    library.setCustomer(customer, book);
                    library.setLastCustomer(customer);
                } else if (text.contains("You can have this other book")
                           || text.contains("please accept a token of my thanks.")
                           || text.contains("Thanks, I'll get on with reading it.")
                           || text.contains("Thanks for finding my book.")
                           || text.contains("Thanks for finding the book.")
                           || text.contains("Thank you for finding my book.")
                ) {
                    library.setCustomer(null, null);
                }
            }
        }
    }

    private IWidget getWidget() {
        return Widgets.getAll(193).stream().filter(x -> x.getItemId() > 0).findFirst().orElse(null);
    }
}