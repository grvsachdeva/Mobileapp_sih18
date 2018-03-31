package com.example.namankhanna.sihmobileapp;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NotificationServices {
    public static final String URL_FCM = "https://fcm." +
            "googleapis.com/fcm/send";
    public static String APP_KEY = "AIzaSyDLMEbTuS94nKon39FTdohMZ8Azg65lfTg";

    public static void sendNotification(String token)
    {
        String postData = "{\"to\": \"" + token + "\" ,\n" +
                "\t\"notification\": {\n" +
                "\t\t\"body\": \"Please mark your attendance\" ,\n" +
                "\t\t\"title\" : \"Message from Admin\",\n" +
                "\t\t\"tag\" : \"1\",\n" +
                "\t\t\"color\" : \"#5566FF\"\n" +
                "\t},\n" +
                "\t\"collapse_key\" : \"notifications\"\n" +
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

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
                Log.v("Notifcation : ",inputLine);
            }
            in.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private static HttpURLConnection getConnection() throws Exception {
        URL url = new URL(URL_FCM);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestProperty("Authorization","key=" + APP_KEY);
        httpURLConnection.setRequestProperty("Content-Type", "application/json; UTF-8");
        return httpURLConnection;
    }

    public static class sendNotifications extends AsyncTask<String,Void,Void>
    {
        @Override
        protected Void doInBackground(String... strings) {
            String token = strings[0];
            sendNotification(token);
            return null;
        }
    }
}
