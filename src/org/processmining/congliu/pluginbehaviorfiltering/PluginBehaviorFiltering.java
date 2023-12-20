package org.processmining.congliu.pluginbehaviorfiltering;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

/**
 * this plugin aims to filter the plugin calling recordings from the original plugin-level log.  
 * @author cliu3
 *
 */
public class PluginBehaviorFiltering {

	@Plugin(
			// plugin name
			name = "Plugin Behavior Filter ",
			
			//return labels
			returnLabels = {"Plugin Behavior Related Log"}, 
			// return class
			returnTypes = {XLog.class},
			
			userAccessible = true,
			help = "This plugin aims to obtain the plguin behavior related event logs, it removes the plugin calling information from the origianl log using time interval", 
			
			//input labels, corresponding with the second parameter of main function
			parameterLabels = {"ProM Plugin Execution Log"} 
			)
			
		@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	
	// the input of this plugin is an Xlog, and returns a filtered XLog (without plugin calling information)
    public static XLog fitering(PluginContext context, XLog inputlog) {
		//keep the input log unchanged

		XLog  originallog = (XLog) inputlog.clone();
		
		Filtering filter = new Filtering(originallog);
		
		XLog filtered = filter.filtering();

        return filtered;
}
}
