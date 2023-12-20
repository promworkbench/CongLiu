package congliu.processmining.objectusage;

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

@Plugin(name = "Visualize Co-occurance Graph", 
		returnLabels = { "Dot visualization" }, 	
		returnTypes = { JComponent.class }, 	
		parameterLabels = { "Co-occurance Graph" }, 	
		userAccessible = false)
@Visualizer
public class VisualizeCooccuranceGraph {

	@UITopiaVariant(affiliation = UITopiaVariant.EHV, author = "Cong", email = "c.liu.3@tue.nl")
	@PluginVariant(variantLabel = "cig", requiredParameterLabels = { 0 })
	public JComponent visualize(PluginContext context, CooccuranceGraph cog) 
	{
		/**
		 * add a slider to set the threshold. on the right panel.
		 */
		Dot dot = convert(cog);
		return new DotPanel(dot);
	}
	
	public static Dot convert(CooccuranceGraph cog) {
		Dot dot = new Dot();
		dot.setDirection(GraphDirection.leftRight);
		
		//prepare the nodes
		HashMap<String, DotNode> activityToNode = new HashMap<String, DotNode>();
		for (String activity : cog.getCooccuranceGraph().vertexSet()) 
		{
			DotNode node = dot.addNode(activity);
			activityToNode.put(activity, node);
			node.setOption("shape", "box");
		}
		
		//prepare the edges
		for (DefaultWeightedEdge edge :cog.getCooccuranceGraph().edgeSet()) 
		{
			String from = cog.getCooccuranceGraph().getEdgeSource(edge);
			String to =  cog.getCooccuranceGraph().getEdgeTarget(edge);
			double weight = cog.getCooccuranceGraph().getEdgeWeight(edge);

			DotNode source = activityToNode.get(from);
			DotNode target = activityToNode.get(to);
			String label = String.valueOf(weight);

			DotEdge dotEdge =dot.addEdge(source, target, label);
			
			//dotEdge.setOption("color", ColourMap.toHexString(ColourMaps.colourMapBlue((long)weight/2, (long)weight)));
			dotEdge.setOption("color", ColourMap.toHexString(ColourMaps.colourMapBlue((long)weight/2, (long)weight)));
			dotEdge.setOption("dir", "none");// without arrow direction
			//dotEdge.setOption("color", ColourMap.toHexString(new ColourMapBlue().colour((long)weight/2, 0, (long)weight)));
			//dotEdge.setOption("penwidth",String.valueOf(weight/5));
		}

		return dot;
	}

}
