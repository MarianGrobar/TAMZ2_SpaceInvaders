package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Iterator;

public class Enemy extends GameObject {

    private static final double SPEED_PIXELS_PER_SECOND = 200.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private final Context context;
    private Paint paint;
    private double velocityX;
    private boolean movingRight;
    private boolean movingLeft;
    public boolean isDestroyed;

    public ArrayList<Shot> shots;
    private long lastShot;


    public Enemy(double positionX, double positionY, double radius, Context context){

        this.positionX = positionX;
        this.positionY = positionY;
        this.radius = radius;
        this.context = context;
        this.lastShot = 0;

        shots = new ArrayList<Shot>();
        paint = new Paint();
        int color = ContextCompat.getColor(this.context,R.color.enemy);
        paint.setColor(color);
        movingRight=true;
        isDestroyed = false;
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle((float)positionX, (float)positionY,(float)radius,paint);
        for (Shot shot: shots){
            shot.draw(canvas);
        }
    }

    public void update() {

        if (!isDestroyed){
            if (movingRight && this.positionX+this.radius < Game.widthScreen){
                velocityX = 1 * MAX_SPEED;
                this.positionX += velocityX;
            }
            else if (movingLeft && this.positionX-this.radius > 0){
                velocityX = 1 * MAX_SPEED;
                this.positionX -= velocityX;
            }else{

                movingRight = !movingRight;
                movingLeft = !movingLeft;
                this.positionY = this.positionY + this.radius*2;
            }
        }

        if ((System.currentTimeMillis()- lastShot) > 1000){
            lastShot = System.currentTimeMillis();
            shots.add(new Shot(this.positionX,this.positionY+this.radius+10,10,context));
        }

        Iterator itr = shots.iterator();
        while (itr.hasNext())
        {
            Shot i = (Shot)itr.next();

            if (i.GetPositionY()>Game.heightScreen){
                itr.remove();
            }else{
                i.update(1);
            }
        }
    }

    @Override
    public void destroyed() {
        isDestroyed = true;
    }
}
