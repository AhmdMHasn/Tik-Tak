package client;

import Interface.ClientInterface;
import Interface.Player;
import Interface.ServerInterface;
import controllers.MultiplayerController;
import controllers.SigninWindowController;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.media.AudioClip;
import javafx.stage.Modality;
import javafx.util.Duration;
import myxml.JavaToXML;
import org.controlsfx.control.Notifications;

public class ClientImplementation extends UnicastRemoteObject implements ClientInterface {

    private boolean currentTurn = false, isBeginer = false;
    private String receiver, sender, _symbol;
    int moveCounter = 0, _columnIndex, _rowIndex;
    JavaToXML toXMLClass = new JavaToXML(); // class to export/ import XML file

    SigninWindowController signinWindowController;
    ServerInterface serverInterface = Utils.serverinterface;

    public ClientImplementation(SigninWindowController signinWindowController) throws RemoteException {
        this.signinWindowController = signinWindowController;
    }

    public ClientImplementation() throws RemoteException {
    }

    @Override
    public void setClientActive(String clientName) throws RemoteException {
//        try {
//            signinWindowController.displayStatusActive(clientName);
//        } catch (SQLException ex) {
//            System.out.println("ss");
//        }
    }

    @Override
    public void setClientInactive(String clientName) throws RemoteException {
//        try {
//            signinWindowController.displyStatusInactive(clientName);
//        } catch (SQLException ex) {
//            System.out.println("ss");
//        }
    }

    public void startGame() {
        // this.currentTurn = true;
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Game Status");
            alert.setHeaderText(null);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Let’s play");
            alert.showAndWait();
        });
        if (this.isBeginer) {
            this.currentTurn = true;
        }
        if (!this.isBeginer) {
            this.currentTurn = false;
        }
    }

    public void sendInvitation(Player selectedPlayer) {
        this.isBeginer = true;
        this.sender = SigninWindowController.player.getUserName();
        this.receiver = selectedPlayer.getUserName();
        try {
            System.out.println(selectedPlayer.getUserName());
            if (serverInterface != null  && selectedPlayer != null ) {
                serverInterface.sendInvitation(SigninWindowController.player, selectedPlayer);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ClientImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void receiveInvitation(String sender, String receiver) throws RemoteException {
        this.isBeginer = false;
        this.receiver = receiver;
        this.sender = sender;
        String message = "Hi, " + receiver + ". " + sender + " wants to play with you?";
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Play Invitation");
            alert.setHeaderText(null);
            alert.setContentText(message);
            Optional<ButtonType> action = alert.showAndWait();
            try {
                if (action.get() == ButtonType.OK) {
                    System.out.println("sender");
                    // sender
                    serverInterface.acceptInvitation(sender, receiver);
//                    MultiplayerController.getInstance().startPlay();
                } else {
                    serverInterface.rejectInvitation(sender, receiver);
                }
            } catch (RemoteException ex) {
                Logger.getLogger(ClientImplementation.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @Override
    public void acceptInvitation(String sender, String receiver) throws RemoteException {
        System.out.println("receiver in accept invitation:" + receiver);
        this.receiver = receiver;
        MultiplayerController.getInstance().startPlay();

    }

    @Override
    public void rejectInvitation(String sender, String receiver) throws RemoteException {
        System.out.println("refuse");
    }

    @Override
    public void receiveMove(int rowIndex, int columnIndex, char symbol) throws RemoteException {

        _symbol = symbol + "";
        _columnIndex = columnIndex;
        _rowIndex = rowIndex;
        toXMLClass.addItemsToList(moveCounter++, _symbol + "", _columnIndex, _rowIndex);
        Platform.runLater(() -> MultiplayerController.getInstance().drawCell(rowIndex, columnIndex, symbol));
    }

    public boolean isItMyTurn() {
        return currentTurn;
    }

    public boolean isbeginer() {
        return isBeginer;
    }

    public void sendMove(int rowIndex, int columnIndex, char symbol) {
        String senderUserName = SigninWindowController.player.getUserName();
        try {
            if (serverInterface != null && isbeginer()) {
               
                    serverInterface.sendMove(senderUserName,
                            this.receiver, rowIndex, columnIndex, symbol);
                 
            } else if (serverInterface != null && !isbeginer()) {
  
                    serverInterface.sendMove(this.receiver, this.sender, rowIndex,
                            columnIndex, symbol);
               
            }

        } catch (RemoteException ex) {
            Logger.getLogger(ClientImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void winMessage() throws RemoteException {
        toXMLClass.addItemsToList(moveCounter++, _symbol + "", _columnIndex, _rowIndex);
        toXMLClass.writeToFIle(receiver);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("You won!");
            alert.setHeaderText(null);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Winner.. Winner!");
            Optional<ButtonType> action = alert.showAndWait();

        });
    }

    @Override
    public void loseMessage() throws RemoteException {
        toXMLClass.addItemsToList(moveCounter++, _symbol + "", _columnIndex, _rowIndex);
        toXMLClass.writeToFIle(receiver);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("You Lost!");
            alert.setHeaderText(null);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Loseeeeer :P");
            Optional<ButtonType> action = alert.showAndWait();
        });
    }

    @Override
    public void drawMessage() throws RemoteException {
        toXMLClass.addItemsToList(moveCounter++, _symbol + "", _columnIndex, _rowIndex);
        toXMLClass.writeToFIle(receiver);
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Draw");
            alert.setHeaderText(null);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("No one got it!");
            alert.showAndWait();
        });
    }

    @Override
    public void receiveMsg(String sender, String msg, String receiver) {
        MultiplayerController.getInstance().displayMsg(msg);
    }

    @Override
    public void receiveErrorMesssage(String userName) {
        try {
            MultiplayerController.getInstance().displayErrorMessage(userName);
        } catch (RemoteException ex) {
            Logger.getLogger(ClientImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("MultiplayerController.getInstance(): " + MultiplayerController.getInstance());
    }

    public void shiftTurn() {

        currentTurn = !currentTurn;

    }

    @Override
    public void receiveResetMove() throws RemoteException {

        Platform.runLater(() -> MultiplayerController.getInstance().clearScreen());
        //  System.out.println("receive ya somaaa");
    }

    // reset variables like  the begining  of game 
    @Override
    public void resetGame() throws RemoteException {

        if (this.isBeginer) {
            this.currentTurn = true;
        }
        if (!this.isBeginer) {
            this.currentTurn = false;
        }
    }

    @Override
    public void updatePlayerScore(int win, int lose, int draw) throws RemoteException {
        Platform.runLater(() -> {
            try {
                MultiplayerController.getInstance().updateScore(win, lose, draw);
            } catch (RemoteException ex) {
                Logger.getLogger(ClientImplementation.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @Override
    public void makeRestVisible() throws RemoteException {
        Platform.runLater(() -> MultiplayerController.getInstance().makeRestButtonVisible());
    }

    @Override
    public void makeRestInVisible() throws RemoteException {
        Platform.runLater(() -> MultiplayerController.getInstance().makeRestButtonInVisible());
    }

    @Override
    public void notifyOthers(String userName) throws RemoteException {
        Platform.runLater(() -> {
            MultiplayerController.getInstance().displayPlayerList();
            Notifications notificationBuilder = Notifications.create()
                    .title("Online Player")
                    .text(userName + " Join For Game Client")
                    .darkStyle()
                    .graphic(null)
                    .hideAfter(Duration.seconds(5))
                    .position(Pos.BOTTOM_RIGHT);
            AudioClip note = new AudioClip(ClientImplementation.this.getClass().getResource("/sources/definite.mp3").toString());
            note.play();
            notificationBuilder.showInformation();
        });
    }

    @Override
    public void updateScoreDuringInit(int win, int lose, int draw) throws RemoteException {
        Platform.runLater(() -> {
            try {
                MultiplayerController.getInstance().updateScore(win, lose, draw);
            } catch (RemoteException ex) {
                Logger.getLogger(ClientImplementation.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    @Override
    public void sendMsg(String msg) throws RemoteException {
        serverInterface.sendMessage(sender, msg, receiver);
    }

    @Override
    public String getValueOfReceiver() throws RemoteException {
        return receiver;
    }

    @Override
    public void playerOutMessage() throws RemoteException {
        Platform.runLater(() -> {
            MultiplayerController.getInstance().displayPlayerList();
            MultiplayerController.getInstance().resetGame();
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Draw");
            alert.setHeaderText(null);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.setContentText("Player go out!");
            alert.showAndWait();
        });
    }
}
