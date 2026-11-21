package tp1;

public class Position {
	private int x ;
    private int y;
    
    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() { 
    	return x; 
    	}
    public int getY() {
    	return y; 
    	}
	public double distance(Position p) {
        double dx = x - p.x;
        double dy = y - p.y;
        return Math.sqrt(dx * dx + dy * dy);
	}
	public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
