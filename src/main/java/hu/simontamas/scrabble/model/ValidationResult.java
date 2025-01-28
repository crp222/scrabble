package hu.simontamas.scrabble.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ValidationResult {

    private int previousScore = 0;
    private int newScore = 0;
    private List<String> addedWords = new ArrayList<>();

    private final List<String> errors = new ArrayList<>();

    public void addError(String error) {
        errors.add(error);
    }
}
