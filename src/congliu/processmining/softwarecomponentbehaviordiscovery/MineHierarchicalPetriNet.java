package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;
import org.processmining.plugins.petrinet.reduction.Murata;
import org.processmining.plugins.petrinet.reduction.MurataParameters;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processmining.softwareprocessmining.OrderingEventsNano;

/**
 * this class aims to discover a hierarchical petri net with multi-instances blocks from a hierarchical software event log. 
 * it is implemented in a recursive manner. 
 * @author cliu3
 *
 */
public class MineHierarchicalPetriNet {
	// the input is a hierarchical event log
	public static HierarchicalPetriNet mineHierarchicalPetriNet(PluginContext context, HSoftwareEventLog hseLog, MiningParameters parameters) throws ConnectionCannotBeObtained
	{
		HierarchicalPetriNet hpn = new HierarchicalPetriNet();
		
		//mine the top-level main
		// use the inductive miner to discover the base model(with block transitions)
//		Petrinet pn =(Petrinet) IMPetriNet.minePetriNet(OrderingEventsNano.ordering(hseLog.getMainLog(), XSoftwareExtension.KEY_STARTTIMENANO), 
//				parameters, new Canceller() {
//			public boolean isCancelled() {
//				return false;
//			}
//		})[0];
		
		Object[] objs =IMPetriNet.minePetriNet(OrderingEventsNano.ordering(hseLog.getMainLog(), XSoftwareExtension.KEY_STARTTIMENANO), 
				parameters, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		});
		
		// use Petri net reduction rules, based on Murata rules, i.e., Reduce Silent Transitions, Preserve Behavior
		Murata  murata = new Murata ();
		MurataParameters paras = new MurataParameters();
		paras.setAllowFPTSacredNode(false);
		Petrinet pn =(Petrinet) murata.run(context, (Petrinet)objs[0], (Marking)objs[1], paras)[0];
		
		hpn.setPn(pn);
		
		//to deal with its sub-mapping from eventclass to hierarchical petri net
		HashMap<XEventClass, HierarchicalPetriNet> XEventClass2hpn =new HashMap<XEventClass, HierarchicalPetriNet>();
		
		if (hseLog.getSubLogMapping().keySet().size()>0)
		{
			for(XEventClass key:hseLog.getSubLogMapping().keySet())
			{
				XEventClass2hpn.put(key, mineHierarchicalPetriNet(context,hseLog.getSubLogMapping().get(key), parameters));
			}
		}
		
		hpn.setXEventClass2hpn(XEventClass2hpn);
				
		return hpn;
	}
}
