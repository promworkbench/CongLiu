package congliu.processmining.classobjectinteractiongraph;
/**
 * this plug-in is used to discover a group of class object interaction graph, 
 * each refers to a case. 
 * For shared classes among component, it a challenge~!
 * @author cliu3
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processmining.softwareprocessmining.Component2Classes;
import congliu.processmining.softwareprocessmining.FileChooserConfiguration;
import congliu.processmining.softwareprocessmining.FileChooserPanel;
import congliu.processmining.softwareprocessmining.ProgramExecutionData2SoftwareEventLogplugin;

@Plugin(
		name = "Class Object Interaction Graphs Discovery",// plugin name
		
		returnLabels = {"Class Object Interaction Graphs"}, //reture labels
		returnTypes = {ClassObjectInteractionGraphSet.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software event log"},
		
		userAccessible = true,
		help = "This plugin aims to discover a class object interaction graph set from a software event log." 
		)

public class ClassObjectInteractionGraphPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Construct Class Object Interaction Graphs, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	public ClassObjectInteractionGraphSet coigDiscovery(UIPluginContext context, XLog softwareLog)
	{
		//get the log name from the original log. it is shown as the title of returned results. 
		context.getFutureResult(0).setLabel("Class Object Interaction Graphs of: "+XConceptExtension.instance().extractName(softwareLog));
		
		//get the component to class mapping
		FileChooserConfiguration class2componentMappingFile = new FileChooserConfiguration();
		new FileChooserPanel(class2componentMappingFile);

		// obtain the mapping from component to classes. 
		ArrayList<Component2Classes> c2c =ProgramExecutionData2SoftwareEventLogplugin.component2Class(class2componentMappingFile.getFilename());
		
		// each case relates to a coig.
		ClassObjectInteractionGraphSet coigSet = new ClassObjectInteractionGraphSet();
		
		//we discover a coig for each case. 
		for (XTrace trace: softwareLog)
		{
			// for each trace, we construct a mapping from component instance to objects. 
			HashMap<ComponentInstance, Set<String>> comIns2Objs = componentInstance2Objects(c2c, trace);
			
			ClassObjectInteractionGraph coig = new ClassObjectInteractionGraph();
			for(XEvent event:trace)
			{
				// for the main method call, we only add a node without edge.
				if (XSoftwareExtension.instance().extractCallerclass(event).equals("null"))
				{
					Node node = new Node(XSoftwareExtension.instance().extractClassObject(event),
							XSoftwareExtension.instance().extractClass(event), 
							getComponentName(XSoftwareExtension.instance().extractClassObject(event),comIns2Objs),// get component name according to class obj.
							getComponentInstance(XSoftwareExtension.instance().extractClassObject(event), comIns2Objs));
					coig.addVertex(node); 
				}
				else
				{
					Node source = new Node(XSoftwareExtension.instance().extractCallerclassobject(event), 
							XSoftwareExtension.instance().extractCallerclass(event),
							getComponentName(XSoftwareExtension.instance().extractCallerclassobject(event),comIns2Objs),
							getComponentInstance(XSoftwareExtension.instance().extractCallerclassobject(event), comIns2Objs));
					Node target = new Node(XSoftwareExtension.instance().extractClassObject(event), 
							XSoftwareExtension.instance().extractClass(event),
							getComponentName(XSoftwareExtension.instance().extractClassObject(event),comIns2Objs),
							getComponentInstance(XSoftwareExtension.instance().extractClassObject(event), comIns2Objs));
					coig.addVertex(source);
					coig.addVertex(target);
					coig.addEdge(source, target);
				}
			}
			
			// add to the group
			coigSet.setArray(XConceptExtension.instance().extractName(trace), coig);
			//System.out.println("next trace");
		}
		
		return coigSet;
	}
	
	
	// construct a directed graph using JGraphT, the current case and the classes of the component is used. 
	public static DirectedGraph<String, DefaultEdge> constructDirectedGraph(XTrace trace, ArrayList<String> classList)
	{
		// we first conctruct a connected graph
		 DirectedGraph<String, DefaultEdge> directedGraph =
		            new DefaultDirectedGraph<String, DefaultEdge>
		            (DefaultEdge.class);
		 
		 // traverse through each event in the case
		 for (XEvent event :trace)
		 {
			 //if the callee of this event belongs to the current component.
			 if (classList.contains(XSoftwareExtension.instance().extractClass(event)))
			 {
				 directedGraph.addVertex(XSoftwareExtension.instance().extractClassObject(event));
				//if the caller of this recording belongs to the component.
				if (classList.contains(XSoftwareExtension.instance().extractCallerclass(event)))
				{
					directedGraph.addVertex(XSoftwareExtension.instance().extractCallerclassobject(event));
					// add an arc from caller to callee
					directedGraph.addEdge(XSoftwareExtension.instance().extractClassObject(event), 
							XSoftwareExtension.instance().extractCallerclassobject(event));
				}
			 }
		 }		 
		 return directedGraph;
	}
	
		
	// for each trace, we construct a mapping from component instance to objects. 
	public static HashMap<ComponentInstance, Set<String>> componentInstance2Objects(ArrayList<Component2Classes> c2c, XTrace trace)
	{
		// component instance to object set mapping.
		HashMap<ComponentInstance, Set<String>> comIns2Objs = new HashMap<ComponentInstance, Set<String>>();
		// traverse through different components 
		for (Component2Classes com2classes: c2c)
		{
			//the class list of this component
			ArrayList<String> classList = com2classes.getClasses(); 
			
			// construct a directed graph of the current component
			DirectedGraph<String, DefaultEdge> dg = constructDirectedGraph(trace, classList);
			
			//compute all weakly connected component
	        ConnectivityInspector ci = new ConnectivityInspector(dg);
	        
	        //Returns a list of Set s, where each set contains 
	        //all vertices that are in the same maximally connected component.
	        java.util.List connected = ci.connectedSets();
	        
	        // add component instance to objects mapping, component instance is represented as: component name+i
	        for (int i=0;i<connected.size();i++)
	        {
	        	comIns2Objs.put(new ComponentInstance(com2classes.getComponent(), Integer.toString(i)), (Set<String>)connected.get(i));
	        	System.out.println(com2classes.getComponent()+"?"+Integer.toString(i)+"?"+(Set<String>)connected.get(i));
	        }
		}
		return comIns2Objs;
	}
	
	// give a class object, this function returns its component.
	public static String getComponentName(String ClassObj, HashMap<ComponentInstance, Set<String>> comIns2Objs)
	{
		String componentName ="NOT_FOUND";  
		
		for(ComponentInstance comIns: comIns2Objs.keySet())
		{
			if (comIns2Objs.get(comIns).contains(ClassObj))
			{
				componentName =comIns.getComponent();
				break;
			}
		}
		return componentName;
	}
	
	// give an object, this function returns its component instance. 
	public static String getComponentInstance(String ClassObj, HashMap<ComponentInstance, Set<String>> comIns2Objs)
	{
		String componentInstance ="NOT_FOUND"; 
		for(ComponentInstance comIns: comIns2Objs.keySet())
		{
			if (comIns2Objs.get(comIns).contains(ClassObj))
			{
				componentInstance =comIns.getInstance();
				break;
			}
		}
		
		return componentInstance;
	}
}
