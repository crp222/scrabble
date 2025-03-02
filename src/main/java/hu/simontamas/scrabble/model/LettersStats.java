package hu.simontamas.scrabble.model;

import hu.simontamas.scrabble.enums.Letters;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class LettersStats implements Serializable {
    @Getter
    @Setter
    public static final class LetterStat implements Serializable {
        private final Letters letter;
        private Integer usedTimes = 0;
        private Long averageThinkingTimeWhenPresent;

        private LetterStat(Letters letters) {
            this.letter = letters;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof LetterStat) {
                return ((LetterStat) obj).letter == letter;
            }
            return super.equals(obj);
        }
    }

    private Set<LetterStat> stats = new HashSet<>();
    private List<Letters> hand = new ArrayList<>();
    private Long thinkingTime;

    public void addLetterStat(Letters letter) {
        LetterStat stat = new LetterStat(letter);
        if (stats.contains(stat)) {
            stat.setUsedTimes(stat.usedTimes + 1);
            stat.setAverageThinkingTimeWhenPresent((stat.averageThinkingTimeWhenPresent + thinkingTime) / 2);
        } else {
            stat.setUsedTimes(0);
            stat.setAverageThinkingTimeWhenPresent(thinkingTime);
            stats.add(stat);
        }
    }
}
