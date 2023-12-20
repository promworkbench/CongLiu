package congliu.processmining.softwareprocessmining;

import java.util.ArrayList;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

public class OrderingEventsNano {
	
	public static XLog ordering(XLog inputlog, String attributeName)
	{
//		//get the log name from the original log. it is shown as the title of returned results. 
//		context.getFutureResult(0).setLabel(
//				"Ordering: " + XConceptExtension.instance().extractName(inputlog));
		
		//keep the input log unchanged
		XLog  originalLog = (XLog) inputlog.clone();
		
		//create a list of traces to store ordered traces.
		ArrayList<XTrace> orderedTraceList = new ArrayList<XTrace>();
		
		// ordering each traces 
		for (XTrace trace: originalLog)
		{
			//add each ordered trace to list
			orderedTraceList.add(orderEventLogwithTimestamp(trace, attributeName));
		}
		
		//clear existing traces
		originalLog.clear();
		
		//
		for (XTrace trace:orderedTraceList)
		{
			originalLog.add(trace);
		}
		
		return originalLog;
	}
	
	
	// order the events in each trace based on the timestamp (nano-seconds). 
	public static XTrace orderEventLogwithTimestamp(XTrace inorderedTrace, String attributeName)
	{
		XFactory factory = new XFactoryNaiveImpl();
		XTrace orderedTrace = factory.createTrace();
		
		//add trace name for each newly created trace. if the current trace has name.
		if(inorderedTrace.getAttributes().get("concept:name")!=null)
		{
			orderedTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name",
					inorderedTrace.getAttributes().get("concept:name").toString()));
		}

		for (XEvent event: inorderedTrace)
		{
			//adding using nano-
			OrderedInsert(orderedTrace, event, attributeName);
		}

		return orderedTrace;
	}
	
	// insert event to trace with Timestamp_Nano order
	public static synchronized void OrderedInsert(XTrace trace, XEvent event, String attributeName)
	{
		if (trace.size() == 0) {
			// append if the current trace is empty
			trace.add(event);
			return;
		}
		
		//get the current key of timestamp in nano. 
		XAttribute currentAttr = event.getAttributes().get(attributeName);
		
		//if event does not have this attribute, just add it
		if (currentAttr == null) {
			// append if event has no timestamp
			trace.add(event);
			return;
		}
		
		long currentValue = Long.parseLong(((XAttributeLiteral)currentAttr).getValue().toString());
		
		for (int i = (trace.size() - 1); i >= 0; i--) {
			XAttribute refTsAttr = trace.get(i).getAttributes().get(attributeName);
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

}
