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

package oscript.translator;

import oscript.syntaxtree.*;


/**
 * A translator to implement one part of the language in terms of another.
 * This simplifies the compiler/interpreter implementation.  This translates
 * from:
 * <pre>
 *   "'{" Program "}"
 * </pre>
 * to
 * <pre>
 *   "function" "(" "args..." ")" "{" Program "}"
 * </pre>
 * 
 * <!--
 * Note that current implementation substitutes NodeToken-s for the most
 * similar NodeToken in the input.  This makes row/col #'s the most sane,
 * but the string representation may be wrong, for example "function"
 * instead of "var".  I did it this way since it is the least overhead,
 * which matters for the interpreter.  That should work fine with the
 * current interpreter and compiler implementations
 * -->
 * 
 * @author Rob Clark
 * @version 0.1
 */
public class ShorthandFunctionPrimaryPrefixTranslator
{
  private final static oscript.parser.Token ARGS_TOKEN = new oscript.parser.Token();
  private final static oscript.parser.Token DOTDOTDOT_TOKEN = new oscript.parser.Token();
  static {
    ARGS_TOKEN.kind = oscript.parser.OscriptParserConstants.IDENTIFIER;
    DOTDOTDOT_TOKEN.kind = oscript.parser.OscriptParserConstants.DOTDOTDOT;
  }
  private final static NodeOptional ARGLIST = 
    new NodeOptional(
      new Arglist(
        new Permissions( new NodeListOptional() ),
	new NodeToken( "args", oscript.data.OString.makeString("args"), ARGS_TOKEN),
	new NodeListOptional(),
	new NodeOptional(
	  new NodeToken( "...", oscript.data.OString.makeString("..."), DOTDOTDOT_TOKEN)
	)
      )
    );
  private final static NodeOptional EXTENDS = new NodeOptional();
  
  /**
   * Convert a {@link ShorthandFunctionPrimaryPrefix} production in the syntaxtree
   * into an equivalent production.
   * <pre>
   * f0 -> "'{"
   * f1 -> Program(true)
   * f2 -> "}"
   * </pre>
   */
  public static Node translate( ShorthandFunctionPrimaryPrefix n )
  {
    if( n.translated == null )
    {
      n.translated = new FunctionPrimaryPrefix(
        n.f0,                   // "function"
        n.f0,                   // "("
        ARGLIST,                // (Arglist())?
        n.f0,                   // ")"
        EXTENDS,                // ("extends" ...)?
        n.f0,                   // "{"
        n.f1,                   // Program()
        n.f2,                   // "}"
        n.hasVarInScope,
        n.hasFxnInScope
      );
    }
    return n.translated;
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

