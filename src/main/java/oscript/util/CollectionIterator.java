/*=============================================================================
 *     Copyright Texas Instruments 2004.  All Rights Reserved.
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

import java.util.*;


/**
 * A wrapper for {@link Iterator} which adds an {@link #iterator} method.  This
 * is basically a hack so methods that return an Iterator can be be used in a
 * foreach style for loop.
 * 
 * @author Rob Clark (rob@ti.com)
 * @version 1
 */
public class CollectionIterator
  implements Iterator
{
  private Iterator itr;
  
  public CollectionIterator( Iterator itr )
  {
    this.itr = itr;
  }
  
  public boolean hasNext()
  {
    return itr.hasNext();
  }
  
  public Object next()
  {
    return itr.next();
  }
  
  public void remove()
  {
    itr.remove();
  }
  
  public Iterator iterator()
  {
    return itr;
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

