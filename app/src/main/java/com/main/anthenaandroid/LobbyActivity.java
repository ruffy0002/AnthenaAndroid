package com.main.anthenaandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class LobbyActivity extends Activity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

    /**
     * Some older devices needs a small delay between UI widget updates
     * and a change of the status and navigation bar.
     */
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private boolean isRunner = true;

    RoomFinder rf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lobby_page);

        mContentView = findViewById(R.id.fullscreen_content);




        final Button stomperBtn = (Button) findViewById(R.id.stomperbtn);
        final Button runnerBtn = (Button) findViewById(R.id.runnerbtn);
        runnerBtn.setBackgroundColor(Color.GRAY);
        stomperBtn.setBackgroundColor(Color.LTGRAY);
        final Button lockBtn = (Button) findViewById(R.id.lockinbtn);

        stomperBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunner = false;
                stomperBtn.setBackgroundColor(Color.GRAY);
                runnerBtn.setBackgroundColor(Color.LTGRAY);
            }
        });
        runnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunner = true;
                stomperBtn.setBackgroundColor(Color.LTGRAY);
                runnerBtn.setBackgroundColor(Color.GRAY);
            }
        });

        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i;
                if (isRunner){

                    System.out.println("Finding room");
                    rf = new RoomFinder(RoomFinder.TYPE_RUNNER);
                    Thread thread = new Thread(rf);
                    thread.start();

                    i = new Intent(getApplicationContext(), runnerUI.class);
                    startActivity(i);
                }else{

                    i = new Intent(getApplicationContext(), stomperUI.class);
                    startActivity(i);

                }
                lockBtn.setBackgroundColor(Color.GRAY);
            }
        });
        /*
        FrameLayout flWebPre = (FrameLayout) findViewById(R.id.Dummy);

        flWebPre.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v,MotionEvent event) {
                DisplayMetrics displaymetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
                int height = displaymetrics.heightPixels;
                int width = displaymetrics.widthPixels;
                float percentX = event.getX()/(float)height;
                float percentY = event.getY()/(float)width;
                sendStomp(percentX,percentY);
                return true;
            }
        });*/
    }






}
