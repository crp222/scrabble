package hu.simontamas.scrabble.service.wordService;

import hu.simontamas.scrabble.service.IWordService;
import hu.simontamas.scrabble.utils.WordUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


public class SimpleWordService extends IWordService {

    public SimpleWordService() {
        try {
            loadWords();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> wordsIncludingSecInPosition(List<String> hand, String sec, int position) {
        List<String> result = new ArrayList<>();

        List<String> availableLetters = new ArrayList<>(hand);
        availableLetters.addAll(Arrays.stream(sec.split("")).toList());

        for (Map.Entry<Integer, List<String>> entry : words.entrySet()) {
            for (String word : entry.getValue()) {
                if (position >= 0 && position + sec.length() <= word.length()
                        && word.substring(position, position + sec.length()).equals(sec)) {
                    if (WordUtils.canFormWord(word, availableLetters)) {
                        result.add(word);
                    }
                }
            }

        }

        return result;
    }

    @Override
    public List<String> wordsIncludingSec(List<String> hand, String sec) {
        List<String> result = new ArrayList<>();

        List<String> availableLetters = new ArrayList<>(hand);
        availableLetters.addAll(Arrays.stream(sec.split("")).toList());

        for (Map.Entry<Integer, List<String>> entry : words.entrySet()) {
            for (String word : entry.getValue()) {
                if (WordUtils.canFormWord(word, availableLetters)) {
                    result.add(word);
                }
            }
        }

        return result;
    }

    @Override
    public List<String> wordsIncludingSec(List<String> hand, List<String> sec) {
        List<String> result = new ArrayList<>();

        sec.forEach(s -> {
            List<String> availableLetters = new ArrayList<>(hand);
            availableLetters.addAll(Arrays.stream(s.split("")).toList());

            for (Map.Entry<Integer, List<String>> entry : words.entrySet()) {
                for (String word : entry.getValue()) {
                    if (WordUtils.canFormWord(word, availableLetters)) {
                        result.add(word);
                    }
                }
            }
        });

        return result;
    }
}
