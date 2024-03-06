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
    private final ObjectProperty<Owner> ownerProperty = new SimpleObjectProperty<>(Owner.NONE);
    private final BooleanProperty winnerProperty = new SimpleBooleanProperty(false);
    public TicTacToeSquare(final int row, final int column) {
        this.setEditable(false);
        this.setPrefWidth(70);
        this.setPrefHeight(70);
        this.setAlignment(Pos.CENTER);
        this.setPadding(new Insets(-10,0,-10,0));

        this.setOnMouseClicked(e ->{
            boolean isLegalMove = model.legalMove(row, column).get();
            if (isLegalMove) {
                if (ownerProperty().get() == Owner.FIRST) {
                    this.setText("X");
                } else {
                    this.setText("O");
                }
                model.play(row, column);
            }
        });

        this.setOnMouseEntered(e ->{
            this.setCursor(Cursor.HAND);
            boolean isLegalMove = model.legalMove(row, column).get();
            if (isLegalMove){
                this.setStyle("-fx-background-color: GREEN");
            } else {
                this.setStyle("-fx-background-color: RED");
            }
        });

        this.setOnMouseExited(e -> {
            this.setStyle("-fx-background-color: NONE");
            if(winnerProperty.get()){
                this.setFont(Font.font("Arial", 50));
            }
        });

        this.ownerProperty.bind(model.turnProperty());
        this.winnerProperty.bind(model.getWinningSquare(row, column));
    }

    public ObjectProperty<Owner> ownerProperty()
    {
        return this.ownerProperty;
    }
}
