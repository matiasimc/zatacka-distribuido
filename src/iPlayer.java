import java.awt.Color;
import java.awt.Graphics;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface iPlayer extends Remote {

	Color getColor() throws RemoteException;

	ArrayList<Point> getBody() throws RemoteException;

}
