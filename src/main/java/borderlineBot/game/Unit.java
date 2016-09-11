package borderlineBot.game;

import borderlineBot.util.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
/** Defines the unit types in the game */
public enum Unit{
	/** Placeholder Unit */
	NONE(0, false, new int[]{0, 0, 0, 0}),
	/** Unit which faces opponent with a ONE (Front Row) */
	ONE(1, true, new int[]{1, 2, 3, 4}),
	/** Unit which faces opponent with a TWO (Back Row) */
	TWO(2, true, new int[]{2, 3, 4, 1});
	
	
	/** The id of this Unit type */
	@Getter private int id;
	/* Whether this unit is a legal unit instead of a place holder */
	@Getter private boolean isUnit;
	/** The list of ranges this unit can move, index defines direction */
	private int[] range;
	
	
	/** Returns range of this unit in the given direction */
	public int getRange(Direction dir){
		return range[dir.getIndex()];
	}
	
	/** Returns the range, if mirror is true returns the range turned 180 degrees from given direction */
	public int getMirroredRange(Direction dir, boolean mirror){
		if(mirror){
			return getRange(dir.turnBack());
		}
		else{
			return getRange(dir);
		}
	}
	
}
