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
import oscript.util.StackFrame;
import oscript.util.MemberTable;


/**
 * A reference forwards all method calls to the object it is a reference
 * for, but additionally a reference is assignable.  It is used when a
 * variable (or array element) is looked up within a scope, and can be
 * used as either an lval or rval.
 * <p>
 * When a property of an object (ie a scope) has attributes associated
 * with it, those attributes are attributes of the reference.  The
 * attributes are partially enforced by this class (in the case of 
 * "const"), and by <code>ScriptObject</code> (in the case of "public").
 * <p>
 * Note that this could extends {@link AbstractReference}, but doesn't
 * because I think that would have a performance impact... one of these
 * days I should actually verify that...
 * 
 * @author Rob Clark (rob@ti.com)
 */
public final class Reference extends Value
{
  /**
   */
  public static final int ATTR_INVALID   = -1;
  public static final int ATTR_PRIVATE   = 0x01000000;
  public static final int ATTR_PROTECTED = 0x02000000;
  public static final int ATTR_PUBLIC    = 0x04000000;
  public static final int ATTR_CONST     = 0x10000000;
  public static final int ATTR_STATIC    = 0x20000000 | ATTR_PUBLIC;  // XXX for now static is also public
  
  /**
   * The attribute bit-mask, either {@link #ATTR_INVALID} or a mask of the
   * other attribute constants.
   */
  private int     attr;
  private boolean readonly;
  private Value   val;
  
  /*=======================================================================*/
  /**
   * Class Constructor.  Create a reference with the default attributes.
   */
  public Reference()
  {
    this(ATTR_INVALID);
  }
  
  public Reference(Value v) {
	  this(ATTR_INVALID);
	  reset(v);
  }
  
  /*=======================================================================*/
  /**
   * Copy Constructor
   */
  public Reference( Reference r )
  {
    this(r.attr);
    this.val = r.val;
    this.readonly = r.readonly;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.  Create a reference with the specified attributes.
   * 
   * @param attr         attribute bitmask
   */
  public Reference( int attr )
  {
    super();
    reset(attr);
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
    return Type.HIDDEN_TYPE;
  }
  
  /*=======================================================================*/
  /**
   * Get access to this reference's attributes.
   */
  int getAttr()
  {
    return attr;
  }
  
  /*=======================================================================*/
  /**
   * Reset this reference, which is used when the reference is re-used (in-
   * stead of re-allocating)
   */
  final void reset( int attr )
  {
    this.attr = attr;
    this.val  = UNDEFINED;
    this.readonly = false;
  }
  
  public final void reset( Value val )
  {
    this.attr = ATTR_PUBLIC;
    this.val  = val.unhand();
    this.readonly = false;
  }
  
  public final void reset()
  {
    this.attr = ATTR_INVALID;
    this.val  = UNDEFINED;
    this.readonly = false;
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
    return val.hashCode();
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
    return val.equals(obj);
  }
  
  /*=======================================================================*/
  /**
   * Determine if this reference is public.  This is used by ScriptObject to
   * determine if it should restrict access to this reference.
   * 
   * @return <code>true</code> if public, else <code>false</code>
   */
  boolean isPublic()
  {
    return (attr & ATTR_PUBLIC) != 0;
  }
  
  /*=======================================================================*/
  /**
   * For references to an object (ie variables), this returns the actual
   * value this is a reference to, otherwise this return <code>this</code>.
   * 
   * @return the actual object
   */
  public Value unhand()
  {
    return val.unhand();
  }
  
  /*=======================================================================*/
  /**
   * Return the object used for implementing <i>synchronized</i>.  For a
   * normal script object, the object is it's own monitor.  For a java
   * object, it is the java object rather than the {@link JavaObjectWrapper}.
   * 
   * @return the object to synchronize on
   */
  public Object getMonitor()
  {
    return val.getMonitor();
  }
  
  /*=======================================================================*/
  /**
   * If this object is a type, determine if an instance of this type is
   * an instance of the specified type, ie. if this is <code>type</code>,
   * or a subclass.
   * 
   * @param type         the type to compare this type to
   * @return <code>true</code> or <code>false</code>
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public boolean isA( Value type )
  {
    return val.isA(type);
  }
  
  /*=======================================================================*/
  /**
   * Get the type of this object.  A reference doesn't actually have a type,
   * but instead is the type of whatever it contains... really I am not sure
   * if a reference is a first class type, or perhaps could be implemented
   * as an inner-class for OArray and ScriptObject.  Perhaps Value should be
   * an interface, and what is now Value becomes some sort of adapter
   * class?
   * 
   * @return the object's type
   */
  public Value getType()
  {
    return val.getType();
  }
  
  /*=======================================================================*/
  /* Casting methods:
   */
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>boolean</code> value.
   * 
   * @return a boolean value
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public boolean castToBoolean()
    throws PackagedScriptObjectException
  {
    return val.castToBoolean();
  }

  public boolean castToBooleanSoft()
		  throws PackagedScriptObjectException
  {
	  return val.castToBooleanSoft();
  }

  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>String</code> value.
   * 
   * @return a String value
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public String castToString()
    throws PackagedScriptObjectException
  {
    return val.castToString();
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
    return val.castToExactNumber();
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>double</code> value.
   * 
   * @return a double value
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public double castToInexactNumber()
    throws PackagedScriptObjectException
  {
    return val.castToInexactNumber();
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
    return val.castToJavaObject();
  }
  
  /*=======================================================================*/
  /* The binary operators:
   */
  
  /*=======================================================================*/
  /**
   * Perform the cast operation, <code>(a)b</code> is equivalent to <code>a.bopCast(b)</code>
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopCast( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopCast(val);
  }
  public Value bopCastR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopCastR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the instanceof operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopInstanceOf( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopInstanceOf(val);
  }
  public Value bopInstanceOfR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopInstanceOfR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the logical OR operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopLogicalOr( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopLogicalOr(val);
  }
  public Value bopLogicalOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopLogicalOrR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the logical AND operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopLogicalAnd( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopLogicalAnd(val);
  }
  public Value bopLogicalAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopLogicalAndR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the bitwise OR operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopBitwiseOr( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopBitwiseOr(val);
  }
  public Value bopBitwiseOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopBitwiseOrR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the bitwise XOR operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopBitwiseXor( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopBitwiseXor(val);
  }
  public Value bopBitwiseXorR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopBitwiseXorR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the bitwise AND operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopBitwiseAnd( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopBitwiseAnd(val);
  }
  public Value bopBitwiseAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopBitwiseAndR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "==" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopEquals( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopEquals(val);
  }
  public Value bopEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopEqualsR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "!=" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopNotEquals( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopNotEquals(val);
  }
  public Value bopNotEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopNotEqualsR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "<" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopLessThan( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopLessThan(val);
  }
  public Value bopLessThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopLessThanR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the ">" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopGreaterThan( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopGreaterThan(val);
  }
  public Value bopGreaterThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopGreaterThanR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "<=" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopLessThanOrEquals( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopLessThanOrEquals(val);
  }
  public Value bopLessThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopLessThanOrEqualsR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the ">=" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopGreaterThanOrEquals( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopGreaterThanOrEquals(val);
  }
  public Value bopGreaterThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopGreaterThanOrEqualsR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "<<" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopLeftShift( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopLeftShift(val);
  }
  public Value bopLeftShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopLeftShiftR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the ">>" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopSignedRightShift( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopSignedRightShift(val);
  }
  public Value bopSignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopSignedRightShiftR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the ">>>" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopUnsignedRightShift( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopUnsignedRightShift(val);
  }
  public Value bopUnsignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopUnsignedRightShiftR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "+" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopPlus( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopPlus(val);
  }
  public Value bopPlusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopPlusR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "-" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopMinus( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopMinus(val);
  }
  public Value bopMinusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopMinusR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "*" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopMultiply( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopMultiply(val);
  }
  public Value bopMultiplyR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopMultiplyR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "/" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopDivide( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopDivide(val);
  }
  public Value bopDivideR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopDivideR( val, e );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "%" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopRemainder( Value val )
    throws PackagedScriptObjectException
  {
    return this.val.bopRemainder(val);
  }
  public Value bopRemainderR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return this.val.bopRemainderR( val, e );
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
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopIncrement()
    throws PackagedScriptObjectException
  {
    return val.uopIncrement();
  }
  
  /*=======================================================================*/
  /**
   * Perform the "--" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopDecrement()
    throws PackagedScriptObjectException
  {
    return val.uopDecrement();
  }
  
  /*=======================================================================*/
  /**
   * Perform the "+" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopPlus()
    throws PackagedScriptObjectException
  {
    return val.uopPlus();
  }
  
  /*=======================================================================*/
  /**
   * Perform the "-" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopMinus()
    throws PackagedScriptObjectException
  {
    return val.uopMinus();
  }
  
  /*=======================================================================*/
  /**
   * Perform the "~" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopBitwiseNot()
    throws PackagedScriptObjectException
  {
    return val.uopBitwiseNot();
  }
  
  /*=======================================================================*/
  /**
   * Perform the "!" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopLogicalNot()
    throws PackagedScriptObjectException
  {
    return val.uopLogicalNot();
  }
  
  /*=======================================================================*/
  /* The misc operators:
   */
  
  /*=======================================================================*/
  /**
   * Perform assignment.  Set the value of this reference to the specified
   * value.
   * 
   * @param val          the value to set this reference to
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public void opAssign( Value val )
    throws PackagedScriptObjectException
  {
    // we can't have a reference to a reference:
    if( val instanceof Reference )
      val = val.unhand();
    
    if( val == UNDEFINED )
    {
      if( !"warning".equals( System.getProperty("oscript.undefined.assign") ) )
        throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("cannot assign (undefined) to variable") );
      
      String desc = "";
      
      //////////////////////////////////////////////////////////////////////////
      // the following code is a hack to attempt to determine what script called
      // this, so we can print a more informative warning message:
      try
      {
        oscript.util.StackFrame sf = oscript.util.StackFrame.currentStackFrame();
        desc = " at " + sf.toString();
      }
      catch(Throwable t)
      {
        // ignore... an exception may be thrown if not called from script 
      }
      //////////////////////////////////////////////////////////////////////////
      oscript.util.ErrorHandler.warning("warning: cannot assign (undefined) to variable" + desc);
    }
    
    // in the case of "const" values, they can be assigned a value
    // only once:
    if( (attr & ATTR_CONST) != 0 )
    {
      if(readonly)
        throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("cannot assign value to constant") );
      else
        readonly = true;
    }
    
    this.val = val;
  }
  
  /*=======================================================================*/
  /**
   * Call this object as a function.
   * 
   * @param sf           the current stack frame
   * @param args         the arguments to the function
   * @return the value returned by the function
   * @throws PackagedScriptObjectException
   * @see Function
   */
  public Value callAsFunction( StackFrame sf, MemberTable args )
    throws PackagedScriptObjectException
  {
    return val.callAsFunction( sf, args );
  }
  
  /*=======================================================================*/
  /**
   * Call this object as a constructor.
   * 
   * @param sf           the current stack frame
   * @param args         the arguments to the function
   * @return the newly constructed object
   * @throws PackagedScriptObjectException
   * @see Function
   */
  public Value callAsConstructor( StackFrame sf, MemberTable args )
    throws PackagedScriptObjectException
  {
    return val.callAsConstructor( sf, args );
  }
  
  /*=======================================================================*/
  /**
   * Call this object as a parent class constructor.
   * 
   * @param sf           the current stack frame
   * @param scope        the object
   * @param args         the arguments to the function
   * @return the value returned by the function
   * @throws PackagedScriptObjectException
   * @see Function
   */
  public Value callAsExtends( StackFrame sf, Scope scope, MemberTable args )
    throws PackagedScriptObjectException
  {
    return val.callAsExtends( sf, scope, args );
  }
  
  /*=======================================================================*/
  /**
   * Get a member of this object.
   * 
   * @param id           the id of the symbol that maps to the member
   * @param exception    whether an exception should be thrown if the
   *   member object is not resolved
   * @return a reference to the member
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value getMember( int id, boolean exception )
    throws PackagedScriptObjectException
  {
    return val.getMember( id, exception );
  }
  
  /*=======================================================================*/
  /**
   * Get a member of this type.  This is used to interface to the java
   * method of having members be attributes of a type.  Regular object-
   * script object's members are attributes of the object, but in the
   * case of java types (including built-in types), the members are
   * attributes of the type.
   * 
   * @param obj          an object of this type
   * @param id           the id of the symbol that maps to the member
   * @return a reference to the member, or null
   */
  protected Value getTypeMember( Value obj, int id )
  {
    return val.getTypeMember( obj, id );
  }
  
  /*=======================================================================*/
  /**
   * For types that implement <code>elementAt</code>, this returns the
   * number of elements.
   * 
   * @return an integer length
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   * @see #elementAt
   * @see #elementsAt
   */
  public int length()
    throws PackagedScriptObjectException
  {
    return val.length();
  }
  
  /*=======================================================================*/
  /**
   * Get the specified index of this object, if this object is an array.  If
   * needed, the array is grown to the appropriate size.
   * 
   * @param idx          the index to get
   * @return a reference to the member
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   * @see #length
   * @see #elementsAt
   */
  public Value elementAt( Value idx )
    throws PackagedScriptObjectException
  {
    return val.elementAt(idx);
  }
  
  /*=======================================================================*/
  /**
   * Get the specified range of this object, if this object is an array.  
   * This returns a copy of a range of the array.
   * 
   * @param idx1         the index index of the beginning of the range, inclusive
   * @param idx2         the index of the end of the range, inclusive
   * @return a copy of the specified range of this array
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   * @see #length
   * @see #elementAt
   */
  public Value elementsAt( Value idx1, Value idx2 )
    throws PackagedScriptObjectException
  {
    return val.elementsAt( idx1, idx2 );
  }
  
  /*=======================================================================*/
  /**
   * Returns the names of the members of this object.
   * 
   * @return a collection view of the names of the members of this object
   */
  public final java.util.Set memberSet()
  {
    return val.memberSet();
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

