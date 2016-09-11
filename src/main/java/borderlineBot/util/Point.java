package borderlineBot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

/** This Object represents an (immutable) Point of two Integer Values */
public class Point{
	
	/** The X value of the Point */
	@Getter private int x;
	/** The Y value of the Point */
	@Getter private int y;
	
	
	/** Constructs new Point */
	public Point(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	
	/** Returns Point of the sum of this Point with given Point */
	public Point add(Point point){
		return new Point(x+point.getX(), y+point.getY());
	}
	
	/** Returns Point of the difference of this Point with given Point */
	public Point substract(Point point){
		return new Point(x-point.getX(), y-point.getY());
	}
	
	/** Returns an int array of the form [x, y] */
	public int[] toArray(){
		return new int[]{x, y};
	}
	
	/** Returns a String of the Point */
	public String toString(){
		StringBuffer buffer = new StringBuffer();
		buffer.append('[').append(x).append('|').append(y).append(']');
		return buffer.toString();
	}
	
}
