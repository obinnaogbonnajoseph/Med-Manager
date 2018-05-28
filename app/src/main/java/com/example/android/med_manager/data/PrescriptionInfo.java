package com.example.android.med_manager.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * This is an adapter that adapts the cardview to the
 * the recycler view
 * Note that sql database only accepts
 * integers, strings and nulls
 */
@Entity(tableName = "med_table")
public class PrescriptionInfo {

    // TODO: Implement a type converter
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "med_name")
    private String mMedName;

    @ColumnInfo(name = "med_description")
    private String mMedDescription;

    @ColumnInfo(name = "med_freq")
    private int mFrequency;

    @ColumnInfo(name = "med_time")
    private String timeRemind;

    @ColumnInfo(name = "med_time1")
    private String timeRemind1;

    @ColumnInfo(name = "med_time2")
    private String timeRemind2;

    @ColumnInfo(name = "day_sunday")
    private int day_sunday;

    @ColumnInfo(name = "day_monday")
    private int day_monday;

    @ColumnInfo(name = "day_tuesday")
    private int day_tuesday;

    @ColumnInfo(name = "day_wednesday")
    private int day_wednesday;

    @ColumnInfo(name = "day_thursday")
    private int day_thursday;

    @ColumnInfo(name = "day_friday")
    private int day_friday;

    @ColumnInfo(name = "day_saturday")
    private int day_saturday;

    @ColumnInfo(name = "start_date")
    private Date start_date;

    @ColumnInfo(name = "end_date")
    private Date end_date;

    @Ignore
    private ArrayList<Integer> mWeekdays;

    @Ignore
    private ArrayList<String> mTimes;

    @Ignore
    private ArrayList<Date> mDates;


    public PrescriptionInfo(@NonNull String medName, String medDescription, int medFrequency,
                            String timeRemind, String timeRemind1, String timeRemind2, int day_sunday,
                            int day_monday, int day_tuesday, int day_wednesday, int day_thursday,
                            int day_friday, int day_saturday, Date start_date, Date end_date) {
        // Set the values.
        this.mMedName = medName;
        this.mMedDescription = medDescription;
        this.mFrequency = medFrequency;
        this.timeRemind = timeRemind; this.timeRemind1 = timeRemind1; this.timeRemind2 = timeRemind2;
        this.day_sunday = day_sunday; this.day_monday = day_monday; this.day_tuesday = day_tuesday;
        this.day_wednesday = day_wednesday; this.day_thursday = day_thursday;
        this.day_friday = day_friday; this.day_saturday = day_saturday;
        this.start_date = start_date; this.end_date = end_date;

        // Put the time data into an array
        mTimes.add(timeRemind); mTimes.add(timeRemind1); mTimes.add(timeRemind2);
        // Put the days data into an array
        mWeekdays.add(day_sunday); mWeekdays.add(day_monday); mWeekdays.add(day_tuesday);
        mWeekdays.add(day_wednesday); mWeekdays.add(day_thursday); mWeekdays.add(day_friday);
        mWeekdays.add(day_saturday);
        // Put the dates into an array
        mDates.add(start_date); mDates.add(end_date);
    }

    public String getMedname() {return this.mMedName;}

    public String getMedDescription() {return this.mMedDescription;}

    public int getFrequency() {return this.mFrequency;}

    public List<String> getTimes() {return this.mTimes;}

    public List<Integer> getWeekdays() {return this.mWeekdays;}

    public List<Date> getDates() {return this.mDates;}
}
