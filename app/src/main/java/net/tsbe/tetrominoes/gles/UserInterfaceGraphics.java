package net.tsbe.tetrominoes.gles;

import android.opengl.GLES30;

import net.tsbe.tetrominoes.models.Tetrominoe;
import net.tsbe.tetrominoes.utils.ArrayUtils;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class UserInterfaceGraphics {

    public UserInterfaceGraphics(int width, int height) {
        GRID_HEIGHT = height;
        GRID_WIDTH = width;
        init();
    }

    static int GRID_HEIGHT;
    static int GRID_WIDTH;

    static final int COORDS_PER_VERTEX = 3;
    static final int COLORS_PER_VERTEX = 4;

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorsBuffer;
    private IntBuffer indiceBuffer;


    private int[] indexes = {
            // BASE BACKGROUND
            0, 1, 2,
            2, 1, 3,

            // FALLING INDICATOR
            4, 5, 6,
            6, 5, 7
    };
    private float[] vertices;
    private float[] colors = {
            // BASE BACKGROUND
            0.2f, 0.2f, 0.2f, 0.2f,
            0.2f, 0.2f, 0.2f, 0.2f,
            0.2f, 0.2f, 0.2f, 0.2f,
            0.2f, 0.2f, 0.2f, 0.2f,

            // FALLING INDICATOR
            0.2f, 0.2f, 0.2f, 0.2f,
            0.2f, 0.2f, 0.2f, 0.2f,
            0.2f, 0.2f, 0.2f, 0.2f,
            0.2f, 0.2f, 0.2f, 0.2f,
    };

    private final int TOTAL_NUMBER_OF_SQUARES = 2;

    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private final int colorStride = COLORS_PER_VERTEX * 4;

    private int programId;
    int[] linkStatus = {0};
    private int mvpId;
    private int colorId;
    private int positionId;

    public void init(){

        vertices = new float[] {};

        ByteBuffer bb = ByteBuffer.allocateDirect(TOTAL_NUMBER_OF_SQUARES * 4 * 3 * 4); // MAX AMOUNT OF VERTEX (last 4: 4bytes of float / int)
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        ByteBuffer cb = ByteBuffer.allocateDirect(TOTAL_NUMBER_OF_SQUARES * 4 * 4 * 4);
        cb.order(ByteOrder.nativeOrder());
        colorsBuffer = cb.asFloatBuffer();
        colorsBuffer.put(colors);
        colorsBuffer.position(0);

        ByteBuffer ib = ByteBuffer.allocateDirect(TOTAL_NUMBER_OF_SQUARES * 2 * 3 * 4);
        ib.order(ByteOrder.nativeOrder());
        indiceBuffer = ib.asIntBuffer();
        indiceBuffer.put(indexes);
        indiceBuffer.position(0);

        int vertexShader = GLESRenderer.loadShader(GLES30.GL_VERTEX_SHADER, Tetrominoe.getVertexShaderCode());
        int fragShader = GLESRenderer.loadShader(GLES30.GL_FRAGMENT_SHADER, Tetrominoe.getFragmentShaderCode());

        programId = GLES30.glCreateProgram();
        GLES30.glAttachShader(programId, vertexShader);
        GLES30.glAttachShader(programId, fragShader);
        GLES30.glLinkProgram(programId);
        GLES30.glGetProgramiv(programId, GLES30.GL_LINK_STATUS, linkStatus, 0);
    }


    public void draw(float[] mvp){

        // UPDATE-ABLE VERTICES
        vertices = new float[] {
                // GRID BACKGROUND
                0.0f, 0.0f, 0.0f,
                0f, GRID_HEIGHT * 1.0f, 0f,
                GRID_WIDTH * 1.0f, 0f, 0f,
                GRID_WIDTH * 1.0f, GRID_HEIGHT * 1.0f, 0f,
                ////
        };

        float[] indicatorVertices = new float[]{
                // FALLING INDICATOR
                GLESSurfaceView.getCurrentlySelectedColumn(), 0.0f, 0.0f,
                GLESSurfaceView.getCurrentlySelectedColumn(), GRID_HEIGHT * 1.0f, 0.0f,
                GLESSurfaceView.getCurrentlySelectedColumn() + GLESSurfaceView.getCurrentlySelectedTetrominoe().getWidth(), 0.0f, 0.0f,
                GLESSurfaceView.getCurrentlySelectedColumn() + GLESSurfaceView.getCurrentlySelectedTetrominoe().getWidth(), GRID_HEIGHT * 1.0f, 0.0f
        };

        if(GLESSurfaceView.IS_GAME_RUNNING){
            vertices = ArrayUtils.concat(vertices, indicatorVertices);
        }

        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        GLES30.glUseProgram(programId);
        mvpId = GLES30.glGetUniformLocation(programId, "uMVP");
        GLES30.glUniformMatrix4fv(mvpId, 1, false, mvp, 0);
        positionId = GLES30.glGetAttribLocation(programId, "vPosition");
        colorId = GLES30.glGetAttribLocation(programId, "vColor");
        GLES30.glEnableVertexAttribArray(positionId);
        GLES30.glEnableVertexAttribArray(colorId);

        GLES30.glVertexAttribPointer(positionId, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES30.glVertexAttribPointer(colorId, COLORS_PER_VERTEX, GLES30.GL_FLOAT, false, colorStride, colorsBuffer);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexes.length, GLES30.GL_UNSIGNED_INT, indiceBuffer);
        cleanup();
    }

    public void cleanup(){
        GLES30.glDisableVertexAttribArray(positionId);
        GLES30.glDisableVertexAttribArray(colorId);
    }

}
