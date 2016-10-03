package borderlineBot.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
/** Container for two instances of two different Object Types */
public class Tuple<Generic1, Generic2>{
	
	/** Object 1 */
	@Setter @Getter Generic1 a;
	/** Object 2 */
	@Setter @Getter Generic2 b;
	
}
