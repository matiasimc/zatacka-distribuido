

import java.awt.*;
import java.rmi.RemoteException;
import java.util.ArrayList;


public class Board extends Canvas{

	public ClientGame cGame;
    public int width, height;

    // doble buffer para dibujar
    public Image img;
    public Graphics buffer;


    public Board(int width, int height, ClientGame cGame) {
        this.width = width;
        this.height = height;
    	this.cGame =cGame;
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
        try {
        	for(iPlayer player: this.cGame.gamePlayers()){
            	buffer.setColor(player.getColor());
            	drawSnake(player,buffer);
        	}
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        graphics.drawImage(img, 0, 0, null);
    }

	public void drawSnake(iPlayer player,Graphics graphics) throws RemoteException {
        for (Point p : player.getBody())
            if (p.visible)
                graphics.fillOval(p.x - Point.dHip/2, p.y - Point.dHip/2, Point.dHip, Point.dHip);
    }
}
