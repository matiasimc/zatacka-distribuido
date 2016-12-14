package server;


import java.awt.Color;
import java.io.*;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import client.iClient;
import client.iClientGame;
import game.Game;
import game.Player;
import game.Point;
import game.PositionMatrix;
import game.iGame;
import game.iPlayer;


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
					long time = System.nanoTime();
					while(true){
						updateUsage();
						if (soyelmain) {
							if (getUsage()>=cap) migrate();
							System.out.println(getUsage()); 
							if ((System.nanoTime()-time)/1000000> 10000){
								Server.this.createSnapshot();
								time = System.nanoTime();
							}
						}
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
					long time = System.nanoTime();
					while(true){
						updateUsage();
						if (soyelmain) {
							if (getUsage()>=cap) migrate();
							System.out.println(getUsage()); 
							if ((System.nanoTime()-time)/1000000> 10000){
								Server.this.createSnapshot();
								time = System.nanoTime();
							}
						}
					}
				} catch (Exception e) {
					System.out.println("falle en thread de server");
					e.printStackTrace();
				}
			}
		}.start();
	}
	
	public Server(String ip, File f) throws Exception{
		Naming.bind("rmi://"+ip+":1099/ABC", this);
		int auxSize=0;
		this.myDir = ip;
		soyelmain = true;
		this.addressToId = new HashMap<String,Integer>();
		this.serverQueue = new LinkedList<iServer>();
		this.serverQueue.add(this);
		BufferedReader br = new BufferedReader(new FileReader(f));
		auxSize = Integer.parseInt(br.readLine());
		for(int i=0; i <auxSize; i++){
			String key = br.readLine();
			Integer value = Integer.parseInt(br.readLine());
			this.addressToId.put(key, value);			
		}
		this.waitPlayers= Integer.parseInt(br.readLine());
		auxSize = Integer.parseInt(br.readLine());
		for(int i=0; i <auxSize; i++){
			String line = br.readLine();
			if (!line.equals(ip)){
				iServer serv = (iServer) Naming.lookup("rmi://"+line+":1099/ABC");
				this.serverQueue.add(serv);
			}
		}
		this.id= Integer.parseInt(br.readLine());
		//game
		int votes = Integer.parseInt(br.readLine());
		int frames = Integer.parseInt(br.readLine());
		boolean paused = Boolean.parseBoolean(br.readLine());
		auxSize = Integer.parseInt(br.readLine());
		HashMap<Integer,Boolean> askFrames = new HashMap<Integer,Boolean>();
		for(int i=0; i <auxSize; i++){
			int key = Integer.parseInt(br.readLine());
			boolean value = Boolean.parseBoolean(br.readLine());
			askFrames.put(key, value);			
		}
		auxSize = Integer.parseInt(br.readLine());
		HashMap<Integer,iPlayer> players = new HashMap<Integer,iPlayer>();
		for(int i=0; i <auxSize; i++){
			int key = Integer.parseInt(br.readLine());
			int angle = Integer.parseInt(br.readLine());
			int idPlayer = Integer.parseInt(br.readLine());
			StringTokenizer st= new StringTokenizer(br.readLine());
			int r= Integer.parseInt(st.nextToken());
			int g= Integer.parseInt(st.nextToken());
			int b= Integer.parseInt(st.nextToken());
			Color color = new Color(r,g,b);
			int j= Integer.parseInt(br.readLine());
			ArrayList<Point> body = new ArrayList<Point>();
			for(int k=0; k<j; k++){
				st= new StringTokenizer(br.readLine());
				int x= Integer.parseInt(st.nextToken());
				int y= Integer.parseInt(st.nextToken());
				body.add(new Point(x,y));				
			}
			boolean alive = Boolean.parseBoolean(br.readLine());
			int score = Integer.parseInt(br.readLine());
			
			players.put(key, new Player(angle, idPlayer, color, body, alive, score));
		}
		auxSize = Integer.parseInt(br.readLine());
		HashMap<Integer,iPlayer> futurePlayers = new HashMap<Integer,iPlayer>();
		for(int i=0; i <auxSize; i++){
			int key = Integer.parseInt(br.readLine());
			int angle = Integer.parseInt(br.readLine());
			int idPlayer = Integer.parseInt(br.readLine());
			StringTokenizer st= new StringTokenizer(br.readLine());
			int r= Integer.parseInt(st.nextToken());
			int g= Integer.parseInt(st.nextToken());
			int b= Integer.parseInt(st.nextToken());
			Color color = new Color(r,g,b);
			int j= Integer.parseInt(br.readLine());
			ArrayList<Point> body = new ArrayList<Point>();
			for(int k=0; k<j; k++){
				st= new StringTokenizer(br.readLine());
				int x= Integer.parseInt(st.nextToken());
				int y= Integer.parseInt(st.nextToken());
				body.add(new Point(x,y));				
			}
			boolean alive = Boolean.parseBoolean(br.readLine());
			int score = Integer.parseInt(br.readLine());
			
			futurePlayers.put(key, new Player(angle, idPlayer, color, body, alive, score));
		}
		auxSize = Integer.parseInt(br.readLine());
		HashMap<Color, Boolean> colorMap = new HashMap<Color, Boolean>();
		for(int i=0; i <auxSize; i++){
			StringTokenizer st= new StringTokenizer(br.readLine());
			int r= Integer.parseInt(st.nextToken());
			int g= Integer.parseInt(st.nextToken());
			int b= Integer.parseInt(st.nextToken());
			Color color = new Color(r,g,b);
			boolean occupied = Boolean.parseBoolean(br.readLine());
			colorMap.put(color, occupied);
		}
		StringTokenizer st= new StringTokenizer(br.readLine());
		int width = Integer.parseInt(st.nextToken());
		int height = Integer.parseInt(st.nextToken());
		PositionMatrix matrix = new PositionMatrix(width,height); 
		for(int i=0; i<width; i++){
			for(int j=0; j<height; j++){
				matrix.setValue(i,j,Integer.parseInt(br.readLine()));
			}
		}
		boolean playing = Boolean.parseBoolean(br.readLine());
		this.game = new Game(votes, frames, paused, askFrames, players, futurePlayers, colorMap, matrix, playing, this);
		
		
		auxSize = Integer.parseInt(br.readLine());
		this.clients = new HashMap<Integer, iClient>();
		for(int i=0; i <auxSize; i++){
			int idClient = Integer.parseInt(br.readLine());
			String ipClient = br.readLine();
			System.out.println(ipClient);
			
			//iClient client = (iClient) Naming.lookup("rmi://"+ipClient+":1099/Client");
			//this.clients.put(idClient, client);
			//client.restoreServer(this);
		}
		this.started= Boolean.parseBoolean(br.readLine());
		new Thread() {
			public void run() {
				try {
					long time = System.nanoTime();
					while(true){
						updateUsage();
						if (soyelmain) {
							if (getUsage()>=cap) migrate();
							System.out.println(getUsage()); 
							if ((System.nanoTime()-time)/1000000> 10000){
								Server.this.createSnapshot();
								time = System.nanoTime();
							}
						}
					}
				}  catch (Exception e) {
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
	
	public void createSnapshot() throws RemoteException{
		System.out.println("Empezaré el snapshot");
		try{
		    PrintWriter writer = new PrintWriter("snapshot.txt", "UTF-8");
		    writer.println(this.addressToId.size());
		    for (Entry<String, Integer> entry : addressToId.entrySet()) {
		        String key = entry.getKey();
		        Integer value = entry.getValue();
		        writer.println(key.toString());
		        writer.println(value.toString());
		    }
		    writer.println(this.waitPlayers);
		    writer.println(this.serverQueue.size());
		    for(iServer s: serverQueue){
			    writer.println(s.getDir());
		    }
		    writer.println(this.id);
		    writer.println(this.game.getSnapshot());
		    writer.println(this.clients.size());
		    for (Entry<Integer, iClient> entry : clients.entrySet()) {
		        Integer key = entry.getKey();
		        iClient value = entry.getValue();
		        writer.println(key.toString());
		        writer.println(value.getSnapshot());
		    }
		    writer.println(this.started);
		    writer.close();
		} catch (IOException e) {
		   	System.out.println("Falló Snapshot");
		}
		System.out.println("Terminé el snapshot");
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
				c.setServerIp(newServer.getDir());
			}
			System.out.println("Migrated to "+newServer.getDir());
			newServer.printMigrate();
			newServer.createSnapshot();
		}
	}
	
	private iServer getNew() throws RemoteException {
        System.out.println("Dame un server");
		double minLoad = 101;
		iServer newServer = this;
		for (iServer s: serverQueue) {
			if (s.getUsage() < minLoad) {
				minLoad = s.getUsage();
				newServer = s;
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
		clients.remove(clientId);
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
	
	public synchronized void addClient2(int id, iClient client) throws RemoteException{
		clients.put(id, client);
	}
}
