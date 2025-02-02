package hu.simontamas.scrabble.service;

import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.model.Board;
import hu.simontamas.scrabble.service.wordService.SimpleWordService;
import hu.simontamas.scrabble.service.wordService.WordsService;
import hu.simontamas.scrabble.threads.ValidateBoardTask;
import hu.simontamas.scrabble.viewElements.LetterInputField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.function.Function;

import static hu.simontamas.scrabble.config.Constants.LETTERS;
import static hu.simontamas.scrabble.config.Constants.TILE_SIZE;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final ThreadService threadService;

    private final WordsService wordService;

    private Board board = new Board();

    public void drawBoard(Pane pane) {
        pane.getChildren().clear();

        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                LetterInputField lf = new LetterInputField(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, Board.tiles[i][j]);
                lf.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
                updateBoardListener(i, j, lf);
                pane.getChildren().add(lf);
                if (board.state[i * Board.SIZE + j] != null) {
                    lf.setText(board.state[i * Board.SIZE + j].toString());
                } else if (board.newState[i * Board.SIZE + j] != null) {
                    lf.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                    lf.setText(board.newState[i * Board.SIZE + j].toString());
                }
            }
        }
    }

    private void updateBoardListener(int i, int j, LetterInputField tf) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().isEmpty()) {
                    tf.setBorder(null);
                    board.newState[Board.SIZE * i + j] = null;
                } else if (LETTERS.contains(tf.getText().toUpperCase())) {
                    board.newState[Board.SIZE * i + j] = Letters.valueOf(tf.getText().toUpperCase());
                    tf.setText(tf.getText().toUpperCase());
                    tf.setBorder(null);
                } else {
                    tf.setBorder(Border.stroke(Color.RED));
                }

                if (board.state[i * Board.SIZE + j] != board.newState[i * Board.SIZE + j]) {
                    tf.setFont(Font.font("Arial", FontWeight.BOLD, 13));
                } else {
                    tf.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
                }
            }
        });
    }


    public void validateBoard(Label errorLabel, Label scoreLabel, Function<Void, Void> onValid) {
        ValidateBoardTask isBoardValid = new ValidateBoardTask(board, wordService);

        threadService.runTask(isBoardValid, (res) -> {
            scoreLabel.setText((res.getNewScore() - res.getPreviousScore())
                    + "\n With the following words: \n"
                    + res.getAddedWords()
            );

            if (res.getErrors().isEmpty()) {
                errorLabel.setText("Board is valid!");
                if (onValid != null)
                    onValid.apply(null);
            } else {
                errorLabel.setText("Board is not valid! \n Errors: " + res.getErrors());
            }

            return null;
        });
    }

    public void validateBoard(Label errorLabel, Label scoreLabel) {
        wordService.setType(SimpleWordService.class);
        validateBoard(errorLabel, scoreLabel, null);
    }

    public void saveBoard(Label errorLabel, Label scoreLabel, Pane boardPane) {
        validateBoard(errorLabel, scoreLabel, (res) -> {
            System.arraycopy(board.newState, 0, board.state, 0, Board.SIZE * Board.SIZE);
            drawBoard(boardPane);
            return null;
        });
    }

    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }
}
