package oscript.varray;

import oscript.data.OExactNumber;
import oscript.data.Symbols;
import oscript.data.Value;
import server.ValueConvertor;

/**
 * Implementation of map key, needed by map, in oscript.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 17460 $
 * @date 	$LastChangedDate: 2014-06-23 10:39:09 +0300 (Mo, 23 Jun 2014) $
 * @project VisionR Server 
 */
public class MapKeyValueReference<KeyType> extends oscript.data.AbstractReference {
	
	private MultiMapElement el;
	private int pos;
	
	public MapKeyValueReference(MultiMapElement el,int pos) {
		super();
		this.el=el;
		this.pos=pos;
	}

	 public Object getKey() {
		 return el.getKey();
	 }

	 public Object getValue() {
		 return el.getValue();
	 }
	 
	 public Value getType()
	 {
		 return get().getType();
	 }
	 
	  public Value getTypeImpl()
	  {
		  return this;
	  }
	  
	  public Object castToJavaObject() {
		  return get().castToJavaObject();
	   }
		  	  
	  protected Value get() {
		  return ValueConvertor.convert(el.getValue());
	  }
		
	  public Value getMember( int id, boolean exception )
	  {		
		  if (id == Symbols.KEY_TYPE)  {
			  return ValueConvertor.convert(el.getKey());
		  }
		  if (id == Symbols.VALUE_TYPE) {
			  return ValueConvertor.convert(el.getValue());
		  }
		  if (id == Symbols.POS_TYPE) {
			  return new OExactNumber(pos);
		  }
		  Value val = get();
		  if (val != null)
			  return val.getMember(id,exception);
		  return super.getMember( id,exception);
	  }

	  public Value getTypeMember(Value obj,int id) {
		return null;
	  }
}
