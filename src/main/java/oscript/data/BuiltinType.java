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


import java.util.*;
import java.lang.reflect.*;

import oscript.util.*;
import oscript.exceptions.*;


/**
 * A <code>BuiltinType</code> instance is used to represent a built-in type.
 * This is similar to <code>JavaClassWrapper</code>, in that it allows a java
 * type to be sub-classed or instantiated, but it's difference is that it
 * restricts access.  Because the <code>Value</code> interface has dummy
 * methods for all the possible methods it's subclass may implement, we have
 * to do this to prevent the built-in types from appearing to have methods
 * that they don't have.  For example:
 * <pre>
 *   var m = true.bopPlus;
 * </pre>
 * The <code>Value</code> dummy methods throw the appropriate exceptions
 * if you try and use a non-existant method, for example:
 * <pre>
 *   var v = true + false;
 * </pre>
 * To accomodate the possibility of things changing between when an object
 * referencing this object is serialized, and when it is read back from a
 * serial data stream (in a different invokation of the JVM), only the class-
 * name is stored.  The other paramters are loaded as via reflection as static
 * fields of the named class:
 * <ul>
 *   <li> public static String PARENT_TYPE_NAME;  // class-name
 *   <li> public static String TYPE_NAME;         // the name of this type
 *   <li> public static String[] MEMBER_NAMES;    // names of members of this type
 * </ul>
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.38
 */
public class BuiltinType extends Type
{
	  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.BuiltinType");
	  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
	  public final static String TYPE_NAME        = "JavaClass";
	  public final static String[] MEMBER_NAMES   = new String[] {
	                       "isA",
	                       "castToString",
	                       "castToJavaObject",
	                       "callAsConstructor",
	                       "callAsExtends",
	                       "getMember",
	                       "getClassLoader",
	                       "getName"
	                     };
	@Override
	protected Value getTypeImpl() {
		// TODO Auto-generated method stub
		return null;
	}
  /**
   * The name of the java class implementing this type.  The other attributes 
   * of this object are determined using reflection, to ensure that they are
   * up-to-date if this type is read from a serialized stream.
   */
  private String className;
  
  /**
   * The name of this type.
   */
  transient private String  typeName;  // XXX we cant make an OString in constructor, because it needs OString.TYPE
  
  /**
   * In the inheritance hierarchy, this type's parent.
   */
  transient private BuiltinType   parentType;
  
  /**
   * Build a table of our members, and parent type members, and so on,
   * in order to quickly determine if we have a requested member.
   */
  transient private SymbolTable memberSet = null;
  
  
  /**
   */
  transient private boolean initialized = false;
  
  /**
   * The constructor for <code>javaClass</code> that takes an array of 
   * <code>Value</code>.  This is the constructor used by derived class
   * when this built-in is subclassed.
   */
  transient private Constructor javaClassConstructor;
  transient private Constructor javaWrapperClassConstructor;
  
  
  /**
   * Table of all builtin types.  A <code>BuiltinType</code> instance is 
   * intern'd, so there shouldn't be two instances representing the same 
   * builtin type.
   */
  private static Hashtable classCache;
  
  /**
   * Get an instance of a built-in type.  This method ensures that only a single
   * instance representing a type gets created, regardless of the number of
   * times this is called.  This is needed to keep things sane when serialization
   * is going on.
   * 
   * @param className    the name of the java class implementing the built-in type.
   */
  public static final BuiltinType makeBuiltinType( String className )
  {
    return makeBuiltinType( className, null );
  }
  private static synchronized final BuiltinType makeBuiltinType( String className, BuiltinType type )
  {
    if( classCache == null )
      classCache = new Hashtable();
    
    BuiltinType bt = (BuiltinType)(classCache.get(className));
    
    if( bt == null )
    {
      if( type != null )
        bt = type;
      else
        bt = new BuiltinType(className);
      
      classCache.put( className, bt );
    }
    
    return bt;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param className    the name of the java class implementing the built-in type.
   */
  private BuiltinType( String className )
  {
    this.className = className;
  }
  
  /*=======================================================================*/
  protected synchronized void init()
  {
    if( !initialized )
    {
      try
      {
       /* if( javaClass.getField("PARENT_TYPE_NAME").get(null) != null )
          parentType = makeBuiltinType( (String)(javaClass.getField("PARENT_TYPE_NAME").get(null)) );
        else
          parentType = null;
        
        typeName  = (String)(javaClass.getField("TYPE_NAME").get(null));*/
        
        memberSet = new OpenHashSymbolTable();
        /*BuiltinType bt = this;
        while( bt != null )
        {
          String[] names = (String[])(bt.javaClass.getField("MEMBER_NAMES").get(null));
          for( int i=0; i<names.length; i++ )
            memberSet.create( Symbol.getSymbol( names[i] ).getId() );
          bt = bt.parentType;
          if( (bt != null) && (bt != this) )
            bt.init();
        }*/
      }
      catch(Exception e)
      {
        throw OJavaException.convertException(e);
      }
      initialized = true;
    }
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
    // the check could be done only in init(), at expense of extra method call:
    if( !initialized ) init();
    
    return (this == type.unhand()) || ((parentType != null) ? parentType.isA(type) : false);
  }
  
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
    // note:  somehow due to eclipse's exception logging and error handling,
    //   we actually manage to get called recursively.. sort of.. anyways,
    //   and this seems to be the solution:   (see #388)
    if( typeName == null ) init();
    
    return typeName;
  }
  
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
	// note symbol is pushed to ScriptObject to work around a starup dependency issue
    Value bopCast = getMember( ScriptObject._BOPCAST, false );
    if( bopCast != null )
      return bopCast.callAsFunction( new Value[] { val } );
    return super.bopCast(val);
  }
  
  // bopCastR would be an instance member, not static (class) member
  
  /*=======================================================================*/
  /**
   * Overloaded to hide "non-existant" methods.  (Ie. methods from Value
   * base class.)
   * 
   * @param obj          an object of this type
   * @param id           the id of the symbol that maps to the member
   * @return a reference to the member, or null
   */
  protected Value getTypeMember( Value obj, int id )
  {
    // the check could be done only in init(), at expense of extra method call:
    if( !initialized ) init();
    
    if( memberSet.get(id) != -1 )
      return super.getTypeMember( obj, id );
    else if( obj.castToJavaObject() instanceof Proxy )
      return ((Proxy)(obj.castToJavaObject())).getTypeMember( obj, id );
    else
      return null;
  }
  
  /*=======================================================================*/
  /**
   * Overloaded, because of how we handle args to the constructor.
   */
  protected Object doConstruct( StackFrame sf, MemberTable args, boolean isWrapper )
  {
    // the check could be done only in init(), at expense of extra method call:
    if( !initialized ) init();
    
    Constructor c = isWrapper ? javaWrapperClassConstructor : javaClassConstructor;
    
    if( c == null )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("can't call as constructor") );
    
    try
    {
      if( args == null )
        args = new OArray(0);
      Object[] arr = new Object[] { args };
      return (Value)(c.newInstance(arr));
    }
    catch(InvocationTargetException e)
    {
      Throwable t = e.getTargetException();
      
      if( t instanceof PackagedScriptObjectException )
        throw (PackagedScriptObjectException)t;
      else
        throw PackagedScriptObjectException.makeExceptionWrapper( new OException(t + ": " + t.getMessage()) );
    }
    catch(Throwable e)
    {
      if(DEBUG)
        e.printStackTrace();
      throw PackagedScriptObjectException.makeExceptionWrapper( new OException(e + ": " + e.getMessage()) );
    }
  }
  
  /*=======================================================================*/
  /**
   * maintains unique-ness of a JavaClassWrapper when stuff gets serialized or
   * un-serialized
   */
  Object readResolve()
    throws java.io.ObjectStreamException
  {
    return makeBuiltinType( className, this );
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
  protected void populateTypeMemberSet( Set s, boolean _dbg )
  {
    init();
    
    for( Iterator itr=memberSet.symbols(); itr.hasNext(); )
      s.add( Symbol.getSymbol( ((Integer)(itr.next())).intValue() ) );
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
  protected void populateMemberSet( Set s, boolean _dbg )  {}
  
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

