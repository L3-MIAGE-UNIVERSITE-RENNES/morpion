package tro.dieng.morpion;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.StringExpression;
import javafx.beans.property.*;

import java.util.concurrent.Callable;

public class TicTacToeModel {
    private static final int BOARD_WIDTH =  3;
    private static final int BOARD_HEIGHT =  3;
    private static final int WINNING_COUNT =  3;
    private static final int CASES_NUMBER =  BOARD_WIDTH *  BOARD_HEIGHT;

    private final ObjectProperty<Owner> turn;
    private final ObjectProperty<Owner> winner;
    private final ObjectProperty<Owner>[][] board;
    private final BooleanProperty[][] winningBoard;

    private final SimpleIntegerProperty firstPlayerCount;
    private final SimpleIntegerProperty secondPlayerCount;
    private final SimpleIntegerProperty emptyCount;

    private final StringProperty endOfGameMessage;

    private int count;

    private TicTacToeModel() {
        board = new ObjectProperty[BOARD_WIDTH][BOARD_HEIGHT];
        winningBoard = new BooleanProperty[BOARD_WIDTH][BOARD_HEIGHT];
        firstPlayerCount = new SimpleIntegerProperty(0);
        secondPlayerCount = new SimpleIntegerProperty(0);
        emptyCount = new SimpleIntegerProperty(CASES_NUMBER);
        endOfGameMessage = new SimpleStringProperty("");

        for (int i =  0; i < BOARD_WIDTH; i++) {
            for (int j =  0; j < BOARD_HEIGHT; j++) {
                board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
                winningBoard[i][j] = new SimpleBooleanProperty(false);
            }
        }

        turn = new SimpleObjectProperty<>(Owner.FIRST);
        winner = new SimpleObjectProperty<>(Owner.NONE);
        count = 0;
    }

    /**
     * @return la seule instance possible du jeu. */
    public static TicTacToeModel getInstance() {
        return TicTacToeModelHolder.INSTANCE;
    }

    /**
     * Classe interne selon le pattern singleton.
     */
    private static class TicTacToeModelHolder {
        private static final TicTacToeModel INSTANCE = new TicTacToeModel();
    }

    public void restart() {
        for (int i =  0; i < BOARD_WIDTH; i++) {
            for (int j =  0; j < BOARD_HEIGHT; j++) {
                board[i][j] = new SimpleObjectProperty<>(Owner.NONE);
                winningBoard[i][j] = new SimpleBooleanProperty(false);
            }
        }
        turn.set(Owner.FIRST);
        winner.set(Owner.NONE);
        count = 0;
        this.emptyCount.set(CASES_NUMBER);
        this.firstPlayerCount.set(0);
        this.secondPlayerCount.set(0);
        this.endOfGameMessage.set("");
    }

    public final ObjectProperty<Owner> turnProperty() {
        return turn;
    }

    public final ObjectProperty<Owner> getSquare(int row, int column){
        return board[row][column];
    }

    public final BooleanProperty getWinningSquare(int row, int column){
        return winningBoard[row][column];
    }

    public void setWinner(Owner winner) {
       this.winner.set(winner);
    }

    public boolean validSquare(int row, int column) {
        return row >= 0 && column >= 0 && row<3 && column < 3;
    }

    public void nextPlayer() {
        Owner next = turn.get().opposite();
        turn.set(next);
    }

    /**
     * Jouer dans la case (row, column) quand c’est possible.
     */
    public void play(int row, int column) {
        if(legalMove(row, column).get()) {
            board[row][column].set(turn.get());
            if(turn.get() == Owner.SECOND){
                this.secondPlayerCount.set(secondPlayerCount.get() + 1);
            } else if(turn.get() == Owner.FIRST){
                this.firstPlayerCount.set(firstPlayerCount.get() + 1);
            }
            count++;
            nextPlayer();
            if(count >= 5) {
                setWinner(this.getWinner());
                getEndOfGameMessage();
            }
            this.emptyCount.set(emptyCount.get() - 1);
        }
    }

    /**
     * @return true s’il est possible de jouer dans la case
     * c’est-à-dire la case est libre et le jeu n’est pas terminé */
    public BooleanBinding legalMove(int row, int column) {
        boolean isLegalMove = validSquare(row, column) && !gameOver().get() && board[row][column].get().equals(Owner.NONE);
        return Bindings.createBooleanBinding((Callable<Boolean>) () -> isLegalMove);
    }

    /**
     * Cette fonction ne doit donner le bon résultat que si le jeu
     * est terminé. L’affichage peut être caché avant la fin du jeu.
     *
     * @return résultat du jeu sous forme de texte
     */
    public final StringExpression getEndOfGameMessage() {
        if (winner.get().equals(Owner.FIRST)){
            endOfGameMessage.set("Game Over. Le gagnant est le premier joueur.");
        } else if (winner.get().equals(Owner.SECOND)){
            endOfGameMessage.set("Game Over. Le gagnant est le deuxième joueur.");
        } else if (winner.get().equals(Owner.NONE) && gameOver().get()){
            endOfGameMessage.set("Match Nul");
        }
        return endOfGameMessage;
    }

    /**
     * @return true si le jeu est terminé
     * (soit un joueur a gagné, soit il n’y a plus de cases à jouer)
     */
    public BooleanBinding gameOver() {
        boolean isGameOver = !this.winner.get().equals(Owner.NONE) || count == BOARD_WIDTH * BOARD_HEIGHT;
        return Bindings.createBooleanBinding( () -> isGameOver );
    }

    public static int getBoardHeight() {
        return BOARD_HEIGHT;
    }

    public static int getBoardWidth(){
        return BOARD_WIDTH;
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
