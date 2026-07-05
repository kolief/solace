package net.solace.loader.plugins.profiles.panel;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;
import net.solace.loader.plugins.profiles.data.AccountManager;
import net.solace.loader.plugins.profiles.panel.dialog.DialogUtil;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Arrays;

@Slf4j
public class AccountSwitcherPanel extends PluginPanel {
    private final AccountManager accountManager;
    private AccountListPanel accountListPanel;
    private HeaderPanel headerPanel;

    @Inject
    public AccountSwitcherPanel(AccountManager accountManager) {
        super(false);
        this.accountManager = accountManager;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        if (!accountManager.isMasterPasswordSet()) {
            showSetPasswordPanel();
        } else {
            showLoginPanel();
        }
    }

    public void showSetPasswordPanel() {
        removeAll();

        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        // Title
        JLabel titleLabel = new JLabel("Set Master Password");
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(titleLabel, c);

        // Requirements label
        JLabel requirementsLabel = new JLabel("<html>Password must be at least 8 characters</html>");
        requirementsLabel.setForeground(Color.GRAY);
        c.gridy = 1;
        panel.add(requirementsLabel, c);

        // Password fields
        JPasswordField passwordField = new JPasswordField(20);
        JPasswordField confirmField = new JPasswordField(20);

        // Create password panel with toggle
        JPanel passPanel = new JPanel(new BorderLayout(5, 0));
        JToggleButton toggleButton = new JToggleButton("Show");
        toggleButton.setPreferredSize(new Dimension(60, passwordField.getPreferredSize().height));
        passPanel.add(passwordField, BorderLayout.CENTER);
        passPanel.add(toggleButton, BorderLayout.EAST);

        toggleButton.addActionListener(e -> {
            if (toggleButton.isSelected()) {
                passwordField.setEchoChar((char) 0);
                toggleButton.setText("Hide");
            } else {
                passwordField.setEchoChar('•');
                toggleButton.setText("Show");
            }
        });

        c.gridy = 2;
        panel.add(passPanel, c);

        // Confirm password field
        JPanel confirmPanel = new JPanel(new BorderLayout(5, 0));
        confirmPanel.add(new JLabel("Confirm:"), BorderLayout.WEST);
        confirmPanel.add(confirmField, BorderLayout.CENTER);
        c.gridy = 3;
        panel.add(confirmPanel, c);

        // Set button
        JButton setButton = new JButton("Set Password");
        c.gridy = 4;
        c.insets = new Insets(15, 5, 5, 5);
        panel.add(setButton, c);

        setButton.addActionListener(e -> {
            try {
                if (!Arrays.equals(passwordField.getPassword(), confirmField.getPassword())) {
                    DialogUtil.showError(this, "Error", "Passwords do not match");
                    return;
                }

                accountManager.setMasterPassword(passwordField.getPassword());
                showLoginPanel();
            } catch (Exception ex) {
                DialogUtil.showError(this, "Error", ex.getMessage());
            }
        });

        add(panel, BorderLayout.NORTH);
        revalidate();
        repaint();
    }

    public void showLoginPanel() {
        removeAll();

        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5, 5, 5, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        // Login components
        JPasswordField passwordField = new JPasswordField(20);
        JPanel passPanel = createPasswordPanel(passwordField);
        JButton loginButton = new JButton("Login");
        JButton resetButton = new JButton("Reset");
        resetButton.setForeground(Color.RED);

        // Layout
        c.gridx = 0;
        c.gridy = 0;
        panel.add(new JLabel("Enter Master Password"), c);
        c.gridy = 1;
        panel.add(passPanel, c);
        c.gridy = 2;
        panel.add(loginButton, c);
        c.gridy = 3;
        panel.add(resetButton, c);

        // Actions
        loginButton.addActionListener(e -> handleLogin(passwordField.getPassword()));
        resetButton.addActionListener(e -> handleReset());
        passwordField.addActionListener(e -> loginButton.doClick());

        add(panel, BorderLayout.NORTH);
        revalidate();
        repaint();
        passwordField.requestFocusInWindow();
    }

    private JPanel createPasswordPanel(JPasswordField passwordField) {
        JPanel panel = new JPanel(new BorderLayout(5, 0));
        JToggleButton toggleButton = new JToggleButton("Show");
        toggleButton.setPreferredSize(new Dimension(60, passwordField.getPreferredSize().height));

        toggleButton.addActionListener(e -> {
            passwordField.setEchoChar(toggleButton.isSelected() ? (char) 0 : '•');
            toggleButton.setText(toggleButton.isSelected() ? "Hide" : "Show");
        });

        panel.add(passwordField, BorderLayout.CENTER);
        panel.add(toggleButton, BorderLayout.EAST);
        return panel;
    }

    private void handleLogin(char[] password) {
        try {
            if (accountManager.unlock(password)) {
                showMainPanel();
            } else {
                DialogUtil.showError(this, "Login Failed", "Incorrect password");
            }
        } catch (Exception ex) {
            DialogUtil.showError(this, "Error", "Failed to unlock: " + ex.getMessage());
        }
    }

    private void handleReset() {
        if (DialogUtil.showConfirm(this,
                "This will delete all saved accounts and reset the master password.\n" +
                        "This action cannot be undone.\n\n" +
                        "Are you sure you want to continue?",
                "Confirm Reset")) {
            try {
                if (accountManager.MASTER_FILE.exists()) {
                    accountManager.MASTER_FILE.delete();
                }
                if (accountManager.ACCOUNTS_FILE.exists()) {
                    accountManager.ACCOUNTS_FILE.delete();
                }
                showSetPasswordPanel();
            } catch (Exception ex) {
                DialogUtil.showError(this, "Error", "Failed to reset: " + ex.getMessage());
            }
        }
    }

    private void showMainPanel() {
        removeAll();

        // Create main components
        accountListPanel = new AccountListPanel(accountManager);
        headerPanel = new HeaderPanel(accountManager, accountListPanel);

        // Layout
        JPanel mainContainer = new JPanel(new BorderLayout(0, 5));
        mainContainer.add(headerPanel, BorderLayout.NORTH);

        JScrollPane scrollPane = new JScrollPane(accountListPanel);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());

        mainContainer.add(scrollPane, BorderLayout.CENTER);

        add(mainContainer);
        revalidate();
        repaint();
    }
}