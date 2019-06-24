/*=============================================================================
 *     Copyright Texas Instruments 2003.  All Rights Reserved.
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

package oscript.visitor;

import oscript.syntaxtree.*;
import oscript.translator.*;

import java.io.*;


/**
 * 
 * @author Rob Clark
 * @version 0.1
 */
public class TranslatedTreeDumper
  extends TreeDumper
{
  public TranslatedTreeDumper() { super(); }
  public TranslatedTreeDumper( Writer o ) { super(o); }
  public TranslatedTreeDumper( OutputStream o ) { super(o); }
  
  
  /**
   * <PRE>
   * f0 -> Permissions(true)
   * f1 -> "function"
   * f2 -> &lt;IDENTIFIER&gt;
   * f3 -> "("
   * f4 -> ( Arglist() )?
   * f5 -> ")"
   * f6 -> ( "extends" PrimaryExpressionWithTrailingFxnCallExpList() FunctionCallExpressionList() )?
   * f7 -> "{"
   * f8 -> Program(true)
   * f9 -> "}"
   * </PRE>
   */
  public void visit( FunctionDeclaration n )
  {
    FunctionDeclarationTranslator.translate(n).accept(this);
  }
  
  /**
   * <PRE>
   * f0 -> "for"
   * f1 -> "("
   * f2 -> (  PreLoopStatement() )?
   * f3 -> ";"
   * f4 -> ( Expression() )?
   * f5 -> ";"
   * f6 -> ( Expression() )?
   * f7 -> ")"
   * f8 -> EvaluationUnit()
   * </PRE>
   */
  public void visit( ForLoopStatement n )
  {
    ForLoopStatementTranslator.translate(n).accept(this);
  }
  
  /**
   * <PRE>
   * f0 -> "for"
   * f1 -> "("
   * f2 -> PreLoopStatement()
   * f3 -> ":"
   * f4 -> Expression()
   * f5 -> ")"
   * f6 -> EvaluationUnit()
   * </PRE>
   */
  public void visit( CollectionForLoopStatement n )
  {
    CollectionForLoopStatementTranslator.translate(n).accept(this);
  }
  
  /**
   * <PRE>
   * f0 -> "'{"
   * f1 -> Program(true)
   * f2 -> "}"
   * </PRE>
   */
  public void visit( ShorthandFunctionPrimaryPrefix n )
  {
    ShorthandFunctionPrimaryPrefixTranslator.translate(n).accept(this);
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

