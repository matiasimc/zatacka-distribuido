

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface iGame extends Remote {

	void startGame(ArrayList<iClient> clients) throws RemoteException;

	void addThread(iClientGame clientGame)throws RemoteException;
	

}