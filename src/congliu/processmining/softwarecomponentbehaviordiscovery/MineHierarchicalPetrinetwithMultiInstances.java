package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;

/**
 * this class aims to discover a hierarchical petri net with multi-instances blocks from a hierarchical software event log. 
 * it is implemented in a recursive manner. 
 * @author cliu3
 *
 */
public class MineHierarchicalPetrinetwithMultiInstances {

	// the input is a hierarchical event log
	public static HierarchicalPetrinetMultiInstances mineHierarchicalPetriNetWithMultiInstance(HSoftwareEventLog hseLog, MiningParameters parameters)
	{
		HierarchicalPetrinetMultiInstances hpnmi = new HierarchicalPetrinetMultiInstances();
		
		//mine the top-level main
		PetrinetMultiInstances pnmi = MinePetrinetwithMultiInstances.minePetriNetWithMultiInstance(hseLog.getMainLog(), parameters); 
		hpnmi.setPnmi(pnmi);
		
		//to deal with its sub-mapping from eventclass to hierarchical petri net with multi-instance
		HashMap<XEventClass, HierarchicalPetrinetMultiInstances> XEventClass2hpnmi =new HashMap<XEventClass, HierarchicalPetrinetMultiInstances>();
		if (hseLog.getSubLogMapping().keySet().size()>0)
		{
			for(XEventClass key:hseLog.getSubLogMapping().keySet())
			{
				XEventClass2hpnmi.put(key, mineHierarchicalPetriNetWithMultiInstance(hseLog.getSubLogMapping().get(key), parameters));
			}
		}
		hpnmi.setXEventClass2hpnmi(XEventClass2hpnmi);
				
		return hpnmi;
	}
		
}
