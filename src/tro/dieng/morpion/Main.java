package tro.dieng.morpion;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.scene.Scene;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
            Parent root = FXMLLoader.load(getClass().getResource("Main.fxml"));
            // Create a scene with the text node as content
            Scene scene = new Scene(root);

            // Set the stage title and scene
            stage.setTitle("TicTacToe");
            stage.setResizable(false);
            stage.setScene(scene);
            // Show the stage
            stage.show();

    }

}
