package hu.simontamas.scrabble.service;

import hu.simontamas.scrabble.enums.AiS;
import hu.simontamas.scrabble.model.Board;
import hu.simontamas.scrabble.model.Hand;
import hu.simontamas.scrabble.model.SelfGameResult;
import hu.simontamas.scrabble.model.SelfGameResultViewEntity;
import hu.simontamas.scrabble.service.wordService.WordsService;
import hu.simontamas.scrabble.threads.AiSelfGameTask;
import hu.simontamas.scrabble.threads.ValidateBoardTask;
import hu.simontamas.scrabble.utils.AiSearchTask;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiSelfGameService {
    private final BoardService boardService;
    private final HandService handService;
    private final StorageService storageService;
    private final ThreadService threadService;

    private final WordsService wordsService;

    public void runSelfGameOnDifferentThread(AiS ai, ProgressBar progressBar, Button runButton) throws Exception {
        AiSearchTask aiTask = ai.type.getDeclaredConstructor(HandService.class, WordsService.class, BoardService.class)
                .newInstance(handService, wordsService, boardService);
        SelfGameResult result = new SelfGameResult();
        result.setAi(ai);
        AiSelfGameTask task = new AiSelfGameTask(boardService, handService, storageService, progressBar, runButton, aiTask, result);
        threadService.runTask(task, unused -> null);
    }

    public SelfGameResultViewEntity loadSelfGame(File file) throws Exception {
        if (!file.exists() || !file.isDirectory()) {
            throw new Exception("File doesn't exist or not a directory!");
        }
        SelfGameResultViewEntity viewEntity = new SelfGameResultViewEntity();

        File dataFile = new File(file, "data");
        if (!dataFile.exists()) {
            throw new Exception("The directory does not contain a file or folder named 'data'!");
        }

        SelfGameResult result = (SelfGameResult) storageService.serializeDataIn(dataFile.getPath());

        List<Board> boards = new ArrayList<>();
        File[] boardFiles = file.listFiles((dir, name) -> name.contains("board"));

        if (boardFiles != null) {
            Arrays.sort(boardFiles, Comparator.comparingInt(f -> Integer.parseInt(f.getName().split("-")[1])));

            for (File boardFile : boardFiles) {
                Board board = (Board) storageService.serializeDataIn(boardFile.getPath());
                boards.add(board);
            }
        }

        viewEntity.setAiName(result.getAi().name);

        for (int i = 0; i < boards.size(); i++) {
            ValidateBoardTask validateBoardTask = new ValidateBoardTask(boards.get(i), wordsService);
            viewEntity.getStates().add(SelfGameResultViewEntity.SelfGameResultState.builder()
                    .board(boards.get(i))
                    .hand(Hand.fromStr(result.getHands().get(i)))
                    .aiResult(result.getResults().get(i))
                    .validationResult(validateBoardTask.check())
                    .bag(result.getBag().get(i))
                    .time(result.getTimes().get(i))
                    .build()
            );
        }

        return viewEntity;
    }
}
