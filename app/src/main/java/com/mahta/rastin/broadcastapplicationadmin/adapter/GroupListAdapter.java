package com.mahta.rastin.broadcastapplicationadmin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;

import java.util.List;

public class GroupListAdapter extends RecyclerView.Adapter<GroupListAdapter.GroupViewHolder> {

    private LayoutInflater inflater;
    private List<Group> data;
    private OnItemClickListener onItemClickListener;

    public GroupListAdapter(Context context, List<Group> data) {

        inflater = LayoutInflater.from(context);
        this.data = data;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = inflater.inflate(R.layout.layout_group_list_item,parent,false);

        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {

        final Group group = data.get(position); //This is nice
        holder.grouptitle.setText(group.getTitle());
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    public class GroupViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView grouptitle;
        LinearLayout layoutItem;

        private GroupViewHolder(View itemView) {
            super(itemView);

            grouptitle = itemView.findViewById(R.id.txtGroup);
            layoutItem = itemView.findViewById(R.id.groupItem);

            layoutItem.setOnClickListener(this);
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
