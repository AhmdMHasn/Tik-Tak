package Interface;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;


public interface ServerInterface extends Remote {

    void sendInvitation(Player sender, Player receiver) throws RemoteException;

    void acceptInvitation(String sender, String receiver) throws RemoteException;

    void rejectInvitation(String sender, String receiver) throws RemoteException;

    void sendMove(String sender, String reciever, int rowIndex,
            int columnIndex, char symbol) throws RemoteException;

    Player login(String name, String password) throws RemoteException;

    List<Player> displayClientList() throws RemoteException;

    boolean signUp(Player player) throws RemoteException;

    void registerClient(String userName, ClientInterface clientInterface) throws RemoteException;

    void unregisterClient(String userName) throws RemoteException;

    void sendMessage(String sender, String msg, String receiver) throws RemoteException;

    void notifyAll(String newPlayer) throws RemoteException;

    void sendToOnlineClientErrorMessage(String userName) throws RemoteException;

    void sendResetMove() throws RemoteException;

    boolean isPlaying(String receiver) throws RemoteException;

    void makeResetInVisible() throws RemoteException;

    boolean checkPlayerOnline() throws RemoteException;

    void getScoreDuringInit(String userName) throws RemoteException;
   
    void makePlayersOffline() throws RemoteException;
    
    void tellPlayerStatusOther()throws RemoteException;
    
     void  updateGameBoard(int rowIndex,
            int columnIndex, char symbol)   throws RemoteException;    
}
