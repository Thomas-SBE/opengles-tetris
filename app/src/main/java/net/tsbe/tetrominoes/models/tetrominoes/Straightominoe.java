package net.tsbe.tetrominoes.models.tetrominoes;

import net.tsbe.tetrominoes.models.Tetrominoe;

public class Straightominoe extends Tetrominoe {

    @Override
    public char[] getShape() {
        return new char[] {
                'A',
                'A',
                'A',
                'A'
        };
    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 4;
    }

    @Override
    public int getAmountOfCells() {
        return 4;
    }
}
