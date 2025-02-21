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

    protected static boolean DEVMODE = false;

    protected final HandService handService;
    protected final Board board;
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
                for (int row = 0; row < Board.SIZE; row++) {
                    for (int col = 0; col < Board.SIZE; col++) {
                        if (board.newState[col * Board.SIZE + row] != null) {
                            System.out.print(board.newState[col * Board.SIZE + row].toString() + "|");
                        } else {
                            System.out.print(" |");
                        }
                    }
                    System.out.println();
                }
                Thread.sleep(100);
            }
        } catch (Exception err) {
            err.printStackTrace();
            throw new RuntimeException("Failed to sleep in devmode!");
        }
    }

    protected void resetState() {
        for (int i = 0; i < Board.SIZE * Board.SIZE; i++) {
            board.newState[i] = board.state[i];
        }
    }

    public abstract void setUpFastestSearch();
}
