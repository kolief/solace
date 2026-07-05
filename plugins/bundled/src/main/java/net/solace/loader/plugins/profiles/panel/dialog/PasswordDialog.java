package net.solace.loader.plugins.profiles.panel.dialog;

import lombok.Getter;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JToggleButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class PasswordDialog extends BaseDialog {
    private final JPasswordField passwordField;
    @Getter
    private boolean confirmed = false;

    public PasswordDialog(Frame owner, String title) {
        super(owner, title);

        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1.0;

        passwordField = new JPasswordField(20);
        JPanel passPanel = new JPanel(new BorderLayout(5, 0));
        JToggleButton toggleButton = new JToggleButton("Show");
        toggleButton.setPreferredSize(new Dimension(60, passwordField.getPreferredSize().height));

        passPanel.add(passwordField, BorderLayout.CENTER);
        passPanel.add(toggleButton, BorderLayout.EAST);

        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(new JLabel("Password:"), c);
        c.gridx = 1;
        mainPanel.add(passPanel, c);

        toggleButton.addActionListener(e -> {
            if (toggleButton.isSelected()) {
                passwordField.setEchoChar((char) 0);
                toggleButton.setText("Hide");
            } else {
                passwordField.setEchoChar('•');
                toggleButton.setText("Show");
            }
        });

        JButton confirmButton = new JButton("Confirm");
        JButton cancelButton = new JButton("Cancel");

        confirmButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        passwordField.addActionListener(e -> confirmButton.doClick());

        finishDialog();
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

}
