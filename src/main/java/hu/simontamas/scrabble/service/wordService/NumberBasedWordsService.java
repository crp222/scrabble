package hu.simontamas.scrabble.service.wordService;

import hu.simontamas.scrabble.service.IWordService;

import java.io.IOException;
import java.util.List;
import java.util.Set;


public class NumberBasedWordsService extends IWordService {
    @Override
    public Set<String> getWords() {
        return null;
    }

    @Override
    public void loadWords() throws IOException {

    }

    @Override
    public boolean wordExist(String word) {
        return false;
    }

    @Override
    public List<String> wordsIncludingSecInPosition(int length, String sec, int position) {
        return null;
    }

    @Override
    public List<String> wordsIncludingSecInPosition(String[] hand, int length, String sec, int position) {
        return null;
    }
}
