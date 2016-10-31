import java.awt.Color;
import java.awt.Graphics;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface iPlayer extends Remote {

	Color getColor() throws RemoteException;
	void moveUp() throws RemoteException;
	void moveDown() throws RemoteException;
	void growUp(boolean visibility) throws RemoteException;
	ArrayList<Point> getBody() throws RemoteException;
	Point getHead() throws RemoteException;
	int getId() throws RemoteException;
	boolean isAlive() throws RemoteException;
	void die() throws RemoteException;
	void addScore() throws RemoteException;
	int getScore() throws RemoteException;
	void resetBody() throws RemoteException;
	void revive(Point p) throws RemoteException;

}
