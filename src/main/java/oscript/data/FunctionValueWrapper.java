package oscript.data;


import oscript.util.StackFrame;


/**
 * TODO
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 11930 $
 * @date 	$LastChangedDate: 2012-01-17 16:27:36 +0100 (Di, 17 Jan 2012) $
 * @project VisionR Server 
 */
public class FunctionValueWrapper extends ValueWrapperTempReference {
	
	public final static FunctionValueWrapper NULL = new FunctionValueWrapper(null);
	
	private Value ref;
	public FunctionValueWrapper(Value val) {
		super();	
		this.ref=val;
	}
	
	public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
		return ref;
	}
	
	public Value get() {
		return ref;
	}

	@Override
	public Value castToSimpleValue() {
		if (ref == null)
			return Value.NULL;
		Value ref=this.ref.unhand();
		if (ref instanceof ValueWrapperTempReference)
			return ((ValueWrapperTempReference)ref).castToSimpleValue();
		return ref;
	}
}
