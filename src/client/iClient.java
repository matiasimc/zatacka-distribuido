package client;


import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import server.iServer;

public interface iClient extends Remote {

	iClientGame getClientGame()throws RemoteException;

	int getID() throws RemoteException;
	void start(boolean b) throws RemoteException;
	public void setServer(iServer server) throws MalformedURLException, RemoteException, NotBoundException;

	public iServer getServer() throws RemoteException;
	public String getAddress() throws RemoteException;
	public void restoreServer(iServer server) throws MalformedURLException, RemoteException, NotBoundException;
	public String getSnapshot() throws RemoteException;
	public String getServerIp() throws RemoteException;
	public void setServerIp(String serverIp) throws RemoteException;


}
