package com.main.anthenaandroid; /**
 * Created by ruffy0002_2 on 20/5/2016.
 */

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class AndroidClient extends Activity {

    EditText textOut;
    TextView textIn;

    RoomFinder rf;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        textOut = (EditText)findViewById(R.id.textout);
        Button buttonSend = (Button)findViewById(R.id.dummy_button);
        Button buttonData = (Button)findViewById(R.id.data_button);
        textIn = (TextView)findViewById(R.id.feedbackText);
        buttonSend.setOnClickListener(buttonSendOnClickListener);
        buttonData.setOnClickListener(buttonDataOnClickListener);
        FrameLayout flWebPre = (FrameLayout) findViewById(R.id.DummyFrame);
        flWebPre.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v,MotionEvent event) {

                sendStomp(event.getX(),event.getY());
                return true;
            }
        });
    }

    Button.OnClickListener buttonSendOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {



            Intent i = new Intent(getApplicationContext(), FullscreenActivity.class);
            startActivity(i);
        }
    };

    Button.OnClickListener buttonDataOnClickListener
            = new Button.OnClickListener(){

        @Override
        public void onClick(View arg0) {

            if (rf != null) {
                if(rf.hasDataFromServer()) {
                    GamePacket tem = rf.getDataFromServer();
                    textIn.setText(tem.getX() + ", " + tem.getY());
                } else {
                    textIn.setText("No data");
                }
            }
        }
    };

    public void sendStomp (float x, float y){
        if (rf != null) {
            rf.sendStomp(x,y);
        }
    }
}