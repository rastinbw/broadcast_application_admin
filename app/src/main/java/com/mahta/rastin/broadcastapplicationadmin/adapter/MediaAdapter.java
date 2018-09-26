package com.mahta.rastin.broadcastapplicationadmin.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.custom.TextViewPlus;
import com.mahta.rastin.broadcastapplicationadmin.model.Media;

import io.realm.RealmResults;

public class MediaAdapter extends RealmRecyclerViewAdapter<Media,MediaAdapter.CustomViewHolder>{

    private LayoutInflater inflater;
    private com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener onItemDeleteListener;
    private com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener onItemClickListener;
    Media current = null;
    private Activity activity;

    public MediaAdapter(Activity activity, RealmResults<Media> realmResults) {
        super(activity, realmResults);
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_media_adapter_item,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        current = realmResults.get(position); //This is nice
        holder.txtTitle.setText(current.getTitle());
        holder.txtDesc.setText(current.getDescription());

        try {
            com.mahta.rastin.broadcastapplication.helper.DateConverter converter = new com.mahta.rastin.broadcastapplication.helper.DateConverter();
            String[] date = current.getDate().split(" ")[0].split("-");

            holder.txtDate.setText(converter.GregorianToPersian(
                    Integer.parseInt(date[0]),
                    Integer.parseInt(date[1]),
                    Integer.parseInt(date[2])
            ).toString());
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return realmResults.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextViewPlus txtTitle, txtDesc;
        TextView txtDate;
        RelativeLayout lnlListItem;
        ImageButton imgDelete;

        private CustomViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtBack);
            txtDesc = itemView.findViewById(R.id.txtPreview);
            txtDate = itemView.findViewById(R.id.txtDate);
            lnlListItem = itemView.findViewById(R.id.lnlListItem);
            imgDelete = itemView.findViewById(R.id.imgbtn_delete);

            lnlListItem.setOnClickListener(this);

            imgDelete.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            final int id = view.getId();

            switch (id) {

                case R.id.lnlListItem:
                    if (onItemClickListener != null)
                        onItemClickListener.onItemClicked(view, getAdapterPosition());
                    break;

                case R.id.imgbtn_delete:
                    if (onItemDeleteListener != null)
                        onItemDeleteListener.onItemClicked(view, getAdapterPosition());
                    break;
            }
        }

    }

    public void setOnItemClickListener(com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemDeleteListener(com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener onItemDeleteListener){
        this.onItemDeleteListener = onItemDeleteListener;
    }
}