package congliu.processmining.petrinet2processgraph;

import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.models.graphbased.directed.petrinet.Petrinet;
import org.processmining.models.graphbased.directed.petrinet.PetrinetEdge;
import org.processmining.models.graphbased.directed.petrinet.PetrinetNode;
import org.processmining.models.graphbased.directed.petrinet.elements.Place;
import org.processmining.models.graphbased.directed.petrinet.elements.Transition;

/**
 * this plug-in is used to transform a petri net to a business process graph, 
 * @author cliu3
 */

@Plugin(
		name = "Transform a Petri net to a Business Process Graph",// plugin name
		
		returnLabels = {"Business Process Graph"}, //reture labels
		returnTypes = {BusinessProcessGraph.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Petri net"},
		
		userAccessible = true,
		help = "This plugin aims to transform a Petri net to a business process graph." 
		)

public class PetrinetToBusinessProcessGraphPlugin {
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
	public BusinessProcessGraph BusinessProcessGraphTransformation(UIPluginContext context, Petrinet pn)
	{
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel("Business Process Graph");
		
		// vertex set
		HashSet<String> vertexSet = new HashSet<String>();
		// edge set
		HashMap<Edge, Double> edgeSet = new HashMap<Edge, Double>();
		
		//create vertex set
		for(Transition trans:pn.getTransitions())
		{
			vertexSet.add(trans.getLabel());
		}
		
		//create edge set, by parsing through each place.
		for(Place place: pn.getPlaces())
		{
			HashSet<String> sourceT = new HashSet<>();
			HashSet<String> targetT = new HashSet<>();
			for(PetrinetEdge<? extends PetrinetNode, ? extends PetrinetNode> edge : pn.getEdges())
			{
				//if there is no incoming arc, this place is a start place
				if(edge.getTarget().getLabel().equals(place.getLabel()))
				{
					sourceT.add(edge.getSource().getLabel());
				}
				if(edge.getSource().getLabel().equals(place.getLabel()))
				{
					targetT.add(edge.getTarget().getLabel());
				}
			}
			if(sourceT.size()>0&&targetT.size()>0)
			{
				for(String source: sourceT)
				{
					for(String target: targetT)
					{
						Edge e = new Edge(source,target);
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
			
		}
		
		//create class interaction graph
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> Newg = 
				new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		BusinessProcessGraph bpg = new BusinessProcessGraph(Newg);
		
		// add vertexs
		for (String vertex:vertexSet)
		{
			bpg.addVertex(vertex);
		}
		
		
		//add edges with weight
		for(Edge ed: edgeSet.keySet())
		{
			System.out.println(ed.source+":"+ed.target);
			DefaultWeightedEdge edge = bpg.addEdge(ed.source,ed.target);
			bpg.setEdgeWeight(edge, edgeSet.get(ed));
		}
		
		return bpg;
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
