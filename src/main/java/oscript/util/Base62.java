package oscript.util;

import java.util.Base64;

public class Base62
{
	private Base62() {}
	
	public static String encode(byte[] data)
	{
		return base64ToBase62(Base64.getEncoder().withoutPadding().encodeToString(data));
	}
	
	/**
	 * Returns a Base62 encoded string to it's original state.
	 */
	public static byte[] decode(String base62)
	{
		String base64 = base62ToBase64(base62);
		return Base64.getDecoder().decode(base64);
	}
	
	protected static String base64ToBase62(String base64)
	{
		StringBuffer buf = new StringBuffer(base64.length() * 2);
		
		for (int i=0; i<base64.length(); i++)
		{
			char ch = base64.charAt(i);
			switch (ch)
			{
				case 'i':
					buf.append("ii");
					break;
					
				case '+':
					buf.append("ip");
					break;
					
				case '/':
					buf.append("is");
					break;
					
				case '=':
					buf.append("ie");
					break;
					
				case '\r':
				case '\n':
					// Strip out
					break;
					
				default:
					buf.append(ch);
			}
		}
		
		return buf.toString();
	}
	
	/**
	 * Returns a string encoded with encodeBase62 to its original
	 * (base64 encoded) state.
	 */
	protected static String base62ToBase64(String base62)
	{
		StringBuffer buf = new StringBuffer(base62.length());
		
		int i = 0;
		while (i < base62.length())
		{
			char ch = base62.charAt(i);
			
			if (ch == 'i')
			{
				i++;
				char code = base62.charAt(i);
				switch (code)
				{
					case 'i':
						buf.append('i');
						break;
						
					case 'p':
						buf.append('+');
						break;
						
					case 's':
						buf.append('/');
						break;
						
					case 'e':
						buf.append('=');
						break;
						
					default:
						throw new IllegalStateException("Illegal code in base62 encoding");
				}
			}
			else
			{
				buf.append(ch);
			}
			
			i++;
		}
		
		return buf.toString();
	}
}