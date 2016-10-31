

import java.awt.Color;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class Game extends UnicastRemoteObject implements iGame{

	private int height = 800;
	private int width = 800;
	PositionMatrix matrix; 
	public ArrayList<iClientGame> gameThreads;
	ArrayList<iPlayer> players;
	static int numberOfPlayers = 5;
	static final Color[] colorList = {Color.red, Color.green, Color.pink, Color.blue, Color.orange};
	private boolean playing;
	
	protected Game() throws RemoteException {
		super();
		players = new ArrayList<iPlayer>();
		gameThreads = new ArrayList<iClientGame>();
		matrix = new PositionMatrix(height, width);
		playing = true;
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
		for (iClientGame cGame: gameThreads){
			if (!cGame.isRunning()) {
				GameThreads gt= new GameThreads(cGame);
				gt.start();
				cGame.setRunning(true);
				System.out.println("x");
			}
			
		}
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
				playing = false;
				for (iPlayer p: players()) p.resetBody();
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

	private synchronized Color assignColor(int id){
		return colorList[id];
	}
}
	

