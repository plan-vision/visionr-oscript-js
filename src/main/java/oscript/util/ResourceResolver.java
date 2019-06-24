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

import java.net.URL;



/**
 * Sort of a hack to deal with eclipse, which needs to call some
 * eclipse core APIs to convert a plugin URL to a native URL.
 * 
 * @author Rob Clark (rob@ti.com)
 * @version 1
 */
public abstract class ResourceResolver
{
  private static ResourceResolver resolver = new ResourceResolver() {
    public URL resolveImpl( URL url ) { return url; }
  };
  
  public static void setResourceResolver( ResourceResolver resolver )
  {
    ResourceResolver.resolver = resolver;
  }
  
  public static URL resolve( URL url )
  {
    return resolver.resolveImpl(url);
  }
  
  protected abstract URL resolveImpl( URL url );
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

