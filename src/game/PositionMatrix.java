package game;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

class CollisionException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1067667527103670246L;}

public class PositionMatrix implements Serializable {
	/*
	 * matrix[x][y] = clientID indica si un cliente paso
	 * por esa posicion, y 0 si no ha pasado nadie
	 */
	private int Width;
	private int Height;
	private int[][] matrix;
	
	public PositionMatrix(int Width, int Height) {
		this.Width = Width;
		this.Height = Height;
		matrix = new int[Height][Width];
	}
	
	public PositionMatrix(PositionMatrix m) {
		this.Width = m.Width;
		this.Height = m.Height;
		matrix = m.matrix.clone();
	}
	
	public int checkCircle(int cx, int cy, int r) throws CollisionException {
		for (int i = cx-r; i < cx+r; i++) {
			for (int j = cy-r; j < cy+r; j++) {
				if (Math.pow((i-cx),2) + Math.pow((j-cy),2) <= Math.pow(r-1,2)) {
					try {
						if (matrix[i][j] != 0) return matrix[i][j];
					}
					catch (IndexOutOfBoundsException e) {
						System.out.println("Alguién chocó contra el borde");
						throw new CollisionException();
					}
				}
			}
		}
		return 0;
	}
	
	public void deleteCircle(int cx, int cy, int r, int id) {
		for (int i = cx-r; i < cx+r; i++) {
			for (int j = cy-r; j < cy+r; j++) {
				if (Math.pow((i-cx),2) + Math.pow((j-cy),2) <= Math.pow(r,2)) {
					try {
						if (matrix[i][j] == id) matrix[i][j] = 0;
					}
					catch (IndexOutOfBoundsException e) {
						continue;
					}
				}
			}
		}
	}
	
	public void fillCircle(int cx, int cy, int r, int id) {
		for (int i = cx-r; i < cx+r; i++) {
			for (int j = cy-r; j < cy+r; j++) {
				if (Math.pow((i-cx),2) + Math.pow((j-cy),2) <= Math.pow(r,2)) {
					try {
						matrix[i][j] = id;
					}
					catch (IndexOutOfBoundsException e) {
						continue;
					}
				}
			}
		}
	}
	
	public void fill(int x, int y, int id, boolean visible) throws CollisionException {
		int cho;
		if ((cho = checkCircle(x,y, Point.dHip/2)) != 0 && visible){
			System.out.println("Alguien choco contra "+cho);
			throw new CollisionException();
		}
		else if (visible) fillCircle(x,y, Point.dHip/2, id);
	}
	
	public void deletePlayer(ArrayList<Point> body, int id) {
		for (Point p: body){
			deleteCircle(p.x, p.y, Point.dHip/2, id);
		}
	}
	
	public Point getPlace(){
			int w = Width/10;
			int h = Height/10;
		int x,y;
		while(true){
			x = ThreadLocalRandom.current().nextInt(w, 8*w+1);
			y = ThreadLocalRandom.current().nextInt(h, 8*h+1);
			System.out.println("Buscando un punto");
			try {
				if (matrix[x][y] == 0) {
					System.out.println("Te encontré un punto");
					return new Point(x, y, true);
				}
			}
			catch (IndexOutOfBoundsException e) {
				continue;
			}
		}
	}
	
}