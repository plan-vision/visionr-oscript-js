/*=============================================================================
 *     Copyright Texas Instruments 2000-2004.  All Rights Reserved.
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

/**
 * An array instance.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class OArray 
  extends OObject
  implements java.io.Externalizable, MemberTable
{
	
	/*=======================================================================*/
  // members:
  //   (I was using java.util.Vector, but it became easier to just do it myself)
  private Reference[] arr;
  private int size = 0;
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void readExternal( java.io.ObjectInput in )
    throws java.io.IOException, ClassNotFoundException
  {
    size = in.readInt();
    arr = new Reference[size];
    for( int i=0; i<size; i++ )
      arr[i] = (Reference)(in.readObject());
  }
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void writeExternal( java.io.ObjectOutput out )
    throws java.io.IOException
  {
    out.writeInt(size);
    for( int i=0; i<size; i++ )
      out.writeObject( arr[i] );
  }
  /*=======================================================================*/
  
  /**
   * The type object for an instance of Array.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.OArray");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "Array";
  public final static String[] MEMBER_NAMES   = new String[] {
    "castToString",   "castToJavaObject", "length",         "elementAt",
    "elementsAt",     "iterator",         "concat",         "join",
    "slice",          "splice",           "pop",            "push",
    "shift",          "unshift",          "reverse",        "sort",
    "every",          "some",             "filter",         "map",
    "forEach"
  };
  
  
  /*=======================================================================*/
  /**
   */
  public final static OArray makeArray( final Object arrObject )
  {
    return new OJavaArray(arrObject);
  }
  private static final int ADD = 81; //Symbols.java
  private static final int PUSH = 78; //Symbols.java
  private static final int SIZE  = 173;
  private static final int LENGTH  = 172;
  private static final int IS_EMPTY = 164;
  private static final int IS_EMPTY2 = 109;
  private static final int FIRST = 175;
  private static final int LAST = 176;
  private static final int PUT_ALL = 80; //Symbols.java
  private static final int POS_TYPE = 69; //Symbols.java
	
  public Value getMember(int id, boolean exception) throws PackagedScriptObjectException 
  {
	  	if (id == SIZE || id == LENGTH) 
		{
	  		return new FunctionValueWrapper(new OExactNumber(length()));
		}
  		if (id == IS_EMPTY || id == IS_EMPTY2) 
		{
	  		return new FunctionValueWrapper(OBoolean.makeBoolean(length() == 0));
		}
  		if (id == FIRST) 
		{
	  		return new FunctionValueWrapper(
	  				length() == 0 ? Value.NULL : elementAt(0)
	  		);
		}
  		if (id == LAST) 
		{
	  		return new FunctionValueWrapper(
	  				length() == 0 ? Value.NULL : elementAt(length()-1)
	  		);
		}
		if (id == PUT_ALL) 
		{
			return new Value() 
			{
				public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException 
				{
					if (args.length() != 1)						
				          throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("unsupported argument count for oarray putAll!") );  // XXX
					Value v = args.referenceAt(0).unhand();
					if (v.bopEquals(Value.NULL).castToBoolean())
						return OArray.this;
					int s = (int)v.getMember("length").callAsFunction(new Value[0]).castToExactNumber();
					for (int i=0;i<s;i++)
						OArray.this.push1(v.elementAt(OExactNumber.makeExactNumber(i)));
					return OArray.this;
				};
				protected Value getTypeImpl() {return this;};
			};
		}

  		if (id == ADD) {
 			id=PUSH;
		}
  		return super.getMember(id,exception);
  }
  
  public static class OJavaArray
    extends OArray
  {
        private final int len;
        private final Object arrObject;
        
        OJavaArray( Object arrObject )
        {
          this.arrObject = arrObject;
          this.len = java.lang.reflect.Array.getLength(arrObject);
        }
        
        public Object castToJavaObject()
        {
          return arrObject;
        }
        
        public int length()
        {
          return len;
        }
        
        public synchronized Value elementAt( final int idx )
          throws PackagedScriptObjectException
        {
          if( (idx < 0) || (idx >= length()) )
            throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("invalid array index: " + idx) );
          
          return new AbstractReference() {
              
              public void opAssign( Value val )
                throws PackagedScriptObjectException
              {
                Object obj = JavaBridge.convertToJavaObject( val, arrObject.getClass().getComponentType() );
                
                java.lang.reflect.Array.set( arrObject, idx, obj );
              }
              
              protected Value get()
              {
                return JavaBridge.convertToScriptObject( java.lang.reflect.Array.get( arrObject, idx ) );
              }
              
            };
        }
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   */
  public OArray()
  {
    this(2);
  }
  
  /**
   * private constructor, used internally and by compiler... 
   * initialLength == -1 is used to create arrays
   * that wrap native java arrays, rather than having to do a copy when
   * an array is passed from java to script.  For java arrays, arr should
   * equal null, so methods that don't work for java arrays know to throw
   * an exception... ugly, but it works
   */
  public OArray( int initialLength )
  {
    super();
    if( initialLength >= 0 )
      arr = new Reference[initialLength];
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.  This constructor is not intended for public
   * consumption... it is just used by StackFrame$StackFrameArray for
   * quickly making a safe-copy when already copied out of the stack
   */
  public OArray( Reference[] arr, int size )
  {
    super();
    this.arr  = arr;
    this.size = size;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.  This is the constructor that is called via a
   * <code>BuiltinType</code> instance.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public OArray( oscript.util.MemberTable args )
  {
    this( args.length() );
    
    int alen = args.length();
    
    for( int i=0; i<alen; i++ )
      referenceAt(i).reset( args.referenceAt(i) );
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.  This is the constructor that is called via a
   * <code>BuiltinType</code> instance.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public OArray( Value[] args )
  {
    this( args.length );
    int alen = args.length;
    ensureCapacity(alen-1);
    for( int i=0; i<alen; i++ )
      referenceAt(i).reset( args[i] );
  }
  
  /*=======================================================================*/
  /**
   * Get the type of this object.  The returned type doesn't have to take
   * into account the possibility of a script type extending a built-in
   * type, since that is handled by {@link #getType}.
   * 
   * @return the object's type
   */
  protected Value getTypeImpl()
  {
    return TYPE;
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>String</code> value.
   * 
   * @return a String value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public String castToString()
    throws PackagedScriptObjectException
  {
    StringBuffer sb = new StringBuffer();
    
    sb.append('[');
    
    for( int i=0; i<length(); i++ )
    {
      if( i != 0 )
        sb.append(", ");
      
      sb.append( elementAt(i).castToString() );
    }
    
    sb.append(']');
    
    return sb.toString();
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>Object</code> value.
   * 
   * @return a java object
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public Object castToJavaObject()
    throws PackagedScriptObjectException
  {
    /*Object[] obj = new Object[length()];
    
    for( int i=0; i<obj.length; i++ )
      obj[i] = elementAt(i).castToJavaObject();
    
    return obj;*/
	return this;
  }
  
  /*=======================================================================*/
  /**
   * For types that implement <code>elementAt</code>, this returns the
   * number of elements.  This is the same as the <i>length</i> property
   * of an object.
   * 
   * @return an integer length
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   * @see #elementAt
   */
  public int length()
    throws PackagedScriptObjectException
  {
    return size;
  }
  
  /*=======================================================================*/
  /**
   * Get the specified index of this object, if this object is an array.  If
   * needed, the array is grown to the appropriate size.
   * 
   * @param idx          the index to get
   * @return a reference to the member
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   * @see #length
   */
  public Value elementAt( Value idx )
    throws PackagedScriptObjectException
  {
    return elementAt( (int)(idx.castToExactNumber()) );
  }
  
  public synchronized Value elementAt( int idx )
  {
    if( idx < 0 )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("invalid array index: " + idx) );
    ensureCapacity(idx);
    return referenceAt( idx, 0 );
  }
  
  public final Reference referenceAt( int idx )
  {
    return referenceAt( idx, Reference.ATTR_INVALID );
  }
  
  public final Reference referenceAt( int idx, int attr )
  {
    if( arr[idx] == null )
    {
      arr[idx] = new Reference(attr);
      
      if( idx >= size )
        size = idx + 1;
    }
    
    return arr[idx];
  }
  
  public final void ensureCapacity( int sz )
  {
    if( sz >= arr.length )
    {
      // grow to fit
      int newSize = arr.length * 2;
      
      if( sz >= newSize )
        newSize = sz + 1;
      
      Reference[] newArr = new Reference[newSize];
      System.arraycopy( arr, 0, newArr, 0, size );
      arr = newArr;
    }
  }
  
  /*=======================================================================*/
  /**
   * Get the specified range of this object, if this object is an array.  
   * This returns a copy of a range of the array.
   * 
   * @param idx1         the index index of the beginning of the range, inclusive
   * @param idx2         the index of the end of the range, inclusive
   * @return a copy of the specified range of this array
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   * @see #length
   * @see #elementAt
   */
  public Value elementsAt( Value idx1, Value idx2 )
    throws PackagedScriptObjectException
  {
    return elementsAt( (int)(idx1.castToExactNumber()), (int)(idx2.castToExactNumber()) );
  }
  
  public Value elementsAt( int idx1, int idx2 )
  {
    if( (idx1 < 0) || (idx2 < 0) || (idx2 < idx1) )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("invalid array index: " + idx1 + ", " + idx2) );
    
    OArray arr = new OArray( idx2 - idx1 + 1 );
    
    for( int i=idx1; i<=idx2; i++ )
      arr.elementAt(i-idx1).opAssign( elementAt(i) );
    
    return arr;
  }
  
  /*=======================================================================*/
  
  private static abstract class IteratorWithPos extends Value implements java.util.Iterator {
  }
  
  /**
   * Get an iterator of all the elements of this array.  This makes arrays
   * useful in the "for( var item : set )" syntx.
   * 
   * @return an iterator of elements of the array
   */
  public java.util.Iterator iterator()
  {
    return new IteratorWithPos() 
    {
        private int idx = 0;
        
        public boolean hasNext() { return idx < OArray.this.length(); }
        public void remove() { throw new UnsupportedOperationException("remove"); }
        
        public Object next()
        {
          Value val = OArray.this.elementAt(idx);
          idx++;
          return val;
        }
        
        @Override
        public Value getMember(int id, boolean exception)
        		throws PackagedScriptObjectException 
        {        	
        	if (id == 107) /* hasNext */ {
        		return new OBoolean(hasNext()) 
        		{
        			public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException { 
        				return this; 
        			};
        		};
        	}
        	if (id == 178) /* next */ 
        	{
        		Object o = next();
        		final Value v = JavaBridge.convertToScriptObject(o);
        		return new AbstractReference() 
        		{
					@Override
					protected Value get() {
						return v;
					}
					@Override
					public Value getMember(int id, boolean exception)
							throws PackagedScriptObjectException 
					{
			        	if (id == POS_TYPE)
			        		return new OExactNumber(idx-1);
						return super.getMember(id, exception);
					}
					@Override
					public Value callAsFunction(StackFrame sf, MemberTable args)
							throws PackagedScriptObjectException {
						return this;
					}
				};
        	}
        	return super.getMember(id, exception);
        }
        
		@Override
		protected Value getTypeImpl() {
			// TODO Auto-generated method stub
			return null;
		}
        
      };
  }
  
  /*=======================================================================*/
  /**
   * Join this array object with one or more additional arrays.  This 
   * performs a shallow copy, so the original arrays are unmodified, but
   * for mutable objects there may be side effects.
   */
  public final Value concat = new VarArgFunction("concat") {
      
      public Value callAsFunction( StackFrame sf, MemberTable args )
      {
        if( args == null )
          return OArray.this;
        Value tmp;
        int alen = args.length();
        int  len = OArray.this.length();
        for( int i=0; i<alen; i++ )
          len += args.referenceAt(i).length();
        OArray result = new OArray(len);
        int i0 = 0;
        for( int i1=0; i1<OArray.this.length(); i1++ )
          result.elementAt(i0++).opAssign( OArray.this.elementAt(i1) );
        for( int i=0; i<alen; i++ )
          if( (tmp=args.referenceAt(i)) instanceof OArray )
            for( int i1=0; i1<tmp.length(); i1++ )
              result.elementAt(i0++).opAssign( ((OArray)tmp).elementAt(i1) );
          else
            for( int i1=0; i1<tmp.length(); i1++ )
              result.elementAt(i0++).opAssign( tmp.elementAt( JavaBridge.convertToScriptObject(i1) ) );
        return result;
      }
      
    };
  
  /*=======================================================================*/
  /**
   * Join all elements in the array into a single string, separated by 
   * commas
   * 
   * @return the resulting string
   */
  public Value join()
  {
    return join( JavaBridge.convertToScriptObject(",") );
  }
  
  /*=======================================================================*/
  /**
   * Join all elements in the array into a single string, separated by the
   * specified separator.
   * 
   * @param separator      the separator string
   * @return the resulting string
   */
  public Value join( Value separator )
  {
    String str = "";
    String sep = separator.castToString();
    
    if( length() > 0 )
    {
      str = elementAt(0).castToString();
      
      for( int i=1; i<length(); i++ )
        str += sep + elementAt(i).castToString();
    }
    
    return JavaBridge.convertToScriptObject(str);
  }
  
  /*=======================================================================*/
  /**
   * Slice out a section of the array.  This works like <code>arr[begin..
   * arr.length()-1]</code>.  The index can be negative, in which case it 
   * will count from the end of the array.
   * 
   * @param begin    the begin index, inclusive
   * @return the resulting array
   */
  public Value slice( Value begin )
  {
    int a = getAbsoluteIndex(begin);
    int b = length();
    return elementsAt( a, b-1 );
  }
  
  /*=======================================================================*/
  /**
   * Slice out a section of the array.  This works like <code>arr[begin..
   * end-1]</code>.  Either index can be negative, in which case it 
   * will count from the end of the array.
   * 
   * @param begin    the begin index, inclusive
   * @param end      the end index, exclusive
   * @return the resulting array
   */
  public Value slice( Value begin, Value end )
  {
    int a = getAbsoluteIndex(begin);
    int b = getAbsoluteIndex(end);
    return elementsAt( a, b-1 );
  }
  
  /*=======================================================================*/
  /**
   * Add and remove elements from an array
   * 
   * @param idx     the index to begin removing from
   * @param num     the number of elements to remove (optional, defaults to
   *    everything after <code>idx</code>)
   * @param args... optional additional arguments to replace the removed
   *    elements
   * @return the removed elements
   */
  public final Value splice = new VarArgFunction("splice") {
      
      public Value callAsFunction( StackFrame sf, MemberTable args )
      {
        int alen = (args == null) ? 0 : args.length();
        
        if( arr == null )
          throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("unsupported operation for java array!") );  // XXX
          
        if( alen < 2 )
          throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
        
        synchronized(OArray.this)
        {
          int len   = OArray.this.length();
          int begin = getAbsoluteIndex( args.referenceAt(0) );
          int end   = (int)(args.referenceAt(1).castToExactNumber()) + begin;
          
          Value result = OArray.this.elementsAt( begin, end-1 );
          
          int delta = begin - end + Math.max( 0, alen - 2 );
          
          if( delta > 0 )
            for( int i=len-1; i>=end; i-- )
              OArray.this.elementAt( i + delta ).opAssign( OArray.this.elementAt(i) );
          else if( delta < 0 )
            for( int i=end; i<len; i++ )
              OArray.this.elementAt( i + delta ).opAssign( OArray.this.elementAt(i) );
          
          for( int i=2; i<alen; i++ )
            OArray.this.elementAt( i - 2 + begin ).opAssign( args.referenceAt(i) );
          
          for( int i=len+delta; i<size; i++ )
            arr[i] = null;
          size = len + delta;
          
          return result;
        }
      }
    };
  
  /*=======================================================================*/
  /**
   * Remove and return the last element of the array.
   */
  public synchronized Value pop()
  {
    if( arr == null )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("unsupported operation for java array!") );  // XXX
    int idx = size - 1;
    Value result = elementAt(idx);  // elementAt throws exception if idx is invalid
    size--;
    arr[idx] = null;
    return result;
  }
  
  /*=======================================================================*/
  /**
   * Add one or more elements to the end of the array, returning the new
   * length.
   */
  public final Value push = new VarArgFunction("push") {
      
      public Value callAsFunction( StackFrame sf, MemberTable args )
      {
        int alen = (args == null) ? 0 : args.length();
        int idx = OArray.this.length();
        for( int i=0; i<alen; i++ )
          OArray.this.elementAt(idx++).opAssign( args.referenceAt(i) );
        return JavaBridge.convertToScriptObject(idx);
      }
      
    };
  
  /*=======================================================================*/
  /**
   * Remove and return the first element of the array.
   */
  public synchronized Value shift()
  {
    if( arr == null )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("unsupported operation for java array!") );  // XXX
    
    Value result = elementAt(0);  // elementAt throws exception if idx is invalid
    System.arraycopy( arr, 1, arr, 0, size-1 );
    arr[size-1] = null;
    size--;
    return result;
  }
  
  /*=======================================================================*/
  /**
   * Add one or more elements to the beginning of the array, returning the
   * new length.
   */
  public final Value unshift = new VarArgFunction("unshift") {
      
      public Value callAsFunction( StackFrame sf, MemberTable args )
      {
        synchronized(OArray.this)
        {
          int alen = (args == null) ? 0 : args.length();
          if( alen > 0 )
            for( int i=OArray.this.length()-1; i>=0; i-- )
              OArray.this.elementAt( i + alen ).opAssign( OArray.this.elementAt(i) );
          for( int i=0; i<alen; i++ )
            OArray.this.elementAt(i).opAssign( args.referenceAt(i) );
          return JavaBridge.convertToScriptObject( OArray.this.length() );
        }
      }
      
    };
  
  /*=======================================================================*/
  /**
   * Reverse the order of elements in this array, returning this array.  The
   * array is reversed in order.
   * 
   * @return this array object itself
   */
  public synchronized Value reverse()
  {
    if( arr == null )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("unsupported operation for java array!") );  // XXX
    
    for( int i0=0; ; i0++ )
    {
      int i1 = arr.length - 1 - i0;
      if( i1 <= i0 )
        break;
      Reference tmp = arr[i0];
      arr[i0] = arr[i1];
      arr[i1] = tmp;
    }
    return this;
  }
  
  /*=======================================================================*/
  /**
   * Executes the provided function <code>fxn</code> once for each element 
   * present in the array until it finds one where callback returns 
   * <code>false</code>. If such an element is found, the test aborts and 
   * <code>false</code> is returned, otherwise (callback returned 
   * <code>true</code> for each of the elements) every will return 
   * <code>true</code>.  This array is not mutated.
   * 
   * @param fxn  invoked with three arguments: the value of the element, 
   *   the index of the element, and the array object containing the
   *   element;  should return <code>true</code> or <code>false</code>
   * @return <code>true</code> if every element passes the test, otherwise
   *   <code>false</code>
   */
  public Value every( Value fxn )
  {
    int len = length();
    Value[] args = new Value[] { null, null, this };
    for( int i=0; i<len; i++ )
    {
      args[0] = elementAt(i);
      args[1] = OExactNumber.makeExactNumber(i);
      if( ! fxn.callAsFunction(args).castToBoolean() )
        return OBoolean.FALSE;
    }
    return OBoolean.TRUE;
  }
  
  /*=======================================================================*/
  /**
   * Tests whether some element in the array passes the test implemented by 
   * the provided function.  Executes the provided function once for each 
   * element present in the array until it finds one where callback returns 
   * <code>true</code>. If such an element is found, the test aborts and 
   * <code>true</code> is returned, otherwise (callback returned 
   * <code>false</code> for each of the elements) return <code>false</code>.
   * 
   * @param fxn  invoked with three arguments: the value of the element, 
   *   the index of the element, and the array object containing the
   *   element;  should return <code>true</code> or <code>false</code>
   * @return <code>true</code> if some element passes the test, otherwise
   *   <code>false</code>
   */
  public Value some( Value fxn )
  {
    int len = length();
    Value[] args = new Value[] { null, null, this };
    for( int i=0; i<len; i++ )
    {
      args[0] = elementAt(i);
      args[1] = OExactNumber.makeExactNumber(i);
      if( fxn.callAsFunction(args).castToBoolean() )
        return OBoolean.TRUE;
    }
    return OBoolean.FALSE;
  }
  
  /*=======================================================================*/
  /**
   * Create a new array with all elements that pass the test implemented 
   * by the provided function.  The provided callback is invoked for each
   * element in the array, and a new array is constructed containing (in
   * the same order) the elements for which the callback returned
   * <code>true</code>.  Elements for which the callback returns
   * <code>false</code> are skipped.  This array is not mutated.
   * 
   * @param fxn  invoked with three arguments: the value of the element, 
   *   the index of the element, and the array object containing the
   *   element;  should return <code>true</code> or <code>false</code>
   * @return the filtered array.
   */
  public Value filter( Value fxn ) 
  {
    OArray arr = new OArray(0);
    int len = length();
    Value[] args = new Value[] { null, null, this };
    for( int i=0; i<len; i++ )
    {
      args[0] = elementAt(i);
      args[1] = OExactNumber.makeExactNumber(i);
      if( fxn.callAsFunction(args).castToBoolean() )
        arr.push1( elementAt(i) );
    }
    return arr;
  }
  
  /*=======================================================================*/
  /**
   * Creates a new array with the results of calling a provided function 
   * on every element in this array.  This array is not mutated.
   * 
   * @param fxn  invoked with three arguments: the value of the element, 
   *   the index of the element, and the array object containing the
   *   element
   * @return the new array
   */
  public Value map( Value fxn )
  {
    OArray arr = new OArray( length() );
    int len = length();
    Value[] args = new Value[] { null, null, this };
    for( int i=0; i<len; i++ )
    {
      args[0] = elementAt(i);
      args[1] = OExactNumber.makeExactNumber(i);
      arr.push1( fxn.callAsFunction(args) );
    }
    return arr;
  }
  
  /*=======================================================================*/
  /**
   * Executes a provided function once per array element.  This array is
   * not mutated.
   * 
   * @param fxn  invoked with three arguments: the value of the element, 
   *   the index of the element, and the array object containing the
   *   element
   */
  public void forEach( Value fxn )
  {
    int len = length();
    Value[] args = new Value[] { null, null, this };
    for( int i=0; i<len; i++ )
    {
      args[0] = elementAt(i);
      args[1] = OExactNumber.makeExactNumber(i);
      fxn.callAsFunction(args);
    }
  }
  
  /*=======================================================================*/
  /**
   * Sort the elements in the array, by converting each element to string,
   * and sorting in ascending order.  The array is sorted in place.
   * 
   * @return this array object itself
   */
  public Value sort()
  {
	final Value m1=new OExactNumber(-1);
	final Value p1=new OExactNumber(1);
	final Value z=new OExactNumber(0);
    return sort( new Value() {
    	@Override
    	public Value callAsFunction(StackFrame sf, MemberTable args) throws PackagedScriptObjectException {
    		if (args.referenceAt(0).bopGreaterThan(args.referenceAt(1)).castToBoolean()) return p1;
    		if (args.referenceAt(0).bopLessThan(args.referenceAt(1)).castToBoolean()) return m1;
    		return z;
    	}
    	@Override
    	protected Value getTypeImpl() {
    		// TODO Auto-generated method stub
    		return null;
    	}
    });
  }  
  /*=======================================================================*/
  /**
   * Sort the elements in the array, by using the specified comparision
   * function.  The comparision <code>function(a,b)</code> should return
   * less than zero if <tt>a</tt> is less than <tt>b</tt>, zero if they
   * are equal, and greater than zero if <tt>a</tt> is greater than 
   * <tt>b</tt>.  The array is sorted in place.
   * 
   * @param fxn      the comparasion function
   * @return this array object itself
   */
  public synchronized Value sort( Value fxn )
  {
    if( arr == null )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OUnsupportedOperationException("unsupported operation for java array!") );  // XXX
    _qsort( fxn, arr, 0, size-1 );
    return this;
  }
  
  private static final void _qsort( Value fxn, Reference[] arr, int lo, int hi )
  {
    if( lo >= hi )
      return;
    
    // short cut for sequential elements:
    if( hi == (lo+1) )
    {
      if( _compare( fxn, arr[lo], arr[hi] ) > 0 )
        _swap( arr, hi, lo );
      return;
    }
    
    int pivot = (lo + hi) / 2;
    
    _swap( arr, pivot, hi );
    
    int bot = lo;
    int top = hi-1;
    
    while( bot < top )
    {
      while( (bot <= top) && (_compare( fxn, arr[bot], arr[hi] ) <= 0) )
        bot++;
      while( (bot <= top) && (_compare( fxn, arr[top], arr[hi] ) >= 0) )
        top--;
      
      if( bot < top )
        _swap( arr, top, bot );
    }
    
    _swap( arr, bot, hi );
    
    _qsort( fxn, arr, lo, bot-1 );
    _qsort( fxn, arr, bot+1, hi );
    
    // XXX double check generated code to verify that arr[hi] (the pivot) gets hoisted
  }
  
  private static final int _compare( Value fxn, Reference a, Reference b )
  {
    return (int)(fxn.callAsFunction( new Value[] { a.unhand(), b.unhand() } ).castToExactNumber());
  }
  
  private static final void _swap( Reference[] arr, int a, int b )
  {
    Reference tmp = arr[a];
    arr[a] = arr[b];
    arr[b] = tmp;
  }
  
  /*=======================================================================*/
  /**
   * Helper function to convert a relative index to an absolute index
   */
  private int getAbsoluteIndex( Value idx )
  {
    int i = (int)(idx.castToExactNumber());
    if( i < 0 )
      i = length() + i;
    return i;
  }
  
  
  /*=======================================================================*/
  // hmm, move this?
  private static class VarArgFunction extends Value
  {
    private final int id;
    
    VarArgFunction( String name )
    {
      super();
      this.id = Symbol.getSymbol(name).getId();
    }
    
    protected Value getTypeImpl()
    {
      return Function.TYPE;
    }
    
    public Value getName()
    {
      return Symbol.getSymbol(id);
    }
    
    public String castToString() { return "[function: " + getName() + "]"; }
  }
  
  /*=======================================================================*/
  
  public MemberTable safeCopy()
  {
    // no-op... OArray is always safe
    return this;
  }
  
  public void push1( Value val )
  {
    int idx = length();
    ensureCapacity(idx);
    referenceAt(idx).reset(val);
  }
  
  public void push2( Value val1, Value val2 )
  {
    push1(val1);
    push1(val2);
  }
  
  public void push3( Value val1, Value val2, Value val3 )
  {
    push1(val1);
    push1(val2);
    push1(val3);
  }
  
  public void push4( Value val1, Value val2, Value val3, Value val4 )
  {
    push1(val1);
    push1(val2);
    push1(val3);
    push1(val4);
  }
  
  public void reset()
  {
    for( int i=length()-1; i>=0; i-- )
      referenceAt(i).reset();
  }
  
  public void free()
  {
    // no-op
  }
  
  /*=======================================================================*/
  public static void fillByteArray( byte[] arr )
  {
    for( int i=0; i<arr.length; i++ )
      arr[i] = (byte)i;
  }
  public static void fillCharacterArray( char[] arr )
  {
    for( int i=0; i<arr.length; i++ )
      arr[i] = (char)i;
  }
  public static void fillShortArray( short[] arr )
  {
    for( int i=0; i<arr.length; i++ )
      arr[i] = (short)i;
  }
  public static void fillIntegerArray( int[] arr )
  {
    for( int i=0; i<arr.length; i++ )
      arr[i] = i;
  }
  public static void fillLongArray( long[] arr )
  {
    for( int i=0; i<arr.length; i++ )
      arr[i] = i;
  }
  public static void fillFloatArray( float[] arr )
  {
    for( int i=0; i<arr.length; i++ )
      arr[i] = i;
  }
  public static void fillDoubleArray( double[] arr )
  {
    for( int i=0; i<arr.length; i++ )
      arr[i] = i;
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

