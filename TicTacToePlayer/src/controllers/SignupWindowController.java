package controllers;

import Interface.Player;
import Interface.ServerInterface;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.ResourceBundle;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
 * FXML Controller class
 *
 * @author A7med
 */
public class SignupWindowController implements Initializable {

    // to control transition time
    private double pauseTransitionTime;
    @FXML
    private TextField signup_username;

    @FXML
    private PasswordField signup_password;

    @FXML
    private PasswordField signup_repassword;

    @FXML
    private Button signup_signup;

    @FXML
    private ImageView signup_img;

    @FXML
    private RadioButton gender_male;

    @FXML
    private RadioButton gender_female;

    @FXML
    private Text signup_error;
    ServerInterface serverInterface;
    boolean checkOnName;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        signup_img.setVisible(false);
        signup_error.setVisible(false);
    }

    @FXML
    // signup function
    void signupAction(ActionEvent event) {
        signup_img.setVisible(false);
        signup_error.setVisible(false);
        PauseTransition pt = new PauseTransition();
        pt.setDuration(Duration.seconds(pauseTransitionTime));
        pt.setOnFinished(e -> {
            System.out.println("Signup successful!");
            signup_img.setVisible(false);
        });

        // check signup fields
        if (signup_username.getText().isEmpty() || signup_password.getText().isEmpty()
                || signup_repassword.getText().isEmpty()) {
            signup_error.setVisible(true);
            signup_error.setText("Fields cannot be empty");

        } else if (signup_username.getText().matches(".*\\d+.*")) {
            signup_error.setVisible(true);
            signup_error.setText("Name should be characters only");
        } else if (signup_username.getText().length() > 20) {
            signup_error.setVisible(true);
            signup_error.setText("Username cannot be more than 20 characters");
        } else if (signup_password.getText().length() > 14
                || signup_repassword.getText().length() > 14
                || signup_password.getText().length() < 5
                || signup_repassword.getText().length() < 5) {
            signup_error.setVisible(true);
            signup_error.setText("Password should be between 5-14 characters");
        } else if (!signup_password.getText().matches(signup_repassword.getText())) {
            signup_error.setVisible(true);
            signup_error.setText("Passwords are not the same");
        } else {
            try {
                Player player = new Player();
                player.setUserName(signup_username.getText().toLowerCase());
                player.setPassword(signup_password.getText().toLowerCase());
                checkOnName = serverInterface.signUp(player);
                if (checkOnName == false) {
                    signup_error.setText("An error has occured. Username may exist!");
                    signup_error.setVisible(true);
                } else {
                    signup_error.setText("Signup successfully.");
                    signup_error.setVisible(true);
                    pt.play();
                    // get a handle to the stage
                    Stage stage = (Stage) signup_signup.getScene().getWindow();
                    // do what you have to do
                    stage.close();
                }
            } catch (RemoteException ex) {
                System.err.println(ex.getMessage());
            }
        }
    }

}
