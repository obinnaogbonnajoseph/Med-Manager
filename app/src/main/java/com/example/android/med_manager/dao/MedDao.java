package com.example.android.med_manager.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.RoomWarnings;
import android.arch.persistence.room.Update;

import com.example.android.med_manager.data.PrescriptionInfo;

import java.util.List;

/**
 * This is an adapter that adapts the cardview to the
 * the recycler view
 */
@Dao
public interface MedDao {

    @Query("SELECT * FROM med_table")
    LiveData<List<PrescriptionInfo>> getAllMeds();

    // For search results
    @Query("SELECT * FROM med_table WHERE med_name LIKE :search")
    List<PrescriptionInfo> getResults(String search);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(PrescriptionInfo... prescriptions);

    @Delete
    void deleteMeds(PrescriptionInfo... prescriptions);

    @Update
    void updateMeds(PrescriptionInfo prescription);
}
