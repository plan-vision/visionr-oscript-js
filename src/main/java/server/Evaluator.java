package server;

import java.io.File;
import bridge.bridge;
import oscript.data.Value;
import oscript.util.MemberTable;
import oscript.util.StackFrame;

public class Evaluator 
{
	
	protected static Boolean DEBUG_SCRIPT = null;
	protected int[] params;
	protected long executionTimeout = 0;
	protected static File sourceFolder;

	public static class SuperWrapper extends Value 
	{
		public String pro;
		public String odkey;
		public long id;
		
		public SuperWrapper() {
			super();
		}

		public SuperWrapper(String odkey,long id,String pro) {
			super();
			this.odkey=odkey;
			this.pro=pro;
			this.id=id;
		}

		public Value callAsFunction(StackFrame sf, oscript.util.MemberTable params) {
			String tscr = bridge.getPropertyDefaultValueScript(odkey,pro);
			if (tscr == null) return Value.UNDEFINED;
			String podkey = bridge.getParentPropertyObjectDef(odkey,pro);
			while (podkey != null && bridge.getPropertyDefaultValueScript(podkey,pro) == tscr)
				podkey = bridge.getParentPropertyObjectDef(odkey,pro);
			if (podkey == null || bridge.getPropertyDefaultValueScript(podkey,pro) == null)
				return Value.UNDEFINED;
			//--------------------------------------------------------------
			
			//--------------------------------------------------------------
			Object[] t = new Object[params.length()];
			for (int i=0;i<params.length();i++)
				t[i]=ValueConvertor.convertToJavaObject(params.referenceAt(i).unhand());
			return ValueConvertor.convert(bridge.callScriptProperty(podkey, id, pro,t));
		}

		@Override
		protected Value getTypeImpl() {
			return this;
		}
	}

	public static final SuperWrapper __emptySuper = new SuperWrapper() 
	{
		public Value callAsFunction(StackFrame sf, MemberTable params) {return Value.NULL;};
	};

	public static Value executeDefaultValue(StackFrame sf, MemberTable params, String odkey,long id,String pro) 
	{
		Object[] t = new Object[params.length()];
		for (int i=0;i<params.length();i++)
			t[i]=ValueConvertor.convertToJavaObject(params.referenceAt(i).unhand());
		return ValueConvertor.convert(bridge.callScriptProperty(odkey,id,pro,t));
	}
}
