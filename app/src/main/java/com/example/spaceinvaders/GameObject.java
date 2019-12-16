package com.example.spaceinvaders;

import android.content.Context;

public class GameObject {
    public double positionX;
    public double positionY;
    public double Width;
    public double Height;
    public double radius;


    public  void destroyed(){};

    public  double getCenterX(){ return 0;};
    public  double getCenterY(){return  0;};
}
