

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface iServer extends Remote {
	
	public void addClient(iClient client) throws RemoteException;
	//public void removeClient(IClient client);
	public void ready(iClient client) throws RemoteException;
	public void gettingInformation(iClient client) throws RemoteException;
	public iGame getGame() throws RemoteException;
	public int getIDClient() throws RemoteException;
}
