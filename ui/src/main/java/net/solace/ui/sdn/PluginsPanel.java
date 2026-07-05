package net.solace.ui.sdn;

import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.IconTextField;
import net.runelite.client.ui.components.shadowlabel.JShadowedLabel;
import net.runelite.client.util.ImageUtil;
import net.solace.sdn.SdnPluginManager;
import net.solace.sdn.update.UpdateManager;
import net.solace.api.plugins.config.DeferredDocumentChangedListener;
import net.solace.api.ui.ColorScheme;
import net.solace.api.util.SwingUtil;
import net.solace.loader.events.SdnLoaded;
import net.solace.loader.events.SdnPluginChanged;
import net.solace.sdn.pf4j.PluginNotUsableException;
import org.apache.commons.text.similarity.JaroWinklerDistance;
import org.pf4j.VersionManager;
import org.pf4j.update.PluginInfo;
import org.pf4j.update.VerifyException;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Slf4j
public class PluginsPanel extends JPanel {
    private static final JaroWinklerDistance DISTANCE = new JaroWinklerDistance();

    private static final ImageIcon ADD_ICON;
    private static final ImageIcon ADD_HOVER_ICON;
    private static final ImageIcon DELETE_ICON;
    private static final ImageIcon DELETE_HOVER_ICON;
    private static final ImageIcon DELETE_ICON_GRAY;
    private static final ImageIcon DELETE_HOVER_ICON_GRAY;

    static {
        final var addIcon =
                ImageUtil.recolorImage(
                        ImageUtil.loadImageResource(PluginsPanel.class, "add_icon.png"), ColorScheme.BRAND_CRIMSON
                );
        ADD_ICON = new ImageIcon(addIcon);
        ADD_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(addIcon, 0.53f));

        final var deleteImg =
                ImageUtil.recolorImage(
                        ImageUtil.resizeCanvas(
                                ImageUtil.loadImageResource(PluginsPanel.class, "delete_icon.png"), 14, 14
                        ), ColorScheme.BRAND_CRIMSON
                );
        DELETE_ICON = new ImageIcon(deleteImg);
        DELETE_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(deleteImg, 0.53f));

        DELETE_ICON_GRAY = new ImageIcon(ImageUtil.grayscaleImage(deleteImg));
        DELETE_HOVER_ICON_GRAY = new ImageIcon(ImageUtil.alphaOffset(ImageUtil.grayscaleImage(deleteImg), 0.53f));
    }

    private final SdnPluginManager sdnPluginManager;
    private final VersionManager versionManager;
    private final UpdateManager updateManager;

    private final IconTextField searchBar = new IconTextField();
    private final JPanel filterwrapper = new JPanel(new BorderLayout(0, 10));
    private final List<PluginInfo> installedPluginsList = new ArrayList<>();
    private final List<PluginInfo> availablePluginsList = new ArrayList<>();
    private final JPanel installedPluginsPanel = new JPanel(new GridBagLayout());
    private final JPanel availablePluginsPanel = new JPanel(new GridBagLayout());

    private Set<String> deps;

    PluginsPanel(SdnPluginManager sdnPluginManager, EventBus eventBus) {
        this.sdnPluginManager = sdnPluginManager;
        this.versionManager = sdnPluginManager.getPluginManager().getVersionManager();
        this.updateManager = sdnPluginManager.getUpdateManager();

        setLayout(new BorderLayout(0, 10));
        setBackground(net.runelite.client.ui.ColorScheme.DARK_GRAY_COLOR);

        buildFilter();

        var mainTabPane = new JTabbedPane();

        mainTabPane.add("Installed", SdnPluginManagerPanel.wrapContainer(installedPluginsPanel()));
        mainTabPane.add("Available", SdnPluginManagerPanel.wrapContainer(availablePluginsPanel()));

        add(filterwrapper, BorderLayout.NORTH);
        add(mainTabPane, BorderLayout.CENTER);

        eventBus.register(this);

        reloadPlugins();
    }

    private void buildFilter() {
        filterwrapper.removeAll();

        var listener = new DeferredDocumentChangedListener();
        listener.addChangeListener(e ->
        {
            installedPlugins();
            availablePlugins();
        });

        filterwrapper.setBorder(new EmptyBorder(10, 10, 0, 10));

        searchBar.setIcon(IconTextField.Icon.SEARCH);
        searchBar.setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 30));
        searchBar.setBackground(net.runelite.client.ui.ColorScheme.DARKER_GRAY_COLOR);
        searchBar.setHoverBackgroundColor(net.runelite.client.ui.ColorScheme.DARK_GRAY_HOVER_COLOR);
        searchBar.getDocument().addDocumentListener(listener);

        filterwrapper.add(searchBar, BorderLayout.CENTER);
    }

    private JLabel titleLabel(String text) {
        JLabel title = new JShadowedLabel();

        title.setFont(FontManager.getRunescapeSmallFont());
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setText("<html><body style = 'text-align:center'>" + text + "</body></html>");

        return title;
    }

    private JPanel installedPluginsPanel() {
        var installedPluginsContainer = new JPanel();
        installedPluginsContainer.setLayout(new BorderLayout(0, 5));
        installedPluginsContainer.setBorder(new EmptyBorder(0, 10, 10, 10));
        installedPluginsContainer.add(installedPluginsPanel, BorderLayout.CENTER);

        return installedPluginsContainer;
    }

    private JPanel availablePluginsPanel() {
        var availablePluginsContainer = new JPanel();
        availablePluginsContainer.setLayout(new BorderLayout(0, 5));
        availablePluginsContainer.setBorder(new EmptyBorder(0, 10, 10, 10));
        availablePluginsContainer.add(availablePluginsPanel, BorderLayout.CENTER);

        return availablePluginsContainer;
    }

    static boolean mismatchesSearchTerms(String search, PluginInfo pluginInfo) {
        final var searchTerms = search.toLowerCase().split(" ");
        final var pluginTerms = (pluginInfo.name + " " + pluginInfo.description).toLowerCase().split("[/\\s]");
        for (var term : searchTerms) {
            if (Arrays.stream(pluginTerms).noneMatch((t) -> t.contains(term) ||
                    DISTANCE.apply(t, term) > 0.9)) {
                return true;
            }
        }
        return false;
    }

    public void reloadPlugins() {
        fetchPlugins();

        try {
            SwingUtil.syncExec(() ->
            {
                this.installedPlugins();
                this.availablePlugins();
            });

        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    private void onSdnPluginChanged(SdnPluginChanged externalPluginChanged) {
        var pluginId = externalPluginChanged.getPluginId();
        Optional<Component> externalBox;

        if (externalPluginChanged.isAdded()) {
            externalBox = Arrays.stream(
                    availablePluginsPanel.getComponents()
            ).filter(extBox ->
                    extBox instanceof ExternalBox && ((ExternalBox) extBox).pluginInfo.id.equals(pluginId)
            ).findFirst();
        } else {
            externalBox = Arrays.stream(
                    installedPluginsPanel.getComponents()
            ).filter(extBox ->
                    extBox instanceof ExternalBox && ((ExternalBox) extBox).pluginInfo.id.equals(pluginId)
            ).findFirst();
        }

        if (externalBox.isEmpty()) {
            return;
        }

        var extBox = (ExternalBox) externalBox.get();
        deps = sdnPluginManager.getDependencies();

        try {
            SwingUtil.syncExec(() ->
            {
                if (externalPluginChanged.isAdded()) {
                    availablePluginsPanel.remove(externalBox.get());
                    availablePluginsList.remove(extBox.pluginInfo);

                    installedPluginsList.add(extBox.pluginInfo);
                    installedPluginsList.sort(Comparator.naturalOrder());

                    installedPlugins();

                    pluginInstallButton(extBox.install, extBox.pluginInfo, true, deps.contains(extBox.pluginInfo.id));
                } else {
                    installedPluginsPanel.remove(externalBox.get());
                    installedPluginsList.remove(extBox.pluginInfo);

                    availablePluginsList.add(extBox.pluginInfo);
                    availablePluginsList.sort(Comparator.naturalOrder());

                    availablePlugins();

                    pluginInstallButton(extBox.install, extBox.pluginInfo, false, false);
                }
            });
        } catch (InvocationTargetException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Subscribe
    private void onSdnLoaded(SdnLoaded sdnLoaded) {
        SwingUtilities.invokeLater(this::reloadPlugins);
    }

    private void installedPlugins() {
        var c = new GridBagConstraints();

        installedPluginsPanel.removeAll();
        var search = searchBar.getText();

        for (var pluginInfo : installedPluginsList) {
            if (!search.isEmpty() && mismatchesSearchTerms(search, pluginInfo)) {
                continue;
            }

            var pluginBox = new ExternalBox(pluginInfo);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.gridy += 1;
            c.insets = new Insets(5, 0, 0, 0);

            pluginInstallButton(pluginBox.install, pluginInfo, true, deps.contains(pluginInfo.id));
            installedPluginsPanel.add(pluginBox, c);
        }

        if (installedPluginsPanel.getComponents().length < 1) {
            installedPluginsPanel.add(titleLabel("No plugins found"));
        }
    }

    private void availablePlugins() {
        var c = new GridBagConstraints();

        availablePluginsPanel.removeAll();
        var search = searchBar.getText();

        for (var pluginInfo : availablePluginsList) {
            if (pluginInfo.releases
                    .stream()
                    .noneMatch((pluginRelease) -> versionManager.checkVersionConstraint(sdnPluginManager.getPluginManager().getSystemVersion(), pluginRelease.requires))) {
                continue;
            }

            if (!search.equals("") && mismatchesSearchTerms(search, pluginInfo)) {
                continue;
            }

            var pluginBox = new ExternalBox(pluginInfo);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.gridy += 1;
            c.insets = new Insets(5, 0, 0, 0);

            pluginInstallButton(pluginBox.install, pluginInfo, false, false);
            availablePluginsPanel.add(pluginBox, c);
        }

        if (availablePluginsPanel.getComponents().length < 1) {
            availablePluginsPanel.add(titleLabel("No plugins found"));
        }
    }


    private void pluginInstallButton(JLabel install, PluginInfo pluginInfo, boolean installed, boolean hideAction) {
        install.setIcon(installed ? hideAction ? DELETE_ICON_GRAY : DELETE_ICON : ADD_ICON);
        install.setText("");

        if (!hideAction) {
            install.setToolTipText(installed ? "Uninstall" : "Install");
        }
        install.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (installed) {
                    if (hideAction) {
                        JOptionPane.showMessageDialog(null, "This plugin can't be uninstalled because one or more other plugins have a dependency on it.", "Error!", JOptionPane.ERROR_MESSAGE);
                    } else {
                        install.setIcon(null);
                        install.setText("Uninstalling");

                        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                            @Override
                            protected Boolean doInBackground() {
                                return sdnPluginManager.uninstall(pluginInfo.id);
                            }

                            @Override
                            protected void done() {

                                var status = false;
                                try {
                                    status = get();
                                } catch (InterruptedException | ExecutionException e) {
                                }

                                if (!status) {
                                    pluginInstallButton(install, pluginInfo, installed, hideAction);
                                }
                            }
                        };
                        worker.execute();
                    }
                } else {
                    install.setIcon(null);
                    install.setText("Installing");

                    SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
                        @Override
                        protected Boolean doInBackground() {
                            return installPlugin(pluginInfo);
                        }

                        @Override
                        protected void done() {

                            var status = false;
                            try {
                                status = get();
                            } catch (InterruptedException | ExecutionException ignored) {
                            }

                            if (!status) {
                                pluginInstallButton(install, pluginInfo, false, hideAction);
                            }
                        }
                    };
                    worker.execute();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (install.getText().toLowerCase().contains("installing")) {
                    return;
                }

                install.setIcon(installed ? hideAction ? DELETE_HOVER_ICON_GRAY : DELETE_HOVER_ICON : ADD_HOVER_ICON);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (install.getText().toLowerCase().contains("installing")) {
                    return;
                }

                install.setIcon(installed ? hideAction ? DELETE_ICON_GRAY : DELETE_ICON : ADD_ICON);
            }
        });
    }

    private boolean installPlugin(PluginInfo pluginInfo) {
        try {
            return sdnPluginManager.install(pluginInfo.id);
        } catch (VerifyException ex) {
            displayError(pluginInfo.name + " could not be installed, the hash could not be verified.");
        } catch (IOException e) {
            log.error("Error installing plugin", e);
            displayError(
                    String.format("Plugin '%s' could not be installed: %s. " +
                            "Close all clients through Task Manager and try again.", pluginInfo.name, e.getMessage())
            );
        } catch (PluginNotUsableException e) {
            displayError(e.getMessage());
        } catch (Exception e) {
            log.error("Error installing plugin", e);
            displayError("Plugin could not be installed: " + e.getMessage());
        }

        return false;
    }

    private void displayError(String message) {
        try {
            SwingUtil.syncExec(() ->
                    JOptionPane.showMessageDialog(null, message, "Error!", JOptionPane.ERROR_MESSAGE));
        } catch (InvocationTargetException | InterruptedException ignored) {
        }
    }

    private void fetchPlugins() {
        List<PluginInfo> availablePlugins = null;
        List<PluginInfo> plugins = null;
        var disabledPlugins = sdnPluginManager.getDisabledPluginIds();

        try {
            availablePlugins = updateManager.getAvailablePlugins();
            plugins = updateManager.getPlugins();
        } catch (JsonSyntaxException ex) {
            log.error(String.valueOf(ex));
        }

        if (availablePlugins == null || plugins == null) {
            JOptionPane.showMessageDialog(null, "The external plugin list could not be loaded.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        availablePluginsList.clear();
        installedPluginsList.clear();

        deps = sdnPluginManager.getDependencies();

        for (var pluginInfo : plugins) {
            if (availablePlugins.contains(pluginInfo) || disabledPlugins.contains(pluginInfo.id)) {
                availablePluginsList.add(pluginInfo);
            } else {
                installedPluginsList.add(pluginInfo);
            }
        }
    }
}
