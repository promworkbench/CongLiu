package org.processmining.congliu.ModularPetriNet;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
/**
 * this class aims to discover a modular petri net from an event log (without hierarchies)
 * @author cliu3
 *
 */
//@Plugin(
//		// plugin name
//		name = "Mine Modular Petri Net",
//		
//		//return labels
//		returnLabels = {"Modular Petri Net"}, 
//		// return class, here the DFGExtended is an improved Dfg by extending nested and component information 
//		returnTypes = {ModularPetriNet.class},
//		
//		userAccessible = true,
//		help = "This plugin aims to discover a modular Petri Net from an event log", 
//		
//		//input labels, corresponding with the second parameter of main function
//		parameterLabels = {"ProM Software Event Log"} 
//		)
public class MineModularPetriNet {
	
//	@UITopiaVariant(
//	        affiliation = "TU/e", 
//	        author = "Cong liu", 
//	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
//	        )
//	@PluginVariant(
//			variantLabel = "Modular Petri Net",
//			// the number of required parameters, {0} means one input parameter
//			requiredParameterLabels = {0}
//			)
	public static ModularPetriNet mineModularPetriNet (MiningParameters IMparameters, XLog inputlog) 
	{
//		//get the log name 
//		context.getFutureResult(0).setLabel(
//				"Modular Petri Net of " + XConceptExtension.instance().extractName(inputlog));
		
		//to get the event class via the classifier. 
		//how to decide which classifier to use, one choice is to use dialog for selection, like the dfg of Sander. 
		// it should be adapted before applied to the big cases, one should make sure its log classifier. 
		// here we use the classifier named "Activity Name" it contains concept:name class package. 
//		List <XEventClassifier> classiferList = inputlog.getClassifiers();
//		XEventClassifier ourClassifier =classiferList.get(0);
		
		// here we can choose any Petri net discovery algorithm, like IM, Alpha Miner, to get the petri net part.  
		Petrinet pn =minePetriNetfromLog(IMparameters, inputlog);
		
		//get the mapping from transition (xeventclass) to component and nesting combination
		HashMap<XEventClass, ComponentNesting> minePetriNetfromLog = mineMappingfromXevnet2compN(IMparameters.getClassifier(), inputlog);
		
		// create the modular Petri net using the mined petri net and mapping. 
		ModularPetriNet mPetrinet = new ModularPetriNet(pn, minePetriNetfromLog);
		return mPetrinet;
	
	}
	
	// this function using IM to mine a Petri net from the event Log. 
	// here we can choose any mining algorithm that produces a petri net as the miner. 
	public static Petrinet minePetriNetfromLog(MiningParameters IMparameters, XLog inputlog) 
	{
//		// 
//		IMMiningDialog dialog = new IMMiningDialog(inputlog);
//		InteractionResult result = context.showWizard("Configure Inductive Miner", true, true, dialog);
//		if (result != InteractionResult.FINISHED) {
//			return null;
//		}
//		Petrinet pn =(Petrinet) IMPetriNet.minePetriNet(context, inputlog, dialog.getMiningParameters())[0];
		
		// how to set the default classifier as activity name. 
		//using the inductive miner, set the parameter
//		MiningParameters IMparameters= new MiningParametersIMi();
//		IMparameters.setClassifier(ourClassifier);
//		IMparameters.setIncompleteThreshold((float) 0.8);
//		IMparameters.setNoiseThreshold((float) 0.8);
		Petrinet pn =(Petrinet) IMPetriNet.minePetriNet(inputlog, IMparameters, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		})[0];

		return pn;
	}
	
	// this function takes an event log as input and obtain the mapping from xeventclass to componentNesting 
	public static HashMap<XEventClass, ComponentNesting> mineMappingfromXevnet2compN(XEventClassifier ourClassifier, XLog inputlog) 
	{
		// essentially, it is the mapping from transition to its component and nesting attributes. 
		HashMap<XEventClass, ComponentNesting> xevent2compNest = new HashMap<XEventClass, ComponentNesting>();
		
		XLog inputLog = (XLog)inputlog.clone();

		XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(inputLog, ourClassifier);
		
		//traverse through the whole log, and add the xeventclass of each transition to the hashmap. 
		
		for (XTrace trace: inputLog)
		{
			for(XEvent event : trace)
			{
				if (event.getAttributes().get("Belonging_Component").toString()!=null)
				{
					// construct the componentNesting part
					ComponentNesting cn = new ComponentNesting(event.getAttributes().get("Belonging_Component").toString(), 
							event.getAttributes().get("Nested").toString());
					//construct the xeventclass part
					XEventClass eventClass= Xloginfo.getEventClasses().getClassOf(event);
					xevent2compNest.put(eventClass, cn);
				}

			}
		}
		
		// preprocess the event log to construct the xevent2compoNest
		return xevent2compNest;
	
	}
}
