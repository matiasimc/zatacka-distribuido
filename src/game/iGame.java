package game;

import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import client.iClientGame;
import server.iServer;

public interface iGame extends Remote {

	void startGame() throws RemoteException ;

	void addClient(int id, iClientGame clientGame) throws RemoteException;

	ArrayList<iPlayer> players() throws RemoteException ;

	iPlayer gettingPlayer(int id) throws RemoteException;
	
	iPlayer newPlayer(int id, boolean started) throws RemoteException;
	
	public int getAlives() throws RemoteException;
	
	boolean checkCollision(int clientId) throws RemoteException;
	
	int getHeight() throws RemoteException;
	
	int getWidth() throws RemoteException;
	
	void updateScores() throws RemoteException ;
	
	boolean isPlaying() throws RemoteException;
	
	public void addPlayer(iPlayer player) throws RemoteException ;
	
	public void voteNo(int clientId) throws RemoteException ;

	public void increaseFrames(int id) throws RemoteException ;
	
	public int getFrames() throws RemoteException;
	
	public int getGrowRate() throws RemoteException ;
	
	public boolean isAlive(int clientId) throws RemoteException;
	
	public void growUp(int clientId, boolean visibility) throws RemoteException;
	
	public void moveDown(int clientId) throws RemoteException ;
	
	public void moveUp(int clientId) throws RemoteException;
	
	public HashSet<Integer> clientIds() throws RemoteException;
	
	public Point getHead(int clientId) throws RemoteException;
	
	public ArrayList<Point> getBody(int clientId) throws RemoteException;
	
	public Color getColor(int clientId) throws RemoteException;

	public void removeClient(int clientId, boolean migrate) throws RemoteException;
	
	public iServer getServer() throws RemoteException;
	
	public PositionMatrix getPositionMatrix() throws RemoteException;
	
	public HashMap<Integer, Boolean> getAskFrames() throws RemoteException;
	
	public HashMap<Integer, iPlayer> getPlayers() throws RemoteException;
	
	public HashMap<Integer, iPlayer> getFuturePlayers() throws RemoteException; 
	
	public void setPlaying(boolean p) throws RemoteException;
	
	public int getVotes() throws RemoteException;
	
	public HashMap<Integer,iClientGame> getClientGames() throws RemoteException;
	
	public HashMap<Color, Boolean> getColors() throws RemoteException;
	
	public void forceCollision(int clientId) throws RemoteException;
	
	public void setPaused(boolean p) throws RemoteException;
	
	public boolean getPaused() throws RemoteException;
	
	public void setCountdown(int time) throws RemoteException;

	public String getSnapshot();
	
	//public int getMaxVotes() throws RemoteException;

}