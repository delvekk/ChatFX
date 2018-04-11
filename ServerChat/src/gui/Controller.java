package gui;

import Server.ServerChat;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.io.IOException;

public class Controller {
    private static String serverAddress;
    private String serverPort;
    private static int parsedServerPort;
    private Thread serverThread;
    @FXML
    private TextField addressField;
    @FXML
    private TextField portField;
    @FXML
    private Button startBtn;
    @FXML
    private Button stopBtn;
    @FXML
    private TextArea textArea;


    @FXML
    public void initialize() {
        startBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                textArea.appendText("Starting server...\n");
                serverPort = portField.getText();
                try {
                    parsedServerPort = Integer.parseInt(serverPort);
                } catch (NumberFormatException e) {
                    textArea.appendText("Something wrong...\n");
                    return;
                }
                setAddress();
                stopBtn.setDisable(false);
                serverThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ServerChat.starServer();
                    }
                });
                serverThread.setDaemon(true);
                serverThread.start();
                textArea.appendText("Server is running...\n");

            }
        });

        stopBtn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                stopBtn.setDisable(true);
                textArea.appendText("Stopping server...\n");
                serverPort = null;
                serverAddress = null;
                try {
                    ServerChat.getServerSocket().close();
                    textArea.appendText("Server stopped\n");
                } catch (IOException e) {
                }
            }
        });
        startBtn.disableProperty().bind(Bindings.isEmpty(addressField.textProperty()).or(Bindings.isEmpty(portField.textProperty())));
    }

    public static int getPort() {
        return parsedServerPort;
    }

    public void setAddress() {
        serverAddress = addressField.getText();
    }

    public static String getServerAddress() {
        return serverAddress;
    }
}
