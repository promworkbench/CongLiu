package org.processmining.congliu.localizeddiscovery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMi;
import org.processmining.plugins.InductiveMiner.plugins.IMProcessTree;
import org.processmining.plugins.log.logfilters.AttributeFilterParameters;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.impl.ProcessTreeImpl;

/**
 * This plugin aims to discover a merged process tree using localized event log
 * 
 * step 1: split the log into sublogs according to its component attribute
 * step 2: use inductive miner to discover sub-trees
 * step 3: merge the sub-trees using sequential operator
 * @author cliu3
 *
 */

@Plugin(
		name = "Localized Process Discovery (Non-overlapping Region Case)",// plugin name
		
		returnLabels = {"ProM Behavior Model (Process Tree)"}, //reture labels
		returnTypes = {ProcessTree.class},//reture class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Localized ProM Execution Log"},
		
		userAccessible = true,
		help = "This plugin aims to disover ProM behavior model using localized log." 
		)

public class LocalizedDiscovery {
		
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Localized Process Discovery (Non-overlapping Region Case), default",
			requiredParameterLabels = { 0 }
			)
	
	// the input of this plugin is an Xlog, and returns a filtered XLog
    public static ProcessTree localizedDiscovery(UIPluginContext context, XLog inputlog) throws CancellationException {
		//keep the input log unchanged
		XLog  originallog = (XLog) inputlog.clone();
		
		
		//define the final merged process tree
		ProcessTree mergedtree = new ProcessTreeImpl("Merged Tree");
		
		
		//adding a dialog to select which attribute to be used for splitting
		
		AttributeFilterParameters parameters = new AttributeFilterParameters(context, originallog);
		
		LocalizedDiscoveryConfiguration configuration = new LocalizedDiscoveryConfiguration(parameters
				.getFilter().keySet().iterator().next());
		
		// final argument.
		LocalizedDiscoveryDialog dialog = new LocalizedDiscoveryDialog(context, originallog, configuration);
		
		InteractionResult result = context.showWizard("Configure Region", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		
		// take the original event log as input and generate sublogs according to localization information, time....
		//log name, time list
		LocalizationSplitting ls = new LocalizationSplitting();
		List<XLog> sublogs = ls.split(originallog, configuration);	
		
		//for each sublog, we discover its process model, and store them in petrilist, 
		
		List<ProcessTree> treelist = new ArrayList<ProcessTree>();

		for (XLog sublog: sublogs)
		{
 			//use existing mining algorithms
//			Petrinet net = context.tryToFindOrConstructFirstNamedObject(Petrinet.class, "Mine Petri net with Inductive Miner", null, null, sublog);
//			petrilist.add(net);
			
			
			//using the inductive miner, set the parameter
			MiningParameters IMparameters= new MiningParametersIMi();
			//mine a process tree
			ProcessTree ptree = IMProcessTree.mineProcessTree(sublog, IMparameters);
//			
//			// the petri net with markings has the following three components: pn.petrinet, pn.initialMarking, pn.finalMarking
//			PetrinetWithMarkings pnm = ProcessTree2Petrinet.convert(ptree);
//			
//			//Petrinet petrinet = IMPetriNet.minePetriNet(sublog, parameters);

			treelist.add(ptree);
		}
		
		
		//wf-net merge, using time information
		ModelMerge mm =new ModelMerge();
		mergedtree = mm.modelMerge(treelist);  

		
		return mergedtree;
	}
}
