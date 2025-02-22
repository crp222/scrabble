package hu.simontamas.scrabble.threads;

import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.exceptions.AiException;
import hu.simontamas.scrabble.model.AiResult;
import hu.simontamas.scrabble.model.Board;
import hu.simontamas.scrabble.model.ValidationResult;
import hu.simontamas.scrabble.service.BoardService;
import hu.simontamas.scrabble.service.HandService;
import hu.simontamas.scrabble.service.wordService.SimpleWordService;
import hu.simontamas.scrabble.service.wordService.WordsService;
import hu.simontamas.scrabble.utils.AiSearchTask;
import hu.simontamas.scrabble.utils.WordUtils;

import java.util.*;

public class ExactMatchingSearch extends AiSearchTask {

    public int WORD_LIMIT = 10;

    public ExactMatchingSearch(HandService handService, WordsService wordsService, BoardService boardService) {
        super(handService, wordsService, boardService);
        DEVMODE = false;
    }

    @Override
    public AiResult callAi() throws Exception {
        wordsService.setType(SimpleWordService.class);
        Letters[] state = boardService.getBoard().newState;
        AiResult aiResult = new AiResult();
        search(state, aiResult);
        return aiResult;
    }

    @Override
    public void setUpFastestSearch() {
        WORD_LIMIT = 2;
    }

    public void search(Letters[] state, AiResult result) throws AiException {
        for (Map.Entry<Integer, List<String>> entry : wordsService.getWords().entrySet()) {
            for (String word : entry.getValue()) {
                boolean wordFormableWithHand = false;
                for (String letter : handService.getCurrentHandStr()) {
                    if (word.contains(letter)) {
                        wordFormableWithHand = true;
                        break;
                    }
                }
                if (!wordFormableWithHand) {
                    continue;
                }
                for (int i = 0; i < Board.SIZE; i++) {
                    for (int j = 0; j < Board.SIZE; j++) {
                        tryToFillInWord(result, word, i, j, 0);
                        tryToFillInWord(result, word, i, j, 1);

                        if (result.getWords().size() > WORD_LIMIT) {
                            return;
                        }

                    }
                }
            }
        }
    }

    public boolean tryToFillInWord(AiResult aiResult, String wordStr, int row, int col, int dir) {
        try {
            resetState();
            List<Letters> word = WordUtils.strToLetters(wordStr);
            AiResult.AiResultWord aiResultWord = new AiResult.AiResultWord();
            aiResultWord.setUsedLetters(new ArrayList<>());
            List<String> positions = new ArrayList<>(); // these are like 1-1 or 3-2
            Iterator<Letters> wordIterator = word.iterator();
            int wordStart, wordEnd;

            List<Letters> availableLetters = new ArrayList<>(List.of(handService.getCurrentHand().state));

            switch (dir) {
                case 0: {
                    wordStart = col;
                    wordEnd = wordStart + word.size();
                    if (wordStart < 0 || wordEnd > Board.SIZE) {
                        return false;
                    }

                    for (int i = 0; i < word.size(); i++) {
                        int index = row * Board.SIZE + col + i;
                        Letters next = wordIterator.next();

                        if (boardService.getBoard().state[index] != null) {
                            if (boardService.getBoard().state[index] != next) {
                                return false;
                            }
                            continue;
                        }

                        boardService.getBoard().newState[index] = next;
                        positions.add(row + "-" + (col + i)); // Save position
                        aiResultWord.getUsedLetters().add(boardService.getBoard().newState[index]);  // Add new used letter
                        if (availableLetters.contains(next)) {
                            availableLetters.remove(next);
                        } else {
                            return false;
                        }
                    }
                }
                break;

                case 1: {
                    wordStart = row;
                    wordEnd = wordStart + word.size();
                    if (wordStart < 0 || wordEnd > Board.SIZE) {
                        return false;
                    }

                    for (int i = 0; i < word.size(); i++) {
                        int index = (row + i) * Board.SIZE + col;
                        Letters next = wordIterator.next();

                        if (boardService.getBoard().state[index] != null) {
                            if (boardService.getBoard().state[index] != next) {
                                return false;
                            }
                            continue;
                        }

                        boardService.getBoard().newState[index] = next;
                        positions.add((row + i) + "-" + col); // Save position
                        aiResultWord.getUsedLetters().add(boardService.getBoard().newState[index]); // Add new used letter
                        if (availableLetters.contains(next)) {
                            availableLetters.remove(next);
                        } else {
                            return false;
                        }
                    }
                }
                break;
            }

            ValidateBoardTask task = new ValidateBoardTask(boardService.getBoard(), wordsService);
            ValidationResult result = task.check();
            printDevmode();

            if (result.getErrors().isEmpty() && !aiResult.getStrWords().contains(wordStr)) {
                aiResultWord.setWord(wordStr);
                aiResultWord.setPositions(positions);
                aiResultWord.setWord(result.getAddedWords().stream().max(Comparator.comparingInt(String::length)).get());
                aiResultWord.setScore(result.getNewScore() - result.getPreviousScore());
                aiResult.getWords().add(aiResultWord);
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }
}
