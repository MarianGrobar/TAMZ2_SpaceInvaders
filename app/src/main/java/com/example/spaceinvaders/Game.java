package com.example.spaceinvaders;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;

import com.example.spaceinvaders.databaseClasses.Score;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

class Game extends SurfaceView implements SurfaceHolder.Callback {

    public Player player;
    public static int widthScreen;
    public static int heightScreen;
    public boolean state;
    private GameLoop gameLoop;
    public static ArrayList<GameObject> gameObjects;
    public ArrayList<Enemy> enemies;
    public ArrayList<Upgrade> upgrades;
    private long lastEnemy;
    private Bitmap background;
    private int score;
    public SoundPlayer soundPlayer;
    private RelativeLayout parentLayout;
    boolean pause;
    boolean endGame;
    GestureDetectorCompat gestureDetectorCompat;
    private int enemyTimer;

    public Game(Context context, WindowManager windowManager, RelativeLayout parentLayout) {
        super(context);
        soundPlayer = new SoundPlayer(context);
        this.parentLayout = parentLayout;
        soundPlayer.playMusic();

        enemyTimer = 0;
        endGame = false;
        state = true;
        pause = false;
        lastEnemy = 0;
        gameObjects = new ArrayList<GameObject>();
        enemies = new ArrayList<Enemy>();
        upgrades = new ArrayList<>();
        score = 0;
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreen = size.x;
        heightScreen = size.y;

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        gameLoop = new GameLoop(this, surfaceHolder);

/*        Enemy e = new Enemy(300,150,30,getContext());
        enemies.add(e);
        gameObjects.add(e);*/

        player = new Player(500, 950, 30, getContext(), soundPlayer);
        setFocusable(true);
        generateUpgrade();
        gestureDetectorCompat = new GestureDetectorCompat(getContext(), new GestureListener());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        gestureDetectorCompat.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (endGame) {
                    Intent intent = new Intent(getContext(), HomePage.class);
                    getContext().startActivity(intent);
                }
                player.moving((double) event.getX());
                return true;
            case MotionEvent.ACTION_UP:
                player.stopMoving();
                return true;
        }

        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Thread.State state = gameLoop.getState();
        if (state == Thread.State.NEW) {
            gameLoop.startLoop();
        } else {
            SurfaceHolder surfaceHolder = getHolder();
            gameLoop = new GameLoop(this, surfaceHolder);
        }

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseResources();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        canvas.drawBitmap(background, 0, 0, null);
        drawScore(canvas);

        player.draw(canvas);
        for (Enemy e : enemies) {
            e.draw(canvas);
        }

        for (Upgrade u : upgrades) {
            u.draw(canvas);
        }
    }

    public void drawUPS(Canvas canvas) {
        String averageUPS = Double.toString(gameLoop.getAverageUPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("UPS: " + averageUPS, 100, 60, paint);
    }

    public void drawFPS(Canvas canvas) {
        String averageFPS = Double.toString(gameLoop.getAverageFPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("FPS: " + averageFPS, 100, 200, paint);
    }

    public void drawScore(Canvas canvas) {
        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(), R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("Score: " + score, 100, 60, paint);
    }

    public void update() {
        if (player.isDestroyed) {
            endGame();
        }
        player.update();
        generateEnemies();

        Iterator itr = enemies.iterator();
        while (itr.hasNext()) {
            Enemy i = (Enemy) itr.next();
            if (i.isDestroyed && !i.isCounted) {
                    soundPlayer.playKillSound();
                    score++;
                    i.isCounted = true;
            } else{
                i.update();
                if (i.canBeRemoved()) {
                    gameObjects.remove(i);
                    itr.remove();
                }
            }
        }

        Iterator iupitr = upgrades.iterator();
        while (iupitr.hasNext()) {
            Upgrade u = (Upgrade) iupitr.next();
            if (u.hasOwner) {
                gameObjects.remove(u);
                iupitr.remove();
            } else{
                u.update(-1);
            }
        }
    }

    private void endGame(){

        if (  endGame==false  ){
            List<Score> scores;
            scores = HomePage.appDatabase.appDatabaseObject().readScore();
            if (scores.size() >= 5){
                Score scoreObject = new Score();
                scoreObject.setValue(score);
                scores.add(scoreObject);
                Collections.sort(scores);

                HomePage.appDatabase.appDatabaseObject().deleteScore(scores);
                int counter=0;
                for (Score s: scores){
                    if (counter<5){
                        HomePage.appDatabase.appDatabaseObject().addScore(s);
                    }
                    counter++;
                }
            }else{
                Score scoreObject = new Score();
                scoreObject.setValue(score);
                HomePage.appDatabase.appDatabaseObject().addScore(scoreObject);
            }
            gameLoop.interrupt();
            endGame = true;
        }

    }

    private void generateEnemies() {
        enemyTimer += 20;
        if (enemyTimer > 3000) {
            enemyTimer = 0;
            int min = 0;
            int max = widthScreen;
            int randomX = new Random().nextInt((max - min) + 1) + min;
            max = 200;
            int randomY = new Random().nextInt((max - min) + 1) + min;
            Enemy e = new Enemy(randomX, randomY, 30, getContext());
            enemies.add(e);
            gameObjects.add(e);
        }
    }

    private void generateUpgrade(){
        Upgrade up1 = new Upgrade(50,50,getContext(),UpgradeType.MachineGun,player);
        Upgrade up2 = new Upgrade(50,150,getContext(),UpgradeType.Follow,player);
        gameObjects.add(up1);
        upgrades.add(up1);
        gameObjects.add(up2);
        upgrades.add(up2);

    }

    public void pauseGame() {

        pause = !pause;
        if (pause) {
            gameLoop.Pause();
            soundPlayer.pauseMediaPlayer();
        } else {
            gameLoop.Resume();
            soundPlayer.startMediaPlayer(this.getContext());
        }
    }

    public String getEnemiesJSON() {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(gameObjects);

        return jsonStr;
    }

    public String getPlayerJSON() {
        Gson gson = new Gson();
        String jsonStr = gson.toJson(player);

        return jsonStr;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    class GestureListener extends GestureDetector.SimpleOnGestureListener {

        private static final int SWIPE_MIN_DISTANCE = 10;
        private static final int SWIPE_MAX_OFF_PATH = 250;
        private static final int SWIPE_THRESHOLD_VELOCITY = 100;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            if (Math.abs(e1.getX() - e2.getX()) > SWIPE_MAX_OFF_PATH){
                return false;
            }
            if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
                OnFlingMethod();
                return super.onFling(e1, e2, velocityX, velocityY);
            }
            return false;
        }

        public void OnFlingMethod(){
            Toast.makeText(getContext(), "Fling", Toast.LENGTH_SHORT).show();
            int marginTop;
            List<Upgrade> listUpgrade = new ArrayList<Upgrade>(player.upgrades.values());
            for (final Upgrade u:listUpgrade){
                if (u.button==null && u.Display){
                    Button pauseBtn = new Button(getContext());
                    pauseBtn.setWidth(150);
                    pauseBtn.setText(u.type.toString());

                    RelativeLayout.LayoutParams b1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                    parentLayout.addView(pauseBtn);
                    b1.addRule(RelativeLayout.ALIGN_PARENT_START, RelativeLayout.TRUE);
                    b1.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
                    b1.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
                    marginTop = 130 + 130 * (listUpgrade.indexOf(u)+1);
                    b1.setMargins(350, marginTop, 350, 0);
                    pauseBtn.getBackground().setAlpha(130);
                    pauseBtn.setLayoutParams(b1);
                    pauseBtn.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            u.activate();
                        }
                    });
                    u.button = pauseBtn;
                    u.Display = !u.Display;
                }
                else if (u.Display){
                    u.button.setVisibility(VISIBLE);
                    u.button.setEnabled(true);
                    u.Display = !u.Display;
                }else{
                    u.button.setVisibility(INVISIBLE);
                    u.button.setEnabled(false);
                    u.Display = !u.Display;
                }

            }
            pauseGame();
        }
    }

    public void releaseResources() {
        soundPlayer.releaseMediaPlayer();

    }
}