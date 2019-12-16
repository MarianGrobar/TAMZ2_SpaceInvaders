package com.example.spaceinvaders.databaseClasses;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DatabaseAccessInterface {

    @Insert
    public void addScore(Score score);

    @Query("Select * from score")
    public List<Score> readScore();

    @Delete
    public void deleteScore(List<Score> score);
}
