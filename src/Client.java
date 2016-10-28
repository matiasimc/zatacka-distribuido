

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Client extends UnicastRemoteObject implements iClient {
	public iServer server;
	public iClientGame cGame;
	public int id;
	
	public Client(iServer server) throws RemoteException {
		//super();
		this.server = server;
		this.id = server.getIDClient();
		cGame = new ClientGame(this.server.getGame());

	}
	
	public iClientGame getClientGame() throws RemoteException{
		return this.cGame;
	}

	@Override
	public int getID() throws RemoteException {
		return id;
	}

}