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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;

public class
MainActivity extends AppCompatActivity {
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
            ReadText = tv.getText().toString();


//            Check if Charging or not when phone is plugged in or removed
            CheckChargeStatus();
            try {


                if (i.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
                    Vibrate();
                    ChangeStatusText.setText(R.string.Charging);
                    AnnouncePhoneConnected();
                } else if (i.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)) {
                    Vibrate();
                    ChangeStatusText.setText(R.string.NotCharging);
                    AnnouncePhoneConnected();
                }
                PhoneConnected = "Device " + ChangeStatusText.getText().toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    String ReadText;
    TextToSpeech mTTS;
    Button speak;
    TextToSpeech AnnouncePhoneConnected;
    String PhoneConnected;

    TextView ChangeStatusText;
    private FirebaseAnalytics mFirebaseAnalytics;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Firebase Analytics
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //Register the receiver which triggers event
        //when battery charge is changed
        registerReceiver(mBatInfoReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

        speak = findViewById(R.id.speak);
        speak.setOnClickListener(v -> SpeakBatteryLevel());

        ChangeStatusText = findViewById(R.id.chargestatustext);

//        Restore values on device rotation
        if (savedInstanceState != null) {
            String Restorevalue = savedInstanceState.getString("ChargeStatus");
            ChangeStatusText.setText(Restorevalue);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        CreateTTS();
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            if (mTTS != null) {
                mTTS.stop();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    public void ReleaseTTS() {
        try {
            if (mTTS != null) {
                mTTS.stop();
                mTTS.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void SpeakBatteryLevel() {
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
        BatteryManager ba = (BatteryManager) getSystemService(BATTERY_SERVICE);
        int batLevel = ba.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        AnnouncePhoneConnected = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                AnnouncePhoneConnected.setLanguage(Locale.ENGLISH);
            }
//                    Speak button click
            AnnouncePhoneConnected.setSpeechRate(0.97f);
            AnnouncePhoneConnected.speak("Battery Level " + batLevel + " % " + PhoneConnected, TextToSpeech.QUEUE_FLUSH, null, null);

        });
    }


    public void openTTSSettings() {
        //Open Android Text-To-Speech Settings
        Intent intent = new Intent();
        intent.setAction("com.android.settings.TTS_SETTINGS");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    //    Menu File

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        String Readoptionsout = item.getTitle().toString();
        mTTS.speak(Readoptionsout, TextToSpeech.QUEUE_FLUSH, null, null);

        switch (item.getItemId()) {
            case R.id.Light:
                Vibrate();
//                switch to Light Mode
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case R.id.Dark:
                Vibrate();
//                switch to Light Dark
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case R.id.System:
                Vibrate();
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
            case R.id.tts:
                Vibrate();
                openTTSSettings();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ReleaseTTS();
    }

    //    Save the value of charging and not charging in bundle
    protected void onSaveInstanceState(@NonNull Bundle OutState) {
        super.onSaveInstanceState(OutState);
        OutState.putString("ChargeStatus", ChangeStatusText.getText().toString());
    }

}
