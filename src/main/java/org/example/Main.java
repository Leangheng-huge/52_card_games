package org.example;

import org.example.games.*;
import org.example.ui.Theme;
import org.example.games.*;

import javax.swing.*;
import java.awt.*;

public class Main extends JFrame {

    public Main() {
        setTitle("🃏 52-Card Games Suite");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 720);
        setMinimumSize(new Dimension(800, 600));
        setLocationRelativeTo(null);

        // Dark background
        getContentPane().setBackground(Theme.BG_DARK);

        // Tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("Georgia", Font.BOLD, 14));
        tabs.setBackground(Theme.BG_DARK);
        tabs.setForeground(Theme.ACCENT);
        tabs.setOpaque(true);

        // Tabs for each game
        tabs.addTab("♠ Blackjack",  null, new BlackjackPanel(), "Beat the dealer to 21");
        tabs.addTab("🃏 Poker",      null, new PokerPanel(),     "5-Card Draw Poker");
        tabs.addTab("⚔ War",        null, new WarPanel(),        "Battle card by card");
        tabs.addTab("👏 Snap",       null, new SnapPanel(),       "Spot the match!");
        tabs.addTab("🐟 Go Fish",    null, new GoFishPanel(),     "Ask and collect books");
        tabs.addTab("👴 Old Maid",   null, new OldMaidPanel(),    "Avoid the lone Queen!");

        // Style the tabs
        UIManager.put("TabbedPane.selected",          Theme.BG_TABLE);
        UIManager.put("TabbedPane.background",        Theme.BG_DARK);
        UIManager.put("TabbedPane.foreground",        Theme.ACCENT);
        UIManager.put("TabbedPane.tabAreaBackground", Theme.BG_DARK);
        UIManager.put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));

        add(tabs, BorderLayout.CENTER);

        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(new Color(5, 20, 5));
        header.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        JLabel logo = new JLabel("♠ ♥ Card Games Suite ♦ ♣", SwingConstants.CENTER);
        logo.setFont(new Font("Georgia", Font.BOLD, 18));
        logo.setForeground(Theme.ACCENT);
        JLabel sub = new JLabel("6 Classic Games  •  52-Card Deck  •  vs CPU", SwingConstants.CENTER);
        sub.setFont(Theme.FONT_SUB);
        sub.setForeground(Theme.TEXT_DIM);
        header.add(logo, BorderLayout.CENTER);
        header.add(sub,  BorderLayout.SOUTH);
        add(header, BorderLayout.NORTH);

        // Status bar
        JPanel statusBar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        statusBar.setBackground(new Color(5, 20, 5));
        JLabel ver = new JLabel("v1.0  |  Java Swing  |  52-Card Games Suite");
        ver.setFont(new Font("Tahoma", Font.PLAIN, 11));
        ver.setForeground(Theme.TEXT_DIM);
        statusBar.add(ver);
        add(statusBar, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> new Main().setVisible(true));
    }
}