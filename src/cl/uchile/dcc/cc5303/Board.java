package cl.uchile.dcc.cc5303;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Board extends Canvas{

    public int width, height;
    
    // Jugadores y elementos del juego acá
    public ArrayList<Snake> snakes;

    // doble buffer para dibujar
    public Image img;
    public Graphics buffer;


    public Board(int width, int height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public void update(Graphics graphics) {
        paint(graphics);
    }

    @Override
    public void paint(Graphics graphics) {
        if(this.buffer==null){
            this.img = createImage(getWidth(), getHeight());
            this.buffer = this.img.getGraphics();
        }

        this.buffer.setColor(Color.black);
        this.buffer.fillRect(0, 0, getWidth(), getHeight());

        // dibujar elementos del juego
        for (Snake s: snakes) {
        	buffer.setColor(s.color);
        	try {
				s.draw(buffer);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }

        graphics.drawImage(img, 0, 0, null);
    }
}
