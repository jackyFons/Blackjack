package com.blackjack;

import static com.blackjack.BlackjackController.GameResult.*;
import static com.blackjack.Settings.*;
import static com.blackjack.Player.PlayerType.*;

import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.util.Arrays;
import java.util.List;

public class BlackjackController {

    public enum GameResult { WIN, LOSE, TIE, BLACKJACK, BUST }

    private Deck deck;
    private Player user, dealer;
    private int betTotal;

    @FXML
    private Label dealerScoreLbl, userScoreLbl, userMoneyLbl, betLbl;

    @FXML
    private HBox dealersHand, usersHand;

    @FXML
    private GridPane tablePane;

    @FXML
    private VBox betPane;

    @FXML
    private Button hitButton, standButton, doubleDownButton;

    @FXML
    public Button button_5, button_25, button_50, button_100, button_500, betButton, allInButton, clearButton;


    /*
     * Sets up the game on start-up by initializing variables.
     */
    @FXML
    public void initialize() {
        user = new Player(USER);
        dealer = new Player(DEALER);
        deck = new Deck();

        betTotal = 0;

        betLbl.setText("Your Money: " + user.getMoney() +  " Current Bet: " + betTotal);
    }


    /*
     * Handles the betting before each round.
     */
    public void onTokenButtonClick(ActionEvent event) {
        Button button = (Button) event.getSource();
        String txt = button.getText();

        switch (txt) {
            case "Clear":
                betTotal = 0;
                break;
            case "All In":
                betTotal = user.getMoney();
                user.makeBet(betTotal);
                changeView(tablePane);
                return;
            case "Bet":
                if (betTotal > 0) {
                    user.makeBet(betTotal);
                    changeView(tablePane);
                    return;
                }
                break;
            default:
                int bet = Integer.parseInt(txt);
                if (betTotal + bet <= user.getMoney()) {
                    betTotal += bet;
                }
                break;
        }
        betLbl.setText("Your Money: " + user.getMoney() +  " Current Bet: " + betTotal);
    }


    /*
     * User chooses to "Stand" and it becomes the dealer's turn to draw cards or reveal their hand.
     */
    @FXML
    protected void onStandButtonClick() {
        changeButtonVisibility(Arrays.asList(hitButton, doubleDownButton, standButton), false);
        dealersTurn(true);
    }


    /*
     * Player chooses to draw another card.
     */
    @FXML
    protected void onHitButtonClick() {
        if (doubleDownButton.visibleProperty().get()) {
            doubleDownButton.setVisible(false);
        }
        Card card = deck.drawCard();
        SequentialTransition transition = new SequentialTransition();
        drawCard(card, transition, user, usersHand, userScoreLbl);
        transition.setOnFinished(event -> checkScore());
        transition.play();
    }


    /*
     * Doubling down: The user's bet is doubled, and they get one more card. The user can no longer hit.
     */
    @FXML
    protected void onDoubleDownClick() {
        if (user.getMoney() > betTotal) {
            user.makeBet(betTotal);
            betTotal *= 2;
            userMoneyLbl.setText("Money: " + user.getMoney());
        }
        // Draw a card using the onHit function
        onHitButtonClick();
        changeButtonVisibility(Arrays.asList(hitButton, doubleDownButton, standButton), false);
        dealersTurn(true);
    }


    /*
     * Gives each player two cards to start off the round.
     */
    private void dealCards() {
        SequentialTransition transition = new SequentialTransition();

        // Alternate who gets a card
        drawCard(deck.drawCard(), transition, user, usersHand, userScoreLbl);
        // The dealer's first card will be hidden.
        Card hiddenCard = deck.drawCard();
        hiddenCard.makeHidden();
        drawCard(hiddenCard, transition, dealer, dealersHand, dealerScoreLbl);
        drawCard(deck.drawCard(), transition, user, usersHand, userScoreLbl);
        drawCard(deck.drawCard(), transition, dealer, dealersHand, dealerScoreLbl);

        // After cards have been dealt, the hit & stand buttons become available and the user's score is checked.
        transition.setOnFinished(event -> {
            changeButtonVisibility(Arrays.asList(hitButton, standButton), true);
            if (user.canDoubleDown()) {
                doubleDownButton.setVisible(true);
            }
            checkScore();
        });
        transition.play();
    }


    /*
     *  Creates the animation for drawing a card.
     */
    private void drawCard(Card card, SequentialTransition transition, Player player, HBox handHBox, Label scoreLbl) {
        // Adds card to player's hand
        player.addCard(card);

        // Fade transition to simulate card being dealt.
        FadeTransition ft = new FadeTransition(Duration.millis(200), card.getImage());
        ft.setFromValue(0);
        ft.setToValue(1.0);

        // Updates player score after the card has been handed.
        int score = (player.getType() == DEALER) ? player.getHiddenScore() : player.getScore();
        ft.setOnFinished(event -> scoreLbl.setText("Score: " + score));

        // Adds fade transition and a pause between fades to the animation.
        transition.getChildren().addAll(ft,  new PauseTransition(Duration.millis(300)));

        // Updates visuals
        handHBox.getChildren().add(card.getImage());
    }


    /*
     * Method used to stop the user's turn if their score hits 21 or above.
     * -- If the user hits 21, have the dealer draw cards next, and if the user goes over 21, reveal the dealer's hand.
     */
    private void checkScore() {
        if (user.getScore() == BLACKJACK_TARGET) {
            changeButtonVisibility(Arrays.asList(hitButton, doubleDownButton, standButton), false);
            dealersTurn(true);
        }
        else if (user.getScore() > BLACKJACK_TARGET) {
            changeButtonVisibility(Arrays.asList(hitButton, doubleDownButton, standButton), false);
            dealersTurn(false);
        }
    }


    /*
     * When it's the dealer's turn, the dealer will draw cards until it reaches a minimum score of 17, then
     * it's hidden card and actual score will be revealed.
     */
    private void dealersTurn(Boolean dealerDraw) {
        SequentialTransition transition = new SequentialTransition();
        transition.getChildren().add(new PauseTransition(Duration.millis(400)));

        // Draw cards until the dealer's score totals at least 17.
        while (dealerDraw && dealer.getScore() < DEALER_TARGET) {
            Card card = deck.drawCard();
            drawCard(card, transition, dealer, dealersHand, dealerScoreLbl);
        }

        // Show hidden card and score
        revealDealersCard(transition);

        // End game when animation is done
        transition.setOnFinished(event -> endGame());
        transition.play();
    }


    /*
     * Handles card flipping animation used to reveal the dealer's hidden card and actual score.
     */
    private void revealDealersCard(SequentialTransition transition) {
        // Makes card visible
        dealer.hand.get(0).makeVisible();
        ImageView card = (ImageView) dealersHand.getChildren().get(0);

        // Rotations for flipping
        RotateTransition firstRotation = getRotationTransition(card, 0, 90);
        RotateTransition secondRotation = getRotationTransition(card, 90, 180);

        // Pause to switch images and update score
        PauseTransition firstPause = new PauseTransition(Duration.millis(200));
        firstPause.setOnFinished(event -> {
            ImageView cardView = dealer.hand.get(0).getImage();
            card.setImage(cardView.getImage());
            card.setScaleY(-1);
            dealerScoreLbl.setText("Score: " + dealer.getScore());
        });

        // Second pause to allow the player time to view cards and scores.
        PauseTransition secondPause = new PauseTransition(Duration.millis(2000));

        transition.getChildren().addAll(firstRotation, firstPause, secondRotation, secondPause);
    }


    /*
     * Returns a rotation animation on the card's y-axis.
     */
    private RotateTransition getRotationTransition(ImageView card, int startAngle, int endAngle) {
        RotateTransition rt = new RotateTransition(Duration.millis(400), card);

        rt.setAxis(Rotate.Y_AXIS);
        rt.setFromAngle(startAngle);
        rt.setToAngle(endAngle);

        rt.setInterpolator(Interpolator.LINEAR);
        rt.setCycleCount(1);

        return rt;

    }


    /*
     * Creates a pop-up depending on the outcome of the game. If the user has no money left, they will exit the game
     * and be given the start-up screen.
     */
    private void endGame() {
        SequentialTransition st = new SequentialTransition();
        GameResult result = getGameResult();
        if (result == LOSE) {
            // Player lost and has no money to bet
            if (user.getMoney() == 0) {
                announcementPopup("Game over!", Duration.millis(2000), st);
                st.setOnFinished(event -> resetGame());
                st.play();
                return;
            }
            // Player lost
            announcementPopup("You lost!", Duration.millis(3000), st);
        }
        // Player won
        else if (result == WIN) {
            user.updateMoney(betTotal*2);
            announcementPopup("You won!", Duration.millis(3000), st);
        }
        // Bust - user does not get their money back
        else if (result == BUST) {
            announcementPopup("Bust!", Duration.millis(3000), st);
        }
        else if (result == BLACKJACK) {
            user.updateMoney(betTotal*3);
            announcementPopup("Blackjack!", Duration.millis(3000), st);
        }
        else {
            // Tie
            user.updateMoney(betTotal);
            announcementPopup("Tie!", Duration.millis(3000), st);
        }
        st.play();
        resetRound();
        // Allows the user to continue playing
        changeView(betPane);
    }


    /*
     * Creates a new stage as a pop-up.
     *
     * @param dialog     prompt to be shown within the pop-up.
     * @param duration   amount of time (in milliseconds) the pop-up is shown.
     * @param st         animation used for showing and closing the pop-up.
     */
    private void announcementPopup(String dialog, Duration duration, SequentialTransition st) {
        // Creates the pop-up
        Stage stage = new Stage();
        stage.initStyle(StageStyle.UNDECORATED);

        // VBox to add text in its center
        VBox vBox = new VBox(400);
        vBox.setAlignment(Pos.CENTER);
        vBox.getChildren().add(new Text(dialog));

        // Adds CSS to the scene
        Scene scene = new Scene(vBox, 400, 300);
        scene.getStylesheets().add(getClass().getResource("Styles/styles.css").toExternalForm());
        vBox.getStyleClass().add("dialog");

        // Shows scene
        stage.setScene(scene);
        stage.show();

        // Closes pop-up after 'duration' milliseconds.
        PauseTransition pt = new PauseTransition(duration);
        pt.setOnFinished(actionEvent -> stage.close());

        st.getChildren().add(pt);
    }


    /*
     * Gets enum of game's result.
     */
    private GameResult getGameResult() {
        int dealerScore = dealer.getScore();
        int userScore = user.getScore();

        // User goes over 21.
        if (userScore > BLACKJACK_TARGET) {
            return BUST;
        }
        else if (dealerScore == userScore) {
            // Dealer has a natural and user doesn't.
            if (dealer.isNatural() && !user.isNatural()){
                return LOSE;
            }
            // User has a natural and dealer doesn't.
            else if (!dealer.isNatural() && user.isNatural()) {
                return BLACKJACK;
            }
            // Both players have the same score.
            return TIE;
        }
        // User wins when dealer goes over 21 or when the user's score is greater than the dealer's
        else if (dealerScore > BLACKJACK_TARGET || userScore > dealerScore) {
            return WIN;
        }
        // User loses when their score is less than the dealer's.
        else {
            return LOSE;
        }
    }


    /*
     * Resets round
     */
    private void resetRound() {
        if (deck.deckSize() < TOTAL_CARDS / 2) {
            deck.reset();
        }

        user.resetHand();
        dealer.resetHand();

        resetVisuals();
    }


    /*
     * Resets the whole game.
     */
    private void resetGame() {
        deck.reset();
        user.reset();
        dealer.reset();

        changeView(betPane);
        resetVisuals();

        startScreenView();
    }


    /*
     * Allows for switching between betting screen and playing screen.
     */
    private void changeView(Node child) {
        child.toFront();
        if (child == tablePane) {
            resetVisuals();
            dealCards();
        }
        else {
            betTotal = 0;
            betLbl.setText("Your Money: " + user.getMoney() + " Current Bet: " + betTotal);
        }
    }


    /*
     * Used to change the visibility of buttons depending on whose turn it is.
     */
    private void changeButtonVisibility(List<Button> buttons, Boolean bool) {
        for (Button button : buttons) {
            button.setVisible(bool);
        }
    }


    /*
     * Clears shown cards and resets labels.
     */
    private void resetVisuals() {
        usersHand.getChildren().clear();
        dealersHand.getChildren().clear();

        userMoneyLbl.setText("Money: " + user.getMoney());
        userScoreLbl.setText("Score: 0");
        dealerScoreLbl.setText("Score: 0");
    }


    /*
     * Used to switch scenes.
     */
    public void startScreenView() {
        Application.changeScene(Application.startScene);
    }
}

