package tro.dieng.morpion;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

import java.net.URL;
import java.util.ResourceBundle;

public class TicTacToeController implements Initializable {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label message;

    @FXML
    private Label firtPlayerCountLabel;

    @FXML
    private Label secondPlayerCountLabel;
    @FXML
    private Label emptyCountLabel;

    private GridPane grid;
    private final int BOARD_HEIGHT = TicTacToeModel.getBoardHeight();
    private final int BOARD_WIDTH = TicTacToeModel.getBoardHeight();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configure grids
        grid = new GridPane();
        grid.setPrefHeight(BOARD_HEIGHT);
        grid.setPrefWidth(BOARD_WIDTH);

        for (int i = 0; i < BOARD_HEIGHT; i++) {
            grid.getRowConstraints().add(new RowConstraints(70));
        }

        for (int j = 0; j < BOARD_WIDTH; j++) {
            grid.getColumnConstraints().add(new ColumnConstraints(70));
        }

        // Add Squares in the grid
        for (int i = 0; i < BOARD_HEIGHT; i++) {
            for (int j = 0; j < BOARD_WIDTH; j++) {
                grid.add(new TicTacToeSquare(i,j), j, i);
            }
        }
        grid.setGridLinesVisible(true);
        // Set the anchor constraints to center the content
        AnchorPane.setTopAnchor(grid, 30.0);
        AnchorPane.setBottomAnchor(grid, 50.0);
        AnchorPane.setLeftAnchor(grid, 110.0);
        AnchorPane.setRightAnchor(grid, 100.0);
        anchorPane.getChildren().add(grid);

        TicTacToeModel model = TicTacToeModel.getInstance();

        // Bind label properties to models properties
        message.textProperty().bind(model.getEndOfGameMessage());
        firtPlayerCountLabel.textProperty().bind(model.getFirstPlayerCount().asString("%d cases pour X "));
        secondPlayerCountLabel.textProperty().bind(model.getSecondPlayerCount().asString("%d cases pour O "));
        emptyCountLabel.textProperty().bind(model.getEmptyCount().asString("%d cases libres "));
    }

    public void restart(ActionEvent e) {
        anchorPane.getChildren().remove(grid);
        TicTacToeModel.getInstance().restart();
        this.initialize(null, null);
    }
}
