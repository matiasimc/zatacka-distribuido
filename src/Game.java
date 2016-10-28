

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Game extends UnicastRemoteObject implements iGame{

	public ArrayList<iClientGame> gameThreads;
	
	protected Game() throws RemoteException {
		super();
		gameThreads = new ArrayList<iClientGame>();
	}

	public void startGame(ArrayList<iClient> clients) throws RemoteException {
		for (iClientGame cGame: gameThreads){
			((ClientGame)cGame).run();
			System.out.println("x");
		}
	}
	public synchronized void moveClient(iClient client){
		System.out.println("asdgasdf");
		
	}

	@Override
	public void addThread(iClientGame clientGame) throws RemoteException {

		System.out.println("ayura");
		gameThreads.add(clientGame);
		
	}
}
	

