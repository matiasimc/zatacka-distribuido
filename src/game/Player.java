package game;
import java.awt.Color;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Player extends UnicastRemoteObject implements iPlayer, Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3495319638631365240L;
	public int angle;
	private int id;
	public Color color;
	private ArrayList<Point> body;
	private boolean alive;
	private int score;
	
	public Player(Color color, Point head, int id) throws RemoteException {
		this.color = color;
		body = new ArrayList<Point>();
		this.body.add(head);
		this.id = id;
		this.angle = ThreadLocalRandom.current().nextInt(0, 361);
		this.alive = true;
		this.score = 0;
		
	}
	
	@Override
	public synchronized void moveUp() throws RemoteException {
        this.angle = (this.angle + 10) % 360;
    }

	@Override
    public synchronized void moveDown() throws RemoteException {
        this.angle = (this.angle - 10) % 360;
    }
    
    @Override
    public synchronized void growUp(boolean visibility) throws RemoteException {
    	if (this.body.isEmpty()) return;
        Point head = this.body.get(this.body.size() - 1);
        int x = (int) (head.x + (Point.dHip+2)*Math.cos(Math.toRadians(this.angle)));
        int y = (int) (head.y + (Point.dHip+2)*Math.sin(Math.toRadians(this.angle)));
        this.body.add(new Point(x,y, visibility));
    }

	
	public Color getColor() throws RemoteException {
		return this.color;
	}

	
	public synchronized ArrayList<Point> getBody() throws RemoteException {
		return this.body;
	}
	
	@Override
	public synchronized Point getHead() throws RemoteException {
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

	@Override
	public void die() throws RemoteException {
		resetBody();
		this.alive = false;
	}
	
	@Override
	public int getScore() throws RemoteException {
		return this.score;
	}
	
	@Override
	public void addScore() throws RemoteException {
		score++;
	}
	
	@Override
	public void resetBody() throws RemoteException {
		this.body = new ArrayList<Point>();
	}
	
	@Override
	public void revive(Point p) throws RemoteException {
		this.alive = true;
		this.body.add(p);
	}
	
	public boolean equals(Object p) {
		try {
			return ((Player) p).getId() == this.getId();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

}
