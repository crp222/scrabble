package hu.simontamas.scrabble.view;

import hu.simontamas.scrabble.model.LettersStats;
import hu.simontamas.scrabble.enums.Letters;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class LetterStatsView implements Initializable {

    @FXML
    private ListView<LettersStats> lettersStatsListView;

    private List<LettersStats> statsData;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        configureListView();
    }

    public void setStatsData(List<LettersStats> statsData) {
        this.statsData = statsData;
        lettersStatsListView.getItems().setAll(statsData);
    }

    private void configureListView() {
        lettersStatsListView.setCellFactory(param -> new LettersStatsCell());
    }

    @FXML
    private void closeWindow() {
        ((Stage) lettersStatsListView.getScene().getWindow()).close();
    }

    // Custom cell to display LettersStats objects
    private static class LettersStatsCell extends ListCell<LettersStats> {
        @Override
        protected void updateItem(LettersStats item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null) {
                setText(null);
                setGraphic(null);
                return;
            }

            VBox mainContainer = new VBox(10);
            mainContainer.setPadding(new Insets(10));
            mainContainer.setStyle("-fx-background-color: #f5f5f5; -fx-background-radius: 5;");

            // Hand information section
            VBox handContainer = new VBox(5);
            Label handLabel = new Label("Hand:");
            handLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

            HBox lettersHBox = new HBox(5);
            String handLetters = item.getHand().stream()
                    .map(Letters::toString)
                    .collect(Collectors.joining(", "));
            Label lettersLabel = new Label(handLetters);
            lettersHBox.getChildren().add(lettersLabel);

            handContainer.getChildren().addAll(handLabel, lettersHBox);

            // Thinking time information
            Label thinkingTimeLabel = new Label("Thinking Time: " +
                    (item.getThinkingTime() != null ? item.getThinkingTime() + " ms" : "N/A"));
            thinkingTimeLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

            // Letter stats section
            Label statsLabel = new Label("Letter Statistics:");
            statsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

            GridPane statsGrid = new GridPane();
            statsGrid.setHgap(10);
            statsGrid.setVgap(5);

            // Headers
            Label letterHeader = new Label("Letter");
            letterHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
            Label usedTimesHeader = new Label("Used Times");
            usedTimesHeader.setFont(Font.font("System", FontWeight.BOLD, 12));
            Label avgTimeHeader = new Label("Avg. Thinking Time");
            avgTimeHeader.setFont(Font.font("System", FontWeight.BOLD, 12));

            statsGrid.add(letterHeader, 0, 0);
            statsGrid.add(usedTimesHeader, 1, 0);
            statsGrid.add(avgTimeHeader, 2, 0);

            // Add each letter stat to the grid
            int row = 1;
            for (LettersStats.LetterStat stat : item.getStats()) {
                Label letterLabel = new Label(stat.getLetter().toString());
                Label usedTimesLabel = new Label(stat.getUsedTimes().toString());
                Label avgTimeLabel = new Label(
                        stat.getAverageThinkingTimeWhenPresent() != null
                                ? stat.getAverageThinkingTimeWhenPresent() + " ms"
                                : "N/A");

                statsGrid.add(letterLabel, 0, row);
                statsGrid.add(usedTimesLabel, 1, row);
                statsGrid.add(avgTimeLabel, 2, row);

                row++;
            }

            mainContainer.getChildren().addAll(
                    handContainer,
                    thinkingTimeLabel,
                    statsLabel,
                    statsGrid,
                    new Separator()
            );

            setGraphic(mainContainer);
        }
    }
}