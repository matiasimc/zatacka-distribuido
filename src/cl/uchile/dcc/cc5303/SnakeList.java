package cl.uchile.dcc.cc5303;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class SnakeList extends UnicastRemoteObject implements iSnakeList {
	
	protected SnakeList() throws RemoteException {
		super();
		// TODO Auto-generated constructor stub
	}

	ArrayList<iSnake> snakeList = new ArrayList<iSnake>();
	public void addSnake(iSnake snake) throws RemoteException {
		snakeList.add(snake);

	}

	public void removeSnake(int id) throws RemoteException {
		for (iSnake s: snakeList) {
			if (s.getId() == id) {
				snakeList.remove(s);
				break;
			}
		}
	}

}
