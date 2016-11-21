package game;


import java.awt.Color;
import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import client.ClientGame;
import client.iClient;
import client.iClientGame;
import server.iServer;

public class Game extends UnicastRemoteObject implements iGame{

	private int height = 600;
	private int width = 800;
	PositionMatrix matrix; 
	public ArrayList<iClientGame> gameThreads;
	ArrayList<iPlayer> players;
	ArrayList<iPlayer> futurePlayers;
	private int votes;
	static int numberOfPlayers = 5;
	static final Color[] colorList = {Color.red, Color.green, Color.pink, Color.blue, Color.orange};
	private boolean playing;
	private iServer server;
	
	public Game(iServer server) throws RemoteException {
		players = new ArrayList<iPlayer>();
		gameThreads = new ArrayList<iClientGame>();
		matrix = new PositionMatrix(width, height);
		playing = false;
		this.server = server;
		
	}
	
	public iServer getServer() {
		return server;
	}

	public void setServer(iServer server) throws MalformedURLException, RemoteException, NotBoundException, AlreadyBoundException {
		this.server = server;
		Naming.bind("rmi://"+server.getDir()+":1099/ABC", this.server);
	}

	@Override
	public synchronized void updateScores() throws RemoteException{
		for (iPlayer p : players()){
			if (p.isAlive()) {
				p.addScore();
			}
		}
	}
	@Override
	public synchronized void startGame(ArrayList<iClient> clients) throws RemoteException {
		for (iClient client: clients){
			client.getClientGame().setStarted(true);
			client.getClientGame().setCountdown(90);
		}
		playing = true;
	}
	
	@Override
	public int getHeight() throws RemoteException{
		return this.height;
	}
	
	@Override
	public int getWidth() throws RemoteException{
		return this.width;
	}
	
	@Override
	public synchronized boolean checkCollision(iPlayer player) throws RemoteException {
		if (player.getBody().size() == 0) return false;
		Point head = player.getHead();
		try{
			matrix.fill(head.x, head.y, player.getId(), head.visible);
			return false;
		}
		catch (CollisionException e) {
			matrix.deletePlayer(player.getBody(), player.getId());
			player.die();
			updateScores();
			if (getAlives() == 1){
				sortPlayers();
				playing = false;
				votes = 0;
				matrix = new PositionMatrix(height, width);
				for (iClientGame cGame: gameThreads) cGame.resetVote();
				for (iPlayer p: players()) p.resetBody();
				futurePlayers = new ArrayList<iPlayer>();
			}
			return true;
		}
	}
	
	@Override
	public synchronized void addClient(iClientGame clientGame) throws RemoteException {
;
		gameThreads.add(clientGame);
		
	}
	
	@Override
	public synchronized void addPlayer(iPlayer p) throws RemoteException {
		futurePlayers.add(p);
		System.out.print("Agregado");
		votes++;
		reset();
	}

	@Override
	public synchronized void voteNo() throws RemoteException {
		votes++;
		reset();
	}
	
	private synchronized void reset() throws RemoteException{
		if (votes == players.size()){
			for (iClientGame cgame: gameThreads) cgame.resetBuffer();
			players = futurePlayers;
			if (players.size() < 2) {
				for (iClientGame cgame: gameThreads) {
					System.out.println("Mate un cgame\n\n\n\n\n\n\n");
					cgame.close();
				}
				System.exit(1);
			}
			for (iPlayer pl: players){
				System.out.println("Revivido");
				pl.revive(assignPoint());
			}
			playing = true;
		}
	}
	
	@Override
	public synchronized ArrayList<iPlayer> players() throws RemoteException {
		return this.players;
	}

	
	public synchronized iPlayer gettingPlayer(int clientId) throws RemoteException {
		iPlayer player = new Player(assignColor(clientId), assignPoint(), clientId + 1);
		players.add(player);
		return player;
	}
	
	public synchronized int getAlives() throws RemoteException {
		int n = 0;
		for (iPlayer p: players()) {
			if (p.isAlive()) n++;
		}
		return n;
	}

	public synchronized void sortPlayers() throws RemoteException {
		Collections.sort(this.players, new Comparator<iPlayer>() {
	        @Override
	        public int compare(iPlayer p2, iPlayer p1)
	        {
	        	try {
	        		return  p1.getScore() - p2.getScore();
	        	}
	        	catch (RemoteException e){
	        		return 0;
	        	}
	        }	
	    });
	}
	
	public synchronized boolean isPlaying() throws RemoteException {
		return this.playing;
	}
	private synchronized Point assignPoint() {

		return matrix.getPlace();
	}
	
	public synchronized ArrayList<iClientGame> getClientGames() throws RemoteException {
		return this.gameThreads;
	}
	
	private synchronized Color assignColor(int id){
		return colorList[id];
	}
}
	

