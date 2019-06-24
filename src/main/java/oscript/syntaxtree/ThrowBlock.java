//
// Generated by JTB 1.2.1
//

package oscript.syntaxtree;

/**
 * Grammar production:
 * <PRE>
 * f0 -> "throw"
 * f1 -> Expression()
 * f2 -> ";"
 * </PRE>
 */
public class ThrowBlock implements Node {
   public NodeToken f0;
   public Expression f1;
   public NodeToken f2;

   public ThrowBlock(NodeToken n0, Expression n1, NodeToken n2) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
   }

   public ThrowBlock(Expression n0) {
      f0 = new NodeToken("throw");
      f1 = n0;
      f2 = new NodeToken(";");
   }

   public void accept(oscript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(oscript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

