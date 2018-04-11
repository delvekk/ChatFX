package chat;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.input.KeyEvent;

import java.io.IOException;


public class StartController {

    private static String userName;
    private static String connectionPort;
    private static String connectionAddress;
    @FXML
    private TextField nameField;
    @FXML
    private Button btnOpenNewWindow;
    @FXML
    private TextField addressField;
    @FXML
    private TextField portField;

    @FXML
    public void initialize() {
        btnOpenNewWindow.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Parent root;
                setName();
                setPort();
                setAddress();
                try {
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("chat/ChatWindow/window.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Chat");
                    stage.setScene(new Scene(root, 700, 500));
                    stage.show();
                    stage.setResizable(false);
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                } catch (IOException e) {
                }
            }
        });

        btnOpenNewWindow.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                Parent root;
                setName();
                setPort();
                setAddress();

                try {
                    root = FXMLLoader.load(getClass().getClassLoader().getResource("chat/ChatWindow/window.fxml"));
                    Stage stage = new Stage();
                    stage.setTitle("Chat");
                    stage.setScene(new Scene(root, 700, 500));
                    stage.setResizable(false);
                    stage.show();
                    ((Node) (event.getSource())).getScene().getWindow().hide();
                } catch (IOException e) {
                }
            }
        });
        btnOpenNewWindow.disableProperty().bind(Bindings.isEmpty(nameField.textProperty()).or(Bindings.isEmpty(addressField.textProperty())).or(Bindings.isEmpty(portField.textProperty())));
    }

    public void setName() {
        userName = nameField.getText();
    }

    public static String getUserName() {
        return userName;
    }

    public void setPort() {
        connectionPort = portField.getText();
    }

    public static String getConnectionPort() {
        return connectionPort;
    }

    public void setAddress() {
        connectionAddress = addressField.getText();
    }

    public static String getConnectionAddress() {
        return connectionAddress;
    }


}






