package net.solace.ui.plugins.items;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.EventBus;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.FontManager;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.plugins.config.ConfigManager;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.HeadlessException;
import java.awt.Insets;

@Slf4j
public class LoadoutEditor extends JFrame {
    private final EventBus eventBus;
    private final ItemManager itemManager;
    private final ItemSelector itemSelector;
    private final ConfigManager configManager;
    private final IClientThread clientThread;

    private JPanel inventoryPanel;
    private JPanel equipmentPanel;
    private JPanel runePouchPanel;

    public LoadoutEditor(
            EventBus eventBus,
            ItemManager itemManager,
            ItemSelector itemSelector,
            ConfigManager configManager,
            IClientThread clientThread
    ) throws HeadlessException {
        super("Solace Loadout Editor");
        this.eventBus = eventBus;
        this.itemManager = itemManager;
        this.itemSelector = itemSelector;
        this.configManager = configManager;
        this.clientThread = clientThread;
    }

    public void init(
            Loadout loadout,
            String configGroup,
            String configKey
    ) {
        getContentPane().removeAll();

        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout(5, 5));

        JPanel mainContainer = new JPanel();
        mainContainer.setLayout(new GridBagLayout());
        mainContainer.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        mainContainer.setBackground(new Color(35, 35, 35));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 0, 12);

        if (!loadout.isInventoryDisabled()) {
            JPanel inventoryContainer = new JPanel(new BorderLayout(0, 8));
            inventoryContainer.setBackground(new Color(35, 35, 35));

            JLabel inventoryLabel = new JLabel("Inventory", SwingConstants.CENTER);
            inventoryLabel.setFont(FontManager.getRunescapeBoldFont());
            inventoryLabel.setForeground(new Color(200, 200, 200));
            inventoryContainer.add(inventoryLabel, BorderLayout.NORTH);

            inventoryPanel = new InventoryLoadoutPanel(
                    itemSelector, loadout, configManager,
                    configGroup, configKey, clientThread, itemManager
            );
            inventoryPanel.setBackground(new Color(35, 35, 35));

            JPanel inventoryWrapper = new JPanel(new BorderLayout());
            inventoryWrapper.setBackground(new Color(35, 35, 35));
            inventoryWrapper.add(inventoryPanel, BorderLayout.NORTH);
            inventoryContainer.add(inventoryWrapper, BorderLayout.CENTER);

            eventBus.register(inventoryPanel);

            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.gridheight = 1;
            gbc.anchor = GridBagConstraints.NORTHWEST;
            gbc.weighty = 0;
            mainContainer.add(inventoryContainer, gbc);
        }

        JPanel rightContainer = new JPanel();
        rightContainer.setLayout(new GridBagLayout());
        rightContainer.setBackground(new Color(35, 35, 35));
        GridBagConstraints rightGbc = new GridBagConstraints();

        if (!loadout.isEquipmentDisabled()) {
            JPanel equipmentContainer = new JPanel(new BorderLayout(0, 8));
            equipmentContainer.setBackground(new Color(35, 35, 35));

            JLabel equipmentLabel = new JLabel("Equipment", SwingConstants.CENTER);
            equipmentLabel.setFont(FontManager.getRunescapeBoldFont());
            equipmentLabel.setForeground(new Color(200, 200, 200));
            equipmentContainer.add(equipmentLabel, BorderLayout.NORTH);

            equipmentPanel = new EquipmentLoadoutPanel(
                    itemSelector, loadout, configManager,
                    configGroup, configKey, clientThread, itemManager
            );
            equipmentPanel.setBackground(new Color(35, 35, 35));
            equipmentContainer.add(equipmentPanel, BorderLayout.CENTER);
            eventBus.register(equipmentPanel);

            rightGbc.gridx = 0;
            rightGbc.gridy = 0;
            rightGbc.anchor = GridBagConstraints.NORTH;
            rightGbc.weighty = 0;
            rightGbc.insets = new Insets(0, 0, 12, 0);
            rightContainer.add(equipmentContainer, rightGbc);
        }

        if (!loadout.isEquipmentDisabled() && !loadout.isRunePouchDisabled()) {
            JPanel spacer = new JPanel();
            spacer.setBackground(new Color(35, 35, 35));
            spacer.setPreferredSize(new Dimension(1, 1));
            rightGbc.gridx = 0;
            rightGbc.gridy = 1;
            rightGbc.weighty = 1.0;
            rightGbc.fill = GridBagConstraints.VERTICAL;
            rightGbc.insets = new Insets(0, 0, 0, 0);
            rightContainer.add(spacer, rightGbc);
        }

        if (!loadout.isRunePouchDisabled()) {
            JPanel runePouchContainer = new JPanel(new BorderLayout(0, 8));
            runePouchContainer.setBackground(new Color(35, 35, 35));

            JLabel runePouchLabel = new JLabel("Rune Pouch", SwingConstants.CENTER);
            runePouchLabel.setFont(FontManager.getRunescapeBoldFont());
            runePouchLabel.setForeground(new Color(200, 200, 200));
            runePouchContainer.add(runePouchLabel, BorderLayout.NORTH);

            runePouchPanel = new RunePouchLoadoutPanel(
                    itemSelector, loadout, configManager,
                    configGroup, configKey, clientThread, itemManager
            );
            runePouchPanel.setBackground(new Color(35, 35, 35));
            runePouchContainer.add(runePouchPanel, BorderLayout.CENTER);
            eventBus.register(runePouchPanel);

            rightGbc.gridx = 0;
            rightGbc.gridy = 2;
            rightGbc.anchor = GridBagConstraints.SOUTH;
            rightGbc.weighty = 0;
            rightGbc.fill = GridBagConstraints.NONE;
            rightGbc.insets = new Insets(0, 0, 0, 0);
            rightContainer.add(runePouchContainer, rightGbc);
        }

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.insets = new Insets(0, 0, 0, 0);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.VERTICAL;
        mainContainer.add(rightContainer, gbc);

        add(mainContainer, BorderLayout.CENTER);

        pack();
        setVisible(true);
    }

    @Override
    public void dispose() {
        super.dispose();

        if (inventoryPanel != null) {
            eventBus.unregister(inventoryPanel);
        }

        if (equipmentPanel != null) {
            eventBus.unregister(equipmentPanel);
        }

        if (runePouchPanel != null) {
            eventBus.unregister(runePouchPanel);
        }
    }
}