package server;

import java.util.Iterator;

import oscript.data.FunctionValueWrapper;
import oscript.data.OBoolean;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.data.ValueWrapperTempReference;


/**
 * TODO
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 15408 $
 * @date 	$LastChangedDate: 2013-06-24 14:33:39 +0300 (Mo, 24 Jun 2013) $
 * @project VisionR Server 
 */
public class IteratedValueWrapper extends ValueWrapperTempReference {
	
	public final static FunctionValueWrapper NULL = new FunctionValueWrapper(null);
	
	private Iterator<Value> it;
	private Value val=null;

	public IteratedValueWrapper(Iterator<Value> it) {
		super();	
		this.it=it;
	}

	
	public Value resolve(int symbol) 
	{		
		if (symbol == Symbols.NEXT) 
		{
			val=null;
			if (!it.hasNext())
				return Value.NULL;
			val = it.next();
			return new FunctionValueWrapper(val);
		}
		if (symbol == Symbols.HAS_NEXT1 || symbol == Symbols.HAS_NEXT2) {
			return new FunctionValueWrapper(OBoolean.makeBoolean(it.hasNext()));
		}
		if (symbol == Symbols.IS_EMPTY || symbol == Symbols.IS_EMPTY2) {
			return new FunctionValueWrapper(OBoolean.makeBoolean(val == null));
		}
		if (symbol == Symbols.REMOVE) 
		{
			return new Value() {
				@Override
				protected Value getTypeImpl() {
					return this;
				}
				public Value callAsFunction(oscript.util.StackFrame sf, oscript.util.MemberTable args) throws oscript.exceptions.PackagedScriptObjectException {
					try {it.remove(); } catch (IllegalStateException e) {}
					return Value.NULL;
				};
			};
		}

 		return null;
	}
	
	public Value getMember( int id, boolean exception )
	 {
	   Value val = resolve(id);
	   if( val != null )
	     return val;
	   return super.getMember( id,exception);
	 }

	 @Override
	 protected Value getTypeImpl() {
		return this;
	 }
	 
	 public Value get() {
		if (val == null)
			return Value.NULL;
		return val;
	}


	@Override
	public Value castToSimpleValue() {
		return Value.NULL;
	}
}