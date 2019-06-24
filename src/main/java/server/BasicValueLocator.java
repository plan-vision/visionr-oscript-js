package server;


public final class BasicValueLocator 
{
	// see ValueWrapper 
	public String pro;
	public int context=0;
	public int pos=0;
	public String lang=null;
	public boolean oldMode=false;
	public BasicValueLocator() {}
	public BasicValueLocator(int context,String pro,String lang,int pos,boolean oldMode) 
	{
		this.context=context;
		this.pro=pro;
		this.lang=lang;
		this.pos=pos;
		this.oldMode=oldMode;
	}
	
	public String getLang() { return lang; }
	public int getPos() { return pos; }
	public int getContext() { return context; }
	public String getProperty() { return pro; }
	public boolean getOldMode() { return oldMode; }
	
	public void setOldMode(boolean oldMode) { this.oldMode=oldMode; }
	public void setLang(String lang) { this.lang=lang; }
	public void setPos(int pos) { this.pos=pos; }
	public void setContext(int context) { this.context=context; }
	public void setProperty(String pro) { this.pro=pro; }
	
}
