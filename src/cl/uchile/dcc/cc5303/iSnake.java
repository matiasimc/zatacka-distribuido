package cl.uchile.dcc.cc5303;
import java.awt.*;
import java.rmi.*;

public interface iSnake extends Remote {
	public void draw(Graphics graphics) throws RemoteException;
	public void moveUp() throws RemoteException;
	public void moveDown() throws RemoteException;
	public void growUp(boolean visibility) throws RemoteException;
	public int getId();
}
