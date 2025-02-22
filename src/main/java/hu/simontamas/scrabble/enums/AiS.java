package hu.simontamas.scrabble.enums;

import hu.simontamas.scrabble.threads.BruteForceIndexingSearch;
import hu.simontamas.scrabble.threads.BruteForceSearch;
import hu.simontamas.scrabble.threads.ExactMatchingSearch;
import hu.simontamas.scrabble.utils.AiSearchTask;

import java.io.Serializable;

public enum AiS implements Serializable {
    BRUTE_FORCE("Brute Force Search", BruteForceSearch.class),
    INDEXING_BRUTE_FORCE("Brute Force With Indexing", BruteForceIndexingSearch.class),

    MATCHING_SEARCH("Matching Search", ExactMatchingSearch.class);

    public String name;
    public Class<? extends AiSearchTask> type;

    AiS(String name, Class type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return name;
    }
}
