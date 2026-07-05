package net.solace.loader.plugins.profiles.panel.dialog;

import net.solace.loader.plugins.profiles.data.AccountData;
import net.solace.loader.plugins.profiles.data.AccountManager;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AccountSelectionDialog extends BaseDialog {
    private final Map<JCheckBox, AccountData> checkBoxMap = new HashMap<>();
    private final AccountManager accountManager;

    public AccountSelectionDialog(Frame owner, List<AccountData> accounts, AccountManager accountManager) {
        super(owner, "Select Accounts to Import");
        this.accountManager = accountManager;

        // Create list panel with checkboxes
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));

        for (AccountData account : accounts) {
            JCheckBox checkBox = new JCheckBox();
            String label = account.getType() == AccountData.AccountType.JL ?
                    "[JL] " + account.getDisplayName() :
                    account.getUsername();
            checkBox.setText(label);
            checkBox.setSelected(true);  // Select all by default
            checkBoxMap.put(checkBox, account);
            listPanel.add(checkBox);
        }

        JScrollPane scrollPane = new JScrollPane(listPanel);
        scrollPane.setPreferredSize(new Dimension(300, 200));

        mainPanel.setLayout(new BorderLayout());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JButton importButton = new JButton("Import Selected");
        JButton cancelButton = new JButton("Cancel");

        importButton.addActionListener(e -> handleImport());
        cancelButton.addActionListener(e -> dispose());

        buttonPanel.add(importButton);
        buttonPanel.add(cancelButton);

        finishDialog();
    }

    private void handleImport() {
        try {
            List<AccountData> selectedAccounts = checkBoxMap.entrySet().stream()
                    .filter(entry -> entry.getKey().isSelected())
                    .map(Map.Entry::getValue)
                    .collect(Collectors.toList());

            if (selectedAccounts.isEmpty()) {
                DialogUtil.showError(this,
                        "Import Failed",
                        "No accounts selected");
                return;
            }

            for (AccountData account : selectedAccounts) {
                try {
                    accountManager.validateAndAddAccount(account);
                } catch (IllegalArgumentException ex) {
                    DialogUtil.showWarning(this,
                            "Warning",
                            "Failed to import account '" + account.getName() + "': " + ex.getMessage());
                }
            }

            dispose();
        } catch (Exception ex) {
            DialogUtil.showError(this,
                    "Error",
                    "Failed to import accounts: " + ex.getMessage());
        }
    }
}