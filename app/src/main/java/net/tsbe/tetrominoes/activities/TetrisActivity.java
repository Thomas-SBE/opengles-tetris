package net.tsbe.tetrominoes.activities;

import android.app.Activity;
import android.graphics.Point;
import android.graphics.Typeface;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import net.tsbe.tetrominoes.R;
import net.tsbe.tetrominoes.examples.MyGLSurfaceView;
import net.tsbe.tetrominoes.gles.GLESSurfaceView;
import net.tsbe.tetrominoes.gles.TetrominoesShaders;
import net.tsbe.tetrominoes.models.mechanics.Clock;

public class TetrisActivity extends Activity {

    // le conteneur View pour faire du rendu OpenGL
    private GLESSurfaceView mGLView;
    private TextView scoreView;

    private ConstraintLayout layout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TetrominoesShaders.INIT(this);

        /* Pour le plein écran */
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        layout = new ConstraintLayout(this);
        layout.setId(View.generateViewId());
        ConstraintSet setScore = new ConstraintSet();

        int gwidth = 20;
        int gheight = 20;

        if(getIntent() != null && getIntent().hasExtra("DIM")){
            String[] values = getIntent().getStringExtra("DIM").split(";");
            gwidth = Integer.parseInt(values[0]);
            gheight = Integer.parseInt(values[1]);
        }

        boolean cblock = getIntent().hasExtra("CBLOCK");

        mGLView = new GLESSurfaceView(this, gwidth, gheight, cblock);
        mGLView.setId(View.generateViewId());

        scoreView = new TextView(this);
        scoreView.setId(View.generateViewId());

        mGLView.setScoreTextView(scoreView);

        System.out.println("ID: " + scoreView.getId());
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);

        System.out.println("WIDTH: " + size.x);

        scoreView.setTextSize(size.x / 40.0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            scoreView.setTypeface(Typeface.create(null, 700, false));
        }
        scoreView.setText("SCORE");

        layout.addView(mGLView);
        layout.addView(scoreView);

        setScore.clone(layout);
        setScore.connect(scoreView.getId(), ConstraintSet.LEFT, layout.getId(), ConstraintSet.LEFT, 35);
        setScore.connect(scoreView.getId(), ConstraintSet.TOP, layout.getId(), ConstraintSet.TOP, 35);
        setScore.applyTo(layout);

        /* Définition de View pour cette activité */
        setContentView(layout);
    }
}
