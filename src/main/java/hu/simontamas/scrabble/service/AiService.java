package hu.simontamas.scrabble.service;

import hu.simontamas.scrabble.exceptions.AiException;
import hu.simontamas.scrabble.model.AiResult;
import hu.simontamas.scrabble.service.wordService.WordsService;
import hu.simontamas.scrabble.utils.AiSearchTask;
import hu.simontamas.scrabble.utils.BoardUtils;
import hu.simontamas.scrabble.utils.HandUtils;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Slf4j
@RequiredArgsConstructor
@Service
public class AiService {

    private final HandService handService;
    private final WordsService wordsService;
    private final BoardService boardService;
    private final ThreadService threadService;

    public void runAi(final Class<? extends AiSearchTask> aiSearchClass, VBox foundWords) {
        runAi(aiSearchClass, foundWords, null);
    }

    public void runAi(final Class<? extends AiSearchTask> aiSearchClass, VBox foundWords, Function<Void, Void> resultClickCallback) {
        try {
            AiSearchTask searchTask = aiSearchClass.getDeclaredConstructor(HandService.class, WordsService.class, BoardService.class)
                    .newInstance(handService, wordsService, boardService);
            Label label = new Label("...");
            label.setText("...");
            foundWords.getChildren().add(label);
            threadService.runTask(searchTask, res -> {
                foundWords.getChildren().clear();
                foundWords.setMaxHeight(500);

                Button button = new Button();
                button.setText("Reset");
                button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                    HandUtils.resetHandNewState(handService.getCurrentHand());
                    BoardUtils.resetBoard(boardService.getBoard());
                    resultClickCallback.apply(null);
                });
                foundWords.getChildren().add(button);


                for (AiResult.AiResultWord word : res.getWords()) {
                    button = new Button();
                    button.setText("Found: " + word.getWord());

                    button.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
                        HandUtils.resetHandNewState(handService.getCurrentHand());
                        BoardUtils.fillInWord(word.getPositions(), word.getLetters(), boardService.getBoard());
                        HandUtils.removeFromNewHand(word.getUsedLetters(), handService.getCurrentHand());
                        resultClickCallback.apply(null);
                    });

                    foundWords.getChildren().add(button);
                }
                return null;
            });
        } catch (AiException aiException) {
            Label label = new Label("...");
            label.setText("...");
            foundWords.getChildren().add(label);
            log.error(aiSearchClass.getName() + " search failed!");
        } catch (Exception exception) {
            exception.printStackTrace();
            log.error(aiSearchClass.getName() + " search failed!");
        }
    }

}