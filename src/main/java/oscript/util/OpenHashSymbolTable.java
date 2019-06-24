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
 * A symbol table implementation based on a open hash map, using double
 * hashing as a strategy to avoid collisions.  Double hasing uses two hash
 * functions to compute an index into the table:
 * <pre>
 *    Idx(i,k) := H1(k) + i * H2(k)
 * </pre>
 * where for a given key <code>k</code>, <code>i</code> is incremented from
 * <code>0</code> until an unused table slot is found.
 * <p>
 * Threading note: this class is not synchronized, but is designed to
 * save to read from multiple threads, while write from a single thread
 * context (at a time).
 * 
 * @author Rob Clark (rob@ti.com)
 * @version 1.0
 */
public class OpenHashSymbolTable
  implements SymbolTable, java.io.Externalizable
{
  /**
   * The loading factor, the capacity must always be size * load
   */
  private /*final*/ float load;
  
  /**
   * The state is maintained in an inner class, so it can be automically
   * updated when we have to resize the the tables.
   */
  private State state;
  
  private class State
  {
    /**
     * The number of mappings that exist in the table.
     */
    public int size;
    
    /**
     * Once the <code>size</code> exceeds the threshold, it is time to 
     * grow the table and re-hash.
     */
    public final int threshold;
    
    /**
     * A key value of <code>0</code> in the <code>keys</code> indicates an 
     * unused slot.
     */
    public final int[] keys;
    public final int[] vals;
    
    /**
     * State constructor
     */
    State( int capacity )
    {
      capacity  = checkCapacity(capacity);
      threshold = (int)(load * (float)capacity);
      size = 0;
      keys = new int[capacity];
      vals = new int[capacity];
    }
  }
  
  /**
   * Class Constructor.
   */
  public OpenHashSymbolTable()
  {
    this( 10, 0.75f );
  }
  
  /**
   * Class Constructor.
   * 
   * @param capacity     the initial capacity
   * @param load         the loading factor
   */
  public OpenHashSymbolTable( int capacity, float load )
  {
    this.load = load;
    
    if( capacity < 2 )
      capacity = 2;
    
    state = new State(capacity);
    
    if(COLLECT_STATS) logIncreaseCapacity( state.keys.length );
  }
  
  /**
   */
  public float getLoad() { return load; }
  
  /**
   * Get the index that the specified symbol maps to.
   * 
   * @param id           the id of the symbol to get a mapping for
   * @return an index, or <code>-1</code> if no mapping exists for the
   *    specified symbol
   */
  public final int get( int id )
  {
    State state = this.state;
    
    if(COLLECT_STATS) logGet();
    
    if( oscript.data.Value.DEBUG )
      if( id < MIN_SYMBOL_ID )
        throw new RuntimeException("bad id: " + id);
    
    int n = h1( id, state.keys.length );
    int k = 0;
    
    if(COLLECT_STATS) logLookup();
    
    for( int i=0; i<state.keys.length; i++ )
    {
      if(COLLECT_STATS) logProbe();
      
      if( state.keys[n] == 0 )
        return -1;
      
      if( state.keys[n] == id )
        return state.vals[n];
      
      if( k == 0 )
        k = h2( id, state.keys.length );
      
      n = Math.abs(n + k) % state.keys.length;
    }
    
    return -1;
  }
  
  /**
   * Get the index that the specified symbol maps to, and create a new one 
   * if a mapping does not already exist.  If a new mapping is created, 
   * it's value is the next successive array index, ie. the the previous
   * array index plus one.  The first mapping created has the value zero.
   * 
   * @param id           the id of the symbol to get a mapping for
   * @return an index
   */
  public int create( int id )
  {
    State state = this.state;
    
    if(COLLECT_STATS) logCreate();
    
    if( oscript.data.Value.DEBUG )
      if( id < MIN_SYMBOL_ID )
        throw new RuntimeException("bad id: " + id);
    
    // first ensure capacity... assume we actually will be creating
    // a new entry, because that is the common case:
    if( (state.size+1) >= state.threshold )
    {
      int[] oldkeys = state.keys;
      int[] oldvals = state.vals;
      
      // reset to new bigger size:
      State newState = new State( 2 * oldkeys.length );
      
      if(COLLECT_STATS) logIncreaseCapacity( state.keys.length - oldkeys.length );
      
      for( int n=0; n<oldkeys.length; n++ )
        if( oldkeys[n] != 0 )
          putIfNotIn( newState, oldkeys[n], oldvals[n] );
      
      // automically update state:
      this.state = state = newState;
    }
    
    return putIfNotIn( state, id, state.size );
  }
  
  private static final int putIfNotIn( State state, int id, int val )
  {
    int n = h1( id, state.keys.length );
    int k = 0;
    
    if(COLLECT_STATS) logPutImpl();
    if(COLLECT_STATS) logLookup();
    
    for( int i=0; i<state.keys.length; i++ )
    {
      if(COLLECT_STATS) logProbe();
      
      if( state.keys[n] == 0 )
      {
        // order is important here, because in the common case this will
        // not be synchronized:
        state.vals[n] = val;
        state.keys[n] = id;
        if(COLLECT_STATS) logAdd();
        state.size++;
        return state.vals[n];
      }
      
      if( state.keys[n] == id )
        return state.vals[n];
      
      if( k == 0 )
        k = h2( id, state.keys.length );
      
      n = (n + k) % state.keys.length;
    }
    
    throw new RuntimeException("shouldn't get here if load factor is less than one!!");
  }
  
  /**
   * Some statistics gathering code... all calls to the stat gathering methods
   * should be wrapped with an if(COLLECT_STATS) so it gets compiled out for
   * a regular build.
   */
  
  private static final boolean COLLECT_STATS = false;
  
  private static final synchronized void logGet()     { numGets++; }
  private static final synchronized void logCreate()  { numCreates++; }
  private static final synchronized void logPutImpl() { numPutImpl++; }  // including re-hashing
  private static final synchronized void logLookup()  { numLookups++; }
  private static final synchronized void logProbe()   { numProbes++; }
  private static final synchronized void logAdd()     { totalSize++; }
  private static final synchronized void logIncreaseCapacity( int amount ) { totalCapacity += amount; }
  
  private static int numGets       = 0;
  private static int numCreates    = 0;
  private static int numPutImpl    = 0;
  private static int numLookups    = 0;
  private static int numProbes     = 0;
  private static int totalSize     = 0;
  private static int totalCapacity = 0;
  
  public static final synchronized String getStats()
  {
    return ("OpenHashSymbolTable stats\n" +
            "------------------- -----\n" +
            "  numGets:            " + numGets + "\n" +
            "  numCreates:         " + numCreates + "\n" +
            "  numPutImpl:         " + numPutImpl + " (" + (numPutImpl - numCreates) + " due to rehash)\n" +
            "  numLookups:         " + numLookups + "\n" +
            "  numProbes:          " + numProbes + "\n" +
            "  avgProbesPerLookup: " + (((float)numProbes)/((float)numLookups)) + "\n" +
            "  totalSize:          " + totalSize + "\n" +
            "  totalCapacity:      " + totalCapacity + "\n");
  }
  
  /**
   * The number of mappings that exist in this table.
   * 
   * @return the number of mappings in the table
   */
  public int size()
  {
    return state.size;
  }
  
  /**
   * Return an iteration of the keys (symbols) into this table.  To conform to
   * the {@link java.util.Iterator} interface, each symbol is wrapped (boxed)
   * in a {@link Integer}.
   * 
   * @return an iteration of symbols that are keys into this table
   */
  public synchronized java.util.Iterator symbols()
  {
    return new java.util.Iterator() {
        
        private int     idx = -1;
        private int[]  keys = OpenHashSymbolTable.this.state.keys;
        private Object _next = null;
        
        public boolean hasNext()
        {
          refresh();
          return _next != null;
        }
        
        public Object next()
        {
          if( !hasNext() )
            throw new java.util.NoSuchElementException("no more elements");
          
          refresh();
          
          Object r = _next;
          _next = null;
          return r;
        }
        
        private void refresh()
        {
          if( _next == null )
          {
            while( ++idx < keys.length )
            {
              if( keys[idx] != 0 )
              {
                _next = Integer.valueOf( keys[idx] );
                return;
              }
            }
          }
        }
        
        public void remove()
        {
          throw new UnsupportedOperationException("remove");
        }
      };
  }
  
  /* 
   * The hash functions... for now I'm using endianess swap as the primary
   * hash function (which should deal well with symbol ids from 0 to n...
   * ideal would probably be a bitwise reverse, but I think this is good
   * enough
   * 
   * These hashing functions rely on the table size being a prime number.  
   * Using an alternate version of h2() could result in this restriction
   * being lifted, but I am not sure which approach performs better, so
   * some benchmarking is probably in order...
   */
  private static final int h1( int k, int capacity )
  {
    int v = ( ((k & 0xff000000) >> 24) |
              ((k & 0x00ff0000) >> 8) |
              ((k & 0x0000ff00) << 8) |
              ((k & 0x000000ff) << 24) );
    
    v %= capacity;
    
    if( v < 0 )
      v = -v;
    
    return v;
  }
  
  private static final int h2( int k, int capacity )
  {
    return 1 + (k % (capacity - 2));
  }
  
  private final static int checkCapacity( int sz )
  {
    int idx = java.util.Arrays.binarySearch( Primes.PRIMES, sz );
    if( idx < 0 )
      idx = -idx - 1;
    
    // XXX choose closest prime number, rather than next largest:
    if( (idx > 0) && ((Primes.PRIMES[idx] - sz) > (sz - Primes.PRIMES[idx-1])) )
      idx--;
    // XXX
    
    return Primes.PRIMES[idx];
  }
  
  /*=======================================================================*/
  public void readExternal( java.io.ObjectInput in )
    throws java.io.IOException
  {
    load = in.readFloat();
    
    int capacity = in.readInt();
    
    State state = new State(capacity);
    for( int i=0; i<capacity; i++ )
    {
      state.keys[i] = in.readInt();
      state.vals[i] = in.readInt();
    }
    
    state.size = in.readInt();
    
    this.state = state;
  }
  
  public void writeExternal( java.io.ObjectOutput out )
    throws java.io.IOException
  {
    out.writeFloat(load);
    
    State  state = this.state;
    int capacity = state.keys.length;
    
    out.writeInt(capacity);
    
    for( int i=0; i<capacity; i++ )
    {
      out.writeInt( state.keys[i] );
      out.writeInt( state.vals[i] );
    }
    
    out.writeInt(state.size);
  }
  /*=======================================================================*/
  
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

