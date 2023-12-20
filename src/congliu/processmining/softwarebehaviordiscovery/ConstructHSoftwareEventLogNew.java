package congliu.processmining.softwarebehaviordiscovery;
/**
 * this class extends the ConstructHSoftwareEventLog in congliu.processmining.softwareprocessmining.InitializeSoftwareEventLog package.
 * here we mainly extend the notion: nested event should satisfy that its callee must belongs to the current component. 
 * @author cliu3
 *
 */



import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

import congliu.processmining.softwarecomponentbehaviordiscovery.HSoftwareEventLog;
import congliu.processmining.softwarecomponentbehaviordiscovery.MainLogAndNestedEventListClass;
import congliu.processmining.softwareprocessmining.InitializeSoftwareEventLog;

public class ConstructHSoftwareEventLogNew {

	//  the input is eventclass, the correponding nested events of this eventclass, original log, and classifier. 
	public static HSoftwareEventLog ConstructHSoftwareEventLogRecusively(XEventClass xeventclass, HashSet<XEvent> eventList, XLog originalLog, XEventClassifier currentClassifier, 
			String ComponentName)
	{
		//the hierarchical event log contains two parts, main log, and mapping.
		HSoftwareEventLog hsoftwareEventLog = new HSoftwareEventLog();
		
		XFactory factory = new XFactoryNaiveImpl();
		
		//the main part
		XLog mainLog;
		// a structure to store main log and nested event list
		MainLogAndNestedEventListClass mainLogNestedList;
		//the mapping from nested eventclass (events) to correponding sub-log. 
		HashMap<XEventClass, HSoftwareEventLog> subLogMapping =new HashMap<XEventClass, HSoftwareEventLog>();
		
		//we need to distinguish the top-level and other levels, because sometime main() is included and sometimes not. 
		//for top-level, we need to add all events with (nesting-length==0) to the main log
		if ((xeventclass==null) && (eventList==null))// means this is the top-level.
		{
			//the main part, each main log has a name
			mainLog =InitializeSoftwareEventLog.initialize(factory, "Top-level");//set the log name
			
			//add traces to the main log, and add nesting attribute to each nested method, return (1) mainlog, (2) a list of nested events.
			//the getStartMain is only used to get the main log of the top-level log.
			mainLogNestedList = getStartMain(mainLog, originalLog, factory, ComponentName);

			//set the mainlog part
			hsoftwareEventLog.setMainLog(mainLogNestedList.getMainLog());
			
//			//set the nested eventlist
//			hsoftwareEventLog.setNestedEventList(mainLogNestedList.getNestedEventList());
			
			//convert the nested eventlist to a hashmap<xeventclass, arraylist<Xevent>>
			
			XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(originalLog, currentClassifier);
			HashMap<XEventClass, HashSet<XEvent>> xeventclass2Eventlist =convertEventList2xeventClass(mainLogNestedList.getNestedEventSet(), Xloginfo);
			
			//construct the mapping from nested events to sub-log
			
			if (xeventclass2Eventlist.size()>0)// there exist nested events
			{
				//here need a construct to mapping eventclass to events. we construct a sub-log for each eventclass rather an event
				for(XEventClass xeventc:xeventclass2Eventlist.keySet())
				{
					subLogMapping.put(xeventc, ConstructHSoftwareEventLogRecusively(xeventc, xeventclass2Eventlist.get(xeventc), 
							originalLog, currentClassifier, ComponentName));
				}
			}
//			else
//			{
//				//set the mapping null
//				subLogMapping=null;
//			}
			
			//set the mapping part
			hsoftwareEventLog.setSubLogMapping(subLogMapping);
			
		}
		else //other levels, except for the input xevent and eventlist are null
		{
			//the main part, each main log has a name
			mainLog =InitializeSoftwareEventLog.initialize(factory, xeventclass.toString());//set the log name use the xeventclass
			
			//add traces to the main log, and add nesting attribute to each nested method, return (1) mainlog, (2) a list of nested events.
			//the getMainOtherLevels is used to get the main log of the different levels, using eventList of this.
			mainLogNestedList = getMainOtherLevels(mainLog, eventList, originalLog, factory, ComponentName);
			
			//set the mainlog part
			hsoftwareEventLog.setMainLog(mainLogNestedList.getMainLog());
			
			
			//convert the nested eventlist to a hashmap<xeventclass, arraylist<Xevent>>
			XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(originalLog, currentClassifier);
			HashMap<XEventClass, HashSet<XEvent>> xeventclass2Eventlist =convertEventList2xeventClass(mainLogNestedList.getNestedEventSet(), Xloginfo);
			
			//construct the mapping from nested events to sub-log
			
			if (xeventclass2Eventlist.size()>0)// there exist nested events
			{
				//here need a construct to mapping eventclass to events. we construct a sub-log for each eventclass rather an event
				for(XEventClass xeventc:xeventclass2Eventlist.keySet())
				{
					subLogMapping.put(xeventc, ConstructHSoftwareEventLogRecusively(xeventc, xeventclass2Eventlist.get(xeventc), 
							originalLog, currentClassifier, ComponentName));
				}
			}

			hsoftwareEventLog.setSubLogMapping(subLogMapping);
		}
		
		return hsoftwareEventLog;
		
	}
	
	
	//get the main log for other levels, i.e., filtering the log using the input event list (find the those event whose caller is in the list)
	// the number of traces= |eventlist|*|inputlog|
	public static MainLogAndNestedEventListClass getMainOtherLevels(XLog mainLog, HashSet<XEvent> eventList, XLog originalLog, XFactory factory, String componentName)
	{
		System.out.println("in other get main... ");
		MainLogAndNestedEventListClass mainLogNestedList = new MainLogAndNestedEventListClass();
		// to store the nested methods
		HashSet<XEvent> nestedEventList = new HashSet<XEvent>();


		
		// we need to seperately cope with different caller, main and others. 
		for(XEvent callerEvent: eventList)
		{
			// if the eventlist is not main()
			for (XTrace trace:originalLog)
			{
				XTrace tempTrace = factory.createTrace();	
				for (XEvent calleeEvent: trace)
				{
					if(callerEvent.getAttributes().get("Class_Object").toString().equals(calleeEvent.getAttributes().get("Caller_Class_Object").toString())
							&& callerEvent.getAttributes().get("concept:name").toString().equals(calleeEvent.getAttributes().get("Caller_Method").toString()))
					{
						// if a event (callee class) does not belong to the current component, it cannot be denoted as nesting
						String nestedFlag = checkNesting(calleeEvent, trace, componentName);
						//if the current event is nested, then (1) add nested flag; (2) add to nested event list
						if (nestedFlag.equals("1"))
						{
							nestedEventList.add(calleeEvent);
							calleeEvent.getAttributes().put("Nested_Flag", new XAttributeLiteralImpl("Nested_Flag",nestedFlag));
						}
						else
						{
							//if it is not nested, add the nested_flag ==0.
							calleeEvent.getAttributes().put("Nested_Flag", new XAttributeLiteralImpl("Nested_Flag",nestedFlag));
						}
						tempTrace.add(calleeEvent);
					}
				}
				
				if(tempTrace.size()>0)
				{
					mainLog.add(tempTrace);
				}	
			}
			
			// if the caller is main, we only need to execute once, because the object of main are same.
			if (callerEvent.getAttributes().get("concept:name").toString().equals("main()"))
			{				
				break;
			}

		}
		
		
		mainLogNestedList.setMainLog(mainLog);
		mainLogNestedList.setNestedEventSet(nestedEventList);
		return mainLogNestedList;
	}
	
	//get the main log for the top-level, i.e., filtering log with nesting-length==0
	public static MainLogAndNestedEventListClass getStartMain(XLog mainLog, XLog originalLog, XFactory factory, String componentName)
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
				//collect all top-level events, i.e., nesting-level==0
				if (event.getAttributes().get("Nesting_Level").toString().equals("0"))
				{
					System.out.println("nesting_level====0");
					String nestedFlag = checkNesting(event, trace, componentName);
					//if the current event is nested, then (1) add nested flag; (2) add to nested event list
					if (nestedFlag.equals("1"))
					{
						nestedEventList.add(event);
						
						event.getAttributes().put("Nested_Flag", new XAttributeLiteralImpl("Nested_Flag",nestedFlag));
					}
					else
					{
						//if it is not nested, add the nested_flag ==0.
						event.getAttributes().put("Nested_Flag", new XAttributeLiteralImpl("Nested_Flag",nestedFlag));
					}
					mainTrace.add(event);
				}
				
			}
			mainLog.add(mainTrace);
		}
		
		mainLogNestedList.setMainLog(mainLog);
		mainLogNestedList.setNestedEventSet(nestedEventList);
		System.out.println("nested Event:" +nestedEventList.size());
		return mainLogNestedList;
	}
	
	//check if an event is nested, i.e. there exist at least one event whose caller method and caller class object equals with its method and class object
	//here, we assume that if an event (callee) does not belongs to the current component, it cannot call others (nestingflag =0). 
	//if (com2class.getClasses().contains(qe.getPreRecording().split(";")[1]))
	public static String checkNesting(XEvent event, XTrace trace, String ComponentName)
	{
		String nestedFlag="0";
		//
		if (ComponentName.equals(event.getAttributes().get("Component").toString()))
		{
			for (XEvent e: trace)
			{
				if (event.getAttributes().get("Class_Object").toString().equals(e.getAttributes().get("Caller_Class_Object").toString())
						&& event.getAttributes().get("concept:name").toString().equals(e.getAttributes().get("Caller_Method").toString()))
				{
					nestedFlag="1";
					return nestedFlag;
				}
			}
		}

		return nestedFlag;
	}
	
	//convert the nested eventlist to a hashset<xeventclass, hashmap<Xevent>>
	public static HashMap<XEventClass, HashSet<XEvent>> convertEventList2xeventClass(HashSet<XEvent> nestedEventList, XLogInfo Xloginfo)
	{
		//create a hashmap, here the xeventclass should be with package+class+method, and without class object.
		HashMap<XEventClass, HashSet<XEvent>> xeventclass2nestedevents = new HashMap<XEventClass, HashSet<XEvent>>();
		
		//first get the xeventclass set of these nested events.
		HashSet<XEventClass> xeventclassSet = new HashSet<XEventClass>();
		for (XEvent event:nestedEventList)
		{
			xeventclassSet.add(Xloginfo.getEventClasses().getClassOf(event));
		}
		
		//then go through the eventclassset, 
		for (XEventClass xeventclass: xeventclassSet)
		{
			xeventclass2nestedevents.put(xeventclass, getXevents4EventClass(xeventclass, nestedEventList, Xloginfo));
		}
		
		return xeventclass2nestedevents;
	}
	
	//get arraylist<Xevent> of one xeventclass. the input is the eventclass and the original eventlist
	public static HashSet<XEvent> getXevents4EventClass(XEventClass xeventclass, HashSet<XEvent> nestedEventList, XLogInfo Xloginfo)
	{
		HashSet<XEvent> tempEventList = new HashSet<XEvent>();
		for (XEvent event:nestedEventList)
		{
			if (xeventclass.toString().equals(Xloginfo.getEventClasses().getClassOf(event).toString()))
			{
				tempEventList.add(event);
			}
				
		}
		return tempEventList;
	}
}
