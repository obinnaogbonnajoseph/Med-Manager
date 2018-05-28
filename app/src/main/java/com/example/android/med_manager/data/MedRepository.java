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

    public LiveData<List<PrescriptionInfo>> query(String search) {
        new queryAsyncTask(mMedDao).execute(search);
        return queryAsyncTask.searchResult();
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

    private static class queryAsyncTask extends AsyncTask<String, Void, LiveData<List<PrescriptionInfo>>> {

        private MedDao sAsyncTaskDao;
        private static LiveData<List<PrescriptionInfo>> sResults;

        queryAsyncTask(MedDao dao) {sAsyncTaskDao = dao;}

        @Override
        protected LiveData<List<PrescriptionInfo>> doInBackground(String... strings) {
            return sAsyncTaskDao.getResults(strings[0]);
        }

        @Override
        protected void onPostExecute(LiveData<List<PrescriptionInfo>> listLiveData) {
            super.onPostExecute(listLiveData);
            listLiveData = sResults;
        }

        public static LiveData<List<PrescriptionInfo>> searchResult() {
            return sResults;
        }
    }
}
