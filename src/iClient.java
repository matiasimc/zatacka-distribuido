

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iClient extends Remote {

	iClientGame getClientGame()throws RemoteException;


}
