package cong.liu.processmining.classinteractiongraph;

import java.util.HashMap;

import javax.swing.JComponent;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.contexts.uitopia.annotations.Visualizer;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.plugins.graphviz.colourMaps.ColourMap;
import org.processmining.plugins.graphviz.colourMaps.ColourMaps;
import org.processmining.plugins.graphviz.dot.Dot;
import org.processmining.plugins.graphviz.dot.Dot.GraphDirection;
import org.processmining.plugins.graphviz.dot.DotEdge;
import org.processmining.plugins.graphviz.dot.DotNode;
import org.processmining.plugins.graphviz.visualisation.DotPanel;

@Plugin(name = "Visualize Class Interaction Graph", returnLabels = { "Dot visualization" }, returnTypes = { JComponent.class }, parameterLabels = { "Petri net" }, userAccessible = true)
@Visualizer
public class VisualizeClassInteractionGraphPlugin {
	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Cong", email = "c.liu.3@tue.nl")
	@PluginVariant(variantLabel = "cig", requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, ClassInteractionGraph cig) 
	{
		Dot dot = convert(cig);
		return new DotPanel(dot);
	}
	
	public static Dot convert(ClassInteractionGraph cig) {
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);
		
		//prepare the nodes
		HashMap<String, DotNode> activityToNode = new HashMap<String, DotNode>();
		for (String activity : cig.getClassInteractionGraph().vertexSet()) 
		{
			DotNode node = dot.addNode(activity);
			activityToNode.put(activity, node);
			node.setOption("shape", "box");
		}
		
		//prepare the edges
		for (DefaultWeightedEdge edge :cig.getClassInteractionGraph().edgeSet()) 
		{
			String from = cig.getClassInteractionGraph().getEdgeSource(edge);
			String to =  cig.getClassInteractionGraph().getEdgeTarget(edge);
			double weight = cig.getClassInteractionGraph().getEdgeWeight(edge);

			DotNode source = activityToNode.get(from);
			DotNode target = activityToNode.get(to);
			String label = String.valueOf(weight);

			DotEdge dotEdge =dot.addEdge(source, target, label);
			
			//dotEdge.setOption("color", ColourMap.toHexString(ColourMaps.colourMapBlue((long)weight/2, (long)weight)));
			dotEdge.setOption("color", ColourMap.toHexString(ColourMaps.colourMapBlue((long)weight/2, (long)weight)));
			//dotEdge.setOption("color", ColourMap.toHexString(new ColourMapBlue().colour((long)weight/2, 0, (long)weight)));
			//dotEdge.setOption("penwidth",String.valueOf(weight/5));
		}

		return dot;
	}
}
