

import java.awt.Color;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server extends UnicastRemoteObject implements iServer{


	iGame game;
	ArrayList<iClient> clients;
	
	public Server() throws RemoteException{
		game = new Game(); 
		clients = new ArrayList<iClient>();
	}
	@Override
	public void addClient(iClient client) throws RemoteException{
		this.clients.add(client);
		client.getClientGame();
		this.game.addClient(client.getClientGame());
		if(this.clients.size()>=2){
			System.out.println("YEY");
			this.game.startGame(this.clients);
		}
	}

	@Override
	public void ready(iClient client) throws RemoteException{
		// TODO Auto-generated method stub
		
	}
	
	public iGame getGame() throws RemoteException{
		return this.game;
	}
}

