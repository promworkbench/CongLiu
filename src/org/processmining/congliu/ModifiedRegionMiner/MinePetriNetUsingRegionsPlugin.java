package org.processmining.congliu.ModifiedRegionMiner;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.acceptingpetrinet.connections.MergeAcceptingPetriNetArrayIntoAcceptingPetriNetConnection;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNet;
import org.processmining.acceptingpetrinet.models.AcceptingPetriNetArray;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetArrayFactory;
import org.processmining.acceptingpetrinet.models.impl.AcceptingPetriNetFactory;
import org.processmining.acceptingpetrinetminer.connections.DiscoverAcceptingPetriNetArrayFromEventLogArrayConnection;
import org.processmining.acceptingpetrinetminer.dialogs.DiscoverAcceptingPetriNetArrayFromEventLogArrayDialog;
import org.processmining.acceptingpetrinetminer.parameters.DiscoverAcceptingPetriNetArrayFromEventLogArrayParameters;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.log.models.EventLogArray;
import org.processmining.log.models.impl.EventLogArrayImpl;
import org.processmining.plugins.log.logfilters.AttributeFilterParameters;

@Plugin(name = "Mine Petri net using regions (Modified Version For Software)", parameterLabels = { "Log" }, returnLabels = { "Accepting Petri net",
		"AcceptingPetriNetArray", "Event Log Array" }, returnTypes = { AcceptingPetriNet.class,
		AcceptingPetriNetArray.class, EventLogArray.class }, userAccessible = true, help = "Discovers Petri net from software log with regions")
public class MinePetriNetUsingRegionsPlugin {

	
	@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "C.liu.3@tue.nl")
	@PluginVariant(variantLabel = "Mine Petri net using regions (Modified Version For Software)", requiredParameterLabels = { 0 })
	
	public Object[] mineUnionNetPlugin(UIPluginContext context, XLog log) {

		AttributeFilterParameters parameters = new AttributeFilterParameters(context, log);
		MinePetriNetUsingRegionsConfiguration configuration = new MinePetriNetUsingRegionsConfiguration(parameters
				.getFilter().keySet().iterator().next());
		MinePetriNetUsingRegionsDialog dialog = new MinePetriNetUsingRegionsDialog(context, log, configuration);
		InteractionResult result = context.showWizard("Configure mining (region)", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		return mineUnionNet(context, log, configuration);
	}

	/**
	 * Mine a union net from an event log with regions
	 * 
	 * @param context
	 * @param log
	 * @param configuration
	 * @return
	 */
	private Object[] mineUnionNet(UIPluginContext context, XLog log, MinePetriNetUsingRegionsConfiguration configuration) {

		// If there are no shared events, log should be enriched 
//		if (configuration.isToEnrich()) {
//			System.out.println("enrich!!!");
//			LogEnricher enricher = new LogEnricher(log, configuration.getRegionName());
//			log = enricher.enrich();
//		}

		// Filter the log using regions
		EventLogArray eventLogArray = filterLogUsingRegions(context, log, configuration);
		
		for (int i = 0;i <eventLogArray.getSize(); i++)
		{
			System.out.println(eventLogArray.getLog(i).getAttributes().get("concept:name"));
		}
		
		
		//cong Liu, modify the log filtering part

		// Construct array of nets
		AcceptingPetriNetArray netsArray = constructArrayofNets(context, eventLogArray);

		// Merge nets 
		AcceptingPetriNet mergedNet = mergePetriNets(context, netsArray);

		// Refine merged net
		refineAcceptingNet(context, mergedNet, eventLogArray);

		// Save the name of the region in log attributes
		log.getAttributes().put("region", new XAttributeLiteralImpl("region", configuration.getRegionName()));
		
		return new Object[] { mergedNet, netsArray, eventLogArray };
	}

	/**
	 * Refine merged net
	 * 
	 * @param context
	 * @param mergedNet
	 * @param eventLogArray
	 */
	private void refineAcceptingNet(PluginContext context, AcceptingPetriNet mergedNet, EventLogArray eventLogArray) {

		AcceptingPetriNetUtils.reduce(context, mergedNet);

		AcceptingPetriNetUtils.removeEqualSourceAndSinkNodes(mergedNet);

		AcceptingPetriNetUtils.colorMergedNet(mergedNet, eventLogArray);
	}

	/**
	 * Merge accepting Petri nets
	 * 
	 * @param context
	 * @param netsArray
	 * @return
	 */
	private AcceptingPetriNet mergePetriNets(PluginContext context, AcceptingPetriNetArray netsArray) {

		AcceptingPetriNet mergedNet = AcceptingPetriNetFactory.createAcceptingPetriNet();
		try {
			mergedNet = context.tryToFindOrConstructFirstObject(AcceptingPetriNet.class,
					MergeAcceptingPetriNetArrayIntoAcceptingPetriNetConnection.class,
					MergeAcceptingPetriNetArrayIntoAcceptingPetriNetConnection.NET, netsArray);
		} catch (ConnectionCannotBeObtained e) {
			context.log("Can't obtain connection for " + netsArray);
			e.printStackTrace();
		}
		return mergedNet;
	}

	/**
	 * Construct array of nets from array of logs
	 * 
	 * @param context
	 * @param eventArray
	 * @return
	 */
	private AcceptingPetriNetArray constructArrayofNets(UIPluginContext context, EventLogArray eventArray) {

		AcceptingPetriNetArray netsArray = AcceptingPetriNetArrayFactory.createAcceptingPetriNetArray();
		DiscoverAcceptingPetriNetArrayFromEventLogArrayParameters parameters = new DiscoverAcceptingPetriNetArrayFromEventLogArrayParameters(
				eventArray);
		DiscoverAcceptingPetriNetArrayFromEventLogArrayDialog dialog = new DiscoverAcceptingPetriNetArrayFromEventLogArrayDialog(
				eventArray, parameters);
		InteractionResult result = context.showWizard("Configure discovery (classifier, miner)", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		try {
			netsArray = context.tryToFindOrConstructFirstObject(AcceptingPetriNetArray.class,
					DiscoverAcceptingPetriNetArrayFromEventLogArrayConnection.class,
					DiscoverAcceptingPetriNetArrayFromEventLogArrayConnection.NETS, eventArray, parameters);
		} catch (ConnectionCannotBeObtained e) {
			context.log("Can't obtain connection for " + eventArray);
			e.printStackTrace();
		}

		return netsArray;
	}

	/**
	 * Filter log using regions
	 * 
	 * @param context
	 * @param log
	 * @param configuration
	 * @return
	 */
	private EventLogArray filterLogUsingRegions(PluginContext context, XLog log,
			MinePetriNetUsingRegionsConfiguration configuration) {
		EventLogArray eventArray = new EventLogArrayImpl();

		AttributeFilterParameters parameters = new AttributeFilterParameters(context, log);
		for (String region : parameters.getFilter().get(configuration.getRegionName())) {
			//if (!region.contains(" ")) {
			System.out.println(region);
				XLog filteredLog = modifiedfilter(log, region, configuration.getRegionName());
				
				//modify here cong liu
				filteredLog.getAttributes().put("concept:name", new XAttributeLiteralImpl("concept:name", region));
				eventArray.addLog(filteredLog);
			//}
		}
		return eventArray;
	}

	/**
	 * Retrieve sublog for the region
	 * 
	 * @param log
	 * @param region
	 * @return
	 */
//	private XLog filter(XLog log, String region, String regionName) {
//		XFactory factory = XFactoryRegistry.instance().currentDefault();
////		XLog filteredLog = factory.createLog((XAttributeMap) log.getAttributes().clone());
//		XLog filteredLog = factory.createLog();
////		filteredLog.getClassifiers().addAll(log.getClassifiers());
////		filteredLog.getExtensions().addAll(log.getExtensions());
////		filteredLog.getGlobalTraceAttributes().addAll(log.getGlobalTraceAttributes());
////		filteredLog.getGlobalEventAttributes().addAll(log.getGlobalEventAttributes());
////		filteredLog.getAttributes().put("name", new XAttributeLiteralImpl("name", regionName));
//		for (XTrace trace : log) {
//			XTrace filteredTrace = factory.createTrace();
//			for (XEvent event : trace) {
//				boolean add = true;
//				List<String> values = new ArrayList<String>();
//				if (event.getAttributes().get(regionName) != null) {
//					values = java.util.Arrays.asList(event.getAttributes().get(regionName).toString().split("\\s+"));
//				}
//				if (!values.contains(region)) {
//					add = false;
//					continue;
//				}
//				if (add) {
//					filteredTrace.add(event);
//				}
//			}
//			if(filteredTrace.size() > 0) {
//				filteredLog.add(filteredTrace);
//			}
//		}
//		return filteredLog;
//	}
	
	
	private XLog modifiedfilter(XLog log, String region, String regionName) {
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		XLog filteredLog = factory.createLog();
		//System.out.println(region +":"+ regionName);
		for (XTrace trace : log) {
			XTrace filteredTrace = factory.createTrace();
			for (XEvent event : trace) {

				if (event.getAttributes().get(regionName).toString().equals(region))
				{
					filteredTrace.add(event);
				}
			}
			if(filteredTrace.size() > 0) {
				filteredLog.add(filteredTrace);
			}
		}
		return filteredLog;
	}
}
