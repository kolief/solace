package net.solace.loader.plugins.profiles.panel.dialog;

import lombok.extern.slf4j.Slf4j;

import javax.swing.JOptionPane;
import java.awt.Component;

@Slf4j
public class DialogUtil {
    public static void showError(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.ERROR_MESSAGE);
    }

    public static void showWarning(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.WARNING_MESSAGE);
    }

    public static void showSuccess(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(parent, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    public static boolean showConfirm(Component parent, String message, String title) {
        return JOptionPane.showConfirmDialog(parent,
                message,
                title,
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION;
    }

    public static String showInput(Component parent, String message, String title) {
        return JOptionPane.showInputDialog(parent,
                message,
                title,
                JOptionPane.PLAIN_MESSAGE);
    }
}