package com.example.android.med_manager.model;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.android.med_manager.data.MedRepository;
import com.example.android.med_manager.data.PrescriptionInfo;

import java.util.List;

/**
 * This is an adapter that adapts the cardview to the
 * the recycler view
 */
public class MedViewModel extends AndroidViewModel {

    private MedRepository mRepository;

    private LiveData<List<PrescriptionInfo>> mAllMeds;

    public MedViewModel(@NonNull Application application) {
        super(application);
        mRepository = new MedRepository(application);
        mAllMeds = mRepository.getAllMeds();
    }

    public LiveData<List<PrescriptionInfo>> getAllMeds() { return mAllMeds; }

    public void insert(PrescriptionInfo prescription) {mRepository.insert(prescription); }

    public void delete(PrescriptionInfo prescription) {mRepository.delete(prescription);}

    public void update(PrescriptionInfo prescription) {mRepository.update(prescription);}

    public List<PrescriptionInfo> query(String search) { return mRepository.query(search);}
}
