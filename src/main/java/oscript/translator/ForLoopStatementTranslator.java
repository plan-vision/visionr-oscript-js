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
 *   "for" "(" (PreLoopStatement)? ";" (Expression1)? ";" (Expression2)? ")"
 *     EvaluationUnit
 * </pre>
 * to
 * <pre>
 *   "{"
 *     (PreLoopStatement)? ";"
 *     "while" "(" Expression1 ")"
 *     "{"
 *       EvaluationUnit
 *       (Expression2)?
 *     "}"
 *   "}"
 * </pre>
 * 
 * <!--
 * Note that current implementation substitutes NodeToken-s for the most
 * similar NodeToken in the input.  This makes row/col #'s the most sane,
 * but the string representation may be wrong, for example "for" instead 
 * of "while".  I did it this way since it is the least overhead, which 
 * matters for the interpreter.  That should work find with the current 
 * interpreter and compiler implementations
 * -->
 * 
 * @author Rob Clark
 * @version 0.1
 */
public class ForLoopStatementTranslator
{
  /**
   * Convert a {@link ForLoopStatement} production in the syntaxtree
   * into an equivalent production.
   * <PRE>
   * f0 -> "for"
   * f1 -> "("
   * f2 -> ( PreLoopStatement() )?
   * f3 -> ";"
   * f4 -> ( Expression() )?
   * f5 -> ";"
   * f6 -> ( Expression() )?
   * f7 -> ")"
   * f8 -> EvaluationUnit()
   * </PRE>
   */
  public static Node translate( ForLoopStatement n )
  {
    if( n.translated == null )
    {
      n.translated = new ScopeBlock(
        n.f0,                 // "{"
        makeProgram(n),       // Program
        n.f0,                 // "}"
        n.hasVarInScope,
        n.hasFxnInScope
      );
    }
    return n.translated;
  }
  
  private static Program makeProgram( ForLoopStatement n )
  {
    NodeListOptional nlo = new NodeListOptional();
    
    if( n.f2.present() )
      nlo.addNode( new EvaluationUnit( new NodeChoice(n.f2.node) ) );// PreLoopStatement
    nlo.addNode( makeWhileLoopStatement(n) );
    
    return new Program( nlo, false );
  }
  
  private static WhileLoopStatement makeWhileLoopStatement( ForLoopStatement n )
  {
    return new WhileLoopStatement(
      n.f0,                   // "while"
      n.f1,                   // "("
      makeExpression(n),      // Expression1
      n.f7,                   // ")"
      makeEvaluationUnit(n)   // EvaluationUnit
    );
  }
  
  private static Expression makeExpression( ForLoopStatement n )
  {
    if( n.f4.present() )
      return (Expression)(n.f4.node);
    else
      return makeTrueExpression(n);
  }
  
  private static final NodeListOptional EMPTY_NLO = new NodeListOptional();
  private static final NodeOptional     EMPTY_NO  = new NodeOptional();
  
  private static Expression makeTrueExpression( ForLoopStatement n )
  {
    final oscript.parser.Token token = new oscript.parser.Token();
    token.kind = oscript.parser.OscriptParserConstants.TRUE;
    
    token.beginLine   = n.f5.beginLine;
    token.beginColumn = n.f5.beginColumn;
    token.beginOffset = n.f5.beginOffset;
    token.endLine     = n.f5.endLine;
    token.endColumn   = n.f5.endColumn;
    token.endOffset   = n.f5.endOffset;
    
    return new Expression(
      new AssignmentExpression(
        new ConditionalExpression(
          new LogicalOrExpression(
            new LogicalAndExpression(
              new BitwiseOrExpression(
                new BitwiseXorExpression(
                  new BitwiseAndExpression(
                    new EqualityExpression(
                      new RelationalExpression(
                        new ShiftExpression(
                          new AdditiveExpression(
                            new MultiplicativeExpression(
                              new UnaryExpression(
                                EMPTY_NO,
                                new PostfixExpression(
                                  new TypeExpression(
                                    new NodeChoice(
                                      new NodeToken(
                                        "true",
                                        oscript.data.OString.makeString("true"),
                                        token
                                      )
                                    )
                                  ),
                                  EMPTY_NO
                                )
                              ),
                              EMPTY_NLO
                            ),
                            EMPTY_NLO
                          ),
                          EMPTY_NLO
                        ),
                        EMPTY_NLO
                      ),
                      EMPTY_NLO
                    ),
                    EMPTY_NLO
                  ),
                  EMPTY_NLO
                ),
                EMPTY_NLO
              ),
              EMPTY_NLO
            ),
            EMPTY_NLO
          ),
          EMPTY_NO
        ),
        EMPTY_NLO
      ),
      EMPTY_NLO
    );
  }
  
  private static EvaluationUnit makeEvaluationUnit( ForLoopStatement n )
  {
    NodeListOptional nlo = new NodeListOptional();
    
    nlo.addNode(n.f8);        // EvaluationUnit
    if( n.f6.present() )
      nlo.addNode(n.f6.node); // Expression2
    
    Program loopBody = new Program( nlo, false );
    
    return new EvaluationUnit( 
      new NodeChoice( 
        new ScopeBlock(
          n.f0,               // "{"
          loopBody,           // Program
          n.f0,               // "}"
          false,
          n.hasFxnInScope
        )
      )
    );
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

