package congliu.processmining.dynamicclassobjectprocessgraph;

import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processmining.softwarecomponentbehaviordiscovery.HSoftwareEventLog;
import congliu.processmining.softwarecomponentbehaviordiscovery.HierarchicalPetriNet;
import congliu.processmining.softwarecomponentbehaviordiscovery.MineHierarchicalPetriNet;

/**
 * this plug-in takes a software execution log as input, and returns a set of 
 * class object process graphs. 
 * @author cliu3
 *
 */

@Plugin(
		name = "Dynamic Class Object Process Graph Discovery",// plugin name
		
		returnLabels = {"Software Component Behavior"}, //return labels
		returnTypes = {ClassObjectProcessGraphs.class},//return class, a set of component to hpns 
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software Event Log"},
		
		userAccessible = true,
		help = "This plugin aims to discover the dynamic class object process graphs of a software." 
		)
public class DynamicClassObjectProcessGraphDiscoveryPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Dynamic Class Object Process Graphs, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	
	public ClassObjectProcessGraphs componentBehaviorDiscovery(UIPluginContext context, XLog originalLog) throws ConnectionCannotBeObtained 
	{
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel("Dynamic Class Object Process Graphs: " + XConceptExtension.instance().extractName(originalLog));
		
		//set the inductive miner parameters, the original log is used to set the classifier
		IMMiningDialog dialog = new IMMiningDialog(originalLog);
		InteractionResult result = context.showWizard("Configure Parameters for Inductive Miner (used for all component models)", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		// the mining parameters are set here 
		MiningParameters IMparameters = dialog.getMiningParameters(); //IMparameters.getClassifier()
	
		//create factory to create Xlog, Xtrace and Xevent.
		XFactory factory = new XFactoryNaiveImpl();
		
		// obtain the class set appeared in the original event log
		HashSet<String> classSet = new HashSet<>();
		for(XTrace t: originalLog)
		{
			for (XEvent e: t)
			{
				classSet.add(XSoftwareExtension.instance().extractPackage(e)+"."+XSoftwareExtension.instance().extractClass(e));
			}
		}
		
		//for each class, we construct its event log. 
		HashMap<String, XLog> class2Log = new HashMap<String, XLog>(); 
		//The main work is to identify class objects, and construct software event log for each class.
		for (String Class: classSet)
		{
			//obtain the software event log for each component.
			// input:(1) classes of this one specific component; and (2) the original software log
			XLog softwareComponentLog = ConstractEventLog4Class.generatingEventLog(Class,originalLog,factory);
			
			//add to the final software event log list
			class2Log.put(Class, softwareComponentLog);
		}	
		
		// discover the class object process graphs
		ClassObjectProcessGraphs classobjProcessGraphs = new ClassObjectProcessGraphs();
		//for each component log, we construct its hlog and discover a hpn.
		for(String Class: class2Log.keySet())
		{
			
			// hierarchical software event log, null means start from Top-level
			HSoftwareEventLog hseLog = ConstructHLog.ConstructHLogRecusively(factory, Class.split("\\.")[1], null, null, class2Log.get(Class), 
					IMparameters.getClassifier()); // here the input class name only contains the class (without the package part)
			
			//software component behavior, HPN
			HierarchicalPetriNet hpn = MineHierarchicalPetriNet.mineHierarchicalPetriNet(context, hseLog, IMparameters);
			
			classobjProcessGraphs.addClassObjectProcessGraph(Class, hpn);
			
		}
		return classobjProcessGraphs;
		
	}
}
