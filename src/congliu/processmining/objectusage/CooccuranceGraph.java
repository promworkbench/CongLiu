package congliu.processmining.objectusage;

import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

/**
 * the cooccurance graph is an un-directed weighted graph. 
 * @author cliu3
 *
 */
public class CooccuranceGraph {
		/*
		 * A simple graph is an undirected graph for which at most one edge connects any two vertices, 
		 * and loops are not permitted. 
		 * If you're unsure about simple graphs, see: http://mathworld.wolfram.com/SimpleGraph.html.
		 * A simple weighted graph is a simple graph for which edges are assigned weights.
		 */
	
		private SimpleWeightedGraph<String, DefaultWeightedEdge> g;
		
		public CooccuranceGraph(SimpleWeightedGraph<String, DefaultWeightedEdge> g)
		{
			this.g =g;
		}
		
		/*
		 * add a vertex to the graph
		 */
		public void addVertex(String name) 
		{
			g.addVertex(name);
		}
		
		/*
		 * add one edge to the graph
		 */
		public DefaultWeightedEdge addEdge(String v1, String v2) 
		{
			return g.addEdge(v1, v2);
		}
		
		
		// set the weight of each edge.
		public void setEdgeWeight(DefaultWeightedEdge edge, double edge_weight) 
		{
			g.setEdgeWeight(edge, edge_weight);
		}
		
		
		/*
		 * remove an edge from the graph
		 */
		public void removeEdge(DefaultWeightedEdge edge)
		{
			g.removeEdge(edge);
		}
		/**
		 * add an edge
		 * @return
		 */
		public String getEdgeSource(DefaultWeightedEdge edge)
		{
			return g.getEdgeSource(edge);
		}
		
		public String getEdgeTarget(DefaultWeightedEdge edge)
		{
			return g.getEdgeTarget(edge);
		}
		
		public DefaultWeightedEdge addEdge(DefaultWeightedEdge edge)
		{
			return g.addEdge(g.getEdgeSource(edge), g.getEdgeTarget(edge));
		}
		
		/*
		 * return all edges
		 */
		public Set<DefaultWeightedEdge> getAllEdges()
		{
			return g.edgeSet();
		}
		
		/*
		 * return all nodes
		 * 
		 */
		public Set<String> getAllVertexes()
		{
			return g.vertexSet();
		}
		
		/*
		 * return the weight of an edge
		 */
		public double getEdgeWeight(DefaultWeightedEdge edge)
		{
			return g.getEdgeWeight(edge);
		}
		
		
		public SimpleWeightedGraph<String, DefaultWeightedEdge> getCooccuranceGraph() {
			return g; 	
		}

}
