package com.mahta.rastin.broadcastapplicationadmin.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;

import io.realm.RealmResults;

public class ProgramAdapter extends RealmRecyclerViewAdapter<Program,ProgramAdapter.CustomViewHolder>{

    private LayoutInflater inflater;
    private com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener onItemClickListener;

    public ProgramAdapter(Context context, RealmResults<Program> realmResults) {
        super(context, realmResults);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_program_adapter_item,parent,false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        final Program current = realmResults.get(position); //This is nice

        holder.txtTitle.setText(current.getTitle());
        holder.txtPreview.setText(current.getPreview());

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

        TextView txtTitle;
        TextView txtPreview;
        TextView txtDate;
        LinearLayout lnlListItem;

        private CustomViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtBack);
            txtPreview = itemView.findViewById(R.id.txtPreview);
            txtDate = itemView.findViewById(R.id.txtDate);
            lnlListItem = itemView.findViewById(R.id.lnlListItem);

            lnlListItem.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (onItemClickListener != null)
                onItemClickListener.onItemClicked(view,getAdapterPosition());
        }

    }

    public void setOnItemClickListener(com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}