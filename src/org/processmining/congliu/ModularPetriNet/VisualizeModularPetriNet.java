package org.processmining.congliu.ModularPetriNet;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.dot.DotNode;

/**
 * this class is used to visualize modular petri net using graphviz dot
 * @author cliu3
 *
 */
//@Plugin(name = "Graphviz Modular Petri net visualisation", returnLabels = { "Dot visualization" }, returnTypes = { JComponent.class }, parameterLabels = { "Petri net" }, userAccessible = false)
//@Visualizer


public class VisualizeModularPetriNet {
//	@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
//	@PluginVariant(variantLabel = "Visualize Modular Petri net with Graphviz", requiredParameterLabels = { 0 })
//	
//	public JComponent visualize(PluginContext context, ModularPetriNet mpetrinet) {
//		Dot dot = convert(mpetrinet);
//		return new DotPanel(dot);
//	}
//	
	
	public static Dot convert(ModularPetriNet mpetrinet) {
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);
		
		Petrinet pn = mpetrinet.getPn();
		
		HashMap<XEventClass, ComponentNesting> xeventclass2cn = mpetrinet.getXevent2compNest();
		//map transition to componentNesting
		HashMap<Transition, ComponentNesting> transition2componentNesting = transition2component(xeventclass2cn, pn.getTransitions());
		
		//map place to component, we set a place has the same component with its target transition using arc.
		HashMap<Place, String> place2Component = place2component(transition2componentNesting, pn);
		
		//map invisible transition to component
		HashMap<Transition, String> invtransition2component = invisibleTransition2Component(place2Component, pn);
				
		//obtain all components, and create a cluster for each component
		Set<String> componentSet =mpetrinet.getComponentSet();
		
		//map the component name with the cluster object. 
		HashMap<String, DotCluster> component2cluster = new HashMap<String, DotCluster> ();
		for(String componentName: componentSet)
		{
			DotCluster cluster =dot.addCluster();
			//cluster.setLabel(componentName);// there is no label name available, why ???
			cluster.setOption("label", componentName);
			//cluster.setOption("bgcolor","lightseagreen");
			//The color
			cluster.setOption("color", "green");
			
			//cluster.setOption("style", "bold");
			cluster.setOption("fontcolor","green");
			cluster.setOption("fontsize","24");
			// width of the cluster frame
			cluster.setOption("penwidth", "5.0");
			component2cluster.put(componentName, cluster);
		}
				
		
		// the mapping from transition(place) to dotNode
		HashMap<PetrinetNode, DotNode> mapPetrinet2Dot = new HashMap<PetrinetNode, DotNode>();
		
		//add transitions
		for (Transition t : pn.getTransitions()) {
			DotNode tDot;
			if (t.isInvisible()) {
				tDot = new LocalDotTransition();
				//add the invisible transition dot node to a cluster
				component2cluster.get(invtransition2component.get(t)).addNode(tDot);
			} else {
				tDot = new LocalDotTransition(ActivityNameMethod(t.getLabel()));
				// add the visible transition dot node to a cluster
				component2cluster.get(transition2componentNesting.get(t).getComponent()).addNode(tDot);
			}			
			//dot.addNode(tDot);
			mapPetrinet2Dot.put(t, tDot);
		}
		
		//add places
		for (Place p : pn.getPlaces()) {
			DotNode pDot;
			pDot = new LocalDotPlace();
			// add the palce dot node to a cluster
			component2cluster.get(place2Component.get(p)).addNode(pDot);
			//dot.addNode(pDot);
			mapPetrinet2Dot.put(p, pDot);
		}
		
		//add arcs
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges()) {
			if (mapPetrinet2Dot.get(edge.getSource()) != null && mapPetrinet2Dot.get(edge.getTarget()) != null) {
				dot.addEdge(mapPetrinet2Dot.get(edge.getSource()), mapPetrinet2Dot.get(edge.getTarget()));
				
			}
		}
		return dot;
	}
	
	
	//inner class for transition dot
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
	}// inner class for transition dot
	
	//inner class for place dot
	private static class LocalDotPlace extends DotNode {
		public LocalDotPlace() {
			super("", null);
			setOption("shape", "circle");
		}
	}// inner class for place dot
	
	//here the input is the event class, showing the "class+package+method"
	public static String ActivityNameMethod(String input)
	{
		String []parts = input.split("\\+");
			
		if (parts.length>2)
		{
			return parts[2]; 
		}
		else 
		{
			return input;
		}
		//return parts[2]+"."+parts[0]+"."+parts[3]+"()"+parts[1];
	}
	
	//construct the mapping from transition to componentNesting
	public static HashMap<Transition, ComponentNesting> transition2component(HashMap<XEventClass, ComponentNesting> xeventclass2cn, Collection<Transition> transitionSet )
	{
		HashMap<Transition, ComponentNesting> transition2component = new HashMap<Transition, ComponentNesting>();
		for (Transition t : transitionSet) 
		{
			if (!t.isInvisible())
			{
				for(XEventClass xevent: xeventclass2cn.keySet())
				{
					if(xevent.toString().equals(t.getLabel()))
					{
						transition2component.put(t, xeventclass2cn.get(xevent));
						
						break;
					}
				}
				
			}
		}
		return transition2component;
	}// construct the mapping from transition to componentandNesting
	
	//construct the mapping from place to component, using visible transitions and petri net structure
	public static HashMap<Place, String> place2component(HashMap<Transition, ComponentNesting> transition2component, Petrinet pn)
	{
		HashMap<Place, String> place2component = new HashMap<Place, String>();
		for (Place p : pn.getPlaces()) 
		{
			String component = "";
			String preTransition = "";
			String postTransition ="";
			int multiPostLabel = 0;
			int multiPreLable = 0;
			for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges()) 
			{
				if (edge.getSource().equals(p))
				{
					Transition currentTargetT =(Transition) edge.getTarget();
					if(!currentTargetT.isInvisible())
					{
						if (postTransition.equals(""))// get the first postTransition
						{
							postTransition = transition2component.get(currentTargetT).getComponent();
						}
						else// not the first postTransition 
						{
							if (!postTransition.equals(transition2component.get(currentTargetT).getComponent()))
							{
								postTransition = transition2component.get(currentTargetT).getComponent();
								multiPostLabel=1;
							}
						}
					}
				}
				else if (edge.getTarget().equals(p))
				{
					Transition currentSourceT =(Transition) edge.getSource();
					
					if (!currentSourceT.isInvisible())
					{
						if(preTransition.equals(""))//get the first preTransition
						{
							preTransition = transition2component.get(currentSourceT).getComponent();
						}
						else //not the first pre-position
						{
							if (!preTransition.equals(transition2component.get(currentSourceT).getComponent()))
							{
								preTransition = transition2component.get(edge.getSource()).getComponent();
								multiPreLable=1;
							}
						}
					}
				}
			}
			if (preTransition.equals("")&&(!postTransition.equals("")))// source place
			{
				component = postTransition;
			}
			else if (postTransition.equals("")&&(!preTransition.equals("")))// sink place
			{
				component = preTransition;
			}
			else if ((!preTransition.equals(""))&&(!postTransition.equals("")))
			{
				if (multiPreLable!=0)//multiple pre
				{
					component = preTransition;
				}
				else if(multiPostLabel!=0)
				{
					component = postTransition;
				}
				else {
					component = preTransition;
				}
				
			}
			else // for the last case, i.e., the component is still null, those place never connected with visible transition like the loop discovered from inductive miner 
			{
				//obtain the first element of collection transition2component.values().
				ComponentNesting firstCN = transition2component.values().iterator().next();

				component =firstCN.getComponent();
			}
			place2component.put(p, component);
		}
		return place2component;
	}	//construct the mapping from place to component

	//map invisible transition to component
	public static HashMap<Transition, String> invisibleTransition2Component(HashMap<Place, String> place2component, Petrinet pn)
	{
		HashMap<Transition, String> invtransition2component = new HashMap<Transition, String>();
		for (Transition t : pn.getTransitions()) 
		{
			//for all invisible transitions
			if (t.isInvisible())
			{
				String component = "";
				String prePlace = "";
				String postPlace ="";
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges()) 
				{
					if (edge.getSource().equals(t))
					{
						Place currentTargetP =(Place) edge.getTarget();
						postPlace = place2component.get(currentTargetP);
					}
					else if(edge.getTarget().equals(t))
					{
						Place currentSourceP =(Place) edge.getSource();
						prePlace = place2component.get(currentSourceP);
					}
				}
				if ((!prePlace.equals(""))&&(!postPlace.equals("")))
				{
					if (prePlace.equals(postPlace))
					{
						component=postPlace;
					}
					else
					{
						component=prePlace;
					}
				}
				else if((!prePlace.equals("")))
				{
					component=prePlace;
				}
				else if (!postPlace.equals(""))
				{
					component=postPlace;
				}
				else {
					component=prePlace;
				}
				
				invtransition2component.put(t, component);
			}	
		}
		return invtransition2component;
	}//map invisible transition to component
	
}
