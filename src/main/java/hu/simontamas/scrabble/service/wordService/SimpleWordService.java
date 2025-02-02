package hu.simontamas.scrabble.service.wordService;

import hu.simontamas.scrabble.service.IWordService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;


public class SimpleWordService extends IWordService {

    private final Set<String> words = new HashSet<>();

    public SimpleWordService() {
        try {
            loadWords();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public Set<String> getWords() {
        return words;
    }

    public void loadWords() throws IOException {
        File file = new File(".\\src\\main\\resources\\hu\\simontamas\\scrabble\\assets\\dictionary.txt");
        InputStream inputStream = new FileInputStream(file);
        Scanner scanner = new Scanner(inputStream);
        while(scanner.hasNext()) {
            words.add(scanner.nextLine());
        }
    }

    public boolean wordExist(String word) {
        return words.contains(word);
    }

    public List<String> wordsIncludingSecInPosition(int length, String sec, int position) {
        List<String> result = new ArrayList<>();

        for (String word : words) {
            // Check if the word length matches
            if (word.length() == length) {
                // Check if the substring `sec` appears at the specified position
                if (position >= 0 && position + sec.length() <= word.length()
                        && word.substring(position, position + sec.length()).equals(sec)) {
                    result.add(word);
                }
            }
        }

        return result;
    }

    public List<String> wordsIncludingSecInPosition(String[] hand, int length, String sec, int position) {
        List<String> result = new ArrayList<>();

        List<String> availableLetters = new ArrayList<>(Arrays.stream(hand).toList());
        availableLetters.addAll(Arrays.stream(sec.split("")).toList());

        for (String word : words) {
            if (word.length() == length) {
                if (position >= 0 && position + sec.length() <= word.length()
                        && word.substring(position, position + sec.length()).equals(sec)) {
                    if (canFormWord(word, availableLetters)) {
                        result.add(word);
                    }
                }
            }
        }

        return result;
    }

    private boolean canFormWord(String word, List<String> availableLetters) {
        Map<String, Integer> handCount = new HashMap<>();

        for (String letter : availableLetters) {
            handCount.put(letter, handCount.getOrDefault(letter, 1));
        }

        for (String c : word.split("")) {
            if (!handCount.containsKey(c) || handCount.get(c) == 0) {
                return false;
            }
            handCount.put(c, handCount.get(c) - 1);
        }

        return true;
    }
}
