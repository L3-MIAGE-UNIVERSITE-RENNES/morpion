package tro.dieng.morpion;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;

public class TicTacToeModel {
    private static final int BOARD_WIDTH =  3;
    private static final int BOARD_HEIGHT =  3;
    private static final int WINNING_COUNT =  3;
    private static final int TOTAL_SQUARES = BOARD_WIDTH  * BOARD_HEIGHT ;

    private final ObjectProperty<Owner> turn;
    private final ObjectProperty<Owner> winner;
    private final ObjectProperty<Owner>[][] board;
    private final BooleanProperty[][] winningBoard;

    private final SimpleIntegerProperty firstPlayerCount;
    private final SimpleIntegerProperty secondPlayerCount;
    private final SimpleIntegerProperty emptyCount;

    private final StringProperty endOfGameMessage;

    private int movesMade;

    private TicTacToeModel() {
        board = new ObjectProperty[BOARD_WIDTH][BOARD_HEIGHT];
        winningBoard = new BooleanProperty[BOARD_WIDTH][BOARD_HEIGHT];
        firstPlayerCount = new SimpleIntegerProperty(0);
        secondPlayerCount = new SimpleIntegerProperty(0);
        emptyCount = new SimpleIntegerProperty(TOTAL_SQUARES);
        endOfGameMessage = new SimpleStringProperty("");

        initializeBoard();
        turn = new SimpleObjectProperty<>(Owner.FIRST);
        winner = new SimpleObjectProperty<>(Owner.NONE);
        movesMade = 0;
    }

    private void initializeBoard() {
        for (int i = 0; i < BOARD_WIDTH; i++) {
            for (int j = 0; j < BOARD_HEIGHT; j++) {
                board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
                winningBoard[i][j] = new SimpleBooleanProperty(false);
            }
        }
    }

    public static TicTacToeModel getInstance() {
        return TicTacToeModelHolder.INSTANCE;
    }

    private static class TicTacToeModelHolder {
        private static final TicTacToeModel INSTANCE = new TicTacToeModel();
    }

    public void restart() {
        initializeBoard();
        turn.set(Owner.FIRST);
        winner.set(Owner.NONE);
        movesMade = 0;
        emptyCount.set(TOTAL_SQUARES);
        firstPlayerCount.set(0);
        secondPlayerCount.set(0);
        endOfGameMessage.set("");
    }

    public final ObjectProperty<Owner> turnProperty() {
        return turn;
    }

    public final ObjectProperty<Owner> getSquare(int row, int column) {
        return board[row][column];
    }

    public final BooleanProperty getWinningSquare(int row, int column) {
        return winningBoard[row][column];
    }

    public void setWinner(Owner winner) {
        this.winner.set(winner);
    }

    public BooleanProperty[][] getWinningBoard() {
        return winningBoard;
    }

    public boolean validSquare(int row, int column) {
        return row >= 0 && column >= 0 && row < BOARD_WIDTH && column < BOARD_HEIGHT;
    }

    public void nextPlayer() {
        turn.set(turn.get().opposite());
    }

    public void play(int row, int column) {
        if (legalMove(row, column).get()) {
            board[row][column].set(turn.get());
            updatePlayerCount();
            movesMade++;
            nextPlayer();
            if (movesMade >= 5) {
                setWinner(getWinner());
                getEndOfGameMessage();
                getWinningSquare(row, column);
            }
            emptyCount.set(emptyCount.get() - 1);
        }
    }

    private void updatePlayerCount() {
        if (turn.get() == Owner.SECOND) {
            secondPlayerCount.set(secondPlayerCount.get() + 1);
        } else if (turn.get() == Owner.FIRST) {
            firstPlayerCount.set(firstPlayerCount.get() + 1);
        }
    }

    public BooleanBinding legalMove(int row, int column) {
        return Bindings.createBooleanBinding(() -> validSquare(row, column) && !gameOver().get() && board[row][column].get().equals(Owner.NONE));
    }

    public final StringExpression getEndOfGameMessage() {
        if (winner.get().equals(Owner.FIRST)) {
            endOfGameMessage.set("Game Over. Le gagnant est le premier joueur.");
        } else if (winner.get().equals(Owner.SECOND)) {
            endOfGameMessage.set("Game Over. Le gagnant est le deuxième joueur.");
        } else if (winner.get().equals(Owner.NONE) && gameOver().get()) {
            endOfGameMessage.set("Match Nul");
        }
        return endOfGameMessage;
    }

    public BooleanBinding gameOver() {
        return Bindings.createBooleanBinding(() -> !winner.get().equals(Owner.NONE) || movesMade == TOTAL_SQUARES);
    }

    public static int getBoardHeight() {
        return BOARD_HEIGHT;
    }

    private Owner getWinner() {
        // Vérification des lignes
        for (int i = 0; i < BOARD_WIDTH; i++) {
            if (board[i][0].get() != Owner.NONE) {
                boolean isWinningLine = true;
                for (int j = 1; j < BOARD_HEIGHT; j++) {
                    if (board[i][j].get() != board[i][0].get()) {
                        isWinningLine = false;
                        break;
                    }
                }
                if (isWinningLine) {
                    winningBoard[i][0].set(true);
                    for (int j = 1; j < BOARD_HEIGHT; j++) {
                        winningBoard[i][j].set(true);
                    }
                    return board[i][0].get();
                }
            }
        }

        // Vérification des colonnes
        for (int j = 0; j < BOARD_HEIGHT; j++) {
            if (board[0][j].get() != Owner.NONE) {
                boolean isWinningColumn = true;
                for (int i = 1; i < BOARD_WIDTH; i++) {
                    if (board[i][j].get() != board[0][j].get()) {
                        isWinningColumn = false;
                        break;
                    }
                }
                if (isWinningColumn) {
                    winningBoard[0][j].set(true);
                    for (int i = 1; i < BOARD_WIDTH; i++) {
                        winningBoard[i][j].set(true);
                    }
                    return board[0][j].get();
                }
            }
        }

        // Vérification des diagonales
        if (board[0][0].get() != Owner.NONE) {
            boolean isWinningDiagonal1 = true;
            for (int i = 1; i < BOARD_WIDTH; i++) {
                if (board[i][i].get() != board[0][0].get()) {
                    isWinningDiagonal1 = false;
                    break;
                }
            }
            if (isWinningDiagonal1) {
                winningBoard[0][0].set(true);
                for (int i = 1; i < BOARD_WIDTH; i++) {
                    winningBoard[i][i].set(true);
                }
                return board[0][0].get();
            }
        }
        if (board[0][BOARD_WIDTH - 1].get() != Owner.NONE) {
            boolean isWinningDiagonal2 = true;
            for (int i = 1; i < BOARD_WIDTH; i++) {
                if (board[i][BOARD_WIDTH - 1 - i].get() != board[0][BOARD_WIDTH - 1].get()) {
                    isWinningDiagonal2 = false;
                    break;
                }
            }
            if (isWinningDiagonal2) {
                winningBoard[0][BOARD_WIDTH - 1].set(true);
                for (int i = 1; i < BOARD_WIDTH; i++) {
                    winningBoard[i][BOARD_WIDTH - 1 - i].set(true);
                }
                return board[0][BOARD_WIDTH - 1].get();
            }
        }

        // Pas de gagnant
        return Owner.NONE;
    }


    public IntegerProperty getFirstPlayerCount() {
        return firstPlayerCount;
    }

    public IntegerProperty getSecondPlayerCount() {
        return secondPlayerCount;
    }

    public IntegerProperty getEmptyCount() {
        return emptyCount;
    }
}
