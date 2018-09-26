package com.mahta.rastin.broadcastapplicationadmin.helper;

import android.content.ContentValues;

import com.mahta.rastin.broadcastapplicationadmin.global.G;
import com.mahta.rastin.broadcastapplicationadmin.global.Keys;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.OnResultListener;
import com.mahta.rastin.broadcastapplicationadmin.interfaces.UploadProgressListener;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.internal.Util;
import okio.Buffer;
import okio.BufferedSink;
import okio.ByteString;
import okio.Okio;
import okio.Source;

public class UploadProgress {

    //this class is for upload progress

    private static final int CONNECT_TIMEOUT = 10;
    private static final int WRITE_TIMEOUT = 10;
    private static final int READ_TIMEOUT = 30;

    private OnResultListener onResultListener;
    private String httpResult;
    private OkHttpClient client;
    private String token, title, description, url;
    private String[] args;
    ContentValues params;
    private File file;

    private UploadProgressListener listener;

    public UploadProgress(String url, File file, ContentValues params, String[] args) throws Exception {

        client = new OkHttpClient.Builder()
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .build();

        this.params = params;
        this.file = file;
        this.url = url;
        this.args = args;

        run();
    }


    public void run() throws Exception {

        token = params.get(Keys.KEY_TOKEN).toString();
        title = params.get(Keys.KEY_TITLE).toString();
        description = params.get(Keys.KEY_DESCRIPTION).toString();

        StringBuilder aBuilder = new StringBuilder();
        aBuilder.append(url);

        if (args != null && args.length > 0)
            for (String arg : args) {
                aBuilder.append("/");
                aBuilder.append(arg);
            }


        listener = new UploadProgressListener() {
            @Override
            public void transferred(long num) {
                G.i(num + "");
            }
        };



        RequestBody requestBody = new CustomRequestBody(file, "audio/mpeg", listener);


//        OkHttpClient client = new OkHttpClient.Builder()
//                .addInterceptor(new Interceptor() {
//
//                    @Override
//                    public Response intercept(Interceptor.Chain chain) throws IOException {
//
//                        Request request = chain.request();
//                        Request.Builder requestBuilder = request.newBuilder();
//
//                        RequestBody formBody = new FormEncodingBuilder()
//                                .add("email", "Jurassic@Park.com")
//                                .add("tel", "90301171XX")
//                                .build();
//
//                        String postBodyString = Utils.bodyToString(request.body());
//                        postBodyString += ((postBodyString.length() > 0) ? "&" : "") +  Utils.bodyToString(formBody);
//
//                        request = requestBuilder
//                                .post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString))
//                                .build();
//
//                        return chain.proceed(request);
//                    }
//                })
//                .build();
//
//        public static String bodyToString(final RequestBody request){
//
//            try {
//                final RequestBody copy = request;
//                final Buffer buffer = new Buffer();
//
//                if(copy != null)
//                    copy.writeTo(buffer);
//
//                else
//                    return "";
//
//                return buffer.readUtf8();
//            }
//            catch (final IOException e) {
//
//                return "did not work";
//            }
//        }


        RequestBody formBody = new MultipartBody.Builder()
                .addFormDataPart(Keys.KEY_TOKEN, token)
                .addFormDataPart(Keys.KEY_TITLE, title)
                .addFormDataPart(Keys.KEY_DESCRIPTION, description)
                .build();



//        RequestBody formBody = new MultipartBody.Builder()
//                .setType(MultipartBody.FORM)
//                .addFormDataPart(Keys.KEY_MEDIA, file.getName(),
//                        RequestBody.create(MediaType.parse("audio/mpeg"), file))
//
//                .addFormDataPart(Keys.KEY_TOKEN, token)
//                .addFormDataPart(Keys.KEY_TITLE, title)
//                .addFormDataPart(Keys.KEY_DESCRIPTION, description)
//                .build();



        Request request = new Request.Builder()
                .url(aBuilder.toString())
//                .post(formBody)
                .post(requestBody)
                .build();


        doRequest(request);
    }


    private void doRequest(final Request request){

        G.i("I'm in doRequest");

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

                        if(onResultListener != null){

                            G.HANDLER.post(new Runnable() {
                                @Override
                                public void run() {
                                    G.i(httpResult);

                                    onResultListener.onResult(httpResult);
                                }
                            });
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }



    public class CustomRequestBody extends RequestBody {

        private static final int SEGMENT_SIZE = 2048; // okio.Segment.SIZE

        private final File file;
        private final UploadProgressListener listener;
        private final String contentType;

        public CustomRequestBody(File file, String contentType, UploadProgressListener listener) {
            this.file = file;
            this.contentType = contentType;
            this.listener = listener;
        }


        @Override
        public long contentLength() {
            return file.length();
        }

        @Override
        public MediaType contentType() {
            return MediaType.parse(contentType);
        }

        @Override
        public void writeTo(BufferedSink sink) throws IOException {

            Source source = null;
            try {
                source = Okio.source(file);
                long total = 0;
                long read;

                while ((read = source.read(sink.buffer(), SEGMENT_SIZE)) != -1) {
                    total += read;
                    sink.flush();
                    this.listener.transferred(total);

                }
            } finally {
                Util.closeQuietly(source);
            }
        }

    }

}
