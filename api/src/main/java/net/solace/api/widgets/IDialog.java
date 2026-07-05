package net.solace.api.widgets;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.solace.api.commons.Predicates;
import net.solace.api.domain.widgets.IWidget;

public interface IDialog {
    public void continueTutorial();

    public boolean isEnterInputOpen();

    public boolean isOpen();

    public void input(int var1, String var2);

    default public void enterFriendName(String input) {
        this.input(2, input);
    }

    default public void enterChatChannelName(String input) {
        this.input(10, input);
    }

    default public void enterName(String input) {
        this.input(8, input);
    }

    default public void enterText(String input) {
        this.input(9, input);
    }

    default public void enterAmount(int input) {
        this.input(7, String.valueOf(input));
    }

    default public boolean hasOption(String option) {
        return this.hasOption(Predicates.texts(option));
    }

    default public boolean hasOption(Predicate<String> option) {
        return this.getOption(option) != null;
    }

    default public IWidget getOption(String option) {
        return this.getOption(Predicates.texts(option));
    }

    default public IWidget getOption(Predicate<String> option) {
        return this.getOptions().stream().filter(Objects::nonNull).filter(widget -> option.test(widget.getText())).findAny().orElse(null);
    }

    default public boolean chooseOption(String ... options) {
        if (this.isViewingOptions()) {
            for (int i = 0; i < this.getOptions().size(); ++i) {
                IWidget widget = this.getOptions().get(i);
                for (String option : options) {
                    if (!widget.getText().contains(option)) continue;
                    return this.chooseOption(i + 1);
                }
            }
        }
        return false;
    }

    default public boolean chooseOption(Predicate<String> option) {
        if (this.isViewingOptions()) {
            for (int i = 0; i < this.getOptions().size(); ++i) {
                IWidget widget = this.getOptions().get(i);
                if (!option.test(widget.getText())) continue;
                return this.chooseOption(i + 1);
            }
        }
        return false;
    }

    public boolean chooseOption(int var1);

    public boolean canContinue();

    public void continueSpace();

    default public boolean isViewingOptions() {
        return !this.getOptions().isEmpty();
    }

    public IWidget getOptionTitle();

    public List<IWidget> getOptions();

    public void forceOpen();

    public void forceClose();

    public String getText();

    public String getName();
}

