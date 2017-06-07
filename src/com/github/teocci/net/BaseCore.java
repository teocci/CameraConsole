package com.github.teocci.net;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by teocci.
 * <p>
 * BaseCore
 *
 * @author teocci@yandex.com on 2017-May-19
 */
public abstract class BaseCore
{
    protected Socket socket;
    protected DataInputStream dis;
    protected DataOutputStream dos;

    protected String host;
    protected int port;
    protected boolean isSSL;

    private static final int TIMEOUT = 10000;

    public void setHost(String host, int port, boolean isSSL)
    {
        this.isSSL = isSSL;
        this.host = host;
        this.port = port;
    }

    protected boolean connect()
    {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), TIMEOUT);
            if (socket.isConnected()) {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                socket.setSoTimeout(0);

                return true;
            }

        } catch (Exception e) {
            System.out.println("Socket_Fail; " + e.toString());
        }
        return false;
    }

    protected void write(String msg) throws Exception
    {
        String str = msg + "\n";
        dos.write(str.getBytes("UTF-8"));
        System.out.println("Send; " + str);
        dos.flush();
    }

    protected void writeByte(byte[] msg) throws Exception
    {
        dos.write(msg);
        System.out.println("Send_b; " + msg.toString());
        dos.flush();
    }

    protected void readMessage() throws Exception
    {
        byte[] buf = new byte[4096];
        int size;

        while (true) {
            size = dis.read(buf);
            String readMessage = new String(buf, 0, size, "UTF-8");
            System.out.println("Recv; " + readMessage);
        }
    }

    public boolean isClose()
    {
        return socket != null && socket.isConnected();
    }

    public void disConnect() throws IOException
    {
        if (socket != null && socket.isConnected()) {
            socket.close();
        }
    }
}
