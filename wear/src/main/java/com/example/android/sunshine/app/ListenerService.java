package com.example.android.sunshine.app;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by michaelHahn on 1/16/15.
 * Listener service or data events on the data layer
 */
public class ListenerService extends WearableListenerService {

    private static final String WEARABLE_DATA_PATH = "/wearable_data";
    BroadcastReceiver receiver;



//    @Override
//    public void onMessageReceived(MessageEvent messageEvent){
//        //  super.onMessageReceived(messageEvent);
//
//
//        if (messageEvent.getPath().equals(WEARABLE_DATA_PATH)) {
//            final String message = new String(messageEvent.getData());
//
//
//            Intent intent = new Intent(this, WeatherWatchFaceService.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//
//
//
//        }else {
//            super.onMessageReceived(messageEvent);
//        }
//    }


//    @Override
//    public void onMessageReceived(MessageEvent messageEvent){
//        //  super.onMessageReceived(messageEvent);
//
//
//        if (messageEvent.getPath().equals(WEARABLE_DATA_PATH)) {
//            final String message = new String(messageEvent.getData());
//
//
//            Intent intent = new Intent(this, WeatherWatchFaceService.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(intent);
//
//
//
//        }else {
//            super.onMessageReceived(messageEvent);
//        }
//    }


//    @Override
//    public void onDataChanged(DataEventBuffer dataEvents) {
//
//        DataMap dataMap;
//        for (DataEvent event : dataEvents) {
//            Log.v("myTag", "DataMap received on watch: " + DataMapItem.fromDataItem(event.getDataItem()).getDataMap());
//            // Check the data type
//            if (event.getType() == DataEvent.TYPE_CHANGED) {
//                // Check the data path
//                String path = event.getDataItem().getUri().getPath();
//                if (path.equals(WEARABLE_DATA_PATH)) {
//                }
//                dataMap = DataMapItem.fromDataItem(event.getDataItem()).getDataMap();
//
//
//                // Broadcast DataMap contents to wearable activity for display
//                // The content has the golf hole number and distances to the front,
//                // middle and back pin placements.
//
//                Intent messageIntent = new Intent(this, WeatherWatchFaceService.class);
//                messageIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                messageIntent.putExtra("datamap", dataMap.toBundle());
//                startActivity(messageIntent);
//                //LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
//
//            }
//        }
//    }


//    @Override
//    public void onMessageReceived(MessageEvent messageEvent) {
//
//        DataMap dataMap;
//        dataMap = (DataMap) messageEvent;
//        Intent messageIntent = new Intent();
//        messageIntent.setAction(Intent.ACTION_SEND);
//        messageIntent.putExtra("datamap", dataMap.toBundle());
//        LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
//
//    }

    private static final String TAG = ListenerService.class.getSimpleName();

    private GoogleApiClient mGoogleApiClient;
    private static String condition;
    private static int temperature;
    private static int temperature_low;
    private static long sunrise;
    private static long sunset;
    private static int temperature_scale;
    private static int theme = 3;
    private static int time_unit;
    private static int interval;
    private static boolean alreadyInitialize;
    private static String path;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, "onMessageReceived: " + messageEvent);
        Log.v("myTag", "DataMap received on watch: " + messageEvent);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Wearable.API)
                    .build();
        }

        if (!mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

        DataMap dataMap = DataMap.fromByteArray(messageEvent.getData());

        path = messageEvent.getPath();
        PutDataMapRequest putDataMapRequest = PutDataMapRequest.create(path);
        DataMap config = putDataMapRequest.getDataMap();

        if (path.equals(Consts.PATH_WEATHER_INFO)) {

            if (dataMap.containsKey(Consts.KEY_WEATHER_CONDITION)) {
                condition = dataMap.getString(Consts.KEY_WEATHER_CONDITION);
            }

            if (dataMap.containsKey(Consts.KEY_WEATHER_TEMPERATURE)) {
                temperature = dataMap.getInt(Consts.KEY_WEATHER_TEMPERATURE);
            }

            if (dataMap.containsKey(Consts.KEY_WEATHER_TEMPERATURE_LOW)) {
                temperature_low = dataMap.getInt(Consts.KEY_WEATHER_TEMPERATURE_LOW);
            }

            if (dataMap.containsKey(Consts.KEY_WEATHER_SUNRISE)) {
                sunrise = dataMap.getLong(Consts.KEY_WEATHER_SUNRISE);
            }

            if (dataMap.containsKey(Consts.KEY_WEATHER_SUNSET)) {
                sunset = dataMap.getLong(Consts.KEY_WEATHER_SUNSET);
            }

            config.putLong(Consts.KEY_WEATHER_UPDATE_TIME, System.currentTimeMillis());
            config.putString(Consts.KEY_WEATHER_CONDITION, condition);
            config.putInt(Consts.KEY_WEATHER_TEMPERATURE, temperature);
            config.putInt(Consts.KEY_WEATHER_TEMPERATURE_LOW, temperature_low);
            config.putLong(Consts.KEY_WEATHER_SUNRISE, sunrise);
            config.putLong(Consts.KEY_WEATHER_SUNSET, sunset);
        } else {
            if (!alreadyInitialize) {
                Wearable.NodeApi.getLocalNode(mGoogleApiClient).setResultCallback(new ResultCallback<NodeApi.GetLocalNodeResult>() {
                    @Override
                    public void onResult(NodeApi.GetLocalNodeResult getLocalNodeResult) {
                        Uri uri = new Uri.Builder()
                                .scheme("wear")
                                .path(path)
                                .authority(getLocalNodeResult.getNode().getId())
                                .build();

                        Wearable.DataApi.getDataItem(mGoogleApiClient, uri)
                                .setResultCallback(
                                        new ResultCallback<DataApi.DataItemResult>() {
                                            @Override
                                            public void onResult(DataApi.DataItemResult dataItemResult) {
                                                if (dataItemResult.getStatus().isSuccess() && dataItemResult.getDataItem() != null) {
                                                    fetchConfig(DataMapItem.fromDataItem(dataItemResult.getDataItem()).getDataMap());
                                                }

                                                alreadyInitialize = true;
                                            }
                                        }
                                );
                    }
                });

                while (!alreadyInitialize) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (dataMap.containsKey(Consts.KEY_CONFIG_TEMPERATURE_SCALE)) {
                temperature_scale = dataMap.getInt(Consts.KEY_CONFIG_TEMPERATURE_SCALE);
            }

            if (dataMap.containsKey(Consts.KEY_CONFIG_THEME)) {
                theme = dataMap.getInt(Consts.KEY_CONFIG_THEME);
            }

            if (dataMap.containsKey(Consts.KEY_CONFIG_TIME_UNIT)) {
                time_unit = dataMap.getInt(Consts.KEY_CONFIG_TIME_UNIT);
            }

            if (dataMap.containsKey(Consts.KEY_CONFIG_REQUIRE_INTERVAL)) {
                interval = dataMap.getInt(Consts.KEY_CONFIG_REQUIRE_INTERVAL);
            }


            config.putInt(Consts.KEY_CONFIG_TEMPERATURE_SCALE, temperature_scale);
            config.putInt(Consts.KEY_CONFIG_THEME, theme);
            config.putInt(Consts.KEY_CONFIG_TIME_UNIT, time_unit);
            config.putInt(Consts.KEY_CONFIG_REQUIRE_INTERVAL, interval);
        }

        Wearable.DataApi.putDataItem(mGoogleApiClient, putDataMapRequest.asPutDataRequest())
                .setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
                    @Override
                    public void onResult(DataApi.DataItemResult dataItemResult) {
                        Log.d(TAG, "SaveConfig: " + dataItemResult.getStatus() + ", " + dataItemResult.getDataItem().getUri());

                        mGoogleApiClient.disconnect();
                    }
                });
    }

    protected void fetchConfig(DataMap config) {
        if (config.containsKey(Consts.KEY_WEATHER_CONDITION)) {
            condition = config.getString(Consts.KEY_WEATHER_CONDITION);
        }

        if (config.containsKey(Consts.KEY_WEATHER_TEMPERATURE)) {
            temperature = config.getInt(Consts.KEY_WEATHER_TEMPERATURE);
        }

        if (config.containsKey(Consts.KEY_WEATHER_TEMPERATURE_LOW)) {
            temperature_low = config.getInt(Consts.KEY_WEATHER_TEMPERATURE_LOW);
        }


        if (config.containsKey(Consts.KEY_WEATHER_SUNRISE)) {
            sunrise = config.getLong(Consts.KEY_WEATHER_SUNRISE);
        }

        if (config.containsKey(Consts.KEY_WEATHER_SUNSET)) {
            sunset = config.getLong(Consts.KEY_WEATHER_SUNSET);
        }

        if (config.containsKey(Consts.KEY_CONFIG_TEMPERATURE_SCALE)) {
            temperature_scale = config.getInt(Consts.KEY_CONFIG_TEMPERATURE_SCALE);
        }

        if (config.containsKey(Consts.KEY_CONFIG_THEME)) {
            theme = config.getInt(Consts.KEY_CONFIG_THEME);
        }

        if (config.containsKey(Consts.KEY_CONFIG_TIME_UNIT)) {
            time_unit = config.getInt(Consts.KEY_CONFIG_TIME_UNIT);
        }

        if (config.containsKey(Consts.KEY_CONFIG_REQUIRE_INTERVAL)) {
            interval = config.getInt(Consts.KEY_CONFIG_REQUIRE_INTERVAL);
        }
    }



}


