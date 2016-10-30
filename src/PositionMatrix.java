import java.util.concurrent.ThreadLocalRandom;

class CollisionException extends Exception {}

public class PositionMatrix {
	/*
	 * matrix[x][y] = clientID indica si un cliente paso
	 * por esa posicion, y 0 si no ha pasado nadie
	 */
	private static int WIDTH = 800;
	private static int HEIGHT = 800;
	private int[][] matrix;
	
	public PositionMatrix() {
		matrix = new int[WIDTH][HEIGHT];
	}
	
	public int checkCircle(int cx, int cy, int r) throws CollisionException {
		for (int i = cx-r; i < cx+r; i++) {
			for (int j = cy-r; j < cy+r; j++) {
				if (Math.pow((i-cx),2) + Math.pow((j-cy),2) < Math.pow(r,2)) {
					try {
						if (matrix[i][j] != 0) return matrix[i][j];
					}
					catch (IndexOutOfBoundsException e) {
						throw new CollisionException();
					}
				}
			}
		}
		return 0;
	}
	
	public void fillCircle(int cx, int cy, int r, int id) {
		for (int i = cx-r; i < cx+r; i++) {
			for (int j = cy-r; j < cy+r; j++) {
				if (Math.pow((i-cx),2) + Math.pow((j-cy),2) < Math.pow(r,2)) {
					matrix[i][j] = id;
				}
			}
		}
	}
	
	public void fill(int x, int y, int id, boolean visible) throws CollisionException {
		if (checkCircle(x,y, Point.dHip/2) != 0 && visible) throw new CollisionException();
		else if (visible) fillCircle(x,y, Point.dHip/2, id);
	}
	
	public Point getPlace(){
			int w = WIDTH/10;
			int h = HEIGHT/10;
		int x,y;
		while(true){
			x = ThreadLocalRandom.current().nextInt(w, 8*w+1);
			y = ThreadLocalRandom.current().nextInt(h, 8*h+1);
			System.out.println("Buscando un punto");
			if (matrix[x][y] == 0) {
				System.out.println("Te encontré un punto");
				return new Point(x, y, true);
			}
		}
	}
}