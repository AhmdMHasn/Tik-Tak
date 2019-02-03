package controllers;

import Interface.ServerInterface;
import client.Utils;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author A7med
 */
public class MainWindowController implements Initializable {

    Pane home; // main pane of the program

    // controllers to check if any window is opened
    boolean playOfflineCheck;
    boolean playOnlineCheck;
    boolean howToPlayCheck;
    boolean aboutUsCheck;

    @FXML
    private Button home_btn_playOffline;

    @FXML
    private Button home_btn_playOnline;

    @FXML
    private Button home_btn_howToPlay;

    @FXML
    private Button home_btn_aboutUs;

    @FXML
    private Button home_btn_exit;

    @FXML
    public AnchorPane home_mainPane;

    @FXML
    private HBox menuPane = new HBox();

    @FXML
    Label welcomeText;

    String name;
    
    boolean checkOnServerStatus = false;

     public static ServerInterface serverinterface;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            createPage();
            mainCheck();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    /*void setUsername(String s){
     welcomeText.setText("Welcome, " + s);
     }*/
    @FXML
    void home_exit_action(ActionEvent event) throws Exception {
        Platform.exit();
        System.exit(0);
    }

    @FXML
    void home_aboutUs_action(ActionEvent event) throws IOException {
        if (aboutUsCheck == false) {
            mainCheck();
            aboutUsCheck = true;
            home = FXMLLoader.load(getClass().getResource("/fxml/AboutUs.fxml"));
            home.getStylesheets().add(getClass().getResource("/style/MainStyle.css").toExternalForm());
            setNode(home);
            System.out.println("about us");
        }
    }

    @FXML
    void home_howToPlay_action(ActionEvent event) throws IOException {
        if (howToPlayCheck == false) {
            mainCheck();
            howToPlayCheck = true;
            home = FXMLLoader.load(getClass().getResource("/fxml/HowToPlay.fxml"));
            home.getStylesheets().add(getClass().getResource("/style/MainStyle.css").toExternalForm());
            setNode(home);
            System.out.println("how to play");
        }
    }

    @FXML
    void home_playOffline_action(ActionEvent event) throws IOException {
        if (playOfflineCheck == false) {
            mainCheck();
            playOfflineCheck = true;
            home = FXMLLoader.load(getClass().getResource("/fxml/Gameplay.fxml"));
            home.getStylesheets().add(getClass().getResource("/style/Gameplay.css").toExternalForm());
            home.setPrefHeight(600);
            home.setPrefWidth(600);
            setNode(home);
            System.out.println("playOffline");
        }
    }

    @FXML
    void home_playOnline_action(ActionEvent event) throws IOException, InterruptedException {
        try {
            checkOnServerStatus = false;
            serverinterface = Utils.getServerInterface();
        } catch (RemoteException ex) {
            connectionErrorDialog();
            checkOnServerStatus = true;
        }
        if (checkOnServerStatus == false) {
            if (playOnlineCheck == false) {
                mainCheck();
                playOnlineCheck = true;
             
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SigninWindow.fxml"));
                Parent root = loader.load();

                //   SigninWindowController signinWindowController = (SigninWindowController) loader.getController();
                // signinWindowController.setname(home_mainPane);
                root.getStylesheets().add(getClass().getResource("/style/MainStyle.css").toExternalForm());
                setNode(root);
                System.out.println("playOnline");
            }
        }
    }
    
    public static void connectionErrorDialog() {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Check your connection!");
            Optional<ButtonType> action = alert.showAndWait();
        });
    }
        // display new window
    private void setNode(Node node) {
        home_mainPane.getChildren().clear();
        home_mainPane.getChildren().add((Node) node);

        FadeTransition ft = new FadeTransition(Duration.millis(1000), node);
        ft.setFromValue(.1);
        ft.setToValue(1);
        ft.setCycleCount(1);
        ft.setAutoReverse(false);
        ft.play();
    }

    // say welcome method
    private void createPage() throws IOException {
        home = FXMLLoader.load(getClass().getResource("/fxml/Welcome.fxml"));
        setNode(home);
    }

    // set all open windows to false == none
    private void mainCheck() {
        playOfflineCheck = false;
        playOnlineCheck = false;
        howToPlayCheck = false;
        aboutUsCheck = false;
    }

    // move main window
    @FXML
    private VBox topBar;

    private double mouseDragDeltaX = 0;
    private double mouseDragDeltaY = 0;
    private EventHandler<MouseEvent> mousePressedHandler;
    private EventHandler<MouseEvent> mouseDraggedHandler;
    private WeakEventHandler<MouseEvent> weakMousePressedHandler;
    private WeakEventHandler<MouseEvent> weakMouseDraggedHandler;

    protected void allowDrag(Node node, Stage stage) {
        mousePressedHandler = (MouseEvent event) -> {
            mouseDragDeltaX = node.getLayoutX() - event.getSceneX();
            mouseDragDeltaY = node.getLayoutY() - event.getSceneY();
        };
        weakMousePressedHandler = new WeakEventHandler<>(mousePressedHandler);
        node.setOnMousePressed(weakMousePressedHandler);

        mouseDraggedHandler = (MouseEvent event) -> {
            stage.setX(event.getScreenX() + mouseDragDeltaX);
            stage.setY(event.getScreenY() + mouseDragDeltaY);
        };
        weakMouseDraggedHandler = new WeakEventHandler<>(mouseDraggedHandler);
        node.setOnMouseDragged(weakMouseDraggedHandler);
    }

    @FXML
    void moveWindow(MouseEvent event) {
        allowDrag(topBar, (Stage) topBar.getScene().getWindow());
    }

}
