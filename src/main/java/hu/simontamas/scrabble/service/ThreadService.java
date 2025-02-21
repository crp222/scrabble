package hu.simontamas.scrabble.service;

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import org.springframework.stereotype.Service;

import java.util.function.Function;

@Service
public class ThreadService {

    public <I,R> void runTask(Task<I> task, Function<I, R> onSucceed) {
        long startTime = System.currentTimeMillis();
        Thread thread = new Thread(task);
        thread.start();
        task.setOnSucceeded(event -> {
            try {
                printExecutionTime(task, startTime, System.currentTimeMillis());
                onSucceed.apply(task.get());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });

        task.setOnFailed(event -> {
            System.err.println("Task failed: " + task.toString());
            if (task.getException() != null) {
                task.getException().printStackTrace();
                throw new RuntimeException();
            }
        });
    }

    private <R> void printExecutionTime(Task<R> task, long start, long end) {
        System.out.println("Task named: " + task.toString() + " execution time: " + (end - start));
    }
}
