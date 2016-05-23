package com.main.anthenaandroid;

import java.io.Serializable;

public class GamePacket implements Serializable {
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