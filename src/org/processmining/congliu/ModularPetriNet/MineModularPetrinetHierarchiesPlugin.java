package org.processmining.congliu.ModularPetriNet;

import java.util.HashMap;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.congliu.softwareBehaviorDiscovery.NestedMethodCallDetectionTrueConcurrency;
import org.processmining.congliu.softwareBehaviorDiscovery.NestingFilterDialog;
import org.processmining.congliu.softwareBehaviorDiscovery.NestingThresholdValue;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

/**
 * this plugin aims to discover a hierarchical petri net with components.
 * Input: XLog (obtained from the ProM software event execution. it should be composed of event lifecycle pairs,
 * we assume the "consistent" property)
 * Output: hierarchical petri net with components, i.e. ModularPetrinetHierarchies
 * @author cliu3
 *
 */
@Plugin(
		// plugin name
		name = "Mine a Hierarchical Petri Net with Components",
		
		//return labels
		returnLabels = {"Hierarchical Petri Net with Components"}, 
		// return class 
		returnTypes = {ModularPetrinetHierarchies.class},
		
		userAccessible = true,
		help = "This plugin aims to discover a Hierarchical Petri net with Components from the origianl"
				+ "software event log with lifecycle information.", 
		
		//input labels, corresponding with the second parameter of main function
		parameterLabels = {"Software Event Log (lifecycle)"} 
		)

public class MineModularPetrinetHierarchiesPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Hierarchical Petri Net with Components, default",
			
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	
	public static ModularPetrinetHierarchies discoverModularPetriNetHierarchy (UIPluginContext context, XLog inputlog) 
	{
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel(
				"Discovered Hierarchical Petri Net with Components from " + XConceptExtension.instance().extractName(inputlog));
		
		// we need to set the nesting detection filter parameter, use a slide bar. we plan to modify the IMMiningDialog
		
		// here we first use a fixed number, then extend it to configurable parameter. @c.liu 2016-2-8
		
		final NestingThresholdValue threshold = new NestingThresholdValue();
		NestingFilterDialog nestDialog = new NestingFilterDialog(context,threshold);
		
		InteractionResult result1 = context.showWizard("Configure Nesting Filtering Parameter", true, true, nestDialog);
		if (result1 != InteractionResult.FINISHED) {
			return null;
		}
		
		System.out.println("the input threshold is: "+threshold.getValue()); 
		//set the inductive miner parameters
		IMMiningDialog dialog = new IMMiningDialog(inputlog);
		InteractionResult result = context.showWizard("Configure Parameters for Inductive Miner (used for all sub-nets)", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		// the mining parameters are set here
		MiningParameters IMparameters = dialog.getMiningParameters();
		
		return mine(threshold.getValue(), IMparameters, inputlog);
	}
	
	public static ModularPetrinetHierarchies mine(double threshold, MiningParameters IMparameters, XLog inputlog)
	{
		// first use the nested method detection plugin to get the main one containing top-level methods as well as
	    // a hashmap maps the nested event classes to sub-log. 
				
		//NestedMethodCallDetection nestedDetection = new NestedMethodCallDetection(IMparameters.getClassifier(),inputlog);
		//here we change the source of nested detection to handle infrequent behavior and distinguish concurrency and nesting
		// by taking the threshold as input. 
		NestedMethodCallDetectionTrueConcurrency nestedDetection = 
				new NestedMethodCallDetectionTrueConcurrency(threshold, IMparameters.getClassifier(), inputlog);
		
		// the filetered Main event log 
		XLog filteredMainLog= nestedDetection.getFilteredLog();
		
		// the sublog mapping with its correspoing xeventclass
		HashMap<XEventClass, XLog> xeventclass2Sublog = nestedDetection.getXeventClass2XLog();
		
		// create the ModularPetrinetHierarchies
		ModularPetrinetHierarchies mpnh = new ModularPetrinetHierarchies();
		
		//its mpn part. 
		//function log2dfge is used to discover a mpn from a log. 
		ModularPetriNet mpn =MineModularPetriNet.mineModularPetriNet(IMparameters, filteredMainLog);
		mpnh.setMpn(mpn);
		
		//mine its mapping from XEventClass to ModularPetrinetHierarchies part
		HashMap<XEventClass, ModularPetrinetHierarchies> eventclass2dfgeh = xeventclass2mpnh(threshold, IMparameters, xeventclass2Sublog);
		mpnh.setXEventClass2mpnh(eventclass2dfgeh);
		
		return mpnh;
	}
	
	//get the mpnh for each eventclass, we try to extend it to length-N nesting
	public static HashMap<XEventClass, ModularPetrinetHierarchies> xeventclass2mpnh(double threshold,MiningParameters IMparameters, HashMap<XEventClass, XLog> xeventclass2sublog) 
	{
		HashMap<XEventClass, ModularPetrinetHierarchies> xeventclass2mpnh= new HashMap<XEventClass, ModularPetrinetHierarchies>();
		
		for(XEventClass xeventClass :xeventclass2sublog.keySet())
		{
			// detect the nested call
			NestedMethodCallDetectionTrueConcurrency nestedCallDetection = 
					new NestedMethodCallDetectionTrueConcurrency(threshold, IMparameters.getClassifier(), xeventclass2sublog.get(xeventClass));
			
			//get the main event log
			XLog MainLog= nestedCallDetection.getFilteredLog();
			
			//get the sub-event log
			HashMap<XEventClass, XLog> XeventClass2Sublog = nestedCallDetection.getXeventClass2XLog();
			
			// create the ModularPetrinetHierarchies
			ModularPetrinetHierarchies mpnh = new ModularPetrinetHierarchies();
			
			//its mpn part. 
			//function log2dfge is used to discover a mpn from a log. 
			ModularPetriNet mpn =MineModularPetriNet.mineModularPetriNet(IMparameters,MainLog);
			mpnh.setMpn(mpn);
			
			//if the XeventClass2SubLog is null
			if (XeventClass2Sublog.size()==0)
			{
				mpnh.setXEventClass2mpnh(null);
			}
			else
			{
				//mine its mapping from XEventClass to ModularPetrinetHierarchies part
				HashMap<XEventClass, ModularPetrinetHierarchies> event2mpnh= xeventclass2mpnh(threshold, IMparameters, XeventClass2Sublog);
				mpnh.setXEventClass2mpnh(event2mpnh);
			}
			xeventclass2mpnh.put(xeventClass, mpnh);
		}
		return xeventclass2mpnh;
	}

}
