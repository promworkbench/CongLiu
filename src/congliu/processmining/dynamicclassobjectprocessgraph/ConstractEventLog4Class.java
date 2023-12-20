package congliu.processmining.dynamicclassobjectprocessgraph;

import java.util.HashSet;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.ConstructHLog;


public class ConstractEventLog4Class {
	

	/**
	 * this method generate an event log for each class, each case refers to a class object. 
	 * @param Class
	 * @param originalLog
	 * @param factory
	 * @return
	 */
	public static XLog generatingEventLog(String Class,XLog originalLog, XFactory factory)
	{
		// create log
		XLog classLog =ConstructHLog.initialize(factory, Class);
		
		for(XTrace trace: originalLog)
		{
			XTrace tempTrace = factory.createTrace();
			for(XEvent event: trace)
			{
				// filtering the trace according to the component classes
				
				if(Class.equals(XSoftwareExtension.instance().extractPackage(event)+"."+XSoftwareExtension.instance().extractClass(event)))
				{
					tempTrace.add(event);
				}
			}
			
			//get the object set of the current trace
			HashSet<String> objectSet = new HashSet<>();
			
			for(XEvent e: tempTrace)
			{
				objectSet.add(XSoftwareExtension.instance().extractClassObject(e));
			}
			
			// create object trace and add to class Log. 
			for(String obj:objectSet)
			{
				XTrace objTrace = factory.createTrace();
				for(XEvent e: tempTrace)
				{
					if (obj.equals(XSoftwareExtension.instance().extractClassObject(e)))
					{
						objTrace.add(e);
					}
				}	
				classLog.add(objTrace);
			}
		}
		return classLog;
	}
}
