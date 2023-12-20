package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.util.HashMap;

import org.deckfour.xes.classification.XEventClass;
import org.deckfour.xes.model.XLog;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;

/**
 * for each main log (flat event log of each layer), we want to discover a petri net with multi-instance blocks. 
 * According to the definition, we need to discover two parts, i.e., the base petri net and mapping from blocking to sub-net.
 * @author cliu3
 *
 */
public class MinePetrinetwithMultiInstances {

	public static PetrinetMultiInstances minePetriNetWithMultiInstance(XLog input, MiningParameters parameters)
	{
		PetrinetMultiInstances pnmi = new PetrinetMultiInstances();
		
		//identifying multi-instance blocks from the event log
		EventLogwithBlocksClass eventBlocks = IdentifyingMultiInstanceBlocks.identifyingBlocks(input, parameters);
		
		// use the inductive miner to discover the base model(with block transitions)
		Petrinet pn =(Petrinet) IMPetriNet.minePetriNet(eventBlocks.getMainLogwithBlocks(), parameters, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		})[0];
		pnmi.setPn(pn);
		
		// use the inductive miner to discover the multi-instance model for each block
		HashMap<XEventClass, Petrinet> block2pn = new HashMap<XEventClass, Petrinet>();
		if(eventBlocks.getBlock2subLog()!=null)
		{
			for (XEventClass blocks: eventBlocks.getBlock2subLog().keySet())
			{
				Petrinet sub_pn =(Petrinet) IMPetriNet.minePetriNet(eventBlocks.getBlock2subLog().get(blocks), parameters, new Canceller() {
					public boolean isCancelled() {
						return false;
					}
				})[0];
				block2pn.put(blocks, sub_pn);// here the sub_pn is multi-instance sub-net
			}
		}
		
		pnmi.setBlock2subnet(block2pn);
		
		//pnmi.setPn((Petrinet) IMPetriNet.minePetriNet(input, parameters)[0]);
		
		
		return pnmi;
	}	
}
