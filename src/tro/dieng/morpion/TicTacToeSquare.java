package tro.dieng.morpion;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

public class TicTacToeSquare extends TextField {
    private static final TicTacToeModel model = TicTacToeModel.getInstance();
    private final ObjectProperty<Owner> owner = new SimpleObjectProperty<>(Owner.NONE);
    private final BooleanProperty isWinningSquare = new SimpleBooleanProperty(false);

    public TicTacToeSquare(int row, int column) {
        initializeSquare();
        styleSquare();
        bindProperties(row, column);
        handleMouseInteractions(row, column);
    }

    private void initializeSquare() {
        setEditable(false);
        setPrefSize(70, 70);
        setAlignment(Pos.CENTER);
        setPadding(new Insets(-10, 0, -10, 0));
    }

    private void styleSquare() {
        setFont(Font.font("Arial", 20)); // Set initial font size
    }

    private void bindProperties(int row, int column) {
        owner.bind(model.turnProperty());
        model.getWinningSquare(row, column).addListener((observable, oldValue, newValue) -> {
            if (Boolean.TRUE.equals(newValue)) {
                this.setFont(Font.font("Arial", 50));
            }
        });
    }

    private void handleMouseInteractions(int row, int column) {
        setOnMouseClicked(event -> handleSquareClick(row, column));
        setOnMouseEntered(event -> handleMouseOver(row, column));
        setOnMouseExited(event -> setStyle("-fx-background-color: NONE"));
    }

    private void handleSquareClick(int row, int column) {
        if (model.legalMove(row, column).get()) {
            setText(owner.get() == Owner.FIRST ? "X" : "O");
            model.play(row, column);
        }
    }

    private void handleMouseOver(int row, int column) {
        setCursor(Cursor.HAND);
        setStyle(model.legalMove(row, column).get() ? "-fx-background-color: GREEN" : "-fx-background-color: RED");
    }

    public ObjectProperty<Owner> ownerProperty() {
        return owner;
    }
}
