
import java.awt.Color;
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
		try {
			iServer server = new Server();
			System.out.println("SIII");
			Naming.bind("//localhost/ABC", server);
		}catch(Exception e){
			e.printStackTrace();
		}
	}	
}
