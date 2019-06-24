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
 * A script-object is basically just a scope, but also provides java
 * wrappers for all the methods defined in <code>Value</code>, which allows
 * a lot of flexibility for script objects to extend built-in types, or
 * implement built-in operators (methods), such as +, -, *, /, etc., etc.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class ScriptObject extends BasicScope
{
  /**
   * If this object is of a type that extends a java class, the superFxn
   * must attach a java-object to this object.  The object is an instance
   * of the java class wrapped by the superFxn.
   */
  private Object javaObject = null;
  
  /**
   * The type of the script object.
   */
  private Value type;
  
  private static final Value[] EMPTY_ARRAY = new Value[] {};
  
  /**
   * The type object for an instance of ScriptObject... which can't really be
   * instantiated, but this is needed internally.
   */
  public final static BuiltinType TYPE = BuiltinType.makeBuiltinType("oscript.data.ScriptObject");
  public final static String PARENT_TYPE_NAME = null;
  public final static String TYPE_NAME        = "ScriptObject";
  public final static String[] MEMBER_NAMES   = new String[] {
//                        "_unhand",
//                        "_getType",
//                        "_castToJavaObject",
//                        "_castToString",
//                        "_bopInstanceOf",
//                        "_bopInstanceOfR",
//                        "_bopEquals",
//                        "_bopNotEquals",
                     };
  
  /* What about members like "getType"?  Right now, we don't overload
   * so it would be weird if script type overloaded that member.  We
   * can't do it with the getMember("getType").callAsFunction(...)
   * approach, because the getType method of OObject is not what we
   * want...
   * 
   * For pretty much everything, if we catch an exception we call
   * super.whateverMethod(with,args)... this is sorta like multiple
   * inheritance for objects that subclass java types, but is needed
   * to ensure that all objects in the system have the methods that
   * are implemented in Value... the counterpart to this for objects
   * that are instances of java types is in JavaClassWrapper, where
   * it calls Value.TYPE.getTypeMemberImpl()...
   * 
   * what we should really do is make the getMember() call not throw
   * an exception, but use the method from Value... hmmm... but when
   * I was doing that I was getting an "object not instance of
   * delaring class" type error from JavaMethodWrapper...
   */
  
  /*=======================================================================*/
  /**
   * Class Constructor.  Construct a element in the scope chain.  This
   * constructs a "function" element in the scope chain.  This is
   * called from the <code>Function</code> class when a function is 
   * evaluated.
   * 
   * @param type         the type of the object
   * @param previous     previous in environment scope chain
   * @param smit         shared member idx table
   */
  ScriptObject( Value type, Scope previous, oscript.util.SymbolTable smit )
  {
    super( previous, smit, new OArray(smit.size()) );
    this.type = type;
  }
  
  /* Needed to make JavaClassWrapper happy...
   */
  public Value _getType()
  {
    return super.getType();
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
    return type;
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
    return (javaObject != null) ? javaObject : super.getMonitor();
  }
  
  private static final int CASTTOBOOLEAN = Symbol.getSymbol("castToBoolean").getId();
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>boolean</code> value.
   * 
   * @return a boolean value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public boolean castToBoolean()
    throws PackagedScriptObjectException
  {
    return getMember(CASTTOBOOLEAN).callAsFunction(EMPTY_ARRAY).castToBoolean();
  }
  public boolean _castToBoolean()
    throws PackagedScriptObjectException
  {
    return super.castToBoolean();
  }
  
  private static final int CASTTOSTRING = Symbol.getSymbol("castToString").getId();
  
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
    return getMember(CASTTOSTRING).callAsFunction(EMPTY_ARRAY).castToString();
    // handled specially for objects that subclass java types:
//     try
//     {
//       return getMember(CASTTOSTRING).callAsFunction(EMPTY_ARRAY).castToString();
//     }
//     catch(PackagedScriptObjectException e)
//     {
//       // this causes infinite loop due to Value::toString() calling castToString()...
//       try
//       {
//         return getMember(TOSTRING).callAsFunction(EMPTY_ARRAY).castToString();
//       }
//       catch(PackagedScriptObjectException e2)
//       {
//         throw e;
//       }
//     }
  }
  public String _castToString()
    throws PackagedScriptObjectException
  {
    return super.castToString();
  }
    
  private static final int CASTTOEXACTNUMBER = Symbol.getSymbol("castToExactNumber").getId();
  
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
    return getMember(CASTTOEXACTNUMBER).callAsFunction(EMPTY_ARRAY).castToExactNumber();
  }
  public long _castToExactNumber()
    throws PackagedScriptObjectException
  {
    return super.castToExactNumber();
  }
    
  private static final int CASTTOINEXACTNUMBER = Symbol.getSymbol("castToInexactNumber").getId();
  
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
    return getMember(CASTTOINEXACTNUMBER).callAsFunction(EMPTY_ARRAY).castToInexactNumber();
  }
  public double _castToInexactNumber()
    throws PackagedScriptObjectException
  {
    return super.castToInexactNumber();
  }
    
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>Object</code> value.
   * 
   * @return a java object
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Object castToJavaObject()
    throws PackagedScriptObjectException
  {
    // XXX what about script objects implementing castToJavaObject?  Should 
    //     that be allowed?
    
    if( javaObject != null )
    {
      return javaObject;
    }
    else
    {
      return super.castToJavaObject();
    }
  }
  
  /*=======================================================================*/
  /**
   * Set the java-object associated with a script object... this is used
   * when a script type subclasses a java type.
   * 
   * @param javaObject   the java-object
   */
  public void __setJavaObject( Object javaObject )
  {
    this.javaObject = javaObject;
  }
  
  static final int _BOPCAST = Symbol.getSymbol("_bopCast").getId();
  private static final int BOPCAST = Symbol.getSymbol("bopCast").getId();
  private static final int BOPCASTR = Symbol.getSymbol("bopCastR").getId();
  
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
    return getMember(BOPCAST).callAsFunction( new Value[] { val } );
  }
  public Value _bopCast( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopCast(val);
  }
  public Value bopCastR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPCASTR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopCastR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopCastR( val, e );
  }
  
  private static final int BOPINSTANCEOF = Symbol.getSymbol("bopInstanceOf").getId();
  private static final int BOPINSTANCEOFR = Symbol.getSymbol("bopInstanceOfR").getId();
  
  /*=======================================================================*/
  /**
   * Perform the instanceof operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopInstanceOf( Value val )
    throws PackagedScriptObjectException
  {
    return getMember(BOPINSTANCEOF).callAsFunction( new Value[] { val } );
  }
  public Value _bopInstanceOf( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopInstanceOf(val);
  }
  public Value bopInstanceOfR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPINSTANCEOFR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopInstanceOfR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopInstanceOfR( val, e );
  }
    
  private static final int BOPLOGICALOR = Symbol.getSymbol("bopLogicalOr").getId();
  private static final int BOPLOGICALORR = Symbol.getSymbol("bopLogicalOrR").getId();
  
  /*=======================================================================*/
  /**
   * Perform the logical OR operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopLogicalOr( Value val )
    throws PackagedScriptObjectException
  {
    return getMember(BOPLOGICALOR).callAsFunction( new Value[] { val } );
  }
  public Value _bopLogicalOr( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopLogicalOr(val);
  }
  public Value bopLogicalOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPLOGICALORR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopLogicalOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopLogicalOrR( val, e );
  }
    
  private static final int BOPLOGICALAND = Symbol.getSymbol("bopLogicalAnd").getId();
  private static final int BOPLOGICALANDR = Symbol.getSymbol("bopLogicalAndR").getId();
  
  /*=======================================================================*/
  /**
   * Perform the logical AND operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopLogicalAnd( Value val )
    throws PackagedScriptObjectException
  {
    return getMember(BOPLOGICALAND).callAsFunction( new Value[] { val } );
  }
  public Value _bopLogicalAnd( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopLogicalAnd(val);
  }
  public Value bopLogicalAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPLOGICALANDR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopLogicalAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopLogicalAndR( val, e );
  }
    
  private static final int BOPBITWISEOR = Symbol.getSymbol("bopBitwiseOr").getId();
  private static final int BOPBITWISEORR = Symbol.getSymbol("bopBitwiseOrR").getId();
  
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
    return getMember(BOPBITWISEOR).callAsFunction( new Value[] { val } );
  }
  public Value _bopBitwiseOr( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopBitwiseOr(val);
  }
  public Value bopBitwiseOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPBITWISEORR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopBitwiseOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopBitwiseOrR( val, e );
  }
    
  private static final int BOPBITWISEXOR = Symbol.getSymbol("bopBitwiseXor").getId();
  private static final int BOPBITWISEXORR = Symbol.getSymbol("bopBitwiseXorR").getId();
  
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
    return getMember(BOPBITWISEXOR).callAsFunction( new Value[] { val } );
  }
  public Value _bopBitwiseXor( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopBitwiseXor(val);
  }
  public Value bopBitwiseXorR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPBITWISEXORR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopBitwiseXorR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopBitwiseXorR( val, e );
  }
    
  private static final int BOPBITEWISEAND = Symbol.getSymbol("bopBitewiseAnd").getId();
  private static final int BOPBITEWISEANDR = Symbol.getSymbol("bopBitewiseAndR").getId();
  
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
    return getMember(BOPBITEWISEAND).callAsFunction( new Value[] { val } );
  }
  public Value _bopBitwiseAnd( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopBitwiseAnd(val);
  }
  public Value bopBitwiseAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPBITEWISEANDR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopBitwiseAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopBitwiseAndR( val, e );
  }
    
  private static final int BOPEQUALS = Symbol.getSymbol("bopEquals").getId();
  private static final int BOPEQUALSR = Symbol.getSymbol("bopEqualsR").getId();
  
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
    return getMember(BOPEQUALS).callAsFunction( new Value[] { val } );
  }
  public Value _bopEquals( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopEquals(val);
  }
  public Value bopEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPEQUALSR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopEqualsR( val, e );
  }
    
  private static final int BOPNOTEQUALS = Symbol.getSymbol("bopNotEquals").getId();
  private static final int BOPNOTEQUALSR = Symbol.getSymbol("bopNotEqualsR").getId();
  
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
    return getMember(BOPNOTEQUALS).callAsFunction( new Value[] { val } );
  }
  public Value _bopNotEquals( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopNotEquals(val);
  }
  public Value bopNotEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPNOTEQUALSR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopNotEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopNotEqualsR( val, e );
  }
    
  private static final int BOPLESSTHAN = Symbol.getSymbol("bopLessThan").getId();
  private static final int BOPLESSTHANR = Symbol.getSymbol("bopLessThanR").getId();
  
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
    return getMember(BOPLESSTHAN).callAsFunction( new Value[] { val } );
  }
  public Value _bopLessThan( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopLessThan(val);
  }
  public Value bopLessThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPLESSTHANR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopLessThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopLessThanR( val, e );
  }
    
  private static final int BOPGREATERTHAN = Symbol.getSymbol("bopGreaterThan").getId();
  private static final int BOPGREATERTHANR = Symbol.getSymbol("bopGreaterThanR").getId();
  
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
    return getMember(BOPGREATERTHAN).callAsFunction( new Value[] { val } );
  }
  public Value _bopGreaterThan( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopGreaterThan(val);
  }
  public Value bopGreaterThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPGREATERTHANR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopGreaterThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopGreaterThanR( val, e );
  }
    
  private static final int BOPLESSTHANOREQUALS = Symbol.getSymbol("bopLessThanOrEquals").getId();
  private static final int BOPLESSTHANOREQUALSR = Symbol.getSymbol("bopLessThanOrEqualsR").getId();
  
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
    return getMember(BOPLESSTHANOREQUALS).callAsFunction( new Value[] { val } );
  }
  public Value _bopLessThanOrEquals( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopLessThanOrEquals(val);
  }
  public Value bopLessThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPLESSTHANOREQUALSR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopLessThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopLessThanOrEqualsR( val, e );
  }
  
  private static final int BOPGREATORTHANOREQUALS = Symbol.getSymbol("bopGreatorThanOrEquals").getId();
  private static final int BOPGREATORTHANOREQUALSR = Symbol.getSymbol("bopGreatorThanOrEqualsR").getId();
  
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
    return getMember(BOPGREATORTHANOREQUALS).callAsFunction( new Value[] { val } );
  }
  public Value _bopGreaterThanOrEquals( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopGreaterThanOrEquals(val);
  }
  public Value bopGreaterThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPGREATORTHANOREQUALSR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopGreaterThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopGreaterThanOrEqualsR( val, e );
  }
  
  private static final int BOPLEFTSHIFT = Symbol.getSymbol("bopLeftShift").getId();
  private static final int BOPLEFTSHIFTR = Symbol.getSymbol("bopLeftShiftR").getId();
  
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
    return getMember(BOPLEFTSHIFT).callAsFunction( new Value[] { val } );
  }
  public Value _bopLeftShift( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopLeftShift(val);
  }
  public Value bopLeftShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPLEFTSHIFTR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopLeftShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopLeftShiftR( val, e );
  }
  
  private static final int BOPSIGNEDRIGHTSHIFT = Symbol.getSymbol("bopSignedRightShift").getId();
  private static final int BOPSIGNEDRIGHTSHIFTR = Symbol.getSymbol("bopSignedRightShiftR").getId();
  
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
    return getMember(BOPSIGNEDRIGHTSHIFT).callAsFunction( new Value[] { val } );
  }
  public Value _bopSignedRightShift( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopSignedRightShift(val);
  }
  public Value bopSignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPSIGNEDRIGHTSHIFTR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopSignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopSignedRightShiftR( val, e );
  }
  
  private static final int BOPUNSIGNEDRIGHTSHIFT = Symbol.getSymbol("bopUnsignedRightShift").getId();
  private static final int BOPUNSIGNEDRIGHTSHIFTR = Symbol.getSymbol("bopUnsignedRightShiftR").getId();
  
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
    return getMember(BOPUNSIGNEDRIGHTSHIFT).callAsFunction( new Value[] { val } );
  }
  public Value _bopUnsignedRightShift( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopUnsignedRightShift(val);
  }
  public Value bopUnsignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPUNSIGNEDRIGHTSHIFTR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopUnsignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopUnsignedRightShiftR( val, e );
  }
  
  private static final int BOPPLUS = Symbol.getSymbol("bopPlus").getId();
  private static final int BOPPLUSR = Symbol.getSymbol("bopPlusR").getId();
  
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
    return getMember(BOPPLUS).callAsFunction( new Value[] { val } );
  }
  public Value _bopPlus( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopPlus(val);
  }
  public Value bopPlusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPPLUSR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopPlusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopPlusR( val, e );
  }
  
  private static final int BOPMINUS = Symbol.getSymbol("bopMinus").getId();
  private static final int BOPMINUSR = Symbol.getSymbol("bopMinusR").getId();
  
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
    return getMember(BOPMINUS).callAsFunction( new Value[] { val } );
  }
  public Value _bopMinus( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopMinus(val);
  }
  public Value bopMinusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPMINUSR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopMinusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopMinusR( val, e );
  }
  
  private static final int BOPMULTIPLY = Symbol.getSymbol("bopMultiply").getId();
  private static final int BOPMULTIPLYR = Symbol.getSymbol("bopMultiplyR").getId();
  
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
    return getMember(BOPMULTIPLY).callAsFunction( new Value[] { val } );
  }
  public Value _bopMultiply( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopMultiply(val);
  }
  public Value bopMultiplyR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPMULTIPLYR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopMultiplyR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopMultiplyR( val, e );
  }
    
  private static final int BOPDIVIDE = Symbol.getSymbol("bopDivide").getId();
  private static final int BOPDIVIDER = Symbol.getSymbol("bopDivideR").getId();
  
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
    return getMember(BOPDIVIDE).callAsFunction( new Value[] { val } );
  }
  public Value _bopDivide( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopDivide(val);
  }
  public Value bopDivideR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPDIVIDER).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopDivideR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopDivideR( val, e );
  }
  
  private static final int BOPREMAINDER = Symbol.getSymbol("bopRemainder").getId();
  private static final int BOPREMAINDERR = Symbol.getSymbol("bopRemainderR").getId();
  
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
    return getMember(BOPREMAINDER).callAsFunction( new Value[] { val } );
  }
  public Value _bopRemainder( Value val )
    throws PackagedScriptObjectException
  {
    return super.bopRemainder(val);
  }
  public Value bopRemainderR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return getMember(BOPREMAINDERR).callAsFunction( new Value[] { val, JavaBridge.convertToScriptObject(e) } );
  }
  public Value _bopRemainderR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return super.bopRemainderR( val, e );
  }
  
  private static final int UOPINCREMENT = Symbol.getSymbol("uopIncrement").getId();
  
  /*=======================================================================*/
  /**
   * Perform the "++" operation.
   * 
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopIncrement()
    throws PackagedScriptObjectException
  {
    return getMember(UOPINCREMENT).callAsFunction(EMPTY_ARRAY);
  }
  public Value _uopIncrement()
    throws PackagedScriptObjectException
  {
    return super.uopIncrement();
  }
  
  private static final int UOPDECREMENT = Symbol.getSymbol("uopDecrement").getId();
  
  /*=======================================================================*/
  /**
   * Perform the "--" operation.
   * 
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopDecrement()
    throws PackagedScriptObjectException
  {
    return getMember(UOPDECREMENT).callAsFunction(EMPTY_ARRAY);
  }
  public Value _uopDecrement()
    throws PackagedScriptObjectException
  {
    return super.uopDecrement();
  }
  
  private static final int UOPPLUS = Symbol.getSymbol("uopPlus").getId();
  
  /*=======================================================================*/
  /**
   * Perform the "+" operation.
   * 
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopPlus()
    throws PackagedScriptObjectException
  {
    return getMember(UOPPLUS).callAsFunction(EMPTY_ARRAY);
  }
  public Value _uopPlus()
    throws PackagedScriptObjectException
  {
    return super.uopPlus();
  }
  
  private static final int UOPMINUS = Symbol.getSymbol("uopMinus").getId();
  
  /*=======================================================================*/
  /**
   * Perform the "-" operation.
   * 
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopMinus()
    throws PackagedScriptObjectException
  {
    return getMember(UOPMINUS).callAsFunction(EMPTY_ARRAY);
  }
  public Value _uopMinus()
    throws PackagedScriptObjectException
  {
    return super.uopMinus();
  }
  
  private static final int UOPBITWISENOT = Symbol.getSymbol("uopBitwiseNot").getId();
  
  /*=======================================================================*/
  /**
   * Perform the "~" operation.
   * 
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopBitwiseNot()
    throws PackagedScriptObjectException
  {
    return getMember(UOPBITWISENOT).callAsFunction(EMPTY_ARRAY);
  }
  public Value _uopBitwiseNot()
    throws PackagedScriptObjectException
  {
    return super.uopBitwiseNot();
  }
  
  private static final int UOPLOGICALNOT = Symbol.getSymbol("uopLogicalNot").getId();
  
  /*=======================================================================*/
  /**
   * Perform the "!" operation.
   * 
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopLogicalNot()
    throws PackagedScriptObjectException
  {
    return getMember(UOPLOGICALNOT).callAsFunction(EMPTY_ARRAY);
  }
  public Value _uopLogicalNot()
    throws PackagedScriptObjectException
  {
    return super.uopLogicalNot();
  }
  
  
  /*=======================================================================*/
  /* The misc operators:
   */
  
  private static final int OPASSIGN = Symbol.getSymbol("opAssign").getId();
  
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
    getMember(OPASSIGN).callAsFunction( new Value[] { val } );
  }
  public void _opAssign( Value val )
    throws PackagedScriptObjectException
  {
    super.opAssign(val);
  }
  
  private static final int CALLASFUNCTION = Symbol.getSymbol("callAsFunction").getId();
  
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
    return getMember(CALLASFUNCTION).callAsFunction( new Value[] {
      (args == null) ? new OArray(0) : new OArray(args)
    } );
  }
  public Value _callAsFunction( StackFrame sf, MemberTable args )
    throws PackagedScriptObjectException
  {
    return super.callAsFunction( sf, args );
  }
  
  private static final int CALLASCONSTRUCTOR = Symbol.getSymbol("callAsConstructor").getId();
  
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
    return getMember(CALLASCONSTRUCTOR).callAsFunction( new Value[] {
      (args == null) ? new OArray(0) : new OArray(args)
    } );
  }
  public Value _callAsConstructor( StackFrame sf, MemberTable args )
    throws PackagedScriptObjectException
  {
    return super.callAsConstructor( sf, args );
  }
  
  private static final int CALLASEXTENDS = Symbol.getSymbol("callAsExtends").getId();
  
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
    return getMember(CALLASEXTENDS).callAsFunction( new Value[] { 
      (args == null) ? new OArray(0) : new OArray(args)
    } );
  }
  public Value _callAsExtends( StackFrame sf, Scope scope, MemberTable args )
    throws PackagedScriptObjectException
  {
    return super.callAsExtends( sf, scope, args );
  }
  
  
  private static final int LENGTH = Symbol.getSymbol("length").getId();
  
  /*=======================================================================*/
  /**
   * For types that implement <code>elementAt</code>, this returns the
   * number of elements.  This is the same as the <i>length</i> property
   * of an object.
   * 
   * @return an integer length
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   * @see #elementAt
   */
  public int length()
    throws PackagedScriptObjectException
  {
    return (int)(getMember(LENGTH).callAsFunction(EMPTY_ARRAY).castToExactNumber());
  }
  public int _length()
    throws PackagedScriptObjectException
  {
    return super.length();
  }
  
  private static final int ELEMENTAT = Symbol.getSymbol("elementAt").getId();
  
  /*=======================================================================*/
  /**
   * Get the specified index of this object, if this object is an array.  If
   * needed, the array is grown to the appropriate size.
   * 
   * @param idx          the index to get
   * @return a reference to the member
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   * @see #length
   */
  public Value elementAt( Value idx )
    throws PackagedScriptObjectException
  {
    return getMember(ELEMENTAT).callAsFunction( new Value[] { idx } );
  }
  public Value _elementAt( Value idx )
    throws PackagedScriptObjectException
  {
    return super.elementAt(idx);
  }
  
  private static final int ELEMENTSAT = Symbol.getSymbol("elementsAt").getId();
  
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
    return getMember(ELEMENTSAT).callAsFunction( new Value[] { idx1, idx2 } );
  }
  public Value _elementsAt( Value idx1, Value idx2 )
    throws PackagedScriptObjectException
  {
    return super.elementsAt( idx1, idx2 );
  }
  
  private static final int FINALIZE = Symbol.getSymbol("finalize").getId();
  
  /*=======================================================================*/
  /**
   * Called when the script object is GC'd
   */
  protected void finalize()
    throws PackagedScriptObjectException
  {
    Value fxn = getMemberImpl(FINALIZE);  // use getMemberImpl() so function doesn't have to be public
    if( fxn != null )
      fxn.callAsFunction(EMPTY_ARRAY);
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

