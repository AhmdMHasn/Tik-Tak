package controllers;

import Interface.Player;
import Interface.ServerInterface;
import client.ClientImplementation;
import client.Utils;
import java.io.IOException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author A7med
 */
public class SigninWindowController implements Initializable {

    AnchorPane home; // main pane of the program

    @FXML
    private TextField signin_username;

    @FXML
    private PasswordField signin_password;

    @FXML
    private Button signin_signup;

    @FXML
    private Button signin_signin;

    @FXML
    private Text signin_error;

    @FXML
    private ImageView signin_loading;

    @FXML
    private AnchorPane mainPane; // root pane

    // pause time after sign in/ up
    private final double pauseTransitionTime = 2.0;
    Registry registry;
    static ServerInterface serverinterface;
    ClientImplementation clientImplementation;
    boolean checkOnValidation;
    MultiplayerController multiplayerController;
    SignupWindowController signupWindowController;
    //name of client
    //String name = null;
    public static Player player;
    boolean notSignIn = true;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        signin_loading.setVisible(false);
        signin_error.setVisible(false);
        try {
            connectToServer();
        } catch (NotBoundException ex) {
            System.out.println("Check your connection!");
        }
    }

    public void connectionErrorDialog(String msg) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText(msg);
            alert.showAndWait();
        });
    }

    public void connectToServer() throws NotBoundException {

        try {
            serverinterface = Utils.getServerInterface();
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
            connectionErrorDialog("Check your connection!");
        }
        try {
            clientImplementation = new ClientImplementation(this);
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
            connectionErrorDialog("Check your connection!");
        }
    }

    @FXML
    // signin button action
    void signinAction(ActionEvent event) throws RemoteException {
        try {
            signin_loading.setVisible(false);
            signin_error.setVisible(false);
            PauseTransition pt = new PauseTransition();
            pt.setDuration(Duration.seconds(pauseTransitionTime));
            pt.setOnFinished(e -> {
                System.out.println("Signin successful!");
                signin_loading.setVisible(false);
            });

            // if the server is not exist
            if (serverinterface == null) {
                connectionErrorDialog("Check your connection then try to open the Multiplayer tab again.");
                return;
            }

            //validation on name and password
            for (Player signPlayer : serverinterface.displayClientList()) {
                if (signPlayer.getUserName().equals(signin_username.getText())) {
                    signin_error.setText("Already Login");
                    signin_error.setVisible(true);
                    notSignIn = false;
                }
            }

            if (signin_username.getText().isEmpty() || signin_password.getText().isEmpty()) {
                signin_error.setVisible(true);
                signin_error.setText("please fill fields");
                //username and password are not found in database
            } else {
                player = serverinterface.login(signin_username.getText(), signin_password.getText());
                if (player == null) {
                    signin_error.setVisible(true);
                    signin_error.setText("username or password are wrong");
                } else if (notSignIn) {
                    try {
                        serverinterface.registerClient(player.getUserName(), clientImplementation);
                        signin_loading.setVisible(true);
                        pt.play();

                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Multiplayer.fxml"));
                        Parent root = loader.load();

                        //get instance of multiplayercontroller
                        multiplayerController = (MultiplayerController) loader.getController();
                        //send object of serverImplementation to multiplayerController
                        multiplayerController.serverInterface = serverinterface;
                        //send clientobject to multiplayerController
                        multiplayerController.clientImplementation = clientImplementation;
                        //send name of client to multiplayerController
//                    multiplayerController.player = player;
                        root.getStylesheets().add(getClass().getResource("/style/MainStyle.css").toExternalForm());
//                    mainPane.getChildren().clear();
//                    mainPane.getChildren().add((Node) root);
                        mainPane.getScene().getWindow().hide();
                        Scene scene = new Scene(root);
                        Stage multiplayer = new Stage();
                        multiplayer.setResizable(false);
                        multiplayer.initModality(Modality.APPLICATION_MODAL);
                        multiplayer.initStyle(StageStyle.UNDECORATED);
                        multiplayer.getIcons().add(new Image("/sources/tic-tac-toe.png"));
                        multiplayer.setScene(scene);
                        multiplayer.show();
                    } catch (IOException ex) {
                        System.out.println(ex.getMessage());
                        connectionErrorDialog("Check your connection!");
                    }
                }
            }
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
            connectionErrorDialog("Check your connection!");
        }
    }

    @FXML
    // signup button action
    void signupAction(ActionEvent event) {
        signin_loading.setVisible(false);
        signin_error.setVisible(false);
        signin_loading.setVisible(true);

        signin_username.setText("");
        signin_password.setText("");

        PauseTransition pt = new PauseTransition();
        pt.setDuration(Duration.seconds(pauseTransitionTime));
        pt.setOnFinished((ActionEvent e) -> {
            try {
                Stage signUp = new Stage();
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/SignupWindow.fxml"));
                Parent root = loader.load();
                //get instance from SignupWindowController
                signupWindowController = (SignupWindowController) loader.getController();
                //
                signupWindowController.serverInterface = serverinterface;
                root.getStylesheets().add(getClass().getResource("/style/MainStyle.css").toExternalForm());
                Scene scene = new Scene(root);
                signUp.getIcons().add(new Image("/sources/tic-tac-toe.png"));
                signUp.setTitle("Signup");
                signUp.setResizable(false);
                signUp.setScene(scene);
                signUp.initModality(Modality.APPLICATION_MODAL);
                signUp.show();
                System.out.println("Signup successful!");
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                connectionErrorDialog("Check your connection!");
            }
        });
        pt.play();
    }
}
