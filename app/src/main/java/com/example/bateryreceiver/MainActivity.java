package com.example.bateryreceiver;

import static com.example.bateryreceiver.R.color;
import static com.example.bateryreceiver.R.id;
import static com.example.bateryreceiver.R.layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {
    //Create Broadcast Receiver Object along with class definition
    private final BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {

        @Override
        //When Event is published, onReceive method is called
        public void onReceive(Context c, Intent i) {
            ConstraintLayout rl = findViewById(id.layout);

            //Get Battery %
            int level = i.getIntExtra("level", 0);
            //Find the progressbar creating in main.xml
            ProgressBar pb = findViewById(id.progressBar);
            //Set progress level with battery % value
            pb.setProgress(level);
            //Find textview control created in main.xml
            TextView tv = findViewById(id.textView);

            //Set TextView with text

            tv.setText("Battery Level  \n " + level + "%");
            if (level > 90 && level <= 100) {
                rl.setBackgroundResource(R.color.Green);
            } else if (level > 50 && level <= 90) {
                rl.setBackgroundResource(color.Blue);
            }else if (level > 15 && level <= 50) {
                rl.setBackgroundResource(color.Yellow);
            }
            else {
                rl.setBackgroundResource(color.Red);
            }

        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);
        //Register the receiver which triggers event
        //when battery charge is changed
        registerReceiver(mBatInfoReceiver, new IntentFilter(
                Intent.ACTION_BATTERY_CHANGED));

    }
}
