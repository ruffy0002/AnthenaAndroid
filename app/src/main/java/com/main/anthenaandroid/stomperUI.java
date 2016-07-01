package com.main.anthenaandroid;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class stomperUI extends Activity {
    RoomFinder rf = LobbyActivity.rf;
    GameLoop gl;
    @Override
     protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stomper_ui);
        gl = new GameLoop(this,false);
        RelativeLayout stomperLayout = (RelativeLayout) findViewById(R.id.stomperLayout);

        rf.sendGameStartedPacket();
        stomperLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX()/v.getWidth();
                float y = event.getY()/v.getHeight();
                if(gl.sp.canStomp()) {
                    sendStomp(x, y);
                }

                return true;
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
