package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;

import congliu.processmining.softwareprocessmining.InitializeSoftwareEventLog;
import congliu.processmining.softwareprocessmining.OrderingEventsNano;

public class IdentifyingMultiInstanceBlocks {

	// the identifying blocks and return the filtered log.
	public static EventLogwithBlocksClass identifyingBlocks(XLog input, MiningParameters parameters)
	{
		// the return class constaining the main log where multi-instance trace fragment is replaced by a "block transition"
		// and a mapping from block transitions to sub-log 
		EventLogwithBlocksClass eventBlocks = new EventLogwithBlocksClass();
		
//		//get the class set with multiple objects, do we need how many object does each class have?
		HashSet<String> multiInstancesClassSet= IdentifyingClasseswithMultipleObjects(input);
		
//		// decide how many blocks are there, we assume that one multi-instance block will consecutively executed in one trace
//		HashMap<String, HashSet<String>> block2Classset = IdentifyingBlocks (multiInstancesClassSet, input);
		

		if(multiInstancesClassSet.size()==0)// there is no multi-instances for the current log
		{
			eventBlocks.setMainLogwithBlocks(input);
			return eventBlocks;
		}
		else// there exist multi-instance blocks. next, we assume at most only one block
		{
			XFactory factory = new XFactoryNaiveImpl();
			XLog mainLog =InitializeSoftwareEventLog.initialize(factory, "main_Log");// the main log after replaced with block
			XEvent blockEvent = factory.createEvent();// newly added block event, whose classifier should be same for different traces
			HashSet<ArrayList<XEvent>> sub_EventListSet = new HashSet<ArrayList<XEvent>>(); // store multi-instance block events for each trace
			for (XTrace trace: input)
			{
				// arraylist to store the multi-instanced events.
				ArrayList<XEvent> sub_EventList = new ArrayList<XEvent>();
				XTrace mainTrace = factory.createTrace();
				//add trace name for each newly created trace. if the current trace has this attribute.
				if(trace.getAttributes().get("concept:name")!=null)
				{
					mainTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name",
							trace.getAttributes().get("concept:name").toString()));
				}
				
				for (XEvent event: trace)
				{
					if (multiInstancesClassSet.contains(event.getAttributes().get("Class").toString()))
					{
						// put into multi-instance arraylist
						System.out.println("not here?????????");
						sub_EventList.add(event);
					}
					else
					{
						//put into main trace
						mainTrace.add(event);
					}
				}
				
				// the multiinstance part is not null
				if (sub_EventList.size()>1)
				{
					System.out.println("the sub event list size: "+sub_EventList.size());
					sub_EventListSet.add(sub_EventList);
					//add the block event to mainTrace, we use the first event by modifying its attributes.
					blockEvent =(XEvent) sub_EventList.get(0).clone();
					blockEvent.getAttributes().put(XConceptExtension.KEY_NAME, new XAttributeLiteralImpl(XConceptExtension.KEY_NAME,"MultiInstanceBlock"));
					blockEvent.getAttributes().put("Class", new XAttributeLiteralImpl("Class","BlockClass"));
					blockEvent.getAttributes().put("Package", new XAttributeLiteralImpl("Package","BlockPac"));
//					blockEvent.getAttributes().put("Class_Object", new XAttributeLiteralImpl("Class_Object","@00000"));
//					blockEvent.getAttributes().put("Component", new XAttributeLiteralImpl("Component","BlockCom"));
//					blockEvent.getAttributes().put("Caller_Method", new XAttributeLiteralImpl("Caller_Method","non"));
//					blockEvent.getAttributes().put("Caller_Class", new XAttributeLiteralImpl("Caller_Class","non"));
//					blockEvent.getAttributes().put("Caller_Package", new XAttributeLiteralImpl("Caller_Package","non"));
//					blockEvent.getAttributes().put("Caller_Class_Object", new XAttributeLiteralImpl("Caller_Class_Object","non"));
//					blockEvent.getAttributes().put("Caller_Component", new XAttributeLiteralImpl("Caller_Component","non"));
//					blockEvent.getAttributes().put(XLifecycleExtension.KEY_TRANSITION, new XAttributeLiteralImpl(XLifecycleExtension.KEY_TRANSITION,"start"));
//					blockEvent.getAttributes().put("Nesting_Level", new XAttributeLiteralImpl("Nesting_Level","-1"));
//					blockEvent.getAttributes().put("Timestamp_Nano_Start", new XAttributeLiteralImpl("Timestamp_Nano_Start",
//							sub_EventList.get(0).getAttributes().get("Timestamp_Nano_Start").toString()));
//					blockEvent.getAttributes().put("Timestamp_Nano_End", new XAttributeLiteralImpl("Timestamp_Nano_End",
//							sub_EventList.get(0).getAttributes().get("Timestamp_Nano_End").toString()));
					
//					XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Method Call Identifier", 
//							"Package", "Class", XConceptExtension.KEY_NAME);
					
//					XLogInfo tempXloginfo = XLogInfoFactory.createLogInfo(input, input.getClassifiers().get(0));
//					System.out.println("block event information: "+tempXloginfo.getEventClasses().getClassOf(blockEvent));
//					System.out.println("example event information: "+tempXloginfo.getEventClasses().getClassOf(sub_EventList.get(0)));
					mainTrace.add(blockEvent);

					
				}
				mainLog.add(mainTrace);			
			}
			
			//set the main log part, ordering the main log using time stamp....
			eventBlocks.setMainLogwithBlocks(OrderingEventsNano.ordering(mainLog, "Timestamp_Nano_Start"));
			
			//get the xeventclass information for the blockevent. if the blockEvent is not null
			XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(input, parameters.getClassifier());
			
			XLog subBlockLog = convertMultiinstanceEventLists2Blocklog(Xloginfo, sub_EventListSet);
			
			// set the mapping from block event to sub-log
			HashMap<XEventClass, XLog> block2subLog = new HashMap<XEventClass, XLog>();
			if(blockEvent!=null)
			{
				XEventClass blockEventClass=null;
				for (XEvent blockevent: mainLog.get(0))
				{
					// the xloginfo should corresponds with one log, and applies to its beloning events.
					if (blockevent.getAttributes().get(XConceptExtension.KEY_NAME).toString().equals("MultiInstanceBlock"))
					{
						XLogInfo mainXloginfo = XLogInfoFactory.createLogInfo(mainLog, mainLog.getClassifiers().get(0));
						blockEventClass=mainXloginfo.getEventClasses().getClassOf(blockevent);
						break;
					}
				}
				//blockEventClass=Xloginfo.getEventClasses().getClassOf(blockEvent);// here the value is null
				System.out.println("input block information: "+blockEventClass);
				block2subLog.put(blockEventClass, subBlockLog);
				
//				//serialization the current XESlog to disk
//				try {
//					FileOutputStream fosgz = new FileOutputStream("D:\\[6]\\making examples\\test blocks\\"+
//							subBlockLog.getAttributes().get(XConceptExtension.KEY_NAME)+System.currentTimeMillis()+".xes"); 
//					//FileOutputStream fos = new FileOutputStream("D:\\KiekerData\\CaseStudy001\\EnrichedMethodLevelLog.xes.gz"); 
//					
//					new XesXmlSerializer().serialize(subBlockLog, fosgz); 
//		            // serialize to xes.gz
//					//new XesXmlGZIPSerializer().serialize(log, fosgz);
//		
//					fosgz.close();
//					
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				} 
			}
			
			eventBlocks.setClock2subLog(block2subLog);
			
			return eventBlocks;
		}// else(there exist multi-instance blocks.)
	}
	
	
	
	// this method aims to detect classes with multiple objects
	public static HashSet<String> IdentifyingClasseswithMultipleObjects(XLog input)
	{
		// we first get the class set of this log.
		HashSet<String> classSet = new HashSet<String>();
		for (XTrace trace: input)
		{
			for(XEvent event: trace)
			{
				classSet.add(event.getAttributes().get("Class").toString());
			}
		}
		
		// checking which classes have multiple objects. 
		HashSet<String> multiInstancesClassSet = new HashSet<String>();
		
		for (String className: classSet)
		{
			for (XTrace trace: input)
			{
				//there exist one trace, where this class has multiple objtects
				HashSet<String> objSet = new HashSet<String>();
				for(XEvent event: trace)
				{
					if (event.getAttributes().get("Class").toString().equals(className))
					{
						objSet.add(event.getAttributes().get("Class_Object").toString());
					}
				}
				
				//for this case, current class has multiple objects, then add it to multiple instances, 
				if (objSet.size()>1)
				{
					multiInstancesClassSet.add(className);
					System.out.println("multi-instance class name: "+ className);
					break;//go to the next class
				}
			}
		}
		
		return multiInstancesClassSet;
	}
	
	// decide how many blocks are there, 
	public static HashMap<String, HashSet<String>> IdentifyingBlocks (HashSet<String> multiInstancesClassSet, XLog input)
	{
		HashMap<String, HashSet<String>> block2Classset = new HashMap<String, HashSet<String>>();
		
		// if there is only one class with multiple objects, (OR) different classes belongs to to one block.
		if (multiInstancesClassSet.size()==1)
		{
			block2Classset.put("MultiInstanceBlock", multiInstancesClassSet);
			return block2Classset;
		}
		
		// if there is multiple classes with multiple objects, and there is multiple blocks.
		// here we assume that different blocks should separate (not continuous) in a trace
		
		//extended to multiple blocks.....@C.liu
		
		return block2Classset;
		
	}
	
	
	public static XTrace convertArrayList2Trace(ArrayList<XEvent> list)
	{
		XFactory factory = new XFactoryNaiveImpl();
		XTrace newTrace =factory.createTrace();
		for (XEvent event: list)
		{
			newTrace.add(event);
		}
		return newTrace;
	}
	
	//convert multi event list to sub-logs for each block, here we only consider sequential multi-instance.
	public static XLog convertMultiinstanceEventLists2Blocklog(XLogInfo Xloginfo, HashSet<ArrayList<XEvent>> sub_EventListSet)
	{
		//set the mapping from block transition (xeventclass) to sub-log
		XFactory factory = new XFactoryNaiveImpl();
		XLog subBlockLog =InitializeSoftwareEventLog.initialize(factory, "subblock_Log");// the main log after replaced with block
				
		for(ArrayList<XEvent> multiInstanceEventList: sub_EventListSet)
		{
			ArrayList<XEvent> tempEventList = new ArrayList<XEvent>();
		
			//identifying instances, each describing a trace
			// the current algorithm only solves the sequential multi-instance case.
			for (XEvent event: multiInstanceEventList)
			{
				if (tempEventList.size()==0)
				{
					tempEventList.add(event);
				}
				else
				{	//the start re-currence event
					if (Xloginfo.getEventClasses().getClassOf(event).toString().equals(Xloginfo.getEventClasses().getClassOf(tempEventList.get(0)).toString()))
					{
						subBlockLog.add(convertArrayList2Trace(tempEventList));// add this trace to the sub-log
						tempEventList.clear();
						tempEventList.add(event);
					}
					else
					{
						tempEventList.add(event);
					}
					
				}
			}
			//add the last part
			subBlockLog.add(convertArrayList2Trace(tempEventList));	
		}
		
		return subBlockLog;
	}
	
}
