package bridge;

public class common 
{	
	public static String stringToHTML(Object _string) {
		if (_string == null)
			return "";
		String string=_string.toString();
	    StringBuffer sb = new StringBuffer(string.length());
	    // true if last char was blank
	    int len = string.length();
	    char c;

	    for (int i = 0; i < len; i++)
        {
        c = string.charAt(i);
            // HTML Special Chars
            if (c == '\'')
                sb.append("&#39;");
            else if (c == '"')
                sb.append("&quot;");
            else if (c == '&')
                sb.append("&amp;");
            else if (c == '<')
                sb.append("&lt;");
            else if (c == '>')
                sb.append("&gt;");
            else if (c == '\n')
                // Handle Newline
                sb.append("<br/>");
            else 
            	sb.append(c);
        }
	    return sb.toString();
	}
	
}
