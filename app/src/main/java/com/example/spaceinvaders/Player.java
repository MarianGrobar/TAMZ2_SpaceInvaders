package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;

public class Player extends GameObject {
    private static final double SPEED_PIXELS_PER_SECOND = 200.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private final Context context;
    private Paint paint;
    private double velocityX;
    private boolean movingRight;
    private boolean movingLeft;


    private ArrayList<Shot> shots;
    private long lastShot;


    public Player(double positionX, double positionY, double radius, Context context){

        this.positionX = positionX;
        this.positionY = positionY;
        this.radius = radius;
        this.context = context;
        this.lastShot = 0;

        shots = new ArrayList<Shot>();
        paint = new Paint();
        int color = ContextCompat.getColor(this.context,R.color.player);
        paint.setColor(color);
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle((float)positionX, (float)positionY,(float)radius,paint);
        for (Shot shot: shots){
            shot.draw(canvas);
        }
    }

    public void update() {
        if (movingRight){
            velocityX = 1 * MAX_SPEED;
            this.positionX += velocityX;
        }
        else if(movingLeft && this.positionX-this.radius > 0){
            velocityX = 1 * MAX_SPEED;
            this.positionX -= velocityX;
        }
        if ((System.currentTimeMillis()- lastShot) > 800){
            lastShot = System.currentTimeMillis();
            shots.add(new Shot(this.positionX,this.positionY-this.radius-10,10,context));
        }

        collision(Game.gameObjects);
        
        Iterator itr = shots.iterator();
        while (itr.hasNext())
        {
            Shot i = (Shot)itr.next();

            if (i.GetPositionY()<0){
                itr.remove();
            }else{
                i.update(-1);
            }
        }



    }

    private void collision(ArrayList<GameObject> gameObjects) {
        for (GameObject item: gameObjects) {
            for (Shot shot: shots){

                double dx = item.positionX - shot.positionX;
                double dy = item.positionY - shot.positionY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < item.radius + shot.radius) {
                    item.destroyed();
                }

            }
            for ( Shot shot: ((Enemy) item).shots ){

                double dx = shot.positionX - this.positionX;
                double dy = shot.positionY - this.positionY;
                double distance = Math.sqrt(dx * dx + dy * dy);

                if (distance < this.radius + shot.radius) {
                    this.destroyed();
                }
            }
        }
    }

    public void moving(double positionX) {
        if (positionX > this.positionX)
        {
            movingRight = true;
            movingLeft = false;

        }else if (positionX < this.positionX){
            movingLeft = true;
            movingRight = false;
        }
    }

    public void stopMoving() {
         movingRight = false;
         movingLeft = false;
    }

    @Override
    public void destroyed() {
        
    }
}
