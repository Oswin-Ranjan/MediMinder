package com.example.mediminder;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "Medicine")
public class Medicine {

    public static final int STATUS_PENDING = 0;
    public static final int STATUS_TAKEN = 1;
    public static final int STATUS_MISSED = 2;

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private int hour;
    private int minute;
    private int alarmId;
    private String dosage;
    private String instruction;
    private String repeatDays;

    private int status;
    private long takenTimeMillis;

    public Medicine(String name, String dosage, String instruction, int hour, int minute) {
        this.name = name;
        this.dosage = dosage;
        this.instruction = instruction;
        this.hour = hour;
        this.minute = minute;
        this.status = STATUS_PENDING;
        this.takenTimeMillis = 0;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getHour() { return hour; }
    public void setHour(int hour) { this.hour = hour; }

    public int getMinute() { return minute; }
    public void setMinute(int minute) { this.minute = minute; }

    public int getAlarmId() { return alarmId; }
    public void setAlarmId(int alarmId) { this.alarmId = alarmId; }

    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }

    public String getInstruction() { return instruction; }
    public void setInstruction(String instruction) { this.instruction = instruction; }

    public String getRepeatDays() { return repeatDays; }
    public void setRepeatDays(String repeatDays) { this.repeatDays = repeatDays; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public long getTakenTimeMillis() { return takenTimeMillis; }
    public void setTakenTimeMillis(long takenTimeMillis) { this.takenTimeMillis = takenTimeMillis; }

    public boolean isPending() { return status == STATUS_PENDING; }
    public boolean isTaken() { return status == STATUS_TAKEN; }
    public boolean isMissed() { return status == STATUS_MISSED;}
}