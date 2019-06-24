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
 * An inexact number is a non-integer number.  An exact number can be
 * converted to an inexact-number with no loss of precision, but not
 * visa-versa.  An <code>OInxactNumber</code> is immutable, meaning once
 * the instance is constructed, it won't change.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class OInexactNumber extends OObject implements java.io.Externalizable,OInexactInterface
{
  /**
   * The type object for an instance of InexactNumber.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.OInexactNumber");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "InexactNumber";
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
                                                      "bopPlus",
                                                      "bopPlusR",
                                                      "bopMinus",
                                                      "bopMinusR",
                                                      "bopMultiply",
                                                      "bopMultiplyR",
                                                      "bopDivide",
                                                      "bopDivideR",
                                                      "uopIncrement",
                                                      "uopDecrement",
                                                      "uopPlus",
                                                      "uopMinus",
                                                    };
  
  /*=======================================================================*/
  /**
   */
  public static final OInexactNumber makeInexactNumber( double doubleVal )
  {
    return new OInexactNumber(doubleVal);
  }
  
  /*=======================================================================*/
  // members:
  private double doubleVal;
  
  // Externalizable support:
  public OInexactNumber() {}
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void readExternal( java.io.ObjectInput in )
    throws java.io.IOException
  {
    doubleVal = in.readDouble();
  }
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void writeExternal( java.io.ObjectOutput out )
    throws java.io.IOException
  {
    out.writeDouble(doubleVal);
  }
  /*=======================================================================*/
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param doubleVal    the value of this inexact number
   */
  public OInexactNumber( double doubleVal )
  {
    super();
    this.doubleVal = doubleVal;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.  This is the constructor that is called via a
   * <code>BuiltinType</code> instance.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public OInexactNumber( oscript.util.MemberTable args )
  {
    super();
    
    if( args.length() != 1 )
    {
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
    }
    else
    {
      doubleVal = args.referenceAt(0).castToInexactNumber();
    }
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
    if( (obj instanceof OInexactNumber) &&
        (((OInexactNumber)obj).doubleVal == doubleVal) )
    {
      return true;
    }
    else if( (obj instanceof Number) &&
             (((Number)obj).doubleValue() == doubleVal) )
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
   * Convert this object to a native java <code>String</code> value.
   * 
   * @return a String value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public String castToString()
    throws PackagedScriptObjectException
  {
    return String.valueOf(doubleVal);
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>long</code> value.
   * 
   * @return a long value
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public long castToExactNumber()
    throws PackagedScriptObjectException
  {
    return (long)doubleVal;
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
    return doubleVal;
  }
  
  public static Value _bopCast( Value val )
  {
    if( val instanceof OInexactNumber )
      return val;
    return makeInexactNumber( val.castToInexactNumber() );
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
    return Double.valueOf(doubleVal);
  }
  
  public int intValue() { return (int)doubleVal; }
  public float floatValue() { return (float)doubleVal; }
  public short shortValue() { return (short)doubleVal; }
  public char charValue() { return (char)doubleVal; }
  public byte byteValue() { return (byte)doubleVal; }
  
  /*=======================================================================*/
  /* The binary operators:
   */
  
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
    try
    {
      return OBoolean.makeBoolean( doubleVal == val.castToInexactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopEqualsR( this, e );
    }
  }
  public Value bopEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( val.castToInexactNumber() == doubleVal );
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
    try
    {
      return OBoolean.makeBoolean( doubleVal != val.castToInexactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopNotEqualsR( this, e );
    }
  }
  public Value bopNotEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( val.castToInexactNumber() != doubleVal );
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
    try
    {
      return OBoolean.makeBoolean( doubleVal < val.castToInexactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopLessThanR( this, e );
    }
  }
  public Value bopLessThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( val.castToInexactNumber() < doubleVal );
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
    try
    {
      return OBoolean.makeBoolean( doubleVal > val.castToInexactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopGreaterThanR( this, e );
    }
  }
  public Value bopGreaterThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( val.castToInexactNumber() > doubleVal );
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
    try
    {
      return OBoolean.makeBoolean( doubleVal <= val.castToInexactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopLessThanOrEqualsR( this, e );
    }
  }
  public Value bopLessThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( val.castToInexactNumber() <= doubleVal );
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
    try
    {
      return OBoolean.makeBoolean( doubleVal >= val.castToInexactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopGreaterThanOrEqualsR( this, e );
    }
  }
  public Value bopGreaterThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( val.castToInexactNumber() >= doubleVal );
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
    try
    {
      return OInexactNumber.makeInexactNumber( doubleVal + val.castToInexactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopPlusR( this, e );
    }
  }
  public Value bopPlusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OInexactNumber.makeInexactNumber( val.castToInexactNumber() + doubleVal );
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
      return OInexactNumber.makeInexactNumber( doubleVal - val.castToInexactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopMinusR( this, e );
    }
  }
  public Value bopMinusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OInexactNumber.makeInexactNumber( val.castToInexactNumber() - doubleVal );
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
      return OInexactNumber.makeInexactNumber( doubleVal * val.castToInexactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopMultiplyR( this, e );
    }
  }
  public Value bopMultiplyR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OInexactNumber.makeInexactNumber( val.castToInexactNumber() * doubleVal );
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
    try
    {
      return OInexactNumber.makeInexactNumber( doubleVal / val.castToInexactNumber() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopDivideR( this, e );
    }
  }
  public Value bopDivideR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OInexactNumber.makeInexactNumber( val.castToInexactNumber() / doubleVal );
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
    return OInexactNumber.makeInexactNumber( doubleVal + 1.0 );
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
    return OInexactNumber.makeInexactNumber( doubleVal - 1.0 );
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
    return OInexactNumber.makeInexactNumber( +doubleVal );
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
    return OInexactNumber.makeInexactNumber( -doubleVal );
  }
  
  @Override
  public boolean castToBooleanSoft() throws PackagedScriptObjectException
  {
	  // VisionR Fix -> add standard javascript like operator
	  if (doubleVal == 0.0)
		  return false;
	  return true;
  }

	@Override
	public boolean isInexactNumber() {
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

