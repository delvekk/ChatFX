package chat.ChatWindow;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import chat.StartController;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Controller extends Thread {
    private Socket socket;
    private int connectionPort;
    private String connectionAddress;
    private BufferedReader in;
    private PrintWriter out;
    private String userName;
    private String messageToSend;
    private boolean connectionStatus = false;
    private ObservableList<String> connectedUsers;
    @FXML
    private TextArea textArea;
    @FXML
    private TextField textField;
    @FXML
    private Button discButton;
    @FXML
    private ListView<String> usersList;
    @FXML
    private Button sendButton;


    @FXML
    public void initialize() {
        try {
            connectedUsers = FXCollections.observableArrayList();
            usersList.setItems(connectedUsers);
            userName = StartController.getUserName();
            try {
                connectionPort = Integer.parseInt(StartController.getConnectionPort());
            } catch (NumberFormatException e) {
            }

            connectionAddress = StartController.getConnectionAddress();
            socket = new Socket(connectionAddress, connectionPort);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            if (in.readLine().equals("CONNECTING")) {
                out.println(userName);
                connectionStatus = true;
            }
            if (in.readLine().equals("ADDED")) {
                int countedUsers = Integer.parseInt(in.readLine());
                for (int i = 0; i < countedUsers; i++) {
                    connectedUsers.add(in.readLine());
                }
                textField.setDisable(false);
                textArea.appendText(in.readLine() + "\n");
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("OOOPS");
                alert.setHeaderText("NAME RESERVED");
                alert.setContentText("Can't connect to the server, try again");
                alert.showAndWait();
                textArea.appendText("NOT CONNECTED");
            }
        } catch (IOException e) {
        }
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if (connectionStatus) {
                    while (true) {
                        try {
                            String chatText = in.readLine();
                            if (chatText != null) {
                                String[] split = chatText.split(" ");
                                if (split.length == 2) {
                                    if (split[1].equals("JOINED")) {
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                connectedUsers.add(split[0]);
                                            }
                                        });
                                    }
                                    if (split[1].equals("DISCONNECTED")) {
                                        int index = connectedUsers.indexOf(split[0]);
                                        Platform.runLater(new Runnable() {
                                            @Override
                                            public void run() {
                                                connectedUsers.remove(index);
                                            }
                                        });
                                    }
                                }
                                textArea.appendText(chatText + "\n");
                            }
                        } catch (IOException e) {
                        }
                    }
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("OOOPS");
                            alert.setHeaderText("SERVER ERROR");
                            alert.setContentText("Can't connect to the server, try again");
                            alert.showAndWait();
                            textArea.appendText("NOT CONNECTED");
                        }
                    });
                }
            }
        });
        t.setDaemon(true);
        t.start();

        discButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Parent root;
                try {
                    if (connectionStatus) {
                        out.println("exit");
                    }
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("chat/start.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Welcome to chat");
                    stage.setScene(new Scene(root, 500, 300));
                    stage.show();
                    stage.setResizable(false);
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                } catch (IOException e) {
                }
            }
        });

        sendButton.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    sendButton.fire();
                }
            }
        });

        textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    sendButton.fire();
                }
            }
        });
    }

    @FXML
    public void sendMessage() {
        messageToSend = textField.getText();
        if (!messageToSend.equals("")) {
            out.println(messageToSend);
            textField.requestFocus();
            textField.clear();
        }
        if (messageToSend.equals("exit")) {
            try {
                socket.close();
                Platform.exit();
                System.exit(0);
            } catch (IOException e) {
            }
        }
    }

    @FXML
    public void exit() {
        if (connectionStatus) {
            out.println("exit");
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
        Platform.exit();
        System.exit(0);
    }

    @FXML
    public void clearChat() {
        textArea.clear();
    }
}
