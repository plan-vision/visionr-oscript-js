package server;

import java.util.HashMap;

import oscript.data.Symbols;
import oscript.data.Value;
import oscript.data.ValueWrapperTempReference;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.StackFrame;

/**
 * Wrapper for values in the TEMPDB, that are not persistently stored in the DB.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 17526 $
 * @date 	$LastChangedDate: 2014-07-05 16:29:39 +0300 (Sa, 05 Jul 2014) $
 * @project VisionR Server 
 */
public class TempValueWrapper extends ValueWrapperTempReference  {
	
	private Value oval; 
	private String odkey;
	private long id;
	private String key;
	
	private static HashMap<String,HashMap<String,Value>> _vals = new HashMap();

	// TODO CALL ON TRANSACTION COMMIT
	public static void clearCache() {
		_vals.clear();
	}
	public static void clearForObject(String shkey,long id) {
		_vals.remove(shkey+":"+id);
	}
	
	public TempValueWrapper(String odkey,long id,String key) 
	{
		super();	
		this.odkey=odkey;
		this.id=id;
		this.key=key;
		this.oval=Value.NULL;
		HashMap<String,Value> t = _vals.get(odkey+":"+id);
		if (t != null) {
			Value v = t.get(key);
			if (v != null) oval=v;
		}
	}
	
	public Value get() 
	{
		return oval;
	}

	public void opAssign( Value val ) throws PackagedScriptObjectException {
		oval=val.unhand();
		HashMap<String,Value> t = _vals.get(odkey+":"+id);
		if (t == null) {
			t = new HashMap();
			_vals.put(odkey+":"+id, t);
		}
		t.put(key, oval);
	}
	
	public void remove() throws PackagedScriptObjectException 
	{
		HashMap<String,Value> t = _vals.get(odkey+":"+id);
		if (t != null) _vals.remove(key);
	}

	
	public Value resolve(int symbol) 
	{		
		if (symbol == Symbols.RESET) {
			return new Value() 
			{				  
				  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) 
				  {
					  Value val = args.referenceAt(0).unhand();
					  TempValueWrapper.this.opAssign(val);
					  return null;
				 }
				@Override
				protected Value getTypeImpl() {
					return this;
				}
			  };
		}

		if (symbol == Symbols.CLEAR) {
			return new Value() 
			{				  
				  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) 
				  {
					  TempValueWrapper.this.remove();
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
	   return this.oval.getMember(id,exception);
	 }

	 @Override
	 protected Value getTypeImpl() {
		return this;
	 }

	@Override
	public Value castToSimpleValue() {
		if (oval == null)
			return Value.NULL;
		Value oval = this.oval.unhand();
		if (oval instanceof ValueWrapperTempReference)
			return ((ValueWrapperTempReference)oval).castToSimpleValue();
		return oval;
	}
}

