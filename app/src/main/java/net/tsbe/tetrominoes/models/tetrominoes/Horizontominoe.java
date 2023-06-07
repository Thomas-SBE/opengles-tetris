package net.tsbe.tetrominoes.models.tetrominoes;

import net.tsbe.tetrominoes.models.Tetrominoe;

public class Horizontominoe extends Tetrominoe {

    @Override
    public char[] getShape() {
        return new char[] {
                'H',
                'H',
                'H',
        };
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 1;
    }

    @Override
    public int getAmountOfCells() {
        return 3;
    }
}
