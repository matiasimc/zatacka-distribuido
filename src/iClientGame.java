
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface iClientGame extends Remote {

	void start() throws RemoteException;
	
	public void setRunning(boolean running) throws RemoteException;
	public boolean isRunning() throws RemoteException;
	public ArrayList<iPlayer> gamePlayers() throws RemoteException;

}
