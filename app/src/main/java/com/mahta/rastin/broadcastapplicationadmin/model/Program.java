package com.mahta.rastin.broadcastapplicationadmin.model;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Program extends RealmObject implements Parcelable{
    @PrimaryKey
    private int id;

    private String title;

    private String content;

    private String preview;

    private String date;

    private String category;
    
    private int group_id;

    private int field_id;


    public Program(){}

    //parcelable part***********************************************************************
    protected Program(Parcel in) {
        id = in.readInt();
        title = in.readString();
        content = in.readString();
        preview = in.readString();
        date = in.readString();
        category = in.readString();
        group_id = in.readInt();
        field_id = in.readInt();
    }

    public static final Creator<Program> CREATOR = new Creator<Program>() {
        @Override
        public Program createFromParcel(Parcel in) {
            return new Program(in);
        }

        @Override
        public Program[] newArray(int size) {
            return new Program[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(preview);
        dest.writeString(date);
        dest.writeString(category);
        dest.writeInt(group_id);
        dest.writeInt(field_id);
    }
    //parcelable part***********************************************************************


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public int getField_id() {
        return field_id;
    }

    public void setField_id(int field_id) {
        this.field_id = field_id;
    }

}
