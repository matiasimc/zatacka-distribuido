package ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.rmi.RemoteException;
import java.util.ArrayList;

import client.ClientGame;
import game.iPlayer;

public class Scores extends Canvas {
	
	public int height;
	public final int width = 200;
	private ClientGame cGame;
	private Image img;
    private Graphics buffer;
	
	public Scores(int height, ClientGame cGame) {
		this.height = height;
		this.cGame = cGame;
		this.setSize(width, height);
	}
	
	public void update(Graphics graphics) {
        paint(graphics);
    }
	
	public void paint(Graphics graphics) {
		
        this.img = createImage(this.width, this.height);
        this.buffer = this.img.getGraphics();
        this.buffer.setColor(Color.black);
        this.buffer.fillRect(0, 0, this.width, this.height);

        // dibujar elementos del juego
        try {
        	showScores(this.cGame.gamePlayers());
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
       
        graphics.drawImage(img, 0, 0, null);
	}
	
	private void showScores(ArrayList<iPlayer> players) throws RemoteException{
		if (this.buffer == null) this.buffer = this.img.getGraphics();
		buffer.setColor(Color.WHITE);
		buffer.setFont(new Font("Impact", Font.PLAIN, 20));
		buffer.drawString("Puntajes:" , 10 , 100);
		int offset = 50;
		for (iPlayer p: players){
			String toDraw = "Player "+p.getId();
			if (cGame.player.getId() == p.getId()) toDraw = toDraw+" (you)";
			toDraw = toDraw + ": "+p.getScore();
			buffer.setColor(p.getColor());
			buffer.drawString(toDraw, 10, 100+offset);
			offset += 50;
		}
		buffer.setColor(Color.WHITE);
	}
	
}
