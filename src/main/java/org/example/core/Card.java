package org.example.core;

public class Card {
    public enum Suit { HEARTS, DIAMONDS, CLUBS, SPADES }
    public enum Rank {
        TWO(2,"2"), THREE(3,"3"), FOUR(4,"4"), FIVE(5,"5"), SIX(6,"6"),
        SEVEN(7,"7"), EIGHT(8,"8"), NINE(9,"9"), TEN(10,"10"),
        JACK(10,"J"), QUEEN(10,"Q"), KING(10,"K"), ACE(11,"A");

        public final int value;
        public final String symbol;
        Rank(int value, String symbol) { this.value = value; this.symbol = symbol; }
    }

    public final Suit suit;
    public final Rank rank;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
    }

    public String getSuitSymbol() {
        return switch (suit) {
            case HEARTS -> "♥";
            case DIAMONDS -> "♦";
            case CLUBS -> "♣";
            case SPADES -> "♠";
        };
    }

    public boolean isRed() {
        return suit == Suit.HEARTS || suit == Suit.DIAMONDS;
    }

    public int getWarValue() { return rank.ordinal(); }

    @Override
    public String toString() {
        return rank.symbol + getSuitSymbol();
    }
}