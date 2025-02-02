package hu.simontamas.scrabble.threads;

import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.model.Board;
import hu.simontamas.scrabble.model.Position;
import hu.simontamas.scrabble.model.ValidationResult;
import hu.simontamas.scrabble.service.wordService.WordsService;
import hu.simontamas.scrabble.utils.BoardUtils;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.*;

@RequiredArgsConstructor
@ToString
public class ValidateBoardTask extends Task<ValidationResult> {

    private final Board board;
    private final WordsService wordService;

    private void checkLine(ValidationResult result, Letters[] letters, Position[] positions) {
        StringBuilder word = new StringBuilder();
        int wordEndIndex = 0;
        int wordStartIndex = 0;
        boolean newWord = false;
        for (Letters letter : letters) {
            if (letter != null) {
                if(!newWord) {
                    wordStartIndex = wordEndIndex;
                    newWord = true;
                }
                word.append(letter);
            } else {
                if (word.length() > 1 && !wordService.wordExist(word.toString())) {
                    result.addError(word.toString() + " is not a vaid word!");
                } else if (word.length() > 1) {
                    addWordScore(result, letters, positions, word.toString(), wordStartIndex, wordEndIndex);
                }
                word = new StringBuilder();
                newWord = false;
            }
            wordEndIndex++;
        }
        if (word.length() > 1 && !wordService.wordExist(word.toString())) {
            result.addError(word.toString() + " is not a vaid word!");
        } else if (word.length() > 1) {
            addWordScore(result, letters, positions, word.toString(), wordStartIndex, wordEndIndex);
        }
    }

    @Override
    protected ValidationResult call() throws Exception {
        ValidationResult result = new ValidationResult();
        Letters[] newState = board.newState;
        checkReachableAndNeighbors(result, newState);
        if (result.getErrors().isEmpty()) {
            checkColumns(result, newState);
            checkRows(result, newState);
        }

        return result;
    }

    private void checkRows(ValidationResult result, Letters[] state) {
        Letters[] line = new Letters[Board.SIZE];
        Position[] positions = new Position[Board.SIZE];
        for (int i = 0; i < Board.SIZE; i++) {
            int n = 0;
            for (int k = i; k < Board.SIZE * Board.SIZE; k += Board.SIZE) {
                line[n] = state[k];
                if (line[n] != null)
                    positions[n] = new Position(n, i);
                n++;
            }
            checkLine(result, line, positions);
        }
    }

    private void checkColumns(ValidationResult result, Letters[] state) {
        Letters[] line = new Letters[Board.SIZE];
        Position[] positions = new Position[Board.SIZE];
        for (int i = 0; i < Board.SIZE; i++) {
            int n = 0;
            for (int j = 0; j < Board.SIZE; j++) {
                line[n] = state[i * Board.SIZE + j];
                if (line[n] != null)
                    positions[n] = new Position(i, j);
                n++;
            }
            checkLine(result, line, positions);
        }
    }

    private void addWordScore(ValidationResult result, Letters[] letters, Position[] positions, String word,
                              int wordStartIndex, int wordEndIndex) {
        int score = 0;
        int wordMultiplier = 1;
        boolean newWord = false;
        int n = 0;
        for (Position p : positions) {
            if (n == wordEndIndex) {
                break;
            }
            if(n < wordStartIndex) {
                n++;
                continue;
            }
            if (p == null) {
                n++;
                continue;
            }
            if (letters[n] != null) {
                if (isNewLetter(p.x, p.y)) {
                    newWord = true;
                    switch (Board.tiles[p.x][p.y]) {
                        case "DW" -> {
                            wordMultiplier *= 2;
                            score += letters[n].value;
                        }
                        case "TW" -> {
                            wordMultiplier *= 3;
                            score += letters[n].value;
                        }
                        case "TL" -> score += letters[n].value * 3;
                        case "DL" -> score += letters[n].value * 2;
                        default -> score += letters[n].value;
                    }
                } else {
                    score += letters[n].value;
                }
            }
            n++;
        }
        if (n == wordEndIndex) {
            if (newWord) {
                score *= wordMultiplier;
                result.getAddedWords().add(word);
            } else {
                result.setPreviousScore(result.getPreviousScore() + score);
            }
        }
        result.setNewScore(result.getNewScore() + score);
    }

    private boolean isNewLetter(int row, int col) {
        return board.newState[Board.SIZE * row + col] != board.state[Board.SIZE * row + col];
    }

    private void checkReachableAndNeighbors(ValidationResult result, Letters[] state) {
        if (state[Board.SIZE * 7 + 7] == null) {
            result.addError("Center is empty!");
            return;
        }
        List<Letters> reachableRelatedErrors = new ArrayList<>();
        Set<String> positions = BoardUtils.getLetterPositions(state);

        for (int i = 0; i < Board.SIZE; i++) {
            for (int j = 0; j < Board.SIZE; j++) {
                if (state[Board.SIZE * i + j] != null && !positions.contains(i + "-" + j)) {
                    reachableRelatedErrors.add(state[Board.SIZE * i + j]);
                }
                checkIfHavingANeighbour(i, j, result, state);
            }
        }

        if (!reachableRelatedErrors.isEmpty()) {
            result.addError("The following does not connected to the center: " + reachableRelatedErrors);
        }
    }

    private void checkIfHavingANeighbour(int i, int j, ValidationResult validationResult, Letters[] state) {
        if (state[Board.SIZE * i + j] != null) {
            var neighbors = BoardUtils.getNeighboringPositions(i, j);
            int strike = 0;
            for (String n : neighbors) {
                String[] current = n.split("-");
                int currentRow = Integer.parseInt(current[0]);
                int currentCol = Integer.parseInt(current[1]);
                if (state[Board.SIZE * currentRow + currentCol] == null) {
                    strike++;
                }
            }
            if (strike == 4) {
                validationResult.addError(state[Board.SIZE * i + j] + " not having neighbours!");
            }
        }
    }
}
