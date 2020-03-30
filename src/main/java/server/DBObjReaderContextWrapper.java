package server;

import java.util.HashMap;

import bridge.bridge;

import oscript.data.AbstractReference;
import oscript.data.OBoolean;
import oscript.data.OExactNumber;
import oscript.data.OString;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.MemberTable;
import oscript.util.StackFrame;
import oscript.varray.Map;

public final class DBObjReaderContextWrapper extends AbstractReference
{
	
	protected String loadMode = null;
	public String odefkey;
	private String condition;
	protected HashMap<String,Object> params = new HashMap();
	//private boolean isData;
	
	public DBObjReaderContextWrapper( String odefkey, String condition) {
		this.odefkey=odefkey;
		this.condition = (condition == null) ? "" : condition.trim();
		if (this.condition.length() == 0)
			this.condition=null;
	}
	
	Value _result = null;
	private Value check() {
		if (_result != null) return _result;
		
		Object[] r = bridge.SELECT(odefkey,condition, params,loadMode);
		Map res = new Map();
		for (Object k : r)
			res.put((ObjectWrapper)k);
		return res;		
	}
	

	public Value elementAt(Value key) 
	{
		  Object x = key.castToJavaObject();
		  if (x instanceof String) 
		  {
			  final String t = (String)x;
			  return new Value() {
				public void opAssign(Value val) 
				{
					Object x = val.castToJavaObject();
					if (x == null) 
						params.remove(t);
					else 
						params.put(t, x);
   			    }
				@Override
				protected Value getTypeImpl() {
					return this;
				}
			  };				
		  }
		  return check().elementAt(key);
	  }
	
	  public int getSize()  
	  {
		 return (int)check().getMember("size").castToExactNumber();
	  }
	  
	  public boolean isEmpty() 
	  {
		  return getSize() == 0;
	  }
	
	public Value resolve(int symbol) 
	{
		switch (symbol) 
		{
			case Symbols.VALUE_TYPE :
				return get();
			case Symbols.SIZE :
				return OExactNumber.makeExactNumber(getSize());
			case Symbols.IS_CACHED : 
					 return new OBoolean(false) {
							public void opAssign(Value val) {}
							@Override
							protected Value getTypeImpl() {
								return this;
							}
					 };	
			 case Symbols.PAGE_SIZE :
				 return new OExactNumber(0) {
						public void opAssign(Value val) {}
						@Override
						protected Value getTypeImpl() {
							return this;
						}
				 };
			 case Symbols.IS_EMPTY :
			 case Symbols.IS_EMPTY2 :
				 return OBoolean.makeBoolean(isEmpty());
			 case Symbols.LOAD_MODE : 
				 return new OString(loadMode) 
				 {
						public void opAssign(Value val) {loadMode=val.castToString();}
						@Override
						protected Value getTypeImpl() {
							return this;
						}
				 };	

			 case Symbols.GET_AS_MAP :
				 return new Value() 
				 {
					 @Override
					 public Value callAsFunction(StackFrame sf, MemberTable args)
							throws PackagedScriptObjectException 
					 {
						 return get();
					 }
					 @Override
					protected Value getTypeImpl() 
					{
						 return this;
					};
				 };
			 case Symbols.GET_OBJECT_COUNT :
				 return new Value() 
				 {
					 @Override
					 public Value callAsFunction(StackFrame sf, MemberTable args)
							throws PackagedScriptObjectException 
					 {
						 return OExactNumber.makeExactNumber(getSize());
					 }
					 @Override
					protected Value getTypeImpl() 
					{
						 return this;
					};
				 };
			 case Symbols.GET_OBJECT :
				 return new Value() 
				 {
					 @Override
					 public Value callAsFunction(StackFrame sf, MemberTable args)
							throws PackagedScriptObjectException 
					 {
						 if (args.length() != 1)
							 return Value.NULL;
						 return elementAt(args.referenceAt(0));
					 }
					 @Override
					protected Value getTypeImpl() 
					{
						 return this;
					};
				 };
			 case Symbols.IS_ACCESS_ENABLED :
				 return new OBoolean(true) 
				 {
						public void opAssign(Value val) {
							boolean enabled = val.castToBoolean();
							if (!enabled)
								System.err.println("LOCAL DBObjReaderContextWrapper : access enabled not implemented. TODO!");							
						}
						@Override
						protected Value getTypeImpl() {
							return this;
						}
				 };	
			 case Symbols.IS_RESULT_MULTIPLE_COLS :
				 return new OBoolean(false/*isData*/) 
				 {
						public void opAssign(Value val) {
							//isData=true;
						}
						@Override
						protected Value getTypeImpl() {
							return this;
						}
				 };	
				 
				 

		}
		return null;
	}
	
	public Value getTypeMember(Value obj,int id) {
		return null;
	}
	//------------------------------------------------------------------------------------------
	@Override
	public Value unhand() {
		return this;
	}
	
	@Override
	public Value bopEquals(Value val) throws PackagedScriptObjectException 
	{
		if (val.bopEquals(Value.NULL).castToBoolean()) 
		{
			return OBoolean.FALSE;
		}
		return super.bopEquals(val);
	}
	@Override
	public Value bopNotEquals(Value val) throws PackagedScriptObjectException {
		if (val.bopEquals(Value.NULL).castToBoolean()) 
		{
			return OBoolean.TRUE;
		}
		return super.bopEquals(val);
	}
	
	
	@Override
	public Value get() {
		return check();
	}

	@Override
	public Object castToJavaObject() throws PackagedScriptObjectException {
		return this;
	}
	
	public Value getMember( int id, boolean exception )
	{
		Value val = resolve(id);
		if( val != null ) {
			return val;
		}
		return super.getMember( id,exception);
	 } 
}
