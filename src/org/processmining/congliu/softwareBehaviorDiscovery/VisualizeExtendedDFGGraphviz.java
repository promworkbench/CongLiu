package org.processmining.congliu.softwareBehaviorDiscovery;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JComponent;
import javax.swing.JPopupMenu;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotCluster;
import org.processmining.plugins.graphviz.dot.DotEdge;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

public class VisualizeExtendedDFGGraphviz {
	private static JPopupMenu popuop;

	@Plugin(name = "Visualize Extended Directly Followed Graph with Component", returnLabels = { "Dot visualization" }, returnTypes = { JComponent.class }, parameterLabels = { "Process Tree" }, userAccessible = false)
	@Visualizer
	@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
	@PluginVariant(variantLabel = "Display directly-follows graph with component", requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, DFGExtended dfge) {

//		//we only visualize 60 nodes at most
//		if (dfge.getDirectlyFollowsGraph().getVertices().length > 100) {
//			return new JPanel();
//		}

//		JSplitPane splitPane = new JSplitPane();
//		splitPane.setDividerLocation(0.8);
//		//http://zxc8899.iteye.com/blog/1556094
		return new DotPanel(dfge2Dot(dfge));
	}
	
	
	public static Dot dfge2Dot(DFGExtended dfge) 
	{
//		Dot dot = new Dot();
//        DotNode node1 = dot.addNode("node");
//        DotCluster cluster = dot.addCluster();
//        cluster.setLabel("cluster name");
//        DotNode node2 =cluster.addNode("node in cluster");
//        cluster.addNode(node1);
//        //cluster.addEdge(node1, node2);
//        dot.addEdge(node1, node2);
		
		//System.out.println("$$$$$$$$$$$$$$$$$$$ [in the dot]");
		Dot dot = new Dot();
		//set the direction of dot graph, GraphDirection.topDown;
		dot.setDirection(GraphDirection.topDown);
		
		
		//obtain all component, and create a cluster for each component
		HashSet<String> componentSet =dfge.getComponentSet();
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
		for (final XEventClassExtended activity : dfge.getDirectlyFollowsGraph().getVertices()) 
		{
			//System.out.println(activity.getXeventclass().toString());
			
			// create a node and use the event class as its label. 
			DotNode node = dot.addNode(activity.getXeventclass().toString().split("\\+")[2]);
			 //add mouse listener for each node, each time a mouse enter a node, then show its full name, i.e., package.class.method
			node.addMouseListener(new MouseListener() {
				public void mouseEntered(java.awt.event.MouseEvent e) {
					// TODO Auto-generated method stub
					// show the full name as text box
                    //System.out.println(activity.getXeventclass().toString());				
					
					//e.getLocationOnScreen(); the location of an event
                    popuop= new JPopupMenu();
                    popuop.add(newActivityName(activity.getXeventclass().toString()));
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
						//here, we plan to use the Jsplit Panel to add the inside behavior on the right part of the panel is an nested node is clicked.
						System.out.println("mouse clicked"+activity.getXeventclass().toString());			
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
		for (long edge : dfge.getDirectlyFollowsGraph().getEdges()) {
			XEventClassExtended from = dfge.getDirectlyFollowsGraph().getEdgeSource(edge);
			XEventClassExtended to = dfge.getDirectlyFollowsGraph().getEdgeTarget(edge);
			int weight = (int) dfge.getDirectlyFollowsGraph().getEdgeWeight(edge);

			//System.out.println(from.getXeventclass()+"-->"+to.getXeventclass()+weight);
			
			DotNode source = activityToNode.get(from);
			DotNode target = activityToNode.get(to);
			String label = String.valueOf(weight);
			final DotEdge dotEdge = new DotEdge(source, target);
			dotEdge.setLabel(label);
			dot.addEdge(dotEdge);
			dotEdge.addMouseListener(new MouseListener() {

				public void mouseClicked(MouseEvent e) {
					// TODO Auto-generated method stub
					//here, we plan to highlight the current selected edge, how to do this???
					System.out.println("edge selected:"+dotEdge.getLabel());		
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
	public static String newActivityName(String input)
	{
		String []parts = input.split("\\+");
		return parts[1]+"."+parts[0]+"."+parts[2]; 
	}
}
