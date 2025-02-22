package hu.simontamas.scrabble.model;

import hu.simontamas.scrabble.enums.Letters;

import java.util.List;

public class Hand {

    public static int SIZE = 7;

    public Letters[] state = new Letters[SIZE];

    public Letters[] newState = new Letters[SIZE];

    public static Hand fromStr(List<String> handStr) {
        if (handStr == null || handStr.size() != SIZE) {
            throw new IllegalArgumentException("Invalid hand size. Expected " + SIZE + " elements.");
        }

        Hand hand = new Hand();
        for (int i = 0; i < SIZE; i++) {
            hand.state[i] = Letters.valueOf(handStr.get(i));
            hand.newState[i] = Letters.valueOf(handStr.get(i));
        }

        return hand;
    }
}
