package MultiInstanceProcessDiscovery;

import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
/**
 * this plugin aims to discover a hierarchical process model from a lifecycle event log;
 * Input 1: an event log
 * Input 2: a nesting threshold
 * 
 * Output: hierarchical Petri nets
 */
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.widgets.helper.ProMUIHelper;
import org.processmining.framework.util.ui.widgets.helper.UserCancelledException;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

@Plugin(
		name = "Hierarchical Business Process Model Discovery",// plugin name
		
		returnLabels = {"Hierarchical Business Process Model"}, //return labels
		returnTypes = {HierarchicalPetriNet.class},//return class, a cross-organization process model
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Lifecycle Event Log"},
		
		userAccessible = true,
		help = "This plugin aims to discover hierarchical process models from lifecycle event logs." 
		)
public class MultiInstanceBusinessProcessDiscoveryPlugin {
	@UITopiaVariant(
	        affiliation = "SDUT", 
	        author = "Cong liu", 
	        email = "liucongchina@sdust.edu.cn"
	        )
	@PluginVariant(
			variantLabel = "Multi-instance Business Process Model Discovery, default",
			// the number of required parameters, 0 means the first input parameter 
			requiredParameterLabels = {0})
	public HierarchicalPetriNet MultiInstanceBehaviorDiscovery(UIPluginContext context, XLog lifecycleLog) throws ConnectionCannotBeObtained, UserCancelledException 
	{
		// the input nesting threshold
		double nestingRationThreshold = ProMUIHelper.queryForDouble(context, "Select Nesting Threshold", 0, 1,0.85);	
		//double nestingRationThreshold =0.85;
		
		//set the inductive miner parameters, the original log is used to set the classifier
		IMMiningDialog dialog = new IMMiningDialog(lifecycleLog);
		InteractionResult result = context.showWizard("Configure Parameters for Inductive Miner (used for all sub-processes)", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		// the mining parameters are set here 
		MiningParameters IMparameters = dialog.getMiningParameters(); //IMparameters.getClassifier()
		//create factory to create Xlog, Xtrace and Xevent.
		XFactory factory = new XFactoryNaiveImpl();
		XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(lifecycleLog, IMparameters.getClassifier());
		
		//get the activity set of an event log;
		HashSet<String> activitySet =ActivityRelationDetection.getActivitySet(lifecycleLog);
		System.out.println("Activity Set: "+activitySet);
		
		//get all possible activity pairs
		HashSet<ActivityPair> activityPariSet = ActivityRelationDetection.getAllActivityPairs(activitySet);
		System.out.println("Activity Pair Set: "+activityPariSet);
		
		//get the frequency of directly follow relations 
		HashMap<ActivityPair, Integer> directlyFollowFrequency =ActivityRelationDetection.getFrequencyofDirectlyFollowRelation(lifecycleLog, activityPariSet);
		System.out.println("Directly Follow Frequency: "+directlyFollowFrequency);
		
		//get the frequency of overlap relations
		HashMap<ActivityPair, Integer> overlapFrequency =ActivityRelationDetection.getFrequencyofOverlapRelation(lifecycleLog, activityPariSet);
		System.out.println("Overlap Frequency: "+overlapFrequency);

		//get the frequency of contain relations
		HashMap<ActivityPair, Integer> containFrequency =ActivityRelationDetection.getFrequencyofContainRelation(lifecycleLog, activityPariSet);
		System.out.println("Contain Frequency: "+containFrequency);

		//get the set of nesting activity pairs that meet the input nesting ratio
		HashSet<ActivityPair> nestingActivityPariSet = new HashSet<>();
		for(ActivityPair ap : containFrequency.keySet())
		{
			//computing the nesting ratio
			double apNestingRatio =ActivityRelationDetection.nestingFrequencyRatio(ap, containFrequency, directlyFollowFrequency, overlapFrequency);
			System.out.println(ap+" Nesting Ratio: "+apNestingRatio);
			
			if(apNestingRatio>=nestingRationThreshold)
			{
				nestingActivityPariSet.add(ap);
			}
		}
		System.out.println("Nesting Activity Pairs Meeting Input Nesting Ratio: "+nestingActivityPariSet); 

		//get the nesting activity pairs and remove transitive reduction. 
		ActivityNestingGraph ang =TransitiveNestingRelationReduction.ActivityPrecedencyGraphConstruction(nestingActivityPariSet);
		
//		//get all nested activities
//		HashSet<String> allNestedActivities = new HashSet<>();
//		for(String n: TransitiveNestingRelationReduction.getAllNestedActivities(ang))
//		{
//			allNestedActivities.add(n);
//		}
//		System.out.println("Nested Activities: "+allNestedActivities); 

//		//get all root nesting activities
//		HashSet<String> rootActivities  =TransitiveNestingRelationReduction.getAllRootActivities(ang);
//		System.out.println("Root Activities: "+rootActivities); 

		//get the nested nodes of an nesting activity
		for(String node: ang.getAllVertexes())
		{
			System.out.println("Nested Activities of "+node+" are: "+TransitiveNestingRelationReduction.getNestedActivitiesOfAnActivity(ang, node)); 
		}
				
		//Hierarchical Log construction
		HEventLog hlog =HEventLogConstruction.constructHierarchicalLog(ang, activitySet, lifecycleLog, factory, Xloginfo);

		//Hierarchical pn discovery 
		HierarchicalPetriNet hpn  = DiscoverHierarchicalPetriNet.mineHierarchicalPetriNet(context, hlog, IMparameters);
		return hpn;
	}
	
}

