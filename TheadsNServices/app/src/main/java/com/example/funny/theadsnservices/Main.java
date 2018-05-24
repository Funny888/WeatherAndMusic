package com.example.funny.theadsnservices;
//key = AIzaSyBzd2O1eSDUDN5fYl-4XMFzOCxNZXKvDf0

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.TimeUnit;

public class Main extends AppCompatActivity implements Frag1.myState,Frag1.onAction, View.OnClickListener, LocationListener {
    FragmentTransaction ft;
    Button bService1, bService2;
    Fragment frag2;
    ProgressBar pBar;
    Handler handler;
    BroadcastReceiver br;
    TextView weatherView;
    public static final String _WIFI_STATE = "WIFI", _MOBILE_STATE = "MOBILE";
    Intent intentTest;
    Location location;
    String key = "AIzaSyBzd2O1eSDUDN5fYl-4XMFzOCxNZXKvDf0",title, placeData = null; //req= "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latiTude +  "," + longTitude + "&key=" + key;
    private double _latiTude, _longTitude;
    ConnectivityManager _internet;
    NetworkInfo _info;
    LocationManager locationManager;
    boolean flag = false;
    String[] weatherData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        ft = getFragmentManager().beginTransaction();
        frag2 = new Frag2();
        frag2.setRetainInstance(true);
        ft.add(R.id.frame, frag2);
        ft.commit();
        bService1 = findViewById(R.id.bService1);
        bService1.setOnClickListener(this);
        bService2 = findViewById(R.id.bService2);
        bService2.setOnClickListener(this);
        weatherView = findViewById(R.id.weather);
        pBar = findViewById(R.id.pBar);
        pBar.setIndeterminate(true);
        if (savedInstanceState != null) {

            frag2 = getFragmentManager().getFragment(savedInstanceState, "frag2");
            ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.frame, frag2);
            ft.commit();
            weatherView.setText((CharSequence) savedInstanceState.get("weather"));
            pBar.setVisibility(savedInstanceState.getInt("pBarV"));
        }

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                weatherData = intent.getStringArrayExtra("weatherData");
                weatherView.setText(weatherData[0] + "\n" + weatherData[1] + "\n" + weatherData[2] + "\n" + weatherData[3]
                        + "\n" + weatherData[4] + "\n" + weatherData[5] + "\n" + weatherData[6] + "\n" + weatherData[7] + "\n"
                        + weatherData[8]);
                pBar.setVisibility(View.INVISIBLE);

                if (flag == true)
                    locationManager.removeUpdates(Main.this);
            }
        };

        IntentFilter intentFilter = new IntentFilter("com.example.funny.theadsnservices");
        registerReceiver(br, intentFilter);
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int d = msg.what;
                pBar.setMax(9);
                pBar.setProgress(d);
            }
        };
    }

    private String stateInternet() {

        _internet = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
        _info = _internet.getActiveNetworkInfo();
        String state = _info.getTypeName();
        return state;
    }

    @Override
    public void methot(String s) {
        this.title = s;

      ((TextView) frag2.getView().findViewById(R.id.view)).setText(s);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putCharSequence("weather", weatherView.getText());
        getFragmentManager().putFragment(outState, "frag2", frag2);
        outState.putInt("pBarV", pBar.getVisibility());
        outState.putString("title",title);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        title = savedInstanceState.getString("title");
        ((TextView) frag2.getView().findViewById(R.id.view)).setText(title);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.bService1: {
                switch (stateInternet()) {
                    case _WIFI_STATE: {
                        intentTest = new Intent().setPackage("com.example.funny.servicesclass");
                        startService(intentTest);
                        break;
                    }

                    case _MOBILE_STATE: {
                        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                        } else {
                            getLayLongtitude();
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    while (placeData == null) {

                                        try {
                                            TimeUnit.SECONDS.sleep(3);

                                            if (location != null) {
                                                request();
                                                handler.post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Toast.makeText(getBaseContext(), "Данные получены", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    intentTest = new Intent().setPackage("com.example.funny.servicesclass").putExtra("placeData", placeData);
                                    startService(intentTest);
                                    flag = true;
                                    placeData = null;
                                }
                            }).start();
                            break;
                        }
                    }
//                    default:
//                        Toast.makeText(getBaseContext(), "Нет интернета, попытайтесь снова", Toast.LENGTH_SHORT).show();

                }

                ((TextView) frag2.getView().findViewById(R.id.view)).setText(R.string.serv1start);
                pBar.setVisibility(View.VISIBLE);
                break;

            }

            case R.id.bService2: {
                ((TextView) frag2.getView().findViewById(R.id.view)).setText(R.string.serv1stopText);
                intentTest = new Intent().setPackage("com.example.funny.servicesclass");
                stopService(intentTest);
                weatherView.setText("");
                pBar.setVisibility(View.INVISIBLE);
                if (flag == true) {
                    flag = false;
                    locationManager.removeUpdates(this);
                }
                break;
            }
        }

    }
    public void getLayLongtitude() {
        if (ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(Main.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, PackageManager.PERMISSION_GRANTED);
            return;
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, this);


    }

    public void request() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    URL _url = new URL("https://maps.googleapis.com/maps/api/geocode/json?latlng=" + String.valueOf(_latiTude = location.getLatitude()) +
                            "," + String.valueOf(_longTitude = location.getLongitude()) + "&key=" + key);
                    URLConnection con = _url.openConnection();
                    con.setDoInput(true);
                    con.setDoOutput(true);

                    BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    StringBuilder sb = new StringBuilder();


                    String read = "";
                    while ((read = br.readLine()) != null) {
                        sb.append(read + "\n");
                    }

                    Log.d("result", sb.toString());
                    placeData = cityDate(sb.toString());
                    Log.i("Log ", placeData);
                    createLog(placeData);


                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1) {

            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                createLog(placeData);
            } else {
                if (!shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Snackbar.make(this.bService1, "Включите разрешение", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Разрешение", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + getPackageName()));
                                    startActivity(intent);

                                }
                            })
                            .show();

                } else {
                    Toast.makeText(getBaseContext(), "разрешение отклонено", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    private boolean createLog(String fLogCity) {
        File logCity = new File(File.separator + "sdcard" + File.separator + "logCity.txt");


        try {
            FileWriter fw = new FileWriter(logCity, true);
            fw.append(fLogCity);
            fw.flush();
            fw.close();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    public String cityDate(String date) throws JSONException {

        JSONObject _result = new JSONObject(date);
        JSONArray _resArray = _result.getJSONArray("results");
        JSONObject obj1 = _resArray.getJSONObject(1);
        String adr = obj1.getString("formatted_address");
        return adr;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (flag == true) {
            flag = false;
            locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (flag == false) {
            flag = true;
            getLayLongtitude();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }


    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void actionButton(int buttonCode, int numberTrack, boolean status) {
        startService(new Intent(Main.this,ServiceTwo.class)
                .putExtra("buttonCode",buttonCode)
                .putExtra("numberTrack",numberTrack)
                .putExtra("status",status)
        );
    }
}
