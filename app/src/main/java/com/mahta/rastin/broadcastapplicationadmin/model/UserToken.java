package com.mahta.rastin.broadcastapplicationadmin.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class UserToken extends RealmObject {

    @PrimaryKey
    private int id;

    @Required
    private String token;

    public UserToken(String token) {
        this.token = token;
    }

    public UserToken() { }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

}
