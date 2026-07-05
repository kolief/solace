package net.solace.api.util;

import java.awt.Component;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.SecondaryLoop;
import java.awt.Toolkit;
import java.lang.reflect.InvocationTargetException;
import javax.annotation.Nullable;
import javax.swing.AbstractButton;
import javax.swing.SwingUtilities;
import net.runelite.client.ui.Activatable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SwingUtil {
    private static final Logger log = LoggerFactory.getLogger(SwingUtil.class);

    public static void syncExec(Runnable r) throws InvocationTargetException, InterruptedException {
        if (EventQueue.isDispatchThread()) {
            r.run();
        } else {
            EventQueue.invokeAndWait(r);
        }
    }

    public static void removeButtonDecorations(AbstractButton button) {
        button.putClientProperty("FlatLaf.styleClass", "iconButton legacyIconButton");
    }

    public static void addModalTooltip(AbstractButton button, String on, String off) {
        button.setToolTipText(button.isSelected() ? on : off);
        button.addItemListener(l -> button.setToolTipText(button.isSelected() ? on : off));
    }

    public static void fastRemoveAll(Container c) {
        assert (SwingUtilities.isEventDispatchThread());
        c.invalidate();
        for (int i = 0; i < c.getComponentCount(); ++i) {
            Component ic = c.getComponent(i);
            if (ic instanceof Container) {
                SwingUtil.fastRemoveAll((Container)ic);
            }
            SwingUtil.pumpPendingEvents();
            ic.removeNotify();
        }
        c.removeAll();
    }

    public static void pumpPendingEvents() {
        EventQueue eq = Toolkit.getDefaultToolkit().getSystemEventQueue();
        if (eq.peekEvent() != null) {
            SecondaryLoop l = eq.createSecondaryLoop();
            SwingUtilities.invokeLater(l::exit);
            l.enter();
        }
    }

    public static void activate(@Nullable Object maybeActivatable) {
        if (maybeActivatable instanceof Activatable) {
            try {
                ((Activatable)maybeActivatable).onActivate();
            }
            catch (Exception e) {
                log.warn("uncaught exception in activate", (Throwable)e);
            }
        }
    }

    public static void deactivate(@Nullable Object maybeActivatable) {
        if (maybeActivatable instanceof Activatable) {
            try {
                ((Activatable)maybeActivatable).onDeactivate();
            }
            catch (Exception e) {
                log.warn("uncaught exception in deactivate", (Throwable)e);
            }
        }
    }
}

