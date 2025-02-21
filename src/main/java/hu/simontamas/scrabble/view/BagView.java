package hu.simontamas.scrabble.view;

import hu.simontamas.scrabble.enums.AiS;
import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.service.HandService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BagView {

    @FXML
    public VBox BagVBOX;

    @Autowired
    public HandService handService;

    private final Map<Letters, Spinner<Integer>> state = new HashMap<>();

    @FXML
    public void initialize() {
        BagVBOX.getChildren().clear();
        Map<Letters, Integer> bag = handService.getCurrentBag();

        for(var entry : bag.entrySet()) {
            Label label = new Label();
            label.setText(entry.getKey().toString() + " : ");
            Spinner<Integer> spinner = new Spinner<>();
            spinner.setEditable(true);
            spinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, entry.getValue()));

            GridPane gridPane = new GridPane();
            gridPane.addColumn(0, label);
            gridPane.addColumn(1, spinner);

            BagVBOX.getChildren().add(gridPane);

            state.put(entry.getKey(), spinner);
        }
    }

    @FXML
    public void saveBag() {
        try {
            Map<Letters, Integer> newBag = new HashMap<>();
            for(var entry : state.entrySet().stream().map(entry -> Map.entry(entry.getKey(), entry.getValue().getValue())).collect(Collectors.toSet())) {
                newBag.put(entry.getKey(), entry.getValue());
            }
            handService.setCurrentBag(newBag);
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Bag saved!");
            alert.show();
        } catch (Exception ignore) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Failed to save bag!");
            alert.show();
        }
    }
}
