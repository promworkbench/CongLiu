package congliu.processmining.softwareinterfacediscovery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processminig.softwarecomponentinteractionbehaviordiscovery.ConstructHLog;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

public class BasicOperations {

	/*
	 * Given a software event log and component (a set of classes), construct the component event log. 
	 */
	public static XLog generatingSoftwareEventLog(String component, Set<ClassClass> cc, XLog originalLog, XFactory factory)
	{
		// create log
		XLog componentLog =ConstructHLog.initialize(factory, component);
		
		//construct class set of the current component 
		HashSet<String> classSet = new HashSet<>();
		for(ClassClass c: cc)
		{
			classSet.add(c.getPackageName()+"."+c.getClassName());
		}
		
		for(XTrace trace: originalLog)
		{
			XTrace tempTrace = factory.createTrace();
			for(XEvent event: trace)
			{
				// filtering the trace according to the classes
				if(classSet.contains(XSoftwareExtension.instance().extractPackage(event)+"."+XSoftwareExtension.instance().extractClass(event)))
				{
					tempTrace.add(event);
				}
			}
			if(tempTrace.size()>0)// we do not add empty traces
			{
				componentLog.add(tempTrace);
			}
		}
		return componentLog;
	}
	
	/*
	 * Given a component log, the caller method set, the top-level method set of each interface, return the event log of an interface. 
	 * In this function, there is always a problem caused by the nesting method call. 
	 */
	
	public static XLog constructInterfaceLog(XLog comLog, HashSet<MethodClass> methodSet, HashSet<MethodClass> callerMethods, XFactory factory)
	{
		// create log
		XLog interfaceLog =ConstructHLog.initialize(factory, "interface");
		
		//construct top-level method set of the current interface 
		HashSet<String> MethodSet = new HashSet<>();
		for(MethodClass c: methodSet)
		{
			MethodSet.add(c.getPackageName()+"."+c.getClassName()+"."+c.getMethodName());
		}
				
		//construct caller method set of the top-level method set
		HashSet<String> CallerMethodSet = new HashSet<>();
		for(MethodClass c: callerMethods)
		{
			CallerMethodSet.add(c.getPackageName()+"."+c.getClassName()+"."+c.getMethodName());
		}
		
		for(XTrace trace: comLog)
		{
			// here, we only add events that are detected as nesting but not calling itself.
			Queue<XEvent> nestedEventQueue = new LinkedList<XEvent>() ; 
					
			XTrace tempTrace = factory.createTrace();
			
			for(XEvent event: trace)
			{
				//nesting depth = 1
				//for event (1) included in the top-level method set (2) the caller also satisfied 
				if(MethodSet.contains(XSoftwareExtension.instance().extractPackage(event)+"."
						+XSoftwareExtension.instance().extractClass(event)+"."+
						XConceptExtension.instance().extractName(event))
						&&CallerMethodSet.contains(XSoftwareExtension.instance().extractCallerpackage(event)+"."
								+XSoftwareExtension.instance().extractCallerclass(event)+"."
								+XSoftwareExtension.instance().extractCallermethod(event)))
				{
					tempTrace.add(event);
					if(checkNesting(event,trace))
					{
						nestedEventQueue.add(event);
					}
				}
			}
			
			while(!nestedEventQueue.isEmpty())
			{
				System.out.println("<><><><>the nested queue is not empty");
				
				XEvent nestedE= nestedEventQueue.poll();
				System.out.println("poll out nesting event: "+XConceptExtension.instance().extractName(nestedE));

				for(XEvent event: trace)
				{
					if(XSoftwareExtension.instance().extractClassObject(nestedE).equals(XSoftwareExtension.instance().extractCallerclassobject(event))
							&&XConceptExtension.instance().extractName(nestedE).equals(XSoftwareExtension.instance().extractCallermethod(event)))
					{
						tempTrace.add(event);
						if(checkNesting(event,trace))
						{
							nestedEventQueue.add(event);
						}
					}
				}
				
			}
			
			interfaceLog.add(tempTrace);
		}
		
		return interfaceLog;		
	}
	
	/*
	 * Given a component log, the nesting depth to consider, the caller method set, the top-level method set of each interface, return the event log of an interface. 
	 * In this function, there is always a problem caused by the nesting method call. 
	 */
	
	public static XLog constructNNestingInterfaceLog(XLog comLog, int n, HashSet<MethodClass> methodSet, HashSet<MethodClass> callerMethods, XFactory factory)
	{
		int count =1;
		
		HashMap<Integer, XTrace> nestedTraces = new HashMap<>();
		
		// create log
		XLog interfaceLog =ConstructHLog.initialize(factory, "interface");
		
		//construct top-level method set of the current interface 
		HashSet<String> MethodSet = new HashSet<>();
		for(MethodClass c: methodSet)
		{
			MethodSet.add(c.getPackageName()+"."+c.getClassName()+"."+c.getMethodName());
		}
				
		//construct caller method set of the top-level method set
		HashSet<String> CallerMethodSet = new HashSet<>();
		for(MethodClass c: callerMethods)
		{
			CallerMethodSet.add(c.getPackageName()+"."+c.getClassName()+"."+c.getMethodName());
		}
		
		for(XTrace trace: comLog)
		{		
			XTrace temprace = factory.createTrace();
			XTrace topLevelTrace = factory.createTrace();
			
			//get the top-level events
			for(XEvent event: trace)
			{
				//for event (1) included in the top-level method set (2) the caller also satisfied 
				if(MethodSet.contains(XSoftwareExtension.instance().extractPackage(event)+"."
						+XSoftwareExtension.instance().extractClass(event)+"."+
						XConceptExtension.instance().extractName(event))
						&&CallerMethodSet.contains(XSoftwareExtension.instance().extractCallerpackage(event)+"."
								+XSoftwareExtension.instance().extractCallerclass(event)+"."
								+XSoftwareExtension.instance().extractCallermethod(event)))
				{
					topLevelTrace.add(event);
				}
			}
			nestedTraces.put(1, topLevelTrace);
			
			//for each top-level events, if it is a nested event, then add 
			for(int i=1;i<=n;i++)
			{	
				nestedTraces.put(i+1, factory.createTrace());
				//get the current level traces, start from the top-level (i =1)
				for(XEvent event: nestedTraces.get(i))
				{
					if(checkNesting(event,trace))
					{
						for(XEvent e:trace)
						{
							if(XSoftwareExtension.instance().extractClassObject(event).equals(XSoftwareExtension.instance().extractCallerclassobject(e))
								&&XConceptExtension.instance().extractName(event).equals(XSoftwareExtension.instance().extractCallermethod(e)))
							{
								nestedTraces.get(i+1).add(e);
							}
						}
						
					}
				}
			}
			
			//add all traces of different nesting levels to the current trace
			for(int i: nestedTraces.keySet())
			{
				temprace.addAll(nestedTraces.get(i));
			}
			
			//
			
			interfaceLog.add(temprace);
		}
		
		return interfaceLog;		
	}
	
	/*
	 * checking if an event is a nested event in a trace
	 */
	
	public static boolean checkNesting(XEvent event, XTrace trace)
	{
		//get the index of the current event. 
		int index  = trace.indexOf(event);
		System.out.println("caller: "+XConceptExtension.instance().extractName(event));
		for(int i = index; i<trace.size();i++)
		{
			if  (XSoftwareExtension.instance().extractClassObject(event).equals(XSoftwareExtension.instance().extractCallerclassobject(trace.get(i)))
					&&XConceptExtension.instance().extractName(event).equals(XSoftwareExtension.instance().extractCallermethod(trace.get(i)))
					&&!XConceptExtension.instance().extractName(event).equals(XConceptExtension.instance().extractName(trace.get(i))))
			{
				System.out.println("added nesting: "+XConceptExtension.instance().extractName(trace.get(i)));
				return true;
			}
		}
//		for (XEvent e: trace)
//		{
//			if (XSoftwareExtension.instance().extractClassObject(event).equals(XSoftwareExtension.instance().extractCallerclassobject(e))
//					&&XConceptExtension.instance().extractName(event).equals(XSoftwareExtension.instance().extractCallermethod(e))
//					&&!XConceptExtension.instance().extractName(event).equals(XConceptExtension.instance().extractName(e)))
//			{
//				return true;
//			}
//		}
		return false;
	}
	
	/*
	 * given a set of class and a component log, return the top-level method set of the component.
	 */
	public static HashSet<MethodClass> constructTopMethodSet(Set<ClassClass> cc, XLog componentLog)
	{
		//construct class set of the current component 
		HashSet<String> classSet = new HashSet<>();
		for(ClassClass c: cc)
		{
			classSet.add(c.getPackageName()+"."+c.getClassName());
		}
		HashSet<MethodClass> topMethodSet = new HashSet<>();
		
		for(XTrace trace: componentLog)
		{
			for(XEvent event: trace)
			{
				if(!classSet.contains(XSoftwareExtension.instance().extractCallerpackage(event)+"."+XSoftwareExtension.instance().extractCallerclass(event)))
				{
					MethodClass m =new MethodClass();
					m.setMethodName(XConceptExtension.instance().extractName(event));
					m.setClassName(XSoftwareExtension.instance().extractClass(event));
					m.setPackageName(XSoftwareExtension.instance().extractPackage(event));
					topMethodSet.add(m);
				}				
			}
		}
			
		return topMethodSet;
	}
	
	/*
	 * Given a component event log, return its caller method set. 
	 * For events in the component log, their callee methods belong to the cc.
	 * The caller methods set are those whose caller do not belong to the cc.
	 */
	public static HashSet<MethodClass> constructCallerMethodSet(Set<ClassClass> cc, XLog componentLog)
	{
		//construct class set of the current component 
		HashSet<String> classSet = new HashSet<>();
		for(ClassClass c: cc)
		{
			classSet.add(c.getPackageName()+"."+c.getClassName());
		}
		HashSet<MethodClass> callerMethodSet = new HashSet<>();
		
		for(XTrace trace: componentLog)
		{
			for(XEvent event: trace)
			{
				if(!classSet.contains(XSoftwareExtension.instance().extractCallerpackage(event)+"."+XSoftwareExtension.instance().extractCallerclass(event)))
				{
					MethodClass m =new MethodClass();
					m.setMethodName(XSoftwareExtension.instance().extractCallermethod(event));
					m.setClassName(XSoftwareExtension.instance().extractCallerclass(event));
					m.setPackageName(XSoftwareExtension.instance().extractCallerpackage(event));
					if(!callerMethodSet.contains(m))
					{
						callerMethodSet.add(m);
					}
				}				
			}
		}
		
		
		return callerMethodSet;
	}
	
	
	/*
	 * Given a component event log and a caller method, return its corresponding candidate interface, represented by its invoked top-level method calls.
	 */
	public static HashSet<MethodClass> constructCandidateInterface(XLog componentLog, MethodClass callerM)
	{
		HashSet<MethodClass> InterfaceMethodSet = new HashSet<>();
		
		for(XTrace trace: componentLog)
		{
			for(XEvent event: trace)
			{
				if(callerM.getPackageName().equals(XSoftwareExtension.instance().extractCallerpackage(event))
						&&callerM.getClassName().equals(XSoftwareExtension.instance().extractCallerclass(event))
								&&callerM.getMethodName().equals(XSoftwareExtension.instance().extractCallermethod(event)))
				{
					MethodClass m =new MethodClass();
					m.setMethodName(XConceptExtension.instance().extractName(event));
					m.setClassName(XSoftwareExtension.instance().extractClass(event));
					m.setPackageName(XSoftwareExtension.instance().extractPackage(event));
					if(!InterfaceMethodSet.contains(m))
					{
						InterfaceMethodSet.add(m);
					}
				}				
			}
		}
		
		return InterfaceMethodSet;
	}

	
	/*
	 * return the interface list by merging similar candidates
	 * oldInters is first assigned as empty and newInters is first as the candidates. 
	 */
	
	public static HashMap<HashSet<MethodClass>, HashSet<MethodClass>> recursiveComputing 
	(HashMap<HashSet<MethodClass>, HashSet<MethodClass>> interface2callerSet, 
			ArrayList<HashSet<MethodClass>> oldInters, 
			ArrayList<HashSet<MethodClass>> newInters, 
			double threshold)
	{
		
		System.out.println("recursion");
		System.out.println(interface2callerSet);
		System.out.println("old interface: "+oldInters);
		System.out.println("new interface: "+newInters);
//		if(newInters.equals(oldInters))
//		{
//			return newInters;
//		}
//		else{
			oldInters.clear();
			oldInters.addAll(newInters);
			newInters.clear();
			System.out.println("old interface: "+oldInters);
			System.out.println("new interface: "+newInters);	
			for(int i=0;i<oldInters.size();i++)
			{
				for(int j=i+1; j<oldInters.size();j++)
				{
					System.out.println("i="+i+",j="+j);
					//if the similarity is greater than the threshold, them merge them and add to new list
					double sim =similarityTwoInterfaceCandidate(oldInters.get(i),oldInters.get(j));
					System.out.println("minus"+(sim-threshold));

					if(sim-threshold>0)
					{
						//combine the ith and jth interface and add them to newInters.
						HashSet<MethodClass> mergeInterface = new HashSet<MethodClass>();
						mergeInterface.addAll(oldInters.get(i));
						mergeInterface.addAll(oldInters.get(j));
						newInters.add(mergeInterface);
						
						//combine the ith and jth caller method set and add to interface2callerset
						HashSet<MethodClass> mergeCaller = new HashSet<>();
						mergeCaller.addAll(interface2callerSet.get(oldInters.get(i)));
						mergeCaller.addAll(interface2callerSet.get(oldInters.get(j)));
						
						//remove the merged interface from the interface to caller mapping
						interface2callerSet.remove(oldInters.get(i));
						interface2callerSet.remove(oldInters.get(j));
						
//						System.out.println("simi satisfied merge interface: "+mergeInterface);
//						System.out.println("simi satisfied merge caller: "+mergeCaller);
						interface2callerSet.put(mergeInterface, mergeCaller);
						
						// also add the rest candidates (except ith and jth) to the newInters
						ArrayList<HashSet<MethodClass>> tempInters =new ArrayList<>();
						tempInters.addAll(oldInters);
						tempInters.remove(oldInters.get(i));
						tempInters.remove(oldInters.get(j));

						newInters.addAll(tempInters);
//						System.out.println("simi satisfied old interface: "+oldInters);
//						System.out.println("simi satisfied new interface: "+newInters);
//						System.out.println("simi satisfied mapping: "+interface2callerSet);
						recursiveComputing(interface2callerSet, oldInters, newInters, threshold);
					}
				}
			}// the resursion will stop when the similarity of any two candidate interfaces is less than the threshold.  
			
			return interface2callerSet;	
//		}
	}
	/*
	 * compute the similarity of two interface candidate
	 */
	public static double similarityTwoInterfaceCandidate(HashSet<MethodClass> group1, HashSet<MethodClass> group2)
	{	
		double sim= (double)interactionNumber(group1, group2)/(double)unionNumber(group1,group2);
		return sim;
	}
	
	
	/*
	 * the union number of elements of two hashset
	 */
	
	public static int unionNumber(HashSet<MethodClass> group1, HashSet<MethodClass> group2)
	{
		HashSet<MethodClass> temp1 = new HashSet<MethodClass>();
		temp1.addAll(group1);
		temp1.addAll(group2);
		return temp1.size();		
	}
	/*
	 * the interact number of elements  of two hashset
	 */
	public static int interactionNumber(HashSet<MethodClass> group1, HashSet<MethodClass> group2)
	{
		HashSet<MethodClass> temp1 = new HashSet<MethodClass>();
		temp1.addAll(group1);
		temp1.retainAll(group2);
		
		return temp1.size();
	}

	/*
	 * //refactoring the event log by identifying interface interfaces
	 */
	
	public static XLog constructInterfaceInstanceLog(XLog interfaceLog,XFactory factory, Set<ClassClass> cc)
	{
		// create log
		XLog interfaceInstanceLog =ConstructHLog.initialize(factory, "interface");
		
		//for each trace, we first construct its instance objects, return its connected graph. 
		for(XTrace trace: interfaceLog)
		{
			//get the interface instance set
			List connected = InterfaceInstances(trace,cc);
			for (int i=0;i<connected.size();i++)
	        {
				XTrace instanceTrace = factory.createTrace();
				for(XEvent event: trace)
				{
					if(((Set<String>)connected.get(i)).contains(XSoftwareExtension.instance().extractClassObject(event)))
					{
						instanceTrace.add(event);
					}
				}
				interfaceInstanceLog.add(instanceTrace);
	        }	
		}
		
		return interfaceInstanceLog;
				
	}
	
	//construct the instance for each interface
	public static List InterfaceInstances(XTrace trace, Set<ClassClass> cc)
	{
		//construct class set of the current component 
		HashSet<String> classSet = new HashSet<>();
		for(ClassClass c: cc)
		{
			classSet.add(c.getPackageName()+"."+c.getClassName());
		}
		
		// we first conctruct a connected graph
		 DirectedGraph<String, DefaultEdge> directedGraph =
		            new DefaultDirectedGraph<String, DefaultEdge>
		            (DefaultEdge.class);
		 
		 // traverse through each event in the case
		 for (XEvent event :trace)
		 {
			 directedGraph.addVertex(XSoftwareExtension.instance().extractClassObject(event));
			
			 //if the caller of this recording belongs to the component/interface.
			if (classSet.contains(XSoftwareExtension.instance().extractCallerpackage(event)+"."+XSoftwareExtension.instance().extractCallerclass(event)))
			{
				directedGraph.addVertex(XSoftwareExtension.instance().extractCallerclassobject(event));
				// add an arc from caller to callee
				directedGraph.addEdge(XSoftwareExtension.instance().extractClassObject(event), 
						XSoftwareExtension.instance().extractCallerclassobject(event));
			}
		 }	
		 
		//compute all weakly connected component
        ConnectivityInspector ci = new ConnectivityInspector(directedGraph);
        
        //Returns a list of Set s, where each set contains all vertices that are in the same maximally connected component.
        java.util.List connected = ci.connectedSets();
        return connected;        
	}
		
	
}
