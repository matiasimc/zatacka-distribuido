import java.rmi.RemoteException;


public class GameThreads extends Thread{

	iClientGame cGame;
	
	public GameThreads(iClientGame cGame) {
		this.cGame =cGame;
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() {
		try {
			this.cGame.start();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}

}
