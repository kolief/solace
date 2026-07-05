/*
 * Copyright (c) 2017, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.solace.ui.plugins;

import com.google.common.base.MoreObjects;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Sets;
import com.google.common.primitives.Ints;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.ModifierlessKeybind;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.DynamicGridLayout;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.UnitFormatterFactory;
import net.runelite.client.ui.components.ColorJButton;
import net.runelite.client.ui.components.TitleCaseListCellRenderer;
import net.runelite.client.ui.components.colorpicker.ColorPickerManager;
import net.runelite.client.ui.components.colorpicker.RuneliteColorPicker;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.ColorUtil;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.LinkBrowser;
import net.runelite.client.util.SwingUtil;
import net.runelite.client.util.Text;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.events.ConfigButtonClicked;
import net.solace.api.events.ConfigChanged;
import net.solace.api.events.ExternalPluginsChanged;
import net.solace.api.events.PluginChanged;
import net.solace.api.events.PluginToggleHiddenChanged;
import net.solace.api.events.ProfileChanged;
import net.solace.api.items.loadouts.ILoadoutFactory;
import net.solace.api.items.loadouts.Loadout;
import net.solace.impl.items.loadouts.LoadoutImpl;
import net.solace.api.items.loadouts.LoadoutItem;
import net.solace.api.plugins.PluginManager;
import net.solace.api.plugins.config.Button;
import net.solace.api.plugins.config.Config;
import net.solace.api.plugins.config.ConfigDescriptor;
import net.solace.api.plugins.config.ConfigImageResource;
import net.solace.api.plugins.config.ConfigItem;
import net.solace.api.plugins.config.ConfigItemDescriptor;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.ConfigObject;
import net.solace.api.plugins.config.ConfigPanel;
import net.solace.api.plugins.config.ConfigSectionDescriptor;
import net.solace.api.plugins.config.DeferredDocumentChangedListener;
import net.solace.api.plugins.config.FixedWidthPanel;
import net.solace.api.plugins.config.HotkeyButton;
import net.solace.api.plugins.config.ItemConfig;
import net.solace.api.plugins.config.PluginConfigurationDescriptor;
import net.solace.api.plugins.config.PluginListPanel;
import net.solace.api.plugins.config.PluginToggleButton;
import net.solace.api.plugins.exception.PluginInstantiationException;
import net.solace.sdn.SdnPluginManager;
import net.solace.ui.plugins.items.ItemSelector;
import net.solace.ui.plugins.items.LoadoutEditor;
import net.solace.ui.plugins.items.LoadoutImporter;

import javax.inject.Provider;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeListener;
import javax.swing.text.JTextComponent;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static net.solace.api.ui.ColorScheme.BRAND_CRIMSON;

@Slf4j
public class ConfigPanelImpl extends ConfigPanel {
    private static final Map<Integer, AsyncBufferedImage> CACHED_ITEM_IMAGES = new HashMap<>();

    public static final ImageIcon SECTION_EXPAND_ICON;
    public static final ImageIcon SECTION_RETRACT_ICON;
    static final ImageIcon CONFIG_ICON;
    static final ImageIcon BACK_ICON;
    static final ImageIcon BACK_ICON_HOVER;
    private static final int SPINNER_FIELD_WIDTH = 6;
    private static final ImageIcon SECTION_EXPAND_ICON_HOVER;
    private static final ImageIcon SECTION_RETRACT_ICON_HOVER;
    private static final Map<ConfigSectionDescriptor, Boolean> sectionExpandStates = new HashMap<>();

    static {
        final var backIcon = ImageUtil.loadImageResource(ConfigPanelImpl.class, "config_back_icon.png");
        BACK_ICON = new ImageIcon(backIcon);
        BACK_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(backIcon, -100));

        var sectionRetractIcon = ImageUtil.loadImageResource(ConfigPanelImpl.class, "/util/arrow_right.png");
        sectionRetractIcon = ImageUtil.luminanceOffset(sectionRetractIcon, -121);
        SECTION_EXPAND_ICON = new ImageIcon(sectionRetractIcon);
        SECTION_EXPAND_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(sectionRetractIcon, -100));
        final var sectionExpandIcon = ImageUtil.rotateImage(sectionRetractIcon, Math.PI / 2);
        SECTION_RETRACT_ICON = new ImageIcon(sectionExpandIcon);
        var configIcon = ImageUtil.loadImageResource(ConfigPanelImpl.class, "config_edit_icon.png");
        CONFIG_ICON = new ImageIcon(configIcon);
        SECTION_RETRACT_ICON_HOVER = new ImageIcon(ImageUtil.alphaOffset(sectionExpandIcon, -100));
    }

    private final PluginListPanel pluginList;
    private final ConfigManager configManager;
    private final PluginManager pluginManager;
    private final SdnPluginManager sdnPluginManager;
    private final ColorPickerManager colorPickerManager;
    private final EventBus eventBus;
    private final ItemManager itemManager;
    private final Provider<ItemSelector> itemSelectorProvider;
    private final IClientThread clientThread;
    private final LoadoutImporter loadoutImporter;
    private final ILoadoutFactory loadoutFactory;

    private final TitleCaseListCellRenderer listCellRenderer = new TitleCaseListCellRenderer();

    private final JScrollPane scrollPane;
    @Getter
    private final FixedWidthPanel mainPanel;
    private final JLabel title;
    private final PluginToggleButton pluginToggle;
    private final JButton topPanelBackButton;

    private PluginConfigurationDescriptor pluginConfig = null;
    private boolean skipRebuild;

    public ConfigPanelImpl(
            PluginListPanel pluginList,
            ConfigManager configManager,
            PluginManager pluginManager,
            ColorPickerManager colorPickerManager,
            SdnPluginManager sdnPluginManager,
            EventBus eventBus,
            ItemManager itemManager,
            Provider<ItemSelector> itemSelectorProvider,
            IClientThread clientThread,
            ILoadoutFactory loadoutFactory
    ) {
        this.pluginList = pluginList;
        this.configManager = configManager;
        this.pluginManager = pluginManager;
        this.sdnPluginManager = sdnPluginManager;
        this.colorPickerManager = colorPickerManager;
        this.eventBus = eventBus;
        this.itemManager = itemManager;
        this.itemSelectorProvider = itemSelectorProvider;
        this.clientThread = clientThread;
        this.loadoutFactory = loadoutFactory;
        this.loadoutImporter = new LoadoutImporter(configManager);

        setLayout(new BorderLayout());
        setBackground(ColorScheme.DARK_GRAY_COLOR);

        var topPanel = new JPanel();
        topPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        topPanel.setLayout(new BorderLayout(0, BORDER_OFFSET));
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

        topPanelBackButton = new JButton(BACK_ICON);
        topPanelBackButton.setRolloverIcon(BACK_ICON_HOVER);
        SwingUtil.removeButtonDecorations(topPanelBackButton);
        topPanelBackButton.setPreferredSize(new Dimension(22, 0));
        topPanelBackButton.setBorder(new EmptyBorder(0, 0, 0, 5));
        topPanelBackButton.addActionListener(e -> pluginList.getMuxer().popState());
        topPanelBackButton.setToolTipText("Back");
        topPanel.add(topPanelBackButton, BorderLayout.WEST);

        pluginToggle = new PluginToggleButton();
        topPanel.add(pluginToggle, BorderLayout.EAST);
        title = new JLabel();
        title.setForeground(Color.WHITE);

        topPanel.add(title);
    }

    private static String htmlLabel(String key, String value) {
        return "<html><body style = 'color:#a5a5a5'>" + key + ": <span style = 'color:white'>" + value + "</span></body></html>";
    }

    @Override
    public void init(PluginConfigurationDescriptor pluginConfig, boolean backButtonVisible) {
        assert this.pluginConfig == null;
        this.pluginConfig = pluginConfig;

        scrollPane.getVerticalScrollBar().setValue(0);

        var name = pluginConfig.getName();
        title.setText(name);
        title.setForeground(Color.WHITE);
        title.setToolTipText("<html>" + name + ":<br>" + pluginConfig.getDescription() + "</html>");

        PluginListItemImpl.addLabelPopupMenu(title, pluginConfig.createSupportMenuItem(), null);

        var plugin = pluginConfig.getPlugin();
        if (plugin != null) {
            pluginToggle.setVisible(!plugin.isToggleHidden());
            pluginToggle.setConflicts(pluginConfig.getConflicts());
            pluginToggle.setSelected(pluginManager.isPluginEnabled(plugin));
            pluginToggle.addItemListener(i -> {
                if (pluginToggle.isSelected()) {
                    try {
                        pluginList.startPlugin(plugin);
                    } catch (PluginInstantiationException e) {
                        log.error("Error starting plugin", e);
                        pluginToggle.setSelected(false);
                    }
                } else {
                    try {
                        pluginList.stopPlugin(plugin);
                    } catch (PluginInstantiationException e) {
                        log.error("Error stopping plugin", e);
                        pluginToggle.setSelected(true);
                    }
                }
            });
        } else {
            pluginToggle.setVisible(false);
        }

        if (!backButtonVisible) {
            topPanelBackButton.setVisible(false);
        }

        rebuild(false);
    }

    private void toggleSection(ConfigSectionDescriptor csd, JButton button, JPanel contents) {
        var newState = !contents.isVisible();
        contents.setVisible(newState);
        button.setIcon(newState ? SECTION_RETRACT_ICON : SECTION_EXPAND_ICON);
        button.setRolloverIcon(newState ? SECTION_RETRACT_ICON_HOVER : SECTION_EXPAND_ICON_HOVER);
        button.setToolTipText(newState ? "Retract" : "Expand");
        sectionExpandStates.put(csd, newState);
        SwingUtilities.invokeLater(contents::revalidate);
    }

    private void rebuild(boolean refresh) {
        var scrollBarPosition = scrollPane.getVerticalScrollBar().getValue();

        mainPanel.removeAll();

        var cd = pluginConfig.getConfigDescriptor();

        var pluginsInfoMap = sdnPluginManager.getPluginsInfoMap();

        if (pluginConfig.getPlugin() != null && pluginsInfoMap.containsKey(pluginConfig.getPlugin().getName())) {

            var infoPanel = new JPanel();
            infoPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);
            infoPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
            infoPanel.setLayout(new GridLayout(0, 1));

            final var smallFont = FontManager.getRunescapeSmallFont();

            var pluginInfo = pluginsInfoMap.get(pluginConfig.getPlugin().getName());

            var versionLabel = new JLabel(htmlLabel("version", pluginInfo.get("version")));
            versionLabel.setFont(smallFont);
            infoPanel.add(versionLabel);

            var providerLabel = new JLabel(htmlLabel("provider", pluginInfo.get("provider")));
            providerLabel.setFont(smallFont);
            infoPanel.add(providerLabel);

            var commitHashLabel = new JLabel(htmlLabel("hash", pluginInfo.get("commitHash")));
            commitHashLabel.setFont(smallFont);
            infoPanel.add(commitHashLabel);

            var button = new JButton("Support");
            button.addActionListener(e -> LinkBrowser.browse(pluginInfo.get("support")));

            var separator = new JSeparator() {
                @Override
                protected void paintComponent(Graphics g) {
                    var width = this.getSize().width;
                    var g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(2));
                    g2.setColor(BRAND_CRIMSON);
                    g2.drawLine(0, 0, width, 0);
                }
            };

            mainPanel.add(infoPanel);
            mainPanel.add(button);
            mainPanel.add(separator);
        }

        final Map<String, JPanel> sectionWidgets = new HashMap<>();
        final Map<String, JPanel> titleWidgets = new HashMap<>();
        final Map<ConfigObject, JPanel> topLevelPanels = new TreeMap<>((a, b) ->
                ComparisonChain.start()
                        .compare(a.position(), b.position())
                        .compare(a.name(), b.name())
                        .result());

        for (var csd : cd.getSections()) {
            var cs = csd.getSection();
            final boolean isOpen = sectionExpandStates.getOrDefault(csd, !cs.closedByDefault());

            final var section = new JPanel();
            section.setLayout(new BoxLayout(section, BoxLayout.Y_AXIS));
            section.setMinimumSize(new Dimension(PANEL_WIDTH, 0));

            final var sectionHeader = new JPanel();
            sectionHeader.setLayout(new BorderLayout());
            sectionHeader.setMinimumSize(new Dimension(PANEL_WIDTH, 0));
            // For whatever reason, the header extends out by a single pixel when closed. Adding a single pixel of
            // border on the right only affects the width when closed, fixing the issue.
            sectionHeader.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, ColorScheme.MEDIUM_GRAY_COLOR),
                    new EmptyBorder(0, 0, 3, 1)));
            section.add(sectionHeader, BorderLayout.NORTH);

            final var sectionToggle = new JButton(isOpen ? SECTION_RETRACT_ICON : SECTION_EXPAND_ICON);
            sectionToggle.setRolloverIcon(isOpen ? SECTION_RETRACT_ICON_HOVER : SECTION_EXPAND_ICON_HOVER);
            sectionToggle.setPreferredSize(new Dimension(18, 0));
            sectionToggle.setBorder(new EmptyBorder(0, 0, 0, 5));
            sectionToggle.setToolTipText(isOpen ? "Retract" : "Expand");
            SwingUtil.removeButtonDecorations(sectionToggle);
            sectionHeader.add(sectionToggle, BorderLayout.WEST);

            var name = cs.name();
            final var sectionName = new JLabel(name);
            sectionName.setForeground(BRAND_CRIMSON);
            sectionName.setFont(FontManager.getRunescapeBoldFont());
            sectionName.setToolTipText("<html>" + name + ":<br>" + cs.description() + "</html>");
            sectionHeader.add(sectionName, BorderLayout.CENTER);

            final var sectionContents = new JPanel();
            sectionContents.setLayout(new DynamicGridLayout(0, 1, 0, 5));
            sectionContents.setMinimumSize(new Dimension(PANEL_WIDTH, 0));
            sectionContents.setBorder(new CompoundBorder(
                    new MatteBorder(0, 0, 1, 0, ColorScheme.MEDIUM_GRAY_COLOR),
                    new EmptyBorder(BORDER_OFFSET, 0, BORDER_OFFSET, 0)));
            sectionContents.setVisible(isOpen);
            section.add(sectionContents, BorderLayout.SOUTH);

            // Add listeners to each part of the header so that it's easier to toggle them
            final var adapter = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    toggleSection(csd, sectionToggle, sectionContents);
                }
            };
            sectionToggle.addActionListener(actionEvent -> toggleSection(csd, sectionToggle, sectionContents));
            sectionName.addMouseListener(adapter);
            sectionHeader.addMouseListener(adapter);

            sectionWidgets.put(csd.getKey(), sectionContents);

            topLevelPanels.put(csd, section);
        }

        for (var ctd : cd.getTitles()) {
            var ct = ctd.getTitle();
            final var title = new JPanel();
            title.setLayout(new BoxLayout(title, BoxLayout.Y_AXIS));
            title.setMinimumSize(new Dimension(PANEL_WIDTH, 0));

            final var sectionHeader = new JPanel();
            sectionHeader.setLayout(new BorderLayout());
            sectionHeader.setMinimumSize(new Dimension(PANEL_WIDTH, 0));

            title.add(sectionHeader, BorderLayout.NORTH);

            var name = ct.name();
            final var sectionName = new JLabel(name);
            sectionName.setForeground(BRAND_CRIMSON);
            sectionName.setFont(FontManager.getRunescapeBoldFont());
            sectionName.setToolTipText("<html>" + name + ":<br>" + ct.description() + "</html>");
            sectionName.setBorder(new EmptyBorder(0, 0, 3, 1));
            sectionHeader.add(sectionName, BorderLayout.CENTER);

            final var sectionContents = new JPanel();
            sectionContents.setLayout(new DynamicGridLayout(0, 1, 0, 5));
            sectionContents.setMinimumSize(new Dimension(PANEL_WIDTH, 0));
            sectionContents.setBorder(new EmptyBorder(0, 5, 0, 0));
            title.add(sectionContents, BorderLayout.SOUTH);

            titleWidgets.put(ctd.getKey(), sectionContents);

            // Allow for sub-sections
            var section = sectionWidgets.get(ct.section());
            var titleSection = titleWidgets.get(ct.title());

            if (section != null) {
                section.add(title);
            } else if (titleSection != null) {
                titleSection.add(title);
            } else {
                topLevelPanels.put(ctd, title);
            }
        }

        for (var cid : cd.getItems()) {
            if (!shouldBeHidden(cid)) {
                continue;
            }

            var item = new JPanel();
            item.setLayout(new BorderLayout());
            item.setMinimumSize(new Dimension(PANEL_WIDTH, 0));
            var name = cid.getItem().name();
            var configEntryName = new JLabel(name);
            configEntryName.setForeground(Color.WHITE);
            var description = cid.getItem().description();
            if (!"".equals(description)) {
                configEntryName.setToolTipText("<html>" + name + ":<br>" + description + "</html>");
            }
            PluginListItemImpl.addLabelPopupMenu(configEntryName, createResetMenuItem(pluginConfig, cid));
            item.add(configEntryName, BorderLayout.CENTER);

            if (cid.getType() == Button.class) {
                item.remove(configEntryName);
                item.add(createButton(cd, cid), BorderLayout.CENTER);
            } else if (cid.getType() == ConfigImageResource.class) {
                item.remove(configEntryName);
                item.add(createImageLabel(cd, cid), BorderLayout.CENTER);
            } else if (cid.getType() == boolean.class) {
                item.add(createCheckbox(cd, cid), BorderLayout.EAST);
            } else if (cid.getType() == int.class) {
                item.add(createIntSpinner(cd, cid), BorderLayout.EAST);
            } else if (cid.getType() == double.class) {
                item.add(createDoubleSpinner(cd, cid), BorderLayout.EAST);
            } else if (cid.getType() == String.class) {
                var textField = createTextField(cd, cid);

                if (cid.getItem().parse()) {
                    var parsingLabel = createParseLabel(cid, textField);

                    item.add(configEntryName, BorderLayout.NORTH);
                    item.add(textField, BorderLayout.CENTER);

                    parseLabel(cid.getItem(), parsingLabel, textField.getText());
                    item.add(parsingLabel, BorderLayout.SOUTH);
                } else {
                    item.add(textField, BorderLayout.SOUTH);
                }
            } else if (cid.getType() == Color.class) {
                item.add(createColorPicker(cd, cid), BorderLayout.EAST);
            } else if (cid.getType() == Dimension.class) {
                item.add(createDimension(cd, cid), BorderLayout.EAST);
            } else if (cid.getType() instanceof Class && ((Class<?>) cid.getType()).isEnum()) {
                item.add(createComboBox(cd, cid), BorderLayout.EAST);
            } else if (cid.getType() == Keybind.class || cid.getType() == ModifierlessKeybind.class) {
                item.add(createKeybind(cd, cid), BorderLayout.EAST);
            } else if (cid.getType() instanceof ParameterizedType) {
                var parameterizedType = (ParameterizedType) cid.getType();
                if (parameterizedType.getRawType() == Set.class) {
                    item.add(createList(cd, cid), BorderLayout.SOUTH);
                } else if (parameterizedType.getRawType() == Consumer.class) {
                    item.remove(configEntryName);
                    item.add(createConsumer(cd, cid), BorderLayout.CENTER);
                }
            } else if (cid.getType() == Loadout.class) {
                item.remove(configEntryName);
                item.add(createLoadoutLayout(pluginConfig.getConfig(), name, cd.getGroup().value(), cid.key()), BorderLayout.SOUTH);
            } else if (cid.getType() == ItemConfig.class) {
                item.add(createItemConfigLayout(cd.getGroup().value(), cid.key()), BorderLayout.SOUTH);
            }

            var section = sectionWidgets.get(cid.getItem().section());
            var title = titleWidgets.get(cid.getItem().title());

            if (section != null) {
                section.add(item);
            } else if (title != null) {
                title.add(item);
            } else {
                topLevelPanels.put(cid, item);
            }
        }

        topLevelPanels.values().forEach(mainPanel::add);

        var resetButton = new JButton("Reset");
        resetButton.addActionListener((e) ->
        {
            final var result = JOptionPane.showOptionDialog(resetButton, "Are you sure you want to reset this plugin's configuration?",
                    "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, new String[]{"Yes", "No"}, "No");

            if (result == JOptionPane.YES_OPTION) {
                configManager.setDefaultConfiguration(pluginConfig.getConfig(), true);

                // Reset non-config panel keys
                var plugin = pluginConfig.getPlugin();
                if (plugin != null) {
                    plugin.resetConfiguration();
                }

                rebuild(false);
            }
        });
        mainPanel.add(resetButton);

        var backButton = new JButton("Back");
        backButton.addActionListener(e -> pluginList.getMuxer().popState());
        mainPanel.add(backButton);

        if (refresh) {
            scrollPane.getVerticalScrollBar().setValue(scrollBarPosition);
        } else {
            scrollPane.getVerticalScrollBar().setValue(0);
        }

        revalidate();
    }

    private JButton createConsumer(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        var button = new JButton(cid.getItem().name());
        button.addActionListener((e) ->
        {
            log.debug("Running consumer: {}.{}", cd.getGroup().value(), cid.getItem().keyName());
            configManager.getConsumer(cd.getGroup().value(), cid.getItem().keyName()).accept(pluginConfig.getPlugin());
        });

        return button;
    }

    private JButton createButton(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        var button = new JButton(cid.name());
        button.addActionListener((e) -> {
            var event = new ConfigButtonClicked();
            event.setGroup(cd.getGroup().value());
            event.setKey(cid.getItem().keyName());
            eventBus.post(event);
        });

        return button;
    }

    private JLabel createImageLabel(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        ConfigImageResource image = configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), ConfigImageResource.class);
        if (image == null) {
            log.warn("Image config is null for {}.{}", cd.getGroup().value(), cid.getItem().keyName());
            return new JLabel("No image set");
        }

        var configClass = pluginConfig.getPlugin().getClass();
        var bufferedImage = ImageUtil.loadImageResource(configClass, image.getResourceName());
        if (bufferedImage == null) {
            log.warn("Failed to load image resource: {}", image.getResourceName());
            return new JLabel("Image not found");
        }

        var label = new JLabel();
        label.setIcon(new ImageIcon(bufferedImage));
        label.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        label.setMaximumSize(new Dimension(PANEL_WIDTH, 250));
        label.setToolTipText(cid.getItem().description());

        return label;
    }

    private JCheckBox createCheckbox(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        JCheckBox checkbox = new JCheckBox();
        checkbox.setSelected(Boolean.parseBoolean(configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName())));
        checkbox.addActionListener(ae -> changeConfiguration(checkbox, cd, cid));
        return checkbox;
    }

    private JComponent createIntSpinner(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        int value = MoreObjects.firstNonNull(configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), int.class), 0);

        var range = cid.getRange();
        int min = 0, max = Integer.MAX_VALUE;
        if (range != null) {
            min = range.min();
            max = range.max();
        }

        value = Ints.constrainToRange(value, min, max);

        SpinnerModel model = new SpinnerNumberModel(value, min, max, 1);
        var spinner = new JSpinner(model);
        var editor = spinner.getEditor();
        var spinnerTextField = ((JSpinner.DefaultEditor) editor).getTextField();
        spinnerTextField.setColumns(SPINNER_FIELD_WIDTH);
        spinner.addChangeListener(ce -> changeConfiguration(spinner, cd, cid));

        var units = cid.getUnits();
        if (units != null) {
            var delegate = spinnerTextField.getFormatterFactory();
            spinnerTextField.setFormatterFactory(new UnitFormatterFactory(delegate, units.value()));
        }

        return spinner;
    }

    private JSpinner createDoubleSpinner(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        double value = MoreObjects.firstNonNull(configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), double.class), 0d);

        SpinnerModel model = new SpinnerNumberModel(value, 0, Double.MAX_VALUE, 0.1);
        var spinner = new JSpinner(model);
        Component editor = spinner.getEditor();
        var spinnerTextField = ((JSpinner.DefaultEditor) editor).getTextField();
        spinnerTextField.setColumns(SPINNER_FIELD_WIDTH);
        spinner.addChangeListener(ce -> changeConfiguration(spinner, cd, cid));

        var units = cid.getUnits();
        if (units != null) {
            var delegate = spinnerTextField.getFormatterFactory();
            spinnerTextField.setFormatterFactory(new UnitFormatterFactory(delegate, units.value()));
        }

        return spinner;
    }

    private JTextComponent createTextField(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        JTextComponent textField;

        if (cid.getItem().secret()) {
            textField = new JPasswordField();
        } else {
            final var textArea = new JTextArea();
            textArea.setLineWrap(true);
            textArea.setWrapStyleWord(true);

            if (!cid.getItem().editable()) {
                textArea.setEditable(false);
            }

            textField = textArea;
        }

        textField.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textField.setText(configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName()));

        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                changeConfiguration(textField, cd, cid);
            }
        });

        return textField;
    }

    private JLabel createParseLabel(ConfigItemDescriptor cid, JTextComponent textField) {
        var parsingLabel = new JLabel();
        parsingLabel.setHorizontalAlignment(SwingConstants.CENTER);
        parsingLabel.setPreferredSize(new Dimension(PANEL_WIDTH, 15));

        var listener = new DeferredDocumentChangedListener();
        listener.addChangeListener(e ->
        {
            if (cid.getItem().parse()) {
                parseLabel(cid.getItem(), parsingLabel, textField.getText());
            }
        });
        textField.getDocument().addDocumentListener(listener);

        return parsingLabel;
    }

    private ColorJButton createColorPicker(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        Color existing = configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), Color.class);

        ColorJButton colorPickerBtn;

        var alphaHidden = cid.getAlpha() == null;

        if (existing == null) {
            colorPickerBtn = new ColorJButton("Pick a color", Color.BLACK);
        } else {
            var colorHex = "#" + (alphaHidden ? ColorUtil.colorToHexCode(existing) : ColorUtil.colorToAlphaHexCode(existing)).toUpperCase();
            colorPickerBtn = new ColorJButton(colorHex, existing);
        }

        colorPickerBtn.setFocusable(false);
        colorPickerBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                var colorPicker = colorPickerManager.create(
                        SwingUtilities.windowForComponent(ConfigPanelImpl.this),
                        colorPickerBtn.getColor(),
                        cid.getItem().name(),
                        alphaHidden);
                colorPicker.setLocationRelativeTo(colorPickerBtn);
                colorPicker.setOnColorChange(c ->
                {
                    colorPickerBtn.setColor(c);
                    colorPickerBtn.setText("#" + (alphaHidden ? ColorUtil.colorToHexCode(c) : ColorUtil.colorToAlphaHexCode(c)).toUpperCase());
                });
                colorPicker.setOnClose(c -> changeConfiguration(colorPicker, cd, cid));
                colorPicker.setVisible(true);
            }
        });

        return colorPickerBtn;
    }

    private JPanel createDimension(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        var dimensionPanel = new JPanel();
        dimensionPanel.setLayout(new BorderLayout());

        var dimension = MoreObjects.firstNonNull(configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName(), Dimension.class), new Dimension());
        var width = dimension.width;
        var height = dimension.height;

        SpinnerModel widthModel = new SpinnerNumberModel(width, 0, Integer.MAX_VALUE, 1);
        var widthSpinner = new JSpinner(widthModel);
        Component widthEditor = widthSpinner.getEditor();
        var widthSpinnerTextField = ((JSpinner.DefaultEditor) widthEditor).getTextField();
        widthSpinnerTextField.setColumns(4);

        SpinnerModel heightModel = new SpinnerNumberModel(height, 0, Integer.MAX_VALUE, 1);
        var heightSpinner = new JSpinner(heightModel);
        Component heightEditor = heightSpinner.getEditor();
        var heightSpinnerTextField = ((JSpinner.DefaultEditor) heightEditor).getTextField();
        heightSpinnerTextField.setColumns(4);

        ChangeListener listener = e ->
                configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), widthSpinner.getValue() + "x" + heightSpinner.getValue());

        widthSpinner.addChangeListener(listener);
        heightSpinner.addChangeListener(listener);

        dimensionPanel.add(widthSpinner, BorderLayout.WEST);
        dimensionPanel.add(new JLabel(" x "), BorderLayout.CENTER);
        dimensionPanel.add(heightSpinner, BorderLayout.EAST);

        return dimensionPanel;
    }

    private JComboBox<Enum<?>> createComboBox(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        var type = (Class<? extends Enum>) cid.getType();

        var box = new JComboBox<Enum<?>>(type.getEnumConstants()); // NOPMD: UseDiamondOperator
        // set renderer prior to calling box.getPreferredSize(), since it will invoke the renderer
        // to build components for each combobox element in order to compute the display size of the
        // combobox
        box.setRenderer(listCellRenderer);
        box.setPreferredSize(new Dimension(box.getPreferredSize().width, 25));
        box.setForeground(Color.WHITE);
        box.setFocusable(false);

        try {
            Enum<?> selectedItem = Enum.valueOf(type, configManager.getConfiguration(cd.getGroup().value(), cid.getItem().keyName()));
            box.setSelectedItem(selectedItem);
            box.setToolTipText(Text.titleCase(selectedItem));
        } catch (IllegalArgumentException ex) {
            log.debug("invalid selected item", ex);
        }
        box.addItemListener(e ->
        {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                changeConfiguration(box, cd, cid);
                box.setToolTipText(Text.titleCase((Enum<?>) box.getSelectedItem()));
            }
        });

        return box;
    }

    private HotkeyButton createKeybind(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        Keybind startingValue = configManager.getConfiguration(cd.getGroup().value(),
                cid.getItem().keyName(),
                (Class<? extends Keybind>) cid.getType());

        var button = new HotkeyButton(startingValue, cid.getType() == ModifierlessKeybind.class);

        button.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                changeConfiguration(button, cd, cid);
            }
        });

        return button;
    }

    private JPanel createList(ConfigDescriptor cd, ConfigItemDescriptor cid) {
        var parameterizedType = (ParameterizedType) cid.getType();
        var type = (Class<? extends Enum>) parameterizedType.getActualTypeArguments()[0];
        Set<? extends Enum> set = configManager.getConfiguration(cd.getGroup().value(), null,
                cid.getItem().keyName(), parameterizedType);

        int cols;
        if (cid.getItem().wide()) {
            cols = 1;
        } else {
            cols = 2;
        }

        var layout = new JPanel(new GridLayout(0, cols));
        List<ConfigCheckBox> checkBoxes = new ArrayList<>();

        Set<?> selectedItems = new HashSet<>(Objects.requireNonNullElse(set, Collections.emptySet()));

        for (Object obj : type.getEnumConstants()) {
            var checkbox = new ConfigCheckBox(obj);
            checkbox.setText(Text.titleCase((Enum<?>) obj));
            checkbox.setBackground(ColorScheme.DARK_GRAY_COLOR);
            checkbox.setSelected(selectedItems.contains(obj));
            checkbox.setToolTipText("<html>" + Text.titleCase((Enum<?>) obj) + "</html>");
            checkBoxes.add(checkbox);

            layout.add(checkbox);
        }

        checkBoxes.forEach(checkbox -> checkbox.addActionListener(ae -> changeConfiguration(checkBoxes, cd, cid)));

        return layout;
    }

    private void changeConfiguration(List<ConfigCheckBox> components, ConfigDescriptor cd, ConfigItemDescriptor cid) {
        var values = components
                .stream()
                .filter(ConfigCheckBox::isSelected)
                .map(ConfigCheckBox::getObject)
                .collect(Collectors.toSet());
        configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), values);
    }

    public JPanel createLoadoutLayout(Config config, String title, String group, String key) {
        var loadoutLayout = new JPanel(new GridBagLayout());
        loadoutLayout.setBorder(new TitledBorder(title));

        var button = new JButton("Import");
        button.addActionListener(a -> loadoutImporter.init(group, key));
        addComponent(button, loadoutLayout, 0, 0, 1, 1);

        var edit = new JButton("Edit");
        edit.addActionListener(e -> onLoadoutEdit(group, key));
        addComponent(edit, loadoutLayout, 1, 0, 1, 1);

        var reset = new JButton("Reset");
        reset.addActionListener(e -> {
            var yesNo = JOptionPane.showConfirmDialog(this, "Are you sure you want to reset this loadout?");
            if (yesNo != JOptionPane.YES_OPTION) {
                return;
            }

            configManager.unsetConfiguration(group, key);
            configManager.setDefaultConfiguration(config, false);
        });
        addComponent(reset, loadoutLayout, 2, 0, 1, 1);

        var grabSetup = new JButton("Import current setup");
        grabSetup.addActionListener(e -> onLoadoutImport(group, key));
        addComponent(grabSetup, loadoutLayout, 0, 1, 3, 1);

        return loadoutLayout;
    }

    private JPanel createItemConfigLayout(String group, String key) {
        var layout = new JPanel(new BorderLayout());
        ItemConfig itemConfig = configManager.getConfiguration(group, key, ItemConfig.class);

        var label = new JLabel("No Item Selected");
        if (itemConfig != null) {
            label.setText(itemConfig.getName());
            var image = CACHED_ITEM_IMAGES.computeIfAbsent(itemConfig.getId(), itemManager::getImage);
            image.addTo(label);
        }

        layout.add(label, BorderLayout.CENTER);

        var edit = new JButton("Search an Item");
        edit.addActionListener(e -> itemSelectorProvider.get().init(group, key, true));

        layout.add(edit, BorderLayout.SOUTH);

        return layout;
    }

    private Boolean parse(ConfigItem item, String value) {
        try {
            var parse = item.clazz().getMethod(item.method(), String.class);

            return (boolean) parse.invoke(null, value);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ex) {
            log.error("Parsing failed: {}", ex.getMessage());
        }

        return null;
    }

    private void parseLabel(ConfigItem item, JLabel label, String value) {
        var result = parse(item, value);

        if (result == null) {
            label.setForeground(Color.RED);
            label.setText("Parsing failed");
        } else if (result) {
            label.setForeground(Color.GREEN);
            label.setText("Valid input");
        } else {
            label.setForeground(Color.RED);
            label.setText("Invalid input");
        }
    }

    private void changeConfiguration(Component component, ConfigDescriptor cd, ConfigItemDescriptor cid) {
        final var configItem = cid.getItem();

        if (!Strings.isNullOrEmpty(configItem.warning())) {
            final var result = JOptionPane.showOptionDialog(component, configItem.warning(),
                    "Are you sure?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE,
                    null, new String[]{"Yes", "No"}, "No");

            if (result != JOptionPane.YES_OPTION) {
                rebuild(false);
                return;
            }
        }

        skipRebuild = true;

        if (component instanceof JCheckBox) {
            var checkbox = (JCheckBox) component;
            configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), "" + checkbox.isSelected());
        } else if (component instanceof JSpinner) {
            var spinner = (JSpinner) component;
            configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), "" + spinner.getValue());
        } else if (component instanceof JSlider) {
            var slider = (JSlider) component;
            configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), slider.getValue());
        } else if (component instanceof JTextComponent) {
            var textField = (JTextComponent) component;
            configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), textField.getText());
        } else if (component instanceof RuneliteColorPicker) {
            var colorPicker = (RuneliteColorPicker) component;
            configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), colorPicker.getSelectedColor().getRGB() + "");
        } else if (component instanceof JComboBox) {
            var jComboBox = (JComboBox) component;
            configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), ((Enum) jComboBox.getSelectedItem()).name());
        } else if (component instanceof HotkeyButton) {
            var hotkeyButton = (HotkeyButton) component;
            configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), hotkeyButton.getValue());
        } else if (component instanceof JList) {
            var list = (JList<?>) component;
            var selectedValues = list.getSelectedValuesList();

            configManager.setConfiguration(cd.getGroup().value(), cid.getItem().keyName(), Sets.newHashSet(selectedValues));
        }

        if (enableDisable(component, cid) || hideUnhide(component, cd, cid)) {
            rebuild(true);
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PANEL_WIDTH + SCROLLBAR_WIDTH, super.getPreferredSize().height);
    }

    @Subscribe
    public void onPluginChanged(PluginChanged event) {
        if (event.getPlugin() == this.pluginConfig.getPlugin()) {
            SwingUtilities.invokeLater(() ->
            {
                pluginToggle.setSelected(event.isLoaded());
            });
        }
    }

    @Subscribe
    private void onExternalPluginsChanged(ExternalPluginsChanged ev) {
        if (pluginManager.getPlugins().stream()
                .noneMatch(p -> p == this.pluginConfig.getPlugin())) {
            pluginList.getMuxer().popState();
        }
        SwingUtilities.invokeLater(() -> rebuild(false));
    }

    @Subscribe
    private void onConfigChanged(ConfigChanged event) {
        if (pluginConfig.getConfigDescriptor() == null) {
            return;
        }

        if (!skipRebuild && pluginConfig.getConfigDescriptor().getGroup().value().equals(event.getGroup())) {
            SwingUtilities.invokeLater(() -> rebuild(true));
        }

        skipRebuild = false;
    }

    @Subscribe
    private void onProfileChanged(ProfileChanged profileChanged) {
        SwingUtilities.invokeLater(() -> rebuild(false));
    }

    @Subscribe
    private void onPluginToggleHiddenChanged(PluginToggleHiddenChanged e) {
        var plugin = pluginConfig.getPlugin();
        if (plugin != null) {
            SwingUtilities.invokeLater(() -> pluginToggle.setVisible(!plugin.isToggleHidden()));
        }
    }

    private JMenuItem createResetMenuItem(PluginConfigurationDescriptor pluginConfig, ConfigItemDescriptor configItemDescriptor) {
        var menuItem = new JMenuItem("Reset");
        menuItem.addActionListener(e ->
        {
            var configDescriptor = pluginConfig.getConfigDescriptor();
            var configGroup = configDescriptor.getGroup();
            var configItem = configItemDescriptor.getItem();

            // To reset one item we'll just unset it and then apply defaults over the whole group
            configManager.unsetConfiguration(configGroup.value(), configItem.keyName());
            configManager.setDefaultConfiguration(pluginConfig.getConfig(), false);

            rebuild(false);
        });
        return menuItem;
    }

    private boolean hideUnhide(Component component, ConfigDescriptor cd, ConfigItemDescriptor cid) {
        var rebuild = false;

        if (component instanceof JCheckBox) {
            var checkbox = (JCheckBox) component;

            for (var cid2 : cd.getItems()) {
                if (cid2.getItem().hidden() || !cid2.getItem().hide().isEmpty()) {
                    var itemHide = Splitter
                            .onPattern("\\|\\|")
                            .trimResults()
                            .omitEmptyStrings()
                            .splitToList(String.format("%s || %s", cid2.getItem().unhide(), cid2.getItem().hide()));

                    if (itemHide.contains(cid.getItem().keyName())) {
                        rebuild = true;
                    }
                }

                if (checkbox.isSelected()) {
                    if (cid2.getItem().enabledBy().contains(cid.getItem().keyName())) {
                        skipRebuild = true;
                        configManager.setConfiguration(cd.getGroup().value(), cid2.getItem().keyName(), "true");
                        rebuild = true;
                    } else if (cid2.getItem().disabledBy().contains(cid.getItem().keyName())) {
                        skipRebuild = true;
                        configManager.setConfiguration(cd.getGroup().value(), cid2.getItem().keyName(), "false");
                        rebuild = true;
                    }
                }
            }
        } else if (component instanceof JComboBox) {
            var jComboBox = (JComboBox) component;

            for (var cid2 : cd.getItems()) {
                if (cid2.getItem().hidden() || !cid2.getItem().hide().isEmpty()) {
                    var itemHide = Splitter
                            .onPattern("\\|\\|")
                            .trimResults()
                            .omitEmptyStrings()
                            .splitToList(String.format("%s || %s", cid2.getItem().unhide(), cid2.getItem().hide()));

                    var changedVal = ((Enum) jComboBox.getSelectedItem()).name();

                    if (cid2.getItem().enabledBy().contains(cid.getItem().keyName()) && cid2.getItem().enabledByValue().equals(changedVal)) {
                        skipRebuild = true;
                        configManager.setConfiguration(cd.getGroup().value(), cid2.getItem().keyName(), "true");
                        rebuild = true;
                    } else if (cid2.getItem().disabledBy().contains(cid.getItem().keyName()) && cid2.getItem().disabledByValue().equals(changedVal)) {
                        skipRebuild = true;
                        configManager.setConfiguration(cd.getGroup().value(), cid2.getItem().keyName(), "false");
                        rebuild = true;
                    } else if (itemHide.contains(cid.getItem().keyName())) {
                        rebuild = true;
                    }
                }
            }
        } else if (component instanceof JList) {
            var jList = (JList) component;

            for (var cid2 : cd.getItems()) {
                if (cid2.getItem().hidden() || !cid2.getItem().hide().isEmpty()) {
                    var itemHide = Splitter
                            .onPattern("\\|\\|")
                            .trimResults()
                            .omitEmptyStrings()
                            .splitToList(String.format("%s || %s", cid2.getItem().unhide(), cid2.getItem().hide()));

                    var changedVal = String.valueOf((jList.getSelectedValues()));

                    if (cid2.getItem().enabledBy().contains(cid.getItem().keyName()) && cid2.getItem().enabledByValue().equals(changedVal)) {
                        skipRebuild = true;
                        configManager.setConfiguration(cd.getGroup().value(), cid2.getItem().keyName(), "true");
                        rebuild = true;
                    } else if (cid2.getItem().disabledBy().contains(cid.getItem().keyName()) && cid2.getItem().disabledByValue().equals(changedVal)) {
                        skipRebuild = true;
                        configManager.setConfiguration(cd.getGroup().value(), cid2.getItem().keyName(), "false");
                        rebuild = true;
                    } else if (itemHide.contains(cid.getItem().keyName())) {
                        rebuild = true;
                    }
                }
            }
        }

        return rebuild;
    }

    private boolean shouldBeHidden(ConfigItemDescriptor cid) {
        var cd = pluginConfig.getConfigDescriptor();

        var unhide = cid.getItem().hidden();
        var hide = !cid.getItem().hide().isEmpty();

        if (unhide || hide) {
            var show = false;

            var itemHide = Splitter
                    .onPattern("\\|\\|")
                    .trimResults()
                    .omitEmptyStrings()
                    .splitToList(String.format("%s || %s", cid.getItem().unhide(), cid.getItem().hide()));

            for (var cid2 : cd.getItems()) {
                if (itemHide.contains(cid2.getItem().keyName())) {
                    if (cid2.getType() == boolean.class) {
                        show = Boolean.parseBoolean(configManager.getConfiguration(cd.getGroup().value(), cid2.getItem().keyName()));
                    } else if (cid2.getType() instanceof Class && ((Class<?>) cid2.getType()).isEnum()) {
                        var type = (Class<? extends Enum>) cid2.getType();
                        try {
                            var selectedItem = Enum.valueOf(type, configManager.getConfiguration(cd.getGroup().value(), cid2.getItem().keyName()));
                            if (!cid.getItem().unhideValue().equals("")) {
                                var unhideValue = Splitter
                                        .onPattern("\\|\\|")
                                        .trimResults()
                                        .omitEmptyStrings()
                                        .splitToList(cid.getItem().unhideValue());

                                show = unhideValue.contains(selectedItem.toString());
                            } else if (!cid.getItem().hideValue().equals("")) {
                                var hideValue = Splitter
                                        .onPattern("\\|\\|")
                                        .trimResults()
                                        .omitEmptyStrings()
                                        .splitToList(cid.getItem().hideValue());

                                show = !hideValue.contains(selectedItem.toString());
                            }
                        } catch (IllegalArgumentException ignored) {
                        }
                    }
                }
            }

            return (!unhide || show) && (!hide || !show);
        }

        return true;
    }

    private boolean enableDisable(Component component, ConfigItemDescriptor cid) {
        var rebuild = false;

        var cd = pluginConfig.getConfigDescriptor();

        if (component instanceof JCheckBox) {
            var checkbox = (JCheckBox) component;

            for (var cid2 : cd.getItems()) {
                if (checkbox.isSelected()) {
                    if (cid2.getItem().enabledBy().contains(cid.getItem().keyName())) {
                        skipRebuild = true;
                        configManager.setConfiguration(cd.getGroup().value(), cid2.getItem().keyName(), "true");
                        rebuild = true;
                    } else if (cid2.getItem().disabledBy().contains(cid.getItem().keyName())) {
                        skipRebuild = true;
                        configManager.setConfiguration(cd.getGroup().value(), cid2.getItem().keyName(), "false");
                        rebuild = true;
                    }
                }
            }
        } else if (component instanceof JComboBox) {
            var jComboBox = (JComboBox) component;

            for (var cid2 : cd.getItems()) {
                var changedVal = ((Enum) jComboBox.getSelectedItem()).name();

                if (cid2.getItem().enabledBy().contains(cid.getItem().keyName()) && cid2.getItem().enabledByValue().equals(changedVal)) {
                    skipRebuild = true;
                    configManager.setConfiguration(cd.getGroup().value(), cid2.getItem().keyName(), "true");
                    rebuild = true;
                } else if (cid2.getItem().disabledBy().contains(cid.getItem().keyName()) && cid2.getItem().disabledByValue().equals(changedVal)) {
                    skipRebuild = true;
                    configManager.setConfiguration(cd.getGroup().value(), cid2.getItem().keyName(), "false");
                    rebuild = true;
                }
            }
        }

        return rebuild;
    }

    private static void addComponent(Component comp, JPanel panel, int x, int y, int w, int h) {
        GridBagConstraints cons = new GridBagConstraints();
        cons.weightx = 1.0;
        cons.fill = GridBagConstraints.BOTH;
        cons.gridx = x;
        cons.gridy = y;
        cons.gridwidth = w;
        cons.gridheight = h;
        panel.add(comp, cons);
    }

    private void onLoadoutEdit(String group, String key) {
        LoadoutImpl loadout = configManager.getConfiguration(group, key, Loadout.class);
        new LoadoutEditor(eventBus, itemManager, itemSelectorProvider.get(), configManager, clientThread)
                .init(loadout, group, key);
    }

    private void onLoadoutImport(String group, String key) {
        var yesNo = JOptionPane.showConfirmDialog(this,
                "This will grab your current inv/gear/runepouch and overwrite your loadout, continue?");
        if (yesNo != JOptionPane.YES_OPTION) {
            return;
        }

        LoadoutImpl loadout = configManager.getConfiguration(group, key, Loadout.class);

        var builder = loadoutFactory.newBuilder();
        var hasStackables = false;
        if (!loadout.isInventoryDisabled()) {
            var currentInv = loadoutFactory.fromCurrentInventory();
            if (hasStackables(currentInv.getInventory())) {
                hasStackables = true;
            }

            var filtered = Arrays.stream(currentInv.getInventory())
                    .filter(item -> item != null && !item.isStackable())
                    .toArray(LoadoutItem[]::new);
            builder.items(filtered);
        } else {
            builder.disableInventory();
        }

        if (!loadout.isEquipmentDisabled()) {
            var currentEquipment = loadoutFactory.fromCurrentEquipment();
            if (hasStackables(currentEquipment.getEquipment())) {
                hasStackables = true;
            }

            var filtered = Arrays.stream(currentEquipment.getEquipment())
                    .filter(item -> item != null && !item.isStackable())
                    .toArray(LoadoutItem[]::new);
            builder.items(filtered);
        } else {
            builder.disableEquipment();
        }

        if (loadout.isRunePouchDisabled()) {
            builder.disableRunePouch();
        }

        if (hasStackables) {
            JOptionPane.showMessageDialog(this, "Your current gear contains stackable items. " +
                                                "For an optimal setup, you will need to manually add these items to your " +
                                                "loadout by pressing the edit button.");
        }

        var newLoadout = builder.build();
        configManager.setConfiguration(group, key, newLoadout);
    }

    private boolean hasStackables(LoadoutItem[] items) {
        for (var item : items) {
            if (item != null && item.isStackable()) {
                return true;
            }
        }

        return false;
    }
}
