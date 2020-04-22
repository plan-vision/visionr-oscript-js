package server;

import java.util.HashMap;

import bridge.bridge;

import oscript.data.FunctionValueWrapper;
import oscript.data.OArray;
import oscript.data.OBoolean;
import oscript.data.OExactNumber;
import oscript.data.OString;
import oscript.data.Symbol;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.data.ValueWrapperTempReference;
import oscript.util.StackFrame;
import oscript.varray.Map;

public final class ObjectWrapper extends Value implements Comparable
{
	public static final int TYPE_OBJECT=0;
	public static final int TYPE_PROPERTY=1; 
	public static final int TYPE_MODULE=2;
	public static final int TYPE_OBJECTDEF=3;
	public int _getType()  {return type;}
	protected int type; 
	private String odkey;
	private boolean resolveChildren=false;
	private String _objSchema;
	private long _objId; 
	public ObjectWrapper() {};
	public static ObjectWrapper makeProperty(String odefkey,String pro) {
		ObjectWrapper that = new ObjectWrapper();
		that._objSchema="core.property";
		that._objId=bridge.getObjectDefProperyId(odefkey, pro);
		that.odkey="core.objectdef";
		that.type=TYPE_PROPERTY;
		return that;
	}
	public static ObjectWrapper makeModule(String mod,boolean resolveChildren) {
		ObjectWrapper that = new ObjectWrapper();
		that._objSchema=that.odkey="core.module";
		that._objId=bridge.getModuleId(mod);
		that.type=TYPE_MODULE;
		that.resolveChildren=resolveChildren;
		return that;
	}
	public static ObjectWrapper makeObjectDef(String odefkey,boolean resolveChildren) {
		ObjectWrapper that = new ObjectWrapper();
		that._objSchema="core.objectdef";
		that._objId=bridge.getObjectDefId(odefkey);
		that.odkey=odefkey;
		that.type=TYPE_OBJECTDEF;
		that.resolveChildren=resolveChildren;
		return that;
	}
	public static ObjectWrapper makeObject(String odefkey,long id) {
		ObjectWrapper that = new ObjectWrapper();
		that.type=TYPE_OBJECT;
		that.resolveChildren=false;
		that._objId=id;
		that._objSchema=that.odkey=odefkey;
		return that;
	}
	public static ObjectWrapper makeObject(String odefkey,long id,String forceCode) {
		ObjectWrapper that = new ObjectWrapper();
		that.type=TYPE_OBJECT;
		that.resolveChildren=false;
		that._objId=id;
		that._objSchema=that.odkey=odefkey;
		that.forceCode=forceCode;  
		return that;
	}
	//-----------------------------------------------------------------------------------------------------------------
	private String forceCode;
	public String getOD() {
		return odkey;
	}
	public int getObjectWrapperType() {
		return type;
	}
	public boolean isSame(Object _t) {
		if (_t instanceof ObjectWrapper)
		{
			ObjectWrapper ow = (ObjectWrapper)_t;
			if (ow._objId == _objId && ow._objSchema.equals(_objSchema)) return true;
		}
		return false;
	}
	public long id() {
		return _objId;
	}

	public String odefkey() 
	{ 
		return _objSchema;
	}

	public String code() {
		if (forceCode != null)
			return forceCode;
		if (type == TYPE_OBJECTDEF) 
			return this.odkey.substring(this.odkey.indexOf(".")+1);
		String code = (String)bridge.getObjectValue(_objSchema, _objId, "code");
		if (code == null) return "";
		return code;
	}

	public Value callAsConstructor(StackFrame sf,
            oscript.util.MemberTable args) 
	{		
		if (type != TYPE_OBJECTDEF) {
			throw new RuntimeException("Can not call as constructor : wrong object type. Objectdef expected!");
		}
		/*String code=null;
		ExecutionCallbackParams cb = objectDefMemoryMode ? null : tr.getContext().getNewObjectCallback();
		if (cb != null) 
		{
			// FORMS ONLY CALLBACK
			Object[] a=new Object[1];
			a[0]=od.getModule().getCode()+"."+od.getCode();
			return JSConverter.JS2VR(cb.execute(a));
		}
		return DBObjImporter.importObject(tr,od,code,Mode.INSERT,DBConstants.ALL_PROJECT_ID,objectDefMemoryMode,transactionLevel).getObjectWrapper();*/
		// TODO Auto-generated catch bloc
		OArray a = new OArray(args);
		return bridge.newObject(odkey,a);
	}

	public String toString() {
		if (type == TYPE_OBJECTDEF)
			return "_OD@"+odkey; 
		if (type == TYPE_MODULE)
			return "_MOD@"+bridge.getModuleById(_objId);
		return "_OBJ@"+_objSchema+":"+_objId;
	}

	public void delete() {
		bridge.deleteObject(_objSchema,_objId); 
	}
	
	public Value elementAt(Value val) {
		Object rv;
		if (val == null)
			return Value.NULL;
		val = val.unhand();
		if (val instanceof ValueWrapperTempReference) {
			rv = ((ValueWrapperTempReference)val).castToSimpleValue().castToJavaObject();
		} else
			rv = val.castToJavaObject();

		if (rv instanceof ObjectWrapper) {
			ObjectWrapper ow = (ObjectWrapper)rv;
			if (ow.type == TYPE_PROPERTY)
				rv = ow.code();
		} else if (resolveChildren) {
			// operator [code or id]
			if (type == TYPE_OBJECTDEF)
				return _odefProperties().elementAt(val);
			if (type == TYPE_MODULE) 
				return _moduleObjectdefs().elementAt(val);			 
		}		
		Value res = null;
		if (rv instanceof String) {
			String to = type == TYPE_OBJECTDEF ? "core.objectdef" : odkey;
			String p = (String)rv;
			if (!bridge.objectDefHasPropery(to, p))
				return Value.NULL;
			res = getValue(1,p);
		} else if (rv instanceof Number)
			throw new RuntimeException("ObjectWrapper[pro id] NOT IMPLEMENTED!");
		if (res == null) { return Value.NULL; }
		return res;
	}
		
	public void commit() {
		bridge.commitObject(_objSchema,_objId);
	}
	
	public String castToString() {
		return _objSchema+"@"+this.code()+" | "+_objId;
	}
	
	public Value resolve(int symbol)
	{
		Value o =  _resolve(symbol);
		if (o != null)
			return o;
		String code = Symbol.getSymbol(symbol).castToString();
		String p = null;
		if (resolveChildren) 
		{
			if (type == TYPE_MODULE) {
				String module = bridge.getModuleById(_objId); 
				if (bridge.moduleHasChild(module, code))
					return this.elementAt(new OString(code));
				else {
					// resolve dummy 
					if (code.startsWith("history_")) 
						return Value.NULL; 
				}
			} else if (type == TYPE_OBJECTDEF) {
				if (code.equals("code")) {
					int k = odkey.indexOf(".");
					return new OString(odkey.substring(k+1));
				}
				if (bridge.objectDefHasPropery(odkey, code))
					return this.elementAt(new OString(code));
			}
		} else if (type == TYPE_OBJECTDEF) {
			if (code.equals("code")) {
				int k = odkey.indexOf(".");
				return new OString(odkey.substring(k+1));
			}
			// NOT IMPL
		} else {
			if (bridge.objectDefHasPropery(odkey, code)) {
				if (code.equals("objectdef")) {
					return new Value() {
						@Override
						protected Value getTypeImpl() {
							return null;
						}				
						@Override
						public Value getMember( int id, boolean exception ) {	
							String code = Symbol.getSymbol(id).castToString();
							if (code.equals("name")) {
								return new OString(bridge.getSchemaName(odkey));
							}							
							return super.getMember(id,exception);
						}
						public String castToString() {
							return bridge.getSchemaName(odkey);
						}
					};
				}
				p=code; 
			} else {
				if (code.equals("is_temporary") || symbol == Symbols.IS_EMPTY)	// hack for db.currentSession().user.is_empty
					return new OBoolean(false);				
			}
		}
		if (p == null)
			return null;
		Value x = getValue(1,p);
		if (x == null)
			return Value.NULL;
		return x;
	}	
	
	public Value bopInstanceOf(Value _val)  {	
		
		Object val;
		if (_val == null)
			return Value.NULL;
		
		_val=_val.unhand();
		
		if (_val instanceof ValueWrapperTempReference)
			val = ((ValueWrapperTempReference)_val).castToSimpleValue().castToJavaObject();
		else
			val = _val.castToJavaObject();
			
		if (val instanceof DBWrapper)
			return OBoolean.TRUE;
		
		if (!(val instanceof ObjectWrapper)) {
			//throw new VException("Can not execute inherits() : argument is not objectdef");
			return OBoolean.FALSE;
		}
		ObjectWrapper ow = (ObjectWrapper)val;
		if (ow.type != TYPE_OBJECTDEF) {
			throw new RuntimeException("Can not execute inherits() : argument is not objectdef");
		}		
		String od = ow.odkey;
		String tod = this.odkey;
		//-----------------------------------------------------------		
		if (bridge.getTopObjectDef(od) != bridge.getTopObjectDef(tod))
			return OBoolean.FALSE;
		return bridge.objectDefAllInheritsContains(tod, od) ? OBoolean.TRUE : OBoolean.FALSE;
	}
	
	public Value bopEquals(Value _val) {
		return this.bopNotEquals(_val).uopLogicalNot();
	}

	public Value bopNotEquals(Value _val) 
	{
		if (_val == null) return OBoolean.FALSE;
		_val=_val.unhand();
		Object val;
		if (_val instanceof ValueWrapperTempReference)
			val = ((ValueWrapperTempReference)_val).castToSimpleValue().castToJavaObject();
		else
			val =  _val.castToJavaObject();
		if (type == TYPE_OBJECTDEF) {
			if (val instanceof ObjectWrapper) {
				ObjectWrapper ow = (ObjectWrapper)val;
				return OBoolean.makeBoolean(!ow.odkey.equals(odkey));
			}
			return OBoolean.TRUE;
		}
		if (val instanceof ObjectWrapper) {
			ObjectWrapper ow = (ObjectWrapper)val;			
			return OBoolean.makeBoolean(!(ow._objSchema.equals(_objSchema) && ow._objId == _objId));
		}
		if (val instanceof String) {
			String s = code();
			if (s == null)
				return OBoolean.TRUE;
			return OBoolean.makeBoolean(!code().equals(val));
		}
		if (val instanceof Number)
			return OBoolean.makeBoolean(_objId != ((Number)val).longValue());
		return OBoolean.TRUE;
	}

	@Override
	protected Value getTypeImpl() {
		return this;
	}
	
	public Value getMember( int id, boolean exception )
	{
	    Value val = resolve(id);
	    if( val != null )
	      return val;
	    return super.getMember( id,exception);
	}
	

	@Override
	public int compareTo(Object o) 
	{
		if (o == this)
			return 0;
		if (o instanceof ObjectWrapper) 
		{
			ObjectWrapper k = (ObjectWrapper)o;
			if (id() < k.id())
				return -1;
			if (id() > k.id())
				return 1;
			return _objSchema.compareTo(k._objSchema);
		}
		return this.getClass().hashCode()-o.getClass().hashCode();
	}
	
	
	public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) 
	{
		if (type == TYPE_OBJECT) 
		{
			switch (_objSchema) {
				case "core.script":
				case "core.vscript":
				case "core.jscript":
					Object a[] = new Object[args.length()];
					for (int i=0;i<args.length();i++) {
						Value v = args.referenceAt(i).unhand();
						a[i]=ValueConvertor.convertToJavaObject(v);
					}
					return ValueConvertor.convert(bridge.callScript(code(),a,null,null));
			}
		}
		throw new RuntimeException("can't call as function. Object is not a script : "+this.toString());
	}

	public long getID() 
	{		
		return _objId;
	}
	//----------------------------------------------------
	private String _ref;
	public String getObjectReference() {
		if (_ref == null) _ref=_objSchema+":"+_objId; 
		return _ref;
	}
	//----------------------------------------------------
	private Map _modOdefs;
	private Map _moduleObjectdefs() {
		//getModule().children().cachedVRMap()
		if (_modOdefs != null) return _modOdefs;
		_modOdefs=new Map();
		String module = bridge.getModuleById(_objId);
		for (String s : bridge.getModuleChildren(module)) {
			String odkey = module+"."+s;
			_modOdefs.put(s,makeObjectDef(odkey, true));
		}
		return _modOdefs;
	}
	//----------------------------------------------------
	private Map _odefProperties;
	private Map _odefProperties() {
		if (_odefProperties != null) return _odefProperties;
		_odefProperties=new Map();
		for (String p : bridge.getObjectDefProperties(odkey)) {
			_odefProperties.put(p,makeProperty(odkey,p));
		}
		return _odefProperties;
	}
	//----------------------------------------------------
	private Value _resolve(int symbol)  
	{
		if (symbol == Symbols.ID && (type != TYPE_OBJECTDEF || !resolveChildren)) 
			return OExactNumber.makeExactNumber(getID());
		//-----------------------------------------------------------------------
		if (type == TYPE_OBJECT) {
			return _resolveObject(symbol);
		} else if (type == TYPE_MODULE) {
			return _resolveModule(symbol);
		} else if (type == TYPE_OBJECTDEF) {
			return _resolveObjectDef(symbol);
		} else if (type == TYPE_PROPERTY) 
			return _resolveProperty(symbol);
		return null;
	}
	private Value _resolveModule(int symbol) 
	{
		if (symbol == Symbols.OBJECTDEFS)
			return _moduleObjectdefs();
		if (symbol == Symbols.FROM_CACHE_TYPE)
			return this;
		if (symbol == Symbols.CONTAINS) { 
			return new Value() {
				public Value callAsFunction(StackFrame sf, oscript.util.MemberTable args) {
					int l = args.length();
					if (l < 1)
						throw new RuntimeException("Can not execute contains(element) : wrong number of args (<1)");
					Object t = args.referenceAt(0).castToJavaObject();
					if (t instanceof ObjectWrapper)
						throw new RuntimeException("Can not execute contains(OBJECT) : wrong object type. Only objectdef is allowed!");
					if (type == TYPE_OBJECTDEF) {
						if (t instanceof String) {
							return OBoolean.makeBoolean(bridge.objectDefHasPropery(odkey, (String)t));
						} else if (t instanceof Number) {
							throw new RuntimeException("ObjectWrapper SCHEMA contains : int operator not implemented!");
						}
					} else {
						String module = bridge.getModuleById(_objId);
						if (t instanceof String)
							return OBoolean.makeBoolean(bridge.moduleHasChild(module,(String)t));
						else if (t instanceof Number) {
							throw new RuntimeException("ObjectWrapper MODULE contains : int operator not implemented!");
						}
					}
					throw new RuntimeException("Can not execute contains(element) : wrong agument type.String or Integer allowed!");
				}
				@Override
				protected Value getTypeImpl() {
					return this;
				}
			};
		}
		return null;
	}
	
	private Value _resolveObjectDef(int symbol) {
		switch (symbol) {
			case Symbols.KEY_TYPE :
				return new OString(this.odkey);
			case Symbols.PARENT_OBJECTDEF :
				String pk = bridge.getParentObjectDef(this.odkey);
				if (pk == null) return Value.NULL;
				return makeObjectDef(pk,true);
			case Symbols.OBJECT_ACCESS_TYPE : 
			{
				return new Value() {
					@Override
					protected Value getTypeImpl() {
						return this;
					}
					@Override
					public Value getMember(int id, boolean exception) {
						String s = Symbol.getSymbol(id).castToString();
						if (s.startsWith("is_"))
							return new OBoolean(bridge.getSchemaAccess(odkey,s.substring(3)));
						return super.getMember(id,exception);
					}
				};
			}
	
			case Symbols.CLEAR_GLOBAL_CACHE : 
				return new Value() {
					@Override
					protected Value getTypeImpl() {return null;}
					@Override
					public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
						return Value.NULL;
					}
				};				
		
			case Symbols.CONTAINS : 
					return new Value() 
					{
					  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
						  int l = args.length();
						  if (l < 1)
							  throw new RuntimeException("Can not execute contains(element) : wrong number of args (<1)");
						  
						  Object t = args.referenceAt(0).castToJavaObject();
						  if (t instanceof ObjectWrapper) 
						  {
							ObjectWrapper ow = (ObjectWrapper)t;
							if (type == TYPE_OBJECTDEF) {
								if (ow.type != TYPE_PROPERTY)
									throw new RuntimeException("Can not execute contains(OBJECT) : wrong object type. Only property is allowed!");
								t = ow.code();
							} else if (ow.type == TYPE_OBJECTDEF) {
								t = ow.code();							  
							} else {
								throw new RuntimeException("Can not execute contains(OBJECT) : wrong object type. Only objectdef is allowed!");
							}
						  }
						  if (t instanceof String) {
							  return OBoolean.makeBoolean(bridge.objectDefHasPropery(odkey,(String)t));
						  } else if (t instanceof Number)
								throw new RuntimeException("ObjectWrapper OBJECTDEF contains : int operator not implemented!");
						  throw new RuntimeException("Can not execute contains(element) : wrong agument type.String or Integer allowed!");
					 }
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				  };
			case Symbols.CLEAR_CACHE1:
			case Symbols.CLEARCACHE2:
			case Symbols.CLEAR_CACHE_BASIC:
				return new Value() {
					public Value callAsFunction(StackFrame sf, oscript.util.MemberTable args) {
						// TODO
						return Value.NULL;
					}
	
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				};
			case Symbols.INSTANCES_TYPE:
				return new Value() {
					public Value elementAt(Value val) {
						Object o = val.castToJavaObject();
						if (o instanceof String) {
							Object ob = bridge.getObjectByCode(odkey, (String)o);
							if (ob == null) return Value.NULL;
							return (ObjectWrapper)ob;
						}
						if (o instanceof Number) return makeObject(odkey, ((Number)o).longValue());
						return Value.NULL;
					}
		
					@Override
					protected Value getTypeImpl() {
						return this;
					}
				};
			case Symbols.FROM_CACHE_TYPE:
				return ObjectWrapper.this;
			case Symbols.PROPERTIES:
			case Symbols.PROPERTY_TYPE:
				return _odefProperties();
			case Symbols.HAS_OBJECTDEF1:
			case Symbols.HAS_OBJECTDEF2:
				return new OBoolean(bridge.objectDefHasPropery(odkey, "objectdef"));
			/*case Symbols.INHERITED_FROM:
				return od.getInheritedFromMap();
			case Symbols.ALL_INHERITED_FROM:
			case Symbols.ALL_INHERITANCE_CHILDREN:
				return od.getAllInheritedFromMap();
			case Symbols.ALL_INHERITED:
			case Symbols.ALL_INHERITANCE_PARENTS:
				return od.getAllInheritsMap();*/
			case Symbols.HAS_CODE1:
			case Symbols.HAS_CODE2:
				return new OBoolean(bridge.objectDefHasPropery(odkey, "code"));
			case Symbols.HAS_NAME1:
			case Symbols.HAS_NAME2:
				return new OBoolean(bridge.objectDefHasPropery(odkey, "name"));
			case Symbols.MODULE_TYPE:
				return DBWrapper.that.getMember(odkey.split("\\.")[0]);	
			/*case Symbols.TOP_OBJECTDEF:
				return od.getTopObjectDef().getObjectWrapper();
			case Symbols.PARENT_OBJECTDEF: {
				if (od.getParentObjectDef() == null)
					return Value.NULL;
				return od.getParentObjectDef().getObjectWrapper();
			}*/
			case Symbols.SELECT_TYPE: {
				return new Value() {
					// where is STRING or instanceof core.abstract_vsql_condition
					public Value callAsFunction(StackFrame sf, oscript.util.MemberTable args) {
						int l = args == null ? 0 : args.length();
						if (l > 2)
							throw new RuntimeException(
									"Wrong parameter format : Use SELECT(conditionSting OR core.abstract_vsql_condition)");
						return new DBObjReaderContextWrapper(odkey, args.length() > 0 ? args.referenceAt(0).castToString() : null);
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
	private Value _resolveObject(int symbol) {
		switch (symbol)
		{
			case Symbols.OBJECT_ACCESS_TYPE : 
			{
				return new Value() {
					@Override
					protected Value getTypeImpl() {
						return this;
					}
					@Override
					public Value getMember(int id, boolean exception) {
						String s = Symbol.getSymbol(id).castToString();
						if (s.startsWith("is_"))
							return new OBoolean(bridge.getObjectAccess(getOD(),getID(),s.substring(3)));
						return super.getMember(id,exception);
					}
				};
			}

			case Symbols.REFRESH_OBJECT_TYPE :
			{
			  return new Value() {
				  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
					  return Value.NULL;
				 }
				@Override
				protected Value getTypeImpl() { 
					return this;
				}
			  };
			}
			case Symbols.DELETE :
			{
			  return new Value() {
				  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
					  delete();
					  return Value.NULL;
				 }
				@Override
				protected Value getTypeImpl() {
					return this;
				}
			  };
			}
			case Symbols.FROM_CACHE_TYPE : 
				return this;
			case Symbols.OBJECTDEF_TYPE : 
				String aa[]=odkey.split("\\.");
				return DBWrapper.that.getMember(aa[0]).getMember(aa[1]);
			case Symbols.TO_STRING2 :
			case Symbols.TOSTRING1 :
				return new FunctionValueWrapper(new OString(this.toString()));
			case Symbols.COMMIT : 
				return new Value() {
				  public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
					  commit();
					  return Value.NULL;
				 }
				@Override
				protected Value getTypeImpl() {
					return this;
				}
			  };
			case Symbols.IS_DELETED1 :
			case Symbols.ISDELETED2 :
				return new FunctionValueWrapper(OBoolean.makeBoolean(bridge.isDeleted(_objSchema, _objId)));
			case Symbols.IS_INSERTED1 :
			case Symbols.ISINSERTED2 : 
				return new FunctionValueWrapper(OBoolean.makeBoolean(bridge.isInserted(_objSchema, _objId)));
			case Symbols.TEMP_TYPE : 
			{
				return new Value() 
				{
				  	@Override
					public Value getMember( int id, boolean exception )
					{
				  		Value val = super.getMember(id,false);
				  		if (val != null)			  			
				  			return val;
				  		
				  		if (id == Symbols.CLEAR) 
				  		{
				  			return new Value() 
							{
								public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
									TempValueWrapper.clearForObject(_objSchema, _objId);
									return Value.NULL;
								}
								protected Value getTypeImpl() {
									return this;
								}
							};
				  		}
				  		String key = Symbol.getSymbol(id).castToString();
				  		return new TempValueWrapper(_objSchema,_objId,key);
					}
	
					@Override
					protected Value getTypeImpl() {
						return this;
					}
					
					public Value elementAt(Value val) {
						String key = val.castToString();
						return new TempValueWrapper(_objSchema,_objId,key);
					}
				};
			}
		}
		//-------------------------------------------------------
		switch (_objSchema) 
		{
			case "core.user" : 
			case "contacts.user" : 
			case "contacts.user_registered" :
				switch (Symbol.getSymbol(symbol).castToString()) {
					case "get_setting_value" : 
			  			return new Value() 
						{
							public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
								String code = args.referenceAt(0).castToString();
								return ValueConvertor.convert(bridge.getUserSetting(code));
							}
							protected Value getTypeImpl() {
								return this;
							}
						};
						
					case "set_setting" : 
			  			return new Value() 
						{
							public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
								TempValueWrapper.clearForObject(_objSchema, _objId);
								String code = args.referenceAt(0).castToString();
								Object val = ValueConvertor.convertToJavaObject(args.referenceAt(1).unhand());
								if (val instanceof ObjectWrapper) {
									ObjectWrapper o = (ObjectWrapper)val;
									bridge.setUserSettingRel(code,o.getOD(),o.getID());
								} else
									bridge.setUserSetting(code,val);
								return Value.NULL;
							}
							protected Value getTypeImpl() {
								return this;
							}
						};
				}
				break;
		}
		return null;
	}

	
	/* TODO ? */
	private Value _resolveProperty(int symbol) {
		return null;
	}

	//--------------------------------------------------------------------------------
	
	public Value getValue(int newContext,String p)
	 {
		return getValueWrapper(newContext,p,null,0,false);
	 }

	
	private HashMap<String,ValueWrapper> vwrps;
	private HashMap<String,ValueWrapper> vwrpsOld;
	public ValueWrapper getValueWrapper(int context,String pro,String lang,int pos,boolean oldMode) 
	{
		final boolean isCached = (context == 1);
		if (isCached) {
			ValueWrapper valw = null;
			if (oldMode) {
				if (vwrpsOld == null) 
					vwrpsOld=new HashMap();
				else
					valw=vwrpsOld.get(pro);
			} else {
				if (vwrps== null) 
					vwrps=new HashMap();
				else
					valw=vwrps.get(pro);
			}
			if (valw != null)
				return valw;
		}
		//context = 0,1,2,3,4
		BasicValueLocator b = new BasicValueLocator();
		b.context=context;
		b.pro=pro;
		b.lang=lang;
		b.pos=pos;
		b.oldMode=oldMode;
		ValueWrapper res=new ValueWrapper(b,this);
		if (isCached) {
			if (oldMode)
				vwrpsOld.put(pro, res);
			else
				vwrps.put(pro, res);
		}
		return res;
	}


}

	
