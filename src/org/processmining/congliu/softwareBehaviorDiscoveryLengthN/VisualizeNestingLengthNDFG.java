package org.processmining.congliu.softwareBehaviorDiscoveryLengthN;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.processmining.congliu.softwareBehaviorDiscovery.XEventClassExtended;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.util.ui.widgets.ProMSplitPane;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.dot.DotEdge;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

/**
 * this visualizer tries to visualize the extended directly followed graph from its method view. 
 * we use JSplitPane to shown the inside information of the nested method. 
 * It supports length-N nesting. 
 * @author cliu3
 *
 */

public class VisualizeNestingLengthNDFG {	
	private static JPopupMenu popuop;
	
	@Plugin(name = "Visualize Length-N Nested DFG, New", returnLabels = { "Dot visualization" }, returnTypes = { JComponent.class }, parameterLabels = { "DFG" }, userAccessible = false)
	@Visualizer
	@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
	@PluginVariant(variantLabel = "Display directly-follows graph with component, and show nested method information", requiredParameterLabels = { 0 })
	public JComponent visualizeTop(PluginContext context, DFGExtendedHierarchies dfgeh) {
		// define the main splitPane for top-level DFGH
		ProMSplitPane dfgSplitPane =new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);
		
		return visualize(dfgeh, dfgSplitPane, "Top-level Model");

	}
	
	public static ProMSplitPane visualize(DFGExtendedHierarchies dfgeh, ProMSplitPane dfgSplitPaneArg, String currentName)
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
		DotPanel leftDotpanel= new DotPanel(dfge2Dot(dfgeh, rightDotPanel, currentName));
		dfgSplitPane.setLeftComponent(leftDotpanel);
		
		return dfgSplitPane;
	}
	
	public static Dot dfge2Dot(final DFGExtendedHierarchies dfgeh, final ProMSplitPane rightDotPanel, String currentName) 
	{
	
		//System.out.println("$$$$$$$$$$$$$$$$$$$ [in the dot]");
		Dot dot = new Dot();
		//set the direction of dot graph, GraphDirection.topDown;
		dot.setDirection(GraphDirection.topDown);
		dot.setOption("label", currentName);
		
		//obtain all component, and create a cluster for each component
		HashSet<String> componentSet =dfgeh.getDfgExtended().getComponentSet();
		//map the component name with the cluster object. 
		HashMap<String, DotCluster> cluster2component = new HashMap<String, DotCluster> ();
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
			cluster2component.put(componentName, cluster);
		}
		
		
		//prepare the nodes
		HashMap<XEventClassExtended, DotNode> activityToNode = new HashMap<XEventClassExtended, DotNode>();
		for (final XEventClassExtended activity: dfgeh.getDfgExtended().getDirectlyFollowsGraph().getVertices()) 
		{
			//System.out.println(activity.getXeventclass().toString());
			
			// create a node and use the event class as its label. 
			DotNode node = dot.addNode(ActivityNameMethod(activity.getXeventclass().toString()));
			
			 //add mouse listener for each node, each time a mouse enter a node, then show its full name, i.e., package.class.method
			node.addMouseListener(new MouseListener() {
				public void mouseEntered(java.awt.event.MouseEvent e) {
					// TODO Auto-generated method stub
					// show the full name as text box
                    //System.out.println(activity.getXeventclass().toString());				
					
					//e.getLocationOnScreen(); the location of an event
                    popuop= new JPopupMenu();
                    popuop.add(ActivityNamePackageClassMethod(activity.getXeventclass().toString()));
                    popuop.show(e.getComponent(), e.getX(),e.getY());
				}

				@SuppressWarnings("deprecation")
				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
//					new JDialog().show(); 
				}

				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub
					popuop.setVisible(false);
				}

			});
			
			//add the created node to a cluster according to its component name
			cluster2component.get(activity.getComponentName()).addNode(node);
			
			//DotNode node =cluster2component.get(activity.getComponentName()).addNode(activity.getXeventclass().toString());
			activityToNode.put(activity, node);
			//node.addMouseListener(l); add mouse event, 
			
			
			//highlight the nested activities
			if(activity.getNestedFlag().toString().equals("True"))
			{
				//set the border to be thicker
				node.setOption("style", "filled");
				node.setOption("shape", "doubleoctagon");
				
				//add mouse listener for each nested node, each time a mouse clicked a node, then show its sub dfg full name, i.e., package.class.method
				node.addMouseListener(new MouseListener() {

					// show its inside process when mouse clicked. 
					public void mouseClicked(MouseEvent e) {
						// TODO Auto-generated method stub
						//here, we the left part of Jsplit Panel to add the inside behavior when a nested node is clicked.
						//System.out.println("mouse clicked"+activity.getXeventclass().toString());
						
						// first we get the corresponding dfge of the current activity (selected nested node). 
						rightDotPanel.setVisible(true);
						DFGExtendedHierarchies newDFGEHie= dfgeh.getXEventClass2DFGH().get(activity.getXeventclass());
						visualize(newDFGEHie, rightDotPanel, ActivityNamePackageClassMethod(activity.getXeventclass().toString()));
						rightDotPanel.repaint();
//						//obtain the mapping from eventclass to dfg of sub process. 
//						HashMap<XEventClass, DFGExtendedHierarchies> eventclass2dfg = dfgeh.getXEventClass2DFGH();
						//set the right panel be a splitPanel. 
						//ProMSplitPane sub_rightDotPanel = new ProMSplitPane(ProMSplitPane.HORIZONTAL_SPLIT);

								
						// set the left panel be a DotPanel contain the dfgMain. 
//						DotPanel Sub_leftDotpanel= new DotPanel(dfge2Dot(dfgeh, sub_rightDotPanel));
//						rightDotPanel.setLeftComponent(Sub_leftDotpanel);
//						Dot dot = new Dot();
//						//dot.addNode(activity.getXeventclass().toString());
//						dot = dfge2Dot(eventclass2dfg.get(activity.getXeventclass()));
//						dot4subDfg =dfge2Dot(eventclass2dfg.get(activity.getXeventclass()).getDfgExtended());;
//						dot4subDfg.setOption("label", ActivityNamePackageClassMethod(activity.getXeventclass().toString()));
//						rightDotPanel.changeDot(dot4subDfg, true);
					}

					public void mousePressed(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void mouseReleased(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void mouseEntered(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}

					public void mouseExited(MouseEvent e) {
						// TODO Auto-generated method stub
						
					}
				});
			}
			// this attribute is for all nodes. 
			node.setOption("shape", "box");
		}
		//add the directly-follows edges
		for (long edge : dfgeh.getDfgExtended().getDirectlyFollowsGraph().getEdges()) {
			XEventClassExtended from = dfgeh.getDfgExtended().getDirectlyFollowsGraph().getEdgeSource(edge);
			XEventClassExtended to = dfgeh.getDfgExtended().getDirectlyFollowsGraph().getEdgeTarget(edge);
			int weight = (int) dfgeh.getDfgExtended().getDirectlyFollowsGraph().getEdgeWeight(edge);

			//System.out.println(from.getXeventclass()+"-->"+to.getXeventclass()+weight);
			
			DotNode source = activityToNode.get(from);
			DotNode target = activityToNode.get(to);
			String labelCount = String.valueOf(weight);
//			String labelType = "";
//			// from =to and they are not XES Library
//			if ((from.getComponentName().toString().equals(to.getComponentName().toString()))&& 
//					(from.getComponentName().toString().equals("XES Library")))
//			{
//				labelType="Internal Call";
//			}
//			else if((!from.getComponentName().toString().equals(to.getComponentName().toString()))&&(true))
//			{
//				labelType="Inter Call";
//			}
//			else {
//				labelType ="Intra Call";
//			}
			
			final DotEdge dotEdge = new DotEdge(source, target);
			dotEdge.setLabel(labelCount);
			dot.addEdge(dotEdge);
			dotEdge.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					//here, we plan to highlight the current selected edge, how to do this???
					//System.out.println("edge selected:"+dotEdge.getLabel());
                    popuop= new JPopupMenu();
                    popuop.add(dotEdge.getLabel());
                    popuop.show(e.getComponent(), e.getX(),e.getY());
//					dotEdge.setOption("color", "green");
				}

				public void mousePressed(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void mouseReleased(MouseEvent e) {
					// TODO Auto-generated method stub
					
				}

				public void mouseEntered(MouseEvent e) {
					// TODO Auto-generated method stub
//					dotEdge.setOption("color", "green");
//					dotEdge.setOption("penwidth", "2.0");
				}

				public void mouseExited(MouseEvent e) {
					// TODO Auto-generated method stub

				}
			});;
			//setOption("color", "blue");
		}
		
		return dot;
	}
	
	//here the input is the event class, showing the "class+package+method"
	public static String ActivityNamePackageClassMethod(String input)
	{
		
		String []parts = input.split("\\+");
		if (parts.length>2)
		{
			return parts[1]+"."+parts[0]+"."+parts[2]; 
		}
		else 
		{
			return input;
		}
		
	}

	//here the input is the event class, showing the "class+package+method"
	public static String ActivityNameMethod(String input)
	{
		String []parts = input.split("\\+");
		return parts[2]; 
	}
}
