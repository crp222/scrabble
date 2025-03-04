package hu.simontamas.scrabble.utils;

import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.model.Board;

import java.util.*;

public class BoardUtils {
    public static List<String> getNeighboringPositions(int row, int col) {
        ArrayList<String> neighbors = new ArrayList<>();
        // Down neighbor
        String downNeighbor = (row + 1) + "-" + col;
        if (row + 1 < Board.SIZE) {
            neighbors.add(downNeighbor);
        }
        // Up neighbor
        String upNeighbor = (row - 1) + "-" + col;
        if (row - 1 >= 0) {
            neighbors.add(upNeighbor);
        }
        // Right neighbor
        String rightNeighbor = row + "-" + (col + 1);
        if (col + 1 < Board.SIZE) {
            neighbors.add(rightNeighbor);
        }
        // Left neighbor
        String leftNeighbor = row + "-" + (col - 1);
        if (col - 1 >= 0) {
            neighbors.add(leftNeighbor);
        }
        return neighbors;
    }

    public static Set<String> getLetterPositions(Letters[] state) {
        Set<String> positions = new HashSet<>();
        LinkedList<String> list = new LinkedList<>();
        list.add("7-7"); // adding the center
        positions.add("7-7"); // adding the center
        // starting a bfs from the center to map out the placed letters
        while (!list.isEmpty()) {
            String currentStr = list.pop();
            positions.add(currentStr);
            String[] current = currentStr.split("-");
            int currentRow = Integer.parseInt(current[0]);
            int currentCol = Integer.parseInt(current[1]);
            if (state[Board.SIZE * currentRow + currentCol] == null)
                continue;
            for(var neighbour : BoardUtils.getNeighboringPositions(currentRow, currentCol)){
                if(!positions.contains(neighbour)) {
                    list.add(neighbour);
                }
            }
        }
        return positions;
    }

    public static Letters getLetter(int row, int col, Letters[] state) {
        return state[row * Board.SIZE + col];
    }

    public static void fillInWord(List<String> positions, List<Letters> letters, Board board) {
        resetBoard(board);

        int k = 0;
        for(String position : positions) {
            String[] positionParts = position.split("-");
            int row = Integer.parseInt(positionParts[0]);
            int col =  Integer.parseInt(positionParts[1]);
            int i = row * Board.SIZE + col;
            if(board.state[i] == null) {
                board.newState[i] = letters.get(k);
            }
            k++;
        }
    }

    public static void resetBoard(Board board) {
        for (int i = 0; i < board.state.length; i++) {
            board.newState[i] = board.state[i];
        }
    }

    public static void printBoard(Board board) {
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
    }

    public static void printBoardState(Board board) {
        for (int row = 0; row < Board.SIZE; row++) {
            for (int col = 0; col < Board.SIZE; col++) {
                if (board.state[col * Board.SIZE + row] != null) {
                    System.out.print(board.state[col * Board.SIZE + row].toString() + "|");
                } else {
                    System.out.print(" |");
                }
            }
            System.out.println();
        }
    }
}
