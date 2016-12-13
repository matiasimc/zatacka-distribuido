package server;


import java.net.MalformedURLException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.LinkedList;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;

import client.iClient;
import client.iClientGame;
import game.Game;
import game.iGame;


public class Server extends UnicastRemoteObject implements iServer{


	/**
	 * 
	 */
	private static final long serialVersionUID = -5827401619662767775L;
	private Sigar sigar = new Sigar();
	iGame game;
	HashMap<String, Integer> addressToId;
	HashMap<Integer, iClient> clients;
	int id;
	int waitPlayers;
	boolean started;
	volatile boolean soyelmain;
	private static final int maxPlayers = 5;
	private static final int cap = 75;
	private LinkedList<iServer> serverQueue;
	private String myDir;
	private CpuPerc perc;
	
	public Server(String myDir) throws RemoteException {
		this.addressToId = new HashMap<String, Integer>();
		this.myDir = myDir;
		perc = null;
		soyelmain = false;
		new Thread() {
			public void run() {
				try {
					while(true){
						updateUsage();
						if (soyelmain) System.out.println(getUsage());
						if (soyelmain && getUsage()>=cap) migrate();	
					}
				} catch (Exception e) {
					System.out.println("falle en thread de server");
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public Server(int waitPlayers, String myDir) throws RemoteException {
		this.addressToId = new HashMap<String, Integer>();
		perc = null;
		this.setIdCounter(0);
		this.started = false;
		this.serverQueue = new LinkedList<iServer>();
		this.serverQueue.add(this);
		this.myDir = myDir;
		this.waitPlayers = waitPlayers;
		this.game = new Game(this);
		this.clients = new HashMap<Integer, iClient>();
		this.soyelmain = true;
		new Thread() {
			public void run() {
				try {
					while(true){
						updateUsage();
						if (soyelmain) System.out.println(getUsage());
						if (soyelmain && getUsage()>=cap) migrate();
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
		System.out.println("Llamaron a migrar");
		if(this.serverQueue.size()>1){
			System.out.println("Migrando");
			iServer newServer = this.getNew();
			if (newServer.getDir().equals(this.getDir())) {
				System.out.println("Fuiste elegido nuevamente");
				return;
			}
			this.soyelmain = false;
			newServer.setMain(true);
			newServer.setStarted(this.started);
			newServer.setQueue(this.serverQueue);
			newServer.setIdCounter(this.id);
			newServer.setWaitPlayers(this.waitPlayers);
			newServer.setGame(this.game);
			newServer.setAdressToId(this.addressToId);
			newServer.setClients(this.clients);
			for (iClient c: this.clients.values()) {
				c.setServer(newServer);
			}
			System.out.println("Migrated to "+newServer.getDir());
			newServer.printMigrate();
		}
	}
	
	private iServer getNew() throws RemoteException {
        System.out.println("Dame un server");
		double minLoad = 101;
		iServer newServer = this;
		LinkedList<iServer> queueCopy = new LinkedList<iServer>(this.serverQueue);
		for (iServer s: queueCopy) {
			try{
				double usage = s.getUsage();
				if (usage < minLoad) {
					minLoad = usage;
					newServer = s;
				}
			}
			catch(ConnectException ex){
				this.serverQueue.remove(s);
			}
		}
		return newServer;
	}

	public void setWaitPlayers(int w) throws RemoteException {
		this.waitPlayers = w;
	}
	
	public void setAdressToId(HashMap<String, Integer> map) throws RemoteException {
		this.addressToId = map;
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
	
	
	public synchronized int getIDClient(String address) throws RemoteException {
		int idGived= id;
		this.addressToId.put(address, id);
		id++;
		return idGived;
	}
	
	public void updateUsage() throws RemoteException {
		try {
        	Thread.sleep(1000);
        	perc = sigar.getCpuPerc();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	public double getUsage() throws RemoteException {
        try{
        	if (perc == null){
            	perc = sigar.getCpuPerc();
    		}
        	return perc.getCombined()*100.0;
        }
        catch (Exception e){
        	return 0;
        }
	}
	
	public void printMigrate(){
		System.out.println("Soy el nuevo server");
	}
	
	public synchronized void removeClient(int clientId) throws RemoteException{
		iClient client = clients.remove(clientId);
		addressToId.remove(client.getAddress());
	}
	
	public synchronized boolean alreadyExist(String address) throws RemoteException{
		return this.addressToId.containsKey(address);
	}
	
	public synchronized int addressToId(String address) throws RemoteException{
		return this.addressToId.get(address);
	}
	
	public synchronized void update(iClient client, iClientGame cgame) throws RemoteException{
		clients.put(client.getID(), client);
		game.update(cgame);
	}
}
