

import java.awt.Color;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Game extends UnicastRemoteObject implements iGame{

	public ArrayList<iClientGame> gameThreads;
	ArrayList<iPlayer> players;
	static int numberOfPlayers = 5;
	static final Color[] colorList = {Color.blue, Color.red, Color.green, Color.yellow, Color.orange};
	
	protected Game() throws RemoteException {
		super();
		players = new ArrayList<iPlayer>();
		gameThreads = new ArrayList<iClientGame>();
	}

	public void startGame(ArrayList<iClient> clients) throws RemoteException {
		for (iClientGame cGame: gameThreads){
			GameThreads gt= new GameThreads(cGame);
			gt.start();
			System.out.println("x");
			
		}
	}
	public synchronized void movePlayer(iPlayer player){
		System.out.println("asdgasdf");
		
	}

	@Override
	public void addClient(iClientGame clientGame) throws RemoteException {

		System.out.println("ayura");
		gameThreads.add(clientGame);
		
	}

	@Override
	public void doSomething() throws RemoteException {
		System.out.println(42);
		
	}

	@Override
	public ArrayList<iPlayer> players() throws RemoteException {
		return this.players;
	}

	@Override
	public synchronized iPlayer gettingPlayer(int clientId) throws RemoteException {
		iPlayer player = new Player(assignColor(clientId), assignPoint(clientId+2), clientId + 1);
		players.add(player);
		return player;
	}
	
	private Point assignPoint(int id) {
		return new Point(id*id+20,id*id+20);
	}

	private Color assignColor(int id){
		return colorList[id];
	}
}
	

