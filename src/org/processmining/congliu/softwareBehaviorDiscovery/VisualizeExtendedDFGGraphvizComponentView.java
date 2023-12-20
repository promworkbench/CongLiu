package org.processmining.congliu.softwareBehaviorDiscovery;

import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JComponent;

import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.datapetrinets.visualization.graphviz.RatioAwareDotPanel;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotEdge;
import org.processmining.plugins.graphviz.dot.DotNode;

/**
 * this visualizer aims to visualize the directly followed graph from its component view. 
 * @author cliu3
 *
 */
public class VisualizeExtendedDFGGraphvizComponentView {

	@Plugin(name = "Visualize Extended Directly Followed Graph (Component View)", returnLabels = { "Dot visualization" }, returnTypes = { JComponent.class }, parameterLabels = { "Process Tree" }, userAccessible = false)
	@Visualizer
	@UITopiaVariant(affiliation = "TU/e", author = "Cong Liu", email = "c.liu.3@tue.nl OR liucongchina@163.com")	
	@PluginVariant(variantLabel = "Display directly-follows graph with component (component view)", requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, DFGExtendedHierarchies dfgeh) {
		
		return new RatioAwareDotPanel(dfge2DotComponentView(dfgeh.getDfgExtended()));
		//return new DotPanel(dfge2DotComponentView(dfgeh.getDfgExtended()));
	}

	public static Dot dfge2DotComponentView(DFGExtended dfge) 
	{
		//first discover the component view of directly followed graph, different from the traditional directly-followed graph, 
		// here each node correspond to the component, its corresponding dfge is defined as DFGExtendedComponent
		
		DFGExtendedComponent dfgeComponentView = new DFGExtendedComponent(1);
		
		//obtain all component, and create a vertice in dfgeComponentView for each component
		HashSet<String> componentSet =dfge.getComponentSet();
		
//		//map the component name with the cluster object. 
//		HashMap<String, DotCluster> cluster2component = new HashMap<String, DotCluster> ();
		
		// add the component as vertices in the dfgeComponentView
		for(String componentName: componentSet)
		{
			dfgeComponentView.addActivity(componentName);
		}
		
		//add the edge of dfgeComponentView by counting those in dfge
		for (long edge : dfge.getDirectlyFollowsGraph().getEdges()) 
		{
			XEventClassExtended from = dfge.getDirectlyFollowsGraph().getEdgeSource(edge);
			XEventClassExtended to = dfge.getDirectlyFollowsGraph().getEdgeTarget(edge);
			int weight = (int) dfge.getDirectlyFollowsGraph().getEdgeWeight(edge);
			dfgeComponentView.addDirectlyFollowsEdge(from.getComponentName(), to.getComponentName(), weight);
		}
		
		//visualize the component view of dfg, dfgeComponentView
		Dot dot = new Dot();
		//set the direction of dot graph, GraphDirection.topDown;
		dot.setDirection(GraphDirection.topDown);
		
		
		//prepare the nodes                                                                               
		HashMap<String, DotNode> activityToNode = new HashMap<String, DotNode>();
		//add node to dot
		for (final String activity : dfgeComponentView.getDirectlyFollowsGraph().getVertices()) 
		{
			// create a node and use the activity name. 
			DotNode node = dot.addNode(activity);
			//DotNode node =cluster2component.get(activity.getComponentName()).addNode(activity.getXeventclass().toString());
			activityToNode.put(activity, node);
			// this attribute is for all nodes. 
			node.setOption("shape", "box");
		}
		
		//add the directly-follows edges to dot
		for (long edge : dfgeComponentView.getDirectlyFollowsGraph().getEdges()) {
			String from = dfgeComponentView.getDirectlyFollowsGraph().getEdgeSource(edge);
			String to = dfgeComponentView.getDirectlyFollowsGraph().getEdgeTarget(edge);
			int weight = (int) dfgeComponentView.getDirectlyFollowsGraph().getEdgeWeight(edge);
			
			
			DotNode source = activityToNode.get(from);
			DotNode target = activityToNode.get(to);
			String label = String.valueOf(weight);
			final DotEdge dotEdge = new DotEdge(source, target);
			dotEdge.setLabel(label);
			dot.addEdge(dotEdge);
		}
		
		return dot;
	}

}
