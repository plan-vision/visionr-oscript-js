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
 * Each interpreter instance has a single global scope, which serves to
 * terminate the scope chain.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class GlobalScope extends BasicScope
{
  public static final Value TYPE = OSpecial.makeSpecial("(global)");
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   */
  public GlobalScope()
  {
    super(null);
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
   * In case a scope has any resource allocated from a source which will
   * no long be valid after a stack frame has returned (ie. resource 
   * allocated from stack), return a copy of the scope that is safe to
   * keep after the stack frame returns.
   */
  public Scope getSafeCopy()
  {
    return this;
  }
  
  /*=======================================================================*/
  /**
   * Lookup the "super" within a scope.  Within a function body, "super"
   * is the overriden function (if there is one).
   * 
   * @return the "this" ScriptObject within this scope
   */
  public Value getSuper()
  {
    throw PackagedScriptObjectException.makeExceptionWrapper( new OException("no super!") );
  }
  
  /*=======================================================================*/
  /**
   * Lookup the "this" within a scope.  The "this" is the first scope chain
   * node that is an object, rather than a regular scope chain node.
   * 
   * @return the "this" ScriptObject within this scope
   */
  public Value getThis()
  {
    throw PackagedScriptObjectException.makeExceptionWrapper( new OException("no this!") );
  }
  
  /*=======================================================================*/
  /**
   * Lookup the qualified "this" within a scope.  The qualified "this" is 
   * the first scope chain node that is an object and an instance of the
   * specified type, rather than a regular scope chain node.
   * 
   * @param val   the type that the "this" qualifies
   * @return the qualified "this" ScriptObject within this scope
   */
  public Value getThis( Value val )
  {
    return getThis();
  }
  
  /*=======================================================================*/
  /**
   * Get a member from this scope.  This is used to access local variables
   * and object attributes from methods of the object.  If the attribute
   * isn't in this node in the scope chain, then the <code>previous</code>
   * node in the scope chain is checked.
   * 
   * @param id           the id of the symbol that maps to the member
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value lookupInScope( int id )
    throws PackagedScriptObjectException
  {
    Value val = getMemberImpl(id);
    
    if( val == null )
      throw noSuchMember( Symbol.getSymbol(id).castToString() );
    
    return val;
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

