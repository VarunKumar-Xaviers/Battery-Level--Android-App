package com.example.bateryreceiver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
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

            tv.setText("Battery Level is : " + level + "%");
            if (level > 90 && level <= 100) {
                rl.setBackgroundColor(Color.GREEN);
                Toast.makeText(getApplicationContext(), "sufficient battery", Toast.LENGTH_LONG).show();
            } else if (level > 50 && level <= 90) {
                rl.setBackgroundColor(android.R.color.holo_orange_dark);
                Toast.makeText(getApplicationContext(), "BATTERY WILL DIE SOON", Toast.LENGTH_LONG).show();
            }else if (level > 15 && level <= 50) {
                rl.setBackgroundColor(Color.YELLOW);
                Toast.makeText(getApplicationContext(), "BATTERY WILL DIE SOON", Toast.LENGTH_LONG).show();
            }
            else {
                rl.setBackgroundColor(Color.RED);
                Toast.makeText(getApplicationContext(), "PHONE will die soon!!!!", Toast.LENGTH_LONG).show();
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

    }
}
