import java.awt.event.KeyEvent;
import java.util.ArrayList;


public class ClientGame implements iClientGame, Runnable {

	public iGame game;
    public boolean[] keys;
	
	public ClientGame(iGame game){
		this.game= game;
	}
	@Override
	public void run() {
	    keys = new boolean[KeyEvent.KEY_LAST];
		System.out.println("Comenzó!");
		for(;;){ 
            // Controles
            if (keys[KeyEvent.VK_UP]) {
        		System.out.println("Arriba");
            }
            if (keys[KeyEvent.VK_DOWN]) {
        		System.out.println("Abajo");
            }
		}
	}

}
