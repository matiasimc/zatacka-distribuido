package game;

import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import client.iClient;
import client.iClientGame;
import server.iServer;

public interface iGame extends Remote {

	void startGame(ArrayList<iClient> clients) throws RemoteException ;

	void addClient(int id, iClientGame clientGame);

	ArrayList<iPlayer> players() ;

	iPlayer gettingPlayer(int id) ;
	
	iPlayer newPlayer(int id) ;
	
	public int getAlives() ;
	
	boolean checkCollision(int clientId) ;
	
	int getHeight() ;
	
	int getWidth() ;
	
	void updateScores() ;
	
	void sortPlayers() ;
	
	boolean isPlaying() ;
	
	public void addPlayer(iPlayer player) throws RemoteException ;
	
	public void voteNo() throws RemoteException ;

	public void increaseFrames(int id) ;
	
	public int getFrames() ;
	
	public int getGrowRate() ;
	
	public boolean isAlive(int clientId) ;
	
	public void growUp(int clientId, boolean visibility) ;
	
	public void moveDown(int clientId) ;
	
	public void moveUp(int clientId) ;
	
	public HashSet<Integer> clientIds() ;
	
	public Point getHead(int clientId) ;
	
	public ArrayList<Point> getBody(int clientId) ;
	
	public Color getColor(int clientId) ;

	//public ArrayList<iClientGame> getClientGames() ;
	
	public iServer getServer() ;
	
	public PositionMatrix getPositionMatrix();
	
	public HashMap<Integer, Boolean> getAskFrames();
	
	public HashMap<Integer, iPlayer> getPlayers();
	
	public HashMap<Integer, iPlayer> getFuturePlayers(); 
	
	public int getVotes();
	
	public HashMap<Integer,iClientGame> getClientGames();

}