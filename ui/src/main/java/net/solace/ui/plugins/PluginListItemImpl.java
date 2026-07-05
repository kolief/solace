/*
 * Copyright (c) 2018, Daniel Teo <https://github.com/takuyakanbr>
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

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.SwingUtil;
import net.solace.sdn.SdnPluginManager;
import net.solace.api.plugins.config.PluginConfigurationDescriptor;
import net.solace.api.plugins.config.PluginListItem;
import net.solace.api.plugins.config.PluginListPanel;
import net.solace.api.plugins.config.PluginToggleButton;
import net.solace.api.plugins.exception.PluginInstantiationException;
import net.solace.api.ui.ColorScheme;
import net.solace.loader.events.SdnPluginUpdated;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.MouseInfo;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
public class PluginListItemImpl extends PluginListItem {
    private static final ImageIcon CONFIG_ICON;
    private static final ImageIcon CONFIG_ICON_HOVER;
    private static final ImageIcon REFRESH_ICON;
    private static final ImageIcon REFRESH_ICON_HOVER;
    private static final ImageIcon ON_STAR;
    private static final ImageIcon OFF_STAR;
    private static final ImageIcon ALERT;
    private static final ImageIcon ALERT_HOVER;

    static {
        var configIcon = ImageUtil.loadImageResource(ConfigPanelImpl.class, "config_edit_icon.png");
        var refreshIcon = ImageUtil.loadImageResource(ConfigPanelImpl.class, "refresh.png");
        var onStar = ImageUtil.loadImageResource(ConfigPanelImpl.class, "star_on.png");
        var alert = ImageUtil.loadImageResource(ConfigPanelImpl.class, "mdi_alert.png");

        CONFIG_ICON = new ImageIcon(configIcon);
        REFRESH_ICON = new ImageIcon(refreshIcon);
        ON_STAR = new ImageIcon(ImageUtil.recolorImage(onStar, ColorScheme.BRAND_CRIMSON));
        CONFIG_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(configIcon, -100));
        REFRESH_ICON_HOVER = new ImageIcon(ImageUtil.luminanceOffset(refreshIcon, -100));
        ALERT = new ImageIcon(alert);
        ALERT_HOVER = new ImageIcon(ImageUtil.luminanceOffset(alert, -100));

        var offStar = ImageUtil.luminanceScale(
                ImageUtil.grayscaleImage(onStar),
                0.77f
        );
        OFF_STAR = new ImageIcon(offStar);
    }

    private final PluginListPanel pluginListPanel;
    @Getter
    private final PluginConfigurationDescriptor pluginConfig;
    @Getter
    private final List<String> keywords = new ArrayList<>();
    private final JToggleButton pinButton;
    private final PluginToggleButton onOffToggle;

    public PluginListItemImpl(
            PluginListPanel pluginListPanel,
            PluginConfigurationDescriptor pluginConfig,
            SdnPluginManager sdnPluginManager,
            SdnPluginUpdated update
    ) {
        this.pluginListPanel = pluginListPanel;
        this.pluginConfig = pluginConfig;

        Collections.addAll(keywords, pluginConfig.getName().toLowerCase().split(" "));
        Collections.addAll(keywords, pluginConfig.getDescription().toLowerCase().split(" "));
        Collections.addAll(keywords, pluginConfig.getTags());
        var internalName = pluginConfig.getInternalPluginHubName();
        if (internalName != null) {
            keywords.add("pluginhub");
            keywords.add(internalName);
        } else {
            keywords.add("plugin"); // we don't want searching plugin to only show hub plugins
        }

        setLayout(new BorderLayout(3, 0));
        setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH, 20));

        var name = pluginConfig.getName();
        var nameLabel = new JLabel(name);
        nameLabel.setForeground(Color.WHITE);

        if (!pluginConfig.getDescription().isEmpty()) {
            nameLabel.setToolTipText("<html>" + pluginConfig.getName() + ":<br>" + pluginConfig.getDescription() + "</html>");
        }

        pinButton = new JToggleButton(OFF_STAR);
        pinButton.setSelectedIcon(ON_STAR);
        SwingUtil.removeButtonDecorations(pinButton);
        SwingUtil.addModalTooltip(pinButton, "Unpin plugin", "Pin plugin");
        pinButton.setPreferredSize(new Dimension(21, 0));
        add(pinButton, BorderLayout.LINE_START);

        pinButton.addActionListener(e -> {
            pluginListPanel.savePinnedPlugins();
            pluginListPanel.refresh();
        });

        final var buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(1, 2));
        add(buttonPanel, BorderLayout.LINE_END);
        if (update != null) {
            var lastVersion = update.getLastVersion();
            var updateButton = new JButton(ALERT);
            updateButton.setRolloverIcon(ALERT_HOVER);
            SwingUtil.removeButtonDecorations(updateButton);
            updateButton.setPreferredSize(new Dimension(25, 0));
            updateButton.setVisible(true);
            updateButton.addActionListener(e ->
                    JOptionPane.showMessageDialog(
                            null,
                            "'" + pluginConfig.getName() + "' version " + lastVersion + " is available." +
                                    " Either restart the client, or uninstall and re-install this plugin to get" +
                                    " the latest version!",
                            "Plugin Update",
                            JOptionPane.INFORMATION_MESSAGE
                    ));
            buttonPanel.add(updateButton);
        }

        var plugin = pluginConfig.getPlugin();
        if (sdnPluginManager.isDevMode()
                && plugin != null
                && sdnPluginManager.getPluginsInfoMap().containsKey(plugin.getName())
        ) {
            var hotSwapButton = new JButton(REFRESH_ICON);
            hotSwapButton.setRolloverIcon(REFRESH_ICON_HOVER);
            SwingUtil.removeButtonDecorations(hotSwapButton);
            hotSwapButton.setPreferredSize(new Dimension(25, 0));
            hotSwapButton.setVisible(false);
            buttonPanel.add(hotSwapButton);

            hotSwapButton.addActionListener(e -> {
                var pluginInfo = sdnPluginManager.getPluginsInfoMap().get(plugin.getName());
                var pluginId = pluginInfo.get("id");

                hotSwapButton.setIcon(REFRESH_ICON);

                new SwingWorker<>() {
                    @Override
                    protected Boolean doInBackground() {
                        return sdnPluginManager.uninstall(pluginId);
                    }

                    @Override
                    protected void done() {
                        new SwingWorker<>() {
                            @Override
                            protected Boolean doInBackground() {
                                return sdnPluginManager.reloadStart(pluginId);
                            }

                            @Override
                            protected void done() {
                                pluginListPanel.rebuildPluginList();
                            }
                        }.execute();
                    }
                }.execute();
            });

            hotSwapButton.setVisible(true);
            hotSwapButton.setToolTipText("Hotswap plugin");
        }

        JMenuItem configMenuItem = null;
        if (pluginConfig.getConfigDescriptor() != null) {
            var configButton = new JButton(CONFIG_ICON);
            configButton.setRolloverIcon(CONFIG_ICON_HOVER);
            SwingUtil.removeButtonDecorations(configButton);
            configButton.setPreferredSize(new Dimension(25, 0));
            configButton.setVisible(false);
            buttonPanel.add(configButton);

            configButton.addActionListener(e ->
            {
                configButton.setIcon(CONFIG_ICON);
                openGroupConfigPanel();
            });

            configButton.setVisible(true);
            configButton.setToolTipText("Edit plugin configuration");

            configMenuItem = new JMenuItem("Configure");
            configMenuItem.addActionListener(e -> openGroupConfigPanel());
        }

        addLabelPopupMenu(nameLabel, configMenuItem, pluginConfig.createSupportMenuItem(), null);
        add(nameLabel, BorderLayout.CENTER);

        onOffToggle = new PluginToggleButton();
        onOffToggle.setConflicts(pluginConfig.getConflicts());
        buttonPanel.add(onOffToggle);
        if (plugin != null) {
            onOffToggle.setVisible(!plugin.isToggleHidden());
            onOffToggle.addActionListener(i ->
            {
                if (onOffToggle.isSelected()) {
                    try {
                        pluginListPanel.startPlugin(plugin);
                    } catch (PluginInstantiationException e) {
                        log.error("Error starting plugin", e);
                        onOffToggle.setSelected(false);
                    }
                } else {
                    try {
                        pluginListPanel.stopPlugin(plugin);
                    } catch (PluginInstantiationException e) {
                        log.error("Error stopping plugin", e);
                        onOffToggle.setSelected(true);
                    }
                }
            });
        } else {
            onOffToggle.setVisible(false);
        }
    }

    /**
     * Adds a mouseover effect to change the text of the passed label to {@link ColorScheme#BRAND_CRIMSON} color, and
     * adds the passed menu items to a popup menu shown when the label is clicked.
     *
     * @param label     The label to attach the mouseover and click effects to
     * @param menuItems The menu items to be shown when the label is clicked
     */
    public static void addLabelPopupMenu(JLabel label, JMenuItem... menuItems) {
        final var menu = new JPopupMenu();
        final var labelForeground = label.getForeground();
        menu.setBorder(new EmptyBorder(5, 5, 5, 5));

        for (final var menuItem : menuItems) {
            if (menuItem == null) {
                continue;
            }

            // Some machines register mouseEntered through a popup menu, and do not register mouseExited when a popup
            // menu item is clicked, so reset the label's color when we click one of these options.
            menuItem.addActionListener(e -> label.setForeground(labelForeground));
            menu.add(menuItem);
        }

        label.addMouseListener(new MouseAdapter() {
            private Color lastForeground;

            @Override
            public void mouseClicked(MouseEvent mouseEvent) {
                var source = (Component) mouseEvent.getSource();
                var location = MouseInfo.getPointerInfo().getLocation();
                SwingUtilities.convertPointFromScreen(location, source);
                menu.show(source, location.x, location.y);
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {
                lastForeground = label.getForeground();
                label.setForeground(ColorScheme.BRAND_CRIMSON);
            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {
                label.setForeground(lastForeground);
            }
        });
    }

    @Override
    public String getSearchableName() {
        return pluginConfig.getName();
    }

    @Override
    public boolean isPinned() {
        return pinButton.isSelected();
    }

    public void setPinned(boolean pinned) {
        pinButton.setSelected(pinned);
    }

    public void setPluginEnabled(boolean enabled) {
        onOffToggle.setSelected(enabled);
    }

    private void openGroupConfigPanel() {
        pluginListPanel.openConfigurationPanel(pluginConfig);
    }
}
