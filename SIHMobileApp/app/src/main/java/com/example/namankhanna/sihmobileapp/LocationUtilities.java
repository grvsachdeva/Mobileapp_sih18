package com.example.namankhanna.sihmobileapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

public class LocationUtilities {

    public static GoogleApiClient mGoogleApiClient;
    public static boolean checkPermission(Context context)
    {
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }
        else
            return true;
    }
    public static void getCurrentLocation(Context context)
    {
        if(!checkPermission(context))
        {
            Log.v(LocationUtilities.class.getSimpleName(),"Permission not available");
            return;
        }

        mGoogleApiClient = new GoogleApiClient
                .Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();
        mGoogleApiClient.connect();

        try{
            PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                    .getCurrentPlace(mGoogleApiClient, null);
            result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {
                @Override
                public void onResult(PlaceLikelihoodBuffer placeLikelihoods) {

                    for (PlaceLikelihood placeLikelihood : placeLikelihoods) {
                        Log.v(LocationUtilities.class.getSimpleName(),placeLikelihood.getPlace().getName().toString());
                    }

                    placeLikelihoods.release();
                }
            });

        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
