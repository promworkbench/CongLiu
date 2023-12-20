package congliu.processmining.objectusage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import XESSoftwareExtension.XSoftwareExtension;

//plugin name
@Plugin(
		name = "Class Co-occurance Graph Discovery",// plugin name	
			returnLabels = {"Class Co-occurance Graph"}, //reture labels
			returnTypes = {CooccuranceGraph.class},//return class
			
			//input parameter labels, corresponding with the second parameter of main function
			parameterLabels = {"Software event log"},
			
			userAccessible = true,
			help = "This plugin aims to discover class co-occurance graph from a software event log." 
			)
public class ClassCooccuranceGraphDiscoveryPlugin {
		@UITopiaVariant(
		        affiliation = "TU/e", 
		        author = "Cong liu", 
		        email = "c.liu.3@tue.nl OR liucongchina@163.com"
		        )
		@PluginVariant(
				variantLabel = "Method behavior Usages, default",
				// the number of required parameters, {0} means one input parameter
				requiredParameterLabels = {0}
				)
		public CooccuranceGraph cooccuranceGraphDiscovery(UIPluginContext context, XLog softwareLog) throws ConnectionCannotBeObtained
		{
			//get the log name from the original log. it is shown as the title of returned results. 
			context.getFutureResult(0).setLabel("Class Co-occurance Graph: "+XConceptExtension.instance().extractName(softwareLog));
					
			//get the method set of the software event log
			obtainMethods om= new obtainMethods();
			HashSet<String> methods= om.getMethods(softwareLog);
			
			XFactory factory = new XFactoryNaiveImpl();
			
			//for each method, we construct its software event log from the original log.
			obtainMethod2subLog om2l= new obtainMethod2subLog();
			HashMap<String, XLog> method2Log =om2l.getMethod2subLog(softwareLog, methods, factory);
			
			System.out.println("the method-->log part is no probelm!");
			return constructCooccuranceGraph(method2Log);
		}
		
		/**
		 * construct the co-occurance graph of the whole log, trace by trace, 
		 * for each trace, we generate the full arc set of all nodes(classes)
		 * @param method2Log
		 * @return
		 */
		public static CooccuranceGraph constructCooccuranceGraph(HashMap<String, XLog> method2Log) 
		{
			// vertex set
			HashSet<String> vertexSet = new HashSet<String>();
			// edge set
			HashMap<Edge, Double> edgeSet = new HashMap<Edge, Double>();
			
			for(String method: method2Log.keySet())
			{
				System.out.println("construcut the vertexSet and edgeSet: "+method);
				
				for(XTrace trace: method2Log.get(method))
				{
					ArrayList<String> nodeListTrace =getNodes4Trace(trace);
					HashSet<Edge> edgeSetTrace = produceFullArcs(nodeListTrace);// no self ars is included
					
					//add node to the vertex set
					for(String node: nodeListTrace)
					{
						vertexSet.add(node);
					}
					
					// to avoid the same edge appears multiple times in the hash map.
					for(Edge e:edgeSetTrace)
					{
						if (edgeSet.keySet().contains(e))
						{
							edgeSet.put(e, edgeSet.get(e)+1.00);
						}
						else {
							edgeSet.put(e, 1.00);
						}
//						int flag =0;
//						for (Edge ed: edgeSet.keySet())
//						{
//							if (ed.equals(e))
//							{
//								edgeSet.put(ed,edgeSet.get(ed)+1.00);
//								flag=1;
//								break;
//							}
//						}
//						if (flag==0)
//						{
//							edgeSet.put(e, 1.00);
//						}
					}					
				}
			}
			
			//create co-occurance graph based on the vertex set and arc set(with weight)
			
			System.out.println("add vertex to co-occurence graph");
			
			SimpleWeightedGraph<String, DefaultWeightedEdge> g = 
					new SimpleWeightedGraph<String, DefaultWeightedEdge>(DefaultWeightedEdge.class);
			CooccuranceGraph cg = new CooccuranceGraph(g);
			// add vertexs
			for (String vertex:vertexSet)
			{
				cg.addVertex(vertex);
			}
			
			System.out.println("add edges and weight to co-occurence graph");
			//add edges with weights
			for(Edge ed: edgeSet.keySet())
			{
				System.out.println("edge: " +ed);
				DefaultWeightedEdge edge = cg.addEdge(ed.source,ed.target);
				cg.setEdgeWeight(edge, edgeSet.get(ed));
			}
			return cg;
		}
		
		/**
		 * get the node set for each trace, each node is a class
		 * @param trace
		 * @return
		 */
		public static ArrayList<String> getNodes4Trace(XTrace trace)
		{
			ArrayList<String> nodes4Trace = new ArrayList<>();
			HashSet<String> nodes = new HashSet<>();
			for (XEvent e: trace)// to avoid duplicate the nodes
			{
				nodes.add(XSoftwareExtension.instance().extractClass(e));
			}
			
			//convert to list
			for(String n: nodes)
			{
				nodes4Trace.add(n);
			}
			
			return nodes4Trace;
		}
		

		/**
		 * generate full arcs based on the nodes. 
		 * @param nodeList
		 * @return
		 */
		
		public static HashSet<Edge> produceFullArcs(ArrayList<String> nodeList)
		{
			HashSet<Edge> edgeSetTrace = new HashSet<Edge>();
			
			// generate full arcs based on node list. 
			for(int i=0; i<nodeList.size();i++)
			{
				for(int j=i+1;j<nodeList.size();j++)
				{
					Edge e = new Edge(nodeList.get(i), nodeList.get(j));
					edgeSetTrace.add(e);
				}
			}
			return edgeSetTrace;
		}
}
