package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.deckfour.uitopia.api.event.TaskListener.InteractionResult;
import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMflc;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
//import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
//import org.processmining.plugins.InductiveMiner.mining.MiningParametersIMflc;
//import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
//import org.processmining.plugins.InductiveMiner.plugins.dialogs.IMMiningDialog;
import org.processmining.plugins.petrinet.reduction.Murata;
import org.processmining.plugins.petrinet.reduction.MurataParameters;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processmining.softwarecomponentbehaviorNew.SoftwareComponentBehaviorDiscoveryPlugin;
import congliu.processmining.softwarecomponentbehaviordiscovery.HSoftwareEventLog;
import congliu.processmining.softwarecomponentbehaviordiscovery.HierarchicalPetriNet;
import congliu.processmining.softwarecomponentbehaviordiscovery.MineHierarchicalPetriNet;
import congliu.processmining.softwareprocessmining.Component2Classes;
import congliu.processmining.softwareprocessmining.OrderingEventsNano;

/**
 * this plugin aims to discover the interaction behavior among software components. 
 * The interaction contains: (1) method call to interfaces calling; 
 * 		and (2) the instance level cardinality (multiplicity) relation, i.e. 1...1, and 1...n.
 * 		and (3) inter-component behavior models, i.e., each component has multiple interfaces (each refers to a hpn). 
 * This plugin is different from the softwarebehaviordiscovery which uses the one to one mapping to represent interactions. 
 * 
 * Input 1: a software event log (original data, each trace refers to one software execution), obtained from SoftwareLogRefactoingPlugin 
 * using Maiker's XPort Instrumentation as tool;
 * Input 2: configuration file indicating the mapping from component to classes. 
 * This step can be further extended using class clustering approaches in the further work.  
 * 
 * Output1 : a set of components, each with a set of interfaces (HPN models)
 * Output2: interaction, from event(method) to interaction (a flat petri net) 
 * Output3: interface cardinality....derived from interface instances. 
 * @author cliu3
 *
 */

@Plugin(
		name = "Software Component Interaction Behavior Discovery",// plugin name
		
		returnLabels = {"Software Component Interaction Behavior"}, //return labels
		returnTypes = {SoftwareInteractionModel.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software Event Log", "Component to Classes Mapping"},
		
		userAccessible = true,
		help = "This plugin aims to discover the interaction behavior model of a piece of software." 
		)
public class SoftwareComponentInteractionBehaviorDiscoveryPlugin {
	public static int interfaceID =1;
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Discovering software component interaction behavior model, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0, 1}
			)

	public SoftwareInteractionModel interactionBehaviorDiscovery(UIPluginContext context, XLog originalLog, 
			congliu.processmining.objectusage.Component2Classes com2c) throws ConnectionCannotBeObtained
	{
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel("Software Component Interaction Behavior Discovery");
				
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
		MiningParameters IMparameters =  dialog.getMiningParameters(); //IMparameters.getClassifier()
		
		//set the interface similarity threshold. 
		SimilarityThresholdConfiguration simiConfig = new SimilarityThresholdConfiguration(0);
		SimilarityThresholdDialog simiDialog = new SimilarityThresholdDialog(context, simiConfig);
		
		InteractionResult resultSimi = context.showWizard("Configure similarity value", true, true, simiDialog);
		if (resultSimi != InteractionResult.FINISHED) {
			return null;
		}
		
		System.out.println("selected similarity value is: " +simiConfig.getThresholdValue()/10000);
		
		
		//create XLogInfo to get the xeventclass for each event. 
		XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(originalLog, IMparameters.getClassifier());
		
		//create factory to create Xlog, Xtrace and Xevent.
		XFactory factory = new XFactoryNaiveImpl();
		
		// obtain the mapping from component to classes. 
//		ArrayList<Component2Classes> c2c =ProgramExecutionData2SoftwareEventLogplugin.component2Class(class2componentMappingFile.getFilename());
		ArrayList<Component2Classes> c2c=SoftwareComponentBehaviorDiscoveryPlugin.Adapter(com2c);
		
		//the component log: each component refers to a interface2Log set(each with interface and a log)
		HashMap<String, HashSet<Interface2Log>> componentLog = new HashMap<String, HashSet<Interface2Log>>();
		
		// the interaction log: method to log. The log is composed of abstracted interface events.
		HashMap<XEventClass, XLog> interactionLog = new HashMap<XEventClass, XLog>();
		
		// interface cardinality, multi-instances counting. we have two type of relations: 1..1, 1..N
		HashMap<Interface, Integer> interfaceCardinality = new HashMap<Interface, Integer>();
		
		// hashmap from interface to log. it is used as the temp container for component Log. 
		//for each trace we get only one interface trace (without considering the instance factor), 
		//and then add each trace to interface2Log after refactoring.
		HashMap<Interface, XLog>  interface2Log = new HashMap<Interface, XLog>();
		
		
		// global interface type name to interface type id, i.e., interface toString() to id
		final HashMap<String, String> Iname2Iid = new HashMap<String, String>(); // interface full name to its short name

		
		//we traverse each trace and construct the component log, interaction log, interface cardinality.  
		for (XTrace trace: originalLog)
		{
			// for each trace, we create a mapping from interface to trace. 
			// in this stage we do not consider the component instance, as well as the interface cardinality. 
			HashMap<Interface, XTrace>  interface2Trace = new HashMap<Interface, XTrace>();
			
			//each trace keep a queue to store all events that need to recurse. 
			// here, we only add events that are detected as nesting.
			Queue<XEventAndInterface> nestedEventQueue = new LinkedList<XEventAndInterface>() ; 
			
			// start from the main method.
			for (XEvent event: trace)
			{
				// find the main(), i.e., its callers are null. this is the entry of the whole construction. 
				if (XSoftwareExtension.instance().extractCallerclass(event).equals("null"))
				{
					// construct the interface2trace for main()
					XTrace tempMainTrace = factory.createTrace();
					tempMainTrace.add(event);
					Interface mainInter= new Interface("null", getComponentNameByClass(XSoftwareExtension.instance().extractClass(event),c2c));
					if(!Iname2Iid.keySet().contains(mainInter.toString()))
					{
						mainInter.setId("Interface"+interfaceID);
						Iname2Iid.put(mainInter.toString(), mainInter.getId());
						interfaceID++;
					}
					else{
						mainInter.setId(Iname2Iid.get(mainInter.toString()));
					}
					
					
					interface2Trace.put(mainInter, tempMainTrace);
					// add main() to the queue. 
					nestedEventQueue.add(new XEventAndInterface(event, mainInter));
				}
			}
			
			// iterate all nested event in the queue. 
			while(!nestedEventQueue.isEmpty())
			{
				XEventAndInterface tempEventInter = nestedEventQueue.poll(); 
				System.out.println("Nested events: "+Xloginfo.getEventClasses().getClassOf(tempEventInter.getEvent()).toString());
			
				// call recursion.... 
				recursiveConstruction(Xloginfo, tempEventInter.getEvent(), tempEventInter.getInterface(), trace, c2c, factory,
						nestedEventQueue, interface2Trace, interactionLog, Iname2Iid);	
			}
			
			//for each interface, we refactored its trace to log by identifying interface instances. 
			refactoring(interface2Trace, interface2Log, c2c, factory, interfaceCardinality);

		}//for (XTrace trace: originalLog) 

		
		for(String interName: Iname2Iid.keySet())
		{
			System.out.println(interName+"-->"+Iname2Iid.get(interName));
		}
		
		// construct component to interface set
		HashSet<String> componentSet = new HashSet<>();
		for(Interface inter:interface2Log.keySet())
		{
			componentSet.add(inter.getComponent());
		}
		
		for (String com: componentSet)
		{
			HashSet<Interface2Log> tempInter = new HashSet<Interface2Log>();
			for (Interface inter: interface2Log.keySet())
			{
				if(inter.getComponent().equals(com))
				{
					tempInter.add(new Interface2Log(inter, interface2Log.get(inter)));
				}
			}
			
			componentLog.put(com, tempInter);
		}
		//construct the behavior model
		
		//the component model, discovered from component log: before discovery, we need first transform each interface log to HLog.
		ComponentModelsSet componentModelSet = new ComponentModelsSet();
		for(String component: componentLog.keySet())
		{
			ComponentModels componenModels = new ComponentModels();
			componenModels.setComponent(component);
			HashSet<Interface2HPN> i2hpnSet = new HashSet<Interface2HPN>();
			
			for(Interface2Log i2l: componentLog.get(component))
			{				
//				//serialization the current XESlog to disk
//				try {
//					FileOutputStream fosgz = new FileOutputStream("D:\\[7]\\interaction\\"+
//							i2l.getLog().getAttributes().get(XConceptExtension.KEY_NAME)+".xes"); 					
//					new XesXmlSerializer().serialize(i2l.getLog(), fosgz); 
//		
//					fosgz.close();
//					
//				} catch (IOException e) {
//					e.printStackTrace();
//				} 
				
				// hierarchical software event log, null means start from Top-level
				HSoftwareEventLog hseLog = ConstructHLog.ConstructHLogRecusively(factory,c2c, null, null, i2l.getLog(), IMparameters.getClassifier(), component);
				
				//software component behavior, HPN
				HierarchicalPetriNet hpn = MineHierarchicalPetriNet.mineHierarchicalPetriNet(context, hseLog, IMparameters);
				
				Interface2HPN i2hpn = new Interface2HPN(i2l.getInterface(), hpn);
				i2hpnSet.add(i2hpn);
				
			}
			componenModels.setI2hpn(i2hpnSet);
			componentModelSet.addComponent2HPNSet(componenModels);
		}
		
//		//set the inductive miner parameters, this miner is used to mine the interaction model. 
//		IMMiningDialog dialogNew = new IMMiningDialog(interactionLog.get(interactionLog.keySet().iterator().next()));
//		InteractionResult resultNew = context.showWizard("Configure Parameters for Inductive Miner (used for interaction models)", true, true, dialogNew);
//		if (resultNew != InteractionResult.FINISHED) {
//			return null;
//		}
//		// the mining parameters are set here 
//		MiningParameters IMparameterNew = dialogNew.getMiningParameters(); //IMparameters.getClassifier()
				
		MiningParameters IMparameterNew = (MiningParameters) new MiningParametersIMflc();
		//discover interaction model from interactionLog.
		InteractionModels interactionModels = new InteractionModels();
		for (XEventClass xevent:interactionLog.keySet())
		{
//			//serialization the current XESlog to disk
//			try {
//				FileOutputStream fosgz = new FileOutputStream("D:\\[7]\\interaction\\"+
//						interactionLog.get(xevent).getAttributes().get(XConceptExtension.KEY_NAME)+".xes"); 
//				//FileOutputStream fos = new FileOutputStream("D:\\KiekerData\\CaseStudy001\\EnrichedMethodLevelLog.xes.gz"); 
//				
//				new XesXmlSerializer().serialize(OrderingEventsNano.ordering(interactionLog.get(xevent), XSoftwareExtension.KEY_STARTTIMENANO), fosgz); 
//	            // serialize to xes.gz
//				//new XesXmlGZIPSerializer().serialize(log, fosgz);
//	
//				fosgz.close();
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
			
			IMparameterNew.setClassifier(interactionLog.get(xevent).getClassifiers().get(0));
			IMparameterNew.setNoiseThreshold((float) 0.2);

			Object[] objs =IMPetriNet.minePetriNet(OrderingEventsNano.ordering(interactionLog.get(xevent), XSoftwareExtension.KEY_STARTTIMENANO), 
					IMparameterNew, new Canceller() {
				public boolean isCancelled() {
					return false;
				}
			});
			
			// use Petri net reduction rules, based on Murata rules, i.e., Reduce Silent Transitions, Preserve Behavior
			Murata  murata = new Murata ();
			MurataParameters paras = new MurataParameters();
			paras.setAllowFPTSacredNode(false);
			Petrinet pn =(Petrinet) murata.run(context, (Petrinet)objs[0], (Marking)objs[1], paras)[0];
			
//			
//			Petrinet pn =(Petrinet) IMPetriNet.minePetriNet(OrderingEventsNano.ordering(interactionLog.get(xevent), XSoftwareExtension.KEY_STARTTIMENANO), 
//					IMparameterNew, new Canceller() {
//				public boolean isCancelled() {
//					return false;
//				}
//			})[0];
			interactionModels.addInteractionModel(xevent, pn);
		}
		
		
		//finally, create the SoftwareInteractionModel with three parts.
		SoftwareInteractionModel softwareInteractionModel = new SoftwareInteractionModel();
		softwareInteractionModel.setComponentModelSet(componentModelSet);
		softwareInteractionModel.setInteractionModels(interactionModels);
		softwareInteractionModel.setInterfaceCardinality(interfaceCardinality);
		
		return softwareInteractionModel;
		
	}// interactionBehaviorDiscovery()
	
	/**
	 * 
	 * @param Xloginfo
	 * @param currentEvent: the current event
	 * @param inter: the interface of current event
	 * @param trace
	 * @param c2c
	 * @param factory
	 * @param nestedEventQueue: final 
	 * @param interface2Trace: final 
	 * @param interactionLog: final 
	 */
	public static synchronized void recursiveConstruction(XLogInfo Xloginfo, XEvent currentEvent, Interface inter, XTrace trace, 
			ArrayList<Component2Classes> c2c, XFactory factory,
			final Queue<XEventAndInterface> nestedEventQueue, final HashMap<Interface, XTrace> interface2Trace, 
			final HashMap<XEventClass, XLog> interactionLog, 
			final HashMap<String, String> Iname2Iid)
	{
		HashSet<XEvent> eventSet= getCalledEventSet(currentEvent, trace); // the event set of event
		
		//label event with interface, i.e., transform eventSet<XEvent> to eventSet<XEventAndInterface>
		HashSet<XEventAndInterface> eventAndInterSet = new HashSet<XEventAndInterface>();// the caller is currentEvent.
		for(XEvent e: eventSet)
		{
			//the component is different from the caller, add new interface to each callee.
			if (!getComponentNameByClass(XSoftwareExtension.instance().extractClass(e),c2c).equals(inter.getComponent()))
			{
				Interface tempInter = new Interface(Xloginfo.getEventClasses().getClassOf(currentEvent).toString(), 
					getComponentNameByClass(XSoftwareExtension.instance().extractClass(e),c2c));
				if(!Iname2Iid.keySet().contains(tempInter.toString()))
				{
					tempInter.setId("Interface"+interfaceID);
					Iname2Iid.put(tempInter.toString(), tempInter.getId());
					interfaceID++;
				}
				else{
					tempInter.setId(Iname2Iid.get(tempInter.toString()));
				}
				eventAndInterSet.add(new XEventAndInterface(e, tempInter));
			}
			else{
				eventAndInterSet.add(new XEventAndInterface(e, inter));
			}
		}
		
		//update the nestedEventQueue, we only put in the nested events. 
		// interface set, it is possible that the current inter (interface) is included
		HashSet<Interface> interfaceSet = new HashSet<>();
		for(XEventAndInterface ei: eventAndInterSet)
		{
			interfaceSet.add(ei.getInterface());
			if (checkNesting(ei.getEvent(), trace))// if this event is nested
			{
				nestedEventQueue.add(ei);
			}
		}
		
		//check if the current event is an interaction event, i.e., if the eventSet belongs to the same component. 
		if (checkInteraction(currentEvent, eventSet, c2c)){
			//create new interface2Traces.
			//create event set for each interface
			HashMap<Interface,HashSet<XEvent>> inter2EventSet = new HashMap<Interface,HashSet<XEvent>>();
			for(Interface in: interfaceSet)
			{
				if(!interface2Trace.keySet().contains(in)) // if the newly created interfaces is not included in the interface2Trace
				{
					interface2Trace.put(in, factory.createTrace());	
				}
				HashSet<XEvent> eventS= new HashSet<>();
				for(XEventAndInterface ei: eventAndInterSet)
				{
					if(ei.getInterface().equals(in))
					{
						eventS.add(ei.getEvent());
					}
				}
				inter2EventSet.put(in, eventS);
			}
			
			// add each events to its corresponding interface trace
			for(Interface in: inter2EventSet.keySet())
			{
				XTrace tempTrace =interface2Trace.get(in);
				for(XEvent e: inter2EventSet.get(in))
				{
					tempTrace.add(e);
				}
				interface2Trace.put(in, tempTrace);
				
			}
			// add interaction log with interface abstraction to interactionLog
			// create normal software event log.
			//first check if there exist a interaction event (event class) ...
			if(interactionLog.keySet().contains(Xloginfo.getEventClasses().getClassOf(currentEvent)))
			{
				XLog currentinteractionLog = interactionLog.get(Xloginfo.getEventClasses().getClassOf(currentEvent));
				XTrace currentTrace= InitializeInteractionLog.createTrace(inter2EventSet, factory);
				currentinteractionLog.add(currentTrace);// add the newly created trace
				interactionLog.put(Xloginfo.getEventClasses().getClassOf(currentEvent), currentinteractionLog);
			}
			else{
				XLog currentinteractionLog = InitializeInteractionLog.initializeInteraction(factory, Xloginfo.getEventClasses().getClassOf(currentEvent).toString());
				XTrace currentTrace= InitializeInteractionLog.createTrace(inter2EventSet, factory);
				currentinteractionLog.add(currentTrace);// add the newly created trace
				interactionLog.put(Xloginfo.getEventClasses().getClassOf(currentEvent), currentinteractionLog);
			}
		}
		else // add the current event set to the current interface trace. 
		{
			XTrace tempTrace =interface2Trace.get(inter);
			// get the trace of current event (interface) from interface2Trace
			for(XEvent e: eventSet)
			{
				tempTrace.add(e);//null pointer
			}	
			interface2Trace.put(inter, tempTrace);
		}
	}
	
	/**
	 * refactoring each interface to trace and add them to the interface to log. 
	 * @param interface2Trace
	 * @param interface2Log: final
	 * @param c2c
	 * @param factory
	 * @param interfaceCardinality: final
	 */
	
	public static void refactoring(HashMap<Interface, XTrace> interface2Trace, final HashMap<Interface, XLog> interface2Log, 
			ArrayList<Component2Classes> c2c, XFactory factory, final HashMap<Interface, Integer> interfaceCardinality)
	{
		for(Interface inter:interface2Trace.keySet())
		{
			if(interface2Log.keySet().contains(inter))// the interface already exist
			{
				XLog tempLog =interface2Log.get(inter);
				// create new traces (each corresponds to one component instance)
				HashMap<String, Set<String>> interins2objectset =new HashMap<String, Set<String>>(); 
				for (Component2Classes com2classes: c2c)
				{
					if (com2classes.getComponent().equals(inter.getComponent()))
					{
						interins2objectset =InterfaceInstance2Objects(interface2Trace.get(inter),com2classes.getClasses());
					}
				}

				// set the cardinality for interfaces
				if(interfaceCardinality.keySet().contains(inter))
				{
					if (interfaceCardinality.get(inter)<interins2objectset.size())
					{
						interfaceCardinality.put(inter, interins2objectset.size());
					}
				}
				else{
					interfaceCardinality.put(inter, interins2objectset.size());
				}
				
				for(String interins:interins2objectset.keySet())
				{
					XTrace tempTrace = factory.createTrace();
					for(XEvent event: interface2Trace.get(inter))
					{
						if (interins2objectset.get(interins).contains(XSoftwareExtension.instance().extractClassObject(event)))
						{
							tempTrace.add(event);
						}
					}
						
					tempLog.add(tempTrace);
				}
				interface2Log.put(inter, tempLog);
			}
			else
			{
				//create new interface log.
				XLog tempLog = ConstructHLog.initialize(factory,inter.toString());
				
				// create new traces (each corresponds to one component instance)
				HashMap<String, Set<String>> interins2objectset =new HashMap<String, Set<String>>(); 
				for (Component2Classes com2classes: c2c)
				{
					if (com2classes.getComponent().equals(inter.getComponent()))
					{
						interins2objectset =InterfaceInstance2Objects(interface2Trace.get(inter),com2classes.getClasses());
					}
				}
				// set the cardinality for interfaces
				if(interfaceCardinality.keySet().contains(inter))
				{
					if (interfaceCardinality.get(inter)<interins2objectset.size())
					{
						interfaceCardinality.put(inter, interins2objectset.size());
					}
				}
				else{
					interfaceCardinality.put(inter, interins2objectset.size());
				}
				for(String interins:interins2objectset.keySet()){
					XTrace tempTrace = factory.createTrace();
					for(XEvent event: interface2Trace.get(inter)){
						if (interins2objectset.get(interins).contains(XSoftwareExtension.instance().extractClassObject(event)))
						{
							tempTrace.add(event);
						}
					}
						
					tempLog.add(tempTrace);
				}
				
				interface2Log.put(inter, tempLog);
			}	
		}
	}
	
	
	// return the event set called by an event in a trace
	public static HashSet<XEvent> getCalledEventSet(XEvent event, XTrace trace)
	{
		HashSet<XEvent> eventSet= new HashSet<XEvent>();
		for (XEvent e: trace)
		{
			if (XSoftwareExtension.instance().extractClassObject(event).equals(XSoftwareExtension.instance().extractCallerclassobject(e))
					&&XConceptExtension.instance().extractName(event).equals(XSoftwareExtension.instance().extractCallermethod(e)))
			{
				eventSet.add(e);
			}
		}
		return eventSet;
	}
	
	//check if the current event is an interaction event, i.e., if there exist an event with component!="NOT_FOUND" differnt from the component of  current event.
	public static boolean checkInteraction(XEvent currentEvent, HashSet<XEvent> eventSet, ArrayList<Component2Classes> c2c)
	{
		String currentComponent = getComponentNameByClass(XSoftwareExtension.instance().extractClass(currentEvent),c2c);
		for(XEvent e: eventSet)
		{
			String eComponent =getComponentNameByClass(XSoftwareExtension.instance().extractClass(e),c2c);
			if(!eComponent.equals(currentComponent))
			{
				return true;
			}
		}
		
		return false;
				
	}
	
	//check if an event is nested, i.e. there exist at least one event whose caller method and caller class object equals with its method and class object
	public static boolean checkNesting(XEvent event, XTrace trace)
	{
		for (XEvent e: trace)
		{
			if (XSoftwareExtension.instance().extractClassObject(event).equals(XSoftwareExtension.instance().extractCallerclassobject(e))
					&&XConceptExtension.instance().extractName(event).equals(XSoftwareExtension.instance().extractCallermethod(e)))
			{
				return true;
			}
		}
		return false;
	}


	//construct the instance for each interface
	public static HashMap<String, Set<String>> InterfaceInstance2Objects(XTrace trace, ArrayList<String> classList)
	{
		// we first conctruct a connected graph
		 DirectedGraph<String, DefaultEdge> directedGraph =
		            new DefaultDirectedGraph<String, DefaultEdge>
		            (DefaultEdge.class);
		 
		 // traverse through each event in the case
		 for (XEvent event :trace)
		 {
			 if(!XSoftwareExtension.instance().extractClassObject(event).equals("0"))//remove the effect of static methods
			 {
				 directedGraph.addVertex(XSoftwareExtension.instance().extractClassObject(event));
					//if the caller of this recording belongs to the component.
					if (classList.contains(XSoftwareExtension.instance().extractCallerclass(event)))
					{
						directedGraph.addVertex(XSoftwareExtension.instance().extractCallerclassobject(event));
						// add an arc from caller to callee
						directedGraph.addEdge(XSoftwareExtension.instance().extractClassObject(event), 
								XSoftwareExtension.instance().extractCallerclassobject(event));
					}
			 }
			 
		 }	
		 
		//compute all weakly connected component
        ConnectivityInspector ci = new ConnectivityInspector(directedGraph);
        
        //Returns a list of Set s, where each set contains 
        //all vertices that are in the same maximally connected component.
        java.util.List connected = ci.connectedSets();
        HashMap<String, Set<String>> interIns2Objs = new HashMap<String, Set<String>>();
        
        for (int i=0;i<connected.size();i++)
        {
        	interIns2Objs.put(i+XConceptExtension.instance().extractName(trace), (Set<String>)connected.get(i));
        }
        
        return interIns2Objs;
	}
	
	
	class Interface2Log
	{
		Interface inter;
		XLog log;
		public Interface2Log(Interface inter, XLog log)
		{
			this.inter = inter;
			this.log= log;
		}
		
		public void setInterface(Interface inter) 
		{
			this.inter = inter;
		}
		
		public Interface getInterface()
		{
			return this.inter;
		}
		
		public void setLog(XLog log)
		{
			this.log= log;
		}
		
		public XLog getLog()
		{
			return this.log;
		}
	}
	
	// give a class, this function returns its component
	public static String getComponentNameByClass(String Class,ArrayList<Component2Classes> c2c)
	{
		String componentName ="NOT_FOUND";  
		
		for (Component2Classes com2class: c2c)
		{
			if(com2class.getClasses().contains(Class))
			{
				componentName=com2class.getComponent();
				break;
			}
		}
		return componentName;
	}


}//class
