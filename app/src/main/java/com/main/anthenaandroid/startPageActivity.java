package com.main.anthenaandroid; /**
 * Created by ruffy0002_2 on 20/5/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

public class startPageActivity extends Activity {

    EditText textOut;
    TextView textIn;

    public static RoomFinder rf;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        textOut = (EditText)findViewById(R.id.textout);
        rf = new RoomFinder(RoomFinder.TYPE_RUNNER);
        /*
        Button buttonSend = (Button)findViewById(R.id.dummy_button);
        //Button buttonData = (Button)findViewById(R.id.data_button);
       // textIn = (TextView)findViewById(R.id.feedbackText);
        buttonSend.setOnClickListener(buttonSendOnClickListener);*/
        //buttonData.setOnClickListener(buttonDataOnClickListener);
        FrameLayout flWebPre = (FrameLayout) findViewById(R.id.DummyFrame);
        Button joinGame = (Button) findViewById(R.id.startBtn);
        joinGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Thread thread = new Thread(rf);
                thread.start();
                Intent nextIntent = new Intent(getApplicationContext(), LobbyActivity.class);
                startActivity(nextIntent);
            }
        });
    }


}