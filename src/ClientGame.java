import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

public class ClientGame extends UnicastRemoteObject implements iClientGame {

	public iGame game;
	public iPlayer player;
	public int id;
	public boolean running;
	
	public boolean[] keys;
    private final static String TITLE = "Juego - CC5303";

    private final static int WIDTH = 800, HEIGHT = 800;
    private final static int UPDATE_RATE = 30;
    private final static int GROW_RATE = 3;

    private JFrame frame;
    private Board tablero;
    
    public ClientGame(iGame game, int id) throws RemoteException{
		this.game = game;
		this.id = id;
    }
	
	public void start() throws RemoteException {
		player = this.game.gettingPlayer(id);
		Random random = new Random();
		System.out.println("Hola");
        keys = new boolean[KeyEvent.KEY_LAST];
        
        frame = new JFrame(TITLE);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tablero = new Board(WIDTH, HEIGHT, this);

        frame.add(tablero);
        tablero.setSize(WIDTH, HEIGHT);

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
        int skipFrames = 0;
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
            	if(this.game.checkCollision(player)) {
                	System.out.println("Te moriste");
                	player.die();
                }
            	else if (skipFrames-- > 0){
                	System.out.println("Skipping");
                    player.growUp(false);
                }else {
                	System.out.println("Avanzando");
                    skipFrames = 0;
                    player.growUp(true);

                    if(random.nextFloat()< 0.1){
                        skipFrames = 2 + random.nextInt(4);
                        System.out.println(skipFrames);
                    }
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
	
	public boolean isRunning() throws RemoteException {
		return this.running;
	}
	
	public void setRunning(boolean running) throws RemoteException {
		this.running = running;
	}

	
	public ArrayList<iPlayer> gamePlayers() throws RemoteException {
		return this.game.players();
	}
}


