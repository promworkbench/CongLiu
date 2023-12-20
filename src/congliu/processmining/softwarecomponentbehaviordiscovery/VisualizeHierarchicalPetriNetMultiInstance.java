package congliu.processmining.softwarecomponentbehaviordiscovery;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.deckfour.xes.classification.XEventClass;
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
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

/**
 * this visualizer aims to visualize hierarchical petri net with multi-instances
 * @author cliu3
 *
 */
public class VisualizeHierarchicalPetriNetMultiInstance {
	
	private static JPopupMenu popuop;
	
	@Plugin(name = "Visualize Hierarchical Petri Net with Multi-instances", 
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
		dot.setDirection(GraphDirection.topDown);
		//dot.setDirection(GraphDirection.leftRight);
		dot.setOption("label", currentName);
	
		
		//get the nested event to hpnmi mapping part, the keyset is a set of transitions
		final HashMap<XEventClass, HierarchicalPetrinetMultiInstances> XEventClass2hpnmi =hpnmi.getXEventClass2hpnmi();
		
		//get the pnmi part
		PetrinetMultiInstances pnmi=hpnmi.getPnmi();
		
		// the petri net part with multi-instance block
		Petrinet pn = pnmi.getPn();
		
		// the block to sub-net mapping. a block is a transition in the main petri net. 
		HashMap<XEventClass, Petrinet> block2subnet= pnmi.getBlock2subnet();
		
//		// construct the mapping from block transition to sub-net, if there exist block transitions
//		HashMap<Transition, Petrinet> blockTransition2petrinet = new HashMap<Transition, Petrinet>();
//		if (block2subnet.keySet().size()>0)
//		{
//			for (Transition t: pn.getTransitions()) 
//			{
//				for (XEventClass blockEventClass: block2subnet.keySet())
//				{
//					if(blockEventClass.toString().equals(t.getLabel())) // non pointer exception.......
//					{
//						blockTransition2petrinet.put(t, block2subnet.get(blockEventClass));
//					}
//				}
//			}
//		}

		
		// for each block (if there exist at least one) we create a component, 
		// to be continued...
		
		
		
		
		// the mapping from transition(place) to dotNode
		HashMap<PetrinetNode, DotNode> mapPetrinet2Dot = new HashMap<PetrinetNode, DotNode>();
		
		//add transitions
		for (final Transition t : pn.getTransitions()) 
		{
			DotNode tDot;
//			int blockFlag=0;// to denote if the transition is a block transition
			
			if (t.isInvisible()) // invisible transition
			{
				tDot = new LocalDotTransition();

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
					tDot = new LocalDotTransition(t.getLabel(), 0);
					
//					//check if this net has multi-instance block
//					if (pnmi.getBlock2subnet().keySet().size()==0)
//					{
//						tDot = new LocalDotTransition(t.getLabel(), 0);
//						dot.addNode(tDot);
//						mapPetrinet2Dot.put(t, tDot);
//					}
//					else //there exist a block
//					{
//						//check if the current transition is the block transition
//						Petrinet subPN =null;
//						for(XEventClass eventclass: pnmi.getBlock2subnet().keySet())
//						{
//							if (eventclass.toString().equals(t.getLabel()))
//							{
//								subPN =pnmi.getBlock2subnet().get(eventclass);
//								break;
//							}
//						}
//						
//						//if there exist a sub-net(multi-instance block)
//						if (subPN!=null)
//						{
//							blockFlag=1;
//							//create a cluster and add it to dot.
//							DotCluster cluster =dot.addCluster();
//							cluster.setOption("label", t.getLabel());
//							cluster.setOption("color", "green");
//							cluster.setOption("fontcolor","green");
//							cluster.setOption("fontsize","24");
//							cluster.setOption("penwidth", "5.0");
//							
//							// add the transition, places, arcs to the cluster
//							
//							// create two special transitions and connected to the start and end place using special arcs.
//						}
//					}					
				}//if (nestedFlag==0)
				else// nested transitions
				{
					tDot = new LocalDotTransition(t.getLabel(), 1);

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
							visualize(newhpnmi, rightDotPanel, t.getLabel());
							rightDotPanel.repaint();
							}
						}
					);
				}

			}
			
			dot.addNode(tDot);
			mapPetrinet2Dot.put(t, tDot);
//			
//			if (blockFlag==0)
//			{
//				mapPetrinet2Dot.put(t, tDot);
//			}
			
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
				setOption("style", "filled");
				setOption("peripheries","2");//double line
			}
			else // multi-instance
			{
				setOption("shape", "box");
				setOption("shape", "box");
				setOption("fillcolor", "green");
				setOption("width", "0.45");
				setOption("height", "0.15");
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
}
