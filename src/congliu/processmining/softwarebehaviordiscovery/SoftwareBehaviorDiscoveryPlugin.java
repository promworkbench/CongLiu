//package congliu.processmining.softwarebehaviordiscovery;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//
//import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
//import org.deckfour.xes.model.XLog;
//import org.processmining.contexts.uitopia.UIPluginContext;
//import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
//import org.processmining.framework.connections.ConnectionCannotBeObtained;
//import org.processmining.framework.plugin.annotations.Plugin;
//import org.processmining.framework.plugin.annotations.PluginVariant;
//import org.processmining.log.models.EventLogArray;
//import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
//import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
//
//import congliu.processmining.softwarecomponentbehaviordiscovery.HSoftwareEventLog;
//import congliu.processmining.softwarecomponentbehaviordiscovery.HierarchicalPetriNet;
//import congliu.processmining.softwarecomponentbehaviordiscovery.MineHierarchicalPetriNet;
//import congliu.processmining.softwareprocessmining.Component2Classes;
//import congliu.processmining.softwareprocessmining.FileChooserConfiguration;
//import congliu.processmining.softwareprocessmining.FileChooserPanel;
//import congliu.processmining.softwareprocessmining.ProgramExecutionData2SoftwareEventLogplugin;
//
///**
// * this plug-in is used to discover a software behavior model, 
// * (1) internal component behavior model
// * (2) inter-component interactions
// * @author cliu3
// */
//
//@Plugin(
//		name = "Software Behavior Discovery",// plugin name
//		
//		returnLabels = {"Software Behavior"}, //reture labels
//		returnTypes = {Component2HPNArraySet.class},//return class
//		
//		//input parameter labels, corresponding with the second parameter of main function
//		parameterLabels = {"Software execution data and Component"},
//		
//		userAccessible = false,
//		help = "This plugin aims to discover the behavioral model of a software." 
//		)
//
//public class SoftwareBehaviorDiscoveryPlugin {
//	@UITopiaVariant(
//	        affiliation = "TU/e", 
//	        author = "Cong liu", 
//	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
//	        )
//	@PluginVariant(
//			variantLabel = "Discovering software behavioral model, default",
//			// the number of required parameters, {0} means one input parameter
//			requiredParameterLabels = {}
//			)
//	public Component2HPNArraySet behaviorDiscovery(UIPluginContext context) throws ConnectionCannotBeObtained
//	{
//		//get the log name from the original log. it is shown as the title of returned results. 
//		context.getFutureResult(0).setLabel("Software Behavior Discovery");
//		
//		//Select the inputs(1) software execution data (CSV), (2) class2component mapping;
//		FileChooserConfiguration softwareEventDataFile = new FileChooserConfiguration();
//		FileChooserConfiguration class2componentMappingFile = new FileChooserConfiguration();
//		
//		new FileChooserPanel(softwareEventDataFile);
//		new FileChooserPanel(class2componentMappingFile);
//		
//		// obtain the mapping from component to classes. 
//		ArrayList<Component2Classes> c2c =ProgramExecutionData2SoftwareEventLogplugin.component2Class(class2componentMappingFile.getFilename());
//		
//		//each component corresponds to an event log array.
//		HashMap<String, EventLogArray> component2EventLogArray = new HashMap<String, EventLogArray>();
//		
//		// for each component, we identifying its Event log array.
//		for (Component2Classes com2Class: c2c)
//		{
//			EventLogArray softwareEventLogArray = GenerateEventLogArray4Component.generatingSoftwareEventLogArray(com2Class,c2c,softwareEventDataFile.getFilename());
//			component2EventLogArray.put(com2Class.getComponent(), softwareEventLogArray);
//		}
//		
//		XLog eventlog4IM = component2EventLogArray.get(component2EventLogArray.keySet().toArray()[0]).getLog(0);
//		//set the inductive miner parameters, the original log is used to set the classifier
//		IMMiningDialog dialog = new IMMiningDialog(eventlog4IM);
//		InteractionResult result = context.showWizard("Configure Parameters for Inductive Miner (used for all sub-nets)", true, true, dialog);
//		if (result != InteractionResult.FINISHED) {
//			return null;
//		}
//		
//		// the mining parameters are set here
//		MiningParameters IMparameters = dialog.getMiningParameters(); //IMparameters.getClassifier()
//		
//		// a software has multiple components, each has a component to hpns 
//		Component2HPNArraySet component2HPNArrayset = new Component2HPNArraySet();
//		
//		// for each component, we construct its HPNArray by taking its event log array as input
//		for(String componentName: component2EventLogArray.keySet())
//		{
//			//each component correspond an event log array.
//			Component2HPNArray component2hpns = new Component2HPNArray();
//			component2hpns.setComponentName(componentName);// component name part
//			HPNArray hpnArray = new HPNArray();// the hpns parts
//			
//			
//			// get the event log of this component
//			for (int i=0; i< component2EventLogArray.get(componentName).getSize();i++)
//			{
//				XLog flateventLog =component2EventLogArray.get(componentName).getLog(i);
//				
////				//serialization the current XESlog to disk
////				try {
////					FileOutputStream fosgz = new FileOutputStream("D:\\[7]\\software event logs\\"+
////							flateventLog.getAttributes().get(XConceptExtension.KEY_NAME)+".xes"); 
////					//FileOutputStream fos = new FileOutputStream("D:\\KiekerData\\CaseStudy001\\EnrichedMethodLevelLog.xes.gz"); 
////					
////					new XesXmlSerializer().serialize(flateventLog, fosgz); 
////		            // serialize to xes.gz
////					//new XesXmlGZIPSerializer().serialize(log, fosgz);
////		
////					fosgz.close();
////					
////				} catch (IOException e) {
////					// TODO Auto-generated catch block
////					e.printStackTrace();
////				} 
//				
//
//				/**
//				 * set the nesting level to create hierarchical log for certain levels.
//				 */
//				
//				// hierarchical software event log, null means start from Top-level
//				HSoftwareEventLog hseLog = ConstructHSoftwareEventLogNew.ConstructHSoftwareEventLogRecusively(null, null, flateventLog, IMparameters.getClassifier(), componentName);
//				
//				//software component behavior, HPN
//				HierarchicalPetriNet hpn = MineHierarchicalPetriNet.mineHierarchicalPetriNet(context,hseLog, IMparameters);
//				
//				/**
//				 * set the privided and required transitions (eventclasses) of the hpn
//				 * we directly detect it from the flat event log.
//				 */
//				hpnArray.addHPN(hpn);
//				component2hpns.setPEventClass(DetectProvidedRequiredEventClass.providedEventclass(flateventLog, IMparameters.getClassifier())); //append
//				component2hpns.setREventClass(DetectProvidedRequiredEventClass.requiredEventclass(flateventLog, IMparameters.getClassifier()));
//			}			
//			component2hpns.setHpnArray(hpnArray);// set the hpns
//			
//			
//			component2HPNArrayset.putC2HPNs(component2hpns);
//		}
//		
//		return component2HPNArrayset;	
//	}
//	
//	
//}
