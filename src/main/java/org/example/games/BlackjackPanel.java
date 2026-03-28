package org.example.games;

import org.example.core.Card;
import org.example.core.Deck;
import org.example.ui.*;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BlackjackPanel extends JPanel {
    private Deck deck;
    private List<Card> playerHand, dealerHand;
    private JPanel playerCardsPanel, dealerCardsPanel;
    private JLabel playerScoreLabel, dealerScoreLabel, statusLabel, balanceLabel, betLabel;
    private JButton hitBtn, standBtn, dealBtn, doubleBtn;
    private int balance = 1000, bet = 0;
    private boolean gameActive = false;
    private boolean dealerRevealed = false;

    public BlackjackPanel() {
        setBackground(Theme.BG_TABLE);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        deck = new Deck();
        playerHand = new ArrayList<>();
        dealerHand = new ArrayList<>();
        initUI();
    }

    private void initUI() {
        // Title
        JLabel title = new JLabel("♠ BLACKJACK ♠", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.ACCENT);
        title.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

        // Dealer area
        JPanel dealerArea = makeSectionPanel("Dealer");
        dealerCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        dealerCardsPanel.setOpaque(false);
        dealerScoreLabel = makeScoreLabel("Score: ?");
        dealerArea.add(dealerCardsPanel);
        dealerArea.add(dealerScoreLabel);

        // Player area
        JPanel playerArea = makeSectionPanel("Your Hand");
        playerCardsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
        playerCardsPanel.setOpaque(false);
        playerScoreLabel = makeScoreLabel("Score: 0");
        playerArea.add(playerCardsPanel);
        playerArea.add(playerScoreLabel);

        // Center split
        JSplitPane tablePane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, dealerArea, playerArea);
        tablePane.setOpaque(false);
        tablePane.setDividerSize(4);
        tablePane.setResizeWeight(0.5);
        tablePane.setBackground(Theme.BG_TABLE_DARK);

        // Status
        statusLabel = new JLabel("Place your bet and deal!", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_STATUS);
        statusLabel.setForeground(Theme.TEXT_LIGHT);

        // Balance & Bet
        balanceLabel = new JLabel("Balance: $" + balance, SwingConstants.CENTER);
        balanceLabel.setFont(Theme.FONT_BODY);
        balanceLabel.setForeground(Theme.ACCENT2);

        betLabel = new JLabel("Bet: $" + bet, SwingConstants.CENTER);
        betLabel.setFont(Theme.FONT_BODY);
        betLabel.setForeground(Theme.TEXT_LIGHT);

        // Bet buttons
        JPanel betPanel = new JPanel(new FlowLayout());
        betPanel.setOpaque(false);
        for (int amt : new int[]{10, 25, 50, 100}) {
            JButton b = Theme.makeButton("+$" + amt);
            b.setPreferredSize(new Dimension(80, 32));
            int finalAmt = amt;
            b.addActionListener(e -> { if (!gameActive) { bet = Math.min(bet + finalAmt, balance); betLabel.setText("Bet: $" + bet); } });
            betPanel.add(b);
        }
        JButton clearBet = Theme.makeButton("Clear", Theme.BTN_RED, Theme.BTN_RED_HOVER);
        clearBet.setPreferredSize(new Dimension(70, 32));
        clearBet.addActionListener(e -> { if (!gameActive) { bet = 0; betLabel.setText("Bet: $0"); } });
        betPanel.add(clearBet);

        // Action buttons
        hitBtn     = Theme.makeButton("Hit");
        standBtn   = Theme.makeButton("Stand");
        doubleBtn  = Theme.makeButton("Double");
        dealBtn    = Theme.makeButton("Deal");

        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);
        doubleBtn.setEnabled(false);

        hitBtn.addActionListener(e -> playerHit());
        standBtn.addActionListener(e -> dealerPlay());
        doubleBtn.addActionListener(e -> doubleDown());
        dealBtn.addActionListener(e -> startGame());

        JPanel actionPanel = new JPanel(new FlowLayout());
        actionPanel.setOpaque(false);
        actionPanel.add(dealBtn);
        actionPanel.add(hitBtn);
        actionPanel.add(standBtn);
        actionPanel.add(doubleBtn);

        // Bottom
        JPanel bottomPanel = new JPanel(new BorderLayout(5, 5));
        bottomPanel.setOpaque(false);

        JPanel infoRow = new JPanel(new FlowLayout());
        infoRow.setOpaque(false);
        infoRow.add(balanceLabel);
        infoRow.add(new JLabel("  |  ") {{ setForeground(Theme.TEXT_DIM); }});
        infoRow.add(betLabel);

        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        bottomPanel.add(infoRow, BorderLayout.CENTER);
        bottomPanel.add(betPanel, BorderLayout.SOUTH);

        add(title, BorderLayout.NORTH);
        add(tablePane, BorderLayout.CENTER);
        JPanel southAll = new JPanel(new BorderLayout());
        southAll.setOpaque(false);
        southAll.add(bottomPanel, BorderLayout.NORTH);
        southAll.add(actionPanel, BorderLayout.SOUTH);
        add(southAll, BorderLayout.SOUTH);
    }

    private JPanel makeSectionPanel(String title) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Theme.BG_TABLE_DARK);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PANEL_BORDER, 1),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        JLabel lbl = new JLabel(title, SwingConstants.CENTER);
        lbl.setFont(Theme.FONT_SUB);
        lbl.setForeground(Theme.TEXT_DIM);
        lbl.setAlignmentX(CENTER_ALIGNMENT);
        p.add(lbl);
        p.add(Box.createVerticalStrut(5));
        return p;
    }

    private JLabel makeScoreLabel(String text) {
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.TEXT_LIGHT);
        l.setAlignmentX(CENTER_ALIGNMENT);
        return l;
    }

    private void startGame() {
        if (bet == 0) { statusLabel.setText("Place a bet first!"); return; }
        if (bet > balance) { statusLabel.setText("Not enough balance!"); return; }
        balance -= bet;
        balanceLabel.setText("Balance: $" + balance);

        deck.reset(); deck.shuffle();
        playerHand.clear(); dealerHand.clear();
        dealerRevealed = false;
        gameActive = true;

        playerHand.add(deck.deal()); dealerHand.add(deck.deal());
        playerHand.add(deck.deal()); dealerHand.add(deck.deal());

        renderCards();
        updateScores(false);

        hitBtn.setEnabled(true);
        standBtn.setEnabled(true);
        doubleBtn.setEnabled(balance >= bet);
        dealBtn.setEnabled(false);

        if (handValue(playerHand) == 21) {
            statusLabel.setText("BLACKJACK! 🎉");
            endGame("blackjack");
        } else {
            statusLabel.setText("Your turn — Hit or Stand?");
        }
    }

    private void playerHit() {
        playerHand.add(deck.deal());
        doubleBtn.setEnabled(false);
        renderCards();
        int val = handValue(playerHand);
        updateScores(false);
        if (val > 21) {
            statusLabel.setText("BUST! You went over 21 💥");
            endGame("bust");
        } else if (val == 21) {
            dealerPlay();
        }
    }

    private void doubleDown() {
        balance -= bet;
        bet *= 2;
        balanceLabel.setText("Balance: $" + balance);
        betLabel.setText("Bet: $" + bet);
        playerHand.add(deck.deal());
        renderCards();
        updateScores(false);
        if (handValue(playerHand) > 21) {
            statusLabel.setText("BUST on double! 💥");
            endGame("bust");
        } else {
            dealerPlay();
        }
    }

    private void dealerPlay() {
        dealerRevealed = true;
        hitBtn.setEnabled(false);
        standBtn.setEnabled(false);
        doubleBtn.setEnabled(false);

        while (handValue(dealerHand) < 17)
            dealerHand.add(deck.deal());

        renderCards();
        updateScores(true);

        int pv = handValue(playerHand), dv = handValue(dealerHand);
        if (dv > 21) {
            statusLabel.setText("Dealer busts! You WIN! 🏆");
            endGame("win");
        } else if (pv > dv) {
            statusLabel.setText("You WIN! 🏆");
            endGame("win");
        } else if (pv < dv) {
            statusLabel.setText("Dealer wins. 😔");
            endGame("loss");
        } else {
            statusLabel.setText("PUSH — it's a tie!");
            endGame("push");
        }
    }

    private void endGame(String result) {
        gameActive = false;
        switch (result) {
            case "win"       -> balance += bet * 2;
            case "blackjack" -> balance += (int)(bet * 2.5);
            case "push"      -> balance += bet;
        }
        bet = 0;
        betLabel.setText("Bet: $0");
        balanceLabel.setText("Balance: $" + balance);
        dealBtn.setEnabled(true);
        dealerRevealed = true;
        renderCards();
        updateScores(true);
        if (balance == 0) {
            statusLabel.setText("Out of money! Game over.");
            dealBtn.setEnabled(false);
        }
    }

    private int handValue(List<Card> hand) {
        int total = 0, aces = 0;
        for (Card c : hand) {
            total += c.rank.value;
            if (c.rank == Card.Rank.ACE) aces++;
        }
        while (total > 21 && aces-- > 0) total -= 10;
        return total;
    }

    private void renderCards() {
        playerCardsPanel.removeAll();
        dealerCardsPanel.removeAll();
        for (Card c : playerHand) playerCardsPanel.add(new CardPanel(c, false));
        for (int i = 0; i < dealerHand.size(); i++) {
            dealerCardsPanel.add(new CardPanel(dealerHand.get(i), i == 1 && !dealerRevealed));
        }
        playerCardsPanel.revalidate(); playerCardsPanel.repaint();
        dealerCardsPanel.revalidate(); dealerCardsPanel.repaint();
    }

    private void updateScores(boolean showDealer) {
        playerScoreLabel.setText("Score: " + handValue(playerHand));
        if (showDealer) {
            int dv = handValue(dealerHand);
            dealerScoreLabel.setText("Score: " + dv + (dv > 21 ? " (BUST)" : ""));
        } else {
            dealerScoreLabel.setText("Score: " + dealerHand.get(0).rank.value + " + ?");
        }
    }
}