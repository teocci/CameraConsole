package controller;

import classes.main.Main;
import classes.main.ServiceDiscovery;
import classes.main.SocketConnect;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;


import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceTypeListener;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by sol on 2017-05-24.
 */
public class ConnectController implements Initializable {

    @FXML TextField ipTextField;
    @FXML TextField portTextField;
    @FXML
    Label connResultLabel;

    private Main main;
    private Thread thread;
    private SocketConnect socketConn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    /**
     * Connect 버튼 클릭 이벤트
     */
    public void connectSocket() throws IOException {
        try {



            // 입력한 IP, PORT 가 있으면 소켓 연결 시도
            if ( !ipTextField.getText().equals("") && !portTextField.getText().equals("")) {
                connResultLabel.setText("[" + ipTextField.getText() + "] 연결 시도");

                String host = ipTextField.getText().toString();
                int port = Integer.parseInt(portTextField.getText());

                main.setHost(host);
                main.setPort(port);

                socketConn = new SocketConnect(main, host, port);
                thread = new Thread(socketConn);
                thread.start();
                main.setSocketConnect(socketConn);

            } else {
                connResultLabel.setText("IP 또는 PORT 를 입력하세요");

                // 자동 탐색 테스트
                String list[] = new String[] {
                        "_http._tcp.local.",
                        "_ftp._tcp.local.",
                        "_tftp._tcp.local.",
                        "_ssh._tcp.local.",
                        "_smb._tcp.local.",
                        "_printer._tcp.local.",
                        "_airport._tcp.local.",
                        "_afpovertcp._tcp.local.",
                        "_ichat._tcp.local.",
                        "_eppc._tcp.local.",
                        "_presence._tcp.local.",
                        "_zookeeper._tcp.local.",
                        "_test._tcp.local."
                };

                ServiceDiscovery serviceDiscovery = new ServiceDiscovery();
                serviceDiscovery.main(list, this);


            }


        } catch ( Exception e ) {
            System.out.println(e.toString());
        }
    }


    public void settingMain(Main main) {
        this.main = main;
    }

    public void setConnResultLabel(Boolean b, String str) {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                // Socket 연결 성공
                if ( b ) { connResultLabel.setText("연결 성공^0^"); }
                // 연결 실패
                else { connResultLabel.setText("다시 시도해주세요"); }
            }
        });
    }

    String resultStr;
    public void setConnectResult(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
             //   resultStr = str;
             //   resultStr += str + "// \n";
                connResultLabel.setText(str);
            }
        });
    }

}
