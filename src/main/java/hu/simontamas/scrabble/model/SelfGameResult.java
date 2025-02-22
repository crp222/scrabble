package hu.simontamas.scrabble.model;

import hu.simontamas.scrabble.enums.AiS;
import hu.simontamas.scrabble.enums.Letters;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class SelfGameResult implements Serializable {
    private List<List<String>> hands = new ArrayList<>();
    private List<Map<Letters, Integer>> bag = new ArrayList<>();
    private List<AiResult> results = new ArrayList<>();
    private AiS ai;
    private List<Long> times = new ArrayList<>();
}
