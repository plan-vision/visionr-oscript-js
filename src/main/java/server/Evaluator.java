package server;

import java.io.File;
import bridge.bridge;
import oscript.data.Value;
import oscript.util.MemberTable;
import oscript.util.StackFrame;

public class Evaluator 
{
	
	protected static Boolean DEBUG_SCRIPT = null;
	protected int[] params;
	protected long executionTimeout = 0;
	protected static File sourceFolder;
}
