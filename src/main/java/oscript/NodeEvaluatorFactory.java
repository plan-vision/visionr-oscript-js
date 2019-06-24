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

import oscript.syntaxtree.Node;


/* XXX TODO... need to change API to:
 *   public NodeEvaluator createNodeEvaluator( Value name, Node node );
 */

/**
 * A <code>NodeEvaluatorFactory</code> transforms a <code>Node</code> into a
 * <code>NodeEvaluator</code>.  This forms the basis for the plug-in evaluator
 * and/or compiler.  By creating a <code>NodeEvaluator</code> we can abstract 
 * away wether or not the code gets compiled.  It may seem weird to have the 
 * interpreter be a pluggable component, but just think of it as a nice clean
 * distinction between the interpreter and the data.
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.3
 */
public interface NodeEvaluatorFactory
{
  /**
   * Given a <code>Node</code>, generate a <code>NodeEvaluator</code>.
   * 
   * @param name         name of node to process, for debugging
   * @param node         the node
   * @return the node-evaluator
   */
  public NodeEvaluator createNodeEvaluator( String name, Node node );
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

