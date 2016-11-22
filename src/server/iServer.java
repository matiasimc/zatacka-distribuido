package server;


import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedList;

import client.iClient;
import game.iGame;

public interface iServer extends Remote {
	
	public void addClient(iClient client) throws RemoteException;
	public void ready(iClient client) throws RemoteException;
	public void gettingInformation(iClient client) throws RemoteException;
	public iGame getGame() throws RemoteException;
	public int getIDClient() throws RemoteException;
	public boolean canPlay() throws RemoteException;
	public void setQueue(LinkedList<iServer> s) throws RemoteException;
	public void setIdCounter(int id) throws RemoteException;
	public void setWaitPlayers(int w) throws RemoteException;
	public void setGame(iGame g) throws RemoteException;
	public void setClients(HashMap<Integer, iClient> l) throws RemoteException;
	public void setStarted(boolean b) throws RemoteException;
	public void addServer(iServer server) throws RemoteException;
	public String getDir() throws RemoteException;
	public void migrate() throws RemoteException, MalformedURLException, NotBoundException;
	public void printMigrate() throws RemoteException;
	public void removeClient(int clientId) throws RemoteException;
	public void setMain(boolean b) throws RemoteException;
	public boolean getMain() throws RemoteException;
	public void load() throws RemoteException;
	public double getUsage() throws RemoteException;
}
