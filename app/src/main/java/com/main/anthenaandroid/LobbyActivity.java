package com.main.anthenaandroid;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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

    public static RoomFinder rf = startPageActivity.rf;
    private Thread loadIntoGame;

    private void awaitGameStart() {
        loadIntoGame = new Thread() {
            private boolean changeActivity = true;
            public void run() {
                while(true){
                    if(rf.checkGameStarted()) {
                        break;
                    }
                    if(Thread.currentThread().isInterrupted()) {
                        System.out.println("Successfully unreadied");
                        changeActivity = false;
                        break;
                    }
                }
                if(changeActivity) {
                    Intent i;
                    if (isRunner) {
                        i = new Intent(getApplicationContext(), runnerUI.class);
                        startActivity(i);
                    } else {

                        i = new Intent(getApplicationContext(), stomperUI.class);
                        startActivity(i);
                    }
                }
            }
        };
        loadIntoGame.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.lobby_page);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

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
                rf.switchPlayerType(RoomFinder.TYPE_STOMPER);
            }
        });
        runnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRunner = true;
                stomperBtn.setBackgroundColor(Color.LTGRAY);
                runnerBtn.setBackgroundColor(Color.GRAY);
                rf.switchPlayerType(RoomFinder.TYPE_RUNNER);
            }
        });

        lockBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rf.toggleReadyState();
                if(rf.checkReadyState()) {
                    lockBtn.setBackgroundColor(Color.GRAY);
                    runnerBtn.setClickable(false);
                    stomperBtn.setClickable(false);
                    awaitGameStart();
                } else {
                    runnerBtn.setClickable(true);
                    stomperBtn.setClickable(true);
                    lockBtn.setBackgroundColor(Color.LTGRAY);
                    loadIntoGame.interrupt();
                }

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
