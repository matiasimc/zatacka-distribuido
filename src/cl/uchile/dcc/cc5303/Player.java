package cl.uchile.dcc.cc5303;

public class Player {
	private Snake mySnake;
	private String name;
	
	public Player(String name) {
		setName(name);
	}

	public Snake getMySnake() {
		return mySnake;
	}

	public void setMySnake(Snake mySnake) {
		this.mySnake = mySnake;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
