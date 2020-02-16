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
			OscriptInterpreter.eval(" \"\" + function(a){ return a + 6+a*3 - 6; };");	// force load, do not execute
			OscriptInterpreter.eval("var a=[];a.push(2);");
			//------------------------------------------
			//GLOB("reports.misc.test",...)
			// TEST 
			//OscriptInterpreter.eval("GLOB(\"a.b\",\"public function b() {}\");");
			//OscriptInterpreter.eval("GLOB(\"a.c\",\"public function b() {}\");");
			//OscriptInterpreter.eval("t.b();");
			//OscriptInterpreter.eval("GLOB(\"aa\",\"\");");			
			//------------------------------------------
			bridge.boostrap();
			base.init();
		} catch (ParseException e) { 
			System.err.println("ERROR : "+e);
		}
	}	
}
