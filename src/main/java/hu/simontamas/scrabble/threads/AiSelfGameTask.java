package hu.simontamas.scrabble.threads;

import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.exceptions.BagException;
import hu.simontamas.scrabble.model.AiResult;
import hu.simontamas.scrabble.model.Board;
import hu.simontamas.scrabble.model.SelfGameResult;
import hu.simontamas.scrabble.service.BoardService;
import hu.simontamas.scrabble.service.HandService;
import hu.simontamas.scrabble.service.StorageService;
import hu.simontamas.scrabble.service.ThreadService;
import hu.simontamas.scrabble.utils.AiSearchTask;
import hu.simontamas.scrabble.utils.BoardUtils;
import hu.simontamas.scrabble.utils.HandUtils;
import javafx.concurrent.Task;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class AiSelfGameTask extends Task<Void> {

    private final boolean DEV_MODE = false;

    private final BoardService boardService;
    private final HandService handService;
    private final StorageService storageService;

    private int MAX_ITERATIONS = 35;

    private Letters[] INITIALIZE_WORD = {Letters.S, Letters.H, Letters.O, Letters.R, Letters.T};

    private int selfGameProgress = 0;

    private final ProgressBar progressBar;

    private final Button runButton;

    private final AiSearchTask ai;

    private final SelfGameResult gameResult;

    public void runSelfGame(AiSearchTask ai, SelfGameResult gameResult) throws Exception {
        resetState();
        initializeBoardStateWithWord(INITIALIZE_WORD);
        File gameDir = createGameDirectory();
        ai.setUpFastestSearch();

        int k = 0;
        selfGameProgress = 0;
        handService.fillHandWithRandom();
        HandUtils.updateHandState(handService.getCurrentHand());
        while (k < MAX_ITERATIONS) {
            long startTime = System.currentTimeMillis();
            if(DEV_MODE) {
                System.out.println();
                System.out.println();
                BoardUtils.printBoardState(boardService.getBoard());
            }

            progressBar.setProgress((double) selfGameProgress / MAX_ITERATIONS);
            AiResult result = ai.callAi();
            gameResult.getResults().add(result);

            if (result.getWords().isEmpty()) {
                break;
            }

            AiResult.AiResultWord bestResult = result.getWords().stream().max(Comparator.comparingInt(AiResult.AiResultWord::getScore)).get();
            HandUtils.resetHandNewState(handService.getCurrentHand());
            BoardUtils.fillInWord(bestResult.getPositions(), bestResult.getUsedLetters(), boardService.getBoard());

            try {
                boardService.saveBoard();
                saveBoardToGameDir(gameDir);
            } catch (Exception err) {
                err.printStackTrace();
                saveBoardToGameDir(gameDir, "invalid");
            }

            HandUtils.removeFromNewHand(bestResult.getUsedLetters(), handService.getCurrentHand());

            gameResult.getHands().add(handService.getCurrentHandStr());

            handService.fillHandWithRandom();
            HandUtils.updateHandState(handService.getCurrentHand());

            gameResult.getBag().add(new HashMap<>(handService.getCurrentBag()));

            k++;
            selfGameProgress++;

            gameResult.getTimes().add(System.currentTimeMillis() - startTime);
        }

        storageService.serializeDataOut(Path.of(gameDir.getPath(), "data").toString(), gameResult);
    }

    private void resetState() {
        boardService.resetBoard();
        handService.resetBag();
        handService.resetHand();
    }

    private void initializeBoardStateWithWord(Letters[] letters) throws BagException {
        if (letters.length > 5 || letters.length < 3) {
            throw new RuntimeException("Board can't be initialized with longer word than 10 characters and shorter than 3");
        }
        int startRow = 7;
        int startCol = 5;
        int index = startRow * Board.SIZE + startCol;
        for (Letters l : letters) {
            boardService.getBoard().state[index] = l;
            handService.removeFromBag(l);
            index++;
        }
    }

    private File createGameDirectory() {
        File gamesDir = new File("games");
        if (!gamesDir.exists() || !gamesDir.isDirectory()) {
            throw new RuntimeException("Games directory doesn't exist");
        }
        int gameDirIndex = 1;
        File gameDir = new File("games/game-0");
        while (gameDir.exists()) {
            gameDir = new File("games/game-" + gameDirIndex);
            gameDirIndex++;
        }
        if (!gameDir.mkdir()) {
            throw new RuntimeException("Cannot create game directory!");
        }
        return gameDir;
    }

    private void saveBoardToGameDir(File gameDir) throws IOException {
        saveBoardToGameDir(gameDir, "");
    }

    private void saveBoardToGameDir(File gameDir, String info) throws IOException {
        if (!gameDir.exists() || !gameDir.isDirectory()) {
            throw new RuntimeException("Game directory doesn't exist! " + gameDir);
        }

        File[] contents = gameDir.listFiles();
        int lastBoardIndex = 0;
        if (contents != null) {
            for (File f : contents) {
                if (f.getName().startsWith("board-")) {
                    int boardIndex = Integer.parseInt(f.getName().split("-")[1]);
                    if (boardIndex > lastBoardIndex) {
                        lastBoardIndex = boardIndex;
                    }
                }
            }
        }

        String boardFilePath = "board-" + (lastBoardIndex + 1) + "-" + Arrays.stream(INITIALIZE_WORD).map(Letters::toString).collect(Collectors.joining("")) + "-" + info;

        storageService.serializeDataOut(Path.of(gameDir.getPath(), boardFilePath) + ".board", boardService.getBoard());
    }

    @Override
    protected Void call() throws Exception {
        runButton.setDisable(true);
        try {
            runSelfGame(ai, gameResult);
        } catch (Exception err) {
            err.printStackTrace();
            throw new RuntimeException();
        }
        runButton.setDisable(false);
        progressBar.setProgress(0.0);
        return null;
    }
}
