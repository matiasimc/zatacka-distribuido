package client;


import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import server.iServer;

public class Client extends UnicastRemoteObject implements iClient {
	public iServer server;
	public iClientGame cGame;
	public int id;
	
	public Client(iServer server) throws RemoteException {
		//super();
		this.server = server;
		this.id = server.getIDClient();
		cGame = new ClientGame(this.server.getGame(), id);

	}
	
	public iClientGame getClientGame() throws RemoteException{
		return this.cGame;
	}
	
	public void start() {
		new Thread() {
			public void run() {
				try {
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

	public iClientGame getcGame() {
		return this.cGame;
	}

}