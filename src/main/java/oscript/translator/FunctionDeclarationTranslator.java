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

package oscript.translator;

import oscript.syntaxtree.*;


/**
 * A translator to implement one part of the language in terms of another.
 * This simplifies the compiler/interpreter implementation.  This translates
 * from:
 * <pre>
 *   Permissions "function" <IDENTIFIER> "(" Arglist ")" ("extends" ...)?
 *   "{"
 *     Program
 *   "}"
 * </pre>
 * to
 * <pre>
 *   Permissions "var" <IDENTIFIER> "=" "function" "(" Arglist ")" ("extends" ...)?
 *   "{"
 *     Program
 *   "};"
 * </pre>
 * 
 * <!--
 * Note that current implementation substitutes NodeToken-s for the most
 * similar NodeToken in the input.  This makes row/col #'s the most sane,
 * but the string representation may be wrong, for example "function"
 * instead of "var".  I did it this way since it is the least overhead,
 * which matters for the interpreter.  That should work find with the
 * current interpreter and compiler implementations
 * -->
 * 
 * @author Rob Clark
 * @version 0.1
 */
public class FunctionDeclarationTranslator
{
  /**
   * Convert a {@link FunctionDeclaration} production in the syntaxtree
   * into an equivalent production.
   * <pre>
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
   * </pre>
   */
  public static Node translate( FunctionDeclaration n )
  {
    if( n.translated == null )
    {
      n.translated = new VariableDeclaration(
        n.f0,                   // Permissions()
        n.f1,                   // "var"
        n.f2,                   // <IDENTIFIER>
        makeNodeOptional(n)     // ("=" Expression())?
      );
    }
    return n.translated;
  }
  
  private static NodeOptional makeNodeOptional( FunctionDeclaration n )
  {
    NodeSequence ns = new NodeSequence(2);
    
    ns.addNode(n.f1);    // "="
    
    // NOTE: we are cheating a bit by putting the FunctionPrimaryPrefix
    //    directly here, and skipping all the intermediary nodes.  This
    //    is faster, and works with the current compiler/interpreter
    ns.addNode( makeFunctionPrimaryPrefix(n) );
    
    return new NodeOptional(ns);
  }
  
  private static FunctionPrimaryPrefix makeFunctionPrimaryPrefix( FunctionDeclaration n )
  {
    FunctionPrimaryPrefix fpp = new FunctionPrimaryPrefix(
      n.f1,                   // "function"
      n.f3,                   // "("
      n.f4,                   // (Arglist())?
      n.f5,                   // ")"
      n.f6,                   // ("extends" ...)?
      n.f7,                   // "{"
      n.f8,                   // Program()
      n.f9,                   // "}"
      n.hasVarInScope,
      n.hasFxnInScope
    );
    
    fpp.id = oscript.data.Symbol.getSymbol( n.f2.toString() ).getId();
    
    return fpp;
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

