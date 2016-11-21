package client;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

import javax.swing.JFrame;

import game.iGame;
import game.iPlayer;
import ui.Board;
import ui.Scores;
import ui.Window;

public class ClientGame extends UnicastRemoteObject implements iClientGame {

	public iGame game;
	public iPlayer player;
	public int id;
	public boolean running;
	public volatile boolean started;
	
	public boolean[] keys;
    private final static String TITLE = "Juego - CC5303";

    private final static int UPDATE_RATE = 30;
    private int GROW_RATE;
    
    private int width;
    private int height;
    private Window window;
    private Board tablero;
    private boolean voted;
    private int frames;
    private Scores scores;
    public volatile int countdown = 0;
    
    public ClientGame(iGame game, int id) throws RemoteException{
		this.game = game;
		this.id = id;
		this.width = game.getWidth();
		this.height = game.getHeight();
		this.voted = false;
		this.started = false;
		this.GROW_RATE = game.getGrowRate();
    }
	
    
	@Override
	public void start() throws RemoteException {
		player = this.game.gettingPlayer(id);
        keys = new boolean[KeyEvent.KEY_LAST];
        
        tablero = new Board(width, height, this);
        scores = new Scores(height, this);
        window = new Window(TITLE, tablero, scores);
        window.addKeyListener(new KeyListener() {
            
            public void keyTyped(KeyEvent e) {}

            
            public void keyPressed(KeyEvent e) {
                keys[e.getKeyCode()] = true;
            }

            
            public void keyReleased(KeyEvent e) {
                keys[e.getKeyCode()] = false;
            }
        });
        
        window.pack();
        window.setVisible(true);
        
		int skipFrames = 0;
        while (true) { // Main loop
        	tablero.repaint();
            scores.repaint();
            if (countdown > 0) {
            	if (started) {
            		countdown--;
            	}
            }
            if (started && countdown < 1) {
        		// Controles
                if (keys[KeyEvent.VK_UP]) {
                	player.moveUp();
                	System.out.println("Arriba");
                }
                if (keys[KeyEvent.VK_DOWN]) {	
                	player.moveDown();
                	System.out.println("Abajo");
                }
                
                
                game.increaseFrames(id);
                System.out.println(game.getFrames());
                
                
                if (game.getFrames() == GROW_RATE && player.isAlive() && this.game.isPlaying()){
                	System.out.println("hola");
                	if(!this.game.checkCollision(player)){
                		if (skipFrames-- > 0){
                            player.growUp(false);
                        }else {
                        	skipFrames = 0;
                            player.growUp(true);
                            if(ThreadLocalRandom.current().nextFloat() < 0.035){
                                skipFrames = ThreadLocalRandom.current().nextInt(2,4);
                            }
                        }
                    }
                	else {
                		System.out.print("Te moriste");
                	}	
      
                }
                
                if (!this.game.isPlaying() && !voted){
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
                }
                
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
		//frames = 0;
	}


	public void setStarted(boolean s) throws RemoteException {
		this.started = s;
	}


	public void setCountdown(int n) throws RemoteException {
		this.countdown = n;
	}
	
}


