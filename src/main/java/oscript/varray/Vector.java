package oscript.varray;

import java.util.Collection;
import java.util.Iterator;

/**
 * Delegate class, providing vector for oscripts.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 17460 $
 * @date 	$LastChangedDate: 2014-06-23 10:39:09 +0300 (Mo, 23 Jun 2014) $
 * @project VisionR Server 
 */
public class Vector extends Map
{
	public Vector() {
		onlyObjects=false;
		objMap=new ObjMultipleMap();
	}

	public Vector(MultiMap objMap) {
		onlyObjects=false;
		this.objMap=objMap;
	}
	
	public Vector(java.util.Map m) {
		onlyObjects=false;
		Iterator<java.util.Map.Entry> it = m.entrySet().iterator();
		objMap = new ObjMultipleMap();
		while (it.hasNext()) {
			java.util.Map.Entry e = it.next();
			put(e.getKey(),e.getValue());			
		}
	}
	
	public Vector(Collection key,Collection values) {
		onlyObjects=false;
		objMap=new ObjMultipleMap();
		Iterator it1 = key.iterator();
		Iterator it2 = key.iterator();
		while (it1.hasNext()) {
			if (!it2.hasNext()) {
				throw new RuntimeException("creating vecor with wrong parameters : collection size");
			}
			put(it1.next(),it2.next());
		}
		if (it2.hasNext())
			throw new RuntimeException("creating vector with wrong parameters : collection size");		
	}
	
 	public Vector(Object[] obj) {
		onlyObjects=false;
		objMap=new ObjMultipleMap();
 		for (int i=0;i<obj.length;i++) {
 			put(i,obj[i]);
 		}
 	}

 	public Vector(Collection col) {
		Iterator it = col.iterator();
		onlyObjects=false;
		objMap = new ObjMultipleMap();
		while (it.hasNext()) {
			Object obj = cnv(it.next());
			put(obj);
		}
	}

	

}
