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
	HashMap<Integer, iClient> clients;
	int id;
	int waitPlayers;
	boolean started;
	volatile boolean soyelmain;
	private static final int maxPlayers = 5;
	private static final int cap = 75;
	private LinkedList<iServer> serverQueue;
	private String myDir;
	
	
	public Server(String myDir) throws RemoteException {
		this.myDir = myDir;
		soyelmain = false;
		new Thread() {
			public void run() {
				try {
					while(true){
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
	
	public Server(String ip, File f) throws RemoteException{
		int auxSize=0;
		this.serverQueue = new LinkedList<iServer>();
		BufferedReader br = new BufferedReader(new FileReader(f));
		this.waitPlayers= Integer.parseInt(br.readLine());
		auxSize = Integer.parseInt(br.readLine());
		for(int i=0; i <auxSize; i++){
			this.serverQueue.add(new Server(br.readLine()));
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
			for(int k=0; k<j; j++){
				st= new StringTokenizer(br.readLine());
				int x= Integer.parseInt(st.nextToken());
				int y= Integer.parseInt(st.nextToken());
				body.add(new Point(x,y));				
			}
			boolean alive = Boolean.parseBoolean(br.readLine());
			int score = Integer.parseInt(br.readLine());
			
			players.put(key, new Player(angle, id, color, body, alive, score));
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
			for(int k=0; k<j; j++){
				st= new StringTokenizer(br.readLine());
				int x= Integer.parseInt(st.nextToken());
				int y= Integer.parseInt(st.nextToken());
				body.add(new Point(x,y));				
			}
			boolean alive = Boolean.parseBoolean(br.readLine());
			int score = Integer.parseInt(br.readLine());
			
			futurePlayers.put(key, new Player(angle, id, color, body, alive, score));
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
		this.game = new Game(votes, frames, paused, askFrames, players, futurePlayers, colorMap, matrix, playing);
		
		
		auxSize = Integer.parseInt(br.readLine());
		for(int i=0; i <auxSize; i++){
			String ipClient = br.readLine();
			iClient client = (iClient) Naming.lookup("rmi://"+ipClient+":1099/Client");
			this.clients.put(id, client);
		}
		this.started= Boolean.parseBoolean(br.readLine());
		
		
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
	
	public double getUsage() throws RemoteException {
		CpuPerc perc= null;
        try {
        	Thread.sleep(1000);
        	perc = sigar.getCpuPerc();
        } catch (Exception se) {
            se.printStackTrace();
        }
        return perc.getCombined()*100.0;
	}
	
	public void printMigrate(){
		System.out.println("Soy el nuevo server");
	}
	
	public synchronized void removeClient(int clientId) throws RemoteException{
		clients.remove(clientId);
		
		
		
		
		
		
		
		
		
		
		
		
		
	}

}
