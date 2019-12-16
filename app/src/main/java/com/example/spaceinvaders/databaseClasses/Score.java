package com.example.spaceinvaders.databaseClasses;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "score")
public class Score implements Comparable<Score> {
    @PrimaryKey(autoGenerate = true)
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getValue() {
        return Value;
    }

    public void setValue(int value) {
        Value = value;
    }

    @ColumnInfo(name = "value")
    private int Value;

    @Override
    public int compareTo(Score o) {
        if (this.Value > o.Value){
            return -1;
        }else if(this.Value < o.Value){
            return 1;
        }else{
            return 0;
        }
    }
}
