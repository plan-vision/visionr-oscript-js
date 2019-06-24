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

import java.util.*;

import oscript.exceptions.*;
import oscript.util.*;



/**
 * Scope is an internal object use to represent a scope of execution.  This
 * class implements a basic scope which can have members created and accessed
 * via <code>lookupInScope</code> or <code>getMember</code>.
 * <p>
 * Different instances of a scope that represent the same part of the syntax
 * tree can share the hashtable that maps member name to member index.  This
 * reduces the number of hashtables that need to be created as scopes are
 * created, to improve performance.  The member index is an index into each
 * scope instances array of members.
 * <p>
 * In cases where it is safe to allocate storage for this scope from the
 * stack, the current {@link StackFrame} may be passed to the constructor,
 * in which case this object will try and allocate it's member storage from
 * the stack.  It is only safe to use the stack for storage allocation in
 * cases where this scope does not enclose a function instance, so if the
 * scope does enclose a function declaration, <code>null</code> must be
 * passed in to the constructor instead of the current stack frame.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class BasicScope extends Scope
{
  /**
   * The shared member index table maps member name to an index into the
   * <code>members</code> array.
   */
  protected SymbolTable smit;
  
  /**
   * The table of members of the scope.  This is unique to each scope
   * instance.
   */
  protected MemberTable members;
  
  /**
   * List of mixed in vars, or <code>null</code> if none.
   */
  protected Value[] mixins = null;
  
  /*=======================================================================*/
  /*=======================================================================*/
  /*=======================================================================*/
  
  /**
   * Class Constructor.  Construct a new element in the scope chain.  This
   * constructs a "regular" element in the scope chain, as opposed to the
   * element that is created when a function is called.
   * 
   * @param previous     previous in environment scope chain
   */
  public BasicScope( Scope previous )
  {
    this( 
      previous, 
      new OpenHashSymbolTable( 3, 0.67f )
    );
  }
  
  
  /**
   * Class Constructor.  Construct a new element in the scope chain.  This
   * constructs a "regular" element in the scope chain, as opposed to the
   * element that is created when a function is called.
   * 
   * @param previous     previous in environment scope chain
   * @param smit         shared member idx table
   */
  public BasicScope( Scope previous, SymbolTable smit )
  {
    this( 
      previous, 
      smit, 
      new OArray( smit.size() )
    );
  }
  
  /**
   * Class Constructor.
   * 
   * @param previous   previous scope
   * @param smit       shared-member-index-table
   * @param members    members, can be used by function scope to directly
   *    map arguments to the function into the function's scope
   */
  protected BasicScope( Scope previous, SymbolTable smit, MemberTable members )
  {
    super(previous);
    
//    if( smit == null )
//    {
//System.err.println("yes, SMIT can be null"); // XXX
//      smit = new OpenHashSymbolTable( 3, 0.67f );
//    }
    
    this.smit  = smit;
    this.members = members;
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
    members = members.safeCopy();
    return super.getSafeCopy();
  }
  
  // XXX for debugging, determine if this is a scope that is safe to hold
  //     a reference to after it is exited
  public boolean isSafe()
  {
//    StringBuffer sb = new StringBuffer();
//    sb.append(this);
//    sb.append(": ");
//    sb.append( getClass().getName() );
//    sb.append(", ");
//    sb.append( findDesc(this) );
//    sb.append(", ");
//    
//    Set memberSet = new HashSet();
//    populateMemberSet( memberSet, true );
//    sb.append(memberSet);
//    
//    if( members == StackFrame.currentStackFrame().members )
//    {
//      System.err.println("scope not safe: " + sb);
//      return false;
//    }
//    else if( (previous != null) && !previous.isSafe() )
//    {
//      System.err.println("  ->   " + sb);
//      return false;
//    }
    return true;
  }
  
  /*=======================================================================*/
  /*=======================================================================*/
  /*=======================================================================*/
  
  protected Value getMemberImpl( int id )
  {
    Value val = getInstanceMemberImpl(id);
    
    if( val == null )
      val = getType().getTypeMember( this, id );
    
    if( val == null && (mixins != null) )
      for( int i=0; (i<mixins.length) && (val==null); i++ )
        val = mixins[i].getMember( id, false );
    
    return val;
  }
  
  // only for use by wrapper class (oscript.classwrap.ClassWrapGen)
  public Value __getInstanceMember( int id )
  {
    return getInstanceMemberImpl(id);
  }
  
  protected Value getInstanceMemberImpl( int id )
  {
    int idx = smit.get(id);
    
    if( (idx < 0) || (idx >= members.length()) )
      return null;
    
    Reference ref = members.referenceAt(idx);
    if( (ref == null) || (ref.getAttr() == Reference.ATTR_INVALID) )
      return null;
    
    return ref;
  }
  
  /**
   * Reset this scope object.  When program execution has left this scope
   * block, it must be reset to ensure that any reference to it's members
   * be freed.
   */
  public final void reset()
  {
    members.reset();
    mixins = null;
  }
  
  /*=======================================================================*/
  /*=======================================================================*/
  /*=======================================================================*/
  
  /**
   * Get the type of this object.  This is overloaded so that mixed in
   * objects have an appropriate effect on <code>instanceof</code>
   * 
   * @return the object's type
   */
  public Value getType()
  {
    final Value type = super.getType();
    
    if( mixins == null )
    {
      return type;
    }
    else
    {
      return new AbstractReference() {
          
          public boolean isA( Value type )
          {
            if( get().isA(type) )
              return true;
            
            for( int i=0; i<mixins.length; i++ )
              if( mixins[i].getType().isA(type) )
                return true;
            
            return false;
          }
          
          protected Value get()
          {
            return type;
          }
          
        };
    }
  }
  
  /**
   * Create a member of this object with the specified value.
   * <p>
   * Note that the theory behind not synchronizing this is that it can only
   * be a race condition against itself, not against getMember, etc, and that
   * this will only be called from a single thread context.
   * 
   * @param id           the id of the symbol that maps to the member
   * @param attr         the attributes of the object (see <code>Reference</code>.)
   * @see Reference
   */
  public Value createMember( int id, int attr )
  {
    int idx = smit.create(id);
    members.ensureCapacity(idx);
    Reference ref = members.referenceAt(idx);
    ref.reset(attr);
    return ref;
  }
  
  /**
   * "mixin" the specified variable into the current scope.
   * 
   * @param val          the value to mixin to this scope
   */
  public void mixin( Value val )
  {
    int oldLength = (this.mixins == null) ? 0 : this.mixins.length;
    Value[] mixins = new Value[ oldLength + 1 ];
    
    if( this.mixins != null )
      System.arraycopy( this.mixins, 0, mixins, 0, oldLength );
    
    // NOTE: important to do this before overwriting this.mixins or
    //    there will be a race condition
    mixins[oldLength] = val;
    
    this.mixins = mixins;
  }
  
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
    Value val = getMemberImpl(id);
    
    if( val == null )
      return super.getMember( id, exception );
    
    if( (val instanceof Reference) && !((Reference)val).isPublic() )
    {
      if(exception)
        throw PackagedScriptObjectException.makeExceptionWrapper( new OException(getType().castToString() + ": " + Symbol.getSymbol(id) + " is not public") );
      else
        val = null;
    }
    
    return val;
  }
  
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
      val = previous.lookupInScope(id);
    
    return val;
  }
  
  /**
   * Indicate that this scope is no longer needed
   */
  public void free()
  {
    // XXX currently members are freed elsewhere... should investigate
    // consolodating freeing members and scope somehow
  }
  
  /*=======================================================================*/
  /*=======================================================================*/
  /*=======================================================================*/
  
//    protected String convertToString()
//    {
//      throw new ProgrammingErrorException("unimplemented");
//  //      String str = "";
    
//  //      if( memberTable != null )
//  //      {
//  //        for( Enumeration e = memberTable.keys();
//  //             e.hasMoreElements(); )
//  //        {
//  //          Object key = e.nextElement();
//  //          str += "(" + ((Value)key).castToString() + ")";
//  //        }
//  //      }
    
//  //      return str;
//    }
  
  /*=======================================================================*/
  /*=======================================================================*/
  /*=======================================================================*/
  
  
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
  protected void populateMemberSet( Set s, boolean _dbg )
  {
    if( members == null )
      return;
    for( Iterator itr=smit.symbols(); itr.hasNext(); )
    {
      int id  = ((Integer)(itr.next())).intValue();
      int idx = smit.get(id);
      
      if( idx < members.length() )
      {
        Reference ref = members.referenceAt(idx);
        if( (ref.getAttr() != Reference.ATTR_INVALID) && 
            (_dbg || ref.isPublic()) )
          s.add( Symbol.getSymbol(id) );
      }
    }
    
    Value[] mixins = this.mixins;
    if( mixins != null )
      for( int i=0; i<mixins.length; i++ )
        mixins[i].populateMemberSet( s, _dbg );
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

