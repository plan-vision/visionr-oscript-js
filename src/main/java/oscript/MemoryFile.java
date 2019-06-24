package oscript;

import java.io.File;
import java.io.IOException;

public class MemoryFile extends File 
{
	public String content;
	public long lastModified = System.currentTimeMillis(); 
	public String name;
	public MemoryFile(String pathname,String content) 
	{
		super(pathname);
		this.name=pathname;
		this.content=content;
	}
	public String getContent() {
		return content;
	}	

	public long getLastModified() {
		return lastModified;
	}
	
	@Override
	public long lastModified() {
		return lastModified;
	}	
	
	@Override
	public boolean canRead() {
		return true;
	}

	@Override
	public boolean canWrite() {
		return true;
	}

	@Override
	public boolean createNewFile() throws IOException {
		return false;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public boolean isDirectory() {
		return false;
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public long length() {
		return this.content.length();
	}

	public boolean setLastModified( long lastModified ) {
		this.lastModified = lastModified;
		return true;
	}

	/**
	 * Change content of virtual script file
	 * @param content
	 */
	public void setContent( String content ) {
		this.content = content;
	}
	
	public String toString() {
		return this.content;
	}
}