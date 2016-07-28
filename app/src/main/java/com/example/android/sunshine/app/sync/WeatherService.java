package com.example.android.sunshine.app.sync;


import android.content.Context;
import android.content.Intent;
import android.location.Location;

import android.location.LocationListener;
import android.location.LocationManager;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;


public class WeatherService extends WearableListenerService
{

    public static final  String KEY_WEATHER_CONDITION   = "Condition";
    public static final  String KEY_WEATHER_SUNRISE     = "Sunrise";
    public static final  String KEY_WEATHER_SUNSET      = "Sunset";
    public static final  String KEY_WEATHER_TEMPERATURE = "Temperature";
    public static final  String PATH_WEATHER_INFO       = "/WeatherWatchFace/WeatherInfo";
    public static final  String PATH_SERVICE_REQUIRE    = "/WeatherService/Require";
    private static final String TAG                     = "WeatherService";
    private GoogleApiClient mGoogleApiClient;
    private LocationManager mLocationManager;
    private Location        mLocation;
    private String          mPeerId;

    @Override
    public int onStartCommand( Intent intent, int flags, int startId )
    {
        if ( intent != null )
        {
                mPeerId = "PeerId";
                startTask();

        }

        return super.onStartCommand( intent, flags, startId );
    }

    @Override
    public void onMessageReceived( MessageEvent messageEvent )
    {
        super.onMessageReceived( messageEvent );
        mPeerId = messageEvent.getSourceNodeId();
        Log.d( TAG, "MessageReceived: " + messageEvent.getPath() );
        if ( messageEvent.getPath().equals( PATH_SERVICE_REQUIRE ) )
        {
            startTask();
        }
    }

    private void startTask()
    {
        Log.d( TAG, "Start Weather AsyncTask" );
        mGoogleApiClient = new GoogleApiClient.Builder( this ).addApi( Wearable.API ).build();

        mLocationManager = (LocationManager) this.getSystemService( Context.LOCATION_SERVICE );

            Task task = new Task();
            task.execute();

    }

    private class Task extends AsyncTask
    {


        @Override
        protected Object doInBackground( Object[] params )
        {
            try
            {
                Log.d(TAG, "Task Running");

                if ( !mGoogleApiClient.isConnected() )
                { mGoogleApiClient.connect();
                    Wearable.MessageApi.addListener(mGoogleApiClient, WeatherService.this);
                }

                DataMap config = new DataMap();


                //real
                config.putInt( "Temperature", 42 );
                config.putInt( "Temperature", 32 );
                config.putString("Condition", "clear");


                //test
                //Random random = new Random();
                //config.putInt("Temperature",random.nextInt(100));
                //config.putString("Condition", new String[]{"clear","rain","snow","thunder","cloudy"}[random.nextInt
                // (4)]);

                PutDataMapRequest putDataMapReq = PutDataMapRequest.create("/count").setUrgent();
                putDataMapReq.getDataMap().putLong("currentTimeMillis", System.currentTimeMillis());
                Wearable.MessageApi.sendMessage( mGoogleApiClient, "PeerId", PATH_WEATHER_INFO, config.toByteArray() )
                        .setResultCallback(
                                new ResultCallback<MessageApi.SendMessageResult>() {
                                    @Override
                                    public void onResult(MessageApi.SendMessageResult sendMessageResult) {
                                        Log.d(TAG, "SendUpdateMessage: " + sendMessageResult.getStatus());
                                    }
                                }
                        );
            }
            catch ( Exception e )
            {
                Log.d( TAG, "Task Fail: " + e );
            }
            return null;
        }
    }

}

