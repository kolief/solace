package net.solace.loader.plugins.profiles.panel;

import lombok.extern.slf4j.Slf4j;
import net.solace.loader.plugins.profiles.data.AccountData;
import net.solace.loader.plugins.profiles.data.AccountManager;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AccountListPanel extends JPanel {
    private final AccountManager accountManager;
    private final Map<String, AccountPanel> accountRows = new HashMap<>();

    public AccountListPanel(AccountManager accountManager) {
        this.accountManager = accountManager;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        refresh();
    }

    public void refresh() {
        removeAll();
        accountRows.clear();

        try {
            boolean first = true;
            for (AccountData account : accountManager.getAccounts()) {
                if (!first) {
                    add(Box.createRigidArea(new Dimension(0, 3)));
                }
                first = false;

                AccountPanel accountPanel = new AccountPanel(account, accountManager, this::refresh);
                accountRows.put(account.getName(), accountPanel);
                add(accountPanel);
            }
        } catch (Exception e) {
            add(new JLabel("Error loading accounts: " + e.getMessage()));
            log.error("Error loading accounts", e);
        }

        revalidate();
        repaint();
    }

    public void filter(String searchText) {
        String query = searchText.toLowerCase();
        for (AccountData account : accountManager.getAccounts()) {
            AccountPanel panel = accountRows.get(account.getName());
            if (panel != null) {
                String displayText = account.getType() == AccountData.AccountType.JL ?
                        "[JL] " + account.getName() : account.getName();
                boolean visible = (displayText != null &&
                        displayText.toLowerCase().contains(query));

                panel.setVisible(visible);
            }
        }
        revalidate();
        repaint();
    }

    public void handleSearch(JTextField searchBar) {
        Timer searchTimer = new Timer(150, e -> filter(searchBar.getText()));
        searchTimer.setRepeats(false);

        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                searchTimer.restart();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                searchTimer.restart();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                searchTimer.restart();
            }
        });
    }
}