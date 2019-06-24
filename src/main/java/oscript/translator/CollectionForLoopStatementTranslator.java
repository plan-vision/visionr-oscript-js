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
import oscript.parser.*;


/**
 * A translator to implement one part of the language in terms of another.
 * This simplifies the compiler/interpreter implementation.  This translates
 * from:
 * <pre>
 *   "for" "(" PreLoopStatement ":" Expression ")"
 *     EvaluationUnit
 * </pre>
 * to
 * <pre>
 *   "{"
 *     "var" "$itr$" = "(" Expression ")" "." "iterator" "(" ")" ";"
 *     "while" "(" "$itr$" "." "hasNext" "(" ")" ")"
 *     "{"
 *       PreLoopStatement "=" "$itr$" "." "next" "(" ")" ";"
 *       EvaluationUnit
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
public class CollectionForLoopStatementTranslator
{
  private final static NodeToken ITR_ID      = makeIdNt("$itr$");
  private final static NodeToken ITERATOR_ID = makeIdNt("iterator");
  private final static NodeToken NEXT_ID     = makeIdNt("next");
  private final static NodeToken HAS_NEXT_ID = makeIdNt("hasNext");
  
  /**
   * Convert a {@link CollectionForLoopStatement} production in the syntaxtree
   * into an equivalent production.
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
  public static Node translate( CollectionForLoopStatement n )
  {
    if( n.translated == null )
    {
      n.translated = new ScopeBlock(
        nt(n,"{"),            // "{"
        makeProgram(n),       // Program
        nt(n,"}"),            // "}"
        true,
        n.hasFxnInScope
      );
      
//       System.err.println("-- From: --------------");
//       n.accept( new oscript.visitor.TreeDumper(System.err) );
//       System.err.println("-- To: ----------------");
//       n.translated.accept( new oscript.visitor.TreeDumper(System.err) );
    }
    return n.translated;
  }
  
  private static Program makeProgram( CollectionForLoopStatement n )
  {
    NodeListOptional nlo = new NodeListOptional();
    
    nlo.addNode( 
      new VariableDeclarationBlock(
        makeVariableDeclaration(n),
        nt(n,";")             // ";"
      )
    );
    nlo.addNode( makeWhileLoopStatement(n) );
    
    return new Program( nlo, false );
  }
  
  private static VariableDeclaration makeVariableDeclaration( CollectionForLoopStatement n )
  {
    NodeSequence ns = new NodeSequence(2);
    
    ns.addNode( nt(n,"=") );  // "="
    
    NodeListOptional nlo = new NodeListOptional();
    
    nlo.addNode(
      new PrimaryPostfix(
        new NodeChoice(
          new PropertyIdentifierPrimaryPostfix(
            nt(n,"."),        // "."
            ITERATOR_ID       // "iterator"
          )
        )
      )
    );
    
    nlo.addNode(
      new PrimaryPostfix(
        new NodeChoice(
          new FunctionCallPrimaryPostfix(
            new FunctionCallExpressionList(
              nt(n,"("),      // "("
              new NodeOptional(),// (FunctionCallExpressionListBody)?
              nt(n,")")       // ")"
            )
          )
        )
      )
    );
    
    ns.addNode( 
      new PrimaryExpression(
        new PrimaryPrefix(
          new NodeChoice(
            new ParenPrimaryPrefix(
              nt(n,"("),      // "("
              n.f4,           // Expression
              nt(n,")")       // ")"
            )
          )
        ),
        nlo                   // "." "iterator" "(" ")"
      )
    );
    
    return new VariableDeclaration(
      new Permissions( new NodeListOptional() ),
      nt(n,"var"),            // "var"
      ITR_ID,                 // "$itr$"
      new NodeOptional(ns)    // "=" Expression
    );
  }
  
  private static WhileLoopStatement makeWhileLoopStatement( CollectionForLoopStatement n )
  {
    return new WhileLoopStatement(
      nt(n,"while"),          // "while"
      nt(n,"("),              // "("
      makeWhileLoopExpression(n),
      nt(n,")"),              // ")"
      new EvaluationUnit(
        new NodeChoice( 
          new ScopeBlock(
            nt(n,"{"),        // "{"
            makeWhileLoopProgram(n),
            nt(n,"}"),        // "}"
            n.hasVarInScope,
            n.hasFxnInScope
          )
        )
      )
    );
  }
  
  private final static NodeListOptional EMPTY_NLO = new NodeListOptional();
  private final static NodeOptional     EMPTY_NO  = new NodeOptional();
  
  private static Expression makeWhileLoopExpression( CollectionForLoopStatement n )
  {
    // "$itr$" "." "hasNext" "(" ")"
    NodeListOptional nlo = new NodeListOptional();
    
    nlo.addNode(
      new PrimaryPostfix(
        new NodeChoice(
          new PropertyIdentifierPrimaryPostfix(
            nt(n,"."),       // "."
            HAS_NEXT_ID      // "hasNext"
          )
        )
      )
    );
    
    nlo.addNode(
      new PrimaryPostfix(
        new NodeChoice(
          new FunctionCallPrimaryPostfix(
            new FunctionCallExpressionList(
              nt(n,"("),      // "("
              new NodeOptional(),// (FunctionCallExpressionListBody)?
              nt(n,")")       // ")"
            )
          )
        )
      )
    );
    
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
                                      new PrimaryExpression(
                                        new PrimaryPrefix(
                                          new NodeChoice(
                                            new IdentifierPrimaryPrefix(
                                              ITR_ID // "$itr$"
                                            )
                                          )
                                        ),
                                        nlo         // "." "hasNext" "(" ")"
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
  
  private static Program makeWhileLoopProgram( CollectionForLoopStatement n )
  {
    NodeListOptional nlo = new NodeListOptional();
    
    nlo.addNode( makeExpression(n) );
    nlo.addNode(n.f6);        // EvaluationUnit
    
    return new Program( nlo, false );
  }
  
  private static Node makeExpression( CollectionForLoopStatement n )
  {
    // PreLoopStatement "=" "$itr$" "." "next" "(" ")" ";"
    NodeListOptional nlo = new NodeListOptional();
    
    nlo.addNode(
      new PrimaryPostfix(
        new NodeChoice(
          new PropertyIdentifierPrimaryPostfix(
            nt(n,"."),        // "."
            NEXT_ID           // "next"
          )
        )
      )
    );
    
    nlo.addNode(
      new PrimaryPostfix(
        new NodeChoice(
          new FunctionCallPrimaryPostfix(
            new FunctionCallExpressionList(
              nt(n,"("),      // "("
              new NodeOptional(),// (FunctionCallExpressionListBody)?
              nt(n,")")       // ")"
            )
          )
        )
      )
    );
    
    ConditionalExpression ce = new ConditionalExpression(
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
                                  new PrimaryExpression(
                                    new PrimaryPrefix(
                                      new NodeChoice(
                                        new IdentifierPrimaryPrefix(
                                          ITR_ID          // "$itr$"
                                        )
                                      )
                                    ),
                                    nlo                   // "." "next" "(" ")"
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
    );
    
    Node node;
    
    if( n.f2.f0.choice instanceof VariableDeclaration )
    {
      VariableDeclaration vd = (VariableDeclaration)(n.f2.f0.choice);
      
      if( vd.f3.present() )
        throw new RuntimeException("the parser shouldn't let this happen!");
      
      NodeSequence ns = new NodeSequence(2);
      
      ns.addNode(nt(n,"="));  // "="
      ns.addNode(ce);
      
      node = new VariableDeclarationBlock(
        new VariableDeclaration(
          new Permissions( new NodeListOptional() ),
          vd.f1,              // "var"
          vd.f2,              // <IDENTIFIER>
          new NodeOptional(ns)// "=" Expression
        ),
        nt(n,";")             // ";"
      );
    }
    else
    {
      node = new ExpressionBlock(
        makeAssignmentExpression( n, (Expression)(n.f2.f0.choice), ce ),
        nt(n,";")             // ";"
      );
    }
    
    return new EvaluationUnit( new NodeChoice(node) );
  }
  
  private static Expression makeAssignmentExpression( CollectionForLoopStatement n, 
                                                      Expression e,
                                                      ConditionalExpression ce )
  {
    NodeListOptional nlo = new NodeListOptional();
    NodeSequence ns = new NodeSequence(2);
    
    Token eqToken = new Token();
    eqToken.kind = OscriptParserConstants.ASSIGN;
    ns.addNode( new NodeChoice( new NodeToken( "=", null, eqToken) ) );
    ns.addNode(ce);
    
    nlo.addNode(ns);
    
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
                                      new PrimaryExpression(
                                        new PrimaryPrefix(
                                          new NodeChoice(
                                            new ParenPrimaryPrefix(
                                              nt(n,"("),    // "("
                                              e,            // Expression
                                              nt(n,")")     // ")"
                                            )
                                          )
                                        ),
                                        EMPTY_NLO
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
        nlo
      ),
      EMPTY_NLO
    );
  }
  
  private static final NodeToken nt( CollectionForLoopStatement n, String img )
  {
    return n.f0;
    //return new NodeToken(img);
  }
  
  private static final NodeToken makeIdNt( String img )
  {
    Token token = new Token();
    token.kind = OscriptParserConstants.IDENTIFIER;
    return new NodeToken( img, oscript.data.OString.makeString(img), token);
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

