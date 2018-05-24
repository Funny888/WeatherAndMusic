package com.example.funny.theadsnservices;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.app.job.JobScheduler;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.session.MediaSession;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.ArrayMap;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ServiceTwo extends Service  implements MediaPlayer.OnPreparedListener,MediaPlayer.OnCompletionListener{

    ExecutorService es;
    Intent intent;
    String r;
    ListMusic lm;
    int numberTrack;
    NotificationManager notificationManager;
    MediaPlayer mdPlayer;
    MediaSession mds;
    Executors exec;
    MyBind binder = new MyBind();

    ArrayMap<String, String> sounds = new ArrayMap<>();
    public static final int  BUTTON_BACK_FORWARD = 5, BUTTON_PLAY_STOP = 4;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();



        Random random = new Random();
        r = String.valueOf(random.nextInt(59) + 1);

        lm = new ListMusic();
        lm.execute();
        mdPlayer = new MediaPlayer();
        mdPlayer.setOnCompletionListener(this);
        mdPlayer.setOnPreparedListener(this);
        mdPlayer.setVolume(50, 50);
        mdPlayer.setWakeMode(getBaseContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mds = new MediaSession(this,MEDIA_SESSION_SERVICE);
        notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {



        boolean status = intent.getBooleanExtra("status",false);
        numberTrack = intent.getIntExtra("numberTrack",0);
        int buttonAction = intent.getIntExtra("buttonCode",0);
        int buttonCodeForground = intent.getIntExtra("buttonCodeForground",0);


        Log.i("log", "status >> " + String.valueOf(status)+ " numberTrack >> " + String.valueOf(numberTrack) + " buttonAction " + String.valueOf(buttonAction));

         if (lm.getStatus() == ListMusic.Status.FINISHED) {
             switch (buttonAction) {
                 case BUTTON_BACK_FORWARD: {
                     backForward(mdPlayer, numberTrack);

                     break;
                 }

                 case BUTTON_PLAY_STOP: {
                     startStop(mdPlayer, status);
                     break;
                 }
             }


             if (buttonCodeForground == 1)
             {
                 Log.i("before id", "buttonCodeForeground" + String.valueOf(numberTrack));
                 numberTrack ++;
                 Log.i("after id", "buttonCodeForeground" + String.valueOf(numberTrack));
                 backForward(mdPlayer,intent.getIntExtra("number",0));
             }
             else if (buttonCodeForground == 2)
             {
                 numberTrack --;
                 backForward(mdPlayer,numberTrack);
             }
          sendNotification();

         }
         else
         {
             try {
                 TimeUnit.SECONDS.sleep(3);
             } catch (InterruptedException e) {
                 e.printStackTrace();
             }
         }
        return START_REDELIVER_INTENT;
    }



    @RequiresApi (api = Build.VERSION_CODES.LOLLIPOP)
    private void sendNotification()
    {
        Intent intent1 = new Intent(this,ServiceTwo.class)
                .putExtra("buttonCodeForground",1)
                .putExtra("number",numberTrack +1);


        PendingIntent pi = PendingIntent.getService(this,0, intent1,PendingIntent.FLAG_NO_CREATE);

        Notification musicNotif = new Notification.Builder(this)
                .setSmallIcon(R.drawable.thumb_lay)
                .setContentTitle("Сейчас играет: " + sounds.keyAt(numberTrack))
                .setContentText("Переключить")
                .addAction(R.drawable.start_music,"pause",pi)
                .setStyle(new Notification.MediaStyle()
                        .setShowActionsInCompactView(0 /* #1: pause button */)
                        .setMediaSession(mds.getSessionToken()))
                .build();
        startForeground(777,musicNotif);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if (mp.isPlaying())
        {mp.stop();}
        Log.i("befor reset", "comletetion");
        mp.reset();
        Log.i("after reset", "comletetion");
        try {
            numberTrack ++;
            Log.i("befor setdata", "comletetion");
            mp.setDataSource(sounds.valueAt(numberTrack));

            Log.i("after setdata", "comletetion");

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("befor prepear", "comletetion");
        mp.prepareAsync();
        Log.i("after prepear", "comletetion");

    }
@RequiresApi (api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onPrepared(MediaPlayer mp) {
        Log.i("befor start", "prepeared: ");
        mp.start();
        sendNotification();
        Log.i("after start", "prepeared: ");
        sendBroadcast(new Intent("android.intent.category.TITLE_MUSIC")
                .putExtra("title",sounds.keyAt(numberTrack))
                .putExtra("duration",mp.getDuration())
        );


    }

    public void startStop(final MediaPlayer mp, final boolean status)
    {

                    if (status == false) {
                        mp.pause();
                    } else
                    {
                        mp.start();
                    }
    }

    public void backForward(MediaPlayer mp, int numberTrack)
    {

                Log.i("serviceFore", "buttonCodeForeground" + String.valueOf(numberTrack));
                if (mp.isPlaying())
                {mp.stop();}
                Log.i("befor reset", "run: ");
                mp.reset();
                try {
                    Log.i("befor setdata", "run: ");
                    mp.setDataSource(sounds.valueAt(numberTrack));
                    Log.i("after setdata", "run: ");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.i("befor prepear", "run: ");
                mp.prepareAsync();
                Log.i("after prepear", "run: ");
                mdPlayer.start();
                Log.i("track >> ", sounds.valueAt(numberTrack));



    }


    class ListMusic extends AsyncTask<String, String, ArrayMap<String, String>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected ArrayMap<String, String> doInBackground(String... strings) {

            ArrayMap<String, String> am = new ArrayMap();
            String name = null;
            String href = null;
            String url1 = "http://zaycev.net/new/index.html";
            String url = "http://zaycev.net/genres/dance/index_" + r + ".html";
            try {
                Document stranica = null;
                try {
                    stranica = Jsoup.connect(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Elements elements = stranica.select("div.musicset-track__track-name");
                for (int i = 0; i < elements.select("a[href*=/pages/]").size(); i++) {
                    name = elements.get(i).text();
                    href = "http://cdndl.zaycev.net" + elements.get(i).getElementsByTag("a").attr("href").replace("/pages", "").replace(".shtml", "/" + name + ".mp3");
                    am.put(name, href);

                    Log.i("stranica ", "href >> " + href);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return am;
        }

        @Override
        protected void onPostExecute(ArrayMap<String, String> stringStringMap) {
            super.onPostExecute(stringStringMap);
            sounds = stringStringMap;
//            Toast.makeText(getBaseContext(),"плей лист загружен",Toast.LENGTH_SHORT).show();
            try {
                mdPlayer.setDataSource(stringStringMap.valueAt(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
               mdPlayer.prepareAsync();
        }
    }

    class MyBind extends Binder
    {
       ServiceTwo getService()
       {
           return ServiceTwo.this;
       }
    }
}