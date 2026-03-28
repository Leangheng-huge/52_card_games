package org.example.ui;

import java.awt.*;

public class Theme {
    public static final Color BG_DARK       = new Color(15, 40, 15);
    public static final Color BG_TABLE      = new Color(20, 100, 45);
    public static final Color BG_TABLE_DARK = new Color(10, 70, 30);
    public static final Color ACCENT        = new Color(220, 180, 50);
    public static final Color ACCENT2       = new Color(255, 215, 80);
    public static final Color TEXT_LIGHT    = new Color(240, 235, 210);
    public static final Color TEXT_DIM      = new Color(160, 155, 130);
    public static final Color BTN_BG        = new Color(180, 140, 30);
    public static final Color BTN_HOVER     = new Color(220, 180, 50);
    public static final Color BTN_RED       = new Color(180, 40, 40);
    public static final Color BTN_RED_HOVER = new Color(220, 70, 70);
    public static final Color PANEL_BG      = new Color(10, 55, 25);
    public static final Color PANEL_BORDER  = new Color(60, 130, 70);

    public static final Font FONT_TITLE  = new Font("Georgia", Font.BOLD, 22);
    public static final Font FONT_SUB    = new Font("Georgia", Font.ITALIC, 14);
    public static final Font FONT_BODY   = new Font("Tahoma", Font.PLAIN, 13);
    public static final Font FONT_BTN    = new Font("Tahoma", Font.BOLD, 13);
    public static final Font FONT_STATUS = new Font("Tahoma", Font.BOLD, 15);

    public static javax.swing.JButton makeButton(String text, Color bg, Color hover) {
        javax.swing.JButton btn = new javax.swing.JButton(text) {
            boolean hovered = false;
            { addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) { hovered = true; repaint(); }
                public void mouseExited(java.awt.event.MouseEvent e)  { hovered = false; repaint(); }
            }); }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(hovered ? hover : bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                g2.setColor(ACCENT);
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 12, 12);
                g2.setColor(new Color(0,0,0,80));
                g2.setFont(getFont());
                FontMetrics fm = g2.getFontMetrics();
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2+1, (getHeight()+fm.getAscent()-fm.getDescent())/2+1);
                g2.setColor(TEXT_LIGHT);
                g2.drawString(getText(), (getWidth()-fm.stringWidth(getText()))/2, (getHeight()+fm.getAscent()-fm.getDescent())/2);
            }
        };
        btn.setFont(FONT_BTN);
        btn.setForeground(TEXT_LIGHT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(130, 38));
        return btn;
    }

    public static javax.swing.JButton makeButton(String text) {
        return makeButton(text, BTN_BG, BTN_HOVER);
    }
}