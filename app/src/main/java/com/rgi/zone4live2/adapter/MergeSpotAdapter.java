package com.rgi.zone4live2.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.rgi.zone4live2.R;
import com.rgi.zone4live2.databinding.ItemMergeSpotBinding;
import com.rgi.zone4live2.databinding.ItemUserSpotBinding;
import com.rgi.zone4live2.model.SpotDataModel;

import java.util.List;

public class MergeSpotAdapter extends RecyclerView.Adapter<MergeSpotAdapter.ViewHolder> {
    public static final String TAG = UserAdapter.class.getName();
    private LayoutInflater layoutInflater;
    private Context mcontext;
    List<SpotDataModel.SpotDatum> userData;
    private OnItemClickListener mListener;
    private int lastCheckedPosition = 0;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public void updateList(List<SpotDataModel.SpotDatum> temp) {
        userData = temp;
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClickSelect(SpotDataModel.SpotDatum spotDatum, int position);
    }

    public MergeSpotAdapter(Activity activity, List<SpotDataModel.SpotDatum> userData, OnItemClickListener listener) {
        this.mcontext = activity;
        this.userData = userData;
        mListener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemMergeSpotBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_merge_spot, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (userData.size() > 0) {
            holder.itemProductBinding.setSpotDetails(userData.get(position));
            if(lastCheckedPosition == position){
                holder.itemProductBinding.cbAssign.setChecked(true);
            }else
                holder.itemProductBinding.cbAssign.setChecked(false);

        }
    }


    @Override
    public int getItemCount() {
        return userData != null ? userData.size() : 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemMergeSpotBinding itemProductBinding;

        public ViewHolder(@NonNull ItemMergeSpotBinding itemView) {
            super(itemView.getRoot());
            this.itemProductBinding = itemView;
            itemProductBinding.cbAssign.setOnClickListener(v -> {
               // itemProductBinding.cbAssign.setChecked(true);
                if(lastCheckedPosition !=getAdapterPosition()) {
                    notifyItemChanged(lastCheckedPosition);
                    lastCheckedPosition = getAdapterPosition();
                }

                  mListener.onItemClickSelect(userData.get(getAdapterPosition()), getAdapterPosition());
            });
            //ivCheck = itemView.findViewById(R.id.ivCheck);
        }
    }
}

