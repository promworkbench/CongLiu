package org.proecssmining.congliu.directlyfollowedgraphvisualization;

import java.util.HashMap;
import java.util.Set;

/**
 * This visualizer is adapted from GraphvizPetrinet in IM, to visualize modular Petri net. 
 * here, we use the belonging information of each transition as the module information. 
 */
import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

@Plugin(name = "Graphviz Petri net visualisation (Cluster)", returnLabels = { "Dot visualization" }, returnTypes = { JComponent.class }, parameterLabels = { "Petri net" }, userAccessible = false)
@Visualizer
public class GraphvizPetriNetCluster {

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "S.J.J. Leemans", email = "s.j.j.leemans@tue.nl")
	@PluginVariant(variantLabel = "Convert Petri net", requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, Petrinet petrinet) {
		Dot dot = convert(petrinet, null);
		return new DotPanel(dot);
	}
	
	public static Dot convert(Petrinet petrinet, Marking initialMarking, Marking... finalMarkings) {
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);
		//convert(dot, petrinet, initialMarking, finalMarkings);
		//convertwithComponent(dot, petrinet, initialMarking, finalMarkings);
		tryDot(dot);
		return dot;
	}

	private static void tryDot(Dot dot)
	{
	
        DotNode node1 = dot.addNode("node");
        DotCluster cluster = dot.addCluster();
        cluster.setLabel("cluster name");
        DotNode node2 =cluster.addNode("node in cluster");
        cluster.addNode(node1);
        //cluster.addEdge(node1, node2);
        dot.addEdge(node1, node2);


	}
	private static void convertwithComponent (final Dot dot, Petrinet petrinet, Marking initialMarking, Marking... finalMarkings)
	{
		//create a hashmap to keep the mapping from Petri net nodes to dot nodes. Key: petri net node
		HashMap <PetrinetNode, DotNode> mapPetrinet2Dot = new HashMap<PetrinetNode, DotNode>();
		
		//find the correct component for each transition and place, belonging component. 
		//for each transition it can be determined directly, for each place, if its pre and post transitions both belongs to this component 
		
		//create the belonging component 
		HashMap<String, String> component2Package = new HashMap<String, String>();
		component2Package.put("XES Library", "org.deckfour.xes");
		component2Package.put("Open XES Log File (Naive)", "org.processmining.plugins.log");
		component2Package.put("Alpha Miner", "org.processmining.plugins.petrinet");
		component2Package.put("Fuzzy Miner", "processmining.plugins.fuzzymodel");
		
		Set<String> componentList =component2Package.keySet();
		
		//set the belonging component for each transition
		//the format of transition "class+package+method"
		HashMap<Transition, String> transition2Component = new HashMap<Transition, String>();
		for(Transition t : petrinet.getTransitions())
		{
			//int flag= 0;
			for(String component: componentList)
			{
//				if (t.getLabel().split("\\+")[1].startsWith(component2Package.get(component)))
//				{
//					transition2Component.put(t, component);
//					flag=1;
//					break;
//				}	
				if (t.getLabel().contains(component2Package.get(component)))
				{
					transition2Component.put(t, component);
					//flag=1;
					break;
				}
			}
//			if (flag==0)
//			{
//				transition2Component.put(t, "Null");
//			}
		}
//		//set the belonging component for each place
//		HashMap<Place, String> place2Component = new HashMap<Place, String>();
		
		for (String componentName:componentList)
		{
			
			DotCluster cluster =dot.addCluster();
			cluster.setLabel(componentName);
			
			//add transition to each cluster
			for(Transition t : petrinet.getTransitions())
			{
				//if (t.getLabel().split("+")[1].equals(cluster.getLabel()))
				if (t.getLabel().contains(componentName))
				{
					DotNode transition;
					if (t.isInvisible()) {
						transition = new LocalDotTransition();
					} else {
						transition = new LocalDotTransition(t.getLabel());
					}
					cluster.addNode(transition);
					mapPetrinet2Dot.put(t, transition);
				}
			}
			//dot.addNode(cluster);	
			//dot.addCluster(cluster);
		}
			
	//add place to each cluster

	for (Place p : petrinet.getPlaces()) {
		DotNode place;

		//find final marking
		boolean inFinalMarking = false;
		if (finalMarkings != null) {
			for (Marking finalMarking : finalMarkings) {
				inFinalMarking |= finalMarking.contains(p);
			}
		}

		if (initialMarking != null && initialMarking.contains(p)) {
			place = new LocalDotPlace();
			place.setOption("style", "filled");
			place.setOption("fillcolor", "green");
		} else if (finalMarkings != null && inFinalMarking) {
			place = new LocalDotPlace();
			place.setOption("style", "filled");
			place.setOption("fillcolor", "red");
		} else {
			place = new LocalDotPlace();
		}
		dot.addNode(place);
		mapPetrinet2Dot.put(p, place);
		}
		
		
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : petrinet.getEdges()) {
			if (mapPetrinet2Dot.get(edge.getSource()) != null && mapPetrinet2Dot.get(edge.getTarget()) != null) 
			{
				dot.addEdge(mapPetrinet2Dot.get(edge.getSource()), mapPetrinet2Dot.get(edge.getTarget()));
			}
		}
		
	}
	
//	// define a cluster class
//	private static class LocalDotCluster extends DotCluster {
//		public LocalDotCluster(String label) {
//			super();
//			setOption("label", label);
//		}
//	}
	//define a transition class
	private static class LocalDotTransition extends DotNode {
		//transition
		public LocalDotTransition(String label) {
			super(label, null);
			setOption("shape", "box");
		}

		//tau transition
		public LocalDotTransition() {
			super("", null);
			setOption("style", "filled");
			setOption("fillcolor", "#EEEEEE");
			setOption("width", "0.15");
			setOption("shape", "box");
		}
	}
	
	//define a place class
	private static class LocalDotPlace extends DotNode {
		public LocalDotPlace() {
			super("", null);
			setOption("shape", "circle");
		}
	}
	
	// returns the pre and post transition of an place
	
}
