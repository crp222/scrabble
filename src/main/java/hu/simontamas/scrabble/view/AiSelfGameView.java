package hu.simontamas.scrabble.view;

import hu.simontamas.scrabble.ScrabbleApplication;
import hu.simontamas.scrabble.enums.AiS;
import hu.simontamas.scrabble.model.AiResult;
import hu.simontamas.scrabble.model.SelfGameResultViewEntity;
import hu.simontamas.scrabble.service.AiSelfGameService;
import hu.simontamas.scrabble.service.BoardService;
import hu.simontamas.scrabble.service.HandService;
import hu.simontamas.scrabble.service.wordService.WordsService;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Comparator;

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
    public ProgressBar ProgressBar;
    @FXML
    public Button RunButton;

    @FXML
    public Label Title;

    @FXML
    public ListView<GridPane> ListView;

    @FXML
    public void initialize() {
        SelectAiComboBox.setValue(AiS.BRUTE_FORCE);
        SelectAiComboBox.getItems().addAll(AiS.values());
    }

    @FXML
    public void run() throws Exception {
        gameService.runSelfGameOnDifferentThread(SelectAiComboBox.getValue(), ProgressBar, RunButton);
    }

    @FXML
    public void load() throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Load self played game");
        File file = directoryChooser.showDialog(ScrabbleApplication.getStage());
        SelfGameResultViewEntity entity = null;
        if (file != null) {
            try {
                entity = gameService.loadSelfGame(file);
                Title.setText(file.getName() + " - " + entity.getAiName());

                SelfGameResultViewEntity finalEntity = entity;
                ObservableList<GridPane> selfGameResultObsList = new ObservableListBase<>() {
                    @Override
                    public GridPane get(int index) {
                        return selfGameResultViewEntityStateToGrid(finalEntity.getStates().get(index), index, finalEntity);
                    }

                    @Override
                    public int size() {
                        return finalEntity.getStates().size();
                    }
                };

                ListView.setItems(selfGameResultObsList);
                ListView.refresh();
            } catch (Exception exception) {
                exception.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Load error!");
                alert.setHeaderText("Failed to load the game!");
                alert.setContentText(exception.getMessage());
                alert.show();
            }
        }
    }


    private GridPane selfGameResultViewEntityStateToGrid(SelfGameResultViewEntity.SelfGameResultState state, int index, SelfGameResultViewEntity entity) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(5);

        if (index == 0) {
            grid.add(new Label("Show"), 0, 0);
            grid.add(new Label("Best Word"), 1, 0);
            grid.add(new Label("Gained Score"), 2, 0);
            grid.add(new Label("Max Score"), 3, 0);
            grid.add(new Label("Time"), 4, 0);
            grid.add(new Label("Total Time"), 5, 0);
        }

        Button showStateBtn = new Button("Show State");

        showStateBtn.setOnAction(event -> {
            boardService.setBoard(state.getBoard());
            handService.setCurrentBag(state.getBag());
            handService.setCurrentHand(state.getHand());

            boardService.drawBoard(MainView.DefaultBoardPane);
            handService.drawHand(MainView.DefaultHandPane);
        });

        Label word = new Label(state.getAiResult().getWords()
                .stream()
                .max(Comparator.comparingInt(AiResult.AiResultWord::getScore))
                .map(AiResult.AiResultWord::getWord)
                .orElse("N/A"));

        Label gainedScore = new Label(String.valueOf(state.getValidationResult().getNewScore() - state.getValidationResult().getPreviousScore()));
        Label maxScore = new Label(String.valueOf(state.getValidationResult().getNewScore()));
        Label time = new Label(state.getTime().toString());

        long totalTimeValue = entity.getStates().subList(0, index).stream()
                .map(SelfGameResultViewEntity.SelfGameResultState::getTime)
                .reduce(0L, Long::sum);
        Label totalTime = new Label(String.valueOf(totalTimeValue));

        grid.add(showStateBtn, 0, 0);
        grid.add(word, 1, 0);
        grid.add(gainedScore, 2, 0);
        grid.add(maxScore, 3, 0);
        grid.add(time, 4, 0);
        grid.add(totalTime, 5, 0);

        return grid;
    }
}
