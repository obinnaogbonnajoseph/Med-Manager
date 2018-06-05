package com.example.android.med_manager.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.med_manager.R;
import com.example.android.med_manager.data.PrescriptionInfo;

import java.util.Date;
import java.util.Formatter;
import java.util.List;


/**
 * This is an adapter that adapts the cardview to the
 * the recycler view
 */
public class PrescriptionListAdapter extends RecyclerView.Adapter<
        PrescriptionListAdapter.PrescriptionViewHolder> {

    private final LayoutInflater mInflater;

    private AdapterOnClickHandler mClickHandler;

    private List<PrescriptionInfo> mMeds; // Cached copy of prescriptions

    /**
     * The interface that receives onClick messages.
     */
    public interface AdapterOnClickHandler {
        void onClick(PrescriptionInfo prescription);
    }

    public PrescriptionListAdapter(Context context, AdapterOnClickHandler clickHandler) {
        mInflater = LayoutInflater.from(context);
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public PrescriptionListAdapter.PrescriptionViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.recyclerview_item, parent, false);
        return new PrescriptionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionListAdapter.PrescriptionViewHolder holder,
                                 int position) {
        if(mMeds != null) {
            // Set the start day, month, drug name and description
            PrescriptionInfo currentMed = mMeds.get(position);
            // Get the start date
           Date startDate = new Date(currentMed.getStartDate());
            // Set the start day
            Formatter fmt = new Formatter();
            holder.day.setText(fmt.format("%td",startDate).toString());
            // Set the start month
            fmt = new Formatter();
            holder.month.setText(fmt.format("%tb",startDate).toString());
            // Set the drug name
            holder.medName.setText(currentMed.getMedName());
            // Set the drug description
            if(!TextUtils.isEmpty(currentMed.getMedDescription())) {
                String noDesc = "No Description";
                holder.description.setText(noDesc);
            }
            holder.description.setText(currentMed.getMedDescription());
        } else {
            // Covers the case of data not being ready yet.
            String noDay = "00";
            String noMonth = "Null";
            String noData = "No Data";
            holder.day.setText(noDay);
            holder.month.setText(noMonth);
            holder.medName.setText(noData);
            holder.description.setText(noData);
        }
    }

    public void setPrescriptions(List<PrescriptionInfo> prescriptions) {
        mMeds = prescriptions;
        notifyDataSetChanged();
    }

    // When this method is first called, prescriptions is not yet updated
    // We can't return null, so we set it to zero.
    @Override
    public int getItemCount() {
        if(mMeds != null) return mMeds.size();
        else return 0;
    }

    public class PrescriptionViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {

        private final TextView day, month, medName, description;

        PrescriptionViewHolder(View itemView) {
            super(itemView);
            day = itemView.findViewById(R.id.date);
            month = itemView.findViewById(R.id.month);
            medName = itemView.findViewById(R.id.med_name);
            description = itemView.findViewById(R.id.med_description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(mMeds.get(adapterPosition));
        }
    }
}
