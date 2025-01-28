package hu.simontamas.scrabble;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;

public class ScrabbleApplication extends Application {

    private static Stage stage;
    @Override
    public void start(Stage stage) throws IOException {
        ScrabbleApplication.stage = stage;
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.scan("hu.simontamas.scrabble");
        context.refresh();

        FXMLLoader fxmlLoader = new FXMLLoader(ScrabbleApplication.class.getResource("view/main_view.fxml"));
        fxmlLoader.setControllerFactory(context::getBean);
        Scene scene = new Scene(fxmlLoader.load(), 1100, 800);
        stage.setTitle("Scrabble!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    public static Stage getStage() {
        return stage;
    }
}