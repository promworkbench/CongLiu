package congliu.processmining.objectusage;

import java.util.HashMap;
import java.util.Set;

import org.deckfour.xes.model.XLog;

public class MethodLogSet {
	private HashMap<String, XLog> method2Log;
	
	public MethodLogSet()
	{
		method2Log = new HashMap<String, XLog>();
		
	}
	
	public void addMethod2Log(String name, XLog log)
	{
		method2Log.put(name, log);
	}
	
	public XLog getLog(String method)
	{
		return method2Log.get(method);
	}
	
	public Set<String> getMethodSet()
	{
		return method2Log.keySet();
	}
}
