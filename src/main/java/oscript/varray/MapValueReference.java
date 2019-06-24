package oscript.varray;

import java.util.Collection;

import oscript.data.FunctionValueWrapper;
import oscript.data.JavaBridge;
import oscript.data.OBoolean;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.util.StackFrame;
import server.ValueConvertor;

/**
 * Implementation of map value, required by map, for oscript.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 17460 $
 * @date 	$LastChangedDate: 2014-06-23 10:39:09 +0300 (Mo, 23 Jun 2014) $
 * @project VisionR Server 
 */
public class MapValueReference extends oscript.data.AbstractReference {
		
	private Object key;
	private Map parent;
	private Collection value;	//<? extends MultiMapElement> 
	
	public MapValueReference(Map parent,Object key,Collection<? extends MultiMapElement> value) {
		super();
		this.parent=parent;
		this.key=key;
		this.value=value;
	}

	public Object getKey() {
		return key;
	}
	
	public Value getType() {
		return get().getType();
	}
	 
	public Value getTypeImpl() {
		return this;
	}
	  
	public Object castToJavaObject() {
		return get().castToJavaObject();
	}
		  	  
	protected Value get() {
		if (value == null || value.isEmpty())  {		
			return Value.NULL;
		}
		return ValueConvertor.convert(value.iterator().next());
	}
		
	public Value getMember( int id, boolean exception ) {
		if (id == Symbols.IS_EMPTY || id == Symbols.IS_EMPTY2) {
			if (value == null || value.isEmpty())
				return OBoolean.TRUE;
			else
				return OBoolean.FALSE;
		}
		  
		if (value != null) {
			if (id == Symbols.VALUE_ITERATOR) {
				return new FunctionValueWrapper(iterator());
			}
			if (id == Symbols.KEY_TYPE) {
				return JavaBridge.convertToScriptObject(key);
			}

			if (id == Symbols.REMOVE) {
				return new Value() {

					public Value callAsFunction(StackFrame sf,
							oscript.util.MemberTable args) {
						parent.clear(key);
						return Value.NULL;
					}

					@Override
					protected Value getTypeImpl() {
						return this;
					}
				};
			}

			if (id == Symbols.VALUE_TYPE) {
				return this;
			}

			Value val = get();
			if (val == null)
				return super.getMember(id, exception);
			return val.getMember(id, exception);
		}

		return super.getMember(id, exception);
	}

	public Collection getCollection() {
		return (Collection<? extends MultiMapElement>) parent.get(key, 0);
	}

	private void insert(Value val) {
		parent.put(key, val.castToJavaObject());
	}

	public void opAssign(Value val) {
		parent.clear(key);
		insert(val);
		value = (Collection) parent.get(key, 0);
	}

	public Value bopLeftShift(Value val) {
		insert(val);
		value = (Collection) parent.get(key, 0);
		return this;
	}

	public ScriptIterator iterator() {
		return new IteratorWrapper(value.iterator());
	}

	public Value getTypeMember(Value obj, int id) {
		return null;
	}

}
