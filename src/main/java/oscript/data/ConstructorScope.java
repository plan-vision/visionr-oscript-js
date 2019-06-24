/*=============================================================================
 *     Copyright Texas Instruments 2000.  All Rights Reserved.
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
 * The <code>ConstructorScope</code> to implement the scope for a 
 * constructor, acting as a switch to cause <code>private</code>
 * variables to be declared in a scope private to the constructor
 * while <code>public</code> and <code>protected</code> members
 * are in a scope shared by any parent and child classes.
 * 
 * @author Rob Clark (rob@ti.com)
 * @version 1.8
 */
public class ConstructorScope extends FunctionScope
{
  /**
   * Class Constructor.
   * 
   * @param fxn          the function
   * @param previous     the previous
   * @param smit         shared member idx table
   */
  ConstructorScope( Function fxn, Scope previous, oscript.util.SymbolTable smit )
  {
    super( fxn, previous, smit );
  }
  
  /**
   * Create a member of this object with the specified value.  This actually
   * creates the member in the previous scope.
   * 
   * @param id           the id of the symbol that maps to the member
   * @param attr         the attributes of the object (see <code>Reference</code>.)
   * @see Reference
   */
  public Value createMember( int id, int attr )
  {
    // if private, member is part of constructor scope (this):
    if( (attr & Reference.ATTR_PRIVATE) != 0 )
      return super.createMember( id, attr );
    
    // else it is a member of the object, as long as it doesn't
    // eclipse a member of the constructor scope:
    if( getMemberImpl(id) != null )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OException("\"" + Symbol.getSymbol(id) + "\" would eclipse local") );
    
    return previous.createMember( id, attr );
  }
  
  /**
   * "mixin" the specified variable into the current scope.
   * 
   * @param val          the value to mixin to this scope
   */
  public void mixin( Value val )
  {
    previous.mixin(val);
  }
  
  /**
   * Lookup the "this" within a scope.  The "this" is actually a union of
   * this scope which contains private members and args, and the script
   * object itself.
   * 
   * @return the "this" ScriptObject within this scope
   */
  public Value getThis()
  {
    return new OThis(this);
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

