package com.main.anthenaandroid;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Queue;

/**
 * Created by ruffy0002_2 on 27/5/2016.
 */
public class RoomFinder implements Runnable{
    DatagramSocket c;
    int portNo = 1355;
    int dataPortNo = 1356;
    boolean roomFound = false;
    DataSender ds = null;
    boolean running = true;

    boolean isRoomFound = false;

    Queue<GamePacket> packetQueue = new LinkedList<GamePacket>();
    private InetAddress _ipToSend;
    private int _portNo;

    Socket socket = null;
    ObjectOutputStream dataOutputStream = null;
    ObjectInputStream dataInputStream = null;

    public void sendStomp (float x, float y) {
        if(isRoomFound) {
            GamePacket newPacket = new GamePacket(x, y);
            packetQueue.add(newPacket);
        }
    }

    public void run (){
        // Find the server using UDP broadcast
        try {
            //Open a random port to send the package
            c = new DatagramSocket();
            c.setBroadcast(true);


            BroadcastPacket o = new BroadcastPacket(false);
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
            final ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(o);
            final byte[] sendData = baos.toByteArray();

            //Try the 255.255.255.255 first
            try {
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName("255.255.255.255"), portNo);
                c.send(sendPacket);
                System.out.println(getClass().getName() + "Request packet sent to: 255.255.255.255 (DEFAULT)");
            } catch (Exception e) {
            }

            // Broadcast the message over all the network interfaces
            Enumeration interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = (NetworkInterface) interfaces.nextElement();

                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Don't want to broadcast to the loopback interface
                }

                for (InterfaceAddress interfaceAddress : networkInterface.getInterfaceAddresses()) {
                    InetAddress broadcast = interfaceAddress.getBroadcast();
                    if (broadcast == null) {
                        continue;
                    }

                    // Send the broadcast package!
                    try {
                        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, broadcast, portNo);
                        c.send(sendPacket);
                    } catch (Exception e) {
                    }

                    System.out.println("Request packet sent to: " + broadcast.getHostAddress() + "; Interface: " + networkInterface.getDisplayName());
                }
            }
            System.out.println("Done looping over all network interfaces. Now waiting for a reply!");
            //Wait for a response
            byte[] recvBuf = new byte[15000];
            DatagramPacket receivePacket = new DatagramPacket(recvBuf, recvBuf.length);
            c.receive(receivePacket);

            //We have a response
            System.out.println("Broadcast response from server: " + receivePacket.getAddress().getHostAddress());

            //Check if the message is correct
            ByteArrayInputStream byteStream = new ByteArrayInputStream(recvBuf);
            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
            Object replyObject = is.readObject();
            if (replyObject instanceof BroadcastPacket) {
                BroadcastPacket serverReply = (BroadcastPacket) replyObject;
                if(serverReply.isServer()) {
                    System.out.println("Found a server at " + receivePacket.getAddress());
                    isRoomFound = true;
                    _ipToSend = receivePacket.getAddress();
                    _portNo = dataPortNo;

                }
            }
            //Close the port!
            c.close();
        } catch (IOException ex) {

        } catch (ClassNotFoundException ex) {

        }

        startTCPStream();
        while(running) {
            if(packetQueue.size() > 0) {
                try {
                    dataOutputStream.writeObject(packetQueue.poll());
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void startTCPStream () {
        try {
            socket = new Socket(_ipToSend, _portNo);
            dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
            dataInputStream = new ObjectInputStream(socket.getInputStream());
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
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
}
