package cong.liu.processmining.classinteractiongraph;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import congliu.processmining.objectusage.Component2Classes;

/*
 * this class aims to clustering the class interaction graph to differet groups, by taking a threshold. 
 * The class interaction graph is a weighted directed graph.
 * Step1: remove the edges whose weight is less than the threshold; and
 * Step2: Use the weakly connected component algorithm (GraphT package) to get clusters. 
 * 
 */
public class ClusteringClassInteractionGraph {
	/**
	 * get the highest weight for the graph.
	 * @param cig
	 * @return
	 */
	public static int getHighestVaule(ClassInteractionGraph cig)
	{
		int highestValue=0;
		Set<DefaultWeightedEdge> edgeSet = cig.getAllEdges();
		
		for(DefaultWeightedEdge edge: edgeSet)
		{
			// if the weight of an edge bigger than the current maximal value
			if(highestValue<cig.getEdgeWeight(edge))
			{
				highestValue = (int) cig.getEdgeWeight(edge);
			}
		}
		
		return highestValue+10;
	}
	
	/**
	 * use the threshold to filter the graph, i.e., edges whose whose weight is less than the threshold, are removed. 
	 * @param threshold
	 * @param cog
	 * @return
	 */
	public static ClassInteractionGraph filterEdges(double threshold, ClassInteractionGraph cig)
	{
		// create a new cog
		DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g = 
				new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		ClassInteractionGraph newg =new ClassInteractionGraph(g);
		
		// add nodes to newg
		for (String vertex: cig.getAllVertexes())
		{
			newg.addVertex(vertex);
		}
		
		Set<DefaultWeightedEdge> edgeSetKeeped = new HashSet<>();
		
		for(DefaultWeightedEdge edge: cig.getAllEdges())
		{
			// if the weight of an edge is bigger than the threshold, we add this edge should be add to the newg.
			if(threshold<=cig.getEdgeWeight(edge))
			{
				//newg.removeEdge(edge);
				edgeSetKeeped.add(edge);
			}
		}
		
		//add the edges to newg
		for(DefaultWeightedEdge e: edgeSetKeeped)
		{
			//System.out.println(e+" is add!");
			DefaultWeightedEdge tempE = newg.addEdge(e);
			newg.setEdgeWeight(tempE, cig.getEdgeWeight(e));
		}
		
		return newg;
	}
	
	/**
	 * for the filterd class interaction graph, we get its weakly connected graphs. 
	 * @param cog
	 * @return
	 */
	
	public static Component2Classes getClusters(ClassInteractionGraph cig)
	{
		//compute all weakly connected component
	    ConnectivityInspector ci = new ConnectivityInspector(cig.getClassInteractionGraph());
	    
	    //Returns a list of Set s, where each set contains 
	    //all vertices that are in the same maximally connected component.
	    java.util.List connected = ci.connectedSets();
	    Component2Classes group2Classes = new Component2Classes();
	    
	    for (int i=0;i<connected.size();i++)
	    {
	    	group2Classes.add("Group "+i, (Set<String>)connected.get(i));
	    }
	    return group2Classes;
	}
	
}
