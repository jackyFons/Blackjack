package com.blackjack;

import java.util.ArrayList;
import java.util.List;

public class Player implements Settings {
    public enum PlayerType { USER, DEALER }


    private final PlayerType type;      // Player type (user or dealer)
    public List<Card> hand;             // Player's current hand
    private int score;                  // Hand's total score
    private int hiddenScore;            // Hand's hidden score (hides first card)
    private int numOfAces;              // Number of aces in hand
    private int money;                  // User's remaining money


    /*
     * Constructor
     *
     * @param playerType  used to specify the player.
     */
    public Player(PlayerType playerType) {
        this.type = playerType;
        this.hand = new ArrayList<>();
        this.score = 0;
        this.hiddenScore = 0;
        this.numOfAces = 0;
        this.money = STARTING_MONEY;
    }

    /*
     * Resets needed variables for new game.
     */
    public void resetHand() {
        hand.clear();
        score = 0;
        hiddenScore = 0;
        numOfAces = 0;
    }

    /*
     * Clears all variables.
     */
    public void reset() {
        resetHand();
        money = STARTING_MONEY;
    }

    /*
     * Adds card to hand and updates player's variables
     *
     * @param card   the card that will be added to player's hand
     */
    public void addCard(Card card) {
        // Add card to our hand
        hand.add(card);

        // Update variable numOfAces if our card is an Ace
        if (card.rank == Card.Rank.ACE) {
            numOfAces++;
        }
        // Update Scores
        updateScores(card.rank.value);

        // Modify score if it's over 21
        if (score > 21 && numOfAces > 0) {
            score -= 10;
            numOfAces--;
        }
    }

    /*
     * Method to change the player's score when they draw a card.
     *
     * @param value    the value of the card drawn
     */
    public void updateScores(int value) {
        score += value;
        hiddenScore = score - hand.get(0).rank.value;
    }

    /*
     * Updates the player's money after a bet is made.
     *
     * @param bet    the amount of money the player has bet.
     */
    public void makeBet(int bet) {
        money -= bet;
    }

    /*
     * Adds to the players total money after they won.
     *
     * @param toAdd    the amount of money the player has won.
     */
    public void updateMoney(int toAdd) {
        money += toAdd;
    }


    /*
     * Returns whether the player has a natural hand (2 cards totaling 21)
     */
    public boolean isNatural() {
        return hand.size() == 2 && score == BLACKJACK;
    }


    // ------- GETTERS ---------
    public int getMoney() {
        return money;
    }

    public PlayerType getType() {
        return type;
    }

    public int getScore() {
        return score;
    }

    public int getHiddenScore() {
        return hiddenScore;
    }


}
