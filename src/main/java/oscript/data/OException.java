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

import java.util.*;




/**
 * Base class for the script type "Exception".  This isn't a java exception,
 * but instead is wrapped in a <code>PackagedScriptObjectException</code>.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class OException extends OObject
{
  /**
   * The type object for an instance of Exception.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.OException");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "Exception";
  public final static String[] MEMBER_NAMES   = new String[] {
                                                      "castToString",
                                                      "stackFrameIterator",
                                                      "getMessage"
                                                    };
  
  
  /**
   * The type of the exception object
   */
  private Value type;
  
  /**
   * The message
   */
  private String str;
  
  /**
   * The stack trace.
   */
  private StackFrame sf;
  
  /**
   * Because the line number may change before {@link #preserveStackFrame},
   * we need to take care to remember the original line number for accurate
   * back-traces.
   */
  private int line;
  
  // called from StackFrame#evalNode
  public final void preserveStackFrame()
  {
    if( line != sf.getLineNumber() )
      sf.setLineNumber(line);
    sf = sf.getSafeCopy();
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   */
  public OException( String str )
  {
    this( TYPE, str );
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.  This is the constructor called from built-in types
   * that subclss <i>Exception</i>.
   */
  protected OException( Value type, String str )
  {
    super();
    this.type = type;
    this.str  = str;
    
    sf = StackFrame.currentStackFrame();
    line = sf.getLineNumber();
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
    return type;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.  This is the constructor that is called via a
   * <code>BuiltinType</code> instance.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public OException( oscript.util.MemberTable args )
  {
    this( getArg0(args) );
  }
  
  protected static final String getArg0( oscript.util.MemberTable args )
  {
    if( args.length() != 1 )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
    else
      return args.referenceAt(0).castToString();
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
    Iterator iterator = stackFrameIterator();
    String str = getType().castToString() + ": " + getMessage();
    
    while( iterator.hasNext() )
      str += "\n  at " + iterator.next();
    
    return str;
  }
  
  /*=======================================================================*/
  /**
   * Get the message.
   * 
   * @return a String value
   */
  public String getMessage()
  {
    return str;
  }
  
  /*=======================================================================*/
  /**
   * Return an iteration of stack frames, starting with the most deeply 
   * nested stack frame.
   * 
   * @return an iterator of StackFrames
   */
  public Iterator stackFrameIterator()
  {
    preserveStackFrame();
    return sf.iterator();
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

