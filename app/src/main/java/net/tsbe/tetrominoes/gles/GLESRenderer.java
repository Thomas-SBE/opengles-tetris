package net.tsbe.tetrominoes.gles;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES30;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import net.tsbe.tetrominoes.R;
import net.tsbe.tetrominoes.models.GameGrid;
import net.tsbe.tetrominoes.models.Position;
import net.tsbe.tetrominoes.models.Tetrominoe;
import net.tsbe.tetrominoes.models.tetrominoes.Horizontominoe;
import net.tsbe.tetrominoes.models.tetrominoes.InvertedStairTetrominoe;
import net.tsbe.tetrominoes.models.tetrominoes.InvertedTTetrominoe;
import net.tsbe.tetrominoes.models.tetrominoes.StairTetrominoe;
import net.tsbe.tetrominoes.models.tetrominoes.Straightominoe;
import net.tsbe.tetrominoes.models.tetrominoes.TTetrominoe;
import net.tsbe.tetrominoes.models.tetrominoes.Testrominoe;
import net.tsbe.tetrominoes.models.tetrominoes.Uniominoe;

import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class GLESRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "TetrominoesRenderer";

    private final float[] mMVP = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];
    private final float[] mModelMatrix = new float[16];

    public static int textureSlotId = -1;

    UserInterfaceGraphics ui;
    GameGrid playGrid;

    public GLESSurfaceView surface;
    Context ctx;

    private int gWidth = 10;
    private int gHeight = 20;
    private boolean gameOfCBlocks = false;

    public GLESRenderer(Context c){
        ctx = c;
    }
    public GLESRenderer(Context c, int width, int height, boolean isCblock){
        ctx = c; gWidth = width; gHeight = height; gameOfCBlocks = isCblock;
    }

    public void setSurface(GLESSurfaceView surface){ this.surface = surface; }

    @Override
    public void onSurfaceCreated(GL10 gl10, EGLConfig eglConfig) {
        GLESRenderer.textureSlotId = loadTexture(ctx, R.raw.tileset);
        playGrid = new GameGrid(gWidth, gHeight);
        playGrid.setRenderer(this);
        playGrid.placeTetrominoe(new Testrominoe(), new Position(0, 0));
        if(gameOfCBlocks){
            for(int iter = 0; iter < 12; iter++){
                Tetrominoe currentlySelectedTetrominoe;
                int i = new Random().nextInt(8);
                switch (i) {
                    case 0:
                        currentlySelectedTetrominoe = new Testrominoe();
                        break;
                    case 1:
                        currentlySelectedTetrominoe = new StairTetrominoe();
                        break;
                    case 2:
                        currentlySelectedTetrominoe = new Straightominoe();
                        break;
                    case 3:
                        currentlySelectedTetrominoe = new Horizontominoe();
                        break;
                    case 4:
                        currentlySelectedTetrominoe = new InvertedStairTetrominoe();
                        break;
                    case 5:
                        currentlySelectedTetrominoe = new InvertedTTetrominoe();
                        break;
                    case 6:
                        currentlySelectedTetrominoe = new TTetrominoe();
                        break;
                    case 7:
                        currentlySelectedTetrominoe = new Uniominoe();
                        break;
                    default:
                        currentlySelectedTetrominoe = new Testrominoe();
                }
                int rcl = new Random().nextInt(gWidth);
                playGrid.placeTetrominoe(currentlySelectedTetrominoe, new Position(rcl, 0));
            }
        }
        GLES30.glClearColor(250/255.0f, 250/255.0f, 250/255.0f, 1.0f);
        GLES30.glEnable(GLES30.GL_BLEND);
        GLES30.glBlendFunc(GLES30.GL_SRC_ALPHA, GLES30.GL_ONE_MINUS_SRC_ALPHA);
        ui = new UserInterfaceGraphics(gWidth, gHeight);
    }

    @Override
    public void onSurfaceChanged(GL10 gl10, int i, int i1) {
        GLES30.glViewport(0, 0, i, i1);

        float MAX = Math.max(i, i1);
        float MIN = Math.min(i, i1);

        float widthToHeightRatio = MAX / MIN;

        float MAX_GRIDSIDE = Math.max(gWidth, gHeight) * 0.6f;

        if(MAX == i){
            Matrix.orthoM(mProjectionMatrix, 0, -MAX_GRIDSIDE * widthToHeightRatio, MAX_GRIDSIDE * widthToHeightRatio, -MAX_GRIDSIDE, MAX_GRIDSIDE, -1.0f, 1.0f);
        }else{
            Matrix.orthoM(mProjectionMatrix, 0, -MAX_GRIDSIDE, MAX_GRIDSIDE, -MAX_GRIDSIDE * widthToHeightRatio, MAX_GRIDSIDE * widthToHeightRatio, -1.0f, 1.0f);
        }
    }

    @Override
    public void onDrawFrame(GL10 gl10) {
        float[] _temp = new float[16];

        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);
        Matrix.setIdentityM(mViewMatrix, 0);
        Matrix.multiplyMM(mMVP, 0, mProjectionMatrix, 0, mViewMatrix, 0);
        Matrix.setIdentityM(mModelMatrix, 0);
        // TODO: Faire les translations des formes affichées
        Matrix.translateM(mModelMatrix, 0, -gWidth/2.0f, -gHeight/2.0f, 0);
        Matrix.multiplyMM(_temp, 0, mMVP, 0, mModelMatrix, 0);

        ui.draw(_temp);
        playGrid.getGraphics().draw(_temp);
    }

    public static int loadShader(int type, String shaderCode){
        int shader = GLES30.glCreateShader(type);
        GLES30.glShaderSource(shader, shaderCode);
        GLES30.glCompileShader(shader);
        return shader;
    }

    public int loadTexture(Context context, int resourceId){
        final int[] textureHandle = new int[1];
        GLES30.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0)
        {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
            GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textureHandle[0]);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
            GLES30.glTexParameteri(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_NEAREST);
            GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
            bitmap.recycle();
        }

        if (textureHandle[0] == 0)
        {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }
}
