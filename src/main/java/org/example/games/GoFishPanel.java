package org.example.games;

import org.example.core.*;
import org.example.ui.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class GoFishPanel extends JPanel {
    private Deck deck;
    private List<Card> playerHand, cpuHand;
    private int playerBooks = 0, cpuBooks = 0;
    private JPanel playerCardsPanel;
    private JLabel statusLabel, playerBooksLabel, cpuBooksLabel, deckCountLabel;
    private JButton goFishBtn, newGameBtn;
    private JComboBox<String> rankSelector;
    private JTextArea logArea;

    public GoFishPanel() {
        setBackground(Theme.BG_TABLE);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        deck = new Deck();
        playerHand = new ArrayList<>();
        cpuHand    = new ArrayList<>();
        initUI();
        startGame();
    }

    private void initUI() {
        JLabel title = new JLabel("🐟 GO FISH 🐟", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.ACCENT);

        // Player hand area
        JPanel handSection = makeSectionPanel("Your Hand");
        playerCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        playerCardsPanel.setOpaque(false);
        handSection.add(playerCardsPanel);

        // Books
        JPanel booksPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        booksPanel.setOpaque(false);
        playerBooksLabel = makeInfoLabel("Your Books: 0");
        playerBooksLabel.setForeground(new Color(100,220,100));
        cpuBooksLabel    = makeInfoLabel("CPU Books: 0");
        cpuBooksLabel.setForeground(new Color(220,100,100));
        deckCountLabel   = makeInfoLabel("Deck: 52");
        deckCountLabel.setForeground(Theme.TEXT_DIM);
        booksPanel.add(playerBooksLabel);
        booksPanel.add(deckCountLabel);
        booksPanel.add(cpuBooksLabel);

        // Ask panel
        JPanel askPanel = new JPanel(new FlowLayout());
        askPanel.setOpaque(false);
        askPanel.add(new JLabel("Ask for rank:") {{ setForeground(Theme.TEXT_LIGHT); setFont(Theme.FONT_BODY); }});
        rankSelector = new JComboBox<>();
        rankSelector.setFont(Theme.FONT_BODY);
        rankSelector.setBackground(new Color(30, 80, 40));
        rankSelector.setForeground(Theme.TEXT_LIGHT);
        askPanel.add(rankSelector);

        goFishBtn  = Theme.makeButton("Ask CPU!");
        newGameBtn = Theme.makeButton("New Game", Theme.BTN_RED, Theme.BTN_RED_HOVER);
        goFishBtn.addActionListener(e -> playerAsk());
        newGameBtn.addActionListener(e -> startGame());
        askPanel.add(goFishBtn);
        askPanel.add(newGameBtn);

        // Status
        statusLabel = new JLabel("Your turn!", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_STATUS);
        statusLabel.setForeground(Theme.TEXT_LIGHT);

        // Log
        logArea = new JTextArea(6, 30);
        logArea.setEditable(false);
        logArea.setBackground(new Color(10, 40, 15));
        logArea.setForeground(Theme.TEXT_LIGHT);
        logArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        logArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JScrollPane logScroll = new JScrollPane(logArea);
        logScroll.setBorder(BorderFactory.createLineBorder(Theme.PANEL_BORDER));

        JPanel centerPanel = new JPanel(new BorderLayout(5, 5));
        centerPanel.setOpaque(false);
        centerPanel.add(handSection, BorderLayout.NORTH);
        centerPanel.add(booksPanel, BorderLayout.CENTER);
        centerPanel.add(logScroll, BorderLayout.SOUTH);

        JPanel southPanel = new JPanel(new BorderLayout(5, 5));
        southPanel.setOpaque(false);
        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(askPanel, BorderLayout.CENTER);

        add(title, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
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
        return l;
    }

    private void startGame() {
        deck.reset(); deck.shuffle();
        playerHand.clear(); cpuHand.clear();
        playerBooks = 0; cpuBooks = 0;
        logArea.setText("");

        for (int i = 0; i < 7; i++) {
            playerHand.add(deck.deal());
            cpuHand.add(deck.deal());
        }
        checkBooks(playerHand, "You"); checkBooks(cpuHand, "CPU");
        refreshUI();
        log("=== New Game of Go Fish! ===");
        log("You have " + playerHand.size() + " cards, CPU has " + cpuHand.size() + " cards.");
        statusLabel.setText("Your turn! Ask the CPU for a rank.");
    }

    private void playerAsk() {
        if (rankSelector.getItemCount() == 0) return;
        String rankName = (String) rankSelector.getSelectedItem();
        Card.Rank askRank = Card.Rank.valueOf(rankName);

        List<Card> got = cpuHand.stream().filter(c -> c.rank == askRank).collect(Collectors.toList());
        if (!got.isEmpty()) {
            cpuHand.removeAll(got);
            playerHand.addAll(got);
            log("You: 'Got any " + rankName + "s?' → CPU gave you " + got.size() + " card(s)!");
            checkBooks(playerHand, "You");
        } else {
            log("You: 'Got any " + rankName + "s?' → CPU says: GO FISH! 🐟");
            if (!deck.isEmpty()) {
                Card drawn = deck.deal();
                playerHand.add(drawn);
                log("You drew: " + drawn);
                if (drawn.rank == askRank) log("Lucky! You drew the rank you asked for!");
                checkBooks(playerHand, "You");
            }
            // CPU's turn
            SwingUtilities.invokeLater(this::cpuTurn);
        }

        refreshUI();
        checkGameOver();
    }

    private void cpuTurn() {
        if (cpuHand.isEmpty()) return;
        // CPU strategy: ask for rank it has most of
        Map<Card.Rank, Long> freq = cpuHand.stream()
                .collect(Collectors.groupingBy(c -> c.rank, Collectors.counting()));
        Card.Rank askRank = freq.entrySet().stream()
                .max(Map.Entry.comparingByValue()).map(Map.Entry::getKey).orElse(cpuHand.get(0).rank);

        List<Card> got = playerHand.stream().filter(c -> c.rank == askRank).collect(Collectors.toList());
        if (!got.isEmpty()) {
            playerHand.removeAll(got);
            cpuHand.addAll(got);
            log("CPU asks for " + askRank.symbol + "s → got " + got.size() + " from you!");
            checkBooks(cpuHand, "CPU");
        } else {
            log("CPU asks for " + askRank.symbol + "s → GO FISH! 🐟");
            if (!deck.isEmpty()) {
                cpuHand.add(deck.deal());
                checkBooks(cpuHand, "CPU");
            }
        }

        refreshUI();
        checkGameOver();
    }

    private void checkBooks(List<Card> hand, String owner) {
        Map<Card.Rank, Long> freq = new HashMap<>();
        for (Card c : hand) freq.merge(c.rank, 1L, Long::sum);
        for (Map.Entry<Card.Rank, Long> e : freq.entrySet()) {
            if (e.getValue() == 4) {
                hand.removeIf(c -> c.rank == e.getKey());
                if (owner.equals("You")) { playerBooks++; log("📚 You completed a book of " + e.getKey().symbol + "s!"); }
                else                    { cpuBooks++;    log("📚 CPU completed a book of " + e.getKey().symbol + "s!"); }
            }
        }
    }

    public void refreshUI() {
        playerCardsPanel.removeAll();
        playerHand.stream().sorted(Comparator.comparingInt(c -> c.rank.ordinal()))
                .forEach(c -> playerCardsPanel.add(new CardPanel(c, false)));
        playerCardsPanel.revalidate(); playerCardsPanel.repaint();

        playerBooksLabel.setText("Your Books: " + playerBooks);
        cpuBooksLabel.setText("CPU Books: " + cpuBooks);
        deckCountLabel.setText("Deck: " + deck.size());

        rankSelector.removeAllItems();
        playerHand.stream().map(c -> c.rank).distinct()
                .sorted(Comparator.comparingInt(Enum::ordinal))
                .forEach(r -> rankSelector.addItem(r.name()));
    }

    private void checkGameOver() {
        if ((playerHand.isEmpty() || cpuHand.isEmpty()) && deck.isEmpty()) {
            goFishBtn.setEnabled(false);
            String winner = playerBooks > cpuBooks ? "YOU WIN! 🏆 (" + playerBooks + " vs " + cpuBooks + " books)"
                    : cpuBooks > playerBooks ? "CPU WINS! 😔 (" + cpuBooks + " vs " + playerBooks + " books)"
                    : "TIE! (" + playerBooks + " books each)";
            statusLabel.setText(winner);
            statusLabel.setForeground(Theme.ACCENT);
            log("=== Game Over: " + winner + " ===");
        }
    }

    private void log(String msg) {
        logArea.append(msg + "\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());
    }
}