package com.main.anthenaandroid;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.Socket;
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

    boolean ready = false;
    boolean isGameStarted = false;

    long retryTimer = 0;
    private static final int RETRY_DELAY = 2000;
    private static final int SOCKET_TIMEOUT_DURATION = 100;

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
    public RoomFinder () {
        this.type = RoomFinder.TYPE_STOMPER;
    }

    /**
     * Sends a stomp at the coordinates specified towards the server at the next loop
     * @param x
     * @param y
     */
    public void sendStomp (float x, float y) {
        if(isRoomFound) {
            GamePacket newPacket = new GamePacket(x, y, GamePacket.TYPE_STOMPER);
            packetQueue.add(newPacket);
        }
    }

    /**
     * Sends a movement packet ot the coordinates specified towards the server at the next loop
     * @param x
     * @param y
     */
    public void sendMovement (float x, float y) {
        if(isRoomFound) {
            GamePacket newPacket = new GamePacket(x, y, GamePacket.TYPE_RUNNER);
            packetQueue.add(newPacket);
        }
    }

    /**
     * Checks if the server has already sent a packet to indicate a game start after all players
     * have readied
     * @return true if game has started, false otherwise
     */
    public boolean checkGameStarted (){
        return isGameStarted;
    }

    public boolean checkRoomFound () { return isRoomFound;}

    /**
     * Broadcasts and establishes a TCP connection to an anthena server, then starts an infinite
     * loop of getting and sending data to the server every socket timeout. Sends 1 packet everytime
     * the socket times out
     */
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

    public boolean checkReadyState(){
        return ready;
    }
    /**
     * Gets data from the TCP socket until sockettimeout occurs
     */
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

    /**
     * Sends the recieved data into a queue to be retrieved from the main program. Edit in this
     * function if the data was to be sent directly to the main activity
     */
    private void transferGameDataToProgram (GamePacket gameData) {
        if(gameData.getType() == GamePacket.TYPE_GAMESTART) {
            isGameStarted = true;
        } else {
            rcvedPacketQueue.add(gameData);
        }
    }

    /**
     * Checks if there is data sent from the server
     *
     * @return true if there is data, false otherwise
     */
    public boolean hasDataFromServer () {
        if(rcvedPacketQueue.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Gets GamePacket data that is sent from the server, use hasDataFromServer prior to this to
     * check if there is data to be retrieved
     *
     * @return
     */
    public GamePacket getDataFromServer () {
        return rcvedPacketQueue.poll();
    }

    /*
        Sends the queued data to the server at each cycle
     */
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

    /**
     * Used in lobby to switch the player's type
     *
     * @param type - RoomFinder.TYPE_STOMPER, RoomFinder.TYPE_RUNNER
     */
    public void switchPlayerType (int type) {
        this.type = type;
        sendTypeChangePacket();
    }

    /**
     * Toggles the ready/unready state for lobby and sends the change to the server
     */
    public void toggleReadyState () {
        if(ready) {
            //Sets the player to not ready
            ready = false;
            sendUnreadyPacket();
        } else {
            ready = true;
            sendReadyPacket();
        }
    }

    private void sendReadyPacket () {
        if(isRoomFound) {
            GamePacket newPacket = new GamePacket(0, 0, GamePacket.TYPE_READY);
            packetQueue.add(newPacket);
        }
    }

    public void sendGameStartedPacket () {
        if(isRoomFound) {
            System.out.println("Sent I am loaded packet");
            GamePacket newPacket = new GamePacket(0, 0, GamePacket.TYPE_GAMESTART);
            packetQueue.add(newPacket);
        }
    }

    private void sendUnreadyPacket () {
        if(isRoomFound) {
            GamePacket newPacket = new GamePacket(0, 0, GamePacket.TYPE_UNREADY);
            packetQueue.add(newPacket);
        }
    }
    /**
     * Intialises a TCP connection to the server found in the broadcast, or reconnects to the server
     * via TCP
     */
    public void startTCPStream () {
        if(System.currentTimeMillis() > retryTimer) {
            try {
                socket = new Socket(_ipToSend, _portNo);
                dataOutputStream = new ObjectOutputStream(socket.getOutputStream());
                dataInputStream = new ObjectInputStream(socket.getInputStream());
                socket.setSoTimeout(SOCKET_TIMEOUT_DURATION);
                connected = true;
                //Initialisation packet to let server know what kind of player this is
                sendTypeChangePacket();
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

    /**
     * Sends a packet to let the server know what kind of player this is. Uses -1000,-1000 as a
     * reserved value for x,y for initialisation
     */
    private void sendTypeChangePacket() {
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
