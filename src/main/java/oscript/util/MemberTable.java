/*=============================================================================
 *     Copyright Texas Instruments 2005.  All Rights Reserved.
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


package oscript.util;

import oscript.data.*;


/**
 * A member table is a special array, with some special methods that don't
 * need to exist for regular script arrays:
 * <ul>
 *   <li> direct access to the Reference
 *   <li> pushN() methods
 * </ul>
 * Since the member table is used by scope and to pass args to a function/
 * constructor, performance is critical, which is the reason for some of these
 * methods.
 * 
 * @author Rob Clark (rob@ti.com)
 * @version 1
 */
public interface MemberTable
{
  /**
   * Return the reference at the specified index.  This does not necessarily 
   * grow the array, so the user should be sure to use 
   * {@link #ensureCapacity(int)} to ensure the array has sufficient capacity 
   * before dereferencing an index into the table which is not known to exist.
   * 
   * @param idx   an index into the member-table
   * @return a reference
   */
  public Reference referenceAt( int idx );
  
  /**
   * Ensure that the member-table has sufficient capacity to accomodate the
   * index <code>sz</code>.  Grow the array, if necessary.
   * 
   * @param sz   the requested table size
   */
  public void ensureCapacity( int sz );
  
  /**
   * Indication to the member-table that a "safe" copy is required.  This
   * means that the table may need to outlive the stack-frame that it was
   * (possibly) allocated from.  What it means to convert this table into
   * a "safe" copy depends on the implementation of the table.  A safe
   * copy is still valid after {@link #free()} is called.
   * @return a safe copy of this table
   */
  public MemberTable safeCopy();
  
  /**
   * Push a single parameter into the table.
   * @param val   the value to push
   */
  public void push1( Value val );
  
  /**
   * Push two values into the table.
   * 
   * @param val1  the value to push
   * @param val2  the value to push
   */
  public void push2( Value val1, Value val2 );
  
  /**
   * Push three values into the table.
   * 
   * @param val1  the value to push
   * @param val2  the value to push
   * @param val3  the value to push
   */
  public void push3( Value val1, Value val2, Value val3 );
  
  /**
   * Push four values into the table.
   * 
   * @param val1  the value to push
   * @param val2  the value to push
   * @param val3  the value to push
   * @param val4  the value to push
   */
  public void push4( Value val1, Value val2, Value val3, Value val4 );
  
  /**
   * An indication from the creator of the member-table that, while the table
   * itself is still required, the references referred to by the table are
   * no longer required and can be freed.
   */
  public void reset();
  
  /**
   * Indication from creator of member-table that resources allocated from
   * the stack are no longer needed and should be released.  (If the member
   * table is needed after this point, a safe copy should have already been
   * obtained by calling {@link #safeCopy()}.)
   */
  public void free();
  
  /**
   * Get the current size of the member-table.  The maximum index which
   * can be referenced via {@link #referenceAt(int)} is <code>length()-1</code>
   * 
   * @return the current size
   */
  public int length();
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

