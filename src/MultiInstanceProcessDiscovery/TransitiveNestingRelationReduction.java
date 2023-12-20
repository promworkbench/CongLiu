package MultiInstanceProcessDiscovery;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;


public class TransitiveNestingRelationReduction {
	
	//construct activity nesting relation graph
	public static ActivityNestingGraph ActivityPrecedencyGraphConstruction(HashSet<ActivityPair> nestingActivityPariSet)
	{
		// we first construct a connected graph
		DefaultDirectedGraph<String, DefaultEdge> g = 
				new DefaultDirectedGraph<String, DefaultEdge>(DefaultEdge.class);
		ActivityNestingGraph ang = new ActivityNestingGraph(g);
				
		//get the discovered nesting relations, and construct the activity nesting graph
		for(ActivityPair ap : nestingActivityPariSet)
		{		
			ang.addVertex(ap.getSourceActivity());
			ang.addVertex(ap.getTargetActivity());
			ang.addEdge(ap.getSourceActivity(), ap.getTargetActivity());
		}
		
		System.out.println("Before transitive reduction, the number of edges:" +ang.getAllEdges().size());
		
		// perform the transitive reduction for the activity nesting graph
		TransitiveReduction.INSTANCE.reduce(ang.getActivityNestingGraph());
		System.out.println("After transitive reduction, the number of edges:" +ang.getAllEdges().size());
		
		return ang;
	}
	
	//get all nested activities
	public static Set<String> getAllNestedActivities(ActivityNestingGraph ang)
	{
		return ang.getAllVertexes();
	}
	
	//get all root nodes, i.e., all nodes without incoming edges. 
	public static HashSet<String> getAllRootActivities(ActivityNestingGraph ang)
	{
		HashSet<String> rootActivities = new HashSet<>();
		for(String node: ang.getAllVertexes())
		{
			if(ang.getInDegreeOf(node)==0)
			{
				rootActivities.add(node);
			}
		}
		
		return rootActivities;
	}
	
	
	//we get all nested activities of an activity. 
	public static Set<String> getNestedActivitiesOfAnActivity(ActivityNestingGraph ang, String node)
	{
		return ang.getOutGoingVertexes(node);
	}
}
