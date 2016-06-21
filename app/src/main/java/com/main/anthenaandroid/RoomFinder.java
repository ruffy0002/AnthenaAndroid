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
    Queue<GamePacket> rcvedPacketQueue = new LinkedList<GamePacket>();
    private InetAddress _ipToSend;
    private int _portNo;

    Socket socket = null;
    ObjectOutputStream dataOutputStream = null;
    ObjectInputStream dataInputStream = null;

    boolean connected = false;

    long retryTimer = 0;
    private static final int RETRY_DELAY = 2000;

    public static final int TYPE_STOMPER = 0;
    public static final int TYPE_RUNNER = 1;
    public int type = TYPE_STOMPER;

    /**
     * Default type would be stomper
     * @param type - RoomFinder.TYPE_STOMPER, RoomFinder.TYPE_RUNNER
     */
    public RoomFinder (int type) {
        this.type = type;
    }

    public void sendStomp (float x, float y) {
        if(isRoomFound) {
            GamePacket newPacket = new GamePacket(x, y, GamePacket.TYPE_STOMPER);
            packetQueue.add(newPacket);
        }
    }

    public void sendMovement (float x, float y) {
        if(isRoomFound) {
            GamePacket newPacket = new GamePacket(x, y, GamePacket.TYPE_RUNNER);
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
            if(connected) {
                rcvData();
                sendData();
            } else {
                startTCPStream();
            }
        }
    }

    private void rcvData() {
        Object tmp;
        try {
            tmp = dataInputStream.readObject();
            if(tmp instanceof GamePacket) {
                transferGameDataToProgram((GamePacket) tmp);
            }
        } catch (IOException e) {

        } catch (ClassNotFoundException e) {

        }
    }

    private void transferGameDataToProgram (GamePacket gameData) {
        rcvedPacketQueue.add(gameData);
    }

    public boolean hasDataFromServer () {
        if(rcvedPacketQueue.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public GamePacket getDataFromServer () {
        return rcvedPacketQueue.poll();
    }

    private void sendData() {
        if (packetQueue.size() > 0) {
            try {
                dataOutputStream.writeObject(packetQueue.poll());
                System.out.println("Sending a packet");
            } catch (IOException ex) {
                System.out.println("Disconnected from server");
                connected = false;
            }
        }
    }

    public void startTCPStream () {
        if(System.currentTimeMillis() > retryTimer) {
            try {
                socket = new Socket(_ipToSend, _portNo);
                dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
                dataInputStream = new ObjectInputStream(socket.getInputStream());
                socket.setSoTimeout(100);
                connected = true;
                //Initialisation packet to let server know what kind of player this is
                sendInitialisationPacket();
                System.out.println("Connected to server");
            } catch (UnknownHostException e) {
                System.out.println("Connection failed, retrying in " + RETRY_DELAY + " seconds");
                retryTimer = System.currentTimeMillis() + RETRY_DELAY;
            } catch (IOException e) {
                System.out.println("Connection failed, retrying in " + RETRY_DELAY + " seconds");
                retryTimer = System.currentTimeMillis() + RETRY_DELAY;
            }
        }
    }

    private void sendInitialisationPacket() {
        if(type == GamePacket.TYPE_STOMPER) {
            sendStomp(-1000,-1000);
        } else if(type == GamePacket.TYPE_RUNNER) {
            sendMovement(-1000,-1000);
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
