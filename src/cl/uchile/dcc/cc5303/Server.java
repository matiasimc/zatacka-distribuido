package cl.uchile.dcc.cc5303;

import java.awt.Color;
import java.rmi.Naming;

public class Server {
	
	public static int numberOfPlayers = 5;
	private static final Color[] colorList = {Color.blue, Color.red, Color.green, Color.yellow, Color.orange};
	private static final Point[] initialPointList = {null, null, null, null, null};
	public static void main(String[] args) {
		try {
			PositionMatrix matrix = new PositionMatrix();
			ISnakeOrganizer snakeList = new SnakeOrganizer();
			for (int i = 0; i < numberOfPlayers; i++) {
				ISnake snake = new Snake(colorList[i], initialPointList[i], i);
			}
			Naming.rebind("snakeList (CAMBIAR)", snakeList);
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
