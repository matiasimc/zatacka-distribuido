package game;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

import client.iClient;
import client.iClientGame;

public interface iGame extends Remote {

	void startGame(ArrayList<iClient> clients) throws RemoteException;

	void addClient(iClientGame clientGame)throws RemoteException;

	ArrayList<iPlayer> players() throws RemoteException;

	iPlayer gettingPlayer(int id) throws RemoteException;
	
	public int getAlives() throws RemoteException;
	
	boolean checkCollision(iPlayer player) throws RemoteException;
	
	int getHeight() throws RemoteException;
	
	int getWidth() throws RemoteException;
	
	void updateScores() throws RemoteException;
	
	void sortPlayers() throws RemoteException;
	
	boolean isPlaying() throws RemoteException;
	
	public void addPlayer(iPlayer player) throws RemoteException;
	
	public void voteNo() throws RemoteException;
	
	public ArrayList<iClientGame> getClientGames() throws RemoteException;

}