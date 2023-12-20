package congliu.processmining.objectusage;

import java.util.HashMap;

import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.models.semantics.petrinet.Marking;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.dot.DotNode;

import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.VisualizeHPNandInteraction2Dot;

public class VisualizePN {
	
	public static DotCluster visualizePN2Dot(Petrinet pn, Marking initialM, Marking finalM, DotCluster cluster)
	{
		// the mapping from transition(place) to dotNode
		HashMap<PetrinetNode, DotNode> mapPetrinet2Dot = new HashMap<PetrinetNode, DotNode>();

		//add transitions
		if(pn.getTransitions().size()==0)
		{
			return cluster;
		}
		for (final Transition t : pn.getTransitions()) 
		{
			DotNode tDot;
			if (t.isInvisible()) // invisible transition
			{
				tDot = new LocalDotTransition();
			} 
			else// normal transition, i.e., interfaces
			{	
				tDot = new LocalDotTransition(VisualizeHPNandInteraction2Dot.ActivityNameMethodClassPackage(t.getLabel()), 0);	
			}
			cluster.addNode(tDot);
			mapPetrinet2Dot.put(t, tDot);		
		}
		
		//add places
		for (final Place p : pn.getPlaces()) 
		{
//			System.out.println(p.getLabel()+"initial marking:"+getMarkingLabel(initialM));
//			System.out.println(p.getLabel()+"final marking:"+getMarkingLabel(finalM));
//			int startFlag=0;
//			int endFlag=0;
//			//check if the p is start or end. 
//			for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges())
//			{
//				//if there is no incoming arc, this place is a start place
//				if(edge.getTarget().getId().equals(p.getId()))
//				{
//					startFlag=1;
//					break;
//				}
//			}
//			
//			//check if the p is start or end. 
//			for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges())
//			{
//				//if there is no out-going arc, this place is a end place
//				if(edge.getSource().getId().equals(p.getId()))
//				{
//					endFlag=1;
//					break;
//				}
//			}
			
			//if the place is a start place
			DotNode pDot;
			if(p.getLabel().equals(getMarkingLabel(initialM)))
			{
				pDot = new LocalDotPlace(0);
			}
			else if (p.getLabel().equals(getMarkingLabel(finalM)))
			{
				pDot = new LocalDotPlace(1);
			}
			else {
				pDot = new LocalDotPlace();
			}
//			DotNode pDot;
//			pDot = new LocalDotPlace();
			
			cluster.addNode(pDot);
			mapPetrinet2Dot.put(p, pDot);
		}
		
		//add arcs
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges()) 
		{
			if (mapPetrinet2Dot.get(edge.getSource()) != null && mapPetrinet2Dot.get(edge.getTarget()) != null) 
			{
				cluster.addEdge(mapPetrinet2Dot.get(edge.getSource()), mapPetrinet2Dot.get(edge.getTarget()));
			}
		}
		
		return cluster;
	}
	
	//get the place label for initial and final markings, e.g., get source 1 from [(source 1,1)]
	
	private static String getMarkingLabel(Marking marking)
	{
		String [] split1 = marking.toString().split("\\(");
		
		String [] split2 = split1[1].split("\\,");
		return split2[0];
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
				setOption("style", "filled");
				setOption("fillcolor", "lightblue");
				//setOption("shape", "ellipse");
//				setOption("width", "0.45");
//				setOption("height", "0.15");
				setOption("peripheries","2");//double line
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
		public LocalDotPlace(int flag) {
			super("", null);
			if (flag==0) // the start place. with green.
			{
				setOption("shape", "doublecircle");
				setOption("style", "filled");
				setOption("fillcolor", "green");
//					setOption("image", "D:/triangle.svg");
				
			}
			else // the complete place with red
			{
				setOption("shape", "doublecircle");
				setOption("style", "filled");
				setOption("fillcolor", "red");
			}
		}
			
		}// inner class for place dot

	
}
