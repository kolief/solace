package net.solace.ui.plugins.items;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.client.game.ItemManager;
import net.runelite.client.util.AsyncBufferedImage;
import net.solace.api.domain.game.IClientThread;
import net.solace.api.plugins.config.ConfigManager;
import net.solace.api.plugins.config.ItemConfig;

import javax.inject.Inject;
import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.RoundRectangle2D;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Slf4j
public class ItemSelector extends JFrame {
    private String configGroup;
    private String configKey;
    private boolean setConfig;

    private final JTextField searchField;
    private final JPanel gridPanel;
    private final JLabel statusLabel;
    private final JButton confirmButton;
    private final Definitions definitions;
    private final Client client;
    private final ConfigManager configManager;
    private final ItemManager itemManager;
    private final IClientThread clientThread;
    private JPanel selectedPanel = null;
    private JPanel hoveredPanel = null;
    private ItemConfig selectedItem = null;

    private static final int GRID_COLUMNS = 4;
    private static final int MAX_RESULTS = 32;

    public ItemSelector(ItemManager itemManager, Client client, Definitions definitions, ConfigManager configManager, IClientThread clientThread) {
        super("Solace Item Selector");
        this.definitions = definitions;
        this.client = client;
        this.configManager = configManager;
        this.itemManager = itemManager;
        this.clientThread = clientThread;

        Color bgColor = new Color(35, 35, 35);
        Color panelBgColor = new Color(35, 35, 35);
        Color searchBgColor = new Color(40, 40, 40);
        Color textColor = new Color(200, 200, 200);
        Color borderColor = new Color(50, 50, 50);

        setLayout(new BorderLayout());
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(380, 480);
        setResizable(false);
        getContentPane().setBackground(bgColor);

        JPanel mainPanel = new JPanel(new BorderLayout(0, 8));
        mainPanel.setBackground(bgColor);
        mainPanel.setBorder(new EmptyBorder(8, 8, 8, 8));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBackground(bgColor);

        searchField = new JTextField();
        searchField.setBackground(searchBgColor);
        searchField.setForeground(textColor);
        searchField.setCaretColor(textColor);
        searchField.setFont(new Font("Dialog", Font.PLAIN, 13));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(borderColor, 1, 5),
                new EmptyBorder(6, 10, 6, 10)
        ));

        JButton clearButton = getXButton(searchBgColor, borderColor);

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(clearButton, BorderLayout.EAST);

        gridPanel = new JPanel();
        gridPanel.setLayout(new GridLayout(0, GRID_COLUMNS, 3, 3));
        gridPanel.setBackground(panelBgColor);
        gridPanel.setBorder(new EmptyBorder(4, 4, 4, 4));

        JScrollPane scrollPane = new JScrollPane(gridPanel);
        scrollPane.setBorder(new RoundedBorder(borderColor, 1, 5));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(panelBgColor);

        statusLabel = new JLabel("Search for items above");
        statusLabel.setForeground(new Color(130, 130, 130));
        statusLabel.setFont(new Font("Dialog", Font.PLAIN, 11));
        statusLabel.setBorder(new EmptyBorder(4, 2, 0, 2));
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);

        confirmButton = new JButton("Confirm Selection");
        confirmButton.setBackground(new Color(70, 60, 100));
        confirmButton.setForeground(new Color(200, 200, 200));
        confirmButton.setFont(new Font("Dialog", Font.BOLD, 12));
        confirmButton.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(new Color(110, 90, 130), 1, 5),
                new EmptyBorder(6, 12, 6, 12)
        ));
        confirmButton.setFocusPainted(false);
        confirmButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        confirmButton.setVisible(false);

        confirmButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                confirmButton.setBackground(new Color(80, 70, 110));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                confirmButton.setBackground(new Color(70, 60, 100));
            }
        });

        confirmButton.addActionListener(e -> {
            if (selectedItem != null) {
                handleItemSelection(selectedItem);
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(bgColor);
        bottomPanel.add(statusLabel, BorderLayout.CENTER);
        bottomPanel.add(confirmButton, BorderLayout.SOUTH);

        mainPanel.add(searchPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                performSearch();
            }

            public void removeUpdate(DocumentEvent e) {
                performSearch();
            }

            public void changedUpdate(DocumentEvent e) {
                performSearch();
            }
        });

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                client.getCallbacks().post(new ItemSelectorClosed());
                clearGrid();
            }
        });
    }

    private JButton getXButton(Color searchBgColor, Color borderColor) {
        JButton clearButton = new JButton("✕");
        clearButton.setBackground(searchBgColor);
        clearButton.setForeground(new Color(180, 80, 80));
        clearButton.setFont(new Font("Dialog", Font.BOLD, 14));
        clearButton.setBorder(BorderFactory.createCompoundBorder(
                new RoundedBorder(borderColor, 1, 5),
                new EmptyBorder(4, 10, 4, 10)
        ));
        clearButton.setFocusPainted(false);
        clearButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        clearButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                clearButton.setBackground(new Color(60, 50, 50));
                clearButton.setForeground(new Color(220, 100, 100));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                clearButton.setBackground(searchBgColor);
                clearButton.setForeground(new Color(180, 80, 80));
            }
        });

        clearButton.addActionListener(e -> {
            searchField.setText("");
            clearGrid();
        });
        return clearButton;
    }

    private void performSearch() {
        String searchText = searchField.getText().trim();

        if (searchText.isEmpty()) {
            clearGrid();
            return;
        }

        log.info("Searching for: {}", searchText);
        List<ItemConfig> results = definitions.searchItem(searchText);
        log.info("Found {} results", results.size());

        updateGrid(results);
    }

    private void updateGrid(List<ItemConfig> results) {
        gridPanel.removeAll();
        selectedPanel = null;
        hoveredPanel = null;
        selectedItem = null;
        confirmButton.setVisible(false);

        int count = Math.min(results.size(), MAX_RESULTS);

        final List<ItemConfig> itemsToLoad = results.subList(0, count);
        Map<Integer, AsyncBufferedImage> imageMap =
                clientThread.invokeAndWait(() -> {
                    Map<Integer, AsyncBufferedImage> map = new HashMap<>();
                    for (ItemConfig item : itemsToLoad) {
                        map.put(item.getId(), itemManager.getImage(item.getId()));
                    }
                    return map;
                });

        for (ItemConfig item : itemsToLoad) {
            JPanel itemPanel = createItemPanel(item, imageMap.get(item.getId()));
            gridPanel.add(itemPanel);
        }

        if (results.isEmpty()) {
            statusLabel.setText("No items found");
        } else if (results.size() > count) {
            statusLabel.setText(String.format("Showing %d of %d - Click to select", count, results.size()));
        } else {
            statusLabel.setText(String.format("%d result%s - Click to select", count, count == 1 ? "" : "s"));
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private JPanel createItemPanel(ItemConfig item, AsyncBufferedImage image) {
        Color normalBg = new Color(45, 45, 45);
        Color hoverBg = new Color(38, 38, 38);
        Color selectedBg = new Color(38, 38, 38);
        Color normalBorder = new Color(60, 60, 60);
        Color hoverBorder = new Color(110, 90, 130, 160);
        Color glowColor = new Color(100, 80, 120, 35);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2d.setColor(getBackground());
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);

                if (selectedPanel == this || isHovered(this)) {
                    g2d.setColor(glowColor);
                    g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                }

                if (selectedPanel == this || isHovered(this)) {
                    g2d.setColor(hoverBorder);
                } else {
                    g2d.setColor(normalBorder);
                }
                g2d.setStroke(new BasicStroke(1));
                g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);

                g2d.dispose();
            }
        };

        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(normalBg);
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(80, 60));
        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel iconLabel = new JLabel();
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);

        if (image != null) {
            image.addTo(iconLabel);
        }

        String displayName = item.getName();
        if (displayName.length() > 12) {
            displayName = displayName.substring(0, 10) + "...";
        }

        JLabel nameLabel = new JLabel(displayName);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setForeground(new Color(200, 200, 200));
        nameLabel.setFont(new Font("Dialog", Font.PLAIN, 9));

        JLabel idLabel = new JLabel("(" + item.getId() + ")");
        idLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        idLabel.setForeground(new Color(150, 150, 150));
        idLabel.setFont(new Font("Dialog", Font.PLAIN, 8));

        panel.add(Box.createVerticalGlue());
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(2));
        panel.add(nameLabel);
        panel.add(Box.createVerticalStrut(1));
        panel.add(idLabel);
        panel.add(Box.createVerticalGlue());

        MouseAdapter mouseAdapter = new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                if (selectedPanel != panel) {
                    hoveredPanel = panel;
                    panel.setBackground(hoverBg);
                    panel.repaint();
                }
            }

            public void mouseExited(MouseEvent e) {
                if (selectedPanel != panel) {
                    hoveredPanel = null;
                    panel.setBackground(normalBg);
                    panel.repaint();
                }
            }

            public void mousePressed(MouseEvent e) {
                if (selectedPanel != null && selectedPanel != panel) {
                    selectedPanel.setBackground(normalBg);
                    selectedPanel.repaint();
                }

                selectedPanel = panel;
                selectedItem = item;
                panel.setBackground(selectedBg);
                panel.repaint();

                confirmButton.setVisible(true);
                confirmButton.revalidate();
            }
        };

        panel.addMouseListener(mouseAdapter);
        iconLabel.addMouseListener(mouseAdapter);
        nameLabel.addMouseListener(mouseAdapter);
        idLabel.addMouseListener(mouseAdapter);

        iconLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        nameLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        idLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return panel;
    }

    private boolean isHovered(JPanel panel) {
        return hoveredPanel == panel;
    }

    private void clearGrid() {
        gridPanel.removeAll();
        selectedPanel = null;
        hoveredPanel = null;
        selectedItem = null;
        confirmButton.setVisible(false);
        statusLabel.setText("Search for items above");
        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private void handleItemSelection(ItemConfig selectedItem) {
        log.info("Item selected: {} ({})", selectedItem.getName(), selectedItem.getId());

        if (setConfig) {
            configManager.setConfiguration(this.configGroup, this.configKey, selectedItem);
        } else {
            var event = new ItemSelectorItemSelected(
                    this.configGroup,
                    this.configKey,
                    selectedItem.getId(),
                    selectedItem.getName()
            );
            client.getCallbacks().post(event);
            var consumer = event.getConsumer();
            if (consumer != null) {
                consumer.accept(this);
            }
        }
    }

    public void init(String configGroup, String configKey, boolean setConfig) {
        this.configGroup = configGroup;
        this.configKey = configKey;
        this.setConfig = setConfig;

        clearGrid();
        searchField.setText("");
        setVisible(true);
        searchField.requestFocusInWindow();
    }

    public void clear() {
        clearGrid();
        dispose();
    }

    /**
     * Custom border with rounded corners
     */
    private static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int thickness;
        private final int radius;

        public RoundedBorder(Color color, int thickness, int radius) {
            this.color = color;
            this.thickness = thickness;
            this.radius = radius;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(color);
            g2d.setStroke(new BasicStroke(thickness));

            int offset = thickness / 2;
            g2d.draw(new RoundRectangle2D.Double(
                    x + offset,
                    y + offset,
                    width - thickness,
                    height - thickness,
                    radius,
                    radius
            ));

            g2d.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(thickness, thickness, thickness, thickness);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = thickness;
            return insets;
        }
    }
}