package congliu.processmining.softwarecomponentbehaviorNew;

import java.util.ArrayList;
import java.util.HashMap;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.ConstructHLog;
import congliu.processmining.softwarecomponentbehaviordiscovery.HSoftwareEventLog;
import congliu.processmining.softwarecomponentbehaviordiscovery.HierarchicalPetriNet;
import congliu.processmining.softwarecomponentbehaviordiscovery.MineHierarchicalPetriNet;
import congliu.processmining.softwareprocessmining.Component2Classes;

/**
 * this plugin aims to discover the behavior model for each software component. 
 * each component model refers to a hpn. 
 * 
 * Input 1: a software event log (original data, each trace refers to one software execution), obtained from SoftwareLogRefactoingPlugin 
 * using Maiker's XPort Instrumentation as tool;
 * Input 2: configuration file indicating the mapping from component to classes. 
 * This step can be further extended using class clustering approaches in the further work.  
 * 
 * Output1 : a set of component models describing the lifecycle behavior of each component (HPN models).
 * @author cliu3
 *
 */

@Plugin(
		name = "Software Component Behavior Discovery",// plugin name
		
		returnLabels = {"Software Component Behavior"}, //return labels
		returnTypes = {ComponentModelSet.class},//return class, a set of component to hpns 
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software Event Log", "Component to Class Mapping"},
		
		userAccessible = true,
		help = "This plugin aims to discover the component behavior models of software." 
		)
public class SoftwareComponentBehaviorDiscoveryPlugin {
	
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Software Component Behavior Discovery for each Component, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = { 0,1 })
	
	public ComponentModelSet componentBehaviorDiscovery(UIPluginContext context, XLog originalLog, 
			congliu.processmining.objectusage.Component2Classes com2c) throws ConnectionCannotBeObtained 
	{
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel("Software Component Lifecycle Behavior: " + XConceptExtension.instance().extractName(originalLog));
		
//		//Select the input2: class2component mapping;
//		FileChooserConfiguration class2componentMappingFile = new FileChooserConfiguration();
//		new FileChooserPanel(class2componentMappingFile);
		
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
		
		// obtain the mapping from component to classes. 
		ArrayList<Component2Classes> c2c=Adapter(com2c);
//		ArrayList<Component2Classes> c2c =ProgramExecutionData2SoftwareEventLogplugin.component2Class(class2componentMappingFile.getFilename());
		
		// the final output is a set of event logs, each describing a software component.
		HashMap<String, XLog> component2Log = new HashMap<String, XLog>(); 
		
		//The main work is to identify component instances, and construct software event log for each component.
		for (Component2Classes com2Class: c2c)
		{
			//obtain the software event log for each component.
			// input:(1) classes of this one specific component; and (2) the original software log
			XLog softwareComponentLog = ConstructSoftwareComponentLog.generatingSoftwareEventLog(com2Class,originalLog,factory);
			
			//add to the final software event log list
			component2Log.put(com2Class.getComponent(), softwareComponentLog);
		}	
		
		ComponentModelSet componenModels = new ComponentModelSet();
		
		//for each component log, we construct its hlog and discover a hpn.
		for(String com: component2Log.keySet())
		{
			
			// hierarchical software event log, null means start from Top-level
			HSoftwareEventLog hseLog = ConstructHLog.ConstructHLogRecusively(factory, c2c, null, null, component2Log.get(com), 
					IMparameters.getClassifier(), com);
			
			//software component behavior, HPN
			HierarchicalPetriNet hpn = MineHierarchicalPetriNet.mineHierarchicalPetriNet(context, hseLog, IMparameters);
			
			componenModels.addComponentModel(com, hpn);
			
		}
		return componenModels;
	}

	public static ArrayList<Component2Classes> Adapter(congliu.processmining.objectusage.Component2Classes com2c)
	{
		ArrayList<Component2Classes> c2c= new ArrayList<Component2Classes>();
		
		for(String com: com2c.getAllComponents())
		{
			Component2Classes c= new Component2Classes();
			c.setComponent(com);
			ArrayList<String> classes = new ArrayList<String>();
			for(String cl: com2c.getClasses(com))
			{
				classes.add(cl);
			}
			c.setClasses(classes);
			c2c.add(c);
		}
		
		return c2c;
	}
}
