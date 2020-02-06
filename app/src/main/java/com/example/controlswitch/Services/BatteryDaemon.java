package com.example.controlswitch.Services;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Handler;


import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.example.controlswitch.Helpers.ConectionHelper;
import com.example.controlswitch.R;
import com.example.controlswitch.Receivers.PowerConnectionReceiver;
import com.example.controlswitch.Receivers.Restarter;

import java.util.Timer;
import java.util.TimerTask;


public class BatteryDaemon extends IntentService {

    public static final String BATTERY_UPDATE = "battery";
    public static final String TAG="com.juango.controlswitch.Services.BatteryDaemon";
    private static final String CHANNEL_ID ="BatteryDaemon" ;
    private boolean isFull=false;
    private float max_percentage;
    private SharedPreferences sharedPref;



    //Receiver
    private PowerConnectionReceiver mBatInfoReceiver = new PowerConnectionReceiver();

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        /*if (intent !=null && intent.getExtras()!=null){
        }*/
    }


    public BatteryDaemon() {
        super("BatteryDaemon");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.registerReceiver(this.mBatInfoReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        sharedPref = getApplicationContext().getSharedPreferences(
                "control_switch", Context.MODE_PRIVATE);
        max_percentage=sharedPref.getInt("percentage_alert", 90);
        createNotificationChannel();
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),CHANNEL_ID).setSmallIcon(R.drawable.battery90_icon)
                    .setContentTitle("Checking battery status")
                    .setContentText("Control switch is checking your battery level")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);


            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());

            // notificationId is a unique int for each notification that you must define
            notificationManager.notify(0, builder.build());
        final Handler handler = new Handler();
        final Timer timer = new Timer();
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            BatteryCheckAsync task= new BatteryCheckAsync();
                            boolean batteryIsFull=task.execute(max_percentage).get();
                            if(batteryIsFull==true){
                                new ConectionHelper().execute("/chargerOff");
                                timer.cancel();
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(),CHANNEL_ID).setSmallIcon(R.drawable.battery90_icon)
                                        .setContentTitle("Full battery")
                                        .setContentText("switch off")
                                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);


                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getBaseContext());

                                // notificationId is a unique int for each notification that you must define
                                notificationManager.notify(0, builder.build());
                            }
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, 60000); //execute in every 20000 ms
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(mBatInfoReceiver);
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }


    private class BatteryCheckAsync extends AsyncTask<Float, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Float... floats) {
            IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
            Intent batteryStatus = BatteryDaemon.this.registerReceiver(null, ifilter);

            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);



            float chargelevel=(level / (float)scale);
            //Log.i("BatteryInfo", "Battery charge level: " + chargelevel);
            //Log.i("BatteryInfo", "Battery charge defined: " + (floats[0]/100));
            if(isFull==false){
                if(chargelevel>=(floats[0]/100)){
                    isFull=true;
                    //Log.i("BatteryInfo","FULL_BATTERY");
                }
            }



            return isFull;
        }

        @Override
        protected void onPostExecute(Boolean b) {
            super.onPostExecute(b);
        }


    }
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


}



