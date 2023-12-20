package congliu.processmining.objectusage;

import java.util.HashSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import XESSoftwareExtension.XSoftwareExtension;

public class obtainMethods {
	public HashSet<String> getMethods(XLog softwareLog)
	{
		HashSet<String> methods = new HashSet<String>();
		for(XTrace trace: softwareLog)
		{
			for(XEvent e: trace)
			{
				methods.add(XSoftwareExtension.instance().extractPackage(e)
						+"."+XSoftwareExtension.instance().extractClass(e)
						+"."+XConceptExtension.instance().extractName(e));		
			}
		}
		
		return methods;
	}
}
