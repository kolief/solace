package net.solace.sdk.input;

import java.awt.Canvas;
import java.awt.event.KeyEvent;
import net.solace.api.Static;
import net.solace.api.input.IKeyboard;
import net.solace.sdk.game.Client;

public class Keyboard {
    private static final IKeyboard KEYBOARD = Static.getKeyboard();

    public static void pressed(int keyCode) {
        Keyboard.pressed(keyCode, '\uffff');
    }

    public static void pressed(int keyCode, char keyChar) {
        Canvas canvas = Client.getCanvas();
        long time = System.currentTimeMillis();
        KeyEvent event = new KeyEvent(canvas, 401, time, 0, keyCode, keyChar, 1);
        canvas.dispatchEvent(event);
    }

    public static void typed(int keyCode) {
        Keyboard.typed(keyCode, '\uffff');
    }

    public static void typed(int keyCode, char keyChar) {
        Canvas canvas = Client.getCanvas();
        long time = System.currentTimeMillis();
        KeyEvent event = new KeyEvent(canvas, 400, time, 0, keyCode, keyChar, 0);
        canvas.dispatchEvent(event);
    }

    public static void released(int keyCode) {
        Keyboard.released(keyCode, '\uffff');
    }

    public static void released(int keyCode, char keyChar) {
        Canvas canvas = Client.getCanvas();
        long time = System.currentTimeMillis();
        KeyEvent event = new KeyEvent(canvas, 402, time, 0, keyCode, keyChar, 1);
        canvas.dispatchEvent(event);
    }

    public static void type(char c) {
        KEYBOARD.type(c);
    }

    public static void type(int number) {
        Keyboard.type(String.valueOf(number));
    }

    public static void type(String text) {
        Keyboard.type(text, false);
    }

    public static void type(String text, boolean sendEnter) {
        char[] chars;
        for (char c : chars = text.toCharArray()) {
            Keyboard.type(c);
        }
        if (sendEnter) {
            Keyboard.sendEnter();
        }
    }

    public static void sendKey(int keyCode) {
        KEYBOARD.sendKey(keyCode);
    }

    public static void sendEnter() {
        Keyboard.type('\n');
    }

    public static void sendSpace() {
        Keyboard.type(' ');
    }
}

