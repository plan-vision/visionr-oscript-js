package oscript.data;

import oscript.exceptions.PackagedScriptObjectException;
import oscript.util.MemberTable;
import oscript.util.StackFrame;


/**
 * Wrapper for reference to temp value.
 *
 * @author  plan-vision
 * @version $LastChangedRevision: 14753 $
 * @date 	$LastChangedDate: 2013-03-08 13:54:09 +0100 (Fr, 08 Mrz 2013) $
 * @project VisionR Server 
 */
public abstract class ValueWrapperTempReference extends Value {
	
	public String toString() {
		return get().toString();
	}
	
  public ValueWrapperTempReference()
  {
	  super();
  }

  public abstract Value get();/* {
	  return get();
  }*/
  //--------------------------------------------------------------------------------------------------------------
 
  public int hashCode()
  {
    return get().hashCode();
  }
  
  public boolean equals( Object obj )
  {
    return get().equals(obj);
  }
  
  public Value unhand()
  {
    return this;
  }
  
  
  public Object getMonitor()
  {
    return get().getMonitor();
  }
  
  public boolean isA( Value type )
  {
    return get().isA(type);
  }
  
  public boolean castToBoolean()
    throws PackagedScriptObjectException
  {
    return get().castToBoolean();
  }

  public boolean castToBooleanSoft() throws PackagedScriptObjectException
  {
	  return get().castToBooleanSoft();
  }

  public String castToString()
    throws PackagedScriptObjectException
  {
    return get().castToString();
  }
  
  public long castToExactNumber()
    throws PackagedScriptObjectException
  {
    return get().castToExactNumber();
  }
  
  public double castToInexactNumber()
    throws PackagedScriptObjectException
  {
    return get().castToInexactNumber();
  }

  abstract public Value castToSimpleValue();
  
  public Object castToJavaObject()
    throws PackagedScriptObjectException
  {
    return get().castToJavaObject();
  }
  
  public Value bopCast( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopCast(val);
  }
  public Value bopCastR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopCastR( val, e );
  }

  public Value bopInstanceOf( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopInstanceOf(val);
  }
  public Value bopInstanceOfR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopInstanceOfR( val, e );
  }
  
  public Value bopLogicalOr( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopLogicalOr(val);
  }
  public Value bopLogicalOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopLogicalOrR( val, e );
  }
  
  public Value bopLogicalAnd( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopLogicalAnd(val);
  }
  public Value bopLogicalAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopLogicalAndR( val, e );
  }
  
  public Value bopBitwiseOr( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopBitwiseOr(val);
  }
  public Value bopBitwiseOrR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopBitwiseOrR( val, e );
  }
  
  public Value bopBitwiseXor( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopBitwiseXor(val);
  }
  public Value bopBitwiseXorR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopBitwiseXorR( val, e );
  }
 
  public Value bopBitwiseAnd( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopBitwiseAnd(val);
  }
  public Value bopBitwiseAndR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopBitwiseAndR( val, e );
  }
  
  public Value bopEquals( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopEquals(val);
  }
  public Value bopEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopEqualsR( val, e );
  }
  
  public Value bopNotEquals( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopNotEquals(val);
  }
  public Value bopNotEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopNotEqualsR( val, e );
  }
  
  public Value bopLessThan( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopLessThan(val);
  }
  public Value bopLessThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopLessThanR( val, e );
  }
  
  public Value bopGreaterThan( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopGreaterThan(val);
  }
  public Value bopGreaterThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopGreaterThanR( val, e );
  }
  
  public Value bopLessThanOrEquals( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopLessThanOrEquals(val);
  }
  public Value bopLessThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopLessThanOrEqualsR( val, e );
  }
  
  public Value bopGreaterThanOrEquals( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopGreaterThanOrEquals(val);
  }
  public Value bopGreaterThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopGreaterThanOrEqualsR( val, e );
  }
  
  public Value bopLeftShift( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopLeftShift(val);
  }
  public Value bopLeftShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopLeftShiftR( val, e );
  }
  
  public Value bopSignedRightShift( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopSignedRightShift(val);
  }
  public Value bopSignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopSignedRightShiftR( val, e );
  }
 
  public Value bopUnsignedRightShift( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopUnsignedRightShift(val);
  }
  public Value bopUnsignedRightShiftR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopUnsignedRightShiftR( val, e );
  }
  
  public Value bopPlus( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopPlus(val);
  }
  public Value bopPlusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopPlusR( val, e );
  }
  
  public Value bopMinus( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopMinus(val);
  }
  public Value bopMinusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopMinusR( val, e );
  }
  
  public Value bopMultiply( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopMultiply(val);
  }
  public Value bopMultiplyR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopMultiplyR( val, e );
  }
  
  
  public Value bopDivide( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopDivide(val);
  }
  public Value bopDivideR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopDivideR( val, e );
  }
  
  
  public Value bopRemainder( Value val )
    throws PackagedScriptObjectException
  {
    return get().bopRemainder(val);
  }
  public Value bopRemainderR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return get().bopRemainderR( val, e );
  }
  
  
  public Value uopIncrement()
    throws PackagedScriptObjectException
  {
    return get().uopIncrement();
  }
  
  
  public Value uopDecrement()
    throws PackagedScriptObjectException
  {
    return get().uopDecrement();
  }
 
  public Value uopPlus()
    throws PackagedScriptObjectException
  {
    return get().uopPlus();
  }
  
  
  public Value uopMinus()
    throws PackagedScriptObjectException
  {
    return get().uopMinus();
  }
  
  
  public Value uopBitwiseNot()
    throws PackagedScriptObjectException
  {
    return get().uopBitwiseNot();
  }
  
  
  public Value uopLogicalNot()
    throws PackagedScriptObjectException
  {
    return get().uopLogicalNot();
  }
 public Value callAsFunction( StackFrame sf, MemberTable args )
    throws PackagedScriptObjectException
  {
    return get().callAsFunction( sf, args );
  }
  public Value callAsConstructor( StackFrame sf, MemberTable args )
    throws PackagedScriptObjectException
  {
    return get().callAsConstructor( sf, args );
  }
  
  public Value callAsExtends( StackFrame sf, Scope scope, MemberTable args )
    throws PackagedScriptObjectException
  {
    return get().callAsExtends( sf, scope, args );
  }
  
  public int length()
    throws PackagedScriptObjectException
  {
    return get().length();
  }
  
 
  public Value elementAt( Value idx )
    throws PackagedScriptObjectException
  {
    return get().elementAt(idx);
  }
  
  
  public Value elementsAt( Value idx1, Value idx2 )
    throws PackagedScriptObjectException
  {
    return get().elementsAt( idx1, idx2 );
  }
  
  public final java.util.Set memberSet()
  {
    return get().memberSet();
  }

  @Override

  public Value getType()
  {
    return this;
  }
  
 
  protected Value getTypeImpl() {
	  return this;
  }
  
  public Value getMember( int id, boolean exception ) {
	Value val = get();
	if (val != null && val != Value.NULL)
		return val.getMember(id,exception);
	return super.getMember( id,exception);
  }
 
}





