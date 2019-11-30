package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

class Game extends SurfaceView implements SurfaceHolder.Callback {

    private final Player player;
    public static int widthScreen;
    public static int heightScreen;
    private GameLoop gameLoop;
    public static ArrayList<GameObject> gameObjects;
    private ArrayList<Enemy> enemies;
    private long lastEnemy;

    public Game(Context context, WindowManager windowManager){
        super(context);
        lastEnemy=0;
        gameObjects = new ArrayList<GameObject>();
        enemies = new ArrayList<Enemy>();

        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        widthScreen = size.x;
        heightScreen = size.y;

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        gameLoop = new GameLoop(this,surfaceHolder);

        player = new Player(500,950,30,getContext());
        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                player.moving((double)event.getX());
                return  true;
            case MotionEvent.ACTION_UP:
                player.stopMoving();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        drawFPS(canvas);
        drawUPS(canvas);
        player.draw(canvas);
        for (Enemy e: enemies){
            e.draw(canvas);
        }
    }

    public void drawUPS(Canvas canvas){
        String averageUPS = Double.toString(gameLoop.getAverageUPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(),R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("UPS: " + averageUPS,100,60,paint);
    }

    public void drawFPS(Canvas canvas){
        String averageFPS = Double.toString(gameLoop.getAverageFPS());
        Paint paint = new Paint();
        int color = ContextCompat.getColor(getContext(),R.color.magenta);
        paint.setColor(color);
        paint.setTextSize(50);
        canvas.drawText("FPS: "  + averageFPS,100,200,paint);
    }

    public void update() {
        player.update();
        generateEnemies();
        Iterator itr = enemies.iterator();
        while (itr.hasNext())
        {
            Enemy i = (Enemy)itr.next();
            if (i.isDestroyed){
                itr.remove();
                gameObjects.remove(i);
            }else{
                i.update();
            }
        }
    }

    private void generateEnemies() {

        if ((System.currentTimeMillis()- lastEnemy) > 4000){
            lastEnemy = System.currentTimeMillis();
             int min = 0;
             int max = widthScreen;
             int randomX = new Random().nextInt((max - min) + 1) + min;
             max = 200;
             int randomY = new Random().nextInt((max - min) + 1) + min;
             Enemy e = new Enemy(randomX,randomY,30,getContext());
             enemies.add(e);
             gameObjects.add(e);
        }
    }
}
