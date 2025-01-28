package hu.simontamas.scrabble.enums;

public enum TileTypes {

    CENTER("C "),
    DEFAULT("  "),
    DOUBLE_LETTER("DL"),
    TRIPLE_LETTER("TL"),
    DOUBLE_WORD("DW"),
    TRIPLE_WORD("TW");

    public final String value;
    TileTypes(String value) {
        this.value = value;
    }
}
