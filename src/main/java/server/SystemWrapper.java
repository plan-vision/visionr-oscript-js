package server;
import oscript.data.OBoolean;
import oscript.data.OException;
import oscript.data.OString;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.StackFrame;
/**
 * Wrapper for System operations in the VScript. This is the main interface to
 * the system script functions, for example: var a =
 * system.conf.getInt("web.session.expiry");
 * 
 * @author plan-vision
 * @version $LastChangedRevision$
 * @date $LastChangedDate$
 * @project VisionR Server
 */
public class SystemWrapper extends OString {

	public SystemWrapper() {
		super("system");
	}
	
	public Value resolve(final int symbol) {
		if (symbol == Symbols.CONF_TYPE || symbol == Symbols.CONF_TYPE2) {
			// return system.conf
			return new Value() {
				public Value getMember(int symbol2, boolean exception) {
					// return system.conf.get
					if (symbol2 == Symbols.GET)
					return new Value() {
						// system.conf.get(key)
						public Value callAsFunction(StackFrame sf,
								oscript.util.MemberTable args) {
							/*String key = args.referenceAt(0).castToString();
							String defValue = null;
							String res = null;
							if (args.length() == 2) {
								defValue = args.referenceAt(1).castToString();
								res = CorePrefs.getStrPref(key,defValue);
							} else {
								res = CorePrefs.getStrPref(key);
							}							
							return res == null ? Value.NULL : OString.makeString(res);*/
							// TODO XCONF data JS 
							return Value.NULL;
						}

						@Override
						protected Value getTypeImpl() {
							return this;
						}
					};
					return Value.NULL;
				}

				@Override
				protected Value getTypeImpl() {
					return this;
				}
			};
		}
		if (symbol == Symbols.LIBVSP) 
		{
			return new Value() {
				public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) 
					throws PackagedScriptObjectException
				{
					if (args.length() != 1)
						throw PackagedScriptObjectException.makeExceptionWrapper(
								new OException("Function system.libvsp(path): wrong agruments!"));
					String path = args.referenceAt(0).castToString();
				 	String jsc = "<%vsp:include page=\"${web.lib.dir}/"+path+"\"/>";
				 	Value ta[]=new Value[2];
				 	ta[0]=new OString(jsc);
				 	ta[1]=OBoolean.TRUE;
				 	return SystemWrapper.this.getMember("vsp").callAsFunction(ta);
				}

				@Override
				protected Value getTypeImpl() {					
					return this;
				}
			};
		}

		if (symbol == Symbols.LIBVSC) 
		{
			return new Value() {
				public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) 
					throws PackagedScriptObjectException
				{
					if (args.length() != 1)
						throw PackagedScriptObjectException.makeExceptionWrapper(
								new OException("Function system.libvsc(path): wrong agruments!"));
					String path = args.referenceAt(0).castToString();
				 	String jsc = "<%vsp:include script=\"${web.lib.dir}/"+path+"\"/>";
				 	Value ta[]=new Value[2];
				 	ta[0]=new OString(jsc);
				 	ta[1]=OBoolean.TRUE;
				 	return SystemWrapper.this.getMember("vsp").callAsFunction(ta);
				}

				@Override
				protected Value getTypeImpl() {					
					return this;
				}
			};
		}
		if (symbol == Symbols.CLEAR_WEB_CACHE_ENTRY_TYPE)
			return new Value() {
				public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) throws PackagedScriptObjectException {
					return Value.NULL;
				}	
				@Override
				protected Value getTypeImpl() { return this; }
			};
			
		return Value.NULL;
	}

	public Value getMember(int id, boolean exception) {
		Value val = resolve(id);
		if (val != null)
			return val;
		return super.getMember(id, exception);
	}

	@Override
	protected Value getTypeImpl() {
		return this;
	}
}
