package game;


import java.awt.Color;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import client.iClient;
import client.iClientGame;
import server.iServer;

class fHashMap extends HashMap<Integer, Boolean>{
	@Override
	public Boolean get(Object key) {
		if (super.get(key)==null) return false;
		else return super.get(key);
	}
}
public class Game extends UnicastRemoteObject implements iGame{

	private int height = 600;
	private int width = 800;
	private int growRate = 2; 
	PositionMatrix matrix; 
	private HashMap<Integer, iClientGame> gameThreads;
	private fHashMap askFrames;
	private HashMap<Integer, iPlayer> players;
	private HashMap<Integer, iPlayer> futurePlayers;
	private int votes;
	static int numberOfPlayers = 5;
	static final Color[] colorList = {Color.red, Color.green, Color.pink, Color.blue, Color.orange};
	private boolean playing;
	private iServer server;
	private int frames;
	
	public Game() throws RemoteException {
		players = new HashMap<Integer, iPlayer>();
		gameThreads = new HashMap<Integer, iClientGame>();
	}
		
	public Game(iServer server) throws RemoteException {
		players = new HashMap<Integer, iPlayer>();
		gameThreads = new HashMap<Integer, iClientGame>();
		matrix = new PositionMatrix(width, height);
		askFrames = new fHashMap();
		frames = 0;
		playing = false;
		this.server = server;
	}
	
	public iServer getServer() {
		return server;
	}

	public void setServer(iServer server) throws MalformedURLException, RemoteException, NotBoundException {
		this.server = server;
		this.server = (iServer) Naming.lookup("rmi://"+server.getDir()+":1099/ABC");
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
	public synchronized boolean checkCollision(int clientId) throws RemoteException {
		iPlayer player = gettingPlayer(clientId);
		if (player.getBody().size() == 0) return false;
		Point head = player.getHead();
		try{
			matrix.fill(head.x, head.y, player.getId(), head.visible);
			return false;
		}
		catch (CollisionException e) {
			return false;
		}
	}
	
	@Override
	public synchronized void addClient(int id, iClientGame clientGame) throws RemoteException {
;
		gameThreads.put(id, clientGame);
		
	}
	
	@Override
	public synchronized void addPlayer(iPlayer p) throws RemoteException {
		futurePlayers.put(p.getId()-1,p);
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
			for (iClientGame cgame: gameThreads.values()) cgame.resetBuffer();
			players = futurePlayers;
			if (players.size() < 2) {
				for (iClientGame cgame: gameThreads.values()) {
					System.out.println("Mate un cgame\n\n\n\n\n\n\n");
					cgame.close();
				}
				System.exit(1);
			}
			for (iPlayer pl: players.values()){
				System.out.println("Revivido");
				pl.revive(assignPoint());
			}
			playing = true;
		}
	}
	
	@Override
	public synchronized ArrayList<iPlayer> players() throws RemoteException {

		return new ArrayList<iPlayer>(this.players.values());
	}

	
	public synchronized iPlayer gettingPlayer(int clientId) throws RemoteException {
		return players.get(clientId);
	}
	
	public synchronized iPlayer newPlayer(int clientId) throws RemoteException {
		iPlayer player = new Player(assignColor(clientId), assignPoint(), clientId + 1);
		players.put(clientId,player);
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
		return;
		/*Collections.sort(this.players, new Comparator<iPlayer>() {
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
	    });*/
	}
	
	public synchronized boolean isPlaying() throws RemoteException {
		return this.playing;
	}
	private synchronized Point assignPoint() {

		return matrix.getPlace();
	}
	
	private synchronized Color assignColor(int id){
		return colorList[id];
	}
	public synchronized int getFrames() throws RemoteException{
		return this.frames;
	}
	public synchronized void increaseFrames(int id) throws RemoteException{
		boolean someoneask = false;
		for(int id2 : gameThreads.keySet()){
			if (askFrames.get(id2) == true){
				someoneask = true;
				break;
			}
		}
		if (!someoneask) {
			if (this.frames==this.growRate) this.frames = 1;
			else this.frames++;
		}
		askFrames.put(id, true);
		boolean allask = true;
		for(int id2 : gameThreads.keySet()){
			if (askFrames.get(id2) == false){
				allask = false;
				break;
			}
		}
		if (allask){
			for(int id2 : gameThreads.keySet()){
				askFrames.put(id2, false);
			}
		}
	}
	public synchronized int getGrowRate() throws RemoteException{
		return this.growRate;
	}
	
	public synchronized void moveUp(int clientId) throws RemoteException{
		this.gettingPlayer(clientId).moveUp();
	}
	
	public synchronized void moveDown(int clientId) throws RemoteException{
		this.gettingPlayer(clientId).moveDown();
	}

	@Override
	public synchronized boolean isAlive(int clientId) throws RemoteException {
		return this.gettingPlayer(clientId).isAlive();
	}

	@Override
	public synchronized void growUp(int clientId, boolean visibility) throws RemoteException {
		this.gettingPlayer(clientId).growUp(visibility);
	}
	
	public synchronized ArrayList<Point> getBody(int clientId) throws RemoteException{
		return this.gettingPlayer(clientId).getBody();
	}
	
	public synchronized Point getHead(int clientId) throws RemoteException{
		return this.gettingPlayer(clientId).getHead();
	}
	
	public synchronized HashSet<Integer> clientIds() throws RemoteException{
		return new HashSet<Integer>(this.gameThreads.keySet());
	}
	
	public synchronized Color getColor(int clientId) throws RemoteException{
		return this.gettingPlayer(clientId).getColor();
	}
}
	

