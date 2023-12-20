package org.processmining.congliu.softwareBehaviorDiscovery;

import java.util.HashMap;
import java.util.List;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

/**
 * this class aims to detect nested method call. 
 * Input: XLog with lifecycle information. 
 * Output: (1) filtered main log with labeled nested methods; (2) sub-log corresponds with each nested method call.
 * Challenge is to detect the many times nested method call with the same eventclass. 
 * Also another challenge is output the xlog of each nested method call. 
 * @author cliu3
 *
 */

		
public class NestedMethodCallDetection {
	
	// the main filtered log. 
	private XLog filteredLog; 
	// the mapping from nested eventclass to correponding sub log. 
	private HashMap<XEventClass, XLog> subLogMapping;
	
	//constructor
	public NestedMethodCallDetection(XLog inputlog)
	{
		nestedMethodCallDetection(inputlog);
	}
	
	public NestedMethodCallDetection(XEventClassifier ourClassifier, XLog inputlog)
	{
		nestedMethodCallDetection(ourClassifier, inputlog);
	}
	
	
	//return the filtered Log, the main one containing top-level methods 
	public XLog getFilteredLog()
	{
		return filteredLog;
	}
	
	//return the filtered log, the subLog one containing the sub method call. 
	public HashMap<XEventClass, XLog> getXeventClass2XLog()
	{
		return subLogMapping;
	}
	
	public void nestedMethodCallDetection (XEventClassifier ourClassifier,XLog inputlog) 
	{
		//keep the input log unchanged
		XLog  OriginalLog = (XLog) inputlog.clone();
		
		//to get the event class via the classifier. 
		XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(OriginalLog, ourClassifier);
		
		//filteredLog only contains the top level events (nested events are separated)
		XFactory factory = new XFactoryNaiveImpl();
		//XFactory factory = new XFactoryExternalStore.MapDBDiskSequentialAccessImpl();
		filteredLog = factory.createLog();
		
		// build basic information for the filtered event log
		filteredLog.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "MainEventLog"));
		//create standard extension
		XExtension conceptExtension = XConceptExtension.instance();
		XExtension organizationalExtension = XOrganizationalExtension.instance();
		XExtension timeExtension = XTimeExtension.instance();
		XExtension lifecycleExtension=	XLifecycleExtension.instance();
		
		// create extensions
		filteredLog.getExtensions().add(conceptExtension);
		filteredLog.getExtensions().add(organizationalExtension);
		filteredLog.getExtensions().add(lifecycleExtension);
		filteredLog.getExtensions().add(timeExtension);
		
		// create trace level global attributes
		XAttribute xtrace = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
		filteredLog.getGlobalTraceAttributes().add(xtrace);

		// create event level global attributes
		XAttribute xeventname = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
		//XAttribute xeventresource = new XAttributeLiteralImpl(XOrganizationalExtension.KEY_RESOURCE, "C.Liu"); 
		XAttribute xeventlifecycle = new XAttributeLiteralImpl(XLifecycleExtension.KEY_TRANSITION, "complete");
		XAttribute xeventClass = new XAttributeLiteralImpl("Class", "DEFAULT"); 
		XAttribute xeventPackage = new XAttributeLiteralImpl("Package", "DEFAULT"); 
		//XAttribute xeventRuntimeComponent = new XAttributeLiteralImpl("Runtime_Component", "DEFAULT"); 
		XAttribute xeventBelongingComponent = new XAttributeLiteralImpl("Belonging_Component", "DEFAULT"); 
		//XAttribute xeventInteractionType = new XAttributeLiteralImpl("Interaction_Type", "DEFAULT"); 
		XAttribute xeventTimeNano = new XAttributeLiteralImpl("Timestamp_Nano", "DEFAULT"); 

		filteredLog.getGlobalEventAttributes().add(xeventname);
		//filteredLog.getGlobalEventAttributes().add(xeventresource);
		filteredLog.getGlobalEventAttributes().add(xeventlifecycle);
		filteredLog.getGlobalEventAttributes().add(xeventClass);
		filteredLog.getGlobalEventAttributes().add(xeventPackage);
		//filteredLog.getGlobalEventAttributes().add(xeventRuntimeComponent);
		filteredLog.getGlobalEventAttributes().add(xeventBelongingComponent);
		//filteredLog.getGlobalEventAttributes().add(xeventInteractionType);
		filteredLog.getGlobalEventAttributes().add(xeventTimeNano);
		
		// create classifiers based on global attribute		
		XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Acticity Name", XConceptExtension.KEY_NAME, "Class", "Package");
		XEventAttributeClassifier classifierComponent = new XEventAttributeClassifier("Component","Belonging_Component");
		filteredLog.getClassifiers().add(classifierActivity);
		filteredLog.getClassifiers().add(classifierComponent);
		// the nestedEvent2Subtrace is used to store the nested event class and its sub-eventlogs
		//HashMap<XEvent, XTrace> nestedEvent2Subtrace = new HashMap<XEvent, XTrace>();

		//the case2nestedEvents is used to store the map from caseId to its nested event list.
		final HashMap<String, HashMap<XEventClass, XTrace>>  caseID2nestedEvents=
				new HashMap<String, HashMap<XEventClass, XTrace>>();// the event class for each lifecycle events are same 
		
		int nestedTimes =0;
		// traverse through the whole event log to detect nested event using lifecycle information
		for (XTrace trace: OriginalLog) 
		{
			String traceName =trace.getAttributes().get("concept:name").toString();
			//create a filtered trace, its name is the same with the old one.
			XTrace filteredTrace = factory.createTrace();// the main trace
			filteredTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", traceName));
			
			// the nestedEvent2Subtrace is used to store the nested event and its sub-events (XTrace)
			HashMap<XEventClass, XTrace> nestedEvent2Subtrace = new HashMap<XEventClass, XTrace>();
			
			// create the nested trace
			XTrace nestedTrace = factory.createTrace();
			
			XEvent startEvent =null;
			XEvent completeEvent =null;
			
			for(XEvent event: trace)
			{
			
//				System.out.println(Xloginfo.getEventClasses().getClassOf(event).toString()
//						+"\t"+event.getAttributes().get("lifecycle:transition").toString());
				//for the start of each event class, its start event and complete event should be null
				if ((startEvent ==null) && (completeEvent ==null))
				{
					//assign the current event as the start event.
					if (event.getAttributes().get("lifecycle:transition").toString().equals("start"))
					{
						//if the current lifecycle is start, then it is added to the startEvent. otherwise, discard this event
						startEvent = event;
					}
					continue;
				}
				else if((startEvent!=null) && (completeEvent==null))
				{
					//assign the current event as the complete event.
					completeEvent = event;
					continue;
				}
				else if ((startEvent!=null) && (completeEvent!=null))
				{
					//both start and complete events are not null, we need to decide if they belongs to the same eventclass
					//i.e. if they have the same event class but with different lifecycle
					XEventClass startEventClass= Xloginfo.getEventClasses().getClassOf(startEvent);
					XEventClass completeEventClass= Xloginfo.getEventClasses().getClassOf(completeEvent);
					String startEventlifecycle = startEvent.getAttributes().get("lifecycle:transition").toString();
					String completeEventlifecycle = completeEvent.getAttributes().get("lifecycle:transition").toString();
					//if the current start and complete eventclass match, they belong to the same method with different lifecycle. 
					if ((startEventClass.toString().equals(completeEventClass.toString()))
							&&(!startEventlifecycle.equals(completeEventlifecycle)))
					{
						//no nested method calls has been added to the subtrace. 
						if (nestedTimes==0)
						{
							//add the current completeEvent and nestedTrace to the nestedEvent2Subtrace
							if (nestedTrace.size()!=0)
							{
								// the current start and complete events are nested events. 
								startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
								completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
								// add the nested Trace to the
								XTrace newlyCreatedTrace =(XTrace)nestedTrace.clone();
								nestedEvent2Subtrace.put(completeEventClass, newlyCreatedTrace);
								nestedTrace.clear();
							}
							else
							{
								startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
								completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
								//nestedTrace.clear();
							}
							//As and Ac
							filteredTrace.add(startEvent);
							//System.out.println(startEventClass.toString()+"\t"+startEventlifecycle);
							filteredTrace.add(completeEvent);
							//System.out.println(startEventClass.toString()+"\t"+completeEventlifecycle);
							
							if (event.getAttributes().get("lifecycle:transition").toString().equals("start"))
							{
								startEvent =event;
								completeEvent=null;
							}
							else
							{
								startEvent =null;
								completeEvent=null;
							}
							
						}
						else //nestedTime!=0
						{
							//As As Ac 
							nestedTimes--;
							nestedTrace.add(completeEvent);
							completeEvent=event;
						}
					}
					// to deal with those nested method also with the same name. 
					else if ((startEventClass.toString().equals(completeEventClass.toString()))
							&&(startEventlifecycle.equals(completeEventlifecycle)))
					{
						//As As
						nestedTimes++;
						nestedTrace.add(completeEvent);
						completeEvent=event;
					}
					else if (!startEventClass.toString().equals(completeEventClass.toString()))
					{
						// add the completeEvent to the sub-event trace
						nestedTrace.add(completeEvent);
						completeEvent=event;
					}
					else 
					{
						continue;
					}
				}
				else 
				{
					continue;
				}
				
			}//for(XEvent event: trace)
			
			// to deal with the last, we add it to the filteredTrace.
			//add the current completeEvent and nestedTrace to the nestedEvent2Subtrace
			if((startEvent!=null) && (completeEvent!=null))
			{
				
				if((startEvent.getAttributes().get("lifecycle:transition").toString().equals("start")) && 
						(completeEvent.getAttributes().get("lifecycle:transition").toString().equals("complete")))
				{
					
					if (nestedTrace.size()!=0)
					{
						// the current start and complete events are nested events. 
						startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
						completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
						// add the nested Trace to the
						XTrace newlyCreatedTrace =(XTrace)nestedTrace.clone();
						newlyCreatedTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", traceName));
						nestedEvent2Subtrace.put(Xloginfo.getEventClasses().getClassOf(completeEvent), newlyCreatedTrace);
						nestedTrace.clear();
					}
					else
					{
						
						startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
						completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
						//nestedTrace.clear();
					}
					filteredTrace.add(startEvent);
					filteredTrace.add(completeEvent);
					
				}
			}
			//add trace to the main log.
			filteredLog.add(filteredTrace);

			//add the trace name and nestedEventList to the hashmap. 
			caseID2nestedEvents.put(traceName, nestedEvent2Subtrace);
			
			
		}//for (XTrace trace: OriginalLog)
		
		
		// this hashmap is used to store the mapping from a nested eventclass to its corresponding subLog. 
		
		subLogMapping = Convert(caseID2nestedEvents);
	}
	
	
	// the input of this function is an Xlog with lifecycle information, based on which we can detect hierarchical method call.    
	public void nestedMethodCallDetection (XLog inputlog) {
		//keep the input log unchanged
		XLog  OriginalLog = (XLog) inputlog.clone();
		
		//how to decide which classifier to use, one choice is to use dialog for selection, like the dfg of Sander. 
		// it should be adapted before applied to the big cases, one should make sure its log classifier. 
		// here we use the classifier named "Activity Name" it contains concept:name class package. 
		List <XEventClassifier> classiferList = OriginalLog.getClassifiers();
		XEventClassifier ourClassifier =classiferList.get(0);
		
		//if we have the selected classifier name, we can decide which to use. 
//		for (XEventClassifier cl: classiferList)
//		{
//			if (cl.name().equals("Acticity Name"))
//			{
//				ourClassifier=cl;
//				break;
//			}
//		}

		//to get the event class via the classifier. 
		XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(OriginalLog, ourClassifier);
		
		//filteredLog only contains the top level events (nested events are separated)
		XFactory factory = new XFactoryNaiveImpl();
		//XFactory factory = new XFactoryExternalStore.MapDBDiskSequentialAccessImpl();
		filteredLog = factory.createLog();
		
		// build basic information for the filtered event log
		filteredLog.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "MainEventLog"));
		//create standard extension
		XExtension conceptExtension = XConceptExtension.instance();
		XExtension organizationalExtension = XOrganizationalExtension.instance();
		XExtension timeExtension = XTimeExtension.instance();
		XExtension lifecycleExtension=	XLifecycleExtension.instance();
		
		// create extensions
		filteredLog.getExtensions().add(conceptExtension);
		filteredLog.getExtensions().add(organizationalExtension);
		filteredLog.getExtensions().add(lifecycleExtension);
		filteredLog.getExtensions().add(timeExtension);
		
		// create trace level global attributes
		XAttribute xtrace = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
		filteredLog.getGlobalTraceAttributes().add(xtrace);

		// create event level global attributes
		XAttribute xeventname = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
		//XAttribute xeventresource = new XAttributeLiteralImpl(XOrganizationalExtension.KEY_RESOURCE, "C.Liu"); 
		XAttribute xeventlifecycle = new XAttributeLiteralImpl(XLifecycleExtension.KEY_TRANSITION, "complete");
		XAttribute xeventClass = new XAttributeLiteralImpl("Class", "DEFAULT"); 
		XAttribute xeventPackage = new XAttributeLiteralImpl("Package", "DEFAULT"); 
		//XAttribute xeventRuntimeComponent = new XAttributeLiteralImpl("Runtime_Component", "DEFAULT"); 
		XAttribute xeventBelongingComponent = new XAttributeLiteralImpl("Belonging_Component", "DEFAULT"); 
		//XAttribute xeventInteractionType = new XAttributeLiteralImpl("Interaction_Type", "DEFAULT"); 
		XAttribute xeventTimeNano = new XAttributeLiteralImpl("Timestamp_Nano", "DEFAULT"); 

		filteredLog.getGlobalEventAttributes().add(xeventname);
		//filteredLog.getGlobalEventAttributes().add(xeventresource);
		filteredLog.getGlobalEventAttributes().add(xeventlifecycle);
		filteredLog.getGlobalEventAttributes().add(xeventClass);
		filteredLog.getGlobalEventAttributes().add(xeventPackage);
		//filteredLog.getGlobalEventAttributes().add(xeventRuntimeComponent);
		filteredLog.getGlobalEventAttributes().add(xeventBelongingComponent);
		//filteredLog.getGlobalEventAttributes().add(xeventInteractionType);
		filteredLog.getGlobalEventAttributes().add(xeventTimeNano);
		
		// create classifiers based on global attribute		
		XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Acticity Name", XConceptExtension.KEY_NAME, "Class", "Package");
		XEventAttributeClassifier classifierComponent = new XEventAttributeClassifier("Component","Belonging_Component");
		filteredLog.getClassifiers().add(classifierActivity);
		filteredLog.getClassifiers().add(classifierComponent);
		// the nestedEvent2Subtrace is used to store the nested event class and its sub-eventlogs
		//HashMap<XEvent, XTrace> nestedEvent2Subtrace = new HashMap<XEvent, XTrace>();

		//the case2nestedEvents is used to store the map from caseId to its nested event list.
		final HashMap<String, HashMap<XEventClass, XTrace>>  caseID2nestedEvents=
				new HashMap<String, HashMap<XEventClass, XTrace>>();// the event class for each lifecycle events are same 
		
		int nestedTimes =0;
		// traverse through the whole event log to detect nested event using lifecycle information
		for (XTrace trace: OriginalLog) 
		{
			String traceName =trace.getAttributes().get("concept:name").toString();
			//create a filtered trace, its name is the same with the old one.
			XTrace filteredTrace = factory.createTrace();// the main trace
			filteredTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", traceName));
			
			// the nestedEvent2Subtrace is used to store the nested event and its sub-events (XTrace)
			HashMap<XEventClass, XTrace> nestedEvent2Subtrace = new HashMap<XEventClass, XTrace>();
			
			// create the nested trace
			XTrace nestedTrace = factory.createTrace();
			
			XEvent startEvent =null;
			XEvent completeEvent =null;
			
			for(XEvent event: trace)
			{
//				System.out.println(Xloginfo.getEventClasses().getClassOf(event).toString()
//						+"\t"+event.getAttributes().get("lifecycle:transition").toString());
				//for the start of each event class, its start event and complete event should be null
				if ((startEvent ==null) && (completeEvent ==null))
				{
					//assign the current event as the start event.
					if (event.getAttributes().get("lifecycle:transition").toString().equals("start"))
					{
						//if the current lifecycle is start, then it is added to the startEvent. otherwise, discard this event
						startEvent = event;
					}
					continue;
				}
				else if((startEvent!=null) && (completeEvent==null))
				{
					//assign the current event as the complete event.
					completeEvent = event;
					continue;
				}
				else if ((startEvent!=null) && (completeEvent!=null))
				{
					//both start and complete events are not null, we need to decide if they belongs to the same eventclass
					//i.e. if they have the same event class but with different lifecycle
					XEventClass startEventClass= Xloginfo.getEventClasses().getClassOf(startEvent);
					XEventClass completeEventClass= Xloginfo.getEventClasses().getClassOf(completeEvent);
					String startEventlifecycle = startEvent.getAttributes().get("lifecycle:transition").toString();
					String completeEventlifecycle = completeEvent.getAttributes().get("lifecycle:transition").toString();
					//if the current start and complete eventclass match, they belong to the same method with different lifecycle. 
					if ((startEventClass.toString().equals(completeEventClass.toString()))
							&&(!startEventlifecycle.equals(completeEventlifecycle)))
					{
						//no nested method calls has been added to the subtrace. 
						if (nestedTimes==0)
						{
							//add the current completeEvent and nestedTrace to the nestedEvent2Subtrace
							if (nestedTrace.size()!=0)
							{
								// the current start and complete events are nested events. 
								startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
								completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
								// add the nested Trace to the
								XTrace newlyCreatedTrace =(XTrace)nestedTrace.clone();
								nestedEvent2Subtrace.put(completeEventClass, newlyCreatedTrace);
								nestedTrace.clear();
							}
							else
							{
								startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
								completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
								//nestedTrace.clear();
							}
							//As and Ac
							filteredTrace.add(startEvent);
							//System.out.println(startEventClass.toString()+"\t"+startEventlifecycle);
							filteredTrace.add(completeEvent);
							//System.out.println(startEventClass.toString()+"\t"+completeEventlifecycle);
							
							if (event.getAttributes().get("lifecycle:transition").toString().equals("start"))
							{
								startEvent =event;
								completeEvent=null;
							}
							else
							{
								startEvent =null;
								completeEvent=null;
							}
							
						}
						else //nestedTime!=0
						{
							//As As Ac 
							nestedTimes--;
							nestedTrace.add(completeEvent);
							completeEvent=event;
						}
					}
					// to deal with those nested method also with the same name. 
					else if ((startEventClass.toString().equals(completeEventClass.toString()))
							&&(startEventlifecycle.equals(completeEventlifecycle)))
					{
						//As As
						nestedTimes++;
						nestedTrace.add(completeEvent);
						completeEvent=event;
					}
					else if (!startEventClass.toString().equals(completeEventClass.toString()))
					{
						// add the completeEvent to the sub-event trace
						nestedTrace.add(completeEvent);
						completeEvent=event;
					}
					else 
					{
						continue;
					}
				}
				else 
				{
					continue;
				}
				
			}//for(XEvent event: trace)
			
			// to deal with the last, we add it to the filteredTrace.
			//add the current completeEvent and nestedTrace to the nestedEvent2Subtrace
			if((startEvent!=null) && (completeEvent!=null))
			{
				
				if((startEvent.getAttributes().get("lifecycle:transition").toString().equals("start")) && 
						(completeEvent.getAttributes().get("lifecycle:transition").toString().equals("complete")))
				{
					
					if (nestedTrace.size()!=0)
					{
						// the current start and complete events are nested events. 
						startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
						completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
						// add the nested Trace to the
						XTrace newlyCreatedTrace =(XTrace)nestedTrace.clone();
						newlyCreatedTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", traceName));
						nestedEvent2Subtrace.put(Xloginfo.getEventClasses().getClassOf(completeEvent), newlyCreatedTrace);
						nestedTrace.clear();
					}
					else
					{
						
						startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
						completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
						//nestedTrace.clear();
					}
					filteredTrace.add(startEvent);
					filteredTrace.add(completeEvent);
					
				}
			}
			//add trace to the main log.
			filteredLog.add(filteredTrace);

			//add the trace name and nestedEventList to the hashmap. 
			caseID2nestedEvents.put(traceName, nestedEvent2Subtrace);
			
			
		}//for (XTrace trace: OriginalLog)
		
		
		// this hashmap is used to store the mapping from a nested eventclass to its corresponding subLog. 
		
		subLogMapping = Convert(caseID2nestedEvents);
	}
	
	
	
	// this method tries to convert the sub log information for each eventclass from caseID2nestedEvents
	public static HashMap<XEventClass, XLog> Convert(HashMap<String, HashMap<XEventClass, XTrace>> inputCaseId2Hash) 
	{
		final HashMap<XEventClass, XLog>  convertedSubLogMapping = new HashMap<XEventClass, XLog> ();
		XFactory factory = new XFactoryNaiveImpl();
		//XFactory factory = new XFactoryExternalStore.MapDBDiskSequentialAccessImpl();
		
		//first create the mapping from event class to its sub log
		for(String caseName: inputCaseId2Hash.keySet())
		{//HashMap<String, ArrayList<HashMap<XEventClass, XTrace>>> 
			for (XEventClass eventclass: inputCaseId2Hash.get(caseName).keySet())
			{//eventclass2trace contains all eventclass to tracce mapping in caseName
				if(!convertedSubLogMapping.keySet().contains(eventclass))// do not contain this eventclass. then create a log add it. 
				{
					XLog subLog= factory.createLog();
					subLog.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "subLog of "+eventclass.toString().split("\\+")[2]));
					XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Acticity Name", XConceptExtension.KEY_NAME, "Class", "Package");
					XEventAttributeClassifier classifierComponent = new XEventAttributeClassifier("Component","Belonging_Component");
					subLog.getClassifiers().add(classifierActivity);
					subLog.getClassifiers().add(classifierComponent);
					convertedSubLogMapping.put(eventclass, subLog);
				}
			}
		}
		
		
		// read through the inputHash to add subtraces for each sub-log 

//		for(String caseName: inputHash.keySet())
//		{
//			for (XEventClass eventclass: inputHash.get(caseName).keySet())
//			{
//				if(convertedSubLogMapping.keySet().contains(eventclass))
//				{
//					convertedSubLogMapping.get(eventclass).add(inputHash.get(caseName).get(eventclass));
//				}
//
//			}
//		}
		
		//construct the log for each nested method call. 
		
		for(String caseName: inputCaseId2Hash.keySet())
		{
			for (XEventClass eventclassinput :inputCaseId2Hash.get(caseName).keySet())
			{
				for (XEventClass eventclass: convertedSubLogMapping.keySet())
				{
					if (eventclass.toString().equals(eventclassinput.toString()))
					{
						XTrace newlyCreatedTrace =inputCaseId2Hash.get(caseName).get(eventclassinput);
						newlyCreatedTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", caseName));
						convertedSubLogMapping.get(eventclass).add(newlyCreatedTrace);
					}
					
				}

			}
		}
		return  convertedSubLogMapping;
	}
	
}
