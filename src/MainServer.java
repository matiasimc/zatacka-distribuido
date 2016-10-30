
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
		String ip = args[0];
		System.out.println(ip);
		try {
			System.setProperty("java.rmi.server.hostaname", ip);
			iServer server = new Server();
			Naming.bind("rmi://"+ip+":1099/ABC", server);
			System.out.println("Server UP");
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}
