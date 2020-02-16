exports.JS2JAVA = JS2JAVA;
exports.JAVA2JS = JAVA2JS;
//---------------------------------------------------------
var makeObject = function(shkey,id) {
	makeObject = server.ObjectWrapper.makeObject$S$J;
	return makeObject(shkey,id);
};
//---------------------------------------------------------
function JS2JAVA(val) 
{
	if (val instanceof db) {		
		return makeObject(val.SCHEMA.KEY,val.id);
	} else if (val instanceof Array) {
		var t = Clazz.array(java.lang.Object, [val.length]);
		for (var i=0;i<val.length;i++) t[i]=JS2JAVA(val[i]);
		return t;
	} else if (val instanceof Date) {
		return new java.util.Date(val.getTime());
	} else if (val instanceof Object) {
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
			var v = JAVA2JS(t.get$O(k));
			a[k]=v;
		}
		return a;
	} else if (Clazz.instanceOf(val, "java.util.Date")) {
		return new Date(val.getTime());
	} else if (Clazz.instanceOf(val, "java.lang.Double")) {
		return val.doubleValue();		
	} else if (Clazz.instanceOf(val, "java.lang.Number")) {
		return val.longValue();
	} else if (Clazz.instanceOf(val, "java.lang.String")) {
		return val.toString();
	} else if (Clazz.instanceOf(val, "java.lang.Boolean")) {
		return val.booleanValue();
	}
	return undefined;
}
