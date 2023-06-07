package net.tsbe.tetrominoes.models.tetrominoes;

import net.tsbe.tetrominoes.models.Tetrominoe;

public class Uniominoe extends Tetrominoe {

    @Override
    public char[] getShape() {
        return new char[] {
                'U'
        };
    }

    @Override
    public int getAmountOfCells() {
        return 1;
    }

    @Override
    public int getWidth() {
        return 1;
    }

    @Override
    public int getHeight() {
        return 1;
    }
}
