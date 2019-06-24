//
// Generated by JTB 1.2.1
//

package oscript.syntaxtree;

import java.util.*;

import oscript.data.OString;
import oscript.data.Value;
import oscript.parser.Token;

/**
 * Represents a single token in the grammar.  If the "-tk" option
 * is used, also contains a Vector of preceding special tokens.
 */
public class NodeToken implements Node {
    
   public NodeToken(String s) {
      this(s, null , null);    }

   public NodeToken(OString os) {
      this(null, os, null);    }

   public NodeToken(String s, OString os, Token t) {
      tokenImage = s;
      otokenImage = os;
      specialTokens = null;
      
      if( t != null ) {
          this.kind = t.kind;
          this.beginLine = t.beginLine;
          this.beginColumn = t.beginColumn;
          //this.beginOffset = t.beginOffset;
          this.endLine = t.endLine;
          this.endColumn = t.endColumn;
          //this.endOffset = t.endOffset;
      }
    
   }

   public NodeToken getSpecialAt(int i) {
      if ( specialTokens == null )
         throw new NoSuchElementException("No specials in token");
      return (NodeToken)specialTokens.elementAt(i);
   }

   public int numSpecials() {
      if ( specialTokens == null ) return 0;
      return specialTokens.size();
   }

   public void addSpecial(NodeToken s) {
      if ( specialTokens == null ) specialTokens = new Vector();
      specialTokens.addElement(s);
   }

   public void trimSpecials() {
      if ( specialTokens == null ) return;
      specialTokens.trimToSize();
   }

   public String toString()     { return tokenImage; }

   public String withSpecials() {
      if ( specialTokens == null )
          return tokenImage;

       StringBuffer buf = new StringBuffer();

       for ( Enumeration e = specialTokens.elements(); e.hasMoreElements(); )
          buf.append(e.nextElement().toString());

       buf.append(tokenImage);
       return buf.toString();
   }

   public void accept(oscript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(oscript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }

   public String tokenImage;
   public OString otokenImage;
   
   public Value   cachedValue;
   
   // Stores a list of NodeTokens
   public Vector specialTokens;
    
   // offset into document stream
   public int off;
   
   // -1 for these ints means no position info is available.
   public int beginLine = -1, beginColumn = -1, beginOffset = -1, endLine = -1, endColumn = -1, endOffset = -1;
   
   // begin offset including specials
   private int actualBeginOffset = -1;
   
   public int getActualBeginOffset() {
       if( actualBeginOffset == -1 ) {
           if( (specialTokens == null) || (specialTokens.size() == 0) ) {
               actualBeginOffset = beginOffset;
           } else {
               actualBeginOffset = getSpecialAt(0).beginOffset;
           }
       }
       return actualBeginOffset;
   }
   
   // Equal to the JavaCC token "kind" integer.
   // -1 if not available.
   public int kind;
}

