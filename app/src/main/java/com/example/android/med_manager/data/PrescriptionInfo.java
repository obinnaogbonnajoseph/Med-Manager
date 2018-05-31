package com.example.android.med_manager.data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;


/**
 * This is an adapter that adapts the cardview to the
 * the recycler view
 * Note that sql database only accepts
 * integers, strings and nulls
 */
@Entity(tableName = "med_table")
public class PrescriptionInfo implements Parcelable{

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
    private int daySunday;

    @ColumnInfo(name = "day_monday")
    private int dayMonday;

    @ColumnInfo(name = "day_tuesday")
    private int dayTuesday;

    @ColumnInfo(name = "day_wednesday")
    private int dayWednesday;

    @ColumnInfo(name = "day_thursday")
    private int dayThursday;

    @ColumnInfo(name = "day_friday")
    private int dayFriday;

    @ColumnInfo(name = "day_saturday")
    private int daySaturday;

    @ColumnInfo(name = "start_date")
    private long startDate;

    @ColumnInfo(name = "end_date")
    private long endDate;

    public PrescriptionInfo(@NonNull String medName, String medDescription, int mFrequency,
                            String timeRemind, String timeRemind1, String timeRemind2, int daySunday,
                            int dayMonday, int dayTuesday, int dayWednesday, int dayThursday,
                            int dayFriday, int daySaturday, long startDate, long endDate) {
        // Set the values.
        this.mMedName = medName;
        this.mMedDescription = medDescription;
        this.mFrequency = mFrequency;
        this.timeRemind = timeRemind; this.timeRemind1 = timeRemind1; this.timeRemind2 = timeRemind2;
        this.daySunday = daySunday; this.dayMonday = dayMonday; this.dayTuesday = dayTuesday;
        this.dayWednesday = dayWednesday; this.dayThursday = dayThursday;
        this.dayFriday = dayFriday; this.daySaturday = daySaturday;
        this.startDate = startDate; this.endDate = endDate;
    }

    protected PrescriptionInfo(Parcel in) {
        mMedName = in.readString();
        mMedDescription = in.readString();
        mFrequency = in.readInt();
        timeRemind = in.readString();
        timeRemind1 = in.readString();
        timeRemind2 = in.readString();
        daySunday = in.readInt();
        dayMonday = in.readInt();
        dayTuesday = in.readInt();
        dayWednesday = in.readInt();
        dayThursday = in.readInt();
        dayFriday = in.readInt();
        daySaturday = in.readInt();
        startDate = in.readLong();
        endDate = in.readLong();

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mMedName);
        dest.writeString(mMedDescription);
        dest.writeInt(mFrequency);
        dest.writeString(timeRemind);
        dest.writeString(timeRemind1);
        dest.writeString(timeRemind2);
        dest.writeInt(daySunday);
        dest.writeInt(dayMonday);
        dest.writeInt(dayTuesday);
        dest.writeInt(dayWednesday);
        dest.writeInt(dayThursday);
        dest.writeInt(dayFriday);
        dest.writeInt(daySaturday);
        dest.writeLong(startDate);
        dest.writeLong(endDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PrescriptionInfo> CREATOR = new Creator<PrescriptionInfo>() {
        @Override
        public PrescriptionInfo createFromParcel(Parcel in) {
            return new PrescriptionInfo(in);
        }

        @Override
        public PrescriptionInfo[] newArray(int size) {
            return new PrescriptionInfo[size];
        }
    };

    public String getMedName() {return this.mMedName;}
    public void setMedName(String medName) {this.mMedName = medName;}

    public String getMedDescription() {return this.mMedDescription;}
    public void setMedDescription(String description) {this.mMedDescription = description;}

    public int getFrequency() {return this.mFrequency;}
    public void setFrequency(int freq) {this.mFrequency = freq;}

    public String getTimeRemind() {return this.timeRemind;}
    public void setTimeRemind(String timeRemind) {this.timeRemind = timeRemind;}

    public String getTimeRemind1() {return this.timeRemind1;}
    public void setTimeRemind1(String timeRemind1) {this.timeRemind1 = timeRemind1;}

    public String getTimeRemind2() {return this.timeRemind2;}
    public void setTimeRemind2(String timeRemind2) {this.timeRemind2 = timeRemind2;}

    public int getDaySunday() {return this.daySunday;}
    public void setDaySunday(int daySunday) {this.daySunday = daySunday;}

    public int getDayMonday() {return this.dayMonday;}
    public void setDayMonday(int dayMonday) {this.dayMonday = dayMonday;}

    public int getDayTuesday() {return this.dayTuesday;}
    public void setDayTuesday(int dayTuesday) {this.dayTuesday = dayTuesday;}

    public int getDayWednesday() {return this.dayWednesday;}
    public void setDayWednesday(int dayWednesday) {this.dayWednesday = dayWednesday;}

    public int getDayThursday() {return this.dayThursday;}
    public void setDayThursday(int dayThursday) {this.dayThursday = dayThursday;}

    public int getDayFriday() {return this.dayFriday;}
    public void setDayFriday(int dayFriday) {this.dayFriday = dayFriday;}

    public int getDaySaturday() {return this.daySaturday;}
    public void setDaySaturday(int daySaturday) {this.daySaturday = daySaturday;}

    public long getStartDate() {return this.startDate;}
    public void setStartDate(long startDate) {this.startDate = startDate;}

    public long getEndDate() {return this.endDate;}
    public void setEndDate(long endDate) {this.endDate = endDate;}
}
