package net.solace.loader.plugins.breakhandler.ui;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.disposables.Disposable;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.PluginErrorPanel;
import net.runelite.client.util.ImageUtil;
import net.solace.api.breaks.BreakHandler;
import net.solace.api.game.IGame;
import net.solace.api.plugins.Plugin;
import net.solace.loader.plugins.breakhandler.SolaceBreakHandlerPlugin;
import net.solace.loader.plugins.breakhandler.ui.utils.ConfigPanel;
import net.solace.loader.plugins.breakhandler.ui.utils.ImageUtils;
import net.solace.loader.plugins.breakhandler.ui.utils.JMultilineLabel;

import javax.inject.Inject;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.solace.api.breaks.BreakHandler.CONFIG_GROUP;
import static net.solace.api.util.SwingUtil.syncExec;

public class SolaceBreakHandlerPanel extends PluginPanel {
    public final static Color PANEL_BACKGROUND_COLOR = ColorScheme.DARK_GRAY_COLOR;
    final static Color BACKGROUND_COLOR = ColorScheme.DARKER_GRAY_COLOR;

    static final Font NORMAL_FONT = FontManager.getRunescapeFont();
    static final Font SMALL_FONT = FontManager.getRunescapeSmallFont();

    private static final ImageIcon HELP_ICON;
    private static final ImageIcon HELP_HOVER_ICON;

    static {
        final BufferedImage helpIcon =
                ImageUtils.recolorImage(
                        ImageUtil.loadImageResource(SolaceBreakHandlerPlugin.class, "help.png"), net.solace.api.ui.ColorScheme.BRAND_CRIMSON
                );
        HELP_ICON = new ImageIcon(helpIcon);
        HELP_HOVER_ICON = new ImageIcon(ImageUtil.alphaOffset(helpIcon, 0.53f));
    }

    private final SolaceBreakHandlerPlugin solaceBreakHandlerPlugin;
    private final BreakHandler solaceBreakHandler;
    private final ConfigPanel configPanel;
    private final IGame game;

    private final JPanel unlockAccountPanel = new JPanel(new BorderLayout());
    private final JPanel breakTimingsPanel = new JPanel(new GridLayout(0, 1));
    public @NonNull Disposable pluginDisposable;
    public @NonNull Disposable activeDisposable;
    public @NonNull Disposable currentDisposable;
    public @NonNull Disposable startDisposable;
    public @NonNull Disposable configDisposable;

    @Inject
    private SolaceBreakHandlerPanel(SolaceBreakHandlerPlugin solaceBreakHandlerPlugin, BreakHandler solaceBreakHandler, ConfigPanel configPanel, IGame game) {
        super(false);
        this.game = game;

        configPanel.init(solaceBreakHandlerPlugin.getOptionsConfig());

        this.solaceBreakHandlerPlugin = solaceBreakHandlerPlugin;
        this.solaceBreakHandler = solaceBreakHandler;
        this.configPanel = configPanel;

        pluginDisposable = solaceBreakHandler
                .getPluginObservable()
                .subscribe((Map<Plugin, Boolean> plugins) ->
                        syncExec(() ->
                                buildPanel(plugins)));

        activeDisposable = solaceBreakHandler
                .getActiveObservable()
                .subscribe(
                        (ignored) ->
                                syncExec(() ->
                                        buildPanel(solaceBreakHandler.getPlugins()))
                );

        currentDisposable = solaceBreakHandler
                .getActiveBreaksObservable()
                .subscribe(
                        (ignored) ->
                                syncExec(() ->
                                        buildPanel(solaceBreakHandler.getPlugins()))
                );

        startDisposable = solaceBreakHandler
                .getActiveObservable()
                .subscribe(
                        (ignored) ->
                                syncExec(() ->
                                {
                                    unlockAccountsPanel();
                                    unlockAccountPanel.revalidate();
                                    unlockAccountPanel.repaint();

                                    breakTimingsPanel();
                                    breakTimingsPanel.revalidate();
                                    breakTimingsPanel.repaint();
                                })
                );

        configDisposable = solaceBreakHandler
                .getConfigChanged()
                .subscribe(
                        (ignored) ->
                                syncExec(() ->
                                {
                                    unlockAccountsPanel();
                                    unlockAccountPanel.revalidate();
                                    unlockAccountPanel.repaint();
                                })
                );

        this.setBackground(PANEL_BACKGROUND_COLOR);
        this.setLayout(new BorderLayout());

        buildPanel(solaceBreakHandler.getPlugins());
    }

    public static JScrollPane wrapContainer(final JPanel container) {
        final JPanel wrapped = new JPanel(new BorderLayout());
        wrapped.add(container, BorderLayout.NORTH);
        wrapped.setBackground(PANEL_BACKGROUND_COLOR);

        final JScrollPane scroller = new JScrollPane(wrapped);
        scroller.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroller.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        scroller.setBackground(PANEL_BACKGROUND_COLOR);

        return scroller;
    }

    void buildPanel(Map<Plugin, Boolean> plugins) {
        removeAll();

        if (plugins.isEmpty()) {
            PluginErrorPanel errorPanel = new PluginErrorPanel();
            errorPanel.setContent("Solace break handler", "There were no plugins that registered themselves with the break handler.");

            add(errorPanel, BorderLayout.NORTH);
        } else {
            JPanel contentPanel = new JPanel(new BorderLayout());

            contentPanel.add(statusPanel(), BorderLayout.NORTH);
            contentPanel.add(tabbedPane(plugins), BorderLayout.CENTER);

            add(titleBar(), BorderLayout.NORTH);
            add(contentPanel, BorderLayout.CENTER);
        }

        revalidate();
        repaint();
    }

    private JPanel titleBar() {
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel();
        JLabel help = new JLabel(HELP_ICON);

        title.setText("Solace break handler");
        title.setForeground(Color.WHITE);

        help.setToolTipText("Info");

        help.setBorder(new EmptyBorder(0, 3, 0, 0));

        titlePanel.add(title, BorderLayout.WEST);
        titlePanel.add(help, BorderLayout.EAST);

        return titlePanel;
    }

    private boolean unlockAccountsPanel() {
        unlockAccountPanel.removeAll();

        Set<Plugin> activePlugins = solaceBreakHandler.getActivePlugins();

        boolean manual = Boolean.parseBoolean(solaceBreakHandlerPlugin.getConfigManager().getConfiguration(CONFIG_GROUP, "accountselection"));

        String data = SolaceBreakHandlerPlugin.data;

        if (activePlugins.isEmpty() || manual || (data != null && !data.trim().isEmpty())) {
            return false;
        }

        JPanel titleWrapper = new JPanel(new BorderLayout());
        titleWrapper.setBackground(new Color(125, 40, 40));
        titleWrapper.setBorder(new CompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(115, 30, 30)),
                BorderFactory.createLineBorder(new Color(125, 40, 40))
        ));

        JLabel title = new JLabel();
        title.setText("Warning");
        title.setFont(NORMAL_FONT);
        title.setPreferredSize(new Dimension(0, 24));
        title.setForeground(Color.WHITE);
        title.setBorder(new EmptyBorder(0, 8, 0, 0));

        titleWrapper.add(title, BorderLayout.CENTER);

        unlockAccountPanel.add(titleWrapper, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(125, 40, 40));

        JMultilineLabel description = new JMultilineLabel();

        description.setText("Please make sure to unlock your profiles plugins data in the account tab!");
        description.setFont(SMALL_FONT);
        description.setDisabledTextColor(Color.WHITE);
        description.setBackground(new Color(115, 30, 30));

        description.setBorder(new EmptyBorder(5, 5, 10, 5));

        contentPanel.add(description, BorderLayout.CENTER);

        unlockAccountPanel.add(contentPanel, BorderLayout.CENTER);

        return true;
    }

    private boolean breakTimingsPanel() {
        breakTimingsPanel.removeAll();

        Set<Plugin> pluginStream = solaceBreakHandler.getActivePlugins().stream().filter(e -> !solaceBreakHandlerPlugin.isValidBreak(e)).collect(Collectors.toSet());

        if (pluginStream.isEmpty()) {
            return false;
        }

        for (Plugin plugin : pluginStream) {
            JPanel wrapperPanel = new JPanel(new BorderLayout());

            JPanel titleWrapper = new JPanel(new BorderLayout());
            titleWrapper.setBackground(new Color(125, 40, 40));
            titleWrapper.setBorder(new CompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(115, 30, 30)),
                    BorderFactory.createLineBorder(new Color(125, 40, 40))
            ));

            JLabel title = new JLabel();
            title.setText("Warning");
            title.setFont(NORMAL_FONT);
            title.setPreferredSize(new Dimension(0, 24));
            title.setForeground(Color.WHITE);
            title.setBorder(new EmptyBorder(0, 8, 0, 0));

            titleWrapper.add(title, BorderLayout.CENTER);

            wrapperPanel.add(titleWrapper, BorderLayout.NORTH);

            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(new Color(125, 40, 40));

            JMultilineLabel description = new JMultilineLabel();

            description.setText("The break timings for " + plugin.getName() + " are invalid!");
            description.setFont(SMALL_FONT);
            description.setDisabledTextColor(Color.WHITE);
            description.setBackground(new Color(115, 30, 30));

            description.setBorder(new EmptyBorder(5, 5, 10, 5));

            contentPanel.add(description, BorderLayout.CENTER);

            wrapperPanel.add(contentPanel, BorderLayout.CENTER);

            breakTimingsPanel.add(wrapperPanel);
        }

        return true;
    }

    private JPanel statusPanel() {
        Set<Plugin> activePlugins = solaceBreakHandler.getActivePlugins();

        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        if (unlockAccountsPanel()) {
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.gridy += 1;
            c.insets = new Insets(5, 10, 0, 10);

            contentPanel.add(unlockAccountPanel, c);
        }

        if (breakTimingsPanel()) {
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.gridy += 1;
            c.insets = new Insets(5, 10, 0, 10);

            contentPanel.add(breakTimingsPanel, c);
        }

        if (activePlugins.isEmpty()) {
            return contentPanel;
        }

        for (Plugin plugin : activePlugins) {
            SolaceBreakHandlerStatusPanel statusPanel = new SolaceBreakHandlerStatusPanel(solaceBreakHandlerPlugin, solaceBreakHandler, plugin);

            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.gridy += 1;
            c.insets = new Insets(5, 10, 0, 10);

            contentPanel.add(statusPanel, c);
        }

        JButton scheduleBreakButton = new JButton("Schedule break now");

        if (activePlugins.size() > 0) {
            scheduleBreakButton.addActionListener(e -> activePlugins.forEach(plugin ->
            {
                if (!solaceBreakHandler.isBreakActive(plugin)) {
                    solaceBreakHandler.planBreak(plugin, Instant.now());
                }
            }));

            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.gridy += 1;
            c.insets = new Insets(5, 10, 0, 10);

            contentPanel.add(scheduleBreakButton, c);
        }

        return contentPanel;
    }

    private JTabbedPane tabbedPane(Map<Plugin, Boolean> plugins) {
        JTabbedPane mainTabPane = new JTabbedPane();

        JScrollPane pluginPanel = wrapContainer(contentPane(plugins));
        JScrollPane repositoryPanel = wrapContainer(new SolaceBreakHandlerAccountPanel(solaceBreakHandlerPlugin, solaceBreakHandler, game));
        JScrollPane optionsPanel = wrapContainer(configPanel);

        mainTabPane.add("Plugins", pluginPanel);
        mainTabPane.add("Accounts", repositoryPanel);
        mainTabPane.add("Options", optionsPanel);

        return mainTabPane;
    }

    private JPanel contentPane(Map<Plugin, Boolean> plugins) {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();

        if (solaceBreakHandler.getPlugins().isEmpty()) {
            return contentPanel;
        }

        for (Map.Entry<Plugin, Boolean> plugin : plugins.entrySet()) {
            SolaceBreakHandlerPluginPanel panel = new SolaceBreakHandlerPluginPanel(solaceBreakHandlerPlugin, plugin.getKey(), plugin.getValue());

            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 1.0;
            c.gridy += 1;
            c.insets = new Insets(5, 10, 0, 10);

            contentPanel.add(panel, c);
        }

        return contentPanel;
    }
}