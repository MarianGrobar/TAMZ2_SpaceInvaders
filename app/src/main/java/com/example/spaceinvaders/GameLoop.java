package com.example.spaceinvaders;

import android.graphics.Canvas;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.zip.Checksum;

class GameLoop extends Thread {
    public static final double MAX_UPS = 60;
    private static final double UPS_PERIOD = 1E+3 / MAX_UPS;
    private boolean isRunnig = false;
    private SurfaceHolder surfaceHolder;
    private Game game;
    private double averageUPS;
    private double averageFPS;
    private Object mPauseLock;
    private boolean mPaused;

    public GameLoop(Game game, SurfaceHolder surfaceHolder) {
        this.surfaceHolder = surfaceHolder;
        this.game = game;
        mPauseLock = new Object();
        mPaused = false;
    }

    public double getAverageUPS() {
        return averageUPS;
    }

    public double getAverageFPS() {
        return averageFPS;
    }

    public void startLoop() {
        isRunnig = true;
        start();
    }

    @Override
    public void run() {
        super.run();
        try {

            int updateCount = 0;
            int frameCount = 0;

            long startTime;
            long elapsedTime = 0;
            long sleepTime;


            //Game loop
            Canvas canvas = null;
            startTime = System.currentTimeMillis();
            while(isRunnig){

                if (this.isInterrupted()){
                    return;
                }
                synchronized (mPauseLock) {
                    while (mPaused) {
                        try {
                            mPauseLock.wait();
                        } catch (InterruptedException e) {}
                    }
                }
                    try {
                        canvas = surfaceHolder.lockCanvas();
                        synchronized (surfaceHolder) {
                            game.update();
                            updateCount++;
                            game.draw(canvas);
                        }
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } finally {
                        if (canvas != null) {
                            try {
                                surfaceHolder.unlockCanvasAndPost(canvas);
                                frameCount++;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    elapsedTime = System.currentTimeMillis() - startTime;
                    sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTime);

                    if (sleepTime > 0) {
                        try {
                            sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    while (sleepTime < 0 && updateCount < MAX_UPS - 1) {
                        game.update();
                        updateCount++;
                        elapsedTime = System.currentTimeMillis() - startTime;
                        sleepTime = (long) (updateCount * UPS_PERIOD - elapsedTime);
                    }

                    elapsedTime = System.currentTimeMillis() - startTime;
                    if (elapsedTime >= 1000) {
                        updateCount = 0;
                        frameCount = 0;
                        startTime = System.currentTimeMillis();
                    }
            }
        }
        catch (Exception e){

        }
    }
    public void Pause() {
        synchronized (mPauseLock) {
            mPaused = true;
        }
    }
    public void Resume() {
        synchronized (mPauseLock) {
            mPaused = false;
            mPauseLock.notifyAll();
        }
    }
}
