package org.example.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private final List<Card> cards = new ArrayList<>();

    public Deck() { reset(); }

    public void reset() {
        cards.clear();
        for (Card.Suit suit : Card.Suit.values())
            for (Card.Rank rank : Card.Rank.values())
                cards.add(new Card(suit, rank));
    }

    public void shuffle() { Collections.shuffle(cards); }

    public Card deal() {
        if (cards.isEmpty()) return null;
        return cards.remove(0);
    }

    public int size() { return cards.size(); }
    public boolean isEmpty() { return cards.isEmpty(); }
    public List<Card> getCards() { return cards; }
}