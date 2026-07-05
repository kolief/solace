package net.solace.loader.plugins.profiles.panel.dialog;

import javax.swing.JDialog;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;

public abstract class BaseDialog extends JDialog {
    protected final JPanel mainPanel;
    protected final JPanel buttonPanel;

    protected BaseDialog(Frame owner, String title) {
        super(owner, title, true);
        setLayout(new BorderLayout());

        mainPanel = new JPanel();
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    protected void finishDialog() {
        pack();
        setLocationRelativeTo(getOwner());
    }
}