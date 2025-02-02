package hu.simontamas.scrabble.service;

import java.io.IOException;
import java.util.*;

public abstract class IWordService {
    protected final Set<String> words = new HashSet<>();

    public abstract Set<String> getWords();

    public abstract void loadWords() throws IOException;

    public abstract boolean wordExist(String word);

    public abstract List<String> wordsIncludingSecInPosition(int length, String sec, int position);

    public abstract List<String> wordsIncludingSecInPosition(String[] hand, int length, String sec, int position);
}
