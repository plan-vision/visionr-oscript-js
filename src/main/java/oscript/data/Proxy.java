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
 */


package oscript.data;

import oscript.exceptions.*;

import java.util.*;


/**
 * A proxy object acts as a proxy, all attempts to resolve a member go
 * thru the <code>resolve</code> method which should be implemented by
 * the derived script class.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class Proxy extends OObject
{
  /**
   * The type object for an instance of Proxy.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.Proxy");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "Proxy";
  public final static String[] MEMBER_NAMES   = new String[] { "getTypeMember" };
  
  
  /**
   * Class Constructor.
   */
  public Proxy()
  {
    super();
  }
  
  /**
   * Class Constructor.  This is the constructor that gets called via an
   * BuiltinType instance.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public Proxy( oscript.util.MemberTable args )
  {
    this();
    
    if( args.length() != 0 )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
  }
  
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
  
  /**
   * Get a member of this type.  This uses the resolve() method implemented
   * by the derived script class to help resolve members.
   * 
   * @param obj          an object of this type
   * @param id           the id of the symbol that maps to the member
   * @return a reference to the member, or null
   */
  protected Value getTypeMember( Value obj, int id )
  {
    Value name = Symbol.getSymbol(id);
    Value val = null;
    
    Thread currentThread = Thread.currentThread();
    if( ! recursedSet.contains(currentThread) )
    {
      try
      {
        recursedSet.add(currentThread);
        Value resolve = obj.getMember( RESOLVE, false );
        if( resolve != null )
          val = resolve.callAsFunction( new Value[] { name } );
      }
      finally
      {
        recursedSet.remove(currentThread);
      }
    }
    
    if( val != null )
      return val;
    
    return super.getTypeMember( obj, id );
  }
  
  private Set recursedSet = new HashSet();
  
  private static final int RESOLVE = Symbol.getSymbol("resolve").getId();
  
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

