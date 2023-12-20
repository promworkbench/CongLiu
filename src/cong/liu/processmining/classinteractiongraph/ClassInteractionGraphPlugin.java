package cong.liu.processmining.classinteractiongraph;

import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import XESSoftwareExtension.XSoftwareExtension;

/**
 * this plug-in is used to discover a class interaction graph, 
 * @author cliu3
 */

@Plugin(
		name = "Class Interaction Graph Discovery",// plugin name
		
		returnLabels = {"Class Interaction Graph"}, //reture labels
		returnTypes = {ClassInteractionGraph.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software event log"},
		
		userAccessible = true,
		help = "This plugin aims to discover the class interaction graph from a software event log." 
		)

public class ClassInteractionGraphPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Construct Class Interaction Graph, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	public ClassInteractionGraph cigDiscovery(UIPluginContext context, XLog softwareLog)
	{
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel("Class Interaction Graph");
		
		// vertex set
		HashSet<String> vertexSet = new HashSet<String>();
		// edge set
		HashMap<Edge, Double> edgeSet = new HashMap<Edge, Double>();
		
		
		//we discover a cig for each trace, and finally merge them together
		for (XTrace trace: softwareLog)
		{
			for(XEvent event:trace)
			{
				if (XSoftwareExtension.instance().extractCallerclass(event).equals("null"))
				{
					vertexSet.add(XSoftwareExtension.instance().extractClass(event));
				}
				else
				{
					vertexSet.add(XSoftwareExtension.instance().extractClass(event));
					vertexSet.add(XSoftwareExtension.instance().extractCallerclass(event));
					Edge e = new Edge(XSoftwareExtension.instance().extractCallerclass(event), XSoftwareExtension.instance().extractClass(event));
					
					// to avoid the same edge appears multiple times in the hash map.
					int flag =0;
					for (Edge ed: edgeSet.keySet())
					{
						if (ed.equals(e))
						{
							edgeSet.put(ed,edgeSet.get(ed)+1.00);
							flag=1;
							break;
						}
					}
					if (flag==0)
					{
						edgeSet.put(e, 1.00);
					}
				}
			}
		}
		
		//create class interaction graph
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> Newg = 
				new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		ClassInteractionGraph cig = new ClassInteractionGraph(Newg);
		
		// add vertexs
		for (String vertex:vertexSet)
		{
			cig.addVertex(vertex);
		}
		
		
		//add edges with weights
		for(Edge ed: edgeSet.keySet())
		{
			DefaultWeightedEdge edge = cig.addEdge(ed.source,ed.target);
			cig.setEdgeWeight(edge, edgeSet.get(ed));
		}
		
		return cig;
	}	
	
	class Edge
	{
		String source;
		String target;
		public Edge(String s, String t)
		{
			source=s;
			target=t;
		}
		public boolean equals(Edge obj) {
			// TODO Auto-generated method stub
			if (source.equals(obj.source)&&target.equals(obj.target))
			{
				return true;
			}
			else 
			{
				return false;
			}
		}
	}
	
}
