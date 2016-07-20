package com.main.anthenaandroid;

import android.app.Activity;
import android.content.Intent;
import android.widget.ImageView;
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
    RoomFinder rf;

    long messageDurationLeft = 0;

    public GameLoop(Activity a, boolean runner, RoomFinder _rf){
        parentActivity = a;
        isRunner = runner;
        sp = new StomperPlayer();
        rp = new RunnerPlayer();
        rf = _rf;
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
        checkServerData();
    }

    /**
     * Checks for game response to server sent packets
     */
    private void checkServerData() {
        if(rf.hasDataFromServer()) {
            GamePacket gp = rf.getDataFromServer();
            if(gp.getType() == GamePacket.TYPE_CHANGEPLAYERTYPE && isRunner) {
                parentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent(parentActivity.getApplicationContext(), runnerUI.class);
                        parentActivity.startActivity(i);
                    }
                });
            }
        }
    }

    private void clearMessages() {
        if(messageDurationLeft != 0) {
            if(System.currentTimeMillis() > messageDurationLeft) {
                System.out.println("Clearing trapped message");
                clearTrappedMessage();
                messageDurationLeft = 0;
            }
        }
    }

    public void clearTrappedMessage() {
        ImageView trappedText = (ImageView) parentActivity.findViewById(R.id.trappedText);
        trappedText.setVisibility(ImageView.INVISIBLE);

        TextView trappedTextName = (TextView) parentActivity.findViewById(R.id.trappedTextName);
        trappedTextName.setVisibility(ImageView.INVISIBLE);
    }

    private void updateServerMessages() {
        if(rf.hasMessageDataFromServer()) {
            GameMessagePacket gmp = rf.getMessageDataFromServer();
            if(gmp.getType() == GameMessagePacket.MESSAGETYPE_TRAPPED) {
                displayTrappedText(gmp.getDuration(), gmp.getMessage());
            }
        }
    }

    /**
     * Makes the TRAPPED word appear in the middle of the runner UI and append the message passed
     * in below the TRAPPED word
     *
     * @param duration in ms
     * @param message
     */
    public void displayTrappedText (int duration, String message) {
        ImageView trappedText = (ImageView) parentActivity.findViewById(R.id.trappedText);
        trappedText.setVisibility(ImageView.VISIBLE);
        messageDurationLeft = duration + System.currentTimeMillis();

        TextView trappedTextName = (TextView) parentActivity.findViewById(R.id.trappedTextName);
        trappedTextName.setVisibility(ImageView.VISIBLE);
        trappedTextName.setText(message);
    }

    public void render() {
        if (!isRunner) {
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView tv = (TextView) parentActivity.findViewById(R.id.StompsRemaining);
                    tv.setText("Stomps Remaining: " + sp.stompsLeft);
                    TextView tv2 = (TextView) parentActivity.findViewById(R.id.stomperFeedback);
                    tv2.setText("Packets Sent: " + rf.getPacketsSent());
                }
            });
        } else{
            parentActivity.runOnUiThread(new Runnable() {
                @Override
                public  void run() {
                    TextView tv = (TextView) parentActivity.findViewById(R.id.joystickcoords);
                    tv.setText("Stomps Remaining: " + rp.changeDirStart);
                    TextView tv2 = (TextView) parentActivity.findViewById(R.id.runnerFeedback);
                    tv2.setText("Packets Sent: "+ rf.getPacketsSent());
                    updateServerMessages();
                    clearMessages();
                }
            });
        }

    }
}
