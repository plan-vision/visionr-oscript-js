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


import java.io.*;
import java.util.*;

import oscript.syntaxtree.FunctionPrimaryPrefix;
import oscript.util.StackFrame;
import oscript.util.MemberTable;
import oscript.util.SymbolTable;
import oscript.exceptions.*;
import oscript.NodeEvaluator;


/**
 * A script function/constructor.  Since native (and other) objects that
 * behave as a function can re-use some functionality (ie checking number
 * of args, type casting, etc., the stuff specific to a script function/
 * constructor is pushed out into a seperate class.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class Function extends Type
{
  /**
   * The type object for an script function.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.Function");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "Function";
  public final static String[] MEMBER_NAMES   = new String[] {
    "getName",
    "getComment",
    "getMinimumArgCount",
    "takesVarArgs",
    "getArgNames",
    "isA",
    "castToString",
    "callAsFunction",
    "callAsConstructor",
    "callAsExtends"
  };
  
  /**
   * The scope this function is defined in.  This does not change throughout
   * the life of the function.
   */
  public Scope enclosingScope;
  
  /**
   * The scope the static members of this function are defined in.  When
   * the function is constructed, if there are any static members, they
   * are evaluated within this scope.  Otherwise this is null.
   */
  private final Scope staticScope;
    
  /**
   * The function this function extends, if any.
   */
  private final Value superFxn;
  
  /**
   * The shared function data... parameters that are shared by all instances
   * of the same function.
   * <p>
   * public for {@link StackFrame#evalNode}
   */
  public final FunctionData fd;
  
  /**
   * If this function overrides a value, this is the previous value.
   */
  public Value overriden;
  
  /**
   * In order to keep function instances more lightweight, the values that
   * will be the same for any instance of a function representing the same
   * portion of the parse tree have been split out into this class, in order
   * to be shared between different function instances.
   */
  public static final class FunctionData
    implements Externalizable
  {
    /* 
     * XXX   all these would be final if it weren't for needing the no-arg
     *       constructor for serialization
     */
    
    /**
     * The node-evaluator for evaluating the body of this function.
     * <p>
     * only public for {@link StackFrame}
     */
    public NodeEvaluator program;
    
    /**
     * The node-evaluator for evaluating the static body of this function.
     */
    NodeEvaluator sprogram;
    
    /**
     * The expressions to evaluate to determine args to superFxn.
     */
    NodeEvaluator exprList;
    
    /**
     * The id of this function.
     */
    int   id;
    
    /**
     * The ids of the arguments and there permissions.  The n'th parameter
     * to the function has its <code>id</code> specified by the 2*n element
     * in the array, and <code>attr</code> specified by the 2*n+1 element in
     * the array.
     */
    int[] argIds;
    
    /**
     * The number of args (if not vararg fxn), or minimum number of args (if
     * vararg fxn).
     */
    public int nargs;
    
    /**
     * Is this a var-args function.  If it is, then the zero or more remaining
     * args to the function are copied into an array that is assigned to the
     * last arg to the function.
     */
    boolean varargs;
    
    /**
     * A hint from the parser about whether we need to create an extra level
     * of scope (ie. there are variables declared in that scope)
     */
    boolean hasVarInScope;
    
    /**
     * A hint from the parser about whether scope storage can be allocated from
     * the stack, which can only be done if there are no functions declared
     * within this function
     * <p>
     * only public for {@link StackFrame}
     */
    public boolean hasFxnInScope;
    
    /**
     * Comment generated from javadoc comment block in src file.
     */
    Value comment;
    
    /**
     * Class Constructor.
     * 
     * @param id           the id of the symbol that maps to the member, ie. it's name
     * @param argIds       array of argument ids and attributes
     * @param varargs      is this a function that can take a variable number of args?
     * @param exprList     expressions to evaluate to get args to <code>superFxn</code> 
     *    or <code>null</code> if <code>superFxn</code> is <code>null</code>
     * @param program      the body of the function
     * @param sprogram     the static body of the function, or <code>null</code>
     * @param hasVarInScope whether one or more vars/functions are declared in the
     *    function body's scope... this is a hint from the parser to tell us if we
     *    can avoid creating a scope object at runtime
     * @param hasFxnInScope whether one or more functions are enclosed by this function
     *    body's scope... this is a hint from the parser to tell us if we can allocate
     *    scope storage from the stack
     * @param comment      html formatted comment generated from javadoc 
     *    comment in src file, or <code>null</code>
     */
    public FunctionData( int       id,
                         int[]     argIds,
                         boolean   varargs,
                         NodeEvaluator exprList,
                         NodeEvaluator program,
                         NodeEvaluator sprogram,
                         boolean   hasVarInScope,
                         boolean   hasFxnInScope,
                         Value     comment )
    {
      this.id       = id;
      this.argIds   = argIds;
      this.varargs  = varargs;
      this.exprList = exprList;
      this.program  = program;
      this.sprogram = sprogram;
      this.hasVarInScope = hasVarInScope;
      this.hasFxnInScope = hasFxnInScope;
      this.comment  = comment;
      
      if(varargs)
        nargs = (argIds.length/2) - 1;
      else
        nargs = (argIds.length/2);
    }
    
    /* 
     * Externalizable Support:
     */
    
    public FunctionData() {}
    
    /**
     * Derived class that implements {@link java.io.Externalizable} must
     * call this if it overrides it.  It should override it to save/restore
     * it's own state.
     */
    public void readExternal( ObjectInput _in )
      throws IOException, ClassNotFoundException
    {
      program       = (NodeEvaluator)(_in.readObject());
      sprogram      = (NodeEvaluator)(_in.readObject());
      exprList      = (NodeEvaluator)(_in.readObject());
      id            = _in.readInt();
      argIds        = new int[ _in.readInt() ];
      for( int i=0; i<argIds.length; i++ )
        argIds[i]   = _in.readInt();
      nargs         = _in.readInt();
      varargs       = _in.readBoolean();
      hasVarInScope = _in.readBoolean();
      hasFxnInScope = _in.readBoolean();
      if( _in.readByte() == 1 )
      {
        comment = new OString();
        comment.readExternal(_in);
      }
    }
    
    /**
     * Derived class that implements {@link Externalizable} must
     * call this if it overrides it.  It should override it to save/restore
     * it's own state.
     */
    public void writeExternal( ObjectOutput out )
      throws IOException
    {
      out.writeObject(program);
      out.writeObject(sprogram);
      out.writeObject(exprList);
      out.writeInt(id);
      out.writeInt( argIds.length );
      for( int i=0; i<argIds.length; i++ )
        out.writeInt( argIds[i] );
      out.writeInt(nargs);
      out.writeBoolean(varargs);
      out.writeBoolean(hasVarInScope);
      out.writeBoolean(hasFxnInScope);
      if( comment != null  )
      {
        out.writeByte(1);
        comment.writeExternal(out);
      }
      else
      {
        out.writeByte(0);
      }
    }
    
    /**
     * Map arguments to a function into the member-table which is used for
     * a function scope.  Since the compiler ensures that the function
     * parameters map to idx 0 thru n in the function-scope, all this has
     * to do is collapse the var-arg parameter (if present) into a single
     * array, and if there is a function within this function's body copy
     * into new table..  This also ensures that the correct number of parameters is
     * passed to the function.  This is used instead of {@link #addArgs} 
     * when calling as a function.
     * <p>
     * XXX this could be used in case of constructor scope, by stripping
     * out private parameters... maybe
     * 
     * @param args   the input arguments
     * @return
     */
    public static final OArray __empty = new OArray();
    
    public final MemberTable mapArgs( MemberTable args )
    {
      if(hasFxnInScope)
        args = args.safeCopy();
      int alen = args.length();
      int diff1 = alen - nargs;
      if(diff1 == 0 || (varargs && (alen >= nargs)))
      {
        if(varargs)
        {
          OArray arr = diff1 == 0 ? __empty : new OArray( diff1 );
          for( int i=nargs; i<alen; i++ )
        	  arr.elementAt(i-nargs).opAssign( args.referenceAt(i) );          
          args.ensureCapacity(nargs);
          args.referenceAt(nargs).reset(arr);
        }
        return args;
      }
      else
      {
        throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
      }
    }

    /**
     * A helper to populate a fxn-scope with args
     */
    public final void addArgs( FunctionScope fxnScope, MemberTable args )
    {
      int len = (args == null) ? 0 : args.length();
      
      if( (len == nargs) || (varargs && (len >= nargs)) )
      {
        for( int i=0; i<nargs; i++ )
        {
          int id   = argIds[2*i];
          int attr = argIds[2*i+1];
          
          fxnScope.createMember( id, attr ).opAssign(args.referenceAt(i));
        }
        
        if(varargs)
        {
          int id   = argIds[2*nargs];
          int attr = argIds[2*nargs+1];
          
          // XXX in theory, it should be possible to bring back an optimization
          // to avoid the copy, if nargs==0....
          
          OArray arr = new OArray( len - nargs );
          for( int i=nargs; i<len; i++ )
            arr.elementAt(i-nargs).opAssign( args.referenceAt(i) );
          
          fxnScope.createMember( id, attr ).opAssign(arr);
        }
      }
      else
      {
        throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
      }
    }
    
    public Value getName()
    {
      return Symbol.getSymbol(id);
    }
  }
  
  
  /*=======================================================================*/
  /**
   * Class Constructor.  Construct an anonymous function.
   * 
   * @param enclosingScope the context the function was declared in
   * @param superFxn     the function this function extends, or 
   *    <code>null</code>
   * @param fd           the shared function data, for all instances 
   *    of this function
   * 
   */
  public Function( Scope enclosingScope,
                   Value superFxn,
                   FunctionData fd )
  {
    super();
    
    // every script type implicitly inherits from <i>Object</i>
    if( superFxn == null )
      superFxn = OObject.TYPE;
    
    // if this function is overriding a function in an object scope, keep a
    // reference to the overriden function:
    if( (fd.id != FunctionPrimaryPrefix.ANON_FXN_ID) && (enclosingScope instanceof ConstructorScope) )
    {
      Scope scope = enclosingScope.getPreviousScope();
      while( !(scope instanceof ScriptObject) )
        scope = scope.getPreviousScope();
      overriden = scope.getMemberImpl(fd.id);
      if( overriden != null )
        overriden = overriden.unhand();
    }
    
// XXX seems to cause apple's VM to bus-error... not sure if it causes 
// problems on other platforms or not. --RDC
//    if(DEBUG)
//      if( !enclosingScope.isSafe() )
//        StackFrame.dumpStack(System.err);
    
    this.enclosingScope = enclosingScope;
    this.superFxn  = superFxn;
    this.fd   = fd;
    
    if( fd.sprogram != null )
    {
      staticScope = new BasicScope(enclosingScope);
      StackFrame.currentStackFrame().evalNode( fd.sprogram, staticScope );
    }
    else
    {
      staticScope = null;
    }
  }
  
  /*=======================================================================*/
  /**
   * Get the function that this function extends, or <code>null</code> if
   * none.
   */
  Value getSuper() { return superFxn; }
  
  /*=======================================================================*/
  /**
   * If this function overrides a value, this method returns it.  Otherwise
   * it returns <code>null</code>.
   */
  Value getOverriden() { return overriden; }
  
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
   * Get the name of this function.  An anonymous function will have the
   * name "anon".
   * 
   * @return the function's name
   */
  public Value getName()
  {
    return fd.getName();
  }
  
  /*=======================================================================*/
  /**
   * Get the comment block.  If there was a javadoc comment block preceding
   * the definition of this function in the src file, it can be accessed
   * with this method.
   * 
   * @return the function's comment, or <code>null</code>
   */
  public Value getComment()
  {
    return fd.comment;
  }
  
  /*=======================================================================*/
  /**
   * Get the minimum number of args that should be passed to this function.
   * If {@link #isVarArgs} returns <code>true</code>, then it is possible
   * to pass more arguments to this function, otherwise, you should pass
   * exactly this number of args to the function.
   * 
   * @return minimum number of args to pass when calling this function
   * @see #isVarArgs
   * @see #getArgNames
   */
  public int getMinimumArgCount()
  {
    return fd.nargs;
  }
  
  /*=======================================================================*/
  /**
   * Can this function be called with a variable number of arguments?
   * @see #getMinimumArgCount
   * @see #getArgNames
   */
  public boolean takesVarArgs()
  {
    return fd.varargs;
  }
  
  /*=======================================================================*/
  /**
   * Get the names of the arguments to the function, in order.  If this 
   * function takes a variable number of arguments, the last name in the 
   * array is the "var-arg" variable, to which the array of all remaining
   * arguments are bound.
   */
  public Value[] getArgNames()
  {
    Value[] names = new Value[ fd.argIds.length / 2 ];
    for( int i=0; i<names.length; i++ )
      names[i] = Symbol.getSymbol( fd.argIds[2*i] );
    return names;
  }
  
  /* Note:  arg-permissions are not made visible, because that seems
   *        to me like an implementation detail, whereas the arg-names
   *        is (sort of) part of the interface
   */
  
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
    return super.isA(type) || this.superFxn.isA(type);
  }
  
  private static final int BOPCAST = Symbol.getSymbol("_bopCast").getId();
  
  /*=======================================================================*/
  /**
   * Perform the cast operation, <code>(a)b</code> is equivalent to <code>a.bopCast(b)</code>
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Value bopCast( Value val )
    throws PackagedScriptObjectException
  {
    Value bopCast = getMember( BOPCAST, false );
    if( bopCast != null )
      return bopCast.callAsFunction( new Value[] { val } );
    return super.bopCast(val);
  }
  
  // bopCastR would be an instance member, not static (class) member
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>String</code> value.
   * 
   * @return a String value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public String castToString()
    throws PackagedScriptObjectException
  {
    return "[function: " + getName() + "]";
  }
  
  /*=======================================================================*/
  /**
   * Call this object as a function.
   * 
   * @param sf           the current stack frame
   * @param args         the arguments to the function, or <code>null</code> if none
   * @return the value returned by the function
   * @throws PackagedScriptObjectException
   * @see Function
   */
  public Value callAsFunction( StackFrame sf, MemberTable args )
    throws PackagedScriptObjectException
  {
    if( superFxn != OObject.TYPE )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException(getName() + ": cannot call as function!") );
    
    if( !fd.hasVarInScope && (fd.argIds.length == 0) && (args == null) )
    {
      return (Value)(sf.evalNode( fd.program, enclosingScope ));
    }
    else
    {
      Scope scope;
      SymbolTable smit = fd.program.getSharedMemberIndexTable(NodeEvaluator.ALL);
      if( args == null )
        args = fd.hasFxnInScope ? new OArray(0) : sf.allocateMemberTable(0);
      args = fd.mapArgs(args);  // even if args length is zero, to deal with var-args
      if( !fd.hasFxnInScope )
        scope = sf.allocateFunctionScope( this, enclosingScope, smit, args );
      else
        scope = new FunctionScope( this, enclosingScope, smit, args );
      try {
        return (Value)(sf.evalNode( fd.program, scope ));
      } finally {
        scope.free();
      }
    }
  }
  
  /*=======================================================================*/
  /**
   * Call this object as a constructor.
   * 
   * @param sf           the current stack frame
   * @param args         the arguments to the function, or <code>null</code> if none
   * @return the newly constructed object
   * @throws PackagedScriptObjectException
   * @see Function
   */
  public Value callAsConstructor( StackFrame sf, MemberTable args )
    throws PackagedScriptObjectException
  {
    /* XXX we should only need to create ConstructorScope if the number of
     * args is greater than zero, and hasVarInScope
     */
    ScriptObject newThisScope = new ScriptObject( 
      this, enclosingScope, fd.program.getSharedMemberIndexTable(NodeEvaluator.PUBPROT)
    );
    ConstructorScope fxnScope = new ConstructorScope( 
      this, newThisScope, fd.program.getSharedMemberIndexTable(NodeEvaluator.PRIVATE)
    );
    
    fd.addArgs( fxnScope, args );
    
    MemberTable superFxnArgs;
    if( fd.exprList != null )
      superFxnArgs = (MemberTable)(sf.evalNode( fd.exprList, fxnScope ));
    else
      superFxnArgs = new OArray(0);
    
    superFxn.callAsExtends( sf, newThisScope, superFxnArgs );
    
    sf.evalNode( fd.program, fxnScope );
    
    return newThisScope;
  }
  
  /*=======================================================================*/
  /**
   * Call this object as a parent class constructor.
   * 
   * @param sf           the current stack frame
   * @param scope        the object
   * @param args         the arguments to the function, or <code>null</code> if none
   * @return the value returned by the function
   * @throws PackagedScriptObjectException
   * @see Function
   */
  public Value callAsExtends( StackFrame sf, Scope scope, MemberTable args )
    throws PackagedScriptObjectException
  {
    /* XXX we should only need to create ConstructorScope if the number of
     * args is greater than zero, and hasVarInScope
     */
    
    scope = new ForkScope( scope, enclosingScope );
    ConstructorScope fxnScope = new ConstructorScope( 
      this, scope, fd.program.getSharedMemberIndexTable(NodeEvaluator.PRIVATE)
    );
    
    fd.addArgs( fxnScope, args );
    
    MemberTable superFxnArgs;
    if( fd.exprList != null )
      superFxnArgs = (MemberTable)(sf.evalNode( fd.exprList, fxnScope ));
    else
      superFxnArgs = new OArray(0);
    
    superFxn.callAsExtends( sf, scope, superFxnArgs );
    
    return (Value)(sf.evalNode( fd.program, fxnScope ));
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
    Value val = getStaticMember(id);
    
    if( val == null )
      val = super.getMember( id, exception );
    
    return val;
  }
  
  /*=======================================================================*/
  /**
   * Get a member of this type.  This is used to interface to the java
   * method of having members be attributes of a type.  Regular object-
   * script object's members are attributes of the object, but in the
   * case of java types (including built-in types), the members are
   * attributes of the type.
   * 
   * @param obj          an object of this type
   * @param id           the id of the symbol that maps to the member
   * @return a reference to the member, or null
   */
  protected Value getTypeMember( Value obj, int id )
  {
    Value val = superFxn.getTypeMember( obj, id );
    
    if( val == null )
      val = getStaticMember(id);
    
    return val;
  }
  
  /*=======================================================================*/
  /**
   * Get a static member of this function object.
   */
  final Value getStaticMember( int id )
  {
    Value val = null;
    
    if( staticScope != null )
      val = staticScope.getMember( id, false );
    
    return val;
  }
  
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
  protected void populateMemberSet( Set s, boolean dbg )
  {
    if( staticScope != null )
      staticScope.populateMemberSet( s, dbg );
  }
  
  /*=======================================================================*/
  /**
   * Derived classes that implement {@link #getTypeMember} should also
   * implement this.
   * 
   * @param s   the set to populate
   * @param debugger  <code>true</code> if being used by debugger, in
   *   which case both public and private/protected field names should 
   *   be returned
   * @see #getTypeMember
   */
  protected void populateTypeMemberSet( Set s, boolean dbg )
  {
    if( superFxn != null )
      superFxn.populateTypeMemberSet( s, dbg );
  }
  
  /*=======================================================================*/
  /**
   */
  public static Value extractJavadocComment( Vector specials, Value name, int[] argIds )
  {
    Value[] argNames = new Value[ argIds.length/2 ];
    for( int i=0; i<argNames.length; i++ )
      argNames[i] = Symbol.getSymbol( argIds[2*i] );
    
    StringBuffer sb = new StringBuffer();
    
    for( int i=0; i<specials.size(); i++ )
      sb.append( ((oscript.syntaxtree.NodeToken)(specials.elementAt(i))).toString() );
    
    return extractJavadocComment( sb.toString(), name, argNames );
  }
  public static Value extractJavadocComment( String str, Value name, Value[] argNames )
  {
    int idx = str.lastIndexOf("/**");  /**/
    
    if( idx == -1 )
      return null;
    
    str = str.substring( idx + "/**".length() ); /**/
    
    StringBuffer sb = new StringBuffer();
    
    sb.append("<html><head><title>" + name + "</title></head><body>");
    extractJavadocCommentBodyImpl( sb, str, name, argNames );
    sb.append("</body></html>");
    
    return new OString( sb.toString() );
  }
  public static Value extractJavadocCommentBody( String str, Value name, Value[] argNames )
  {
    int idx = str.lastIndexOf("/**");  /**/
    
    if( idx == -1 )
      return null;
    
    str = str.substring( idx + "/**".length() ); /**/
    
    StringBuffer sb = new StringBuffer();
    
    extractJavadocCommentBodyImpl( sb, str, name, argNames );
    
    return new OString( sb.toString() );
  }    
  private static String extractJavadocCommentBodyImpl( StringBuffer sb, String str, Value name, Value[] argNames )
  {
    BufferedReader br = new BufferedReader( new StringReader(str) );
    
    LinkedList paramList  = new LinkedList();
    LinkedList throwsList = new LinkedList();
    LinkedList returnList = new LinkedList(); // shouldn't have more than one!
    
    sb.append("<pre>function <b>" + name + "</b>(");
    for( int i=0; i<argNames.length; i++ )
      sb.append( ((i==0)?"":", ") + argNames[i]);
//     if(fd.varargs)
//       sb.append("...");
    sb.append(")</pre>");
    
    try
    {
      while( (str=strip(br.readLine())) != null )
      {
        // check for @XXX parameters:
        if( str.startsWith("@") )
          break;
        else
          sb.append(str + " ");
      }
      
      // handle @XXX paramters:
      while( str != null )
      {
        LinkedList list = null;
        String match = null;
        
        if( str.startsWith("@param") )
        {
          list  = paramList;
          match = "@param";
        }
        else if( str.startsWith("@throws") )
        {
          list  = throwsList;
          match = "@throws";
        }
        else if( str.startsWith("@return") )
        {
          list  = returnList;
          match = "@return";
        }
        // else ignore...
        
        if( list != null )
        {
          str = str.substring( match.length() ).trim();
          String val = str;
          
          // check for continuation on following line:
          do {
            str = strip(br.readLine());
            if( (str == null) || str.startsWith("@") )
              break;
            else
              val += str;
          } while(true);
          
          list.add(val);
          
          continue;
        }
        
        str = strip(br.readLine());
      }
    }
    catch(IOException e)
    {
      e.printStackTrace();
      throw new RuntimeException("this shouldn't happen: " + e);
    }
    
    
    if( paramList.size() > 0 )
      appendParamBlock( sb, "Parameters", paramList );
    
    if( throwsList.size() > 0 )
      appendParamBlock( sb, "Exceptions", throwsList );
    
    if( returnList.size() > 0 )
      sb.append("<dl><dt><b>Returns</b></dt><dd>" + returnList.getFirst() + "</dd></dl>");
    
    return sb.toString();
  }
  
  // strip off leading whitespace, and "*"'s
  private static final String strip( String str )
  {
    if( str == null )
      return null;
    str = str.trim();
    while( str.startsWith("*/") )
      str = str.substring(2);
    while( str.startsWith("*") )
      str = str.substring(1);
    str = str.trim();
    return str;
  }

  public void setEnclosingScope(Scope scope) {
	  this.enclosingScope=scope;
  }
  
  public Scope getEnclosingScope() {
	  return enclosingScope;
  }
  
  private static final void appendParamBlock( StringBuffer sb, String title, LinkedList list )
  {
    sb.append("<dl><dt><b>" + title + "</b></dt>");
    
    for( Iterator itr=list.iterator(); itr.hasNext(); )
    {
      String str = (String)(itr.next());
      String name;
      int idx = str.indexOf(" ");
      
      if( idx != -1 )
      {
        name = str.substring(0,idx);
        str  = str.substring(idx).trim();
      }
      else
      {
        name = str;
        str  = "";
      }
      
      sb.append("<dd><code>" + name + "</code> - " + str + "</dd>");
    }
    
    sb.append("</dl>");
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

