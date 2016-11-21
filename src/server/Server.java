package server;


import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.LinkedList;

import client.iClient;
import game.Game;
import game.iGame;

public class Server extends UnicastRemoteObject implements iServer{


	/**
	 * 
	 */
	private static final long serialVersionUID = -5827401619662767775L;
	iGame game;
	ArrayList<iClient> clients;
	int id;
	int waitPlayers;
	private static final int maxPlayers = 5;
	private LinkedList<iServer> serverQueue;
	private String myDir;
	
	public Server(String myDir) throws RemoteException {
		this.myDir = myDir;
	}
	
	public Server(int waitPlayers, String myDir) throws RemoteException {
		this.setIdCounter(0);
		this.serverQueue = new LinkedList<iServer>();
		this.myDir = myDir;
		this.waitPlayers = waitPlayers;
		this.game = new Game(this);
		this.clients = new ArrayList<iClient>();
	}
	
	public String getDir() throws RemoteException {
		return myDir;
	}
	
	public void addServer(iServer server) throws RemoteException {
		this.serverQueue.add(server);
		System.out.println("Server "+server.getDir()+" enqueued");
	}
	
	public void migrate() throws RemoteException, MalformedURLException, NotBoundException {
		if(!this.serverQueue.isEmpty()){
			iServer newServer = this.serverQueue.removeFirst();
			newServer.setQueue(this.serverQueue);
			newServer.setIdCounter(this.id);
			newServer.setWaitPlayers(this.waitPlayers);
			newServer.setGame(this.game);
			newServer.setClients(this.clients);
			for (iClient c: this.clients) {
				c.setServer(newServer);
			}
			System.out.println("Migrated to "+newServer.getDir());
		}
	}
	
	public void setWaitPlayers(int w) throws RemoteException {
		this.waitPlayers = w;
	}
	
	public void setGame(iGame g) throws RemoteException {
		this.game = g;
	}
	
	public void setClients(ArrayList<iClient> l) throws RemoteException {
		this.clients = l;
	}
	
	public void setIdCounter(int id) throws RemoteException {
		this.id = id;
	}
	
	public boolean canPlay() throws RemoteException{
		return clients.size() < maxPlayers;
	}
	
	public void setQueue(LinkedList<iServer> s) {
		this.serverQueue = s;
	}
	
	public void addClient(iClient client) throws RemoteException{
		this.clients.add(client);
		client.getClientGame();
		this.game.addClient(client.getID(), client.getClientGame());
		client.start();
		if(this.clients.size()>=this.waitPlayers){
			System.out.println("Comenzando juego...");
			if (!this.game.isPlaying()) this.game.startGame(this.clients);
			else client.getClientGame().setStarted(true);
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

