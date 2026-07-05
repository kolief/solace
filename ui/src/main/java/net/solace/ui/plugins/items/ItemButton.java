package net.solace.ui.plugins.items;

import net.runelite.client.ui.FontManager;

import javax.swing.Icon;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import javax.imageio.ImageIO;

public class ItemButton extends JButton {
    private String minStack;
    private String maxStack;
    private boolean isStrict = false;
    private static final int ICON_OFFSET_X = 2;
    private static final int ICON_OFFSET_Y = 0;
    private boolean isHovered = false;
    private static Image lockIcon = null;

    private final Color BACKGROUND_COLOR = new Color(45, 45, 45);
    private final Color BACKGROUND_HOVER_COLOR = new Color(38, 38, 38);
    private final Color BORDER_COLOR = new Color(60, 60, 60);
    private final Color BORDER_HOVER_COLOR = new Color(110, 90, 130, 160);
    private final Color GLOW_COLOR = new Color(100, 80, 120, 35);
    private final Color TEXT_COLOR = new Color(255, 220, 80);
    private final Color TEXT_SHADOW_COLOR = new Color(0, 0, 0);

    static {
        try {
            var lockIconUrl = ItemButton.class.getResource("lock_icon.png");
            if (lockIconUrl != null) {
                lockIcon = ImageIO.read(lockIconUrl).getScaledInstance(12, 12, Image.SCALE_SMOOTH);
            }
        } catch (IOException ignored) {
        }
    }

    public ItemButton() {
        super();
        setFocusPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (isHovered) {
            g2d.setColor(BACKGROUND_HOVER_COLOR);
        } else {
            g2d.setColor(BACKGROUND_COLOR);
        }

        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);

        if (isHovered) {
            g2d.setColor(GLOW_COLOR);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 6, 6);
        }

        if (isHovered) {
            g2d.setColor(BORDER_HOVER_COLOR);
        } else {
            g2d.setColor(BORDER_COLOR);
        }
        g2d.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 6, 6);

        Icon tempIcon = getIcon();
        if (tempIcon != null) {
            int x = (getWidth() - tempIcon.getIconWidth()) / 2 + ICON_OFFSET_X;
            int y = (getHeight() - tempIcon.getIconHeight()) / 2 + ICON_OFFSET_Y;
            tempIcon.paintIcon(this, g2d, x, y);
        }

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (minStack != null && maxStack != null) {
            Font font = FontManager.getRunescapeSmallFont();
            g2d.setFont(font);

            FontMetrics fm = g2d.getFontMetrics();
            int maxWidth = fm.stringWidth(maxStack);

            int minX = 0;
            int minY = fm.getAscent();

            int maxX = getWidth() - maxWidth;
            int maxY = getHeight() - fm.getDescent();

            g2d.setColor(TEXT_SHADOW_COLOR);
            g2d.drawString(minStack, minX + 1, minY + 1);
            g2d.setColor(TEXT_COLOR);
            g2d.drawString(minStack, minX, minY);

            g2d.setColor(TEXT_SHADOW_COLOR);
            g2d.drawString(maxStack, maxX + 1, maxY + 1);
            g2d.setColor(TEXT_COLOR);
            g2d.drawString(maxStack, maxX, maxY);
        }

        if (isStrict && lockIcon != null) {
            int lockIconWidth = lockIcon.getWidth(this);
            int lockIconHeight = lockIcon.getHeight(this);
            
            int lockX = getWidth() - lockIconWidth;

            g2d.drawImage(lockIcon, lockX, lockIconHeight, this);
        }

        g2d.dispose();
    }

    public void setMinStack(String text) {
        this.minStack = text;
        repaint();
    }

    public void setMaxStack(String text) {
        this.maxStack = text;
        repaint();
    }

    public void setStrict(boolean strict) {
        this.isStrict = strict;
        repaint();
    }
}