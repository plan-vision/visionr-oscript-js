package oscript.util;

public class TerminableThread extends Thread 
{
	public TerminableThread(String name) {
		super(name);
	}
	protected boolean terminated = false;
}
