package com.main.anthenaandroid;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class runnerUI extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runner_ui);
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
        });
    }
}
