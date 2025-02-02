package hu.simontamas.scrabble.threads;

import hu.simontamas.scrabble.model.AiResult;
import hu.simontamas.scrabble.service.BoardService;
import hu.simontamas.scrabble.service.HandService;
import hu.simontamas.scrabble.service.wordService.NumberBasedWordsService;
import hu.simontamas.scrabble.service.wordService.WordsService;

public class NumberBasedBruteForceSearch extends BruteForceSearch {
    public NumberBasedBruteForceSearch(HandService handService, WordsService wordsService, BoardService boardService) {
        super(handService, wordsService, boardService);
    }

    @Override
    protected AiResult call() throws Exception {
        wordsService.setType(NumberBasedWordsService.class);
        AiResult aiResult = new AiResult();
        search(board.state, aiResult);
        return aiResult;
    }

}
