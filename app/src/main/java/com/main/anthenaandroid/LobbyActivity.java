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
    private Thread directLoadIntoGame;

    private void checkForDirectLoad() {
        directLoadIntoGame = new Thread() {
            private boolean changeActivity = true;
            public void run() {
                while(true){
                    if(rf.checkToForceGameStart()) {
                        if(rf.type == GamePacket.TYPE_RUNNER) {
                            isRunner = true;
                        } else {
                            isRunner = false;
                        }
                        if(loadIntoGame != null) {
                            loadIntoGame.interrupt();
                        }
                        break;
                    }
                    if(Thread.currentThread().isInterrupted()) {
                        System.out.println("Stopped waiting for forced game start");
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
        directLoadIntoGame.start();
    }

    private void awaitGameStart() {
        loadIntoGame = new Thread() {
            private boolean changeActivity = true;
            public void run() {
                while(true){
                    if(rf.checkGameStarted()) {
                        if(directLoadIntoGame != null) {
                            directLoadIntoGame.interrupt();
                        }
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

        final Button reconnectBtn = (Button) findViewById(R.id.reconnectBtn);

        reconnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rf.stop();
                rf = new RoomFinder(RoomFinder.TYPE_RUNNER);
                Thread thread = new Thread(rf);
                thread.start();
                if (!isRunner) {
                    rf.switchPlayerType(RoomFinder.TYPE_STOMPER);
                }
                rf.sendUnreadyUponReconnect();
                runnerBtn.setClickable(true);
                stomperBtn.setClickable(true);
                lockBtn.setBackgroundColor(Color.LTGRAY);
                if(loadIntoGame != null) {
                    loadIntoGame.interrupt();
                }
            }
        });

        checkForDirectLoad();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(loadIntoGame != null){
            loadIntoGame.interrupt();
        }
    }
}
