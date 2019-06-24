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
 */


package oscript.data;

import oscript.exceptions.*;


/**
 * An <code>OSpecial</code> is used for different special values that
 * aren't really members of any other type.  The values of Special are
 * unique, and special care is taken when constructing, and serializing
 * and unserializing the special to ensure that this property is
 * preservied... ie. there are never two instances of <code>NULL</code>.
 * 
 * @author Rob Clark (rob@ti.com)
 */
class OSpecial extends Value
{
  private static java.util.Hashtable specials = new java.util.Hashtable();
  
  private String str;
  
  static Value makeSpecial( String str )
  {
    Value val = null;
    
    synchronized(specials)
    {
      val = (Value)(specials.get(str));
      
      if( val == null )
      {
        val = new OSpecial(str);
        specials.put( str, val );
      }
    }
    
    return val;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param str          the string representation of this special
   */
  protected OSpecial( String str )
  {
    super();
    
    synchronized(specials)
    {
      if( specials.get(str) != null )
        throw new RuntimeException("duplicate special!");
    }
    
    this.str = str;
  }
  
  /*=======================================================================*/
  /**
   * Class Constructor.
   * 
   * @param args         arguments to this constructor
   * @throws PackagedScriptObjectException(Exception) if wrong number of args
   */
  public OSpecial( oscript.util.MemberTable args )
  {
    super();
    
    // you can't instantiate this!
    throw PackagedScriptObjectException.makeExceptionWrapper( new OIllegalArgumentException("wrong number of args!") );
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
    return this;
  }
  
  /*=======================================================================*/
  /**
   * If this object is a type, determine if an instance of this type is
   * an instance of the specified type, ie. if this is <code>type</code>,
   * or a subclass.
   * 
   * @param type         the type to compare this type to
   * @return <code>true</code> or <code>false</code>
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public boolean isA( Value type )
  {
    return bopEquals(type).castToBoolean();
  }
  
  /*=======================================================================*/
  /**
   * Convert this object to a native java <code>String</code> value.
   * 
   * @return a String value
   * @throws PackagedScriptObjectException(NoSuchMemberException)
   */
  public String castToString()
    throws PackagedScriptObjectException
  {
    return str;
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
    return null;
  }
  
  protected PackagedScriptObjectException noSuchMember( String member )
  {
    return PackagedScriptObjectException.makeExceptionWrapper( 
    		new ONoSuchMemberException(getType(),member)	// SOME MORE SPECIFIC INFORMATION
    		//new ONullReferenceException(str) 
    );
  }
  
  protected PackagedScriptObjectException noSuchMember( Value type,String member )
  {
    return PackagedScriptObjectException.makeExceptionWrapper( 
    		new ONoSuchMemberException(type,member)
    		//new ONullReferenceException(str) 
    );
  }
  
  /*=======================================================================*/
  // maintains unique-ness of a OSpecial:
  Object readResolve()
    throws java.io.ObjectStreamException
  {
    Object obj;
    
    synchronized(specials)
    {
      obj = specials.get(str);
      
      if( obj == null )
      {
        specials.put( str, this );
        obj = this;
      }
    }
    
    return obj;
  }

  //SPECIAL FIX FOR IS_EMPTY OPERATOR (returns allways true)
  private static final int IS_EMPTY = Symbol.getSymbol("is_empty").getId();
  private static final int IS_EMPTY2 = Symbol.getSymbol("isEmpty").getId();
  public Value getMember(int id, boolean exception) throws PackagedScriptObjectException 
  {
		if (id == IS_EMPTY || id == IS_EMPTY2) 
		{
	  		return new FunctionValueWrapper(OBoolean.makeBoolean(true));
		}
  		return super.getMember(id,exception);
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

