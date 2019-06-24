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
 * A boolean type, can have either the value <i>true</i> or <i>false</i>.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class OBoolean extends OObject implements java.io.Externalizable
{
  /**
   * The type object for an instance of Boolean.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.OBoolean");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "Boolean";
  public final static String[] MEMBER_NAMES   = new String[] {
                                                      "castToBoolean",
                                                      "castToString",
                                                      "castToJavaObject",
                                                      "bopLogicalOr",
                                                      "bopLogicalOrR",
                                                      "bopLogicalAnd",
                                                      "bopLogicalAndR",
                                                      "bopEquals",
                                                      "bopEqualsR",
                                                      "bopNotEquals",
                                                      "bopNotEqualsR",
                                                      "uopLogicalNot"
                                                    };
  
  public static final OBoolean TRUE  = new OBoolean(true);
  public static final OBoolean FALSE = new OBoolean(false);
  
  /*=======================================================================*/
  /**
   */
  public static final OBoolean makeBoolean( boolean booleanVal )
  {
    return booleanVal ? TRUE : FALSE;
  }
  
  /*=======================================================================*/
  // members:
  protected boolean booleanVal;
  
  // Externalizable support:
  public OBoolean() {}
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void readExternal( java.io.ObjectInput in )
    throws java.io.IOException
  {
    booleanVal = in.readBoolean();
  }
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void writeExternal( java.io.ObjectOutput out )
    throws java.io.IOException
  {
    out.writeBoolean(booleanVal);
  }
  /*=======================================================================*/
  
  /*=======================================================================*/
  /**
   * Class Constructor
   * 
   * @param booleanVal          the value of this boolean
   */
  public OBoolean( boolean booleanVal )
  {
    super();
    this.booleanVal = booleanVal;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.  This is the constructor that is called via the
   * <code>BuiltinType</code> instance.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public OBoolean( oscript.util.MemberTable args )
  {
    super();
    
    if( args.length() != 1 )
    {
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
    }
    else
    {
      booleanVal = args.referenceAt(0).castToBoolean();
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
   * Convert this object to a native java <code>boolean</code> value.
   * 
   * @return a boolean value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public boolean castToBoolean()
    throws PackagedScriptObjectException
  {
    return booleanVal;
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
    return booleanVal ? "true" : "false";
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
    return booleanVal ? Boolean.TRUE : Boolean.FALSE;
  }
  
  /*=======================================================================*/
  /* The binary operators:
   */
  
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
    try
    {
      return makeBoolean( booleanVal || val.castToBoolean() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopLogicalOrR( this, e );
    }
  }
  public Value bopLogicalOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return makeBoolean( val.castToBoolean() || booleanVal );
  }
  
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
    try
    {
      return makeBoolean( booleanVal && val.castToBoolean() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopLogicalAndR( this, e );
    }
  }
  public Value bopLogicalAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return makeBoolean( val.castToBoolean() && booleanVal );
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
    try
    {
      return makeBoolean( booleanVal == val.castToBoolean() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopEqualsR( this, e );
    }
  }
  public Value bopEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return makeBoolean( val.castToBoolean() == booleanVal );
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
    try
    {
      return makeBoolean( booleanVal != val.castToBoolean() );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopNotEqualsR( this, e );
    }
  }
  public Value bopNotEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return makeBoolean( val.castToBoolean() != booleanVal );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "!" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value uopLogicalNot()
    throws PackagedScriptObjectException
  {
    return makeBoolean( ! booleanVal );
  }
  
  @Override
	public boolean castToBooleanSoft() throws PackagedScriptObjectException {
		return castToBoolean();
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

