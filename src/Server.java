

import java.awt.Color;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server extends UnicastRemoteObject implements iServer{


	iGame game;
	ArrayList<iClient> clients;
	int id;
	int waitPlayers;
	private static final int maxPlayers = 5;
	
	public Server(int waitPlayers) throws RemoteException{
		game = new Game(); 
		clients = new ArrayList<iClient>();
		id=0;
		this.waitPlayers = waitPlayers;
	}
	
	public boolean canPlay() throws RemoteException{
		return clients.size() < maxPlayers;
	}
	
	public void addClient(iClient client) throws RemoteException{
		this.clients.add(client);
		client.getClientGame();
		this.game.addClient(client.getClientGame());
		if(this.clients.size()>=this.waitPlayers){
			System.out.println("Comenzando juego...");
			this.game.startGame(this.clients);
		}
		else {
			System.out.println("Esperando jugadores...");
		}
	}

	
	public void ready(iClient client) throws RemoteException{
		// TODO Auto-generated method stub
		
	}
	
	public iGame getGame() throws RemoteException{
		return this.game;
	}
	
	public void gettingInformation(iClient client) throws RemoteException {
		System.out.print("Client "+ client.getID() +"hizo algo");
		
	}
	
	
	public synchronized int getIDClient() throws RemoteException {
		int idGived= id;
		id++;
		return idGived;
	}
}

