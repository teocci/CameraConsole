package sample;

import classes.main.SocketConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class ConnectMainController implements Initializable {


    @FXML private TextField ipTextField;    // IP 입력
    @FXML private TextField portTextField;  // Port 입력
    @FXML private TextArea sendTextArea;    // Socket Send 할 문자열 입력 영역
    @FXML public Label conStateLabel;  // 연결 상태글 표시
    @FXML private Label resultLabel;    // 입력된 키보드 문자 표시
    @FXML private AnchorPane sendPane;

    private Thread thread;
    private SocketConnect socketConn;
    private String keyInfo = "";    // 입력된 키보드 문자 저장

    /**
     * 소켓연결 시도
     * @param actionEvent
     * @throws Exception
     */
    public void connectClient(ActionEvent actionEvent) throws Exception {
        // 입력한 ip 로 연결 시도
       // if ( ipTextField.getText() != null && ipTextField.getText() != "" ) {

        System.out.println(ipTextField.getText());
        if ( !ipTextField.getText().equals("") && !portTextField.getText().equals("")) {
            conStateLabel.setText("["+ipTextField.getText() + "] 연결 시도");

            String host = ipTextField.getText().toString(); //"192.168.1.208"
            int port = Integer.parseInt(portTextField.getText());

            socketConn = new SocketConnect(null, host, port);
      //      socketConn.setting(null, host, port, false);

            try {
                //Thread thread = new Thread((Runnable) socketConn);
                thread = new Thread(socketConn);
                thread.start();

            } catch ( Exception e ) {
                System.out.println(e.toString());
            }

        } else {
            conStateLabel.setText("IP 또는 PORT 를 입력해주세요");
        }
    }

    public void set(String str) throws Exception {
        conStateLabel.setText(str);
    }

    /**
     * 'send' 버튼 클릭시, Socket Send 발생
     * @throws Exception
     */
    public void sendText() throws Exception {
        if ( socketConn != null && !sendTextArea.getText().equals("") )

            socketConn.writeData(sendTextArea.getText());
    }

    /**
     * Key Input Label Clear
     * @param actionEvent
     */
    public void clickTestBtn(ActionEvent actionEvent) {
        System.out.println("Click");
        if ( !resultLabel.getText().equals("") ) {
            keyInfo = "";
            resultLabel.setText(keyInfo);
        }

        try {
            if (socketConn.isConn()) {
                socketConn.disConnect();
            }
        } catch ( IOException e ) {
            System.out.println("Bye; " + e.toString());
        }
    }



    /**
     * KeyBoard Press Event
     * @param keyEvent
     */
    public void keyPressed(KeyEvent keyEvent) throws Exception {

        String keyInput = keyEvent.getCode().toString();
        if ( keyInput.equals("UP") || keyInput.equals("DOWN") || keyInput.equals("LEFT") || keyInput.equals("RIGHT")) {

            System.out.println(keyEvent.getCode());

            keyInfo += keyInput;//keyEvent.getCode().toString();
            resultLabel.setText(keyInfo);

            // 입력된 키보드 Input 정보 연결된 Socket 으로 전송
            if ( socketConn != null && thread != null ) socketConn.writeData(keyEvent.getCode().toString());

        }

    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
    }
}
