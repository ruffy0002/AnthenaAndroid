package com.main.anthenaandroid;

import java.io.Serializable;

public class GamePacket implements Serializable {

    static final long serialVersionUID = 568750792;

    public GamePacket (float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     *
     * @param x
     * @param y
     * @param type which type of packet this is, GamePacket.TYPE_STOMPER or GamePacket.TYPE_RUNNER
     *             or GamePacket.TYPE_POSITIONUPDATE
     */
    public GamePacket (float x, float y, int type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    private float x;
    private float y;
    private int type;

    public static int TYPE_STOMPER = 0;
    public static int TYPE_RUNNER = 1;
    public static int TYPE_POSITIONUPDATE = 2;
    public static int TYPE_TYPECHANGE = 3;
    public static int TYPE_GAMEEND = 4;

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public int getType() {
        return type;
    }
    public void setType(int type) {
        this.type = type;
    }


}