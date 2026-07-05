package net.solace.sdk.widgets;

import java.util.List;
import java.util.function.Predicate;
import net.solace.api.Static;
import net.solace.api.domain.widgets.IWidget;
import net.solace.api.widgets.IDialog;

public class Dialog {
    private static final IDialog DIALOG = Static.getDialog();

    public static void continueTutorial() {
        DIALOG.continueTutorial();
    }

    public static boolean isOpen() {
        return DIALOG.isOpen();
    }

    public static boolean canContinue() {
        return DIALOG.canContinue();
    }

    public static boolean isEnterInputOpen() {
        return DIALOG.isEnterInputOpen();
    }

    public static void enterFriendName(String input) {
        DIALOG.enterFriendName(input);
    }

    public static void enterChatChannelName(String input) {
        DIALOG.enterChatChannelName(input);
    }

    public static void enterName(String input) {
        DIALOG.enterName(input);
    }

    public static void enterText(String input) {
        DIALOG.enterText(input);
    }

    public static void enterAmount(int input) {
        DIALOG.enterAmount(input);
    }

    public static void input(int inputType, String value) {
        DIALOG.input(inputType, value);
    }

    public static boolean isViewingOptions() {
        return DIALOG.isViewingOptions();
    }

    public static void continueSpace() {
        DIALOG.continueSpace();
    }

    public static boolean chooseOption(int index) {
        return DIALOG.chooseOption(index);
    }

    public static boolean chooseOption(String ... options) {
        return DIALOG.chooseOption(options);
    }

    public static boolean chooseOption(Predicate<String> option) {
        return DIALOG.chooseOption(option);
    }

    public static boolean hasOption(String option) {
        return DIALOG.hasOption(option);
    }

    public static boolean hasOption(Predicate<String> option) {
        return DIALOG.hasOption(option);
    }

    public static IWidget getOption(String option) {
        return DIALOG.getOption(option);
    }

    public static IWidget getOption(Predicate<String> option) {
        return DIALOG.getOption(option);
    }

    public static IWidget getOptionTitle() {
        return DIALOG.getOptionTitle();
    }

    public static List<IWidget> getOptions() {
        return DIALOG.getOptions();
    }

    public static void forceOpen() {
        DIALOG.forceOpen();
    }

    public static void forceClose() {
        DIALOG.forceClose();
    }

    public static String getText() {
        return DIALOG.getText();
    }

    public static String getName() {
        return DIALOG.getName();
    }
}

