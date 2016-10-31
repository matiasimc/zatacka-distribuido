import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;

public class ClientGame extends UnicastRemoteObject implements iClientGame {

	public iGame game;
	public iPlayer player;
	public int id;
	public boolean running;
	
	public boolean[] keys;
    private final static String TITLE = "Juego - CC5303";

    private final static int UPDATE_RATE = 30;
    private final static int GROW_RATE = 2;
    
    private int width;
    private int height;
    private JFrame frame;
    private Board tablero;
    private boolean voted;
    private int frames;
    
    public ClientGame(iGame game, int id) throws RemoteException{
		this.game = game;
		this.id = id;
		this.width = game.getWidth();
		this.height = game.getHeight();
		this.voted = false;
		this.frames = 0;
    }
	
    
	@Override
	public void start() throws RemoteException {
		player = this.game.gettingPlayer(id);
        keys = new boolean[KeyEvent.KEY_LAST];
        
        frame = new JFrame(TITLE);
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
        frame.setVisible(true);
        
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
            
            
            if (frames == GROW_RATE && player.isAlive() && this.game.isPlaying()){
            	System.out.println("hola");
            	if(!this.game.checkCollision(player)){
            		if (skipFrames-- > 0){
                        player.growUp(false);
                    }else {
                    	skipFrames = 0;
                        player.growUp(true);
                        if(ThreadLocalRandom.current().nextFloat() < 0.035){
                        	System.out.println(skipFrames);
                            skipFrames = ThreadLocalRandom.current().nextInt(2,4);
                        }
                    }
                    frames = 0;
                }
            	else {
            		System.out.print("Te moriste");
            	}	
  
            }
            
            
            tablero.repaint();
            
            if (!this.game.isPlaying() && !voted){
            	System.out.println("VOTA QLO");
            	this.tablero.setShow(true);
            	if (keys[KeyEvent.VK_Y]) {
            		voted = true;
                	System.out.println("Votaste si");
                	this.game.addPlayer(player);
                }
                if (keys[KeyEvent.VK_N]) {
                	voted = true;
                	System.out.println("Votaste no");
                	this.game.voteNo();
                }
            	frames = 0;
            }
            
            try {
                Thread.sleep(1000 / UPDATE_RATE);
            } catch (InterruptedException ex) {

            }
        }
    }
	
	@Override
	public void close() throws RemoteException {
		System.exit(1);
	}
	@Override
	public boolean isRunning() throws RemoteException {
		return this.running;
	}
	
	@Override
	public void setRunning(boolean running) throws RemoteException {
		this.running = running;
	}

	@Override
	public ArrayList<iPlayer> gamePlayers() throws RemoteException {
		return this.game.players();
	}
	
	@Override
	public void resetVote(){
		voted = false;
	}
	
	@Override
	public void resetBuffer() throws RemoteException{
		tablero.buffer = null;
		tablero.setShow(false);
		frames = 0;
	}
	
}


