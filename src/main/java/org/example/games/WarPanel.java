package org.example.games;

import org.example.core.Card;
import org.example.ui.*;
import org.example.core.Deck;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class WarPanel extends JPanel {
    private Deck deck;
    private List<Card> playerDeck, cpuDeck;
    private JLabel playerCountLabel, cpuCountLabel, statusLabel;
    private JPanel battleArea;
    private JButton drawBtn, newGameBtn;
    private CardPanel playerCardDisplay, cpuCardDisplay;
    private int round = 0;

    public WarPanel() {
        setBackground(Theme.BG_TABLE);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        deck = new Deck();
        playerDeck = new ArrayList<>();
        cpuDeck    = new ArrayList<>();
        initUI();
        startGame();
    }

    private void initUI() {
        JLabel title = new JLabel("⚔ WAR ⚔", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.ACCENT);

        // Battle display
        JPanel cpuSide   = makeSidePanel("CPU");
        JPanel playerSide = makeSidePanel("You");

        cpuCardDisplay    = new CardPanel(null, true);
        playerCardDisplay = new CardPanel(null, true);

        cpuCountLabel     = new JLabel("26 cards", SwingConstants.CENTER);
        playerCountLabel  = new JLabel("26 cards", SwingConstants.CENTER);
        styleCountLabel(cpuCountLabel);
        styleCountLabel(playerCountLabel);

        cpuSide.add(cpuCardDisplay);
        cpuSide.add(Box.createVerticalStrut(8));
        cpuSide.add(cpuCountLabel);

        playerSide.add(playerCardDisplay);
        playerSide.add(Box.createVerticalStrut(8));
        playerSide.add(playerCountLabel);

        JLabel vsLabel = new JLabel("VS", SwingConstants.CENTER);
        vsLabel.setFont(new Font("Georgia", Font.BOLD, 32));
        vsLabel.setForeground(Theme.ACCENT);

        battleArea = new JPanel(new GridBagLayout());
        battleArea.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.gridx = 0; battleArea.add(cpuSide, gbc);
        gbc.gridx = 1; battleArea.add(vsLabel, gbc);
        gbc.gridx = 2; battleArea.add(playerSide, gbc);

        statusLabel = new JLabel("Press DRAW to flip cards!", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_STATUS);
        statusLabel.setForeground(Theme.TEXT_LIGHT);

        JLabel roundLabel = new JLabel("Round 0", SwingConstants.CENTER);

        drawBtn    = Theme.makeButton("Draw ▶");
        newGameBtn = Theme.makeButton("New Game", Theme.BTN_RED, Theme.BTN_RED_HOVER);

        drawBtn.addActionListener(e -> drawRound());
        newGameBtn.addActionListener(e -> startGame());

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        btnPanel.add(drawBtn);
        btnPanel.add(newGameBtn);

        JPanel southPanel = new JPanel(new BorderLayout(4,4));
        southPanel.setOpaque(false);
        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(btnPanel, BorderLayout.CENTER);

        add(title, BorderLayout.NORTH);
        add(battleArea, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private JPanel makeSidePanel(String heading) {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Theme.BG_TABLE_DARK);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.PANEL_BORDER, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        JLabel h = new JLabel(heading, SwingConstants.CENTER);
        h.setFont(Theme.FONT_SUB);
        h.setForeground(Theme.TEXT_DIM);
        h.setAlignmentX(CENTER_ALIGNMENT);
        p.add(h);
        p.add(Box.createVerticalStrut(5));
        return p;
    }

    private void styleCountLabel(JLabel l) {
        l.setFont(Theme.FONT_BODY);
        l.setForeground(Theme.ACCENT2);
        l.setAlignmentX(CENTER_ALIGNMENT);
    }

    private void startGame() {
        deck.reset(); deck.shuffle();
        playerDeck.clear(); cpuDeck.clear();
        round = 0;
        for (int i = 0; i < 52; i++) {
            if (i % 2 == 0) playerDeck.add(deck.deal());
            else cpuDeck.add(deck.deal());
        }
        cpuCardDisplay.setFaceDown(true); cpuCardDisplay.repaint();
        playerCardDisplay.setFaceDown(true); playerCardDisplay.repaint();
        playerCountLabel.setText(playerDeck.size() + " cards");
        cpuCountLabel.setText(cpuDeck.size() + " cards");
        statusLabel.setText("Game started! Press DRAW.");
        drawBtn.setEnabled(true);
    }

    private void drawRound() {
        if (playerDeck.isEmpty() || cpuDeck.isEmpty()) { checkWinner(); return; }
        round++;
        Card pCard = playerDeck.remove(0);
        Card cCard = cpuDeck.remove(0);

        playerCardDisplay.setCard(pCard); playerCardDisplay.setFaceDown(false);
        cpuCardDisplay.setCard(cCard);    cpuCardDisplay.setFaceDown(false);
        playerCardDisplay.repaint(); cpuCardDisplay.repaint();

        int pv = pCard.getWarValue(), cv = cCard.getWarValue();
        if (pv > cv) {
            playerDeck.add(pCard); playerDeck.add(cCard);
            statusLabel.setText("Round " + round + ": You win! (+1 card) 🏅");
            statusLabel.setForeground(new Color(100, 220, 100));
        } else if (cv > pv) {
            cpuDeck.add(pCard); cpuDeck.add(cCard);
            statusLabel.setText("Round " + round + ": CPU wins! 😬");
            statusLabel.setForeground(new Color(220, 100, 100));
        } else {
            // WAR!
            statusLabel.setText("⚔ WAR! Both decks clash! ⚔");
            statusLabel.setForeground(Theme.ACCENT);
            List<Card> pot = new ArrayList<>();
            pot.add(pCard); pot.add(cCard);
            for (int i = 0; i < 3 && !playerDeck.isEmpty() && !cpuDeck.isEmpty(); i++) {
                pot.add(playerDeck.remove(0));
                pot.add(cpuDeck.remove(0));
            }
            // Win the war
            Card pw = playerDeck.isEmpty() ? null : playerDeck.remove(0);
            Card cw = cpuDeck.isEmpty()    ? null : cpuDeck.remove(0);
            if (pw == null || cw == null) {
                playerDeck.addAll(pot);
                statusLabel.setText("WAR: not enough cards, you take the pot!");
            } else {
                pot.add(pw); pot.add(cw);
                if (pw.getWarValue() >= cw.getWarValue()) playerDeck.addAll(pot);
                else cpuDeck.addAll(pot);
            }
        }

        playerCountLabel.setText(playerDeck.size() + " cards");
        cpuCountLabel.setText(cpuDeck.size() + " cards");

        if (playerDeck.isEmpty() || cpuDeck.isEmpty()) checkWinner();
    }

    private void checkWinner() {
        drawBtn.setEnabled(false);
        if (playerDeck.isEmpty() && cpuDeck.isEmpty()) {
            statusLabel.setText("Draw! No cards left.");
        } else if (playerDeck.isEmpty()) {
            statusLabel.setText("CPU WINS THE WAR! 😢 Start a new game!");
        } else {
            statusLabel.setText("YOU WIN THE WAR! 🏆 Congratulations!");
        }
        statusLabel.setForeground(Theme.ACCENT2);
    }
}