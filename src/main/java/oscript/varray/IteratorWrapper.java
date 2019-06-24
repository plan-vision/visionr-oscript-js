package oscript.varray;

import java.util.Iterator;

import server.ValueConvertor;

/**
 * List iterator for oscript elements.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 10196 $
 * @date 	$LastChangedDate: 2010-04-27 04:29:23 +0300 (Di, 27 Apr 2010) $
 * @project VisionR Server 
 */
public class IteratorWrapper extends ScriptIterator {
	private Iterator ri;
	public IteratorWrapper(Iterator it) {
		ri=it;
	}
	
	public void remove() { 
		throw new RuntimeException("Removing iterator element not supported");
	}
	
	public Object next() { 
		Object o = ri.next();
		return ValueConvertor.convert(o);
	}			
	public boolean hasNext() { return ri.hasNext(); }

	public static Iterator getEmptyIterator() {
		return new EmptyIterator();
	}
	
	private static class EmptyIterator implements Iterator {

		public EmptyIterator() {};
		
		public boolean hasNext() {
			return false;
		}

		public Object next() {
			throw new RuntimeException("Calling next on empty iterator");
		}

		public void remove() {
			throw new RuntimeException("Removing iterator element not supported");				
		}
	}
}
	
