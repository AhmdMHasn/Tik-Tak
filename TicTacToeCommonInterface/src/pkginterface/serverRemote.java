package pkginterface;

 import java.rmi.*;
import pkginterface.clientRemote;

public interface serverRemote extends Remote
{

    /**
     *
     * @param i
     * @return
     * @throws java.rmi.RemoteException
     */
   void tellAll (String str) throws RemoteException;
   void register (clientRemote clientInt) throws RemoteException;
    void unregister (clientRemote clientInt) throws RemoteException;
}
 
