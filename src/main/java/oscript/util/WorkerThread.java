/*=============================================================================
 *     Copyright Texas Instruments 2005.  All Rights Reserved.
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

import java.util.*;

/**
 * A common minimum priority worker thread for all background tasks.
 * 
 * @author Rob Clark (rob@ti.com)
 * @version 1
 */
public class WorkerThread
  extends Thread
{
  private static final Runnable[] EMPTY_RUNNABLES = new Runnable[0];
  
  private static Runnable[] runnables = EMPTY_RUNNABLES;
  private static LinkedList runnableList = new LinkedList();
  
  private static LinkedList oneShotRunnables = new LinkedList();
  
  private static WorkerThread worker = new WorkerThread();
  
  private WorkerThread()
  {
    super("background worker");
    setPriority(MIN_PRIORITY);
    setDaemon(true);
    start();
  }
  
  /**
   * Call the specified runnable from the worker thread as soon as possible.
   */
  public static synchronized void invokeLater( Runnable r )
  {
    oneShotRunnables.add(r);
    synchronized(worker) {
      worker.notify();
    }
  }
  
  /**
   * Add a runnable to be called periodically from the background worker thread.
   * 
   * @param r       the runnable
   * @param freq    how frequently (in ms) should the worker attempt to run
   *    the runnable.  Of course higher priority tasks can cause the runnable
   *    to be run less frequently.  <b>(note:  not implemented yet... everything
   *    is currently hard coded to 500ms)</b>
   */
  public static synchronized void addRunnable( Runnable r, int frequency )
  {
    runnables = null;
    runnableList.add(r);
  }
  
  /**
   * Remove a runnable.
   */
  public static synchronized void removeRunnable( Runnable r )
  {
    runnables = null;
    runnableList.remove(r);
  }
  
  
  
  private static synchronized Runnable[] getOneShotRunnables()
  {
    if( oneShotRunnables.size() == 0 )
      return EMPTY_RUNNABLES;
    LinkedList tmp = oneShotRunnables;
    oneShotRunnables = new LinkedList();
    return (Runnable[])(tmp.toArray( new Runnable[ tmp.size() ] ));
  }
  
  private static synchronized Runnable[] getRunnables()
  {
    if( runnables == null )
    {
      if( runnableList.size() == 0 )
        runnables = EMPTY_RUNNABLES;
      else
        runnables = (Runnable[])(runnableList.toArray( new Runnable[ runnableList.size() ] ));
    }
    return runnables;
  }
  
  
  
  public void run()
  {
    while(true)
    {
      try
      {
        synchronized(this) {
          this.wait(500);
        }
        
        Runnable[] runnables = getOneShotRunnables();
        for( int i=0; i<runnables.length; i++ )
          runnables[i].run();
        
        // note:  keep our own copy, because the list may change beneth us:
        runnables = getRunnables();
        for( int i=0; i<runnables.length; i++ )
          if( runnableList.contains(runnables[i]) )
            runnables[i].run();
      }
      catch(Throwable t)
      {
        t.printStackTrace(); // XXX
      }
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

