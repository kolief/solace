package net.solace.api.input;

public interface IKeyboard {
    public void type(char var1);

    public void sendKey(int var1);

    default public void type(String text, boolean sendEnter) {
        for (char c : text.toCharArray()) {
            this.type(c);
        }
        if (sendEnter) {
            this.sendEnter();
        }
    }

    default public void type(int number) {
        this.type(String.valueOf(number));
    }

    default public void type(String text) {
        this.type(text, false);
    }

    default public void sendEnter() {
        this.type('\n');
    }

    default public void sendSpace() {
        this.type(' ');
    }
}

