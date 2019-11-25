package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Shot {

    private double positionX;
    private double positionY;
    private double radius;
    private Paint paint;
    private double velocityY;

    public Shot(double positionX, double positionY, double radius, Context context){

        this.positionX = positionX;
        this.positionY = positionY;
        this.radius = radius;

        paint = new Paint();
        int color = ContextCompat.getColor(context,R.color.magenta);
        paint.setColor(color);
    }

    public void draw(Canvas canvas) {
        canvas.drawCircle((float)positionX, (float)positionY,(float)radius,paint);
        }

    public void update() {
        velocityY = 0.5 * Player.MAX_SPEED;
        this.positionY -= velocityY;
    }

    public double GetPositionY(){
        return this.positionY;
    }
}
