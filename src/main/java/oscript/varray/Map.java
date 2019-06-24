package oscript.varray;

import java.util.Collection;
import java.util.Iterator;
import java.util.MissingResourceException;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Vector;

import oscript.OscriptInterpreter;
import oscript.data.AbstractReference;
import oscript.data.Function;
import oscript.data.FunctionValueWrapper;
import oscript.data.OArray;
import oscript.data.OBoolean;
import oscript.data.OExactNumber;
import oscript.data.OString;
import oscript.data.Symbol;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.MemberTable;
import oscript.util.StackFrame;
import server.ObjectWrapper;
import server.ValueConvertor;

/**
 * Implementation of map in oscript.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 17895 $
 * @date 	$LastChangedDate: 2014-09-15 18:06:15 +0300 (Mo, 15 Sep 2014) $
 * @project VisionR Server 
 */
public class Map extends Value {

	protected boolean onlyObjects;
	protected boolean isCodeUnique;
	protected boolean isIdUnique;
	protected boolean objMapsValid=false;
	
	protected TreeMap<String,Object> idMap=null;
	protected TreeMap<String,Object> codeMap=null;
	protected MultiMap objMap=null;
	
	public MultiMap getObjMap() {
		return objMap;
	}
	
	public TreeMap<String,Object> getCodeMap() {
		return codeMap;
	}
	
	public Map() {
		onlyObjects=true;
		isCodeUnique=true;
		isIdUnique=true;
		objMap=new ObjMultipleMap();
	}

	public Map(MultiMap objMap) {
		onlyObjects=true;
		isCodeUnique=true;
		isIdUnique=true;
		this.objMap=objMap;
	}
	
	public Map(java.util.Map m) {
		onlyObjects=true;
		isIdUnique=true;
		isCodeUnique=true;
		Iterator<java.util.Map.Entry> it = m.entrySet().iterator();
		
		objMap = new ObjMultipleMap();
		while (it.hasNext()) {
			java.util.Map.Entry e = it.next();
			put(e.getKey(),e.getValue());			
		}
	}
	
	public Map(Collection key,Collection values) {
		onlyObjects=true;
		isIdUnique=true;
		isCodeUnique=true;
		objMap=new ObjMultipleMap();
		Iterator it1 = key.iterator();
		Iterator it2 = key.iterator();
		while (it1.hasNext()) {
			if (!it2.hasNext()) {
				throw new RuntimeException("creating map with wrong parameters : collection size");
			}
			put(it1.next(),it2.next());
		}
		if (it2.hasNext())
			throw new RuntimeException("creating map with wrong parameters : collection size");		
	}
	
 	public Map(Object[] obj) {
		onlyObjects=true;
		isIdUnique=true;
		isCodeUnique=true;
		objMap=new ObjMultipleMap();
 		for (int i=0;i<obj.length;i++) {
 			put(i,obj[i]);
 		}
 	}

 	public Map(Collection col) {
		Iterator it = col.iterator();
		onlyObjects=true;
		isIdUnique=true;
		isCodeUnique=true;
		objMap = new ObjMultipleMap();
		while (it.hasNext()) {
			Object obj = cnv(it.next());
			put(obj);
		}
	}

 	public Object put(Object key,Object value) {
 		value=cnv(value); 		

 		if (onlyObjects) 
 		{ 
 			if (key instanceof ObjectWrapper) 
 			{
 				isCodeUnique=false;
 				onlyObjects=false;
 				isIdUnique=false;
 				idMap=null;
	 			codeMap=null;
 			}

 			if (value instanceof ObjectWrapper) 
 			{
 				validateData();
 				ObjectWrapper o = (ObjectWrapper)value;
 				Object eval = objMap.get(key);
	 			if (eval != null && o.isSame(eval)) 
	 			{
	 				return value;
	 			}
 				if (isIdUnique) 
 				{
	 				
 			 		if (idMap == null) 
 					{
 						if (codeMap != null) throw new RuntimeException("Internal error in Map");
 						idMap = new TreeMap();
 					}
 					Object f = idMap.get(o.getObjectReference());
 					if (f != null) 
 					{	
 						isIdUnique=false;
 	 	 				idMap=null;
 					} else {
 						idMap.put(o.getObjectReference(),o);
 					}
 				} 
 				if (isCodeUnique) {
					if (codeMap == null) {
						codeMap = new TreeMap();
 					}
					Object f = codeMap.get(o.code());
 					if (f != null) 
 					{
 						isCodeUnique=false;
 	 					codeMap=null;
 					} else {
 						codeMap.put(o.code(),o);
 					}
 				}
 			} else {
 				onlyObjects=false;
 			}
 		}
		if (objMap == null) 
	 		objMap = new ObjMultipleMap();
 		objMap.put(key,value);
 		return value;
 	}

 	public Object put(Object obj) {
 		obj=cnv(obj);
 		
 		if (onlyObjects) 
 		{
 			if (!(obj instanceof ObjectWrapper)) {
 	 			onlyObjects=false; 
 	 			if (objMap.isEmpty()) {
	 				objMap.put(0,obj);
 	 			}
 	 			else
 	 				objMap.put((Integer)objMap.getMaxKey()+1,obj);
 	 			return obj;
 			}
 			validateData();
			if (objMapsValid) 
			{
				boolean found=false;
	 			ObjectWrapper o = (ObjectWrapper)obj;
	 			if (isIdUnique) 
	 			{
	 				if (idMap == null)
	 					idMap=new TreeMap();
	 				@SuppressWarnings("unlikely-arg-type")
					Object eval = idMap.get(o);
	 				if (eval != null) 
	 				{
	 					if (!o.isSame(eval)) 
	 					{
		 					isIdUnique=false;
		 					idMap=null;
	 					} else {
	 						found=true;
	 					}
	 				} else { 					
	 					idMap.put(o.getObjectReference(),o);
	 				}
	 			}
	 			
	 			if (isCodeUnique) 
	 			{
	 				if (codeMap == null)
	 					codeMap=new TreeMap();
	 				
	 				Object eval = codeMap.get(o.code());  
	 				if (eval != null /*&& xs.id() != 0*/)
	 				{
	 					if (!o.isSame(eval)) 
	 					{
		 					isCodeUnique=false;
		 					codeMap=null;
	 					} else {
	 						found=true;
	 					}
	 				} else {
	 					codeMap.put(o.code(),o);
	 				}
	 			}
	 			
	 			if (!found) 
	 			{
				  	if (objMap.isEmpty()) {
				 		objMap.put(0,obj);
				 	}
					else 
						objMap.put((Integer)objMap.getMaxKey()+1,obj);
	 			}
			} else {
				if (objMap.isEmpty()) {
			 		objMap.put(0,obj);
		 	 	}
		 		else 
		 			objMap.put((Integer)objMap.getMaxKey()+1,obj);				
			}
 		} else {		
	 		if (objMap.isEmpty()) 
	 				objMap.put(0,obj);
	 		else
	 			objMap.put((Integer)objMap.getMaxKey()+1,obj);
 		}
 		return obj;
 	}
 	
 	private void validateData() {
 		if (!onlyObjects) {
 			return;
 		}
 		if (objMapsValid)
 			return;
 		objMapsValid=true;
 		Iterator it = objMap.valueIterator();
 		while (it.hasNext()) 
 		{
 			Object o = cnv(it.next());
 			if (!(o instanceof ObjectWrapper)) {
 				onlyObjects=false;
 				isCodeUnique=false;
 				isIdUnique=false; 
 				idMap = null;
 				codeMap=null;
 				return;
 			} 
 			ObjectWrapper ob = (ObjectWrapper)o;
 			if (isIdUnique) {
 				if (idMap == null)
 					idMap=new TreeMap();
 				Object rs = idMap.get(ob.getObjectReference());
 				if (rs != null) 
 				{
 	 				if (!ob.isSame(rs)) 
 	 				{
 	 					isIdUnique=false;
 	 					idMap=null;
 	 				} 
 				} else { 					
 					idMap.put(ob.getObjectReference(),o);
 				}
 			} 			
 			if (isCodeUnique) {
 				if (codeMap == null)
 					codeMap=new TreeMap();
 				
 				if( ob.code()  == null ) {
 					isCodeUnique=false;
 					codeMap=null;
 				} else {
	 				Object rs = codeMap.get(ob.code());  
	 				if (rs != null/* && xs.id() != 0*/)
	 				{
	 	 				if (!ob.isSame(rs)) 
	 	 				{
		 					isCodeUnique=false;
		 					codeMap=null;
	 	 				}
	 				} else {
	 					codeMap.put(ob.code(),o);
	 				}
 				}
 			}
 		}
 	}

 	protected Object get(Object o,int mode) {
 		o=cnv(o);

 		if (objMap == null || objMap.size() == 0)
 			return null;
 		
 		switch (mode)
 		{
 		case 0 : {
 			Object r;

 			if (onlyObjects && o instanceof ObjectWrapper) {
 				validateData();
 				if (isIdUnique) {
 	 				r = idMap.get(((ObjectWrapper)o).getObjectReference());
 	 				if (r == null)
 	 					return null;
 	 				Vector v = new Vector(1);
 		 			v.add(r);
 	 				return v;
 				}
 			}

 			r = objMap.get(o);
 			
 			if (r == null && onlyObjects && o instanceof String) 
 			{
 				validateData();
 				if (isCodeUnique) {
 	 				if (codeMap== null)
 	 					throw new MissingResourceException("Accessing missing object : "+o.toString(),getClass().getName(),o.toString());
 	 				r = codeMap.get(o);
 	 				if (r == null)
 	 					return null;
 	 				Vector v = new Vector(1);
 	 				v.add(r);
 	 				return v;
 	 			}
 			}

 			// Return copy of elements collection instead a view on the TreeMap
 			if( r != null && r instanceof Collection) {
 				r = new Vector((Collection)r);
 			}
			return r;
 		} case 1 : {
 			validateData();
 			if (!onlyObjects)
				throw new RuntimeException("Accessing map with id but map does not contains only db objects");
 			
 			if (o instanceof Number) {
 					throw new RuntimeException("Accessing map with id not supported");
 			} 
 			
 			if (o instanceof String) {
 				if (!isCodeUnique)
 					throw new RuntimeException("Accessing map with id but map does not contains unique code");
 				if (codeMap== null)
 					throw new MissingResourceException("Accessing missing object : "+o.toString(),getClass().getName(),o.toString());
 				Object r = codeMap.get(o);
 				return ValueConvertor.convert(r);
 			}
		
 			if (o instanceof ObjectWrapper)
 				return ValueConvertor.convert(o);
 			throw new RuntimeException("Accessing dbobject map with unknown key type (not integer or string)");
 		} default:
 			throw new RuntimeException("Accessing dbobject map with unknown lookup mode");
 		}
	}

 	public Object get(Object o) {
 		return get(o,0);
 	}
 	
 	public boolean isEmpty() {
 		return objMap.isEmpty();
 	}
 	
 	public boolean contains(Object key) {
 		Object o = get(key,0);
 		if (o != null)
 			return true;
 		return false;
 	}
 	 	
 	ScriptIterator valueIterator(final boolean sort) 
 	{
 		return new ScriptIterator() 
 		{
 			private Iterator it; 
 			private Object lastValue;
 			private int pos = -1;
 			// Anonymous instance initializer to set proper iterator
 			{
 				if( sort ) 
 					it = new TreeSet(objMap.getValues()).iterator();
 				else
 					it = objMap.valueIterator();
 			}
 			
			public boolean hasNext() {
				return it.hasNext();
			}

			public Object next() 
			{
				pos++;
				lastValue = it.next();
				final Value v = ValueConvertor.convert(lastValue);
				return new AbstractReference() 
				{
					@Override
					public Value getMember(int id, boolean exception)
							throws PackagedScriptObjectException 
					{
						if (id == Symbols.POS_TYPE)
							return new OExactNumber(pos);
						return super.getMember(id, exception);
					}
					@Override
					protected Value get() {
						return v;
					}
				};
			}
			
			public void remove() {
				it.remove();
				if (onlyObjects && objMapsValid) {
					ObjectWrapper o = (ObjectWrapper)lastValue;
					if (isIdUnique) {
						idMap.remove(o.getObjectReference());
					}
					if (isCodeUnique) {
						codeMap.remove(o.code());
					}
				}
			}
 		};
 	} 	
 	
 	public ScriptIterator keyIterator() {
 		return new ScriptIterator() {

 			private Iterator<? extends MultiMapElement> it = objMap.iterator();
 			private MultiMapElement lastEl;
 			private int pos = -1;
 			
			public boolean hasNext() {
				return it.hasNext();
			}

			public Object next() {
				pos++;
				lastEl = it.next();				
				final Value v = ValueConvertor.convert(lastEl.getKey());
				return new AbstractReference() 
				{
					@Override
					public Value getMember(int id, boolean exception)
							throws PackagedScriptObjectException 
					{
						if (id == Symbols.POS_TYPE)
							return new OExactNumber(pos);
						return super.getMember(id, exception);
					}
					@Override
					protected Value get() {
						return v;
					}
				};				
			}

			public void remove() {
				it.remove();
				if (onlyObjects && objMapsValid) {
					ObjectWrapper o = (ObjectWrapper)lastEl.getValue();
					if (isIdUnique) {
						idMap.remove(o.getObjectReference());
					}
					if (isCodeUnique) {
						codeMap.remove(o.code());
					}
				}
			}
 		};
 	}
 	
	public int size() {
		return objMap.size();
	}

	public boolean containsKey(Object key) {
		return contains(key);
	}

	public boolean containsValue(Object value) {
 		value=cnv(value); 		
		boolean ih = value instanceof Comparable;
		Iterator x = valueIterator(false);
		while (x.hasNext()) {
			Object k = x.next();
			if (!ih) {
				if (value == k)
					return true;
			} else {
				if (((Comparable)value).equals(k))
					return true;
			}
		}
		return false;
	}

	public void putAll(java.util.Map t) {
		Iterator<java.util.Map.Entry> it = t.entrySet().iterator();
		while (it.hasNext()) {
			java.util.Map.Entry e = it.next();
			put(e.getKey(),e.getValue());
		}
	}

	public void clear() {
		onlyObjects=true;
		isCodeUnique=true;
		isIdUnique=true;
		idMap=null;
		codeMap=null;
		objMap.clear();
	}

	public void clear(Object key) {
		Collection c = objMap.get(key);
		if (c != null) {
			Iterator it = c.iterator();
			while (it.hasNext()) {
				Object val=it.next();
				if (onlyObjects && objMapsValid) {
					ObjectWrapper obj=(ObjectWrapper)val;
					if (isIdUnique)
						idMap.remove(obj.getObjectReference());
					if (isCodeUnique)
						codeMap.remove(obj.code());
				}
				it.remove();
			}
		}
	}

	private MapValueCollection _values = null;
	private MapValueCollection _keys = null;
	static final int STR_values = Symbol.getSymbol("values").getId();
		
	public MapValueCollection values() {
		if (_values != null)
			return _values;
		validateData();
		if (onlyObjects)
			_values=new MapValueCollection(MapValueCollection.Mode.VALUES,this);
		else 
			_values=new MapValueCollection(MapValueCollection.Mode.COLLECTION,this);
		return _values;
	}
	
	public MapValueCollection keys() {
		if (_keys != null)
			return _keys;
		_keys=new MapValueCollection(MapValueCollection.Mode.KEYS,this);
		return _keys;
	}
	
	public String toString() {
		String str = "VMAP : ";
		Iterator<ObjMultipleMapElement> k = objMap.iterator();
		while (k.hasNext() ) {
			MultiMapElement e = k.next();
			str+=e.getKey()+" -> "+e.getValue()+"\n";
		}
		return str;
	}
	
	public ScriptIterator iterator() 
	{

		return new ScriptIterator() 
		{ 		
			private int pos = -1;
			private Iterator<? extends MultiMapElement> it = objMap.iterator();
 			private MultiMapElement lastEl;
		
 			public boolean hasNext() {
				return it.hasNext();
			}

 			public Object next() {
				if (!it.hasNext())
					return null;
				lastEl = it.next();
				pos++;
				return new MapKeyValueReference(lastEl,pos);
			}

			public void remove() {
				it.remove();
				if (onlyObjects && objMapsValid) {
					ObjectWrapper o = (ObjectWrapper)lastEl.getValue();
					if (isIdUnique) {
						idMap.remove(o.getObjectReference());
					}
					if (isCodeUnique) {
						codeMap.remove(o.code());
					}
				}
			}
 		};
	}
	
	public Value elementAt(Value _key) {
		Object key = _key.castToJavaObject();
		// special case : MAP [ map ] : return all MAP elements with index all 'map' elements
		if (key instanceof Map || key instanceof MapValueCollection) {
			Map n = new Map();
			Value it = _key.getMember("iterator");
			while (it.getMember("has_next").castToBoolean()) {
				Value el = it.getMember("next");
				if (this.contains(el)) 
				{
					Value rel = this.elementAt(el);
					n.put(rel);
				}
			}
			return n;
		}
		Collection o = (Collection)get(key);
		return new MapValueReference(this,key,o);
	}
		
	public Value getMember( int id, boolean exception )
	{
	    Value val = resolve(id);
	    if( val != null )
	      return val;
	    return super.getMember( id,exception);
	}
	  		
	public Value resolve(int symbol)
	{
		switch (symbol) 
		{
			case Symbols.PUT :
			case Symbols.PUSH :
			case Symbols.ADD : 
	 		{
				return new Value() {
					  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
						  int l = args.length();
						  if (l != 1 && l != 2)
							  throw new RuntimeException("Can not execute MAP.(put/push/add) : wrong number of args (NOT (1 or 2))");
						  Object t = args.referenceAt(0).castToJavaObject();
						  if (l == 1) {
							  put(t);
						  } else {
							  Object v = args.referenceAt(1).castToJavaObject();
							  put(t,v);
						  }
						  return Value.NULL;
					 }
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				  };
			}
			case Symbols.PUT_ALL : 
			{
				return new Value() {
					  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
						  int l = args.length();
						  if (l != 1 )
							  throw new RuntimeException("Can not execute MAP.putAll: wrong number of args (NOT 1)");
						  Object t = args.referenceAt(0).castToJavaObject();
						  if( t instanceof Map ) {
							  Map a = (Map) t;
							  ScriptIterator it = a.keyIterator();
							  while( it.hasNext() ) {
								  Object key = it.next();
								  if (key instanceof Value)
									  key=((Value)key).castToJavaObject();
								  Object val = a.get(key);
								  if( val instanceof Vector ) {
									  Vector vec = (Vector) val;
									  if( a.isIdUnique || a.isCodeUnique && vec.size() > 0 ) {
										  put(key, vec.elementAt(0));
									  } else {
										  for( int i=0; i < vec.size(); i++)
											  put( key, vec.elementAt(i));
									  }
								  } else {
									  put(key, val);
								  }
							  }
						  }
						  return Value.NULL;
					 }
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				  };
			}	
			case Symbols.GET : 
			{
				return new Value() {
					  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
						  int l = args.length();
						  if (l != 1)
							  throw new RuntimeException("Can not execute MAP.get : wrong number of args (!=1)");
						  Value res =  Map.this.elementAt(args.referenceAt(0));
						  if (res == null)
							  return Value.NULL;
						  return res;
					 }
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				  };
			}
			case Symbols.REMOVE : 
			{
				return new Value() {
					  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
						  int l = args.length();
						  if (l != 1)
							  throw new RuntimeException("Can not execute MAP.get : wrong number of args (!=1)");
						  Value res =  Map.this.elementAt(args.referenceAt(0));
						  if (res == null || res.bopEquals(Value.NULL).castToBoolean())
							  return Value.NULL;
						  return res.getMember("remove").callAsFunction(new Value[0]);
					 }
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				  };
			}
			case Symbols.CONTAINS : {
				return new Value() {
					  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
						  int l = args.length();
						  if (l != 1)
							  throw new RuntimeException("Can not execute MAP.get : wrong number of args (!=1)");
						  return OBoolean.makeBoolean(Map.this.contains(args.referenceAt(0)));
					 }
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				  };
			}
			case Symbols.IS_EMPTY :
			case Symbols.IS_EMPTY2 : 
				return new FunctionValueWrapper(OBoolean.makeBoolean(size() == 0));
			case Symbols.SIZE :
			case Symbols.LENGTH :
				return new FunctionValueWrapper(new FunctionValueWrapper(OExactNumber.makeExactNumber(size())));
			case Symbols.FIRST : 
				if (size() == 0)
					return Value.NULL;
				ScriptIterator it = this.iterator();
				if (!it.hasNext())
					return Value.NULL;				
				return new FunctionValueWrapper((Value)it.next());
			case Symbols.LAST : {
				if (size() == 0)
					return Value.NULL;
				Collection o = (Collection)get(objMap.getMaxKey());
				return new MapValueReference(this,objMap.getMaxKey(),o);
			}
			case Symbols.ITERATOR :
				return new FunctionValueWrapper(this.iterator());
			case Symbols.VALUES :
				return new FunctionValueWrapper(values());
			case Symbols.KEYS : 
				return new FunctionValueWrapper(keys());
			case Symbols.JOIN :  
			{
				return new Value() 
				{
					  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
						  int l = args.length();
						  if (l != 1)
							  throw new RuntimeException("Can not execute MAP.join : wrong number of args (!=1)");
						  String s = args.referenceAt(0).castToString();
						  StringBuffer f = new StringBuffer();
						  ScriptIterator it = iterator();
						  boolean first=true;
						  while (it.hasNext()) 
						  {
							  	if (!first)
							  		f.append(s);
							  	first=false;
								Object no = it.next();
								f.append(no);
						 }
						 return OString.makeString(f.toString());
					}
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				  };
			}
			case Symbols.TO_STRING2 :
			case Symbols.TOSTRING1 : 
				return new FunctionValueWrapper(new OString(this.toString()));
			case Symbols.SORT :  
			{
				return new Value() 
				{
					@Override
					public Value callAsFunction(StackFrame sf, MemberTable args)
							throws PackagedScriptObjectException 
					{
						if (args != null && args.length() > 1)
							throw new RuntimeException("Map.sort : wrong number of arguments!One param with compare function needed!");
						
						OArray a = new OArray();
						Value k = Map.this.getMember("iterator");
						int p = 0;
						while (k.getMember("hasNext").castToBoolean()) 
						{
							Value e = ValueConvertor.convert(k.getMember("next").castToJavaObject());
							a.elementAt(p++).opAssign(e);
						}
						if (args == null || args.length() == 0)
							a.sort();
						else
							a.sort(args.referenceAt(0));
						clear();
						for (int i=0;i<a.length();i++) 
						{
							Value e=a.elementAt(i);
							put(e);
						}
						return Map.this;
					}

					@Override
					protected Value getTypeImpl() {
						return this;
					}
				};
			}
			
			case Symbols.CLEAR :  
			{
				return new Value() 
				{
					@Override
					public Value callAsFunction(StackFrame sf, MemberTable args)
							throws PackagedScriptObjectException 
					{
						Map.this.clear();
						return Value.NULL;
					}
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				};
			}
			case Symbols.ORDER_BY_TYPE :  
			{
				return new Value() 
				{
					@Override
					public Value callAsFunction(StackFrame sf, MemberTable args)
							throws PackagedScriptObjectException {
						Function f;
						
						if (args == null || args.length() == 0) {
							f = (Function)(OscriptInterpreter.getGlobalScope().getMember("data_view").getMember("ctrl").getMember("get_object_as_string").unhand());
						} else {
							if (args.length() != 1) {
								throw new RuntimeException("Map.ORDER_BY : wrong number of arguments!");
							}
							Value x = args.referenceAt(0).unhand();
							if (!(x instanceof Function)) {
								throw new RuntimeException("Map.ORDER_BY : Argument should be an script function with one parameter the iterated value itself!");
							}
							f = (Function)x;
						}
							
						final class TmpEl implements Comparable<TmpEl> 
						{
							private Object key;
							private Object obj;
							public TmpEl(Object key,Object obj) 
							{
								this.key=key;
								this.obj=obj;
							}
							@Override
							public int compareTo(TmpEl o) 
							{
								int r;
								int ac = key instanceof Comparable ? 0 : 1;
								int bc = o.key instanceof Comparable ? 0 : 1;
								int s = ac-bc;
								if (s != 0)
									return s;
								if (ac != 0) {
									r = key.hashCode()-o.key.hashCode();
								} else {
									if (key instanceof Number && o.key instanceof Number) 
									{
										if (key instanceof Long || key instanceof Integer) 
										{
											long df = ((Number)key).longValue() - ((Number)o.key).longValue();
											if (df < 0)
												r=-1;
											else if (df > 0)
												r=1;
											else
												r=0;
										} else {
											double df = ((Number)key).doubleValue() - ((Number)o.key).doubleValue();
											if (df < 0)
												r=-1;
											else if (df > 0)
												r=1;
											else
												r=0;
										}
									}
										else r =((Comparable)key).compareTo(o.key);
								}
								if (r != 0)
									return r;
								
								ac = obj instanceof Comparable ? 0 : 1;
								bc = o.obj instanceof Comparable ? 0 : 1;
								s = ac-bc;
								if (s != 0)
									return s;
								if (ac != 0) {
									return obj.hashCode()-o.obj.hashCode();
								}
								if (obj instanceof Number && o.obj instanceof Number) 
								{
									if (obj instanceof Long || obj instanceof Integer) 
									{
										long df = ((Number)obj).longValue() - ((Number)o.obj).longValue();
										if (df < 0)
											r=-1;
										else if (df > 0)
											r=1;
										else
											r=0;
									} else {
										double df = ((Number)obj).doubleValue() - ((Number)o.obj).doubleValue();
										if (df < 0)
											r=-1;
										else if (df > 0)
											r=1;
										else
											r=0;
									}
								} else {
									r = ((Comparable)obj).compareTo(o.obj);
								}
								return r;
							}
						};
						
						
						TreeSet<TmpEl> tt = new TreeSet();
						ScriptIterator it = iterator();
						Value[] k = new Value[1];
						while (it.hasNext()) 
						{
							Object no = it.next();
							k[0]=ValueConvertor.convert(no);
							Object r = f.callAsFunction(k).castToJavaObject();
							if (r == null)
								r = Value.NULL;
							tt.add(new TmpEl(r,no));
						}
						Map kk = new Map();
						Iterator<TmpEl> kt = tt.iterator();
						while (kt.hasNext()) {
							kk.put(kt.next().obj);
						}
						return kk;
					}

					@Override
					protected Value getTypeImpl() {
						return this;
					}
				};
			}
		}
		return null;
	}

	@Override
	protected Value getTypeImpl() {
		return this;
	}
	
	protected Object cnv(Object val) {
		if (val == null)
			return val;		
		if (val instanceof Value)
			val = ((Value)val).castToJavaObject();
		return val;
	}
	
	public Value bopInstanceOf( Value val ) {
		if( val instanceof Map || val.castToJavaObject() == this.getClass() || val.castToJavaObject() == Map.class) {
			return OBoolean.TRUE;
		}
		return OBoolean.FALSE;
	}
	
	@Override
	public int length() throws PackagedScriptObjectException
	{
		return size();
	}

}
