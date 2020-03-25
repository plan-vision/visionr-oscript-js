package bridge;

import oscript.OscriptInterpreter;
import oscript.data.OExactNumber;
import oscript.data.OString;
import oscript.data.Reference;
import oscript.data.Scope;
import oscript.data.Symbol;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.parser.ParseException;
import oscript.util.MemberTable;
import oscript.util.StackFrame;
import oscript.varray.Map;
import oscript.varray.Vector;
import server.DBWrapper;
import server.ObjectWrapper;
import server.SessionWrapper;
import server.SystemWrapper;
import server.ValueConvertor;
import server.ValueWrapper;

//import java.text.DecimalFormat;
//import java.text.DecimalFormatSymbols;

import bridge.common;

public class base {

	public static void init() {
		Scope s = OscriptInterpreter.getGlobalScope();
		s.createMember("FALSE", Reference.ATTR_PUBLIC);
		s.createMember("TRUE", Reference.ATTR_PUBLIC);
		s.getMember("FALSE").opAssign(ObjectWrapper.makeObject("core.option", 0));
		s.getMember("TRUE").opAssign(ObjectWrapper.makeObject("core.option", 1));
		//-----------------------------------------------------------------------
		s.createMember("SCRIPTS", Reference.ATTR_PUBLIC);
		s.getMember("SCRIPTS").opAssign(new Value() {
			@Override
			protected Value getTypeImpl() {
				return null;
			}
			@Override
			public Value elementAt(Value idx) throws PackagedScriptObjectException {
				Object t = bridge.getObjectByCode("core.script",idx.castToString());
				if (t == null) return Value.NULL;
				return (ObjectWrapper)t; 
			}
		});
		s.createMember("MSG", Reference.ATTR_PUBLIC);
		s.getMember("MSG").opAssign(new Value() {
			@Override
			protected Value getTypeImpl() {
				return null;
			}
			@Override
			public Value elementAt(Value idx) throws PackagedScriptObjectException {
				Object t = bridge.getObjectByCode("core.script",idx.castToString());
				if (t == null) return Value.NULL;
				ObjectWrapper w = (ObjectWrapper)t;
				Value v = w.getMember("name");
				if (v.bopEquals(Value.NULL).castToBoolean())
					return new OString(w.code());
				return v.unhand();				
			}		
		});

		s.createMember("db", Reference.ATTR_PUBLIC);
		s.createMember("session", Reference.ATTR_PUBLIC);
		s.createMember("system", Reference.ATTR_PUBLIC);
		s.getMember("db").opAssign(DBWrapper.that);
		s.getMember("session").opAssign(new SessionWrapper());
		s.getMember("system").opAssign(new SystemWrapper());

		s.createMember("M", Reference.ATTR_PUBLIC);
		s.createMember("V", Reference.ATTR_PUBLIC);
		s.createMember("SB", Reference.ATTR_PUBLIC);
		s.getMember("M").opAssign(new Value() {
			@Override
			protected Value getTypeImpl() {
				return null;
			}
			@Override
			public Value callAsConstructor(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
				return new Map();
			}
		});
		s.getMember("V").opAssign(new Value() {
			@Override
			protected Value getTypeImpl() {
				return null;
			}
			@Override
			public Value callAsConstructor(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
				return new Vector();
			}
		});
		s.getMember("SB").opAssign(new Value() {
			@Override
			protected Value getTypeImpl() {
				return null;
			}
			@Override
			public Value callAsConstructor(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
				final StringBuffer sb = new StringBuffer();
				return new Value() {
					@Override
					public Value getMember(int id, boolean exception) throws PackagedScriptObjectException {
						switch (id) {
							case Symbols.LENGTH: 
								return new Value() {
									@Override
									protected Value getTypeImpl() {return null;}
									public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
										return new OExactNumber(sb.length());
									}
								};
							case Symbols.TOSTRING1:
							case Symbols.TO_STRING2:
								return new Value() {
									public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
										return new OString(sb.toString());
									}
									protected Value getTypeImpl() {return null;}
								};
							case Symbols.APPEND:
								return new Value() {
									@Override
									protected Value getTypeImpl() {return null;}
									public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
										sb.append(args.referenceAt(0).castToString());
										return Value.NULL;
									}
								};
						}
						return super.getMember(id, exception);
					}
					@Override
					public String castToString() throws PackagedScriptObjectException {
						return sb.toString();
					}
					@Override
					protected Value getTypeImpl() { return null; }
				};
			}
		});

		s.createMember("core", Reference.ATTR_PUBLIC);
		s.getMember("core").opAssign(new Value() {

			@Override
			protected Value getTypeImpl() {
				return null;
			}
			@Override
			public Value getMember(int id, boolean exception) throws PackagedScriptObjectException {
				switch (Symbol.getSymbol(id).castToString()) 
				{
					case "misc" : return new Value() {
						@Override
						protected Value getTypeImpl() {
							return null;
						}
						@Override
						public Value getMember(int id, boolean exception) throws PackagedScriptObjectException {
							switch (Symbol.getSymbol(id).castToString()) {
								case "historizeDeletedObject" : 
								case "historizeValue" :
									return new Value() {
										@Override
										public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
											// no impl!
											return Value.NULL;
										}

										@Override
										protected Value getTypeImpl() {
											return null;
										}
									};
							}
							return super.getMember(id,exception);
						}			
					};
				} 
				return super.getMember(id,exception);
			}			
		});

		s.createMember("java", Reference.ATTR_PUBLIC);
		s.getMember("java").opAssign(new Value() {		
			@Override
			public Value getMember(int id, boolean exception) throws PackagedScriptObjectException {
				switch (Symbol.getSymbol(id).castToString()) 
				{
					case "math" :
						return new Value() {
							@Override
							protected Value getTypeImpl() {return null;}
							public Value getMember(int id, boolean exception) throws PackagedScriptObjectException {
								switch (Symbol.getSymbol(id).castToString()) 
								{
									case "randomUUID" :
										return new Value() {
											@Override
											protected Value getTypeImpl() {return null;}
											public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
												return randomUUID();
											}
										};
								}
								return super.getMember(id, exception);
							}
						};
					case "string" :
						return new Value() {
							@Override
							protected Value getTypeImpl() {return null;}
							public Value getMember(int id, boolean exception) throws PackagedScriptObjectException {
								switch (Symbol.getSymbol(id).castToString()) 
								{
									case "format" :
										return new Value() {
											public Value getMember(int id, boolean exception) throws PackagedScriptObjectException {
												switch (Symbol.getSymbol(id).castToString()) 
												{
													case "formatNumber" :
														return new Value() {
															@Override
															public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
																String format = args.referenceAt(0).castToString(); 
																long num = args.referenceAt(1).castToExactNumber();
																String nstr = num+"";
																int nz = 0;
																for (int pos = 0;pos < format.length(); pos++) 
																	if (format.charAt(pos) == '0')
																		nz++;
																while (nstr.length() < nz) nstr="0"+nstr;
																//---------------------------------------------
																int cz = 0;
																String res = "";  																		
																for (int pos = 0;pos < format.length(); pos++) {
																	char c = format.charAt(pos); 
																	if (c == '0') {
																		res+=nstr.charAt(cz);
																		cz++;
																	} else {
																		res+=c;
																	}
																}																
																return new OString(res);
															}															
															@Override
															protected Value getTypeImpl() {return null;}
														};
												}
												return super.getMember(id, exception);
											}											
											@Override
											protected Value getTypeImpl() {return null;}
										};
								}
								return super.getMember(id, exception);
							}
						};						
					case "log" : 
						return new Value() {
							@Override
							protected Value getTypeImpl() {return null;}
							public Value getMember(int id, boolean exception) throws PackagedScriptObjectException {
								switch (Symbol.getSymbol(id).castToString()) 
								{
									case "warn" :
									case "error" :
										return new Value() {
											@Override
											protected Value getTypeImpl() {return null;}
											public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
												System.err.println(args.referenceAt(0).castToString());return Value.NULL;
											}
										};
									case "log" :
									case "info" :
										return new Value() {
											@Override
											protected Value getTypeImpl() {return null;}
											public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
												System.out.println(args.referenceAt(0).castToString());return Value.NULL;
											}
										};
								}
								return super.getMember(id, exception);
							}
						};
				}
				return super.getMember(id, exception);
			}
			@Override
			protected Value getTypeImpl() { return null; }
		});
		
		s.createMember("REQUIRE", Reference.ATTR_PUBLIC);
		s.getMember("REQUIRE").opAssign(new Value() {
			@Override
			public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
				String require = args.referenceAt(0).castToString();
				String api = args.referenceAt(1).castToString();
				Object[] a = new Object[args.length()-2];
				for (int i=0;i<a.length;i++) 
					a[i]=ValueConvertor.convertToJavaObject(args.referenceAt(i+2).unhand());
				return ValueConvertor.convert(bridge.require(require,api,a));
			}
			@Override
			protected Value getTypeImpl() { return null; }
		});
		s.createMember("OBJSTR", Reference.ATTR_PUBLIC);
		s.getMember("OBJSTR").opAssign(new Value() {
			@Override
			public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
				Value val = args.referenceAt(0).unhand();
				if (val.bopEquals(Value.NULL).castToBoolean()) {
					return new OString("");
				}
				if (!(val instanceof ObjectWrapper) && !(val instanceof ValueWrapper))
					return new OString(val.toString());
				ObjectWrapper obj = (ObjectWrapper)val;
				Object[] a = new Object[1];a[0]=obj;
				Object rs = bridge.require("server/misc","OBJSTR",a);
				if (rs == null) return new OString("");
				return new OString(rs.toString());
			} 
			@Override
			protected Value getTypeImpl() { return null; }
		});
		s.createMember("GLOB",Reference.ATTR_PUBLIC).opAssign(new Value() {
			@Override
			public Value callAsFunction( StackFrame sf, MemberTable args ) throws PackagedScriptObjectException {
				Value path = args.referenceAt(0);
				Value body = args.referenceAt(1);
				GLOB(path.toString(),body.toString());
				return Value.NULL;
			}
			@Override
			protected Value getTypeImpl() {
				return null;
			}
			
		});
		s.createMember("STR2HTML",Reference.ATTR_PUBLIC).opAssign(new Value() {
			@Override
			public Value callAsFunction( StackFrame sf, MemberTable args ) throws PackagedScriptObjectException {
				Value str = args.referenceAt(0);
				return new OString(common.stringToHTML(str.toString()));
			}
			@Override
			protected Value getTypeImpl() {
				return null;
			}			
		});
		
		s.createMember("FORMAT_INTEGER",Reference.ATTR_PUBLIC).opAssign(new Value() {
			@Override
			public Value callAsFunction( StackFrame sf, MemberTable args ) throws PackagedScriptObjectException {
				Object a[] = new Object[1];a[0]=args.referenceAt(0).castToInexactNumber();
				return new OString(bridge.require("server/misc","formatInteger",a).toString());
			}
			@Override
			protected Value getTypeImpl() {
				return null;
			}			
		});
		s.createMember("FORMAT_INTEGER_SEPARATOR",Reference.ATTR_PUBLIC).opAssign(s.getMember("FORMAT_INTEGER"));		
		s.createMember("FORMAT_DOUBLE",Reference.ATTR_PUBLIC).opAssign(new Value() {
			@Override
			public Value callAsFunction( StackFrame sf, MemberTable args ) throws PackagedScriptObjectException {
				Object a[] = new Object[1];a[0]=args.referenceAt(0).castToInexactNumber();
				return new OString(bridge.require("server/misc","formatDouble",a).toString());
			}
			@Override
			protected Value getTypeImpl() {
				return null;
			}			
		});
		s.createMember("FORMAT_DATETIME",Reference.ATTR_PUBLIC).opAssign(new Value() {
			@Override
			public Value callAsFunction( StackFrame sf, MemberTable args ) throws PackagedScriptObjectException {
				Object a[] = new Object[1];a[0]=ValueConvertor.convertToJavaObject(args.referenceAt(0).unhand());
				return new OString(bridge.require("server/misc","formatDatetime",a).toString());
			}
			@Override
			protected Value getTypeImpl() {
				return null;
			}			
		});
		s.createMember("FORMAT_DATE",Reference.ATTR_PUBLIC).opAssign(new Value() {
			@Override
			public Value callAsFunction( StackFrame sf, MemberTable args ) throws PackagedScriptObjectException {
				Object a[] = new Object[1];a[0]=ValueConvertor.convertToJavaObject(args.referenceAt(0).unhand());
				return new OString(bridge.require("server/misc","formatDate",a).toString());
			}
			@Override
			protected Value getTypeImpl() {
				return null;
			}			
		});
		s.createMember("FORMAT_TIME",Reference.ATTR_PUBLIC).opAssign(new Value() {
			@Override
			public Value callAsFunction( StackFrame sf, MemberTable args ) throws PackagedScriptObjectException {
				Object a[] = new Object[1];a[0]=ValueConvertor.convertToJavaObject(args.referenceAt(0).unhand());
				return new OString(bridge.require("server/misc","formatTime",a).toString());
			}
			@Override
			protected Value getTypeImpl() {
				return null;
			}			
		});
		s.createMember("FORMAT_HOURS_MINUTES",Reference.ATTR_PUBLIC).opAssign(new Value() {
			@Override
			public Value callAsFunction( StackFrame sf, MemberTable args ) throws PackagedScriptObjectException {
				Object a[] = new Object[1];a[0]=ValueConvertor.convertToJavaObject(args.referenceAt(0).unhand());
				return new OString(bridge.require("server/misc","formatHoursMinutes",a).toString());
			}
			@Override
			protected Value getTypeImpl() {
				return null;
			}			
		});
		s.createMember("FORMAT_DATETIME_MILLIS",Reference.ATTR_PUBLIC).opAssign(new Value() {
			@Override
			public Value callAsFunction( StackFrame sf, MemberTable args ) throws PackagedScriptObjectException {
				Object a[] = new Object[1];a[0]=ValueConvertor.convertToJavaObject(args.referenceAt(0).unhand());
				return new OString(bridge.require("server/misc","formatDatetimeMillis",a).toString());
			}
			@Override
			protected Value getTypeImpl() {
				return null;
			}			
		});
		s.createMember("FORMAT_DATETIME_HOUR_MINUTES",Reference.ATTR_PUBLIC).opAssign(new Value() {
			@Override
			public Value callAsFunction( StackFrame sf, MemberTable args ) throws PackagedScriptObjectException {
				Object a[] = new Object[1];a[0]=ValueConvertor.convertToJavaObject(args.referenceAt(0).unhand());
				return new OString(bridge.require("server/misc","formatDatetimeHoursMinutes",a).toString());
			}
			@Override
			protected Value getTypeImpl() {
				return null;
			}			
		});
			/*
		IMG
		OIMG
		OIMG_CLEAN
		OIMG_NOPREVIEW
		STR2HTML
		DATA
		SDATA
		ABS
		ROUND0
		ROUND1
		ROUND2
		ROUND3
		ROUND4
		ROUND5
		*/
	}
	

	private static String b64chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
	private static Value randomUUID() {		
		StringBuffer b = new StringBuffer();
		for (int i = 0; i < 22; i++) {
			int c = (int)(Math.random()*63.0);
			b.append(b64chars.charAt(c));
		}
		return OString.makeString(b.toString());
	}
	
	public static void GLOB(String path,String body) {
		//--------------------------------
		String aa[] = path.split("\\.");
		oscript.data.Scope c = OscriptInterpreter.getGlobalScope();
		for (int j=0;j<aa.length;j++) {
			int s = Symbol.getSymbol(aa[j]).getId();
			Value m = c.__getInstanceMember(s);
			if (m == null) {
				m = c.createMember(aa[j],Reference.ATTR_PUBLIC);
				c = new oscript.data.BasicScope(c);
				m.opAssign(c);
			} else {
				c = (oscript.data.Scope)m.unhand();
			}
		}
		try {
			OscriptInterpreter.eval( body, c );
		} catch (ParseException e) {
			System.err.println("ERROR : "+e);
		}
	}

	
	
	public static Object callVScriptFunction(Object key,Object body,Object[] args,Object that,Object _super) {
		//---------------------------------------------------------------------------------------------------------
		Value t = bridge._evals.get(key);
		if (t == null) { 
			try {
				oscript.OscriptInterpreter.eval("__eval="+body+";");
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
			t=oscript.OscriptInterpreter.getGlobalScope().getMember("__eval").unhand();
			bridge._evals.put(key.toString(),t);
		}
		Value[] a = new Value[args.length];
		for (int i=0;i<a.length;i++)
			a[i]=ValueConvertor.convertWithCollections(args[i]);		
		//------------------------------------------------------------------------
		oscript.data.BasicScope x = new oscript.data.BasicScope(oscript.OscriptInterpreter.getGlobalScope());
		x.createMember("this", oscript.data.Reference.ATTR_PUBLIC);
		x.createMember("super", oscript.data.Reference.ATTR_PUBLIC);
		x.getMember("this").opAssign(ValueConvertor.convert(that));
		final Object fsuper = _super;
		if (_super == null)  
			x.getMember("super").opAssign(Value.NULL);
		else if (_super instanceof Value)
			x.getMember("super").opAssign((Value)_super);
		else
			x.getMember("super").opAssign(new Value() {				
				@Override
				public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
					return ((callback)fsuper).call(args);
				}
				@Override
				protected Value getTypeImpl() {
					return null;
				}
			});
		return ValueConvertor.convertToJavaObject(t.callAsExtends(oscript.util.StackFrame.currentStackFrame(), x, new oscript.data.OArray(a)));
	}
	
	public static interface callback {
		Value call(Object args);
	}

}