package net.solace.loader;

import net.runelite.client.RuneLite;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

public final class SolaceLauncher {
    private SolaceLauncher() {
    }

    public static void main(String[] args) {
        try {
            RuneLite.main(args);
            SolaceLoader.inject();
        } catch (Throwable t) {
            showFatal("Solace failed to start.", t);
            System.exit(1);
        }
    }

    static void showFatal(String message, Throwable t) {
        t.printStackTrace();
        if (SwingUtilities.isEventDispatchThread()) {
            JOptionPane.showMessageDialog(null, message + "\n\n" + t.getMessage(), "Solace", JOptionPane.ERROR_MESSAGE);
        } else {
            SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                    null, message + "\n\n" + t.getMessage(), "Solace", JOptionPane.ERROR_MESSAGE));
        }
    }
}
