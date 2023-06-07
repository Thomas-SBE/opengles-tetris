package net.tsbe.tetrominoes.gles;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.MotionEvent;
import android.widget.TextView;

import net.tsbe.tetrominoes.R;
import net.tsbe.tetrominoes.activities.HomeActivity;
import net.tsbe.tetrominoes.models.GameGrid;
import net.tsbe.tetrominoes.models.GameUpdateable;
import net.tsbe.tetrominoes.models.Position;
import net.tsbe.tetrominoes.models.mechanics.Clock;
import net.tsbe.tetrominoes.models.tetrominoes.Horizontominoe;
import net.tsbe.tetrominoes.models.tetrominoes.InvertedStairTetrominoe;
import net.tsbe.tetrominoes.models.tetrominoes.InvertedTTetrominoe;
import net.tsbe.tetrominoes.models.tetrominoes.StairTetrominoe;
import net.tsbe.tetrominoes.models.tetrominoes.Straightominoe;
import net.tsbe.tetrominoes.models.tetrominoes.TTetrominoe;
import net.tsbe.tetrominoes.models.tetrominoes.Testrominoe;
import net.tsbe.tetrominoes.models.Tetrominoe;
import net.tsbe.tetrominoes.models.tetrominoes.Uniominoe;

import java.util.Random;
import java.util.concurrent.Executors;

public class GLESSurfaceView extends GLSurfaceView implements GameUpdateable {

    private final GLESRenderer mRenderer;
    private static int currentlySelectedColumn = 0;
    private static int currentlySelectedFallingHeight = 1;
    private static Tetrominoe currentlySelectedTetrominoe;

    public static Clock gameClock;
    public static int getCurrentlySelectedColumn() {
        return currentlySelectedColumn;
    }
    public static int getCurrentlySelectedFallingHeight() { return currentlySelectedFallingHeight; }

    public static boolean IS_GAME_RUNNING = false;

    private TextView scoreTextView = null;
    private int score = 0;

    public static Tetrominoe getCurrentlySelectedTetrominoe() {
        return currentlySelectedTetrominoe;
    }

    private int g_width = 20;
    private int g_height = 20;

    public void setScoreTextView(TextView v){ scoreTextView = v; }

    public GLESSurfaceView(Context context, int width, int height, boolean isCblock) {
        super(context);

        setDesiredGridSize(width, height);

        currentlySelectedTetrominoe = new Testrominoe();
        currentlySelectedColumn = 0;
        currentlySelectedFallingHeight = 0;

        mRenderer = new GLESRenderer(getContext(), g_width, g_height, isCblock);
        mRenderer.setSurface(this);

        setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        setEGLContextClientVersion(3);
        setRenderer(mRenderer);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

        IS_GAME_RUNNING = true;
        gameClock = new Clock(this, this);
        gameClock.start();
    }

    private void setDesiredGridSize(int width, int height){
        g_width = width;
        g_height = height;
    }

    public void selectRandomTetrominoe(){
        Tetrominoe old = currentlySelectedTetrominoe;
        while(old.equals(currentlySelectedTetrominoe)){
            int i = new Random().nextInt(8);
            switch (i){
                case 0: currentlySelectedTetrominoe = new Testrominoe(); break;
                case 1: currentlySelectedTetrominoe = new StairTetrominoe(); break;
                case 2: currentlySelectedTetrominoe = new Straightominoe(); break;
                case 3: currentlySelectedTetrominoe = new Horizontominoe(); break;
                case 4: currentlySelectedTetrominoe = new InvertedStairTetrominoe(); break;
                case 5: currentlySelectedTetrominoe = new InvertedTTetrominoe(); break;
                case 6: currentlySelectedTetrominoe = new TTetrominoe(); break;
                case 7: currentlySelectedTetrominoe = new Uniominoe(); break;
                default: currentlySelectedTetrominoe = new Testrominoe();
            }
        }
        currentlySelectedColumn = 0;
    }

    // TODO: TOUCH EVENTS GO HERE
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN){
            if(event.getY() > getHeight() / 2.0f){
                if(event.getX() <= getWidth() / 2.0f) {
                    currentlySelectedColumn = currentlySelectedColumn - 1;
                    if (currentlySelectedColumn < 0)
                        currentlySelectedColumn = 0;
                }else{
                    currentlySelectedColumn = (currentlySelectedColumn+1);
                    if(currentlySelectedColumn+currentlySelectedTetrominoe.getWidth() > mRenderer.playGrid.getWidth()){
                        currentlySelectedColumn = mRenderer.playGrid.getWidth()-currentlySelectedTetrominoe.getWidth();
                    }
                }
            }else{
                mRenderer.playGrid.placeTetrominoe(currentlySelectedTetrominoe, new Position(currentlySelectedColumn, currentlySelectedFallingHeight));
                incrementScore();
                currentlySelectedColumn = 0;
                currentlySelectedFallingHeight = 0;
                selectRandomTetrominoe();
            }
            requestRender();
            // mRenderer.playGrid.debugShowGrid();
        }

        return true;
    }

    private long elapsedDeltaTime = 0;
    private long fallingTetrominoeInterval = 400; // in ms
    @Override
    public void Tick(long deltaTime) {
        elapsedDeltaTime += deltaTime;
        if(elapsedDeltaTime >= fallingTetrominoeInterval){
            elapsedDeltaTime = 0;
            makeTetrominoeFall();
        }
    }

    public void makeTetrominoeFall(){
        GameGrid G = mRenderer.playGrid;

        if(G != null && G.canTetrominoeFall(currentlySelectedTetrominoe, new Position(currentlySelectedColumn, currentlySelectedFallingHeight))){
            currentlySelectedFallingHeight++;
        }else{
            // CANNOT FALL AND IS OVERLAPPING A TETROMINOE ( IF GAME IS OVER ) :(
            if(G.doTetromonoeOverlap(currentlySelectedTetrominoe, new Position(currentlySelectedColumn, currentlySelectedFallingHeight))){
                System.out.println("GAME IS OVER, LOST !");
                gameClock.stopped = true;
                gameClock.interrupt();
                IS_GAME_RUNNING = false;

                new AlertDialog.Builder(getContext())
                    .setTitle("Perdu !")
                    .setCancelable(false)
                    .setMessage("Vous avez placé un total de " + numberOfPlacedTetrominoe + " tétrominoes, pour un score de " + score + " !")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences prefs = getContext().getApplicationContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
                            if(prefs.getInt("HIGHSCORE",0) < score){
                                SharedPreferences.Editor prefedit = prefs.edit();
                                prefedit.putInt("HIGHSCORE", score);
                                prefedit.commit();
                            }

                            Intent intent = new Intent(getContext(), HomeActivity.class);
                            getContext().startActivity(intent);
                        }
                    }).setIcon(android.R.drawable.ic_delete)
                    .show();

            }else{
                incrementScore();
            }

            G.placeTetrominoe(currentlySelectedTetrominoe, new Position(currentlySelectedColumn, currentlySelectedFallingHeight));
            currentlySelectedFallingHeight = 0;
            currentlySelectedColumn = 0;
        }

        G.removeTetrisLines();
        requestRender();
    }

    public void incrementScore(){
        incrementScore(false);
    }

    public int numberOfPlacedTetrominoe = 0;

    public void placedATetrominoe(){
        numberOfPlacedTetrominoe += 1;
        if(fallingTetrominoeInterval > 100){
            fallingTetrominoeInterval -= 4 / 200.0f;
            if(fallingTetrominoeInterval < 100) fallingTetrominoeInterval = 100; // SPEED CLAMP, NOT TOO FAST !
        }
    }

    public void incrementScore(boolean isTetris){
        score += isTetris ? 20 : 1;
        scoreTextView.setText(String.format("SCORE: %d", score));
    }
}
