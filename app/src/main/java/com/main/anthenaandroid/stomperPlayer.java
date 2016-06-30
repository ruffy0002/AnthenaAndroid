package com.main.anthenaandroid;

import java.util.concurrent.TimeUnit;

/**
 * Created by Ten Zhi Yang on 6/30/2016.
 */
public class StomperPlayer {
    final int CDTIMING = 2;
    final int castTime = 200;
    long isStomping;
    long coolDownStart = -1;
    int stompsLeft = 5;
    public StomperPlayer(){
        stompsLeft = 5;
        coolDownStart =-1;
        isStomping = -1;
    }

    public boolean canStomp(){
        long curr = System.currentTimeMillis();
        if(stompsLeft>0) {

            if(isStomping<0) {
                stompsLeft--;
                if(coolDownStart<0){
                    coolDownStart = curr;
                }
                isStomping = curr;
                return true;
            }else{
                return false;
            }

        }
        else{
            return false;
        }
    }

    public void checkCoolDown() {
        long curr = System.currentTimeMillis();
        if (coolDownStart < 0) {
            if(stompsLeft<5){
                coolDownStart = curr;
            }
        }else{
            if(curr >= coolDownStart+ TimeUnit.SECONDS.toMillis(CDTIMING)){
                stompsLeft++;
                coolDownStart = -1;
            }
            if(curr >= isStomping+castTime){
                isStomping = -1;
            }
        }
    }
}
