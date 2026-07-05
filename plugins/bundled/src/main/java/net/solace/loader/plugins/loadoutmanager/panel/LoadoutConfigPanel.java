package net.solace.loader.plugins.loadoutmanager.panel;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.util.Text;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.items.loadouts.ILoadoutFactory;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.plugins.PluginManager;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.ConfigPanel;
import net.solace.api.plugins.config.PluginListPanel;
import net.solace.loader.plugins.loadoutmanager.SolaceLoadoutManagerConfig;
import net.solace.loader.plugins.loadoutmanager.SolaceLoadoutManagerPlugin;
import net.solace.sdn.SdnPluginManager;
import net.solace.ui.plugins.ConfigPanelImpl;
import net.solace.ui.plugins.items.ItemSelector;

import javax.inject.Provider;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import static net.solace.loader.plugins.loadoutmanager.SolaceLoadoutManagerConfig.CONFIG_GROUP;

@Slf4j
public class LoadoutConfigPanel extends ConfigPanelImpl {
    private final SolaceLoadoutManagerPlugin plugin;
    private final SolaceLoadoutManagerConfig config;
    private final ConfigManager configManager;
    private final ILoadoutFactory loadoutFactory;
    private final JButton cancelButton = new JButton("Cancel fetch");
    private final DefaultListModel<String> loadoutListModel = new DefaultListModel<>();
    private final JList<String> loadoutList = new JList<>(loadoutListModel);
    private final JPanel bottomPanel = new JPanel(new DynamicGridLayout(0, 1, 0, 5));

    public LoadoutConfigPanel(PluginListPanel pluginList, ConfigManager configManager, PluginManager pluginManager,
                              ColorPickerManager colorPickerManager, SdnPluginManager sdnPluginManager,
                              EventBus eventBus, ItemManager itemManager, Provider<ItemSelector> itemSelectorProvider,
                              IClientThread clientThread, SolaceLoadoutManagerPlugin plugin, SolaceLoadoutManagerConfig config,
                              ILoadoutFactory loadoutFactory) {
        super(pluginList, configManager, pluginManager, colorPickerManager, sdnPluginManager, eventBus, itemManager,
                itemSelectorProvider, clientThread, loadoutFactory);
        this.plugin = plugin;
        this.config = config;
        this.configManager = configManager;
        this.loadoutFactory = loadoutFactory;

        var button = new JButton("Create Loadout");
        button.addActionListener(a -> {
            var name = JOptionPane.showInputDialog("Enter a name for this Loadout");
            var keys = new ArrayList<>(getLoadoutConfigKeys());
            keys.add(name);
            config.loadoutConfigKeys(Text.toCSV(keys));
        });

        getMainPanel().add(button);

        var refreshButton = new JButton("Refresh Loadouts");
        refreshButton.addActionListener(a -> rebuild());

        getMainPanel().add(refreshButton);

        var scroll = new JScrollPane(loadoutList);
        scroll.setBorder(new TitledBorder("Loadouts"));
        scroll.setPreferredSize(new Dimension(ConfigPanel.PANEL_WIDTH, 300));
        getMainPanel().add(scroll);

        bottomPanel.setPreferredSize(new Dimension(ConfigPanel.PANEL_WIDTH, 175));
        getMainPanel().add(bottomPanel);

        loadoutList.addListSelectionListener(this::onLoadoutSelected);

        cancelButton.addActionListener(a -> plugin.setCurrentFetchingLoadout(null));

        getMainPanel().add(cancelButton);
    }

    public void rebuild() {
        loadoutListModel.clear();

        cancelButton.setVisible(plugin.getCurrentFetchingLoadout() != null);

        for (var loadoutConfigKey : getLoadoutConfigKeys()) {
            loadoutListModel.addElement(loadoutConfigKey);
        }

        getMainPanel().revalidate();
        getMainPanel().repaint();
    }

    private void onLoadoutSelected(ListSelectionEvent e) {
        if (e.getValueIsAdjusting()) {
            return;
        }

        bottomPanel.removeAll();
        bottomPanel.setBorder(null);

        var loadoutConfigKey = loadoutList.getSelectedValue();
        if (loadoutConfigKey != null) {
            bottomPanel.setBorder(new TitledBorder("'" + loadoutConfigKey + "'"));
            var key = "loadoutMgr_" + loadoutConfigKey;
            if (configManager.getConfiguration(CONFIG_GROUP, key, Loadout.class) == null) {
                var newLoadout = loadoutFactory.newBuilder().build();
                newLoadout.save(configManager, CONFIG_GROUP, key);
            }

            var loadoutPanel = createLoadoutLayout(config, "Configuration", CONFIG_GROUP, key);
            bottomPanel.add(loadoutPanel);

            var actionsPanel = new JPanel(new DynamicGridLayout(0, 1, 0, 5));
            actionsPanel.setBorder(new TitledBorder("Loadout Actions"));

            var fetchButton = new JButton("Fetch from bank");
            fetchButton.addActionListener(a -> {
                if (plugin.getCurrentFetchingLoadout() == null) {
                    plugin.setCurrentFetchingLoadout(configManager.getConfiguration(CONFIG_GROUP, key, Loadout.class));
                }
            });

            actionsPanel.add(fetchButton);

            var deleteButton = new JButton("Delete");
            deleteButton.addActionListener(a -> {
                var yesNo = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this loadout?");
                if (yesNo != JOptionPane.YES_OPTION) {
                    return;
                }

                var keys = new ArrayList<>(getLoadoutConfigKeys());
                keys.remove(loadoutConfigKey);
                config.loadoutConfigKeys(Text.toCSV(keys));
            });

            actionsPanel.add(deleteButton);
            bottomPanel.add(actionsPanel);
        }

        bottomPanel.revalidate();
        bottomPanel.repaint();
    }

    private List<String> getLoadoutConfigKeys() {
        return Text.fromCSV(config.loadoutConfigKeys());
    }
}
