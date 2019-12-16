package com.example.spaceinvaders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GestureDetectorCompat;
import androidx.room.Room;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.spaceinvaders.databaseClasses.AppDatabase;
import com.example.spaceinvaders.databaseClasses.Score;

import java.util.List;

public class HomePage extends Activity {

    public static AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_home_page);
        //Toast.makeText(this, "HomePage", Toast.LENGTH_SHORT).show();

        appDatabase = Room.databaseBuilder(this, AppDatabase.class, "scoreinfo").allowMainThreadQueries().build();

        List<Score> scores = HomePage.appDatabase.appDatabaseObject().readScore();

    }

    @Override
    protected void onResume() {
        //Toast.makeText(this, "HomePageResume", Toast.LENGTH_SHORT).show();
        super.onResume();
    }

    public void StartGame(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void ScoreActivity(View view) {
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
    }
}
