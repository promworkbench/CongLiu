package congliu.processmining.classobjectinteractiongraph;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class ClassObjectInteractionGraph {

	//A default directed graph is a non-simple directed graph in which multiple edges between any two vertices are not permitted, but loops are. 
	//A simple directed graph is a directed graph in which neither multiple edges between any two vertices nor loops are permitted.
	
	private final DefaultDirectedGraph<Node, DefaultEdge> g = 
			new DefaultDirectedGraph<Node, DefaultEdge>(DefaultEdge.class);


	public void addVertex(Node node) 
	{
		g.addVertex(node);
	}
	
	public DefaultEdge addEdge(Node v1, Node v2) {
		DefaultEdge e1 = g.addEdge(v1, v2);
		return e1;
	}
   
   
	public DefaultDirectedGraph<Node, DefaultEdge> getClassObjectInteractionGraph() {
		return g; 	
	}
}
