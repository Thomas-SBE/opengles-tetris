package net.tsbe.tetrominoes.models;


import net.tsbe.tetrominoes.R;
import net.tsbe.tetrominoes.gles.TetrominoesShaders;

public abstract class Tetrominoe {

    public static String getVertexShaderCode(){
        return TetrominoesShaders.I().readShaderFileFromResources(R.raw.tetrominoe_vert);
    }

    public static String getFragmentShaderCode(){
        return TetrominoesShaders.I().readShaderFileFromResources(R.raw.tetromonoe_frag);
    }

    public int getAmountOfCells() { return 0; }
    public char[] getShape() { return new char[0]; };
    public int getWidth() { return 0; }
    public int getHeight() { return 0; }

}
