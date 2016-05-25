package com.main.anthenaandroid;

import android.widget.EditText;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by ruffy0002_2 on 21/5/2016.
 */
public class DataSender implements Runnable {
    GamePacket p = new GamePacket();
    TextView textIn;

    public DataSender (float x, float y) {
        p.setX(x);
        p.setY(y);
    }

    public void run () {
        Socket socket = null;
        ObjectOutputStream dataOutputStream = null;
        ObjectInputStream dataInputStream = null;

        try {
            socket = new Socket("10.104.64.13", 1356);
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataInputStream = new ObjectInputStream(socket.getInputStream());
            dataOutputStream.writeObject(p);
            //textIn.setText(dataInputStream.readUTF());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
    }
}
