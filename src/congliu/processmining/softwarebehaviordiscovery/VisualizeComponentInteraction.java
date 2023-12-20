package congliu.processmining.softwarebehaviordiscovery;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.congliu.softwareBehaviorDiscoveryLengthN.VisualizeNestingLengthNDFG;
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
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.dot.DotEdge;
import org.processmining.plugins.graphviz.dot.DotElement;
import org.processmining.plugins.graphviz.dot.DotNode;

import com.kitfox.svg.SVGException;

import congliu.processmining.softwarecomponentbehaviordiscovery.HierarchicalPetriNet;

/**
 * this visualizer aims to visualize software behavior (behavior with HPN array)
 * @author cliu3
 */

public class VisualizeComponentInteraction {
	@Plugin(name = "Visualize Software Component Interaction", 
	returnLabels = { "Dot visualization" }, 
	returnTypes = { JComponent.class }, 
	parameterLabels = { "Component2HPNArraySet" }, 
	userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
	@PluginVariant(variantLabel = "Visualize Hierarchical Petri Net", 
			requiredParameterLabels = {0})// it needs one input parameter
	
	public JComponent visualizeTop(PluginContext context, Component2HPNArraySet com2hpnarraySet) throws SVGException, IOException 
	{
		//return Dot2SVG.DOT2SVG(visualizeComponentInteraction2Dot(com2hpnarray), "");
		//return new DotPanel(visualizeComponentInteraction2Dot(com2hpnarraySet));
		return visualizeComponentInteraction2Dot(com2hpnarraySet);
	}
	
	public static JPanel visualizeComponentInteraction2Dot(Component2HPNArraySet com2hpnarraySet) throws SVGException, IOException 
	{
		// the ids of component--> provided and required interfaces (dot transitions)
		ArrayList<Component2Interfaces> com2InterArray = new ArrayList<Component2Interfaces>();
		
		//the dot object of the whole interaction
		Dot dot = new Dot();
		//set the edge from node to cluster
		dot.setOption("compound", "true");
		dot.setDirection(GraphDirection.topDown);
		//dot.setDirection(GraphDirection.leftRight);
		dot.setOption("label", "Software Component Interaction");
		
		// set cluster distance
		dot.setOption("nodesep", "3");// increase space between nodes
		//dot.setOption("ranksep", "1.5");
		
		// for each component we create a cluster
		for  (Component2HPNArray com2hpns: com2hpnarraySet.getC2HPNs())
		{
			// for each component cluster we generate a component2Interfaces elements
			Component2Interfaces c2Inter = new Component2Interfaces();// unique for each component, no matter how many hpn is has
			com2InterArray.add(c2Inter);
			
			DotCluster Com_cluster =dot.addCluster();// unique for each component, no matter how many hpn it has
			Com_cluster.setOption("label", com2hpns.getComponentName());
			Com_cluster.setOption("penwidth", "3.0");
			
			// set the component id			
			c2Inter.setComponentID(Com_cluster);
			
			// get the required and provided interface event class set
			HashSet<XEventClass> providedXeventClass = com2hpns.getPEventClass();
			HashSet<XEventClass> requiredXeventClass = com2hpns.getREventClass();
			
			//add the each hpn to the cluster
			for(HierarchicalPetriNet hpn: com2hpns.getHpnArray().getHPNs())
			{ 
				//visualize each hpn and add them to the current component cluster
				visualizeHPN2Cluster(hpn, Com_cluster, c2Inter, providedXeventClass, requiredXeventClass);
			}
		}
		
		/**
		 * we add svg operations to each dot. 
		 * Input: dot + ArrayList
		 */
		
		for (Component2Interfaces c2I : com2InterArray)
		{
			System.out.println("component: " +c2I.getComponentID());
			for (DotElement tidr: c2I.getRTIDs())
			{
				System.out.println("required: "+ tidr);
			}
			for (DotElement tidp: c2I.getPTIDs())
			{
				System.out.println("provided: "+ tidp);
			}

		}
		
		return Dot2SVG.DOT2SVG(dot, com2InterArray);

		//return dot;
	}
	
	// the input is a hpn, a dot cluster, and the interfaces, provided xevent class set and required xevent class set
	public static DotCluster visualizeHPN2Cluster(HierarchicalPetriNet hpn, final DotCluster Com_cluster, final Component2Interfaces c2Inter, 
			final HashSet<XEventClass> ProvidedXeventClass, final HashSet<XEventClass> RequiredXeventClass)
	{
		//get the nested event to hpn mapping part, the keyset is a set of transitions
		final HashMap<XEventClass, HierarchicalPetriNet> XEventClass2hpn =hpn.getXEventClass2hpn();
		
		//get the pn part
		Petrinet pn=hpn.getPn();
		
		// get the required interface
		//HashSet<XEventClass> ProvidedXeventClass = hpn.getPEventClass();
		HashSet<DotElement> ProvidedTransitionID = new HashSet<DotElement>();
		
		//get the provided interface
		//HashSet<XEventClass> RequiredXeventClass = hpn.getREventClass();
		HashSet<DotElement> RequiredTransitionID = new HashSet<DotElement>();
		
		// the mapping from transition(place) to dotNode
		HashMap<PetrinetNode, DotNode> mapPetrinet2Dot = new HashMap<PetrinetNode, DotNode>();
		
		//add transitions
		for (final Transition t : pn.getTransitions()) 
		{
			DotNode tDot;
			if (t.isInvisible()) // invisible transition
			{
				tDot = new LocalDotTransition();
			} 
			else// normal transition
			{
				// detect if it is a nested transition
				int nestedFlag=0;
				for(XEventClass xevent: XEventClass2hpn.keySet())
				{
					if(t.getLabel().equals(xevent.toString()))
					{
						nestedFlag =1;
						break;
					}	
				}
				
				if (nestedFlag==0) // for normal transitions
				{
					tDot = new LocalDotTransition(t.getLabel(), 0);	
				}//if (nestedFlag==0)
				else// nested transitions
				{
					tDot = new LocalDotTransition(t.getLabel(), 1);
				}
			}
			
			// check if the current transition node is a required interface
			for (XEventClass xep: ProvidedXeventClass)
			{
				if (t.getLabel().equals(xep.toString()))
				{
					ProvidedTransitionID.add(tDot);
					//tDot.setOption("fillcolor", "green");// change the node style
					System.out.println("PA: "+ t.getLabel()+";" + tDot.getId());
				}
			}
			
			// check if the current transition node is a required interface
			for (XEventClass xer: RequiredXeventClass)
			{
				if (t.getLabel().equals(xer.toString()))
				{
					RequiredTransitionID.add(tDot);
					//tDot.setOption("fillcolor", "red");// change the node style
					System.out.println("RA: "+ t.getLabel()+";" + tDot.getId());
				}
			}
			
			Com_cluster.addNode(tDot);
			mapPetrinet2Dot.put(t, tDot);			
		}// add transitions...
		
		//add provided and required transition IDs to c2Inter
		c2Inter.setPTIDs(ProvidedTransitionID);
		c2Inter.setRTIDs(RequiredTransitionID); 
		
		//add places
		for (final Place p : pn.getPlaces()) 
		{
			DotNode pDot;
			pDot = new LocalDotPlace();
			Com_cluster.addNode(pDot);
			mapPetrinet2Dot.put(p, pDot);
		}
		
		//add arcs
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges()) 
		{
			if (mapPetrinet2Dot.get(edge.getSource()) != null && mapPetrinet2Dot.get(edge.getTarget()) != null) 
			{
				Com_cluster.addEdge(mapPetrinet2Dot.get(edge.getSource()), mapPetrinet2Dot.get(edge.getTarget()));
			}
		}
		
		//add refer to sub-block, if there exist nested transitions
		if(XEventClass2hpn.keySet().size()!=0)
		{
			for(XEventClass xevent: XEventClass2hpn.keySet())
			{
				DotCluster Nested_cluster =Com_cluster.addCluster();

				Nested_cluster.setOption("label", VisualizeNestingLengthNDFG.ActivityNameMethod(xevent.toString()));
				Nested_cluster.setOption("penwidth", "2.0");
				Nested_cluster.setOption("style","dashed");
				Nested_cluster.setOption("color","blue");
				
				visualizeHPN2Cluster(XEventClass2hpn.get(xevent), Nested_cluster, c2Inter, ProvidedXeventClass, RequiredXeventClass);// recursion, c2Inter
				
				// add arc from nested transitions to sub-cluster
				for(Transition t : pn.getTransitions())
				{
					if (t.getLabel().equals(xevent.toString()))
					{
						//add the arc from nested transition to the cluster
						List<DotNode> nodeList = Nested_cluster.getNodes();
						//nodeList.size()/2?
						LocalDotEdge tempEdge = new LocalDotEdge(mapPetrinet2Dot.get(t), nodeList.get(nodeList.size()-1), 1);
						Com_cluster.addEdge(tempEdge);
					
						// set arc from the nested transition to cluster
						tempEdge.setOption("lhead", Nested_cluster.getId());
						tempEdge.setOption("color", "blue");// edge color
						tempEdge.setOption("penwidth", "2.0");
						tempEdge.setOption("style","dashed");// arrow style is dashed
						tempEdge.setOption("label", "Call");// label
						tempEdge.setOption("fontcolor", "blue");
						
						/**
						 * the improved Dot package support edge from cluster to another cluster. 
						 * it works like what we have implemented 
						 */
//						LocalDotEdge tempEdge = new LocalDotEdge(mapPetrinet2Dot.get(t), Nested_cluster, 1);
//						Com_cluster.addEdge(tempEdge);
					}
				}
				
			}
		}	
		
		return Com_cluster;
		
	}
	
	//inner class for transition dot
	private static class LocalDotTransition extends DotNode 
	{
		//transition flag =0, normal transition flag=1, nested transition, flag=2 multi-instance transition
		public LocalDotTransition(String label, int flag) {
			super(label, null);
			if (flag==0)
			{
				
				setOption("shape", "box");
			}
			else if (flag==1)//nested transition
			{				
				setOption("shape", "box");
				setOption("style", "filled");
				//setOption("peripheries","2");//double line
			}
			else // multi-instance
			{
				setOption("shape", "box");
				setOption("shape", "box");
				setOption("fillcolor", "green");
				setOption("width", "0.45");
				setOption("height", "0.15");
				//setOption("style", "invis");
				//setOption("shape", "point");
			}
		}
		
		//tau transition
		public LocalDotTransition() {
			super("", null);
			setOption("style", "filled");
			setOption("fillcolor", "black");
			setOption("width", "0.45");
			setOption("height", "0.15");
			setOption("shape", "box");
		}
	}// inner class for transition dot
	
	
	//inner class for place dot
	private static class LocalDotPlace extends DotNode {
		public LocalDotPlace() {
			super("", null);
			setOption("shape", "circle");
		}
	}// inner class for place dot
	
	// inner class for edge dot, we have two types of edges, 
	//the normal one with arrows =1, and the double arrows with arrows =2.
	private static class LocalDotEdge extends DotEdge
	{
		public LocalDotEdge(DotNode source, DotNode target, int arrows)
		{
			super(source, target);
			if (arrows==1)
			{	
				setOption("arrowhead", "vee");
			}
			else
			{
				//startEdge =dot.addEdge(startTransitionDot,startPlaceDot);
				//setOption("penwidth","2");
				//startEdge.setOption("color", "black:black");
				//setOption("label", " N");
				setOption("arrowhead", "vee");//or to use normalnormal twice
				//startEdge.setOption("shape", "vee");
			}
		}
	}
}
