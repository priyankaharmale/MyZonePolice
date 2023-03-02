package com.rgi.zone4live2.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.rgi.zone4live2.R;
import com.rgi.zone4live2.databinding.ItemUserBinding;
import com.rgi.zone4live2.databinding.ItemUserSpotBinding;
import com.rgi.zone4live2.model.SpotDataModel;

import java.util.List;

public class UserSpotAdapter extends RecyclerView.Adapter<UserSpotAdapter.ViewHolder> {
    public static final String TAG = UserAdapter.class.getName();
    private LayoutInflater layoutInflater;
    private Context mcontext;
    List<SpotDataModel.SpotDatum> userData;
    private UserAdapter.OnItemClickListener mListener;



    public UserSpotAdapter(Activity activity, List<SpotDataModel.SpotDatum> userData) {
        this.mcontext = activity;
        this.userData = userData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (layoutInflater == null) {
            layoutInflater = LayoutInflater.from(parent.getContext());
        }
        ItemUserSpotBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_user_spot, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (userData.size() > 0) {
            holder.itemProductBinding.setSpotDetails(userData.get(position));
            holder.itemProductBinding.cbAssign.setOnClickListener(v->{
                if(holder.itemProductBinding.cbAssign.isChecked()){
                    userData.get(position).setAssigned("assigned");
                }else{
                    userData.get(position).setAssigned("unassigned");
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return userData != null ? userData.size() : 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserSpotBinding itemProductBinding;

        public ViewHolder(@NonNull ItemUserSpotBinding itemView) {
            super(itemView.getRoot());
            this.itemProductBinding = itemView;
            //ivCheck = itemView.findViewById(R.id.ivCheck);
        }
    }
}
