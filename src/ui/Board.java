package ui;


import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import client.ClientGame;
import game.Point;
import game.iPlayer;


public class Board extends Canvas{

	private Color headColor = Color.yellow;
	public ClientGame cGame;
    public int width, height;
    private boolean show;
    // doble buffer para dibujar
    public Image img;
    public Graphics buffer;


    public Board(int width, int height, ClientGame cGame) {
        this.width = width;
        this.height = height;
    	this.cGame = cGame;
    	this.show = false;
    	this.setSize(width, height);
    }

    @Override
    public void update(Graphics graphics) {
        paint(graphics);
    }

    @Override
    public void paint(Graphics graphics) {

        this.img = createImage(this.width, this.height);
        this.buffer = this.img.getGraphics();
        this.buffer.setColor(Color.black);
        this.buffer.fillRect(0, 0, this.width, this.height);

        // dibujar elementos del juego
        try {
        	for(iPlayer player: this.cGame.gamePlayers()){
            	drawSnake(player,buffer);
        	}
        	if (!this.cGame.started || this.cGame.countdown > 0) showWaitingMessage();
        	if(show) showVotation (this.cGame.gamePlayers());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        graphics.drawImage(img, 0, 0, null);
    }


	public void drawSnake(iPlayer player,Graphics graphics) throws RemoteException {
		if (graphics == null) graphics = this.img.getGraphics();
		ArrayList<Point> points = player.getBody();
		Graphics2D g2 = (Graphics2D) graphics;
		if (graphics == null) System.out.println("wat wea");
		if (g2 == null) System.out.println("error incoming");
		g2.setColor(player.getColor());
		g2.setStroke(new BasicStroke(4));
		
		
		int len = points.size();
        for (int i = 0; i<len-1;i++){
        	Point p1 = points.get(i);
        	Point p2 = points.get(i+1);
            if (p1.visible && p2.visible){
                g2.draw(new Line2D.Float(p1.x,p1.y,p2.x,p2.y));
        	}
		}
        if (len > 0){
	        Point head = player.getHead();
			graphics.setColor(headColor);
			graphics.fillOval(head.x - Point.dHip/2, head.y - Point.dHip/2, 5, 5);
        }
    }
	
	private void showWaitingMessage() {
		if (this.buffer == null) this.buffer = this.img.getGraphics();
		buffer.setColor(Color.WHITE);
		buffer.setFont(new Font("Impact", Font.PLAIN, 20));
		if (cGame.started && cGame.countdown > 0) {
			buffer.drawString("Partida inicia en "+cGame.countdown/30+" segundos", 140, 300);
		}
		else buffer.drawString("Esperando a otros jugadores...", 140, 300);
		
	}
	
	private void showVotation(ArrayList<iPlayer> players) {
		if (this.buffer == null) this.buffer = this.img.getGraphics();
		buffer.setColor(Color.WHITE);
		buffer.setFont(new Font("Impact", Font.PLAIN, 20));
		buffer.drawString("Apreta Y para seguir jugando o N para abandonar", 140, 300);
	}
	
	public void setShow(boolean bool){
		show = bool;
	}
}
