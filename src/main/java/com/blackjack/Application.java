package com.blackjack;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class Application extends javafx.application.Application {
    public enum SceneLoader {
        START("start-view.fxml"),
        BLACKJACK("blackjack-view.fxml");

        final Scene scene;
        SceneLoader(String path) {
           this.scene = loadScenes(path);
        }

        /*
         * Loads FXML scenes and attaches CSS
         *
         * @param path   holds String path to the resource file.
         */
        private Scene loadScenes(String path){
            FXMLLoader fxmlLoader = new FXMLLoader(Application.class.getResource(path));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 960, 565);
            } catch (IOException e) {
                e.printStackTrace();
            }
            scene.getStylesheets().add(Application.class.getResource("Styles/styles.css").toExternalForm());
            return scene;
        }
    }

    private static Stage stage;
    protected static SceneLoader blackjackScene, startScene;

    @Override
    public void start(Stage st) {
        stage = st;
        stage.setResizable(false);
        stage.setTitle("Blackjack");

        // Pre-load scenes
        blackjackScene = SceneLoader.BLACKJACK;
        startScene = SceneLoader.START;

        // Show the starting screen
        changeScene(startScene);
        stage.show();
    }

    /*
     * Changes stage scene
     *
     * @param name  used to identify which scene to show.
     */
    public static void changeScene(SceneLoader name) {
        stage.setScene(name.scene);
    }

    /*
     * Exits program; used in StartController class.
     */
    public static void exitStage() {
        stage.close();
    }

    public static void main(String[] args) {
        launch();
    }
}