/*
 * Created by JFormDesigner on Sat Aug 01 23:27:36 CEST 2020
 */

package net.solace.loader.plugins.breakhandler.ui;

import com.google.inject.Inject;
import net.runelite.client.ui.PluginPanel;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.events.ConfigChanged;
import net.solace.api.game.IGame;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.ToggleButton;
import net.solace.loader.plugins.breakhandler.SolaceBreakHandlerPlugin;
import net.solace.loader.plugins.breakhandler.ui.utils.DeferredDocumentChangedListener;
import net.solace.loader.plugins.breakhandler.ui.utils.ProfilesData;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;

import static net.runelite.client.ui.PluginPanel.PANEL_WIDTH;
import static net.solace.api.breaks.BreakHandler.CONFIG_GROUP;

public class SolaceBreakHandlerAccountPanel extends JPanel {
    private final ConfigManager configManager;
    private final BreakHandler solaceBreakHandler;
    private final IGame game;
    private final JPanel contentPanel = new JPanel(new GridLayout(0, 1));

    @Inject
    SolaceBreakHandlerAccountPanel(SolaceBreakHandlerPlugin solaceBreakHandlerPlugin, BreakHandler solaceBreakHandler, IGame game) {
        this.configManager = solaceBreakHandlerPlugin.getConfigManager();
        this.solaceBreakHandler = solaceBreakHandler;
        this.game = game;

        setupDefaults();

        setLayout(new BorderLayout());
        setBackground(SolaceBreakHandlerPanel.PANEL_BACKGROUND_COLOR);

        init();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PANEL_WIDTH, super.getPreferredSize().height);
    }

    private boolean getConfigValue() {
        String accountselection = configManager.getConfiguration(CONFIG_GROUP, "accountselection");

        return Boolean.parseBoolean(accountselection);
    }

    private void init() {
        contentPanel.setBorder(new EmptyBorder(10, 10, 0, 10));

        JPanel accountSelection = new JPanel(new GridLayout(0, 2));
        accountSelection.setBorder(new EmptyBorder(5, 0, 0, 0));
        ButtonGroup buttonGroup = new ButtonGroup();

        JCheckBox manualButton = new ToggleButton("Manual");
        JCheckBox profilesButton = new ToggleButton("Profiles plugin");

        String profilesSalt = configManager.getConfiguration("betterProfiles", "salt");
        boolean profilesSavePasswords = Boolean.parseBoolean(configManager.getConfiguration("betterProfiles", "rememberPassword"));

        if (profilesSalt == null || profilesSalt.length() == 0 || !profilesSavePasswords) {
            configManager.setConfiguration(CONFIG_GROUP, "accountselection", true);
            profilesButton.setEnabled(false);
        }

        manualButton.addActionListener(e ->
        {
            configManager.setConfiguration(CONFIG_GROUP, "accountselection", manualButton.isSelected());
            contentPanel(manualButton.isSelected());
        });

        profilesButton.addActionListener(e ->
        {
            configManager.setConfiguration(CONFIG_GROUP, "accountselection", !profilesButton.isSelected());
            contentPanel(!profilesButton.isSelected());
        });

        buttonGroup.add(manualButton);
        buttonGroup.add(profilesButton);

        boolean config = getConfigValue();

        manualButton.setSelected(config);
        profilesButton.setSelected(!config);

        accountSelection.add(manualButton);
        accountSelection.add(profilesButton);

        add(accountSelection, BorderLayout.NORTH);

        contentPanel(config);

        add(contentPanel, BorderLayout.CENTER);
    }

    private void contentPanel(boolean manual) {
        contentPanel.removeAll();

        if (manual) {
            contentPanel.add(new JLabel("Username"));

            final JTextField usernameField = new JTextField();
            usernameField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            usernameField.setText(configManager.getConfiguration(CONFIG_GROUP, "accountselection-manual-username"));
            DeferredDocumentChangedListener usernameListener = new DeferredDocumentChangedListener();
            usernameListener.addChangeListener(e ->
                    configManager.setConfiguration(CONFIG_GROUP, "accountselection-manual-username", usernameField.getText()));
            usernameField.getDocument().addDocumentListener(usernameListener);

            contentPanel.add(usernameField);

            contentPanel.add(new JLabel("Password"));

            final JPasswordField passwordField = new JPasswordField();
            passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            passwordField.setText(configManager.getConfiguration(CONFIG_GROUP, "accountselection-manual-password"));
            DeferredDocumentChangedListener passwordListener = new DeferredDocumentChangedListener();
            passwordListener.addChangeListener(e ->
                    configManager.setConfiguration(CONFIG_GROUP, "accountselection-manual-password", String.valueOf(passwordField.getPassword())));
            passwordField.getDocument().addDocumentListener(passwordListener);

            contentPanel.add(passwordField);

            if (game.getGameAccount() != null) {
                usernameField.setText(game.getGameAccount().getUsername());
                passwordField.setText(game.getGameAccount().getPassword());
            }
        } else if (SolaceBreakHandlerPlugin.data == null) {
            contentPanel.add(new JLabel("Profiles plugin password"));
            final JPasswordField passwordField = new JPasswordField();
            passwordField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            JLabel parsingLabel = new JLabel();
            parsingLabel.setHorizontalAlignment(SwingConstants.CENTER);
            parsingLabel.setPreferredSize(new Dimension(PANEL_WIDTH, 15));

            DeferredDocumentChangedListener passwordListener = new DeferredDocumentChangedListener();
            passwordListener.addChangeListener(e ->
            {
                try {
                    SolaceBreakHandlerPlugin.data = ProfilesData.getProfileData(configManager, passwordField.getPassword());
                    contentPanel(false);
                } catch (InvalidKeySpecException | NoSuchPaddingException | BadPaddingException | InvalidKeyException |
                         IllegalBlockSizeException | NoSuchAlgorithmException ignored) {
                    parsingLabel.setText("Incorrect password!");
                }
            });
            passwordField.getDocument().addDocumentListener(passwordListener);

            contentPanel.add(passwordField);
            contentPanel.add(parsingLabel);
        } else {
            ConfigChanged configChanged = new ConfigChanged();
            configChanged.setGroup("mock");
            configChanged.setKey("mock");
            solaceBreakHandler.getConfigChanged().onNext(configChanged);

            if (!SolaceBreakHandlerPlugin.data.contains(":")) {
                contentPanel.add(new JLabel("No accounts found"));
            } else {
                contentPanel.add(new JLabel("Select account"));

                String[] accounts = Arrays.stream(SolaceBreakHandlerPlugin.data.split("\\n"))
                        .map((s) -> s.split(":")[0])
                        .sorted()
                        .toArray(String[]::new);

                JComboBox<String> filterComboBox = new JComboBox<>(accounts);
                filterComboBox.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
                filterComboBox.addActionListener(e ->
                {
                    if (filterComboBox.getSelectedItem() != null) {
                        configManager.setConfiguration(CONFIG_GROUP, "accountselection-profiles-account", filterComboBox.getSelectedItem().toString());
                    }
                });

                String config = configManager.getConfiguration(CONFIG_GROUP, "accountselection-profiles-account");

                if (config != null) {
                    int index = Arrays.asList(accounts).indexOf(config);

                    if (index != -1) {
                        filterComboBox.setSelectedIndex(index);
                    } else {
                        filterComboBox.setSelectedIndex(0);
                    }
                }

                contentPanel.add(filterComboBox);
            }
        }


        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void setupDefaults() {
        if (configManager.getConfiguration(CONFIG_GROUP, "accountselection") == null) {
            configManager.setConfiguration(CONFIG_GROUP, "accountselection", true);
        }

        if (configManager.getConfiguration(CONFIG_GROUP, "accountselection-manual-username") == null) {
            configManager.setConfiguration(CONFIG_GROUP, "accountselection-manual-username", "");
        }

        if (configManager.getConfiguration(CONFIG_GROUP, "accountselection-manual-password") == null) {
            configManager.setConfiguration(CONFIG_GROUP, "accountselection-manual-password", "");
        }

        if (configManager.getConfiguration(CONFIG_GROUP, "accountselection-profiles-account") == null) {
            configManager.setConfiguration(CONFIG_GROUP, "accountselection-profiles-account", "");
        }
    }
}