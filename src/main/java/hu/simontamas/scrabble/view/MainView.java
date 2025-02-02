package hu.simontamas.scrabble.view;

import hu.simontamas.scrabble.ScrabbleApplication;
import hu.simontamas.scrabble.enums.AiS;
import hu.simontamas.scrabble.model.Board;
import hu.simontamas.scrabble.service.AiService;
import hu.simontamas.scrabble.service.BoardService;
import hu.simontamas.scrabble.service.HandService;
import hu.simontamas.scrabble.service.StorageService;
import hu.simontamas.scrabble.threads.BruteForceSearch;
import hu.simontamas.scrabble.utils.HandUtils;
import javafx.collections.ObservableListBase;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;

@Component
public class MainView {

    @FXML
    public Pane BoardPane;

    @FXML
    public Label ErrorLabel;

    @FXML
    public Label ScoreLabel;

    @FXML
    public VBox AiResults;

    @FXML
    public Pane HandPane;

    @FXML
    public ChoiceBox<AiS> SelectAiComboBox;

    @Autowired
    public BoardService boardService;
    @Autowired
    public HandService handService;

    @Autowired
    public AiService aiService;

    @Autowired
    public StorageService storageService;

    @FXML
    public void initialize() {
        boardService.drawBoard(BoardPane);
        handService.drawHand(HandPane);

        SelectAiComboBox.setValue(AiS.BRUTE_FORCE);
        SelectAiComboBox.getItems().addAll(AiS.values());
    }

    @FXML
    public void validateBoard() {
        boardService.validateBoard(ErrorLabel, ScoreLabel);
    }

    @FXML
    public void saveBoard() {
        boardService.saveBoard(ErrorLabel, ScoreLabel, BoardPane);
    }

    @FXML
    public void runBruteForceSearch() {
        aiService.runAi(SelectAiComboBox.getValue().type, AiResults, unused -> {
            boardService.drawBoard(BoardPane);
            handService.drawHand(HandPane);
            return null;
        });
    }

    @FXML
    public void saveBoardInFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save board");
        fileChooser.setInitialFileName("boardData.board");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.board"));
        File file = fileChooser.showSaveDialog(ScrabbleApplication.getStage());
        if (file != null) {
            try {
                storageService.serializeDataOut(file.getPath(), boardService.getBoard());
            } catch (Exception exception) {
                exception.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save error!");
                alert.setHeaderText("Failed to save the board!");
                alert.setContentText(exception.getMessage());
                alert.show();
            }
        }
    }

    @FXML
    public void loadBoardFromFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load board");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("All Files", "*.board"));
        File file = fileChooser.showOpenDialog(ScrabbleApplication.getStage());
        if (file != null) {
            try {
                Board board = (Board) storageService.serializeDataIn(file.getPath());
                boardService.setBoard(board);
                boardService.drawBoard(BoardPane);
            } catch (Exception exception) {
                exception.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Save error!");
                alert.setHeaderText("Failed to load the board!");
                alert.setContentText(exception.getMessage());
                alert.show();
            }
        }
    }

    @FXML
    public void fillHandWithRandom() {
        handService.fillHandWithRandom();
        handService.drawHand(HandPane);
    }

    @FXML
    public void saveHand() {
        HandUtils.updateHandState(handService.getCurrentHand());
        handService.drawHand(HandPane);
    }
}
