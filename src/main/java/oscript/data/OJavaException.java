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
 * This class wraps a java exception object.  At some point, we could perhaps 
 * make this a script type...
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.7
 */
public class OJavaException extends OException
{
  /**
   * The type object for an instance of <i>JavaException</i>.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.OJavaException");
  public final static String PARENT_TYPE_NAME = "oscript.data.OException";
  public final static String TYPE_NAME        = "JavaException";
  public final static String[] MEMBER_NAMES   = new String[] { "getJavaException" };
  
  // XXX should this be in PackagedScriptObjectException???
  public static final RuntimeException convertException( Throwable t )
  {
    if( t instanceof PackagedScriptObjectException )
      return (PackagedScriptObjectException)t;
    else
      return PackagedScriptObjectException.makeExceptionWrapper( new OJavaException(t) );
  }

  
  /**
   * The java exception as a script object (JavaObjectWrapper)
   */
  private Value ot;
  
  /**
   * Class Constructor.
   * 
   * @param t            a java exception
   */
  public OJavaException( Throwable t )
  {
    this( t, JavaBridge.convertToScriptObject(t) );
  }
  public OJavaException( Throwable t, Value ot )
  {
    super( TYPE, t.getMessage() );
    
    this.ot = ot;
    
    if(DEBUG)
      if( t instanceof PackagedScriptObjectException )
        Thread.dumpStack();
  }
  
  /**
   * Class Constructor.  This is the constructor that is called via a
   * <code>BuiltinType</code> instance.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public OJavaException( oscript.util.MemberTable args )
  {
    this( (Throwable)(args.referenceAt(0).castToJavaObject()) );
  }
  
  /* For better java integration we actually pretend to be a java object
   * by forwarding all these to ot...
   */
  
  public Object getMonitor()
  {
    return ot.getMonitor();
  }
  
  // XXX castToXYZ, bopXYZ
  
  public Object castToJavaObject()
  {
    return ot.castToJavaObject();
  }
  
  public Value getMember( int id, boolean exception )
    throws PackagedScriptObjectException
  {
    return ot.getMember( id, exception );
  }
  
  public Value getType()
  {
    return ot.getType();
  }
  
  public final java.util.Set memberSet()
  {
    return ot.memberSet();
  }
  
  /**
   */
  public Value getJavaException()
  {
    return ot;
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

