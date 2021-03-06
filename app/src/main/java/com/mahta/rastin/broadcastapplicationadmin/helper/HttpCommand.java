package com.mahta.rastin.broadcastapplicationadmin.helper;

import android.content.ContentValues;

import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.global.G;

import java.io.File;
import java.io.IOException;


public class HttpCommand {

    private OnResultListener onResultListener;
    private String currentCommand, title, description;
    private ContentValues currentParams;
    private String[] currentArgs;
    private HttpManager httpManager;
    private File file;

    //Commands
    public static final String COMMAND_AUTHORIZE = "authorize";
    public static final String COMMAND_CONFIRM = "confirm";

    public static final String COMMAND_GET_WORKBOOK = "get workbook";
    public static final String COMMAND_GET_INFO = "get info";
    public static final String COMMAND_GET_POST = "get post";
    public static final String COMMAND_SEND_TICKET = "send ticket";
    public static final String COMMAND_GET_LAST_NOTIFICATIONS = "get last notifications";
    public static final String COMMAND_GET_PASSWORD = "get password";
    public static final String COMMAND_CHANGE_PASSWORD = "change password";


    public static final String COMMAND_CHECK_TOKEN = "check token";
    public static final String COMMAND_GET_GROUP_LIST = "get group list";
    public static final String COMMAND_GET_FIELD_LIST = "get field list";
    public static final String COMMAND_LOGIN = "login";

    public static final String COMMAND_GET_POSTS = "get posts";

    public static final String COMMAND_CREATE_POST = "create post";
    public static final String COMMAND_UPDATE_POST = "update post";
    public static final String COMMAND_DELETE_POST = "delete post";

    public static final String COMMAND_CREATE_PROGRAM = "create program";
    public static final String COMMAND_UPDATE_PROGRAM = "update program";
    public static final String COMMAND_DELETE_PROGRAM = "delete program";

    public static final String COMMAND_CREATE_MEDIA = "create media";
    public static final String COMMAND_UPDATE_MEDIA = "update media";
    public static final String COMMAND_DELETE_MEDIA = "delete media";

    public static final String COMMAND_CREATE_MESSAGE = "create message";
    public static final String COMMAND_UPDATE_MESSAGE = "update message";
    public static final String COMMAND_DELETE_MESSAGE = "delete message";


    public HttpCommand(String command, ContentValues params, String ... args){

        this.currentCommand = command;
        this.currentParams = params;
        this.currentArgs = args;
        httpManager = new HttpManager();
    }

    public HttpCommand(String command, File file, ContentValues params, String ... args){

        this.file = file;
        this.currentCommand = command;
        this.currentParams = params;
        this.currentArgs = args;
        httpManager = new HttpManager();
    }


    public void execute(){

        if (httpManager != null && onResultListener != null){

            switch (currentCommand) {

                case COMMAND_LOGIN:
                    setCommandLogin();
                    break;

                case COMMAND_AUTHORIZE:
                    commandAuthorize();
                    break;

                case COMMAND_GET_POSTS:
                    commandGetPosts();
                    break;

                case COMMAND_CONFIRM:
                    commandConfirm();
                    break;

                case COMMAND_GET_WORKBOOK:
                    commandGetWorkbook();
                    break;

                case COMMAND_GET_INFO:
                    commandGetInfo();
                    break;

                case COMMAND_GET_POST:
                    commandGetPost();
                    break;

                case COMMAND_SEND_TICKET:
                    commandSendTicket();
                    break;

                case COMMAND_GET_LAST_NOTIFICATIONS:
                    commandGetLastNotifications();
                    break;

                case COMMAND_GET_PASSWORD:
                    commandGetPassword();
                    break;

                case COMMAND_CHANGE_PASSWORD:
                    commandChangePassword();
                    break;

                case COMMAND_GET_GROUP_LIST:
                    commandGetGroupList();
                    break;

                case COMMAND_CREATE_POST:
                    commandCreatePost();
                    break;

                case COMMAND_UPDATE_POST:
                    commandUpdatePost();
                    break;

                case COMMAND_DELETE_POST:
                    commandDeletePost();
                    break;

                case COMMAND_CREATE_MEDIA:
                    try {
                        commandCreateMedia();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case COMMAND_CHECK_TOKEN:
                        commandCheckToken();
                    break;

                case COMMAND_UPDATE_PROGRAM:
                    commandUpdateProgram();
                    break;

                case COMMAND_DELETE_PROGRAM:
                    commandDeleteProgram();
                    break;

                case COMMAND_DELETE_MEDIA:
                    commandDeleteMedia();
                    break;

                case COMMAND_CREATE_PROGRAM:
                    commandCreateProgram();
                    break;

                case COMMAND_CREATE_MESSAGE:
                    commandCreateMessage();
                    break;

                case COMMAND_UPDATE_MESSAGE:
                    commandUpdateMessage();
                    break;

                case COMMAND_DELETE_MESSAGE:
                    commandDeleteMessage();
                    break;

                case COMMAND_UPDATE_MEDIA:

                    if (file != null) {

                        try {
                            commandUpdateMediaFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        commandUpdateMediaNoFile();
                    }
                    break;


                case COMMAND_GET_FIELD_LIST:
                    commandGetFieldList();
                    break;


                default:
                    G.e("Invalid Command");
            }
        }
        else
            G.e("Inappropriate Command Structure");
    }

    public HttpCommand setOnResultListener(OnResultListener listener){

        onResultListener = listener;

        httpManager.setOnResultListener(new OnResultListener() {
            @Override
            public void onResult(String result) {

                if(onResultListener != null){
                    onResultListener.onResult(result);

                    G.i("result : " + result);
                }
            }
        });
        return this;
    }

    private void commandAuthorize(){ httpManager.post(G.BASE_URL+"student/authorize",currentParams, currentArgs); }

    private void commandConfirm(){ httpManager.post(G.BASE_URL+"student/confirm",currentParams, currentArgs); }

    private void commandGetWorkbook(){ httpManager.post(G.BASE_URL+"student/workbook",currentParams, currentArgs); }



    private void commandGetPost(){ httpManager.get(G.BASE_URL+"post",currentArgs);}

    private void commandGetInfo() { httpManager.post(G.BASE_URL+"student/info",currentParams, currentArgs); }

    private void commandSendTicket() { httpManager.post(G.BASE_URL+"send_ticket",currentParams, currentArgs); }

    private void commandGetLastNotifications(){ httpManager.get(G.BASE_URL+"notification",currentArgs);}

    private void commandGetPassword() { httpManager.post(G.BASE_URL+"student/get_password",currentParams, currentArgs); }

    private void commandChangePassword() { httpManager.post(G.BASE_URL+"student/change_password",currentParams, currentArgs); }



    private void commandCheckToken() { httpManager.post(G.BASE_URL+"check_token",currentParams, currentArgs);}

    private void setCommandLogin() {httpManager.post(G.BASE_URL+"login", currentParams, currentArgs);}

    private void commandGetGroupList() { httpManager.post(G.BASE_URL+"groups",currentParams, currentArgs); }

    private void commandGetFieldList() { httpManager.post(G.BASE_URL+"fields",currentParams, currentArgs);}

    private void commandCreatePost() { httpManager.post(G.BASE_URL+"post/create", currentParams, currentArgs );}

    private void commandUpdatePost() {httpManager.post(G.BASE_URL+"post/update", currentParams, currentArgs);}

    private void commandGetPosts(){ httpManager.post(G.BASE_URL+"posts",currentParams, currentArgs);}

    private void commandDeletePost() { httpManager.post(G.BASE_URL+"post/delete",currentParams, currentArgs);}

    private void commandCreateProgram() { httpManager.post(G.BASE_URL+"program/create",currentParams, currentArgs); }

    private void commandUpdateProgram() { httpManager.post(G.BASE_URL+"program/update",currentParams, currentArgs);}

    private void commandDeleteProgram() { httpManager.post(G.BASE_URL+"program/delete",currentParams, currentArgs);  }

    private void commandCreateMedia() throws IOException { httpManager.upload(G.BASE_URL+"media/create", file, currentParams, currentArgs);}

//    private void commandCreateMedia() throws IOException {
//        try {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                new UploadProgress(G.BASE_URL+"media/create", file, currentParams, currentArgs);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void commandUpdateMediaFile() throws IOException { httpManager.upload(G.BASE_URL+"media/update", file, currentParams, currentArgs); }

    private void commandUpdateMediaNoFile() { httpManager.post(G.BASE_URL+"media/update",currentParams, currentArgs); }

    private void commandDeleteMedia() { httpManager.post(G.BASE_URL+"media/delete",currentParams, currentArgs); }

    private void commandCreateMessage() { httpManager.post(G.BASE_URL+"message/create",currentParams, currentArgs);   }

    private void commandUpdateMessage() { httpManager.post(G.BASE_URL+"message/update",currentParams, currentArgs);   }

    private void commandDeleteMessage() {  httpManager.post(G.BASE_URL+"message/delete",currentParams, currentArgs);   }

}


