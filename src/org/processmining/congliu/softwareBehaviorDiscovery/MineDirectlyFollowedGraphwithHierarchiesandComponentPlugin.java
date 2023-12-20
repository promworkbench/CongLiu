package org.processmining.congliu.softwareBehaviorDiscovery;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

/**
 * this plugin aims to discover a directly followed graph with component and hierarchical nodes.
 * Input: XLog (obtained from the ProM software event execution. it should be composed of event lifecycle pairs )
 * Output: directly followed graph with component and hierarchies, i.e. DFGExtendedHierarchies
 * @author cliu3
 *
 */
@Plugin(
		// plugin name
		name = "Mine a Directly Followed Graph with Component and Hierarchy (Length-2 Nesting)",
		
		//return labels
		returnLabels = {"Directly Followed Graph with Component and Hierarchy"}, 
		// return class, here the DFGExtended is an improved Dfg by extending nested and component information 
		returnTypes = {DFGExtendedHierarchies.class},
		
		userAccessible = true,
		help = "This plugin aims to discover a directly followed graph from the origianl event log generated from ProM execution", 
		
		//input labels, corresponding with the second parameter of main function
		parameterLabels = {"ProM Software Event Log"} 
		)

public class MineDirectlyFollowedGraphwithHierarchiesandComponentPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Directly Followed Graph with Components and Hierarchies, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	public static DFGExtendedHierarchies discoverDFGwithComponentandHierarchy (PluginContext context, XLog inputlog) 
	{
		//get the log name 
		context.getFutureResult(0).setLabel(
				"Extended Directly-follows graph of " + XConceptExtension.instance().extractName(inputlog));
		return mine(inputlog);
	}
	
	public static DFGExtendedHierarchies mine(XLog inputlog)
	{
		// first use the nested method detection plugin to get the main one containing top-level methods as well as
				// a hashmap maps the xeventclass to xlog for those hierarchical events. 
				
		NestedMethodCallDetection nestedDetection = new NestedMethodCallDetection(inputlog);
		
		XLog filteredMainLog= nestedDetection.getFilteredLog();
		
		HashMap<XEventClass, XLog> xeventclass2Sublog = nestedDetection.getXeventClass2XLog();
		
		// create the DFGExtendedHierarchies
		DFGExtendedHierarchies dfgExtendedhie = new DFGExtendedHierarchies();
		
		//its dfg part. 
		DFGExtended dfge = new DFGExtended(1);
		dfge =DirectlyFollowedGraphwithComponent.log2dfge(filteredMainLog);
		dfgExtendedhie.setDfgExtended(dfge);
		
		//mine its mapping from XEventClass to DFGExtended part
		
		HashMap<XEventClass, DFGExtended> event2dfge = xeventclass2dfg(xeventclass2Sublog);
		dfgExtendedhie.setXEventClass2DFG(event2dfge);
		
		return dfgExtendedhie;
	}
	
	//get the dfge for each eventclass, it can be used for length-2 nesting
	public static HashMap<XEventClass, DFGExtended> xeventclass2dfg(HashMap<XEventClass, XLog> xeventclass2sublog) 
	{
		HashMap<XEventClass, DFGExtended> xeventclass2dfg = new HashMap<XEventClass, DFGExtended>();
		
		//System.out.println(xeventclass2sublog.size());
		for(XEventClass xeventClass :xeventclass2sublog.keySet())
		{
			xeventclass2dfg.put(xeventClass, DirectlyFollowedGraphwithComponent.log2dfge(xeventclass2sublog.get(xeventClass)));
		}
		return xeventclass2dfg;
	}
	
	/*
	//get the dfghie for each eventclass, we try to extend it to length-N nesting
	public static HashMap<XEventClass, DFGExtendedHierarchies> xeventclass2dfghie(HashMap<XEventClass, XLog> xeventclass2sublog) 
	{
		HashMap<XEventClass, DFGExtendedHierarchies> xeventclass2dfghie = new HashMap<XEventClass, DFGExtendedHierarchies>();
		
		//System.out.println(xeventclass2sublog.size());
		for(XEventClass xeventClass :xeventclass2sublog.keySet())
		{
			// detect the nested call
			NestedMethodCallDetection nestedDetection = new NestedMethodCallDetection(xeventclass2sublog.get(xeventClass));
			
			//get the main event log
			XLog MainLog= nestedDetection.getFilteredLog();
			
			//get the sub-event log
			HashMap<XEventClass, XLog> XeventClass2Sublog = nestedDetection.getXeventClass2XLog();
			
			// create the DFGExtendedHierarchies
			DFGExtendedHierarchies dfgExtendedhie = new DFGExtendedHierarchies();
			
			//its dfg part. 
			DFGExtended dfge = new DFGExtended(1);
			dfge =DirectlyFollowedGraphwithComponent.log2dfge(MainLog);
			dfgExtendedhie.setDfgExtended(dfge);
			
			//if the XeventClass2SubLog is null
			if (XeventClass2Sublog.size()==0)
			{
				dfgExtendedhie.setXEventClass2DFG(null);
			}
			else
			{
			//mine its mapping from XEventClass to DFGExtendedHierarchies part
			
			HashMap<XEventClass, DFGExtendedHierarchies> event2dfghie = xeventclass2dfghie(XeventClass2Sublog);
			dfgExtendedhie.setXEventClass2DFG(event2dfghie);
			}
			
			xeventclass2dfghie.put(xeventClass, dfgExtendedhie);
		}
		return xeventclass2dfghie;
	}
	*/
}
