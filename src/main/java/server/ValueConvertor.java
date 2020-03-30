package server; 

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import bridge.bridge;
import oscript.data.OArray;
import oscript.data.Value;
import oscript.data.ValueWrapperTempReference;
import oscript.util.StackFrame;
import oscript.varray.Map;
import oscript.varray.ObjMultipleMap;
import oscript.varray.Vector;

/**
 * Converter for DB values to VScript values.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 17895 $
 * @date 	$LastChangedDate: 2014-09-15 18:06:15 +0300 (Mo, 15 Sep 2014) $
 * @project VisionR Server 
 */
@SuppressWarnings("unused")
public class ValueConvertor 
{
	public static Value convertWithCollections(Object obj) 
	{
		 if (obj instanceof java.util.Map) {
			 return convertMap((java.util.Map)obj);
		 } else if (obj instanceof Collection) {
			return new Vector((Collection)obj);
		 }
		 return convert(obj);
	}

	public static Value convert(Object obj) {
		if (obj == null)
			return Value.NULL;
		if (obj instanceof Value)
			return (Value)obj;
		if (obj instanceof Date) 
			return new ODateTimeValue((Date)obj,0);
		return oscript.data.JavaBridge.convertToScriptObject(obj);
	}
	
	public static Value convertWithCollectionsWithoutDefault(Object obj) 
	{
		 if (obj instanceof java.util.Map) {
			return convertMap((java.util.Map)obj);
		 } else if (obj instanceof Collection) {
			return new Vector((Collection)obj);
		 }
		 return convertWithoutDefault(obj);
	}
	
	public static Value convertMap(java.util.Map obj) {
		Map m = new Map();
		Iterator<java.util.Map.Entry> it = obj.entrySet().iterator();
		while (it.hasNext()) {
			java.util.Map.Entry e = it.next();
			Object k = e.getKey();
			Object v = e.getValue();
			if (k instanceof String && v instanceof String && k.toString().equals("$$TYPE$$")) {					
				switch (v.toString()) {
					case "event" :
						// skip
						m.setForceResolvable(true);
						continue;
				}					
			}
			m.put(k,v);			
		}
		return m;
	}
	

	public static Value convertWithoutDefault(Object obj) {
		if (obj == null)
			return Value.NULL;
		if (obj instanceof Date) 
			return new ODateTimeValue((Date)obj,0);
		return null;
	}
	
	public static Object convertToJavaObject(Object val) {
		if (val == null)
			return null;
		/*if (val instanceof ObjectWrapper) {
			return ((ObjectWrapper)val).getDBObject();
		}*/
		if (val instanceof Value)
			val = ((Value)val).castToJavaObject();
		if (val instanceof ODateTimeValue)
			val = ((ODateTimeValue)val).getDate();
		return val;
	}

	
}
