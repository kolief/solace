/*
 * Copyright (c) 2023 Abex
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

import com.google.inject.Provider;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.materialtabs.MaterialTab;
import net.runelite.client.ui.components.materialtabs.MaterialTabGroup;
import net.runelite.client.util.ImageUtil;
import net.solace.api.plugins.Plugin;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.ConfigPanel;
import net.solace.api.plugins.config.PluginConfigurationDescriptor;
import net.solace.api.plugins.config.PluginListPanel;
import net.solace.api.plugins.config.SolaceConfig;
import net.solace.loader.config.SolaceProperties;
import net.solace.ui.sdn.SdnPluginManagerPanel;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;

public class TopLevelConfigPanel extends PluginPanel {
    private final MaterialTabGroup tabGroup;
    private final CardLayout layout;
    private final JPanel content;
    private final JPanel footer;

    private final EventBus eventBus;
    private final PluginListPanel pluginListPanel;
    private final Provider<ConfigPanel> configPanelProvider;

    private final MaterialTab pluginListPanelTab;
    private final PluginConfigurationDescriptor solaceConfigDescriptor;

    private boolean active = false;
    private PluginPanel current;
    private boolean removeOnTabChange;

    public TopLevelConfigPanel(
            EventBus eventBus,
            PluginListPanel pluginListPanel,
            SdnPluginManagerPanel sdnPanel,
            Provider<ConfigPanel> configPanelProvider,
            SolaceConfig solaceConfig,
            ConfigManager configManager,
            ProfilePanel profilePanel
    ) {
        super(false);

        this.eventBus = eventBus;
        this.pluginListPanel = pluginListPanel;
        this.configPanelProvider = configPanelProvider;

        tabGroup = new MaterialTabGroup();
        tabGroup.setLayout(new GridLayout(1, 0, 7, 7));
        tabGroup.setBorder(new EmptyBorder(10, 10, 0, 10));

        content = new JPanel();
        layout = new CardLayout();
        content.setLayout(layout);

        footer = new JPanel();
        footer.setLayout(new BorderLayout());
        var runeLiteVersion = SolaceProperties.RUNELITE_VERSION;
        var solaceVersion = SolaceProperties.LOADER_VERSION;
        var footerLabel = new JLabel("Solace " + solaceVersion + " for RuneLite " + runeLiteVersion);
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        footer.add(footerLabel, BorderLayout.NORTH);
        var hash = SolaceProperties.COMMIT_HASH;
        var hashTruncated = hash.length() > 7 ? hash.substring(0, 7) : hash;
        var hashLabel = new JLabel(hashTruncated);
        hashLabel.setHorizontalAlignment(JLabel.CENTER);
        footer.add(hashLabel, BorderLayout.CENTER);

        var proxyHost = System.getProperty("socksProxyHost");
        var proxyPort = System.getProperty("socksProxyPort");
        if (proxyHost != null && proxyPort != null) {
            var proxyInfo = new JLabel("Proxy: " + proxyHost + ":" + proxyPort);
            proxyInfo.setHorizontalAlignment(JLabel.CENTER);
            footer.add(proxyInfo, BorderLayout.SOUTH);
        }

        setLayout(new BorderLayout());
        add(tabGroup, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
        add(footer, BorderLayout.SOUTH);

        solaceConfigDescriptor = new PluginConfigurationDescriptor(
                "Solace Settings",
                "Solace settings",
                new String[]{"solace"},
                solaceConfig,
                configManager.getConfigDescriptor(solaceConfig)
        );

        pluginListPanelTab = addTabIcon(pluginListPanel.getMuxer(), "plugin.png", "My plugins");
        addSolaceConfigTab();
        addTabIcon(sdnPanel, "download.png", "Download plugins");
        addTabIcon(profilePanel, "profiles.png", "Profiles");

        tabGroup.select(pluginListPanelTab);
    }

    private MaterialTab addTab(PluginPanel panel, String text, String tooltip) {
        var mt = new MaterialTab(text, tabGroup, null);
        mt.setToolTipText(tooltip);
        tabGroup.addTab(mt);

        content.add(text, panel.getWrappedPanel());
        eventBus.register(panel);

        mt.setOnSelectEvent(() ->
        {
            switchTo(text, panel);
            return true;
        });
        return mt;
    }

    private MaterialTab addTabIcon(PluginPanel panel, String image, String tooltip) {
        var mt = new MaterialTab(
                new ImageIcon(ImageUtil.loadImageResource(TopLevelConfigPanel.class, image)),
                tabGroup,
                null
        );
        mt.setToolTipText(tooltip);
        tabGroup.addTab(mt);

        content.add(image, panel.getWrappedPanel());
        eventBus.register(panel);

        mt.setOnSelectEvent(() ->
        {
            switchTo(image, panel);
            return true;
        });
        return mt;
    }

    private void addSolaceConfigTab() {
        var mt = new MaterialTab(
                new ImageIcon(ImageUtil.loadImageResource(TopLevelConfigPanel.class, "settings.png")),
                tabGroup,
                null
        );
        mt.setToolTipText("Solace settings");
        tabGroup.addTab(mt);

        mt.setOnSelectEvent(() -> {
            var solaceConfigPanel = configPanelProvider.get();
            solaceConfigPanel.init(solaceConfigDescriptor, false);
            content.add("Solace settings", solaceConfigPanel);
            switchTo("Solace settings", solaceConfigPanel);
            return true;
        });
    }

    private void switchTo(String cardName, PluginPanel panel) {
        var doRemove = this.removeOnTabChange;
        var prevPanel = current;
        if (active) {
            prevPanel.onDeactivate();
            panel.onActivate();
        }

        current = panel;
        this.removeOnTabChange = false;

        layout.show(content, cardName);

        if (doRemove) {
            content.remove(prevPanel.getWrappedPanel());
            eventBus.unregister(prevPanel);
        }

        content.revalidate();
    }

    @Override
    public void onActivate() {
        active = true;
        current.onActivate();
    }

    @Override
    public void onDeactivate() {
        active = false;
        current.onDeactivate();
    }

    public void openConfigurationPanel(String name) {
        tabGroup.select(pluginListPanelTab);
        pluginListPanel.openConfigurationPanel(name);
    }

    public void openConfigurationPanel(Plugin plugin) {
        tabGroup.select(pluginListPanelTab);
        pluginListPanel.openConfigurationPanel(plugin);
    }
}
