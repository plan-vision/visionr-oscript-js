package server;

import java.util.HashMap;
import java.util.MissingResourceException;

import bridge.bridge;
import oscript.data.FunctionValueWrapper;
import oscript.data.OBoolean;
import oscript.data.OString;
import oscript.data.Symbol;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.StackFrame;
import oscript.varray.Map;

public class DBWrapper extends Value {

	private Map _map;
	public final static DBWrapper that = new DBWrapper();
	
	private static HashMap<String,ObjectWrapper> _mods = new HashMap();
	private DBWrapper() {
		super();
	}

	private ObjectWrapper getByCode(String code) 
	{
		ObjectWrapper t = _mods.get(code);
		if (t != null) return t;
		if (!bridge.hasModule(code)) return null;
		t = ObjectWrapper.makeModule(code, true);
		_mods.put(code,t);
		return t;
	}

	public String toString() {
		return "DBWrapper@" + hashCode();
	}

	private boolean contains(Object o) {
		if (o instanceof Number)
			return bridge.getModuleById(((Number)o).longValue()) != null;
		if (o instanceof String)
			return bridge.hasModule(((String)o));
		return false;
	}
	
	private void commit() {
		// TODO COMMIT 
	}

	private Value get(Object key) {		
		Value r = null;
		if (key instanceof String)
			r = getByCode((String) key);
		if (r != null)
			return r;
		if (key == null)
			throw new MissingResourceException(
					"Accessing missing object : NULL", getClass().getName(),
					"NULL");
		throw new MissingResourceException("Accessing missing object : "
				+ key.toString(), getClass().getName(), key.toString());
	}

	@Override
	protected Value getTypeImpl() {
		return this;
	}

	public Value elementAt(Value val) {
		Object o = val.castToJavaObject();
		if (o instanceof String) {
			String s = (String) o;
			if (s.contains("_")) 
			{
				int p1 = s.indexOf('_');
				String tod = s.substring(0, p1);
				String tid = s.substring(p1 + 1);
				int od = 0;
				long id = 0;
				boolean ok = false;
				try {
					od = Integer.parseInt(tod);
					id = Long.parseLong(tid);
					ok = true;
				} catch (NumberFormatException e) {
				}
				if (ok) {
					String a = bridge.getObjectDefById(od);
					if (a == null) {
						System.err.println("Can not find objectdef with id = " + od);
						return Value.NULL;
					} else {
						return ObjectWrapper.makeObject(a, id);
					}
				}
			}
		}
		return get(o);
	}

	public Value resolve(int symbol) 
	{
		switch (symbol) 
		{
			case Symbols.CONTAINS:
				return new Value() {
					public Value callAsFunction(StackFrame sf,
							oscript.util.MemberTable args) {
						int l = args.length();
						if (l != 1)
							throw new RuntimeException(
									"Can not execute db.contains(obj) : wrong number of args (!=1)");
						Object o = args.referenceAt(0).castToJavaObject();
						return OBoolean.makeBoolean(contains(o));
					}
	
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				};
			case Symbols.COMMIT: {
				return new Value() {
					public Value callAsFunction(StackFrame sf,
							oscript.util.MemberTable args) {
						commit();
						return null;
					}
	
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				};
			}
	
			case Symbols.TO_STRING2:
			case Symbols.TOSTRING1:
				return new FunctionValueWrapper(new OString(this.toString()));
	
			case Symbols.MODULES: {
				if (_map != null) return _map;
				_map = new Map();
				for (String m : bridge.getModules())
					_map.put(m, ObjectWrapper.makeModule(m, true));
				return _map;
			}
			case Symbols.IS_NESTED_TRANSACTION :
				return new OBoolean(bridge.isNestedTransaction());		
			case Symbols.DEFAULT_LANG: {
				ObjectWrapper t = (ObjectWrapper)bridge.getObjectByCode("core.lang",bridge.defaultLang());
				return new FinalValue(t, null, 0, null);
			}
			case Symbols.CURRENT_LANG: {
				ObjectWrapper t = (ObjectWrapper)bridge.getObjectByCode("core.lang",bridge.currentLang());
				return new FinalValue(t, null, 0, null);
			}
		}
		Symbol s = Symbol.getSymbol(symbol);
		return get(s.castToString());
	}

	// ------------------------------------------------------------------------------------------------------------------------------
	/*
	 * public void cacheDebug() { DBCache.debugMode=3; }
	 */

	public Value getMember(int id, boolean exception) {
		Value val = resolve(id);
		if (val != null)
			return val;
		return super.getMember(id, exception);
	}

	@Override
	public Value bopInstanceOf(Value val) throws PackagedScriptObjectException {
		Object o = val.castToJavaObject();
		return OBoolean.makeBoolean(o instanceof ObjectWrapper
				|| o instanceof DBWrapper);
	}

}
