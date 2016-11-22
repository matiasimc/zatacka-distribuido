package game;
import java.awt.Color;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


public class Player implements iPlayer, Serializable{
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
	
	public Player(Color color, Point head, int id) {
		this.color = color;
		this.body = new ArrayList<Point>();
		this.body.add(head);
		this.id = id;
		this.angle = ThreadLocalRandom.current().nextInt(0, 361);
		this.alive = true;
		this.score = 0;
		
	}
	
	public Player(Color color, int id) {
		this.color = color;
		this.body = new ArrayList<Point>();
		this.id = id;
		this.angle = ThreadLocalRandom.current().nextInt(0, 361);
		this.alive = true;
		this.score = 0;
		
	}
	
	public Player(iPlayer p){
		this.color = p.getColor();
		this.body = p.getBody();
		this.id = p.getId();
		this.angle = p.getAngle();
		this.alive = p.isAlive();
		this.score = this.getScore();
	}
	@Override
	public synchronized void moveUp() {
        this.angle = (this.angle + 10) % 360;
    }

	@Override
    public synchronized void moveDown() {
        this.angle = (this.angle - 10) % 360;
    }
    
    @Override
    public synchronized void growUp(boolean visibility)  {
    	if (this.body.isEmpty()) return;
        Point head = this.body.get(this.body.size() - 1);
        int x = (int) (head.x + (Point.dHip+2)*Math.cos(Math.toRadians(this.angle)));
        int y = (int) (head.y + (Point.dHip+2)*Math.sin(Math.toRadians(this.angle)));
        this.body.add(new Point(x,y, visibility));
    }

	
	public Color getColor() {
		return this.color;
	}

	
	public synchronized ArrayList<Point> getBody() {
		return this.body;
	}
	
	@Override
	public synchronized Point getHead() {
		return this.body.get(this.body.size() - 1);
	}
	
	@Override
	public int getId()  {
		return this.id;
	}
	
	@Override
	public boolean isAlive() {
		return this.alive;
	}

	@Override
	public void die() {
		resetBody();
		this.alive = false;
	}
	
	@Override
	public int getScore()  {
		return this.score;
	}
	
	@Override
	public void addScore()  {
		score++;
	}
	
	@Override
	public void resetBody()  {
		this.body = new ArrayList<Point>();
	}
	
	@Override
	public void revive(Point p) {
		this.alive = true;
		this.body.add(p);
	}
	
	public boolean equals(Object p) {
		return ((Player) p).getId() == this.getId();
	}
	
	@Override
	public int getAngle() {
		return this.angle;
	}
}
