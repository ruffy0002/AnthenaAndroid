package com.main.anthenaandroid;

import java.io.Serializable;

public class GameMessagePacket implements Serializable {
    static final long serialVersionUID = 568750799;

    public static final int MESSAGETYPE_TRAPPED = 1;
    /**
     * Sends a message for the client to display
     *
     * @param _message - The message to be shown on the phone's screen
     * @param _duration - Duration where the message is shown (This is not used
     *            for now)
     * @param _type - how the message is to be shown, look at the declarations
     *            starting with MESSAGETYE_
     */
    GameMessagePacket(String _message, int _duration, int _type) {
        message = _message;
        duration = _duration;
        type = _type;
    }

    private String message;
    private int duration;
    private int type;

    public String getMessage () { return message;}
    public void setMessage (String _message) { message = _message;}

    /**
     * Duration in ms
     * @return
     */
    public int getDuration () { return duration;}
    /**
     * Duration in ms
     * @return
     */
    public void setDuration (int _duration) { duration = _duration;}

    public int getType () { return type;}
    public void setType (int _type) { type = _type;}


}