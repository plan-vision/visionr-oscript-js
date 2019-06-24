/*=============================================================================
 *     Copyright Texas Instruments 2003-2004.  All Rights Reserved.
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
 * A regular expression object.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public abstract class RegExp extends OObject
{
  /**
   * The type object for an instance of RegExp... implementing class should
   * use these values in the class used to construct the {@link BuiltinType}:
   * <pre>
   *   public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.???RegExp");
   *   public final static String PARENT_TYPE_NAME = RegExp.PARENT_TYPE_NAME;
   *   public final static String TYPE_NAME        = RegExp.TYPE_NAME;
   *   public final static String[] MEMBER_NAMES   = RegExp.MEMBER_NAMES;
   * </pre>
   */
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "RegExp";
  public final static String[] MEMBER_NAMES   = new String[] {
                                                      "getType",
                                                      "castToJavaObject",
                                                      "castToString",
                                                      "bopInstanceOf",
                                                      "bopEquals",
                                                      "bopNotEquals",
                                                      "getMember",
                                                      "exec",
                                                      "test"
                                                    };
  
  
  /**
   * The factory for RegExp objects, provided by the implementation of
   * RegExp
   */
  private static RegExpFactory factory;
  
  /**
   * The string representation of the expression
   */
  private Value exp;
  
  /**
   * The string representation of the flags, or empty string if no flags
   */
  private Value flags;
  
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   */
  protected RegExp( Value exp, Value flags )
  {
    super();
    
    this.exp   = exp;
    this.flags = flags;
  }
  
  /*=======================================================================*/
  /**
   * Used by implementations of RegExp to register themselves as being
   * able to implement RegExp.  This will also create a global variable
   * <code>RegExp</code> which is set to the value returned by factory's
   * {@link RegExpFactory#getType} method.
   * 
   * @param factory      the factory that can construct a regular 
   *    expression object
   */
  public static void register( RegExpFactory factory )
  {
    RegExp.factory = factory;
    oscript.OscriptInterpreter.getGlobalScope().
      createMember("RegExp",0).opAssign( factory.getType() );
  }
  
  /*=======================================================================*/
  /**
   * Create a regular expression.
   * 
   * @param str          the string representation of the regular expression
   */
  public static RegExp createRegExp( Value ostr )
  {
    String str = ostr.castToString();
    
    int    idx = str.lastIndexOf('/');
    String exp = str.substring( 1, idx );
    String flags = (idx < (str.length()-1)) ? str.substring(idx+1) : "";
    
    // strip out escape chars for escaped spaces:
    while( (idx=exp.indexOf("\\ ")) != -1 )
      exp = exp.substring(0,idx) + exp.substring(idx+1);
    
    if( factory == null )
      throw PackagedScriptObjectException.makeExceptionWrapper( new ONoSuchMemberException(UNDEFINED.castToString() + ": RegExp") );
    
    return factory.createRegExp( new OString(exp), new OString(flags) );
  }
  
  /*=======================================================================*/
  /**
   * Interface to be implemented by regular-expression factory
   */
  protected interface RegExpFactory
  {
    /**
     * Get the type object, used to construct a RegExp object.
     */
    public Value getType();
    
    /**
     * Construct a RegExp object.
     * 
     * @param exp    the expression string
     * @param flags  the string of zero or more flags
     * @return the RegExp object
     */
    public RegExp createRegExp( Value exp, Value flags );
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
    return factory.getType();
  }
  
  /*=======================================================================*/
  /**
   * Execute the search for a match against a string.
   * <p>
   * Note that this API is modeled after the JavaScript RegExp API, for
   * the benefit of users already familiar with JavaScript.
   * 
   * @param str   the string to match
   * @return the result object
   */
  public abstract RegExpResult exec( Value str );
  
  /*=======================================================================*/
  /**
   * Executes the search for a match between a regular expression and a 
   * specified string.
   * 
   * @param str   the string to match
   * @return <code>true</code> if match was found
   */
  public boolean test( Value str )
  {
    return exec(str).getIndex().castToExactNumber() != -1;
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
    return "/" + exp + "/" + flags;
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

