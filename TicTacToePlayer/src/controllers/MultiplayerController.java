package controllers;

import Interface.Player;
import Interface.ServerInterface;
import client.ClientImplementation;
import client.Utils;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.WeakEventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import static javafx.scene.layout.GridPane.getColumnIndex;
import static javafx.scene.layout.GridPane.getRowIndex;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import screenshot.ScreenShotController;

/**
 * FXML Controller class
 *
 * @author A7med
 */
public class MultiplayerController implements Initializable {

    @FXML
    private AnchorPane mainGamePane; //container for the whole window, used for screenshot

    private AnchorPane multiplayer_gamepane;

    AnchorPane home; // main pane of the program

    @FXML
    public ListView<Player> listView;

    @FXML
    private HBox currentTurn;

    @FXML
    private Label username;

    @FXML
    private ImageView imageView_00;

    @FXML
    private ImageView imageView_01;

    @FXML
    private ImageView imageView_02;

    @FXML
    private ImageView imageView_10;

    @FXML
    private ImageView imageView_11;

    @FXML
    private ImageView imageView_12;

    @FXML
    private ImageView imageView_20;

    @FXML
    private ImageView imageView_21;

    @FXML
    private ImageView imageView_22;

    @FXML
    private Label player1_score;

    @FXML
    private Label player1_name;

    @FXML
    private Label player2_score;

    @FXML
    private Label player2_name;

    @FXML
    private Label draw_score;

    @FXML
    private HBox currentTurn1;

    @FXML
    private ImageView refresh;

    @FXML
    private VBox multiplayer_messenger;

    @FXML
    private TextArea multiplayer_msg_display;

    @FXML
    private TextField multiplayer_msg_input;

    @FXML
    private Button multiplayer_msg_btn;

    @FXML
    private Button exit;

    @FXML
    private Text errorMessage;

    @FXML
    private GridPane gameBoard;

    @FXML
    private Button play_again;

    // move main window
    @FXML
    private VBox topBar;

    private double mouseDragDeltaX = 0;
    private double mouseDragDeltaY = 0;
    private EventHandler<MouseEvent> mousePressedHandler;
    private EventHandler<MouseEvent> mouseDraggedHandler;
    private WeakEventHandler<MouseEvent> weakMousePressedHandler;
    private WeakEventHandler<MouseEvent> weakMouseDraggedHandler;

    int rowIndex = 0;
    int columnIndex = 0;
    char cell[][] = new char[3][3];
    ServerInterface serverInterface;
    ClientImplementation clientImplementation;
    SigninWindowController signinController;

    private static MultiplayerController multiplayerController;
    String currentUser = SigninWindowController.player.getUserName();
    Player selectedPlayer;
    
    

    public MultiplayerController() {
        multiplayerController = this;
    }

    @FXML
    void multiplayer_msg_send(ActionEvent event) {
        try {
            String msg = multiplayer_msg_input.getText();
            if (msg.matches("^(?!\\s*$).+")) {
                clientImplementation.sendMsg(signinController.player.getUserName() + " : " + msg);
            }
        } catch (RemoteException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void displayMsg(String msg) {
        multiplayer_msg_input.setText("");
        multiplayer_msg_display.appendText(msg + "\n");
    }

    @FXML
    void refreshListView(ActionEvent event) {
        displayPlayerList();
    }

    @FXML
    void exit_action(ActionEvent event) throws IOException {
        returnToHomeScreen();

    }

    @FXML
    private void listViewClicked(MouseEvent event) throws RemoteException {
        displayPlayerList();
        if (listView.getSelectionModel().getSelectedItem() == null) {
            System.err.println("You Not Select AnyOne From ListView !!!");
            displayPlayerList();
             Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Play Invitation");
                alert.setHeaderText(null);
                alert.setContentText("Player go out");
                alert.showAndWait();
        } else {
            displayPlayerList();
            selectedPlayer = (Player) listView.getSelectionModel().getSelectedItem();
            if (serverInterface.isPlaying(selectedPlayer.getUserName())) {
                System.out.println("Player Is Busy");
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Play Invitation");
                alert.setHeaderText(null);
                alert.setContentText("Player is busy now");
                alert.showAndWait();
            } else {
                if (selectedPlayer != null) {
                    clientImplementation.sendInvitation(selectedPlayer);
                }
            }
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        username.setText("Hi, " + signinController.player.getUserName());
        System.out.println("player" + signinController.player.getUserName());
        displayPlayerList(); // refresh client list
        makeRestButtonInVisible();
        try {
            serverInterface = Utils.getServerInterface();
            if (serverInterface != null) {
                serverInterface.getScoreDuringInit(signinController.player.getUserName());

            }
        } catch (RemoteException ex) {
            Logger.getLogger(MultiplayerController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void startPlay() {
        System.out.println("Game Started");
        clientImplementation.startGame();
    }

    public void drawCell(int rowIndex, int columnIndex, char symbol) {
        Image x = new Image(getClass().getResourceAsStream("/sources/x.png"));
        Image o = new Image(getClass().getResourceAsStream("/sources/o.png"));

        if (symbol == 'x') {
            if (rowIndex == 0 && columnIndex == 0 && imageView_00.getImage() == null) {
                imageView_00.setImage(x);
                clientImplementation.shiftTurn();
                try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 0 && columnIndex == 1 && imageView_01.getImage() == null) {
                imageView_01.setImage(x);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 0 && columnIndex == 2 && imageView_02.getImage() == null) {
                imageView_02.setImage(x);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 1 && columnIndex == 0 && imageView_10.getImage() == null) {
                imageView_10.setImage(x);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 1 && columnIndex == 1 && imageView_11.getImage() == null) {
                imageView_11.setImage(x);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 1 && columnIndex == 2 && imageView_12.getImage() == null) {
                imageView_12.setImage(x);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 2 && columnIndex == 0 && imageView_20.getImage() == null) {
                imageView_20.setImage(x);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 2 && columnIndex == 1 && imageView_21.getImage() == null) {
                imageView_21.setImage(x);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 2 && columnIndex == 2 && imageView_22.getImage() == null) {
                imageView_22.setImage(x);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            if (rowIndex == 0 && columnIndex == 0 && imageView_00.getImage() == null) {
                imageView_00.setImage(o);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 0 && columnIndex == 1 && imageView_01.getImage() == null) {
                imageView_01.setImage(o);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 0 && columnIndex == 2 && imageView_02.getImage() == null) {
                imageView_02.setImage(o);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 1 && columnIndex == 0 && imageView_10.getImage() == null) {
                imageView_10.setImage(o);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 1 && columnIndex == 1 && imageView_11.getImage() == null) {
                imageView_11.setImage(o);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 1 && columnIndex == 2 && imageView_12.getImage() == null) {
                imageView_12.setImage(o);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 2 && columnIndex == 0 && imageView_20.getImage() == null) {
                imageView_20.setImage(o);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 2 && columnIndex == 1 && imageView_21.getImage() == null) {
                imageView_21.setImage(o);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (rowIndex == 2 && columnIndex == 2 && imageView_22.getImage() == null) {
                imageView_22.setImage(o);
                clientImplementation.shiftTurn();
                 try {
                    serverInterface.updateGameBoard(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @FXML
    private void setPattern(MouseEvent mouseEvent) {

        if (getRowIndex((ImageView) mouseEvent.getSource()) == null) {
            rowIndex = 0;
        } else {
            rowIndex = getRowIndex((ImageView) mouseEvent.getSource());
        }
        if (getColumnIndex((ImageView) mouseEvent.getSource()) == null) {
            columnIndex = 0;
        } else {
            columnIndex = getColumnIndex((ImageView) mouseEvent.getSource());
        }

        if (cell[rowIndex][columnIndex] == '\0') {
            if (clientImplementation != null) {
                if (clientImplementation.isItMyTurn()) {

                    if (clientImplementation.isbeginer()) {
                        clientImplementation.sendMove(rowIndex, columnIndex, 'x');
                    } else {
                        clientImplementation.sendMove(rowIndex, columnIndex, 'o');
                    }
                }
            }
        }

    }

    static class PlayerCell extends ListCell<Player> {

        HBox hbox = new HBox();
        ImageView status = new ImageView();
        Label username = new Label("");
        Label points = new Label("");

        public PlayerCell() {
            super();
            hbox.getChildren().addAll(status, username, points);
            HBox.setHgrow(username, Priority.ALWAYS);
        }

        @Override
        public void updateItem(Player player, boolean empty) {
            super.updateItem(player, empty);
            username.setMaxWidth(150);
            points.setMaxWidth(50);
            if (player != null) {
                if (player.getStatus() == 1) {
                    status.setImage(new Image("/sources/online.png"));
                } else {
                }
                username.setText("  " + player.getUserName());
                points.setText("" + player.getWin());
                setGraphic(hbox);
            }
        }
    }

    public static MultiplayerController getInstance() {
        return multiplayerController;
    }

    // display the XML record
    @FXML
    void displayRecord(ActionEvent event) {
        try {
            Stage newStage = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/myxml/Record.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            newStage.setResizable(false);
            newStage.initModality(Modality.APPLICATION_MODAL);
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

    }

    // update avaiable online users
    public void displayPlayerList() {
        Platform.runLater(() -> {
            try {
                List<Player> activePlayertList = serverInterface.displayClientList();
                for (int i = 0; i < activePlayertList.size(); i++) {
                    if (activePlayertList.get(i).getUserName().equals(SigninWindowController.player.getUserName())) {
                        activePlayertList.remove(i);
                    }
                }
                ObservableList<Player> list = FXCollections.observableList(activePlayertList);
                listView.setItems(list);
                listView.setCellFactory(param -> new PlayerCell());

            } catch (RemoteException ex) {
                Logger.getLogger(MultiplayerController.class
                        .getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

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

    public void displayErrorMessage(String userName) throws RemoteException {
        if (serverInterface != null) {
            serverInterface.unregisterClient(SigninWindowController.player.getUserName());
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText(userName + " You lost the connection...Try later");
                Optional<ButtonType> action = alert.showAndWait();
                if ("OK".equals(action.get().getText())) {
                    //returnToHomeScreen();
                    try {
                        clientImplementation = new ClientImplementation(signinController);
                        if (serverInterface != null) {
                            serverInterface.unregisterClient(SigninWindowController.player.getUserName());
                        } else {
                            System.out.println("Server Interface : null");
                        }
                        Stage stage = (Stage) exit.getScene().getWindow();
                        Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
                        Scene scene = new Scene(root);
                        stage.getIcons().add(new Image("/sources/tic-tac-toe.png"));
                        stage.setScene(scene);
                        stage.show();
                    } catch (RemoteException ex) {
                        Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            });
        }
    }

    @FXML
    void moveWindow(MouseEvent event) {
        allowDrag(topBar, (Stage) topBar.getScene().getWindow());
    }

    // Play again action after game ends
    @FXML
    private void playAgain(ActionEvent event) {
        resetGame();
    }

    public void updateScore(int win, int lose, int draw) throws RemoteException {
        player1_score.setText(win + "");
        player2_score.setText(lose + "");
        draw_score.setText(draw + "");
    }

    // This method used to clear screen after press on reset button 
    public void clearScreen() {
        imageView_00.setImage(null);
        imageView_01.setImage(null);
        imageView_02.setImage(null);
        imageView_10.setImage(null);
        imageView_11.setImage(null);
        imageView_12.setImage(null);
        imageView_20.setImage(null);
        imageView_21.setImage(null);
        imageView_22.setImage(null);
    }

    @FXML
    void takeScreenShot(ActionEvent event) {
        try {
            WritableImage image = mainGamePane.snapshot(new SnapshotParameters(), null);
            // TODO: probably use a file chooser here
            File file = new File("screenshot.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
            } catch (IOException e) {
                // TODO: handle exception here
            }

            Stage prtSc = new Stage();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/screenshot/ScreenShot.fxml"));
            Parent root = loader.load();
            //get instance from SignupWindowController
            ScreenShotController screenShotController = (ScreenShotController) loader.getController();
            screenShotController.setImage(image);
            Scene scene = new Scene(root);
            prtSc.setResizable(false);
            prtSc.setScene(scene);
            prtSc.show();

        } catch (IOException ex) {
            Logger.getLogger(MultiplayerController.class
                    .getName()).log(Level.SEVERE, null, ex);
            // TODO: handle exception here
        }
    }

    public void makeRestButtonVisible() {
        play_again.setVisible(true);
    }

    public void makeRestButtonInVisible() {
        play_again.setVisible(false);
    }

    public void returnToHomeScreen() {
        try {
            clientImplementation = new ClientImplementation(signinController);
            if (serverInterface != null) {
                serverInterface.unregisterClient(SigninWindowController.player.getUserName());
                serverInterface.tellPlayerStatusOther();
              
                 MultiplayerController.getInstance().displayPlayerList();
            } else {
                System.out.println("Server Interface : null");
            }
            Stage stage = (Stage) exit.getScene().getWindow();
            Parent root = FXMLLoader.load(getClass().getResource("/fxml/MainWindow.fxml"));
            Scene scene = new Scene(root);
            stage.getIcons().add(new Image("/sources/tic-tac-toe.png"));
            stage.setScene(scene);
            stage.show();
        } catch (RemoteException ex) {
            Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MultiplayerController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void resetGame(){
    
    try {
            if (serverInterface.checkPlayerOnline()) {
                serverInterface.sendResetMove();
                serverInterface.makeResetInVisible();
            } else {
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Attention");
                    alert.setHeaderText(null);
                    alert.initModality(Modality.APPLICATION_MODAL);
                    alert.setContentText("Player you play.. He is offline now ");
                    alert.showAndWait();
                });

            }
        } catch (RemoteException ex) {
            Logger.getLogger(MultiplayerController.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
    }
}
