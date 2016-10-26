package cl.uchile.dcc.cc5303;
import java.rmi.*;


public interface iSnakeList extends Remote {
	public void addSnake(iSnake snake) throws RemoteException;
	public void removeSnake(int id) throws RemoteException;
}
