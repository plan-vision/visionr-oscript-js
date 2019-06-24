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


package oscript.data;

import oscript.exceptions.*;
import oscript.util.StackFrame;
import oscript.util.MemberTable;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.LinkedList;

import java.util.Iterator;


/**
 * Utilities to convert between script and java types.
 * 
 * @author Rob Clark (rob@ti.com)
 * <!--$Format: " * @version $Revision$"$-->
 * @version 1.29
 */
public class JavaBridge
{
  private static LinkedList functionTransformList = new LinkedList();
  
  /*=======================================================================*/
  /**
   * This abstract class is implemented by transformers that understand how
   * to transform a script object (function) to a certain type of java class.
   * For example, this can be used to register a tranformer that can
   * make a wrapper that implements Runnable, or ActionListener.  This way
   * script code can simply pass a script function to java code that
   * expects to take a, for example, ActionListener.
   */
  public static abstract class FunctionTransformer
  {
    private Class targetClass;
    
    /**
     * Class Constructor
     * 
     * @param targetClass   the class to tranfrom script object to
     */
    public FunctionTransformer( Class targetClass )
    {
      this.targetClass = targetClass;
    }
    
    /**
     * Get the type of the class that this tranformer understands how
     * to transform to.
     */
    public Class getTargetClass()
    {
      return targetClass;
    }
    
    /**
     * Perform the transform, and return a java object that is an
     * instance of the class returned by {@link #getTargetClass}.
     */
    public abstract Object transform( Value fxn );
  }
  
  /**
   */
  public static void registerFunctionTransformer( FunctionTransformer ft )
  {
    functionTransformList.add(ft);
  }
  
  
  /* These should be split out somewhere else... but for now...
   */
  static {
    
    registerFunctionTransformer( new FunctionTransformer(Runnable.class) {
        
        public Object transform( final Value fxn )
        {
          return new Runnable() {
              
              public void run()
              {
                fxn.callAsFunction( new Value[0] );
              }
              
            };
        }
        
      } );
    
    registerFunctionTransformer( new FunctionTransformer(java.awt.event.ActionListener.class) {
        
        public Object transform( final Value fxn )
        {
          return new java.awt.event.ActionListener() {
              
              public void actionPerformed( java.awt.event.ActionEvent evt )
              {
                fxn.callAsFunction( new Value[] { JavaBridge.convertToScriptObject(evt) } );
              }
              
            };
        }
        
      } );
  }
  
  /* 
   * A better idea for converting args:
   * 
   *   if( parameterTypes[i].isArray() )
   *   {
   *     ... handle converting array
   *   }
   *   else if( parameterTypes[i].isPrimitive() )
   *   {
   *     ... handle primitive conversion:
   *           Long    -> long, int, short, byte
   *           Double  -> double, float
   *           Boolean -> boolean
   *           String  -> char
   *   }
   *   else if( parameterTypes[i].isAssignableFrom(args[i].getClass()) )
   *   {
   *     ...
   *   }
   *   else if( parameterTypes[i].isAssignableFrom(args[i].castToJavaObject().getClass()) )
   *   {
   *     ...
   *   }
   *   else
   *   {
   *     for( Iterator itr=functionTransformList.iterator(); itr.hasNext(); )
   *     {
   *       if( parameterTypes[i].isAssignableFrom( itr.next().getTargetClass() ) )
   *       {
   *         ...
   *         return/break/???
   *       }
   *     }
   *     
   *     ... can't convert
   *   }
   *   
   */
  
  /*=======================================================================*/
  /**
   * This is used by java class wrappers to convert the return type back
   * to a java type:
   */
  public static Object convertToJavaObject( Value scriptObj, String javaTypeStr )
  {
	  // TODO 
      throw new RuntimeException("class not found: " + javaTypeStr);
  }
  public static Object convertToJavaObject( Value scriptObj, Class cls )
  {
    Object[] javaArgs = new Object[1];
    
    if( convertArgs( new Class[] { cls },
                     javaArgs,
                     new OArray( new Value[] { scriptObj } ) ) > 0 )
    {
      // conversion possible
      return javaArgs[0];
    }
    
    // conversion not possible:
    throw PackagedScriptObjectException.makeExceptionWrapper(
      new OUnsupportedOperationException("cannot convert to: " + cls.getName())
    );
  }
  
  /*=======================================================================*/
  
  /**
   * Abstracts {@link Method} and {@link Constructor} differences
   */
  public interface JavaCallableAccessor
  {
    Class[] getParameterTypes( Object javaCallable );
    Object call( Object javaCallable, Object javaObject, Object[] args )
      throws InvocationTargetException, InstantiationException, IllegalAccessException;
  }
  
  /**
   * Since choosing the correct method to call, and correct constructor to
   * call, uses the same algorithm, instead of duplicating the logic in two
   * places, it is handled by this method.  Having it in one place also
   * makes it easier to explore optimizations in the future.
   * 
   * @param accessor
   * @param id         the symbol (name) of the method/constructor
   * @param javaObject the java object, to pass to {@link JavaCallableAccessor#call}
   * @param javaCallables   the candidate methods/constructors
   * @param sf         the current stack frame
   * @param args       the args
   * @return the return value of {@link JavaCallableAccessor#call}
   */
  public static final Object call( 
          JavaCallableAccessor accessor, int id, Object javaObject, 
          Object[] javaCallables, StackFrame sf, MemberTable args )
  {
    int alen = (args == null) ? 0 : args.length();
    Object   bestCallable = null;
    int      bestJavaArgsScore = 0;
    Object[] bestJavaArgs = null;
    Object[] javaArgs = null;
    
    for( int i=0; i<javaCallables.length; i++ )
    {
      Class[] parameterTypes = accessor.getParameterTypes( javaCallables[i] );
      
      if( parameterTypes.length == alen )
      {
        if( javaArgs == null )
          javaArgs = new Object[alen];
        
        int javaArgsScore = JavaBridge.convertArgs( parameterTypes, javaArgs, args );
        
        if( javaArgsScore > bestJavaArgsScore )
        {
          bestJavaArgs      = javaArgs;
          bestJavaArgsScore = javaArgsScore;
          bestCallable      = javaCallables[i];
          
          javaArgs = null;
        }
      }
    }
    
    if( bestCallable != null )
    {
      try
      {
        return accessor.call( bestCallable, javaObject, bestJavaArgs );
      }
      catch(InvocationTargetException e)
      {
        Throwable t = e.getTargetException();
        
        if( Value.DEBUG )
          t.printStackTrace();
        
        throw OJavaException.convertException(t);
      }
      catch(Throwable e)         // XXX
      {
        if( Value.DEBUG )
          e.printStackTrace();
        
        throw OJavaException.convertException(e);
      }
    }
    else
    {
      /* if we get here, we didn't find a callable with the
       * correct number of args:
       */
      LinkedList candidateList = new LinkedList();
      
      for( int i=0; i<javaCallables.length; i++ )
      {
        Class[] parameterTypes = accessor.getParameterTypes( javaCallables[i] );
        
        if( parameterTypes.length == alen )
          candidateList.add(parameterTypes);
      }
      
      Value name = Symbol.getSymbol(id);
      
      if( candidateList.size() == 0 )
      {
        throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
      }
      else
      {
        String msg = "wrong arg types!  Possible candidates:\n";
        for( Iterator itr=candidateList.iterator(); itr.hasNext(); )
        {
          Class[] parameterTypes = (Class[])(itr.next());
          msg += "  " + name.castToString() + "(";
          
          for( int i=0; i<parameterTypes.length; i++ )
          {
            if( i != 0 )
              msg += ", ";
            msg += parameterTypes[i].getName();
          }
          
          msg += ") (" + JavaBridge.convertArgs( parameterTypes, new Object[alen], args ) + ")\n";
        }
        
        throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException(msg) );
      }
    }
  }
  
  /*=======================================================================*/
  /**
   * Utility to convert args to javaArgs of the types specified by
   * parameterTypes.  Each array should be of the same length.  This
   * will return a score of the conversion.  A score of less than or
   * equal to zero indicates that the conversion is not possible.  A
   * higher score is better.
   */
  public static int convertArgs( Class[] parameterTypes, Object[] javaArgs, MemberTable args )
  {
    int score = Integer.MAX_VALUE;
    if( (args == null) || (args.length() == 0) )
      return score;
    
    int argslength = args.length();
    
    if( (javaArgs.length != argslength) || (parameterTypes.length < argslength) )
      throw new RuntimeException("bad monkey, no banana");
    
    for( int i=0; (i<argslength) && (score > 0); i++ )
    {
      // in case it is a reference:
      Value arg = args.referenceAt(i).unhand();
      
      if( ( (arg == Value.NULL) || (arg == Value.UNDEFINED) ) &&
          !(parameterTypes[i].isPrimitive() || Value.class.isAssignableFrom(parameterTypes[i])) )
      {
        // null can be assigned to any non-primitive
        javaArgs[i] = null;
      }
      else if (parameterTypes[i] == BigDecimal.class) {
    	  javaArgs[i] = new BigDecimal(arg.castToInexactNumber());
      }
      else if( parameterTypes[i].isArray() )
      {
        try
        {
          int len = arg.length();
          Class componentType = parameterTypes[i].getComponentType();
          
          if( arg instanceof OString )
          {
            if( componentType == Character.TYPE )
            {
              // we want methods that take a String to be preferred over
              // methods that take a char[]
              score--;
              javaArgs[i] = arg.castToString().toCharArray();
            }
            else
            {
              // don't support converting a string to any other sort of array:
              return 0;
            }
          }
          else if( (arg instanceof OArray.OJavaArray) && 
                   compatibleJavaArray( componentType, arg.castToJavaObject() ) )
          {
            javaArgs[i] = arg.castToJavaObject();
          }
          else if( len > 0 )
          {
            Class[]  arrParameterTypes = new Class[len];
            Value[]  arrArgs = new Value[len];
            
            arrParameterTypes[0] = componentType;
            arrArgs[0]           = arg.elementAt( OExactNumber.makeExactNumber(0) );
            for( int j=1; j<len; j++ )
            {
              arrParameterTypes[j] = arrParameterTypes[0];
              arrArgs[j]           = arg.elementAt( OExactNumber.makeExactNumber(j) );
            }
            
            // primitive types need to be handled specially...
            if( arrParameterTypes[0].isPrimitive() )
            {
              // convert into temporary array:
              Object[] tmpArr = new Object[len];
              score -= Integer.MAX_VALUE - convertArgs( arrParameterTypes, tmpArr, new OArray(arrArgs) );
              
              if( score <= 0 )
                return score;
              
              // now copy to final destination:
              javaArgs[i] = Array.newInstance( arrParameterTypes[0], len );
              
              for( int j=0; j<len; j++ )
                Array.set( javaArgs[i], j, tmpArr[j] );
            }
            else
            {
              Object[] arrJavaArgs = (Object[])(Array.newInstance( arrParameterTypes[0], len ));
              score -= Integer.MAX_VALUE - convertArgs( arrParameterTypes, arrJavaArgs, new OArray(arrArgs) );
              javaArgs[i] = arrJavaArgs;
            }
          }
          else
          {
            score--;
            javaArgs[i] = Array.newInstance( parameterTypes[i].getComponentType(), 0 );
          }
        }
        catch(PackagedScriptObjectException e)
        {
          return 0;
        }
      }
      else if( parameterTypes[i].isPrimitive() )
      {
        if( parameterTypes[i] == Boolean.TYPE )
        {
          try
          {
            javaArgs[i] = arg.castToBoolean() ? Boolean.TRUE : Boolean.FALSE;
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( parameterTypes[i] == Character.TYPE )
        {
          try
          {
            String str = arg.castToString();
            
            if( (str != null) && (str.length() == 1) )
              javaArgs[i] = Character.valueOf( str.charAt(0) );
            else
              return 0;
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( parameterTypes[i] == Byte.TYPE )
        {
          try
          {
            long val = arg.castToExactNumber();
            
            if( (long)((byte)val) != val )
              return 0;
            
            if( ! arg.bopInstanceOf( OExactNumber.TYPE ).castToBoolean() )
              score--;
            
            javaArgs[i] = Byte.valueOf( (byte)val );
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( parameterTypes[i] == Short.TYPE )
        {
          try
          {
            long val = arg.castToExactNumber();
            
            if( (long)((short)val) != val )
              return 0;
            
            if( ! arg.bopInstanceOf( OExactNumber.TYPE ).castToBoolean() )
              score--;
            
            javaArgs[i] = Short.valueOf( (short)val );
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( parameterTypes[i] == Integer.TYPE )
        {
          try
          {
            long val = arg.castToExactNumber();
            
            if( (long)((int)val) != val )
              return 0;
            
            if( ! arg.bopInstanceOf( OExactNumber.TYPE ).castToBoolean() )
              score--;
            
            javaArgs[i] = Integer.valueOf( (int)val );
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( parameterTypes[i] == Long.TYPE )
        {
          try
          {
            javaArgs[i] = Long.valueOf( arg.castToExactNumber() );
            
            if( ! arg.bopInstanceOf( OExactNumber.TYPE ).castToBoolean() )
              score--;
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( parameterTypes[i] == Float.TYPE )
        {
          try
          {
            double val = arg.castToInexactNumber();
            
            if( (double)((float)val) != val )
              return 0;
            
            if( ! arg.bopInstanceOf( OInexactNumber.TYPE ).castToBoolean() )
              score--;
            
            javaArgs[i] = Float.valueOf( (float)val );
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else if( parameterTypes[i] == Double.TYPE )
        {
          try
          {
            javaArgs[i] = Double.valueOf( arg.castToInexactNumber() );
            
            if( ! arg.bopInstanceOf( OInexactNumber.TYPE ).castToBoolean() )
              score--;
          }
          catch(PackagedScriptObjectException e)
          {
            return 0;
          }
        }
        else
        {
          return 0;
        }
      }
      else
      {
        Object obj = arg.castToJavaObject();
        
        // to deal with NULL/UNDEFINED:
        if( obj == null )
          obj = arg;
        
        if( parameterTypes[i].isAssignableFrom( obj.getClass() ) )
        {
          if( parameterTypes[i] != obj.getClass() )
            score--;
          
          javaArgs[i] = obj;
        }
        else if( parameterTypes[i].isAssignableFrom( arg.getClass() ) )
        {
          if( parameterTypes[i] != arg.getClass() )
            score--;
          
          javaArgs[i] = arg;
        }
        else
        {
          boolean transformed = false;
          
          for( Iterator itr=functionTransformList.iterator(); itr.hasNext(); )
          {
            FunctionTransformer ft = (FunctionTransformer)(itr.next());
            
            if( parameterTypes[i].isAssignableFrom( ft.getTargetClass() ) )
            {
              javaArgs[i] = ft.transform(arg);
              transformed = true;
              break;
            }
          }
          
          if( !transformed )
            return 0;
        }
      }
    }
    
    return score;
  }
  
  private static final boolean compatibleJavaArray( Class componentType, Object javaArr )
  {
    Class t = javaArr.getClass().getComponentType();
    if( t == null )
      return false;
    return componentType.isAssignableFrom(t);
  }
  
  /*=======================================================================*/
  /**
   * Convert a java object to a script object.  Some java types can be
   * converted back to native script types, rather than need a wrapper,
   * so this handles that conversion.
   * 
   * @param javaObject   the java object to make a wrapper for
   */
  public final static Value convertToScriptObject( Object javaObject )
  {
    if( javaObject == null )
    {
      return Value.NULL;
    }
    else if( javaObject instanceof Number )
    {
      if( (javaObject instanceof Float) || (javaObject instanceof Double) || (javaObject instanceof BigDecimal))
        return OInexactNumber.makeInexactNumber( ((Number)javaObject).doubleValue() );
      else
        return OExactNumber.makeExactNumber( ((Number)javaObject).longValue() );
    }
    else if( javaObject instanceof Boolean )
    {
      return OBoolean.makeBoolean( ((Boolean)javaObject).booleanValue() );
    }
    else if( javaObject instanceof String )
    {
      return new OString( (String)javaObject ); // XXX should this be intern'd?
    }
    else if( javaObject instanceof Character )
    {
      return new OString( ((Character)javaObject).toString() ); // XXX should this be intern'd?
    }
    else if( javaObject instanceof Value )
    {
      return (Value)javaObject;
    }
    else if( javaObject.getClass().isArray() )
    {
      return OArray.makeArray(javaObject);
    }
    else
    {
      return null; // TODO !!! new JavaObjectWrapper(javaObject);
    }
  }
  public final static Value convertToScriptObject( long longVal )
  {
    return OExactNumber.makeExactNumber(longVal);
  }
  public final static Value convertToScriptObject( double doubleVal )
  {
    return OInexactNumber.makeInexactNumber(doubleVal);
  }
  public final static Value convertToScriptObject( boolean javaObject )
  {
    return OBoolean.makeBoolean(javaObject);
  }
  public final static Value convertToScriptObject( String javaObject )
  {
    if( javaObject == null )
    {
      return Value.NULL;
    }
    else
    {
      return new OString(javaObject);
    }
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

