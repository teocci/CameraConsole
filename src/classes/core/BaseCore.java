package classes.core;


import classes.util.Debug;

import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by sol on 2017-05-19.
 */
public abstract class BaseCore {

    protected Socket socket;
    protected DataInputStream dis;
    protected DataOutputStream dos;
    protected ByteArrayOutputStream baos = new ByteArrayOutputStream();

    protected String host;
    protected int port;
    protected boolean isSSL;

    private static final int TIMEOUT = 10000;

    public void setHost(String host, int port, boolean isSSL) {
        this.isSSL = isSSL;
        this.host = host;
        this.port = port;
    }

    protected boolean connect() {

        try {

            socket = new Socket();
            socket.connect(new InetSocketAddress(host, port), TIMEOUT);

            if ( socket.isConnected()) {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
                socket.setSoTimeout(0);

                return true;
            }

        } catch ( Exception e ) {
            System.out.println("Socket_Fail; " + e.toString());
            //connect();
        }
        return false;
    }

    protected void write(String msg) throws Exception {
        String str = msg + "\n";
        dos.write(str.getBytes("UTF-8"));
        System.out.println("Send; " + str);
        dos.flush();
    }

    protected void writeByte(byte[] msg) throws Exception {
        dos.write(msg);
        String s = new String(msg);
        System.out.println("Send_b; " + s);
        dos.flush();
    }

    //protected String readMessage() throws Exception {
    protected void readMessage() throws Exception {

        byte[] buf = new byte[4096];
        int size = -1;//dis.read(buf);

        //while ( !Thread.currentThread().isInterrupted() ) {
        while ( true ) {
            size = dis.read(buf);
            String readMessage = new String(buf, 0, size, "UTF-8");
            System.out.println("Recv; " + readMessage);
            //return readMessage;
        }
       /* return null; */
    }

    public boolean isClose() {
        if ( socket != null && socket.isConnected() ) {
            return false;
        }
        return true;
    }

    public void disConnect() throws IOException {
        if ( socket != null && socket.isConnected() )  {
            socket.close();
        }
    }

}
