package net.tsbe.tetrominoes.gles;

import android.content.Context;

import net.tsbe.tetrominoes.R;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class TetrominoesShaders {

    private static TetrominoesShaders INSTANCE = null;
    public static void INIT(Context ctx){ if(INSTANCE==null) INSTANCE = new TetrominoesShaders(ctx); }
    public static TetrominoesShaders I(){ return INSTANCE; }

    private Context ctx;

    private TetrominoesShaders(Context ctx) {
        this.ctx = ctx;
    }

    public String readShaderFileFromResources(int resourceId){
        try{
            InputStream is = ctx.getResources().openRawResource(resourceId);
            return new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
