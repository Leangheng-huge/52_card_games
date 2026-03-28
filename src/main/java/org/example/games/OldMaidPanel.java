package org.example.games;

import org.example.core.*;
import org.example.ui.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import javax.swing.Timer;
import java.util.List;


public class OldMaidPanel extends JPanel {
    private List<Card> playerHand, cpuHand;
    private JPanel playerCardsPanel, cpuCardsPanel;
    private JLabel statusLabel, playerCountLabel, cpuCountLabel;
    private JButton pickBtn, newGameBtn;
    private JTextArea logArea;
    private int selectedCpuIdx = -1;

    public OldMaidPanel() {
        setBackground(Theme.BG_TABLE);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        playerHand = new ArrayList<>();
        cpuHand    = new ArrayList<>();
        initUI();
        startGame();
    }

    private void initUI() {
        JLabel title = new JLabel("👴 OLD MAID 👴", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.ACCENT);

        // CPU area
        JPanel cpuSection = makeSectionPanel("CPU's Hand (face down — pick one!)");
        cpuCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        cpuCardsPanel.setOpaque(false);
        cpuCountLabel = makeInfoLabel("CPU: 0 cards");
        cpuSection.add(cpuCardsPanel);
        cpuSection.add(cpuCountLabel);

        // Player area
        JPanel playerSection = makeSectionPanel("Your Hand");
        playerCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        playerCardsPanel.setOpaque(false);
        playerCountLabel = makeInfoLabel("You: 0 cards");
        playerSection.add(playerCardsPanel);
        playerSection.add(playerCountLabel);

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cpuSection, playerSection);
        split.setOpaque(false); split.setDividerSize(4); split.setResizeWeight(0.5);

        statusLabel = new JLabel("Pick a card from CPU!", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_STATUS);
        statusLabel.setForeground(Theme.TEXT_LIGHT);

        logArea = new JTextArea(4, 30);
        logArea.setEditable(false);
        logArea.setBackground(new Color(10,40,15));
        logArea.setForeground(Theme.TEXT_LIGHT);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBorder(BorderFactory.createEmptyBorder(4,4,4,4));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createLineBorder(Theme.PANEL_BORDER));

        pickBtn   = Theme.makeButton("Pick Selected");
        newGameBtn = Theme.makeButton("New Game", Theme.BTN_RED, Theme.BTN_RED_HOVER);
        pickBtn.addActionListener(e -> pickFromCpu());
        newGameBtn.addActionListener(e -> startGame());

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        btnPanel.add(pickBtn);
        btnPanel.add(newGameBtn);

        JPanel southPanel = new JPanel(new BorderLayout(5,5));
        southPanel.setOpaque(false);
        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(logScroll, BorderLayout.CENTER);
        southPanel.add(btnPanel, BorderLayout.SOUTH);

        add(title, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel makeSectionPanel(String heading) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Theme.BG_TABLE_DARK);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PANEL_BORDER,1),
                BorderFactory.createEmptyBorder(8,8,8,8)
        ));
        JLabel l = new JLabel(heading, SwingConstants.CENTER);
        l.setFont(Theme.FONT_SUB); l.setForeground(Theme.TEXT_DIM);
        l.setAlignmentX(CENTER_ALIGNMENT);
        p.add(l); p.add(Box.createVerticalStrut(5));
        return p;
    }

    private JLabel makeInfoLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(Theme.FONT_BODY); l.setForeground(Theme.ACCENT2);
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private void startGame() {
        // Build deck without one Queen (Old Maid)
        List<Card> deckCards = new ArrayList<>();
        for (Card.Suit suit : Card.Suit.values())
            for (Card.Rank rank : Card.Rank.values())
                deckCards.add(new Card(suit, rank));
        // Remove Queen of Spades to make one unmatched Queen
        deckCards.removeIf(c -> c.rank == Card.Rank.QUEEN && c.suit == Card.Suit.SPADES);
        Collections.shuffle(deckCards);

        playerHand.clear(); cpuHand.clear();
        for (int i = 0; i < deckCards.size(); i++) {
            if (i % 2 == 0) playerHand.add(deckCards.get(i));
            else cpuHand.add(deckCards.get(i));
        }

        removePairs(playerHand); removePairs(cpuHand);
        selectedCpuIdx = -1;
        logArea.setText("");
        log("=== Old Maid started! One Queen is missing — don't hold it! ===");
        refreshUI();
        statusLabel.setText("Click a face-down CPU card to pick it!");
        statusLabel.setForeground(Theme.TEXT_LIGHT);
        pickBtn.setEnabled(true);
    }

    private void removePairs(List<Card> hand) {
        boolean removed = true;
        while (removed) {
            removed = false;
            outer:
            for (int i = 0; i < hand.size(); i++) {
                for (int j = i+1; j < hand.size(); j++) {
                    if (hand.get(i).rank == hand.get(j).rank) {
                        hand.remove(j); hand.remove(i);
                        removed = true; break outer;
                    }
                }
            }
        }
    }

    private void pickFromCpu() {
        if (selectedCpuIdx < 0 || selectedCpuIdx >= cpuHand.size()) {
            statusLabel.setText("Click a CPU card first!");
            return;
        }
        Card picked = cpuHand.remove(selectedCpuIdx);
        playerHand.add(picked);
        log("You picked: " + picked + " from CPU.");

        // Remove any new pairs
        int before = playerHand.size();
        removePairs(playerHand);
        if (playerHand.size() < before) log("✅ Pair removed from your hand!");

        selectedCpuIdx = -1;

        if (checkGameOver()) return;

        // CPU picks from player
        Timer t = new Timer(800, e -> {
            if (!playerHand.isEmpty()) {
                int idx = (int)(Math.random() * playerHand.size());
                Card cpuPicked = playerHand.remove(idx);
                cpuHand.add(cpuPicked);
                log("CPU picked a card from you (face down).");
                int b = cpuHand.size();
                removePairs(cpuHand);
                if (cpuHand.size() < b) log("CPU removed a pair.");
                refreshUI();
                if (!checkGameOver()) {
                    statusLabel.setText("Your turn — pick a CPU card!");
                }
            }
        });
        t.setRepeats(false); t.start();
        refreshUI();
    }

    private boolean checkGameOver() {
        if (playerHand.isEmpty() && cpuHand.isEmpty()) {
            statusLabel.setText("Draw — both hands empty?");
            return true;
        }
        if (playerHand.size() == 1 && playerHand.get(0).rank == Card.Rank.QUEEN) {
            statusLabel.setText("YOU have the Old Maid! 😢 CPU wins!");
            statusLabel.setForeground(new Color(220,100,100));
            pickBtn.setEnabled(false);
            log("=== YOU are the Old Maid! ===");
            return true;
        }
        if (cpuHand.size() == 1 && cpuHand.get(0).rank == Card.Rank.QUEEN) {
            statusLabel.setText("CPU has the Old Maid! YOU WIN! 🏆");
            statusLabel.setForeground(new Color(100,220,100));
            pickBtn.setEnabled(false);
            log("=== CPU is the Old Maid! You win! ===");
            return true;
        }
        if (playerHand.isEmpty()) {
            statusLabel.setText("Your hand is empty — YOU WIN! 🏆");
            pickBtn.setEnabled(false);
            return true;
        }
        if (cpuHand.isEmpty()) {
            statusLabel.setText("CPU's hand is empty — CPU wins!");
            pickBtn.setEnabled(false);
            return true;
        }
        return false;
    }

    public void refreshUI() {
        // Player cards (face up)
        playerCardsPanel.removeAll();
        playerHand.forEach(c -> playerCardsPanel.add(new CardPanel(c, false)));
        playerCardsPanel.revalidate(); playerCardsPanel.repaint();
        playerCountLabel.setText("You: " + playerHand.size() + " cards");

        // CPU cards (face down, clickable)
        cpuCardsPanel.removeAll();
        for (int i = 0; i < cpuHand.size(); i++) {
            final int idx = i;
            CardPanel cp = new CardPanel(cpuHand.get(i), true);
            if (i == selectedCpuIdx) {
                cp.setBorder(BorderFactory.createLineBorder(Theme.ACCENT, 3));
            }
            cp.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    selectedCpuIdx = idx;
                    refreshUI();
                    statusLabel.setText("Selected CPU card #" + (idx+1) + " — press Pick!");
                }
            });
            cp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            cpuCardsPanel.add(cp);
        }
        cpuCardsPanel.revalidate(); cpuCardsPanel.repaint();
        cpuCountLabel.setText("CPU: " + cpuHand.size() + " cards");
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}