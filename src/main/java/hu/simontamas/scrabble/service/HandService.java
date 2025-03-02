package hu.simontamas.scrabble.service;

import hu.simontamas.scrabble.config.Constants;
import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.exceptions.BagException;
import hu.simontamas.scrabble.model.Hand;
import hu.simontamas.scrabble.viewElements.LetterInputField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

import java.util.*;

import static hu.simontamas.scrabble.config.Constants.LETTERS;

@Service
@Setter
@Getter
public class HandService {

    private static final Map<Letters, Integer> bag = Map.ofEntries(
            Map.entry(Letters.A, 9), Map.entry(Letters.B, 2), Map.entry(Letters.C, 2), Map.entry(Letters.D, 4),
            Map.entry(Letters.E, 12), Map.entry(Letters.F, 2), Map.entry(Letters.G, 3), Map.entry(Letters.H, 2),
            Map.entry(Letters.I, 9), Map.entry(Letters.J, 1), Map.entry(Letters.K, 1), Map.entry(Letters.L, 4),
            Map.entry(Letters.M, 2), Map.entry(Letters.N, 6), Map.entry(Letters.O, 8), Map.entry(Letters.P, 2),
            Map.entry(Letters.Q, 1), Map.entry(Letters.R, 6), Map.entry(Letters.S, 4), Map.entry(Letters.T, 6),
            Map.entry(Letters.U, 4), Map.entry(Letters.V, 2), Map.entry(Letters.W, 2), Map.entry(Letters.X, 1),
            Map.entry(Letters.Y, 2), Map.entry(Letters.Z, 1)
    );

    private Map<Letters, Integer> currentBag = new HashMap<>(bag);

    private Hand currentHand = new Hand();

    private final Random random = new Random();

    public void drawHand(Pane pane) {
        pane.getChildren().clear();

        for (int i = 0; i < Hand.SIZE; i++) {
            LetterInputField lf = new LetterInputField(i * Constants.TILE_SIZE, 0, Constants.TILE_SIZE, "");
            lf.setFont(Font.font("Arial", FontWeight.NORMAL, 13));
            if (currentHand.newState[i] != null) {
                lf.setText(currentHand.newState[i].toString());
            } else {
                lf.setText("");
            }
            if (currentHand.newState[i] != currentHand.state[i]) {
                lf.setFont(Font.font("Arial", FontWeight.BOLD, 13));
            }
            updateHandListener(i, lf);
            pane.getChildren().add(lf);
        }
    }

    private void updateHandListener(int i, LetterInputField tf) {
        tf.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                if (tf.getText().isEmpty()) {
                    tf.setBorder(null);
                    currentHand.newState[i] = null;
                } else if (LETTERS.contains(tf.getText().toUpperCase())) {
                    currentHand.newState[i] = Letters.valueOf(tf.getText().toUpperCase());
                    tf.setText(tf.getText().toUpperCase());
                    tf.setBorder(null);
                } else {
                    tf.setBorder(Border.stroke(Color.RED));
                }
            }
        });
    }

    public List<String> getCurrentHandStr() {
        List<String> handStr = new ArrayList<>(Hand.SIZE);
        for (Letters letter : currentHand.state) {
            handStr.add(letter.toString());
        }
        return handStr;
    }

    public Hand getCurrentHand() {
        return currentHand;
    }

    public void fillHandWithRandom() {
        for (int i = 0; i < Hand.SIZE; i++) {
            if (currentHand.newState[i] == null) {
                List<Letters> letterList = new ArrayList<>(200);
                currentBag.keySet().stream()
                        .filter(key -> currentBag.get(key) > 0).forEach(letter -> {
                            letterList.addAll(Collections.nCopies(currentBag.get(letter), letter));
                        });
                if (letterList.size() > 0) {
                    Letters letter = letterList.get(random.nextInt(letterList.size()));
                    currentHand.newState[i] = letter;
                    currentBag.put(letter, currentBag.get(letter) - 1);
                }
            }
        }

        // TODO : Make clear user indication when bag is empty
        for (int i = 0; i < Hand.SIZE; i++) {
            if (currentHand.newState[i] == null) {
                currentHand.newState[i] = Letters.__;
            }
        }
    }

    public void removeFromBag(Letters letter) throws BagException {
        if (currentBag.get(letter) > 0) {
            currentBag.put(letter, currentBag.get(letter) - 1);
        } else {
            throw new BagException("Letter cannot be removed!");
        }
    }

    public void resetBag() {
        this.currentBag = new HashMap<>(bag);
    }

    public Map<Letters, Integer> getCurrentBag() {
        return currentBag;
    }

    public void setCurrentBag(Map<Letters, Integer> currentBag) {
        this.currentBag = currentBag;
    }

    public void resetHand() {
        for (int i = 0; i < currentHand.state.length; i++) {
            currentHand.state[i] = null;
            currentHand.newState[i] = null;
        }
    }
}
