package cl.uchile.dcc.cc5303;
import java.rmi.*;


public interface ISnakeOrganizer extends Remote {
	public void addSnake(ISnake snake) throws RemoteException;
	public void removeSnake(int id) throws RemoteException;
	public ISnake registerPlayer(Player p) throws NoAvailableSnakesException, RemoteException;
	public ISnake getNextAvailableSnake() throws NoAvailableSnakesException, RemoteException;
}
