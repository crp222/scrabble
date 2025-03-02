package hu.simontamas.scrabble.threads;

import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.model.AiResult;
import hu.simontamas.scrabble.service.BoardService;
import hu.simontamas.scrabble.service.HandService;
import hu.simontamas.scrabble.service.wordService.IndexingWordsService;
import hu.simontamas.scrabble.service.wordService.WordsService;
import hu.simontamas.scrabble.utils.WordUtils;

import java.util.List;

public class BruteForceIndexingSearch extends BruteForceSearch {

    public BruteForceIndexingSearch(HandService handService, WordsService wordsService, BoardService boardService) {
        super(handService, wordsService, boardService);
    }

    @Override
    public AiResult callAi() throws Exception {
        this.hookerWords.clear();
        longestFoundWord = 0;
        wordsService.setType(IndexingWordsService.class);
        AiResult aiResult = new AiResult();
        search(boardService.getBoard().state, aiResult);
        return aiResult;
    }

    @Override
    protected void computeResults(AiResult aiResult, int row, int col, String sec, List<String> handLetters, int limit) {
        for (int j = MAX_WORD_LENGTH; j > 0; j--) {
            List<String> words = wordsService.wordsIncludingSecInPosition(handLetters, sec, j);

            for (String word : words) {
                int foundWordCount = 0;

                // TODO: branching when found letter with 2 length for example 'TY'
                List<Letters> letters = WordUtils.strToLetters(word);
                if (tryToFillWord(aiResult, row, col, letters, 0, j)) {
                    foundWordCount++;
                }

                resetState();

                letters = WordUtils.strToLetters(word);
                if (tryToFillWord(aiResult, row, col, letters, 1, j)) {
                    foundWordCount++;
                }

                resetState();

                if (foundWordCount >= limit) {
                    break;
                }
            }
        }
    }

    @Override
    public void setUpFastestSearch() {
        MAX_WORD_COUNT = 5;
    }
}
