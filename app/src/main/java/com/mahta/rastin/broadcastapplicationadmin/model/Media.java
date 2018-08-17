package com.mahta.rastin.broadcastapplicationadmin.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Media extends RealmObject {

    @PrimaryKey
    private int id;

    private String title;

    private String description;

    private String path;

    private String date;

    public Media(){}

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

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}