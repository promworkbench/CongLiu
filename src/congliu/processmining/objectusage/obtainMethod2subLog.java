package congliu.processmining.objectusage;

import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processminig.XPortSoftwareEventlogTransformation.GetMathodClassPackages;

public class obtainMethod2subLog {

	public HashMap<String, XLog> getMethod2subLog(XLog softwareLog, HashSet<String> methods, XFactory factory )
	{
		//for each method, we construct its software event log from the original log.
		HashMap<String, XLog> method2Log = new HashMap<String, XLog>();
		for(String method: methods)
		{
			XLog subLog = factory.createLog();
			XConceptExtension.instance().assignName(subLog, method);
			
			//get different instance of the current method
			for(XTrace trace: softwareLog)
			{
				HashSet<String> methodins = new HashSet<>();
				for(XEvent e: trace)
				{
					if (XSoftwareExtension.instance().extractPackage(e).equals(GetMathodClassPackages.extractPackage(method))
							&& XSoftwareExtension.instance().extractClass(e).equals(GetMathodClassPackages.extractClass(method)) 
							&& XConceptExtension.instance().extractName(e).equals(GetMathodClassPackages.extractMethod(method)))
					{
						methodins.add(XSoftwareExtension.instance().extractClassObject(e));
					}
				}
				
				for (String ins: methodins)
				{
					XTrace tempTrace= factory.createTrace();
					XConceptExtension.instance().assignName(tempTrace, ins);
					for(XEvent e: trace)
					{
						if (XSoftwareExtension.instance().extractCallerclassobject(e).equals(ins))
						{
							if (XSoftwareExtension.instance().extractCallerpackage(e).equals(GetMathodClassPackages.extractPackage(method))
								&& XSoftwareExtension.instance().extractCallerclass(e).equals(GetMathodClassPackages.extractClass(method)) 
								&& XSoftwareExtension.instance().extractCallermethod(e).equals(GetMathodClassPackages.extractMethod(method)))
							{
								tempTrace.add(e);
							}
						}	
					}
					subLog.add(tempTrace);
				}
			}
			
			method2Log.put(method, subLog);
		}
		return method2Log;
	}
}
