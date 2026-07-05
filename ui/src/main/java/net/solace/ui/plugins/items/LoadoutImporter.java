package net.solace.ui.plugins.items;

import net.runelite.client.util.Text;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.plugins.config.ConfigManager;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;

public class LoadoutImporter extends JDialog {
    private static final String LOADOUT_MGR_GROUP = "solaceloadoutmanager";
    private static final String LOADOUT_MGR_KEY = "loadoutConfigKeys";

    private final ConfigManager configManager;
    private final JComboBox<String> loadoutComboBox;
    private final DefaultComboBoxModel<String> defaultComboBoxModel;

    private String group;
    private String key;

    public LoadoutImporter(ConfigManager configManager) {
        this.configManager = configManager;

        setLayout(new GridBagLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(350, 100);
        setTitle("Loadout Importer");
        setResizable(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        defaultComboBoxModel = new DefaultComboBoxModel<>();
        loadoutComboBox = new JComboBox<>(defaultComboBoxModel);

        var button = new JButton("Select");
        button.addActionListener(this::selectLoadout);

        JLabel label = new JLabel("Select a loadout from the Solace Loadout Manager");
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(label, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        add(loadoutComboBox, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0;
        add(button, gbc);
    }

    public void init(String group, String key) {
        this.group = group;
        this.key = key;

        defaultComboBoxModel.removeAllElements();
        var loadoutManagerConfigs = configManager.getConfiguration(LOADOUT_MGR_GROUP, LOADOUT_MGR_KEY);
        var list = Text.fromCSV(loadoutManagerConfigs);
        defaultComboBoxModel.addAll(list);
        defaultComboBoxModel.addElement("None");

        setVisible(true);
    }

    private void selectLoadout(ActionEvent e) {
        var selectedIndex = loadoutComboBox.getSelectedIndex();
        if (selectedIndex == -1) {
            dispose();
            return;
        }

        var selected = defaultComboBoxModel.getElementAt(selectedIndex);
        if (selected == null || selected.equals("None")) {
            return;
        }

        Loadout selectedLoadout = configManager.getConfiguration(LOADOUT_MGR_GROUP, "loadoutMgr_" + selected, Loadout.class);
        configManager.setConfiguration(group, key, selectedLoadout);

        dispose();
    }
}