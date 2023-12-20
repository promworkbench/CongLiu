package congliu.processmining.objectusage;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/*
 * this class aims to clustering the class co-occurence graph to differet groups, by taking a threshold. 
 * The co-occurence graph is a weighted undirected graph.
 * Step1: remove the edges whose weight is less than the threshold; and
 * Step2: Use the weakly connected component algorithm (GraphT package) to get clusters. 
 * 
 */
public class ClusteringClassCooccurenceGraph {

	/**
	 * use the threshold to filter the graph, i.e., edges whose whose weight is less than the threshold, are removed. 
	 * @param threshold
	 * @param cog
	 * @return
	 */
	public static CooccuranceGraph filterEdges(double threshold, CooccuranceGraph cog)
	{
		// create a new cog
		SimpleWeightedGraph<String, DefaultWeightedEdge> g = 
				new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		CooccuranceGraph newg =new CooccuranceGraph(g);
		
		// add nodes to newg
		for (String vertex: cog.getAllVertexes())
		{
			newg.addVertex(vertex);
		}
		
		Set<DefaultWeightedEdge> edgeSetKeeped = new HashSet<>();
		
		for(DefaultWeightedEdge edge: cog.getAllEdges())
		{
			// if the weight of an edge is bigger than the threshold, we add this edge should be add to the newg.
			if(threshold<=cog.getEdgeWeight(edge))
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
			newg.setEdgeWeight(tempE, cog.getEdgeWeight(e));
		}
		
		return newg;
	}
	
	/**
	 * for the filterd co-occurance graph, we get its weakly connected graphs. 
	 * @param cog
	 * @return
	 */
	
	public static Component2Classes getClusters(CooccuranceGraph cog)
	{
		//compute all weakly connected component
	    ConnectivityInspector ci = new ConnectivityInspector(cog.getCooccuranceGraph());
	    
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
	
	public static int getHighestVaule(CooccuranceGraph cog)
	{
		int highestValue=0;
		Set<DefaultWeightedEdge> edgeSet = cog.getAllEdges();
		
		for(DefaultWeightedEdge edge: edgeSet)
		{
			// if the weight of an edge bigger than the current maximal value
			if(highestValue<cog.getEdgeWeight(edge))
			{
				highestValue = (int) cog.getEdgeWeight(edge);
			}
		}
		
		return highestValue+10;
				
	}
	
	
}
