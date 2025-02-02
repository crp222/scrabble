package hu.simontamas.scrabble.utils;

import hu.simontamas.scrabble.model.AiResult;
import hu.simontamas.scrabble.model.Board;
import hu.simontamas.scrabble.service.BoardService;
import hu.simontamas.scrabble.service.HandService;
import hu.simontamas.scrabble.service.wordService.WordsService;
import javafx.concurrent.Task;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AiSearchTask extends Task<AiResult> {
    protected final HandService handService;
    protected final Board board;
    protected final WordsService wordsService;
    protected final BoardService boardService;

    @Override
    protected abstract AiResult call() throws Exception;
}
