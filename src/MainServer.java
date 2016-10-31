
import java.rmi.Naming;

public class MainServer {
	/**
	static int numberOfPlayers = 5;
	static final Color[] colorList = {Color.blue, Color.red, Color.green, Color.yellow, Color.orange};
	static final Point[] pointList = {null, null, null, null, null};
	public static void main(String[] args) {
		try {
			PositionMatrix matrix = new PositionMatrix();
			iSnakeList snakeList = new SnakeList();
			Naming.rebind("snakeList (CAMBIAR)", snakeList);
			for (int i = 0; i < numberOfPlayers; i++) {
				iSnake snake = new Snake(colorList[i], pointList[i], i, "serpiente"+i);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}**/
	public static void main(String[] args) {
		int waitPlayers = 2;
		String ip = "";
		try {
			ip = args[0];
			System.out.println(ip);
		}
		catch (Exception e) {
			System.out.println("Usage: MainServer.java <ip> -n <waitPlayer>");
			System.exit(1);
		}
		try {
			String option = args[1];
			if (! option.equals("-n")) {
				System.out.println("Usage: MainServer.java <ip> -n <waitPlayer>");
				System.exit(1);
			}
		}
		catch (Exception e) {
			// Do nothing
		}
		try {
			waitPlayers = Integer.parseInt(args[2]);
		}
		catch (Exception e) {
			System.out.println("Usage: MainServer.java <ip> -n <waitPlayer>");
			System.exit(1);
		}
		try {
			System.setProperty("java.rmi.server.hostname", ip);

			
			iServer server = new Server(waitPlayers);
			Naming.bind("rmi://"+ip+":1099/ABC", server);
			System.out.println("Server UP");
		}catch(Exception e){
			System.out.println("La excepcion la capture en el main");
			e.printStackTrace();
		}
	}	
}
