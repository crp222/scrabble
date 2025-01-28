package hu.simontamas.scrabble.threads;

import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.exceptions.AiException;
import hu.simontamas.scrabble.model.AiResult;
import hu.simontamas.scrabble.model.Board;
import hu.simontamas.scrabble.model.ValidationResult;
import hu.simontamas.scrabble.service.BoardService;
import hu.simontamas.scrabble.service.HandService;
import hu.simontamas.scrabble.service.WordsService;
import hu.simontamas.scrabble.utils.AiSearchTask;
import hu.simontamas.scrabble.utils.BoardUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BruteForceSearch extends AiSearchTask {

    private final static boolean DEVMODE = false;

    private final static int MAX_DEEP = -1;

    public BruteForceSearch(HandService handService, WordsService wordsService, BoardService boardService) {
        super(handService, boardService.getBoard(), wordsService, boardService);
    }
    @Override
    protected AiResult call() throws Exception {
        AiResult aiResult = new AiResult();
        search(board.state, aiResult);
        return aiResult;
    }

    private void search(Letters[] state, AiResult aiResult) throws AiException {
        Set<String> positions = BoardUtils.getLetterPositions(state);
        for(String position : positions) {
            String[] parts = position.split("-");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            if(BoardUtils.getLetter(row, col, state) == null) {
                continue;
            }
            String sec = BoardUtils.getLetter(row, col, state).toString();

            try {
                handService.getCurrentHandStr();
            } catch (NullPointerException ignore) {
                throw new AiException("Hand cannot be empty!");
            }

            for(int i = 10; i > 2; i--) {
                computeResults(aiResult, row, col, sec, i);

                if(aiResult.getWords().size() >= MAX_DEEP && MAX_DEEP != -1) {
                    aiResult.setWords(
                            aiResult.getWords().stream().sorted(Comparator.comparing(AiResult.AiResultWord::getScore).reversed())
                                    .collect(Collectors.toList())
                    );
                    break;
                }
            }
        }
    }

    private void computeResults(AiResult aiResult, int row, int col, String sec, int wordLength) {
        for(int j = wordLength; j > 0; j--) {
            List<String> words = wordsService.wordsIncludingSecInPosition(handService.getCurrentHandStr() ,wordLength, sec,j);
            for(String word : words) {
                // TODO: branching when found letter with 2 length for example 'TY'
                List<Letters> letters = strToLetters(word);
                tryToFillWord(aiResult, row, col, letters, 0, j);
                resetState();
            }
            for(String word : words) {
                // TODO: branching when found letter with 2 length for example 'TY'
                List<Letters> letters = strToLetters(word);
                tryToFillWord(aiResult, row, col, letters, 1, j);
                resetState();
            }
        }
    }

    private void tryToFillWord(AiResult aiResult, int row, int col, List<Letters> word, int dir, int center) {
        try {
            AiResult.AiResultWord aiResultWord = new AiResult.AiResultWord();
            aiResultWord.setUsedLetters(new ArrayList<>());
            Letters[] letters = new Letters[word.size()];
            word.toArray(letters);
            aiResultWord.setLetters(letters);
            List<String> positions = new ArrayList<>(); // these are like 1-1 or 3-2
            Iterator<Letters> wordIterator = word.iterator();
            int wordStart, wordEnd;

            switch (dir) {
                // Horizontal (Left to Right)
                case 0: {
                    wordStart = col - center;
                    wordEnd = wordStart + word.size();
                    if (wordStart < 0 || wordEnd > Board.SIZE) {
                        throw new IllegalArgumentException("Word placement is out of horizontal bounds!");
                    }


                    for (int i = -center; i < word.size() - center; i++) {
                        int index = row * Board.SIZE + col + i;
                        if(boardService.getBoard().state[index] != null) {
                            continue;
                        }
                        boardService.getBoard().newState[index] = wordIterator.next();
                        positions.add(row + "-" + (col + i)); // Save position
                        aiResultWord.getUsedLetters().add(boardService.getBoard().newState[index]);  // Add new used letter
                    }
                    break;
                }

                // Vertical (Top to Bottom)
                case 1: {
                    wordStart = row - center;
                    wordEnd = wordStart + word.size();
                    if (wordStart < 0 || wordEnd > Board.SIZE) {
                        throw new IllegalArgumentException("Word placement is out of vertical bounds!");
                    }

                    for (int i = -center; i < word.size() - center; i++) {
                        int index = (row + i) * Board.SIZE + col;
                        if(boardService.getBoard().state[index] != null) {
                            continue;
                        }
                        boardService.getBoard().newState[index] = wordIterator.next();
                        positions.add((row + i) + "-" + col); // Save position
                        aiResultWord.getUsedLetters().add(boardService.getBoard().newState[index]); // Add new used letter
                    }
                    break;
                }

                default:
                    throw new IllegalArgumentException("Invalid direction: " + dir);
            }
            ValidateBoardTask task = new ValidateBoardTask(board, wordsService);
            Thread thread = new Thread(task);
            thread.start();
            ValidationResult result = task.get();
            printDevmode();
            if(result.getErrors().isEmpty()) {
                aiResultWord.setPositions(positions);
                aiResultWord.setWord(result.getAddedWords().get(0));
                aiResultWord.setScore(result.getNewScore() - result.getPreviousScore());
                aiResult.getWords().add(aiResultWord);
            }
        } catch (Exception ignore) {}
    }

    // TODO: Handling when 2 length letters included, for example 'TY'
    private List<Letters> strToLetters(String str) {
        List<Letters> res = new ArrayList<>();
        for(var c : str.toCharArray()) {
            res.add(Letters.valueOf(c+""));
        }
        return res;
    }

    private void resetState() {
        for(int i = 0; i < Board.SIZE * Board.SIZE;i++) {
            board.newState[i] = board.state[i];
        }
    }

    private void printDevmode() {
        try {
            if(DEVMODE) {
                System.out.println();
                System.out.println();
                for (int row = 0; row < Board.SIZE; row++) {
                    for (int col = 0; col < Board.SIZE; col++) {
                        if(board.newState[col * Board.SIZE + row] != null) {
                            System.out.print(board.newState[col * Board.SIZE + row].toString() + "|");
                        }else {
                            System.out.print(" |");
                        }
                    }
                    System.out.println();
                }
                Thread.sleep(100);
            }
        }catch (Exception err) {
            err.printStackTrace();
            throw new RuntimeException("Failed to sleep in devmode!");
        }
    }
}
