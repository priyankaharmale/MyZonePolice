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
import com.rgi.zone4live2.model.LoginModel;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {
    public static final String TAG = UserAdapter.class.getName();
    private LayoutInflater layoutInflater;
    private Context mcontext;
    List<LoginModel.UserData> userData;
    private OnItemClickListener mListener;

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClickShift(LoginModel.UserData userData,int position);
        void onItemClickSpot(LoginModel.UserData userData, int position);
        void onItemClickUpdate(LoginModel.UserData userData, int position);
        void onItemClickDelete(LoginModel.UserData userData,int position);
    }

    public UserAdapter(Activity activity, List<LoginModel.UserData> userData,OnItemClickListener listener) {
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
        ItemUserBinding binding = DataBindingUtil.inflate(layoutInflater, R.layout.item_user, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if(userData.size()>0){
            holder.itemProductBinding.setUserDetails(userData.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return userData != null ? userData.size() : 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ItemUserBinding itemProductBinding;

        public ViewHolder(@NonNull ItemUserBinding itemView) {
            super(itemView.getRoot());
            this.itemProductBinding = itemView;
            //ivCheck = itemView.findViewById(R.id.ivCheck);
            itemProductBinding.btnShift.setOnClickListener(v->{
                mListener.onItemClickShift(userData.get(getAdapterPosition()),getAdapterPosition());
            });
            itemProductBinding.btnSpot.setOnClickListener(v->{
                mListener.onItemClickSpot(userData.get(getAdapterPosition()),getAdapterPosition());
            });
            itemProductBinding.ivDelete.setOnClickListener(v->{
                mListener.onItemClickDelete(userData.get(getAdapterPosition()),getAdapterPosition());
            });
            itemProductBinding.ivEdit.setOnClickListener(v->{
                mListener.onItemClickUpdate(userData.get(getAdapterPosition()),getAdapterPosition());
            });
        }
    }

    public void updateList(List<LoginModel.UserData> list){
        userData = list;
        notifyDataSetChanged();
    }
}
