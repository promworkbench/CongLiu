package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

import XESSoftwareExtension.XSoftwareExtension;

public class InitializeInteractionLog {
	
	public static XLog initializeInteraction(XFactory factory, String logName)
	{
		//add the log name		
		XLog log = factory.createLog();
		log.getAttributes().put(XConceptExtension.KEY_NAME, new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, logName));
		
		//create standard extension
		XExtension conceptExtension = XConceptExtension.instance();
		//XExtension organizationalExtension = XOrganizationalExtension.instance();
		XExtension timeExtension = XTimeExtension.instance();
		XExtension lifecycleExtension=	XLifecycleExtension.instance();
		XExtension softwareExtension=	XSoftwareExtension.instance();
		
		// create extensions
		log.getExtensions().add(conceptExtension);
		log.getExtensions().add(softwareExtension);
		log.getExtensions().add(lifecycleExtension);
		log.getExtensions().add(timeExtension);
		
		// create trace level global attributes
		XAttribute xtrace = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
		log.getGlobalTraceAttributes().add(xtrace);

		// create event level global attributes

		log.getGlobalEventAttributes().add(XConceptExtension.ATTR_NAME);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_STARTTIMENANO);
		log.getGlobalEventAttributes().add(XLifecycleExtension.ATTR_TRANSITION);
		
		// create classifiers based on global attribute		

		/*
		 * XEventAttributeClassifier classifierActivityObject = new XEventAttributeClassifier("Method Call Identifier Object", 
		
				"Package", "Class", XConceptExtension.KEY_NAME, "Class_Object");
		*/
		XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Interface Identifier", XConceptExtension.KEY_NAME);
		log.getClassifiers().add(classifierActivity);
		
		return log;
	}
	
	public static XTrace createTrace(HashMap<Interface,HashSet<XEvent>> inter2EventSet, XFactory factory)
	{
		/*
		 * before convert to the trace, it is required to repair the log as there may be some inaccuracy which will influence the result. 
		 * the rule to repair log is that: if the start timestamp of one event is smaller that another, its complete timestapm should also be smaller.
		 */
		
		XTrace trace = factory.createTrace();
		for(Interface inter: inter2EventSet.keySet())
		{
			//create two events for each interface
			XEvent startEvent = factory.createEvent();
			XEvent endEvent = factory.createEvent();
			
			//set the name of event
			XConceptExtension.instance().assignName(startEvent, inter.getId());
			XConceptExtension.instance().assignName(endEvent, inter.getId());
			
			//set the lifecycle to each event
			XLifecycleExtension.instance().assignTransition(startEvent, "start");
			XLifecycleExtension.instance().assignTransition(endEvent, "complete");
			
			//add the starttime in nano
			long min =Long.MAX_VALUE;
			long max =Long.MIN_VALUE;
			for(XEvent e: inter2EventSet.get(inter))
			{
				if(Long.parseLong(XSoftwareExtension.instance().extractStarttimenano(e))<min)
				{
					min=Long.parseLong(XSoftwareExtension.instance().extractStarttimenano(e));
				}
				if(Long.parseLong(XSoftwareExtension.instance().extractStarttimenano(e))>max)
				{
					max=Long.parseLong(XSoftwareExtension.instance().extractStarttimenano(e));
				}
			}
			XSoftwareExtension.instance().assignStarttimenano(startEvent, Long.toString(min));
			XSoftwareExtension.instance().assignStarttimenano(endEvent, Long.toString(max));

			trace.add(startEvent);
			trace.add(endEvent);
		}
		
		return trace;
	}
	
//	public static HashSet<XEvent> repairTimestamps(HashSet<XEvent> eventSet)
//	{
//		HashSet<XEvent> repairedEvents = new HashSet<XEvent>();
//		
//		XEvent[] events = new XEvent[eventSet.size()];
//		int num=0;
//		for(XEvent e: eventSet)
//		{
//			events[num]=e;
//			num++;
//		}
//				
//		Long[] startTimes = new Long[events.length];
//		XEvent[] orderedEvents = new XEvent[events.length];
//		for(int i=0;i<events.length;i++)
//		{
//			startTimes[i]=Long.parseLong(XSoftwareExtension.instance().extractStarttimenano(events[i]));
//		}
//		//order the events according to the start time stamp.
//		java.util.Arrays.sort(startTimes);
//		
//		for(int i=0;i<startTimes.length;i++)
//		{
//			for(int j=0;j<events.length;j++)
//			{
//				if (startTimes[i]==Long.parseLong(XSoftwareExtension.instance().extractStarttimenano(events[j])))
//				{
//					orderedEvents[i]=events[j];
//					break;
//				}
//			}
//		}
//		
//		for(int i=0;i<orderedEvents.length-1;i++)
//		{
//			if(Long.parseLong(XSoftwareExtension.instance().extractEndtimenano(events[i]))>
//			Long.parseLong(XSoftwareExtension.instance().extractStarttimenano((events[i+1]))))
//			{
//				XSoftwareExtension.instance().assignEndtimenano(events[i], 
//						String.valueOf(Long.parseLong(XSoftwareExtension.instance().extractStarttimenano((events[i+1])))-1));
//			}
//		}
//		
//		for(int i=0;i<orderedEvents.length;i++)
//		{
//			repairedEvents.add(orderedEvents[i]);
//		}
//		
//		return repairedEvents;
//	}
		
}
