package hu.simontamas.scrabble.viewElements;

import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.text.TextAlignment;

public class LetterInputField extends TextField {
    public LetterInputField(int layoutX, int layoutY, int size, String placeholder) {
        super();
        this.setPromptText(placeholder);
        this.setPrefSize(size, size);
        this.setLayoutX(layoutX);
        this.setLayoutY(layoutY);
        this.setAlignment(Pos.CENTER);
    }
}
