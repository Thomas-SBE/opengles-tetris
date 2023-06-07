package net.tsbe.tetrominoes.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import net.tsbe.tetrominoes.R;

public class HomeActivity extends AppCompatActivity {

    TextView maxScore;
    Button startGame;
    Button startGameNormal;

    Button cblock10;
    Button cblock20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        maxScore = findViewById(R.id.maxScoreDisplay);
        startGame = findViewById(R.id.startGame);
        startGameNormal = findViewById(R.id.startGameNormal);
        cblock10 = findViewById(R.id.startGameBlock10);
        cblock20 = findViewById(R.id.startGameBlock20);

        getSupportActionBar().hide();

        SharedPreferences prefs = getSharedPreferences("PREFS", MODE_PRIVATE);
        if(prefs.contains("HIGHSCORE")){
            int value = prefs.getInt("HIGHSCORE", 0);
            maxScore.setText(String.format("Vous avez un score maximal de: %d", value));
        }else{
            maxScore.setText("Vous n'avez pas encore de score maximal.");
        }

        startGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ng = new Intent(getApplicationContext(), TetrisActivity.class);
                ng.putExtra("DIM", "20;20");
                startActivity(ng);
            }
        });

        startGameNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ng = new Intent(getApplicationContext(), TetrisActivity.class);
                ng.putExtra("DIM", "10;20");
                startActivity(ng);
            }
        });

        cblock20.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ng = new Intent(getApplicationContext(), TetrisActivity.class);
                ng.putExtra("DIM", "20;20");
                ng.putExtra("CBLOCK", 1);
                startActivity(ng);
            }
        });

        cblock10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ng = new Intent(getApplicationContext(), TetrisActivity.class);
                ng.putExtra("DIM", "10;20");
                ng.putExtra("CBLOCK", 1);
                startActivity(ng);
            }
        });

    }


}