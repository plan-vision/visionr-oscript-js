exports.JS2JAVA = JS2JAVA;
exports.JAVA2JS = JAVA2JS;
//---------------------------------------------------------
var makeObject = function(shkey,id) {
	return server.ObjectWrapper.makeObject$S$J(shkey,id);
};
var makeObjectDef = function(shkey) {
	return server.ObjectWrapper.makeObjectDef$S$Z(shkey,false);
};
var makeProperty = function(shkey,code) {
	return server.ObjectWrapper.makeProperty$S$S(shkey,code);
};
//---------------------------------------------------------
function JS2JAVA(val) 
{
	if (val instanceof Function) {
		// schema only support
		if (!val.KEY) {
			debugger;
			throw "cnv : JS2JAVA unsupported type "+val;
		}
		return makeObjectDef(val.KEY);
	} else if (val instanceof db) {		
		return makeObject(val.SCHEMA.KEY,val.id);
	} else if (val instanceof Array) {
		var t = Clazz.array(java.lang.Object, [val.length]);
		for (var i=0;i<val.length;i++) t[i]=JS2JAVA(val[i]);
		return t;
	} else if (val instanceof Date) {
		return new java.util.Date(val.getTime());
	} else if (val instanceof Object) {
		if (val._type == "WRP")
			return val;
		if (val._type == "PRO" && val.code && val._schema) // todo check exact ref storage.defs.properties
			return makeProperty(val._schema,val.code)
		var t = new java.util.HashMap()
		for (var i in val) t.put$TK$TV(i,JS2JAVA(val[i]));
		return t;
	} else if (typeof val == "number") {
		if (Math.floor(val) == val)
			return new java.lang.Long(val);
		return new java.lang.Double(val);
	} else if (typeof val == "boolean") {
		 return new java.lang.Boolean(val);
	} else if (typeof val == "string") {
		return new java.lang.String(val);
	}
	return null;
};

function JAVA2JS(val) { 
	if (Clazz.instanceOf(val, "oscript.data.Value")) {
		val=val.castToJavaObject$();
		if (Clazz.instanceOf(val, "server.ObjectWrapper")) {
			var od = db.find(val.odkey);
			if (!od) throw "cnv.JAVA2JS : missing schema "+pro+" of "+val.odkey;
			var obj = od.byId(val._objId);
			if (!obj) console.error("cnv.JAVA2JS : missing object "+val.odkey+":"+val._objId);
			return obj;
		}
		if (val instanceof oscript.varray.Map) {
			var isArray=val instanceof oscript.varray.Vector;
			var l = val.length$();
			if (l == 0) {				
				if (isArray) return [];
				return {};
			}
			if (!isArray) {
				var r={};
				var ki = val.keyIterator$();
				while (ki.hasNext$()) {
					var k = ki.next$().unhand$();
					r[k.castToString$()]=JAVA2JS(val.elementAt$oscript_data_Value(k).unhand$());
				}
				return r;
			} else {
				var r=[];
				var ki = val.keyIterator$();
				while (ki.hasNext$()) {
					var k = ki.next$().unhand$();
					r.push(JAVA2JS(val.elementAt$oscript_data_Value(k).unhand$()));
				}
				return r;				
			}			
		}
	}
	if (Clazz.instanceOf(val, Clazz.array(java.lang.Object, -1))) {
		// ARRAY
		var a=[];
		for (var i=0;i<val.length;i++) a[i]=JAVA2JS(val[i]);
		return a;
	} else if (Clazz.instanceOf(val, "java.util.Map")) {
		var a={};
		var it  = val.keySet$().iterator$();
		while (it.hasNext$()) {
			var k = it.next$().toString();
			var v = JAVA2JS(val.get$O(k));
			a[k]=v;
		}
		return a;
	} else if (Clazz.instanceOf(val, "java.util.Date")) {
		return new Date(val.getTime());
	} else if (Clazz.instanceOf(val, "java.lang.Double")) {
		return val.doubleValue$();		
	} else if (Clazz.instanceOf(val, "java.lang.Number")) {
		return val.longValue$();
	} else if (Clazz.instanceOf(val, "java.lang.String")) {
		return val.toString();
	} else if (Clazz.instanceOf(val, "java.lang.Boolean")) {
		return val.booleanValue();
	}
	return undefined;
}
