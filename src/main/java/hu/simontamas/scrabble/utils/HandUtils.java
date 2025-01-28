package hu.simontamas.scrabble.utils;

import hu.simontamas.scrabble.enums.Letters;
import hu.simontamas.scrabble.model.Hand;

import java.util.List;

public class HandUtils {

    public static void removeFromNewHand(List<Letters> letters, Hand hand) {
        updateHandState(hand);
        Letters[] handLetters = hand.newState;

        for (int i = 0; i < handLetters.length; i++) {
            for (Letters letter : letters) {
                if (handLetters[i] != null && handLetters[i].equals(letter)) {
                    handLetters[i] = null;
                    break;
                }
            }
        }
    }

    public static void updateHandState(Hand hand) {
        for (int i = 0; i < Hand.SIZE; i++) {
            hand.state[i] = hand.newState[i];
        }
    }

    public static void resetHandNewState(Hand hand) {
        for (int i = 0; i < Hand.SIZE; i++) {
            hand.newState[i] = hand.state[i];
        }
    }
}
