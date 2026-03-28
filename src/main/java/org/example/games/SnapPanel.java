package org.example.games;

import org.example.core.*;
import org.example.ui.*;
import org.example.core.Card;
import org.example.core.Deck;
import org.example.ui.CardPanel;
import org.example.ui.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class SnapPanel extends JPanel {
    private Deck deck;
    private List<Card> playerDeck, cpuDeck, pile;
    private CardPanel topCardPanel, prevCardPanel;
    private JLabel playerCountLabel, cpuCountLabel, statusLabel, pileCountLabel;
    private JButton snapBtn, drawBtn, newGameBtn;
    private Timer cpuTimer;
    private int cpuScore = 0, playerScore = 0;

    public SnapPanel() {
        setBackground(Theme.BG_TABLE);
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        deck = new Deck();
        playerDeck = new ArrayList<>();
        cpuDeck    = new ArrayList<>();
        pile       = new ArrayList<>();
        initUI();
        setupCpuTimer();
        startGame();
    }

    private void initUI() {
        JLabel title = new JLabel("👏 SNAP! 👏", SwingConstants.CENTER);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.ACCENT);

        // Card display area
        JPanel pilePanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        pilePanel.setOpaque(false);
        prevCardPanel = new CardPanel(null, true);
        topCardPanel  = new CardPanel(null, true);
        pilePanel.add(prevCardPanel);
        pilePanel.add(topCardPanel);

        pileCountLabel = new JLabel("Pile: 0 cards", SwingConstants.CENTER);
        pileCountLabel.setFont(Theme.FONT_BODY);
        pileCountLabel.setForeground(Theme.TEXT_DIM);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(pilePanel, BorderLayout.CENTER);
        centerPanel.add(pileCountLabel, BorderLayout.SOUTH);

        // Status
        statusLabel = new JLabel("Press DRAW to start!", SwingConstants.CENTER);
        statusLabel.setFont(Theme.FONT_STATUS);
        statusLabel.setForeground(Theme.TEXT_LIGHT);

        // Counts
        JPanel countPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        countPanel.setOpaque(false);

        JPanel cpuInfo = new JPanel(new BorderLayout());
        cpuInfo.setOpaque(false);
        JLabel cpuLbl = new JLabel("CPU", SwingConstants.CENTER);
        cpuLbl.setFont(Theme.FONT_SUB); cpuLbl.setForeground(Theme.TEXT_DIM);
        cpuCountLabel = new JLabel("26 cards", SwingConstants.CENTER);
        cpuCountLabel.setFont(Theme.FONT_BODY); cpuCountLabel.setForeground(new Color(200,80,80));
        cpuInfo.add(cpuLbl, BorderLayout.NORTH);
        cpuInfo.add(cpuCountLabel, BorderLayout.CENTER);

        JPanel youInfo = new JPanel(new BorderLayout());
        youInfo.setOpaque(false);
        JLabel youLbl = new JLabel("You", SwingConstants.CENTER);
        youLbl.setFont(Theme.FONT_SUB); youLbl.setForeground(Theme.TEXT_DIM);
        playerCountLabel = new JLabel("26 cards", SwingConstants.CENTER);
        playerCountLabel.setFont(Theme.FONT_BODY); playerCountLabel.setForeground(new Color(80,200,80));
        youInfo.add(youLbl, BorderLayout.NORTH);
        youInfo.add(playerCountLabel, BorderLayout.CENTER);

        countPanel.add(cpuInfo);
        countPanel.add(youInfo);

        // Buttons
        drawBtn   = Theme.makeButton("Draw Card");
        snapBtn   = Theme.makeButton("SNAP! 👏", Theme.BTN_RED, Theme.BTN_RED_HOVER);
        newGameBtn = Theme.makeButton("New Game");
        snapBtn.setPreferredSize(new Dimension(160, 42));

        drawBtn.addActionListener(e -> drawCard());
        snapBtn.addActionListener(e -> playerSnap());
        newGameBtn.addActionListener(e -> startGame());

        // Keyboard shortcut: SPACE for snap
        InputMap im = getInputMap(WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = getActionMap();
        im.put(KeyStroke.getKeyStroke("SPACE"), "snap");
        am.put("snap", new AbstractAction() {
            public void actionPerformed(ActionEvent e) { playerSnap(); }
        });

        JPanel btnPanel = new JPanel(new FlowLayout());
        btnPanel.setOpaque(false);
        btnPanel.add(drawBtn);
        btnPanel.add(snapBtn);
        btnPanel.add(newGameBtn);

        JLabel hint = new JLabel("[SPACE] to SNAP!", SwingConstants.CENTER);
        hint.setFont(new Font("Tahoma", Font.ITALIC, 11));
        hint.setForeground(Theme.TEXT_DIM);

        JPanel southPanel = new JPanel(new BorderLayout(5, 5));
        southPanel.setOpaque(false);
        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(countPanel, BorderLayout.CENTER);
        southPanel.add(btnPanel, BorderLayout.SOUTH);

        JPanel northPanel = new JPanel(new BorderLayout());
        northPanel.setOpaque(false);
        northPanel.add(title, BorderLayout.NORTH);
        northPanel.add(hint, BorderLayout.SOUTH);

        add(northPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void setupCpuTimer() {
        cpuTimer = new Timer(0, e -> {
            if (cpuTimer.isRunning()) cpuTimer.stop();
            cpuSnap();
        });
        cpuTimer.setRepeats(false);
    }

    private void startGame() {
        if (cpuTimer.isRunning()) cpuTimer.stop();
        deck.reset(); deck.shuffle();
        playerDeck.clear(); cpuDeck.clear(); pile.clear();
        playerScore = 0; cpuScore = 0;

        for (int i = 0; i < 52; i++) {
            if (i % 2 == 0) playerDeck.add(deck.deal());
            else cpuDeck.add(deck.deal());
        }

        prevCardPanel.setCard(null); prevCardPanel.setFaceDown(true);
        topCardPanel.setCard(null);  topCardPanel.setFaceDown(true);
        prevCardPanel.repaint(); topCardPanel.repaint();

        statusLabel.setText("Game started! Press DRAW.");
        statusLabel.setForeground(Theme.TEXT_LIGHT);
        updateCounts();
        drawBtn.setEnabled(true);
        snapBtn.setEnabled(true);
    }

    private void drawCard() {
        List<Card> drawFrom = (Math.random() < 0.5 && !playerDeck.isEmpty()) ? playerDeck : cpuDeck;
        if (drawFrom.isEmpty()) drawFrom = playerDeck.isEmpty() ? cpuDeck : playerDeck;
        if (drawFrom.isEmpty()) { checkWinner(); return; }

        Card prev = pile.isEmpty() ? null : pile.get(pile.size()-1);
        Card drawn = drawFrom.remove(0);
        pile.add(drawn);

        prevCardPanel.setCard(prev);   prevCardPanel.setFaceDown(prev == null);
        topCardPanel.setCard(drawn);   topCardPanel.setFaceDown(false);
        prevCardPanel.repaint(); topCardPanel.repaint();
        pileCountLabel.setText("Pile: " + pile.size() + " cards");
        updateCounts();

        // Check if snap is possible
        if (isSnap()) {
            statusLabel.setText("⚡ SNAP opportunity! Quick — click SNAP or SPACE!");
            statusLabel.setForeground(Theme.ACCENT);

            // CPU reacts with some delay (0.5s - 2s)
            int delay = (int)(500 + Math.random() * 1500);
            cpuTimer.setInitialDelay(delay);
            cpuTimer.restart();
        } else {
            statusLabel.setText("Card drawn. Keep going!");
            statusLabel.setForeground(Theme.TEXT_LIGHT);
        }
    }

    private boolean isSnap() {
        if (pile.size() < 2) return false;
        Card top  = pile.get(pile.size()-1);
        Card prev = pile.get(pile.size()-2);
        return top.rank == prev.rank;
    }

    private void playerSnap() {
        if (cpuTimer.isRunning()) cpuTimer.stop();
        if (isSnap()) {
            playerDeck.addAll(pile);
            pile.clear();
            playerScore++;
            statusLabel.setText("🏅 You SNAPped! +" + pile.size() + " cards!");
            statusLabel.setForeground(new Color(100, 220, 100));
        } else if (!pile.isEmpty()) {
            // Penalty: give 3 cards to cpu
            for (int i = 0; i < 3 && !playerDeck.isEmpty(); i++)
                cpuDeck.add(playerDeck.remove(0));
            statusLabel.setText("Wrong SNAP! -3 card penalty! 😬");
            statusLabel.setForeground(new Color(220, 100, 100));
        }
        resetPileDisplay();
        updateCounts();
        checkWinner();
    }

    private void cpuSnap() {
        if (isSnap()) {
            cpuDeck.addAll(pile);
            pile.clear();
            cpuScore++;
            statusLabel.setText("CPU SNAPped first! 😅 +" + pile.size() + " cards to CPU.");
            statusLabel.setForeground(new Color(220, 100, 100));
            resetPileDisplay();
            updateCounts();
            checkWinner();
        }
    }

    private void resetPileDisplay() {
        prevCardPanel.setCard(null); prevCardPanel.setFaceDown(true);
        topCardPanel.setCard(null);  topCardPanel.setFaceDown(true);
        prevCardPanel.repaint(); topCardPanel.repaint();
        pileCountLabel.setText("Pile: 0 cards");
    }

    private void updateCounts() {
        playerCountLabel.setText(playerDeck.size() + " cards");
        cpuCountLabel.setText(cpuDeck.size() + " cards");
    }

    private void checkWinner() {
        if (playerDeck.isEmpty() && pile.isEmpty()) {
            statusLabel.setText("CPU WINS! You ran out of cards. 😢");
            drawBtn.setEnabled(false); snapBtn.setEnabled(false);
        } else if (cpuDeck.isEmpty() && pile.isEmpty()) {
            statusLabel.setText("YOU WIN! CPU ran out of cards! 🏆");
            drawBtn.setEnabled(false); snapBtn.setEnabled(false);
        }
    }
}