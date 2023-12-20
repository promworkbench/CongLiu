package congliu.processmining.softwarecomponentbehaviordiscovery;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

/**
 * this plugin aims to discover the lifecycle behavior of one component.
 * 
 * Input: software event log of one component, extended with multiple attributes. 
 * Output: lifecycle behavior of this component
 * 
 * The main challenge is to filtering input (flat) software event log to a hierarchical one, 
 * and discovery a hierarchical Petri net with multi-instance blocks, HPNIs.
 * 
 * Step1: taking the software event log as input, we first filter it to a hierarchical one. 
 * step2: by taking the hierarchical log as input, we discover a petri net with multi-instances model for each layer, 
 * and finally construct a a hierarchical Petri net with multi-instance blocks, HPNIs.
 * Step3: visualize the HPNIs model.
 * @author cliu3
 *
 */

@Plugin(
		name = "Software Component Lifecycle Behavior Discovery",// plugin name
		
		returnLabels = {"Software Component Lifecycle Behavior"}, //reture labels
		returnTypes = {HierarchicalPetrinetMultiInstances.class},//reture class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software Event Log of one Component"},
		
		userAccessible = false,
		help = "This plugin aims to discover the lifecycle behavior of one software component." 
		)

public class SoftwareComponentLifecycleBehaviorDiscoveryplugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Discovering software component lifecycle behavior, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	
	public HierarchicalPetrinetMultiInstances lifeCycleBehaviorDiscovery(UIPluginContext context, XLog inputlog)
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
		
		//taking the software event log as input, we generate a hierarchical software event log using (1) the callee and caller relation (2) nesting level
		//first we need to make sure that the caller belongs to the current component. other wise, it is added to the top-level.
		
		//set the classifier
		
		//XEventClassifier currentClassifier = inputlog.getClassifiers().get(0);
		
		// hierarchical software event log, null means start from Top-level
		HSoftwareEventLog hseLog = ConstructHSoftwareEventLog.ConstructHSoftwareEventLogRecusively(null, null, originalLog, IMparameters.getClassifier());
		
		
//		//Output the log to check....
//		serializeHSELog(hseLog);
				
		//software component lifecycle behavior, HPetriNetMI
		HierarchicalPetrinetMultiInstances hpnmi = MineHierarchicalPetrinetwithMultiInstances.mineHierarchicalPetriNetWithMultiInstance(hseLog, IMparameters);
		
		return hpnmi;	
	}
	
	
	//
	
	
//	// just to test if the hierarchical log filtering works?
//	public static void serializeHSELog(HSoftwareEventLog hseLog)
//	{
//		
//		//serialization the current main log to disk
//		try {
//			FileOutputStream fosgz = new FileOutputStream("D:\\[6]\\making examples\\hierarchical log\\"+
//					hseLog.getMainLog().getAttributes().get(XConceptExtension.KEY_NAME)+".xes"); 
//			//FileOutputStream fos = new FileOutputStream("D:\\KiekerData\\CaseStudy001\\EnrichedMethodLevelLog.xes.gz"); 
//			
//			new XesXmlSerializer().serialize(hseLog.getMainLog(), fosgz); 
//            // serialize to xes.gz
//			//new XesXmlGZIPSerializer().serialize(log, fosgz);
//
//			fosgz.close();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
//		
//		// serialize its sub-HSELog
//		if(hseLog.getSubLogMapping().keySet().size()>0)
//		{
//			for(XEventClass key:hseLog.getSubLogMapping().keySet())
//			{
//				serializeHSELog(hseLog.getSubLogMapping().get(key));
//			}
//			
//		}
//		
//	}
	
}
