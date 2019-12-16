package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.Random;

public class Enemy extends GameObject {

    private static final double SPEED_PIXELS_PER_SECOND = 200.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    public boolean isCounted;
    private transient Context context;
    private transient Paint paint;
    private double velocityX;
    private boolean movingRight;
    private boolean movingLeft;
    public boolean isDestroyed;
    private transient Bitmap image;

    public ArrayList<Shot> shots;
    private int counter=0;

    public Enemy(double positionX, double positionY, double radius, Context context){

        this.positionX = positionX;
        this.positionY = positionY;
        this.radius = radius;
        this.context = context;
        shots = new ArrayList<Shot>();
        Init(context);

        int randomY = new Random().nextInt((1 - 0) + 1) + 0;
        if ( randomY == 1){
            movingRight=true;
        }else{
            movingLeft = true;
        }
        isDestroyed = false;
        isCounted = false;
    }

    public void Init(Context context)
    {
        image = BitmapFactory.decodeResource(context.getResources(),R.drawable.enemy);
        this.context = context;
        this.Width = image.getWidth();
        this.Height = image.getHeight();
        paint = new Paint();
        int color = ContextCompat.getColor(this.context,R.color.enemy);
        paint.setColor(color);
        for (Shot s: shots) {
            s.Init(context);
        }
    }

    public void draw(Canvas canvas) {
        if (!isDestroyed){
            canvas.drawBitmap(image,(float)positionX,(float)positionY,null);
        }
        for (Shot shot: shots){
            shot.draw(canvas);
        }
    }

    public void update() {
        counter+=20;

        if (!isDestroyed){
            if (movingRight && this.positionX+image.getWidth() < Game.widthScreen){
                velocityX = 1 * MAX_SPEED;
                this.positionX += velocityX;
            }
            else if (movingLeft && this.positionX > 0){
                velocityX = 1 * MAX_SPEED;
                this.positionX -= velocityX;
            }else{
                movingRight = !movingRight;
                movingLeft = !movingLeft;
                this.positionY = this.positionY + image.getHeight()/2;
            }

            if (counter > 1500){
                counter = 0;
                shots.add(new Shot(this.positionX+image.getWidth()/2,this.positionY+image.getHeight(),9,context));
            }
        }

        Iterator itr = shots.iterator();
        while (itr.hasNext())
        {
            Shot i = (Shot)itr.next();

            if (i.GetPositionY()>Game.heightScreen){
                itr.remove();
            }else{
                i.update(-1);
            }
        }
    }

    @Override
    public void destroyed() {
        isDestroyed = true;
    }

    @Override
    public double getCenterX() {
        return this.positionX + image.getWidth()/2;
    }

    @Override
    public double getCenterY() {
        return this.positionY + image.getHeight()/2;
    }

    public boolean canBeRemoved() {
        if (shots.isEmpty() && isDestroyed){
            return  true;
        }
        return false;
    }
}
