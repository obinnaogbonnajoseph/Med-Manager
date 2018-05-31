package com.example.android.med_manager.data;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.example.android.med_manager.dao.MedDao;

import java.util.List;

/**
 * This is an adapter that adapts the cardview to the
 * the recycler view
 */
public class MedRepository {

    private MedDao mMedDao;
    private LiveData<List<PrescriptionInfo>> mAllMeds;

    public MedRepository(Application application) {
        MedRoomDatabase db = MedRoomDatabase.getDatabase(application);
        mMedDao = db.medDao();
        mAllMeds = mMedDao.getAllMeds();
    }

    public LiveData<List<PrescriptionInfo>> getAllMeds() {
        return mAllMeds;
    }

    public void insert(PrescriptionInfo prescription) {
        new insertAsyncTask(mMedDao).execute(prescription);
    }

    public void delete(PrescriptionInfo prescription) {
        new deleteAsyncTask(mMedDao).execute(prescription);
    }

    public void update(PrescriptionInfo prescription) {
        new updateAsyncTask(mMedDao).execute(prescription);
    }

    public List<PrescriptionInfo> query(String search) {
        return mMedDao.getResults(search);
    }

    private static class insertAsyncTask extends AsyncTask<PrescriptionInfo, Void, Void> {

        private MedDao mAsyncTaskDao;

        insertAsyncTask(MedDao dao) {mAsyncTaskDao = dao;}

        @Override
        protected Void doInBackground(PrescriptionInfo... prescriptionInfos) {
            mAsyncTaskDao.insert(prescriptionInfos[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<PrescriptionInfo, Void, Void> {

        private MedDao mAsyncTaskDao;

        deleteAsyncTask(MedDao dao) {mAsyncTaskDao = dao;}

        @Override
        protected Void doInBackground(PrescriptionInfo... prescriptionInfos) {
            mAsyncTaskDao.deleteMeds(prescriptionInfos[0]);
            return null;
        }
    }

    private static class updateAsyncTask extends AsyncTask<PrescriptionInfo, Void, Void> {

        private MedDao mAsyncTaskDao;

        updateAsyncTask(MedDao dao) {mAsyncTaskDao = dao;}

        @Override
        protected Void doInBackground(PrescriptionInfo... prescriptionInfos) {
            mAsyncTaskDao.updateMeds(prescriptionInfos[0]);
            return null;
        }
    }
}
