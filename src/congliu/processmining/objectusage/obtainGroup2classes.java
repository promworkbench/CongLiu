package congliu.processmining.objectusage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import XESSoftwareExtension.XSoftwareExtension;

public class obtainGroup2classes {
	
	/*
	 * here each group is one class
	 */
	public HashMap<String, Set<String>> getGroup2classes121(HashSet<String> classes)
	{
		HashMap<String, Set<String>> g2c = new HashMap<String, Set<String>>();

		for(String c: classes)
		{
			Set<String> hs =new HashSet<String>();
			hs.add(c);
			g2c.put(c, hs);
		}
		
		return g2c;
	}
	
	public HashSet<String> getClasses(XLog softwareLog)
	{
		HashSet<String> classes = new HashSet<String>();
		for(XTrace trace: softwareLog)
		{
			for(XEvent e: trace)
			{
				classes.add(XSoftwareExtension.instance().extractPackage(e)
						+"."+XSoftwareExtension.instance().extractClass(e));		
			}
		}
		
		
		return classes;
	}
}
