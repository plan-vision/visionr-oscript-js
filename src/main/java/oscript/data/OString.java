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

import java.util.Hashtable;



/**
 * A string class.  An <code>OString</code> is immutable, once the instance is 
 * constructed, it won't change.
 * 
 * @author Rob Clark (rob@ti.com)
 */
public class OString extends OObject implements java.io.Externalizable
{
  /**
   * The type object for an instance of String.
   */
  public final static Value TYPE = BuiltinType.makeBuiltinType("oscript.data.OString");
  public final static String PARENT_TYPE_NAME = "oscript.data.OObject";
  public final static String TYPE_NAME        = "String";
  public final static String[] MEMBER_NAMES   = new String[] {
                                                      "castToBoolean",
                                                      "castToString",
                                                      "castToExactNumber",
                                                      "castToInexactNumber",
                                                      "castToJavaObject",
                                                      "bopEquals",
                                                      "bopEqualsR",
                                                      "bopNotEquals",
                                                      "bopNotEqualsR",
                                                      "bopLessThan",
                                                      "bopLessThanR",
                                                      "bopGreaterThan",
                                                      "bopGreaterThanR",
                                                      "bopLessThanOrEquals",
                                                      "bopLessThanOrEqualsR",
                                                      "bopGreaterThanOrEquals",
                                                      "bopGreaterThanOrEqualsR",
                                                      "bopPlus",
                                                      "bopPlusR",
                                                      "length",
                                                      "elementAt",
                                                      "elementsAt",
                                                      "match",
                                                      "replace",
                                                      "replaceFirst",
                                                      "search",
                                                      "substring",
                                                      "intern",
                                                      "indexOf",
                                                      "lastIndexOf",
                                                      "toUpperCase",
                                                      "toLowerCase",
                                                      "equals",
                                                      "startsWith",
                                                      "endsWith",
                                                      "trim",
                                                      "split"
                                                    };
  
  
  /*=======================================================================*/
  /**
   */
  protected Segment segment;
  
  public OString() {}
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void readExternal( java.io.ObjectInput in )
    throws java.io.IOException
  {
    segment = new StringSegment( in.readUTF() );
  }
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void writeExternal( java.io.ObjectOutput out )
    throws java.io.IOException
  {
    out.writeUTF( segment.toString() );
  }
  /*=======================================================================*/
  
  
  /*=======================================================================*/
  /**
   * Construct a new string.
   * 
   * @param stringVal    the value of the string
   */
  public OString( String stringVal )
  {
    this( new StringSegment(stringVal) );
  }
  
  private OString( Segment segment )
  {
    super();
    this.segment = segment;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.  This is the constructor that gets called via an
   * <code>BuiltinType</code> instance.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public OString( oscript.util.MemberTable args )
  {
    super();
    
    if( args.length() != 1 )
       throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
     else
       segment = new StringSegment( args.referenceAt(0).castToString() );
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
   * Get the value of this string.
   * 
   * @return the string as a java.lang.String
   */
  public final String value()
  {
    return segment.toString();
  }
  
  /*=======================================================================*/
  /**
   * Return a hash code value for this object.
   * 
   * @return a hash code value
   * @see java.lang.Object#hashCode()
   */
  public int hashCode()
  {
    return segment.hashCode();
  }
  
  /*=======================================================================*/
  /**
   * Return a canonical representation of this OString object.  This has the
   * result that <code>x.intern() == y.intern()</code> (for <code>x</code>
   * and <code>y</code> that are OString objects).
   * 
   * @return a OString that has the same value (in the sense of <code>equals
   * </code>) but is guaraneed to be from a unique pool of OStrings
   */
  public OString intern()
  {
    return makeString( this, segment.toString() );
  }
  
  /*=======================================================================*/
  /**
   * Compare two objects for equality.
   * 
   * @param obj          the object to compare to this object
   * @return <code>true</code> if equals, else <code>false</code>
   * @see java.lang.Object#equals(java.lang.Object)
   */
  public boolean equals( Object obj )
  {
    if( this == obj )
      return true;
    
    String str = null;
    
    if( obj instanceof String )
      str = (String)obj;
    else if( obj instanceof Value )
      str = ((Value)obj).castToString();
    
    if( str != null )
      return str.equals( segment.toString() );
    
    return false;
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>boolean</code> value.
   * 
   * @return a boolean value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public boolean castToBoolean()
    throws PackagedScriptObjectException
  {
    if( segment.toString().equals("true") )
    {
      return true;
    }
    else if( segment.toString().equals("false") )
    {
      return false;
    }
    else
    {
      throw noSuchMember("castToBoolean");
    }
  }

  public boolean castToBooleanSoft() throws PackagedScriptObjectException
  {
	    if( segment.toString().equals("true") )
	    {
	      return true;
	    }
	    else if( segment.toString().equals("false") )
	    {
	      return false;
	    }
	    else
	    {
	      return segment.toString().length() > 0;
	    }
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
    return segment.toString();
  }
  
  public static Value _bopCast( Value val )
  {
    if( val instanceof OString )
      return val;
    return new OString( val.castToString() );
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>long</code> value.  In order
   * for a string to be converted to an exact number, it must be of the
   * form:
   * <pre>
   *   STRING       ::== ("-")? (HEX_STRING | OCTAL_STRING | DEC_STRING)
   *   HEX_STRING   ::== ("0x" | "0X") ([0-9] | [a-f] | [A-F])+
   *   OCTAL_STRING ::== "0" ([0-7])+
   *   DEC_STRING   ::== ([0-9])+
   * </pre>
   * 
   * @return a long value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public long castToExactNumber()
    throws PackagedScriptObjectException
  {
    /* we are spending too much time in NumberFormatException::fillInStackTrace 
     * is thrown, so instead we roll our own:
     */
    if( segment.toString().length() > Byte.MAX_VALUE )
    {
      throw PackagedScriptObjectException.makeExceptionWrapper( new ONoSuchMemberException("cannot convert \"" + segment.toString() + "\" to ExactNumber") );
    }
    
    byte radix  = 10;
    byte idx    = 0;
    byte sign   = 1;
    byte max    = (byte)(segment.toString().length());
    
    String str = segment.toString();
    
    if( str.startsWith("-") )
    {
      idx  += 1;
      sign  = -1;
    }
    
    if( str.startsWith("0x",idx) || str.startsWith("0X",idx) )
    {
      idx  += 2;
      radix = 16;
    }
    else if( str.startsWith("0",idx) )
    {
      idx  += 1;
      radix = 8;
    }
    else if( str.startsWith("'",idx) )
    {
      idx  += 1;
      radix = 2;
    }
    
    // check for valid number string, ie "0x", "-", etc., aren't valid:
    if( (idx >= max) && (radix != 8) )
      throw PackagedScriptObjectException.makeExceptionWrapper( new ONoSuchMemberException("cannot convert \"" + str + "\" to ExactNumber") );
    
    return parseExactNumber( segment.toString(), radix, idx, max ) * sign;
  }
  
  // doesn't deal with sign, figure out radix, etc...
  private static final long parseExactNumber( String str, byte radix, byte idx, byte max )
  {
    long result = 0;
    
    while( idx < max )
    {
      int digit = Character.digit( str.charAt(idx++), radix );
      
      // check for characters that aren't digits:
      if( digit == -1 )
        throw PackagedScriptObjectException.makeExceptionWrapper( new ONoSuchMemberException("cannot convert \"" + str + "\" to ExactNumber") );
      
      long oldResult = result;
      
      result *= radix;
      result += digit;
      
      // check for roll-over:
      if( result < oldResult )
        throw PackagedScriptObjectException.makeExceptionWrapper( new ONoSuchMemberException("cannot convert \"" + str + "\" to ExactNumber") );
    }
    
    return result;
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>double</code> value.  In
   * order for a string to be converted to an inexact number, it must be
   * of the form:
   * <pre>
   *   STRING       ::== ("+" | "-")? EXACT_NUMBER ("." EXACT_NUMBER)? (("e" | "E") EXACT_NUMBER)?
   *   EXACT_NUMBER ::== <see convertToExactNumber>
   * </pre>
   * 
   * @return a double value
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public double castToInexactNumber()
    throws PackagedScriptObjectException
  {
    String r = segment.toString();
    
    /* we are spending too much time in NumberFormatException::fillInStackTrace 
     * is thrown, so instead we roll our own:
     */
    if( r.length() > Byte.MAX_VALUE )
      throw PackagedScriptObjectException.makeExceptionWrapper( new ONoSuchMemberException("cannot convert \"" + segment.toString() + "\" to InexactNumber") );
    
    // in case we have something like "0x1234".castToInexactNumber()
    if( r.indexOf('.') == -1 )
      return (double)castToExactNumber();
    
    double sign = 1.0;
    byte tmp;
    
    if( r.startsWith("-") )
    {
      sign = -1.0;
      r = r.substring(1);
    }
    else if( r.startsWith("+") )
    {
      // "+" is the default sign
      r = r.substring(1);
    }
    
    // check for valid string:
    if( r.length() <= 0 )
      throw PackagedScriptObjectException.makeExceptionWrapper( new ONoSuchMemberException("cannot convert \"" + segment.toString() + "\" to InexactNumber") );
    
    String s1;
    String s2;
    String s3;
    
    // look for optional s3:
    tmp = (byte)(r.lastIndexOf('e'));
    if( tmp == -1 )
      tmp = (byte)(r.lastIndexOf('E'));
    
    if( tmp != -1 )
    {
      s3 = r.substring(tmp+1);
      r  = r.substring(0,tmp);
    }
    else
    {
      s3 = null;
    }
    
    // look for optional s2:
    tmp = (byte)(r.lastIndexOf('.'));
    if( tmp != -1 )
    {
      s2 = r.substring(tmp+1);
      r  = r.substring(0,tmp);
    }
    else
    {
      s2 = null;
    }
    
    // whats left is s1:
    s1 = r;
    
    // now convert to a number:
    try
    {
      double result = (double)parseExactNumber( s1, (byte)10, (byte)0, (byte)(s1.length()) );
      
      if( s2 != null )
      {
        double dec = (double)parseExactNumber( s2, (byte)10, (byte)0, (byte)(s2.length()) );
        
        result += dec / Math.pow( 10, s2.length() );
      }
      
      if( s3 != null )
      {
        byte esign = 1;
        if( s3.startsWith("-") )
        {
          esign = -1;
          s3 = s3.substring(1);
        }
        else if( s3.startsWith("+") )
        {
          s3 = s3.substring(1);
        }
        
        double exp = esign * (double)parseExactNumber( s3, (byte)10, (byte)0, (byte)(s3.length()) );
        
        result *= Math.pow( 10, exp );
      }
      
      return sign * result;
    }
    catch(PackagedScriptObjectException e)
    {
      throw PackagedScriptObjectException.makeExceptionWrapper( new ONoSuchMemberException("cannot convert \"" + segment.toString() + "\" to InexactNumber") );
    }
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
    return segment.toString();
  }
  
  /*=======================================================================*/
  /* The binary operators:
   */
  
  /*=======================================================================*/
  /**
   * Perform the "==" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopEquals( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OBoolean.makeBoolean( segment.toString().equals( val.castToString() ) );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopEqualsR( this, e );
    }
  }
  public Value bopEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( val.castToString().equals( segment.toString() ) );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "!=" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopNotEquals( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OBoolean.makeBoolean( ! segment.toString().equals( val.castToString() ) );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopNotEqualsR( this, e );
    }
  }
  public Value bopNotEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( ! val.castToString().equals( segment.toString() ) );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "<" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopLessThan( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OBoolean.makeBoolean( segment.toString().compareTo( val.castToString() ) < 0 );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopLessThanR( this, e );
    }
  }
  public Value bopLessThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( segment.toString().compareTo( val.castToString() ) > 0 );
  }
  
  /*=======================================================================*/
  /**
   * Perform the ">" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopGreaterThan( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OBoolean.makeBoolean( segment.toString().compareTo( val.castToString() ) > 0 );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopGreaterThanR( this, e );
    }
  }
  public Value bopGreaterThanR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( segment.toString().compareTo( val.castToString() ) < 0 );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "<=" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopLessThanOrEquals( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OBoolean.makeBoolean( segment.toString().compareTo( val.castToString() ) <= 0 );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopLessThanOrEqualsR( this, e );
    }
  }
  public Value bopLessThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( segment.toString().compareTo( val.castToString() ) >= 0 );
  }
  
  /*=======================================================================*/
  /**
   * Perform the ">=" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopGreaterThanOrEquals( Value val )
    throws PackagedScriptObjectException
  {
    try
    {
      return OBoolean.makeBoolean( segment.toString().compareTo( val.castToString() ) >= 0 );
    }
    catch(PackagedScriptObjectException e)
    {
      return val.bopGreaterThanOrEqualsR( this, e );
    }
  }
  public Value bopGreaterThanOrEqualsR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    return OBoolean.makeBoolean( segment.toString().compareTo( val.castToString() ) <= 0 );
  }
  
  /*=======================================================================*/
  /**
   * Perform the "+" operation.
   * 
   * @param val          the other value
   * @return the result
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   */
  public Value bopPlus( Value val )
    throws PackagedScriptObjectException
  {
    // val might well be, for example, a Reference, which will screw up
    // the instanceof below:
    val = val.unhand();
    
    try
    {
      Segment s;
      
      if( val instanceof OString )
      {
        s = ((OString)val).segment;
      }
      else
      {
        String str = val.castToString();
        
        if(DEBUG)
          if( str == null )
            throw new RuntimeException("this shouldn't happen, val.castToString() returns null for val=" + val + " (" + val.getType()+ ")");
        
        s = new StringSegment( val.castToString() );
      }
      
      return new OString( new ComboSegment( segment, s ) );
    }
    catch(PackagedScriptObjectException e )
    {
      return val.bopPlusR( this, e );
    }
  }
  public Value bopPlusR( Value val, PackagedScriptObjectException e )
    throws PackagedScriptObjectException
  {
    Segment s = ( (val instanceof OString) ?
                  ((OString)val).segment : 
                  new StringSegment( val.castToString() ) );
    return new OString( new ComboSegment( s, segment ) );
  }
  
  /*=======================================================================*/
  /* The misc operators:
   */
  
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
    return segment.length();
  }
  
  /*=======================================================================*/
  /**
   * Get the specified index of this object.  This makes a string behave as
   * an array, or at least support array indexing.
   * 
   * @param idx          the index to get
   * @return a string of length one
   * @throws PackagedScriptObjectException(NoSuchMethodException)
   * @see #length
   */
  public Value elementAt( Value oidx )
    throws PackagedScriptObjectException
  {
    int idx = (int)(oidx.castToExactNumber());
    checkIndex(idx);
    
    return new OString( "" + segment.toString().charAt(idx) );
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
    return new OString( substring( (int)(idx1.castToExactNumber()),
                                   (int)(idx2.castToExactNumber()) + 1 ) );
  }
  
  /*=======================================================================*/
  /* The regexp operators:
   */
  
  /*=======================================================================*/
  /**
   * Returns the same thing as <code>regexp.exec(this)</code>.  The
   * <code>regexp</code> should either be a <code>RegExp</code> object
   * or a string that can be compiled to a <code>RegExp</code> object.
   * <p>
   * Note: this API is modeled after the JavaScript RegExp API, for the 
   * benefit of users already familiar with JavaScript.
   * 
   * @param regexp       the regular expression
   * @return the result of <code>regexp.exec</code>.
   */
  public Value match( Value regexp )
  {
    return compile(regexp).exec(this);
  }
  
  /*=======================================================================*/
  /**
   * Finds a match between a regular expression and this string object, and 
   * replaces the matched substring with a new substring.  The second 
   * parameter can be either a replacement string, or a function called to 
   * determine the replacement string.
   * <p>
   * If the second parameter is a string, the following replacement patterns
   * are evaluated and replaced with the appropriate value:
   * <div id="regtable"><table>
   *    <tr><th>pattern</th><th>Description</th></tr>
   *    <tr><td><code>$$</code></td> <td>Inserts a <code>$</code></td></tr>
   *    <tr><td><code>$&</code></td> <td>Inserts the matched substring</td></tr>
   *    <tr><td><code>$`</code></td> <td>Inserts the portion of the string that precedes the matched substring</td></tr>
   *    <tr><td><code>$'</code></td> <td>Inserts the portion of the string that follows the matched substring</td></tr>
   *    <tr><td><code>$</code>n</td> <td>Inserts the nth parenthesized submatch string</td></tr>
   * </table></div>
   * If the second parameter is a function, it is called with the following
   * parameters:
   * <div id="regtable"><table>
   *    <tr><th>param</th><th>Description</th></tr>
   *    <tr><td>0</td>   <td>the matched string</td></tr>
   *    <tr><td>1-n</td> <td>zero or more parameters for parenthetical matches</td></tr>
   *    <tr><td>n+1</td> <td>offset of match</td></tr>
   *    <tr><td>n+2</td> <td>the original string</td></tr>
   * </table></div>
   * <p>
   * Note: this API is modeled after the JavaScript RegExp API, for the 
   * benefit of users already familiar with JavaScript.
   * 
   * @param regexp       the regular expression
   * @param strOrFxn     replacement string or function
   * @return the resulting string
   */
  public Value replace( Value regexp, Value strOrFxn )
  {
	  return replace(regexp,strOrFxn,false);
  }
  public Value replaceFirst( Value regexp, Value strOrFxn )
  {
	  return replace(regexp,strOrFxn,true);
  }

  private Value replace( Value regexp, Value strOrFxn,boolean onlyFirst )
  {
	if (regexp.castToJavaObject() instanceof String) 
	{
		if (onlyFirst)
			return OString.makeString(this.toString().replaceFirst(regexp.toString(), strOrFxn.toString()));
		else
			return OString.makeString(this.toString().replace(regexp.toString(), strOrFxn.toString()));
			
	}
	RegExp re = compile(regexp);
    OString ths = this;
    while (true)
    {
    	RegExpResult r = re.exec(ths);
        int idx = (int)(r.getIndex().castToExactNumber());
        if (idx == -1) {
        	break;
        } else {
          Value match = r.elementAt( JavaBridge.convertToScriptObject(0) );
          int len = match.length();
          
          String pre  = ths.substring( 0, idx );
          String post = ths.substring( idx+len, length() );
          String replace;
          
          if( strOrFxn instanceof Function )
          {
            int n = r.length();
            Value[] args = new Value[ n + 2 ];
            for( int i=0; i<n; i++ )
              args[i] = r.elementAt( JavaBridge.convertToScriptObject(i) );
            args[n]   = JavaBridge.convertToScriptObject(idx);
            args[n+1] = r.getInput();
            replace = strOrFxn.callAsFunction(args).castToString();
          }
          else
          {
            String str = strOrFxn.castToString();
            
            // handle replacement patterns:
            int escaped = 0;
            int n = -1;
            
            for( int i=0; i<str.length(); i++ )
            {
              char c = str.charAt(i);
              
              if( escaped > 0 )
              {
                String a = null;
                
                if( n == -1 )
                {
                  switch(c)
                  {
                    case '$':
                      a = "$";
                      escaped++;
                      break;
                    case '&':
                      a = match.castToString();
                      escaped++;
                      break;
                    case '`':
                      a = substring( 0, idx );
                      escaped++;
                      break;
                    case '\'':
                      a = substring( idx + len, length() );
                      escaped++;
                      break;
                    default:
                      if( Character.isDigit(c) )
                      {
                        n = Character.digit(c,10);
                        escaped++;
                      }
                  }
                }
                else
                {
                  if( Character.isDigit(c) )
                  {
                    n = (n*10) + Character.digit(c,10);
                    escaped++;
                  }
                  else
                  {
                    a = r.elementAt( JavaBridge.convertToScriptObject(n) ).castToString();
                    i -= 1;
                  }
                }
                
                if( a != null )
                {
                  str = str.substring( 0, i-escaped+1 ) + a + str.substring(i+1);
                  i += a.length() - escaped;
                  escaped = 0;
                  n = -1;
                }
              }
              else if( c == '$' )
              {
                escaped = 1;
              }
            }
            
            if( n != -1 )
            {
              String a = r.elementAt( JavaBridge.convertToScriptObject(n) ).castToString();;
              str = str.substring( 0, str.length() - escaped ) + a;
            }
            
            replace = str;
          }

          
          // XXX use StringSegments..
          ths = new OString( pre + replace + post );
        }
        if (onlyFirst)
        	break;
    }
    
    return ths;
  }
  
  /*=======================================================================*/
  /**
   * Executes the search for a match between a regular expression and this 
   * string object.
   * <p>
   * Note: this API is modeled after the JavaScript RegExp API, for the 
   * benefit of users already familiar with JavaScript.
   * 
   * @param regexp       the regular expression
   * @return the index of the match, or <code>-1</code> if none
   */
  public Value search( Value regexp )
  {
    return compile(regexp).exec(this).getIndex();
  }
  
  /**
   * utility function to compile a string to a RegExp, or if it is already
   * a RegExp, cast and return...
   */
  private static final RegExp compile( Value regexp )
  {
    if( regexp instanceof RegExp )
      return (RegExp)regexp;
    else
      return RegExp.createRegExp(regexp);
  }
  
  /*=======================================================================*/
  /* The string operators:
   */
  
  public String substring( int begIdx )
  {
    checkIndex(begIdx);
    return segment.toString().substring(begIdx);
  }
  
  public String substring( int begIdx, int endIdx )
  {
    if( begIdx < 0 )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("bad index: " + begIdx + ", length=" + length()) );
    if( endIdx > length() )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("bad index: " + endIdx + ", length=" + length()) );
    if( begIdx > endIdx )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("bad index: " + (endIdx - begIdx) + ", length=" + length()) );
    
    return segment.toString().substring(begIdx,endIdx);
  }
  
  public int indexOf( String str )
  {
    return segment.toString().indexOf(str);
  }
  
  public int indexOf( String str, int fromIdx )
  {
    checkIndex(fromIdx);
    return segment.toString().indexOf(str,fromIdx);
  }
  
  public int lastIndexOf( String str )
  {
    return segment.toString().lastIndexOf(str);
  }
  
  public int lastIndexOf( String str, int fromIdx )
  {
    checkIndex(fromIdx);
    return segment.toString().lastIndexOf(str,fromIdx);
  }
  
  public String toUpperCase()
  {
    return segment.toString().toUpperCase();
  }
  
  public String toLowerCase()
  {
    return segment.toString().toLowerCase();
  }
  
  public boolean startsWith( String str )
  {
    return segment.toString().startsWith(str);
  }
  
  public boolean endsWith( String str )
  {
    return segment.toString().endsWith(str);
  }
  
  public String trim()
  {
    return segment.toString().trim();
  }
  
  public OArray split( String regex )
  {
	  // NEW : limit -1 , do not skip empty at the end
	String[] r = segment.toString().split( regex,-1 );
	OArray or = new OArray(r.length);
	for (int i=0;i<r.length;i++)
		or.push1(new OString(r[i]));
    return or;
  }

  public OArray split( String regex, int limit )
  {
     String r[] = segment.toString().split( regex, limit );
	 OArray or = new OArray(r.length);
	 for (int i=0;i<r.length;i++)
		or.push1(new OString(r[i]));
     return or;
  }

  private final void checkIndex( int idx )
  {
    if( !((0 <= idx) && (idx < length())) )
      throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("bad index: " + idx + ", length=" + length()) );
  }
  
  /*=======================================================================*/
  /*=======================================================================*/
  /*=======================================================================*/
  
  /**
   * Return an intern'd OString....  document this better!
   */
  public static final OString makeString( String str )
  {
	  return new OString(str);
  }
  private static Hashtable interndTable; // key is str, val is ostr
  private static final synchronized OString makeString( OString ostr, String str )
  {
    if( interndTable == null )
      interndTable = new Hashtable();
    
    str = str.intern();  // XXX
    OString val = (OString)(interndTable.get(str));
    
    if( val == null )
    {
      if( ostr != null )
        val = ostr;
      else
        val = new OString(str);
      
      interndTable.put(str,val);
    }
    
    return val;
  }
  
  public static final String chop( String str )
  {
    int idx = str.indexOf('\\');
    
    if( idx == -1 )
    {
      return str;
    }
    else
    {
      char[] value = str.toCharArray();
      StringBuffer sb = new StringBuffer(value.length);
      int lastIdx=0;
      
      for( ; idx<value.length-1; idx++ )
      {
        if( value[idx] == '\\' )
        {
          char c = value[idx+1];
          
          if( c == 'n' )
            value[idx] = '\n';
          else if( c == 't' )
            value[idx] = '\t';
          else if( c == 'b' )
            value[idx] = '\b';
          else if( c == 'f' )
            value[idx] = '\f';
          else if( c == 'r' )
            value[idx] = '\r';
          else
            value[idx] = c;
          
          sb.append( value, lastIdx, ++idx - lastIdx );
          lastIdx = idx + 1;
        }
      }
      
      return sb.append( value, lastIdx, value.length - lastIdx ).toString();
    }
  }
  
  /*=======================================================================*/
  /*=======================================================================*/
  /*=======================================================================*/

  /**
   * The actual string contents.  This abstraction enables us to perform
   * some optimizations for string addition.
   * <p>
   * As a performance optimization, to speed up the performance of string
   * addition (concationation), strings can be comprised of "string-segments"
   * which get lazily converted to an actual string. 
   */
  private static abstract class Segment implements java.io.Serializable
  {
    abstract public int hashCode();
    abstract public String toString();
    abstract protected int length();
    abstract protected void appendTo( StringBuffer sb );
  }
  
  public static class StringSegment extends Segment
  {
    private String stringVal;
    private int    hashCode;
    
    public StringSegment( String stringVal )
    {
      this.stringVal = stringVal;
    }
    
    public int hashCode()
    {
      if( hashCode == 0 )
        hashCode = stringVal.hashCode();
      return hashCode;
    }
    public String toString() { return stringVal; }
    protected int length() { return stringVal == null ? 0 : stringVal.length(); }
    protected void appendTo( StringBuffer sb ) { if (stringVal != null) sb.append(stringVal); }
  }
  
  private static class ComboSegment extends Segment
  {
    private Segment s1;
    private Segment s2;
    
    private String stringVal = null;  // cache combined s1+s2
    private int hashCode;             // cache hashcode
    
    ComboSegment( Segment s1, Segment s2 )
    {
      this.s1 = s1;
      this.s2 = s2;
    }
    
    public int hashCode()
    {
      if( stringVal == null )
        flatten();
      if( hashCode == 0 )
        hashCode = stringVal.hashCode();
      return hashCode;
    }
    
    public String toString()
    {
      if( stringVal == null ) flatten();
      return stringVal;
    }
    
    protected synchronized int length()
    {
      if( stringVal == null )
        return s1.length() + s2.length();
      else
        return stringVal.length();
    }
    
    protected synchronized void appendTo( StringBuffer sb )
    {
      if( stringVal == null )
      {
        s1.appendTo(sb);
        s2.appendTo(sb);
      }
      else
      {
        sb.append(stringVal);
      }
    }
    
    private synchronized void flatten()
    {
      if( stringVal == null ) // do check here to, cause it's sync'd
      {
    	// TODO FIX LATER, CAUSED AN NULL POINTER BUG SOMEWHERE !
        StringBuffer sb;
        try {
        	sb = new StringBuffer(length());
        } catch (Exception e){
        	sb = new StringBuffer();
        }
        appendTo(sb);
        String tmp = sb.toString();
        stringVal = tmp;    /* this step has to be last, because
                             * an unsynchronized access to stringVal
                             * is used to determin if init() is
                             * complete... weird, but avoids needing
                             * a state variable in an object we want
                             * to keep lightweight.
                             */
        s1 = s2 = null;     /* post-flatten cleanup, allow to be GC'd
                             */
      }
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

