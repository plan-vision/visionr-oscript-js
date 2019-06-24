/*=============================================================================
 *     Copyright Texas Instruments 2002. All Rights Reserved.
 */

package oscript.visitor;
import oscript.syntaxtree.*;

import java.util.*;
import oscript.data.Value;
import oscript.data.JavaBridge;

/**
 * <CLASS_COMMENT>
 */
public class ExtensibleVisitor
  extends ObjectDepthFirst
{

  /**
   * table mapping node type to script visitor function
   */
  private Hashtable visitorTable = new Hashtable();


  /**
   * register node visitor
   */
  public Value register( Class nodeType, Value fxn )
  {
    return (Value)(visitorTable.put( nodeType, fxn ));
  }


  /**
   * get node visitor
   */
  public Value get( Class nodeType )
  {
    return (Value)(visitorTable.get(nodeType));
  }


  /**
   * remove node visitor
   */
  public Value remove( Class nodeType )
  {
    return (Value)(visitorTable.remove(nodeType));
  }

  
  
  
  
  
  

  /**
   * handles node tokens
   */
  public Object visit( NodeToken n, Object argu )
  {
    Value val = get( NodeToken.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> ( &lt;UNIX_SELF_EXECUTABLE_COMMENT&gt; )?
   * f1 -> Program(false)
   * f2 -> &lt;EOF&gt;
   * </PRE>
   */
  public Object visit( ProgramFile n, Object argu )
  {
    Value val = get( ProgramFile.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> ( EvaluationUnit() )*
   * </PRE>
   */
  public Object visit( Program n, Object argu )
  {
    Value val = get( Program.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> ScopeBlock()
   *       | VariableDeclarationBlock()
   *       | FunctionDeclaration()
   *       | TryStatement()
   *       | ForLoopStatement()
   *       | CollectionForLoopStatement()
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
    Value val = get( EvaluationUnit.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "{"
   * f1 -> Program(false)
   * f2 -> "}"
   * </PRE>
   */
  public Object visit( ScopeBlock n, Object argu )
  {
    Value val = get( ScopeBlock.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> VariableDeclaration()
   * f1 -> ";"
   * </PRE>
   */
  public Object visit( VariableDeclarationBlock n, Object argu )
  {
    Value val = get( VariableDeclarationBlock.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> Expression()
   * f1 -> ";"
   * </PRE>
   */
  public Object visit( ExpressionBlock n, Object argu )
  {
    Value val = get( ExpressionBlock.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "throw"
   * f1 -> Expression()
   * f2 -> ";"
   * </PRE>
   */
  public Object visit( ThrowBlock n, Object argu )
  {
    Value val = get( ThrowBlock.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>

   * f0 -> "import"
   * f1 -> Expression()
   * f2 -> ";"
   * </PRE>
   */
  public Object visit( ImportBlock n, Object argu )
  {
    Value val = get( ImportBlock.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>

   * f0 -> "mixin"
   * f1 -> Expression()
   * f2 -> ";"
   * </PRE>
   */
  public Object visit( MixinBlock n, Object argu )
  {
    Value val = get( MixinBlock.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>

   * f0 -> "eval"
   * f1 -> Expression()
   * f2 -> ";"
   * </PRE>
   */
  public Object visit( EvalBlock n, Object argu )
  {
    Value val = get( EvalBlock.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>

   * f0 -> Permissions(true)
   * f1 -> "var"
   * f2 -> &lt;IDENTIFIER&gt;
   * f3 -> ( "=" Expression() )?
   * </PRE>
   */
  public Object visit( VariableDeclaration n, Object argu )
  {
    Value val = get( VariableDeclaration.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>

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
  public Object visit( FunctionDeclaration n, Object argu )
  {
    Value val = get( FunctionDeclaration.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> Permissions(false)
   * f1 -> &lt;IDENTIFIER&gt;
   * f2 -> ( "," Permissions(false) &lt;IDENTIFIER&gt; )*
   * f3 -> ( "..." )?
   * </PRE>
   */
  public Object visit( Arglist n, Object argu )
  {
    Value val = get( Arglist.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "try"
   * f1 -> EvaluationUnit()
   * f2 -> ( "catch" "(" Expression() &lt;IDENTIFIER&gt; ")" EvaluationUnit() )*
   * f3 -> ( "catch" "(" &lt;IDENTIFIER&gt; ")" EvaluationUnit() )?
   * f4 -> ( "finally" EvaluationUnit() )?
   * </PRE>
   */
  public Object visit( TryStatement n, Object argu )
  {
    Value val = get( TryStatement.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>

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
  public Object visit( ForLoopStatement n, Object argu )
  {
    Value val = get( ForLoopStatement.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>

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
    Value val = get( CollectionForLoopStatement.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> VariableDeclaration()
   *       | Expression()
   * </PRE>
   */
  public Object visit( PreLoopStatement n, Object argu )
  {
    Value val = get( PreLoopStatement.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "while"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> EvaluationUnit()
   * </PRE>
   */
  public Object visit( WhileLoopStatement n, Object argu )
  {
    Value val = get( WhileLoopStatement.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
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
    Value val = get( ConditionalStatement.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "synchronized"
   * f1 -> "("
   * f2 -> Expression()
   * f3 -> ")"
   * f4 -> EvaluationUnit()
   * </PRE>
   */
  public Object visit( SynchronizedStatement n, Object argu )
  {
    Value val = get( SynchronizedStatement.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "return"
   * f1 -> ( Expression() )?
   * f2 -> ";"
   * </PRE>
   */
  public Object visit( ReturnStatement n, Object argu )
  {
    Value val = get( ReturnStatement.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "break"
   * f1 -> ";"
   * </PRE>
   */
  public Object visit( BreakStatement n, Object argu )
  {
    Value val = get( BreakStatement.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "continue"
   * f1 -> ";"
   * </PRE>
   */
  public Object visit( ContinueStatement n, Object argu )
  {
    Value val = get( ContinueStatement.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> AssignmentExpression()
   * f1 -> ( "," AssignmentExpression() )*
   * </PRE>
   */
  public Object visit( Expression n, Object argu )
  {
    Value val = get( Expression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "("
   * f1 -> ( FunctionCallExpressionListBody() )?
   * f2 -> ")"
   * </PRE>
   */
  public Object visit( FunctionCallExpressionList n, Object argu )
  {
    Value val = get( FunctionCallExpressionList.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> AssignmentExpression()
   * f1 -> ( "," AssignmentExpression() )*
   * </PRE>
   */
  public Object visit( FunctionCallExpressionListBody n, Object argu )
  {
    Value val = get( FunctionCallExpressionListBody.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> ConditionalExpression()
   * f1 -> ( ( "=" | "+=" | "-=" | "*=" | "/=" | "%=" | "&gt;&gt;=" | "&lt;&lt;=" | "&gt;&gt;&gt;=" | "&=" | "^=" | "|=" ) ConditionalExpression() )*
   * </PRE>
   */
  public Object visit( AssignmentExpression n, Object argu )
  {
    Value val = get( AssignmentExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> LogicalOrExpression()
   * f1 -> ( "?" LogicalOrExpression() ":" LogicalOrExpression() )?
   * </PRE>
   */
  public Object visit( ConditionalExpression n, Object argu )
  {
    Value val = get( ConditionalExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> LogicalAndExpression()
   * f1 -> ( "||" LogicalAndExpression() )*
   * </PRE>
   */
  public Object visit( LogicalOrExpression n, Object argu )
  {
    Value val = get( LogicalOrExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> BitwiseOrExpression()
   * f1 -> ( "&&" BitwiseOrExpression() )*
   * </PRE>
   */
  public Object visit( LogicalAndExpression n, Object argu )
  {
    Value val = get( LogicalAndExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> BitwiseXorExpression()
   * f1 -> ( "|" BitwiseXorExpression() )*
   * </PRE>
   */
  public Object visit( BitwiseOrExpression n, Object argu )
  {
    Value val = get( BitwiseOrExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> BitwiseAndExpression()
   * f1 -> ( "^" BitwiseAndExpression() )*
   * </PRE>
   */
  public Object visit( BitwiseXorExpression n, Object argu )
  {
    Value val = get( BitwiseXorExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> EqualityExpression()
   * f1 -> ( "&" EqualityExpression() )*
   * </PRE>
   */
  public Object visit( BitwiseAndExpression n, Object argu )
  {
    Value val = get( BitwiseAndExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> RelationalExpression()
   * f1 -> ( ( "==" | "!=" ) RelationalExpression() )*
   * </PRE>
   */
  public Object visit( EqualityExpression n, Object argu )
  {
    Value val = get( EqualityExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> ShiftExpression()
   * f1 -> ( ( "&lt;" | "&gt;" | "&gt;=" | "&lt;=" | "instanceof" ) ShiftExpression() )*
   * </PRE>
   */
  public Object visit( RelationalExpression n, Object argu )
  {
    Value val = get( RelationalExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> AdditiveExpression()
   * f1 -> ( ( "&lt;&lt;" | "&gt;&gt;" | "&gt;&gt;&gt;" ) AdditiveExpression() )*
   * </PRE>
   */
  public Object visit( ShiftExpression n, Object argu )
  {
    Value val = get( ShiftExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> MultiplicativeExpression()
   * f1 -> ( ( "+" | "-" ) MultiplicativeExpression() )*
   * </PRE>
   */
  public Object visit( AdditiveExpression n, Object argu )
  {
    Value val = get( AdditiveExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> UnaryExpression()
   * f1 -> ( ( "*" | "/" | "%" ) UnaryExpression() )*
   * </PRE>
   */
  public Object visit( MultiplicativeExpression n, Object argu )
  {
    Value val = get( MultiplicativeExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> ( ( "++" | "--" | "+" | "-" | "~" | "!" ) )?
   * f1 -> PostfixExpression()
   * </PRE>
   */
  public Object visit( UnaryExpression n, Object argu )
  {
    Value val = get( UnaryExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> TypeExpression()
   * f1 -> ( "++" | "--" )?
   * </PRE>
   */
  public Object visit( PostfixExpression n, Object argu )
  {
    Value val = get( PostfixExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> AllocationExpression()
   *       | CastExpression()
   *       | PrimaryExpression()
   * </PRE>
   */
  public Object visit( TypeExpression n, Object argu )
  {
    Value val = get( TypeExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "new"
   * f1 -> PrimaryExpressionWithTrailingFxnCallExpList()
   * f2 -> FunctionCallExpressionList()
   * </PRE>
   */
  public Object visit( AllocationExpression n, Object argu )
  {
    Value val = get( AllocationExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "("
   * f1 -> PrimaryExpressionNotFunction()
   * f2 -> ")"
   * f3 -> PrimaryExpression()
   * </PRE>
   */
  public Object visit( CastExpression n, Object argu )
  {
    Value val = get( CastExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> PrimaryPrefix()
   * f1 -> ( PrimaryPostfix() )*
   * </PRE>
   */
  public Object visit( PrimaryExpression n, Object argu )
  {
    Value val = get( PrimaryExpression.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> PrimaryPrefixNotFunction()
   * f1 -> ( PrimaryPostfix() )*
   * </PRE>
   */
  public Object visit( PrimaryExpressionNotFunction n, Object argu )
  {
    Value val = get( PrimaryExpressionNotFunction.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> PrimaryPrefix()
   * f1 -> ( PrimaryPostfixWithTrailingFxnCallExpList() )*
   * </PRE>
   */
  public Object visit( PrimaryExpressionWithTrailingFxnCallExpList n, Object argu )
  {
    Value val = get( PrimaryExpressionWithTrailingFxnCallExpList.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> PrimaryPrefixNotFunction()
   *       | FunctionPrimaryPrefix()
   *       | ShorthandFunctionPrimaryPrefix()
   * </PRE>
   */
  public Object visit( PrimaryPrefix n, Object argu )
  {
    Value val = get( PrimaryPrefix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> ThisPrimaryPrefix()
   *       | SuperPrimaryPrefix()
   *       | CalleePrimaryPrefix()
   *       | ArrayDeclarationPrimaryPrefix()
   *       | IdentifierPrimaryPrefix()
   *       | ParenPrimaryPrefix()
   *       | Literal()
   * </PRE>
   */
  public Object visit( PrimaryPrefixNotFunction n, Object argu )
  {
    Value val = get( PrimaryPrefixNotFunction.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "this"
   * </PRE>
   */
  public Object visit( ThisPrimaryPrefix n, Object argu )
  {
    Value val = get( ThisPrimaryPrefix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "super"
   * </PRE>
   */
  public Object visit( SuperPrimaryPrefix n, Object argu )
  {
    Value val = get( SuperPrimaryPrefix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>

   * f0 -> "callee"
   * </PRE>
   */
  public Object visit( CalleePrimaryPrefix n, Object argu )
  {
    Value val = get( CalleePrimaryPrefix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> &lt;IDENTIFIER&gt;
   * </PRE>
   */
  public Object visit( IdentifierPrimaryPrefix n, Object argu )
  {
    Value val = get( IdentifierPrimaryPrefix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "("
   * f1 -> Expression()
   * f2 -> ")"
   * </PRE>
   */
  public Object visit( ParenPrimaryPrefix n, Object argu )
  {
    Value val = get( ParenPrimaryPrefix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>

   * f0 -> "function"
   * f1 -> "("
   * f2 -> ( Arglist() )?
   * f3 -> ")"
   * f4 -> ( "extends" PrimaryExpressionWithTrailingFxnCallExpList() FunctionCallExpressionList() )?
   * f5 -> "{"
   * f6 -> Program(true)
   * f7 -> "}"
   * </PRE>
   */
  public Object visit( FunctionPrimaryPrefix n, Object argu )
  {
    Value val = get( FunctionPrimaryPrefix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "'{"
   * f1 -> Program(true)
   * f2 -> "}"
   * </PRE>
   */
  public Object visit( ShorthandFunctionPrimaryPrefix n, Object argu )
  {
    Value val = get( ShorthandFunctionPrimaryPrefix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "["
   * f1 -> ( FunctionCallExpressionListBody() )?
   * f2 -> "]"
   * </PRE>
   */
  public Object visit( ArrayDeclarationPrimaryPrefix n, Object argu )
  {
    Value val = get( ArrayDeclarationPrimaryPrefix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> FunctionCallPrimaryPostfix()
   *       | ArraySubscriptPrimaryPostfix()
   *       | ThisScopeQualifierPrimaryPostfix()
   *       | PropertyIdentifierPrimaryPostfix()
   * </PRE>
   */
  public Object visit( PrimaryPostfix n, Object argu )
  {
    Value val = get( PrimaryPostfix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> ArraySubscriptPrimaryPostfix()
   *       | ThisScopeQualifierPrimaryPostfix()
   *       | PropertyIdentifierPrimaryPostfix()
   * </PRE>
   */
  public Object visit( PrimaryPostfixWithTrailingFxnCallExpList n, Object argu )
  {
    Value val = get( PrimaryPostfixWithTrailingFxnCallExpList.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> FunctionCallExpressionList()
   * </PRE>
   */
  public Object visit( FunctionCallPrimaryPostfix n, Object argu )
  {
    Value val = get( FunctionCallPrimaryPostfix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "["
   * f1 -> Expression()
   * f2 -> ( ".." Expression() )?
   * f3 -> "]"
   * </PRE>
   */
  public Object visit( ArraySubscriptPrimaryPostfix n, Object argu )
  {
    Value val = get( ArraySubscriptPrimaryPostfix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "."
   * f1 -> &lt;IDENTIFIER&gt;
   * </PRE>
   */
  public Object visit( PropertyIdentifierPrimaryPostfix n, Object argu )
  {
    Value val = get( PropertyIdentifierPrimaryPostfix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>
   * f0 -> "."
   * f1 -> "this"
   * </PRE>
   */
  public Object visit( ThisScopeQualifierPrimaryPostfix n, Object argu )
  {
    Value val = get( ThisScopeQualifierPrimaryPostfix.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }

  /**
   *   <PRE>
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
    Value val = get( Literal.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }


  /**
   *   <PRE>

   * f0 -> ( "static" | "const" | "private" | "protected" | "public" )*
   * </PRE>
   */
  public Object visit( Permissions n, Object argu )
  {
    Value val = get( Permissions.class );
    if( val != null )
      return val.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(n), JavaBridge.convertToScriptObject(argu) } );
    return super.visit( n, argu );
  }

}
