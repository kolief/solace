package net.solace.sdk.input;

import java.awt.Canvas;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import net.solace.api.commons.Rand;
import net.solace.sdk.commons.Time;
import net.solace.sdk.game.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Mouse {
    public static final Supplier<Point> CLICK_POINT_SUPPLIER = () -> new Point(Rand.nextInt((int)520, (int)568), Rand.nextInt((int)55, (int)70));
    private static final Logger log = LoggerFactory.getLogger(Mouse.class);
    private static final int MENU_REPLACE_DELAY = 80;
    private static final Executor CLICK_EXECUTOR = Executors.newSingleThreadExecutor();
    private static boolean exited = true;

    public static void click(int x, int y, boolean left) {
        if (Client.isClientThread()) {
            CLICK_EXECUTOR.execute(() -> Mouse.handleClick(x, y, left));
        } else {
            Mouse.handleClick(x, y, left);
        }
    }

    private static void handleClick(int x, int y, boolean left) {
        long sleep;
        long start = System.currentTimeMillis();
        Canvas canvas = Client.getCanvas();
        if (exited) {
            Mouse.entered(x, y, canvas, System.currentTimeMillis());
        }
        Mouse.moved(x, y, canvas, System.currentTimeMillis());
        Time.sleep(2, 30);
        Mouse.pressed(x, y, canvas, System.currentTimeMillis(), left ? 1 : 3);
        Time.sleep(2, 30);
        long currTime = System.currentTimeMillis();
        Mouse.released(x, y, canvas, currTime, left ? 1 : 3);
        Mouse.clicked(x, y, canvas, currTime, left ? 1 : 3);
        if (Rand.nextBool() && !exited) {
            Mouse.exited(x, y, canvas, System.currentTimeMillis());
        }
        if ((sleep = 80L - (System.currentTimeMillis() - start)) > 0L) {
            Time.sleep(sleep);
        } else {
            Time.sleep(80L);
        }
    }

    public static void click(Point point, boolean left) {
        Mouse.click((int)point.getX(), (int)point.getY(), left);
    }

    public static void clickRandom(boolean left) {
        Mouse.click(CLICK_POINT_SUPPLIER.get(), left);
    }

    public static synchronized void pressed(int x, int y, Canvas canvas, long time, int button) {
        MouseEvent event = new MouseEvent(canvas, 501, time, 0, x, y, 1, false, button);
        canvas.dispatchEvent(event);
    }

    public static synchronized void released(int x, int y, Canvas canvas, long time, int button) {
        MouseEvent event = new MouseEvent(canvas, 502, time, 0, x, y, 1, false, button);
        canvas.dispatchEvent(event);
    }

    public static synchronized void clicked(int x, int y, Canvas canvas, long time, int button) {
        MouseEvent event = new MouseEvent(canvas, 500, time, 0, x, y, 1, false, button);
        canvas.dispatchEvent(event);
    }

    public static synchronized void released(int x, int y, Canvas canvas, long time) {
        MouseEvent event = new MouseEvent(canvas, 502, time, 0, x, y, 1, false);
        canvas.dispatchEvent(event);
    }

    public static synchronized void clicked(int x, int y, Canvas canvas, long time) {
        MouseEvent event = new MouseEvent(canvas, 500, time, 0, x, y, 1, false);
        canvas.dispatchEvent(event);
    }

    public static synchronized void exited(int x, int y, Canvas canvas, long time) {
        MouseEvent event = new MouseEvent(canvas, 505, time, 0, x, y, 0, false);
        canvas.dispatchEvent(event);
        exited = true;
    }

    public static synchronized void entered(int x, int y, Canvas canvas, long time) {
        MouseEvent event = new MouseEvent(canvas, 504, time, 0, x, y, 0, false);
        canvas.dispatchEvent(event);
        exited = false;
    }

    public static synchronized void moved(int x, int y, Canvas canvas, long time) {
        MouseEvent event = new MouseEvent(canvas, 503, time, 0, x, y, 0, false);
        canvas.dispatchEvent(event);
    }
}

