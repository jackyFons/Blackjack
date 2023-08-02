package com.blackjack;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class StartController {
    @FXML
    Button startButton, exitButton;


    /*
     * Changes to the playing screen.
     */
    @FXML
    private void onStartClick() {
        Application.changeScene(Application.blackjackScene);
    }


    /*
     * Exits the game and closes the window.
     */
    @FXML
    private void onExitClick() {
        Application.exitStage();
    }
}
