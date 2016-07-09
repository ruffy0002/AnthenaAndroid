package com.main.anthenaandroid;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class runnerUI extends Activity {

    RoomFinder rf = LobbyActivity.rf;
    GameLoop gl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runner_ui);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        gl = new GameLoop(this,true);

        rf.sendGameStartedPacket();
        RelativeLayout runnerF = (RelativeLayout) findViewById(R.id.runnerField);
        runnerF.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x = event.getX()/v.getWidth();
                float y = event.getY()/v.getHeight();
                if(gl.rp.canChangeDir()) {
                    sendDir(x, y);
                }
                return true;
            }
        });
        Thread gameLoop = new Thread(gl);
        gameLoop.start();
        /**
        final TextView joystickCoords = (TextView) findViewById(R.id.joystickcoords);
        final View joystick = (View) findViewById(R.id.Stick);
        joystick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float joystickX = event.getX()-125;
                float joystickY = event.getY()-125;
                if (joystickY<-125)
                    joystickY = -125;
                if(joystickX<-125)
                    joystickX = -125;
                if (joystickY>125)
                    joystickY = 125;
                if(joystickX>125)
                    joystickX = 125;
                joystickCoords.setText(joystickX+" , "+joystickY);
                return true;
            }
        });**/
    }
    public void sendDir (float x, float y){
        if (rf != null) {
            rf.sendMovement(x,y);
        }
    }
}
