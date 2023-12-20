/**
 * ordering each trace using "Timestamp_Nano" attribute, 
 * the next step is to show dialog to choose which attribute to use. 
 * 2016-3-06@author cliu3
 */
package congliu.software.processmining.Nesting;

import java.util.ArrayList;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
		name = "Ordering Software Event Log following Nano-seconds",// plugin name
		
		returnLabels = {"Software XES Log"}, //reture labels
		returnTypes = {XLog.class},//reture class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"disordered Software Event log"},
		
		userAccessible = true,
		help = "This plugin aims to ordering a software event log according to its timestamp." 
		)
public class OrdeingEventLogUsingNanoSeconds {

	@UITopiaVariant(
		        affiliation = "TU/e", 
		        author = "Cong liu", 
		        email = "c.liu.3@tue.nl OR liucongchina@163.com"
		        )
		@PluginVariant(
				variantLabel = "Ordering software event log, default",
				// the number of required parameters, {0} means one input parameter
				requiredParameterLabels = {0}
				)
		
		public XLog ordering(UIPluginContext context, XLog inputlog)
		{
			//get the log name from the original log. it is shown as the title of returned results. 
			context.getFutureResult(0).setLabel(
					"Ordering: " + XConceptExtension.instance().extractName(inputlog));
			
			//keep the input log unchanged
			XLog  originalLog = (XLog) inputlog.clone();
			
			//create a list of traces to store ordered traces.
			ArrayList<XTrace> orderedTraceList = new ArrayList<XTrace>();
			
			// ordering each traces 
			for (XTrace trace: originalLog)
			{
				//add each ordered trace to list
				orderedTraceList.add(orderEventLogwithTimestamp(trace));
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
		public XTrace orderEventLogwithTimestamp(XTrace inorderedTrace)
		{
			XFactory factory = new XFactoryNaiveImpl();
			XTrace orderedTrace = factory.createTrace();
			
			if(inorderedTrace.getAttributes().get("concept:name")!=null)
			{
				//add trace name for each newly created trace. 
				orderedTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name",
						inorderedTrace.getAttributes().get("concept:name").toString()));
			}
//			else {
//				//add trace name for each newly created trace. 
//				orderedTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name",
//						 "case"));
//			}

			
			for (XEvent event: inorderedTrace)
			{
				//adding using nano-
				OrderedInsert(orderedTrace, event);
			}

			return orderedTrace;
		}
		
		// insert event to trace with Timestamp_Nano order
		public synchronized void OrderedInsert(XTrace trace, XEvent event)
		{
			if (trace.size() == 0) {
				// append if the current trace is empty
				trace.add(event);
				return;
			}
			
			//get the current key of timestamp in nano. 
			XAttribute currentAttr = event.getAttributes().get("apprun:nanotime");
			
			//if event does not have this attribute, just add it
			if (currentAttr == null) {
				// append if event has no timestamp
				trace.add(event);
				return;
			}
			
			long currentValue = Long.parseLong(((XAttributeLiteral)currentAttr).getValue().toString());
			
			for (int i = (trace.size() - 1); i >= 0; i--) {
				XAttribute refTsAttr = trace.get(i).getAttributes().get("apprun:nanotime");
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
