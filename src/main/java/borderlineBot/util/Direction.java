package borderlineBot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
/** Representation of the 4 main Directions */
public enum Direction{
	UP(0, new Point(0, -1)),
	RIGHT(1, new Point(1, 0)),
	DOWN(2, new Point(0, 1)),
	LEFT(3, new Point(-1, 0));
	
	
	/** Returns the index of this Direction */
	@Getter private int index;
	/** The movement that is implied by this Direction */
	@Getter private Point dir;
	
	
	/** Returns the Direction clockwise to this Direction */
	public Direction turnRight(){
		return getDirectionAt(index+1);
	}
	
	/** Returns the Direction counter clockwise to this Direction */
	public Direction turnLeft(){
		return getDirectionAt(index+3);
	}
	
	/** Returns the Direction opposite to this Direction */
	public Direction turnBack(){
		return getDirectionAt(index+2);
	}
	
	/** Returns whether the given Direction is the same as this direction */
	public boolean isDirection(Direction dir){
		return this.index==dir.getIndex();
	}
	
	//--statics--
	/** Returns the Direction with the given index (given index equivalent to index%4) */
	public static Direction getDirectionAt(int index){
		return Direction.values()[index%4];
	}
	
}
