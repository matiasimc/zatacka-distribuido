package ui;


import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.geom.Line2D;
import java.io.File;
import java.rmi.ConnectException;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.util.ArrayList;

import client.ClientGame;
import game.Point;
import game.iPlayer;


public class Board extends Canvas{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1642281734724797682L;
	private Color headColor = Color.yellow;
	public ClientGame cGame;
    public int width, height;
    private boolean show;
    // doble buffer para dibujar
    public Image img;
    public Graphics buffer;
    private Font cf;


    public Board(int width, int height, ClientGame cGame) {
    	try {
			cf = Font.createFont(Font.TRUETYPE_FONT, new File("IndieFlower.ttf"));
			cf = cf.deriveFont(20.0f);
		     GraphicsEnvironment ge = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
		     ge.registerFont(cf);
		} catch (Exception e) {
		     //Handle exception
		}
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
    public void paint(Graphics graphics){

        this.img = createImage(this.width, this.height);
        this.buffer = this.img.getGraphics();
        this.buffer.setColor(Color.black);
        this.buffer.fillRect(0, 0, this.width, this.height);

        // dibujar elementos del juego
        try {
        	for(int clientId: this.cGame.clientIds()){
            	drawSnake(clientId);
        	}
        	if (!this.cGame.started || (this.cGame.started && this.cGame.game.isPlaying() && this.cGame.countdown>0 && !this.cGame.game.getPaused())) showWaitingMessage();
        	if (this.cGame.game.getPaused()) showPausedMessage();
        	if(show) showVotation();
		}catch (ConnectException c){
			
        }
        catch (RemoteException e) {
			e.printStackTrace();
		}
       
        graphics.drawImage(img, 0, 0, null);
    }
    

	public void drawSnake(int clientId) {
		try{
			if (this.buffer == null) this.buffer = this.img.getGraphics();
			ArrayList<Point> points;
			points = this.cGame.getBody(clientId);
			Graphics2D g2 = (Graphics2D) this.buffer;
			g2.setColor(this.cGame.getColor(clientId));
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
		        Point head = this.cGame.getHead(clientId);
				this.buffer.setColor(headColor);
				this.buffer.fillOval(head.x - Point.dHip/2, head.y - Point.dHip/2, Point.dHip+1, Point.dHip+1);
	        }
		}
		catch (Exception e){
			return;
		}
    }
	
	private void showPausedMessage() {
		try{
			if (this.buffer == null) this.buffer = this.img.getGraphics();
			buffer.setColor(Color.WHITE);
			buffer.setFont(cf);
			buffer.drawString("Paused, press SPACE to continue", 10, 550);
		}
		catch(Exception e){
			return;
		}
	}
	
	private void showWaitingMessage() {
		try{
			if (this.buffer == null) this.buffer = this.img.getGraphics();
			buffer.setColor(Color.WHITE);
			buffer.setFont(cf);
			if (cGame.game.isPlaying() && cGame.countdown > 0 && !cGame.game.getPaused()) {
				buffer.drawString("Desbloqueo en "+cGame.countdown/30+" segundos", 140, 300);
			}
			else buffer.drawString("Esperando a otros jugadores...", 140, 300);
		}
		catch(Exception e){
			return;
		}
		
	}
	
	private void showVotation() {
		if (this.buffer == null) this.buffer = this.img.getGraphics();
		buffer.setColor(Color.WHITE);
		buffer.setFont(cf);
		buffer.drawString("Apreta Y para seguir jugando o N para abandonar", 140, 300);
	}
	
	public void setShow(boolean bool){
		show = bool;
	}
}
