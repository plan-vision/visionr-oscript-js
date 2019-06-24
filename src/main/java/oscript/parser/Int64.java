package oscript.parser;

public class Int64 {
	//BigInteger v;
	int hi;
	int lo;
	
	public Int64(String val) {
		if (val.length() > 8) {
			String slo = val.substring(val.length()-8);
			String shi = val.substring(0,val.length()-8);
			lo=(int)Long.parseLong(slo,16);
			hi=(int)Long.parseLong(shi,16);
		} else {
			lo=(int)Long.parseLong(val,16);
			hi=0;
		}
	}
	public Int64(int hi,int lo) {
		this.hi=hi;
		this.lo=lo;
	}
	
	
	boolean isZero() {
		return hi == 0 && lo == 0;
	}
	
	boolean isNotZero() {
		return hi != 0 || lo != 0;
	}
	
	Int64 and(Int64 a) {
		return new Int64(hi&a.hi,lo & a.lo);
	}

	Int64 or(Int64 a) {
		return new Int64(hi|a.hi,lo|a.lo);
	}

	@Override
	public boolean equals(Object obj) {
		Int64 i = (Int64)obj;
		return (i.hi == hi && i.lo == lo); 
	}
	
	public static Int64 shift1(int p) {
		if (p < 32)
			return new Int64(0,1<<p);
		else
			return new Int64(1<<(p-32),0);
	}
}
