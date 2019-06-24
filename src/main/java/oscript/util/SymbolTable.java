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


package oscript.util;

/**
 * This defines the interface for a table that maps a symbol (integer id) to 
 * an array index.  This is similar to a hashtable, except that allows for
 * a couple domain specific interface level optimizations:
 * <ul>
 *   <li> the value that a symbol maps to is an int <code>idx</code> where 
 *     <code>0 <= idx <= Integer.MAX_VALUE</code>.  This means that an 
 *     <code>idx</code> value of <code>-1</code> can be (and is) used by
 *     {@link #get} to indicate that the table doesn't contain the specified 
 *     symbol.  
 *   <li> since the table knows that the value of the next entry created
 *     is equal to the last plus one (ie. the next successive array index)
 *     the act of checking for the existance of a mapping (get), and 
 *     creating a new mapping if one doesn't already exist (put) can be
 *     combined int a single {@link #create} operation.
 * </ul>
 * 
 * @author Rob Clark (rob@ti.com)
 * @version 1.0
 * @see SymbolMap
 */
public interface SymbolTable
{
  /**
   * Currently the symbol id of zero is reserved for use by the symbol table
   * implementation, and in the future could (hypothetically, at least) add
   * more, therefore the minimum symbol id must be <code>MIN_SYMBOL_ID</code>.
   * 10-500 : reserved for VScript
   */
  public static int MIN_SYMBOL_ID = 500;
  
  /**
   * Get the index that the specified symbol maps to.
   * 
   * @param id           the id of the symbol to get a mapping for
   * @return an index, or <code>-1</code> if no mapping exists for the
   *    specified symbol
   */
  public int get( int id );
  
  /**
   * Get the index that the specified symbol maps to, and create a new one 
   * if a mapping does not already exist.  If a new mapping is created, 
   * it's value is the next successive array index, ie. the the previous
   * array index plus one.  The first mapping created has the value zero.
   * 
   * @param id           the id of the symbol to get a mapping for
   * @return an index
   */
  public int create( int id );
  
  /**
   * The number of mappings that exist in this table.
   * 
   * @return the number of mappings in the table
   */
  public int size();
  
  /**
   * Return an iteration of the keys (symbols) into this table.  To conform to
   * the {@link java.util.Iterator} interface, each symbol is wrapped (boxed)
   * in a {@link Integer}.
   * 
   * @return an iteration of symbols that are keys into this table
   */
  public java.util.Iterator symbols();
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

