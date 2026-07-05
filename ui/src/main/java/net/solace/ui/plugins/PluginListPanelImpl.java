package net.solace.ui.plugins;

import com.google.common.collect.ImmutableList;
import lombok.Getter;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.MultiplexingPluginPanel;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.util.Text;
import net.solace.api.events.ExternalPluginsChanged;
import net.solace.api.events.PluginChanged;
import net.solace.api.events.PluginToggleHiddenChanged;
import net.solace.api.events.ProfileChanged;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.PluginDescriptor;
import net.solace.api.plugins.PluginManager;
import net.solace.api.plugins.config.ConfigGroup;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.ConfigPanel;
import net.solace.api.plugins.config.FixedWidthPanel;
import net.solace.api.plugins.config.PluginConfigurationDescriptor;
import net.solace.api.plugins.config.PluginListItem;
import net.solace.api.plugins.config.PluginListPanel;
import net.solace.api.plugins.config.PluginSearch;
import net.solace.api.plugins.config.SolaceConfig;
import net.solace.api.plugins.exception.PluginInstantiationException;
import net.solace.loader.events.SdnLoaded;
import net.solace.loader.events.SdnPluginUpdated;
import net.solace.sdn.SdnPluginManager;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PluginListPanelImpl extends PluginListPanel {
    private static final String SOLACE_GROUP_NAME = SolaceConfig.class.getAnnotation(ConfigGroup.class).value();
    private static final String PINNED_PLUGINS_CONFIG_KEY = "pinnedPlugins";
    private static final ImmutableList<String> CATEGORY_TAGS = ImmutableList.of(
            "Solace",
            "Combat",
            "Chat",
            "Item",
            "Minigame",
            "Notification",
            "Plugin Hub",
            "Skilling",
            "XP"
    );

    private final PluginManager pluginManager;
    private final ConfigManager configManager;
    private final SdnPluginManager sdnPluginManager;
    private final com.google.inject.Provider<ConfigPanel> configPanelProvider;

    @Getter
    private final MultiplexingPluginPanel muxer;
    private final IconTextField searchBar;
    private final FixedWidthPanel mainPanel;
    private final JScrollPane scrollPane;
    private final List<PluginConfigurationDescriptor> fakePlugins = new ArrayList<>();
    private List<PluginListItem> pluginList;
    private final Map<String, SdnPluginUpdated> updates = new HashMap<>();

    private final JLabel pluginsLoadingLabel = new JLabel("Plugins are loading, please wait...");

    public PluginListPanelImpl(
            PluginManager pluginManager,
            EventBus eventBus,
            ConfigManager configManager,
            SdnPluginManager sdnPluginManager,
            com.google.inject.Provider<ConfigPanel> configPanelProvider
    ) {
        this.pluginManager = pluginManager;
        this.configPanelProvider = configPanelProvider;

        muxer = new MultiplexingPluginPanel(this) {
            @Override
            protected void onAdd(PluginPanel p) {
                eventBus.register(p);
            }

            @Override
            protected void onRemove(PluginPanel p) {
                eventBus.unregister(p);
            }
        };
        this.configManager = configManager;
        this.sdnPluginManager = sdnPluginManager;

        searchBar = new IconTextField();
        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        searchBar.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(ColorScheme.DARK_GRAY_HOVER_COLOR);
        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                onSearchBarChanged();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                onSearchBarChanged();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                onSearchBarChanged();
            }
        });
        CATEGORY_TAGS.forEach(searchBar.getSuggestionListModel()::addElement);

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        var topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new BorderLayout(0, BORDER_OFFSET));
        topPanel.add(searchBar, BorderLayout.CENTER);

        pluginsLoadingLabel.setHorizontalAlignment(JLabel.CENTER);
        topPanel.add(pluginsLoadingLabel, BorderLayout.SOUTH);
        add(topPanel, BorderLayout.NORTH);

        mainPanel = new FixedWidthPanel();
        mainPanel.setBorder(new EmptyBorder(8, 10, 10, 10));
        mainPanel.setLayout(new DynamicGridLayout(0, 1, 0, 5));
        mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel northPanel = new FixedWidthPanel();
        northPanel.setLayout(new BorderLayout());
        northPanel.add(mainPanel, BorderLayout.NORTH);

        scrollPane = new JScrollPane(northPanel);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void onSearchBarChanged() {
        final var text = searchBar.getText();
        pluginList.forEach(mainPanel::remove);
        PluginSearch.search(pluginList, text).forEach(mainPanel::add);
        revalidate();
    }

    @Override
    public void openConfigurationPanel(PluginConfigurationDescriptor pluginConfig) {
        var panel = configPanelProvider.get();
        panel.init(pluginConfig);
        muxer.pushState(panel);
    }

    public void openConfigurationPanel(String configGroup) {
        for (var pluginListItem : pluginList) {
            if (pluginListItem.getPluginConfig().getName().equals(configGroup)) {
                openConfigurationPanel(pluginListItem.getPluginConfig());
                break;
            }
        }
    }

    public void openConfigurationPanel(Plugin plugin) {
        for (var pluginListItem : pluginList) {
            if (pluginListItem.getPluginConfig().getPlugin() == plugin) {
                openConfigurationPanel(pluginListItem.getPluginConfig());
                break;
            }
        }
    }

    @Override
    public void savePinnedPlugins() {
        final var value = pluginList.stream()
                .filter(PluginListItem::isPinned)
                .map(p -> p.getPluginConfig().getName())
                .collect(Collectors.joining(","));

        configManager.setConfiguration(SOLACE_GROUP_NAME, PINNED_PLUGINS_CONFIG_KEY, value);
    }

    @Override
    public void refresh() {
        updates.clear();
        pluginList.forEach(listItem -> {
            final var plugin = listItem.getPluginConfig().getPlugin();
            if (plugin != null) {
                listItem.setPluginEnabled(pluginManager.isPluginEnabled(plugin));
            }
        });

        var scrollBarPosition = scrollPane.getVerticalScrollBar().getValue();

        onSearchBarChanged();
        searchBar.requestFocusInWindow();
        validate();

        scrollPane.getVerticalScrollBar().setValue(scrollBarPosition);
    }

    @Override
    public void rebuildPluginList() {
        final var pinnedPlugins = getPinnedPluginNames();

        // populate pluginList with all non-hidden plugins
        pluginList = Stream.concat(
                        fakePlugins.stream(),
                        pluginManager.getPlugins().stream()
                                .filter(plugin -> !plugin.getClass().getAnnotation(PluginDescriptor.class).hidden())
                                .map(plugin -> {
                                    var descriptor = plugin.getClass().getAnnotation(PluginDescriptor.class);
                                    var config = pluginManager.getPluginConfigProxy(plugin);
                                    var configDescriptor = config == null ? null : configManager.getConfigDescriptor(config);
                                    var conflicts = pluginManager.conflictsForPlugin(plugin).stream()
                                            .map(Plugin::getName)
                                            .collect(Collectors.toList());

                                    return new PluginConfigurationDescriptor(
                                            descriptor.name(),
                                            descriptor.description(),
                                            descriptor.tags(),
                                            plugin,
                                            config,
                                            configDescriptor,
                                            conflicts);
                                })
                )
                .map(desc -> {
                    var plugin = desc.getPlugin();
                    SdnPluginUpdated update = null;
                    if (plugin != null && plugin.getPluginMetaData() != null) {
                        update = updates.get(plugin.getPluginMetaData().getPluginId());
                    }

                    var listItem = new PluginListItemImpl(this, desc, sdnPluginManager, update);
                    listItem.setPinned(pinnedPlugins.contains(desc.getName()));
                    return listItem;
                })
                .sorted(Comparator.comparing(p -> p.getPluginConfig().getName()))
                .collect(Collectors.toList());

        mainPanel.removeAll();
        refresh();
    }

    @Override
    public void startPlugin(Plugin plugin) throws PluginInstantiationException {
        pluginManager.setPluginEnabled(plugin, true);
        pluginManager.startPlugin(plugin);
    }

    @Override
    public void stopPlugin(Plugin plugin) throws PluginInstantiationException {
        pluginManager.setPluginEnabled(plugin, false);
        pluginManager.stopPlugin(plugin);
    }

    private List<String> getPinnedPluginNames() {
        final var config = configManager.getConfiguration(SOLACE_GROUP_NAME, PINNED_PLUGINS_CONFIG_KEY);

        if (config == null) {
            return Collections.emptyList();
        }

        return Text.fromCSV(config);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PANEL_WIDTH + SCROLLBAR_WIDTH, super.getPreferredSize().height);
    }

    @Override
    public void onActivate() {
        super.onActivate();

        if (searchBar.getParent() != null) {
            searchBar.requestFocusInWindow();
        }
    }

    @Subscribe
    private void onExternalPluginsChanged(ExternalPluginsChanged ev) {
        SwingUtilities.invokeLater(this::rebuildPluginList);
    }

    @Subscribe
    private void onProfileChanged(ProfileChanged ev) {
        SwingUtilities.invokeLater(this::rebuildPluginList);
    }

    @Subscribe
    public void onPluginChanged(PluginChanged event) {
        SwingUtilities.invokeLater(this::refresh);
    }

    @Subscribe
    private void onPluginToggleHiddenChanged(PluginToggleHiddenChanged e) {
        SwingUtilities.invokeLater(this::rebuildPluginList);
    }

    @Subscribe
    private void onSdnPluginUpdated(SdnPluginUpdated sdnPluginUpdated) {
        updates.put(sdnPluginUpdated.getPluginInfo().id, sdnPluginUpdated);
        SwingUtilities.invokeLater(this::rebuildPluginList);
    }

    @Subscribe
    private void onSdnLoaded(SdnLoaded sdnLoaded) {
        pluginsLoadingLabel.setVisible(false);
    }
}
