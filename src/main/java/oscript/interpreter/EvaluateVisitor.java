/*=============================================================================
 *     Copyright Texas Instruments 2000-2003.  All Rights Reserved.
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


import oscript.syntaxtree.*;
import oscript.util.StackFrame;
import oscript.data.*;
import oscript.exceptions.*;
import oscript.visitor.ObjectVisitor;
import oscript.parser.OscriptParser;
import oscript.parser.OscriptParserConstants;
import oscript.parser.ParseException;
import oscript.OscriptInterpreter;
import oscript.translator.*;



/* RET like instructions "return", "break", and "continue" are used by
 *     throwing an exception, and catching it in the appropriate places.
 */
class Return extends RuntimeException
{
  Return() { super(); }
}
class Break extends RuntimeException
{
  Break() { super(); }
}
class Continue extends RuntimeException
{
  Continue() { super(); }
}


/* NOTE: in places where values are cached in the syntaxtree, there is too
 *       much overlap between EvaluateVisitor and CompilerVisitor... try to
 *       find a way to rectify this.
 */


/**
 * The EvaluateVisitor is the heart of the interpreter.
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.123
 */
public class EvaluateVisitor implements ObjectVisitor, OscriptParserConstants
{
  /**
   * The current scope.  This is passed as an argument to the constructor, 
   * and does not change for the live of the evaluator.
   */
  private Scope thisScope;
  
  
  /* For now I am using this naming convention to encode values that are
   * "returned" by some of the visit methods.  I want to avoid the overhead
   * of allocating memory to return values, so we do it by instance variables.
   * The convention is that they are set by the visitor and read-only to
   * everyone else.  
   */
  private int       NodeToken_kind;
  private NodeToken NodeToken_lastToken;  // for generating error messages
  private java.util.Vector NodeToken_lastSpecials;
  
  private int[]     Arglist_names;
  private boolean   Arglist_varargs;
  
  private int       Permissions_attr;
  
  
  // to save a bit on the overhead of allocating a new object, and
  // filling in the stack trace (which we don't need), we reuse
  // these exceptions:
  private Value retVal = Value.UNDEFINED;
  private static final Return   RETURN   = new Return();
  //private static final Break    BREAK    = new Break();
  //private static final Continue CONTINUE = new Continue();
  
  /*=======================================================================*/
  /**
   * Class Constructor.  An <code>EvaluateVisitor</code> is constructed
   * to evaluate within a specified context (scope).  The context' purpose
   * is twofold:  1) it is used to implement block structure, for example:
   * <pre>
   *   ... some script ...
   *   {
   *      var someVar;
   *      ... some script ...
   *   }
   * </pre>
   * the block containing the variable <code>someVar</code> is evaluated
   * in a new context (with the current context as the previous context).
   * <p>
   * It is also used for 2) implementing function/method calls.  When a
   * function/method is called, a new context is created, with the
   * context the function/method was declared in as the previous
   * context.
   * <p>
   * NOTE: there are cases where we re-use the <code>EvaluateVisitor</code>
   * for efficiency... need to document that better.
   * 
   * @param thisScope    the context to evaluate within
   */
  public EvaluateVisitor( Scope thisScope )
  {
    this.thisScope = thisScope;
  }
  
  
  /*=======================================================================*/
  /**
   */
  public NodeToken getLastNodeToken()
  {
    return NodeToken_lastToken;
  }
  
  
  /*=======================================================================*/
  /**
   */
  public Value evaluateFunction( Program n )
  {
    Value val = null;
    
    try
    {
      val = (Value)(n.accept( this, null ));
    }
    catch(Return r)
    {
      val = retVal;
    }
    
    if( val == null )
      val = Value.NULL;
    
    return val;
  }
  
  /*=======================================================================*/
  /**
   */
  public Object visit( NodeList n, Object argu )
  {
    return null;
  }
  
  /*=======================================================================*/
  /**
   */
  public Object visit( NodeListOptional n, Object argu )
  {
    return null;
  }
  
  /*=======================================================================*/
  /**
   */
  public Object visit( NodeOptional n, Object argu )
  {
    return null;
  }
  
  /*=======================================================================*/
  /**
   */
  public Object visit( NodeSequence n, Object argu )
  {
    return null;
  }
  
  /*=======================================================================*/
  /**
   */
  public Object visit( NodeToken n, Object argu )
  {
    NodeToken_kind      = n.kind;
    NodeToken_lastToken = n;
    
    StackFrame.currentStackFrame().setLineNumber( thisScope, n.beginLine );
    
    if( n.specialTokens != null )
      NodeToken_lastSpecials = n.specialTokens;
    
    if( n.cachedValue == null )
    {
      switch(n.kind)
      {
        case IDENTIFIER:
          n.cachedValue = n.otokenImage;
          break;
        case INTEGER_LITERAL:
        case HEX_LITERAL:
        case OCTAL_LITERAL:
        case BINARY_LITERAL:
        case DECIMAL_LITERAL:
          n.cachedValue = OExactNumber.makeExactNumber( n.otokenImage.castToExactNumber() );
          break;
        case FLOATING_POINT_LITERAL:
          n.cachedValue = OInexactNumber.makeInexactNumber( n.otokenImage.castToInexactNumber() );
          break;
        case STRING_LITERAL:
          n.cachedValue = new OString( OString.chop( n.tokenImage.substring( 1, n.tokenImage.length()-1 ) ) );  // should this be intern'd???
          break;
        case REGEXP_LITERAL:
          n.cachedValue = RegExp.createRegExp( n.otokenImage );
          break;
        case TRUE:
          n.cachedValue = OBoolean.TRUE;
          break;
        case FALSE:
          n.cachedValue = OBoolean.FALSE;
          break;
        case NULL:
          n.cachedValue = Value.NULL;
          break;
        case UNDEFINED:
          n.cachedValue = Value.UNDEFINED;
          break;
        case -1:
        default:
          // leave as null
      }
    }
    
    return n.cachedValue;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> ( &lt;UNIX_SELF_EXECUTABLE_COMMENT&gt; )?
   * f1 -> Program(false)
   * f2 -> &lt;EOF&gt;
   * </PRE>
   */
  public Object visit( ProgramFile n, Object argu )
  {
    return n.f1.accept( this, argu );
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> ( EvaluationUnit() )*
   * </PRE>
   */
  public Object visit( Program n, Object argu )
  {
    Value val = null;
    
    for( int i=0; i<n.f0.size(); i++ )
    {
      val = (Value)(n.f0.elementAt(i).accept( this, argu ));
    }
    
    return val;
  }
  

  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> ScopeBlock()
   *       | VariableDeclarationBlock()
   *       | FunctionDeclaration()
   *       | TryStatement()
   *       | ForLoopStatement()
   *       | WhileLoopStatement()
   *       | ConditionalStatement()
   *       | SynchronizedStatement()
   *       | ReturnStatement()
   *       | BreakStatement()
   *       | ContinueStatement()
   *       | ExpressionBlock()
   *       | ThrowBlock()
   *       | ImportBlock()
   *       | MixinBlock()
   *       | EvalBlock()
   * </PRE>
   */
  public Object visit( EvaluationUnit n, Object argu )
  {
    return n.f0.accept( this, argu );
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "{"
   * f1 -> Program()
   * f2 -> "}"
   * </PRE>
   */
  public Object visit( ScopeBlock n, Object argu )
  {
    // push new scope:
    Scope savedThisScope = thisScope;
    thisScope = new BasicScope(thisScope);
    
    Object retVal;
    
    try
    {
      retVal = n.f1.accept(this,argu);
    }
    finally
    {
      // pop:
      thisScope = savedThisScope;
    }
    
    return retVal;
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> VariableDeclaration()
   * f1 -> ";"
   * </PRE>
   */
  public Object visit( VariableDeclarationBlock n, Object argu )
  {
    return n.f0.accept( this, argu );
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> Expression()
   * f1 -> ";"
   * </PRE>
   */
  public Object visit( ExpressionBlock n, Object argu )
  {
    return n.f0.accept( this, argu );
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "throw"
   * f1 -> Expression()
   * f2 -> ";"
   * </PRE>
   */
  public Object visit( ThrowBlock n, Object argu )
  {
	  throw new RuntimeException(n.toString()+" : "+argu);
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "mixin"
   * f1 -> Expression()
   * f2 -> ";"
   * </PRE>
   */
  public Object visit( MixinBlock n, Object argu )
  {
    thisScope.mixin( (Value)(n.f1.accept(this,argu)) );
    return null;
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "eval"
   * f1 -> Expression()
   * f2 -> ";"
   * </PRE>
   */
  public Object visit( EvalBlock n, Object argu )
  {
    // f1 should evaluate to a string:
    Value val = (Value)(n.f1.accept( this, argu ));
    
    try
    {
      OscriptInterpreter.eval( val.castToString(), thisScope );
    }
    catch(ParseException e)
    {
      throw PackagedScriptObjectException.makeExceptionWrapper( new OException( e.getMessage() ) );
    }
    
    return null;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> Permissions()
   * f1 -> "var"
   * f2 -> &lt;IDENTIFIER&gt;
   * f3 -> ( "=" Expression() )?
   * </PRE>
   */
  public Object visit( VariableDeclaration n, Object argu )
  {
    n.f0.accept( this, argu );
    
    Value val = null;
    
    if( n.f3.present() )
      val = (Value)(((NodeSequence)(n.f3.node)).elementAt(1).accept(this,argu));
    
    Value var = thisScope.createMember( (Value)(n.f2.accept(this,argu)), Permissions_attr );
    
    if( n.f3.present() )
      var.opAssign(val);
    
    return null;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> Permissions()
   * f1 -> "function"
   * f2 -> &lt;IDENTIFIER&gt;
   * f3 -> "("
   * f4 -> ( Arglist() )?
   * f5 -> ")"
   * f6 -> ( "extends" PrimaryExpressionWithTrailingFxnCallExpList() FunctionCallExpressionList() )?
   * f7 -> "{"
   * f8 -> Program()
   * f9 -> "}"
   * </PRE>
   */
  public Object visit( FunctionDeclaration n, Object argu )
  {
    return FunctionDeclarationTranslator.translate(n).accept( this, argu );
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> Permissions()
   * f1 -> &lt;IDENTIFIER&gt;
   * f2 -> ( "," Permissions() &lt;IDENTIFIER&gt; )*
   * f3 -> ( "..." )?
   * </PRE>
   */
  public Object visit( Arglist n, Object argu )
  {
    if( n.cachedValue == null )
    {
      int len = 2 * (n.f2.size() + 1);
      
      n.cachedValue = new int[len];
      n.cachedValue[0] = Symbol.getSymbol( (OString)(n.f1.accept( this, argu )) ).getId();
      n.cachedValue[1] = getPermissions( n.f0, Reference.ATTR_PRIVATE );
      
      for( int i=0; i<n.f2.size(); i++ )
      {
        n.cachedValue[2*(i+1)]   = Symbol.getSymbol( (OString)(((NodeSequence)(n.f2.elementAt(i))).elementAt(2).accept( this, argu )) ).getId();
        n.cachedValue[2*(i+1)+1] = getPermissions( (Permissions)(((NodeSequence)(n.f2.elementAt(i))).elementAt(1)), Reference.ATTR_PRIVATE );
      }
    }
    
    Arglist_names = n.cachedValue;
    Arglist_varargs = n.f3.present();
    
    return null;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "try"
   * f1 -> EvaluationUnit()
   * f2 -> ( "catch" "(" Expression() &lt;IDENTIFIER&gt; ")" EvaluationUnit() )*
   * f3 -> ( "catch" "(" &lt;IDENTIFIER&gt; ")" EvaluationUnit() )?
   * f4 -> ( "finally" EvaluationUnit() )?
   * </PRE>
   */
  public Object visit( TryStatement n, Object argu )
  {
    try
    {
      n.f1.accept(this,argu);
    }
    catch(PackagedScriptObjectException e)
    {
      boolean handled = false;
      
      for( int i=0; (i<n.f2.size()) && !handled; i++ )
      {
        NodeSequence seq = (NodeSequence)(n.f2.elementAt(i));
        
        Value val = (Value)(seq.elementAt(2).accept( this, argu ));
        
        if( e.val.bopInstanceOf(val).castToBoolean() )
        {
          handled = true;
          
          // push:
          Scope savedThisScope = thisScope;
          thisScope = new BasicScope(thisScope);
          
          try
          {
            Value var = thisScope.createMember( (Value)(seq.elementAt(3).accept(this,argu)), 0 /*XXX*/ );
            var.opAssign(e.val);
            
            seq.elementAt(5).accept( this, argu );
          }
          finally
          {
            // pop:
            thisScope = savedThisScope;
          }
        }
      }
      
      if( n.f3.present() && !handled )
      {
        handled = true;
        
        NodeSequence seq = (NodeSequence)(n.f3.node);
        
        // push:
        Scope savedThisScope = thisScope;
        thisScope = new BasicScope(thisScope);
        
        try
        {
          Value var = thisScope.createMember( (Value)(seq.elementAt(2).accept(this,argu)), 0 /*XXX*/ );
          var.opAssign(e.val);
          
          seq.elementAt(4).accept( this, argu );
        }
        finally
        {
          // pop:
          thisScope = savedThisScope;
        }
      }
      
      if( !handled )
        throw e;
    }
    finally
    {
      // XXX this is maybe a bug, if BREAK, CONTINUE, or RETURN are thrown...
      if( n.f4.present() )
        ((NodeSequence)(n.f4.node)).elementAt(1).accept( this, argu );
    }
    
    return null;
  }

  /*=======================================================================*/
  /**
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
  public Object visit( ForLoopStatement n, Object argu )
  {
    return ForLoopStatementTranslator.translate(n).accept( this, argu );
  }
  
  /*=======================================================================*/
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
  public Object visit( CollectionForLoopStatement n, Object argu )
  {
    return CollectionForLoopStatementTranslator.translate(n).accept( this, argu );
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> VariableDeclaration()
   *       | Expression()
   * </PRE>
   */
  public Object visit( PreLoopStatement n, Object argu )
  {
    return n.f0.accept( this, argu );
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> EvaluationUnit()
   * </PRE>
   */
  public Object visit( WhileLoopStatement n, Object argu )
  {
    while( ((Value)(n.f2.accept(this,argu))).castToBooleanSoft() )
    {
      // evaluate body:
      try
      {
        n.f4.accept( this, argu );
      }
      catch(Break e)
      {
        break;
      }
      catch(Continue e)
      {
        continue;
      }
    }
    
    return null;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "if"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> EvaluationUnit()
   * f5 -> ( "else" EvaluationUnit() )?
   * </PRE>
   */
  public Object visit( ConditionalStatement n, Object argu )
  {
    Value val = (Value)(n.f2.accept( this, argu ));
    
    if( val.castToBooleanSoft() )
    {
      n.f4.accept( this, argu );
    }
    else if( n.f5.present() )
    {
      ((NodeSequence)(n.f5.node)).elementAt(1).accept( this, argu );
    }
    
    return null;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "synchronized"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> EvaluationUnit()
   * </PRE>
   */
  public Object visit( SynchronizedStatement n, Object argu )
  {
    Value syncVal = (Value)(n.f2.accept( this, argu ));
    
    synchronized(syncVal.getMonitor())
    {
      n.f4.accept( this, argu );
    }
    
    return null;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "return"
   * f1 -> ( Expression() )?
   * </PRE>
   */
  public Object visit( ReturnStatement n, Object argu )
  {
    retVal = Value.UNDEFINED;    
    if( n.f1.present() )
      retVal =  (Value)(n.f1.node.accept( this, argu )) ;    
    if( retVal == null )
        retVal = Value.NULL;
    retVal = retVal.unhand();    
    throw RETURN;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "break"
   * f1 -> ";"
   * </PRE>
   */
  public Object visit( BreakStatement n, Object argu )
  {
    throw new Break();
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "continue"
   * f1 -> ";"
   * </PRE>
   */
  public Object visit( ContinueStatement n, Object argu )
  {
    throw new Continue();
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> AssignmentExpression()
   * f1 -> ( "," AssignmentExpression() )*
   * </PRE>
   */
  public Object visit( Expression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      NodeSequence seq = (NodeSequence)(n.f1.elementAt(i));
      
      val = (Value)(seq.elementAt(1).accept( this, argu ));
    }
    
    return val;
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "("
   * f1 -> ( FunctionCallExpressionListBody() )?
   * f2 -> ")"
   * </PRE>
   */
  public Object visit( FunctionCallExpressionList n, Object argu )
  {
    n.f0.accept( this, argu );          // to record last NodeToken
    
    if( n.f1.present() )
      return n.f1.node.accept( this, argu );
    else
      return new Value[0];
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> AssignmentExpression()
   * f1 -> ( "," AssignmentExpression() )*
   * </PRE>
   */
  public Object visit( FunctionCallExpressionListBody n, Object argu )
  {
    Value[] vals = new Value[1+n.f1.size()];
    
    vals[0] = ((Value)(n.f0.accept( this, argu ))).unhand();
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      NodeSequence seq = (NodeSequence)(n.f1.elementAt(i));
      
      vals[i+1] = ((Value)(seq.elementAt(1).accept( this, argu ))).unhand();
    }
    
    return vals;
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> ConditionalExpression()
   * f1 -> ( ( "=" | "+=" | "-=" | "*=" | "/=" | "%=" | "&gt;&gt;=" | "&lt;&lt;=" | "&gt;&gt;&gt;=" | "&=" | "^=" | "|=" ) ConditionalExpression() )*
   * </PRE>
   */
  public Object visit( AssignmentExpression n, Object argu )
  {
    Value lastVal = null;
    int   lastOp  = -1;
    
    for( int i=n.f1.size()-1; i>=-1; i-- )
    {
      Value val;
      int   op = lastOp; // the op this time through the loop was determined last time
      
      if( i >= 0 )
      {
        NodeSequence seq = (NodeSequence)(n.f1.elementAt(i));
        
        val = (Value)(seq.elementAt(1).accept( this, argu ));
        lastOp = ((NodeToken)(((NodeChoice)(seq.elementAt(0))).choice)).kind;
      }
      else
      {
        val = (Value)(n.f0.accept( this, argu ));
      }
      
      if( op != -1 )
      {
        switch(op)
        {
          case ASSIGN:
            // no-op
            break;
          case PLUSASSIGN:
            lastVal = val.bopPlus(lastVal);
            break;
          case MINUSASSIGN:
            lastVal = val.bopMinus(lastVal);
            break;
          case STARASSIGN:
            lastVal = val.bopMultiply(lastVal);
            break;
          case SLASHASSIGN:
            lastVal = val.bopDivide(lastVal);
            break;
          case ANDASSIGN:
            lastVal = val.bopBitwiseAnd(lastVal);
            break;
          case ORASSIGN:
            lastVal = val.bopBitwiseOr(lastVal);
            break;
          case XORASSIGN:
            lastVal = val.bopBitwiseXor(lastVal);
            break;
          case REMASSIGN:
            lastVal = val.bopRemainder(lastVal);
            break;
          case LSHIFTASSIGN:
            lastVal = val.bopLeftShift(lastVal);
            break;
          case RSIGNEDSHIFTASSIGN:
            lastVal = val.bopSignedRightShift(lastVal);
            break;
          case RUNSIGNEDSHIFTASSIGN:
            lastVal = val.bopUnsignedRightShift(lastVal);
            break;
          default:
            throw new RuntimeException("unknown operator: " + op);
        }
        
        val.opAssign(lastVal);
      }
      
      lastVal = val;
    }
    
    return lastVal;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> LogicalOrExpression()
   * f1 -> ( "?" LogicalOrExpression() ":" LogicalOrExpression() )?
   * </PRE>
   */
  public Object visit( ConditionalExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    if( n.f1.present() )
    {
      if( val.castToBooleanSoft() )
        val = (Value)(((NodeListInterface)n.f1.node).elementAt(1).accept( this, argu ));
      else
        val = (Value)(((NodeListInterface)n.f1.node).elementAt(3).accept( this, argu ));
    }
    
    return val;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> LogicalAndExpression()
   * f1 -> ( "||" LogicalAndExpression() )*
   * </PRE>
   */
  public Object visit( LogicalOrExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      if( val.castToBooleanSoft() )
        break;
      
      NodeSequence seq = (NodeSequence)(n.f1.elementAt(i));
      
      // evaluate rhs:
      Value val2 = (Value)(seq.elementAt(1).accept( this, argu ));
      
      val = val.bopLogicalOr(val2);
    }
    
    return val;
  }
 
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> BitwiseOrExpression()
   * f1 -> ( "&&" BitwiseOrExpression() )*
   * </PRE>
   */
  public Object visit( LogicalAndExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      if( ! val.castToBooleanSoft() )
        break;
      
      NodeSequence seq = (NodeSequence)(n.f1.elementAt(i));
      
      // evaluate rhs:
      Value val2 = (Value)(seq.elementAt(1).accept( this, argu ));
      
      val = val.bopLogicalAnd(val2);
    }
    
    return val;
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> BitwiseXorExpression()
   * f1 -> ( "|" BitwiseXorExpression() )*
   * </PRE>
   */
  public Object visit( BitwiseOrExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      Value val2 = (Value)((((NodeSequence)n.f1.elementAt(i))).elementAt(1).accept( this, argu ));
      
      val = val.bopBitwiseOr(val2);
    }
    
    return val;
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> BitwiseAndExpression()
   * f1 -> ( "^" BitwiseAndExpression() )*
   * </PRE>
   */
  public Object visit( BitwiseXorExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      Value val2 = (Value)((((NodeSequence)n.f1.elementAt(i))).elementAt(1).accept( this, argu ));
      
      val = val.bopBitwiseXor(val2);
    }
    
    return val;
  }
  

  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> EqualityExpression()
   * f1 -> ( "&" EqualityExpression() )*
   * </PRE>
   */
  public Object visit( BitwiseAndExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      Value val2 = (Value)((((NodeSequence)n.f1.elementAt(i))).elementAt(1).accept( this, argu ));
      
      val = val.bopBitwiseAnd(val2);
    }
    
    return val;
  }
  

  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> RelationalExpression()
   * f1 -> ( ( "==" | "!=" ) RelationalExpression() )*
   * </PRE>
   */
  public Object visit( EqualityExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      NodeSequence seq = (NodeSequence)(n.f1.elementAt(i));
      NodeToken    op  = (NodeToken)(((NodeChoice)(seq.elementAt(0))).choice);
      
      Value val2 = (Value)(seq.elementAt(1).accept( this, argu ));
      
      switch(op.kind)
      {
        case EQ:
          val = val.bopEquals(val2);
          break;
        case NE:
          val = val.bopNotEquals(val2);
          break;
        default:
          throw new RuntimeException("bad binary op: " + OscriptParser.getTokenString(op.kind));
      }
    }
    
    return val;
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> ShiftExpression()
   * f1 -> ( ( "&lt;" | "&gt;" | "&gt;=" | "&lt;=" | "instanceof" ) ShiftExpression() )*
   * </PRE>
   */
  public Object visit( RelationalExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      NodeSequence seq = (NodeSequence)(n.f1.elementAt(i));
      NodeToken    op  = (NodeToken)(((NodeChoice)(seq.elementAt(0))).choice);
      
      Value val2 = (Value)(seq.elementAt(1).accept( this, argu ));
      
      switch(op.kind)
      {
        case LT:
          val = val.bopLessThan(val2);
          break;
        case GT:
          val = val.bopGreaterThan(val2);
          break;
        case LE:
          val = val.bopLessThanOrEquals(val2);
          break;
        case GE:
          val = val.bopGreaterThanOrEquals(val2);
          break;
        case INSTANCEOF:
          val = val.bopInstanceOf(val2);
          break;
        default:
          throw new RuntimeException("bad binary op: " + OscriptParser.getTokenString(op.kind));
      }
    }
    
    return val;
  }

  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> AdditiveExpression()
   * f1 -> ( ( "&lt;&lt;" | "&gt;&gt;" | "&gt;&gt;&gt;" ) AdditiveExpression() )*
   * </PRE>
   */
  public Object visit( ShiftExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      NodeSequence seq = (NodeSequence)(n.f1.elementAt(i));
      NodeToken    op  = (NodeToken)(((NodeChoice)(seq.elementAt(0))).choice);
      
      Value val2 = (Value)(seq.elementAt(1).accept( this, argu ));
      
      switch(op.kind)
      {
        case LSHIFT:
          val = val.bopLeftShift(val2);
          break;
        case RSIGNEDSHIFT:
          val = val.bopSignedRightShift(val2);
          break;
        case RUNSIGNEDSHIFT:
          val = val.bopUnsignedRightShift(val2);
          break;
        default:
          throw new RuntimeException("bad binary op: " + OscriptParser.getTokenString(op.kind));
      }
    }
    
    return val;
  }

  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> MultiplicativeExpression()
   * f1 -> ( ( "+" | "-" ) MultiplicativeExpression() )*
   * </PRE>
   */
  public Object visit( AdditiveExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      NodeSequence seq = (NodeSequence)(n.f1.elementAt(i));
      NodeToken    op  = (NodeToken)(((NodeChoice)(seq.elementAt(0))).choice);
      
      Value val2 = (Value)(seq.elementAt(1).accept( this, argu ));
      
      switch(op.kind)
      {
        case PLUS:
          val = val.bopPlus(val2);
          break;
        case MINUS:
          val = val.bopMinus(val2);
          break;
        default:
          throw new RuntimeException("bad binary op: " + OscriptParser.getTokenString(op.kind));
      }
    }
    
    return val;
  }


  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> UnaryExpression()
   * f1 -> ( ( "*" | "/" | "%" ) UnaryExpression() )*
   * </PRE>
   */
  public Object visit( MultiplicativeExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      NodeSequence seq = (NodeSequence)(n.f1.elementAt(i));
      NodeToken    op  = (NodeToken)(((NodeChoice)(seq.elementAt(0))).choice);
      
      Value val2 = (Value)(seq.elementAt(1).accept( this, argu ));
      
      switch(op.kind)
      {
        case STAR:
          val = val.bopMultiply(val2);
          break;
        case SLASH:
          val = val.bopDivide(val2);
          break;
        case REM:
          val = val.bopRemainder(val2);
          break;
        default:
          throw new RuntimeException("bad binary op: " + OscriptParser.getTokenString(op.kind));
      }
    }
    
    return val;
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> ( ( "++" | "--" | "+" | "-" | "~" | "!" ) )?
   * f1 -> PostfixExpression()
   * </PRE>
   */
  public Object visit( UnaryExpression n, Object argu )
  {
    Value val = (Value)(n.f1.accept( this, argu ));
    
    if( n.f0.present() )
    {
      // get NodeToken_kind
      n.f0.node.accept( this, argu );
      
      switch(NodeToken_kind)
      {
        case INCR:
          val.opAssign( val.uopIncrement() );
          break;
        case DECR:
          val.opAssign( val.uopDecrement() );
          break;
        case PLUS:
          val = val.uopPlus();
          break;
        case MINUS:
          val = val.uopMinus();
          break;
        case TILDE:
          val = val.uopBitwiseNot();
          break;
        case BANG:
          val = val.uopLogicalNot();
          break;
        default:
          throw new RuntimeException("bad unary op: " + NodeToken_kind);
      }
    }
    
    return val;
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> TypeExpression()
   * f1 -> ( "++" | "--" )?
   * </PRE>
   */
  public Object visit( PostfixExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ));
    
    if( n.f1.present() )
    {
      // get NodeToken_kind
      n.f1.node.accept( this, argu );
      
      // save original value:
      Value origVal = val.unhand();
      
      // do op:
      switch(NodeToken_kind)
      {
        case INCR:
          val.opAssign( val.uopIncrement() );
          break;
        case DECR:
          val.opAssign( val.uopDecrement() );
          break;
        default:
          throw new RuntimeException("bad unary op: " + NodeToken_kind);
      }
      
      val = origVal;
    }
    
    return val;
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> AllocationExpression()
   *       | CastExpression()
   *       | PrimaryExpression()
   * </PRE>
   */
  public Object visit( TypeExpression n, Object argu )
  {
    return n.f0.accept( this, argu );
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "new"
   * f1 -> PrimaryExpressionWithTrailingFxnCallExpList()
   * f2 -> FunctionCallExpressionList()
   * </PRE>
   */
  public Object visit( AllocationExpression n, Object argu )
  {
    Value   val  = (Value)(n.f1.accept( this, argu ));
    Value[] args = (Value[])(n.f2.accept( this, argu ));
    
    return val.callAsConstructor(args);
  }

  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "("
   * f1 -> PrimaryExpressionNotFunction()
   * f2 -> ")"
   * f3 -> PrimaryExpression()
   * </PRE>
   */
  public Object visit( CastExpression n, Object argu )
  {
    return ((Value)(n.f1.accept( this, argu ))).bopCast(
      (Value)(n.f3.accept( this, argu ))
    );
  }

  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> PrimaryPrefix()
   * f1 -> ( PrimaryPostfix() )*
   * </PRE>
   */
  public Object visit( PrimaryExpression n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ) );
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      val = (Value)(n.f1.elementAt(i).accept( this, val ));
    }
    
    return val;
  }

  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> PrimaryPrefix()
   * f1 -> ( PrimaryPostfix() )*
   * </PRE>
   */
  public Object visit( PrimaryExpressionNotFunction n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ) );
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      val = (Value)(n.f1.elementAt(i).accept( this, val ));
    }
    
    return val;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> PrimaryPrefix()
   * f1 -> ( PrimaryPostfixWithTrailingFxnCallExpList() )*
   * </PRE>
   */
  public Object visit( PrimaryExpressionWithTrailingFxnCallExpList n, Object argu )
  {
    Value val = (Value)(n.f0.accept( this, argu ) );
    
    for( int i=0; i<n.f1.size(); i++ )
    {
      val = (Value)(n.f1.elementAt(i).accept( this, val ));
    }
    
    return val;
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> PrimaryPrefixNotFunction()
   *       | FunctionPrimaryPrefix()
   *       | ShorthandFunctionPrimaryPrefix()
   * </PRE>
   */
  public Object visit( PrimaryPrefix n, Object argu )
  {
    return n.f0.accept( this, argu );
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> ThisPrimaryPrefix()
   *       | SuperPrimaryPrefix()
   *       | CalleePrimaryPrefix()
   *       | IdentifierPrimaryPrefix()
   *       | ParenPrimaryPrefix()
   *       | ArrayDeclarationPrimaryPrefix()
   *       | Literal()
   * </PRE>
   */
  public Object visit( PrimaryPrefixNotFunction n, Object argu )
  {
    return n.f0.accept( this, argu );
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "this"
   * </PRE>
   */
  public Object visit( ThisPrimaryPrefix n, Object argu )
  {
    return thisScope.getThis();
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "super"
   * </PRE>
   */
  public Object visit( SuperPrimaryPrefix n, Object argu )
  {
    return thisScope.getSuper();
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "callee"
   * </PRE>
   */
  public Object visit( CalleePrimaryPrefix n, Object argu )
  {
    return thisScope.getCallee();
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> &lt;IDENTIFIER&gt;
   * </PRE>
   */
  public Object visit( IdentifierPrimaryPrefix n, Object argu )
  {
    return thisScope.lookupInScope( (Value)(n.f0.accept( this, argu )) );
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   * </PRE>
   */
  public Object visit( ParenPrimaryPrefix n, Object argu )
  {
    return n.f1.accept( this, argu );
  }
  
  
  /*=======================================================================*/
  private static final int[] EMPTY_ARG_NAMES = new int[0];
  /**
   * <PRE>
   * f0 -> "function"
   * f1 -> "("
   * f2 -> ( Arglist() )?
   * f3 -> ")"
   * f4 -> ( "extends" PrimaryExpressionWithTrailingFxnCallExpList() FunctionCallExpressionList() )?
   * f5 -> "{"
   * f6 -> Program()
   * f7 -> "}"
   * </PRE>
   */
  public Object visit( FunctionPrimaryPrefix n, Object argu )
  {
    if( n.fd == null )
    {
      Value  oname = Symbol.getSymbol( n.id );
      String  name = oname.castToString();
      int[]   argIds;
      boolean varargs;
      
      // just in case, to get the specials...
      n.f0.accept( this, argu );
      
      // get arglist:
      if( n.f2.present() )
      {
        n.f2.node.accept( this, argu );
        argIds  = Arglist_names;
        varargs = Arglist_varargs;
      }
      else
      {
        argIds  = EMPTY_ARG_NAMES;
        varargs = false;
      }
      
      // get extends evaluator:
      oscript.NodeEvaluator functionCallExpressionListEvaluator = null;
      
      if( n.f4.present() )
      {
        FunctionCallExpressionList functionCallExpressionList = 
          (FunctionCallExpressionList)(((NodeSequence)(n.f4.node)).elementAt(2));
        
        synchronized(functionCallExpressionList)
        {
          if( functionCallExpressionList.nodeEvaluator == null )
          {
            functionCallExpressionList.nodeEvaluator = 
              oscript.OscriptInterpreter.createNodeEvaluator( name + "$extends", functionCallExpressionList );
          }
        }
        
        functionCallExpressionListEvaluator = functionCallExpressionList.nodeEvaluator;
      }
      
      oscript.NodeEvaluator nodeEvaluator = OscriptInterpreter.createNodeEvaluator( name, n.f6 );
      
      {
        oscript.util.SymbolTable smit = nodeEvaluator.getSharedMemberIndexTable( oscript.NodeEvaluator.ALL );
        for( int i=0; i<argIds.length; i+=2 )
          smit.create( argIds[i] );
      }
      
      oscript.NodeEvaluator staticNodeEvaluator = null;
      if( n.f6.staticNodes != null )
        staticNodeEvaluator = OscriptInterpreter.createNodeEvaluator( name + "$static", n.f6.staticNodes );
      
      // the syntaxtree won't change, so we only need to parse the comment once:
      synchronized(n)
      {
        if( ! n.commentParsed )
        {
          n.commentParsed = true;
          if( NodeToken_lastSpecials != null )
            n.comment = Function.extractJavadocComment( NodeToken_lastSpecials, oname, argIds );
        }
      }
      
      n.fd = new Function.FunctionData( n.id,
                                        argIds,
                                        varargs,
                                        functionCallExpressionListEvaluator,
                                        nodeEvaluator,
                                        staticNodeEvaluator,
                                        n.hasVarInScope,
                                        n.hasFxnInScope,
                                        n.comment );
    }
    
    // get extends:
    Value superFxn = null;
    if( n.f4.present() )
    {
      Value val = (Value)(((NodeSequence)(n.f4.node)).elementAt(1).accept(this,argu));
      superFxn = val.unhand();
    }
    
    return new Function( thisScope, superFxn, n.fd );
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "'{"
   * f1 -> Program(true)
   * f2 -> "}"
   * </PRE>
   */
  public Object visit( ShorthandFunctionPrimaryPrefix n, Object argu )
  {
    return ShorthandFunctionPrimaryPrefixTranslator.translate(n).accept( this, argu );
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "["
   * f1 ->   (FunctionCallExpressionListBody())?
   * f2 -> "]"
   * </PRE>
   */
  public Object visit( ArrayDeclarationPrimaryPrefix n, Object argu )
  {
    Value[] vals;
    
    if( n.f1.present() )
      vals = (Value[])(n.f1.node.accept( this, argu ));
    else
      vals = new Value[0];
    
    return new OArray(vals);
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> FunctionCallPrimaryPostfix()
   *       | ArraySubscriptPrimaryPostfix()
   *       | ThisScopeQualifierPrimaryPostfix()
   *       | PropertyIdentifierPrimaryPostfix()
   * </PRE>
   */
  public Object visit( PrimaryPostfix n, Object argu )
  {
    return n.f0.accept( this, argu );
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> ArraySubscriptPrimaryPostfix()
   *       | ThisScopeQualifierPrimaryPostfix()
   *       | PropertyIdentifierPrimaryPostfix()
   * </PRE>
   */
  public Object visit( PrimaryPostfixWithTrailingFxnCallExpList n, Object argu )
  {
    return n.f0.accept( this, argu );
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> FunctionCallExpressionList()
   * </PRE>
   */
  public Object visit( FunctionCallPrimaryPostfix n, Object argu )
  {
    Value[] args = (Value[])(n.f0.accept( this, argu ));
    
    return ((Value)argu).callAsFunction(args);
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "["
   * f1 -> Expression()
   * f2 -> ( ".." Expression() )?
   * f3 -> "]"
   * </PRE>
   */
  public Object visit( ArraySubscriptPrimaryPostfix n, Object argu )
  {
    Value idx1 = (Value)(n.f1.accept( this, argu ));
    
    if( n.f2.present() )
    {
      Value idx2 = (Value)(((NodeSequence)(n.f2.node)).elementAt(1).accept( this, argu ));
      return ((Value)argu).elementsAt( idx1, idx2 );
    }
    else
    {
      return ((Value)argu).elementAt(idx1);
    }
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "."
   * f1 -> &lt;IDENTIFIER&gt;
   * </PRE>
   */
  public Object visit( PropertyIdentifierPrimaryPostfix n, Object argu )
  {
    n.f0.accept( this, argu );          // to record last NodeToken
    return ((Value)argu).getMember( (Value)(n.f1.accept( this, argu )) );
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> "."
   * f1 -> "this"
   * </PRE>
   */
  public Object visit( ThisScopeQualifierPrimaryPostfix n, Object argu )
  {
    n.f0.accept( this, argu );          // to record last NodeToken
    return thisScope.getThis( (Value)argu );
  }
  
  /*=======================================================================*/
  /**
   * <PRE>
   * f0 -> &lt;INTEGER_LITERAL&gt;
   *       | &lt;FLOATING_POINT_LITERAL&gt;
   *       | &lt;STRING_LITERAL&gt;
   *       | &lt;REGEXP_LITERAL&gt;
   *       | "true"
   *       | "false"
   *       | "null"
   *       | "undefined"
   * </PRE>
   */
  public Object visit( Literal n, Object argu )
  {
    return n.f0.choice.accept( this, argu );
  }
  
  
  /*=======================================================================*/
  /**
   * <PRE>
    * f0 -> ( "static" | "const" | "private" | "protected" | "public" )*
   * </PRE>
   */
  public Object visit( Permissions n, Object argu )
  {
    Permissions_attr = getPermissions( n, Reference.ATTR_PROTECTED );
    return null;
  }
  
  /**
   * Get the permissions mask...
   * 
   * @param n            the permissions syntaxtree node
   * @param attr         the default permissions value
   * @return the permissions mask
   */
  private int getPermissions( Permissions n, int attr )
  {
    for( int i=0; i<n.f0.size(); i++ )
    {
      n.f0.elementAt(i).accept( this, null );
      
      switch(NodeToken_kind)
      {
        case PRIVATE:
          attr = (attr & 0xf0) | Reference.ATTR_PRIVATE;
          break;
        case PROTECTED:
          attr = (attr & 0xf0) | Reference.ATTR_PROTECTED;
          break;
        case PUBLIC:
          attr = (attr & 0xf0) | Reference.ATTR_PUBLIC;
          break;
        case STATIC:
          attr |= Reference.ATTR_STATIC;
          break;
        case CONST:
          attr |= Reference.ATTR_CONST;
          break;
        default:
          throw new RuntimeException("bad kind: " + OscriptParser.getTokenString(NodeToken_lastToken.kind));
      }
    }
    
    return attr;
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

