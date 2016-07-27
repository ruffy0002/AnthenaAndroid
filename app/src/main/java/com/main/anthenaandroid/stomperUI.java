package com.main.anthenaandroid;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class stomperUI extends Activity {
    RoomFinder rf = LobbyActivity.rf;
    GameLoop gl;
    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stomper_ui);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        gl = new GameLoop(this,false,rf);
        RelativeLayout stomperLayout = (RelativeLayout) findViewById(R.id.stomperLayout);

        rf.sendGameStartedPacket();

        stomperLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN) {
                    float x = event.getX() / v.getWidth();
                    float y = event.getY() / v.getHeight();
                    sendStomp(x, y);
                    return true;
                }
                return false;
            }
        });
        Thread gameLoop = new Thread(gl);
        gameLoop.start();


    }

    public void sendStomp (float x, float y){
        if (rf != null) {
            rf.sendStomp(x,y);
        }
    }
}
