package MultiInstanceProcessDiscovery;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

/**
 * this class defines a directed graph based on GraphT.
 * it is used to represent the activity nesting relation. 
 * @author cliu3
 *
 */
public class ActivityNestingGraph {

	//A directed weighted graph is a non-simple directed graph in which multiple edges between any two vertices are not permitted, 
	//but loops are. The graph has weights on its edges.
	private DefaultDirectedGraph<String, DefaultEdge> g = 
			new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
	

	public ActivityNestingGraph(DefaultDirectedGraph<String, DefaultEdge> g)
	{
		this.g =g;
	}
	
	/**
	 * add vertex to the graph
	 * @param name
	 */
	public void addVertex(String node) {
			g.addVertex(node);
	}
	
	/**
	 * add edge to the vertex
	 * @param v1
	 * @param v2
	 * @return
	 */
	public DefaultEdge addEdge(String n1, String n2) {
		return g.addEdge(n1, n2);
	}
	
	public DefaultEdge addEdge(DefaultEdge edge)
	{
		return g.addEdge(g.getEdgeSource(edge), g.getEdgeTarget(edge));
	}
	
	/**
	 * return the current graph
	 * @return
	 */
	public DefaultDirectedGraph<String, DefaultEdge> getActivityNestingGraph() {
		return g; 	
	}
	
	/**
	 * get the edge set
	 * @return
	 */
	public Set<DefaultEdge> getAllEdges()
	{
		return g.edgeSet();
	}
	
	/**
	 * get the vertext set
	 * @return
	 */
	public Set<String> getAllVertexes()
	{
		return g.vertexSet();
	}
	
	
	/**
	 * get the source of an edge
	 */
	public String getEdgeSource(DefaultEdge edge)
	{
		return  g.getEdgeSource(edge);
	}
	
	/**
	 * get the target of an edge
	 */
	public String getEdgeTarget(DefaultEdge edge)
	{
		return  g.getEdgeTarget(edge);
	}
	
	
	/*
	 * get the inDegreeOf of a vertex, the number incoming edges
	 */
	
	public int getInDegreeOf(String node)
	{
		return  g.inDegreeOf(node);
	}
	
	/*
	 * get the outDegreeOf of a vertex, the number outcoming edges
	 */
	
	public int getOutDegreeOf(String node)
	{
		return  g.outDegreeOf(node);
	}
	
	
	/*
	 * get the incoming Edges Of a vertex
	 */
	public Set<DefaultEdge> getIncomingEdges(String node)
	{
		return g.incomingEdgesOf(node);
	}
	
	/**
	 * get the incoming vertexes of a vertex
	 */
	public Set<String> getInComingVertexes(String node)
	{
		HashSet<String> incomingVertexes = new HashSet<>();
		for(DefaultEdge edge: getIncomingEdges(node))
		{
			incomingVertexes.add(getEdgeSource(edge));
		}
		return incomingVertexes;		
	}
	
	/*
	 * get the outComing edges of a vertex
	 */
	public Set<DefaultEdge> getOutGoingEdges(String node)
	{
		return g.outgoingEdgesOf(node);
	}
	
	/*
	 * get the out going vertexs of a vertex
	 */
	
	public Set<String> getOutGoingVertexes(String node)
	{
		HashSet<String> outGoingVertexes = new HashSet<>();
		for(DefaultEdge edge: getOutGoingEdges(node))
		{
			outGoingVertexes.add(getEdgeTarget(edge));
		}
		return outGoingVertexes;		
	}
	
}
