package oscript.varray;

import java.util.Collection;
import java.util.Iterator;

import oscript.data.FunctionValueWrapper;
import oscript.data.OExactNumber;
import oscript.data.Symbol;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.exceptions.PackagedScriptObjectException;

/**
 * Implementation of map collection for map in VScript.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 15415 $
 * @date 	$LastChangedDate: 2013-06-24 18:27:54 +0300 (Mo, 24 Jun 2013) $
 * @project VisionR Server 
 */
public class MapValueCollection extends Value implements Collection {
	
	enum Mode {
		KEYS, VALUES, COLLECTION, OTHER
	}
	
	private Map parent;
	private Mode mode = Mode.KEYS;
	private boolean sorted = false;
	
	public RuntimeException unimplemented()  {	
		return new RuntimeException("MapValueCollection : Call method does not exists");
	}
	
	public MapValueCollection(Mode mode,Map parent) {
		super();
		this.parent=parent;
		this.mode=mode;
	}

	public int size() {
		return parent.size();
	}

	public boolean isEmpty() {
		return parent.isEmpty();
	}

	public boolean contains(Object o) {
		switch( this.mode ) {
			case KEYS:
				return parent.containsKey(o);		
			case VALUES:
				return parent.containsValue(o);
			default:
				throw new RuntimeException("the values are not only db objects : function not applicable");
		}
	}

	public ScriptIterator iterator() {
		switch( this.mode ) {
			case KEYS:
				return parent.keyIterator();
			case VALUES:
				return parent.valueIterator(this.sorted);
			default:
				return parent.valueIterator(this.sorted);
		}		
	}
	
	public Object[] toArray() {
		int s = size();
		Object[] t= new Object[s];
		Iterator it = iterator();
		int i =0;
		while (it.hasNext()) {
			t[i++]=it.next();
		}
		return t;
	}

	public Object[] toArray(Object[] a) {
		Iterator it = iterator();
		int i =0;
		while (it.hasNext()) {
			a[i++]=it.next();
		}
		return a;
	}

	public boolean add(Object o) {
		throw unimplemented();
	}

	public boolean remove(Object o) {
		throw unimplemented();
	}

	public boolean containsAll(Collection c) {
		throw unimplemented();
	}

	public boolean addAll(Collection c) {
		throw unimplemented();
	}

	public boolean removeAll(Collection c) {
		throw unimplemented();
	}

	public boolean retainAll(Collection c) {
		throw unimplemented();
	}

	public void clear() {
		throw unimplemented();
	}
	
	public Value elementAt(Value val) {
		if ( mode == Mode.VALUES ) {
			Object to = val.castToJavaObject();		
			if (to instanceof String || to instanceof Number) {
				Value v = (Value)parent.get(to,1);
				if (v == null)
					throw new RuntimeException("Accessing missing element :"+to);
				return v;
			} 
			throw new RuntimeException("MapValueCollection : unsupported value type "+val.getClass().getName());
		}
		throw new RuntimeException("MapValueCollection : not applicable mode="+mode);
	}
	
	
	/************************** VALUE Methods *****************************/
	public Value getType() {
		return this;
	}
	public Value getTypeImpl() {
		return this;
	}
	  
	public Object castToJavaObject() {
		return this;
	}
  
	public Value getMember( int id, boolean exception ) {
	    Value val = resolve(id);
		if( val != null ) {
			return val;
		}
	    return super.getMember( id,exception);
	}
  
	public Value resolve(int symbol) {
		if (symbol == Symbols.SIZE) {
			return new FunctionValueWrapper(new FunctionValueWrapper(OExactNumber.makeExactNumber(size())));
		}
		if (symbol == Symbols.FIRST) {
			if (size() == 0)
				return Value.NULL;
			if (size() == 0)
				return Value.NULL;
			ScriptIterator it = this.iterator();
			if (!it.hasNext())
				return Value.NULL;				
			return new FunctionValueWrapper((Value)it.next());
		}
		
		if (symbol == Symbols.ITERATOR) {
			return new FunctionValueWrapper(this.iterator());
		}
		
		if (symbol == Symbols.SORT) {
			this.sorted = true;
			return this;
		}		
		
		if (mode == Mode.VALUES)  {
			if (!parent.isCodeUnique)
				return null;
			return (Value)parent.get(Symbol.getSymbol(symbol).castToString(),1);
		} 
		return null;
	}
	
	@Override
	public int length() throws PackagedScriptObjectException
	{
		return size();
	}
}
