/*
 * This is the modified content of site/swingjs/j2s/bridge/bridge.js
 * Will be overriden before compile
 */
var cnv = require("../../../../src/js/cnv");
var strstream = require("../../../../src/js/strstream");
var JS2JAVA = cnv.JS2JAVA; 
var JAVA2JS = cnv.JAVA2JS;
(function() {
	var P$ = Clazz.newPackage("bridge"), I$ = [ [ 0, 'java.util.HashMap',
			'oscript.OscriptInterpreter', 'oscript.data.Value',
			'server.ValueConvertor' ] ], $I$ = function(i) {
		return I$[i] || (I$[i] = Clazz.load(I$[0][i]))
	};
	var C$ = Clazz.newClass(P$, "bridge");
	C$._evals = null;

	C$.$clinit$ = function() {
		Clazz.load(C$, 1);
		C$._evals = Clazz.new_($I$(1));
	}

	Clazz.newMeth(C$, '$init$', function() {
	}, 1);

	Clazz.newMeth(C$,'callVScriptFunction$S$S$OA',function(key, body, args) {
		var t = C$._evals.get$O(key);
		if (t == null) {
			//$I$(2).eval$S(body );
			$I$(2).eval$S("__eval = " + body + ";");
			t = $I$(2).getGlobalScope$().getMember$S("__eval").unhand$();
			C$._evals.put$TK$TV(key, t);
		}
		args=args||[];
		var a = Clazz.array($I$(3), [ args.length ]);
		for (var i = 0; i < a.length; i++) a[i] = $I$(4).convertWithCollections$O(args[i]);
		return $I$(4).convertToJavaObject$O(t.callAsFunction$oscript_data_ValueA(a));
	}, 1);

	//-----------------------------------------------------------------------------------
	Clazz.newMeth(C$, 'objectDefHasPropery$S$S', function(odkey, code) {
		return !!db.find(odkey).getProperty(code);
	}, 1);
	Clazz.newMeth(C$, 'isMultiple$S$S', function(odkey, pro) {
		var p = db.find(odkey).getProperty(pro);
		if (!p) return false;return !!p.meta.multiple;
	}, 1);
	Clazz.newMeth(C$, 'getObjectValue$S$J$S', function(odkey, id, pro) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		var val = JS2JAVA(obj[pro]);return val;
	}, 1);
	
	Clazz.newMeth(C$, 'getObjectValueCount$S$J$S', function(odkey, id, pro) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		var val = obj[pro];if (val == undefined) return 0;
		if (val instanceof Array) return val.length;return 1;
	}, 1);
	Clazz.newMeth(C$, 'require$S$S$OA', function (require, api, args) {
		debugger;
		return null;
	}, 1);
	//-----------------------------------------------------------------------------------
	Clazz.newMeth(C$, 'getObjectDefId$S', function(odkey) { return db.find(odkey).id; }, 1);
	Clazz.newMeth(C$, 'getObjectDefById$J', function(id) { var odef = storage.defs.odById[id]; if (!odef) throw new "bridge.js Can not find schema by id "+id; return odef.KEY; }, 1);
	Clazz.newMeth(C$, 'getModuleId$S', function(module) { return storage.defs.modules[module].id; }, 1);
	Clazz.newMeth(C$, 'hasModule$S', function(module) { return !!storage.defs.modules[module]; }, 1);
	Clazz.newMeth(C$, 'moduleHasChild$S$S', function(module, odef) { return !!db.find(module+"."+odef); }, 1);
	Clazz.newMeth(C$, 'getModules$', function() { return Object.keys(storage.defs.modules); }, 1);
	Clazz.newMeth(C$, 'getModuleChildren$S', function(module) { var res = Object.keys(db[module]); return res; }, 1);
	Clazz.newMeth(C$, 'isRelationNotOption$S$S', function(odkey, pro) {var m = db.find(odkey).getProprty(pro).meta;return m.relation && !m.option;}, 1);
	Clazz.newMeth(C$, 'isDataTypeDouble$S$S', function(odkey, pro) {var m = db.find(odkey).getProprty(pro).meta;return !!m.double;}, 1);
	Clazz.newMeth(C$, 'isRelation$S$S', function(odkey, pro) {var m = db.find(odkey).getProprty(pro).meta;return !!m.relation;}, 1);
	Clazz.newMeth(C$, 'isI18n$S$S', function(odkey, pro) {var m = db.find(odkey).getProprty(pro).meta; return !!m.i18n;}, 1);
	Clazz.newMeth(C$, 'getPropertyOptionSet$S$S', function(odkey, pro) {var m = db.find(odkey).getProprty(pro).meta;return m.optionSet;}, 1);
	Clazz.newMeth(C$, 'currentLang$', function() { return storage.defs.currentLang; }, 1);
	Clazz.newMeth(C$, 'defaultLang$', function() { return storage.defs.defaultLang; }, 1);
	Clazz.newMeth(C$, 'getObjectByCode$S$S', function (odkey, code) { var o = db.find(odkey).byCode(code);if (!o) return;return JS2JAVA(o); }, 1);
	//-----------------------------------------------------------------------------------
	Clazz.newMeth(C$, 'getModuleById$J', function(id) {
		var mods = storage.defs.modules;
		if (mods._idByCode) 
			return mods._idByCode[id];
		var t={};
		for (var i in mods) t[mods[i].id]=i;
		return (mods._idByCode=t)[id];
	}, 1);
	var _pdefproscache={};
	Clazz.newMeth(C$, 'getObjectDefProperties$S', function(key) {
		var t = _pdefproscache[key];if (t) return t; 
		var res=[];
		var pros = db.find(key).properties;
		for (var i=0;i<pros.length;i++) {
			var e = pros[i];
			res.push(e.code);
		}
		return _pdefproscache[key]=t;
	}, 1);
	var _topodcache={};
	Clazz.newMeth(C$, 'getTopObjectDef$S', function(odkey) {
		var t = _topodcache[key];if (t) return t;
		var t = root.db.find(odkey);
		while (t.parentSchema) t=t.parentSchema;
		return _topodcache[key]=t.KEY;
	}, 1);
	Clazz.newMeth(C$, 'getObjectDefProperyId$S$S', function(odkey, code) {
		var od = db.find(odkey);if (!od) return;
		var pro = od.getProperty(code);
		if (!pro) return;
		return pro.meta.id;
	}, 1);
	var _odinhcache={};
	Clazz.newMeth(C$, 'objectDefAllInheritsContains$S$S', function(odkey, key) {
		var k = odkey+">"+key;
		var t = _odinhcache[k]; if (t != undefined) return t;
		var od = db.find(odkey);
		while (od) {
			if (od.KEY == key)
				return _odinhcache[k]=true; 
			od = od.parentSchema; 
		}
		return _odinhcache[k]=false;
	}, 1);
	Clazz.newMeth(C$, 'getParentPropertyObjectDef$S$S', function(odkey, pro) {
		var od = db.find(odkey);
		var p = od.getProperty(pro);
		od = db.find(p.meta.schema).parentSchema;
		if (!od) return null;
	    p = od.getProperty(pro);
	    if (!p) return null;
	    return p.meta.schema;
	}, 1);
	Clazz.newMeth(C$, 'getObjectValuePos$S$J$S$I', function(odkey, id, pro,pos) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = obj[pro];if (!(obj instanceof Array)) return null;
		return JS2JAVA(val[pos]);
	}, 1);
	Clazz.newMeth(C$, 'getObjectI18nValue$S$J$S$S', function(odkey, id, pro, lang) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = obj[pro];if (!val) return null;
		return JS2JAVA(val[lang]);
	}, 1);
	Clazz.newMeth(C$, 'getObjectI18nValueLanguages$S$J$S', function(odkey, id, pro) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = obj[pro];if (!val) return null;
		return JS2JAVA(Object.keys(val));
	}, 1);
	//-----------------------------------------------------------------------------------
	Clazz.newMeth(C$, 'getObjectValuePosition$S$J$S$O', function(odkey, id, pro, val) {
		if (val instanceof Date) val = val.getTime();
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = obj[pro];
		if (obj instanceof Array) {
			for (var pos=0;pos<obj.lengt;pos++) {
				var e = obj[pos];
				if (e instanceof Date) e=e.getTime();
				if (e == val)
					return pos;
			}
		}
		return -1;
	}, 1);
	Clazz.newMeth(C$, 'getObjectValuePositionRel$S$J$S$J', function(odkey, id, pro, valid) {		
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = obj[pro];
		if (obj instanceof Array) {
			for (var pos=0;pos<obj.lengt;pos++) {
				var e = obj[pos];
				if (e.id == valid) return pos;
			}
		}
		return -1;
	}, 1);
	Clazz.newMeth(C$, 'SELECT$S$S$O', function(odkey,condition, params) {
		params=JAVA2JS(params);
		var groupBy = undefined;
		var orderBy = undefined;
		var limit = undefined;
		var where = undefined;
		if (condition) {
			condition=" "+condition;
			var c = condition.toUpperCase();
			var i = c.lastIndexOf(" LIMIT ");
			if (i > 0) {
				limit = condition.substring(i+7).trim();
				condition = condition.substring(0,i);
				c = condition.toUpperCase();
			}
			var i = c.lastIndexOf(" GROUP BY ");
			if (i > 0) {
				groupBy = condition.substring(i+10).trim();
				condition = condition.substring(0,i);
				c = condition.toUpperCase();
			}
			var i = c.lastIndexOf(" ORDER BY ");
			if (i > 0) {
				orderBy = condition.substring(i+10).trim();
				condition = condition.substring(0,i);
			}
			where = condition.trim();
			if (!where) where=undefined;
		}
		params=params||{};
		params["where"]=where;
		params["orderBy"]=orderBy;
		params["groupBy"]=groupBy;
		params["limit"]=limit;
		var res = db.find(odkey).SELECT(params);
		return JS2JAVA(res);
	}, 1);
	//-----------------------------------------------------------------------------------
	Clazz.newMeth(C$, 'getVScriptPropertyDefaultValue$S$J$S', function (odkey, id, pro) {
		debugger;
		return null;
	}, 1);
	Clazz.newMeth(C$, 'getVScriptPropertyDefaultValueBody$S$J$S', function (odkey, id, pro) {
		debugger;
		return null;
	}, 1);	
	Clazz.newMeth(C$, 'getObjectOldValuePos$S$J$S$I', function(odkey, id,pro, pos) {
		debugger;
		return null;
	}, 1);
	Clazz.newMeth(C$, 'getObjectOldValue$S$J$S', function(odkey, id, pro) {
		debugger;
		return null;
	}, 1);
	Clazz.newMeth(C$, 'getObjectOldI18nValue$S$J$S$S', function(odkey, id,pro, lang) {
		debugger;
		return null;
	}, 1);
	Clazz.newMeth(C$, 'getObjectOldValueCount$S$J$S',function(odkey, id, pro) {
		debugger;
		return 0;
	}, 1);
	Clazz.newMeth(C$, 'getObjectOldI18nValueLanguages$S$J$S', function(odkey,id, pro) {
		debugger;
		return null;
	}, 1);
	Clazz.newMeth(C$, 'isDeleted$S$J', function(odkey, id) {
		debugger;
		return false;
	}, 1);
	Clazz.newMeth(C$, 'isInserted$S$J', function(odkey, id) {
		debugger;
		return false;
	}, 1);
	Clazz.newMeth(C$, 'callScript$S$OA', function(code, args) {
		debugger;
		return null;
	}, 1);
	Clazz.newMeth(C$, 'callScriptProperty$S$J$S$OA', function(odkey, id, pro, args) {
		debugger;
		return null;
	}, 1);
	Clazz.newMeth(C$, 'getPropertyDefaultValueScript$S$S',function(odkey, code) {
		debugger;
		return null;
	}, 1);
	Clazz.newMeth(C$, 'getObjectOldValuePosition$S$J$S$O', function(odkey, id, pro, val) {
		debugger;
		return -1;
	}, 1);
	Clazz.newMeth(C$, 'getObjectOldValuePositionRel$S$J$SJ', function(odkey, id, pro, valid) {
		debugger;
		return -1;
	}, 1);
	Clazz.newMeth(C$, 'deleteObjectValue$S$J$S', function(odkey, id, pro) {
		debugger;
	}, 1);
	Clazz.newMeth(C$, 'deleteObjectValuePos$S$J$S$I', function(odkey, id, pro, pos) {
		debugger;
	}, 1);
	Clazz.newMeth(C$, 'deleteObjectValueLang$S$J$S$S', function(odkey, id, pro, lang) {
		debugger;
	}, 1);
	Clazz.newMeth(C$, 'copyObjectValue$S$J$S$S$J$S', function(odkey, id, pro, todkey, tid, tpro) {
		debugger;
	}, 1);
	Clazz.newMeth(C$, 'setObjectValue$S$J$S$O', function(odkey, id, pro, val) {
		debugger;
	}, 1);
	Clazz.newMeth(C$, 'setObjectValueRel$S$J$S$S$J', function(odkey, id, pro, valodkey, valid) {
		debugger;
	}, 1);
	Clazz.newMeth(C$, 'setObjectValuePos$S$J$S$I$O', function(odkey, id, pro, pos, val) {
		debugger;
	}, 1);
	Clazz.newMeth(C$, 'setObjectValueLang$S$J$S$S$S', function(odkey, id, pro, val, lang) {
		debugger;
	}, 1);
	Clazz.newMeth(C$, 'pushObjectValue$S$J$S$O', function(odkey, id, pro, val) {
		debugger;		
	}, 1);
	Clazz.newMeth(C$, 'pushObjectValueRel$S$J$S$S$J', function(odkey, id, pro, valodkey, valid) {
		debugger;
	}, 1);
	Clazz.newMeth(C$, 'isNestedTransaction$', function() {
		debugger;
		return false;
	}, 1);
	Clazz.newMeth(C$, 'getProgress$', function() {
		debugger;
		return null;
	}, 1);
	Clazz.newMeth(C$, 'setProgress$S', function(val) {
		debugger;
	}, 1);
	//---------------------------------------------------------------------------------
	Clazz.newMeth(C$, 'boostrap$', function() {
		swingjs.JSUtil.getResourceAsStream$S=function(name) {switch (name) {
				case "sun/util/resources/CalendarData.properties" : return strstream.createInputStream("firstDayOfWeek=1\nminimalDaysInFirstWeek=1");
				default : console.warn(">> bridge.js not default str content for resource "+name);return strstream.createInputStream("");
		}}
		//--------------------------------------------------------------------------
		$I$(2).getGlobalScope$().createMember$S$I("__eval", 0x04000000 /* public > reference.java */);
		//--------------------------------------------------------------------------
		window.VSCRIPT={			
				call : function(key,body,args) {
					try {
						return JAVA2JS(C$.callVScriptFunction$S$S$OA(key,body,JS2JAVA(args)));
					} catch(e) {
						if (typeof e.getMessage == "function")
							console.error(e.getMessage());
						else
							console.error(e.detailMessage || (e.val && e.val.str ? e.val.str : e));
					}
				}
		};
		//window.VSCRIPT.call("test","function(){ return 1+2;}",[])
		System.out.println$S(">>> VSC JS BOOT <<<");
	}, 1);
	Clazz.newMeth(C$);
})();
;
Clazz.setTVer('3.2.4.07');// Created 2019-06-05 12:10:16 Java2ScriptVisitor
							// version 3.2.4.07 net.sf.j2s.core.jar version
							// 3.2.4.07
