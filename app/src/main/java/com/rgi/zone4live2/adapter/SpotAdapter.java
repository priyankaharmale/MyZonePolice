package com.rgi.zone4live2.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.rgi.zone4live2.R;
import com.rgi.zone4live2.databinding.ItemSpotBinding;
import com.rgi.zone4live2.databinding.ItemUserBinding;
import com.rgi.zone4live2.model.SpotDataModel;

import java.util.List;

public class SpotAdapter extends RecyclerView.Adapter<SpotAdapter.ViewHolder> {
    public static final String TAG = UserAdapter.class.getName();
    private LayoutInflater layoutInflater;
    private Context mcontext;
    List<SpotDataModel.SpotDatum> userData;
    private OnItemClickListener mListener;

    public void updateList(List<SpotDataModel.SpotDatum> temp) {
        userData = temp;
        notifyDataSetChanged();
    }


    public interface OnItemClickListener {
        void onItemClickMerge(SpotDataModel.SpotDatum spotData,int position);
        void onItemClickUpdate(SpotDataModel.SpotDatum spotData, int position);
        void onItemClickDelete(SpotDataModel.SpotDatum spotData,int position);
    }

    public SpotAdapter(Activity activity, List<SpotDataModel.SpotDatum> userData, OnItemClickListener listener) {
        this.mcontext = activity;
        this.userData = userData;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemSpotBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_spot, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(userData.size()>0){
            holder.itemProductBinding.setSpotDetails(userData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return userData != null ? userData.size() : 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemSpotBinding itemProductBinding;

        public ViewHolder(@NonNull ItemSpotBinding itemView) {
            super(itemView.getRoot());
            this.itemProductBinding = itemView;
            //ivCheck = itemView.findViewById(R.id.ivCheck);
            itemProductBinding.btnMerge.setOnClickListener(v->{
                mListener.onItemClickMerge(userData.get(getAdapterPosition()),getAdapterPosition());
            });

            itemProductBinding.ivDelete.setOnClickListener(v->{
                mListener.onItemClickDelete(userData.get(getAdapterPosition()),getAdapterPosition());
            });
            itemProductBinding.ivEdit.setOnClickListener(v->{
                mListener.onItemClickUpdate(userData.get(getAdapterPosition()),getAdapterPosition());
            });
        }
    }
}

