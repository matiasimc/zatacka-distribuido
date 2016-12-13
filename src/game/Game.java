package game;


import java.awt.Color;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import client.iClientGame;
import server.iServer;

public class Game extends UnicastRemoteObject implements iGame, Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3463318830429467314L;
	private int height = 600;
	private int width = 800;
	private int growRate = 3; 
	PositionMatrix matrix; 
	private HashMap<Integer, iClientGame> gameThreads;
	private HashMap<Integer, Boolean> askFrames;
	private HashMap<Integer, iPlayer> players;
	private HashMap<Integer, iPlayer> futurePlayers;
	private HashMap<Color, Boolean> colors;
	private int votes;
	//private int maxVotes;
	static final Color[] colorList = {Color.red, Color.green, Color.pink, Color.blue, Color.orange};
	private boolean playing;
	private int frames;
	private iServer server;
	private boolean paused;
	
	public Game(iServer server) throws RemoteException{
		players = new HashMap<Integer, iPlayer>();
		gameThreads = new HashMap<Integer, iClientGame>();
		colors = new HashMap<Color, Boolean>();
		this.server = server;
		matrix = new PositionMatrix(height, width);
		askFrames = new HashMap<Integer, Boolean>();
		futurePlayers = new HashMap<Integer, iPlayer>();
		frames = 0;
		votes = 0;
		//maxVotes = 0;
		playing = false;
		paused = false;
	}
	
	public Game(iServer server, iGame g) throws RemoteException {
		this.server = server;
		
		HashMap<Integer, iClientGame> oClientGames = g.getClientGames();
		HashMap<Integer, iPlayer> oPlayers = g.getPlayers();
		HashMap<Integer, iPlayer> oFuture = g.getFuturePlayers();
		PositionMatrix oMatrix = g.getPositionMatrix();
		HashMap<Integer, Boolean> oAsk = g.getAskFrames();
		HashMap<Color, Boolean> oColors = g.getColors();
		
		futurePlayers =new HashMap<Integer, iPlayer>();
		players = new HashMap<Integer, iPlayer>();
		askFrames = new HashMap<Integer, Boolean>();
		gameThreads = new HashMap<Integer, iClientGame>();
		
		colors = oColors;
		frames = g.getFrames();
		playing = g.isPlaying();
		votes = g.getVotes();
		paused = g.getPaused();
		//maxVotes = g.getMaxVotes();
		
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
	public synchronized void startGame() throws RemoteException  {
		for (iClientGame cg: gameThreads.values()){
			cg.setStarted(true);
			cg.setCountdown(90);
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
	public synchronized void forceCollision(int clientId) throws RemoteException {
		System.out.println("forzando");
		iPlayer player = gettingPlayer(clientId);
		try {
			matrix.deletePlayer(player.getBody(), player.getId());
			player.die();
		}
		catch(NullPointerException e){
			;
		}
		updateScores();
		if (getAlives() == 1){
			playing = false;
			votes = 0;
			//maxVotes = players.size();
			matrix = new PositionMatrix(height, width);
			for (iClientGame cGame: gameThreads.values()) cGame.resetVote();
			for (iPlayer p: players()) p.resetBody();
			futurePlayers = new HashMap<Integer, iPlayer>();
		}
	}
	
	@Override
	public synchronized void setCountdown(int time) throws RemoteException {
		for (iClientGame cGame: gameThreads.values()) cGame.setCountdown(time);
	}
	
	@Override
	public synchronized boolean checkCollision(int clientId) throws RemoteException{
		iPlayer player = gettingPlayer(clientId);
		if (player.getBody().size() == 0) return false;
		Point head = player.getHead();
		try{
			matrix.fill(head.x, head.y, player.getId(), head.visible);
			return false;
		}
		catch (CollisionException e) {
			//return false;
			
			matrix.deletePlayer(player.getBody(), player.getId());
			player.die();
			updateScores();
			if (getAlives() == 1){
				playing = false;
				votes = 0;
				//maxVotes = players.size();
				matrix = new PositionMatrix(height, width);
				for (iClientGame cGame: gameThreads.values()) cGame.resetVote();
				for (iPlayer p: players()) p.resetBody();
				futurePlayers = new HashMap<Integer, iPlayer>();
			}
			return true; 
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
	public synchronized void voteNo(int clientId) throws RemoteException {
		removeClient(clientId, false);
		reset();
	}
	
	public synchronized void setPaused(boolean p) throws RemoteException {
		this.paused = p;
	}
	
	@Override
	public synchronized boolean getPaused() throws RemoteException {
		return this.paused;
	}
	
	public synchronized void removeClient(int clientId, boolean migrate) throws RemoteException{
		colors.put(players.remove(clientId).getColor(),false);
		server.removeClient(clientId);
		gameThreads.remove(clientId).close();
		if (players.size() < 2) {
			for (iClientGame cgame: gameThreads.values()) {
				System.out.println("Se acabó el juego niños\n\n\n\n\n\n\n");
				cgame.close();
			}
			System.out.println("cerrandome");
			System.exit(1);
		}
		if (migrate) try{
			this.server.migrate();
		}
		catch(Exception e){
			System.out.println(e.toString());
		}
	}
	private synchronized void reset() throws RemoteException{
		if (votes >= players.size()){
			for (iClientGame cgame: gameThreads.values()) cgame.resetBuffer();
			players = futurePlayers;
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
	
	public synchronized iPlayer newPlayer(int clientId, boolean started)  {
		iPlayer player;
		if (players.containsKey(clientId)){
			return players.get(clientId);
		}
		else if (started && !isPlaying()) {
			System.out.println("no tengo cabeza");
			player = new Player(assignColor(), clientId+1);
		}
		else {
			player = new Player(assignColor(), assignPoint(), clientId + 1);
		}
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
	
	public synchronized boolean isPlaying()  {
		return this.playing;
	}
	public synchronized void setPlaying(boolean p)  {
		this.playing = false;
	}
	private synchronized Point assignPoint() {
		return matrix.getPlace();
	}
	
	private synchronized Color assignColor(){
		for (Color c:colorList){
			if(colors.get(c) == null || colors.get(c) == false) {
				colors.put(c, true);
				return c;
			}
		}
		//this should not happen
		return Color.white;
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
	
	public HashMap<Color, Boolean> getColors() throws RemoteException{
		return this.colors;
	}
	
	public synchronized void update(iClientGame cgame) throws RemoteException{
		gameThreads.put(cgame.getId(), cgame); 
	}
	/*
	@Override
	public int getMaxVotes() throws RemoteException {
		return this.maxVotes;
	}
	*/
}
	

