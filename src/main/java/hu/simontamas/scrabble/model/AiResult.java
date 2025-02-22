package hu.simontamas.scrabble.model;

import hu.simontamas.scrabble.enums.Letters;
import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class AiResult implements Serializable {
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class AiResultWord implements Serializable{
        private String word;
        private List<Letters> usedLetters;
        private int score;
        private List<String> positions;

        @Override
        public String toString() {
            return word + " - " + score;
        }
    }

    private List<AiResultWord> words = new ArrayList<>();

    public List<String> getStrWords() {
        List<String> res = new ArrayList<>();
        for(AiResultWord result : words) {
            res.add(result.getWord());
        }
        return res;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for(var res : words) {
            stringBuilder.append(res.toString()).append("\n");
        }
        return stringBuilder.toString();
    }
}
