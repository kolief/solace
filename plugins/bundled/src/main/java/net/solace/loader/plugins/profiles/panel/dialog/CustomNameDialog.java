package net.solace.loader.plugins.profiles.panel.dialog;

import net.solace.loader.plugins.profiles.data.AccountData;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextField;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class CustomNameDialog extends BaseDialog {
    private final AccountData account;
    private final JTextField nameField;
    private boolean confirmed = false;

    public CustomNameDialog(Frame owner, AccountData account) {
        super(owner, "Import Account");
        this.account = account;

        mainPanel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1.0;

        // Account info preview
        c.gridx = 0;
        c.gridy = 0;
        mainPanel.add(new JLabel("Display Name:"), c);
        c.gridx = 1;
        mainPanel.add(new JLabel(account.getName()), c);

        // Custom name field
        c.gridx = 0;
        c.gridy = 1;
        mainPanel.add(new JLabel("Custom Name (optional):"), c);
        c.gridx = 1;
        nameField = new JTextField(20);
        mainPanel.add(nameField, c);

        JButton importButton = new JButton("Import");
        JButton cancelButton = new JButton("Cancel");

        importButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(importButton);
        buttonPanel.add(cancelButton);

        finishDialog();
    }

    public String getCustomName() {
        return nameField.getText().trim();
    }

    public boolean isConfirmed() {
        return confirmed;
    }
}