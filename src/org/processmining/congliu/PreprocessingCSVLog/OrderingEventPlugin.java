package org.processmining.congliu.PreprocessingCSVLog;

import java.util.ArrayList;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

/**
 * This plugin tries to order the event cases by case according to the time stamp information (Nano)
 * It is specially tailored for software event data, with nanotime as "Timestamp_Nano"
 * In addition, on can further improve this plugin by adding other attribute as order base. 
 * @author cliu3
 *
 */

@Plugin(
		name = "Ordering Software Event Log using Timestamp",// plugin name
		
		returnLabels = {"Ordered Event Log"}, //reture labels
		returnTypes = {XLog.class},//reture class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Event Log"},
		
		userAccessible = true,
		help = "This plugin aims to order the event case by case using the traditional timestamp." 
		)
public class OrderingEventPlugin {
	
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Ordering Software Event Log, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	
	public XLog convert(UIPluginContext context, XLog inputLog)
	{
		XLog originalLog = (XLog) inputLog.clone();
		
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel(
				"Ordering: " + XConceptExtension.instance().extractName(originalLog));
		
		XFactory factory = new XFactoryNaiveImpl();

		//create a list of traces to store ordered traces.
		ArrayList<XTrace> orderedTraceList = new ArrayList<XTrace>();
		
		// ordering each traces 
		for (XTrace originalTtrace: originalLog)
		{
			//add each ordered trace to list
			orderedTraceList.add(orderingTrace(originalTtrace, factory));
		}
		
		originalLog.clear();
		
		//
		for (XTrace trace:orderedTraceList)
		{
			originalLog.add(trace);
		}
		
		return originalLog;
	}
	
	
	public XTrace orderingTrace(XTrace originalTrace, XFactory factory)
	{
		
		XTrace orderedTrace = factory.createTrace();
		//add trace name for each newly created trace. 
		orderedTrace.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name",
				originalTrace.getAttributes().get("concept:name").toString()));
		
		
		for(XEvent event : originalTrace)
		{
			orderedTrace.insertOrdered(event);
		}
		
		return orderedTrace;
		
	}

}
