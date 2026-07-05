package net.solace.loader.plugins.bankpin;

import com.google.inject.Provides;
import net.runelite.api.events.ScriptPostFired;
import net.runelite.api.gameval.InterfaceID;
import net.runelite.client.eventbus.Subscribe;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.widgets.InterfaceAddress;
import net.solace.sdk.game.Client;
import net.solace.sdk.widgets.Widgets;

import javax.inject.Inject;

@PluginDescriptor(
        name = "Solace Bankpin"
)
public class SolaceBankPinPlugin extends Plugin {
    private static final InterfaceAddress[] BANK_PIN_NUMBERS =
            {
                    InterfaceAddress.BANK_PIN_10,
                    InterfaceAddress.BANK_PIN_1,
                    InterfaceAddress.BANK_PIN_2,
                    InterfaceAddress.BANK_PIN_3,
                    InterfaceAddress.BANK_PIN_4,
                    InterfaceAddress.BANK_PIN_5,
                    InterfaceAddress.BANK_PIN_6,
                    InterfaceAddress.BANK_PIN_7,
                    InterfaceAddress.BANK_PIN_8,
                    InterfaceAddress.BANK_PIN_9,
            };
    private static final InterfaceAddress BANK_PIN_FIRST_ENTERED = new InterfaceAddress(213, 3);
    private static final InterfaceAddress BANK_PIN_SECOND_ENTERED = new InterfaceAddress(213, 4);
    private static final InterfaceAddress BANK_PIN_THIRD_ENTERED = new InterfaceAddress(213, 5);
    private static final InterfaceAddress BANK_PIN_FOURTH_ENTERED = new InterfaceAddress(213, 6);

    @Inject
    private SolaceBankPinConfig config;

    @Inject
    private Client client;

    private static void clickNumber(Client client, int number) {
        for (var widgetInfo : BANK_PIN_NUMBERS) {
            var numberBox = Widgets.get(widgetInfo);
            if (!Widgets.isVisible(numberBox)) {
                continue;
            }

            if (numberBox.getChildren() == null || numberBox.getChildren().length < 2) {
                continue;
            }

            if (numberBox.getChild(1).getText().equals(String.valueOf(number))) {
                Client.invokeWidgetAction(1, numberBox.getChild(0).getId(), 0, -1, "Select");
                break;
            }
        }
    }

    @Subscribe
    private void onScriptEvent(ScriptPostFired e) {
        if (e.getScriptId() != 683) {
            return;
        }

        var bankPinContainer = Widgets.get(InterfaceID.BankpinKeypad.UNIVERSE);
        if (!Widgets.isVisible(bankPinContainer)) {
            return;
        }

        var pin = config.pin();
        if (!pin.matches("\\d{4}")) {
            return;
        }

        var pinSplit = pin.split("");
        var first = Widgets.get(BANK_PIN_FIRST_ENTERED);
        var second = Widgets.get(BANK_PIN_SECOND_ENTERED);
        var third = Widgets.get(BANK_PIN_THIRD_ENTERED);
        var fourth = Widgets.get(BANK_PIN_FOURTH_ENTERED);

        if (first.getText().equals("?")) {
            var number = Integer.parseInt(pinSplit[0]);
            clickNumber(client, number);
        } else if (second.getText().equals("?")) {
            var number = Integer.parseInt(pinSplit[1]);
            clickNumber(client, number);
        } else if (third.getText().equals("?")) {
            var number = Integer.parseInt(pinSplit[2]);
            clickNumber(client, number);
        } else if (fourth.getText().equals("?")) {
            var number = Integer.parseInt(pinSplit[3]);
            clickNumber(client, number);
        }
    }

    @Provides
    SolaceBankPinConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(SolaceBankPinConfig.class);
    }
}
