package test; 
import bridge.base;
import bridge.bridge; 
import oscript.OscriptInterpreter;
import oscript.data.FunctionScope;
import oscript.data.JavaBridge;
import oscript.parser.ParseException;
import server.ValueConvertor;
public class EntryPoint {
	
	public static void main(String[] args) {
		try {
			new FunctionScope(); // force load 
			ValueConvertor.convert(null); // force load
			JavaBridge.convertToScriptObject(0); // force load
			new FunctionScope();  // force load
			new ParseException(); // force load
			new oscript.exceptions.PackagedScriptObjectException(null);	// force load
			/*if (System.currentTimeMillis() == 0) */OscriptInterpreter.eval(" \"\" + function(a){ return a + 2+a*3 - 6; };");	// force load, do not execute	
			bridge.boostrap();
			base.init();
		} catch (ParseException e) { 
			System.err.println("ERROR : "+e);
		}
	}
}
