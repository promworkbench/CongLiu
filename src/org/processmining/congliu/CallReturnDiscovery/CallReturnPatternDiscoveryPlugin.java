package org.processmining.congliu.CallReturnDiscovery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.congliu.softwareBehaviorDiscovery.DFGExtended;
import org.processmining.congliu.softwareBehaviorDiscovery.DFGExtendedHierarchies;
import org.processmining.congliu.softwareBehaviorDiscovery.DirectlyFollowedGraphwithComponent;
import org.processmining.congliu.softwareBehaviorDiscovery.MineDirectlyFollowedGraphwithHierarchiesandComponentPlugin;
import org.processmining.congliu.softwareBehaviorDiscovery.NestedMethodCallDetection;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

/**
 * this plugin aims to discover the call and return pattern from the hierarchical software 
 * execution data.  
 * it contains 3 steps:
 * (1) Pre-processing the input log, and select which plugin pattern to discovery
 * (2) Pre-processing the plugin related sub-log, to detect hierarchies. 
 * (3) Discovery a hierarchical modular Petri net. 
 * @author cliu3
 *
 */

@Plugin(
		// plugin name
		name = "Discover Call-and-Return Pattern",
		
		//return labels
		returnLabels = {"Directly Followed Graph with Component and Hierarchy"}, 
		// return class, here the DFGExtended is an improved Dfg by extending nested and component information 
		returnTypes = {DFGExtendedHierarchies.class},
		
		userAccessible = true,
		help = "This plugin aims to discover the call-and-return pattern from the origianl software log", 
		
		//input labels, corresponding with the second parameter of main function
		parameterLabels = {"ProM Software Execution Log"} 
		)
		
public class CallReturnPatternDiscoveryPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Call-and-Return Pattern Discovery, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	
	
	public static DFGExtendedHierarchies discoverCallReturnPattern(UIPluginContext context, XLog inputlog) 
	{
		// the windows label, with plugin name. 
		context.getFutureResult(0).setLabel(
				"Discovered Call-and-Return Pattern of " + XConceptExtension.instance().extractName(inputlog));
		
		//(1) pre-processing the input log and select which plugin's call-return pattern to be discovered 
		
		//configuration setting initialization
		CallReturnPatternDiscoveryConfiguration configuration = new CallReturnPatternDiscoveryConfiguration("Alpha Miner");
		
		//config the plugin name selection dislog
		CallReturnPatternDiscoveryDialog dialog = new CallReturnPatternDiscoveryDialog(context, obtainPluginSet(inputlog), configuration);
		
		InteractionResult result = context.showWizard("Select Plugin Name", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		
		// using the selected plugin to filter sub-Log, it only contains events has same runtime component value with the selected one. 
		XLog subLog = obtainSubLog(inputlog, configuration);
		
		// detect nested method call
		NestedMethodCallDetection nestedDetection = new NestedMethodCallDetection(subLog);
		
		//obtain the main log
		XLog filteredMainLog= nestedDetection.getFilteredLog();
		
		//obtain the event to sublog mapping
		HashMap<XEventClass, XLog> xeventclass2Sublog = nestedDetection.getXeventClass2XLog();
		
		// create the DFGExtendedHierarchies
		DFGExtendedHierarchies dfgExtendedhie = new DFGExtendedHierarchies();
		
		//its dfg part. 
		DFGExtended dfge = new DFGExtended(1);
		dfge =DirectlyFollowedGraphwithComponent.log2dfge(filteredMainLog);
		dfgExtendedhie.setDfgExtended(dfge);
		
		//mine its mapping from XEventClass to DFGExtended part
		HashMap<XEventClass, DFGExtended> event2dfge = MineDirectlyFollowedGraphwithHierarchiesandComponentPlugin.xeventclass2dfg(xeventclass2Sublog);
		
		dfgExtendedhie.setXEventClass2DFG(event2dfge);
		return dfgExtendedhie;
	}
	
	
	// this function is used to obtain the plugin set. 
	public static Set<String> obtainPluginSet(XLog originallog)
	{
		Set<String> PluginSet = new HashSet<String>();
		
		for (int i = 0; i < originallog.size(); i++) 
		{			
			for (XEvent event : originallog.get(i))
			{
				//plugin, or component 
				PluginSet.add(event.getAttributes().get("Runtime_Component").toString());		
				//System.out.println(configuration.getRegionName());
			}
			
		}
		return PluginSet;
	}
	
	//this function is used to filter sub-log using the selected plug-in name
	public static XLog obtainSubLog (XLog originallog, CallReturnPatternDiscoveryConfiguration configuration)
	{
		XLog filteredLog;
			
		//filteredLog only contains the same value with the configuration.getpluginName
		XFactory factory = new XFactoryNaiveImpl();
		//XFactory factory = new XFactoryExternalStore.MapDBDiskSequentialAccessImpl();
		filteredLog = factory.createLog();
		
		// build basic information for the filtered event log
		filteredLog.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", "filtered log"));
		//create standard extension
		XExtension conceptExtension = XConceptExtension.instance();
		XExtension organizationalExtension = XOrganizationalExtension.instance();
		XExtension timeExtension = XTimeExtension.instance();
		XExtension lifecycleExtension=	XLifecycleExtension.instance();
		
		// create extensions
		filteredLog.getExtensions().add(conceptExtension);
		filteredLog.getExtensions().add(organizationalExtension);
		filteredLog.getExtensions().add(lifecycleExtension);
		filteredLog.getExtensions().add(timeExtension);
		
		// create trace level global attributes
		XAttribute xtrace = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
		filteredLog.getGlobalTraceAttributes().add(xtrace);

		// create event level global attributes
		XAttribute xeventname = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
		XAttribute xeventresource = new XAttributeLiteralImpl(XOrganizationalExtension.KEY_RESOURCE, "C.Liu"); 
		XAttribute xeventlifecycle = new XAttributeLiteralImpl(XLifecycleExtension.KEY_TRANSITION, "complete");
		XAttribute xeventClass = new XAttributeLiteralImpl("Class", "DEFAULT"); 
		XAttribute xeventPackage = new XAttributeLiteralImpl("Package", "DEFAULT"); 
		XAttribute xeventRuntimeComponent = new XAttributeLiteralImpl("Runtime_Component", "DEFAULT"); 
		XAttribute xeventBelongingComponent = new XAttributeLiteralImpl("Belonging_Component", "DEFAULT"); 
		XAttribute xeventInteractionType = new XAttributeLiteralImpl("Interaction_Type", "DEFAULT"); 
		XAttribute xeventTimeNano = new XAttributeLiteralImpl("Timestamp_Nano", "DEFAULT"); 

		filteredLog.getGlobalEventAttributes().add(xeventname);
		filteredLog.getGlobalEventAttributes().add(xeventresource);
		filteredLog.getGlobalEventAttributes().add(xeventlifecycle);
		filteredLog.getGlobalEventAttributes().add(xeventClass);
		filteredLog.getGlobalEventAttributes().add(xeventPackage);
		filteredLog.getGlobalEventAttributes().add(xeventRuntimeComponent);
		filteredLog.getGlobalEventAttributes().add(xeventBelongingComponent);
		filteredLog.getGlobalEventAttributes().add(xeventInteractionType);
		filteredLog.getGlobalEventAttributes().add(xeventTimeNano);
		
		// create classifiers based on global attribute		
		XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Acticity Name", XConceptExtension.KEY_NAME, "Class", "Package");
		XEventAttributeClassifier classifierComponent = new XEventAttributeClassifier("Component","Belonging_Component");
		filteredLog.getClassifiers().add(classifierActivity);
		filteredLog.getClassifiers().add(classifierComponent);

		
		// traverse the original log
		for(XTrace trace: originallog)
		{
			XTrace tempTrace = factory.createTrace();
			// set the trace name, parameters[key, value, extension]
			tempTrace.getAttributes().put(XConceptExtension.KEY_NAME,
					factory.createAttributeLiteral(XConceptExtension.KEY_NAME, 
							trace.getAttributes().get(XConceptExtension.KEY_NAME).toString(), conceptExtension));
			for (XEvent event: trace)
			{
				if(event.getAttributes().get("Runtime_Component").toString().equals(configuration.getPluginName()))
				{
					tempTrace.add(event);
				}
			}
			if (!tempTrace.isEmpty())
			{
				filteredLog.add(tempTrace);
			}
		}
		
		return filteredLog;
	}

}
