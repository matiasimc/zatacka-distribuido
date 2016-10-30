

import java.awt.Color;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Game extends UnicastRemoteObject implements iGame{

	private int height = 800;
	private int width = 800;
	PositionMatrix matrix; 
	public ArrayList<iClientGame> gameThreads;
	ArrayList<iPlayer> players;
	static int numberOfPlayers = 5;
	static final Color[] colorList = {Color.blue, Color.red, Color.green, Color.yellow, Color.orange};
	
	protected Game() throws RemoteException {
		super();
		players = new ArrayList<iPlayer>();
		gameThreads = new ArrayList<iClientGame>();
		matrix = new PositionMatrix(height, width);
	}

	@Override
	public synchronized void startGame(ArrayList<iClient> clients) throws RemoteException {
		for (iClientGame cGame: gameThreads){
			GameThreads gt= new GameThreads(cGame);
			gt.start();
			System.out.println("x");
			
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
			return true;
		}
	}
	
	@Override
	public synchronized void addClient(iClientGame clientGame) throws RemoteException {

		System.out.println("ayura");
		gameThreads.add(clientGame);
		
	}

	@Override
	public synchronized ArrayList<iPlayer> players() throws RemoteException {
		return this.players;
	}

	@Override
	public synchronized iPlayer gettingPlayer(int clientId) throws RemoteException {
		iPlayer player = new Player(assignColor(clientId), assignPoint(), clientId + 1);
		players.add(player);
		return player;
	}
	
	private synchronized Point assignPoint() {
		return matrix.getPlace();
	}

	private synchronized Color assignColor(int id){
		return colorList[id];
	}
}
	

