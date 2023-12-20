package congliu.software.processmining.Nesting;

import java.util.ArrayList;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributeTimestamp;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeTimestampImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

/**
 * pre-processing Nick's event data
 * (1) adding component attribute
 * (2) convert sequential label to Highlevel activity.  
 * @author cliu3
 *
 */

@Plugin(
		name = "Pre-processing Nick's Event Data",// plugin name
		
		returnLabels = {"XES Log"}, //reture labels
		returnTypes = {XLog.class},//reture class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"smart home event log"},
		
		userAccessible = true,
		help = "This plugin aims to convert the log." 
		)
public class AddingHighLevelActivity {

	
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
	
		XFactory factory = new XFactoryNaiveImpl();

		//create a list of traces to store ordered traces.
		ArrayList<XTrace> modifiedTraceList = new ArrayList<XTrace>();
		
		// ordering each traces 
		for (XTrace trace: originalLog)
		{
			//add each ordered trace to list
			modifiedTraceList.add(modifyEventLogwithlabel(trace, factory));
		}
		
		//clear existing traces
		originalLog.clear();
		
		//
		for (XTrace trace:modifiedTraceList)
		{
			originalLog.add(trace);
		}
		
		return originalLog;
	}
	
	public XTrace modifyEventLogwithlabel(XTrace originalTrace, XFactory factory)
	{
		
		XTrace modifiedTrace = factory.createTrace();
		//add trace name for each newly created trace. 
		modifiedTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name",
				originalTrace.getAttributes().get("concept:name").toString()));
		
		ArrayList<XEvent> tempEventList = new ArrayList<XEvent>(); 
		XEvent currentEvent = factory.createEvent();
		for(XEvent event : originalTrace)
		{
			currentEvent=event;
			if (tempEventList.size()==0)
			{
				tempEventList.add(currentEvent);
			}
			else
			{
				if (tempEventList.get(0).getAttributes().get("label").toString().equals(
						currentEvent.getAttributes().get("label").toString()))
				{
					tempEventList.add(currentEvent);
				}
				else
				{
					//create start event
					XEvent startEvent = factory.createEvent();

					startEvent.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name",
							tempEventList.get(0).getAttributes().get("label").toString()));
					startEvent.getAttributes().put("lifecycle:transition", new XAttributeLiteralImpl("lifecycle:transition",
							"start"));
					startEvent.getAttributes().put("time:timestamp", new XAttributeTimestampImpl("time:timestamp", 
														(((XAttributeTimestamp)tempEventList.get(0).getAttributes().get("time:timestamp")).getValue()).getTime()-10));

					startEvent.getAttributes().put("Belonging_Component", new XAttributeLiteralImpl("Belonging_Component",
							"A"));

					
					XEvent endEvent = factory.createEvent();

					endEvent.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name",
							tempEventList.get(0).getAttributes().get("label").toString()));
					endEvent.getAttributes().put("lifecycle:transition", new XAttributeLiteralImpl("lifecycle:transition",
							"complete"));
					endEvent.getAttributes().put("time:timestamp", new XAttributeTimestampImpl("time:timestamp", 
							(((XAttributeTimestamp)tempEventList.get(tempEventList.size()-1).getAttributes().get("time:timestamp")).getValue()).getTime()+10));
					endEvent.getAttributes().put("Belonging_Component", new XAttributeLiteralImpl("Belonging_Component",
							"A"));
					
					//add start
					modifiedTrace.add(startEvent);
					//add templist to trace
					for (XEvent e: tempEventList)
					{
						e.getAttributes().put("Belonging_Component", new XAttributeLiteralImpl("Belonging_Component","A"));
						modifiedTrace.add(e);
					}
					tempEventList.clear();
					//add end
					modifiedTrace.add(endEvent); 
					
					tempEventList.add(currentEvent);
				}
			}
			
		}
		
		// to deal with the final cluster
		//create start event
		XEvent startEvent = factory.createEvent();

		startEvent.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name",
				tempEventList.get(0).getAttributes().get("label").toString()));
		startEvent.getAttributes().put("lifecycle:transition", new XAttributeLiteralImpl("lifecycle:transition",
				"start"));
		startEvent.getAttributes().put("time:timestamp", new XAttributeTimestampImpl("time:timestamp", 
											(((XAttributeTimestamp)tempEventList.get(0).getAttributes().get("time:timestamp")).getValue()).getTime()-10));

		startEvent.getAttributes().put("Belonging_Component", new XAttributeLiteralImpl("Belonging_Component",
				"A"));
		
		XEvent endEvent = factory.createEvent();

		endEvent.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name",
				tempEventList.get(0).getAttributes().get("label").toString()));
		endEvent.getAttributes().put("lifecycle:transition", new XAttributeLiteralImpl("lifecycle:transition",
				"complete"));
		endEvent.getAttributes().put("time:timestamp", new XAttributeTimestampImpl("time:timestamp", 
				(((XAttributeTimestamp)tempEventList.get(tempEventList.size()-1).getAttributes().get("time:timestamp")).getValue()).getTime()+10));
		endEvent.getAttributes().put("Belonging_Component", new XAttributeLiteralImpl("Belonging_Component",
				"A"));
		
		//add start
		modifiedTrace.add(startEvent);
		//add templist to trace
		for (XEvent e: tempEventList)
		{
			modifiedTrace.add(e);
			e.getAttributes().put("Belonging_Component", new XAttributeLiteralImpl("Belonging_Component","A"));
		}
		//add end
		modifiedTrace.add(endEvent); 
		
		return modifiedTrace;
		
	}
}



