import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

import javax.swing.JFrame;

public class ClientGame extends UnicastRemoteObject implements iClientGame {

	public iGame game;
	public iPlayer player;
	public int id;
	
	public boolean[] keys;
    private final static String TITLE = "Juego - CC5303";

    private final static int UPDATE_RATE = 30;
    private final static int GROW_RATE = 3;
    
    private int width;
    private int height;
    private JFrame frame;
    private Board tablero;
    
    public ClientGame(iGame game, int id) throws RemoteException{
		this.game = game;
		this.id = id;
		this.width = game.getWidth();
		this.height = game.getHeight();
    }
	
    
	@Override
	public void start() throws RemoteException {
		player = this.game.gettingPlayer(id);
		System.out.println("Hola");
        keys = new boolean[KeyEvent.KEY_LAST];
        
        frame = new JFrame(TITLE);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tablero = new Board(width, height, this);

        frame.add(tablero);
        tablero.setSize(width, height);

        frame.addKeyListener(new KeyListener() {
            
            public void keyTyped(KeyEvent e) {}

            
            public void keyPressed(KeyEvent e) {
                keys[e.getKeyCode()] = true;
            }

            
            public void keyReleased(KeyEvent e) {
                keys[e.getKeyCode()] = false;
            }
        });
        
        frame.pack();

		int frames = 0;
        while (true) { // Main loop
            // Controles
            if (keys[KeyEvent.VK_UP]) {
            	player.moveUp();
            	System.out.println("Arriba");
            }
            if (keys[KeyEvent.VK_DOWN]) {
            	player.moveDown();
            	System.out.println("Abajo");
            }
            
            
            ++frames;
            if (frames == GROW_RATE && player.isAlive()){
            	if(!this.game.checkCollision(player)){
                	System.out.println("Avanzando");
                    player.growUp(true);
                }
            	else {
            		System.out.print("Te moriste");
            	}
                frames = 0;
            }
            
            tablero.repaint();
            
            try {
                Thread.sleep(1000 / UPDATE_RATE);
            } catch (InterruptedException ex) {

            }
        }
    }

	@Override
	public ArrayList<iPlayer> gamePlayers() throws RemoteException {
		return this.game.players();
	}
}


