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

    protected static boolean DEVMODE = true;

    protected final HandService handService;
    protected final WordsService wordsService;
    protected final BoardService boardService;

    @Override
    protected AiResult call() throws Exception {
        return callAi();
    };

    public abstract AiResult callAi() throws Exception;

    protected void printDevmode() {
        try {
            if (DEVMODE) {
                System.out.println();
                System.out.println();
                BoardUtils.printBoard(boardService.getBoard());
                Thread.sleep(100);
            }
        } catch (Exception err) {
            err.printStackTrace();
            throw new RuntimeException("Failed to sleep in devmode!");
        }
    }

    protected void resetState() {
        for (int i = 0; i < Board.SIZE * Board.SIZE; i++) {
            boardService.getBoard().newState[i] = boardService.getBoard().state[i];
        }
    }

    public abstract void setUpFastestSearch();
}
