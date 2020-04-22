/*
 * This is the modified content of site/swingjs/j2s/bridge/bridge.js
 * Will be overriden before compile
 */
var cnv = require("../../../../src/js/cnv");
var strstream = require("../../../../src/js/strstream");
var JS2JAVA = cnv.JS2JAVA; 
var JAVA2JS = cnv.JAVA2JS;

function fnd(odkey,pro) {
	var od = db.find(odkey);
	if (od) {
		var p = od.getProperty(pro);
		if (!p) throw "bridge.fnd : missing property "+pro+" of "+odkey;				
		return p.meta;
	}
	switch (odkey) 
	{
		case "core.objectdef" :
		case "core.property" :
			switch (pro) {
				case "code" : return {
					varchar: true,
					string: true
				};
				case "id" : return {
					integer: true,
					number: true
				};
				case "name" : return {
					varchar: true,
					string: true,
					i18n : true
				};
			}
			break;
	}
	throw "bridge.fnd : missing schema "+odkey;
}
//------------------------------------------------------------------------------------------------
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

	//-----------------------------------------------------------------------------------
	Clazz.newMeth(C$, 'objectDefHasPropery$S$S', function(odkey, code) {
		var od = db.find(odkey);
		if (!od)  
		{
			switch (odkey) 
			{
				case "core.objectdef" :
				case "core.property" :
					switch (code) {
						case "code" :
						case "id" :
						case "name" :
							return true;
					}
					break;
			}
			throw "bridge.objectDefHasPropery$S$S : can not find schema "+odkey;
		}
		return !!od.getProperty(code);
	}, 1);
	Clazz.newMeth(C$, 'isMultiple$S$S', function(odkey, pro) {
		var od = db.find(odkey);
		if (!od) return false;
		var p = od.getProperty(pro);
		if (!p) return false;return !!p.meta.multiple;
	}, 1);
	Clazz.newMeth(C$, 'getObjectValue$S$J$S', function(odkey, id, pro) {
		var od = storage.ocache.ensureObjectsReady(odkey);
		if (!od)  {
			switch (odkey) {
				case "core.property" :
					var p = storage.defs.proById[id];
					if (p) {
						switch (pro) {
							case "code" :
								return p.code;
							case "id" :
								return p.id;
						}
					}
					break;
			}
			throw "bridge.getObjectValue$S$J$S : can not find schema "+odkey;
		}
		var obj = od.get(id);
		var val = JS2JAVA(obj[pro]);return val;
	}, 1);
	
	Clazz.newMeth(C$, 'getObjectValueCount$S$J$S', function(odkey, id, pro) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		return obj.count(pro);
	}, 1);
	Clazz.newMeth(C$, 'require$S$S$OA', function (req, api, args) {
		var r=require(req);
		if (!r) throw "can not find require : "+req;
		r=r[api];
		if (!r) throw "can not find require member : "+req+"."+api;
		//---------------------------------------
		var a = JAVA2JS(args);
		var b = r.apply(null,a);
		//---------------------------------------
		return JS2JAVA(b);
	}, 1);
	//-----------------------------------------------------------------------------------
	Clazz.newMeth(C$, 'getSchemaName$S', function (odkey) {
		var n = storage.defs.schemas[odkey];
		if (!n || !n.name) return odkey;
		 return n.name[storage.defs.lang] || n.name[storage.defs.defaultLang] || odkey; 
	}, 1);
	Clazz.newMeth(C$, 'getObjectDefId$S', function(odkey) { return db.find(odkey).id; }, 1);
	Clazz.newMeth(C$, 'getObjectDefById$J', function(id) { var odef = storage.defs.odById[id]; if (!odef) throw "bridge.js Can not find schema by id "+id; return odef.KEY; }, 1);
	Clazz.newMeth(C$, 'getModuleId$S', function(module) {
		var mod = storage.defs.modules[module];
		if (!mod)
			throw "bridge.js : can not get module id : "+module;
		return mod.id; 
	}, 1);
	Clazz.newMeth(C$, 'hasModule$S', function(module) { return !!storage.defs.modules[module]; }, 1);
	Clazz.newMeth(C$, 'moduleHasChild$S$S', function(module, odef) { return !!db.find(module+"."+odef); }, 1);
	Clazz.newMeth(C$, 'getModules$', function() { return Object.keys(storage.defs.modules); }, 1);
	Clazz.newMeth(C$, 'getModuleChildren$S', function(module) { var res = Object.keys(db[module]); return res; }, 1);
	Clazz.newMeth(C$, 'isRelationNotOption$S$S', function(odkey, pro) {var m = fnd(odkey,pro);return m.relation && !m.option;}, 1);
	Clazz.newMeth(C$, 'isDataTypeDouble$S$S', function(odkey, pro) {var m = fnd(odkey,pro);return !!m.double;}, 1);
	Clazz.newMeth(C$, 'isRelation$S$S', function(odkey, pro) {var m = fnd(odkey,pro);return !!m.relation;}, 1);
	Clazz.newMeth(C$, 'isI18n$S$S', function(odkey, pro) {var m = fnd(odkey,pro); return !!m.i18n;}, 1);
	Clazz.newMeth(C$, 'getPropertyOptionSet$S$S', function(odkey, pro) {var m = fnd(odkey,pro);return m.optionSet;}, 1);
	Clazz.newMeth(C$, 'currentLang$', function() { return storage.defs.lang; }, 1);
	Clazz.newMeth(C$, 'defaultLang$', function() { return storage.defs.defaultLang; }, 1);
	Clazz.newMeth(C$, 'getObjectByCode$S$S', function (odkey, code) {
		switch (odkey) {
			case "core.script" :
			case "core.vscript" :
			case "core.jscript" :
				return server.ObjectWrapper.makeObject$S$J$S(odkey,-1,code); // force code
		}
		var od = db.find(odkey);
		if (!od)  
			throw "bridge.js : getObjectByCode$S$S : missing schema "+odkey;
		var o = od.byCode(code);
		if (!o) return;
		return JS2JAVA(o); 
	}, 1);
	//-----------------------------------------------------------------------------------
	var _idByCode;
	Clazz.newMeth(C$, 'getModuleById$J', function(id) {
		var mods = storage.defs.modules;
		if (_idByCode) 
			return _idByCode[id];
		var t={};
		for (var i in mods) t[mods[i].id]=i;
		return (_idByCode=t)[id];
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
		return _pdefproscache[key]=res;
	}, 1);
	var _topodcache={};
	Clazz.newMeth(C$, 'getTopObjectDef$S', function(odkey) {
		var t = _topodcache[odkey];if (t) return t;
		var t = db.find(odkey);
		while (t.parentSchema) t=t.parentSchema;
		return _topodcache[odkey]=t.KEY;
	}, 1);
	Clazz.newMeth(C$, 'getParentObjectDef$S', function(odkey) {
		var t = db.find(odkey);
		var p = t.parentSchema;
		if (!p) return null;
		return p.KEY;
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
		var val = obj[pro];
		if (!(val instanceof Array)) return null;
		return JS2JAVA(val[pos]);
	}, 1);
	Clazz.newMeth(C$, 'getObjectI18nValue$S$J$S$S', function(odkey, id, pro, lang) {
		var cache = storage.ocache.ensureObjectsReady(odkey);
		if (!cache) {
			switch (odkey) {
				case "core.property" : {
					switch (pro) {
						case "name" : 
							var n = storage.defs.proById[id].name || {};
							return n[lang];
					}
				}
				break
			}
			throw "bridge.getObjectI18nValue$S$J$S$S : can not find schema "+odkey;
		} 
		var obj = cache.get(id);
		if (!obj) return null;
		var val = obj._v(pro);if (!val) return null;		
		return JS2JAVA(val[lang]);
	}, 1);
	Clazz.newMeth(C$, 'getObjectI18nValueLanguages$S$J$S', function(odkey, id, pro) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = obj._v(pro);if (!val) return null;		
		return JS2JAVA(Object.keys(val));
	}, 1);
	//-----------------------------------------------------------------------------------
	Clazz.newMeth(C$, 'getObjectValuePosition$S$J$S$O', function(odkey, id, pro, val) {
		if (val instanceof Date) val = val.getTime();
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = obj[pro];
		if (obj instanceof Array) {
			for (var pos=0;pos<obj.length;pos++) {
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
			for (var pos=0;pos<obj.length;pos++) {
				var e = obj[pos];
				if (e.id == valid) return pos;
			}
		}
		return -1;
	}, 1);
	Clazz.newMeth(C$, 'SELECT$S$S$O$S', function(odkey,condition, params,loadMode) {
		params=JAVA2JS(params);
		var select = undefined;
		var groupBy = undefined;
		var orderBy = undefined;
		var limit = undefined;
		var where = undefined;
		if (loadMode && loadMode.startsWith("@")) {
			var ll = loadMode.toUpperCase();
			var i = ll.indexOf(" WHERE ");
			if (i < 0) i = c.indexOf(" ORDER BY ");
			if (i < 0) i = c.indexOf(" GROUP BY ");
			if (i < 0) i = c.indexOf(" LIMIT ");
			if (i >= 0) {
				select=loadMode.substring(1,i).trim();
				condition=loadMode.substring(i).trim();
				if (condition.toUpperCase().startsWith("WHERE "))
					condition=condition.substring(6);
			}
		}
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
		params.where=where;
		params.orderBy=orderBy;
		params.groupBy=groupBy;
		params.limit=limit;
		var od = db.find(odkey);
		if (select) {
			params.select=select;
			res = od.DATA(params);
		} else {
			res = od.SELECT(params);			
		}
		return JS2JAVA(res);
	}, 1);
	//-----------------------------------------------------------------------------------
	Clazz.newMeth(C$, 'getVScriptPropertyDefaultValue$S$S', function (odefkey,pro) {
		var sh = db.find(odefkey);
		switch (pro) {
			case "on_insert_object" : return sh._def.onInsert;
			case "on_update_object" : return sh._def.onUpdate;
			case "on_delete_object" : return sh._def.onDelete;
		}
		var dval = sh.getProperty(pro).defaultValue;
		if (!dval) return;
		return db.core.script.byId(dval.id).code;
	}, 1);
	Clazz.newMeth(C$, 'getObjectOldValuePos$S$J$S$I', function(odkey, id,pro, pos) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = obj.OLD[pro];if (!(obj instanceof Array)) return null;
		return JS2JAVA(val[pos]);
	}, 1);
	Clazz.newMeth(C$, 'getObjectOldI18nValue$S$J$S$S', function(odkey, id,pro, lang) {
		var cache = storage.ocache.ensureObjectsReady(odkey);
		if (!cache) {
			switch (odkey) {
				case "core.property" : {
					switch (pro) {
						case "name" : 
							var n = storage.defs.proById[id].name || {};
							return n[lang];
					}
				}
				break
			}
			throw "bridge.getObjectI18nValue$S$J$S$S : can not find schema "+odkey;
		} 
		var obj = cache.get(id);
		if (!obj) return null;
		var val = obj.OLD._v(pro);if (!val) return null;		
		return JS2JAVA(val[lang]);
	}, 1);
	Clazz.newMeth(C$, 'getObjectOldI18nValueLanguages$S$J$S', function(odkey,id, pro) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = obj.OLD._v(pro);if (!val) return null;		
		return JS2JAVA(Object.keys(val));
	}, 1);
		
	Clazz.newMeth(C$, 'getObjectOldValue$S$J$S', function(odkey, id, pro) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = JS2JAVA(obj.OLD[pro]);
		return val;
	}, 1);
	Clazz.newMeth(C$, 'getObjectOldValueCount$S$J$S',function(odkey, id, pro) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		return obj.OLD.count(pro);
	}, 1);
	
	Clazz.newMeth(C$, 'getObjectOldValuePosition$S$J$S$O', function(odkey, id, pro, val) {
		if (val instanceof Date) val = val.getTime();
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = obj.OLD[pro];
		if (obj instanceof Array) {
			for (var pos=0;pos<obj.length;pos++) {
				var e = obj[pos];
				if (e instanceof Date) e=e.getTime();
				if (e == val)
					return pos;
			}
		}
		return -1;
	}, 1);
	Clazz.newMeth(C$, 'getObjectOldValuePositionRel$S$J$SJ', function(odkey, id, pro, valid) {
		var obj = storage.ocache.ensureObjectsReady(odkey).get(id);
		if (!obj) return null;
		var val = obj.OLD[pro];
		if (obj instanceof Array) {
			for (var pos=0;pos<obj.length;pos++) {
				var e = obj[pos];
				if (e.id == valid) return pos;
			}
		}
		return -1;
	}, 1);

	Clazz.newMeth(C$, 'getUser$', function () {
		return JS2JAVA(session.user);
	}, 1);

	Clazz.newMeth(C$, 'isDeleted$S$J', function(odkey, id) {
		var obj = db.find(odkey).byId(id);
		return JSCORE.transaction.isDeleted(obj);
	}, 1);
	Clazz.newMeth(C$, 'isInserted$S$J', function(odkey, id) {
		var obj = db.find(odkey).byId(id);
		return JSCORE.transaction.isInserted(obj);
	}, 1);
	
	
	Clazz.newMeth(C$, 'callScript$S$OA$O$O', function (code, args, that, _super) {
		var scr = storage.defs.scripts[code];
		if (scr.lang == "vsc") {
			// VSC
			if (!scr.body)
				scr.body='function ('+scr.params.concat(["in..."]).join(",")+"){ \n"+scr.script+"\n}";
			return bridge.base.callVScriptFunction$O$O$OA$O$O(code,scr.body,args||[],that,_super);
		}
		// JavaScript TODO SUPER
		if (!scr.fnc)
			scr.fnc = applyToConstructor(Function,scr.params.concat([scr.script]));
		return JS2JAVA(src.fnc.apply(JAVA2JS(that),JAVA2JS(args))); // NO SUPER
	}, 1);	

	Clazz.newMeth(C$, 'commitObject$S$J', function(odkey, id) {
		var obj = db.find(odkey).byId(id);
		obj.commit();
	}, 1);
	Clazz.newMeth(C$, 'deleteObject$S$J', function(odkey, id) {
		var obj = db.find(odkey).byId(id);
		obj.delete();
	}, 1);

	Clazz.newMeth(C$, 'newObject$S$O', function(odkey,args) {
		var od = db.find(odkey);
		var obj = new od();
		return JS2JAVA(obj);
		// https://stackoverflow.com/questions/1606797/use-of-apply-with-new-operator-is-this-possible
		/*function construct(constructor, args) {
		    function F() {
		        return constructor.apply(this, args);
		    }
		    F.prototype = constructor.prototype;
		    return new F();
		}		
		return JS2JAVA(construct(od,JAVA2JS(args)));*/		
	}, 1);
	
	Clazz.newMeth(C$, 'deleteObjectValue$S$J$S', function(odkey, id, pro) {
		var obj = db.find(odkey).byId(id);
		JSCORE.transaction.objimporter.deleteValue(obj,pro);
	}, 1);
	Clazz.newMeth(C$, 'deleteObjectValuePos$S$J$S$I', function(odkey, id, pro, pos) {
		var obj = db.find(odkey).byId(id);
		JSCORE.transaction.objimporter.deleteValuePos(obj,pro,pos);
	}, 1);
	Clazz.newMeth(C$, 'deleteObjectValueLang$S$J$S$S', function(odkey, id, pro, lang) {
		var obj = db.find(odkey).byId(id);
		JSCORE.transaction.objimporter.deleteValueLang(obj,pro,lang);
	}, 1);
	Clazz.newMeth(C$, 'copyObjectValue$S$J$S$S$J$S', function(odkey, id, pro, todkey, tid, tpro) {
		var obj = db.find(odkey).byId(id);
		var tobj = db.find(todkey).byId(tid);
		JSCORE.transaction.objimporter.copyValue(obj,pro,tobj,tpro);
	}, 1);
	Clazz.newMeth(C$, 'setObjectValue$S$J$S$O', function(odkey, id, pro, val) {
		var obj = db.find(odkey).byId(id);
		val=JAVA2JS(val);
		JSCORE.transaction.objimporter.setValue(obj,pro,val);
	}, 1);
	
	Clazz.newMeth(C$, 'setObjectValueRel$S$J$S$S$J', function(odkey, id, pro, valodkey, valid) {
		var obj = db.find(odkey).byId(id);
		var val = db.find(valodkey).byId(valid);
		JSCORE.transaction.objimporter.setValue(obj,pro,val);
	}, 1);
	Clazz.newMeth(C$, 'setObjectValuePos$S$J$S$I$O', function(odkey, id, pro, pos, val) {
		var obj = db.find(odkey).byId(id);
		val=JAVA2JS(val);
		JSCORE.transaction.objimporter.setValuePos(obj,pro,pos,val);
	}, 1);
	
	Clazz.newMeth(C$, 'setObjectValuePosRel$S$J$S$I$S$J', function (odefkey, id, pro, pos, valodkey, valid) {
		var obj = db.find(odkey).byId(id);
		var val = db.find(valodkey).byId(valid);
		JSCORE.transaction.objimporter.setValuePos(obj,pro,pos,val);
	}, 1);
	
	Clazz.newMeth(C$, 'setObjectValueLang$S$J$S$S$S', function(odkey, id, pro, val, lang) {
		var obj = db.find(odkey).byId(id);
		val=JAVA2JS(val);
		JSCORE.transaction.objimporter.setValueLang(obj,pro,lang,val);
	}, 1);
	Clazz.newMeth(C$, 'pushObjectValue$S$J$S$O', function(odkey, id, pro, val) {
		var obj = db.find(odkey).byId(id);
		val=JAVA2JS(val);
		JSCORE.transaction.objimporter.pushObjectValue(obj,pro,val);
	}, 1);
	Clazz.newMeth(C$, 'pushObjectValueRel$S$J$S$S$J', function(odkey, id, pro, valodkey, valid) {
		var obj = db.find(odkey).byId(id);
		var val = db.find(valodkey).byId(valid);
		JSCORE.transaction.objimporter.pushObjectValue(obj,pro,val);
	}, 1);
	Clazz.newMeth(C$, 'isNestedTransaction$', function() {		
		return JSCORE.transaction.hasSavePoint();
	}, 1);

	
	Clazz.newMeth(C$, 'getObjectAccess$S$J$S', function (odk, id, mode) {
		var od = db.find(odk);
		var obj = od.byId(id);
		return obj.ACCESS[mode];
	}, 1);

	Clazz.newMeth(C$, 'getSchemaAccess$S$S', function (odk, mode) {
		var od = db.find(odk);
		return od.ACCESS[mode];
	}, 1);

	//-------------------------------------------------------
	// TODO PERSIST STORAGE ?
	// TODO TEMP ONLY?
	Clazz.newMeth(C$, 'getUserSetting$S', function (code) {
		//console.log(">>>> GET USR SETTING "+code);
		return JS2JAVA(undefined);
	}, 1);	
	Clazz.newMeth(C$, 'setUserSetting$S$O', function (code, value) {
		value = JAVA2JS(value);
		//console.log(">>>> SET USR SETTING "+code,value);
	}, 1);
	Clazz.newMeth(C$, 'setUserSettingRel$S$S$J', function (code, valod, valid) {
		var value = db.find(valod).byId(valid);
		//console.log(">>>> SET USR SETTING REL "+code,value);
	}, 1);
	Clazz.newMeth(C$, 'setLastValueInput$S$J$S$O', function (od, id, pro, def) {
		var obj = db.find(od).byId(id);
		//console.log(">>>> SET LAST VALUE INPUT "+obj+" | "+pro,def);
	}, 1);
	Clazz.newMeth(C$, 'setLastValueInputRel$S$J$S$S$J', function (od, id, pro, defod, defid) {
		var obj = db.find(od).byId(id);
		var value = db.find(defod).byId(defid);
		//console.log(">>>> SET LAST VALUE INPUT REL "+obj+" | "+pro,value);
	}, 1);

	//-------------------------------------------------------
	
	Clazz.newMeth(C$, 'getLastValueInput$J$O', function (proid, def) {
		//console.log(">>>> GET USR VALUE INPUT "+proid,def);
	}, 1);

	Clazz.newMeth(C$, 'getLastValueInputRel$J$S$J', function (proid, defod, defid) {
		var def = db.find(valod).byId(valid);
		//console.log(">>>> GET USR VALUE INPUT REF "+proid,def);
	}, 1);

	
	Clazz.newMeth(C$, 'getMessage$S', function (code) {
		return storage.defs.i18n[code] || code;
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
				JAVA2JS : JAVA2JS,
				JS2JAVA : JS2JAVA,
				call : function(key,body,args,that,_super) {
					//console.log(">>> CALL "+key+" > ",args);
					try {
						if (typeof body == "function") body=body();
						return JAVA2JS(bridge.base.callVScriptFunction$O$O$OA$O$O(key,body,JS2JAVA(args),JS2JAVA(that),JS2JAVA(_super)));
					} catch(e) {
						if (typeof e.getMessage == "function")
							console.error(e.getMessage());
						else
							console.error(e.detailMessage || (e.val && e.val.str ? e.val.str : e));
						console.warn("Executed script : ",body);
					}
				}
		};
		//window.VSCRIPT.call("test","function(){ return 1+2;}",[])
		System.out.println$S(">>> VSC JS BOOT <<<");
		window.Error = Clazz._Error; // undo swingjs override 
	}, 1);
	Clazz.newMeth(C$);

	Clazz.newMeth(C$, 'beforeException$', function () {
		//debugger;
	}, 1);

})();
;
Clazz.setTVer('3.2.4.07');// Created 2019-06-05 12:10:16 Java2ScriptVisitor
							// version 3.2.4.07 net.sf.j2s.core.jar version
							// 3.2.4.07
