package hu.simontamas.scrabble.service;

import hu.simontamas.scrabble.threads.AiSelfGameTask;
import hu.simontamas.scrabble.utils.AiSearchTask;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AiSelfGameService {
    private final BoardService boardService;
    private final HandService handService;
    private final StorageService storageService;

    private final ThreadService threadService;


    public void runSelfGameOnDifferentThread(AiSearchTask ai, ProgressBar progressBar, Button runButton) {
        AiSelfGameTask task = new AiSelfGameTask(boardService, handService, storageService, progressBar, runButton, ai);
        threadService.runTask(task, unused -> null);
    }
}
