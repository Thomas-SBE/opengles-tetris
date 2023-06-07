package net.tsbe.tetrominoes.gles;

import android.opengl.GLES30;

import net.tsbe.tetrominoes.models.GameGrid;
import net.tsbe.tetrominoes.models.Tetrominoe;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;

public class GameGridGraphics {

    GameGrid grid;

    public GameGridGraphics(GameGrid grid) {
        this.grid = grid;
        init();
    }

    static final int COORDS_PER_VERTEX = 3;
    static final int COLORS_PER_VERTEX = 4;

    private FloatBuffer vertexBuffer;
    private FloatBuffer colorsBuffer;
    private IntBuffer indiceBuffer;


    private int[] indexes = {
            0, 1, 2,
            2, 1, 3
    };
    private float[] vertices = {
            0.0f, 0.0f, 0.0f,
            0f, 1f, 0f,
            1f, 0f, 0f,
            1f, 1f, 0f
    };
    private float[] colors = {
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
            1f, 0f, 0f, 1f,
    };

    private final int vertexStride = COORDS_PER_VERTEX * 4;
    private final int colorStride = COLORS_PER_VERTEX * 4;

    private int programId;
    int[] linkStatus = {0};
    private int mvpId;
    private int colorId;
    private int positionId;

    public void init(){
        ByteBuffer bb = ByteBuffer.allocateDirect(grid.getWidth()*(grid.getHeight()+2)*4*3*4); // MAX AMOUNT OF VERTEX
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.position(0);

        ByteBuffer cb = ByteBuffer.allocateDirect(grid.getWidth()*(grid.getHeight()+2)*4*3*4);
        cb.order(ByteOrder.nativeOrder());
        colorsBuffer = cb.asFloatBuffer();
        colorsBuffer.position(0);

        ByteBuffer ib = ByteBuffer.allocateDirect(grid.getWidth()* (grid.getHeight()+2)*4*3*4);
        ib.order(ByteOrder.nativeOrder());
        indiceBuffer = ib.asIntBuffer();
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
        GLES30.glUseProgram(programId);
        mvpId = GLES30.glGetUniformLocation(programId, "uMVP");
        GLES30.glUniformMatrix4fv(mvpId, 1, false, mvp, 0);
        positionId = GLES30.glGetAttribLocation(programId, "vPosition");
        colorId = GLES30.glGetAttribLocation(programId, "vColor");
        GLES30.glEnableVertexAttribArray(positionId);
        GLES30.glEnableVertexAttribArray(colorId);

        buffersFromGrid();

        GLES30.glVertexAttribPointer(positionId, COORDS_PER_VERTEX, GLES30.GL_FLOAT, false, vertexStride, vertexBuffer);
        GLES30.glVertexAttribPointer(colorId, COLORS_PER_VERTEX, GLES30.GL_FLOAT, false, colorStride, colorsBuffer);

        GLES30.glDrawElements(GLES30.GL_TRIANGLES, indexes.length, GLES30.GL_UNSIGNED_INT, indiceBuffer);
        cleanup();
    }

    public void buffersFromGrid(){
        char[] G = Arrays.copyOf(grid.getGrid(), grid.getGrid().length);

        // COPY AND PLACE
        int baseOffset = GLESSurfaceView.getCurrentlySelectedColumn();
        int heightOffset = GLESSurfaceView.getCurrentlySelectedFallingHeight();
        Tetrominoe T = GLESSurfaceView.getCurrentlySelectedTetrominoe();
        if(T != null && GLESSurfaceView.IS_GAME_RUNNING){
            for(int i = 0; i < T.getWidth() * T.getHeight(); i++){
                if(T.getShape()[i] != ' ')
                    G[baseOffset+ grid.getWidth()*heightOffset+(i%T.getWidth())+(grid.getWidth()*(i/T.getWidth()))] = '?';
            }
        }

        vertices = new float[(grid.getAmountOfFilledCells()+T.getAmountOfCells()) * 4 * 3];
        colors = new float[(grid.getAmountOfFilledCells()+T.getAmountOfCells()) * 4 * 4];
        indexes = new int[(grid.getAmountOfFilledCells()+T.getAmountOfCells()) * 2 * 3];
        int fill_amount = 0;
        for(int i = 0; i < G.length; i++){
            int line = i/grid.getWidth();
            int offset = i%grid.getWidth();
            char triomino = G[i];

            if(triomino == ' ') continue;

            // FILL A CELL
            vertices[(fill_amount*12)] = 0.0f + offset;
            vertices[(fill_amount*12)+1] = 0.0f + (grid.getHeight()-line-1);
            vertices[(fill_amount*12)+2] = 0.0f;

            vertices[(fill_amount*12)+3] = 0.0f + offset;
            vertices[(fill_amount*12)+4] = 1.0f + (grid.getHeight()-line-1);
            vertices[(fill_amount*12)+5] = 0.0f;

            vertices[(fill_amount*12)+6] = 1.0f + offset;
            vertices[(fill_amount*12)+7] = 0.0f + (grid.getHeight()-line-1);
            vertices[(fill_amount*12)+8] = 0.0f;

            vertices[(fill_amount*12)+9] = 1.0f + offset;
            vertices[(fill_amount*12)+10] = 1.0f + (grid.getHeight()-line-1);
            vertices[(fill_amount*12)+11] = 0.0f;

            // COLOR A CELL
            float[] _clr;
            switch (triomino){
                case 'T': _clr = new float[]{1.0f, 0.0f, 1.0f, 1.0f}; break;
                case 'S': _clr = new float[]{0.0f, 1.0f, 1.0f, 1.0f}; break;
                case '?': _clr = new float[]{0.1f, 0.1f, 0.1f, 1.0f}; break;
                case 'A': _clr = new float[]{0.0f, 1.0f, 0.7f, 1.0f}; break;
                case 'H': _clr = new float[]{0.970f, 0.760f, 0.00f, 1.0f}; break;
                case 'I': _clr = new float[]{0.820f, 0.00f, 0.0547f, 1.0f}; break;
                case '-': _clr = new float[]{0.765f, 0.00f, 0.820f, 1.0f}; break;
                case '+': _clr = new float[]{0.00f, 0.478f, 0.820f, 1.0f}; break;
                case 'U': _clr = new float[]{0.820f, 0.00f, 0.465f, 1.0f}; break;
                default: _clr = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
            }
            for(int v = 0; v < 16; v++) colors[(fill_amount*16)+v] = _clr[v%4];

            // INDEX A CELL
            indexes[(fill_amount*6)] = fill_amount*4;
            indexes[(fill_amount*6)+1] = fill_amount*4+1;
            indexes[(fill_amount*6)+2] = fill_amount*4+2;
            indexes[(fill_amount*6)+3] = fill_amount*4+2;
            indexes[(fill_amount*6)+4] = fill_amount*4+1;
            indexes[(fill_amount*6)+5] = fill_amount*4+3;

            fill_amount++;
        }

        vertexBuffer.clear();
        vertexBuffer.put(vertices);
        vertexBuffer.position(0);

        colorsBuffer.clear();
        colorsBuffer.put(colors);
        colorsBuffer.position(0);

        indiceBuffer.clear();
        indiceBuffer.put(indexes);
        indiceBuffer.position(0);
    }

    public void cleanup(){
        GLES30.glDisableVertexAttribArray(positionId);
        GLES30.glDisableVertexAttribArray(colorId);
    }
}
