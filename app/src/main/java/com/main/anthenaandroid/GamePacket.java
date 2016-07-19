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
    public static int TYPE_READY = 3;
    public static int TYPE_UNREADY = 4;
    public static int TYPE_GAMESTART = 5;
    public static int TYPE_GAMEEND = 6;
    public static int TYPE_CHANGEPLAYERTYPE = 7;
    public static int TYPE_SKILL = 8;

    public static int SKILL_SETTRAP = 1;
    public static int SKILL_KAKEBUSHIN = 2;

    public int getSkill () {
        return (int) x;
    }

    public void setSkill (int skill) {
        x = (float) skill;
    }

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