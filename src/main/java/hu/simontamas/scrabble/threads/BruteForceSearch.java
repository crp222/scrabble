package hu.simontamas.scrabble.threads;

import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.exceptions.AiException;
import hu.simontamas.scrabble.model.AiResult;
import hu.simontamas.scrabble.model.Board;
import hu.simontamas.scrabble.model.Position;
import hu.simontamas.scrabble.model.ValidationResult;
import hu.simontamas.scrabble.service.BoardService;
import hu.simontamas.scrabble.service.HandService;
import hu.simontamas.scrabble.service.wordService.SimpleWordService;
import hu.simontamas.scrabble.service.wordService.WordsService;
import hu.simontamas.scrabble.utils.AiSearchTask;
import hu.simontamas.scrabble.utils.BoardUtils;
import hu.simontamas.scrabble.utils.WordUtils;

import java.util.*;
import java.util.stream.Collectors;

public class BruteForceSearch extends AiSearchTask {

    public final static int MAX_WORD_LENGTH = 11;

    // The AI stops after finding this amount of words in a row or col
    public static int WORD_LIMIT = 2;

    public static int MAX_WORD_COUNT = 10;

    private final Set<String> hookerWords = new HashSet<>();

    private final Set<String> positionDirAlreadyCheckedMap = new HashSet<>();

    private int longestFoundWord = 0;

    private int maxErrorWithLongestFoundWord = 2;

    public BruteForceSearch(HandService handService, WordsService wordsService, BoardService boardService) {
        super(handService, boardService.getBoard(), wordsService, boardService);
        DEVMODE = false;
    }

    @Override
    public AiResult callAi() throws Exception {
        wordsService.setType(SimpleWordService.class);
        AiResult aiResult = new AiResult();
        search(board.state, aiResult);
        return aiResult;
    }

    @Override
    public void setUpFastestSearch() {
        MAX_WORD_COUNT = 2;
    }

    protected void search(Letters[] state, AiResult aiResult) throws AiException {
        longestFoundWord = 0;
        preSearchHookerWords();
        Set<String> positions = BoardUtils.getLetterPositions(state);
        for (String position : positions) {
            String[] parts = position.split("-");
            int row = Integer.parseInt(parts[0]);
            int col = Integer.parseInt(parts[1]);
            if (BoardUtils.getLetter(row, col, state) == null) {
                continue;
            }

            String sec = state[row * Board.SIZE + col].toString();

            try {
                handService.getCurrentHandStr();
            } catch (NullPointerException ignore) {
                throw new AiException("Hand cannot be empty!");
            }

            computeResults(aiResult, row, col, sec, handService.getCurrentHandStr(), WORD_LIMIT);

            if (row + 1 < Board.SIZE && BoardUtils.getLetter(row + 1, col, boardService.getBoard().state) == null) {
                searchHookers(aiResult, row + 1, col);
            }

            if (col + 1 < Board.SIZE && BoardUtils.getLetter(row, col + 1, boardService.getBoard().state) == null) {
                searchHookers(aiResult, row, col + 1);
            }

            aiResult.setWords(
                    aiResult.getWords().stream().sorted(Comparator.comparing(AiResult.AiResultWord::getScore).reversed())
                            .collect(Collectors.toList())
            );

            if (aiResult.getWords().size() > MAX_WORD_COUNT) {
                return;
            }
        }
    }

    protected void searchHookers(AiResult aiResult, int row, int col) {
        List<String> distinctLetters = handService.getCurrentHandStr().stream().distinct().toList();
        for (String letter : distinctLetters) {
            List<String> newLetters = handService.getCurrentHandStr().stream().filter(l -> !Objects.equals(l, letter)).toList();
            computeResults(aiResult, row, col, letter, newLetters, 1, hookerWords.stream().toList());
        }
    }

    protected void preSearchHookerWords() {
        List<String> distinctLetters = handService.getCurrentHandStr().stream().distinct().toList();
        for (String letter : distinctLetters) {
            List<String> newLetters = distinctLetters.stream().filter(l -> !Objects.equals(l, letter)).toList();
            hookerWords.addAll(wordsService.wordsIncludingSec(newLetters, letter));
        }
    }

    protected void computeResults(AiResult aiResult, int row, int col, String sec, List<String> handLetters, int limit) {
        computeResults(aiResult, row, col, sec, handLetters, limit, null);
    }

    private void computeResults(AiResult aiResult, int row, int col, String sec, List<String> handLetters, int limit,
                                List<String> words) {
        if (words == null) {
            words = wordsService.wordsIncludingSec(handLetters, sec);
        }
        for (String word : words) {
            for (int j = 0; j < word.length(); j++) {
                if (aiResult.getStrWords().contains(word)) {
                    return;
                }
                int foundWordCount = 0;

                List<Letters> letters = WordUtils.strToLetters(word);

                Position p = Position.builder().x(row).y(col).build();
                if (!positionDirAlreadyCheckedMap.contains(p + ": 0")){
                    if (tryToFillWord(aiResult, row, col, letters, 0, j)) {
                        foundWordCount++;
                    }
                }

                resetState();

                if (!positionDirAlreadyCheckedMap.contains(p + ": 1")) {
                    if (tryToFillWord(aiResult, row, col, letters, 1, j)) {
                        foundWordCount++;
                    }
                }

                resetState();

                if (foundWordCount >= limit) {
                    break;
                }
            }
        }
    }

    protected boolean tryToFillWord(AiResult aiResult, int row, int col, List<Letters> word, int dir, int center) {
        try {
            if(word.size() < longestFoundWord - maxErrorWithLongestFoundWord) {
                return false;
            }

            AiResult.AiResultWord aiResultWord = new AiResult.AiResultWord();
            aiResultWord.setUsedLetters(new ArrayList<>());
            List<String> positions = new ArrayList<>(); // these are like 1-1 or 3-2
            Iterator<Letters> wordIterator = word.iterator();
            int wordStart, wordEnd;

            switch (dir) {
                // Horizontal (Left to Right)
                case 0: {
                    wordStart = col - center;
                    wordEnd = wordStart + word.size();
                    if (wordStart < 0 || wordEnd > Board.SIZE) {
                        return false;
                    }


                    for (int i = -center; i < word.size() - center; i++) {
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
                    }
                    break;
                }

                // Vertical (Top to Bottom)
                case 1: {
                    wordStart = row - center;
                    wordEnd = wordStart + word.size();
                    if (wordStart < 0 || wordEnd > Board.SIZE) {
                        return false;
                    }

                    for (int i = -center; i < word.size() - center; i++) {
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
                    }
                    break;
                }

                default:
                    throw new IllegalArgumentException("Invalid direction: " + dir);
            }
            ValidateBoardTask task = new ValidateBoardTask(board, wordsService);
            ValidationResult result = task.check();
            printDevmode();
            if (result.getErrors().isEmpty()) {
                aiResultWord.setPositions(positions);
                aiResultWord.setWord(result.getAddedWords().stream().max(Comparator.comparingInt(String::length)).get());
                aiResultWord.setScore(result.getNewScore() - result.getPreviousScore());
                aiResult.getWords().add(aiResultWord);
                if(word.size() > longestFoundWord)  {
                    longestFoundWord = word.size();
                }
                return true;
            }
        } catch (Exception ignore) {
        }
        return false;
    }
}
