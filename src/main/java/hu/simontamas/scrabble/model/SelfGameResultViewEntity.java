package hu.simontamas.scrabble.model;

import hu.simontamas.scrabble.enums.Letters;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SelfGameResultViewEntity {
    @Getter
    @Setter
    @Builder
    public static class SelfGameResultState {
        private Board board;
        private Hand hand;
        private AiResult aiResult;
        private ValidationResult validationResult;
        private Long time;
        private Map<Letters, Integer> bag;
    }

    List<SelfGameResultState> states = new ArrayList<>();

    String aiName;
}
