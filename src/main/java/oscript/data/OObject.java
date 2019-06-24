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
 * The built-in type <i>Object</i>, which is the type that implements the
 * root of the inheritance hierarchy of all language types.  All types
 * either directly or indirectly inherit from this class.
 * <p>
 * Some of the class hierarchy should be re-thought... perhaps the methods
 * that are implemented in <code>Value</code> should be moved to this
 * class?
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class OObject extends Value
{
  /**
   * The type object for an instance of Object.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.OObject");
  public final static String PARENT_TYPE_NAME = null;
  public final static String TYPE_NAME        = "Object";
  public final static String[] MEMBER_NAMES   = new String[] {
                                                      "unhand",
                                                      "getType",
                                                      "castToJavaObject",
                                                      "castToString",
                                                      "bopInstanceOf",
                                                      "bopInstanceOfR",
                                                      "bopEquals",
                                                      "bopNotEquals",
                                                      "memberSet",
                                                      // from java.lang.Object:
                                                      "hashCode",
                                                      "equals",
                                                      "toString",
                                                      "notify",
                                                      "notifyAll",
                                                      "wait",
                                                      "getMember"
                                                    };
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   */
  public OObject()
  {
    super();
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.  This is the constructor that gets called via an
   * BuiltinType instance.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public OObject( oscript.util.MemberTable args )
  {
    super();
    
    if( args.length() != 0 )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
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
   * @throws PackagedObjectException(NoSuchMethodException)
   */
  public String castToString()
    throws PackagedScriptObjectException
  {
    return "[object]";
  }
  
  /*=======================================================================*/
  /**
   * Get a member of this object.
   * 
   * @param name         the name of the member
   * @return a reference to the member
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value getMember( int id, boolean exception )
    throws PackagedScriptObjectException
  {
    return super.getMember( id, exception );
  }
  
//   public Value getMember( Value name )
//     throws PackagedScriptObjectException
//   {
//     Value val = null;
//     Value scriptObject = ClassWrapGen.getScriptObject(this);
//     if( scriptObject != null )
//       val = scriptObject.getMember( name, false );
//     if( val != null )
//       return val;
//     return super.getMember(name);
//   }
  
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
    /*Value scriptObject = ClassWrapGen.getScriptObject(this);
    if( scriptObject != null )
      scriptObject.populateMemberSet( s, debugger );*/
    super.populateMemberSet( s, _dbg );
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

