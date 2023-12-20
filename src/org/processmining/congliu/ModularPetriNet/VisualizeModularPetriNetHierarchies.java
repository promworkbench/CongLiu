package org.processmining.congliu.ModularPetriNet;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.Set;

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
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

/**
 * this visualizer tries to visualize the modular petri net with hierarchies.  
 * we use JSplitPane to shown the inside information of the nested method. 
 * It supports length-N nesting. 
 * @author cliu3
 *
 */
public class VisualizeModularPetriNetHierarchies {
	private static JPopupMenu popuop;
	
	@Plugin(name = "Visualize Modular Petri Net with Hierarchies", returnLabels = { "Dot visualization" }, returnTypes = { JComponent.class }, parameterLabels = { "MPNH" }, userAccessible = true)
	@Visualizer
	@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
	@PluginVariant(variantLabel = "Display Modular Petri Net with nested Hierarchies", requiredParameterLabels = { 0 })

	public JComponent visualizeTop(PluginContext context, ModularPetrinetHierarchies mpnh) {
		// define the main splitPane for top-level DFGH
		ProMSplitPane dfgSplitPane =new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
		
		return visualize(mpnh, dfgSplitPane, "Top-level Model");
	}
	
	public static ProMSplitPane visualize(ModularPetrinetHierarchies mpnh, ProMSplitPane dfgSplitPaneArg, String currentName)
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
		DotPanel leftDotpanel= new DotPanel(mpnh2Dot(mpnh, rightDotPanel, currentName));
		dfgSplitPane.setLeftComponent(leftDotpanel);
		return dfgSplitPane;
	}
	
	public static Dot mpnh2Dot(final ModularPetrinetHierarchies mpnh, final ProMSplitPane rightDotPanel, String currentName) 
	{
		Dot dot = new Dot();
		//dot.setDirection(GraphDirection.topDown);
		dot.setDirection(GraphDirection.leftRight);
		dot.setOption("label", currentName);
		
		ModularPetriNet mpn = mpnh.getMpn(); 
		
		HashMap<XEventClass, ComponentNesting> xeventclass2cn = mpn.getXevent2compNest();
		
		//map transition to componentNesting
		HashMap<Transition, ComponentNesting> transition2componentNesting = VisualizeModularPetriNet.transition2component(xeventclass2cn, mpn.getPn().getTransitions());
		
		//map place to component, we set a place has the same component with its target transition using arc.
		HashMap<Place, String> place2Component = VisualizeModularPetriNet.place2component(transition2componentNesting, mpn.getPn());
		
		//map invisible transition to component
		HashMap<Transition, String> invtransition2component = VisualizeModularPetriNet.invisibleTransition2Component(place2Component, mpn.getPn());
				
		//obtain all components, and create a cluster for each component
		Set<String> componentSet =mpn.getComponentSet();
		
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
		for (final Transition t : mpn.getPn().getTransitions()) {
			DotNode tDot;
			if (t.isInvisible()) // invisible transition
			{
				tDot = new LocalDotTransition();
				//add the invisible transition dot node to a cluster
				component2cluster.get(invtransition2component.get(t)).addNode(tDot);
			} 
			else// normal transition
			{
				// detect if it is a nested transition
				int nestedFlag=0;
				for(XEventClass xevent: mpnh.getMpn().getXevent2compNest().keySet())
				{
					if(t.getLabel().equals(xevent.toString()))
					{
						if (mpnh.getMpn().getXevent2compNest().get(xevent).getNesting().equals("True"))
						{
							nestedFlag =1;
							break;
						}
					}
						
				}
				// for normal transitions
				if (nestedFlag==0)
				{
					tDot = new LocalDotTransition(VisualizeModularPetriNet.ActivityNameMethod(t.getLabel()), 0);
					// add the visible transition dot node to a cluster
					component2cluster.get(transition2componentNesting.get(t).getComponent()).addNode(tDot);
					
					// add listener to visible transition dot node. 
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
							//e.getLocationOnScreen(); the location of an event
		                    popuop= new JPopupMenu();
		                    popuop.add(VisualizeNestingLengthNDFG.ActivityNamePackageClassMethod(t.getLabel()));
		                    popuop.show(e.getComponent(), e.getX(),e.getY());
						}
						
						public void mouseClicked(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				else //for nested transition
				{
					tDot = new LocalDotTransition(VisualizeModularPetriNet.ActivityNameMethod(t.getLabel()), 1);
					// add the visible transition dot node to a cluster
					component2cluster.get(transition2componentNesting.get(t).getComponent()).addNode(tDot);
					
					//add listener to nested transition dot. 
					tDot.addMouseListener(new MouseListener() {
					// show its inside process when mouse clicked. 
					public void mouseClicked(MouseEvent e) {
						// TODO Auto-generated method stub
						//here, we the left part of Jsplit Panel to add the inside behavior when a nested node is clicked.
						// first we get the corresponding dfge of the current activity (selected nested node). 
						rightDotPanel.setVisible(true);
						// we get the xeventclass correponding with the current transition
						XEventClass currentEventClass=null;
						for(XEventClass event: mpnh.getXEventClass2mpnh().keySet())
						{
							if (event.toString().equals(t.getLabel()))
							{
								currentEventClass =event;
								break;
							}
						}
						ModularPetrinetHierarchies newmpnh= mpnh.getXEventClass2mpnh().get(currentEventClass);
						visualize(newmpnh, rightDotPanel, VisualizeNestingLengthNDFG.ActivityNamePackageClassMethod(t.getLabel()));
						rightDotPanel.repaint();
						}

						public void mousePressed(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}

						public void mouseReleased(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}

						public void mouseEntered(MouseEvent e) {
							// TODO Auto-generated method stub
							//e.getLocationOnScreen(); the location of an event
		                    popuop= new JPopupMenu();
		                    popuop.add(VisualizeNestingLengthNDFG.ActivityNamePackageClassMethod(t.getLabel()));
		                    popuop.show(e.getComponent(), e.getX(),e.getY());
							
						}

						public void mouseExited(MouseEvent e) {
							// TODO Auto-generated method stub
							
						}
					});
				}
				
			}			
			//dot.addNode(tDot);
			mapPetrinet2Dot.put(t, tDot);
		}
		
		//add places
		for (final Place p : mpn.getPn().getPlaces()) {
			DotNode pDot;
			pDot = new LocalDotPlace();
			// add the palce dot node to a cluster
			component2cluster.get(place2Component.get(p)).addNode(pDot);
			
			//add listener to place dot
			pDot.addMouseListener(new MouseListener() {
				
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
                    popuop= new JPopupMenu();
                    popuop.add(p.getLabel());
                    popuop.show(e.getComponent(), e.getX(),e.getY());
				}
				
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}
			});
			
			
			mapPetrinet2Dot.put(p, pDot);
		}
		
		//add popMenu for transitions and 
		
		//add arcs
		for (PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : mpn.getPn().getEdges()) {
			if (mapPetrinet2Dot.get(edge.getSource()) != null && mapPetrinet2Dot.get(edge.getTarget()) != null) {
				dot.addEdge(mapPetrinet2Dot.get(edge.getSource()), mapPetrinet2Dot.get(edge.getTarget()));
				
			}
		}
		
	
		//add the listener part. 
		return dot;
	}
	//inner class for transition dot
		private static class LocalDotTransition extends DotNode {
			//transition flag =0, normal transition flag=1, nested transition
			public LocalDotTransition(String label, int flag) {
				super(label, null);
				if (flag==0)
				{
					
					setOption("shape", "box");
				}
				else//nested transition
				{				
					setOption("shape", "box");
					setOption("style", "filled");
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
	
}
