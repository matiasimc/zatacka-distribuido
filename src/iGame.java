

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface iGame extends Remote {

	void startGame(ArrayList<iClient> clients) throws RemoteException;

	void addClient(iClientGame clientGame)throws RemoteException;

	ArrayList<iPlayer> players() throws RemoteException;

	iPlayer gettingPlayer(int id) throws RemoteException;
	
	boolean checkCollision(iPlayer player) throws RemoteException;
	

}