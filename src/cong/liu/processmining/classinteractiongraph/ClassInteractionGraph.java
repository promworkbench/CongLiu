package cong.liu.processmining.classinteractiongraph;

import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class ClassInteractionGraph {

	//A directed weighted graph is a non-simple directed graph in which multiple edges between any two vertices are not permitted, 
	//but loops are. The graph has weights on its edges.
	private DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g = 
			new DefaultDirectedWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	
	//private DefaultWeightedEdge e1;
	public ClassInteractionGraph(DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> g)
	{
		this.g =g;
	}
	
	public void addVertex(String name) {
			g.addVertex(name);
	}
   
	public DefaultWeightedEdge addEdge(String v1, String v2) {
		DefaultWeightedEdge e1 = g.addEdge(v1, v2);
		return e1;
	}
   
	// set the weight of each edge.
	public void setEdgeWeight(DefaultWeightedEdge e, double edge_weight) {
		g.setEdgeWeight(e, edge_weight);
	}
   
	public DefaultDirectedWeightedGraph<String, DefaultWeightedEdge> getClassInteractionGraph() {
		return g; 	
	}
	
	public Set<DefaultWeightedEdge> getAllEdges()
	{
		return g.edgeSet();
	}
	
	public double getEdgeWeight(DefaultWeightedEdge edge)
	{
		return g.getEdgeWeight(edge);
	}
	
	public Set<String> getAllVertexes()
	{
		return g.vertexSet();
	}
	
	public DefaultWeightedEdge addEdge(DefaultWeightedEdge edge)
	{
		return g.addEdge(g.getEdgeSource(edge), g.getEdgeTarget(edge));
	}
}
