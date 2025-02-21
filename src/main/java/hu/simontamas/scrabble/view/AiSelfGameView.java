package hu.simontamas.scrabble.view;

import hu.simontamas.scrabble.enums.AiS;
import hu.simontamas.scrabble.service.AiSelfGameService;
import hu.simontamas.scrabble.service.BoardService;
import hu.simontamas.scrabble.service.HandService;
import hu.simontamas.scrabble.service.wordService.WordsService;
import hu.simontamas.scrabble.utils.AiSearchTask;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ProgressBar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AiSelfGameView {
    @Autowired
    public AiSelfGameService gameService;
    @Autowired
    public BoardService boardService;
    @Autowired
    public HandService handService;
    @Autowired
    public WordsService wordsService;

    @FXML
    public ChoiceBox<AiS> SelectAiComboBox;
    @FXML
    public ProgressBar progressBar;
    @FXML
    public Button runButton;

    @FXML
    public void initialize() {
        SelectAiComboBox.setValue(AiS.BRUTE_FORCE);
        SelectAiComboBox.getItems().addAll(AiS.values());
    }

    @FXML
    public void run() throws Exception{
        AiSearchTask ai = SelectAiComboBox.getValue().type.getDeclaredConstructor(HandService.class, WordsService.class, BoardService.class)
                .newInstance(handService, wordsService, boardService);
        gameService.runSelfGameOnDifferentThread(ai, progressBar, runButton);
    }
}
