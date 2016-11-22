package server;


import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;

import javax.print.attribute.standard.Severity;

import client.iClient;
import game.Game;
import game.iGame;

import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;


public class Server extends UnicastRemoteObject implements iServer{


	/**
	 * 
	 */
	private static final long serialVersionUID = -5827401619662767775L;
	private Sigar sigar = new Sigar();
	iGame game;
	HashMap<Integer, iClient> clients;
	int id;
	int waitPlayers;
	boolean started;
	boolean soyelmain;
	HashMap<Integer, String> carga;
	private static final int maxPlayers = 5;
	private static final int cap = 65;
	private LinkedList<iServer> serverQueue;
	private String myDir;
	
	
	public Server(String myDir) throws RemoteException {
		this.myDir = myDir;
		soyelmain = false;
		carga = new HashMap<Integer, String>();
		new Thread() {
			public void run() {
				try {
					while(true){
						if (soyelmain) {
							System.out.println(getUsage());
							carga.put(carga.size()+1, "perrin");
						}
						if (soyelmain && getUsage()>=cap) migrate();
						Thread.sleep(2000);
					}
				} catch (Exception e) {
					System.out.println("falle en thread de server");
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public Server(int waitPlayers, String myDir) throws RemoteException {
		this.setIdCounter(0);
		this.started = false;
		this.serverQueue = new LinkedList<iServer>();
		this.serverQueue.add(this);
		this.myDir = myDir;
		this.waitPlayers = waitPlayers;
		this.game = new Game(this);
		this.clients = new HashMap<Integer, iClient>();
		this.soyelmain = true;
		carga = new HashMap<Integer, String>();
		new Thread() {
			public void run() {
				try {
					while(true){
						if (soyelmain) {
							System.out.println(getUsage());
							carga.put(carga.size()+1, "perrin");
						}
						if (soyelmain && getUsage()>=cap) migrate();
						Thread.sleep(2000);
					}
				} catch (Exception e) {
					System.out.println("falle en thread de server");
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public String getDir() throws RemoteException {
		return myDir;
	}
	
	public void addServer(iServer server) throws RemoteException {
		this.serverQueue.add(server);
		System.out.println("Server "+server.getDir()+" enqueued");
	}
	
	public void migrate() throws RemoteException, MalformedURLException, NotBoundException {
		if(this.serverQueue.size()>1){
			iServer newServer = this.getNew();
			newServer.setMain(true);
			this.soyelmain = false;
			this.carga = new HashMap<Integer, String>();
			newServer.setStarted(this.started);
			newServer.setQueue(this.serverQueue);
			newServer.setIdCounter(this.id);
			newServer.setWaitPlayers(this.waitPlayers);
			newServer.setGame(this.game);
			newServer.setClients(this.clients);
			for (iClient c: this.clients.values()) {
				c.setServer(newServer);
			}
			System.out.println("Migrated to "+newServer.getDir());
			newServer.printMigrate();
		}
	}
	
	private iServer getNew() throws RemoteException {
		for (iServer server: serverQueue){
			if (!server.getMain()) return server;
		}
		return this;
	}

	public void setWaitPlayers(int w) throws RemoteException {
		this.waitPlayers = w;
	}
	
	public void setGame(iGame g) throws RemoteException {
		this.game = new Game(this, g);
	}
	
	public void setClients(HashMap<Integer, iClient> l) throws RemoteException {
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
	
	public void setStarted(boolean b){
		this.started = b;
	}
	
	public synchronized void setMain(boolean b){
		this.soyelmain = b;
	}
	
	public synchronized boolean getMain(){
		return this.soyelmain;
	}
	
	public void addClient(iClient client) throws RemoteException{
		this.clients.put(client.getID(),client);
		client.getClientGame();
		this.game.addClient(client.getID(), client.getClientGame());
		if(!started && this.clients.size()>=this.waitPlayers) {
			client.start(false);
			System.out.println("Comenzando juego...");
			this.game.startGame();
			started = true;
		}
		else if(started){
			client.start(true);
			client.getClientGame().setCountdown(90);
			client.getClientGame().setStarted(true);
		}
		else {
			client.start(false);
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
	
	public double getUsage(){
		Mem mem = null;
        try {
            mem = sigar.getMem();
        } catch (SigarException se) {
            se.printStackTrace();
        }
        return mem.getUsedPercent();
	}
	
	public void printMigrate(){
		System.out.println("Soy el nuevo server");
	}
	
	public synchronized void removeClient(int clientId) throws RemoteException{
		clients.remove(clientId);
	}
}
