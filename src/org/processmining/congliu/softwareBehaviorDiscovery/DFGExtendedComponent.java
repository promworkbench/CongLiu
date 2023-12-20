package org.processmining.congliu.softwareBehaviorDiscovery;

import org.processmining.plugins.InductiveMiner.graphs.Graph;
import org.processmining.plugins.InductiveMiner.graphs.GraphFactory;
/**
 * this class defines a new type of directly followed graph based on the DFGE in
 * package org.processmining.congliu.softwareBehaviorDiscovery;
 * The difference is that they have different types of graph vertices. 
 * XEventClass vs XEventClassExtended vs String
 * @author cliu3
 *
 */
public class DFGExtendedComponent {

	// the vertices are different from those in Dfg in Inductive package
	private Graph<String> directlyFollowsGraph;
	
	public DFGExtendedComponent (int initialSize) {
		directlyFollowsGraph = GraphFactory.create(String.class, initialSize);
	}
	
	//add vertex to the directly followed graoh
	public void addActivity(String activity) {
		directlyFollowsGraph.addVertex(activity);
	}
	
	//	add directly followed edges to the dfg
	public void addDirectlyFollowsEdge(final String source, final String target, final long cardinality) {
		addActivity(source);
		addActivity(target);
		directlyFollowsGraph.addEdge(source, target, cardinality);
	}
	
	//return the dfg
	public Graph<String> getDirectlyFollowsGraph() {
		return directlyFollowsGraph;
	}
}
