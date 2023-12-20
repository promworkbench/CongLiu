package MultiInstanceProcessDiscovery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

public class HEventLogConstruction {
	/*
	 * construct the hierarchical event log recursively. 
	 */
	public static HEventLog constructHierarchicalLog(ActivityNestingGraph ang, HashSet<String> activitySet , XLog lifecycleLog, XFactory factory, XLogInfo Xloginfo)
	{
		//get all nested activities
		HashSet<String> allNestedActivities = new HashSet<>();
		for(String n: TransitiveNestingRelationReduction.getAllNestedActivities(ang))
		{
			allNestedActivities.add(n);
		}
		System.out.println("All Nested Activities: "+allNestedActivities); 

		//get all root nesting activities
		HashSet<String> rootActivities  =TransitiveNestingRelationReduction.getAllRootActivities(ang);
		System.out.println("Root Activities: "+rootActivities); 
		
		//get all root nesting activities
		HashSet<String> topLevelActivities  =ActivityRelationDetection.getTopLevelActivitySet(activitySet, allNestedActivities, rootActivities);
		System.out.println("Top Level Activities: "+topLevelActivities); 
		
		
		//the hierarchical event log contains two parts, main log, and mapping <XEventClass, HLog>.
		HEventLog hEventLog = new HEventLog();
		
		//convert top-level activities to XEventClass set
		HashSet<XEventClass> XeventClassSetofTopLevelActivities =getEventClassSet(topLevelActivities, lifecycleLog, Xloginfo);
		System.out.println("Top Level XeventClasses: "+XeventClassSetofTopLevelActivities); 


		//the main part
		XLog mainLog =getMainLog(factory, "Top-level", XeventClassSetofTopLevelActivities, lifecycleLog, Xloginfo);//set the log name
				
		hEventLog.setMainLog(mainLog);
		//the mapping from nested eventclass (activities) to its corresponding sub-log. 
		HashMap<XEventClass, HEventLog> subLogMapping =new HashMap<XEventClass, HEventLog>();
		
		for(String rootNestedActivity: rootActivities)
		{
			//get the xeventclass of a root activity
			XEventClass eventClassActivity =getEventClassOfActivity(rootNestedActivity, lifecycleLog, Xloginfo);
			subLogMapping.put(eventClassActivity,
					ConstructHierarchicalEventLogRecusively(factory, convertSet2Hashset(TransitiveNestingRelationReduction.getNestedActivitiesOfAnActivity(ang, rootNestedActivity)), lifecycleLog, Xloginfo, ang));
		}
		
		hEventLog.setSubLogMapping(subLogMapping);
		
		return hEventLog;
	}
	
	
	/*
	 * constructive hierarchical log recursively
	 */
	public static HEventLog ConstructHierarchicalEventLogRecusively(XFactory factory, 
			HashSet<String> nestedActivitySet, XLog lifecycleLog, XLogInfo Xloginfo, ActivityNestingGraph ang)
	{
		//the hierarchical event log contains two parts, main log, and mapping.
		HEventLog hEventLog = new HEventLog();
				
		//for each top-level activity, we construct its HEventLog
		HashSet<XEventClass> XeventClassSetofNestedActivities =getEventClassSet(nestedActivitySet, lifecycleLog, Xloginfo);
				
		///the main part
		XLog mainLog =getMainLog(factory, "sub-level", XeventClassSetofNestedActivities, lifecycleLog, Xloginfo);//set the log name
				
		hEventLog.setMainLog(mainLog);
		
		//the mapping from nested eventclass (activities) to its corresponding sub-log. 
		HashMap<XEventClass, HEventLog> subLogMapping =new HashMap<XEventClass, HEventLog>();
		
		for(String node: nestedActivitySet)
		{
			HashSet<String> nestedActivities =convertSet2Hashset(TransitiveNestingRelationReduction.getNestedActivitiesOfAnActivity(ang, node));
			if(nestedActivities.size()!=0)//recursive calling
			{
				//get the xeventclass of a root activity
				XEventClass xeventClassActivity =getEventClassOfActivity(node, lifecycleLog, Xloginfo);
				subLogMapping.put(xeventClassActivity, ConstructHierarchicalEventLogRecusively(factory, 
								nestedActivities,lifecycleLog, Xloginfo, ang));
			}
		}
		
		hEventLog.setSubLogMapping(subLogMapping);
		
		return hEventLog;
		
	}
	
	public static HashSet<String> convertSet2Hashset (Set<String> set)
	{
		HashSet<String> hashs = new HashSet<>();
		for(String s: set)
		{
			hashs.add(s);
		}
		
		return hashs;
	}
	
	//construct the main log, we only keep the complete event 
	public static XLog getMainLog(XFactory factory, String logName, HashSet<XEventClass> XeventClassSetofTopLevelActivities, XLog lifecycleLog, XLogInfo Xloginfo)
	{
		XLog mainLog =initializeEventLog(factory, "Top-level");//set the log name
		
		for(XTrace trace: lifecycleLog)
		{
			XTrace newTrace = factory.createTrace();
			for(XEvent event: trace)
			{
				if (XeventClassSetofTopLevelActivities.contains(Xloginfo.getEventClasses().getClassOf(event))&&
						XLifecycleExtension.instance().extractTransition(event).toLowerCase().equals("complete"))
				{
					newTrace.add(event);
				}
			}
			if(newTrace.size()>0)
				mainLog.add(newTrace);
		}
		
		return mainLog;
	}
	
	
	
	//get the xeventclass set of a set of nested activities
	public static HashSet<XEventClass> getEventClassSet(HashSet<String> nestedActivities, XLog lifecycleLog,  XLogInfo Xloginfo)
	{
		HashSet<XEventClass> XeventClassSetofNestedActivities = new HashSet<XEventClass>();
		for(String nestA: nestedActivities)
		{
			int flag =0;
			for(XTrace trace: lifecycleLog)
			{
				for (XEvent event:trace)
				{
					if(XConceptExtension.instance().extractName(event).equals(nestA))
					{
						XeventClassSetofNestedActivities.add(Xloginfo.getEventClasses().getClassOf(event));
						flag =1;
						break;
					}
					
				}
				if(flag ==1)// the current event class is found. 
				{
					break;
				}
			}
		}
		return XeventClassSetofNestedActivities;
	}
	
	
	//get the xeventclass set of a set of nested activities
	public static XEventClass getEventClassOfActivity(String nestedActivity, XLog lifecycleLog, XLogInfo Xloginfo)
	{

		for(XTrace trace: lifecycleLog)
		{
			for (XEvent event:trace)
			{
				if(XConceptExtension.instance().extractName(event).equals(nestedActivity))
				{
					return Xloginfo.getEventClasses().getClassOf(event);
				}
			}
		}
		
		return null;
		
	}
	
	// initialize main software event log
	public static XLog initializeEventLog(XFactory factory, String logName)
	{
		//add the log name		
		XLog lifecycleLog = factory.createLog();
		XConceptExtension.instance().assignName(lifecycleLog, logName);
		
		//create standard extension
		XExtension conceptExtension = XConceptExtension.instance();
		XExtension timeExtension = XTimeExtension.instance();
		XExtension lifecycleExtension=XLifecycleExtension.instance();
		
		// create extensions
		lifecycleLog.getExtensions().add(conceptExtension);
		lifecycleLog.getExtensions().add(lifecycleExtension);
		lifecycleLog.getExtensions().add(timeExtension); 
		
		// create trace level global attributes
		XAttribute xtrace = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
		lifecycleLog.getGlobalTraceAttributes().add(xtrace);

		// create event level global attributes		
		lifecycleLog.getGlobalEventAttributes().add(XConceptExtension.ATTR_NAME);
		lifecycleLog.getGlobalEventAttributes().add(XLifecycleExtension.ATTR_TRANSITION);
		
		
		// create classifiers based on global attribute		
		XEventAttributeClassifier activityClassifer = new XEventAttributeClassifier("Activity Name", 
				 XConceptExtension.KEY_NAME);
		lifecycleLog.getClassifiers().add(activityClassifer);
		
		return lifecycleLog;
	}
}
