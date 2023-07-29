package com.blackjack;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Card {
    /*
     * enum for suit of a card. This will be used in the Card object.
     *
     * @param path  used for retrieving image path.
     */
    public enum Suit {
        SPADE("s"),
        HEART("h"),
        CLUBS("c"),
        DIAMOND("d");

        final String path;
        Suit(String path) {
            this.path = path;
        }
    }

    /*
     * enum for card rank. This will be used in the Card object.
     *
     * @param path   used for retrieving image path.
     * @param value  the rank's value in a game of Blackjack.
     */
    public enum Rank {
        ACE("01", 11),
        TWO("02", 2),
        THREE("03", 3),
        FOUR("04", 4),
        FIVE("05", 5),
        SIX("06", 6),
        SEVEN("07", 7),
        EIGHT("08", 8),
        NINE("09", 9),
        TEN("10", 10),
        JACK("11", 10),
        QUEEN("12", 10),
        KING("13", 10);

        final String path;
        final int value;
        Rank(String path, int value) {
            this.path = path;
            this.value = value;
        }
    }

    public final Suit suit;                 // Suit of card
    public final Rank rank;                 // Rank of card
    private final ImageView frontImage;     // Front view of card
    private final ImageView backImage;      // Back view of the card
    private ImageView cardImage;            // Used to specify which side of the card is visible


    /*
     * Constructor
     *
     * Loads card's images based on its suit and rank and sets the front view as the visible image.
     */
    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;

        // Path to card's image.
        String cardImagePath = "Cards/" + suit.path + rank.path + ".png";
        this.frontImage = new ImageView(new Image(
                this.getClass().getResource(cardImagePath).toExternalForm(), 77, 107 , true, true));

        // Path to the back of card's image.
        String backImagePath = "Backs/Card-Back-06.png";
        this.backImage = new ImageView(new Image(
                this.getClass().getResource(backImagePath).toExternalForm(), 77, 107, true, true));

        this.cardImage = frontImage;

    }


    // Methods to change card image
    public void makeHidden() {
        this.cardImage = backImage;
    }

    public void makeVisible() {
        this.cardImage = frontImage;
    }


    // ------- GETTER --------
    public ImageView getImage() {
        return cardImage;
    }


    // ------- toString -------
    @Override
    public String toString() {
        return rank.toString() + " of " + suit.toString();
    }

}
