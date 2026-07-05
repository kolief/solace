package net.solace.loader.plugins.profiles.panel;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.profiles.data.AccountManager;
import net.solace.loader.plugins.profiles.panel.dialog.AddAccountDialog;
import net.solace.loader.plugins.profiles.panel.dialog.DialogUtil;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;

@Slf4j
public class HeaderPanel extends JPanel {
    private final AccountManager accountManager;
    private final JTextField searchBar;
    private final AccountListPanel accountListPanel;

    public HeaderPanel(AccountManager accountManager, AccountListPanel accountListPanel) {
        this.accountManager = accountManager;
        this.accountListPanel = accountListPanel;

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(0, 0, 5, 0));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(0, 5, 0, 5);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1.0;

        // Button panel with Add, Import, and Logout
        JPanel buttonPanel = createButtonPanel();
        c.gridx = 0;
        c.gridy = 0;
        add(buttonPanel, c);

        // Search panel
        JPanel searchPanel = createSearchPanel();
        c.gridy = 1;
        add(searchPanel, c);

        this.searchBar = (JTextField) searchPanel.getComponent(1);
    }

    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        Dimension buttonSize = new Dimension(70, 25);

        JButton addButton = new JButton("Add");
        JButton importButton = new JButton("Import");
        JButton logoutButton = new JButton("Logout");

        addButton.setPreferredSize(buttonSize);
        importButton.setPreferredSize(buttonSize);
        logoutButton.setPreferredSize(buttonSize);

        addButton.addActionListener(e -> showAddAccountDialog());
        importButton.addActionListener(e -> handleImport());
        logoutButton.addActionListener(e -> handleLogout());

        buttonPanel.add(addButton);
        buttonPanel.add(importButton);
        buttonPanel.add(logoutButton);

        return buttonPanel;
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setPreferredSize(new Dimension(50, 25));
        searchLabel.setHorizontalAlignment(SwingConstants.RIGHT);

        JTextField searchBar = new JTextField();
        searchBar.setPreferredSize(new Dimension(0, 25));

        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchBar, BorderLayout.CENTER);

        accountListPanel.handleSearch(searchBar);

        return searchPanel;
    }

    private void handleLogout() {
        try {
            accountManager.logout();
            // Notify the main panel to show login
            Container parent = getParent();
            while (parent != null && !(parent instanceof AccountSwitcherPanel)) {
                parent = parent.getParent();
            }
            if (parent instanceof AccountSwitcherPanel) {
                ((AccountSwitcherPanel) parent).showLoginPanel();
            }
        } catch (Exception e) {
            DialogUtil.showError(this, "Error", "Failed to logout: " + e.getMessage());
        }
    }

    private void handleImport() {
        String[] options = {"From File", "From Client", "From Launcher"};
        int choice = JOptionPane.showOptionDialog(this,
                "Choose import source",
                "Import Account",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]);

        AccountImporter importer = new AccountImporter(accountManager);
        try {
            switch (choice) {
                case 0:
                    importer.importFromFile();
                    break;
                case 1:
                    importer.importFromClient();
                    break;
                case 2:
                    importer.importFromLauncher();
                    break;
            }
            accountListPanel.refresh();
        } catch (Exception e) {
            DialogUtil.showError(this, "Error", "Import failed: " + e.getMessage());
        }
    }

    private void showAddAccountDialog() {
        new AddAccountDialog((Frame) SwingUtilities.getWindowAncestor(this),
                accountManager,
                accountListPanel::refresh)
                .setVisible(true);
    }
}