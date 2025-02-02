package hu.simontamas.scrabble.service.wordService;

import hu.simontamas.scrabble.service.IWordService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class WordsService extends IWordService {

    private final Map<Class<? extends IWordService>, IWordService> services;

    private Class<? extends IWordService> type = SimpleWordService.class;

    public WordsService() {
        services = new HashMap<>();
        services.put(NumberBasedWordsService.class, new NumberBasedWordsService());
        services.put(SimpleWordService.class, new SimpleWordService());
    }

    @Override
    public Set<String> getWords() {
        return services.get(type).getWords();
    }

    @Override
    public void loadWords() throws IOException {
        services.get(type).loadWords();
    }

    @Override
    public boolean wordExist(String word) {
        return services.get(type).wordExist(word);
    }

    @Override
    public List<String> wordsIncludingSecInPosition(int length, String sec, int position) {
        return services.get(type).wordsIncludingSecInPosition(length, sec, position);
    }

    @Override
    public List<String> wordsIncludingSecInPosition(String[] hand, int length, String sec, int position) {
        return services.get(type).wordsIncludingSecInPosition(hand, length, sec, position);
    }

    public Class<? extends IWordService> getType() {
        return type;
    }

    public void setType(Class<? extends IWordService> type) {
        this.type = type;
    }
}
