package client;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import game.iGame;
import game.iPlayer;

public interface iClientGame extends Remote {

	void start() throws RemoteException;
	
	public void setRunning(boolean running) throws RemoteException;
	public boolean isRunning() throws RemoteException;
	public ArrayList<iPlayer> gamePlayers() throws RemoteException;
	public void resetVote() throws RemoteException;
	public void resetBuffer() throws RemoteException;
	public void close() throws RemoteException;
	public void setStarted(boolean s) throws RemoteException;
	public void setCountdown(int n) throws RemoteException;
	public void setGame(iGame game) throws RemoteException;
	public int getId() throws RemoteException;
	public void setContinu(boolean b) throws RemoteException;
}
