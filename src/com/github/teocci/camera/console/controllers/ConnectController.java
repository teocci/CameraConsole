package com.github.teocci.camera.console.controllers;

import com.github.teocci.camera.console.Main;
import com.github.teocci.camera.console.net.SocketManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-May-19
 */
public class ConnectController implements Initializable
{

    @FXML
    TextField ipTextField;
    @FXML
    TextField portTextField;
    @FXML
    Label connResultLabel;

    private Main application;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
    }

    public void connectSocket() throws IOException
    {
        try {
            // Attempt to connect socket if there is IP, PORT
            if (!ipTextField.getText().equals("") && !portTextField.getText().equals("")) {
                connResultLabel.setText("[" + ipTextField.getText() + "] Connection attempt");

                String host = ipTextField.getText();
                int port = Integer.parseInt(portTextField.getText());

                application.setHost(host);
                application.setPort(port);

                SocketManager socketManager = new SocketManager(application, host, port);
                Thread thread = new Thread(socketManager);
                thread.start();
                application.setSocketManager(socketManager);

            } else {
                connResultLabel.setText("Please enter IP and PORT");
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public void settingMain(Main main)
    {
        this.application = main;
    }

    public void setConnResultLabel(Boolean b, String str)
    {
        Platform.runLater(() -> connResultLabel.setText(b ? "Connection succeeded." : "Try again."));
    }

    public void setConnectResult(String str)
    {
        Platform.runLater(() -> {
            connResultLabel.setText(str);
        });
    }
}
