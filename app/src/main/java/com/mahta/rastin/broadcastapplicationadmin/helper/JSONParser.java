package com.mahta.rastin.broadcastapplicationadmin.helper;

import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.model.Field;
import com.mahta.rastin.broadcastapplicationadmin.model.Group;
import com.mahta.rastin.broadcastapplicationadmin.model.Media;
import com.mahta.rastin.broadcastapplicationadmin.model.Message;
import com.mahta.rastin.broadcastapplicationadmin.model.Post;
import com.mahta.rastin.broadcastapplicationadmin.model.Program;
import com.mahta.rastin.broadcastapplicationadmin.model.UserToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JSONParser {

    public static int getResultCodeFromJson(String content){
        int resultCode;

        try {
            JSONObject obj = new JSONObject(content);
            resultCode = obj.getInt(Keys.KEY_RESULT_CODE);

        } catch (JSONException e) {
            G.e("error_getResultCodeFromJson: " + e.getMessage());
            return 0;
        }
        return resultCode;
    }

    public static int getLimitationCode(String content){
        int resultCode;

        try {
            JSONObject obj = new JSONObject(content);
            resultCode = obj.getInt(Keys.KEY_DATA);

        } catch (JSONException e) {
            G.e("error_getLimitCode: " + e.getMessage());
            return 0;
        }
        return resultCode;
    }

    public static UserToken parseToken(String content){

        String token = "";

        try {
            JSONObject obj = new JSONObject(content);
            token = obj.getString(Keys.KEY_DATA);

        } catch (JSONException e) {
            G.e("error_parseToken: " + e.getMessage());
        }
        return new UserToken(token);
    }

    public static List<Group> parseGroups(String content){

        try {
            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);

            if (data.length() > 0){
                List<Group> groupList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {

                    if (!data.isNull(i)){
                        JSONObject jgroup = data.getJSONObject(i);
                        Group group = new Group();

                        group.setId(jgroup.getInt(Keys.KEY_ID));
                        group.setTitle(jgroup.getString(Keys.KEY_TITLE));
                        groupList.add(group);
                    }
                }
                return groupList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("9: " + e.getMessage());
            return null;
        }
    }

    public static List<Field> parseFields(String content){

        try {
            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);

            if (data.length() > 0){
                List<Field> fieldList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {

                    if (!data.isNull(i)){
                        JSONObject jgroup = data.getJSONObject(i);
                        Field field = new Field();

                        field.setId(jgroup.getInt(Keys.KEY_ID));
                        field.setTitle(jgroup.getString(Keys.KEY_TITLE));
                        fieldList.add(field);
                    }
                }
                return fieldList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("9: " + e.getMessage());
            return null;
        }
    }


    public static List<Post> parsePosts(String content){

        try {

            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);

            if (data.length() > 0){
                List<Post> postList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {

                    if (!data.isNull(i)){

                        JSONObject jpost = data.getJSONObject(i);
                        Post post = new Post();

                        post.setId(jpost.getInt(Keys.KEY_ID));
                        post.setTitle(jpost.getString(Keys.KEY_TITLE));
                        post.setContent(jpost.getString(Keys.KEY_CONTENT));
                        post.setPreview(jpost.getString(Keys.KEY_PREVIEW));
                        post.setDate(jpost.getString(Keys.KEY_DATE_UPDATED));
                        postList.add(post);
                    }
                }
                return postList;
            }else
                return null;

        } catch (JSONException e) {

            e.printStackTrace();
            G.e("error_parsePosts: " + e.getMessage());
            return null;
        }
    }


    public static List<Program> parsePrograms(String content){

        try {
            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);

            if (data.length() > 0){
                List<Program> programList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    if (!data.isNull(i)){

                        JSONObject jprogram = data.getJSONObject(i);
                        Program program = new Program();

                        program.setId(jprogram.getInt(Keys.KEY_ID));
                        program.setTitle(jprogram.getString(Keys.KEY_TITLE));
                        program.setContent(jprogram.getString(Keys.KEY_CONTENT));
                        program.setPreview(jprogram.getString(Keys.KEY_PREVIEW));
                        program.setDate(jprogram.getString(Keys.KEY_DATE_UPDATED));

                        if (!jprogram.isNull(Keys.KEY_GROUP_ID))
                            program.setGroup_id(jprogram.getInt(Keys.KEY_GROUP_ID));

                        if (!jprogram.isNull(Keys.KEY_FIELD_ID))
                            program.setField_id(jprogram.getInt(Keys.KEY_FIELD_ID));

                        programList.add(program);
                    }
                }
                return programList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("error_parsePrograms: " + e.getMessage());
            return null;
        }
    }

    public static List<Message> parseMessages(String content){

        try {
            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);

            if (data.length() > 0){
                List<Message> messageList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    if (!data.isNull(i)){

                        JSONObject jprogram = data.getJSONObject(i);
                        Message message = new Message();

                        message.setId(jprogram.getInt(Keys.KEY_ID));
                        message.setTitle(jprogram.getString(Keys.KEY_TITLE));
                        message.setContent(jprogram.getString(Keys.KEY_CONTENT));
                        message.setDate(jprogram.getString(Keys.KEY_DATE_UPDATED));
                        message.setGroup_id(jprogram.getInt(Keys.KEY_GROUP_ID));
                        message.setField_id(jprogram.getInt(Keys.KEY_FIELD_ID));
                        message.setGender(jprogram.getInt(Keys.KEY_GENDER));
                        messageList.add(message);
                    }
                }
                return messageList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("error_parsePrograms: " + e.getMessage());
            return null;
        }
    }


    public static List<Media> parseMedia(String content){

        try {
            JSONObject obj = new JSONObject(content);
            JSONArray data = obj.getJSONArray(Keys.KEY_DATA);

            if (data.length() > 0){
                List<Media> mediaList = new ArrayList<>();

                for (int i = 0; i < data.length(); i++) {
                    if (!data.isNull(i)){
                        JSONObject jmedia = data.getJSONObject(i);
                        Media media = new Media();

                        media.setId(jmedia.getInt(Keys.KEY_ID));
                        media.setTitle(jmedia.getString(Keys.KEY_TITLE));
                        media.setDescription(jmedia.getString(Keys.KEY_DESCRIPTION));
                        media.setPath(jmedia.getString(Keys.KEY_MEDIA));
                        media.setDate(jmedia.getString(Keys.KEY_DATE_UPDATED));
                        mediaList.add(media);
                    }
                }
                return mediaList;
            }else
                return null;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("error_parseMedia: " + e.getMessage());
            return null;
        }
    }


    public static Post parsePost(String content){

        try {
            JSONObject obj = new JSONObject(content);
            JSONObject pobj = obj.getJSONArray(Keys.KEY_DATA).getJSONObject(0);

            Post post = new Post();
            post.setId(pobj.getInt(Keys.KEY_ID));
            post.setTitle(pobj.getString(Keys.KEY_TITLE));
            post.setContent(pobj.getString(Keys.KEY_CONTENT));
            post.setPreview(pobj.getString(Keys.KEY_PREVIEW));
            post.setDate(pobj.getString(Keys.KEY_DATE_UPDATED));
            return post;

        } catch (JSONException e) {
            e.printStackTrace();
            G.e("error_parsePost: " + e.getMessage());
            return null;
        }
    }

}
