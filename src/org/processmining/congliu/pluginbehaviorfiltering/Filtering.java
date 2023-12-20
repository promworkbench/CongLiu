package org.processmining.congliu.pluginbehaviorfiltering;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XEventImpl;
import org.deckfour.xes.model.impl.XLogImpl;

/**
 * this class aims to do the filtering, remove the plguin calling events from original log. 
 * @author cliu3
 *
 */
public class Filtering {

	private XLog OriginalLog; 
	
	//constructor
	public Filtering (XLog log)
	{
		this.OriginalLog = log;
	}
	
	public XLog filtering ()
	{
		// create a new log, the filter log
		XAttributeMap attMap = new XAttributeMapImpl();
		XLog filteredlog = new XLogImpl(attMap);
		
		XTrace oldTrace; 
		XTrace newTrace;
		List<ActivityUnit> list = new ArrayList<ActivityUnit>();
		List<ActivityUnit> newlist = new ArrayList<ActivityUnit>();
		List<ActivityUnit> combineslist = new ArrayList<ActivityUnit>();

		
		
		for (int i = 0; i < OriginalLog.size(); i++) 
		{
			// filter the original trace by trace
			oldTrace = OriginalLog.get(i);
			
//			List <XEventClassifier> classifierList = OriginalLog.get.getClassifiers();
//			
//			for (XEventClassifier eventClassifier :classifierList)
//			{
//				eventClassifier.getDefiningAttributeKeys();
//			}
			
			for (XEvent event :oldTrace)
			{
//				XEventClass xevent= XLogInfoFactory.createLogInfo(OriginalLog, OriginalLog.getClassifiers().get(0)).getEventClasses().getClassOf(event);
//				
				String activiyname = "";
				long starttime = 0;
				long endtime = 0;
				
				//obtain the name of event. another way to do so: event.getAttributes().get("concept:name").toString()
				activiyname = XLogFunctions.getName(event);
				
				
				//event.getAttributes().get("lifecycle:transition").toString()
				if (event.getAttributes().get("lifecycle:transition").toString().equals("start"))
				{  
					starttime = XLogFunctions.getTime(event).getTime();
				}
				
				else if (event.getAttributes().get("lifecycle:transition").toString().equals("complete"))
				{
					endtime = XLogFunctions.getTime(event).getTime();
				}
				
				ActivityUnit au = new ActivityUnit();
				
				au.setActivityName(activiyname);
				au.setStartTime(starttime);
				au.setEndTime(endtime);
				
				
				// adding to the trace list
				list.add(au);
				
			}
			
			//list is available here, current list contains only the ProM
			// combine list


//			for (ActivityUnit au : list)
//			{
//				System.out.println(au.getActivityName()+"\t"+au.getStartTime()+"\t"+au.getEndTime());
//				System.out.println("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
//			}
			
			
			list =combineList(list);
			
//			for (ActivityUnit au : list)
//			{
//				System.out.println(au.getActivityName()+"\t"+au.getStartTime()+"\t"+au.getEndTime());
//				System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
//			}
			
			//remove the plugin calling records
			newlist = removePluginCalling(list);
					
					
			//change to newTrace
			newTrace = ConverListToTrace(newlist, oldTrace.getAttributes().get("concept:name").toString());
			
			list.clear();
			newlist.clear();
						
			filteredlog.add(newTrace);
		}
		
		return filteredlog;
	}
	
	
	/*
	 * convert the filtered ActivityUnit list to Trace. 
	 */
	public XTrace ConverListToTrace(List<ActivityUnit> list, String oldTraceName)
	{
	
		
		//new trace, trace name is the same the old one
		XAttributeMap attMapTrace = new XAttributeMapImpl();
		XLogFunctions.putLiteral(attMapTrace, "concept:name", oldTraceName);
		// initialize a trace with the current time 
		XTrace newTrace = new XFactoryNaiveImpl().createTrace(attMapTrace);
		
		for (ActivityUnit activity: list)
		{
			XAttributeMap attMapStart = new XAttributeMapImpl();
			//convert time
			long dateStart = activity.getStartTime();
			
			XLogFunctions.putLiteral(attMapStart, "org:resource", "C.Liu");
			XLogFunctions.putLiteral(attMapStart, "concept:name", activity.getActivityName());
			XLogFunctions.putTimestamp(attMapStart, "time:timestamp", new Date(dateStart));
			XLogFunctions.putLiteral(attMapStart, "lifecycle:transition", "start");
			
			XEvent eventStart = new XEventImpl(attMapStart);
			newTrace.add(eventStart);
			
			
			XAttributeMap attMapEnd = new XAttributeMapImpl();
			//convert time
			long dateEnd = activity.getEndTime();
			
			XLogFunctions.putLiteral(attMapEnd, "org:resource", "C.Liu");
			XLogFunctions.putLiteral(attMapEnd, "concept:name", activity.getActivityName());
			XLogFunctions.putTimestamp(attMapEnd, "time:timestamp", new Date(dateEnd));
			XLogFunctions.putLiteral(attMapEnd, "lifecycle:transition", "complete");
			
			XEvent eventEnd = new XEventImpl(attMapEnd);
			newTrace.add(eventEnd);
		}
		
		return newTrace;
	}
	
	/*
	 * combine the start au and end au together. 
	 */
	public List<ActivityUnit> combineList(List<ActivityUnit> list)
	{
		List<ActivityUnit> combineslist = new ArrayList<ActivityUnit>();
		for (ActivityUnit a: list)
		{
			for (ActivityUnit b: list)
			{
				// the execution time of a covers that of b, then delete b
				if (a.getActivityName().equals(b.getActivityName()) && (a.getStartTime()!=b.getStartTime()))
				{
					ActivityUnit newactivity = new ActivityUnit();
					newactivity.setActivityName(a.getActivityName());
					//set start time
					if (a.getStartTime()>0)
					{
						newactivity.setStartTime(a.getStartTime());
					}
					else 
					{
						newactivity.setStartTime(b.getStartTime());
					}
					
					
					if (a.getEndTime()>0)
					{
						newactivity.setEndTime(a.getEndTime());
					}
					else
					{
						newactivity.setEndTime(b.getEndTime()); 
					}
					
					
					if (combineslist.isEmpty())
					{
						combineslist.add(newactivity);
					}
					else
					{
						int flag = 0;
						for (ActivityUnit ac: combineslist)
						{
							if (ac.getActivityName().equals(newactivity.getActivityName()))
								
							{
								flag =1;
							}
							
						}
						if(flag==0)
						{
							combineslist.add(newactivity);
						}
							
					}
					break;
				}
				
			}
		}
		return combineslist;			
	}
	
	// remove calling pluins
	
	public List<ActivityUnit> removePluginCalling(List<ActivityUnit> list)
	{
		List<ActivityUnit> newlist = list;
		List<ActivityUnit> removedlist = new ArrayList<ActivityUnit>();
		int flag;
		for (ActivityUnit a: newlist)
		{
			flag = 0;
			for (ActivityUnit b: newlist)
			{
				// b cover a, 
				if (a.getStartTime()-b.getStartTime()>0 &&  a.getEndTime()-b.getEndTime()<0 )
				{
					flag = 1;
					break;
				}
			}
			if (flag==0)
			{
				removedlist.add(a);
			}
				
			
		}
		return removedlist;
	}
}
