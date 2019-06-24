package oscript.varray;

import java.util.Collection;

/**
 * Dedicated class, providing set for oscripts.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 7283 $
 * @date 	$LastChangedDate: 2009-06-30 14:08:26 +0300 (Di, 30 Jun 2009) $
 * @project VisionR Server 
 */
public class Set extends java.util.TreeSet {
	
	public Set(Collection s) {
		super(s);
	}

	public Vector asVector() {
		return new Vector(this);
	}
}

