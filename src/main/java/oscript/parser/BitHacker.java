package oscript.parser;

public class BitHacker {
	
	public static boolean equals0(Int64 a) { 
		return a.isZero();
	}
	public static boolean equals(Int64 a, Int64 b) {
		return a.equals(b);
	}

	public static Int64 and(Int64 a, Int64 b) {
		return a.and(b);
	}
 
	public static Int64 or(Int64 a, Int64 b) {
		return a.or(b);
	}

	public static Int64 shift1(int p) {
		return Int64.shift1(p);
	}

	static final Int64[] jjbitVec0 = { new Int64("fffffffffffffffe"), new Int64("ffffffffffffffff"), new Int64("ffffffffffffffff"), new Int64("ffffffffffffffff")};
	static final Int64[] jjbitVec2 = { new Int64("0"), new Int64("0"), new Int64("ffffffffffffffff"), new Int64("ffffffffffffffff") };
	static final Int64[] jjbitVec3 = { new Int64("1ff00000fffffffe"), new Int64("ffffffffffffc000"), new Int64("ffffffff"), new Int64("600000000000000")};
	static final Int64[] jjbitVec4 = { new Int64("0"), new Int64("0"), new Int64("0"), new Int64("ff7fffffff7fffff") };
	static final Int64[] jjbitVec5 = { new Int64("0"), new Int64("ffffffffffffffff"), new Int64("ffffffffffffffff"), new Int64("ffffffffffffffff")};
	static final Int64[] jjbitVec6 = { new Int64("ffffffffffffffff"), new Int64("ffffffffffffffff"), new Int64("ffff"), new Int64("0")};
	static final Int64[] jjbitVec7 = { new Int64("ffffffffffffffff"), new Int64("ffffffffffffffff"), new Int64("0"), new Int64("0")};
	static final Int64[] jjbitVec8 = { new Int64("3fffffffffff"), new Int64("0"), new Int64("0"), new Int64("0")};
	static final Int64[] jjtoToken = { new Int64("a87ffffffffa0001"), new Int64("3fffffffffffc8")};
	static final Int64[] jjtoSkip = { new Int64("17c3e"), new Int64("0")};
	static final Int64[] jjtoSpecial = { new Int64("17c00"), new Int64("0")};
	static final Int64[] jjtoMore = { new Int64("483c0"), new Int64("0")};

}