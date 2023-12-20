package org.processmining.congliu.softwareBehaviorDiscovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

/**
 * Improved: this class is the second part of nesting relation detection, 
 * i.e., nesting relation filtering according to the nesting relation mapping. 
 * this class aims to detect nested method call. 
 * Input: XLog with lifecycle information (with concurrency feature AsBsAcBc). 
 * Output: (1) filtered main log with labeled nested methods; (2) sub-log corresponds with each nested method call.
 * Contributions: (1) distinguish concurrency and nesting; and (2) handle low frequency/noisy behavior using a threshold. 
 * Assumption: the input log is consistent, i.e., without noise events like (1)AsBsBc; and (2) AcBsBc. 
 * @author cliu3
 *
 */

		
public class NestedMethodCallDetectionTrueConcurrency {
	
	// the main filtered log. 
	private XLog filteredLog; 
	// the mapping from nested eventclass to its corresponding sub log. 
	private HashMap<XEventClass, XLog> subLogMapping;
	
	//constructor
	public NestedMethodCallDetectionTrueConcurrency(double threshold, XLog inputlog)
	{
		nestedMethodCallDetection(threshold, inputlog);
	}
	
	public NestedMethodCallDetectionTrueConcurrency(double threshold, XEventClassifier ourClassifier, XLog inputlog)
	{
		nestedMethodCallDetectionwithClassifier(threshold, ourClassifier, inputlog);
	}
	
	
	//return the filtered Log, the main one containing all top-level methods 
	public XLog getFilteredLog()
	{
		return filteredLog;
	}
	
	//return the filtered log, the mapping from nested event class to subLog.
	public HashMap<XEventClass, XLog> getXeventClass2XLog()
	{
		return subLogMapping;
	}
	
	
	// the input of this function is an Xlog with lifecycle information, based on which we can detect hierarchical method call.    
	public void nestedMethodCallDetection (double threshold, XLog inputlog) {
		//keep the input log unchanged
		XLog  OriginalLog = (XLog) inputlog.clone();
		
		//how to decide which classifier to use, one choice is to use dialog for selection, like the dfg of Sander. 
		// here we use the classifier named "Activity Name" it contains concept:name class package. 
		
		List <XEventClassifier> classiferList = OriginalLog.getClassifiers();
		XEventClassifier ourClassifier =classiferList.get(0);
		nestedMethodCallDetectionwithClassifier(threshold, ourClassifier, OriginalLog);
	}
	

	//add the nesting frequency threshold. 
	@SuppressWarnings("unused")
	public void nestedMethodCallDetectionwithClassifier(double threshold, XEventClassifier ourClassifier, XLog inputlog) 
	{
		//keep the input log unchanged
		XLog  OriginalLog = (XLog) inputlog.clone();
		
		//pre-process the event log to get a list of nested event classes with frequency threshold. 
		//this is where we can improve 
		//ArrayList<XEventClass> nestedEventList=NestedEventClassDetectionUsingThreshold.getNestedEventClasses(ourClassifier, threshold, OriginalLog);
		
		//to get the event class via the classifier. 
		XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(OriginalLog, ourClassifier);
		
		//filteredLog only contains the top level events (nested events are separated)
		XFactory factory = new XFactoryNaiveImpl();
		
		//XFactory factory = new XFactoryExternalStore.MapDBDiskSequentialAccessImpl();
		filteredLog = factory.createLog();
		
		// build basic information for the filtered event log
		filteredLog.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "FilteredMainEventLog"));
		//create standard extension for the current main log
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
		// an alternative way is to use the classifies of OriginalLog...
		XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Acticity Name", XConceptExtension.KEY_NAME, "Class", "Package");
		XEventAttributeClassifier classifierComponent = new XEventAttributeClassifier("Component","Belonging_Component");
		filteredLog.getClassifiers().add(classifierActivity);
		filteredLog.getClassifiers().add(classifierComponent);

		//the case2nestedEvents is used to store the map from caseId to its nested event list.
		final HashMap<String, HashMap<XEventClass, XTrace>>  caseID2nestedEvents=
				new HashMap<String, HashMap<XEventClass, XTrace>>();// the event class for each lifecycle events are same 
		
		
		// traverse through the whole event log to detect nested event using lifecycle information
		for (XTrace trace: OriginalLog) 
		{
			int nestedTimes =0;
			// obtain the current trace name. 
			String traceName =trace.getAttributes().get("concept:name").toString();
			
			System.out.println("****** we are in trace: " + traceName);
			
			//create a filtered trace, its name is the same with the old one.
			XTrace filteredTrace = factory.createTrace();// the main trace
			filteredTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", traceName));
			
			// the nestedEvent2Subtrace is used to store the nested event and its sub-event sequence (XTrace)
			HashMap<XEventClass, XTrace> nestedEvent2Subtrace = new HashMap<XEventClass, XTrace>();
			
			// create the nested trace
			XTrace nestedTrace = factory.createTrace();
			
			XEvent startEvent =null;
			XEvent completeEvent =null;
			
			for(XEvent event: trace)
			{
				//for the start of each event class, its start event and complete event should be null
				if ((startEvent ==null) && (completeEvent ==null))
				{
					//assign the current event as the start event, if start with complete event, it is discard. 
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
					//i.e. if they have the same event class but with different lifecycle information. 
					XEventClass startEventClass= Xloginfo.getEventClasses().getClassOf(startEvent);
					XEventClass completeEventClass= Xloginfo.getEventClasses().getClassOf(completeEvent);
					String startEventlifecycle = startEvent.getAttributes().get("lifecycle:transition").toString();
					String completeEventlifecycle = completeEvent.getAttributes().get("lifecycle:transition").toString();
					
					//if the current start and complete eventclass match, they belong to the same method with different lifecycle. 
					if ((startEventClass.toString().equals(completeEventClass.toString()))
							&&(!startEventlifecycle.equals(completeEventlifecycle)))
					{
						System.out.println("---a pair, the size of nestedTraces: " +nestedTrace.size());
						
						if (nestedTimes==0)
						{						
							ArrayList<XEvent> singleEvents = new ArrayList<XEvent>();
							
							//add the current completeEvent and nestedTrace to the nestedEvent2Subtrace
							if (nestedTrace.size()>0)
							{
								//get the nesting frequency
								double nestingFrequency =computeNestingFrequency(Xloginfo, startEventClass, inputlog, ourClassifier);
								System.out.println(startEventClass +" nestingFrequency: " +nestingFrequency);
								
								// add the nested Trace to the mapping, first filter to include only the coupled pairs. 
								singleEvents = findSingleEvents(Xloginfo, ourClassifier, nestedTrace);
								
								XTrace coupledTrace = findCoupledTraceFragment(Xloginfo, ourClassifier,nestedTrace);
								nestedTrace.clear();
								
								// if the current pair is detected as nesting. 
								if (nestingFrequency >= threshold)
								{
									// the current start and complete events are nested events. 
									startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
									completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
									nestedEvent2Subtrace.put(completeEventClass, coupledTrace);
									// add the current nested event class to the list. 
									System.out.println("Detected Nested Events: "+startEventClass);
								}
								else// for those nested event that is predicted as not, we add its sub trace to the filteredTrace
								{
									startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
									completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
									for (XEvent tempevent: coupledTrace)
									{
										tempevent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
										filteredTrace.add(tempevent);
									}
								}
							}
							else// nestedTrace.size()==0 
							{
								startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
								completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
								nestedTrace.clear();
							}
							//As and Ac
							filteredTrace.add(startEvent);
							
							filteredTrace.add(completeEvent);
							
							//put the single events in the startEvent and complete events. 
							if (singleEvents.size()==0)// (1) no single events or (2) nested trace is null. 
							{
								//if start with complete, then discard
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
							else if (singleEvents.size()==1)//single is not null. 
							{
								// we also need to check if signleEvents.get(0) is "complete"....
								if ((singleEvents.get(0).getAttributes().get("lifecycle:transition").toString().equals("start"))
										&& (startEvent.getAttributes().get("Nested").toString().equals("False")))
								{
									startEvent =singleEvents.get(0);
									completeEvent=event;
								}
								else {
									startEvent =event;
									completeEvent=null;
								}						
							}
							else // for single events are >=2
							{
								int index =-1;
								// we also need to check if signleEvents.get(0) is "complete"....
								for (XEvent tempEvent: singleEvents)
								{
									index++;
									if (tempEvent.getAttributes().get("lifecycle:transition").toString().equals("start"))
									{
										startEvent=tempEvent;
										break;
									}
								}

								if (startEvent==null)
								{
									startEvent=event;
									completeEvent=null;
								}
								else
								{
									completeEvent=event;
								}								
								// add more events to nestedTrace
								for (int i=index;i<singleEvents.size();i++)
								{
									nestedTrace.add(singleEvents.get(i));
								}
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
						//System.out.println("add to list"+ completeEvent.toString());
						completeEvent=event;
					}
				}
				
			}//for(XEvent event: trace)
			
			// to deal with the last, we add it to the filteredTrace.
			//add the current completeEvent and nestedTrace to the nestedEvent2Subtrace
			if((startEvent!=null) && (completeEvent!=null))
			{
				XEventClass startEventClass= Xloginfo.getEventClasses().getClassOf(startEvent);
				XEventClass completeEventClass= Xloginfo.getEventClasses().getClassOf(completeEvent);
				String startEventlifecycle = startEvent.getAttributes().get("lifecycle:transition").toString();
				String completeEventlifecycle = completeEvent.getAttributes().get("lifecycle:transition").toString();
				
				if ((startEventClass.toString().equals(completeEventClass.toString()))
						&&(!startEventlifecycle.equals(completeEventlifecycle)))
				{
					if (nestedTimes==0)
					{						
						ArrayList<XEvent> singleEvents = new ArrayList<XEvent>();
						//add the current completeEvent and nestedTrace to the nestedEvent2Subtrace
						if (nestedTrace.size()>0)
						{
							//get the nesting frequency
							double nestingFrequency =computeNestingFrequency(Xloginfo, startEventClass, inputlog, ourClassifier);
							System.out.println(startEventClass +" nestingFrequency: " +nestingFrequency);
							
							// add the nested Trace to the, first filter to include only the coupled pairs. 
							singleEvents = findSingleEvents(Xloginfo, ourClassifier, nestedTrace);
							//XTrace newlyCreatedTrace =(XTrace)nestedTrace.clone();
							XTrace coupledTrace = findCoupledTraceFragment(Xloginfo, ourClassifier,nestedTrace);
							nestedTrace.clear();
							
							// if the current pair is detected as nesting. 
							if (nestingFrequency >= threshold)
							{
								// the current start and complete events are nested events. 
								startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
								completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "True"));
								nestedEvent2Subtrace.put(completeEventClass, coupledTrace);
								// add the current nested event class to the list. 
								System.out.println("Detected Nested Events: "+startEventClass);
							}
							else// for those nested event that is predicted as not, we add its sub trace to the filteredTrace
							{
								startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
								completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
								for (XEvent tempevent: coupledTrace)
								{
									tempevent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
									filteredTrace.add(tempevent);
								}
							}
						}	
						else //nestedTrace.size()==0
						{
							startEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
							completeEvent.getAttributes().put("Nested", new XAttributeLiteralImpl("Nested", "False"));
						}
					}
					
					
					filteredTrace.add(startEvent);
					filteredTrace.add(completeEvent);
					
				}
			}
			
			//to make sure that all events in the filteredTrace has "Nested" attribute. if not, add it as "False" 
			
			//add trace to the main log after ordering events according to their nano-timestamp.
			filteredLog.add(orderEventLogwithTimestamp(filteredTrace));

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
					//subLog.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "subLog of "+eventclass.toString().split("\\+")[2]));
					subLog.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "subLog of "+eventclass.toString()));
					XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Acticity Name", XConceptExtension.KEY_NAME, "Class", "Package");
					XEventAttributeClassifier classifierComponent = new XEventAttributeClassifier("Component","Belonging_Component");
					subLog.getClassifiers().add(classifierActivity);
					subLog.getClassifiers().add(classifierComponent);
					convertedSubLogMapping.put(eventclass, subLog);
				}
			}
		}
		
		
		// read through the inputHash to add subtraces for each sub-log 
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
	
	// detect single events from a trace, i.e. if input: AsAcBs, then Bs is returned.  
	public static ArrayList<XEvent> findSingleEvents(XLogInfo xlogInfo, XEventClassifier ourClassifer, XTrace trace)
	{
		System.out.println("in single events");
		XTrace original = (XTrace)trace.clone();
		ArrayList<XEvent> singleEventList = new ArrayList<>();
		// store the index of coupled events. 
		Set<Integer> indexes = new HashSet<Integer>();
		for(int i=0;i<original.size();i++)
		{
			//for (int j=i; j< original.size();j++)
			for (int j=original.size()-1; j>=0;j--)
			{
				if((xlogInfo.getEventClasses().getClassOf(original.get(i)).toString().
						equals(xlogInfo.getEventClasses().getClassOf(original.get(j)).toString()))
						&& !( original.get(i).getAttributes().get("lifecycle:transition").toString().
								equals(original.get(j).getAttributes().get("lifecycle:transition").toString())))
				{
					indexes.add(i);
					indexes.add(j);
					break;
				}
			}
		}
		
		for(int i=0;i<original.size();i++)
		{
			if (!indexes.contains(i))
			{
				//construct the single list. 
				System.out.println("the index of single event is: "+original.get(i).toString());
				singleEventList.add(original.get(i));
			}
		}
		
		return singleEventList;
	}
	
	// detect coupled event pairs from the trace, i.e., if input: AsAcBs, then AsAc is the output. 
	public static XTrace findCoupledTraceFragment(XLogInfo xlogInfo, XEventClassifier ourClassifer, XTrace trace)
	{	
		System.out.println("in coupledtrace fragment");
		XTrace original = (XTrace)trace.clone();
		XFactory factory = new XFactoryNaiveImpl();
		XTrace coupledTraceFragment = factory.createTrace();// the main trace

		// store the index of coupled events. 
		Set<Integer> indexes = new HashSet<Integer>();
		for(int i=0;i<original.size();i++)
		{
			//for (int j=i; j< original.size();j++)
			for (int j=original.size()-1; j>=0;j--)
			{
				if((xlogInfo.getEventClasses().getClassOf(original.get(i)).toString().
						equals(xlogInfo.getEventClasses().getClassOf(original.get(j)).toString()))
						&& !( original.get(i).getAttributes().get("lifecycle:transition").toString().
								equals(original.get(j).getAttributes().get("lifecycle:transition").toString())))
				{
					indexes.add(i);
					indexes.add(j);
					break;
				}
			}
		}

		for(int i=0;i<original.size();i++)
		{
			if (indexes.contains(i))
			{
				//System.out.println(i);
				//construct the single list. 
				coupledTraceFragment.add(original.get(i));
			}
		}
		
		return coupledTraceFragment;
	}
	
	// order the events in each trace based on the timestamp (nano-seconds). 
	public static XTrace orderEventLogwithTimestamp(XTrace inorderedTrace)
	{
		XFactory factory = new XFactoryNaiveImpl();
		XTrace orderedTrace = factory.createTrace();
		
		//add trace name for each newly created trace. 
		orderedTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name",
				inorderedTrace.getAttributes().get("concept:name").toString()));
		
		for (XEvent event: inorderedTrace)
		{
			//adding using nano-
			OrderedInsert(orderedTrace, event);
		}

		return orderedTrace;
	}
	// insert event to trace with Timestamp_Nano order
	public static synchronized void OrderedInsert(XTrace trace, XEvent event)
	{
		if (trace.size() == 0) {
			// append if the current trace is empty
			trace.add(event);
			return;
		}
		
		//get the current key of timestamp in nano. 
		XAttribute currentAttr = event.getAttributes().get("Timestamp_Nano");
		
		//if event does not have this attribute, just add it
		if (currentAttr == null) {
			// append if event has no timestamp
			trace.add(event);
			return;
		}
		
		long currentValue = Long.parseLong(((XAttributeLiteral)currentAttr).getValue().toString());
		
		for (int i = (trace.size() - 1); i >= 0; i--) {
			XAttribute refTsAttr = trace.get(i).getAttributes().get("Timestamp_Nano");
			if (refTsAttr == null) {
				// trace contains events w/o timestamps, append.
				trace.add(event);
				return;
			}
			long refTsValue = Long.parseLong(((XAttributeLiteral) refTsAttr).getValue().toString());
			
			if (currentValue-refTsValue>0) {
				// insert position reached
				trace.add(i + 1, event);
				return;
			}
		}
		//insert at the beginning 
		trace.add(0, event);
		return;
	}
	
	// obtain the nesting frequency 
	public static double computeNestingFrequency(XLogInfo Xloginfo, XEventClass startEventClass, XLog inputlog, XEventClassifier ourClassifier)
	{
		int countTraces= NestedEventClassDetectionUsingThreshold.countTracesWithEventClass(Xloginfo, startEventClass, inputlog);
		int countTracesNesting = NestedEventClassDetectionUsingThreshold.countTracesWithEventClassNesting(ourClassifier, Xloginfo, startEventClass, inputlog);
		double nestingFrequency = ((double)countTracesNesting)/((double)countTraces);
		
		return nestingFrequency;
	}
	
}
