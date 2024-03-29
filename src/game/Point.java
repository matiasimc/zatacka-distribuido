package game;
import java.io.Serializable;

public class Point implements Serializable{
    public int x,y;
    public boolean visible;

    public static final int dHip = 4;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
        this.visible = true;
    }

    public Point(int x, int y, boolean visibility) {
        this.x = x;
        this.y = y;
        this.visible = visibility;
    }
    
}

