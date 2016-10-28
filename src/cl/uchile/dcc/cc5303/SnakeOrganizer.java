package cl.uchile.dcc.cc5303;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class SnakeOrganizer extends UnicastRemoteObject implements ISnakeOrganizer {
	
	private ArrayList<ISnake> availableSnakes;
	private HashMap<Player, ISnake> playerSnakeMap; 
	
	protected SnakeOrganizer() throws RemoteException {
		super();
		availableSnakes = new ArrayList<ISnake>();
		playerSnakeMap = new HashMap<Player, ISnake>();
	}
	
	public ISnake registerPlayer(Player p) throws NoAvailableSnakesException {
		ISnake snake = getNextAvailableSnake();
		playerSnakeMap.put(p, snake);
		return snake;
	}
	
	public void addSnake(ISnake snake) throws RemoteException {
		availableSnakes.add(snake);

	}
	
	public ISnake getNextAvailableSnake() throws NoAvailableSnakesException {
		if (availableSnakes.isEmpty()) {
			throw new NoAvailableSnakesException();
		}
		ISnake snake = availableSnakes.get(0);
		availableSnakes.remove(0);
		return snake;
	}

	public void removeSnake(int id) throws RemoteException {
		for (ISnake s: availableSnakes) {
			if (s.getId() == id) {
				availableSnakes.remove(s);
				break;
			}
		}
	}

}
