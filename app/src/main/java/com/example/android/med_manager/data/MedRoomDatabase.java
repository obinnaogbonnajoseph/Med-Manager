package com.example.android.med_manager.data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.example.android.med_manager.adapter.DateTypeConverter;
import com.example.android.med_manager.dao.MedDao;

/**
 * This is an adapter that adapts the cardview to the
 * the recycler view
 */
@Database(entities = {PrescriptionInfo.class}, version = 1, exportSchema = false)
@TypeConverters({DateTypeConverter.class})
public abstract class MedRoomDatabase extends RoomDatabase {

    public abstract MedDao medDao();

    private static MedRoomDatabase INSTANCE;

    static MedRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MedRoomDatabase.class) {
                INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                        MedRoomDatabase.class, "med_database")
                        .build();
            }
        }
        return INSTANCE;
    }
}
