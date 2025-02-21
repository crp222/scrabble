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
        services.put(SimpleWordService.class, new SimpleWordService());
        services.put(IndexingWordsService.class, new IndexingWordsService());
    }

    @Override
    public Map<Integer, List<String>> getWords() {
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
    public List<String> wordsIncludingSecInPosition(List<String> hand, String sec, int position) {
        return services.get(type).wordsIncludingSecInPosition(hand, sec, position);
    }

    @Override
    public List<String> wordsIncludingSec(List<String> hand, String sec) {
        return services.get(type).wordsIncludingSec(hand, sec);
    }

    @Override
    public List<String> wordsIncludingSec(List<String> hand, List<String> sec) {
        return services.get(type).wordsIncludingSec(hand, sec);
    }

    public Class<? extends IWordService> getType() {
        return type;
    }

    public void setType(Class<? extends IWordService> type) {
        this.type = type;
    }
}
