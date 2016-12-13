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
	
	public Client(iServer server) throws RemoteException {
		//super();
		this.server = server;
		this.id = server.getIDClient();
		this.cGame = new ClientGame(this, this.server.getGame(), id);

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
		new Thread() {
			public void run() {
				try {
					cGame.setStarted(b);
					cGame.start();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
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

	@Override
	public String getSnapshot() throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
