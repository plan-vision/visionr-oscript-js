/*=============================================================================
 *     Copyright Texas Instruments 2000.  All Rights Reserved.
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


import java.util.Hashtable;
import java.io.*;


/**
 * The Symbol is a type used internally by the scripting engine to represent
 * identifiers, ie. variable or function names.  Each symbol has a unique
 * integer id.  Furthermore, the mapping between symbols and ids is preserved
 * in <i>/cache</i> so any cache entries (ie. compiled code, etc.) can assume
 * that the same mapping between symbol and id exists as when it was created.
 * (In other words, compiled code can discard the symbol object, and put the
 * symbol's id in it's constant table.)
 * 
 * @author Rob Clark (rob@ti.com)
 * @version 1.56
 */
public class Symbol
  extends OString
  implements Externalizable
{
	//private static final VLog log = VLog.getVLog( LogModule.CORE_ALL, Symbol.class );

	/**
   * This symbol's unique id.
   */
  private int id;
  
  /**
   * Class Constructor.  In order to ensure that the str to id mapping is
   * consistent, you cannot directly create an instance of a symbol.  Instead
   * you must go through {@link #getSymbol}.
   * 
   * @param str          the string representation
   * @param id           the unique id
   */
  private Symbol( String str, int id )
  {
    super(str);
    this.id = id;
  }
  
  public Symbol() {} // for serialization
  
  /**
   * Get the unique id for this symbol.  If two symbols are equal (in
   * terms of {@link #bopEquals}), then they will map to the same id, 
   * and likewise the id will always map to symbols that are equal.
   * 
   * @return the integer id
   */
  public int getId()
  {
    return id;
  }
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void readExternal( ObjectInput in )
    throws IOException
  {
    super.readExternal(in);
    id = in.readInt();
  }
  
  /**
   * Derived class that implements {@link java.io.Externalizable} must
   * call this if it overrides it.  It should override it to save/restore
   * it's own state.
   */
  public void writeExternal( ObjectOutput out )
    throws IOException
  {
    super.writeExternal(out);
    out.writeInt(id);
  }
  
  /* probably should have readResolve(), so symbols that are equal will
   * be equal in terms of the java == operator... for now that doesn't
   * matter anywhere, so for now I'll leave out readResolve()
   */
  
  /**
   * Maps string to symbol.  Also contains a few extra fields which contain
   * characters that are not legal identifiers:
   * <ul>
   *   <li> "<lastId>"  -  tracks the last used id value
   *   <li> "<#>"       -  where # is some integer id number, maps to the
   *     symbol object with the same id 
   * </ul>
   */
  private static Symbols tbl = new Symbols();
  
  private static class Symbols
  {
    int       lastId;
    Hashtable strToIdTable;
    Symbol[]  symbols;
    
    // note: don't initialize fields in constructor, because (hopefully)
    //       the common case will be serialization, not construction
    Symbols()
    {
      lastId       = oscript.util.SymbolTable.MIN_SYMBOL_ID;
      strToIdTable = new Hashtable();
      symbols      = new Symbol[lastId+100];
    }
  }
  static {
      oscript.data.Symbols.init();
  }
  
  /**
   * Given a script object, return a symbol object.  If two objects have
   * the same string representation ({@link #castToString}) then they will
   * always map to the same symbol.
   */
  public static Symbol getSymbol( Value val )
  {
    if( val instanceof Symbol )
      return (Symbol)val;
    return getSymbol( val.castToString() );
  }
  
  /**
   * Given a string, return a symbol object.  If two strings are equals()
   * then they will always map to the same symbol.
   */
  public synchronized static void setSymbol( int id,String str) {
	  if (tbl.symbols[id] != null)
		 throw new RuntimeException("Symbol already defined : "+id+" | "+str); 
      tbl.symbols[id] = new Symbol( str, id );
      if (tbl.strToIdTable.contains(str))
 		 throw new RuntimeException("Symbol already defined (str) : "+id+" | "+str); 
      tbl.strToIdTable.put( str, id );
  }

  
  public synchronized static Symbol getSymbol( String str )
  {
    Integer iid = (Integer)(tbl.strToIdTable.get(str));
    if( iid == null )
    {
      int id = ++tbl.lastId;
      iid = Integer.valueOf(id);
      
      if( id >= tbl.symbols.length )
      {
        Symbol[] tmp = new Symbol[2*id];
        System.arraycopy( tbl.symbols, 0, tmp, 0, tbl.symbols.length );
        tbl.symbols = tmp;
      }
      
      tbl.symbols[id] = new Symbol( str, id );
      tbl.strToIdTable.put( str, iid );
      //log.warn(" N E W   S Y M B O L : "+str+" ID="+id);
    }
    return tbl.symbols[ iid.intValue() ];
  }
  
  /**
   * Given a symbol id, return the symbol object.  The same integer id will
   * always map to the same symbol.  If a symbol with the specified id does
   * not exist, this will return <code>null</code>.
   */
  public static Symbol getSymbol( int id )
  {
    if( id >= tbl.symbols.length )
      return null;
    return tbl.symbols[id];
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

