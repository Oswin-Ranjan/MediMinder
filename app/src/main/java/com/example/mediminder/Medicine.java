package com.example.mediminder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity public class Medicine
{
    @PrimaryKey(autoGenerate = true)

    public int id;

    public String name;
    public int hour;
    public int minute;
    public int alarmId;
    public String dosage;
    public String instruction;
    public String repeatDays;

    public Medicine(String name, String dosage, String instruction, int hour, int minute)
    {
        this.name = name;
        this.hour = hour;
        this.minute = minute;
        this.dosage = dosage;
        this.instruction = instruction;
    }
    public int getId() {

        return id;
    }
    public void setId(int id) {

        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getDosage() {
        return dosage;
    }
    public void setDosage(String dosage) {
        this.dosage = dosage;
    }
    public int getHour() {
        return hour;
    }
    public int getMinute() {
        return minute;
    }
    public String getInstruction() {
        return instruction;
    }
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }
    public String getRepeatDays() {
        return repeatDays;
    }
    public void setRepeatDays(String repeatDays) {
        this.repeatDays = repeatDays;
    }

}

