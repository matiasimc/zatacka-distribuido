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
					catch (Exception e) {
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
	
	public void fill(int x, int y, int id) throws CollisionException {
		if (checkCircle(x,y, Point.dHip/2) != 0) throw new CollisionException();
		else fillCircle(x,y, Point.dHip/2, id);
	}
}