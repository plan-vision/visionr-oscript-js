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


import java.util.Iterator;

/**
 * This utility class provides a more Hashtable-like interface to 
 * {@link SymbolTable}, which normally maps a symbol to a table index.
 * <p>
 * Threading note: this class is not synchronized, but is designed to
 * save to read from multiple threads, while write from a single thread
 * context (at a time).
 * 
 * @author Rob Clark (rob@ti.com)
 * @version 0.0
 * @see SymbolTable
 */
public class SymbolMap
{
  private final SymbolTable table;
  private Object[] values;
  
  /**
   * Class Constructor
   */
  public SymbolMap()
  {
    this( new OpenHashSymbolTable() );
  }
  
  /**
   * Class Constructor
   * 
   * @param table   the underlying table data structure
   */
  public SymbolMap( SymbolTable table )
  {
    this.table = table;
    this.values = new Object[ (table.size() > 0) ? table.size() : 10 ];
  }
  
  /**
   * Get a mapping
   */
  public final Object get( int id )
  {
    int idx = table.get(id);
    if( (idx == -1) || (idx >= values.length) )
      return null;
    return values[idx];
  }
  
  /**
   * Put a new mapping in the table
   */
  public final Object put( int id, Object val )
  {
    int idx = table.create(id);
    
    // grow if needed:
    if( idx >= values.length )
    {
      Object[] newValues = new Object[ values.length * 2 ];
      System.arraycopy( values, 0, newValues, 0, values.length );
      values = newValues;
    }
    
    Object ret = values[idx];
    values[idx] = val;
    return ret;
  }
  
  /**
   * Return an iterator of keys into the table.  Each key is boxed
   * in an {@link Integer}.
   */
  public Iterator keys()
  {
    return table.symbols();
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

