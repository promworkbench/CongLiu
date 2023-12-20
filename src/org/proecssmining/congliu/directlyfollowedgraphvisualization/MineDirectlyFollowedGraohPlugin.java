package org.proecssmining.congliu.directlyfollowedgraphvisualization;

import java.util.concurrent.CancellationException;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.dfgOnly.Dfg;
import org.processmining.plugins.InductiveMiner.dfgOnly.plugins.XLog2Dfg;

/**
 * This plugin aims to test the graphviz visualization of a directly followed graph 
 * 
 * step 1: using the mine directly-followed graph plugin 
 * step 2: use inductive miner to discover sub-trees
 * step 3: merge the sub-trees using sequential operator
 * @author cliu3
 *
 */

@Plugin(
		name = "Mine a Directly Followed Graph ",// plugin name
		
		returnLabels = {"Directly Followed Graph"}, //reture labels
		returnTypes = {Dfg.class},//reture class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Event Log"},
		
		userAccessible = true,
		help = "This plugin aims to disover a directly followed graph using an event log." 
		)
public class MineDirectlyFollowedGraohPlugin {
	
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Mine Directly Followed Graph, default",
			requiredParameterLabels = { 0 }
			)
	  public static Dfg localizedDiscovery(UIPluginContext context, XLog inputlog) throws CancellationException 
		{
		
			// discover the directly followed graph
			XLog2Dfg logtodfg = new XLog2Dfg(); 
			Dfg dfg = logtodfg.log2Dfg(context, inputlog);
			
			return dfg;
			
			// we do not need to do visualization explicitly, the framework will do it automatically. 
			//what we need to do is to develop corresponding visualization plug which takes the specific
			// object as input, just like the GraphvizDirectlyFollowsGraph
		}

}


