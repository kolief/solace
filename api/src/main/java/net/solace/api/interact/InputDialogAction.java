package net.solace.api.interact;

import net.solace.api.interact.Automation;

public final class InputDialogAction
implements Automation {
    private final int inputType;
    private final String inputText;

    public InputDialogAction(int inputType, String inputText) {
        this.inputType = inputType;
        this.inputText = inputText;
    }

    public int getInputType() {
        return this.inputType;
    }

    public String getInputText() {
        return this.inputText;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof InputDialogAction)) {
            return false;
        }
        InputDialogAction other = (InputDialogAction)o;
        if (this.getInputType() != other.getInputType()) {
            return false;
        }
        String this$inputText = this.getInputText();
        String other$inputText = other.getInputText();
        return !(this$inputText == null ? other$inputText != null : !this$inputText.equals(other$inputText));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getInputType();
        String $inputText = this.getInputText();
        result = result * 59 + ($inputText == null ? 43 : $inputText.hashCode());
        return result;
    }

    public String toString() {
        return "InputDialogAction(inputType=" + this.getInputType() + ", inputText=" + this.getInputText() + ")";
    }
}

