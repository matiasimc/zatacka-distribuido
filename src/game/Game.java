package game;


import java.awt.Color;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import client.iClient;
import client.iClientGame;
import server.iServer;

public class Game extends UnicastRemoteObject implements iGame, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3463318830429467314L;
	private int height = 600;
	private int width = 800;
	private int growRate = 2; 
	PositionMatrix matrix; 
	private HashMap<Integer, iClientGame> gameThreads;
	private HashMap<Integer, Boolean> askFrames;
	private HashMap<Integer, iPlayer> players;
	private HashMap<Integer, iPlayer> futurePlayers;
	private int votes;
	static int numberOfPlayers = 5;
	static final Color[] colorList = {Color.red, Color.green, Color.pink, Color.blue, Color.orange};
	private boolean playing;
	private int frames;
	private iServer server;
	
	public Game(iServer server) throws RemoteException{
		players = new HashMap<Integer, iPlayer>();
		gameThreads = new HashMap<Integer, iClientGame>();
		this.server = server;
		matrix = new PositionMatrix(width, height);
		askFrames = new HashMap<Integer, Boolean>();
		futurePlayers = new HashMap<Integer, iPlayer>();
		frames = 0;
		playing = false;
	}
	
	public Game(iServer server, iGame g) throws RemoteException {
		this.server = server;
		
		HashMap<Integer, iClientGame> oClientGames = g.getClientGames();
		HashMap<Integer, iPlayer> oPlayers = g.getPlayers();
		HashMap<Integer, iPlayer> oFuture = g.getFuturePlayers();
		PositionMatrix oMatrix = g.getPositionMatrix();
		HashMap<Integer, Boolean> oAsk = g.getAskFrames();
		
		futurePlayers =new HashMap<Integer, iPlayer>();
		players = new HashMap<Integer, iPlayer>();
		matrix = new PositionMatrix(width, height);
		askFrames = new HashMap<Integer, Boolean>();
		gameThreads = new HashMap<Integer, iClientGame>();
		
		frames = g.getFrames();
		playing = g.isPlaying();
		votes = g.getVotes();
		
		for (Integer id: oPlayers.keySet()){
			players.put(id, new Player(oPlayers.get(id))); //.clone()
		}
		
		for (Integer id: oFuture.keySet()){
			players.put(id, new Player(oFuture.get(id))); //.clone()
		}
		for (Integer id: oClientGames.keySet()){
			gameThreads.put(id, oClientGames.get(id));
		}
		
		matrix = new PositionMatrix(oMatrix);
		
		for (Integer id: oAsk.keySet()){
			askFrames.put(id, oAsk.get(id));
		}
		
	}

	@Override
	public synchronized void updateScores() {
		for (iPlayer p : players()){
			if (p.isAlive()) {
				p.addScore();
			}
		}
	}
	@Override
	public synchronized void startGame(ArrayList<iClient> clients) throws RemoteException  {
		for (iClient client: clients){
			client.getClientGame().setStarted(true);
			client.getClientGame().setCountdown(90);
		}
		playing = true;
	}
	
	@Override
	public int getHeight() {
		return this.height;
	}
	
	@Override
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public synchronized boolean checkCollision(int clientId)  {
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
	public synchronized void addClient(int id, iClientGame clientGame)  {
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
	public synchronized ArrayList<iPlayer> players()  {

		return new ArrayList<iPlayer>(this.players.values());
	}

	
	public synchronized iPlayer gettingPlayer(int clientId)  {
		return players.get(clientId);
	}
	
	public synchronized iPlayer newPlayer(int clientId)  {
		iPlayer player = new Player(assignColor(clientId), assignPoint(), clientId + 1);
		players.put(clientId,player);
		return player;
	}
	public synchronized int getAlives()  {
		int n = 0;
		for (iPlayer p: players()) {
			if (p.isAlive()) n++;
		}
		return n;
	}

	public synchronized void sortPlayers()  {
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
	
	public synchronized boolean isPlaying()  {
		return this.playing;
	}
	private synchronized Point assignPoint() {

		return matrix.getPlace();
	}
	
	private synchronized Color assignColor(int id){
		return colorList[id];
	}
	public synchronized int getFrames() {
		return this.frames;
	}
	public synchronized void increaseFrames(int id) {
		boolean someoneask = false;
		for(int id2 : gameThreads.keySet()){
			if (askFrames.get(id2)!=null && askFrames.get(id2) == true){
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
			if (askFrames.get(id2)==null || askFrames.get(id2) == false){
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
	public synchronized int getGrowRate() {
		return this.growRate;
	}
	
	public synchronized void moveUp(int clientId) {
		this.gettingPlayer(clientId).moveUp();
	}
	
	public synchronized void moveDown(int clientId) {
		this.gettingPlayer(clientId).moveDown();
	}

	@Override
	public synchronized boolean isAlive(int clientId) throws RemoteException {
		return this.gettingPlayer(clientId).isAlive();
	}

	@Override
	public synchronized void growUp(int clientId, boolean visibility)  {
		this.gettingPlayer(clientId).growUp(visibility);
	}
	
	public synchronized ArrayList<Point> getBody(int clientId) {
		return this.gettingPlayer(clientId).getBody();
	}
	
	public synchronized Point getHead(int clientId) {
		return this.gettingPlayer(clientId).getHead();
	}
	
	public synchronized HashSet<Integer> clientIds() {
		return new HashSet<Integer>(this.gameThreads.keySet());
	}
	
	public synchronized Color getColor(int clientId) {
		return this.gettingPlayer(clientId).getColor();
	}

	@Override
	public iServer getServer()  {
		return this.server;
	}
	
	public PositionMatrix getPositionMatrix() throws RemoteException{
		return this.matrix;
	}
	
	public HashMap<Integer, Boolean> getAskFrames() throws RemoteException{
		return this.askFrames;
	}
	
	public HashMap<Integer, iPlayer> getPlayers() throws RemoteException{
		return this.players;
	}
	
	public HashMap<Integer, iPlayer> getFuturePlayers() throws RemoteException {
		return this.futurePlayers;
	}
	
	public int getVotes() throws RemoteException{
		return this.votes;
	}
	
	public HashMap<Integer,iClientGame> getClientGames() throws RemoteException{
		return this.gameThreads;
	}
}
	

