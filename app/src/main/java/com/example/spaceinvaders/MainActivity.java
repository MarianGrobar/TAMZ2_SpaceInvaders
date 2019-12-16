package com.example.spaceinvaders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

public class MainActivity extends Activity {

    Game game;
    FrameLayout gameFrame;
    RelativeLayout GameButtons;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        Init();

    }

    @SuppressLint("ResourceType")
    @Override
    protected void onResume() {

        Toast.makeText(this,String.valueOf(game.state),Toast.LENGTH_SHORT).show();
        if (game.state == false){
            Init();
            Gson gson = new Gson();
            SharedPreferences sharedPref = getPreferences(this.MODE_PRIVATE);

            String pl = sharedPref.getString("player", "");
            Player p =  gson.fromJson(pl,Player.class);
            p.Init(this,game.soundPlayer);
            game.player = p;

            for (Upgrade up: p.upgrades.values()){
                up.Init(this,p);
            }

            String objects = sharedPref.getString("gameObjects", "");
            Enemy[] objs = gson.fromJson(objects,Enemy[].class );
            game.enemies.clear();
            Game.gameObjects.clear();
            for ( Enemy o : objs){
                o.Init(this);
                game.enemies.add(o);
                Game.gameObjects.add(o);
            }
            Integer score = sharedPref.getInt("score", 0);
            game.setScore(score);
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this,String.valueOf(game.state),Toast.LENGTH_SHORT).show();

        SharedPreferences sharedPref = getPreferences(this.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("gameObjects", game.getEnemiesJSON());
        editor.putString("player", game.getPlayerJSON());
        editor.putInt("score", game.getScore());
        editor.commit();
        game.releaseResources();
        game.state = false;
    }

    @SuppressLint("ResourceType")
    public void Init(){
        GameButtons = new RelativeLayout (this);
        gameFrame = new FrameLayout(this);
        game = new Game(this,getWindowManager(),GameButtons);

        setContentView(gameFrame);

        final Button pauseBtn = new Button(this);
        pauseBtn.setWidth(70);
        pauseBtn.setText("P");
        pauseBtn.getBackground().setAlpha(120);

        pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                game.pauseGame();
            }
        });


        RelativeLayout.LayoutParams b1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.MATCH_PARENT);
        GameButtons.setLayoutParams(params);
        GameButtons.addView(pauseBtn);
        b1.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
        b1.addRule(RelativeLayout.ALIGN_PARENT_TOP,RelativeLayout.TRUE);
        pauseBtn.setLayoutParams(b1);
        gameFrame.addView(game);
        gameFrame.addView(GameButtons);

    }
}


