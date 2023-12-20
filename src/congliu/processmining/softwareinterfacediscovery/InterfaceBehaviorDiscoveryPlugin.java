package congliu.processmining.softwareinterfacediscovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;

import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.ComponentModels;
import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.ComponentModelsSet;
import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.Interface;
import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.Interface2HPN;
import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.SimilarityThresholdConfiguration;
import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.SimilarityThresholdDialog;
import congliu.processmining.softwarecomponentbehaviordiscovery.HSoftwareEventLog;
import congliu.processmining.softwarecomponentbehaviordiscovery.HierarchicalPetriNet;
import congliu.processmining.softwarecomponentbehaviordiscovery.MineHierarchicalPetriNet;
import observerpatterndiscovery.MethodClass;

/**
 * this plugin aims to discover the interface for different components. 
 * The main steps contains: (1) group methods to form interfaces according to the similarity threshold; 
 * 		 (2) identify the interface instance and refactoring the interface log; and  
 * 		 (3) each component has multiple interfaces (each refers to a hpn). 
 * 
 * Input 1: a software event log (original data, each trace refers to one software execution), obtained from SoftwareLogRefactoingPluginV1 
 * using Maiker's XPort Instrumentation as tool;
 * Input 2: configuration file indicating the mapping from component to classes, with suffix .conf.
 * 
 * Output: a set of component, each with a set of interface models (HPN models) and interface description. 
 * @author cliu3 2017-4-13
 *
 *New improvement: 2017-5-7
 *to get the invoked method set, not only use method name, class name, package name, we also use class object information. 
 *
 *New improvement: 2015-5-29
 *remove the class name and package name information when getting the invoked method set. 
 *congliu.processminig.softwarecomponentinteractionbehaviordiscovery.ConstructHLog.getMainOtherLevels(XLog mainLog, HashSet<XEvent> eventList, XLog originalLog, XFactory factory, String componentName)
 *This will avoid mistakes caused by class inheritance. 
 *
 */

@Plugin(
		name = "Software Interface Behavior Discovery (Similarity)",// plugin name
		
		returnLabels = {"Software Interface Behavior Models"}, //return labels
		returnTypes = {ComponentModelsSet.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software Event Log", "Component to Classes Mapping"},
		
		userAccessible = true,
		help = "This plugin aims to discover the interfaces as well as their behavior models by using the interaction information in the log." 
		)
public class InterfaceBehaviorDiscoveryPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Discovering interface behavior model, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0, 1}
			)
	
	public ComponentModelsSet interactionBehaviorDiscovery(UIPluginContext context, XLog originalLog, 
			ComponentConfig comconfig) throws ConnectionCannotBeObtained
	{
		int num=0;
		
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel("Software Interface Behavior Discovery");
		context.log("Interface Behavior Discovery Starts...", MessageLevel.NORMAL);		
		
		//the component model 
		ComponentModelsSet componentModelSet = new ComponentModelsSet();
				
		//set the inductive miner parameters, the original log is used to set the classifier
		IMMiningDialog dialog = new IMMiningDialog(originalLog);
		InteractionResult result = context.showWizard("Configure Parameters for Inductive Miner (used for all component models)", true, true, dialog);
		if (result != InteractionResult.FINISHED) {
			return null;
		}
		// the mining parameters are set here 
		MiningParameters IMparameters = dialog.getMiningParameters(); //IMparameters.getClassifier()
		
		//set the interface similarity threshold. 
		SimilarityThresholdConfiguration simiConfig = new SimilarityThresholdConfiguration(0);
		SimilarityThresholdDialog simiDialog = new SimilarityThresholdDialog(context, simiConfig);
		
		InteractionResult resultSimi = context.showWizard("Configure similarity value", true, true, simiDialog);
		if (resultSimi != InteractionResult.FINISHED) {
			return null;
		}
		
		double similarityThreshold = simiConfig.getThresholdValue()/10000;
		//System.out.println("selected similarity value is: " +simiConfig.getThresholdValue()/10000);
		
		context.log("Interface Similarity Threshold is: "+similarityThreshold, MessageLevel.NORMAL);	
		
//		//create XLogInfo to get the xeventclass for each event. 
//		XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(originalLog, IMparameters.getClassifier());
		
		//create factory to create Xlog, Xtrace and Xevent.
		XFactory factory = new XFactoryNaiveImpl();
		
		//for each component, we construct its top-level method set
		for(String com: comconfig.getAllComponents())
		{
			context.log("The current component is: "+com, MessageLevel.NORMAL);		
			System.out.println("The current component is: "+com);
			
			ComponentModels componenModels = new ComponentModels();
			componenModels.setComponent(com);// set component name
					
			HashSet<Interface2HPN> i2hpnSet = new HashSet<Interface2HPN>();//for each component, it has a set of <interfaces->interface model>
						
			//obtain the software event log for each component.
			// input:(1) class set of the current component; and (2) the original software log
			XLog comLog = BasicOperations.generatingSoftwareEventLog(com, comconfig.getClasses(com), originalLog, factory);
			
			//construct the caller method set for each component 
			HashSet<MethodClass> callerMethodSet = BasicOperations.constructCallerMethodSet(comconfig.getClasses(com), comLog); 
			
				
			//construct candidate interfaces, each interface contains a set of top-level methods that invoked by one single caller method. 
			//Note that the same interface (a set of methods) can be called by multiple caller methods, after merging based on similarity. 
			HashMap<HashSet<MethodClass>, HashSet<MethodClass>> CandidateInterface2callerMethod = new HashMap<>();
			
			ArrayList<HashSet<MethodClass>> candidateInterfaceList = new ArrayList<>();// the original interface list without merging.
			for(MethodClass callerM: callerMethodSet)
			{
				//each candidate is represented by its top-level method calls
				HashSet<MethodClass> candidateInterface =BasicOperations.constructCandidateInterface(comLog, callerM);
				candidateInterfaceList.add(candidateInterface);
				if(CandidateInterface2callerMethod.keySet().contains(candidateInterface))
				{
					CandidateInterface2callerMethod.get(candidateInterface).add(callerM);
				}
				else{
					HashSet<MethodClass> callerMethods=new HashSet<>();
					callerMethods.add(callerM);
					CandidateInterface2callerMethod.put(candidateInterface, callerMethods);		
				}		
				//System.out.println(callerM+"----->"+candidateInterface);
			}
			
			//merging similar candidate interfaces according to the threshold
			//ArrayList<HashSet<MethodClass>> interfaceList =BasicOperations.recursiveComputing(CandidateInterface2callerMethod, new ArrayList<HashSet<MethodClass>>(), candidateInterfaceList, similarityThreshold); 
			HashMap<HashSet<MethodClass>, HashSet<MethodClass>> interface2CallerSet=BasicOperations.recursiveComputing(CandidateInterface2callerMethod, new ArrayList<HashSet<MethodClass>>(), candidateInterfaceList, similarityThreshold); 
			
			System.out.println("----->all interfaces"+interface2CallerSet.keySet());
			
			//for each interface, we construct its event log (flat)
			for(HashSet<MethodClass> interfaceM: interface2CallerSet.keySet())
			{
				System.out.println("The current interface is: "+interfaceM);

				//get the event log of each interface, when construct log we need also take the caller methods as input.
				//XLog interfaceLog = BasicOperations.constructInterfaceLog(comLog, interfaceM, interface2CallerSet.get(interfaceM), factory);
				XLog interfaceLog = BasicOperations.constructNNestingInterfaceLog(comLog, 6, interfaceM, interface2CallerSet.get(interfaceM), factory);
				
				//refactoring the event log by identifying interface instances
				XLog interfaceInstanceLog = BasicOperations.constructInterfaceInstanceLog(interfaceLog,factory,comconfig.getClasses(com));
						
				// hierarchical software event log, null means start from Top-level
				HSoftwareEventLog hseLog = ConstructHierarchicalLog.ConstructHierarchicalLogRecusively(factory, 
						comconfig.getClasses(com), null, null, interfaceInstanceLog, IMparameters.getClassifier(), com);
			
				//hierarchical mining
				HierarchicalPetriNet hpn = MineHierarchicalPetriNet.mineHierarchicalPetriNet(context, hseLog, IMparameters);
				
				/*
				 * this part need to be improved...
				 */
				Interface i= new Interface("", com);
				i.setId(com+(num++));
				Interface2HPN i2hpn = new Interface2HPN(i, hpn);
				i2hpnSet.add(i2hpn);
				
				
			}
			componenModels.setI2hpn(i2hpnSet);// the interface part of the component
			componentModelSet.addComponent2HPNSet(componenModels);
		}
		
		return componentModelSet;
	}// plug-in method
}
