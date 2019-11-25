package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;

public class Player {
    private static final double SPEED_PIXELS_PER_SECOND = 400.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private final Context context;
    private double positionX;
    private double positionY;
    private double radius;
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
            velocityX = 0.5 * MAX_SPEED;
            this.positionX += velocityX;
        }
        else if(movingLeft && this.positionX-this.radius > 0){
            velocityX = 0.5 * MAX_SPEED;
            this.positionX -= velocityX;
        }
        if ((System.currentTimeMillis()- lastShot) > 800){
            lastShot = System.currentTimeMillis();
            shots.add(new Shot(this.positionX,this.positionY-this.radius-10,10,context));
        }

        Iterator itr = shots.iterator();
        while (itr.hasNext())
        {
            Shot i = (Shot)itr.next();

            if (i.GetPositionY()<0){
                itr.remove();
            }else{
                i.update();
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
}
