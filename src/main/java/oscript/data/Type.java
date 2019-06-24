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
 * XXX not sure if we need a common parent for all types, but we might end up
 * with some common utility code.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public abstract class Type extends Value
{
  /**
   * The type of special objects, which shouldn't be accessible from
   * a oscript program.  For example, scope.
   */
  public final static Value HIDDEN_TYPE = OSpecial.makeSpecial("(hidden)");
  
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param type         the type of this object
   */
  public Type()
  {
    super();
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public Type( oscript.util.MemberTable args )
  {
    super();
    
    // XXX
    throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
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
    return this == type.unhand();
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

