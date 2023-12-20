package congliu.processmining.softwareprocessmining;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import XESSoftwareExtension.XSoftwareExtension;

public class GeneratingSoftwareEventLog {

	// construct software event log for each component, 
	//input: (1) classes of this one specific component (2) class2component mapping; and 
	//(3)software execution data
	public static XLog generatingSoftwareEventLog(Component2Classes com2class, ArrayList<Component2Classes> com2classList, String softwareEventDataDirectory)
	{
		//open readin file directory. 
		File file = new File(softwareEventDataDirectory); 
		File[] filelist = file.listFiles();
		
		//we use the following CSV list to store (filtered) different program runs 
		//each csv file is the one program run data the given component.
		//the structure of the software execution data: package name, class name, Method name, class object, start time, 
		//complete time, caller package name, caller class name, caller method name, caller class object 
		
		//ArrayList<CSVLog> csvList = new ArrayList<CSVLog>();
		
		HashSet<ArrayList<String>> csvList = new HashSet<ArrayList<String>>();
		String tempString = "";
		String [] tempList; 
		//here each file is a program run. 
		for (File f: filelist)
        {
			//CSVLog csv = new CSVLog(f.getName());
			ArrayList<String> csv = new ArrayList<String>();
			BufferedReader reader = null;
    		try 
    		{
    			reader = new BufferedReader(new FileReader(f));
    		} 
    		catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		//read by line, and add to mappingList,
    		try {
    			while ((tempString = reader.readLine()) != null)
    			{
    				if (tempString.trim().length()>0)
    				{
        				tempList = tempString.split(";");
        				
        				//if this line belongs to this component, add it to the csv
        				if (com2class.getClasses().contains(tempList[1]))
        				{
        					csv.add(tempString);
        					System.out.println(tempString);
        				}
    				}
    			}			
    			//close the CSV file reader
    			reader.close();
    			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		//it is possible to have an empty csv, this means that the current program run does not involves this component.  
        	csvList.add(csv);
        }
		// identifying component instances	
		HashSet<ArrayList<String>> componentInstanceSet = computeComponentInstances(csvList, com2class.getClasses());
		
		// convert the component instance set to a software event Log
		// we also add component information for caller and callee, and Nesting_Level attribute
		XLog softwareComponentLog =convertComponentInstanceSet2EventLog (com2class, com2classList, componentInstanceSet);
		
		return softwareComponentLog;
	}
	
	
	// this method aims to compute the component instance by taking the csvList as input. 
	//it returns a set of component instances (each describing the lifecycle of a component run)
	public static HashSet<ArrayList<String>> computeComponentInstances(HashSet<ArrayList<String>> csvList, ArrayList<String> classList)
	{
		HashSet<ArrayList<String>> componentInstanceSet = new HashSet<ArrayList<String>>();
		
		//traverse through each program run, identify the component instance and add them.
		for (ArrayList<String> run: csvList)
		{
			//for each run, we first construct a class object interaction graph, based on which we identify component instances
			DirectedGraph<String, DefaultEdge> dg = constructDirectedGraph(run, classList);
	        
			//compute all weakly connected component
	        ConnectivityInspector ci = new ConnectivityInspector(dg);
	        
	        //Returns a list of Set s, where each set contains 
	        //all vertices that are in the same maximally connected component.
	        java.util.List connected = ci.connectedSets();
	               
	        //construct the componentInstanceSet from the set of connected component
	        for (int i = 0; i < connected.size(); i++) 
	        {
	        	Set<String> s= (Set<String>) connected.get(i);
	        	for(String str:s)
	        	{
	        		System.out.println(str);
	        	}
	        	System.out.println("-------");
	            //System.out.println(connected.get(i));       	
	        	componentInstanceSet.add(generateComponentRelevantData((Set<String>) connected.get(i),run));
	        }	
		}
		
		return componentInstanceSet;
	}
	
	// construct a directed graph using JGraphT, the current run and the classes of the component is used. 
	public static DirectedGraph<String, DefaultEdge> constructDirectedGraph(ArrayList<String> run, ArrayList<String> classList)
	{
		// we first conctruct a connected graph
		 DirectedGraph<String, DefaultEdge> directedGraph =
		            new DefaultDirectedGraph<String, DefaultEdge>
		            (DefaultEdge.class);
		 
		 // traverse through each recording in a run
		 for (String recording: run)
		 {
			 String [] temp = recording.split(";");
			 //we are sure the callee of this recording belongs to this component.
			 directedGraph.addVertex(temp[3]);
			 
			//if the caller of this recording belongs to the component.
			if (classList.contains(temp[7]))
			{
				directedGraph.addVertex(temp[9]);
				// add an arc from caller to callee
				directedGraph.addEdge(temp[9], temp[3]);
			}
		 }		 
		 return directedGraph;
	}
	
	//obtain each component instance relevant software data
	//Input: the vertex of this component, 
	public static ArrayList<String> generateComponentRelevantData(Set<String> objectSet, ArrayList<String> run)	
	{
		ArrayList<String> componentInstanceData = new ArrayList<String>();
		 for (String recording: run)
		 {
			 String [] temp = recording.split(";");
			 
			//if the callee of this recording belongs to the sub-component.
			if (objectSet.contains(temp[3]))
			{
				componentInstanceData.add(recording);
			}
		 }		 
		
		return componentInstanceData;
		
	}
	
	//convert the component instance set to a software event Log	
	//Input: (1) classes of this one specific component (2) class2component mapping all; and (3) the execution instances of this component
	public static XLog convertComponentInstanceSet2EventLog (Component2Classes com2class, ArrayList<Component2Classes> com2classList, 
			HashSet<ArrayList<String>> compInstanceSet)
	{
		//before do the convert, we first add another "Nesting_Level" attribute to each method.
		HashSet<ArrayList<String>> compInstanceSetExtended = AddNestingLevelAttribute.NestingLevelIdentification(compInstanceSet, com2class);
		
		XFactory factory = new XFactoryNaiveImpl();
		// input the log name and a factory.
		XLog softwareComponentLog =InitializeSoftwareEventLog.initialize(factory, com2class.getComponent());
		
		//each component instance is converted to a software event trace
		
		int traceID = 1;
		for (ArrayList<String> instance :compInstanceSetExtended)
		{
			//each instance is converted to an software trace in the log.
			XTrace tempTrace = factory.createTrace();
			tempTrace.getAttributes().put(XConceptExtension.KEY_NAME, new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "case"+traceID++));
			
			for (String recording: instance)
			{
				String[] eventAttributes = recording.split(";");
				
				// callee component is stable
				XEvent tempEvent = createSoftwareEvent(factory,eventAttributes, com2class, com2classList);
				tempTrace.add(tempEvent);
			}
			
			softwareComponentLog.add(tempTrace);
		}
		
		// ording the software event log using start time of each method. 
		return OrderingEventsNano.ordering(softwareComponentLog, XSoftwareExtension.KEY_STARTTIMENANO);
		
	}
	
	
	public static XEvent createSoftwareEvent (XFactory factory, String [] attributes, Component2Classes com2class, ArrayList<Component2Classes> com2classList)
	{
	
		XAttributeMap attributeMap = new XAttributeMapImpl();
		
		// the concept is the method name here
//		attributeMap.put(XConceptExtension.KEY_NAME,
//				factory.createAttributeLiteral(XConceptExtension.KEY_NAME, attributes[2], XConceptExtension.instance()));
		
//		attributeMap.put(XLifecycleExtension.KEY_TRANSITION, factory.createAttributeLiteral(
//				XLifecycleExtension.KEY_TRANSITION, "start", XLifecycleExtension.instance()));
		
		// domain specific event attributes
//		attributeMap.put("Class", new XAttributeLiteralImpl("Class", attributes[1]));
//		attributeMap.put("Package", new XAttributeLiteralImpl("Package", attributes[0]));
//		attributeMap.put("Class_Object", new XAttributeLiteralImpl("Class_Object", attributes[3]));
		
		String calleeComponentName = getCalleeComponentName(attributes[1], com2classList);
//		attributeMap.put("Component", new XAttributeLiteralImpl("Component", calleeComponentName));

//		attributeMap.put("Caller_Method", new XAttributeLiteralImpl("Caller_Method", attributes[8]));
//		attributeMap.put("Caller_Class", new XAttributeLiteralImpl("Caller_Class", attributes[7]));
//		attributeMap.put("Caller_Package", new XAttributeLiteralImpl("Caller_Package", attributes[6]));
//		attributeMap.put("Caller_Class_Object", new XAttributeLiteralImpl("Caller_Class_Object", attributes[9]));
		
		String callerComponentName = getCallerComponentName(attributes[7], com2classList);
//		attributeMap.put("Caller_Component", new XAttributeLiteralImpl("Caller_Component", callerComponentName));
		
		attributeMap.put("Nesting_Level", new XAttributeLiteralImpl("Nesting_Level", attributes[10]));
		/**
		 *  Interface type, 0: internal component event, 1: required event, 2: provided event
		 */
		attributeMap.put("Interface_Type", new XAttributeLiteralImpl("Interface_Type", getInterfaceType(calleeComponentName, callerComponentName, com2class.getComponent())));
		
//		attributeMap.put("Timestamp_Nano_Start", new XAttributeLiteralImpl("Timestamp_Nano_Start", attributes[4]));
//		attributeMap.put("Timestamp_Nano_End", new XAttributeLiteralImpl("Timestamp_Nano_End", attributes[5]));
		
		XEvent event = factory.createEvent(attributeMap);
		
		XConceptExtension.instance().assignName(event, attributes[2]);
		XLifecycleExtension.instance().assignTransition(event, "start");
		XSoftwareExtension.instance().assignClass(event, attributes[1]);
		XSoftwareExtension.instance().assignPackage(event, attributes[0]);
		XSoftwareExtension.instance().assignClassObject(event, attributes[3]);
		XSoftwareExtension.instance().assignComponent(event, calleeComponentName);
		XSoftwareExtension.instance().assignCallermethod(event, attributes[8]);
		XSoftwareExtension.instance().assignCallerclass(event, attributes[7]);
		XSoftwareExtension.instance().assignCallerclassobject(event, attributes[9]);
		XSoftwareExtension.instance().assignCallerpackage(event, attributes[6]);
		XSoftwareExtension.instance().assignCallercomponent(event, callerComponentName);
		XSoftwareExtension.instance().assignStarttimenano(event, attributes[4]);
		XSoftwareExtension.instance().assignEndtimenano(event, attributes[5]);
		
		
		return event;
	}
	
	// get the component of callee
	public static String getCalleeComponentName (String calleeClassName, ArrayList<Component2Classes> com2classList)
	{
		String calleeComponentName ="null";
		
		for(Component2Classes cc: com2classList)
		{
			for (String className: cc.getClasses())
			{
				if (className.equals(calleeClassName))
				{
					return cc.getComponent();
				}
			}
		}
		return calleeComponentName;
	}
	
	// get the component of caller
	public static String getCallerComponentName (String callerClassName, ArrayList<Component2Classes> com2classList)
	{
		String callerComponentName ="null";
		
		for(Component2Classes cc: com2classList)
		{
			for (String className: cc.getClasses())
			{
				if (className.equals(callerClassName))
				{
					return cc.getComponent();
				}
			}
		}
		return callerComponentName;
	}
	
	/**
	 *  generate interface type, 0: internal component event, 1: required event, 2: provided event
	 */
	
	public static String getInterfaceType(String calleeComponentName, String callerComponentName, String currentComponentName)
	{
		String interType="";
		
		if (calleeComponentName.equals(callerComponentName))
		{
			// internal component event 
			interType="0";
		}
		else if (calleeComponentName.equals(currentComponentName)) 
		{
			interType="2";
		}
		else 
		{
			interType ="1";
		}
		
		return interType;
	}
}
