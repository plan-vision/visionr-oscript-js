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


package oscript.interpreter;

import java.io.File;

import oscript.util.*;
import oscript.data.*;
import oscript.exceptions.*;
import oscript.syntaxtree.*;


/**
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.10
 */
public class InterpretedNodeEvaluator
  extends oscript.NodeEvaluator
{

  private Node node;
  private int  id;
  
  /**
   * Class constructor.
   * 
   * @param name         name of the node, for debugging
   * @param node         the wrapped node
   */
  InterpretedNodeEvaluator( String name, Node node )
  {
    this.id   = name.endsWith(".os") ? -1 : Symbol.getSymbol(name).getId();
    this.node = node;
  }
  
  /**
   * Get the file that this node was parsed from.
   * 
   * @return the file
   */
  public File getFile()
  {
    return null;
  }
  
  /**
   * Get the function symbol (name), if this node evaluator is a function, 
   * otherwise return <code>-1</code>.
   * 
   * @return the symbol, or <code>-1</code>
   */
  public int getId()
  {
    return id;
  }
  
  /**
   * Evaluate, in the specified scope.  If this is a function, the Arguments 
   * to the function, etc., are defined in the <code>scope</code> that the 
   * function is evaluated in.
   * 
   * @param sf           the stack frame to evaluate the node in
   * @param scope        the scope to evaluate the function in
   * @return the result of evaluating the function
   */
  public Object evalNode( StackFrame sf, Scope scope )
    throws PackagedScriptObjectException
  {
    // construct evaluator:
    EvaluateVisitor evaluator = new EvaluateVisitor(scope);
    
    // XXX hack... clean me up!  prolly need to change the grammar so there
    // is a function body:  FunctionBody ::== Program
    // so that we can better handle our functionly duties inside the
    // visitor
    Object result;
    
      if( node instanceof Program )
        result = evaluator.evaluateFunction( (Program)node );
      else
        result = node.accept( evaluator, null );
      
      if( result instanceof Value )
        result = ((Value)result).unhand();
      
      if( result == null )
        result = Value.NULL;
      
      return result;
  }
  
  /**
   * Get the SMIT for the scope(s) created when invoking this node evaluator.
   * 
   * @param perm  <code>PRIVATE</code>, <code>PUBPROT</code>,
   *   <code>ALL</code>
   */
  public SymbolTable getSharedMemberIndexTable( int perm )
  {
    if( smit[perm] == null )
      smit[perm] = new OpenHashSymbolTable( 3, 0.67f );
    return smit[perm];
  }
  
  private transient SymbolTable[] smit = new SymbolTable[3];
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
