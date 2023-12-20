package MultiInstanceProcessDiscovery;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.packages.PackageManager.Canceller;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.InductiveMiner.mining.MiningParameters;
import org.processmining.plugins.InductiveMiner.plugins.IMPetriNet;


public class DiscoverHierarchicalPetriNet {

	// the input is a hierarchical event log
	public static HierarchicalPetriNet mineHierarchicalPetriNet(PluginContext context, HEventLog hseLog, MiningParameters parameters) throws ConnectionCannotBeObtained
	{
		HierarchicalPetriNet hpn = new HierarchicalPetriNet();
		
		// use the inductive miner for discovering. 
		Object[] objs =IMPetriNet.minePetriNet(hseLog.getMainLog(), parameters, new Canceller() {
			public boolean isCancelled() {
				return false;
			}
		});
		
//			// use Petri net reduction rules, based on Murata rules, i.e., Reduce Silent Transitions, Preserve Behavior
//			Murata  murata = new Murata ();
//			MurataParameters paras = new MurataParameters();
//			paras.setAllowFPTSacredNode(false);
//			Petrinet pn =(Petrinet) murata.run(context, (Petrinet)objs[0], (Marking)objs[1], paras)[0];
//			hpn.setPn(pn);
		
//		make sure the source place do not have input arcs, e.g., single entry!
		hpn.setPn(addingArtifitialSouceandTargetPlaces((Petrinet)objs[0], (Marking)objs[1], (Marking)objs[2]));

		
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
	
	//make sure the source place do not have input arcs, e.g., single entry,and the target place do not have outgoing arcs. 
	public static Petrinet addingArtifitialSouceandTargetPlaces(final Petrinet pn, Marking initialM, Marking finalM)
	{
		//get all places in the initial marking. 
		List<Place> Initialplaces = initialM.toList();
		List<Place> Finalplaces = finalM.toList();
//		HashSet<String> placeNames = new HashSet<>();
//		for(Place p: places)
//		{
//			placeNames.add(p.getLabel());
//		}
//		System.out.println(placeNames);

		
		int sourceFlag =1;
		int targetFlag =1;
		for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges())
		{
			//System.out.println(edge.getTarget().getLabel());
			// if there exist an edge with a target place that included in the marking, then we need to add artificial source place and transition.  
			//if(placeNames.contains(edge.getTarget().getLabel()))
			if(Initialplaces.contains(edge.getTarget()))
			{
				sourceFlag=0;
				break;
			}
		}
		
		for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges())
		{
			// if there exist an edge with a source place that included in the marking, then we need to add artificial target place and transition.  
			if(Finalplaces.contains(edge.getSource()))
			{
				targetFlag=0;
				break;
			}
		}
		
		if(sourceFlag==0)//
		{
			int random = ThreadLocalRandom.current().nextInt(1, 100 + 1);
			//create a new source place 
			Place sourceP= pn.addPlace("lc source"+random);
			Transition sourceT = pn.addTransition("");
			sourceT.setInvisible(true);
			
			pn.addArc(sourceP, sourceT);
			pn.addArc(sourceT, Initialplaces.get(0));// an implicit assumption that there is only one place
		}
		
		if(targetFlag==0)//
		{
			//create a new target place 
			int random = ThreadLocalRandom.current().nextInt(1, 100 + 1);

			Place targetP= pn.addPlace("lc target"+random);
			Transition targetT = pn.addTransition("");
			targetT.setInvisible(true);
			
			pn.addArc(Finalplaces.get(0), targetT);
			pn.addArc(targetT, targetP);// an implicit assumption that there is only one place
		}
		return pn;
	}
	
}
