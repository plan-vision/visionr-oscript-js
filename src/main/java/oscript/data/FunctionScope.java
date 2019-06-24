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
 * The <code>FunctionScope</code> to implement the scope for a function.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class FunctionScope extends BasicScope
{
  public FunctionScope() {
	  super(null);
  }

  protected Function fxn;
  
  /**
   * Class Constructor.
   * 
   * @param fxn          the function
   * @param previous     the previous
   * @param smit         shared member idx table
   * @param members      the members table, containing function arguments
   */
  public FunctionScope( Function fxn, Scope previous, SymbolTable smit, MemberTable members )
  {
    super( previous, smit, members );
    this.fxn = fxn;
  }
  
  protected FunctionScope( Function fxn, Scope previous, SymbolTable smit )
  {
    super( previous, smit );
    this.fxn = fxn;
  }
  
  /**
   * Overridden to check for statics
   */
  protected Value getMemberImpl( int id )
  {
    Value val = super.getMemberImpl(id);
    
    if( val == null )
      val = fxn.getStaticMember(id);
    
    return val;
  }
  
  /**
   * Lookup the "super" within a scope.  Within a function body, "super"
   * is the overriden function (if there is one).
   * 
   * @return the "this" ScriptObject within this scope
   */
  public Value getSuper()
  {
    return new OSuper( this, fxn.getOverriden() );
  }
  
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
    if( fxn == val.unhand() )
      return new OThis(this);
    return super.getThis(val);
  }
  
  /**
   * Lookup the "callee" within a scope.  The "callee" is the first scope
   * chain node that is a function-scope, rather than a regular scope chain 
   * node.
   * 
   * @return the "callee" Function within callee scope
   */
  public Value getCallee()
  {
    return fxn;
  }
  
  /**
   * The special value, <code>this</code> doesn't have the same permissions
   * checking for the <code>getMember</code> method.
   * <p>
   * Because of how assignement to a Reference works, this will get
   * unhand()'d if it is assigned or passed as an argument, so the special
   * privilages cannot be passed to another function.
   */
  protected static class OThis extends AbstractReference
  {
    private Scope scope;
    private Scope scriptObject;
    
    OThis( Scope scope )
    {
      super();
      this.scope = scope;
      
      while( !(scope instanceof ScriptObject) )
        scope = scope.previous;
      scriptObject = scope;
      
      if( scriptObject == null )
        throw new RuntimeException("this shouldn't happen");
    }
    
    protected Value get()
    {
      return scriptObject;
    }
    
    public Value getMember( int id, boolean exception )
      throws PackagedScriptObjectException
    {
      // first check constructor scope:
      Value val = scope.getMemberImpl(id);
      
      // then script-object scope:
      if( val == null )
        val = scriptObject.getMemberImpl(id);
      
      if( (val == null) && exception )
        throw noSuchMember( Symbol.getSymbol(id).castToString() );
      
      return val;
    }
  }
  
  /**
   * Super object in most cases acts as the overriden function value, but
   * overrides {@link #getMember(int,boolean)} to allow to access the
   * other overriden function values, ie: <code>super.foo()</code>
   */
  protected static class OSuper extends AbstractReference
  {
    private Scope scope;
    private Value val;
    
    OSuper( Scope scope, Value val )
    {
      if( val == null )
        val = NULL;
      
      this.scope = scope;
      this.val   = val;
    }
    
    protected Value get()
    {
      return val;
    }
    
    
    public Value getMember( int id, boolean exception )
      throws PackagedScriptObjectException
    {
      try
      {
        Value member = scope.lookupInScope(id);
        member = member.unhand();
        if( member instanceof Function )
          return ((Function)member).getOverriden();
      }
      catch(PackagedScriptObjectException e)
      {
      }
      return val.getMember( id, exception );
    }
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

