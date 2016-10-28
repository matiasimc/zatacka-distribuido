import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;

public class ClientGame extends UnicastRemoteObject implements iClientGame {

	public iGame game;
	
	public boolean[] keys;
    private final static String TITLE = "Juego - CC5303";

    private final static int WIDTH = 800, HEIGHT = 800;
    private final static int UPDATE_RATE = 30;
    private final static int GROW_RATE = 3;

    private JFrame frame;
    private Board tablero;
    
    public ClientGame(iGame game)throws RemoteException{
		this.game= game;
    }
	
	@Override
	public void start() throws RemoteException {
		System.out.println("Hola");
        keys = new boolean[KeyEvent.KEY_LAST];
        
        frame = new JFrame(TITLE);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.pack();
        frame.addKeyListener(new KeyListener() {
            
            public void keyTyped(KeyEvent e) {}

            
            public void keyPressed(KeyEvent e) {
                keys[e.getKeyCode()] = true;
            }

            
            public void keyReleased(KeyEvent e) {
                keys[e.getKeyCode()] = false;
            }
        });

		int frames = 0;
        int skipFrames = 0;
        while (true) { // Main loop
            // Controles
            if (keys[KeyEvent.VK_UP]) {
            	System.out.println("Arriba");
            }
            if (keys[KeyEvent.VK_DOWN]) {
            	System.out.println("Abajo");
            }

            if (keys[KeyEvent.VK_S]) {
            	this.game.doSomething();
            }


            ++frames;
            if (frames == GROW_RATE){
                if (skipFrames-- > 0){
                	System.out.println("Skipping");
            	}
            }
            frames = 0;

            try {
                Thread.sleep(1000 / UPDATE_RATE);
            } catch (InterruptedException ex) {

            }
        }
    }
}


