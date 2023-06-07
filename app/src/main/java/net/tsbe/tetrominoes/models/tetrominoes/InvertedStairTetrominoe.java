package net.tsbe.tetrominoes.models.tetrominoes;

import net.tsbe.tetrominoes.models.Tetrominoe;

public class InvertedStairTetrominoe extends Tetrominoe {

    @Override
    public char[] getShape() {
        return new char[] {
                'I', 'I', ' ',
                ' ', 'I', 'I'
        };
    }

    @Override
    public int getAmountOfCells() {
        return 4;
    }

    @Override
    public int getWidth() {
        return 3;
    }

    @Override
    public int getHeight() {
        return 2;
    }
}
