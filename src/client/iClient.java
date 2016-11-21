package client;


import java.rmi.Remote;
import java.rmi.RemoteException;

import server.iServer;

public interface iClient extends Remote {

	iClientGame getClientGame()throws RemoteException;

	int getID() throws RemoteException;
	void start() throws RemoteException;
	void setServer(iServer server) throws RemoteException;


}
