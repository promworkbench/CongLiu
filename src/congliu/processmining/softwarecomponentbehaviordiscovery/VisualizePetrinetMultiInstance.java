package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.deckfour.xes.classification.XEventClass;
import org.processmining.congliu.softwareBehaviorDiscoveryLengthN.VisualizeNestingLengthNDFG;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
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
 * this visualizer aims to visualize hierarchical petri net with multi-instances
 * @author cliu3
 *
 */
public class VisualizePetrinetMultiInstance {
	
	private static JPopupMenu popuop;
	
	@Plugin(name = "Improved Visualize Hierarchical Petri Net with Multi-instances", 
			returnLabels = { "Dot visualization" }, 
			returnTypes = { JComponent.class }, 
			parameterLabels = { "HPNMI" }, 
			userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
	@PluginVariant(variantLabel = "Visualize Hierarchical Petri Net with Multi-Instances", 
					requiredParameterLabels = {0})// it needs one input parameter

	public JComponent visualizeTop(PluginContext context, HierarchicalPetrinetMultiInstances hpnmi) {
		// define the main splitPane for top-level HPNMI
		ProMSplitPane dfgSplitPane =new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
		
		return visualize(hpnmi, dfgSplitPane, "Top-level Model");
	}

	// the main function of the visualizer
	public static ProMSplitPane visualize(HierarchicalPetrinetMultiInstances hpnmi, 
			ProMSplitPane dfgSplitPaneArg, String currentName)
	{
		ProMSplitPane dfgSplitPane = dfgSplitPaneArg;
		//dfgSplitPane.setDividerLocation(0.8);
		dfgSplitPane.setResizeWeight(0.8);
		
		//set the right panel be a splitPanel. 
		ProMSplitPane rightDotPanel = new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
		//do not show too much ProMsplitPanel
		rightDotPanel.setVisible(false);
		dfgSplitPane.setRightComponent(rightDotPanel);
		
		// set the left panel be a DotPanel contain the dfgMain. 
		DotPanel leftDotpanel= new DotPanel(hpnmi2Dot(hpnmi, rightDotPanel, currentName));
		dfgSplitPane.setLeftComponent(leftDotpanel);
		return dfgSplitPane;
	}
	
	public static Dot hpnmi2Dot(final HierarchicalPetrinetMultiInstances hpnmi, 
			final ProMSplitPane rightDotPanel, String currentName) 
	{
		Dot dot = new Dot();
		
		//set the edge from node to cluster
		dot.setOption("compound", "true");
		dot.setDirection(GraphDirection.topDown);
		//dot.setDirection(GraphDirection.leftRight);
		dot.setOption("label", currentName);
	
		// start and end transition of the block
		DotNode startTransitionDot =null;
		DotNode endTransitionDot =null;
		
		//get the nested event to hpnmi mapping part, the keyset is a set of transitions
		final HashMap<XEventClass, HierarchicalPetrinetMultiInstances> XEventClass2hpnmi =hpnmi.getXEventClass2hpnmi();
		
		//get the pnmi part
		PetrinetMultiInstances pnmi=hpnmi.getPnmi();
		
		// the petri net part with multi-instance block
		Petrinet pn = pnmi.getPn();
		
		// the block to sub-net mapping. a block is a transition in the main petri net. 
		HashMap<XEventClass, Petrinet> block2subnet= pnmi.getBlock2subnet();
		
		// construct the mapping from block transition to sub-net, if there exist block transitions
		HashMap<Transition, Petrinet> blockTransition2petrinet = new HashMap<Transition, Petrinet>();
		if (block2subnet.keySet().size()>0)
		{
			for (Transition t: pn.getTransitions()) 
			{
				for (XEventClass blockEventClass: block2subnet.keySet())
				{
					System.out.println("output block information"+blockEventClass);
					if(blockEventClass.toString().equals(t.getLabel())) // non pointer exception.......
					{
						blockTransition2petrinet.put(t, block2subnet.get(blockEventClass));
					}
				}
			}
		}

		
		// for each block (if there exist at least one) we create a component, 
		// to be continued...
		
		
		
		
		// the mapping from transition(place) to dotNode
		HashMap<PetrinetNode, DotNode> mapPetrinet2Dot = new HashMap<PetrinetNode, DotNode>();
		
		// start and end place of the block
		DotNode startPlaceDot= null;
		DotNode endPlaceDot= null;
		
		DotCluster cluster = null;

		//add transitions
		for (final Transition t : pn.getTransitions()) 
		{
			DotNode tDot;
//			int blockFlag=0;// to denote if the transition is a block transition
			
			//first check if it belong to a block transition
			if(blockTransition2petrinet.keySet().contains(t))
			{
				Petrinet blockPetriNet =blockTransition2petrinet.get(t);
				
				//visualize the subnet to dot as a cluster
				// adding start and end special transitions
				cluster =dot.addCluster();
				//cluster.setLabel(componentName);// there is no label name available, why ???
				cluster.setOption("label", "");
				//cluster.setOption("bgcolor","lightseagreen");
				//cluster.setOption("color", "lightseagreen");// grey15, http://www.graphviz.org/doc/info/colors.html
				//cluster.setOption("fontcolor","lightseagreen");
				cluster.setOption("fontsize","18");
				// width of the cluster frame
				cluster.setOption("penwidth", "2.0");
				//cluster.setOption("style", "dashed");
				
				// the mapping from transition(place) to dotNode
				HashMap<PetrinetNode, DotNode> mapBlocknet2Dot = new HashMap<PetrinetNode, DotNode>();
				
				//add transitions to the cluster 
				for (final Transition sub_t : blockPetriNet.getTransitions())
				{
					DotNode subt_Dot;
									
					// invisable
					if (sub_t.isInvisible()) // invisible transition
					{
						subt_Dot = new LocalDotTransition();
					} 
					else//nested 
					{
						// detect if it is a nested transition
						int nestedFlag=0;
						for(XEventClass xevent: XEventClass2hpnmi.keySet())
						{
							if(sub_t.getLabel().equals(xevent.toString()))
							{
								nestedFlag =1;
								break;
							}	
						}
						// for normal transitions
						if (nestedFlag==0)
						{
							subt_Dot = new LocalDotTransition(VisualizeNestingLengthNDFG.ActivityNamePackageClassMethod(sub_t.getLabel()), 0);		
						}//if (nestedFlag==0)
						else// nested transitions
						{
							subt_Dot = new LocalDotTransition(VisualizeNestingLengthNDFG.ActivityNamePackageClassMethod(sub_t.getLabel()), 1);
							//add listener...
							subt_Dot.addMouseListener(new MouseListener() {
								
								public void mouseReleased(MouseEvent e) {
									// TODO Auto-generated method stub
									
								}
								
								public void mousePressed(MouseEvent e) {
									// TODO Auto-generated method stub
									
								}
								
								public void mouseExited(MouseEvent e) {
									// TODO Auto-generated method stub
									
								}
								
								public void mouseEntered(MouseEvent e) {
									// TODO Auto-generated method stub
									
								}
								
								public void mouseClicked(MouseEvent e) {
									// TODO Auto-generated method stub
									rightDotPanel.setVisible(true);
									// we get the xeventclass correponding with the current transition
									XEventClass currentEventClass=null;
									for(XEventClass eventclass: XEventClass2hpnmi.keySet())
									{
										if (eventclass.toString().equals(sub_t.getLabel()))
										{
											currentEventClass =eventclass;
											break;
										}
									}
									HierarchicalPetrinetMultiInstances newhpnmi= XEventClass2hpnmi.get(currentEventClass);
									visualize(newhpnmi, rightDotPanel, VisualizeNestingLengthNDFG.ActivityNamePackageClassMethod(sub_t.getLabel()));
									rightDotPanel.repaint();
									}
								}
							);
						}
					}
					
					mapBlocknet2Dot.put(sub_t, subt_Dot);
					cluster.addNode(subt_Dot);
				}	
				
				//add places to the cluster 
				for (final Place sub_p : blockPetriNet.getPlaces())
				{
					System.out.println("place name"+sub_p.getLabel());
					DotNode sub_pDot;
					sub_pDot = new LocalDotPlace();
					mapBlocknet2Dot.put(sub_p, sub_pDot);
					cluster.addNode(sub_pDot);
				}
				
				// add arcs
				for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : blockPetriNet.getEdges()) 
				{
					if (mapBlocknet2Dot.get(edge.getSource()) != null && mapBlocknet2Dot.get(edge.getTarget()) != null) 
					{
						LocalDotEdge tempEdge = new LocalDotEdge(mapBlocknet2Dot.get(edge.getSource()), mapBlocknet2Dot.get(edge.getTarget()), 1);
						dot.addEdge(tempEdge);
					}
				}
				
				
				//two special transitions, get source place, get sink place
				startTransitionDot = new LocalDotTransition("", 2);		
				endTransitionDot = new LocalDotTransition("", 2);
				cluster.addNode(startTransitionDot);
				cluster.addNode(endTransitionDot);
				
				//get the source and sink place node
				for (final Place sub_p : blockPetriNet.getPlaces())
				{
					int sourceFlag =0;
					int sinkFlag=0;
					for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : blockPetriNet.getEdges()) 
					{
						if (edge.getSource().equals(sub_p))
						{
							sourceFlag=1;
						}
						else if (edge.getTarget().equals(sub_p))
						{
							sinkFlag=1;
						}
						else {
							System.out.println("not connected");
						}
					}
					if (sourceFlag ==0 && sinkFlag==1)
					{
						endPlaceDot =mapBlocknet2Dot.get(sub_p);
					}
					else if (sourceFlag ==1 && sinkFlag==0)
					{
						startPlaceDot =mapBlocknet2Dot.get(sub_p);
					}
				}
				
				// connect source and sink places, we need to set the style......
				LocalDotEdge startEdge = new LocalDotEdge(startTransitionDot,startPlaceDot, 2);
				dot.addEdge(startEdge);
				
				LocalDotEdge endEdge = new LocalDotEdge(endPlaceDot,endTransitionDot, 2);
				dot.addEdge(endEdge);
				/*
				 * Doubled edges can be done by specifying more than one colour for the edge, 
				 * separated by a colon. In this case use the same colour twice: [color="black:black"] 
				 * (or, to separate them slightly more, do [color="black:invis:black"])
				 */
			}
			
			
			
			// if not block transition, then handle invisible nesting separately
			else if (t.isInvisible()) // invisible transition
			{
				tDot = new LocalDotTransition();
				dot.addNode(tDot);
				mapPetrinet2Dot.put(t, tDot);

				//System.out.println("label of invisible:" +t.getLabel());
			} 
			else// normal transition
			{				
				// detect if it is a nested transition
				int nestedFlag=0;
				for(XEventClass xevent: XEventClass2hpnmi.keySet())
				{
					if(t.getLabel().equals(xevent.toString()))
					{
						nestedFlag =1;
						break;
					}	
				}
				
				// for normal transitions
				if (nestedFlag==0)
				{
					tDot = new LocalDotTransition(VisualizeNestingLengthNDFG.ActivityNameMethod(t.getLabel()), 0);		
					dot.addNode(tDot);
					mapPetrinet2Dot.put(t, tDot);
				}//if (nestedFlag==0)
				else// nested transitions
				{
					tDot = new LocalDotTransition(VisualizeNestingLengthNDFG.ActivityNameMethod(t.getLabel()), 1);
					dot.addNode(tDot);
					mapPetrinet2Dot.put(t, tDot);
					//add listener...
					
					tDot.addMouseListener(new MouseListener() {
						
						public void mouseReleased(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}
						
						public void mousePressed(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}
						
						public void mouseExited(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}
						
						public void mouseEntered(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}
						
						public void mouseClicked(MouseEvent e) {
							// TODO Auto-generated method stub
							rightDotPanel.setVisible(true);
							// we get the xeventclass correponding with the current transition
							XEventClass currentEventClass=null;
							for(XEventClass eventclass: XEventClass2hpnmi.keySet())
							{
								if (eventclass.toString().equals(t.getLabel()))
								{
									currentEventClass =eventclass;
									break;
								}
							}
							HierarchicalPetrinetMultiInstances newhpnmi= XEventClass2hpnmi.get(currentEventClass);
							visualize(newhpnmi, rightDotPanel, VisualizeNestingLengthNDFG.ActivityNameMethod(t.getLabel()));
							rightDotPanel.repaint();
							}
						}
					);
				}

			}
			
//			we change to add them separately
//			dot.addNode(tDot);
//			mapPetrinet2Dot.put(t, tDot);

			
		}// add transitions...
		
		
		//add places
		for (final Place p : pn.getPlaces()) 
		{
			DotNode pDot;
			pDot = new LocalDotPlace();
			dot.addNode(pDot);
			mapPetrinet2Dot.put(p, pDot);
		}
		
		
		//add arcs of the main model
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges()) 
		{
			// if the target of this arcs is block 
			if (blockTransition2petrinet.keySet().contains(edge.getTarget()))
			{
				LocalDotEdge tempEdge = new LocalDotEdge(mapPetrinet2Dot.get(edge.getSource()), startTransitionDot, 1);
				// connect the cluster
				tempEdge.setOption("lhead", cluster.getId());
				dot.addEdge(tempEdge);
			}
			// if the source of this arcs is block
			else if (blockTransition2petrinet.keySet().contains(edge.getSource()))
			{
				LocalDotEdge tempEdge = new LocalDotEdge(endTransitionDot, mapPetrinet2Dot.get(edge.getTarget()), 1);
				tempEdge.setOption("ltail", cluster.getId());
				dot.addEdge(tempEdge);
			}
			
			if (mapPetrinet2Dot.get(edge.getSource()) != null && mapPetrinet2Dot.get(edge.getTarget()) != null) 
			{
				LocalDotEdge tempEdge = new LocalDotEdge(mapPetrinet2Dot.get(edge.getSource()), mapPetrinet2Dot.get(edge.getTarget()), 1);
				dot.addEdge(tempEdge);
			}
		}
		
		return dot;
	}// hpnmi2Dot() function
	
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
				//setOption("color", "gray60");
				setOption("peripheries","2");//double line
				setOption("style", "filled");
				
				//setOption("color", "aliceblue");
			}
			else // multi-instance start and complete transition
			{
				setOption("shape", "box");
				setOption("style", "filled");
				setOption("fillcolor", "black");
				//setOption("color", "lightseagreen");
				setOption("width", "0.55");
				setOption("height", "0.25");
				setOption("peripheries","2");//double line
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
