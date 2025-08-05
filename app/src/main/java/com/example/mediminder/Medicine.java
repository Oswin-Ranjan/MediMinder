package com.example.mediminder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity public class Medicine {
    @PrimaryKey(autoGenerate = true)

    private int id;
    private String name;
    private int hour;
    private int minute;

    public Medicine(String name, int hour, int minute) {
        this.name = name;
        this.hour = hour;
        this.minute = minute;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public int getHour() { return hour; }
    public int getMinute() { return minute; }

}

