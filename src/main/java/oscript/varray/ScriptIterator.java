package oscript.varray;

import java.util.Iterator;

import oscript.data.FunctionValueWrapper;
import oscript.data.JavaBridge;
import oscript.data.OBoolean;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.util.StackFrame;


/**
 * For elements interation of oscript code.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 12930 $
 * @date 	$LastChangedDate: 2012-05-25 20:42:02 +0300 (Fr, 25 Mai 2012) $
 * @project VisionR Server 
 */
public abstract class ScriptIterator extends Value implements Iterator {

	protected ScriptIterator() {
		super();
	}
	
	public Value resolve(int symbol)  {
		if (symbol == Symbols.HAS_NEXT1 || symbol == Symbols.HAS_NEXT2) {
			  return new FunctionValueWrapper(OBoolean.makeBoolean(hasNext()));
		}
		if (symbol == Symbols.NEXT) {
			  return new FunctionValueWrapper(JavaBridge.convertToScriptObject(next()));
		}		

		if (symbol == Symbols.REMOVE) {
			  return new Value() {
				  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
					  remove();
					  return null;
				 }
				@Override
				protected Value getTypeImpl() {
					return this;
				}
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
	
	public abstract boolean hasNext();
	public abstract Object next();
	public abstract void remove();

}
