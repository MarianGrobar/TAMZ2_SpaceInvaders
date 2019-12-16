package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class Shot extends GameObject {

    private transient Paint paint;
    private double velocityY;
    public double  followRadius;
    private double velocityX;
    private transient Bitmap image;

    public Shot(double positionX, double positionY, double radius, Context context){

        this.positionX = positionX;
        this.positionY = positionY;
        this.radius = radius;
        this.followRadius = 200;

        velocityX = 0;
        velocityY = -1 * Player.MAX_SPEED;
        Init(context);
}

    public void Init( Context context){

        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.shot);
        paint = new Paint();
        int color = ContextCompat.getColor(context,R.color.magenta);
        paint.setColor(color);
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, (float) positionX-image.getWidth()/2, (float) positionY-image.getHeight()/2, null);
        //canvas.drawCircle((float)positionX, (float)positionY,(float)radius,paint);
        }

    public void update(int direction) {

        this.positionY +=  velocityY*direction;
        this.positionX +=  velocityX;
    }

    public double GetPositionY(){
        return this.positionY;
    }

    @Override
    public void destroyed() {

    }

    @Override
    public double getCenterX() {
        return 0;
    }

    @Override
    public double getCenterY() {
        return 0;
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

    Boolean intersects(GameObject rect)
    {
        double circleDistanceX = Math.abs(this.positionX - rect.getCenterX());
        double circleDistanceY = Math.abs(this.positionY - rect.getCenterY());

        if (circleDistanceX > (rect.Width/2 + this.radius)) { return false; }
        if (circleDistanceY > (rect.Height/2 + this.radius)) { return false; }

        if (circleDistanceX <= (rect.Width/2)) { return true; }
        if (circleDistanceY <= (rect.Height/2 )) { return true; }

        double cornerDistance_sq = Math.pow(circleDistanceX - rect.Width/2,2) + Math.pow(circleDistanceY - rect.Height/2,2);
        return (cornerDistance_sq <= (this.radius * this.radius));
    }

    Boolean canFollow(GameObject rect)
    {
        double circleDistanceX = Math.abs(this.positionX - rect.getCenterX());
        double circleDistanceY = Math.abs(this.positionY - rect.getCenterY());

        if (circleDistanceX > (rect.Width/2 + this.followRadius)) { return false; }
        if (circleDistanceY > (rect.Height/2 + this.followRadius)) { return false; }

        if (circleDistanceX <= (rect.Width/2)) { return true; }
        if (circleDistanceY <= (rect.Height/2 )) { return true; }

        double cornerDistance_sq = Math.pow(circleDistanceX - rect.Width/2,2) + Math.pow(circleDistanceY - rect.Height/2,2);
        return (cornerDistance_sq <= (this.followRadius * this.followRadius));
    }
}
