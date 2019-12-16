package com.example.spaceinvaders;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

public class SoundPlayer {
    private static SoundPool soundPool;
    private  static  int shootSound;
    private static  int killSound;
    private static  int playerDeathSound;
    MediaPlayer mediaPlayer;

    public SoundPlayer(Context context){
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,0);
        mediaPlayer = MediaPlayer.create(context,R.raw.spaceinvaders1);
        shootSound = soundPool.load(context,R.raw.shoot,1);
        killSound = soundPool.load(context,R.raw.invaderkilled,1);
        playerDeathSound = soundPool.load(context,R.raw.explosion,1);

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.start();
            }
        });
    }

    public void playShootSound(){
        soundPool.play(shootSound,0.3f,0.3f,1,0,1.0f );
    }
    public void playKillSound(){
        soundPool.play(killSound,0.3f,0.3f,1,0,1.0f );
    }
    public void playDeathSound(){
        soundPool.play(playerDeathSound,0.3f,0.3f,1,0,1.0f );
    }
    public void playMusic(){
        if (  mediaPlayer!= null  ){
            mediaPlayer.start();
        }

    }
    public void releaseMediaPlayer(){
        if ( mediaPlayer != null)
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
    public void startMediaPlayer(Context context) {
        playMusic();
    }

    public void pauseMediaPlayer() {
        mediaPlayer.pause();
    }
}
