package hu.simontamas.scrabble.model;

import hu.simontamas.scrabble.enums.Letters;

public class Hand {

    public static int SIZE = 7;

    public Letters[] state = new Letters[SIZE];

    public Letters[] newState = new Letters[SIZE];
}
