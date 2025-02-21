package hu.simontamas.scrabble.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class IWordService {
    protected final Map<Integer, List<String>> words = new TreeMap<>(Comparator.comparingInt(Integer::intValue).reversed());

    public Map<Integer, List<String>> getWords() {
        return words;
    }

    public void loadWords() throws IOException {
        File file = new File(".\\src\\main\\resources\\hu\\simontamas\\scrabble\\assets\\dictionary.txt");
        InputStream inputStream = new FileInputStream(file);
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            String next = scanner.next();
            List<String> list = words.getOrDefault(next.length(), new ArrayList<String>());
            list.add(next);
            words.put(next.length(), list);
        }
    };

    public boolean wordExist(String word) {
        return words.get(word.length()).contains(word);
    }

    public List<String> wordsIncludingSecInPosition(List<String> hand, String sec, int position) {
        return null;
    }

    public List<String> wordsIncludingSec(List<String> hand, String sec) {
        return null;
    }

    public List<String> wordsIncludingSec(List<String> hand, List<String> sec) {
        return null;
    }
}
