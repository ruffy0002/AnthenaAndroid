package com.main.anthenaandroid;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

public class stomperUI extends Activity {
    RoomFinder rf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stomper_ui);
        System.out.println("Finding room");
        rf = new RoomFinder(RoomFinder.TYPE_STOMPER);
        Thread thread = new Thread(rf);
        thread.start();
        RelativeLayout stomperLayout = (RelativeLayout) findViewById(R.id.stomperLayout);
        stomperLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX()/v.getWidth();
                float y = event.getY()/v.getHeight();
                sendStomp(x,y);
                return true;
            }
        });
    }
    public void sendStomp (float x, float y){
        if (rf != null) {
            rf.sendStomp(x,y);
        }
    }
}
