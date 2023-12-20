package MultiInstanceProcessDiscovery;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

/**
 * this plugin aims to transform a normal event log (only with complete event log) to a lifecycle event log. 
 * Note that the event name of the normal event log is "AAA_s" or "AAA_c" that indicates the lifecycle information
 * @author cliu3
 *
 */

@Plugin(
		name = "Lifecycle Event Log Generator",// plugin name
		
		returnLabels = {"XES Log"}, //return labels
		returnTypes = {XLog.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"XES Log"},
		
		userAccessible = true,
		help = "This plugin aims to transform a normal event log (only with complete event log) to a lifecycle event log. Note that the event name of the normal event log is AAA_s or AAA_c that indicates the lifecycle information." 
		)
public class LifecycleEventLogGenerator {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl;liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Lifecycle Event Log Generator, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	public XLog lifecycleEventLogGenerator(UIPluginContext context, XLog originalLog) 
	{
		XLog lifecycleLog = (XLog)originalLog.clone();

		for(XTrace trace: lifecycleLog)
		{
			for (XEvent event: trace)
			{
				//we require that all event name are in this format "AAA_s" or "AAA_c"
				String eventName =XConceptExtension.instance().extractName(event);
				String lastElement = eventName.substring(eventName.lastIndexOf("_") + 1);
				;
				
				if(lastElement.equals("s"))
				{
					XLifecycleExtension.instance().assignTransition(event, "start");
				}
				
				if(lastElement.equals("c"))
				{
					XLifecycleExtension.instance().assignTransition(event, "complete");
				}
				
				XConceptExtension.instance().assignName(event, eventName.substring(0, eventName.lastIndexOf("_")));
			}
		}
		
		
		return lifecycleLog;
	}
	
	
}
