package com.mahta.rastin.broadcastapplicationadmin.helper;


import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;
import com.mahta.rastin.broadcastapplicationadmin.model.Media;
import com.mahta.rastin.broadcastapplicationadmin.model.Post;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;
import com.mahta.rastin.broadcastapplicationadmin.model.UserToken;

import java.util.List;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;

public class RealmController {

    private static RealmController instance = new RealmController();
    private final Realm realm = Realm.getInstance(
            new RealmConfiguration.Builder()
                    .name("com_mahta_rastin_broadcastapplication" + ".realm")
                    .build());

    private RealmController(){
        if(instance!=null){
            throw new IllegalStateException("Already instantiated");
        }
    }

    public static RealmController getInstance(){
        return instance;
    }

    public Realm getRealm() {
        return realm;
    }

    //Refresh the realm instance
    public void refresh() {
        realm.refresh();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on UserToken Model
     **********************************************************************************************/
    //find all objects in the UserToken.class
    public UserToken getUserToken() {
        return realm.where(UserToken.class).findFirst();
    }

    //check if UserToken.class is empty
    public boolean hasUserToken() {
        return !realm.where(UserToken.class).findAll().isEmpty();
    }

    //add a UserToken to Realm
    public void addUserToken(UserToken userToken){
        //Because each user can only have one UserToken
        removeUserToken();

        userToken.setId((int) (Math.random()*100));
        realm.beginTransaction();
        realm.copyToRealm(userToken);
        realm.commitTransaction();
    }

    //remove UserToken from realm
    public void removeUserToken() {
        realm.beginTransaction();
        RealmResults<UserToken> result = realm.where(UserToken.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on Group Model
     **********************************************************************************************/
    //find all objects in the Group.class
    public List<Group> getGroupList() {
        return realm.where(Group.class).findAll();
    }

    //check if Group.class is empty
    public boolean hasGroup() {
        return !realm.where(Group.class).findAll().isEmpty();
    }

    //add a Group to Realm
    public void addGroup(Group group){
        realm.beginTransaction();
        realm.copyToRealm(group);
        realm.commitTransaction();
    }

    //remove UserToken from realm
    public void clearGroup() {
        realm.beginTransaction();
        RealmResults<Group> result = realm.where(Group.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on Post Model
     **********************************************************************************************/
    //find all objects in the Post.class
    public RealmResults<Post> getAllPosts() {
        return realm.where(Post.class).findAll();
    }

    //check if Post.class is empty
    public boolean hasPosts() {
        return !realm.where(Post.class).findAll().isEmpty();
    }

    //clear all objects from Post.class
    public void clearAllPosts() {
        realm.beginTransaction();
        RealmResults<Post> result = realm.where(Post.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    //add a Post to Realm
    public void addPost(Post post){
        realm.beginTransaction();
        realm.copyToRealm(post);
        realm.commitTransaction();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on Media Model
     **********************************************************************************************/
    //find all objects in the Media.class
    public RealmResults<Media> getAllMedia() {
        return realm.where(Media.class).findAll();
    }

    //check if Media.class is empty
    public boolean hasMedia() {
        return !realm.where(Media.class).findAll().isEmpty();
    }

    //clear all objects from Post.class
    public void clearAllMedia() {
        realm.beginTransaction();
        RealmResults<Media> result = realm.where(Media.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    //add a Media to Realm
    public void addMedia(Media media){
        realm.beginTransaction();
        realm.copyToRealm(media);
        realm.commitTransaction();
    }

    /***********************************************************************************************
     * This Section Will Handle CRUD operation on Program Model
     **********************************************************************************************/
    //find all objects in the Post.class
    public RealmResults<Program> getAllPrograms() {
        return realm.where(Program.class).findAll();
    }

    //check if Post.class is empty
    public boolean hasPrograms() {
        return !realm.where(Program.class).findAll().isEmpty();
    }

    //clear all objects from Post.class
    public void clearAllPrograms() {
        realm.beginTransaction();
        RealmResults<Program> result = realm.where(Program.class).findAll();
        result.deleteAllFromRealm();
        realm.commitTransaction();
    }

    //add a Post to Realm
    public void addProgram(Program program){
        realm.beginTransaction();
        realm.copyToRealm(program);
        realm.commitTransaction();
    }


}
