package net.solace.loader.plugins.profiles.panel;

import lombok.extern.slf4j.Slf4j;
import net.solace.api.Static;
import net.solace.loader.plugins.profiles.data.AccountData;
import net.solace.loader.plugins.profiles.data.AccountManager;
import net.solace.loader.plugins.profiles.data.JLUtil;
import net.solace.loader.plugins.profiles.panel.dialog.DialogUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;

@Slf4j
public class AccountPanel extends JPanel {
    private final AccountData account;
    private final AccountManager accountManager;
    private final Runnable onRefresh;
    private final JPanel expandedPanel;

    public AccountPanel(AccountData account, AccountManager accountManager, Runnable onRefresh) {
        this.account = account;
        this.accountManager = accountManager;
        this.onRefresh = onRefresh;

        this.expandedPanel = createExpandedPanel();
        setupPanel();
    }

    private void setupPanel() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEtchedBorder());
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        setMinimumSize(new Dimension(0, 40));
        setPreferredSize(new Dimension(0, 40));

        JButton mainButton = createMainButton();

        add(mainButton, BorderLayout.CENTER);
        add(expandedPanel, BorderLayout.SOUTH);
    }

    private JButton createMainButton() {
        String labelText = formatDisplayName();

        JButton mainButton = new JButton();
        mainButton.setLayout(new BorderLayout());
        mainButton.add(new JLabel(labelText), BorderLayout.CENTER);
        mainButton.add(new JLabel("▼"), BorderLayout.EAST);
        mainButton.setHorizontalAlignment(SwingConstants.LEFT);

        mainButton.addActionListener(e -> toggleExpanded(mainButton));

        return mainButton;
    }

    private String formatDisplayName() {
        String displayName = account.getName();
        if (account.getType() == AccountData.AccountType.JL) {
            return String.format("<html><font color='white'>[</font><font color='green'>JL</font><font color='white'>]</font> %s</html>", displayName);
        }
        return displayName;
    }

    private JPanel createExpandedPanel() {
        JPanel expandedPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 5));
        expandedPanel.setVisible(false);

        Dimension buttonSize = new Dimension(60, 25);

        JButton loginButton = new JButton("Login");
        JButton deleteButton = new JButton("Del");
        JButton renameButton = new JButton("Edit");

        loginButton.setPreferredSize(buttonSize);
        deleteButton.setPreferredSize(buttonSize);
        renameButton.setPreferredSize(buttonSize);

        loginButton.addActionListener(e -> handleLogin());
        deleteButton.addActionListener(e -> handleDelete());
        renameButton.addActionListener(e -> handleRename());

        expandedPanel.add(loginButton);
        expandedPanel.add(renameButton);
        expandedPanel.add(deleteButton);

        return expandedPanel;
    }

    private void toggleExpanded(JButton mainButton) {
        boolean isVisible = expandedPanel.isVisible();
        expandedPanel.setVisible(!isVisible);

        JLabel arrow = (JLabel) mainButton.getComponent(1);
        arrow.setText(!isVisible ? "▲" : "▼");

        int height = !isVisible ? 72 : 40;
        setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        setPreferredSize(new Dimension(0, height));

        revalidate();
        repaint();
    }

    private void handleLogin() {
        try {
            if (account.getType() == AccountData.AccountType.JL) {
                JLUtil.prepJlLogin();
                Static.getClient().setLoginIndex(10);
                JLUtil.setAccountToClient(account);
            } else {
                JLUtil.setAccountToClient(null);
                JLUtil.prepNormalLogin();
                Static.getClient().setLoginIndex(2);
                Static.getClient().setUsername(account.getUsername());
                Static.getClient().setPassword(account.getPassword());
            }
        } catch (Exception e) {
            DialogUtil.showError(this, "Error", "Failed to set account credentials: " + e.getMessage());
        }
    }

    private void handleDelete() {
        if (DialogUtil.showConfirm(this,
                "Are you sure you want to delete this account?",
                "Confirm Delete")) {
            try {
                accountManager.deleteAccount(account);
                onRefresh.run();
            } catch (Exception e) {
                DialogUtil.showError(this, "Error", "Failed to delete account: " + e.getMessage());
            }
        }
    }

    private void handleRename() {
        String newName = DialogUtil.showInput(this, "Enter new name:", "Rename Account");
        if (newName != null && !newName.isBlank()) {
            try {
                accountManager.renameAccount(account, newName);
                onRefresh.run();
            } catch (Exception e) {
                DialogUtil.showError(this, "Error", "Failed to rename account: " + e.getMessage());
            }
        }
    }
}