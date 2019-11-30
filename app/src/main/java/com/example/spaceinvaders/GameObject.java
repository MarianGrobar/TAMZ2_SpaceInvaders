package com.example.spaceinvaders;

public abstract class GameObject {
    public double positionX;
    public double positionY;
    public double radius;

    public abstract void destroyed();
}
