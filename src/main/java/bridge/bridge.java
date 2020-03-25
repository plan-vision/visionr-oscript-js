package bridge; 
import java.util.HashMap;
 
import oscript.OscriptInterpreter;
import oscript.data.Value;
 
public class bridge  
{ 
	public static String getSchemaName(String odefkeey) {
		return null; 
	} 
	
	public static String getVScriptPropertyDefaultValue(String odefkey,String pro) {
		return null; 
	}
	public static String getPropertyOptionSet(String odefkey,String pro) {
		// only if option
		return null;
	}
	public static long getObjectDefId(String odefkey) {		
		return 0;
	}
	public static String getObjectDefById(long id) {
		return null;
	}
	public static String getModuleById(long id) { 
		return null;
	}
	public static long getModuleId(String module) { 
		return 0;
	}
	public static boolean hasModule(String module) {
		return false;
	}
	public static boolean moduleHasChild(String module,String odef) {
		return false;
	}
	public static String[] getModules() {
		return null;
	}
	public static String[] getModuleChildren(String module) {
		return null;
	}
	public static String[] getObjectDefProperties(String key) {
		return null;
	}
	public static String getTopObjectDef(String odkey) {
		return odkey;
	}
	// odkeay allInherits.contains(key)
	public static boolean objectDefAllInheritsContains(String odkey,String key) {
		return false;
	}
	public static long getObjectDefProperyId(String odefkey,String code) {
		return 0;
	}
	public static boolean objectDefHasPropery(String odefkey,String code) 
	{	
		return false;
	}
	// MULT @ POS
	public static Object getObjectValuePos(String odefkey,long id,String pro,int pos) {
		return null;
	}
	// MULT @ POS
	public static Object getObjectOldValuePos(String odefkey,long id,String pro,int pos) {
		return null;
	}

	public static Object getObjectOldValue(String odefkey,long id,String pro) {
		return null;
	}
	public static String getObjectOldI18nValue(String odefkey,long id,String pro,String lang) {
		return null;
	}
	public static int getObjectOldValueCount(String odefkey,long id,String pro) {
		return 0;
	}
	public static int getObjectValueCount(String odefkey,long id,String pro) {
		return 0;
	}
	public static Object getObjectValue(String odefkey,long id,String pro) {
		return null;
	}
	public static String getObjectI18nValue(String odefkey,long id,String pro,String lang) {
		return null;
	}
	public static String[] getObjectI18nValueLanguages(String odefkey,long id,String pro) {
		return null;
	}
	public static String[] getObjectOldI18nValueLanguages(String odefkey,long id,String pro) {
		return null;
	}
	public static Object getObjectByCode(String odefkey,String code) {
		return null;
	} 
	public static String[] SELECT(String odkey,String condition,Object params)  {
		return null;
	}
	public static boolean isDeleted(String odkey,long id) {
		return false;
	}
	public static boolean isInserted(String odkey,long id) {
		return false;
	}
	public static Object callScript(String code,Object[] args,Object that,Object _super) {
		return null;
	}		
	public static String getParentPropertyObjectDef(String odkey,String pro) {
		return null; // SCRIPT code
	}
	public static boolean isRelationNotOption(String odkey,String pro) {
		return false;
	}
	public static boolean isDataTypeDouble(String odkey,String pro) {
		return false;
	}
	public static boolean isRelation(String odkey,String pro) {
		return false;
	}
	public static boolean isMultiple(String odkey,String pro) {
		return false;
	}
	public static boolean isI18n(String odkey,String pro) {
		return false;
	}
	public static String currentLang() {return null;}
	public static String defaultLang() {return null;}
	public static int getObjectValuePosition(String odefkey,long id,String pro,Object val) {
		return -1;
	}
	public static int getObjectValuePositionRel(String odefkey,long id,String pro,long valid) {
		return -1;
	}
	public static int getObjectOldValuePosition(String odefkey,long id,String pro,Object val) {
		return -1;
	}
	public static int getObjectOldValuePositionRel(String odefkey,long id,String pro,long valid) {
		return -1;
	}
	//---------------------------
	public static void deleteObject(String odefkey,long id) {
	}
	
	public static Value newObject(String odefkey,Object args) {
		return null;
	}
	
	public static void deleteObjectValue(String odefkey,long id,String pro) {
		// WHOLE VALUE
	}
	public static void deleteObjectValuePos(String odefkey,long id,String pro,int pos) {
		// by pos
	}
	public static void deleteObjectValueLang(String odefkey,long id,String pro,String lang) {
		// by lang
	}
	public static void copyObjectValue(String odefkey,long id,String pro,String todefkey,long tid,String tpro) {
		// COPY WHOLE VALUE
	}
	
	public static void setObjectValue(String odefkey,long id,String pro,Object val) {
		// WHOLE VALUE
	}
	public static void setObjectValueRel(String odefkey,long id,String pro,String valodkey,long valid) {
		// SET REL VALUE
	}
	public static void setObjectValuePos(String odefkey,long id,String pro,int pos,Object val) {
		// by pos
	}
	public static void setObjectValueLang(String odefkey,long id,String pro,String val,String lang) {
		// by lang
	}
	public static void pushObjectValue(String odefkey,long id,String pro,Object val) {
		// MULTIPLE ONLY 
	}
	public static void pushObjectValueRel(String odefkey,long id,String pro,String valodkey,long valid) {
		// MULTIPLE ONLY 
	}
	public static boolean isNestedTransaction() {
		return false;
	}
	public static String getProgress() { return null; }
	public static void setProgress(String val) { }
	public static Object require(String require,String api,Object[] args) {
		return null;
	}
	
	public static Value getUser() { return null; } 
	//---------------------------------------------------------------
	static HashMap<String,Value> _evals=new HashMap();
	public static void boostrap() {
		System.out.println(">>> VSC JS BOOT <<<");
		OscriptInterpreter.getGlobalScope().createMember("__eval",0);
	}
}


