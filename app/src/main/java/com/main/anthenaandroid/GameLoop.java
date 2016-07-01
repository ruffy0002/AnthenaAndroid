package com.main.anthenaandroid;

import android.app.Activity;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Ten Zhi Yang on 6/30/2016.
 */
public class GameLoop extends Thread{
    private final static int    MAX_FPS = 60;
    private final static int    MAX_FRAME_SKIPS = 5;
    private final static int    FRAME_PERIOD = 1000;
    long beginTime;     // the time when the cycle begun
    long timeDiff;      // the time it took for the cycle to execute
    int sleepTime;      // ms to sleep (<0 if we're behind)
    int framesSkipped;  // number of frames being skipped
    public StomperPlayer sp;
    public RunnerPlayer rp;
    boolean isRunner;
    private Activity parentActivity;
    TextView stompsLeftText;
    int prevStompNo;

    public GameLoop(Activity a, boolean runner){
        parentActivity = a;
        isRunner = runner;
        sp = new StomperPlayer();
        rp = new RunnerPlayer();
    }
    @Override
    public void run() {
        while (true) {
            beginTime = System.currentTimeMillis();
            framesSkipped = 0;  // resetting the frames skipped
            // update game state
            stateUpdate();
            // render state to the screen
            render();
            // draws the canvas on the panel

            // calculate how long did the cycle take
            timeDiff = System.currentTimeMillis() - beginTime;
            // calculate sleep time
            sleepTime = (int) (FRAME_PERIOD - timeDiff);

            if (sleepTime > 0) {
                // if sleepTime > 0 we're OK
                try {
                    // send the thread to sleep for a short period
                    // very useful for battery saving
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }

            }
            while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
                // we need to catch up
                stateUpdate();
                // update without rendering
                // add frame period to check if in next frame
                sleepTime += FRAME_PERIOD;
                framesSkipped++;
            }
        }
    }
    public void stateUpdate(){
        if(!isRunner) {
            sp.checkCoolDown();
        }else{
            rp.checkChangeDir();
        }
    }
    public void render() {
        if (!isRunner) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = (TextView) parentActivity.findViewById(R.id.StompsRemaining);
                    tv.setText("Stomps Remaining: " + sp.stompsLeft);
                }
            });
        } else{
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = (TextView) parentActivity.findViewById(R.id.joystickcoords);
                    tv.setText("Stomps Remaining: " + rp.changeDirStart);
                }
            });
        }

    }
}
