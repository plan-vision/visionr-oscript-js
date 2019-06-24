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
import oscript.util.*;


/**
 * Scope is an internal object use to represent a scope of execution.  It is
 * used to implement nested scope, ie. if a lookup in this scope fails, the
 * lookup will procedue in the <code>previous</code> scope, and so on.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public abstract class Scope extends Value
{
  /**
   * Previous in object scope chain.  This is used to <code>lookupInScope</code>,
   * when the member being looked-up doesn't exist in this object (ie scope
   * chain node)
   */
  protected Scope previous;
  
  /*=======================================================================*/
  /**
   * Class Constructor.  Construct a element in the scope chain.  This
   * constructs a "function" element in the scope chain.  This is
   * called from the Function class when a function is evaluateded.
   * 
   * @param previous     previous in environment scope chain
   */
  protected Scope( Scope previous )
  {
    super();
    
    this.previous = previous;
  }
  
  /*=======================================================================*/
  /**
   * Get the previous scope in the scope chain.
   * 
   * @return the parent scope, or <code>null</code> if this is the global-
   * scope (ie. topmost scope in the scope chain)
   */
  public Scope getPreviousScope()
  {
    return previous;
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
    previous.__setJavaObject(javaObject);
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
    return previous.getSuper();
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
    return previous.getThis();
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
    return previous.getThis(val);
  }
  
  /*=======================================================================*/
  /**
   * Lookup the "callee" within a scope.  The "callee" is the first scope
   * chain node that is a function-scope, rather than a regular scope chain 
   * node.
   * 
   * @return the "callee" Function within callee scope
   */
  public Value getCallee()
  {
    return previous.getCallee();
  }
  
  /*=======================================================================*/
  /**
   * Create a member of this object with the specified value.  This method
   * is provided for convenience.
   * 
   * @param name         the name of the member
   * @param attr         the attributes of the object (see <code>Reference</code>.)
   * @see Reference
   */
  public final Value createMember( String name, int attr )
  {
    return createMember( Symbol.getSymbol(name).getId(), attr );
  }
  
  /*=======================================================================*/
  /**
   * Create a member of this object with the specified value.
   * 
   * @param name         the name of the member
   * @param attr         the attributes of the object (see <code>Reference</code>.)
   * @see Reference
   */
  public final Value createMember( Value name, int attr )
  {
    return createMember( Symbol.getSymbol(name).getId(), attr );
  }
  
  /*=======================================================================*/
  /**
   * Create a member of this object with the specified value.
   * 
   * @param id           the id of the symbol that maps to the member
   * @param attr         the attributes of the object (see <code>Reference</code>.)
   * @see Reference
   */
  public abstract Value createMember( int id, int attr );
  
  /*=======================================================================*/
  /**
   * "mixin" the specified variable into the current scope.  When a object
   * is mixed in, all of it's members are mixed in to the current scope. 
   * Members created within this scope will take precedence over a member
   * of a mixed in object.  The following script code example gives an idea
   * of how this works:
   * <pre>
   *   mixin java.lang.Math;  // defines "min", "max", etc
   *   var c = min( a, b );
   *   var d = max( a, b );
   * </pre>
   * Mixins can also be used to implement OO composition, so a constructor
   * function can mixin members of another object.  (Note that this will
   * not make the resulting object an instanceof this mixed in object's
   * type... this could be made to work at some point in the future for
   * script code, but not for java code, if that was deemed a good thing..)
   * <pre>
   *   function ListAndStuff()
   *   {
   *     public function foo() { ... }
   *     var list = new java.util.LinkedList();
   *     mixin list;
   *   }
   *   var obj = new ListAndStuff();
   *   obj.foo();
   *   obj.add(1);
   *   obj.add("two");
   *   for( var o : obj )
   *     writeln("list member: " + o);
   * </pre>
   * 
   * @param val          the value to mixin to this scope
   */
  public abstract void mixin( Value val );
  
  /*=======================================================================*/
  // XXX hack
  protected Value getMemberImpl( int id )
  {
    return null;
  }
  protected Value getInstanceMemberImpl( int id )
  {
    return null;
  }
  
  // only for use by wrapper class (oscript.classwrap.ClassWrapGen)
  public abstract Value __getInstanceMember( int id );
  
  /*=======================================================================*/
  /**
   * Get a member from this scope.  This is used to access local variables
   * and object attributes from methods of the object.  If the attribute
   * isn't in this node in the scope chain, then the <code>previous</code>
   * node in the scope chain is checked.  This method is provided for
   * convenience.
   * 
   * @param name         the name of the member
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public final Value lookupInScope( String name )
    throws PackagedScriptObjectException
  {
    return lookupInScope( Symbol.getSymbol(name).getId() );
  }
  
  /*=======================================================================*/
  /**
   * Get a member from this scope.  This is used to access local variables
   * and object attributes from methods of the object.  If the attribute
   * isn't in this node in the scope chain, then the <code>previous</code>
   * node in the scope chain is checked.
   * 
   * @param name         the name of the member
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public final Value lookupInScope( Value name )
    throws PackagedScriptObjectException
  {
    return lookupInScope( Symbol.getSymbol(name).getId() );
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
  public abstract Value lookupInScope( int id )
    throws PackagedScriptObjectException;
  
  /*=======================================================================*/
  /**
   * In case a scope has any resource allocated from a source which will
   * no long be valid after a stack frame has returned (ie. resource 
   * allocated from stack), return a copy of the scope that is safe to
   * keep after the stack frame returns.
   */
  public Scope getSafeCopy()
  {
    previous = previous.getSafeCopy();
    return this;
  }
  
  /*=======================================================================*/
  // XXX for debugging, determine if this is a scope that is safe to hold
  //     a reference to after it is left
  public abstract boolean isSafe();
  
  /*=======================================================================*/
  /**
   * Indicate that this scope is no longer needed.  This should only be called
   * in cases of scopes allocated from the stack.
   */
  public abstract void free();
  
  /*=======================================================================*/
  protected final static String findDesc( Scope scope )
  {
    for( java.util.Iterator itr=StackFrame.currentStackFrame().iterator(); itr.hasNext(); )
    {
      StackFrame sf=(StackFrame)(itr.next());
      if( sf.getScope() == scope )
        return sf.toString();
    }
    return "??unknown??";
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

