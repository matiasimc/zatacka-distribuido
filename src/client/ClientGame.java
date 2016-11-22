package client;
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ThreadLocalRandom;

import game.Point;
import game.iGame;
import game.iPlayer;
import ui.Board;
import ui.Scores;
import ui.Window;

public class ClientGame extends UnicastRemoteObject implements iClientGame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7891867468608129190L;
	public iGame game;
	public int id;
	private iClient client;
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
    private Scores scores;
    public volatile int countdown = 0;
    
    public ClientGame(iClient client, iGame game, int id) throws RemoteException{
    	this.client = client;
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
        keys = new boolean[KeyEvent.KEY_LAST];
        game.newPlayer(id);
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
                	this.game.moveUp(id);
                	System.out.println("Arriba");
                	System.out.println(this.client.getServer().getDir());
                }
                if (keys[KeyEvent.VK_DOWN]) {	
                	this.game.moveDown(id);
                	System.out.println("Abajo");
                	System.out.println(this.client.getServer().getDir());
                }
                
                if (keys[KeyEvent.VK_M]) {
                	try{
                		this.game.getServer().migrate();
                	}
                	catch (Exception e) {
                		e.printStackTrace();
                	}
                }
                if (keys[KeyEvent.VK_Q]) {
                	this.game.removeClient(id);
                }
                
                
                game.increaseFrames(id);
                
                
                if (game.getFrames() == GROW_RATE && this.game.isAlive(id) && this.game.isPlaying()){
                	System.out.println("hola");
                	if(!this.game.checkCollision(id)){
                		if (skipFrames-- > 0){
                			this.game.growUp(id, false);
                        }else {
                        	skipFrames = 0;
                        	this.game.growUp(id, true);
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
                    	this.game.addPlayer(this.game.gettingPlayer(id));
                    }
                    if (keys[KeyEvent.VK_N]) {
                    	voted = true;
                    	System.out.println("Votaste no");
                    	this.game.voteNo(id);
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
	
	public iPlayer getPlayer() throws RemoteException{
		return this.game.gettingPlayer(id);
	}
	
	public synchronized ArrayList<Point> getBody(int clientId) throws RemoteException{
		return this.game.getBody(clientId);
	}
	
	public Point getHead(int clientId) throws RemoteException{
		return this.game.getHead(clientId);
	}
	
	public Color getColor(int clientId) throws RemoteException{
		return this.game.getColor(clientId);
	}
	
	public HashSet<Integer> clientIds() throws RemoteException{
		return this.game.clientIds();
	}
	
	public void setGame(iGame game){
		this.game = game;
	}
}


