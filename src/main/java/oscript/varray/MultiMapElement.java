package oscript.varray;

/**
 * Interface for MultiMap element that has special key
 * 
 * @author  plan-vision
 * @version $LastChangedRevision: 17460 $
 * @date 	$LastChangedDate: 2014-06-23 10:39:09 +0300 (Mo, 23 Jun 2014) $
 * @project VisionR Server 
 */
public interface MultiMapElement<KeyType,ValueType,ThisType> extends Comparable<ThisType> {
	public ValueType getValue();
	public KeyType getKey();

	public void setKey(KeyType key);
	public void setValue(ValueType val);
}
