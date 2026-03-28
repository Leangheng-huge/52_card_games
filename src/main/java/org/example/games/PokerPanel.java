package org.example.games;

import org.example.core.Card;
import org.example.ui.*;
import org.example.core.Deck;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

public class PokerPanel extends JPanel {
    private Deck deck;
    private List<Card> playerHand, cpuHand;
    private boolean[] playerDiscard;
    private List<CardPanel> playerCardPanels;
    private JPanel playerCardsPanel, cpuCardsPanel;
    private JLabel statusLabel, balanceLabel, betLabel, playerHandLabel, cpuHandLabel;
    private JButton dealBtn, drawBtn, showBtn;
    private int balance = 1000, bet = 0;
    private enum Phase { IDLE, BETTING, DRAW, SHOWDOWN }
    private Phase phase = Phase.IDLE;

    public PokerPanel() {
        setBackground(Theme.BG_TABLE);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        deck = new Deck();
        playerHand = new ArrayList<>();
        cpuHand    = new ArrayList<>();
        playerDiscard = new boolean[5];
        playerCardPanels = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        JLabel title = new JLabel("🃏 5-CARD DRAW POKER 🃏", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.ACCENT);

        // CPU area
        JPanel cpuArea = makeSectionPanel("CPU Hand");
        cpuCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        cpuCardsPanel.setOpaque(false);
        cpuHandLabel = makeInfoLabel("???");
        cpuArea.add(cpuCardsPanel);
        cpuArea.add(cpuHandLabel);

        // Player area
        JPanel playerArea = makeSectionPanel("Your Hand — Click cards to discard (Draw phase)");
        playerCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        playerCardsPanel.setOpaque(false);
        playerHandLabel = makeInfoLabel("");
        playerArea.add(playerCardsPanel);
        playerArea.add(playerHandLabel);

        JSplitPane tablePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, cpuArea, playerArea);
        tablePane.setOpaque(false);
        tablePane.setDividerSize(4);
        tablePane.setResizeWeight(0.5);

        // Status & balance
        statusLabel = new JLabel("Place a bet and deal!", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_STATUS);
        statusLabel.setForeground(Theme.TEXT_LIGHT);

        balanceLabel = new JLabel("Balance: $" + balance, SwingConstants.CENTER);
        balanceLabel.setFont(Theme.FONT_BODY);
        balanceLabel.setForeground(Theme.ACCENT2);

        betLabel = new JLabel("Bet: $0", SwingConstants.CENTER);
        betLabel.setFont(Theme.FONT_BODY);
        betLabel.setForeground(Theme.TEXT_LIGHT);

        // Bet buttons
        JPanel betPanel = new JPanel(new FlowLayout());
        betPanel.setOpaque(false);
        for (int amt : new int[]{10, 25, 50, 100}) {
            JButton b = Theme.makeButton("+$" + amt);
            b.setPreferredSize(new Dimension(80, 32));
            int a = amt;
            b.addActionListener(e -> { if (phase == Phase.IDLE) { bet = Math.min(bet+a, balance); betLabel.setText("Bet: $"+bet); } });
            betPanel.add(b);
        }
        JButton clearBet = Theme.makeButton("Clear", Theme.BTN_RED, Theme.BTN_RED_HOVER);
        clearBet.setPreferredSize(new Dimension(70, 32));
        clearBet.addActionListener(e -> { if (phase == Phase.IDLE) { bet=0; betLabel.setText("Bet: $0"); } });
        betPanel.add(clearBet);

        dealBtn = Theme.makeButton("Deal");
        drawBtn = Theme.makeButton("Draw");
        showBtn = Theme.makeButton("Showdown");
        drawBtn.setEnabled(false);
        showBtn.setEnabled(false);

        dealBtn.addActionListener(e -> deal());
        drawBtn.addActionListener(e -> draw());
        showBtn.addActionListener(e -> showdown());

        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setOpaque(false);
        actionPanel.add(dealBtn);
        actionPanel.add(drawBtn);
        actionPanel.add(showBtn);

        JPanel infoRow = new JPanel(new FlowLayout());
        infoRow.setOpaque(false);
        infoRow.add(balanceLabel);
        infoRow.add(new JLabel("  |  ") {{ setForeground(Theme.TEXT_DIM); }});
        infoRow.add(betLabel);

        JPanel southPanel = new JPanel(new BorderLayout(5,5));
        southPanel.setOpaque(false);
        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(infoRow, BorderLayout.CENTER);
        southPanel.add(betPanel, BorderLayout.SOUTH);

        JPanel veryBottom = new JPanel(new BorderLayout());
        veryBottom.setOpaque(false);
        veryBottom.add(southPanel, BorderLayout.NORTH);
        veryBottom.add(actionPanel, BorderLayout.SOUTH);

        add(title, BorderLayout.NORTH);
        add(tablePane, BorderLayout.CENTER);
        add(veryBottom, BorderLayout.SOUTH);
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

    private void deal() {
        if (bet == 0) { statusLabel.setText("Place a bet first!"); return; }
        if (bet > balance) { statusLabel.setText("Not enough balance!"); return; }
        balance -= bet;
        balanceLabel.setText("Balance: $" + balance);

        deck.reset(); deck.shuffle();
        playerHand.clear(); cpuHand.clear();
        Arrays.fill(playerDiscard, false);

        for (int i = 0; i < 5; i++) { playerHand.add(deck.deal()); cpuHand.add(deck.deal()); }

        phase = Phase.DRAW;
        renderHands(false);
        playerHandLabel.setText(evaluateHand(playerHand));
        cpuHandLabel.setText("???");
        statusLabel.setText("Click cards to discard, then press Draw.");
        statusLabel.setForeground(Theme.TEXT_LIGHT);
        dealBtn.setEnabled(false);
        drawBtn.setEnabled(true);
        showBtn.setEnabled(false);
    }

    private void draw() {
        // CPU discards (simple AI: keep pairs+, discard low cards)
        cpuDiscard();

        // Player draws
        for (int i = 0; i < 5; i++) {
            if (playerDiscard[i]) {
                playerHand.set(i, deck.deal());
                playerDiscard[i] = false;
            }
        }

        phase = Phase.SHOWDOWN;
        renderHands(false);
        playerHandLabel.setText(evaluateHand(playerHand));
        cpuHandLabel.setText("???");
        statusLabel.setText("Cards drawn! Press Showdown to reveal.");
        drawBtn.setEnabled(false);
        showBtn.setEnabled(true);
    }

    private void cpuDiscard() {
        // Simple: discard cards not forming pairs
        Map<Card.Rank, Long> freq = cpuHand.stream().collect(
                Collectors.groupingBy(c -> c.rank, Collectors.counting()));
        Set<Card.Rank> keep = freq.entrySet().stream()
                .filter(e -> e.getValue() >= 2).map(Map.Entry::getKey).collect(Collectors.toSet());

        // If flush draw, keep all same suit
        Map<Card.Suit, Long> suitFreq = cpuHand.stream().collect(
                Collectors.groupingBy(c -> c.suit, Collectors.counting()));
        Optional<Map.Entry<Card.Suit, Long>> flushSuit = suitFreq.entrySet().stream()
                .filter(e -> e.getValue() >= 4).findFirst();
        if (flushSuit.isPresent()) {
            Card.Suit s = flushSuit.get().getKey();
            for (int i = 0; i < 5; i++)
                if (cpuHand.get(i).suit != s) cpuHand.set(i, deck.deal());
            return;
        }

        if (keep.isEmpty()) {
            // Keep high cards (J, Q, K, A)
            for (int i = 0; i < 5; i++) {
                Card c = cpuHand.get(i);
                if (c.rank.value < 10) cpuHand.set(i, deck.deal());
            }
        } else {
            for (int i = 0; i < 5; i++) {
                if (!keep.contains(cpuHand.get(i).rank)) cpuHand.set(i, deck.deal());
            }
        }
    }

    private void showdown() {
        renderHands(true);
        String pRank = evaluateHand(playerHand);
        String cRank = evaluateHand(cpuHand);
        playerHandLabel.setText(pRank);
        cpuHandLabel.setText(cRank);

        int pScore = handScore(playerHand), cScore = handScore(cpuHand);
        String result;
        if (pScore > cScore) {
            balance += bet * 2;
            result = "YOU WIN with " + pRank + "! 🏆 +$" + (bet*2);
            statusLabel.setForeground(new Color(100,220,100));
        } else if (cScore > pScore) {
            result = "CPU wins with " + cRank + ". 😔";
            statusLabel.setForeground(new Color(220,100,100));
        } else {
            balance += bet;
            result = "PUSH — Tie! $" + bet + " returned.";
            statusLabel.setForeground(Theme.ACCENT2);
        }

        bet = 0;
        betLabel.setText("Bet: $0");
        balanceLabel.setText("Balance: $" + balance);
        statusLabel.setText(result);
        phase = Phase.IDLE;
        dealBtn.setEnabled(true);
        drawBtn.setEnabled(false);
        showBtn.setEnabled(false);
    }

    private void renderHands(boolean showCpu) {
        playerCardsPanel.removeAll();
        cpuCardsPanel.removeAll();
        playerCardPanels.clear();

        for (int i = 0; i < playerHand.size(); i++) {
            final int idx = i;
            CardPanel cp = new CardPanel(playerHand.get(i), false);
            if (playerDiscard[i]) {
                cp.setBorder(BorderFactory.createLineBorder(Color.RED, 3));
                cp.setBackground(new Color(255, 220, 220));
            } else {
                cp.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
                cp.setBackground(Color.WHITE);
            }
            if (phase == Phase.DRAW) {
                cp.addMouseListener(new java.awt.event.MouseAdapter() {
                    public void mouseClicked(java.awt.event.MouseEvent e) {
                        playerDiscard[idx] = !playerDiscard[idx];
                        renderHands(false);
                    }
                });
                cp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
            playerCardsPanel.add(cp);
            playerCardPanels.add(cp);
        }

        for (int i = 0; i < cpuHand.size(); i++) {
            cpuCardsPanel.add(new CardPanel(cpuHand.get(i), !showCpu));
        }

        playerCardsPanel.revalidate(); playerCardsPanel.repaint();
        cpuCardsPanel.revalidate();   cpuCardsPanel.repaint();
    }

    // ─── Hand Evaluation ─────────────────────────────────────────────────
    private String evaluateHand(List<Card> hand) {
        int score = handScore(hand);
        return switch (score / 1_000_000) {
            case 8 -> "Straight Flush";
            case 7 -> "Four of a Kind";
            case 6 -> "Full House";
            case 5 -> "Flush";
            case 4 -> "Straight";
            case 3 -> "Three of a Kind";
            case 2 -> "Two Pair";
            case 1 -> "One Pair";
            default -> "High Card";
        };
    }

    private int handScore(List<Card> hand) {
        Map<Card.Rank, Long> freq = hand.stream()
                .collect(Collectors.groupingBy(c -> c.rank, Collectors.counting()));
        boolean flush    = hand.stream().map(c -> c.suit).distinct().count() == 1;
        List<Integer> vals = hand.stream().map(c -> c.rank.ordinal()).sorted().collect(Collectors.toList());
        boolean straight = (vals.get(4)-vals.get(0) == 4 && freq.size() == 5);
        // Ace-low straight
        if (!straight && vals.contains(12) && vals.subList(0,4).equals(List.of(0,1,2,3))) straight = true;

        List<Long> counts = new ArrayList<>(freq.values()); counts.sort(Collections.reverseOrder());

        int base;
        if (straight && flush) base = 8;
        else if (counts.get(0) == 4) base = 7;
        else if (counts.get(0) == 3 && counts.get(1) == 2) base = 6;
        else if (flush)    base = 5;
        else if (straight) base = 4;
        else if (counts.get(0) == 3) base = 3;
        else if (counts.get(0) == 2 && counts.size() > 1 && counts.get(1) == 2) base = 2;
        else if (counts.get(0) == 2) base = 1;
        else base = 0;

        int high = hand.stream().mapToInt(c -> c.rank.ordinal()).max().orElse(0);
        return base * 1_000_000 + high;
    }
}