package congliu.processminig.softwarecomponentinteractionbehaviordiscovery;

import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JComponent;

import org.deckfour.xes.classification.XEventClass;
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
 * this visualizer aims to visualize the software interaction model discovered by SoftwareBehaviorDiscoveryPlugin.
 * @author cliu3
 *
 */
public class VisualizeSoftwareInteractionModel {
	@Plugin(name = "Visualize Software Component Interaction Model", 
			returnLabels = { "Dot visualization" }, 
			returnTypes = { JComponent.class }, 
			parameterLabels = { "SoftwareInteractionModel" }, 
			userAccessible = true)
			@Visualizer
			@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
			@PluginVariant(variantLabel = "Visualize Hierarchical Petri Net", 
					requiredParameterLabels = {0})// it needs one input parameter
	
	public JComponent visualizeTop(PluginContext context, SoftwareInteractionModel softwareInteractionModel)
	{
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.topDown);
		dot.setOption("label", "Component Interaction Model");
		dot.setOption("fontsize", "36");
		
		//get the interaction part(from xeventclass to a flat pn)
		//store all interaction eventclass to pn cluster
		HashMap<XEventClass, DotCluster> xeventClass2InteractionCluster = new HashMap<XEventClass, DotCluster>(); 
		// store all xeventclass and its correspond interfaces (tDot)
		HashSet<DotNode> tDotNodeSet = new HashSet<>();
		InteractionModels interactionModels = softwareInteractionModel.getInteractionModels();
		for(XEventClass xevent:interactionModels.getInteractionModels().keySet())
		{
			//create a cluster for each component.
			DotCluster interactionCluster =dot.addCluster();
			//interactionCluster.setOption("label", xevent.toString());
			//interactionCluster.setOption("label", "Interaction");
			interactionCluster.setOption("fontsize", "24");
			interactionCluster.setOption("style", "filled");
			//interactionCluster.setOption("shape", "ellipse");
			interactionCluster.setOption("fillcolor", "lightblue");
			interactionCluster.setOption("color", "white");
			visualizePN2Dot(interactionModels.getInteractionModels().get(xevent), interactionCluster, tDotNodeSet);
			
			xeventClass2InteractionCluster.put(xevent, interactionCluster);
		}
				
		// get the component model part
		// store all interfaces to interface dotcluster
		ComponentModelsSet compoenntModels =softwareInteractionModel.getComponentModelSet();  
		HashMap<Interface, DotCluster>  interface2InterfaceCluster= new HashMap<Interface, DotCluster> ();
		
//		//store the interface to short name mapping, e.g., <I1, the name of the interface>
//		HashMap<String, String> I2interfacename = new HashMap<String, String>();
		
		//go through each component model, 
		for(ComponentModels com :compoenntModels.getComponent2HPNSet())
		{
			//create a cluster for each component.
			DotCluster componentCluster =dot.addCluster();
			componentCluster.setOption("label", com.getComponent()); // component name, as the label
			componentCluster.setOption("penwidth", "5.0"); // width of the component border\
			componentCluster.setOption("fontsize", "24");
			
			//componentCluster.setOption("style","dashed");
			componentCluster.setOption("color","black");
			for(Interface2HPN Interface2HPN: com.getI2hpn())// handle each component interface. 
			{
				DotCluster InterfaceCluster =componentCluster.addCluster();
				//InterfaceCluster.setOption("label", Interface2HPN.getInterface().toString());
				InterfaceCluster.setOption("label", Interface2HPN.getInterface().getId());
				InterfaceCluster.setOption("penwidth", "3.0");
				InterfaceCluster.setOption("style","dashed");
				VisualizeHPNandInteraction2Dot.visualizeHPN2Dot(Interface2HPN.getHPN(), Interface2HPN.getInterface().toString(),InterfaceCluster, xeventClass2InteractionCluster);
				interface2InterfaceCluster.put(Interface2HPN.getInterface(), InterfaceCluster);
			}
		}
		
		
		//get the interface cardinality information
		HashMap<Interface, Integer> interfaceCardinality = softwareInteractionModel.getInterfaceCardinality();
		
		// add interface arc and cardinality
		for(DotNode tNode:tDotNodeSet)
		{
			for(Interface inter:interface2InterfaceCluster.keySet())
			{
				//add an arc from interface transition to interface hpn
				if (tNode.getLabel().equals(inter.getId()))
				{
					//add an edge from the current t to the cluster
					DotEdge tempEdge = dot.addEdge(tNode, interface2InterfaceCluster.get(inter));				
					// set arc from the nested transition to cluster
					tempEdge.setOption("lhead", interface2InterfaceCluster.get(inter).getId());
					tempEdge.setOption("color", "red");// edge color
					tempEdge.setOption("penwidth", "3.0");
					tempEdge.setOption("fontcolor", "red");
					tempEdge.setOption("fontsize", "24");
					if(interfaceCardinality.get(inter)>1){
						tempEdge.setOption("label", "*");// label
					}
					else {
						tempEdge.setOption("label", "1");// label
					}					
				}
			}
		}
		
		return new DotPanel(dot);
	}
	/**
	 * 
	 * @param pn
	 * @param cluster
	 * @param tDotNodeSet: record all t node in the interaction models 
	 * @return
	 */
	
	public static DotCluster visualizePN2Dot(Petrinet pn, final DotCluster cluster, final HashSet<DotNode> tDotNodeSet)
	{
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
			else// normal transition, i.e., interfaces
			{	
				tDot = new LocalDotTransition(t.getLabel(), 2);	
			}
			cluster.addNode(tDot);
			tDotNodeSet.add(tDot);
			mapPetrinet2Dot.put(t, tDot);		
		}
		
		//add places
		for (final Place p : pn.getPlaces()) 
		{
			int startFlag=0;
			int endFlag=0;
			//check if the p is start or end. 
			for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges())
			{
				//if there is no incoming arc, this place is a start place
				if(edge.getTarget().getId().equals(p.getId()))
				{
					startFlag=1;
					break;
				}
			}
			
			//check if the p is start or end. 
			for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges())
			{
				//if there is no out-going arc, this place is a end place
				if(edge.getSource().getId().equals(p.getId()))
				{
					endFlag=1;
					break;
				}
			}
			
			//if the place is a start place
			DotNode pDot;
			if(startFlag==0)
			{
				pDot = new LocalDotPlace(0);
			}
			else if (endFlag ==0)
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
