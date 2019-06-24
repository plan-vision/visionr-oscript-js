package oscript.varray;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

public class ObjMultipleMap<KeyType,ValueType> implements MultiMap<KeyType,ValueType,ObjMultipleMapElement> {

	protected TreeMap<ObjMultipleMapElement<KeyType,ValueType>,ValueType> values=null;
	
	protected ObjMultipleMapElement<KeyType,ValueType> e1 = new ObjMultipleMapElement(null,null);
	protected ObjMultipleMapElement<KeyType,ValueType> e2 = new ObjMultipleMapElement(null,null);
	
	public Collection<ValueType> getValues() {
		return this.values.values();
	}
	
	public ObjMultipleMap() {
		this.values = new TreeMap<ObjMultipleMapElement<KeyType,ValueType>,ValueType>();
	}
	
	public boolean containsValue(ValueType val) {
		return (this.values.containsValue(val));
	}
	
	public Iterator<ValueType> valueIterator() {
		return getValues().iterator();
	}
	
	public Iterator<ObjMultipleMapElement<KeyType,ValueType>> iterator() {
		return this.values.keySet().iterator();
	}
	
	public void clear() {
		this.values.clear();
	}

	public int size() {
		return this.values.size();
	}

	public boolean isEmpty() {
		return this.values.isEmpty();
	}
	
	
	public void put(KeyType key, ValueType val) 
	{
		if (key == null)
			key=null;
		this.values.put(new ObjMultipleMapElement(key,val),val);
	}

	/**
	 * Must access e1 and e2 synchronized, in order to avoid java reference problems
	 */
	public synchronized Collection<ValueType> get(KeyType key) {
		e1.setKey(key);
		e1.setMinMax((byte)-1);
		e2.setKey(key);
		e2.setMinMax((byte)+1);
		Collection<ValueType> v = values.subMap(e1,e2).values();
		if (v.isEmpty())
			return null;
		return v;
	}
		
	public boolean containsKey(KeyType key) {
		return !get(key).isEmpty();
	}

	public void clear(KeyType key) {
		e1.setKey(key);
		e1.setMinMax((byte)-1);
		e2.setKey(key);
		e2.setMinMax((byte)+1);
		Iterator it = values.subMap(e1,e2).values().iterator();
		while (it.hasNext()) {
			it.next();
			it.remove();
		}
	}

	public Collection<ValueType> getMinValue() {
		KeyType t=getMinKey();
		if (t == null)
			return null;
		return get(t);
	}
	
	public Collection<ValueType> getMaxValue() {
		KeyType t=getMaxKey();
		if (t == null)
			return null;
		return get(t);
	}

	public KeyType getMinKey() {
		if (this.values.firstKey()==null)
			return null;
		return this.values.firstKey().getKey();
	}
	public KeyType getMaxKey() {
		if (this.values.lastKey()==null)
			return null;
		return this.values.lastKey().getKey();
	}
	private class KeyIterator implements Iterator<KeyType> 
	{
		private Iterator<ObjMultipleMapElement<KeyType,ValueType>>  it;		
		public KeyIterator(Iterator<ObjMultipleMapElement<KeyType,ValueType>> it) {
			this.it=it;
		}
		
		public boolean hasNext() {
			return it.hasNext();
		}

		public KeyType next() {
			return it.next().getKey();
		}
		public void remove() {
			it.remove();
		}
	}
	
	public Iterator<KeyType> keyIterator() {
		return new KeyIterator(this.values.keySet().iterator());
	}
		
}
