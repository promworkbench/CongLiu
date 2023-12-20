package org.processmining.congliu.examplecodes;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.congliu.softwareBehaviorDiscoveryLengthN.DFGExtendedHierarchies;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

@Plugin(
		// plugin name
		name = "Mine a Directly Followed Graph with Component and Hierarchy (Length-N Nesting New)",
		
		//return labels
		returnLabels = {"Directly Followed Graph with Component and Hierarchy"}, 
		// return class, here the DFGExtended is an improved Dfg by extending nested and component information 
		returnTypes = {DFGExtendedHierarchies.class},
		
		userAccessible = true,
		help = "This plugin aims to discover a directly followed graph from the origianl event log generated from ProM execution", 
		
		//input labels, corresponding with the second parameter of main function
		parameterLabels = {"ProM Software Event Log"} 
		)
public class pluginExample {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Directly Followed Graph with Components and Hierarchies, default",
			// the number of required parameters, {0} means one input parameter, {0, 1} means two input parameter
			requiredParameterLabels = {0}
			)
	//UIPluginContext 
	public static DFGExtendedHierarchies discoverDFGwithComponentandHierarchy (PluginContext context, XLog inputlog) 
	{
		//get the log name 
		context.getFutureResult(0).setLabel(
				"Extended Directly-follows graph of " + XConceptExtension.instance().extractName(inputlog));
		return new DFGExtendedHierarchies();
	}
	
	
}
