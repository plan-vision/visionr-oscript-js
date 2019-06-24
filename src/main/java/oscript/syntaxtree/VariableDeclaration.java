//
// Generated by JTB 1.2.1
//

package oscript.syntaxtree;

/**
 * Grammar production:
 * <PRE>

 * f0 -> Permissions(true)
 * f1 -> "var"
 * f2 -> &lt;IDENTIFIER&gt;
 * f3 -> ( "=" Expression() )?
 * </PRE>
 */
public class VariableDeclaration implements Node {
   public Permissions f0;
   public NodeToken f1;
   public NodeToken f2;
   public NodeOptional f3;

   public VariableDeclaration(Permissions n0, NodeToken n1, NodeToken n2, NodeOptional n3) {
      f0 = n0;
      f1 = n1;
      f2 = n2;
      f3 = n3;
   }

   public VariableDeclaration(Permissions n0, NodeToken n1, NodeOptional n2) {
      f0 = n0;
      f1 = new NodeToken("var");
      f2 = n1;
      f3 = n2;
   }

   public void accept(oscript.visitor.Visitor v) {
      v.visit(this);
   }
   public Object accept(oscript.visitor.ObjectVisitor v, Object argu) {
      return v.visit(this,argu);
   }
}

