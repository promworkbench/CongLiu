package org.processmining.congliu.localizeddiscovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.model.impl.XLogImpl;
import org.deckfour.xes.model.impl.XTraceImpl;
import org.processmining.congliu.pluginbehaviorfiltering.XLogFunctions;
/**
 * split the original log to sublog using the "component" attribute
 * @author cliu3
 *
 */
public class LocalizationSplitting {

	public List<XLog> split(XLog originallog, LocalizedDiscoveryConfiguration configuration)
	{
		List<XLog> loglist = new ArrayList<XLog>();
		
		Map<Long, XLog> logtime = new HashMap<Long, XLog>();

		//first obtain all localization information and store them to set

		HashSet<String> LocazliationSet = new HashSet<String>();
		for (int i = 0; i < originallog.size(); i++) 
		{			
			for (XEvent event : originallog.get(i))
			{
				//plugin, or component 
				LocazliationSet.add(event.getAttributes().get(configuration.getRegionName()).toString());		
				//System.out.println(configuration.getRegionName());
			}
			
		}
		LocazliationSet.remove("Not Determined");
		
		// sequential the LocazliationSet according to execution time, considering the sequence are same in differnt case
		
		
		
		//
		Iterator<String> itLocazliation=LocazliationSet.iterator();
		while(itLocazliation.hasNext())
		{
			String name = itLocazliation.next().toString();
			
			// traverse the whole log and find those events with the "component = name"
			//create a new log
			XAttributeMap attMaplog = new XAttributeMapImpl();
			XLogFunctions.putLiteral(attMaplog, "concept:name", name);
			XLog templog = new XLogImpl(attMaplog);
			
			long timestamp = 0 ;
			for (int i = 0; i < originallog.size(); i++) 
			{
				timestamp =0;
				//create a new trace
				XAttributeMap attMaptrace = new XAttributeMapImpl();
				XTrace temptrace = new XTraceImpl(attMaptrace);
				
				for (XEvent event :originallog.get(i))
				{
					//plugin, or component
					if (event.getAttributes().get(configuration.getRegionName()).toString().equals(name))
					{
						temptrace.add(event);
						
						//obtain the execution time (the biggest one)
						if (XLogFunctions.getTime(event).getTime() >timestamp)
						{
							timestamp = XLogFunctions.getTime(event).getTime();
						}
					}
					
						
				}
				templog.add(temptrace);
			}
			
			//loglist.add(templog);
			logtime.put(timestamp, templog);
		}
		
		
		// order the map according to the key
		Set<Long> timeset = logtime.keySet();
		List<Long> timelist = new ArrayList<Long>();
		for(long l: timeset)
		{
			timelist.add(l);
		}
		
		//using existing list sorting
		java.util.Collections.sort(timelist);
		for (long l: timelist)
		{
			//add the log in order
			loglist.add(logtime.get(l));
		}
		
		
//		//before return it, we try to serilized 
//		
//		//serialization the current XES log 
//		int i =0;
//		for(XLog templog: loglist)
//		{
//			i++;
//			try {
//				FileOutputStream fos = new FileOutputStream ("D:\\KiekerData\\Localization\\localization"+i+".xes"); 
//				//new FileWriter(fileName,true)
//				new XesXmlSerializer().serialize(templog, fos); 
//				
//
//				fos.close();
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
//		}
	
		
		
		
		return loglist;
	}	
}
