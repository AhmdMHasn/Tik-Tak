package client;

import Interface.ServerInterface;
import java.rmi.AccessException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/**
 *
 * @author Ismail Elmogy
 */
public class Utils {

    static ServerInterface serverinterface;

    public static ServerInterface getServerInterface() throws AccessException, RemoteException {

        Registry reg = null;
        try {
            //  reg = LocateRegistry.getRegistry("10.0.1.175",5000);
            if (serverinterface == null) {
                reg = LocateRegistry.getRegistry(1099);
//                   reg = LocateRegistry.getRegistry("10.0.1.183",1099);
                serverinterface = (ServerInterface) reg.lookup("servicename");
            }
        } catch (NotBoundException ex) {
            System.out.println(ex.getMessage());
            //  connectionErrorDialog();
        } catch (RemoteException ex) {
            System.out.println(ex.getMessage());
            // connectionErrorDialog();
        }

        return serverinterface;
    }
//
//    public static void connectionErrorDialog() {
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setContentText("Check your connection!");
//            Optional<ButtonType> action = alert.showAndWait();
//        });
    //   }

}
