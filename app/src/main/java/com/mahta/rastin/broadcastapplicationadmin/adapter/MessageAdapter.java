package com.mahta.rastin.broadcastapplicationadmin.adapter;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mahta.rastin.broadcastapplicationadmin.R;
import com.mahta.rastin.broadcastapplicationadmin.dialog.DeleteDialog;
import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.helper.HttpCommand;
import com.mahta.rastin.broadcastapplicationadmin.helper.JSONParser;
import com.mahta.rastin.broadcastapplicationadmin.helper.RealmController;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnDeleteListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.model.Message;

import io.realm.RealmResults;

public class MessageAdapter extends RealmRecyclerViewAdapter<Message,MessageAdapter.CustomViewHolder>{

    private LayoutInflater inflater;
    private com.mahta.rastin.broadcastapplicationadmin.interfaces.OnItemClickListener onItemClickListener;
    private Activity activity;
    Message message;

    public MessageAdapter(Activity activity, RealmResults<Message> realmResults) {
        super(activity, realmResults);
        inflater = LayoutInflater.from(activity);
        this.activity = activity;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.layout_message_adapter_item,parent,false);
        return new MessageAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {

        message = realmResults.get(position); //This is nice
        holder.txtTitle.setText(message.getTitle());

        try {
            com.mahta.rastin.broadcastapplication.helper.DateConverter converter = new com.mahta.rastin.broadcastapplication.helper.DateConverter();
            String[] date = message.getDate().split(" ")[0].split("-");

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
        TextView txtDate;
        RelativeLayout lnlListItem;
        ImageButton imgDelete;

        private CustomViewHolder(View itemView) {
            super(itemView);

            txtTitle = itemView.findViewById(R.id.txtBack);
            txtDate = itemView.findViewById(R.id.txtDate);
            lnlListItem = itemView.findViewById(R.id.lnlListItem);
            imgDelete = itemView.findViewById(R.id.imgbtn_delete);

            lnlListItem.setOnClickListener(this);

            imgDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DeleteDialog deleteDialog = new DeleteDialog(activity);

                    deleteDialog.setOnDeleteListener(new OnDeleteListener() {
                        @Override
                        public void onDeleteItem(boolean confirm) {

                            if (confirm) {

                                ContentValues contentValues = new ContentValues();

                                contentValues.put(Keys.KEY_TOKEN, RealmController.getInstance().getUserToken().getToken());

                                new HttpCommand(HttpCommand.COMMAND_DELETE_MESSAGE, contentValues, message.getId()+"").setOnResultListener(new OnResultListener() {
                                    @Override
                                    public void onResult(String result) {

                                        if (JSONParser.getResultCodeFromJson(result) == Keys.RESULT_SUCCESS) {
                                            G.toastShort("پیام با موفقیت حذف شد", activity);
                                        }
                                    }
                                }).execute();
                            }

                        }
                    });

                    deleteDialog.show();
                }
            });
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
