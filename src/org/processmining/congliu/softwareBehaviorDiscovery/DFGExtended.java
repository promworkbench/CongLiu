package org.processmining.congliu.softwareBehaviorDiscovery;

import java.util.HashSet;
import java.util.Iterator;

import org.apache.commons.collections15.iterators.ArrayIterator;
import org.processmining.plugins.InductiveMiner.MultiSet;
import org.processmining.plugins.InductiveMiner.graphs.Graph;
import org.processmining.plugins.InductiveMiner.graphs.GraphFactory;

/**
 * this class defines a new type of directly followed graph based on the Dfg in
 * package org.processmining.plugins.InductiveMiner.dfgOnly
 * The difference is that they have different types of graph vertices. 
 * XEventClass vs XEventClassExtended
 * @author cliu3
 *
 */
public class DFGExtended {

	// the vertices are different from those in Dfg in Inductive package
	private Graph<XEventClassExtended> directlyFollowsGraph;

	private final MultiSet<XEventClassExtended> startActivities;
	private final MultiSet<XEventClassExtended> endActivities;
	
	public DFGExtended (int initialSize) {
		directlyFollowsGraph = GraphFactory.create(XEventClassExtended.class, initialSize);

		startActivities = new MultiSet<>();
		endActivities = new MultiSet<>();
	}
	
	//
	public HashSet<String> getComponentSet()
	{
		HashSet<String> componentSet = new HashSet<String>();
		
		for (XEventClassExtended activity : directlyFollowsGraph.getVertices()) 
		{
			componentSet.add(activity.getComponentName().toString());
		}
		
		return componentSet;
		
	}
	//add vertex to the directly followed graoh
	public void addActivity(XEventClassExtended activity) {
		directlyFollowsGraph.addVertex(activity);
	}
	
	// get all start activities
	public MultiSet<XEventClassExtended> getStartActivities() {
		return startActivities;
	}
	
	// get all end activities
	public MultiSet<XEventClassExtended> getEndActivities() {
		return endActivities;
	}
	
	//	add directly followed edges to the dfg
	public void addDirectlyFollowsEdge(final XEventClassExtended source, final XEventClassExtended target, final long cardinality) {
		addActivity(source);
		addActivity(target);
		directlyFollowsGraph.addEdge(source, target, cardinality);
	}
	
	public void addStartActivity(XEventClassExtended activity, long cardinality) {
		addActivity(activity);
		startActivities.add(activity, cardinality);
	}

	public void addEndActivity(XEventClassExtended activity, long cardinality) {
		addActivity(activity);
		endActivities.add(activity, cardinality);
	}
	
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		for (long edgeIndex : directlyFollowsGraph.getEdges()) {
			result.append(directlyFollowsGraph.getEdgeSource(edgeIndex));
			result.append("->");
			result.append(directlyFollowsGraph.getEdgeTargetIndex(edgeIndex));
			result.append(", ");
		}
		return result.toString();
	}
	
	// get all activities
	public Iterable<XEventClassExtended> getActivities() {
		return new Iterable<XEventClassExtended>() {
			public Iterator<XEventClassExtended> iterator() {
				return new ArrayIterator<XEventClassExtended>(directlyFollowsGraph.getVertices());
			}
		};

	}
	
	//set the dfg as the input one
	public void setDirectlyFollowsGraph(Graph<XEventClassExtended> directlyFollowsGraph) {
		this.directlyFollowsGraph = directlyFollowsGraph;
	}
	
	//return the dfg
	public Graph<XEventClassExtended> getDirectlyFollowsGraph() {
		return directlyFollowsGraph;
	}
}
