package com.main.anthenaandroid;

import java.io.Serializable;

public class GamePacket implements Serializable {

    static final long serialVersionUID = 568750792;

    public GamePacket (float x, float y) {
        this.x = x;
        this.y = y;
    }
    private float x;
    private float y;
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


}