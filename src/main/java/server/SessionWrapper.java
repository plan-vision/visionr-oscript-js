package server;

import bridge.bridge;
import oscript.data.OBoolean;
import oscript.data.OExactNumber;
import oscript.data.OString;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.StackFrame;

/**
 * Wrapper providing access to the user specific session data.
 *
 * @author  plan-vision
 * @version $LastChangedRevision$
 * @date 	$LastChangedDate$
 * @project VisionR Server 
 */
public class SessionWrapper extends Value 
{
	public Value getMember( int id, boolean exception ) 
	{
		if (id == Symbols.PROGRESS) 
		{
			String s = bridge.getProgress();
			if (s == null)
				s = "";			
			return new OString(s) 
			{
				@Override
				public void opAssign(Value val)
						throws PackagedScriptObjectException 
				{
					String s = val.castToString();
					bridge.setProgress(s);
				}
			};
		}
		if( id == Symbols.ADD_TIMEOUT ) 
		{
			return new Value() 
			{
				public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) 
				{
					return new OExactNumber(0);
				}
	
				@Override
				protected Value getTypeImpl() {
					return this;
				}
			};
		} else if (id == Symbols.IS_FORMS) {
			return OBoolean.TRUE;
		}
		
				
		if( id == Symbols.LOGGED_IN) {
			return new OBoolean( true ) {
				public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
					return OBoolean.makeBoolean( true );
				}
				
				public void opAssign( Value val ) {
				}
				
				@Override
				protected Value getTypeImpl() {
					return this;
				}
			};
		}
			
		return super.getMember( id,exception);
	}
		
	public Value elementAt(Value _key) {
		return Value.NULL;
	}
	
	@Override
	protected Value getTypeImpl() {
		return this;
	}
}
