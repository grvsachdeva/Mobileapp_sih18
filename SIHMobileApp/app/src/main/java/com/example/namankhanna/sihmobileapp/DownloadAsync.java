package com.example.namankhanna.sihmobileapp;


import android.os.AsyncTask;

public class DownloadAsync extends AsyncTask<String, Void, String> {
    onDownloadResponse odr;

    public DownloadAsync(onDownloadResponse odr) {
        this.odr = odr;
    }

    @Override
    protected String doInBackground(String... strings) {

        String url1 = strings[0];
        String url2 = strings[1];
        String confidence = FaceRecognition.sendNotification(url1, url2);
        return confidence;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        odr.returnConfidence(s);
    }


    interface onDownloadResponse {

        void returnConfidence(String confidence);
    }
}