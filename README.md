# ♠ ♥ Card Games Suite ♦ ♣

A Java Swing desktop application featuring **6 classic card games** played against a CPU opponent — all in one tabbed window, zero external dependencies.

<img width="883" height="684" alt="Image" src="https://github.com/user-attachments/assets/45b48ea4-4ec5-45cc-918f-318564a3c875" />

---

## 🎮 Games Included

| Tab | Game | Description |
|-----|------|-------------|
| ♠ Blackjack | Blackjack | Beat the dealer to 21. Hit, Stand, Double Down + betting system |
| 🃏 Poker | 5-Card Draw Poker | Click cards to discard, draw replacements. Full hand ranking + CPU AI |
| ⚔ War | War | Flip cards — highest card wins. Includes full WAR mechanic on ties |
| 👏 Snap | Snap | Match ranks fast! Press `SPACE` or click SNAP before the CPU does |
| 🐟 Go Fish | Go Fish | Ask the CPU for ranks and collect books of 4. Turn-based with log |
| 👴 Old Maid | Old Maid | Pick from CPU's face-down hand. Avoid being left with the lone Queen! |

---

## 📁 Project Structure

```
src/org/example/
├── Main.java                  ← App entry point + JTabbedPane window
├── core/
│   ├── Card.java              ← Card model (Suit, Rank, symbols)
│   └── Deck.java              ← 52-card deck with shuffle/deal
├── ui/
│   ├── CardPanel.java         ← Custom card renderer (Swing paintComponent)
│   └── Theme.java             ← Colors, fonts, styled button factory
└── games/
    ├── BlackjackPanel.java
    ├── PokerPanel.java
    ├── WarPanel.java
    ├── SnapPanel.java
    ├── GoFishPanel.java
    └── OldMaidPanel.java
```

---

## ⚙️ Requirements

- **Java JDK 21+** — the only requirement
- No Maven, no Gradle, no external `.jar` files
- Uses only `javax.swing`, `java.awt`, `java.util` (all built into JDK)

---

## 🚀 Getting Started

### Run the JAR (easiest)

```bash
java -jar CardGames.jar
```

### Build & Run from Source (IntelliJ)

1. Clone or extract the source zip
2. Open IntelliJ → **File → New Project from Existing Sources**
3. Select the `src` folder → **Mark Directory as → Sources Root**
4. Go to **File → Project Structure → SDK** → set to JDK 21
5. Open `Main.java` → click the green ▶ **Run** button

> **Package issue?** If your project uses `org.example`, press `Ctrl+Shift+R` and replace all `cardgames.` → `org.example.`

---

## 📖 How to Play

<details>
<summary>♠ Blackjack</summary>

- Place a bet → press **Deal** → choose **Hit** or **Stand**
- Goal: get closer to 21 than the dealer without going over
- **Double Down**: double your bet and take exactly one more card
- Blackjack (Ace + 10-value on deal) pays **2.5x** your bet

</details>

<details>
<summary>🃏 5-Card Draw Poker</summary>

- Place a bet → **Deal** → click cards to mark them for discard → **Draw**
- Press **Showdown** to reveal hands — best poker hand wins
- CPU uses basic AI: keeps pairs/trips, draws to flushes
- Rankings: Straight Flush > Four of a Kind > Full House > Flush > Straight > Three of a Kind > Two Pair > Pair > High Card

</details>

<details>
<summary>⚔ War</summary>

- Press **Draw** — both players flip a card, higher card wins both
- On a tie: **WAR!** Both put 3 cards face down, then flip one to decide
- First player to collect all 52 cards wins

</details>

<details>
<summary>👏 Snap</summary>

- Press **Draw Card** to flip cards onto the pile
- When two consecutive cards share the same rank — **SNAP!**
- Press the **SNAP** button or hit **SPACEBAR** before the CPU does
- Wrong snap = **-3 card penalty**. CPU reacts between 0.5–2 seconds

</details>

<details>
<summary>🐟 Go Fish</summary>

- Select a rank from the dropdown → **Ask CPU**
- If CPU has that rank, you get the cards. Otherwise: **GO FISH** (draw from deck)
- Collect all 4 of a rank = **Book**. Most books at the end wins

</details>

<details>
<summary>👴 Old Maid</summary>

- Pairs are removed from your hand automatically at the start
- Click a face-down CPU card → press **Pick Selected**
- If it matches a card in your hand, the pair is removed
- One Queen has no match — whoever holds it at the end **LOSES**

</details>

---

## 📦 Files

| File | Description |
|------|-------------|
| `CardGames.jar` | Runnable JAR — `java -jar CardGames.jar` |
| `CardGames-source.zip` | All 11 `.java` source files for IntelliJ |
| `README.md` | This file |

---

## 🛠 Tech Stack

- **Language**: Java 21
- **UI**: Java Swing + AWT
- **IDE**: IntelliJ IDEA
- **Dependencies**: None (pure JDK)

---

> Built as a CS coursework project. 6 games, 1 deck, 0 external libraries. 🃏
