package controller;

import classes.main.Main;
import classes.main.SocketConnect;
import classes.util.Debug;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by sol on 2017-05-24.
 */
public class SendMessageController implements Initializable {

    private Main mainCls;
    private SocketConnect socketConnect;    // 소켓연결
    private FXMLLoader fxmlLoader;  // 화면 셋팅을 위함

    private Boolean isPressedKey = false;   // Shift Key 눌렸는지 확인
    static int deltaPos = 10;    // 변화량
    private ObservableList<String> listItems = FXCollections.observableArrayList();

    // 전송 데이터 관련(탑재체 조정)
    private String tmpX = "200";    // x (100~300, 200:중립)
    private String tmpY = "200";    // y (100~300, 200:중립)
    private String tmpOn = "0";   // 탈리신호 (0: off, 1: on)
    private String tmpZ = "2";    // 높이 조절(0: down, 1: up, 2: stop)
    private byte STX = 0x02;
    private byte ETX = 0x03;
    private ByteArrayOutputStream outputStream;

    @FXML private BorderPane backgroud;
    @FXML private Label stateLabel;
    @FXML private ListView<String> sendMessageList;
    @FXML private ListView<String> receiveMessageList;
    @FXML private TextArea sendTextArea;
    @FXML private Button sendBtn;
    @FXML private CheckBox tallyCheckBox;
    @FXML private Label guideLabel;
    @FXML private Button leftBtn;
    @FXML private Button rightBtn;
    @FXML private Button frontBtn;
    @FXML private Button backBtn;
    @FXML private Button upBtn;
    @FXML private Button downBtn;
    @FXML private Button initBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        backgroud.requestFocus();

        String guide= "[X] A, D \n[Y] Q, R \n[Z] W, S";
        guideLabel.setText(guide);
    }

    /**
     * 키보드 이벤트 발생 시, 처리 함수
     * @param keyName
     */
    public void sendInputData(String keyName) {

        backgroud.requestFocus();
        //Debug.log(keyName);
        // 방향키별 액션
        try {
            switch (keyName) {
                case "INIT":
                    initDevice();
                    break;

                case "LEFT":    // 감소
                case "A":
                case "RIGHT":   // 증가
                case "D":
                    // 방향키 사용하여 Y 값 조정할 때엔, Shift 버튼 함께 사용하도록함.
                    if ( keyName.equals("LEFT") || keyName.equals("RIGHT")) {
                        if ( !isPressedKey ) { setValueX(keyName); }
                        else { setValueY(keyName); }
                    } else {
                        setValueX(keyName);
                    }
                    break;

                case "Q":
                case "E":
                    setValueY(keyName);
                    break;

                case "UP":
                case "W":
                case "DOWN":
                case "S":
                    setValueZ(keyName);
                    break;

            }
        } catch (Exception e) {}
    }


    /**
     * 탑재체 조정 초기화
     */
    private void initDevice() {

        leftBtn.setEffect(null);
        rightBtn.setEffect(null);
        upBtn.setEffect(null);
        downBtn.setEffect(null);
        frontBtn.setEffect(null);
        backBtn.setEffect(null);

        tmpX = "200";
        tmpY = "200";
        tmpZ = "2";

        isPressedKey = false;

        sendByteMessage();
    }

    /**
     * 탑재체 탈리 신호 On/Off
     */
    public void setTallySignal() {

        backgroud.requestFocus();
        if ( tallyCheckBox.isSelected() ) { tmpOn = "1"; }
        else { tmpOn = "0"; }

        sendByteMessage();
    }

    /**
     * 탑재체 X값 조정(좌우) - (LEFT, RIGHT), (A, D)
     * @param type
     */
    private void setValueX(String type)
    {
        int initPos = Integer.parseInt(tmpX);

        if ( type.equals("LEFT") || type.equals("A")) {
            leftBtn.setEffect(new InnerShadow());    // 버튼 누름 효과

            tmpX = String.valueOf(initPos-deltaPos);
            // 최소값 알림
            if ( Integer.parseInt(tmpX) < 100 ) {
                tmpX = "100";
                stateLabel.setText("minimum x value...");
            } else stateLabel.setText("");
        }
        else if ( type.equals("RIGHT") || type.equals("D") ) {
            rightBtn.setEffect(new InnerShadow());

            tmpX = String.valueOf(initPos+deltaPos);
            // 최대값 알림
            if ( Integer.parseInt(tmpX) > 300 ) {
                tmpX = "300";
                stateLabel.setText("maximum x value...");
            } else stateLabel.setText("");
        }
        sendByteMessage();
    }

    /**
     * 탑재체 Y값 조정(앞뒤) - (Shift+LEFT, Shift+RIGHT), (Q,E)
     * @param type
     */
    private void setValueY(String type)
    {
        int initPos = Integer.parseInt(tmpY);

        if ( isPressedKey ) {
            if ( type.equals("LEFT")  ) {
                frontBtn.setEffect(new InnerShadow());

                tmpY = String.valueOf(initPos-deltaPos);
                // 최소값 알림
                if ( Integer.parseInt(tmpY) < 100 ) {
                    tmpY = "100";
                    stateLabel.setText("minimum y value...");
                } else stateLabel.setText("");
            }
            else if ( type.equals("RIGHT") ) {
                backBtn.setEffect(new InnerShadow());

                tmpY = String.valueOf(initPos+deltaPos);
                // 최대값 알림
                if ( Integer.parseInt(tmpY) > 300 ) {
                    tmpY = "300";
                    stateLabel.setText("maximum y value...");
                } else stateLabel.setText("");
            }
            sendByteMessage();
        }
        else {
            if ( type.equals("Q") ) {
                frontBtn.setEffect(new InnerShadow());

                tmpY = String.valueOf(initPos-deltaPos);
                // 최소값 알림
                if ( Integer.parseInt(tmpY) < 100 ) {
                    tmpY = "100";
                    stateLabel.setText("minimum y value...");
                } else stateLabel.setText("");
            }
            else if ( type.equals("E") ) {
                backBtn.setEffect(new InnerShadow());

                tmpY = String.valueOf(initPos+deltaPos);
                // 최대값 알림
                if ( Integer.parseInt(tmpY) > 300 ) {
                    tmpY = "300";
                    stateLabel.setText("maximum y value...");
                } else stateLabel.setText("");
            }
            sendByteMessage();
        }

    }

    /**
     * 탑재체 Z값 조정(상하) - (UP, DOWN), (W, S)
     * @param type
     */
    private void setValueZ(String type)
    {
        if ( type.equals("UP") || type.equals("W") ) {
            upBtn.setEffect(new InnerShadow());
            tmpZ = "1";
        }
        else if ( type.equals("DOWN") || type.equals("S") ) {
            downBtn.setEffect(new InnerShadow());
            tmpZ = "0";
        }

        sendByteMessage();
    }

    /**
     * 탑재체 조정 데이터 전송(to Android)
     */
    private void sendByteMessage() {

        try {
            outputStream = new ByteArrayOutputStream();
            outputStream.write(STX);
            outputStream.write(tmpX.getBytes());
            outputStream.write(tmpY.getBytes());
            outputStream.write(tmpOn.getBytes());
            outputStream.write(tmpZ.getBytes());
            outputStream.write(ETX);
            outputStream.write("\n".getBytes());

            byte[] buf;
            buf = outputStream.toByteArray();

            // byte[] 형식으로 데이터 전송
            if (mainCls.getSocketConnect() != null) {
                mainCls.getSocketConnect().writeByteData(buf);
                listItems.add("X(" + tmpX + "), Y(" + tmpY + "), Z(" + tmpZ + "), Tally(" + tmpOn + ")");//new String(buf));
                sendMessageList.setItems(listItems);
                // ListView 스크롤 가장 최신 아이템으로 이동하도록하는 이벤트
                sendMessageList.getItems().addListener(new ListChangeListener<String>() {
                    @Override
                    public void onChanged(Change<? extends String> c) {
                        sendMessageList.scrollTo(c.getList().size()-1);
                    }
                });
            }
        } catch (Exception e) {
            Debug.err(e);
        }
    }

    /**
     * 소켓 통신 - 데이터 전송
     */
    public void sendMessage()  {

        backgroud.requestFocus();
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    if (!sendTextArea.getText().equals("")) {
                       // listItems.add(sendTextArea.getText());

                        if ( mainCls.getSocketConnect() != null ) {
                            mainCls.getSocketConnect().writeData(sendTextArea.getText());
                            listItems.add(sendTextArea.getText());
                            sendMessageList.setItems(listItems);
                        }
                        else System.out.println("SendMessageCtrl; socket is null");
                    }
                } catch (Exception e ){
                    System.out.println(e.toString());
                }
            }
        });
    }



    /**
     * Add Socket Send/Recv Message
     * @param type
     * @param msg
     */
    public void addMessage(String type, String msg) throws Exception {

        // 송신 메시지
        if ( type == "SEND") {

        }
        // 수신 메시지
        else if ( type == "RECV") {

            System.out.println(msg);
            listItems.add(msg);
            receiveMessageList.setItems(listItems);
            receiveMessageList.getItems().addListener(new ListChangeListener<String>() {
                @Override
                public void onChanged(Change<? extends String> c) {
                    receiveMessageList.scrollTo(c.getList().size()-1);
                }
            });
        }
    }


    /**
     * 소켓 재연결
     */
    public void reconnectSocket() {

        mainCls.reconnectSocket();
    }

    /**
     * 소켓 연결 상태 Label
     * @param str
     */
    public void setState(String str) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                stateLabel.setText(str);
              //  reconnectSocket();
            }
        });
    }


    public void init(final Main mainCls) {

        this.mainCls = mainCls;
        mainCls.getPrimaryStage().requestFocus();
        backgroud.requestFocus();

        // 키보드 Pressed/Released 이벤트
        if ( mainCls != null ) {
            // Key Pressed
            mainCls.getPrimaryStage().getScene().setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    sendInputData(event.getCode().toString());
                    isPressedKey = event.isShiftDown();
                }
            });
            // Key Released
            mainCls.getPrimaryStage().getScene().setOnKeyReleased(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    sendInputData("INIT");
                }
            });
        }
    }

    /**
     * 프로그램상 버튼 클릭 이벤트로 탑재체 제어(순서: 초기화,좌,우,앞,뒤,상,하)
     */
    public void clickInitBtn() { sendInputData("INIT"); }
    public void clickLeftBtn() { sendInputData("LEFT"); }
    public void clickRightBtn() { sendInputData("RIGHT"); }
    public void clickFrontBtn() { isPressedKey = true; sendInputData("LEFT"); }
    public void clickBackBtn() { isPressedKey = true; sendInputData("RIGHT"); }
    public void clickUpBtn() { sendInputData("UP"); }
    public void clickDownBtn() { sendInputData("DOWN"); }
}
