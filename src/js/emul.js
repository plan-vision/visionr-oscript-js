// EMULATE DUMMY BROWSER ENV FOR SWINGJS 
module.exports = function(window,loader) 
{
	//Clazz._LoaderProgressMonitor.hideMonitor=Clazz._LoaderProgressMonitor=showStatus=function() {};
	function jQuery() {
		var t=[];
		t.ready=function(){};
		t.hide=function(){};
		t.show=function(){};
		t.bind=function(){};
		t.addClass=function(){};
		t.removeClass=function(){};
		return t;
	}
	jQuery.ajaxSettings={};
	jQuery.ajaxTransport=function(){};
	jQuery.extend=function() {};
	jQuery.support={};
	jQuery.ajaxSetup=function() {};
	jQuery.map=function(){};
	jQuery.event=function(){};
	jQuery.event.special=[];
	jQuery.ajax=function(opts) {
		if (opts.success) {
			setTimeout(function(){
				opts.success({
					
				});
			},0);
		}
		loader(opts.url);
		return {
			state : function() {},
			responseText : "0"	
		}
	}
	window.window=window;
	window.jQuery=jQuery;
	window.self=window;
	window.navigator=window.navigator || {
		userAgent : ""	
	};
	//-------------------------------------
	// HACK JAVA LOG
	var doneTrigger="JSUtil Error running readyCallback method for testApplet"; // WEIRD but ok!
	console._log=console.log; // SAVE OLD LOG FNC
	console.log=function(str) {
		if (arguments.length != 1)
			console._log.apply(this,arguments);
		else {
			//console.log.call(this,str);
			if (typeof str == "string" && str.startsWith(doneTrigger)) {
				console.log=console._log; // RESTORE
				delete console._log;
			}
			
		}
	};
	//-------------------------------------
	console.err=function(str) {
		console.error(str);
	};
	//-------------------------------------
	window.alert=/*window.alert || */(function(msg) {
		console.error(msg);
		//process.exit(-1);
	});
}
