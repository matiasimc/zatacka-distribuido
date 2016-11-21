package game;


import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;

import client.iClient;
import client.iClientGame;

public interface iGame extends Remote {

	void startGame(ArrayList<iClient> clients) throws RemoteException;

	void addClient(int id, iClientGame clientGame)throws RemoteException;

	ArrayList<iPlayer> players() throws RemoteException;

	iPlayer gettingPlayer(int id) throws RemoteException;
	
	iPlayer newPlayer(int id) throws RemoteException;
	
	public int getAlives() throws RemoteException;
	
	boolean checkCollision(int clientId) throws RemoteException;
	
	int getHeight() throws RemoteException;
	
	int getWidth() throws RemoteException;
	
	void updateScores() throws RemoteException;
	
	void sortPlayers() throws RemoteException;
	
	boolean isPlaying() throws RemoteException;
	
	public void addPlayer(iPlayer player) throws RemoteException;
	
	public void voteNo() throws RemoteException;
	
	public void increaseFrames(int id) throws RemoteException;
	
	public int getFrames() throws RemoteException;
	
	public int getGrowRate() throws RemoteException;
	
	public boolean isAlive(int clientId) throws RemoteException;
	
	public void growUp(int clientId, boolean visibility) throws RemoteException;
	
	public void moveDown(int clientId) throws RemoteException;
	
	public void moveUp(int clientId) throws RemoteException;
	
	public HashSet<Integer> clientIds() throws RemoteException;
	
	public Point getHead(int clientId) throws RemoteException;
	
	public ArrayList<Point> getBody(int clientId) throws RemoteException;
	
	public Color getColor(int clientId) throws RemoteException;

}