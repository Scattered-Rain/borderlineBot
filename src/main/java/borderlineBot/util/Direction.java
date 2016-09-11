package borderlineBot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
/** Representation of the 4 main Directions */
public enum Direction{
	UP(0),
	RIGHT(1),
	DOWN(2),
	LEFT(3);
	
	
	/** Returns the index of this Direction */
	@Getter private int index;
	
	
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
	
	
	//--statics--
	/** Returns the Direction with the given index (given index equivalent to index%4) */
	public static Direction getDirectionAt(int index){
		return Direction.values()[index%4];
	}
	
}
