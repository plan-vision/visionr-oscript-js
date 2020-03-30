package server;
import java.util.Iterator;
import bridge.bridge;
import oscript.data.BuiltinType;
import oscript.data.FunctionValueWrapper;
import oscript.data.JavaBridge;
import oscript.data.OArray;
import oscript.data.OBoolean;
import oscript.data.OExactNumber;
import oscript.data.OInexactInterface;
import oscript.data.OString;
import oscript.data.Symbol;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.data.ValueWrapperTempReference;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.StackFrame;
import oscript.varray.Map;
import oscript.varray.MapKeyValueReference;
import oscript.varray.MapValueCollection;
import oscript.varray.MapValueReference;
import oscript.varray.ScriptIterator;


/**
 * VScript wrapper for DB values.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 18062 $
 * @date 	$LastChangedDate: 2014-11-16 17:23:06 +0200 (So, 16 Nov 2014) $
 * @project VisionR Server 
 */
public final class ValueWrapper extends ValueWrapperTempReference implements OInexactInterface {
		
	protected BasicValueLocator locator;
	protected ObjectWrapper obj;
		
	public BasicValueLocator getLocator() {
		return locator;
	}
	public ObjectWrapper getObject() {
		return obj;
	}
	//--------------------------------------------------
	private Boolean isi=null;
	public boolean proIsI18n() 
	{
		if (isi != null) return isi;
		return isi=bridge.isI18n(obj.odefkey(), locator.pro);
	}
	
	private Boolean ism=null;
	public boolean proIsMultiple() 
	{
		if (ism != null) return ism;
		return ism=bridge.isMultiple(obj.odefkey(), locator.pro);
	}

	private Boolean isr=null;
	public boolean proIsRelation() 
	{
		if (isr != null) return isr;
		return isr=bridge.isRelation(obj.odefkey(), locator.pro);
	}

	private String optionSet=null;
	private static String __null="";
	public String proOptionSet() 
	{
		if (optionSet != null) {
			if (optionSet == __null) return null; 
			return optionSet;
			
		}
		optionSet = bridge.getPropertyOptionSet(obj.odefkey(),locator.pro);
		if (optionSet == null) {optionSet=__null;return null;}
		return optionSet;
	}

	private Boolean isonr=null;
	public boolean proIsRelationNotOption() 
	{
		if (isonr != null) return isonr;
		return isonr=bridge.isRelationNotOption(obj.odefkey(), locator.pro);
	}
	//--------------------------------------------------
	public String toString() {
		Object o = get().castToJavaObject();
		if (o != null)
			return o.toString();
		return "";
	}
	public String castToString() {
		return get().castToString();
	}
	public ValueWrapper(BasicValueLocator locator,ObjectWrapper obj) 
	{
		this.obj=obj;
		this.locator=locator;
	}
	private String _forceSchema;
	//-----------------------------------------------------------------------------------------------------------------------------
	public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) 
	{
		Object[] t = new Object[args.length()];
		for (int i=0;i<args.length();i++)
			t[i]=ValueConvertor.convertToJavaObject(args.referenceAt(i).unhand());
		//------------------------------------------------------------------------------------------------------------------------
		String sh = _forceSchema != null ? _forceSchema : obj.getOD();
		String script = bridge.getVScriptPropertyDefaultValue(sh, locator.pro);
		if (script == null) throw new RuntimeException("ValueWrapper : can not call as functon : no default value script!");				
		String pod = bridge.getParentPropertyObjectDef(sh, locator.pro);
		String pscript = bridge.getVScriptPropertyDefaultValue(pod, locator.pro);
		while (pscript == script) 
		{
			pod = bridge.getParentPropertyObjectDef(pod, locator.pro);
			if (pod == null) {
				pscript = null;
				break;
			}			
		}
		//-------------------------------------------
		Value _super = Value.NULL;
		if (pod != null) {
			ValueWrapper c = new ValueWrapper(locator,obj);
			c._forceSchema = pod;
			_super = c;
		}
		//-------------------------------------------
		Object r = bridge.callScript(script,t,obj,_super);
		return ValueConvertor.convert(r);	
	}
	//-----------------------------------------------------------------------------------------------------------------------------
	private Object getLangIdOrPosValue(int newContextLang,int newContextSortID,Object val) {
		if (val instanceof Number) {
			if (!proIsI18n()) return getValue(newContextSortID,null,((Number)val).intValue(),locator.oldMode);
			// i18n
			if (!locator.oldMode) 
			{
				Object v = bridge.getObjectI18nValue(obj.odefkey(), obj.id(), locator.pro,bridge.currentLang());
				if (v != null)
					return getValue(newContextSortID,bridge.currentLang(),0,false);
				return getValue(newContextSortID,bridge.defaultLang(),0,false);
			} else {
				Object v = bridge.getObjectOldI18nValue(obj.odefkey(), obj.id(), locator.pro,bridge.currentLang());
				if (v != null)
					getValue(newContextSortID,bridge.defaultLang(),0,true);
				return getValue(newContextSortID,bridge.defaultLang(),0,true);
			}
		} 
		return getValue(newContextLang,val.toString(),0,locator.oldMode);
	}

	public Value getRealValue() {
		Value v = get();
		if (!(v instanceof FinalValue))
			return v;
		return v;
	}
	public Value resolve(int symbol) {
		return resolveExp(symbol);
	}	
	private Value resolveExp(int symbol) 
	{
		switch (symbol) 
		{
			case Symbols.VALUE_TYPE: 
				return getRealValue();
			case Symbols.LANGUAGES : 
				if (locator.context == 1 && proIsI18n()) 
				{
					Map m = new Map();					
					String[] langs;
					if (locator.oldMode)
						langs=bridge.getObjectOldI18nValueLanguages(obj.odefkey(), obj.id(), locator.pro);
					else
						langs=bridge.getObjectI18nValueLanguages(obj.odefkey(), obj.id(), locator.pro);
					if (langs != null)
					for (int i=0;i<langs.length;i++)
						m.put(bridge.getObjectByCode("core.lang", langs[i]));
					return new FunctionValueWrapper(m);
				}
				break;
			case Symbols.REMOVE:  
				  return new Value() {
					  
					  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
						  if (args != null && args.length() == 1) {
							  Value e  = ValueWrapper.this.elementAt(args.referenceAt(0));
							  if (e.bopEquals(Value.NULL).castToBoolean()) {
								  return Value.NULL;
							  }
							  return e.getMember("remove").callAsFunction(new Value[0]);
						  }
						  opRemove();
						  return null;
					 }
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				  };
			case Symbols.OLD_TYPE : 
				 return new FunctionValueWrapper(this.obj.getValueWrapper(this.locator.context,this.locator.pro,this.locator.lang,this.locator.pos,true));			 
			case Symbols.OBJECT_TYPE : 
				return obj;
			
			case Symbols.PROPERTY_TYPE : 
				return ObjectWrapper.makeProperty(obj.odefkey(), locator.pro);		
			case Symbols.TO_STRING2 :
			case Symbols.TOSTRING1 :
				return new FunctionValueWrapper(new OString(this.toString()));
		}
		//-----------------------------------------------------------------------------------------------------------------------------------
		// not internation??
		if (locator.context != 4 && (!proIsI18n() || locator.context != 1)) 
		switch (symbol) 
		{
			// MULTIPLE VALUE,POS not selected
			case Symbols.ITERATOR :
				Iterator<Value> myit = new Iterator() 
				{
					private int pos = 0;
					private int size = locator.oldMode ? bridge.getObjectOldValueCount(obj.odefkey(), obj.id(), locator.pro) : bridge.getObjectValueCount(obj.odefkey(), obj.id(), locator.pro);
					private Value crr = null;
	
					@Override
					public boolean hasNext() {
						if (pos >= size) {
							crr=null;
							return false;
						}
						return true;
					}
	
					@Override
					public Object next() {
						if (pos >= size) {
							crr=null;
							return null;
						}
						return crr=obj.getValueWrapper(4,locator.pro,locator.lang,pos++,locator.oldMode);
					}
	
					@Override
					public void remove() {
						crr.getMember("remove").callAsFunction(new Value[0]);
						pos--;
						size = locator.oldMode ? bridge.getObjectOldValueCount(obj.odefkey(), obj.id(), locator.pro) : bridge.getObjectValueCount(obj.odefkey(), obj.id(), locator.pro);
					}
				};
				return new FunctionValueWrapper(new IteratedValueWrapper(myit));
			case Symbols.CONTAINS : 
				return new Value() {
					public Value callAsFunction(StackFrame sf, oscript.util.MemberTable args) {
						if (args.length() != 1)
							throw new RuntimeException("Can not execute contains(object) with |parameters|<>1!");
						Object o = args.referenceAt(0).castToJavaObject();
						if (o instanceof ObjectWrapper) {
							ObjectWrapper ow = ((ObjectWrapper)o);
							int pos = locator.oldMode ? 
									bridge.getObjectOldValuePositionRel(obj.odefkey(),obj.id(),locator.pro,ow.id())
							  	  : bridge.getObjectValuePositionRel(obj.odefkey(),obj.id(),locator.pro,ow.id());
							return OBoolean.makeBoolean(pos >= 0);
						}
						if (o instanceof MapValueCollection) {
							o = new Map((MapValueCollection) o);
						} else if (o instanceof OArray) {
							Map x = new Map();
							OArray a = (OArray) o;
							for (int i = 0; i < a.length(); i++) {
								Object to = a.elementAt(i).unhand();
								if (to instanceof ValueWrapper) {
									ValueWrapper vp = (ValueWrapper) to;
									to = vp.get();
								}
								x.put(to);
							}
							o = x;
						}
						if (o instanceof Map) {
							Map map = (Map) o;
							ScriptIterator it = map.iterator();
							boolean result = false;
							while (it.hasNext() && !result) {
								Object elm = map.elementAt((Value) it.next());
								if (elm instanceof MapValueReference) {
									elm = ((MapValueReference) elm).castToJavaObject();
								}
								if (elm instanceof ObjectWrapper) 
								{
									ObjectWrapper ow = (ObjectWrapper)elm;
									int pos = locator.oldMode ? 
											bridge.getObjectOldValuePositionRel(obj.odefkey(),obj.id(),locator.pro,ow.id()):
											bridge.getObjectValuePositionRel(obj.odefkey(),obj.id(),locator.pro,ow.id());
									result = result || (pos >= 0);
									continue;
								}
							}
							return OBoolean.makeBoolean(result);
						}						
						if (proIsI18n()) {
							Value c=castToSimpleValue();
							return c.bopEquals(args.referenceAt(0).unhand());
						}
						if (locator.oldMode) 
							return OBoolean.makeBoolean(bridge.getObjectOldValuePosition(obj.odefkey(),obj.id(),locator.pro,o) >= 0);
						else
							return OBoolean.makeBoolean(bridge.getObjectValuePosition(obj.odefkey(),obj.id(),locator.pro,o) >= 0);
					}
	
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				};
			case Symbols.FIRST : 
				int s = locator.oldMode ? bridge.getObjectOldValueCount(obj.odefkey(), obj.id(), locator.pro) : bridge.getObjectValueCount(obj.odefkey(), obj.id(), locator.pro);
				if (s == 0) return null;
				return new FunctionValueWrapper(this.obj.getValueWrapper(4,this.locator.pro,locator.lang,0,locator.oldMode));
			case Symbols.LAST : 
				s = locator.oldMode ? bridge.getObjectOldValueCount(obj.odefkey(), obj.id(), locator.pro) : bridge.getObjectValueCount(obj.odefkey(), obj.id(), locator.pro);
				if (s == 0) return null;
				return new FunctionValueWrapper(this.obj.getValueWrapper(4,this.locator.pro,locator.lang,s-1,locator.oldMode));
			case Symbols.SIZE : 
				return new FunctionValueWrapper(OExactNumber.makeExactNumber((
						locator.oldMode ? bridge.getObjectOldValueCount(obj.odefkey(), obj.id(), locator.pro) : bridge.getObjectValueCount(obj.odefkey(), obj.id(), locator.pro)
					)));
			case Symbols.IS_EMPTY :
			case Symbols.IS_EMPTY2 :  
				return new FunctionValueWrapper(OBoolean.makeBoolean((
						(locator.oldMode ? bridge.getObjectOldValueCount(obj.odefkey(), obj.id(), locator.pro) : bridge.getObjectValueCount(obj.odefkey(), obj.id(), locator.pro)) == 0
					)));
		}
		//----------------------------------------------------------------------------------------
		if (locator.context == 4) 
		switch (symbol) 
		{
			case Symbols.IS_EMPTY :
			case Symbols.IS_EMPTY2 :  
			{	
				boolean res;
				if (locator.pos < 0)
					res=false;
				else {
					Object v;
					if (proIsI18n())
						v = this.locator.oldMode ? bridge.getObjectOldI18nValue(obj.odefkey(), obj.id(), locator.pro,locator.lang) : bridge.getObjectI18nValue(obj.odefkey(), obj.id(), locator.pro,locator.lang);
					else
						v = this.locator.oldMode ? bridge.getObjectOldValue(obj.odefkey(), obj.id(), locator.pro) : bridge.getObjectValue(obj.odefkey(), obj.id(), locator.pro);
					int ss = v == null ? 0 : 1;
					if (locator.pos < ss)
						res=false;
					else 
						res=true;
				}
				return new FunctionValueWrapper(OBoolean.makeBoolean(res));
			}
			case Symbols.NEXT :
				if (locator.pos >= (locator.oldMode ?
						bridge.getObjectOldValueCount(obj.odefkey(), obj.id(), locator.pro) :
						bridge.getObjectValueCount(obj.odefkey(), obj.id(), locator.pro)
					)) 
					return FunctionValueWrapper.NULL;
				return new FunctionValueWrapper(obj.getValueWrapper(this.locator.context,this.locator.pro,locator.lang,locator.pos+1,locator.oldMode));
			case Symbols.PREV : 
				if (locator.pos == 0) 
					return FunctionValueWrapper.NULL;
				return new FunctionValueWrapper(obj.getValueWrapper(this.locator.context,this.locator.pro,locator.lang,locator.pos-1,locator.oldMode));
			case Symbols.HAS_NEXT1 :
			case Symbols.HAS_NEXT2 : 
				if (locator.pos >= (locator.oldMode ? 
							bridge.getObjectOldValueCount(obj.odefkey(), obj.id(), locator.pro) : 
							bridge.getObjectValueCount(obj.odefkey(), obj.id(), locator.pro)							
						))
					return OBoolean.FALSE;
				return OBoolean.TRUE;
			case Symbols.HAS_PREV1 :
			case Symbols.HAS_PREV2 : 
				if (locator.pos == 0)
					return OBoolean.FALSE;
				return OBoolean.TRUE;
			case Symbols.LANG_TYPE : 
				if (containsFinalValue())
					return new FunctionValueWrapper(
							(ObjectWrapper)bridge.getObjectByCode("core.lang",locator.lang)						
						);
				if (!locator.oldMode) 
				{
					String k = bridge.getObjectI18nValue(obj.odefkey(),obj.id(), locator.pro, locator.lang);
					if (k != null)
						return new FunctionValueWrapper((ObjectWrapper)bridge.getObjectByCode("core.lang",bridge.currentLang()));
					return new FunctionValueWrapper((ObjectWrapper)bridge.getObjectByCode("core.lang",bridge.defaultLang())
					);
				} else {
					String k = bridge.getObjectOldI18nValue(obj.odefkey(),obj.id(), locator.pro, locator.lang);
					if (k != null)
						return new FunctionValueWrapper((ObjectWrapper)bridge.getObjectByCode("core.lang",bridge.currentLang()));
					return new FunctionValueWrapper((ObjectWrapper)bridge.getObjectByCode("core.lang",bridge.defaultLang())
					);
				}				
		}
		//------------------------------------------------------------------------------------------------------------------------------------------------------
		String s;
		if ((locator.context == 1 || locator.context == 3) && proIsI18n() && ( _isLang(s=Symbol.getSymbol(symbol).castToString()) ) ) 
		{
			Value t = get(s,locator.oldMode);
			return t;
		}	
		return null;
	}
		
	private boolean _isLang(String s) {
		if (s.equals("default") || s.length() == 5 && s.charAt(2) == '-')
			return true;
		return false;
	}

	public Value elementAt( Value idx ) 
    {		
			Object o = idx.castToJavaObject();
			if (locator.context == 1 || locator.context == 2) {
				if (o instanceof Number) {
					return get(o,locator.oldMode);
				} 
			}
			boolean i18n = proIsI18n();
			if (i18n) 
			{
				if (o instanceof String) 
				{
					String s = (String)o;
					// simple detect lang
					if (_isLang(s)) {
						  if (s.equals("all")) 
							  return this;
						  Value t = get(s,locator.oldMode);
						  return t;
					}
				}
			}
		
			if (proIsMultiple())  {
				if ((!i18n && locator.context == 1) || (i18n && locator.context == 2)) 
				{
					if (proIsRelation()) {
						if (o instanceof String) {
							Value it = getMember("iterator");
							int pos=0;
							while (it.getMember("has_next").castToBoolean()) 
							{
								Object oO=it.getMember("next").castToJavaObject();
								ObjectWrapper n = (ObjectWrapper)oO;
								String s = n.code();
								if (s != null && s.equals(o)) 
									return obj.getValueWrapper(4,locator.pro,locator.lang,pos,locator.oldMode);
								pos++;
							}
							return new Map().elementAt(idx);
						} 
					}
					// MULTIPLE VALUE,POS not selected
					int pos;
					
					if (o instanceof ObjectWrapper) {
						ObjectWrapper ow = (ObjectWrapper)o;						
						if (!locator.oldMode)
							pos = bridge.getObjectValuePositionRel(obj.odefkey(), obj.id(), locator.pro, ow.id());
						else
							pos = bridge.getObjectOldValuePositionRel(obj.odefkey(), obj.id(), locator.pro, ow.id());
					} else {
						if (!locator.oldMode)
							pos = bridge.getObjectValuePosition(obj.odefkey(), obj.id(), locator.pro, o);
						else
							pos = bridge.getObjectOldValuePosition(obj.odefkey(), obj.id(), locator.pro, o);
					}
					if (pos != -1) {
					// go to pos context
						return obj.getValueWrapper(4,locator.pro,locator.lang,pos,locator.oldMode);
					} else {
						// return empty value wrapper
						return new FinalValue(null,locator.lang,0,this);
					}
				}
			} 			
			Value v = get();
			if (v instanceof FinalValue && ((FinalValue)v).getOValue() == Value.NULL) 
				return Value.NULL;
			return v.elementAt(idx);
    }

	

	public Value get(Object key,boolean oldMode) 
	{
		if (locator.context == 1) { // Choose lang->2 or pos->3 of the default language
			 Object t=getLangIdOrPosValue(2,proIsI18n() ? 3 : 4,key);
			 if (t instanceof Value)
				  return (Value)t;
			 return JavaBridge.convertToScriptObject(t);			
	  } else if (locator.context == 2) { // Choose pos->value (could be array, so pos)
		  	if (!(key instanceof Number))
		  		throw new RuntimeException("pos expected but not integer value found");	  	
			Value t=obj.getValueWrapper(4,locator.pro,locator.lang,((Number)key).intValue(),oldMode);
			return t;
		} else if (locator.context == 3) { // Choose lang->value (could be array, so pos)
		  	if (!(key instanceof String))
		  		throw new RuntimeException("lang expected but not string value found");	  	
			Value t = obj.getValueWrapper(4,locator.pro,(String)key,locator.pos,oldMode);
			return t;
		}
		// context == 4 -> value
  		throw new RuntimeException("Unknown value context (BUG)");	  	
	}

	 //-----------------------------------------------------------------------------------------------------------------------------
	  public Value getMember( int id, boolean exception )
	  {
		  try {
			    Value val = resolve(id);
			    if( val != null )
			      return val;
			    val = get();
			    
			    String optionSet = proOptionSet();
			    if (optionSet != null) 
			    {
			    	if (val instanceof FinalValue) {
			    		FinalValue fv = (FinalValue)val;
			    		Value vl = fv.getOValue();
			    		if (vl instanceof OBoolean) 
			    		{
			    			if (id != Symbols.CAST_TO_BOOLEAN) {
			    				ObjectWrapper obj;
				    			if (vl.castToBoolean())
				    				obj = ObjectWrapper.makeObject("core.option", 1);
				    			else 
				    				obj = ObjectWrapper.makeObject("core.option", 0);
				    			return obj.getMember(id,exception);
			    			}
			    		}
			    	}
			    }
			    //--------------------------------------------------------------------------------------------------------
				if (val != null && val != Value.NULL)
					return val.getMember(id,exception);
				return super.getMember( id,exception);
		  } catch (Throwable e) {
			  System.err.println(">> exception resolving value "+obj.getOD()+"."+locator.pro+"@"+locator.context+"!");
			  throw e;
		  }
	  }
		
	final public boolean containsFinalValue() {
		return (!proIsI18n() || locator.context != 1);
	}
	
	public Value bopLeftShift(Value val) {
		if (!proIsMultiple())
			throw new RuntimeException(
					"Insert operation is only for multiple properties possible (" + locator.pro + ")");
		updateVal(val, false, true);
		return this;
	}

	public void opAssign(Value val) {
		 updateVal(val,true,false);
	}
	 
	public boolean equals(Object val) 
	{
		if (val instanceof Value) 
		{
			Value v=(Value)val;
			return bopEquals(v).castToBoolean();
		}
		return get().equals(val);
	}
	
	public Value bopEquals(Value val) 
	{
		if (val == Value.NULL) {
			return this.getMember(Symbols.IS_EMPTY);
		}
		if (locator.context == 1 && this.proIsI18n()) 
		{
			 Value vval = val.unhand();
			 if (vval instanceof ValueWrapper) 
			 { 
				ValueWrapper w = (ValueWrapper)vval;
				if (w.locator.context == 1 && w.proIsI18n()) 
				{
					String langs1[] = this.locator.oldMode ? bridge.getObjectOldI18nValueLanguages(obj.odefkey(),obj.id(), locator.pro) : bridge.getObjectI18nValueLanguages(obj.odefkey(),obj.id(), locator.pro); 
					String langs2[] = w.locator.oldMode ? bridge.getObjectOldI18nValueLanguages(w.obj.odefkey(),w.obj.id(), w.locator.pro) : bridge.getObjectI18nValueLanguages(w.obj.odefkey(),w.obj.id(), w.locator.pro); 
					if ((langs1 == null) != (langs2 == null))
						return OBoolean.FALSE;
					if (langs1 == null && langs2 == null)
						return OBoolean.TRUE;
					if (langs1.length != langs2.length)
						return OBoolean.FALSE;
					for (int i=0;i<langs1.length;i++) 
					{
						if (!langs1[i].equals(langs2[i])) {
							return OBoolean.FALSE;
						}
						
						Object o1 = this.locator.oldMode ? bridge.getObjectOldI18nValue(obj.odefkey(), obj.id(), locator.pro, langs1[i]) : bridge.getObjectI18nValue(obj.odefkey(), obj.id(), locator.pro, langs1[i]);
						Object o2 = w.locator.oldMode ? bridge.getObjectOldI18nValue(w.obj.odefkey(), w.obj.id(), w.locator.pro, langs1[i]) : bridge.getObjectI18nValue(w.obj.odefkey(), w.obj.id(), w.locator.pro, langs1[i]);
						if (o1 != o2) 
						{
							if (o1 == null || o2 == null || !o1.equals(o2))
								return OBoolean.FALSE;
						}
					}
					return OBoolean.TRUE;
				}
			}
		}
		Object a = this.castToJavaObject();
		Object b = val.castToJavaObject();
		if (a == b)
			return OBoolean.TRUE;
		if (a == null) {
			if (b == null)
				return OBoolean.TRUE;
			return OBoolean.FALSE;
		} 
		if (b == null) {
			return OBoolean.FALSE;
		}
		if (a instanceof Number && b instanceof Number) 
		{
			if (a instanceof Double && !(b instanceof Double))  	
				b=((Number)b).doubleValue();
			else if (b instanceof Double && !(a instanceof Double)) {
				a=((Number)a).doubleValue();
			}
		}
		return JavaBridge.convertToScriptObject(a.equals(b));
	}

	public Value bopNotEquals(Value val) {
		if (val == Value.NULL) {
			boolean isEmpty = this.getMember(Symbols.IS_EMPTY).castToBoolean();
			return OBoolean.makeBoolean(!isEmpty);
		}
		
		if (locator.context == 1 && this.proIsI18n()) 
		{
			 Value vval = val.unhand();
			 if (vval instanceof ValueWrapper) 
			 { 
				ValueWrapper w = (ValueWrapper)vval;
				if (w.locator.context == 1 && w.proIsI18n()) 
				{
					String langs1[] = this.locator.oldMode ? bridge.getObjectOldI18nValueLanguages(obj.odefkey(),obj.id(), locator.pro) : bridge.getObjectI18nValueLanguages(obj.odefkey(),obj.id(), locator.pro); 
					String langs2[] = w.locator.oldMode ? bridge.getObjectOldI18nValueLanguages(w.obj.odefkey(),w.obj.id(), w.locator.pro) : bridge.getObjectI18nValueLanguages(w.obj.odefkey(),w.obj.id(), w.locator.pro); 
					if ((langs1 == null) != (langs2 == null))
						return OBoolean.TRUE;
					if (langs1 == null && langs2 == null)
						return OBoolean.FALSE;
					if (langs1.length != langs2.length)
						return OBoolean.TRUE;
					for (int i=0;i<langs1.length;i++) 
					{
						if (!langs1[i].equals(langs2[i])) {
							return OBoolean.TRUE;
						}
						Object o1 = this.locator.oldMode ? bridge.getObjectOldI18nValue(obj.odefkey(), obj.id(), locator.pro, langs1[i]) : bridge.getObjectI18nValue(obj.odefkey(), obj.id(), locator.pro, langs1[i]);
						Object o2 = w.locator.oldMode ? bridge.getObjectOldI18nValue(w.obj.odefkey(), w.obj.id(), w.locator.pro, langs1[i]) : bridge.getObjectI18nValue(w.obj.odefkey(), w.obj.id(), w.locator.pro, langs1[i]);
						if (o1 != o2) 
						{
							if (o1 == null || o2 == null || !o1.equals(o2))
								return OBoolean.TRUE;
						}
					}
					return OBoolean.FALSE;
				}
			}
		}

		Object a = this.castToJavaObject();
		Object b = val.castToJavaObject();
		if( a == b )
			return OBoolean.FALSE;
		if (a == null) {
			if (b == null)
				return OBoolean.FALSE;
			return OBoolean.TRUE;
		} 
		if (b == null) {
			return OBoolean.TRUE;
		}
		if (a instanceof Number && b instanceof Number) 
		{
			if (a instanceof Double && !(b instanceof Double))  	
				b=((Number)b).doubleValue();
			else if (b instanceof Double && !(a instanceof Double)) {
				a=((Number)a).doubleValue();
			}
		}
		return JavaBridge.convertToScriptObject(!a.equals(b));
	}
	
	public Value bopInstanceOf(Value _val)  {
		Value val=_val.unhand();
		if (val instanceof BuiltinType) 
		{
			if (proIsMultiple() && locator.context != 4)
				return OBoolean.FALSE;
		}
		return get().bopInstanceOf(val);
	}

	@Override
	public boolean castToBooleanSoft() throws PackagedScriptObjectException 
	{
		if (proIsMultiple()) 
		{
			return bopNotEquals(Value.NULL).castToBoolean();
		}
		return super.castToBooleanSoft();
	}

	@Override
	public boolean isInexactNumber() {
		return bridge.isDataTypeDouble(obj.odefkey(), locator.pro);
	}

	private Value getValue(int newContext,String lang,int pos,boolean oldMode)
	{
		if (!proIsMultiple()) {
			if (!proIsI18n()) {
				Value o = obj.getValueWrapper(newContext,locator.pro,null,0,oldMode);				
				return o;
			} else {
				Value w = obj.getValueWrapper(newContext,locator.pro,lang,0,oldMode);
				return w;
			}
		}
		if (!proIsI18n()) {
			Value o = obj.getValueWrapper(newContext,locator.pro,null,pos,oldMode);
			return o;
		} else {
			Value w = obj.getValueWrapper(newContext,locator.pro,lang,pos,oldMode);
			return w;
		}
	}
	//--------------------------------------------------------------------------
	// CACHED DATA
	//--------------------------------------------------------------------------
	@Override
	public Value castToSimpleValue() 
	{
		Value o=null;
		if (containsFinalValue())
		{
			if (!proIsMultiple()) 
			{
				if (proIsI18n()) 
				{
					if (!locator.oldMode)
						o = ValueConvertor.convert(bridge.getObjectI18nValue(obj.odefkey(), obj.id(), locator.pro,locator.lang));
					else
						o = ValueConvertor.convert(bridge.getObjectOldI18nValue(obj.odefkey(), obj.id(), locator.pro,locator.lang));
				} else {
					if (!locator.oldMode)
						o = ValueConvertor.convert(bridge.getObjectValue(obj.odefkey(), obj.id(), locator.pro));
					else
						o = ValueConvertor.convert(bridge.getObjectOldValue(obj.odefkey(), obj.id(), locator.pro));
				}
			} else {
				if (locator.context == 4 || locator.context == 3) 
				{
					if (proIsI18n()) 
					{
						if (!locator.oldMode)
							o=ValueConvertor.convert(bridge.getObjectI18nValue(obj.odefkey(), obj.id(), locator.pro, locator.lang));
						else
							o=ValueConvertor.convert(bridge.getObjectOldI18nValue(obj.odefkey(), obj.id(), locator.pro, locator.lang));
					} else {
						if (!locator.oldMode)
							o=ValueConvertor.convert(bridge.getObjectValuePos(obj.odefkey(), obj.id(), locator.pro, locator.pos));
						else
							o=ValueConvertor.convert(bridge.getObjectOldValuePos(obj.odefkey(), obj.id(), locator.pro, locator.pos));
					}
				} else {
					// NO SIMPLE VALUE->MULTIPLE
					o=Value.NULL;
				}
			}
		} else {
			// i18n 
			if (proIsMultiple())
				return null;
			if (!locator.oldMode) 
			{
				if (bridge.getObjectI18nValue(obj.odefkey(), obj.id(), locator.pro,locator.lang) != null)
					o=getValue(2,bridge.currentLang(),0,false);
				else
					o=getValue(2,bridge.defaultLang(),0,false);
				if (o != null) 
				{
					o = o.unhand();
					if (o instanceof ValueWrapperTempReference) {
						o=((ValueWrapperTempReference) o).castToSimpleValue();
					}
				}
			} else {
				if (bridge.getObjectOldI18nValue(obj.odefkey(), obj.id(), locator.pro,locator.lang) != null)
					o=getValue(2,bridge.currentLang(),0,true);
				else
					o=getValue(2,bridge.defaultLang(),0,true);
				if (o != null) {
					o=o.unhand();
					if (o instanceof ValueWrapperTempReference) {
						o=((ValueWrapperTempReference) o).castToSimpleValue();
					}
				}
			}
		}
		return o;
	}

	public Value get() 
	{
		Value o=null;
		if (containsFinalValue())
		{
			if (!proIsMultiple()) {
				if (proIsI18n()) 
				{
					if (!locator.oldMode)
						o = new FinalValue(bridge.getObjectI18nValue(obj.odefkey(), obj.id(), locator.pro,locator.lang),locator.lang,0,this);
					else
						o = new FinalValue(bridge.getObjectOldI18nValue(obj.odefkey(), obj.id(), locator.pro,locator.lang),locator.lang,0,this);
				} else {
					if (!locator.oldMode)
						o = new FinalValue(bridge.getObjectValue(obj.odefkey(), obj.id(), locator.pro),locator.lang,0,this);
					else
						o = new FinalValue(bridge.getObjectOldValue(obj.odefkey(), obj.id(), locator.pro),locator.lang,0,this);
				}
			} else {
				if (locator.context == 4 || locator.context == 3) 
				{
					if (proIsI18n()) {
						if (!locator.oldMode)
							o=new FinalValue(bridge.getObjectI18nValue(obj.odefkey(), obj.id(), locator.pro, locator.lang),locator.lang,0,this);
						else
							o=new FinalValue(bridge.getObjectOldI18nValue(obj.odefkey(), obj.id(), locator.pro, locator.lang),locator.lang,0,this);
					} else {
						if (!locator.oldMode)
							o=new FinalValue(bridge.getObjectValuePos(obj.odefkey(), obj.id(), locator.pro, locator.pos),null,locator.pos,this);
						else
							o=new FinalValue(bridge.getObjectOldValuePos(obj.odefkey(), obj.id(), locator.pro, locator.pos),null,locator.pos,this);
					}
				} else {
					// MULTIPLE
					if (!locator.oldMode)
						o=new FinalValue(bridge.getObjectValue(obj.odefkey(), obj.id(), locator.pro),null,null,this);
					else
						o=new FinalValue(bridge.getObjectOldValue(obj.odefkey(), obj.id(), locator.pro),null,null,this);
				}
			}
		} else {
			if (!locator.oldMode) 
			{
				if (bridge.getObjectI18nValue(obj.odefkey(), obj.id(), locator.pro,bridge.currentLang()) != null)
					o=getValue(2,bridge.currentLang(),0,false);
				else
					o=getValue(2,bridge.defaultLang(),0,false);
			} else {
				if (bridge.getObjectOldI18nValue(obj.odefkey(), obj.id(), locator.pro,bridge.currentLang()) != null)
					o=getValue(2,bridge.currentLang(),0,true);
				else
					o=getValue(2,bridge.defaultLang(),0,true);
			}
		}		
		return o;
	}
	
	
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void opRemove() {
		switch (locator.context) {
		case 1:
			// whole value
			bridge.deleteObjectValue(obj.odefkey(), obj.id(),locator.pro);
			break;
		case 2:
			// lang
			bridge.deleteObjectValueLang(obj.odefkey(), obj.id(),locator.pro,locator.lang);
			break;
		case 3:
		case 4:
			// lang / pos
			if (proIsI18n())
				bridge.deleteObjectValueLang(obj.odefkey(), obj.id(),locator.pro,locator.lang);
			else
				bridge.deleteObjectValuePos(obj.odefkey(), obj.id(),locator.pro,locator.pos);
			break;
		default:
			throw new RuntimeException("ValueWrapper : can not assign value to object");

		}
	}
	//------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void updateVal(Value val, boolean remove, boolean insert) {
		if (locator.context == 0)
			throw new RuntimeException("Can not assign value to object");
		if (val == null)
			throw new RuntimeException("Error updating value with val=null");
		//---------------------------------------------------------------------------
		//final DBProperty lpro = locator.pro;
		val = val.unhand();
		boolean valIsValWrap = val instanceof ValueWrapper;
		if (locator.context == 1 || locator.context == 2) 
		{ 
			// whole value / 2[lang]? | MERGED 2 cases | TODO?  
			if (valIsValWrap) 
			{
				ValueWrapper v = (ValueWrapper) val;
				if (proIsI18n()) 
				{
					if (v.proIsI18n()) 
						bridge.copyObjectValue(obj.odefkey(),obj.id(),locator.pro,v.obj.odefkey(),v.obj.id(),v.locator.pro);
					else { 
						if (remove) bridge.deleteObjectValue(obj.odefkey(), obj.id(), locator.pro);
						Object k = bridge.getObjectValue(v.obj.odefkey(), v.obj.id(), v.locator.pro);
						if (k == null)
							bridge.deleteObjectValue(obj.odefkey(), obj.id(), locator.pro);
						else
							bridge.setObjectValueLang(obj.odefkey(),obj.id(),locator.pro,k == null ? null : k.toString(),locator.lang == null ? bridge.currentLang() : locator.lang);
					}
					// I18N copy 
					return;
				} else if (v.locator.context == 1 && v.proIsMultiple()) {
					// TODO
					if (remove) {
						// MULTIPLE COPY
						bridge.copyObjectValue(obj.odefkey(),obj.id(),locator.pro,v.obj.odefkey(),v.obj.id(),v.locator.pro);
						return; 
					} // END IF REMOVE
				}
			}
			String lang = locator.lang;
			if (lang == null && proIsI18n())
				lang = bridge.currentLang();
			//Object o = ValueConvertor.convertToJavaObject(val.castToJavaObject());
			if (proIsI18n()) 
			{
				Object o = ValueConvertor.convertToJavaObject(val.castToJavaObject());
				String vv = locator.oldMode ? bridge.getObjectOldI18nValue(obj.odefkey(), obj.id(), locator.pro, lang) : bridge.getObjectI18nValue(obj.odefkey(), obj.id(), locator.pro, lang);
				if (o == null && vv == null) return;
				if (o != null && vv.equals(o.toString())) return;
				bridge.setObjectValueLang(obj.odefkey(), obj.id(),locator.pro,val.toString(),lang);
			} else if (!proIsMultiple()) {
				Value vv = ValueConvertor.convert(locator.oldMode ? bridge.getObjectValue(obj.odefkey(), obj.id(), locator.pro) : bridge.getObjectValue(obj.odefkey(), obj.id(), locator.pro));
				if (vv.bopEquals(val).castToBoolean()) return;	// SAME ? SKIP
				Object jv = ValueConvertor.convertToJavaObject(val);
				if (!(jv instanceof ObjectWrapper))
					bridge.setObjectValue(obj.odefkey(), obj.id(),locator.pro,jv);
				else {
					ObjectWrapper jvo = (ObjectWrapper)jv;
					bridge.setObjectValueRel(obj.odefkey(), obj.id(),locator.pro,jvo.getOD(),jvo.getID());
				}
			} else {
				// PRO IS MULTIPLE
				Object o = ValueConvertor.convertToJavaObject(val.castToJavaObject());
				if (o instanceof Object[]) {
					o = new Map((Object[]) o);
				}
				if (o instanceof MapValueCollection) {
					o = new Map((MapValueCollection) o);
				} else if (o instanceof OArray) {
					Map x = new Map();
					OArray a = (OArray) o;
					for (int i = 0; i < a.length(); i++) {
						Object to = a.elementAt(i).unhand();
						if (to instanceof ValueWrapper) {
							ValueWrapper vp = (ValueWrapper) to;
							to = vp.get();
						}
						x.put(to);
					}
					o = x;
				}
				// ------------------------------------------------------------------------------------------------------------------
				if (remove && bridge.getObjectValueCount(obj.odefkey(), obj.id(), locator.pro) > 0)
					bridge.deleteObjectValue(obj.odefkey(),obj.id(),locator.pro);
				// --------------------------------------------------------------------------------------
				if (o instanceof Map) {
					Map m = (Map) o;
					Iterator it = m.iterator();
					while (it.hasNext()) 
					{
						Object ooo = it.next();
						MapKeyValueReference mr = (MapKeyValueReference) ooo;
						Object vax = mr.getValue();
						Object key = mr.getKey();
						if (!(key instanceof Number)) throw new RuntimeException("ValueWrapper : trying to insert MAP for multiple property with key not integer (pos)");
						ObjectWrapper ow=null;
						if (vax instanceof ObjectWrapper)
							ow=(ObjectWrapper)vax;
						//-------------------------------------
						if (!insert || remove ||  
								(
										ow != null ? 
											bridge.getObjectValuePositionRel(obj.odefkey(), obj.id(), locator.pro, ow.id()) :
											bridge.getObjectValuePosition(obj.odefkey(), obj.id(), locator.pro, vax) 
								)
							< 0) 
						{
							// NEW VALUE
							if (ow != null)
								bridge.pushObjectValueRel(obj.odefkey(),obj.id(),locator.pro,ow.odefkey(),ow.id());
							else 
								bridge.pushObjectValue(obj.odefkey(),obj.id(),locator.pro,vax);
						}
					}
				} else {						
					ObjectWrapper ow=null;
					if (o instanceof ObjectWrapper)
						ow=(ObjectWrapper)o;
					if (!insert || remove ||  
							(
									ow != null ? 
										bridge.getObjectValuePositionRel(obj.odefkey(), obj.id(), locator.pro, ow.id()) :
										bridge.getObjectValuePosition(obj.odefkey(), obj.id(), locator.pro, o) 
							)
						< 0) 
					{
						if (ow != null)
							bridge.pushObjectValueRel(obj.odefkey(),obj.id(),locator.pro,ow.odefkey(),ow.id());
						else 
							bridge.pushObjectValue(obj.odefkey(),obj.id(),locator.pro,o);
					}
				}
			}
		} else if (locator.context == 3) {
			// multiple -> pos
			if (proIsI18n()) throw new RuntimeException("By updating i18n property select language first!");
			Object o = ValueConvertor.convertToJavaObject(val.castToJavaObject());
			if (!(o instanceof ObjectWrapper))
				bridge.setObjectValuePos(obj.odefkey(), obj.id(), locator.pro, locator.pos, o);
			else {
				ObjectWrapper oo = (ObjectWrapper)o;
				bridge.setObjectValuePosRel(obj.odefkey(), obj.id(), locator.pro, locator.pos,oo.getOD(),oo.getID());
			}
		} else if (locator.context == 4) {
			// i18n lang
			Object o = ValueConvertor.convertToJavaObject(val.castToJavaObject());
			bridge.setObjectValueLang(obj.odefkey(), obj.id(), locator.pro, o == null ? null : o.toString(),locator.lang);
		} else
			throw new RuntimeException("Unknown context (BUG!)");
	}
}
  