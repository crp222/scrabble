package hu.simontamas.scrabble.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SelfGameResult {
    private List<List<String>> hands = new ArrayList<>();
}
