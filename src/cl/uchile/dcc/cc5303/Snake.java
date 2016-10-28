package cl.uchile.dcc.cc5303;

import java.awt.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Snake extends UnicastRemoteObject implements ISnake {
	public int angle;
	public Color color;
	private int id;
	private ArrayList<Point> body;
	
	public Snake(Color color, Point head, int id) throws RemoteException {
		this.id = id;
		this.color = color;
		body = new ArrayList<Point>();
		this.body.add(head);
	}
	
	public void draw(Graphics graphics) throws RemoteException {
        for (Point p : this.body)
            if (p.visible)
                graphics.fillOval(p.x - Point.dHip/2, p.y - Point.dHip/2, Point.dHip, Point.dHip);
    }
	
	public void moveUp() {
        this.angle = (this.angle + 10) % 360;
    }

    public void moveDown() {
        this.angle = (this.angle - 10) % 360;
    }
    
    public void growUp(boolean visibility) throws RemoteException {
        Point head = this.body.get(this.body.size() - 1);
        int x = (int) (head.x + Point.dHip*Math.cos(Math.toRadians(this.angle)));
        int y = (int) (head.y + Point.dHip*Math.sin(Math.toRadians(this.angle)));
        this.body.add(new Point(x,y, visibility));
    }

	public int getId() {
		return id;
	}
}
