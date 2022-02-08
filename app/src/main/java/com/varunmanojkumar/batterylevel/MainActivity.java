package com.varunmanojkumar.batterylevel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;

public class
MainActivity extends AppCompatActivity {
    String ReadText;
    TextToSpeech mTTS;
    Button speak;
    TextToSpeech AnnouncePhoneConnected;
    String PhoneConnected;

    TextView ChangeStatusText;
    //Create Broadcast Receiver Object along with class definition
    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

        @Override
        //When Event is published, onReceive method is called
        public void onReceive(Context c, Intent i) {
            ConstraintLayout rl = findViewById(R.id.layout);

            //Get Battery %
            int level = i.getIntExtra("level", 0);
            //Find the progressbar creating in main.xml
            ProgressBar pb = findViewById(R.id.progressBar);
            //Set progress level with battery % value
            pb.setProgress(level);
            //Find textview control created in main.xml
            TextView tv = findViewById(R.id.textView);

            //Set TextView with text

            tv.setText(getString(R.string.BatteryLevel) + level + "%");
            if (level == 100) {
                rl.setBackgroundResource(R.color.Green);
                ChangeStatusText.setText(R.string.FullyCharged);
            } else if (level > 90 && level <= 100) {
                rl.setBackgroundResource(R.color.Green);

            } else if (level > 50 && level <= 90) {
                rl.setBackgroundResource(R.color.Blue);

            } else if (level > 15 && level <= 50) {
                rl.setBackgroundResource(R.color.Yellow);

            } else {
                rl.setBackgroundResource(R.color.Red);

            }

            //Get battery % value
            ReadText = tv.getText().toString() + " Phone is " + ChangeStatusText.getText().toString();


//            Check if Charging or not when phone is plugged in or removed
            CheckChargeStatus();

            if (i.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
                Vibrate();
                ChangeStatusText.setText(R.string.Charging);
                AnnouncePhoneConnected();
            }
            else if (i.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)){
                Vibrate();
                ChangeStatusText.setText(R.string.NotCharging);
                AnnouncePhoneConnected();
            }
            PhoneConnected = "Phone is " + ChangeStatusText.getText().toString();
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Register the receiver which triggers event
        //when battery charge is changed
        registerReceiver(mBatInfoReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        speak = findViewById(R.id.speak);
        speak.setOnClickListener(v -> SpeakBatteryLevel());

        ChangeStatusText=findViewById(R.id.chargestatustext);

        changeBatteryCharging();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReleaseTTS();
    }

    @Override
    protected void onStart() {
        super.onStart();
        CreateTTS();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTTS.stop();
        unregisterReceiver(mBatInfoReceiver);
    }

    public  void CreateTTS(){
        mTTS = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = mTTS.setLanguage(Locale.ENGLISH);
                if (result == TextToSpeech.LANG_MISSING_DATA
                        || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e("TTS", "Language not supported");
                } else {
                    speak.setEnabled(true);
                }
            } else {
                Log.e("TTS", "Initialization failed");
            }
        });
    }
    public void ReleaseTTS(){
        mTTS.stop();
        mTTS.shutdown();
    }

    public void SpeakBatteryLevel(){
        mTTS = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                mTTS.setLanguage(Locale.ENGLISH);
            }
//                    Speak button click
            mTTS.setSpeechRate(0.97f);
            mTTS.speak(ReadText, TextToSpeech.QUEUE_FLUSH, null, null);

        });
    }

    public void Vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        //Vibrate on click
        if (Build.VERSION.SDK_INT >= 26) {
//    Perform forAPI  26 and above
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
//    Perform for API 26 and below
            vibrator.vibrate(200);
        }
    }

    public void CheckChargeStatus() {
        IntentFilter CheckChargeIntentFilter = new IntentFilter();
        CheckChargeIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        CheckChargeIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(mBatInfoReceiver, CheckChargeIntentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ReleaseTTS();
    }

    public void AnnouncePhoneConnected() {
        AnnouncePhoneConnected = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                AnnouncePhoneConnected.setLanguage(Locale.ENGLISH);
            }
//                    Speak button click
            AnnouncePhoneConnected.setSpeechRate(0.97f);
            AnnouncePhoneConnected.speak(PhoneConnected, TextToSpeech.QUEUE_FLUSH, null, null);

        });
    }

    public void changeBatteryCharging() {
        BatteryManager ba = (BatteryManager) getSystemService(BATTERY_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ba.isCharging()) {
                ChangeStatusText.setText(R.string.Charging);
            } else if (!ba.isCharging()) {
                ChangeStatusText.setText(R.string.NotCharging);
            }
        }
    }
}
