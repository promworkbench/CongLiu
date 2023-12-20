package org.processmining.congliu.softwareBehaviorDiscovery;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * Improved: this class aims to discover the nesting relation, binary relation between two activities. 
 * After this, a mapping from nested activities to its nesting activity set is obtained. 
 * this class aims to discover a list contains all nested event classes. 
 * it preprocess an event log by taking the nesting frequency threshold as input. 
 * @author cliu3
 *
 */
public class NestedEventClassDetectionUsingThreshold {
	
	// count the number of traces with an event class
	public static int countTracesWithEventClass(XLogInfo Xloginfo, XEventClass inputEventClass, XLog inputLog)
	{
		int count =0;
		
		for (XTrace trace: inputLog)
		{
			for (XEvent event: trace)
			{
				XEventClass currentEventClass= Xloginfo.getEventClasses().getClassOf(event);
				if(inputEventClass.toString().equals(currentEventClass.toString()))
				{
					count++;
					break;
				}
			}
		}
		
		System.out.println("the number of traces with current event class is: " + count);
		return count;
	}
	
	// count the number of traces with an event class as nesting. 
	public static int countTracesWithEventClassNesting(XEventClassifier ourClassifier, XLogInfo Xloginfo, XEventClass inputEventClass, XLog inputLog)
	{
		System.out.println("???count the number of nested traces");
		int count =0;
		XFactory factory = new XFactoryNaiveImpl();
		
		for (XTrace trace: inputLog)
		{
			System.out.println("current in "+trace.getAttributes().get("concept:name").toString());
			
			XEvent startEvent =null;
			XEvent completeEvent =null;
			XTrace nestedTrace = factory.createTrace();
			int nestedTimes = 0;
			
			for (XEvent event: trace)
			{
				if ((startEvent ==null) && (completeEvent ==null))
				{
					if (Xloginfo.getEventClasses().getClassOf(event).toString().equals(inputEventClass.toString()))
					{
						if (event.getAttributes().get("lifecycle:transition").toString().equals("start"))
						{
							startEvent = event;
						}
					}

				}
				else if((startEvent!=null) && (completeEvent==null))
				{
					completeEvent = event;
				}
				else if ((startEvent!=null) && (completeEvent!=null))
				{
					XEventClass startEventClass= Xloginfo.getEventClasses().getClassOf(startEvent);
					XEventClass completeEventClass= Xloginfo.getEventClasses().getClassOf(completeEvent);
					String startEventlifecycle = startEvent.getAttributes().get("lifecycle:transition").toString();
					String completeEventlifecycle = completeEvent.getAttributes().get("lifecycle:transition").toString();
					
					//if the current start and complete eventclass match, they belong to the same event class with different lifecycle. 
					if ((startEventClass.toString().equals(completeEventClass.toString())) 
							&&(!startEventlifecycle.equals(completeEventlifecycle)))
					{
						//no nested method calls has been added to the subtrace. 
						if (nestedTimes==0)
						{			
							//ArrayList<XEvent> singleEventList = new ArrayList<XEvent>();
							
							//add the current completeEvent and nestedTrace to the nestedEvent2Subtrace
							if (nestedTrace.size()>0)
							{
								XTrace coupledTrace = NestedMethodCallDetectionTrueConcurrency.findCoupledTraceFragment(Xloginfo, ourClassifier,nestedTrace);
								//nestedTrace.clear();
								
								if (coupledTrace.size()>0)
								{
									count++;
									break;
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
						completeEvent=event;
					}
				}
			
				
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
						if (nestedTrace.size()>0)
						{
							XTrace coupledTrace = NestedMethodCallDetectionTrueConcurrency.findCoupledTraceFragment(Xloginfo, ourClassifier,nestedTrace);
							nestedTrace.clear();
							
							if (coupledTrace.size()>0)
							{
								System.out.println(startEvent.getAttributes().get("Timestamp_Nano"));
								count++;
								break;
							}
						}	
					}	
				}
			}	
		}
		}
		System.out.println("the number of traces with current event class as nesting is: " + count);
		return count;
	}
}
