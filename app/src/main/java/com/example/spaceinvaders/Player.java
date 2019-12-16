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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Player extends GameObject {
    private static final double SPEED_PIXELS_PER_SECOND = 200.0;
    public static final double MAX_SPEED = SPEED_PIXELS_PER_SECOND / GameLoop.MAX_UPS;
    private transient Context context;
    private transient Paint paint;
    private double velocityX;
    private boolean movingRight;
    private boolean movingLeft;
    private boolean followShots;
    private transient Bitmap image;
    private transient SoundPlayer soundPlayer;
    int counter = 0;
    public boolean isDestroyed;
    private ArrayList<Shot> shots;
    public Map< UpgradeType,Upgrade> upgrades ;

    int shootTime = 1300;
    private boolean pierceShots=false;

    public Player(double positionX, double positionY, double radius, Context context, SoundPlayer soundPlayer) {

        this.positionX = positionX;
        this.positionY = positionY;
        this.radius = radius;
        shots = new ArrayList<Shot>();
        upgrades = new HashMap< UpgradeType,Upgrade>();
        Init(context, soundPlayer);
        followShots= false;
    }

    public void Init(Context context, SoundPlayer soundPlayer) {
        isDestroyed = false;
        this.soundPlayer = soundPlayer;
        image = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
        this.Width = image.getWidth();
        this.Height = image.getHeight();
        this.context = context;
        paint = new Paint();
        int color = ContextCompat.getColor(this.context, R.color.player);
        paint.setColor(color);
        for (Shot s : shots) {
            s.Init(context);
        }
    }

    public void draw(Canvas canvas) {
        canvas.drawBitmap(image, (float) positionX, (float) positionY, null);
        //canvas.drawCircle((float)positionX, (float)positionY,(float)radius,paint);
        for (Shot shot : shots) {
            shot.draw(canvas);
        }
    }

    public void update() {
        counter += 20;
        if (movingRight && this.positionX + image.getWidth() < Game.widthScreen) {
            velocityX = 1.4 * MAX_SPEED;
            this.positionX += velocityX;
        } else if (movingLeft && this.positionX > 0) {
            velocityX = 1.4 * MAX_SPEED;
            this.positionX -= velocityX;
        }
        if (counter > shootTime) {
            counter = 0;
            soundPlayer.playShootSound();
            shots.add(new Shot(this.positionX + image.getWidth() / 2, this.positionY, 9, context));
        }
        collision(Game.gameObjects);

        for (Shot shot : shots) {
            shot.update(1);
        }
    }

    private void collision(ArrayList<GameObject> gameObjects) {
        for (GameObject item : gameObjects) {
            if (item instanceof Enemy){
                if (((Enemy) item).isDestroyed == false) {
                    Iterator itr = shots.iterator();
                    while (itr.hasNext()) {
                        Shot shot = (Shot) itr.next();

                    if (shot.canFollow(item) && followShots ) {
                            shot.follow(item);
                        }

                        if (shot.intersects(item)) {
                            //lonelyShots.addAll(((Enemy) item).shots);
                            item.destroyed();
                            if(!pierceShots){
                                itr.remove();
                            }
                        }

                        if (shot.positionY < 0 || shot.positionX < 0 || shot.positionY > Game.widthScreen) {
                            itr.remove();
                        }
                    }
                }
                Iterator itrEnemy = ((Enemy) item).shots.iterator();
                while (itrEnemy.hasNext()) {
                    Shot shot = (Shot) itrEnemy.next();

                    if (shot.intersects(this)) {
                        this.destroyed();
                        itrEnemy.remove();
                    }

                    if (shot.positionY > Game.heightScreen || shot.positionX < 0 || shot.positionY > Game.widthScreen) {
                        itrEnemy.remove();
                    }
                }
            }else if (item instanceof Upgrade){
                if (this.positionX < item.positionX + item.Width &&
                        this.positionX + this.Width > item.positionX &&
                        this.positionY < item.positionY + item.Height &&
                        this.positionY + this.Height > item.positionY) {
                    ((Upgrade)item).hasOwner = true;
                    if(!upgrades.containsKey(((Upgrade)item).type)){
                        upgrades.put(((Upgrade)item).type,(Upgrade)item);
                    }

                }
            }



        }

    }

    public void moving(double positionX) {
        if (positionX > this.positionX) {
            movingRight = true;
            movingLeft = false;

        } else if (positionX < this.positionX) {
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
        soundPlayer.playDeathSound();
        isDestroyed = true;
    }

    @Override
    public double getCenterX() {
        return this.positionX + image.getWidth() / 2;
    }

    @Override
    public double getCenterY() {
        return this.positionY + image.getHeight() / 2;
    }

    public void followShot() {
        followShots=true;
        pierceShots = false;
        shootTime = 1300;
    }

    public void pierceShot() {
        pierceShots = true;
        shootTime = 1300;
        followShots=false;
    }

    public void MachineGun() {
        shootTime = 600;
        followShots = false;
        pierceShots = false;
    }
}
