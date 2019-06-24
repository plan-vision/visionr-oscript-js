package server;

import bridge.bridge;
import oscript.data.FunctionValueWrapper;
import oscript.data.OBoolean;
import oscript.data.OExactNumber;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.data.ValueWrapperTempReference;
import oscript.varray.IteratorWrapper;

/**
 * TODO
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 17460 $
 * @date 	$LastChangedDate: 2014-06-23 10:39:09 +0300 (Mo, 23 Jun 2014) $
 * @project VisionR Server 
 */
public class FinalValue extends ValueWrapperTempReference {
	
	private Value oval = Value.NULL; 
	private String lang;
	private Integer pos;
	
	public Value getOValue() {
		return oval;
	}
	public void setOValue(Value val) {
		oval=val;
	}
	
	public FinalValue(Object val,String lang,Integer pos,ValueWrapper wr) {
		super();	

		this.oval=ValueConvertor.convert(val);		
		if (wr != null && this.oval instanceof ODateTimeValue) {
			this.oval = ((ODateTimeValue)oval).getValueWrapperCopy(wr);			
		} else if (oval == null) {
			this.oval = Value.NULL; 
		}
		
		this.lang=lang;
		this.pos=pos;
	}
	
	
	public Value get() {
		return oval;
	}
	
	public Value resolve(int symbol) 
	{		
		if (symbol == Symbols.LANG_TYPE) {
			return (ObjectWrapper)bridge.getObjectByCode("core.lang",lang);
		}
		if (symbol == Symbols.POS_TYPE && pos != null) {
			return OExactNumber.makeExactNumber(pos);
		}
		if (symbol == Symbols.SIZE && pos != null) {
			return OExactNumber.makeExactNumber((oval == Value.NULL)? 0 : 1);
		}
		if (symbol == Symbols.ITERATOR && oval == Value.NULL) {
			return new FunctionValueWrapper(new IteratorWrapper(IteratorWrapper.getEmptyIterator()));
		}
		if (symbol == Symbols.IS_EMPTY || symbol == Symbols.IS_EMPTY2) {
			return OBoolean.makeBoolean(oval == null || oval == Value.NULL);
		}
 		return null;
	}
	
	public Value elementAt(Value idx)
	{
	   Object x = castToSimpleValue().castToJavaObject();
	   if (x instanceof Boolean) {
		   ObjectWrapper w;
		   if ((Boolean)x) 
		   {
			   w = ObjectWrapper.makeObject("core.option", 1);
		   } else {
			   w = ObjectWrapper.makeObject("core.option", 0);
		   }
		   Value val = w.elementAt(idx);
		   if (val != null)
			   return val;
	   }
	   return super.elementAt(idx);
	}
	public Value getMember( int id, boolean exception )
	 {
	   Value val = resolve(id);
	   if( val != null )
	     return val;
	   
	   Object x = castToSimpleValue().castToJavaObject();
	   if (x instanceof Boolean) {
		   ObjectWrapper w;
		   if ((Boolean)x) 
		   {
			   w = ObjectWrapper.makeObject("core.option", 1);
		   } else {
			   w = ObjectWrapper.makeObject("core.option", 0);
		   }
		   val = w.getMember(id,false);
		   if (val != null)
			   return val;
	   }
	   return this.oval.getMember(id,exception);
	 }

	 @Override
	 protected Value getTypeImpl() {
		return this;
	 }	
	 
	
	 public Value castToSimpleValue() {
		 if (oval == null)
				return Value.NULL;
		Value oval = this.oval.unhand();
		if (oval instanceof ValueWrapperTempReference)
			return ((ValueWrapperTempReference)oval).castToSimpleValue();
		return oval;
	}
}
