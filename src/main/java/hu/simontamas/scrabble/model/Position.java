package hu.simontamas.scrabble.model;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Position {
    public int x;
    public int y;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Position other = (Position) obj;
        return x == other.x && y == other.y;
    }

    @Override
    public String toString() {
        return x + " - " + y;
    }
}
