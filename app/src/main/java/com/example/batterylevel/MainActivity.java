package com.example.batterylevel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    String ReadText;
    TextToSpeech mTTS;
    Button speak;
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

            tv.setText("Battery Level  \n " + level + "%");
            if (level > 90 && level <= 100) {
                rl.setBackgroundResource(R.color.Green);
            } else if (level > 50 && level <= 90) {
                rl.setBackgroundResource(R.color.Blue);
            }else if (level > 15 && level <= 50) {
                rl.setBackgroundResource(R.color.Yellow);
            }
            else {
                rl.setBackgroundResource(R.color.Red);
            }
            //Get battery % value
            ReadText =tv.getText().toString();


//            Check if Charging or not
            CheckChargeStatus();

            if (i.getAction().equals(Intent.ACTION_POWER_CONNECTED)){
                Toast.makeText(MainActivity.this, "Phone is Charging", Toast.LENGTH_SHORT).show();
            }
            else if (i.getAction().equals(Intent.ACTION_POWER_DISCONNECTED)){
                Toast.makeText(MainActivity.this, "Phone is not Charging", Toast.LENGTH_SHORT).show();
            }

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
        speak.setOnClickListener(v -> {
            CreateTTS();
            SpeakBatteryLevel();
        });


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
        mTTS.speak(ReadText, TextToSpeech.QUEUE_FLUSH, null,null);
    }

    public void CheckChargeStatus(){
        IntentFilter CheckChargeIntentFilter=new IntentFilter();
        CheckChargeIntentFilter.addAction(Intent.ACTION_POWER_CONNECTED);
        CheckChargeIntentFilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        registerReceiver(mBatInfoReceiver,CheckChargeIntentFilter);
    }
}
