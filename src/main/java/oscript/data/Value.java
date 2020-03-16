/*=============================================================================
 *     Copyright Texas Instruments 2000-2005.  All Rights Reserved.
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
 */


package oscript.data;

import oscript.exceptions.*;
import oscript.util.StackFrame;
import oscript.util.MemberTable;

/**
 * The base class of all values in the interpreter.  This class provides
 * methods (which throw script-exceptions if not overloaded), so that the
 * interpreter has a handy way of calling the methods needed to evaluate
 * a program.  This methods can be overloaded by built-in (ie native java)
 * methods for the built-in types, or via <code>ScriptObject</code> for
 * script types.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public abstract class Value
  implements java.io.Serializable
{
  /**
   * Various and asundry special values.  UNDEFINED is different from
   * NULL in that it is used for un-initialized variables or array
   * entries.  A variable or array entry cannot be given the value
   * UNDEFINED, but can be assigned the value NULL.
   */
  public static final Value UNDEFINED = OSpecial.makeSpecial("(undefined)");
  public static final Value NULL      = OSpecial.makeSpecial("(null)");
  
  /**
   * The type object for an instance of Value... value can't really be
   * instantiated, but this is needed internally.
   */
  public final static BuiltinType TYPE = BuiltinType.makeBuiltinType("oscript.data.Value");
  public final static String PARENT_TYPE_NAME = null;
  public final static String TYPE_NAME        = "Value";
  public final static String[] MEMBER_NAMES   = new String[] {
//                        "unhand",
//                        "getType",
//                        "castToJavaObject",
//                        "castToString",
//                        "bopInstanceOf",
//                        "bopInstanceOfR",
//                        "bopEquals",
//                        "bopNotEquals",
                     };
  
  public final static boolean DEBUG = false;
  
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param type         the type of this object
   */
  public Value() {}
  
  /*=======================================================================*/
  /**
   * For references to an object (ie variables), this returns the actual
   * value this is a reference to, otherwise this return <code>this</code>.
   * 
   * @return the actual object
   */
  public Value unhand()
  {
    return this;
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
    return this;
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
    throw noSuchMember("isA");
  }
  
  /*=======================================================================*/
  /**
   * Get the type of this object.
   * 
   * @return the object's type
   */
  public Value getType()
  {
    Value type = getTypeImpl();
    if( type == null )
      return UNDEFINED;
    else
      return type;
  }
  
  /*=======================================================================*/
  /**
   * Get the type of this object.  The returned type doesn't have to take
   * into account the possibility of a script type extending a built-in
   * type, since that is handled by {@link #getType}.
   * 
   * @return the object's type
   */
  protected abstract Value getTypeImpl();
  
  /*=======================================================================*/
  /* The following methods are used to convert script objects to java
   * objects.  Since everything is a method call, script objects can 
   * implement these methods if they support a particular conversion.
   * (The script object method actually returns a script object, whose
   * corresponding method is called to convert to a java type... so
   * the script object only converts to a built-in type.  The names of
   * the methods for the script types are the same.)
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
    throw noSuchMember("castToBoolean");
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
    // XXX this is sorta hacky... for the benefit of java objects, we want
    // to defer to toString(), but the toString() method in this class 
    // calls this method!  perhaps the best answer is to drop the "cast"
    // part of the castToXXX() methods so the names match java...
    if( this != castToJavaObject() )
      return castToJavaObject().toString();
    else
      return "[object]";
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
    throw noSuchMember("castToExactNumber");
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
    throw noSuchMember("castToInexactNumber");
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
    return this;
  }
  
  /*=======================================================================*/
  /* The binary operators:
   */
  
  /* 
   * Description of binary operator negotiation protocol:
   * 
   *   Ok, so you have some code of the form "a <BOP> b", what do you do in
   *   the case that b cannot be cast to a type that a can deal with?  Ie,
   *   "a.<BOP_METHOD_NAME>(b)" won't work, so you flip the args, and call
   *   the "reversed" method "b.<BOP_METHOD_NAME>R( a, e )".  Note that the
   *   exception thrown while trying to evaluate "a.<BOP_METHOD_NAME>(b)" is
   *   passed in to the R method, so it can be re-thrown if the R method
   *   fails as well.  Note that the "R" method is called by the normal
   *   method, and the "R" methods are technically not part of the public
   *   interface... they are only public for the benefit of script-objects
   *   that subclass a native type.
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
    if( val.bopInstanceOf(this).castToBoolean() )
      return val;
    return bopCastR( val, noSuchMember("bopCast") );
  }
  public Value bopCastR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    try
    {
      if( this.getType().isA(val) )
        return OBoolean.TRUE;
      else
        return OBoolean.FALSE;
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopInstanceOfR( this, e );
    }
  }
  public Value bopInstanceOfR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    if( val.getType().isA(this) )
      return OBoolean.TRUE;
    else
      return OBoolean.FALSE;
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
    return val.bopLogicalOrR( this, noSuchMember("||") );
  }
  public Value bopLogicalOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopLogicalAndR( this, noSuchMember("&&") );
  }
  public Value bopLogicalAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopBitwiseOrR( this, noSuchMember("|") );
  }
  public Value bopBitwiseOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopBitwiseXorR( this, noSuchMember("^") );
  }
  public Value bopBitwiseXorR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopBitwiseAndR( this, noSuchMember("&") );
  }
  public Value bopBitwiseAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return OBoolean.makeBoolean( unhand() == val.unhand() );
  }
  public Value bopEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( val.unhand() == unhand() );
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
    return bopEquals(val).uopLogicalNot();
  }
  public Value bopNotEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return bopEqualsR( val, e ).uopLogicalNot();
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
    return val.bopLessThanR( this, noSuchMember("<") );
  }
  public Value bopLessThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopGreaterThanR( this, noSuchMember(">") );
  }
  public Value bopGreaterThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopLessThanOrEqualsR( this, noSuchMember("<=") );
  }
  public Value bopLessThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopGreaterThanOrEqualsR( this, noSuchMember(">=") );
  }
  public Value bopGreaterThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopLeftShiftR( this, noSuchMember("<<") );
  }
  public Value bopLeftShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopSignedRightShiftR( this, noSuchMember(">>") );
  }
  public Value bopSignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopUnsignedRightShiftR( this, noSuchMember(">>>") );
  }
  public Value bopUnsignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopPlusR( this, noSuchMember("+") );
  }
  public Value bopPlusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopMinusR( this, noSuchMember("-") );
  }
  public Value bopMinusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopMultiplyR( this, noSuchMember("*") );
  }
  public Value bopMultiplyR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopDivideR( this, noSuchMember("/") );
  }
  public Value bopDivideR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
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
    return val.bopRemainderR( this, noSuchMember("%") );
  }
  public Value bopRemainderR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    throw e;
  }
  
  
  /*=======================================================================*/
  /* The unary operators:
   */
  
  /*=======================================================================*/
  /**
   * Perform the "++" operation.
   * 
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopIncrement()
    throws PackagedScriptObjectException
  {
    throw noSuchMember("++");
  }
  
  /*=======================================================================*/
  /**
   * Perform the "--" operation.
   * 
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopDecrement()
    throws PackagedScriptObjectException
  {
    throw noSuchMember("--");
  }
  
  /*=======================================================================*/
  /**
   * Perform the "+" operation.
   * 
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopPlus()
    throws PackagedScriptObjectException
  {
    throw noSuchMember("+");
  }
  
  /*=======================================================================*/
  /**
   * Perform the "-" operation.
   * 
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopMinus()
    throws PackagedScriptObjectException
  {
    throw noSuchMember("-");
  }
  
  /*=======================================================================*/
  /**
   * Perform the "~" operation.
   * 
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopBitwiseNot()
    throws PackagedScriptObjectException
  {
    throw noSuchMember("~");
  }
  
  /*=======================================================================*/
  /**
   * Perform the "!" operation.
   * 
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value uopLogicalNot()
    throws PackagedScriptObjectException
  {
    throw noSuchMember("!");
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
    throw noSuchMember("=");
  }
  
  /* 
   * Note that there are two variants on the callAsXXX() methods... the 
   * first is the "simple" version, taking an array of parameters, and
   * the second is the "optimized" version (which is used by the compiled
   * code), which passes in the current stack-frame and passes the args
   * as a MemberTable object
   */
  
  /*=======================================================================*/
  /**
   * Call this object as a function.
   * 
   * @param sf           the current stack frame
   * @param args         the arguments to the function, or <code>null</code> if none
   * @return the value returned by the function
   * @throws PackagedScriptObjectException
   * @see Function
   */
  public Value callAsFunction( StackFrame sf, MemberTable args )
    throws PackagedScriptObjectException
  {
	  /*Object o = this.castToJavaObject();
	  if (Nashorn.isScriptFunction(o)) {
		  Object[] jsargs = new Object[args.length()];
		  for (int i=0;i<args.length();i++)
			  jsargs[i]=JSConverter.VR2JS(args.referenceAt(i).unhand());
		  Object jsres;
		try {
			jsres = Nashorn.callScriptFunction(o, jsargs);
		} catch (Throwable e) {
		    throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("can't call as function because of JS exception : "+e) );
		}
		  return JSConverter.JS2VR(jsres);
	  }*/
    throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("can't call as function NOT IMPLEMENTED") );
  }
  public final Value callAsFunction( Value oneArg) {
	  Value[] t = new Value[1];
	  t[0]=oneArg;
	  return callAsFunction(t);
  }

  public final Value callAsFunction( Value[] args )
    throws PackagedScriptObjectException
  {
    return callAsFunction( StackFrame.currentStackFrame(), new OArray(args) );
  }
  /** @deprecated */
  public final Value callAsFunction( StackFrame sf, Value[] args )
  {
    throw new RuntimeException("shouldn't get here!!");
  }
  
  /*=======================================================================*/
  /**
   * Call this object as a constructor.
   * 
   * @param sf           the current stack frame
   * @param args         the arguments to the function, or <code>null</code> if none
   * @return the newly constructed object
   * @throws PackagedScriptObjectException
   * @see Function
   */
  public Value callAsConstructor( StackFrame sf, MemberTable args )
    throws PackagedScriptObjectException
  {
    throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("can't call as constructor") );
  }
  public final Value callAsConstructor( Value[] args )
    throws PackagedScriptObjectException
  {
    return callAsConstructor( StackFrame.currentStackFrame(), new OArray(args) );
  }
  /** @deprecated */
  public final Value callAsConstructor( StackFrame sf, Value[] args )
  {
    throw new RuntimeException("shouldn't get here!!");
  }
 
  /*=======================================================================*/
  /**
   * Call this object as a parent class constructor.
   * 
   * @param sf           the current stack frame
   * @param scope        the object
   * @param args         the arguments to the function, or <code>null</code> if none
   * @return the value returned by the function
   * @throws PackagedScriptObjectException
   * @see Function
   */
  public Value callAsExtends( StackFrame sf, Scope scope, MemberTable args )
    throws PackagedScriptObjectException
  {
	  return Value.NULL;
	// js emulation > cancel 	  
    // throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("can't call as constructor") );
  }
  public final Value callAsExtends( Scope scope, MemberTable args )
    throws PackagedScriptObjectException
  {
    return callAsExtends( StackFrame.currentStackFrame(), scope, args );
  }
  /** @deprecated */
  public final Value callAsExtends( StackFrame sf, Scope scope, Value[] args )
  {
    throw new RuntimeException("shouldn't get here!!");
  }
 
  /*=======================================================================*/
  /**
   * Get a member of this object.  This method is provided for convenience.
   * 
   * @param name         the name of the member
   * @return a reference to the member
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   * @see #populateMemberSet
   */
  public final Value getMember( String name )
    throws PackagedScriptObjectException
  {
    return getMember( Symbol.getSymbol(name).getId() );
  }
  
  /*=======================================================================*/
  /**
   * Get a member of this object.
   * 
   * @param name         the name of the member
   * @return a reference to the member
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   * @see #populateMemberSet
   */
// NOTE: not final to fix OBJS56
  public /*final*/ Value getMember( Value name )
    throws PackagedScriptObjectException
  {
    return getMember( Symbol.getSymbol(name).getId() );
  }
  
  /*=======================================================================*/
  /**
   * Get a member of this object.
   * 
   * @param id           the id of the symbol that maps to the member
   * @return a reference to the member
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   * @see #populateMemberSet
   */
  public final Value getMember( int id )
    throws PackagedScriptObjectException
  {
    return getMember( id, true );
  }
  
  /**
   * This isn't really part of the public interface, but is provided for
   * the generated wrapper classes.
   */
  public final Value getMember( String name, boolean exception )
    throws PackagedScriptObjectException
  {
    return getMember( Symbol.getSymbol(name).getId(), exception );
  }
  public final Value getMember( Value name, boolean exception )
    throws PackagedScriptObjectException
  {
    return getMember( Symbol.getSymbol(name).getId(), exception );
  }
  public Value getMember( int id, boolean exception )
    throws PackagedScriptObjectException
  {
    Value member = getType().getTypeMember( this, id );
    
    if( (member == null) && exception )
      throw noSuchMember(Symbol.getSymbol(id).castToString());
    else
      return member;
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
   * @see #populateTypeMemberSet
   */
  protected Value getTypeMember( Value obj, int id )
  {
    return null;
  }
  
  // XXX hack to ensure classes that extend Value to access these protected methods
  public static Value _getTypeMember( Value type, Value obj, int id ) { return type.getTypeMember( obj, id ); }
  public static void _populateTypeMemberSet( Value type, java.util.Set s, boolean d ) { type.populateTypeMemberSet(s,d); }
  
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
    throw noSuchMember("length");
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
    throw noSuchMember("elementAt");
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
    throw noSuchMember("elementsAt");
  }
  
  /*=======================================================================*/
  /**
   * Returns the names of the members of this object.
   * 
   * @return a collection view of the names of the members of this object
   */
  public java.util.Set memberSet()
  {
    java.util.Set s = new java.util.HashSet();
    populateMemberSet( s, false );
    getType().populateTypeMemberSet( s, false );
    return s;
  }
  
  /*=======================================================================*/
  /**
   * Derived classes that implement {@link #getMember} should also
   * implement this.
   * 
   * @param s   the set to populate
   * @param debugger  <code>true</code> if being used by debugger, in
   *   which case both public and private/protected field names should 
   *   be returned
   * @see #getMember
   */
  protected void populateMemberSet( java.util.Set s, boolean _dbg )
  {
  }
  
  /*=======================================================================*/
  /**
   * Derived classes that implement {@link #getTypeMember} should also
   * implement this.
   * 
   * @param s   the set to populate
   * @param debugger  <code>true</code> if being used by debugger, in
   *   which case both public and private/protected field names should 
   *   be returned
   * @see #getTypeMember
   */
  protected void populateTypeMemberSet( java.util.Set s, boolean _dbg )
  {
  }
  
  /*=======================================================================*/
  /**
   * Convert this value to a string, for the benefit of java code.
   * 
   * @return a string
   */
  public String toString()
  {
    return castToString();
  }
  
  protected PackagedScriptObjectException noSuchMember( String member )
  {
    return PackagedScriptObjectException.makeExceptionWrapper( new ONoSuchMemberException( getType(), member ) );
  }
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it, or call {@link #_externalInit} from the
   * {@link #readExternal} method.  This class doesn't (yet!) implement
   * <code>Externalizable</code> because that would force all subclasses
   * to implement it too (ie. override this methods).
   */
  public void readExternal( java.io.ObjectInput in )
    throws ClassNotFoundException, java.io.IOException
  {
  }
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it, or call {@link #setType} from the
   * {@link #readExternal} method.  This class doesn't (yet!) implement
   * <code>Externalizable</code> because that would force all subclasses
   * to implement it too (ie. override this methods).
   */
  public void writeExternal( java.io.ObjectOutput out )
    throws java.io.IOException
  {
  }
  
  public boolean castToBooleanSoft() throws PackagedScriptObjectException
  {
	  return !(this.bopEquals(Value.NULL).castToBoolean()); 
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

