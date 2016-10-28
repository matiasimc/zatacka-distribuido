package cl.uchile.dcc.cc5303;
import java.awt.*;
import javax.swing.*;
import java.rmi.Naming;
import java.util.ArrayList;

public class ClientThread extends Thread {
	private Board tablero;
	private JFrame frame;
	
	public ClientThread() {
		try {
			ISnakeOrganizer snakeOrganizer = (ISnakeOrganizer) Naming.lookup("snakeList (CAMBIAR)");
			Player p = new Player("CAMBIAR A INPUT DE USUARIO O ARG DE EJECUCION"); // TODO
			snakeOrganizer.registerPlayer(p);
			tablero = new Board(800,800);
			
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}
