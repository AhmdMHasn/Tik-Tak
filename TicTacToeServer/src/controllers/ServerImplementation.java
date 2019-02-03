package controllers;

import Interface.ClientInterface;
import Interface.Player;
import Interface.ServerInterface;
import database.DatabaseConnection;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

public class ServerImplementation extends UnicastRemoteObject implements ServerInterface {

    ClientInterface clientInterface;

    Map<String, ClientInterface> clientsMap = new HashMap<>();

    DatabaseConnection databaseImplemntation;

    boolean isEnd = false;

    MainWindowController controller;

    Map<String, GameState> gameStateHashMap = new HashMap<>();

    List<String> list = new ArrayList<>();

    ClientInterface senderClient, receiverClient;

    GameState senderGameBoard, recieverGameBoard;

    public ServerImplementation(MainWindowController controller) throws RemoteException, ClassNotFoundException {
        this.controller = controller;
        databaseImplemntation = new DatabaseConnection();
    }

    @Override
    public boolean signUp(Player player) {
        return databaseImplemntation.signUpPlayer(player);
    }

    @Override
    public Player login(String userName, String password) {
        return databaseImplemntation.login(userName, password);
    }

    @Override
    public List<Player> displayClientList() {
        return databaseImplemntation.selectActivePlayers();
    }

    @Override
    public void registerClient(String userName, ClientInterface clientInterface) throws RemoteException {
        databaseImplemntation.setStatusActive(userName);
        for (Map.Entry<String, ClientInterface> client : clientsMap.entrySet()) {
            client.getValue().notifyOthers(userName);
        }
        clientsMap.put(userName, clientInterface);
        try {
            controller.refreshTableClients();
            notifyAll(userName);
        } catch (SQLException | RemoteException ex) {
            Logger.getLogger(ServerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("client added");
    }

    @Override
    public void unregisterClient(String userName) {
        System.out.println("user name: " + userName);
        databaseImplemntation.setOffline(userName);
        clientsMap.remove(userName);
        list.remove(userName);
        System.out.println("client removed");
        try {
            if (controller != null) {
                controller.refreshTableClients();
                displayClientList();
            } else {
                System.out.println("controller: null");
            }
        } catch (SQLException ex) {
            Logger.getLogger(ServerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendInvitation(Player sender, Player receiver) throws RemoteException {
        receiverClient = clientsMap.get(receiver.getUserName());
        if(sender != null && receiver != null )
        receiverClient.receiveInvitation(sender.getUserName(), receiver.getUserName());
    }

    @Override
    public void acceptInvitation(String sender, String receiver) throws RemoteException {
        senderClient = clientsMap.get(sender);
        list.add(sender);
        list.add(receiver);
        senderClient.acceptInvitation(sender, receiver);
        gameStateHashMap.put(sender, new GameState());
        // added to code
        receiverClient = clientsMap.get(receiver);  // receiver here
        receiverClient.acceptInvitation(sender, receiver);
        gameStateHashMap.put(receiver, new GameState());
    }

    @Override
    public void rejectInvitation(String sender, String receiver) throws RemoteException {
        senderClient = clientsMap.get(sender);
        senderClient.rejectInvitation(sender, receiver);
    }

    @Override
    public void sendMove(String sender, String reciever, int rowIndex,
            int columnIndex, char symbol) throws RemoteException {

        senderClient = clientsMap.get(sender);
        receiverClient = clientsMap.get(reciever);
        if (senderClient != null && receiverClient != null && isEnd != true) {
            if (reciever != null && sender != null) {
                try {
                    senderGameBoard = gameStateHashMap.get(sender);
                    recieverGameBoard = gameStateHashMap.get(reciever); // add to code
                    if (senderGameBoard != null) {
                        //senderGameBoard.setCellInBoard(rowIndex, columnIndex, symbol);
                        if (symbol == 'x') {
                            if (senderGameBoard.isWin(symbol)) {
                                System.out.println("x Win ");
                                senderClient.winMessage();
                                receiverClient.loseMessage();
                                senderClient.makeRestVisible();
                                receiverClient.makeRestVisible();
                                list.remove(reciever);
                                list.remove(sender);
                                isEnd = true;
                                senderGameBoard.setCounter(0);
                                recieverGameBoard.setCounter(0);
                                databaseImplemntation.updateWin(sender);
                                databaseImplemntation.updateLose(reciever);
                                senderClient.updatePlayerScore(databaseImplemntation.getWinScore(sender),
                                        databaseImplemntation.getLoseScore(sender), databaseImplemntation.getDrawScore(sender));
                                receiverClient.updatePlayerScore(databaseImplemntation.getWinScore(reciever),
                                        databaseImplemntation.getLoseScore(reciever), databaseImplemntation.getDrawScore(reciever));
                                System.out.println("sender: counter " + senderGameBoard.getCounter());
                                System.out.println("receiver : counter " + recieverGameBoard.getCounter());
                            } else if (senderGameBoard.isDraw()) {
                                senderClient.drawMessage();
                                receiverClient.drawMessage();
                                senderClient.makeRestVisible();
                                receiverClient.makeRestVisible();
                                list.remove(reciever);
                                list.remove(sender);
                                isEnd = true;
                                senderGameBoard.setCounter(0);
                                recieverGameBoard.setCounter(0);
                                databaseImplemntation.updateDraw(sender);
                                databaseImplemntation.updateDraw(reciever);
                                senderClient.updatePlayerScore(databaseImplemntation.getWinScore(sender),
                                        databaseImplemntation.getLoseScore(sender), databaseImplemntation.getDrawScore(sender));
                                receiverClient.updatePlayerScore(databaseImplemntation.getWinScore(reciever),
                                        databaseImplemntation.getLoseScore(reciever), databaseImplemntation.getDrawScore(reciever));
                                                                System.out.println("sender: counter " + senderGameBoard.getCounter());
                                System.out.println("receiver : counter " + recieverGameBoard.getCounter());
                            }
                        }
                    }
                    // add to code
                    if (recieverGameBoard != null) {
                        //recieverGameBoard.setCellInBoard(rowIndex, columnIndex, symbol);

                        if (symbol == 'o') {
                            if (recieverGameBoard.isWin(symbol)) {
                                senderClient.winMessage();
                                receiverClient.loseMessage();
                                senderClient.makeRestVisible();
                                receiverClient.makeRestVisible();
                                list.remove(reciever);
                                list.remove(sender);
                                isEnd = true;
                                senderGameBoard.setCounter(0);
                                recieverGameBoard.setCounter(0);
                                databaseImplemntation.updateWin(sender);
                                databaseImplemntation.updateLose(reciever);
                                senderClient.updatePlayerScore(databaseImplemntation.getWinScore(sender),
                                        databaseImplemntation.getLoseScore(sender), databaseImplemntation.getDrawScore(sender));
                                receiverClient.updatePlayerScore(databaseImplemntation.getWinScore(reciever),
                                        databaseImplemntation.getLoseScore(reciever), databaseImplemntation.getDrawScore(reciever));
                                                                System.out.println("sender: counter " + senderGameBoard.getCounter());
                                System.out.println("receiver : counter " + recieverGameBoard.getCounter());
                            } else if (recieverGameBoard.isDraw()) {
                                senderClient.drawMessage();
                                receiverClient.drawMessage();
                                senderClient.makeRestVisible();
                                receiverClient.makeRestVisible();
                                list.remove(reciever);
                                list.remove(sender);
                                databaseImplemntation.updateDraw(sender);
                                databaseImplemntation.updateDraw(reciever);
                                isEnd = true;
                                senderGameBoard.setCounter(0);
                                recieverGameBoard.setCounter(0);
                                senderClient.updatePlayerScore(databaseImplemntation.getWinScore(sender),
                                        databaseImplemntation.getLoseScore(sender), databaseImplemntation.getDrawScore(sender));
                                receiverClient.updatePlayerScore(databaseImplemntation.getWinScore(reciever),
                                        databaseImplemntation.getLoseScore(reciever), databaseImplemntation.getDrawScore(reciever));
                                 System.out.println("sender: counter " + senderGameBoard.getCounter());
                                System.out.println("receiver : counter " + recieverGameBoard.getCounter());
                            }
                        }
                    } else {
                        System.out.println("game board is null");
                    }
                    senderClient.receiveMove(rowIndex, columnIndex, symbol);
                    receiverClient.receiveMove(rowIndex, columnIndex, symbol);
                } catch (RemoteException ex) {
                    Logger.getLogger(ServerImplementation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
//            clientInterface.playerOutMessage();
            /*senderClient.playerOutMessage();
           receiverClient.playerOutMessage();
             */
            receiverClient.playerOutMessage();
            
        }

    }

    public List<Player> selectAllPlayers() {
        return databaseImplemntation.selectAllPlayers();
    }

    @Override
    public void sendMessage(String sender, String msg, String receiver) {
        senderClient = clientsMap.get(sender);
        receiverClient = clientsMap.get(receiver);
        try {
            if (senderClient != null && receiverClient != null) {
                senderClient.receiveMsg(sender, msg, receiver);
                receiverClient.receiveMsg(sender, msg, receiver);
            }
        } catch (RemoteException ex) {
            Logger.getLogger(ServerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void sendResetMove() {

        if (senderClient != null || receiverClient != null) {
            try {
                
                if(senderGameBoard != null && recieverGameBoard !=null){
                senderGameBoard.setCounter(0);
                recieverGameBoard.setCounter(0);
                }
                senderClient.receiveResetMove();
                receiverClient.receiveResetMove();
                senderClient.resetGame();
                receiverClient.resetGame();
                isEnd = false;
                clearGameBoard();
            } catch (RemoteException ex) {
                Logger.getLogger(ServerImplementation.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    
    public void clearGameBoard() {

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                senderGameBoard.setCellInBoard(i, j, ' ');
            }
        }

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                recieverGameBoard.setCellInBoard(i, j, ' ');
            }
        }
    }

    @Override
    public void notifyAll(String userName) throws RemoteException {
        displayClientList().forEach((_item) -> {
            Platform.runLater(() -> {
                Notifications notificationBuilder = Notifications.create()
                        .title("Online Player")
                        .text(userName + " Join For Game")
                        .darkStyle()
                        .graphic(null)
                        .hideAfter(Duration.seconds(5))
                        .position(Pos.BOTTOM_RIGHT);
                AudioClip note = new AudioClip(ServerImplementation.this.getClass().getResource("/sources/definite.mp3").toString());
                note.play();
                notificationBuilder.showInformation();
            });
        });
    }

    @Override
    public void sendToOnlineClientErrorMessage(String userName) throws RemoteException {
        //  clientInterface.receiveErrorMesssage();
        //senderClient.receiveErrorMesssage(userName);
        if (clientInterface != null) {
            clientInterface = clientsMap.get(userName);
            clientInterface.receiveErrorMesssage(userName);

        }
    }

    @Override
    public boolean isPlaying(String receiver) {
        for (int i = 0; i < list.size(); i++) {
            String get = list.get(i);
            if (receiver.equals(get)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void makeResetInVisible() throws RemoteException {
        senderClient.makeRestInVisible();
        receiverClient.makeRestInVisible();
    }

    @Override
    public boolean checkPlayerOnline() throws RemoteException {
        if (senderClient != null && receiverClient != null) {
            return databaseImplemntation.getStatus(receiverClient.getValueOfReceiver()) == 1;
        }

        return false;
    }

    @Override
    public void getScoreDuringInit(String userName) throws RemoteException {

        clientInterface = clientsMap.get(userName);

        if (clientInterface != null) {
            clientInterface.updateScoreDuringInit(databaseImplemntation.getWinScore(userName),
                    databaseImplemntation.getLoseScore(userName), databaseImplemntation.getDrawScore(userName));
        }

    }

    @Override
    public void makePlayersOffline() {
        List<Player> players = displayClientList();
        for (Player player : players) {
            databaseImplemntation.setOffline(player.getUserName());
            if (clientInterface != null) {
                clientInterface = clientsMap.get(player.getUserName());
                try {
                    clientInterface.receiveErrorMesssage(player.getUserName());
                } catch (RemoteException ex) {
                    Logger.getLogger(ServerImplementation.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void tellPlayerStatusOther() {
        try {
            //clientInterface.playerOutMessage();
                 
           // receiverClient.playerOutMessage();
           
           clientInterface.playerOutMessage();
            

        } catch (RemoteException ex) {
            Logger.getLogger(ServerImplementation.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void updateGameBoard( int rowIndex,
            int columnIndex, char symbol) throws RemoteException {
        
          if (recieverGameBoard != null && senderGameBoard != null ) {
                     recieverGameBoard.setCellInBoard(rowIndex, columnIndex, symbol);
                     senderGameBoard.setCellInBoard(rowIndex, columnIndex, symbol);
                     System.out.println("receiver : " + senderGameBoard.getCounter());
                     System.out.println("sender : " + recieverGameBoard.getCounter() );
                     System.out.println("receiver game board : " + recieverGameBoard.getBoard());

    } 
    }

    
    

   

   
    
}
