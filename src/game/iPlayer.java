package game;
import java.awt.Color;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;


public interface iPlayer {

	Color getColor();
	void moveUp();
	void moveDown();
	void growUp(boolean visibility);
	ArrayList<Point> getBody();
	Point getHead();
	int getId();
	boolean isAlive();
	void die();
	void addScore();
	int getScore();
	void resetBody();
	void revive(Point p);
	int getAngle();

}
