package hu.simontamas.scrabble.service.wordService;

import hu.simontamas.scrabble.service.IWordService;
import hu.simontamas.scrabble.utils.WordUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class IndexingWordsService extends IWordService {

    private final Map<String, TreeMap<Integer, List<String>>> dictionaryMap = new HashMap<>();

    public IndexingWordsService() {
        try {
            loadWords();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadWords() throws IOException {
        File file = new File(".\\src\\main\\resources\\hu\\simontamas\\scrabble\\assets\\dictionary.txt");
        InputStream inputStream = new FileInputStream(file);
        Scanner scanner = new Scanner(inputStream);
        while (scanner.hasNext()) {
            String next = scanner.next();
            List<String> list = words.getOrDefault(next.length(), new ArrayList<String>());
            list.add(next);
            words.put(next.length(), list);
            putInDicMap(next);
        }
    }

    private void putInDicMap(String word) {
        for (int i = 0; i < word.length(); i++) {
            String key = i + "-" + word.charAt(i);
            if (!dictionaryMap.containsKey(key)) {
                dictionaryMap.put(key, new TreeMap<>());
            }
            if (!dictionaryMap.get(key).containsKey(word.length())) {
                dictionaryMap.get(key).put(word.length(), new ArrayList<>());
            }
            dictionaryMap.get(key).get(word.length()).add(word);
        }
    }

    @Override
    public List<String> wordsIncludingSecInPosition(List<String> hand, String sec, int position) {
        List<String> result = new ArrayList<>();

        List<String> availableLetters = new ArrayList<>(hand);
        availableLetters.addAll(Arrays.stream(sec.split("")).toList());

        for (int i = 0; i < sec.length(); i++) {
            Map<Integer, List<String>> m = dictionaryMap.get(position + i + "-" + sec.charAt(i));
            if (m != null) {
                for (Map.Entry<Integer, List<String>> entry : m.entrySet()) {
                    for (String word : entry.getValue()) {
                        if (WordUtils.canFormWord(word, availableLetters)) {
                            result.add(word);
                        }
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<String> wordsIncludingSec(List<String> hand, String sec) {
        return wordsIncludingSecInPosition(hand, sec, 0);
    }
}
