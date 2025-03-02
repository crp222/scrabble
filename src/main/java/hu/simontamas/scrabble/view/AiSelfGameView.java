package hu.simontamas.scrabble.view;

import hu.simontamas.scrabble.FxmlApplication;
import hu.simontamas.scrabble.enums.AiS;
import hu.simontamas.scrabble.model.AiResult;
import hu.simontamas.scrabble.model.SelfGameResultViewEntity;
import hu.simontamas.scrabble.service.AiSelfGameService;
import hu.simontamas.scrabble.service.BoardService;
import hu.simontamas.scrabble.service.HandService;
import hu.simontamas.scrabble.service.ThreadService;
import hu.simontamas.scrabble.service.wordService.WordsService;
import javafx.collections.ObservableList;
import javafx.collections.ObservableListBase;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    public ThreadService threadService;

    @Autowired
    public LetterStatsView letterStatsView;

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

    private SelfGameResultViewEntity currentViewEntity;

    @FXML
    public void initialize() {
        SelectAiComboBox.setValue(AiS.BRUTE_FORCE);
        SelectAiComboBox.getItems().addAll(AiS.values());
    }

    @FXML
    public void run() throws Exception {
        Task task = gameService.getSelfGameOnDifferentThread(SelectAiComboBox.getValue(), ProgressBar, RunButton);
        threadService.runTask(task, unused -> null);
    }

    @FXML
    public void run5Times() throws Exception {
        Task task1 = gameService.getSelfGameOnDifferentThread(SelectAiComboBox.getValue(), ProgressBar, RunButton);
        Task task2 = gameService.getSelfGameOnDifferentThread(SelectAiComboBox.getValue(), ProgressBar, RunButton);
        Task task3 = gameService.getSelfGameOnDifferentThread(SelectAiComboBox.getValue(), ProgressBar, RunButton);
        Task task4 = gameService.getSelfGameOnDifferentThread(SelectAiComboBox.getValue(), ProgressBar, RunButton);
        Task task5 = gameService.getSelfGameOnDifferentThread(SelectAiComboBox.getValue(), ProgressBar, RunButton);

        threadService.runTask(task1, s1 -> {
            threadService.runTask(task2, s2 -> {
                threadService.runTask(task3, s3 -> {
                    threadService.runTask(task4, s4 -> {
                        threadService.runTask(task5, unusded -> null);
                        return null;
                    });
                    return null;
                });
                return null;
            });
            return null;
        });
    }

    @FXML
    public void load() throws Exception {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Load self played game");
        File file = directoryChooser.showDialog(FxmlApplication.getStage());
        SelfGameResultViewEntity entity = null;
        if (file != null) {
            try {
                entity = gameService.loadSelfGame(file);
                Title.setText(file.getName() + "-" + entity.getAiName());

                this.currentViewEntity = entity;
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

                ListView.setOnMouseClicked(event -> {
                    SelfGameResultViewEntity.SelfGameResultState state = finalEntity.getStates().get(ListView.getSelectionModel().getSelectedIndex());
                    boardService.setBoard(state.getBoard());
                    handService.setCurrentBag(state.getBag());
                    handService.setCurrentHand(state.getHand());

                    boardService.drawBoard(MainView.DefaultBoardPane);
                    handService.drawHand(MainView.DefaultHandPane);
                });

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
        grid.setHgap(50);
        grid.setVgap(5);

        for (int i = 0; i < 5; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 5);
            grid.getColumnConstraints().add(col);
        }


        if (index == 0) {
            grid.add(new Label("Best Word"), 0, 0);
            grid.add(new Label("Gained Score"), 1, 0);
            grid.add(new Label("Max Score"), 2, 0);
            grid.add(new Label("Time"), 3, 0);
            grid.add(new Label("Total Time"), 4, 0);
        }

        Label word = new Label(state.getAiResult().getWords()
                .stream()
                .max(Comparator.comparingInt(AiResult.AiResultWord::getScore))
                .map(AiResult.AiResultWord::getWord)
                .orElse("N/A"));

        int maxScore = entity.getStates().subList(0, index).stream().map(
                v -> v.getValidationResult().getNewScore() - v.getValidationResult().getPreviousScore()
        ).reduce(0, Integer::sum);
        Label gainedScore = new Label(String.valueOf(state.getValidationResult().getNewScore() - state.getValidationResult().getPreviousScore()));
        Label maxScoreL = new Label(String.valueOf(maxScore));
        Label time = new Label(state.getTime().toString());

        long totalTimeValue = entity.getStates().subList(0, index).stream()
                .map(SelfGameResultViewEntity.SelfGameResultState::getTime)
                .reduce(0L, Long::sum);
        Label totalTime = new Label(String.valueOf(totalTimeValue));

        int rowIndex = index == 0 ? 1 : 0;

        grid.add(word, 0, rowIndex);
        grid.add(gainedScore, 1, rowIndex);
        grid.add(maxScoreL, 2, rowIndex);
        grid.add(time, 3, rowIndex);
        grid.add(totalTime, 4, rowIndex);

        return grid;
    }

    @FXML
    public void showTimeChart() {
        if (currentViewEntity == null) {
            return;
        }

        List<Long> times = currentViewEntity.getStates().stream().map(SelfGameResultViewEntity.SelfGameResultState::getTime).toList();

        // Create X and Y series for the chart
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Time Over States");

        // Add data to the series (index as X, time as Y)
        for (int i = 0; i < times.size(); i++) {
            series.getData().add(new XYChart.Data<>(i + 1, times.get(i)));
        }

        // Create a LineChart (you can also use BarChart or other types)
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();
        LineChart<Number, Number> timeChart = new LineChart<>(xAxis, yAxis);
        timeChart.getData().add(series);

        // Customize the chart (e.g., axis labels)
        xAxis.setLabel("State Index");
        yAxis.setLabel("Time (ms)");

        // Create a new Stage (popup window) for the chart
        Stage chartStage = new Stage();
        chartStage.setTitle("Time Chart");

        // Create a layout pane for the chart (e.g., StackPane or VBox)
        StackPane chartPane = new StackPane(timeChart);

        // Set up the scene for the popup window
        Scene chartScene = new Scene(chartPane, 600, 400);  // Adjust width and height as needed
        chartStage.setScene(chartScene);

        // Show the popup window
        chartStage.show();
    }

    @FXML
    public void showLetterStats() throws Exception {
        FxmlApplication.loadScene("view/letter_stats_view.fxml");
        letterStatsView.setStatsData(currentViewEntity.getStates().stream()
                .map(SelfGameResultViewEntity.SelfGameResultState::getLettersStats).collect(Collectors.toList()));
    }
}
