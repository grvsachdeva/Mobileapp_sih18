package com.example.namankhanna.sihmobileapp;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static String LOG_TAG = MyFirebaseInstanceIDService.class.getSimpleName();
    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(LOG_TAG, "Refreshed token: " + refreshedToken);

        sendRegistrationToServer(refreshedToken);
    }
    private void sendRegistrationToServer(String token) {

        // This method is blank, but if you were to build a server that stores users token
        // information, this is where you'd send the token to the server.
    }
}
