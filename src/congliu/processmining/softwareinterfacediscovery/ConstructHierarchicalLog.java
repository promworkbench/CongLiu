package congliu.processmining.softwareinterfacediscovery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.ConstructHLog;
import congliu.processmining.softwarecomponentbehaviordiscovery.HSoftwareEventLog;
import congliu.processmining.softwarecomponentbehaviordiscovery.MainLogAndNestedEventListClass;
import observerpatterndiscovery.ClassClass;

public class ConstructHierarchicalLog {
	
	/*
	 * this method aims to construct a hierarchical log from a flat one. 
	 */
	public static HSoftwareEventLog ConstructHierarchicalLogRecusively(XFactory factory, Set<ClassClass> classSet, 
			XEventClass xeventclass, HashSet<XEvent> eventList, XLog originalLog, XEventClassifier currentClassifier, String ComponentName)
	{
		//the hierarchical event log contains two parts, main log, and mapping.
		HSoftwareEventLog hsoftwareEventLog = new HSoftwareEventLog();
				
		//the main part
		XLog mainLog;
		// a structure to store main log and nested event list
		MainLogAndNestedEventListClass mainLogNestedList;
		
		//the mapping from nested eventclass (events) to correponding sub-log. 
		HashMap<XEventClass, HSoftwareEventLog> subLogMapping =new HashMap<XEventClass, HSoftwareEventLog>();
		
		//for top-level, we need to add all events whose caller class does not belongs to the current component.
		if ((xeventclass==null) && (eventList==null))// means this is the top-level.
		{
			//the main part, each main log has a name
			mainLog =ConstructHLog.initialize(factory, "Top-level");//set the log name
			
			//add traces to the main log, and add nesting attribute to each nested method, return (1) mainlog, (2) a list of nested events.
			//the getStartMain is only used to get the main log of the top-level log.
			mainLogNestedList = getStartMain(mainLog, originalLog, factory, classSet);

			//set the mainlog part
			hsoftwareEventLog.setMainLog(mainLogNestedList.getMainLog());
				
			//convert the nested eventlist to a hashmap<xeventclass, arraylist<Xevent>>
			XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(originalLog, currentClassifier);
			HashMap<XEventClass, HashSet<XEvent>> xeventclass2Eventlist =ConstructHLog.convertEventList2xeventClass(mainLogNestedList.getNestedEventSet(), Xloginfo);
			
			//construct the mapping from nested events to sub-log
			if (xeventclass2Eventlist.size()>0)// there exist nested events
			{
				//here need a construct to map eventclass to events. we construct a sub-log for each eventclass rather than an event
				for(XEventClass xeventc:xeventclass2Eventlist.keySet())
				{
					subLogMapping.put(xeventc, ConstructHierarchicalLogRecusively(factory,classSet, xeventc, xeventclass2Eventlist.get(xeventc), 
							originalLog, currentClassifier, ComponentName));
				}
			}

			//set the mapping part
			hsoftwareEventLog.setSubLogMapping(subLogMapping);
			
		}
		else //other levels, except for the input xevent and eventlist are null
		{
			//the main part, each main log has a name
			mainLog =ConstructHLog.initialize(factory, xeventclass.toString());//set the log name use the xeventclass
			
			//add traces to the main log, and add nesting attribute to each nested method, return (1) mainlog, (2) a list of nested events.
			//the getMainOtherLevels is used to get the main log of the different levels, using eventList of this.
			mainLogNestedList = ConstructHLog.getMainOtherLevels(mainLog, eventList, originalLog, factory, ComponentName);
			
			//set the mainlog part
			hsoftwareEventLog.setMainLog(mainLogNestedList.getMainLog());
			
			
			//convert the nested eventlist to a hashmap<xeventclass, arraylist<Xevent>>
			XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(originalLog, currentClassifier);
			HashMap<XEventClass, HashSet<XEvent>> xeventclass2Eventlist =ConstructHLog.convertEventList2xeventClass(mainLogNestedList.getNestedEventSet(), Xloginfo);
			
			//construct the mapping from nested events to sub-log
			if (xeventclass2Eventlist.size()>0)// there exist nested events
			{
				//here need a construct to mapping eventclass to events. we construct a sub-log for each eventclass rather an event
				for(XEventClass xeventc:xeventclass2Eventlist.keySet())
				{
//					System.out.println("[xeventclass is:]"+xeventc);
//					System.out.println("[events are:]"+xeventclass2Eventlist.get(xeventc));
					subLogMapping.put(xeventc, ConstructHierarchicalLogRecusively(factory,classSet, xeventc, xeventclass2Eventlist.get(xeventc), 
							originalLog, currentClassifier, ComponentName));
				}
			}

			hsoftwareEventLog.setSubLogMapping(subLogMapping);
		}
		
		return hsoftwareEventLog;
		
	}
	
	//get the main log for the top-level,
		public static MainLogAndNestedEventListClass getStartMain(XLog mainLog, XLog originalLog, XFactory factory, Set<ClassClass> classSet)
		{
			System.out.println("in top-level get main... ");
			MainLogAndNestedEventListClass mainLogNestedList = new MainLogAndNestedEventListClass();
			// to store the nested methods
			HashSet<XEvent> nestedEventList = new HashSet<XEvent>();
			for (XTrace trace: originalLog)
			{
				XTrace mainTrace = factory.createTrace();			
				for (XEvent event: trace)
				{
					//collect all top-level events, i.e., 
					if(!classSet.contains(XSoftwareExtension.instance().extractCallerpackage(event)+"."+XSoftwareExtension.instance().extractCallerclass(event)))
					//if (!classSet.contains(XSoftwareExtension.instance().extractCallerclass(event)))
					{
						String nestedFlag = ConstructHLog.checkNesting(event, trace);
						//if the current event is nested, then (1) add nested flag; (2) add to nested event list
						if (nestedFlag.equals("1"))
						{
							nestedEventList.add(event);
							//XSoftwareExtension.instance().assignNesting(event, true);
							event.getAttributes().put("Nested_Flag", new XAttributeLiteralImpl("Nested_Flag",nestedFlag));
						}
						else
						{
							//if it is not nested, add the nested_flag ==0.
							//XSoftwareExtension.instance().assignNesting(event, false);
							event.getAttributes().put("Nested_Flag", new XAttributeLiteralImpl("Nested_Flag",nestedFlag));
						}
						mainTrace.add(event);
						System.out.println("main log event: "+event);
					}
					
				}
				mainLog.add(mainTrace);
			}
			
			mainLogNestedList.setMainLog(mainLog);
			mainLogNestedList.setNestedEventSet(nestedEventList);
//			System.out.println("nested Event:" +nestedEventList.size());
			return mainLogNestedList;
		}
		
}
