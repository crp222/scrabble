package hu.simontamas.scrabble.service;

import hu.simontamas.scrabble.config.Constants;
import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.model.Board;
import hu.simontamas.scrabble.model.Hand;
import hu.simontamas.scrabble.viewElements.LetterInputField;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.springframework.stereotype.Service;

import java.util.Random;

import static hu.simontamas.scrabble.config.Constants.LETTERS;

@Service
public class HandService {
    private final Hand currentHand = new Hand();

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
            if(currentHand.newState[i] != currentHand.state[i]) {
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

    public String[] getCurrentHandStr() {
        String[] handStr = new String[Hand.SIZE];
        int i = 0;
        for (Letters letter : currentHand.state) {
            handStr[i] = letter.toString();
            i++;
        }
        return handStr;
    }

    public Hand getCurrentHand() {
        return currentHand;
    }

    public void fillHandWithRandom() {
        for (int i = 0; i < Hand.SIZE; i++) {
            if (currentHand.newState[i] == null) {
                Letters letter = Letters.values()[random.nextInt(Letters.values().length)];
                currentHand.newState[i] = letter;
            }
        }
    }
}
