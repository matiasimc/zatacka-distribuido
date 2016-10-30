import java.awt.Color;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Player extends UnicastRemoteObject implements iPlayer{
	public int angle;
	private int id;
	public Color color;
	private ArrayList<Point> body;
	private boolean alive;
	
	public Player(Color color, Point head, int id) throws RemoteException {
		this.color = color;
		body = new ArrayList<Point>();
		this.body.add(head);
		this.id = id;
		this.angle = ThreadLocalRandom.current().nextInt(0, 361);
		this.alive = true;
	}
	
	@Override
		public void moveUp() throws RemoteException {
        this.angle = (this.angle + 10) % 360;
    }

	@Override
    public void moveDown() throws RemoteException {
        this.angle = (this.angle - 10) % 360;
    }
    
    @Override
    public void growUp(boolean visibility) throws RemoteException {
        Point head = this.body.get(this.body.size() - 1);
        int x = (int) (head.x + (Point.dHip+2)*Math.cos(Math.toRadians(this.angle)));
        int y = (int) (head.y + (Point.dHip+2)*Math.sin(Math.toRadians(this.angle)));
        this.body.add(new Point(x,y, visibility));
    }

	
	public Color getColor() throws RemoteException {
		return this.color;
	}

	
	public ArrayList<Point> getBody() throws RemoteException {
		return this.body;
	}
	
	@Override
	public Point getHead() throws RemoteException {
		return this.body.get(this.body.size() - 1);
	}
	
	@Override
	public int getId() throws RemoteException {
		return this.id;
	}
	
	@Override
	public boolean isAlive() throws RemoteException {
		return this.alive;
	}

	
	public void die() throws RemoteException {
		this.body = new ArrayList<Point>();
		this.alive = false;
	}

}
