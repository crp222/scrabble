package hu.simontamas.scrabble.utils;

import hu.simontamas.scrabble.enums.Letters;

import java.util.ArrayList;
import java.util.List;

public class WordUtils {

    public static boolean canFormWord(String word, List<String> availableLetters) {
        List<String> availableLettersCopy = new ArrayList<>(availableLetters);
        for (String c : word.split("")) {
            if (!availableLettersCopy.remove(c)) {
                return false;
            }
        }

        return true;
    }

    public static List<Letters> strToLetters(String str) {
        List<Letters> res = new ArrayList<>();
        for (var c : str.toCharArray()) {
            res.add(Letters.valueOf(c + ""));
        }
        return res;
    }
}
