
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface iClientGame extends Remote {

	void start() throws RemoteException;
	


	public ArrayList<iPlayer> gamePlayers() throws RemoteException;

}
