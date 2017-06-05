package classes.main;

import classes.core.BaseCore;
import controller.ConnectController;
import controller.SendMessageController;
import sample.ConnectMainController;

/**
 * Created by sol on 2017-05-19.
 */
public class SocketConnect extends BaseCore implements Runnable {

    private Main main;

    private ConnectMainController connController;
    private ConnectController connectController;
    private SendMessageController sendMessageController;
    private String host;
    private int port;
    private boolean isSSL = false;

    public SocketConnect(Main main, String host, int port) {
        this.main = main;
        this.host = host;
        this.port = port;
        this.connectController = main.getConnectController();
        this.sendMessageController = main.getSendMessageController();
    }

    public void writeData(String str) throws Exception {
        write(str);
        main.getSendMessageController().addMessage("SEND", str);
    }

    public void writeByteData(byte[] msg) throws Exception {
        writeByte(msg);
        main.getSendMessageController().addMessage("SEND", msg.toString());
    }

    @Override
    public void run() {
        try {
            setHost(host, port, isSSL);

            Boolean isConn = connect();
            if ( isConn ) {
                main.setSocketConnect(this);
                main.communicateSocket();

                readMessage();
                //main.getSendMessageController().addMessage("RECV", readMessage());
            }

            if ( main.getSendMessageController() != null ) {
                main.getSendMessageController().setState("재연결성공");
            }
            connectController.setConnResultLabel(isConn, null);

        } catch ( Exception e ) {
            if ( main.getSendMessageController() != null ) { main.getSendMessageController().setState(e.toString()); }
            if ( connectController != null ) { connectController.setConnResultLabel(false, e.toString()); }

            System.out.println("SocketConnect_run_Fail... " + e.toString());

        }
    }

    public boolean isConn() {
        return socket.isConnected();
    }
}
