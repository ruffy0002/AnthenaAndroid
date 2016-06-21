package com.main.anthenaandroid;

import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ruffy0002_2 on 21/5/2016.
 */
public class DataSender implements Runnable {

    Queue<GamePacket> packetQueue = new LinkedList<GamePacket>();
    private InetAddress _ipToSend;
    private int _portNo;

    Socket socket = null;
    ObjectOutputStream dataOutputStream = null;
    ObjectInputStream dataInputStream = null;

    boolean running = true;
    public DataSender (InetAddress ipToSend, int portNo) {
        _ipToSend = ipToSend;
        _portNo = portNo;

        try {
            socket = new Socket(_ipToSend, _portNo);
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataInputStream = new ObjectInputStream(socket.getInputStream());
            dataOutputStream.writeObject((Object) new BroadcastPacket(false));
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeSocket();
        }
    }

    private void closeSocket() {
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (dataOutputStream != null) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (dataInputStream != null) {
            try {
                dataInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run () {
        while(running) {

        }
    }

    public void sendStomp (float x, float y) {
        GamePacket newPacket = new GamePacket(x,y,GamePacket.TYPE_STOMPER);
        packetQueue.add(newPacket);
    }

    public void sendMovement (float x, float y) {
        GamePacket newPacket = new GamePacket(x,y,GamePacket.TYPE_RUNNER);
        packetQueue.add(newPacket);
    }
}
