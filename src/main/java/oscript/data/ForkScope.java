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
 * The <code>ForkScope</code> is used to implement a fork in the scope
 * chain.  This is needed because when evaluating a superFxn, or a
 * method defined in the superFxn, the scope chain should use the scope
 * that the superFxn is defined in, rather than the scope of the
 * derived function.  
 * <p>
 * This is basically just a simple wrapper for the <code>obj</code>, 
 * with the exception that lookupInScope uses <code>env</code> as the
 * previous instead.  This means that any member created in this scope 
 * is actually created in the <code>obj</code> scope.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class ForkScope extends Scope
{
  private Scope obj;
  private Scope env;
  
  
  // XXX for debugging, determine if this is a scope that is safe to hold
  //     a reference to after it is left
  public boolean isSafe()
  {
    if( !(obj.isSafe() && env.isSafe()) )
    {
      //System.err.println("  ->   " + this + " (" + getClass().getName() + ", " + findDesc(this) + ")");
      return false;
    }
    return true;
  }
  
  /*=======================================================================*/
  /**
   * 
   * @param obj          the object
   * @param env          the environment
   */
  ForkScope( Scope obj, Scope env )
  {
    this( simplify(obj), env, false );
  }
  
  private ForkScope( Scope obj, Scope env, boolean bogus )
  {
    super(obj);
    
    this.obj = obj;
    this.env = env;
  }
  
  // get rid of nested ForkScope, ie from multiple levels of extends...
  private static Scope simplify( Scope obj )
  {
    while( obj instanceof ForkScope )
      obj = ((ForkScope)obj).obj;
    
    return obj;
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
    return Type.HIDDEN_TYPE;
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
    obj = obj.getSafeCopy();
    env = env.getSafeCopy();
    return super.getSafeCopy();
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
    return obj.getSuper();
  }
  
  /*=======================================================================*/
  /**
   * Create a member of this object with the specified value.
   * 
   * @param id           the id of the symbol that maps to the member
   * @param attr         the attributes of the object (see <code>Reference</code>.)
   * @see Reference
   */
  public Value createMember( int id, int attr )
  {
    return obj.createMember( id, attr );
  }
  
  /*=======================================================================*/
  /**
   * "mixin" the specified variable into the current scope.
   * 
   * @param val          the value to mixin to this scope
   */
  public void mixin( Value val )
  {
    obj.mixin(val);
  }
  
  /*=======================================================================*/
  /**
   * Get a member of this object.
   * 
   * @param id           the id of the symbol that maps to the member
   * @param exception    whether an exception should be thrown if the
   *   member object is not resolved
   * @return a reference to the member
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value getMember( int id, boolean exception )
    throws PackagedScriptObjectException
  {
    return obj.getMember( id, exception );
  }
  
  // only for use by wrapper class (oscript.classwrap.ClassWrapGen)
  public Value __getInstanceMember( int id )
  {
    return obj.__getInstanceMember(id);
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
    Value val = obj.getMemberImpl(id);
    
    if( val == null )
      val = env.lookupInScope(id);    // use env instead of obj.previous
    
    if( val == null )
      throw noSuchMember( Symbol.getSymbol(id).castToString() );
    
    return val;
  }
  
  /*=======================================================================*/
  /**
   * Indicate that this scope is no longer needed
   */
  public void free()
  {
    System.err.println("probably shouldn't get here... tell rob");
    obj.free();
    env.free();
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


