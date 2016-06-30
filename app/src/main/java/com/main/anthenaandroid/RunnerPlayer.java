package com.main.anthenaandroid;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ten Zhi Yang on 7/1/2016.
 */
public class RunnerPlayer {
    final int changeDirTime = 100;
    long changeDirStart = -1;
    public RunnerPlayer(){
        changeDirStart = -1;
    }
    public boolean canChangeDir(){
        long curr = System.currentTimeMillis();
        if(changeDirStart<0) {
            changeDirStart = curr;
            return true;
        }
        else{
            return false;
        }
    }

    public void checkChangeDir() {
        long curr = System.currentTimeMillis();
        if (changeDirStart> -1) {
            if(curr >= changeDirStart+changeDirTime){
                changeDirStart = -1;
            }
        }
    }
}
