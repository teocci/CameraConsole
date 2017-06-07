package com.github.teocci.net;

import com.github.teocci.Main;
import com.github.teocci.controllers.ConnectController;
import com.github.teocci.controllers.SendMessageController;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-May-19
 */
public class SocketManager extends BaseCore implements Runnable
{
    private Main application;

    private ConnectController connectController;
    private SendMessageController sendMessageController;
    private String host;
    private int port;
    private boolean isSSL = false;

    public SocketManager(Main application, String host, int port)
    {
        this.application = application;
        this.host = host;
        this.port = port;
        this.connectController = application.getConnectController();
        this.sendMessageController = application.getSendMessageController();
    }

    public void writeData(String str) throws Exception
    {
        write(str);
        application.getSendMessageController().addMessage("SEND", str);
    }

    public void writeByteData(byte[] msg) throws Exception
    {
        writeByte(msg);
        application.getSendMessageController().addMessage("SEND", msg.toString());
    }

    @Override
    public void run()
    {
        try {
            setHost(host, port, isSSL);

            Boolean isConn = connect();
            if (isConn) {
                application.setSocketManager(this);
                application.communicateSocket();

                readMessage();
            }

            if (application.getSendMessageController() != null) {
                application.getSendMessageController().setState("Reconnect succeeded");
            }
            connectController.setConnResultLabel(isConn, null);

        } catch (Exception e) {
            if (application.getSendMessageController() != null) { application.getSendMessageController().setState(e.toString()); }
            if (connectController != null) { connectController.setConnResultLabel(false, e.toString()); }

            System.out.println("SocketManager: run | " + e.toString());
        }
    }

    public boolean isConn()
    {
        return socket.isConnected();
    }
}
