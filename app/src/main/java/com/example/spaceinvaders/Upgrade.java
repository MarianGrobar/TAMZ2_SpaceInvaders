package com.example.spaceinvaders;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.widget.Button;

import androidx.core.content.ContextCompat;

public class Upgrade extends GameObject {

    public transient Button button;
    public boolean hasOwner;
    public boolean Display;
    public UpgradeType type;
    private final double velocityX;
    private final double velocityY;
    private transient Paint paint;
    private transient Player player;

    public Upgrade(double positionX, double positionY, Context context,UpgradeType type,Player p){
        hasOwner=false;
        this.positionX = positionX;
        this.positionY = positionY;
        this.Width = 30;
        this.Height = 30;
        this.type = type;
        Init(context,p);
        Display = true;
        velocityX = 0;
        velocityY = -1 * Player.MAX_SPEED;
    }

    public void draw(Canvas canvas) {
        canvas.drawRect((float)positionX, (float)positionY,(float)positionX + 30,(float)positionY + 30,paint);
    }

    public void Init(Context context,Player p){
        player = p;
        paint = new Paint();
        int color;
        if ( type == UpgradeType.Follow){
            color = ContextCompat.getColor(context,R.color.magenta);
        }else if(type == UpgradeType.MachineGun){
            color = ContextCompat.getColor(context,R.color.player);
        }else{
            color = ContextCompat.getColor(context,R.color.enemy);
        }
        paint.setColor(color);
    }

    public void update(int direction) {
        this.positionY +=  velocityY*direction;
        this.positionX +=  velocityX;
    }

    public void activate() {
        if (type == UpgradeType.Follow){
            player.followShot();
        }else if(type == UpgradeType.Pierce){
            player.pierceShot();
        }else {
            player.MachineGun();
        }
    }
}
enum UpgradeType{
    Follow,
    Pierce,
    MachineGun
}
