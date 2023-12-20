package org.processmining.congliu.softwareBehaviorDiscovery;

import java.util.List;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.classification.XEventClassifier;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.info.XLogInfo;
import org.deckfour.xes.info.XLogInfoFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

/**
 * this class aims to discover a directly followed graph with component and hierarchical symbol.
 * Input: XLog (obtained using the nestedMethodcallDetection) only contains the information of nested events. 
 * Output: directly followed graph with component
 * @author cliu3
 *
 */
//@Plugin(
//		// plugin name
//		name = "Directly Followed Graph Miner (with Component and Nested Nodes)",
//		
//		//return labels
//		returnLabels = {"Directly Followed Graph"}, 
//		// return class, here the DFGExtended is an improved Dfg by extending nested and component information 
//		returnTypes = {DFGExtended.class},
//		
//		userAccessible = true,
//		help = "This plugin aims to discover a directly followed graph from the filtered log using lifecycle", 
//		
//		//input labels, corresponding with the second parameter of main function
//		parameterLabels = {"ProM Software Execution Log"} 
//		)
public class DirectlyFollowedGraphwithComponent {

//	@UITopiaVariant(
//	        affiliation = "TU/e", 
//	        author = "Cong liu", 
//	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
//	        )
//	@PluginVariant(
//			variantLabel = "Directly Followed Graph with Component, default",
//			// the number of required parameters, {0} means one input parameter
//			requiredParameterLabels = {0}
//			)
//	// the input of this plugin is an Xlog, and returns a filtered XLog (without method calling information)   
//	public static DFGExtended discoverDFGwithComponent (PluginContext context, XLog inputlog) {
//		
//		context.getFutureResult(0).setLabel(
//				"Extended Directly-follows graph of " + XConceptExtension.instance().extractName(inputlog));
//		
//		//first define a directly followed graph with component. 
//		//then mine the directly followed graph from the log. 
//		//finally we need to visualize the dfge using Graphviz dot package.
//
//		//System.out.println(log2dfge(inputlog).toString());
//		return log2dfge(inputlog);
//	}
	
	public static DFGExtended log2dfge(XLog log) 
	{
		// the questions is that do we need to use the lifecycle information ?
		
		
		//first filter the log to contain only the complete event
		XLog processedLog = filterlifecycle(log);
		
		List <XEventClassifier> classiferList = processedLog.getClassifiers();
		XEventClassifier ourClassifier =classiferList.get(0);

		//to get the event class. 
		XLogInfo Xloginfo = XLogInfoFactory.createLogInfo(processedLog, ourClassifier);
		
		DFGExtended dfge = new DFGExtended(1);
		
		XEventClassExtended fromEventClassExtended;
		XEventClassExtended toEventClassExtended;
		
		//walk through (traverse) the log 
		for (XTrace trace: processedLog) 
		{
			fromEventClassExtended = null;
			toEventClassExtended = null;

			for(XEvent event: trace)
			{
				//construct the extended class event
				XEventClassExtended ece = new XEventClassExtended();
				ece.setXeventclass(Xloginfo.getEventClasses().getClassOf(event));
				//for those sublog event, they do not have this attribute. 
				if (event.getAttributes().containsKey("Nested"))
				{
					ece.setNestedFlag(event.getAttributes().get("Nested").toString());
				}
				ece.setComponentName(event.getAttributes().get("Belonging_Component").toString());
				//add an activity to graph, how to deal with 
				dfge.addActivity(ece);
				//print the nodes in the dfg
				//System.out.println(ece.getXeventclass());
				
				fromEventClassExtended = toEventClassExtended;
				toEventClassExtended = ece;
				
				if (fromEventClassExtended != null) {
					//add edge to directly-follows graph
					dfge.addDirectlyFollowsEdge(fromEventClassExtended, toEventClassExtended, 1);
					//System.out.println(fromEventClassExtended.getXeventclass()+"-->"+toEventClassExtended.getXeventclass());
				} 
//				else {
//					//add edge to start activities
//					dfge.addStartActivity(toEventClassExtended, 1);
//				}
			
			}
			if (toEventClassExtended != null) {
				//add edge to end activities
				dfge.addEndActivity(toEventClassExtended, 1);
			}
		}

		return dfge;
	}
	
	
	
	
	// we filter the event log to contain only the complete event
	public static XLog filterlifecycle(XLog log)
	{
		XFactory factory = new XFactoryNaiveImpl();
		//XFactory factory = new XFactoryExternalStore.MapDBDiskSequentialAccessImpl();
		XLog filteredLog = factory.createLog();
		
		// create classifiers based on global attribute		
		XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Acticity Name", XConceptExtension.KEY_NAME, "Class", "Package");
		filteredLog.getClassifiers().add(classifierActivity);
		// create classifiers based on global attribute		
		XEventAttributeClassifier classifierComponent = new XEventAttributeClassifier("Component","Belonging_Component");
		filteredLog.getClassifiers().add(classifierComponent);
		for (XTrace trace: log) 
		{
			XTrace filteredTrace = factory.createTrace();
			for(XEvent event: trace)
			{
				if (event.getAttributes().get("lifecycle:transition").toString().equals("complete"))
				{
					filteredTrace.add(event);
				}
			}
			
			filteredLog.add(filteredTrace);
		}
		return filteredLog;
	}
	
}
