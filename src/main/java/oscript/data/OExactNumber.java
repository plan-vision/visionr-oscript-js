/*=============================================================================
 *     Copyright Texas Instruments 2000-2004.  All Rights Reserved.
 *   
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * $ProjectHeader: OSCRIPT 0.155 Fri, 20 Dec 2002 18:34:22 -0800 rclark $
 */


package oscript.data;

import oscript.exceptions.*;

/**
 * An exact number.  An <code>OExactNumber</code> is immutable, meaning once
 * the instance is constructed, it won't change.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class OExactNumber extends OObject implements java.io.Externalizable
{
  /**
   * The type object for an instance of ExactNumber.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.OExactNumber");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "ExactNumber";
  public final static String[] MEMBER_NAMES   = new String[] {
                                                      "castToString",
                                                      "castToExactNumber",
                                                      "castToInexactNumber",
                                                      "castToJavaObject",
                                                      "intValue",
                                                      "floatValue",
                                                      "shortValue",
                                                      "charValue",
                                                      "byteValue",
                                                      "bopBitwiseOr",
                                                      "bopBitwiseOrR",
                                                      "bopBitwiseXor",
                                                      "bopBitwiseXorR",
                                                      "bopBitwiseAnd",
                                                      "bopBitwiseAndR",
                                                      "bopEquals",
                                                      "bopEqualsR",
                                                      "bopNotEquals",
                                                      "bopNotEqualsR",
                                                      "bopLessThan",
                                                      "bopLessThanR",
                                                      "bopGreaterThan",
                                                      "bopGreaterThanR",
                                                      "bopLessThanOrEquals",
                                                      "bopLessThanOrEqualsR",
                                                      "bopGreaterThanOrEquals",
                                                      "bopGreaterThanOrEqualsR",
                                                      "bopLeftShift",
                                                      "bopLeftShiftR",
                                                      "bopSignedRightShift",
                                                      "bopSignedRightShiftR",
                                                      "bopUnsignedRightShift",
                                                      "bopUnsignedRightShiftR",
                                                      "bopPlus",
                                                      "bopPlusR",
                                                      "bopMinus",
                                                      "bopMinusR",
                                                      "bopMultiply",
                                                      "bopMultiplyR",
                                                      "bopDivide",
                                                      "bopDivideR",
                                                      "bopRemainder",
                                                      "bopRemainderR",
                                                      "uopIncrement",
                                                      "uopDecrement",
                                                      "uopPlus",
                                                      "uopMinus",
                                                      "uopBitwiseNot"
                                                    };
  
  /**
   * On the theory that many OExactNumbers or on the range 0..?? and are
   * short lived (for example, for loops, etc), a table of pre-allocated
   * OExactNumbers is used
   */
  private static final int MIN_CACHED_EXACT_NUMBER  = -100;
  private static final int MAX_CACHED_EXACT_NUMBER  = 2000;
  private static final OExactNumber[] EXACT_NUMBERS = new OExactNumber[ MAX_CACHED_EXACT_NUMBER - MIN_CACHED_EXACT_NUMBER + 1 ];
  static {
    for( int i=MIN_CACHED_EXACT_NUMBER; i<=MAX_CACHED_EXACT_NUMBER; i++ )
      EXACT_NUMBERS[ i - MIN_CACHED_EXACT_NUMBER ] = new OExactNumber(i);
  }
  
  /*=======================================================================*/
  /**
   */
  public static final OExactNumber makeExactNumber( long longVal )
  {
    if( (MIN_CACHED_EXACT_NUMBER <= longVal) && (longVal <= MAX_CACHED_EXACT_NUMBER) )
      return EXACT_NUMBERS[ (int)longVal - MIN_CACHED_EXACT_NUMBER ];
//System.out.println("makeExactNumber: " + longVal);
    return new OExactNumber(longVal);
  }
  
  /*=======================================================================*/
  // members:
  private long longVal;
  
  // Externalizable support:
  public OExactNumber() {}
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void readExternal( java.io.ObjectInput in )
    throws java.io.IOException
  {
    longVal = in.readLong();
  }
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void writeExternal( java.io.ObjectOutput out )
    throws java.io.IOException
  {
    out.writeLong(longVal);
  }
  /*=======================================================================*/
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param longVal       the longeger value corresponding to this number
   */
  public OExactNumber( long longVal )
  {
    super();
    this.longVal = longVal;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.  This is the constructor that is called via a
   * <code>BuiltinType</code> instance.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public OExactNumber( oscript.util.MemberTable args )
  {
    super();
    
    if( args.length() != 1 )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
    else
      longVal = args.referenceAt(0).castToExactNumber();
  }
  
  /*=======================================================================*/
  /**
   * Get the type of this object.  The returned type doesn't have to take
   * into account the possibility of a script type extending a built-in
   * type, since that is handled by {@link #getType}.
   * 
   * @return the object's type
   */
  protected Value getTypeImpl()
  {
    return TYPE;
  }
  
  /*=======================================================================*/
  /**
   * Return a hash code value for this object.
   * 
   * @return a hash code value
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return castToJavaObject().hashCode();
  }
  
  /*=======================================================================*/
  /**
   * Compare two objects for equality.
   * 
   * @param obj          the object to compare to this object
   * @return <code>true</code> if equals, else <code>false</code>
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals( Object obj )
  {
    if( (obj instanceof OExactNumber) &&
        (((OExactNumber)obj).longVal == longVal) )
    {
      return true;
    }
    else if( (obj instanceof Number) &&
             (((Number)obj).longValue() == longVal) )
    {
      return true;
    }
    else
    {
      return false;
    }
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>String</code> value.
   * 
   * @return a String value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public String castToString()
    throws PackagedScriptObjectException
  {
    return String.valueOf(longVal);
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>long</code> value.
   * 
   * @return a long value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public long castToExactNumber()
    throws PackagedScriptObjectException
  {
    return longVal;
  }
  
  public static Value _bopCast( Value val )
  {
    if( val instanceof OExactNumber )
      return val;
    return makeExactNumber( val.castToExactNumber() );
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>double</code> value.
   * 
   * @return a double value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public double castToInexactNumber()
    throws PackagedScriptObjectException
  {
    return (double)longVal;
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>Object</code> value.
   * 
   * @return a java object
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Object castToJavaObject()
    throws PackagedScriptObjectException
  {
    if( ((long)((int)longVal)) == longVal )
      return Integer.valueOf( (int)longVal );
    else
      return Long.valueOf(longVal);
  }
  
  public int intValue() { return (int)longVal; }
  public float floatValue() { return (float)longVal; }
  public short shortValue() { return (short)longVal; }
  public char charValue() { return (char)longVal; }
  public byte byteValue() { return (byte)longVal; }
  
  /*=======================================================================*/
  /* The binary operators:
   */
  
  /*=======================================================================*/
  /**
   * Perform the bitwise OR operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopBitwiseOr( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OExactNumber.makeExactNumber( longVal | val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopBitwiseOrR( this, e );
    }
  }
  public Value bopBitwiseOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OExactNumber.makeExactNumber( val.castToExactNumber() | longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the bitwise XOR operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopBitwiseXor( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OExactNumber.makeExactNumber( longVal ^ val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopBitwiseXorR( this, e );
    }
  }
  public Value bopBitwiseXorR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OExactNumber.makeExactNumber( val.castToExactNumber() ^ longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the bitwise AND operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopBitwiseAnd( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OExactNumber.makeExactNumber( longVal & val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopBitwiseAndR( this, e );
    }
  }
  public Value bopBitwiseAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OExactNumber.makeExactNumber( val.castToExactNumber() & longVal );
  }
  
  
  private boolean checkCnv(Value val) {
	  val = val.unhand();
	  boolean ok =  val instanceof OInexactInterface;
	  if (!ok)
		  return false;
	  return ((OInexactInterface)val).isInexactNumber();
  }
  /*=======================================================================*/
  /**
   * Perform the "==" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopEquals( Value val )
    throws PackagedScriptObjectException
  {
	if (val == Value.NULL)
		return OBoolean.FALSE;
	if (checkCnv(val)) 
		return (new OInexactNumber(longVal)).bopEquals(val);
    try
    {
      return OBoolean.makeBoolean( longVal == val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopEqualsR( this, e );
    }
  }
  public Value bopEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopNotEqualsR(val,e);
    return OBoolean.makeBoolean( val.castToExactNumber() == longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "!=" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopNotEquals( Value val )
    throws PackagedScriptObjectException
  {
	if (val == Value.NULL)
		return OBoolean.TRUE;
	if (checkCnv(val)) 
		return (new OInexactNumber(longVal)).bopNotEquals(val);
    try
    {
      return OBoolean.makeBoolean( longVal != val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopNotEqualsR( this, e );
    }
  }
  public Value bopNotEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
		if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopNotEqualsR(val,e);
    return OBoolean.makeBoolean( val.castToExactNumber() != longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "<" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopLessThan( Value val )
    throws PackagedScriptObjectException
  {
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopLessThan(val);
    try
    {
      return OBoolean.makeBoolean( longVal < val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopLessThanR( this, e );
    }
  }
  public Value bopLessThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopLessThanR(val,e);
    return OBoolean.makeBoolean( val.castToExactNumber() < longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the ">" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopGreaterThan( Value val )
    throws PackagedScriptObjectException
  {
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopGreaterThan(val);
    try
    {
      return OBoolean.makeBoolean( longVal > val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopGreaterThanR( this, e );
    }
  }
  public Value bopGreaterThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopGreaterThanR(val,e);
    return OBoolean.makeBoolean( val.castToExactNumber() > longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "<=" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopLessThanOrEquals( Value val )
    throws PackagedScriptObjectException
  {
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopLessThanOrEquals(val);
    try
    {
      return OBoolean.makeBoolean( longVal <= val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopLessThanOrEqualsR( this, e );
    }
  }
  public Value bopLessThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopLessThanOrEqualsR(val,e);
    return OBoolean.makeBoolean( val.castToExactNumber() <= longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the ">=" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopGreaterThanOrEquals( Value val )
    throws PackagedScriptObjectException
  {
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopGreaterThanOrEquals(val);
    try
    {
      return OBoolean.makeBoolean( longVal >= val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopGreaterThanOrEqualsR( this, e );
    }
  }
  public Value bopGreaterThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopGreaterThanOrEqualsR(val,e);
    return OBoolean.makeBoolean( val.castToExactNumber() >= longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "<<" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopLeftShift( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OExactNumber.makeExactNumber( longVal << val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopLeftShiftR( this, e );
    }
  }
  public Value bopLeftShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OExactNumber.makeExactNumber(  val.castToExactNumber() << longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the ">>" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopSignedRightShift( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OExactNumber.makeExactNumber( longVal >> val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopSignedRightShiftR( this, e );
    }
  }
  public Value bopSignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OExactNumber.makeExactNumber(  val.castToExactNumber() >> longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the ">>>" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopUnsignedRightShift( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OExactNumber.makeExactNumber( longVal >>> val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopUnsignedRightShiftR( this, e );
    }
  }
  public Value bopUnsignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OExactNumber.makeExactNumber( val.castToExactNumber() >>> longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "+" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopPlus( Value val )
    throws PackagedScriptObjectException
  {
	if (val instanceof OString) {
		return new OString(this.castToString() + ((OString)val).castToString());
	}
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopPlus(val);
    try
    {
      return OExactNumber.makeExactNumber( longVal + val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopPlusR( this, e );
    }
  }
  public Value bopPlusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopPlusR(val,e);
    return OExactNumber.makeExactNumber( val.castToExactNumber() + longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "-" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopMinus( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
  	  if (checkCnv(val)) 
  			return (new OInexactNumber(longVal)).bopMinus(val);
      return OExactNumber.makeExactNumber( longVal - val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopMinusR( this, e );
    }
  }
  public Value bopMinusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
	  if (checkCnv(val)) 
			return (new OInexactNumber(longVal)).bopMinusR(val,e);
    return OExactNumber.makeExactNumber( val.castToExactNumber() - longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "*" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopMultiply( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
  	  if (checkCnv(val)) 
  			return (new OInexactNumber(longVal)).bopMultiply(val);
      return OExactNumber.makeExactNumber( longVal * val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopMultiplyR( this, e );
    }
  }
  public Value bopMultiplyR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
  	  if (checkCnv(val)) 
  			return (new OInexactNumber(longVal)).bopMultiplyR(val,e);
    return OExactNumber.makeExactNumber( val.castToExactNumber() * longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "/" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopDivide( Value val )
    throws PackagedScriptObjectException
  {
  	  if (checkCnv(val)) 
  			return (new OInexactNumber(longVal)).bopDivide(val);
    long l;
    try
    {
      l = val.castToExactNumber();
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopDivideR( this, e );
    }
    
    if( l == 0 )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OException("divide by zero") );
    
    return OExactNumber.makeExactNumber( longVal / l );
  }
  public Value bopDivideR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
  	  if (checkCnv(val)) 
  			return (new OInexactNumber(longVal)).bopDivideR(val,e);
    return OExactNumber.makeExactNumber( val.castToExactNumber() / longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "%" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopRemainder( Value val )
    throws PackagedScriptObjectException
  {
  	  if (checkCnv(val)) 
  			return (new OInexactNumber(longVal)).bopRemainder(val);
    try
    {
      return OExactNumber.makeExactNumber( longVal % val.castToExactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopRemainderR( this, e );
    }
  }
  public Value bopRemainderR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
  	  if (checkCnv(val)) 
  			return (new OInexactNumber(longVal)).bopRemainderR(val,e);
    return OExactNumber.makeExactNumber( val.castToExactNumber() % longVal );
  }
  
  
  /*=======================================================================*/
  /* The unary operators:
   */
  
  /*=======================================================================*/
  /**
   * Perform the "++" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopIncrement()
    throws PackagedScriptObjectException
  {
    return OExactNumber.makeExactNumber( longVal + 1 );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "--" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopDecrement()
    throws PackagedScriptObjectException
  {
    return OExactNumber.makeExactNumber( longVal - 1 );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "+" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopPlus()
    throws PackagedScriptObjectException
  {
    return OExactNumber.makeExactNumber( +longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "-" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopMinus()
    throws PackagedScriptObjectException
  {
    return OExactNumber.makeExactNumber( -longVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "~" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopBitwiseNot()
    throws PackagedScriptObjectException
  {
    return OExactNumber.makeExactNumber( ~longVal );
  }

  @Override
  public boolean castToBooleanSoft() throws PackagedScriptObjectException
  {
	  // VisionR Fix -> add standard javascript like operator
	  if (longVal == 0)
		  return false;
	  return true;
  }
}



/*
 *   Local Variables:
 *   tab-width: 2
 *   indent-tabs-mode: nil
 *   mode: java
 *   c-indentation-style: java
 *   c-basic-offset: 2
 *   eval: (c-set-offset 'substatement-open '0)
 *   eval: (c-set-offset 'case-label '+)
 *   eval: (c-set-offset 'inclass '+)
 *   eval: (c-set-offset 'inline-open '0)
 *   End:
 */

