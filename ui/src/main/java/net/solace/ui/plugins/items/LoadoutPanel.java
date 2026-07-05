package net.solace.ui.plugins.items;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.runelite.client.util.QuantityFormatter;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.items.loadouts.Loadout;
import net.solace.api.items.loadouts.LoadoutItem;
import net.solace.api.plugins.config.ConfigManager;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;

@Slf4j
public abstract class LoadoutPanel extends JPanel {
    private final Map<ItemButton, Integer> buttonSlots = new HashMap<>();
    private final LoadoutItem.Type type;
    private final ItemSelector itemSelector;
    private final ConfigManager configManager;
    private final String configGroup;
    private final String configKey;
    private final IClientThread clientThread;
    private final ItemManager itemManager;
    private final Loadout loadout;

    protected int selectedSlot = -1;
    protected Consumer<LoadoutItem> itemConsumer;
    protected Map<Integer, AsyncBufferedImage> imageCache;

    public LoadoutPanel(
            LoadoutItem.Type type,
            ItemSelector itemSelector,
            ConfigManager configManager,
            String configGroup,
            String configkey,
            IClientThread clientThread,
            ItemManager itemManager,
            Loadout loadout
    ) {
        this.type = type;
        this.itemSelector = itemSelector;
        this.configManager = configManager;
        this.configGroup = configGroup;
        this.configKey = configkey;
        this.clientThread = clientThread;
        this.itemManager = itemManager;
        this.loadout = loadout;

        setLayout(new GridBagLayout());
    }

    protected void loadAllImages() {
        var items = getItems();

        // Batch load all images in one GameThread call
        imageCache = clientThread.invokeAndWait(() -> {
            Map<Integer, AsyncBufferedImage> cache = new HashMap<>();
            for (var item : items) {
                if (item != null) {
                    cache.put(item.getId(), itemManager.getImage(item.getId()));
                }
            }
            return cache;
        });
    }

    protected void addSlot(int slot, int x, int y) {
        var items = getItems();
        var loadoutItem = slot >= items.length ? null : items[slot];
        var itemButton = new ItemButton();

        itemButton.setBorder(BorderFactory.createEmptyBorder());
        itemButton.setHorizontalAlignment(JLabel.CENTER);
        itemButton.setVerticalAlignment(JLabel.CENTER);
        itemButton.setPreferredSize(new Dimension(42, 42));
        itemButton.setMinimumSize(new Dimension(42, 42));
        itemButton.setMaximumSize(new Dimension(42, 42));
        itemButton.setBackground(new Color(45, 45, 45));

        if (loadoutItem != null) {
            var image = imageCache != null ? imageCache.get(loadoutItem.getId()) : null;
            if (image != null) {
                image.addTo(itemButton);
            }
            itemButton.setText("");
            var quantity = loadoutItem.getQuantity();
            if (quantity > 1) {
                var formattedMin = QuantityFormatter.quantityToStackSize(quantity);
                var formattedMax = QuantityFormatter.quantityToStackSize(loadoutItem.getMaxQuantity());
                itemButton.setMinStack(formattedMin);
                itemButton.setMaxStack(formattedMax);
            }
            itemButton.setStrict(loadoutItem.isStrict());
        } else {
            itemButton.setText("");
            itemButton.setStrict(false);
        }

        itemButton.addActionListener(this::handleButtonClick);
        buttonSlots.put(itemButton, slot);

        var gbc = new GridBagConstraints();
        gbc.gridx = x;
        gbc.gridy = y;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = new Insets(2, 2, 2, 2);

        add(itemButton, gbc);
    }

    private void handleButtonClick(ActionEvent event) {
        var button = (ItemButton) event.getSource();
        var slot = buttonSlots.get(button);
        if (slot == null) {
            log.warn("Button slot not found");
            return;
        }

        var items = getItems();
        if (slot >= items.length) {
            log.warn("Slot {} is out of bounds", slot);
            return;
        }

        var item = items[slot];
        if (item == null) {
            log.debug("Selecting item for slot {}", slot);
            selectedSlot = slot;
            itemSelector.init(configGroup, configKey, false);
            return;
        }

        showItemPopupMenu(button, slot, item);
    }

    private void showItemPopupMenu(ItemButton button, int slot, LoadoutItem item) {
        var popup = new JPopupMenu();

        popup.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70), 1));
        popup.setBackground(new Color(40, 40, 40));

        var editItem = new JMenuItem("Edit Slot");
        editItem.setBackground(new Color(50, 50, 50));
        editItem.setForeground(Color.WHITE);
        editItem.addActionListener(e -> handleEditItem(slot));
        popup.add(editItem);

        var lockString = item.isStrict() ? "Unlock Slot" : "Lock Slot";
        var lockSlot = new JMenuItem(lockString);
        lockSlot.setBackground(new Color(50, 50, 50));
        lockSlot.setForeground(Color.WHITE);
        lockSlot.addActionListener(e -> handleLockSlot(slot, item));
        popup.add(lockSlot);

        if (item.isStackable()) {
            var changeQuantities = new JMenuItem("Change Quantities");
            changeQuantities.setBackground(new Color(50, 50, 50));
            changeQuantities.setForeground(Color.WHITE);
            changeQuantities.addActionListener(e -> handleChangeQuantities(slot, item));
            popup.add(changeQuantities);
        }

        popup.addSeparator();

        var deleteItem = new JMenuItem("Delete Item");
        deleteItem.setBackground(new Color(50, 50, 50));
        deleteItem.setForeground(new Color(220, 100, 100));
        deleteItem.addActionListener(e -> handleDeleteItem(slot));
        popup.add(deleteItem);

        // Show the popup at the button location
        popup.show(button, 0, button.getHeight());
    }

    private void handleEditItem(int slot) {
        log.debug("Editing item for slot {}", slot);
        selectedSlot = slot;
        itemSelector.init(configGroup, configKey, false);
    }

    private void handleLockSlot(int slot, LoadoutItem item) {
        var strict = !item.isStrict();
        log.debug("Locking slot {} with strict={}}", slot, strict);
        
        var items = getItems();
        var lockedItem = new LoadoutItem(
                item.getId(),
                item.getQuantity(),
                item.getMaxQuantity(),
                item.isStackable(),
                item.isNoted(),
                strict,
                item.getType(),
                slot
        );
        
        items[slot] = lockedItem;
        loadout.save(configManager, configGroup, configKey);
        rebuild();
        repaint();
    }

    private void handleDeleteItem(int slot) {
        var confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this item?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            var items = getItems();
            items[slot] = null;
            loadout.save(configManager, configGroup, configKey);
            rebuild();
        }
    }

    private void handleChangeQuantities(int slot, LoadoutItem item) {
        var defaultMin = type == LoadoutItem.Type.RUNE_POUCH ? 1000 : item.getQuantity();
        var defaultMax = type == LoadoutItem.Type.RUNE_POUCH ? 16000 : item.getMaxQuantity();

        var dialog = new QuantityDialog(
                SwingUtilities.getWindowAncestor(this),
                defaultMin,
                defaultMax
        );

        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            var quantity = dialog.getMinQuantity();
            var maxQuantity = dialog.getMaxQuantity();

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid quantity",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (maxQuantity <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Invalid max quantity",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (quantity > maxQuantity) {
                JOptionPane.showMessageDialog(
                        this,
                        "Minimum quantity cannot be greater than maximum quantity",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            var items = getItems();
            var updatedItem = new LoadoutItem(
                    item.getId(),
                    quantity,
                    maxQuantity,
                    item.isStackable(),
                    item.isNoted(),
                    item.isStrict(),
                    item.getType(),
                    slot
            );

            items[slot] = updatedItem;
            loadout.save(configManager, configGroup, configKey);
            rebuild();
            repaint();
        }
    }

    @Subscribe
    private void onItemSelectorClosed(ItemSelectorClosed e) {
        selectedSlot = -1;
    }

    @Subscribe
    private void onItemSelectorItemSelected(ItemSelectorItemSelected e) {
        if (selectedSlot == -1) {
            return;
        }

        var group = e.getConfigGroup();
        var key = e.getConfigKey();
        if (!Objects.equals(group, configGroup) || !Objects.equals(key, configKey)) {
            return;
        }

        var itemDef = clientThread.invokeAndWait(() -> itemManager.getItemComposition(e.getId()));
        if (itemDef == null) {
            log.warn("Item definition not found for item {}", e.getId());
            return;
        }

        var quantity = 1;
        var maxQuantity = 1;
        if (itemDef.isStackable()) {
            var defaultMin = type == LoadoutItem.Type.RUNE_POUCH ? 1000 : 1;
            var defaultMax = type == LoadoutItem.Type.RUNE_POUCH ? 16000 : 1;

            var dialog = new QuantityDialog(
                    SwingUtilities.getWindowAncestor(this),
                    defaultMin,
                    defaultMax
            );

            dialog.setVisible(true);

            if (!dialog.isConfirmed()) {
                selectedSlot = -1;
                return;
            }

            quantity = dialog.getMinQuantity();
            maxQuantity = dialog.getMaxQuantity();
        }

        if (quantity <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid quantity", "Error", JOptionPane.ERROR_MESSAGE);
            selectedSlot = -1;
            return;
        }

        if (maxQuantity <= 0) {
            JOptionPane.showMessageDialog(this, "Invalid max quantity", "Error", JOptionPane.ERROR_MESSAGE);
            selectedSlot = -1;
            return;
        }

        if (quantity > maxQuantity) {
            JOptionPane.showMessageDialog(this, "Minimum quantity cannot be greater than maximum quantity", "Error", JOptionPane.ERROR_MESSAGE);
            selectedSlot = -1;
            return;
        }

        boolean noted;
        if (type == LoadoutItem.Type.EQUIPMENT || type == LoadoutItem.Type.RUNE_POUCH) {
            noted = false;
        } else {
            noted = itemDef.getNote() != -1;
        }

        var loadoutItem = new LoadoutItem(e.getId(), quantity, maxQuantity, itemDef.isStackable(), noted, false, type, selectedSlot);
        if (itemConsumer != null) {
            try {
                itemConsumer.accept(loadoutItem);
            } catch (InvalidItemException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Invalid item", JOptionPane.ERROR_MESSAGE);
                selectedSlot = -1;
                return;
            }
        }

        getItems()[selectedSlot] = loadoutItem;
        loadout.save(configManager, configGroup, configKey);

        selectedSlot = -1;
        e.setConsumer(ItemSelector::clear);
        rebuild();
        repaint();
    }

    protected abstract void rebuild();

    protected abstract LoadoutItem[] getItems();

    private static class QuantityDialog extends JDialog {
        private final JTextField minField;
        private final JTextField maxField;
        @Getter
        private boolean confirmed = false;

        public QuantityDialog(Window parent, int defaultMin, int defaultMax) {
            super(parent, "Set Quantities", Dialog.ModalityType.APPLICATION_MODAL);

            setLayout(new GridBagLayout());
            setSize(300, 150);
            setLocationRelativeTo(parent);
            setResizable(false);

            var gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 10, 5, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;

            var minLabel = new JLabel("Minimum Quantity:");
            gbc.gridx = 0;
            gbc.gridy = 0;
            gbc.weightx = 0;
            add(minLabel, gbc);

            minField = new JTextField(String.valueOf(defaultMin), 10);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(minField, gbc);

            var maxLabel = new JLabel("Maximum Quantity:");
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.weightx = 0;
            add(maxLabel, gbc);

            maxField = new JTextField(String.valueOf(defaultMax), 10);
            gbc.gridx = 1;
            gbc.weightx = 1.0;
            add(maxField, gbc);

            var buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

            var okButton = new JButton("OK");
            okButton.addActionListener(e -> {
                confirmed = true;
                dispose();
            });

            var cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> {
                confirmed = false;
                dispose();
            });

            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);

            gbc.gridx = 0;
            gbc.gridy = 2;
            gbc.gridwidth = 2;
            gbc.weightx = 1.0;
            gbc.fill = GridBagConstraints.NONE;
            gbc.anchor = GridBagConstraints.EAST;
            add(buttonPanel, gbc);

            getRootPane().setDefaultButton(okButton);
        }

        public int getMinQuantity() {
            try {
                return Integer.parseInt(minField.getText().trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }

        public int getMaxQuantity() {
            try {
                return Integer.parseInt(maxField.getText().trim());
            } catch (NumberFormatException e) {
                return 0;
            }
        }
    }
}