package com.mahta.rastin.broadcastapplicationadmin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Field;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.CustomViewHolder> {

    private LayoutInflater inflater;
    private List<Group> groupList;
    private OnItemClickListener onItemClickListener;

    public GroupAdapter(Context context, List<Group> groupList) {
        inflater = LayoutInflater.from(context);

            this.groupList = groupList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.layout_filter_adapter_item,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        final Group current = groupList.get(position);
        holder.txtTitle.setText(current.getTitle());
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView txtTitle;
        RelativeLayout rtlListItem;

        private CustomViewHolder(View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.txtBack);
            rtlListItem = itemView.findViewById(R.id.rtlListItem);

            rtlListItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClicked(v,getAdapterPosition());
        }

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}