package congliu.processmining.dynamicclassobjectprocessgraph;
/**
 * this class extends the ConstructHSoftwareEventLog in congliu.processmining.softwareprocessmining.InitializeSoftwareEventLog package.
 * here we mainly extend the notion: nested event should satisfy that its callee must belongs to the current component. 
 * @author cliu3
 *
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processmining.softwarecomponentbehaviordiscovery.HSoftwareEventLog;
import congliu.processmining.softwarecomponentbehaviordiscovery.MainLogAndNestedEventListClass;

public class ConstructHLog {
	/**
	 *  if (xeventclass==null) && (eventList==null), means the top-level events. 
	 * @param factory
	 * @param c2c
	 * @param xeventclass
	 * @param eventList
	 * @param originalLog
	 * @param currentClassifier
	 * @param ComponentName
	 * @return
	 */
	public static HSoftwareEventLog ConstructHLogRecusively(XFactory factory, String className, XEventClass xeventclass, 
			HashSet<XEvent> eventList, XLog originalLog, XEventClassifier currentClassifier)
	{
		//the hierarchical event log contains two parts, main log, and mapping.
		HSoftwareEventLog hsoftwareEventLog = new HSoftwareEventLog();
				
		//the main part
		XLog mainLog;
		// a structure to store main log and nested event list
		MainLogAndNestedEventListClass mainLogNestedList;
		//the mapping from nested eventclass (events) to correponding sub-log. 
		HashMap<XEventClass, HSoftwareEventLog> subLogMapping =new HashMap<XEventClass, HSoftwareEventLog>();
		
		// get class set of the current component. 
		ArrayList<String> classSet = new ArrayList<>();
		classSet.add(className);
		
		//for top-level, we need to add all events whose caller class does not belongs to the current component.
		if ((xeventclass==null) && (eventList==null))// means this is the top-level.
		{
			//the main part, each main log has a name
			mainLog =initialize(factory, "Top-level");//set the log name
			
			//add traces to the main log, and add nesting attribute to each nested method, return (1) mainlog, (2) a list of nested events.
			//the getStartMain is only used to get the main log of the top-level log.
			mainLogNestedList = getStartMain(mainLog, originalLog, factory, classSet);

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
					subLogMapping.put(xeventc, ConstructHLogRecusively(factory,className, xeventc, xeventclass2Eventlist.get(xeventc), 
							originalLog, currentClassifier));
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
			mainLog =initialize(factory, xeventclass.toString());//set the log name use the xeventclass
			
			//add traces to the main log, and add nesting attribute to each nested method, return (1) mainlog, (2) a list of nested events.
			//the getMainOtherLevels is used to get the main log of the different levels, using eventList of this.
			mainLogNestedList = getMainOtherLevels(mainLog, eventList, originalLog, factory);
			
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
					subLogMapping.put(xeventc, ConstructHLogRecusively(factory,className, xeventc, xeventclass2Eventlist.get(xeventc), 
							originalLog, currentClassifier));
				}
			}

			hsoftwareEventLog.setSubLogMapping(subLogMapping);
		}
		
		return hsoftwareEventLog;
		
	}
	
	
	//get the main log for other levels, i.e., filtering the log using the input event list (find the those event whose caller is in the list)
	// the number of traces= |eventlist|*|inputlog|
	public static MainLogAndNestedEventListClass getMainOtherLevels(XLog mainLog, HashSet<XEvent> eventList, XLog originalLog, XFactory factory)
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
					if(XSoftwareExtension.instance().extractClassObject(callerEvent).equals(XSoftwareExtension.instance().extractCallerclassobject(calleeEvent))
							&&XConceptExtension.instance().extractName(callerEvent).equals(XSoftwareExtension.instance().extractCallermethod(calleeEvent)))
					{
						// if a event (callee class) does not belong to the current component, it cannot be denoted as nesting
						String nestedFlag = checkNesting(calleeEvent, trace);
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
		}
		
		
		mainLogNestedList.setMainLog(mainLog);
		mainLogNestedList.setNestedEventSet(nestedEventList);
		return mainLogNestedList;
	}
	
	//get the main log for the top-level,
	public static MainLogAndNestedEventListClass getStartMain(XLog mainLog, XLog originalLog, XFactory factory, ArrayList<String> classSet)
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
				if (!classSet.contains(XSoftwareExtension.instance().extractCallerclass(event)))
				{
					String nestedFlag = checkNesting(event, trace);
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
		System.out.println("nested Event:" +nestedEventList.size());
		return mainLogNestedList;
	}
	
	//check if an event is nested, i.e. there exist at least one event whose caller method and caller class object equals with its method and class object
	//here, we assume that if an event (callee) does not belongs to the current component, it cannot call others (nestingflag =0). 
	//if (com2class.getClasses().contains(qe.getPreRecording().split(";")[1]))
	public static String checkNesting(XEvent event, XTrace trace)
	{
		String nestedFlag="0";
		//
		for (XEvent e: trace)
		{
			if (XSoftwareExtension.instance().extractClassObject(event).equals(XSoftwareExtension.instance().extractCallerclassobject(e))
					&&XConceptExtension.instance().extractName(event).equals(XSoftwareExtension.instance().extractCallermethod(e)))
			{
				nestedFlag="1";
				return nestedFlag;
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
	
	// initialize main log
	public static XLog initialize(XFactory factory, String logName)
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
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CLASS);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_PACKAGE);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CLASSOBJ);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_COMPONENT);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CALLERMETHOD);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CALLERCLASS);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CALLERPACKAGE);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CALLERCLASSOBJ);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CALLERCOMPONENT);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_STARTTIMENANO);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_ENDTIMENANO);
		log.getGlobalEventAttributes().add(XLifecycleExtension.ATTR_TRANSITION);
		
		// create classifiers based on global attribute		

		XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Method Call Identifier", 
				 XConceptExtension.KEY_NAME, XSoftwareExtension.KEY_CLASS, XSoftwareExtension.KEY_PACKAGE);
		//log.getClassifiers().add(classifierActivityObject);
		log.getClassifiers().add(classifierActivity);
		
		return log;
	}
}
