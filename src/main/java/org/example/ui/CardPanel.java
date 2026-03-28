package org.example.ui;

import org.example.core.Card;
import javax.swing.*;
import java.awt.*;

public class CardPanel extends JPanel {
    private Card card;
    private boolean faceDown;

    public CardPanel(Card card, boolean faceDown) {
        this.card = card;
        this.faceDown = faceDown;
        setPreferredSize(new Dimension(70, 100));
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(Color.DARK_GRAY, 1));
    }

    public CardPanel(Card card) { this(card, false); }

    public void setCard(Card card) { this.card = card; repaint(); }
    public void setFaceDown(boolean fd) { this.faceDown = fd; repaint(); }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();

        // Card background
        if (faceDown || card == null) {
            g2.setColor(new Color(30, 60, 140));
            g2.fillRoundRect(2, 2, w-4, h-4, 10, 10);
            g2.setColor(new Color(60, 100, 200));
            // Draw pattern
            for (int i = 6; i < w-6; i += 8)
                for (int j = 6; j < h-6; j += 8)
                    g2.fillOval(i, j, 4, 4);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(2, 2, w-4, h-4, 10, 10);

            Color cardColor = card.isRed() ? new Color(200, 30, 30) : new Color(20, 20, 20);
            g2.setColor(cardColor);

            // Top-left rank+suit
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.drawString(card.rank.symbol, 6, 18);
            g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
            g2.drawString(card.getSuitSymbol(), 6, 30);

            // Center suit
            g2.setFont(new Font("SansSerif", Font.PLAIN, 28));
            FontMetrics fm = g2.getFontMetrics();
            String sym = card.getSuitSymbol();
            int sx = (w - fm.stringWidth(sym)) / 2;
            int sy = (h + fm.getAscent()) / 2 - 4;
            g2.drawString(sym, sx, sy);

            // Bottom-right (rotated)
            g2.setFont(new Font("SansSerif", Font.BOLD, 14));
            g2.drawString(card.rank.symbol, w - 20, h - 18);
        }

        // Border
        g2.setColor(new Color(180, 180, 180));
        g2.setStroke(new BasicStroke(1.5f));
        g2.drawRoundRect(2, 2, w-4, h-4, 10, 10);
    }
}