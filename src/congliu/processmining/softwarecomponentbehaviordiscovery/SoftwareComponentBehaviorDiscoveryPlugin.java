package congliu.processmining.softwarecomponentbehaviordiscovery;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

/**
 * this plug-in is used to discover a hierarchical petri net with nested transitions;
 * different from "SoftwareComponentLifecycleBehaviorDiscovery", we do not consider multi-instance.
 * @author cliu3
 *
 */

@Plugin(
		name = "Software Component Behavior Discovery",// plugin name
		
		returnLabels = {"Software Component Behavior"}, //reture labels
		returnTypes = {HierarchicalPetriNet.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software Event Log of one Component"},
		
		userAccessible = false,
		help = "This plugin aims to discover the behavioral model of one software component." 
		)

public class SoftwareComponentBehaviorDiscoveryPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Discovering software component behavioral model, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	public HierarchicalPetriNet behaviorDiscovery(UIPluginContext context, XLog inputlog) throws ConnectionCannotBeObtained
	{
	
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel(
				"Software Component Lifecycle Behavior: " + XConceptExtension.instance().extractName(inputlog));
		
		//keep the input log unchanged
		XLog  originalLog = (XLog) inputlog.clone();
		
		
		//set the inductive miner parameters
		IMMiningDialog dialog = new IMMiningDialog(originalLog);
		InteractionResult result = context.showWizard("Configure Parameters for Inductive Miner (used for all sub-nets)", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		// the mining parameters are set here
		MiningParameters IMparameters = dialog.getMiningParameters(); //IMparameters.getClassifier()

		
		// hierarchical software event log, null means start from Top-level
		HSoftwareEventLog hseLog = ConstructHSoftwareEventLog.ConstructHSoftwareEventLogRecusively(null, null, originalLog, IMparameters.getClassifier());
		
			
		//software component lifecycle behavior, HPetriNetMI
		HierarchicalPetriNet hpn = MineHierarchicalPetriNet.mineHierarchicalPetriNet(context,hseLog, IMparameters);
		
		return hpn;	
	}
	
	
}
