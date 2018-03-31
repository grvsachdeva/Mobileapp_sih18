package com.example.namankhanna.sihmobileapp;

import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.common.api.Response;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class FaceRecognition {
    public static final String BASE_URL = "https://resultxx.herokuapp.com/";
    static String API_SECRET = "vhRNfRhhh-iTLjZjKctpBoqK4hw53RBO";
    static String API_KEY = "p7JtYLpdYeZrfEin-rcDEpyfl7r4NCYG";
    static String url1 = "https://firebasestorage.googleapis.com/v0/b/sihmobileapp-efa8d.appspot.com/o/pics%2Fnaman_01.jpg?alt=media&token=6676513e-d931-4709-a0f2-ce506cfc1240";
    static String url2 ="https://firebasestorage.googleapis.com/v0/b/sihmobileapp-efa8d.appspot.com/o/pics%2Fnaman_02.jpg?alt=media&token=751aef9a-c016-4e6a-b287-af965d8bafcb";

    public static void sendNotification(String image_url1,String image_url2)
    {
        String postData = "{\n" +
                "\t\"api_key\" : \"p7JtYLpdYeZrfEin-rcDEpyfl7r4NCYG\",\n" +
                "\t\"api_secret\" : \"vhRNfRhhh-iTLjZjKctpBoqK4hw53RBO\",\n" +
                "\t\"image_url1\" : \"" + image_url1 +"\",\n" +
                "\t\"image_url2\" : \"" + image_url2 +"\" \n" +
                "}";

        Log.v("Body : ",postData);
        try {
            HttpURLConnection httpConn = getConnection();
            httpConn.setDoOutput(true);
            httpConn.setUseCaches(false);
            httpConn.setRequestMethod("POST");
            DataOutputStream wr = new DataOutputStream(httpConn.getOutputStream());
            wr.writeBytes(postData);
            wr.flush();
            wr.close();
            Log.v("Request",wr.toString());

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                Log.v("-----Confidence----- : ",inputLine);
            }
            in.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static HttpURLConnection getConnection() throws Exception {
        URL url = new URL(BASE_URL);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
    }

    public static class DownloadAsync extends AsyncTask<String,Void,Void>
    {

        @Override
        protected Void doInBackground(String... strings) {
            sendNotification(url1,url2);
            return null;
        }
    }
}
