package oscript.varray;

import java.util.Collection;
import java.util.Iterator;

/**
 * Interface for sorting by special key
 * 
 * @author  plan-vision
 * @version $LastChangedRevision: 17460 $
 * @date 	$LastChangedDate: 2014-06-23 10:39:09 +0300 (Mo, 23 Jun 2014) $
 * @project VisionR Server 
 */
public interface MultiMap<KeyType,ValueType,ElementType> {
	
	public Collection<ValueType> getValues();
	
	public void put(KeyType key,ValueType val);

	public Collection<ValueType> get(KeyType key);
	
	public boolean containsKey(KeyType key);
	
	public boolean containsValue(ValueType val);
	
	Iterator<ValueType> valueIterator();
	Iterator<KeyType> keyIterator();
	public Iterator<? extends MultiMapElement<KeyType,ValueType,ElementType>> iterator();

	public int size();

	public boolean isEmpty();
	
	
	public Collection<ValueType> getMinValue();
	public KeyType getMinKey();
	public Collection<ValueType> getMaxValue();
	public KeyType getMaxKey();
	
	public void clear();
	public void clear(KeyType key);
	
}
