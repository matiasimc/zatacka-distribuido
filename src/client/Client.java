package client;


import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import server.iServer;

public class Client extends UnicastRemoteObject implements iClient {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3466890429648781313L;
	public iServer server;
	public iClientGame cGame;
	public int id;
	public String address;
	private Thread t;
	
	public Client(iServer server, String address) throws RemoteException {
		//super();
		iClient c;
		this.address = address;
		this.server = server;
		if (server.alreadyExist(address)){
			this.id = server.addressToId(address);
			this.cGame = new ClientGame(this, this.server.getGame(), this.id);
		}
		else{
			this.id = server.getIDClient(address);
			this.cGame = new ClientGame(this, this.server.getGame(), this.id);
		}
	}
	
	public void restoreServer(iServer server) throws MalformedURLException, RemoteException, NotBoundException {
		System.out.println("Reconectando con servidor...");
		this.server = (iServer) Naming.lookup("rmi://"+server.getDir()+":1099/ABC");
		System.out.println("esta deberia ser "+server.getDir());
		System.out.println("esta es "+this.server.getDir());
		this.cGame.setGame(this.server.getGame());
		server.update(this, this.cGame);
		System.out.println("Comunicacion establecida, restaurando juego");
	}
	
	
	public void setServer(iServer server) throws MalformedURLException, RemoteException, NotBoundException {
		System.out.println("cambiando server");
		this.server = (iServer) Naming.lookup("rmi://"+server.getDir()+":1099/ABC");
		System.out.println("esta deberia ser "+server.getDir());
		System.out.println("esta es "+this.server.getDir());
		this.cGame.setGame(this.server.getGame());
	}
	
	public iClientGame getClientGame() throws RemoteException{
		return this.cGame;
	}
	
	public void start(final boolean b) {
		t = new Thread() {
			public void run() {
				try {
					cGame.setStarted(b);
					cGame.start();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();
	}
	
	public int getID() throws RemoteException {
		return id;
	}

	public iClientGame getcGame() throws RemoteException {
		return this.cGame;
	}
	
	public iServer getServer() throws RemoteException  {
		return this.server;
	}
	
	public String getAddress() throws RemoteException  {
		return this.address;
	}

	@Override
	public String getSnapshot() throws RemoteException {
		return this.address;
	}

}
