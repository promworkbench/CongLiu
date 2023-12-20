package org.processmining.congliu.localizeddiscovery;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CancellationException;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMi;
import org.processmining.plugins.InductiveMiner.plugins.IMProcessTree;
import org.processmining.plugins.log.logfilters.AttributeFilterParameters;
import org.processmining.processtree.ProcessTree;
import org.processmining.processtree.impl.ProcessTreeImpl;

/**
 * This plugin aims to discover a merged process tree using localized event log, a concurrent operator
 * 
 * step 1: split the log into sublogs according to its component attribute
 * step 2: use inductive miner to discover sub-trees
 * step 3: merge the sub-trees using sequential operator
 * @author cliu3
 *
 */
public class MergeProcessTreesUsingConcurrentOperator {

	@Plugin(
			name = "Merge Process Trees Using Concurrent Operator",// plugin name
			
			returnLabels = {"Process Tree"}, //reture labels
			returnTypes = {ProcessTree.class},//reture class
			
			userAccessible = true,
			help = "This plugin aims to disover ProM behavior model using localized log.", 
			
			//input labels, corresponding with the second parameter of main function
			parameterLabels = {"Localized ProM Execution Log"} 
			)
			
		@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
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
 			//using the inductive miner, set the parameter
			MiningParameters parametersIM= new MiningParametersIMi();
			//mine a process tree
			ProcessTree ptree = IMProcessTree.mineProcessTree(sublog, parametersIM);
//				
//				// the petri net with markings has the following three components: pn.petrinet, pn.initialMarking, pn.finalMarking
//				PetrinetWithMarkings pnm = ProcessTree2Petrinet.convert(ptree);
//				
//				//Petrinet petrinet = IMPetriNet.minePetriNet(sublog, parameters);

			treelist.add(ptree);
		}
		
		
		//wf-net merge, using time information
		concurrentMerge cm =new concurrentMerge();
		mergedtree = cm.concurrentModelMerge(treelist); 

		
		return mergedtree;
	}
	
	

	
	
}

