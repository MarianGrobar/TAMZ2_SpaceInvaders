package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Shot extends GameObject {

    private Paint paint;
    private double velocityY;
    public double  followRadius;
    private double velocityX;

    public Shot(double positionX, double positionY, double radius, Context context){

        this.positionX = positionX;
        this.positionY = positionY;
        this.radius = radius;
        this.followRadius = 120;

        velocityX = 0;
        velocityY = -1 * Player.MAX_SPEED;

        paint = new Paint();
        int color = ContextCompat.getColor(context,R.color.magenta);
        paint.setColor(color);
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle((float)positionX, (float)positionY,(float)radius,paint);
        }

    public void update(int direction) {

        this.positionY +=  velocityY;
        this.positionX +=  velocityX;
    }

    public double GetPositionY(){
        return this.positionY;
    }

    @Override
    public void destroyed() {

    }

    public void follow(GameObject item) {

        double dx = item.positionX - this.positionX;
        double dy = item.positionY - this.positionY;

        double distanceToObject = Math.sqrt(dx*dx + dy*dy);

        double directionX = dx/distanceToObject;
        double directionY = dy/distanceToObject;

        velocityX = directionX * Player.MAX_SPEED * 1.2;
        velocityY = directionY * Player.MAX_SPEED * 1.2;

/*
        this.positionY +=  velocityY;
        this.positionX +=  velocityX;
*/

    }
}
