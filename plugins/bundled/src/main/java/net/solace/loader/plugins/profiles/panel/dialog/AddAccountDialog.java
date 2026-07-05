package net.solace.loader.plugins.profiles.panel.dialog;


import net.solace.loader.plugins.profiles.data.AccountData;
import net.solace.loader.plugins.profiles.data.AccountManager;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class AddAccountDialog extends BaseDialog {
    private final JComboBox<AccountData.AccountType> typeCombo;
    private final JTextField nameField;
    private final JTextField usernameField;
    private final JPasswordField passwordField;
    private final JTextField characterIdField;
    private final JTextField sessionIdField;
    private final JTextField displayNameField;
    private final JPanel cardPanel;

    public AddAccountDialog(Frame owner, AccountManager accountManager, Runnable refreshCallback) {
        super(owner, "Add Account");

        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1.0;

        // Account type selector
        typeCombo = new JComboBox<>(AccountData.AccountType.values());
        nameField = new JTextField(20);
        usernameField = new JTextField(20);
        passwordField = new JPasswordField(20);
        characterIdField = new JTextField(20);
        sessionIdField = new JTextField(20);
        displayNameField = new JTextField(20);

        c.gridx = 0;
        c.gridy = 0;
        inputPanel.add(new JLabel("Account Type:"), c);
        c.gridx = 1;
        inputPanel.add(typeCombo, c);

        c.gridx = 0;
        c.gridy = 1;
        inputPanel.add(new JLabel("Display Name:"), c);
        c.gridx = 1;
        inputPanel.add(nameField, c);

        // Normal account panel
        JPanel normalPanel = createNormalPanel();

        // JL account panel
        JPanel jlPanel = createJLPanel();

        // Add panels to card layout
        cardPanel = new JPanel(new CardLayout());
        cardPanel.add(normalPanel, "NORMAL");
        cardPanel.add(jlPanel, "JL");

        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 2;
        inputPanel.add(cardPanel, c);

        // Switch panels based on type selection
        typeCombo.addActionListener(e -> {
            CardLayout cl = (CardLayout) cardPanel.getLayout();
            cl.show(cardPanel, typeCombo.getSelectedItem().toString());
        });

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        saveButton.addActionListener(e -> {
            try {
                AccountData account;
                if (typeCombo.getSelectedItem() == AccountData.AccountType.NORMAL) {
                    account = new AccountData(
                            nameField.getText(),
                            usernameField.getText(),
                            new String(passwordField.getPassword())
                    );
                } else {
                    account = new AccountData(
                            nameField.getText(),
                            characterIdField.getText(),
                            sessionIdField.getText(),
                            displayNameField.getText()
                    );
                }

                accountManager.addAccount(account);
                refreshCallback.run();
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Failed to save account: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        finishDialog();
    }

    private JPanel createNormalPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1.0;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Username:"), c);
        c.gridx = 1;
        panel.add(usernameField, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("Password:"), c);
        c.gridx = 1;
        panel.add(passwordField, c);

        return panel;
    }

    private JPanel createJLPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(5, 5, 5, 5);
        c.weightx = 1.0;

        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Character ID:"), c);
        c.gridx = 1;
        panel.add(characterIdField, c);

        c.gridx = 0;
        c.gridy = 1;
        panel.add(new JLabel("Session ID:"), c);
        c.gridx = 1;
        panel.add(sessionIdField, c);

        c.gridx = 0;
        c.gridy = 2;
        panel.add(new JLabel("Display Name:"), c);
        c.gridx = 1;
        panel.add(displayNameField, c);

        return panel;
    }
}
