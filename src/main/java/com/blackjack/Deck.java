package com.blackjack;

import com.blackjack.Card.*;

import java.util.*;

public class Deck implements Settings {
    // Holds all unused cards
    private List<Card> deck;
    // Holds all used cards
    private final List<Card> usedCards;

    /*
     * Constructor
     * Adds cards into a list. NUM_DECKS denotes how many decks of 52 cards will be in the list.
     */
    public Deck() {
        this.deck = new ArrayList<>();
        this.usedCards = new ArrayList<>();

        for (int i= 0; i < NUM_DECKS; i++) {
            for (Suit suit : Suit.values()) {
                for (Rank rank : Rank.values()) {
                    deck.add(new Card(suit, rank));
                }
            }
        }
        shuffle(50);
    }


    /*
     * Shuffles all cards in the deck using the riffle shuffle.
     *
     * @param numOfShuffles  amount of times to use the riffle shuffle on the deck.
     */
    private void shuffle(int numOfShuffles) {
        Random random = new Random();

        for (int i = 0; i < numOfShuffles; i++) {
            // Splits the deck in half.
            List<Card> first = deck.subList(0, deckSize()/2);
            List<Card> second = deck.subList(deckSize()/2, deckSize());
            deck = riffle(random, first, second);
        }
    }

    /*
     * Simulates the riffle shuffle.
     *
     * @param random    used to get a random amount of cards from the top of each deck half.
     * @param first     first deck half.
     * @param second    second deck half.
     */
    private List<Card> riffle(Random random, List<Card> first, List<Card> second) {
        List<Card> newDeck = new ArrayList<>();

        // Alternates adding cards from each half to the deck
        for (int i = 0; i < first.size(); i++) {
            newDeck.add(first.get(i));
            newDeck.add(second.get(i));
        }

        // Splits the deck into two random-sized halves and swaps their position.
        int rand = random.nextInt((deckSize()/2+10)-(deckSize()/2-10)) + (deckSize()/2-10);
        List<Card> subDeck = newDeck.subList(rand, newDeck.size());
        subDeck.addAll(newDeck.subList(0, rand));

        return subDeck;
    }


    /*
     * Will retrieve and remove a card from the deck, and will be added to used cards list.
     */
    public Card drawCard() {
        Card drawn = deck.remove(0);
        usedCards.add(drawn);
        return drawn;
    }


    /*
     * Re-adds all cards to the deck and shuffles it; clears used cards list.
     */
    public void reset() {
        deck.addAll(usedCards);
        shuffle(20);
        usedCards.clear();
    }


    // ------- GETTER -------
    public int deckSize() {
        return deck.size();
    }


    // ------- toString ---------
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (Card card : deck) {
            builder.append(card.toString()).append("\n");
        }
        return builder.toString();
    }


}
