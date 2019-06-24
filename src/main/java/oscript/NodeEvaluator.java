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


package oscript;

import java.io.File;

import oscript.data.Scope;
import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.StackFrame;
import oscript.util.SymbolTable;


/**
 * A <code>NodeEvaluator</code> is created by the <code>NodeFactory</code> to 
 * evaluate a node.  The node-evaluator can be used any number of times in 
 * order to evaluate a node, and must be thread safe.
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.2
 */
public abstract class NodeEvaluator
{
  /**
   * Evaluate, in the specified scope.
   * 
   * @param sf           the stack frame to evaluate the node in
   * @param scope        the scope to evaluate the node in
   * @return the result of evaluating the node
   */
  public abstract Object evalNode( StackFrame sf, Scope scope ) throws PackagedScriptObjectException;
  
  /**
   * Get the file that this node was parsed from.
   * 
   * @return the file
   */
  public abstract File getFile();
  
  /**
   * Get the function symbol (name), if this node evaluator is a function, 
   * otherwise return <code>-1</code>.
   * 
   * @return the symbol, or <code>-1</code>
   */
  public abstract int getId();
  
  public static final int ALL     = 0x00;
  public static final int PUBPROT = 0x01;
  public static final int PRIVATE = 0x02;
  
  public static final int[] SMIT_PERMS = { ALL, PUBPROT, PRIVATE };
  
  /**
   * Get the SMIT for the scope(s) created when invoking this node evaluator.
   * 
   * @param perm  <code>PRIVATE</code>, <code>PUBPROT</code>,
   *   <code>ALL</code>
   */
  public abstract SymbolTable getSharedMemberIndexTable( int perm );
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

