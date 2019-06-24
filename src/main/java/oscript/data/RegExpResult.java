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
 * The result of executing a pattern against a string.  This object acts
 * both as an object and as an array with the following properties and
 * indicies:
 * <div id="regtable"><table>
 *   <tr>
 *     <th>Property/Index</th>
 *     <th>Description</th>
 *   </tr>
 *   <tr>
 *     <td>property: <code>index</code></td>
 *     <td>the index of the matched string</td>
 *   </tr>
 *   <tr>
 *     <td>property: <code>input</code></td>
 *     <td>the original string</td>
 *   </tr>
 *   <tr>
 *     <td>index: <code>0</code></td>
 *     <td>the last matched characters</td>
 *   </tr>
 *   <tr>
 *     <td>index: <code>1</code> thru <code>n</code></td>
 *     <td>The parenthesized substring matches, if any</td>
 *   </tr>
 * </table></div>
 * Note 1: this API is modeled after the JavaScript RegExp API, for the 
 * benefit of users already familiar with JavaScript.
 * <p>
 * Note 2: the properties (<code>index</code>, etc) work by bean-access,
 * so they are implemented by getter methods (since they are read-only)
 * 
 * @author Rob Clark (rob@ti.com)
 */
public abstract class RegExpResult extends OObject
{
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.RegExpResult");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "RegExpResult";
  public final static String[] MEMBER_NAMES   = new String[] {
                                                      "getType",
                                                      "castToJavaObject",
                                                      "castToString",
                                                      "bopInstanceOf",
                                                      "bopEquals",
                                                      "bopNotEquals",
                                                      "getMember",
                                                      "elementAt",
                                                      "length",
                                                      "getIndex",
                                                      "getInput",
                                                      "index",
                                                      "input"
                                                      // XXX
                                                    };
  
  /**
   * The input string, whose match results are contained in this object
   */
  private Value input;
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param input   the string to match the regular expression against
   */
  protected RegExpResult( Value input )
  {
    super();
    this.input = input;
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
   * Get the original input string.
   */
  public Value getInput()
  {
    return input;
  }
  
  /*=======================================================================*/
  /**
   * The index of the match in the string, or <code>-1</code> if no match.
   */
  public abstract Value getIndex();
  
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
  public abstract int length() throws PackagedScriptObjectException;
  
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
  public abstract Value elementAt( Value idx ) throws PackagedScriptObjectException;
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

