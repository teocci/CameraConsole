package classes.main;

import controller.ConnectController;
import controller.SendMessageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application {

    public static Main main;

    private SocketConnect socketConnect;
    private ConnectController connectController;
    private SendMessageController sendMessageController;

    // Socket 관련
    private String host;
    private int port;

    // Stage(Window) 관련
    private ArrayList<Stage> stageArrayList = new ArrayList<Stage>();
    private Stage primaryStage;
    public void addStage(Stage stage) { stageArrayList.add(stage); }
    public void removeStage(Stage stage) { stageArrayList.remove(stage); }
    public ArrayList<Stage> getStageArrayList() { return stageArrayList; }
    public Stage getPrimaryStage() { return primaryStage; }

    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws Exception{

        primaryStage = stage;
        fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/connect_socket.fxml"));

        try {
            Scene scene = new Scene((BorderPane)fxmlLoader.load(), 700, 400);

            primaryStage.setScene(scene);

            connectController = (ConnectController)fxmlLoader.getController();
            connectController.settingMain(this);

        } catch ( IOException e ) {
            System.out.println(e.toString());
        }

        primaryStage.setTitle("흑... 악마같은놈");
        primaryStage.show();
    }

    public void communicateSocket() throws IOException {

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/send_message.fxml"));
                Scene sendScene = primaryStage.getScene();
                try {
                    sendScene.setRoot((Parent)fxmlLoader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                primaryStage.setScene(sendScene);

                sendMessageController = (SendMessageController)fxmlLoader.getController();
                sendMessageController.init(Main.this);
            }
        });

    }

    public void reconnectSocket()
    {
        if ( !socketConnect.isConn() ) {
            System.out.println("ZOZOZOZOZO");
            socketConnect = new SocketConnect(this, host, port);
            Thread thread = new Thread(socketConnect);
            thread.start();
        }
    }

    public void setScene(Scene scene) { primaryStage.setScene(scene); }

    public SocketConnect getSocketConnect() { return socketConnect; }
    public void setSocketConnect(SocketConnect socketConnect) { this.socketConnect = socketConnect; }

    public ConnectController getConnectController() { return connectController;   }

    public SendMessageController getSendMessageController() { return sendMessageController; }
    public void setSendMessageController(SendMessageController sendMessageController) { this.sendMessageController = sendMessageController; }

    public void setHost(String host) { this.host = host; }
    public String getHost() { return host; }

    public void setPort(int port) { this.port = port; }
    public int getPort() { return port; }

    public static void main(String[] args) {
        launch(args);
    }
}
