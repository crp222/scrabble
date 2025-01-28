package hu.simontamas.scrabble.config;

import hu.simontamas.scrabble.enums.Letters;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class Constants {
    public static final int TILE_SIZE = 37;

    public static final Set<String> LETTERS = Arrays.stream(Letters.values()).map(Enum::toString).collect(Collectors.toSet());

}
