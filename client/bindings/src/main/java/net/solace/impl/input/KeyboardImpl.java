package net.solace.impl.input;

import lombok.RequiredArgsConstructor;
import net.solace.api.commons.ITime;
import net.solace.api.domain.game.IClient;
import net.solace.api.input.IKeyboard;

import java.awt.event.KeyEvent;

@RequiredArgsConstructor
public class KeyboardImpl implements IKeyboard {
    private final IClient client;
    private final ITime time;

    @Override
    public void type(char c) {
        var canvas = client.getCanvas();
        var currentTime = System.currentTimeMillis();
        var keyCode = KeyEvent.getExtendedKeyCodeForChar(c);
        var pressed = new KeyEvent(canvas, KeyEvent.KEY_PRESSED, currentTime, 0, keyCode, c, KeyEvent.KEY_LOCATION_STANDARD);
        var typed = new KeyEvent(canvas, KeyEvent.KEY_TYPED, currentTime, 0, 0, c, KeyEvent.KEY_LOCATION_UNKNOWN);
        canvas.dispatchEvent(pressed);
        canvas.dispatchEvent(typed);
        time.sleep(10);
        var released = new KeyEvent(
                canvas,
                KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                0,
                keyCode,
                c,
                KeyEvent.KEY_LOCATION_STANDARD
        );

        canvas.dispatchEvent(released);
    }

    @Override
    public void sendKey(int keyCode) {
        var canvas = client.getCanvas();
        var currentTime = System.currentTimeMillis();
        var pressed = new KeyEvent(canvas, KeyEvent.KEY_PRESSED, currentTime, 0, keyCode, KeyEvent.CHAR_UNDEFINED);
        canvas.dispatchEvent(pressed);
        time.sleep(10);
        var released = new KeyEvent(
                canvas,
                KeyEvent.KEY_RELEASED,
                System.currentTimeMillis(),
                0,
                keyCode,
                KeyEvent.CHAR_UNDEFINED
        );
        canvas.dispatchEvent(released);
    }
}
