package com.github.teocci.camera.console.controllers;

import com.github.teocci.camera.console.Main;
import com.github.teocci.camera.console.net.SocketManager;
import com.github.teocci.camera.console.util.Debug;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;

import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ResourceBundle;

import static com.github.teocci.camera.console.controllers.SendMessageController.KeyOperator.LEFT_KEY;
import static com.github.teocci.camera.console.controllers.SendMessageController.KeyOperator.RIGHT_KEY;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-May-19
 */
public class SendMessageController implements Initializable
{
    public static enum KeyOperator
    {
        INIT("INIT"),
        LEFT_KEY("LEFT"),
        RIGHT_KEY("RIGHT"),
        UP_KEY("UP"),
        DOWN_KEY("DOWN"),
        A_KEY("A"),
        D_KEY("D"),
        Q_KEY("Q"),
        E_KEY("E"),
        W_KEY("W"),
        S_KEY("S"),
        UNKNOWN("");

        private String name;
        private boolean isPressed;

        KeyOperator(String name)
        {
            this.name = name;
        }

        boolean isPressed()
        {
            return this.isPressed;
        }

        void press()
        {
            updateState(true);
        }

        void release()
        {
            updateState(false);
        }

        boolean isKeyOperator(String keyName)
        {
            return this.name.equals(keyName);
        }

        void updateState(boolean state)
        {
            switch (this) {
                case LEFT_KEY:
                case RIGHT_KEY:
                case A_KEY:
                case D_KEY:
                case Q_KEY:
                case E_KEY:
                case UP_KEY:
                case DOWN_KEY:
                case W_KEY:
                case S_KEY:
                    isPressed = state;
                    break;
                default:
                    throw new AssertionError("Unknown operations " + this);
            }
        }

    }

    private final int CENTER_VALUE = 200; // this represent the value of the center
    private final int MAX_RANGE_VALUE = 100; // this represent the maximum value from the center
    private final int TALLY_OFF = 0; // this represent the maximum value from the center
    private final int TALLY_ON = 1; // this represent the maximum value from the center

    private Main application;
    private SocketManager socketManager;    // Socket connection
    private FXMLLoader fxmlLoader;          // UI Loader

    private Boolean isShiftDown = false;   // Shift modifier was pressed on a event
    static int deltaPosition = 10;          // Differential
    private ObservableList<String> listItems = FXCollections.observableArrayList();

    // Transmission data related (payload adjustment)
    private String tmpX;    // x (100~300, 200:center)
    private String tmpY;    // y (100~300, 200:center)
    private String tmpOn;   // Tally signal (0: off, 1: on)
    private String tmpZ;    // Height adjustment (0: down, 1: up, 2: stop)
    private byte STX = 0x02;
    private byte ETX = 0x03;

    @FXML
    private BorderPane background;
    @FXML
    private Label stateLabel;
    @FXML
    private ListView<String> sendMessageList;
    @FXML
    private ListView<String> receiveMessageList;
    @FXML
    private TextArea sendTextArea;
    @FXML
    private Button sendBtn;
    @FXML
    private CheckBox tallyCheckBox;
    @FXML
    private Label guideLabel;
    @FXML
    private Button leftBtn;
    @FXML
    private Button rightBtn;
    @FXML
    private Button frontBtn;
    @FXML
    private Button backBtn;
    @FXML
    private Button upBtn;
    @FXML
    private Button downBtn;
    @FXML
    private Button initBtn;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        initValues();

        String guide = "[X] A, D \n[Y] Q, R \n[Z] W, S";
        guideLabel.setText(guide);
    }


    /**
     * Reset payload adjustment
     */
    private void initValues()
    {
        leftBtn.setEffect(null);
        rightBtn.setEffect(null);
        upBtn.setEffect(null);
        downBtn.setEffect(null);
        frontBtn.setEffect(null);
        backBtn.setEffect(null);


        tmpX = "" + CENTER_VALUE;
        tmpY = "" + CENTER_VALUE;
        tmpZ = "2";
        tmpOn = "" + TALLY_OFF;

        isShiftDown = false;
    }

    /**
     * When a keyboard event occurs, the processing function
     *
     * @param keyOperator
     */
    public void sendInputData(KeyOperator keyOperator)
    {
        background.requestFocus();
        try {
            switch (keyOperator) {
                case INIT:
                    initDevice();
                    break;
                case LEFT_KEY:    // decrease
                case RIGHT_KEY:   // increase
                case A_KEY:
                case D_KEY:
                    // To adjust the Y value using the arrow keys, use the Shift button together.
                    if (isShiftDown && (keyOperator == LEFT_KEY || keyOperator == RIGHT_KEY)) {
                        setValueY(keyOperator.name);
                    } else {
                        setValueX(keyOperator.name);
                    }
                    break;

                case Q_KEY:
                case E_KEY:
                    setValueY(keyOperator.name);
                    break;

                case UP_KEY:
                case DOWN_KEY:
                case W_KEY:
                case S_KEY:
                    setValueZ(keyOperator.name);
                    break;
                default:
                    throw new AssertionError("Unknown operations " + this);
            }
//            switch (name) {
//                case "INIT":
//                    initDevice();
//                    break;
//                case "LEFT":    // decrease
//                case "A":
//                case "RIGHT":   // increase
//                case "D":
//                    // To adjust the Y value using the arrow keys, use the Shift button together.
//                    if (isShiftDown && (name.equals("LEFT") || name.equals("RIGHT"))) {
//                        setValueY(name);
//                    } else {
//                        setValueX(name);
//                    }
//                    break;
//
//                case "Q":
//                case "E":
//                    setValueY(name);
//                    break;
//
//                case "UP":
//                case "W":
//                case "DOWN":
//                case "S":
//                    setValueZ(name);
//                    break;
//
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Reset payload adjustment
     */
    private void initDevice()
    {
        initValues();
        sendByteMessage();
    }

    /**
     * On/Off Tally Signal
     */
    public void setTallySignal()
    {
        background.requestFocus();
        tmpOn = "" + (tallyCheckBox.isSelected() ? TALLY_ON : TALLY_OFF);

        sendByteMessage();
    }

    /**
     * Adjust the payload X value (left and right) - (LEFT, RIGHT), (A, D)
     *
     * @param type
     */
    private void setValueX(String type)
    {
        int initPos = Integer.parseInt(tmpX);

        if (type.equals("LEFT") || type.equals("A")) {
            leftBtn.setEffect(new InnerShadow());    // Effect when the button is pressed

            tmpX = String.valueOf(initPos - deltaPosition * 10);
            if (Integer.parseInt(tmpX) < CENTER_VALUE - MAX_RANGE_VALUE) {
                tmpX = "" + (CENTER_VALUE - MAX_RANGE_VALUE);
                stateLabel.setText("minimum x value...");
            } else stateLabel.setText("");
        } else if (type.equals("RIGHT") || type.equals("D")) {
            rightBtn.setEffect(new InnerShadow());
            tmpX = String.valueOf(initPos + deltaPosition * 10);
            if (Integer.parseInt(tmpX) > CENTER_VALUE + MAX_RANGE_VALUE) {
                tmpX = "" + (CENTER_VALUE + MAX_RANGE_VALUE);
                stateLabel.setText("Maximum x value...");
            } else stateLabel.setText("");
        }
        sendByteMessage();
    }

    /**
     * Adjustment of payload Y value (forward and backward) - (Shift+LEFT, Shift+RIGHT), (Q,E)
     *
     * @param type
     */
    private void setValueY(String type)
    {
        int initPos = Integer.parseInt(tmpY);

        if (isShiftDown) {
            if (type.equals("LEFT")) {
                frontBtn.setEffect(new InnerShadow());

                tmpY = String.valueOf(initPos - deltaPosition);
                if (Integer.parseInt(tmpY) < CENTER_VALUE - MAX_RANGE_VALUE) {
                    tmpY = "" + (CENTER_VALUE - MAX_RANGE_VALUE);
                    stateLabel.setText("minimum y value...");
                } else stateLabel.setText("");
            } else if (type.equals("RIGHT")) {
                backBtn.setEffect(new InnerShadow());

                tmpY = String.valueOf(initPos + deltaPosition);
                if (Integer.parseInt(tmpY) > CENTER_VALUE + MAX_RANGE_VALUE) {
                    tmpY = "" + (CENTER_VALUE + MAX_RANGE_VALUE);
                    stateLabel.setText("Maximum y value...");
                } else stateLabel.setText("");
            }
            sendByteMessage();
        } else {
            if (type.equals("Q")) {
                frontBtn.setEffect(new InnerShadow());

                tmpY = String.valueOf(initPos - deltaPosition);
                if (Integer.parseInt(tmpY) < CENTER_VALUE - MAX_RANGE_VALUE) {
                    tmpY = "" + (CENTER_VALUE - MAX_RANGE_VALUE);
                    stateLabel.setText("Minimum y value...");
                } else stateLabel.setText("");
            } else if (type.equals("E")) {
                backBtn.setEffect(new InnerShadow());

                tmpY = String.valueOf(initPos + deltaPosition);
                if (Integer.parseInt(tmpY) > CENTER_VALUE + MAX_RANGE_VALUE) {
                    tmpY = "" + (CENTER_VALUE + MAX_RANGE_VALUE);
                    stateLabel.setText("Maximum y value...");
                } else stateLabel.setText("");
            }
            sendByteMessage();
        }
    }

    /**
     * Adjust the payload Z value (up and down) - (UP, DOWN), (W, S)
     *
     * @param type
     */
    private void setValueZ(String type)
    {
        if (type.equals("UP") || type.equals("W")) {
            upBtn.setEffect(new InnerShadow());
            tmpZ = "1";
        } else if (type.equals("DOWN") || type.equals("S")) {
            downBtn.setEffect(new InnerShadow());
            tmpZ = "0";
        }

        sendByteMessage();
    }

    /**
     * Payload reconciliation data transfer (to Android)
     */
    private void sendByteMessage()
    {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            outputStream.write(STX);
            outputStream.write(tmpX.getBytes());
            outputStream.write(tmpY.getBytes());
            outputStream.write(tmpOn.getBytes());
            outputStream.write(tmpZ.getBytes());
            outputStream.write(ETX);
            outputStream.write("\n".getBytes());

            if (application.getSocketManager() != null) {
                application.getSocketManager().writeByteData(outputStream.toByteArray());
                listItems.add("X(" + tmpX + "), Y(" + tmpY + "), Z(" + tmpZ + "), Tally(" + tmpOn + ")");//new String(buffer));
                sendMessageList.setItems(listItems);
                // ListView Scroll event to move to the most recent item
                sendMessageList.getItems().addListener(new ListChangeListener<String>()
                {
                    @Override
                    public void onChanged(Change<? extends String> c)
                    {
                        sendMessageList.scrollTo(c.getList().size() - 1);
                    }
                });
            }
        } catch (Exception e) {
            Debug.err(e);
        }
    }

    /**
     * Socket Communications - Data Transfer
     */
    public void sendMessage()
    {

        background.requestFocus();
        Platform.runLater(() -> {
            try {
                if (!sendTextArea.getText().equals("")) {
                    // listItems.add(sendTextArea.getText());

                    if (application.getSocketManager() != null) {
                        application.getSocketManager().writeData(sendTextArea.getText());
                        listItems.add(sendTextArea.getText());
                        sendMessageList.setItems(listItems);
                    } else System.out.println("SendMessageCtrl; socket is null");
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
        });
    }


    /**
     * Adds Socket Send/Receive (SEND/RECV) Message
     *
     * @param type
     * @param msg
     */
    public void addMessage(String type, String msg) throws Exception
    {
        switch (type) {
            case "SEND":  // Outgoing message
                // TODO - implement this part.
                break;
            case "RECV":  // Received message
                System.out.println(msg);
                listItems.add(msg);
                receiveMessageList.setItems(listItems);
                receiveMessageList.getItems().addListener(new ListChangeListener<String>()
                {
                    @Override
                    public void onChanged(Change<? extends String> c)
                    {
                        receiveMessageList.scrollTo(c.getList().size() - 1);
                    }
                });
                break;
        }
    }

    /**
     * Reconnect Socket
     */
    public void reconnectSocket()
    {

        application.reconnectSocket();
    }

    /**
     * Socket connection state Label
     *
     * @param str
     */
    public void setState(String str)
    {
        Platform.runLater(() -> {
            stateLabel.setText(str);
        });
    }


    public void init(final Main application)
    {

        this.application = application;
        application.getPrimaryStage().requestFocus();
        background.requestFocus();

        // Keyboard Pressed / Released Event
        if (application != null) {
            // Key Pressed
            application
                    .getPrimaryStage()
                    .getScene()
                    .setOnKeyPressed(event -> {
                        KeyOperator keyOperator = getOperator(event.getCode().toString());
                        if (!keyOperator.isPressed) {
                            keyOperator.press();
                            sendInputData(keyOperator);
                        }
                        isShiftDown = event.isShiftDown();
                    });
            // Key Released
            application
                    .getPrimaryStage()
                    .getScene()
                    .setOnKeyReleased(event -> {
                        KeyOperator keyOperator = getOperator(event.getCode().toString());
                        if (keyOperator.isPressed) {
                            keyOperator.release();
                            sendInputData(KeyOperator.INIT);
                        }
                    });
        }
    }

    /**
     * Programmatic button Click event to control payload (Sequence: Initialize, Left, Right, Front, Back, Up, Down)
     */
    public void clickInitBtn() { sendInputData(KeyOperator.INIT); }

    public void clickLeftBtn() { sendInputData(LEFT_KEY); }

    public void clickRightBtn() { sendInputData(RIGHT_KEY); }

    public void clickFrontBtn()
    {
        isShiftDown = true;
        sendInputData(LEFT_KEY);
    }

    public void clickBackBtn()
    {
        isShiftDown = true;
        sendInputData(RIGHT_KEY);
    }

    public void clickUpBtn() { sendInputData(KeyOperator.UP_KEY); }

    public void clickDownBtn() { sendInputData(KeyOperator.DOWN_KEY); }

    KeyOperator getOperator(String keyName)
    {
        if (LEFT_KEY.isKeyOperator(keyName)) {
            return LEFT_KEY;
        } else if (RIGHT_KEY.isKeyOperator(keyName)) {
            return RIGHT_KEY;
        } else if (KeyOperator.UP_KEY.isKeyOperator(keyName)) {
            return KeyOperator.UP_KEY;
        } else if (KeyOperator.DOWN_KEY.isKeyOperator(keyName)) {
            return KeyOperator.DOWN_KEY;
        } else if (KeyOperator.A_KEY.isKeyOperator(keyName)) {
            return KeyOperator.A_KEY;
        } else if (KeyOperator.D_KEY.isKeyOperator(keyName)) {
            return KeyOperator.D_KEY;
        } else if (KeyOperator.Q_KEY.isKeyOperator(keyName)) {
            return KeyOperator.Q_KEY;
        } else if (KeyOperator.E_KEY.isKeyOperator(keyName)) {
            return KeyOperator.E_KEY;
        } else if (KeyOperator.W_KEY.isKeyOperator(keyName)) {
            return KeyOperator.W_KEY;
        } else if (KeyOperator.S_KEY.isKeyOperator(keyName)) {
            return KeyOperator.S_KEY;
        }
        return null;
    }
}
