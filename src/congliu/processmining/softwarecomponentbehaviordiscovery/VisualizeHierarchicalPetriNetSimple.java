package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;

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
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

/**
 * this visualizer aims to visualize hierarchical petri net in a simple way, without using multiple split panel
 * @author cliu3
 */
public class VisualizeHierarchicalPetriNetSimple {
	@Plugin(name = "Visualize Hierarchical Petri Net (Simple)", 
			returnLabels = { "Dot visualization" }, 
			returnTypes = { JComponent.class }, 
			parameterLabels = { "HPN" }, 
			userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
	@PluginVariant(variantLabel = "Visualize Hierarchical Petri Net (Simple)", 
					requiredParameterLabels = {0})// it needs one input parameter

	public JComponent visualizeTop(PluginContext context, HierarchicalPetriNet hpn) 
	{

		Dot dot = new Dot();
		dot.setDirection(GraphDirection.topDown);
		//dot.setDirection(GraphDirection.leftRight);
		dot.setOption("label", "");
		return new DotPanel((Dot)visualizeHPN2Dot(hpn, "", dot));	
	}	
	
	
	// the input is a hpn, a dot cluster, and the interfaces, provided xevent class set and required xevent class set
		public static DotCluster visualizeHPN2Dot(HierarchicalPetriNet hpn, String currentName, final DotCluster dot)
		{	
			//get the nested event to hpn mapping part, the keyset is a set of transitions
			final HashMap<XEventClass, HierarchicalPetriNet> XEventClass2hpn =hpn.getXEventClass2hpn();
			
			//get the pn part
			Petrinet pn=hpn.getPn();
			
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
						tDot = new LocalDotTransition(VisualizeNestingLengthNDFG.ActivityNamePackageClassMethod(t.getLabel()), 0);	
					}//if (nestedFlag==0)
					else// nested transitions
					{
						tDot = new LocalDotTransition(VisualizeNestingLengthNDFG.ActivityNamePackageClassMethod(t.getLabel()), 1);
					}
				}
			
				
				dot.addNode(tDot);
				mapPetrinet2Dot.put(t, tDot);			
			}// add transitions...
			
			
			//add places
			for (final Place p : pn.getPlaces()) 
			{
				DotNode pDot;
				pDot = new LocalDotPlace();
				dot.addNode(pDot);
				mapPetrinet2Dot.put(p, pDot);
			}
			
			//add arcs
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges()) 
			{
				if (mapPetrinet2Dot.get(edge.getSource()) != null && mapPetrinet2Dot.get(edge.getTarget()) != null) 
				{
					dot.addEdge(mapPetrinet2Dot.get(edge.getSource()), mapPetrinet2Dot.get(edge.getTarget()));
				}
			}
			
			//add refer to sub-block, if there exist nested transitions
			if(XEventClass2hpn.keySet().size()!=0)
			{
				for(XEventClass xevent: XEventClass2hpn.keySet())
				{
					DotCluster Nested_cluster =dot.addCluster();

					Nested_cluster.setOption("label", VisualizeNestingLengthNDFG.ActivityNamePackageClassMethod(xevent.toString()));
					Nested_cluster.setOption("penwidth", "2.0");
					Nested_cluster.setOption("style","dashed");
					Nested_cluster.setOption("color","blue");
					
					visualizeHPN2Dot(XEventClass2hpn.get(xevent),VisualizeNestingLengthNDFG.ActivityNamePackageClassMethod(xevent.toString()), Nested_cluster);// recursion, c2Inter
					
					// add arc from nested transitions to sub-cluster
					for(Transition t : pn.getTransitions())
					{
						if (t.getLabel().equals(xevent.toString()))
						{
							//add the arc from nested transition to the cluster
							List<DotNode> nodeList = Nested_cluster.getNodes();
							//nodeList.size()/2?
							LocalDotEdge tempEdge = new LocalDotEdge(mapPetrinet2Dot.get(t), nodeList.get(nodeList.size()-1), 1);
							dot.addEdge(tempEdge);
						
							// set arc from the nested transition to cluster
							tempEdge.setOption("lhead", Nested_cluster.getId());
							tempEdge.setOption("color", "blue");// edge color
							tempEdge.setOption("penwidth", "2.0");
							tempEdge.setOption("style","dashed");// arrow style is dashed
							tempEdge.setOption("label", "Call");// label
							tempEdge.setOption("fontcolor", "blue");
							
						}
					}
				}
			}	
			
			return dot;
			
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
