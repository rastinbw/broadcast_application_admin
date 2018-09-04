package com.mahta.rastin.broadcastapplicationadmin.helper;

import android.content.ContentValues;
import android.util.Log;

import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.global.G;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpManager{

    private OnResultListener onResultListener;
    private OkHttpClient client;
    private String httpResult;
    private static final int CONNECT_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 30;

    HttpManager(){
        client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT,TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build();
    }

    public void post(String url, ContentValues params, String[] args){

        StringBuilder aBuilder = new StringBuilder();
        aBuilder.append(url);

        if (args != null && args.length > 0)
            for (String arg : args) {
                aBuilder.append("/");
                aBuilder.append(arg);
            }

        G.i(aBuilder.toString());

        FormBody.Builder builder = new FormBody.Builder();

        if (params != null && params.size() > 0)

            for (String key : params.keySet()) {
                builder.add(key,params.getAsString(key));
            }

        RequestBody body = builder.build();


        Request request = new Request.Builder()
                .url(aBuilder.toString())
                .post(body)
                .build();

        doRequest(request);
    }

    public void upload(String url, File file, String title, String description, String[] args) throws IOException {

        StringBuilder aBuilder = new StringBuilder();
        aBuilder.append(url);

        if (args != null && args.length > 0)
            for (String arg : args) {
                aBuilder.append("/");
                aBuilder.append(arg);
            }

        G.i(aBuilder.toString());

        RequestBody formBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("media", file.getName(),
                          RequestBody.create(MediaType.parse("audio/mpeg"), file))

                .addFormDataPart("token", RealmController.getInstance().getUserToken().getToken())
                .addFormDataPart("title", title)
                .addFormDataPart("description", description)
                .build();



        Request request = new Request.Builder()
                .url(aBuilder.toString())
                .post(formBody)
                .build();

        G.i(RealmController.getInstance().getUserToken().getToken());

        doRequest(request);
    }


    public void get(String url,String[] args){

        StringBuilder builder = new StringBuilder();
        builder.append(url);

        if (args != null && args.length>0)
            for (String arg:args) {
                builder.append("/");
                builder.append(arg);
            }

//        try {
//            URLEncoder.encode(builder.toString(), "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

        G.i(builder.toString()); // TODO: 6/3/18 remove this line
        Request request = new Request.Builder()
                .url(builder.toString())
                .get()
                .build();

        doRequest(request);
    }


    private void doRequest(final Request request){

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Response response;

                try {
                    response = client.newCall(request).execute();
                    if (!response.isSuccessful()) {

                        G.e(response.body().string());
                    }else {

                        httpResult = response.body().string();

                        if(onResultListener!=null){
                            G.HANDLER.post(new Runnable() {
                                @Override
                                public void run() {
                                    onResultListener.onResult(httpResult);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    G.e(e.getMessage());
                }
            }
        });
        thread.start();

    }

    public void setOnResultListener(OnResultListener onResultListener){
        this.onResultListener = onResultListener;
    }

}
