package hu.simontamas.scrabble.model;

import hu.simontamas.scrabble.enums.Letters;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AiResult {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class AiResultWord {
        private String word;
        private Letters[] letters;
        private List<Letters> usedLetters;
        private int score;
        private List<String> positions;

        @Override
        public String toString() {
            return word + " - " + score;
        }
    }

    private List<AiResultWord> words = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(var res : words) {
            stringBuilder.append(res.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
