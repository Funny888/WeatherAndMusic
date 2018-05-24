package com.example.funny.servicesclass;
// key AIzaSyBzd2O1eSDUDN5fYl-4XMFzOCxNZXKvDf0

import android.app.Service;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;


public class ServiceOne extends Service {
    ConnectivityManager _connectInternet;
    NetworkInfo statusInternet;
    Integer _No_Internet = 0, _Internet_Mobile = 1, _WiFi = 2;
    List<String> _imya, _znach;
    String _gorod;
    Handler hand;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Log.i("Log: ", "Сервис запущен");
        _connectInternet = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);

        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.i("Log: ", "Сервис остановлен");
        stopSelf();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(final Intent intent, final int flags, int startId) {

        String placeData = intent.getStringExtra("placeData");
//        String placeData = "43253, nizhny novgorod";
        if (placeData != null) {
            String cityName = cityName(placeData);
            requestChoiceCyty(cityName);

            hand = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    if (msg.what == 2)
                        _gorod = msg.obj.toString();
                    Log.d("how gorod", _gorod);
                    methodInfo(intent, flags);
                }
            };
        } else {
            methodInfo(intent, flags);
        }

        return super.onStartCommand(intent, flags, startId);

    }

    private String cityName(String placeData) {

        String d = placeData.replaceAll(" ", "%20");
        String[] place = d.split(",");
        return place[1] /*+ place[3]*/;
    }


    private void methodInfo(Intent intent, int flags) {
        statusInternet = _connectInternet.getActiveNetworkInfo();


        if (statusInternet.getTypeName().equals("WIFI")) {
            AsInfo reqInfo = new AsInfo(intent, flags, null);
            reqInfo.execute(_WiFi);
        } else if (statusInternet.getTypeName().equals("MOBILE")) {
            AsInfo reqInfo = new AsInfo(intent, flags, _gorod);
            reqInfo.execute(_Internet_Mobile);
        }
        else {
            AsInfo reqInfo = new AsInfo(intent, flags, null);
            reqInfo.execute(_No_Internet);
        }
    }


    private void requestChoiceCyty(final String city) {
        Thread t1 = new Thread(new Runnable() {
            Message msg;

            @Override
            public void run() {

                Log.i("City", city);
                Document document = null;
                try {
                    document = Jsoup.connect("https://www.gismeteo.ru/search/" + city).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Elements elements = document.getElementsByTag("section").attr("class", "catalog_block").eq(0);


                for (Element element : elements) {

                    _imya = element.select("a[href*=weather]").eachText();

                    _znach = element.select("a[href*=weather]").eachAttr("href");

                }
                String _weatherCity = city.replaceAll("%20", "-").toLowerCase();
                Pattern pat = Pattern.compile("/weather" + _weatherCity + "\\W.*");

                for (int i = 0; i < _znach.size(); i++) {
                    String s = _znach.get(i);
                    if (pat.matcher(s).matches()) {
                        msg = hand.obtainMessage(2, _znach.get(i));
                        Log.d("ZNAK: ", _znach.get(i));
                        hand.sendMessage(msg);
                    }
                }


            }
        });
        t1.start();
    }

    class AsInfo extends AsyncTask<Integer, Void, Void> {
        Intent asIntent, answIntent;
        int flag;


        String server = "https://www.gismeteo.ru", _gorod;
        String[] _date;

        public AsInfo(Intent intent, int flags, @Nullable String gorod) {
            this.asIntent = intent;
            this.flag = flags;
            this._gorod = gorod;

        }


        @Override
        protected Void doInBackground(Integer... voids) {
            if (voids[0].equals(1)) {
                requestData(server, _gorod);
                Log.d("MOBILE INTERNET ", " good");
            } else if (voids[0].equals(2)) {
                requestData(server);
                Log.d("WIFI INTERNET ", "good");
            } else {
                Log.d("NO INTERNET: ", "badly");
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {

                if (statusInternet.getTypeName().equals("WIFI")) {
                    answIntent = new Intent("com.example.funny.theadsnservices").putExtra("weatherData", _date);
                    sendBroadcast(answIntent);
//            Toast.makeText(getBaseContext(),_date[0] + "\n" + _date[1] + "\n" +  _date[2] + "\n" +  _date[3] + "\n" +
//                    _date[4] + "\n" +  _date[5] + "\n" +  _date[6] + "\n" +  _date[7] + "\n" + _date[8] ,Toast.LENGTH_LONG).show();
                }
                if (statusInternet.getTypeName().equals("MOBILE")) {
                    answIntent = new Intent("com.example.funny.theadsnservices").putExtra("weatherData", _date);
                    Toast.makeText(getBaseContext(), "Mobile internet", Toast.LENGTH_SHORT).show();
                    sendBroadcast(answIntent);
                }
            } catch (NullPointerException e) {
                Toast.makeText(getBaseContext(), "Интернета нет", Toast.LENGTH_SHORT).show();
            }
        }


        private void requestData(String link) {
            try {


                Document document = Jsoup.connect(link)
                        .get();

                Log.d("debug", link);
                Element element = document.normalise();
                String _poonct = element.getElementsByTag("div").attr("class", "weather_now").eq(30).text();
                String _temperature = getString(R.string.tempAir) + element.getElementsByTag("div").attr("class", "weather_now").eq(38).text();
                String _statusNeba = element.getElementsByTag("div").attr("class", "weather_now").eq(42).text();
                String _ocshuchsenie = element.getElementsByTag("div").attr("class", "weather_now").eq(45).text();
                String _veter = element.getElementsByTag("div").attr("class", "weather_now").eq(48).text();
                String _davlenie = element.getElementsByTag("div").attr("class", "weather_now").eq(52).text();
                String _vlagnost = element.getElementsByTag("div").attr("class", "weather_now").eq(55).text();
                String _magnPole = element.getElementsByTag("div").attr("class", "weather_now").eq(58).text();
                String _tempVodi = element.getElementsByTag("div").attr("class", "weather_now").eq(61).text();

//                for (int i = 0; i <element.getElementsByTag("div").attr("class", "weather_now").size(); i++) {
//                    String _poonct1 = element.getElementsByTag("div").attr("class", "weather_now").eq(i).text();
//
//                    Log.d("stroka" + String.valueOf(i), _poonct1);
//                }
                _date = new String[]{_poonct, _temperature, _statusNeba, _ocshuchsenie, _veter, _davlenie, _vlagnost, _magnPole, _tempVodi};


            } catch (Throwable cause) {
                cause.printStackTrace();
            }
        }

        private void requestData(String link, String gorod) {
            try {
                Log.d("debug LINK", link + gorod + "now/");
                Document document = Jsoup.connect(link + gorod + "now/")
                        .userAgent("Mozilla")
                        .get();


                Element element = document.getElementsByTag("section").first();


                String _poonct = element.getElementsByTag("h1").attr("class", "frame_items_9 now now_day").eq(0).text();
                String _temperature = getString(R.string.tempAir) + element.getElementsByTag("div").attr("class", "frame_items_9 now now_day").eq(57).text();
                String _ocshuchsenie = element.getElementsByTag("div").attr("class", "frame_items_9 now now_day").eq(58).text();
                String _statusNeba = element.getElementsByTag("div").attr("class", "frame_items_9 now now_day").eq(59).text();
                String _veter = element.getElementsByTag("div").attr("class", "frame_items_9 now now_day").eq(62).text();
                String _davlenie = element.getElementsByTag("div").attr("class", "frame_items_9 now now_day").eq(67).text();
                String _vlagnost = element.getElementsByTag("div").attr("class", "frame_items_9 now now_day").eq(72).text();
                String _magnPole = element.getElementsByTag("div").attr("class", "frame_items_9 now now_day").eq(77).text();
                String _tempVodi = element.getElementsByTag("div").attr("class", "frame_items_9 now now_day").eq(82).text();

//                for (int i = 0; i < element.getElementsByTag("div").attr("class", "frame_items_9 now now_day").size(); i++) {
//                    String _poonct1 = element.getElementsByTag("div").get(i).text();
//                    Log.i("Stroka" + String.valueOf(i), _poonct1);
//                }

                _date = new String[]{_poonct, _temperature, _statusNeba, _ocshuchsenie, _veter, _davlenie, _vlagnost, _magnPole, _tempVodi};

            } catch (Throwable cause) {
                cause.printStackTrace();
            }
        }
    }


}