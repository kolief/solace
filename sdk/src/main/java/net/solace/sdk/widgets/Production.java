package net.solace.sdk.widgets;

import java.util.function.Predicate;
import net.solace.api.Static;
import net.solace.api.widgets.IProduction;
import net.solace.api.widgets.ProductionQuantity;

public class Production {
    private static final IProduction PRODUCTION = Static.getProduction();

    public static boolean isOpen() {
        return PRODUCTION.isOpen();
    }

    public static void chooseOption(String option) {
        PRODUCTION.chooseOption(option);
    }

    public static void chooseOption(Predicate<String> option) {
        PRODUCTION.chooseOption(option);
    }

    public static void selectOtherQuantity() {
        PRODUCTION.selectOtherQuantity();
    }

    public static void chooseOption(int index) {
        PRODUCTION.chooseOption(index);
    }

    public static void choosePreviousOption() {
        PRODUCTION.choosePreviousOption();
    }

    public static boolean isEnterInputOpen() {
        return PRODUCTION.isEnterInputOpen();
    }

    public static void enterAmount(int amount) {
        PRODUCTION.enterAmount(amount);
    }

    public static void enterName(String input) {
        PRODUCTION.enterName(input);
    }

    public static void selectItem(String name) {
        PRODUCTION.selectItem(name);
    }

    public static void selectItem(int itemId) {
        PRODUCTION.selectItem(itemId);
    }

    public static int getMakeXQuantity() {
        return PRODUCTION.getMakeXQuantity();
    }

    public static boolean selectMakeXQuantity(int quantity) {
        return PRODUCTION.selectMakeXQuantity(quantity);
    }

    public static ProductionQuantity getSelectedQuantity() {
        return PRODUCTION.getSelectedQuantity();
    }

    public static boolean selectQuantity(ProductionQuantity quantity) {
        return PRODUCTION.selectQuantity(quantity);
    }
}

