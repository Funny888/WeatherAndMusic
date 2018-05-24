package com.example.funny.theadsnservices;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.ToggleButton;


public class Frag1 extends Fragment implements View.OnClickListener {

    int _ID = 0;
    SeekBar seekBar;
    BroadcastReceiver br;
    IntentFilter intFilt;
    Handler hand;
    ServiceConnection servCon;
    ServiceTwo servTwo;

    public interface myState {
        void methot(String s);
    }

    myState state1;

    public interface onAction {
        void actionButton(int buttonCode, int numberTrack, boolean status);
    }

    onAction action;

    @Override
    public void onAttach(Context context) {
        state1 = (myState) context;
        action = (onAction) context;
        super.onAttach(context);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.frag1, null);
        setRetainInstance(true);
        Button bService3 = v.findViewById(R.id.bService3);
        bService3.setOnClickListener(this);
        ToggleButton bService4 = v.findViewById(R.id.bService4);
        bService4.setOnCheckedChangeListener(onPlayStop);
        Button bService5 = v.findViewById(R.id.bService5);
        bService5.setOnClickListener(this);
        seekBar = v.findViewById(R.id.playSeekBar);

        br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, final Intent intent) {
                state1.methot(intent.getStringExtra("title"));
                current();
            }
        };

        hand = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == 50) {
                    seekBar.setProgress(msg.arg1);
                }
            }
        };


        servCon = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                servTwo = ((ServiceTwo.MyBind) service).getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                servTwo.mdPlayer.seekTo(seekBar.getProgress());
            }
        });


        intFilt = new IntentFilter("android.intent.category.TITLE_MUSIC");
        getActivity().registerReceiver(br, intFilt);


        if (savedInstanceState != null) {
            _ID = savedInstanceState.getInt("_ID");
            seekBar.setMax(servTwo.mdPlayer.getDuration());

        } else {

        }
        Intent intent = new Intent(getActivity(), ServiceTwo.class);
        getActivity().bindService(intent, servCon, 0);
        return v;
    }


    void current() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                seekBar.setMax(servTwo.mdPlayer.getDuration());
                seekBar.setProgress(servTwo.mdPlayer.getCurrentPosition());
                Message msg;
                while (servTwo.mdPlayer.getCurrentPosition() < servTwo.mdPlayer.getDuration()) {

                    msg = hand.obtainMessage(50, servTwo.mdPlayer.getCurrentPosition(), 0);
                    hand.sendMessage(msg);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bService3: {
                _ID--;
                action.actionButton(5, _ID, true);
                break;
            }
            case R.id.bService5: {
                _ID++;
                action.actionButton(5, _ID, true);
                break;
            }

        }

    }

    CompoundButton.OnCheckedChangeListener onPlayStop = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(final CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
                action.actionButton(4, _ID, true);


            } else if (!isChecked) {
                action.actionButton(4, _ID, false);
                seekBar.setMax(servTwo.mdPlayer.getDuration());
                seekBar.setProgress(servTwo.mdPlayer.getCurrentPosition());
            }
        }

    };


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("_ID", _ID);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(br);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(br, intFilt);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public class fordBackListen extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
                KeyEvent event = (KeyEvent) intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (KeyEvent.KEYCODE_VOLUME_UP == event.getKeyCode()) {

                }
            }
        }
    }


}
