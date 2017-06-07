package classes.main;

import controller.ConnectController;
import controller.SendMessageController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

public class Main extends Application
{
    private SocketManager socketManager;
    private ConnectController connectController;
    private SendMessageController sendMessageController;

    // Socket 관련
    private String host;
    private int port;

    // Stage(Window) 관련
    private ArrayList<Stage> stageArrayList = new ArrayList<>();
    private Stage primaryStage;

    public void addStage(Stage stage) { stageArrayList.add(stage); }

    public void removeStage(Stage stage) { stageArrayList.remove(stage); }

    public ArrayList<Stage> getStageArrayList() { return stageArrayList; }

    public Stage getPrimaryStage() { return primaryStage; }

    private FXMLLoader fxmlLoader;

    @Override
    public void start(Stage stage) throws Exception
    {
        primaryStage = stage;
        fxmlLoader = new FXMLLoader(getClass().getResource("/views/connect_socket.fxml"));

        try {
            Scene scene = new Scene(fxmlLoader.load(), 700, 400);
            primaryStage.setScene(scene);
            connectController = fxmlLoader.getController();
            connectController.settingMain(this);
        } catch (IOException e) {
            System.out.println(e.toString());
        }

        primaryStage.setTitle("흑... 악마같은놈");
        primaryStage.show();
    }

    public void communicateSocket() throws IOException
    {
        Platform.runLater(() -> {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/views/send_message.fxml"));
                Scene sendScene = primaryStage.getScene();
                try {
                    sendScene.setRoot(fxmlLoader.load());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                primaryStage.setScene(sendScene);

                sendMessageController = fxmlLoader.getController();
                sendMessageController.init(Main.this);
        });
    }

    public void reconnectSocket()
    {
        if (!socketManager.isConn()) {
            socketManager = new SocketManager(this, host, port);
            Thread thread = new Thread(socketManager);
            thread.start();
        }
    }

    public void setScene(Scene scene) { primaryStage.setScene(scene); }

    public SocketManager getSocketManager() { return socketManager; }

    public void setSocketManager(SocketManager socketManager) { this.socketManager = socketManager; }

    public ConnectController getConnectController() { return connectController; }

    public SendMessageController getSendMessageController() { return sendMessageController; }

    public void setSendMessageController(SendMessageController sendMessageController) { this.sendMessageController = sendMessageController; }

    public void setHost(String host) { this.host = host; }

    public String getHost() { return host; }

    public void setPort(int port) { this.port = port; }

    public int getPort() { return port; }

    public static void main(String[] args)
    {
        launch(args);
    }
}
