package congliu.processmining.softwarecomponentbehaviorNew;

import java.util.HashMap;
import java.util.Set;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.ConstructHLog;
import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.SoftwareComponentInteractionBehaviorDiscoveryPlugin;
import congliu.processmining.softwareprocessmining.Component2Classes;

public class ConstructSoftwareComponentLog {

	/**
	 * this class aims to extracting software event log of one component by identifying component instances. 
	 * @param com2class
	 * @param com2classList
	 * @param originalLog
	 * @return
	 */
	public static XLog generatingSoftwareEventLog(Component2Classes com2class,XLog originalLog, XFactory factory)
	{
		// create log
		XLog componentLog =ConstructHLog.initialize(factory, com2class.getComponent());
		
		for(XTrace trace: originalLog)
		{
			XTrace tempTrace = factory.createTrace();
			for(XEvent event: trace)
			{
				// filtering the trace according to the component classes
				
				if(com2class.getClasses().contains(XSoftwareExtension.instance().extractClass(event)))
				{
					tempTrace.add(event);
				}
			}
			// identify component instances for the filtered trace
			
			// create new traces (each corresponds to one component instance)
			HashMap<String, Set<String>> componentInstance2objectset =new HashMap<String, Set<String>>(); 

			componentInstance2objectset =SoftwareComponentInteractionBehaviorDiscoveryPlugin.InterfaceInstance2Objects(tempTrace,
					com2class.getClasses());
			
			
			// create instance trace and add to component Log. 
			for(String comIns:componentInstance2objectset.keySet())
			{
				XTrace insTrace = factory.createTrace();
				for(XEvent e: tempTrace)
				{
					if (componentInstance2objectset.get(comIns).contains(XSoftwareExtension.instance().extractClassObject(e)))
					{
						insTrace.add(e);
					}
				}	
				componentLog.add(insTrace);
			}
			
			
		}
		return componentLog;
	}
}
