package cl.uchile.dcc.cc5303;

import java.awt.*;
import java.rmi.Naming;
import java.util.ArrayList;

public class Player {
	
	public static void main(String[] args) {
		try {
			iSnake mySnake = (iSnake) Naming.lookup("");
			iSnakeList snakeList = (iSnakeList) Naming.lookup("snakeList (CAMBIAR)");
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public Player() {
	}
}
