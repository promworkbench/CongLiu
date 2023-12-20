package congliu.processmining.objectusage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import XESSoftwareExtension.XSoftwareExtension;

public class obtainGroup2subLog {
	
	public HashMap<String, XLog> getGroup2subLog(HashMap<String, XLog> method2Log, HashMap<String, Set<String>> group2classes, XFactory factory )
	{
		//for each group, we construct its software event log from the method to sub-log.
		//each group is composed of several classes, which has been classified as one group.
		HashMap<String, XLog> group2Log = new HashMap<String, XLog>();
		
		for(String group: group2classes.keySet())
		{
			XLog subLog = factory.createLog();
			XConceptExtension.instance().assignName(subLog, group);
			
			//get the object of current group(class)
			/**
			 * there is a big challenge. how to decide which class object groups together if we have multiple classes and objects. 
			 */
			
			for(String method: method2Log.keySet())
			{
				for(XTrace trace: method2Log.get(method))
				{
					HashSet<String> classobj = new HashSet<>();
					for(XEvent event: trace)
					{
						if(group.contains(XSoftwareExtension.instance().extractPackage(event)+"."+XSoftwareExtension.instance().extractClass(event)))
						{
							classobj.add(XSoftwareExtension.instance().extractClassObject(event));
						}
					}
					for (String obj: classobj)
					{
						XTrace tempTrace= factory.createTrace();
						XConceptExtension.instance().assignName(tempTrace, obj);
						
						for(XEvent e: trace)
						{
							if (XSoftwareExtension.instance().extractClassObject(e).equals(obj))
							{
								tempTrace.add(e);
							}
						}
						subLog.add(tempTrace);
					}
					
				}
			}
			
			
			group2Log.put(group, subLog);
		}
		
		return group2Log;
	}
}
