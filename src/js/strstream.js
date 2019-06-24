exports.createInputStream  = function(str) 
{	
	var s = new java.lang.String(str);
	var is = new java.io.ByteArrayInputStream(s.getBytes$());
	var bs = s.getBytes$();
	is.$in={
		buf :	bs
	};
	var pos = 0;
	is.read$BA=function(arr) {
		var c = 0;
		while (pos+c < bs.length && c < arr.length) {
			arr[c]=bs[pos+c];
			c++;
		}
		pos+=c;
		return c; 
	}
	return is;
}