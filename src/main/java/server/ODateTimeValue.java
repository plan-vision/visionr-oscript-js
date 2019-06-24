package server;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


import oscript.data.OBoolean;
import oscript.data.OExactNumber;
import oscript.data.OString;
import oscript.data.Symbol;
import oscript.data.Symbols;
import oscript.data.Value;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.StackFrame;

public class ODateTimeValue extends Value implements Comparable 
{
	private ValueWrapper wrapper;
	private long currentDate;
	private int nanos = 0;
	
	public int hashCode() {
		return (int)(currentDate + (currentDate >>> 32));
	}
	private static final GregorianCalendar s=new GregorianCalendar();
	private static long slong;
	static {
		s.setTimeInMillis(0);
		s.set(Calendar.YEAR, 0);
		s.set(Calendar.MONTH, 0);
		s.set(Calendar.DAY_OF_MONTH, 1);
		slong=s.getTimeInMillis();		
	}
	
	public static GregorianCalendar getDefault() {
		return (GregorianCalendar)s.clone();
	}
	
	
	public GregorianCalendar get()  
	{
		GregorianCalendar cal = (GregorianCalendar)s.clone();
		cal.setTimeInMillis(currentDate);
		return cal;		
	}
	
	public void set(GregorianCalendar val) 
	{
		currentDate=val.getTimeInMillis();
	}
	
	public ODateTimeValue getValueWrapperCopy(ValueWrapper wr) 
	{
		ODateTimeValue f = new ODateTimeValue(this.getDate(),this.getNanos());
		f.wrapper=wr;
		return f;
	}
	
	public void sync() 
	{
		if (wrapper != null)
			wrapper.opAssign(this);	
				
	}
	
	public ODateTimeValue( Date d ) 
	{
		currentDate=d.getTime();
	}
	
	public ODateTimeValue( Date d, java.sql.Timestamp ts) {
		this( d, ts.getNanos() );
	}
	
	public ODateTimeValue( Date d, int nanos ) {
		this( d );
		this.nanos = nanos;
	}

	public ODateTimeValue( long d, int nanos ) {
		this.currentDate=d;
		this.nanos = nanos;
	}

	public ODateTimeValue() {
		this.currentDate=slong;
	}
	
	public void add(int mode,int val) 
	{
		GregorianCalendar c = get();
		c.add(mode,val);
		currentDate=c.getTimeInMillis();
	}

	public void set(int mode,int val) 
	{
		GregorianCalendar c = get();
		c.set(mode,val);
		currentDate=c.getTimeInMillis();
	}

	public int get(int mode) 
	{
		GregorianCalendar c = get();
		return c.get(mode);
	}

	/*public GregorianCalendar getCalendar() {
		return get();
	}*/

	public Date getDate() {
		return new Date(currentDate);
	}
	
	public long getTime() {
		//return date.getTimeInMillis();
		return currentDate;
	}
	
	public int getNanos() {
		return nanos;
	}

	@Override
	protected Value getTypeImpl() {
		return this;
	}

	@Override
	public int compareTo(Object o) 
	{
		if( o instanceof ODateTimeValue) 
		{
			ODateTimeValue c = (ODateTimeValue) o;
			if( currentDate < c.currentDate) {
				return -1;
			} else if( currentDate > c.currentDate) {
				return 1;
			} else {
				if( this.nanos < c.nanos )
					return -1;
				else if( this.nanos > c.nanos )
					return 1;
				else 
					return 0;
			}
		}
		if (o == null)
			return 1;
		int a = this.getClass().hashCode()-o.getClass().hashCode();
		if (a < 0)
			return -1;
		if (a > 0)
			return 1;
		return 0;
	}
	
	@Override
	public boolean equals(Object o) {
		return this.compareTo(o) == 0;
	}
	
	/**
	 * Resolve operators in VScript and execute the intended function
	 * @param symbol VScript operator
	 * @return Result from operation
	 */
	public Value resolve(int symbol) {		
		
		if( symbol == Symbols.CLONE ) {
			return new ODateTimeValue(this.getDate(),this.nanos) {
				public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
						return this;
				}
				@Override
				protected Value getTypeImpl() { return this; }
			};
		}
		
		int type = -1;
		
		// Data
		if( symbol == Symbols.DT_YEAR ) 
			type = Calendar.YEAR;
		if( symbol == Symbols.DT_MONTH )
			type = Calendar.MONTH;
		if( symbol == Symbols.DT_DAY )
			type = Calendar.DAY_OF_MONTH;
		if( symbol == Symbols.DT_WEEKDAY )
			type = Calendar.DAY_OF_WEEK;
		if( symbol == Symbols.DT_HOURS )
			type = Calendar.HOUR_OF_DAY;
		if( symbol == Symbols.DT_MINS || symbol == Symbols.DT_MINS2 )
			type = Calendar.MINUTE;
		if( symbol == Symbols.DT_SECS || symbol == Symbols.DT_SECS2 )
			type = Calendar.SECOND;
		if( symbol == Symbols.DT_MILLIS || symbol == Symbols.DT_MILLIS2 )
			type = Calendar.MILLISECOND;
		if( symbol == Symbols.DT_NANOS || symbol == Symbols.DT_NANOS2 ) {
			return new DateExactNumber(this.nanos,this, -1) {
				public void opAssign(Value val) {
					if( val instanceof OExactNumber ) {
						date.setNanos( (int) val.castToExactNumber() );
						sync();
					} else {
						throw new RuntimeException("Cannot cast value to exact number while setting nanos in DBDateTime: " + 
								val.castToString());
					}
				}
			};
		}
		
		// Return one of the values above
		if( type != -1 ) {
			return new DateExactNumber(get(type),this, type) {
				public void opAssign(Value val) {
					long c = val.castToExactNumber();
					set(type, (int) c );
					sync();
				}
			};			
		}

		if( symbol == Symbols.DT_IS_WORKDAY || symbol == Symbols.DT_IS_WORKDAY2 ) {
			int weekday = get(Calendar.DAY_OF_WEEK);
			final boolean isWorkday = weekday != 7 && weekday != 1; 
			return new OBoolean(isWorkday) {
				public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
					return this;
				}
			};
		}
				
		if( symbol == Symbols.DT_GET_TIME || symbol == Symbols.DT_GET_TIME2 ) {
			return new OExactNumber(getTime()) {
				public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
					return this;
				}
			};
		}
		
		if (symbol == Symbols.WEEK) {
			ODateTimeValue t1 = this.clone();
			t1.getMember("month").opAssign(OExactNumber.makeExactNumber(0));
			t1.getMember("day").opAssign(OExactNumber.makeExactNumber(1));
			t1.getMember("secs").opAssign(OExactNumber.makeExactNumber(0));
			t1.getMember("hours").opAssign(OExactNumber.makeExactNumber(0));
			t1.getMember("nanos").opAssign(OExactNumber.makeExactNumber(0));
			t1.getMember("millis").opAssign(OExactNumber.makeExactNumber(0));
			long diff = this.bopMinus(t1).castToExactNumber();
			return OExactNumber.makeExactNumber(diff/(1000*60*60*24*7));
		}
		
		// Functions
		if (symbol == Symbols.DT_ADD_DATE  || symbol == Symbols.DT_ADD_DATE2 ||
			symbol == Symbols.DT_ADD_TIME || symbol == Symbols.DT_ADD_TIME2 ||
			symbol == Symbols.DT_ADD_YEARS || symbol == Symbols.DT_ADD_YEARS2 ||
			symbol == Symbols.DT_ADD_MONTHS || symbol == Symbols.DT_ADD_MONTHS2 ||
			symbol == Symbols.DT_ADD_DAYS || symbol == Symbols.DT_ADD_DAYS2 ||
			symbol == Symbols.DT_ADD_HOURS || symbol == Symbols.DT_ADD_HOURS ||
			symbol == Symbols.DT_ADD_MINUTES || symbol == Symbols.DT_ADD_MINUTES2 ||
			symbol == Symbols.DT_ADD_MINUTES3 || symbol == Symbols.DT_ADD_MINUTES4 ||
			symbol == Symbols.DT_ADD_SECONDS || symbol == Symbols.DT_ADD_SECONDS2 ||
			symbol == Symbols.DT_ADD_SECONDS3 || symbol == Symbols.DT_ADD_SECONDS4 ||
			symbol == Symbols.DT_ADD_MILLIS || symbol == Symbols.DT_ADD_MILLIS2 ||
			symbol == Symbols.DT_ADD_MILLIS3 || symbol == Symbols.DT_ADD_MILLIS4 ||
			symbol == Symbols.DT_ADD_NANOS || symbol == Symbols.DT_ADD_NANOS2 ||
			symbol == Symbols.DT_ADD_NANOS3 || symbol == Symbols.DT_ADD_NANOS4 ) {
			
			final int sym = symbol;
			return new ODateTimeValue(this.getDate(),this.nanos)
			{
				// selectWhere(where)
				// selectWhere(where,properties)
				public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
					int l = args == null ? 0 : args.length();
					if ( l != 1 )
						throw new RuntimeException("Wrong call, pass 1 parameter only.");
					
					if(sym == Symbols.DT_ADD_DATE  || sym == Symbols.DT_ADD_DATE2 ) {
						Object o = args.referenceAt(0).castToJavaObject();
						if( !(o instanceof ODateTimeValue)) {
							throw new RuntimeException("Calls addDate with another datetime object instead with " + o.getClass());
						}
						ODateTimeValue time = (ODateTimeValue) o;
						GregorianCalendar c = get();
						GregorianCalendar tc = time.get();
						c.add( Calendar.YEAR, tc.get( Calendar.YEAR));
						c.add( Calendar.MONTH, tc.get( Calendar.MONTH));
						c.add( Calendar.DATE, tc.get( Calendar.DATE));
						set(c);
						return this;
					}
					if(sym == Symbols.DT_ADD_TIME || sym == Symbols.DT_ADD_TIME2 ) {
						Object o = args.referenceAt(0).castToJavaObject();
						if( !(o instanceof ODateTimeValue)) {
							throw new RuntimeException("Calls addDate with another datetime object instead with " + o.getClass());
						}
						ODateTimeValue time = (ODateTimeValue) o;
						GregorianCalendar c = get();
						GregorianCalendar tc = time.get();
						
						c.add( Calendar.HOUR_OF_DAY, tc.get( Calendar.HOUR_OF_DAY));
						c.add( Calendar.MINUTE, tc.get( Calendar.MINUTE));
						c.add( Calendar.SECOND, tc.get( Calendar.SECOND));
						c.add( Calendar.MILLISECOND, tc.get( Calendar.MILLISECOND));
						set(c);
						this.setNanos( this.getNanos() + time.getNanos() );
						return this;
					}
					if( sym == Symbols.DT_ADD_YEARS || sym == Symbols.DT_ADD_YEARS2 ) {
						int toAdd = (int) args.referenceAt(0).castToExactNumber();
						this.add( Calendar.YEAR, toAdd);
						return this;
					}
					if( sym == Symbols.DT_ADD_MONTHS || sym == Symbols.DT_ADD_MONTHS2 ) {
						int toAdd = (int) args.referenceAt(0).castToExactNumber();
						this.add( Calendar.MONTH, toAdd);
						return this;
					}
					if( sym == Symbols.DT_ADD_DAYS || sym == Symbols.DT_ADD_DAYS2 ) {
						int toAdd = (int) args.referenceAt(0).castToExactNumber();
						this.add( Calendar.DAY_OF_MONTH, toAdd);
						return this;
					}
					if( sym == Symbols.DT_ADD_HOURS || sym == Symbols.DT_ADD_HOURS ) {
						int toAdd = (int) args.referenceAt(0).castToExactNumber();
						this.add( Calendar.HOUR_OF_DAY, toAdd);
						return this;
					}
					if( sym == Symbols.DT_ADD_MINUTES || sym == Symbols.DT_ADD_MINUTES2 ||
							sym == Symbols.DT_ADD_MINUTES3 || sym == Symbols.DT_ADD_MINUTES4 ) {
						int toAdd = (int) args.referenceAt(0).castToExactNumber();
						this.add( Calendar.MINUTE, toAdd);
						return this;
					}
					if( sym == Symbols.DT_ADD_SECONDS || sym == Symbols.DT_ADD_SECONDS2 ||
							sym == Symbols.DT_ADD_SECONDS3 || sym == Symbols.DT_ADD_SECONDS4 ) {
						int toAdd = (int) args.referenceAt(0).castToExactNumber();
						this.add( Calendar.SECOND, toAdd);
						return this;
					}
					if( sym == Symbols.DT_ADD_MILLIS || sym == Symbols.DT_ADD_MILLIS2 ||
							sym == Symbols.DT_ADD_MILLIS3 || sym == Symbols.DT_ADD_MILLIS4 ) {
						int toAdd = (int) args.referenceAt(0).castToExactNumber();
						this.add( Calendar.MILLISECOND, toAdd);
						return this;
					}
					if( sym == Symbols.DT_ADD_NANOS || sym == Symbols.DT_ADD_NANOS2 ||
					sym == Symbols.DT_ADD_NANOS3 || sym == Symbols.DT_ADD_NANOS4 ) {
						int toAdd = (int) args.referenceAt(0).castToExactNumber();
						this.setNanos( this.getNanos() + toAdd);
						return this;
					}
					return Value.NULL;
				}
				@Override
				protected Value getTypeImpl() {
					return this;
				}
			};		
		}
 		return null;
	}
	
	public Value getMember( int id, boolean exception ) {
	   Value val = resolve(id);
	   if( val != null )
	     return val;
	   
	   String sym = Symbol.getSymbol(id).castToString(); 
	   final String result = this.toString();
	   if( sym != null && sym.equals("castToString") ) {
		   return new Value() {
			   public Value callAsFunction(StackFrame sf,oscript.util.MemberTable args) {
				   return OString.makeString( result );
			   }
				@Override
				protected Value getTypeImpl() {
					return this;
				}
		   };
	   }
	   
	   return super.getMember( id,exception);
	 }
	
	public String toString() {
		return "ODateTimeValue@"+currentDate;
	}
	
	public String castToString() {
		return this.toString();
	}
 
	@Override
	public Value bopPlus(Value val) throws PackagedScriptObjectException {
		Object o = val.castToJavaObject();
		if (o instanceof Number) 
		{
			return new ODateTimeValue(this.currentDate+((Number)o).longValue(),this.nanos);
		}
		throw new RuntimeException("Error on DBDateTimeValue '+' : NOT SUPPORTED"); 
	}
	
	/**
	 * Return the difference of two dates in milliseconds.
	 */
	public Value bopMinus( Value val ) {
		Object o = val.castToJavaObject();
		if (o instanceof Number)
		{
			return new ODateTimeValue(this.currentDate-((Number)o).longValue(),this.nanos);
		}
		if (o instanceof ODateTimeValue)
		{
			ODateTimeValue b = (ODateTimeValue)o;
			return new OExactNumber(this.getTime()-b.getTime());
		}
		
		throw new RuntimeException("Error on DBDateTimeValue '-' : NOT SUPPORTED"); 
	}
	

	
	public void opAssign(Value val) {
		Object o = val.castToJavaObject();
		if( o instanceof ODateTimeValue ) {
			this.currentDate=((ODateTimeValue)o).currentDate;
			sync();
			return;
		}
		if( o instanceof Date ) {
			this.currentDate=((Date)o).getTime();
			sync();
			return;
		}
		if( o instanceof Number ) {
			this.currentDate=((Number)o).longValue();
			sync();
		}		
	}

	/**
	 * Compare two values in oscript:
	 * 	val1 == val2
	 */
	public Value bopEquals( Value value ) {
		Object o = value.castToJavaObject();
		if( !(o instanceof ODateTimeValue) ) {
			return OBoolean.makeBoolean(false);
		}
		
		return OBoolean.makeBoolean( this.compareTo(o) == 0 );
	}
	
	/**
	 * Compare two values in oscript:
	 * 	val1 != val2
	 */
	public Value bopNotEquals( Value value ) {
		Object o = value.castToJavaObject();
		if( !(o instanceof ODateTimeValue) ) {
			return OBoolean.makeBoolean(true);
		}
		
		return OBoolean.makeBoolean( this.compareTo(o) != 0 );
	}
	
	/**
	 * Compare two values in oscript:
	 * 	val1 &lt; val2
	 */
	public Value bopLessThan( Value value ) {
		Object o = value.castToJavaObject();
		if( !(o instanceof ODateTimeValue) ) {
			return OBoolean.makeBoolean(false);
		}
		
		return OBoolean.makeBoolean( this.compareTo(o) < 0 );
	}
	
	public Value bopLessThanR( Value val, PackagedScriptObjectException e )
		throws PackagedScriptObjectException
	{
		return bopLessThan(val);
	}
	
	/**
	 * Compare two values in oscript:
	 * 	val1 &le; val2
	 */
	public Value bopLessThanOrEquals( Value value ) {
		Object o = value.castToJavaObject();
		if( !(o instanceof ODateTimeValue) ) {
			return OBoolean.makeBoolean(false);
		}
		
		return OBoolean.makeBoolean( this.compareTo(o) <= 0  );
	}
	
	public Value bopLessThanOrEqualsR( Value val, PackagedScriptObjectException e )
	    throws PackagedScriptObjectException
	{
	    return bopLessThanOrEquals(val);
	}
	  
	/**
	 * Compare two values in oscript:
	 * 	val1 &gt; val2
	 */
	public Value bopGreaterThan( Value value ) {
		Object o = value.castToJavaObject();
		if( !(o instanceof ODateTimeValue) ) {
			return OBoolean.makeBoolean(false);
		}
		
		return OBoolean.makeBoolean( this.compareTo(o) > 0 );
	}
	
	public Value bopGreaterThanR( Value val, PackagedScriptObjectException e )
	    throws PackagedScriptObjectException
	{
	    return bopGreaterThan(val);
	}
	
	/**
	 * Compare two values in oscript:
	 * 	val1 &ge; val2
	 */
	public Value bopGreaterThanOrEquals( Value value ) {
		Object o = value.castToJavaObject();
		if( !(o instanceof ODateTimeValue) ) {
			return OBoolean.makeBoolean(false);
		}
		
		return OBoolean.makeBoolean( this.compareTo(o) >= 0 );
	}
	
	public Value bopGreaterThanOrEqualsR( Value val, PackagedScriptObjectException e )
	    throws PackagedScriptObjectException
	{
	    return bopGreaterThanOrEquals(val);
	}
	
	/**
	 * Compare two values in oscript:
	 * 	val1 instanceof val2
	 */
	public Value bopInstanceOf(Value value)  {
		Object o = value.castToJavaObject();
		return OBoolean.makeBoolean( o instanceof ODateTimeValue );
	}
	
	public long castToExactNumber() {
		return this.getTime();
	}
	
	/**
	 * Clone the object. Return new instance with same data as the current
	 * object.
	 */
	public ODateTimeValue clone() {
		return new ODateTimeValue( this.getDate(), this.getNanos() );
	}

	public void setYear( int year ) {
		set(Calendar.YEAR, year);
		sync();
	}

	public void setMonth( int month ) {
		set(Calendar.MONTH, month);
		sync();
	}
	
	public void setDay( int day ) {
		set(Calendar.DAY_OF_MONTH, day);
		sync();
	}
	
	public void setHours( int hours ) {
		set(Calendar.HOUR_OF_DAY, hours);
		sync();
	}
	
	public void setMinutes( int mins) {
		set(Calendar.MINUTE, mins);
		sync();
	}
	
	public void setSeconds( int secs) {
		set(Calendar.SECOND, secs);
		sync();
	}
	
	public void setMillis( int millis ) {
		set(Calendar.MILLISECOND, millis);
		sync();
	}
	
	public void setNanos(int nanos) {
		this.nanos = nanos;
		sync();
	}
	
	private static class DateExactNumber extends OExactNumber {
	
		ODateTimeValue date;
		int type;
		
		public DateExactNumber(int number, ODateTimeValue date, int type) {
			super(number);
			this.date = date;
			this.type = type;
		}
	}	
	
}
