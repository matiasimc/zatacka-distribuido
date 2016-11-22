package ui;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.io.File;
import java.rmi.RemoteException;
import java.rmi.UnmarshalException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import client.ClientGame;
import game.iPlayer;

public class Scores extends Canvas {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -597291296125874865L;
	public int height;
	public final int width = 200;
	private ClientGame cGame;
	private Image img;
    private Graphics buffer;
    private Font cf;
	
	public Scores(int height, ClientGame cGame) {
		try {
			cf = Font.createFont(Font.TRUETYPE_FONT, new File("IndieFlower.ttf"));
			cf = cf.deriveFont(18.0f);
		     GraphicsEnvironment ge = 
		         GraphicsEnvironment.getLocalGraphicsEnvironment();
		     ge.registerFont(cf);
		} catch (Exception e) {
		     //Handle exception
		}
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
			return;
		}
       
        graphics.drawImage(img, 0, 0, null);
	}
	
	private void showScores(ArrayList<iPlayer> players) throws RemoteException{
		if (this.buffer == null) this.buffer = this.img.getGraphics()	;
		buffer.setColor(Color.WHITE);
		buffer.setFont(cf);
		buffer.drawString("Puntajes:" , 10 , 100);
		int offset = 50;
		Collections.sort(players, new Comparator<iPlayer>() {
			@Override
        	public int compare(iPlayer p2, iPlayer p1)
        	{
        		return  p1.getScore() - p2.getScore();		
        	}
		});
		try{
			for (iPlayer p: players){
				String toDraw = "Player "+p.getId();
				if (cGame.getPlayer().getId() == p.getId()) toDraw = toDraw+" (you)";
				toDraw = toDraw + ": "+p.getScore();
				buffer.setColor(p.getColor());
				buffer.drawString(toDraw, 10, 100+offset);
				offset += 30;
			}
			buffer.setColor(Color.WHITE);
			buffer.drawString("Pause: SPACE", 10, 500);
			buffer.drawString("Close: Q", 10, 540);
		}
		catch(UnmarshalException e){
			return;
		}
	}
	
}
