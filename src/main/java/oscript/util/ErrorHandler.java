/*=============================================================================
 *     Copyright Texas Instruments 2004.  All Rights Reserved.
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


package oscript.util;

/**
 * The <code>ErrorHandler</code> is used by the interpreter to handle
 * fatal, non-recoverable errors.  When a fatal error occurs, the interpreter
 * calls {@link #fatalError}, which is expected not to return.
 * <p>
 * Currently, the only fatal error is a corrupt symbol table.  See issue 
 * <a href="https://icandy.homeunix.org/cgi-bin/trac.cgi/ticket/246">#246</a>
 * for the juicy details.
 * <p>
 * A default <code>ErrorHandler</code> is provided, which simply prints an 
 * error message to <code>stderr</code> and {@link System#exit}s.  Sometimes, 
 * such as in a GUI application where the user never sees <code>stderr</code>,
 * you might want to implement your own error handler.  There are two ways to
 * do this:
 * <ul>
 *   <li> Set the <code>oscript.error.handler</code> property to the fully
 *     qualified class name of a non-abstract class that extends 
 *     <code>ErrorHandler</code> and has a no-arg constructor
 *   <li> Programatically by calling {@link #setErrorHandler}
 * </ul>
 * 
 * @author Rob Clark (rob@ti.com)
 * @version 1
 */
public abstract class ErrorHandler
{
  private static ErrorHandler handler;
  
  /**
   * Get the fatal error handler.  If it has not yet been set/loaded, this
   * will cause it to be loaded by first trying to load the class specified
   * by the <code>oscript.error.handler</code> property, and if that
   * fails reverting to the default fatal error handler.
   */
  public static ErrorHandler getErrorHandler()
  {
    if( handler == null )
    {
      String str = System.getProperty("oscript.error.handler");
      if( str != null )
      {
        try {
          handler = (ErrorHandler)(Class.forName(str).getConstructor().newInstance());
        } catch(Throwable t) { /* don't care! */ }
      }
      
      if( handler == null )
        handler = new DefaultErrorHandler();
    }
    
    return handler;
  }
  
  /**
   * Set the fatal error handler.
   */
  public static void setErrorHandler( ErrorHandler handler )
  {
    ErrorHandler.handler = handler;
  }
  
  /**
   * Called in times of peril.  This method should definately not return
   * normally, and probably shouldn't return at all, ie. it should call
   * {@link System#exit}, or sit in an infinite loop.  (It may be possible
   * to throw an exception, as long as care is taken to not throw anything
   * that may be caught by the interpreter.  And if you do keep the VM
   * running, don't expect the ObjectScript interpreter to function.)
   * 
   * @param  str     a string describing the error, how the user may
   *    correct the error, etc
   */
  public static void fatalError( String str )
  {
    getErrorHandler().showMessage(str);
    System.exit(-1);
  }
  
  /**
   * 
   * @param  str     a string describing the error, how the user may
   *    correct the error, etc
   */
  public static void warning( String str )
  {
    getErrorHandler().showMessage(str);
  }
  
  /**
   * Display an error message to the user.
   * 
   * @param  str     a string describing the error, how the user may
   *    correct the error, etc
   */
  public abstract void showMessage( String str );
}


/**
 * default error handler prints to <code>stderr</code> and then exits
 */
class DefaultErrorHandler
  extends ErrorHandler
{
  public void showMessage( String str )
  {
    System.err.println(str);
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

