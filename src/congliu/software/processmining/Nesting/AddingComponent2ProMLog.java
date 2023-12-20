package congliu.software.processmining.Nesting;

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

@Plugin(
		name = "Adding component to ProM Log",// plugin name
		
		returnLabels = {"XES Log"}, //reture labels
		returnTypes = {XLog.class},//reture class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"ProM event log"},
		
		userAccessible = true,
		help = "This plugin aims to convert the log." 
		)
public class AddingComponent2ProMLog {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Adding component to ProM Log, default",
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
			modifiedTraceList.add(AddingComponentAttribute(trace, factory));
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
	
	public XTrace AddingComponentAttribute(XTrace originalTrace, XFactory factory)
	{		
		for(XEvent event : originalTrace)
		{
			event.getAttributes().put("Belonging_Component", new XAttributeLiteralImpl("Belonging_Component","ProM"));
		}
		
		return originalTrace;
	}

}
