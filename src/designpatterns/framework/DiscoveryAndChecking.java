package designpatterns.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import congliu.processmining.softwareprocessmining.Component2Classes;
import designpatterns.adapterpattern.ClassTypeHierarchy;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;
/*
 * this class defines how to discover missing roles by using 
 * (1) log-level constraints,(2) candidate, and (3) event log, as inputs.  
 * 
 * then checking if the constraints are satisfied for each candidate pattern instances. 
 */
public class DiscoveryAndChecking {

	//for each log-level constraints, we discover all missing roles
	public static HashMap<String, ArrayList<Object>> discovery(Constraint lc, HashSet<String> roleSetWithValue, HashSet<String> roleSetNoValue, 
				HashMap<String, ArrayList<Object>> role2values, XLog softwareLog)
		{
			//i.e., if the first role does not have value and the second role has value
			if(roleSetNoValue.contains(((LogConstraint)lc).getFirstRole())&&
					roleSetWithValue.contains(((LogConstraint)lc).getSecondRole())){
				//if the constraint type is include
				if(((LogConstraint)lc).getType().equals("include"))
				{				
					if(((LogConstraint)lc).getRelation().equals("methodSet"))//method set
					{
						//get the method set of current class
						HashSet<MethodClass> mc=BasicOperators.MethodSetofClass((ClassClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(0), softwareLog);
						//add the method set to the value of the first role
						for(MethodClass tempM: mc)
						{
							if(!role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempM))
							{
								role2values.get(((LogConstraint)lc).getFirstRole()).add(tempM);
							}
							
						}
					}
					else if (((LogConstraint)lc).getRelation().equals("parameterSet")) {// parameter set
						//get the parameter set of current method
						HashSet<ClassClass> pm=BasicOperators.ParameterSetofMethod((MethodClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(0), softwareLog);
						//add the parameter set to the value of the first role
						for(ClassClass tempC: pm)
						{
							if(!role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempC))
							{
								role2values.get(((LogConstraint)lc).getFirstRole()).add(tempC);
							}
						}
					}
					else{// invokeset
						//get the invoked method set of the current method
						HashSet<MethodClass> mm=BasicOperators.MethodSetofMethod((MethodClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(0), softwareLog);
						//add the invoked set to 
						for(MethodClass tempM: mm)
						{
							if(!role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempM))
							{
								role2values.get(((LogConstraint)lc).getFirstRole()).add(tempM);
							}
							
						}
					}
					
				}
				
				else{// for exculde case
					//if the constraint type is include
					if(((LogConstraint)lc).getType().equals("include"))
					{				
						if(((LogConstraint)lc).getRelation().equals("methodSet"))//method set
						{
							//get the method set of current class
							HashSet<MethodClass> mc=BasicOperators.MethodSetofClass((ClassClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(0), softwareLog);
							//add the method set to the value of the first role
							for(MethodClass tempM: mc){
								if(role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempM)){// if is included in the candidate value, remove it
									
									if(!role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempM))
									{
										role2values.get(((LogConstraint)lc).getFirstRole()).add(tempM);
									}
								}
							}
						}
						else if (((LogConstraint)lc).getRelation().equals("parameterSet")){// parameter set
							//get the parameter set of current method
							HashSet<ClassClass> pm=BasicOperators.ParameterSetofMethod((MethodClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(0), softwareLog);
							//add the parameter set to the value of the first role
							for(ClassClass tempC: pm){
								if(role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempC)){
									if(!role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempC))
									{
										role2values.get(((LogConstraint)lc).getFirstRole()).add(tempC);
									}
									
								}
							}
						}
						else{// invokeset
							//get the invoked method set of the current method
							HashSet<MethodClass> mm=BasicOperators.MethodSetofMethod((MethodClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(0), softwareLog);
							//add the invoked set to 
							for(MethodClass tempM: mm){
								if(role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempM)){
									if(!role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempM))
									{
										role2values.get(((LogConstraint)lc).getFirstRole()).add(tempM);
									}
									
								}	
							}
						}
					}
				}
			}
			return role2values;
		}//discovery
		
		
		
	//for each pattern instance, we check if the log-level constraints are satisfied
	//for missing roles, we assume they satisfy the current constrsint. 
	public static boolean LoglevelChecking(Set<String> missingRoles, HashSet<Constraint> logconstraints, 
			XLog softwareLog, HashMap<String, Object> candidateInstance)
	{
		for(Constraint lc: logconstraints)
		{
			if(missingRoles.contains(((LogConstraint)lc).getFirstRole())||missingRoles.contains(((LogConstraint)lc).getSecondRole()))
			{
				System.out.println("constraint is trivally hold: "+lc);
				continue;
			}
			System.out.println(lc);
			
			if(((LogConstraint)lc).getType().equals("include"))
			{				
				if(((LogConstraint)lc).getRelation().equals("methodSet"))//method set
				{
					//get the method set of current class (the second role)
					HashSet<MethodClass> mc=BasicOperators.MethodSetofClass((ClassClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), softwareLog);
					
					//if the value of the first role belongs to the method set of second role, then continue
					if(mc.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
					{
						continue;
					}
					else{
						return false;
					}
				}
				else if(((LogConstraint)lc).getRelation().equals("parameterSet"))
				{
					//get the parameter set of current method
					HashSet<ClassClass> pm=BasicOperators.ParameterSetofMethod((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), softwareLog);
					//if the value of the first role belongs to the class set of second role, then continue
					if(pm.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
					{
						continue;
					}
					else{
						return false;
					}
				}
				else {// relation is invokeSet.
					//get the invoked method set of the current method
					HashSet<MethodClass> mm=BasicOperators.MethodSetofMethod((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), softwareLog);
					//if the value of the first role belongs to the class set of second role, then continue
					if(mm.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
					{
						continue;
					}
					else{
						return false;
					}
				}
			}
			else //for the exclude case
			{
				if(((LogConstraint)lc).getRelation().equals("methodSet"))//method set
				{
					//get the method set of current class (the second role)
					HashSet<MethodClass> mc=BasicOperators.MethodSetofClass((ClassClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), softwareLog);
					//if(mc.size()==0||candidateInstance.get(((LogConstraint)lc).getFirstRole())==null||mc.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
					if(mc.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
					{
						return false;
					}
					else{
						continue;
					}
				}
				else if(((LogConstraint)lc).getRelation().equals("parameterSet"))
				{
					//get the parameter set of current method
					HashSet<ClassClass> pm=BasicOperators.ParameterSetofMethod((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), softwareLog);
					//if the value of the first role belongs to the class set of second role, then continue
					
					if(pm.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
					{
						return false;
					}
					else{
						continue;
					}
				}
				else {// relation is invokeSet.
					//get the invoked method set of the current method
					HashSet<MethodClass> mm=BasicOperators.MethodSetofMethod((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), softwareLog);
					//if the value of the first role belongs to the class set of second role, then continue
					if(mm.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
					{
						return false;
					}
					else{
						continue;
					}
				}
			}
		}
		return true;
	}
	
	
	//for each pattern instance, we check if the temporal-level/invocation constraints are satisfied
	public static boolean InvocationlevelChecking(Set<String> missingRoles, HashSet<Constraint> invocationConstraints, 
			XLog softwareLog, HashMap<String, Object> candidateInstance)
	{
		for(Constraint c: invocationConstraints)
		{			
			if((c.getType().equals("temporal")))//checking the temporal constraints
			{	
				System.out.println(c);
				
				if(missingRoles.contains(((TemporaConstraint)c).getFirstRole())||missingRoles.contains(((TemporaConstraint)c).getSecondRole()))
				{
					System.out.println("constraint is trivally hold: "+c);
					continue;
				}
				

				MethodClass firstM=(MethodClass)candidateInstance.get(((TemporaConstraint)c).getFirstRole());
				MethodClass secondM=(MethodClass)candidateInstance.get(((TemporaConstraint)c).getSecondRole());
				for(XTrace trace: softwareLog)
				{
					//if these two methods are included in the current trace
					if(BasicOperators.MethodIncludedTrace(firstM, trace) && BasicOperators.MethodIncludedTrace(secondM, trace))
					{
						//get the earlist occurance time of each method.
						if(BasicOperators.MethodEarliestTime(firstM, trace)-BasicOperators.MethodEarliestTime(secondM, trace)>=0)
						{
							return false;
						}
					}
				}
			}
			else{//checking the invocation constraints
				System.out.println(c);
				if(missingRoles.contains(((InvocationConstraint)c).getFirstRole())||missingRoles.contains(((InvocationConstraint)c).getSecondRole()))
				{
					System.out.println("constraint is trivally hold: "+c);
					continue;
				}
				
				MethodClass firstM=(MethodClass)candidateInstance.get(((InvocationConstraint)c).getFirstRole());
				MethodClass secondM=(MethodClass)candidateInstance.get(((InvocationConstraint)c).getSecondRole());
				if(((InvocationConstraint)c).getCardinality().equals("multiple"))//cardinality is multiple
				{
					for(XTrace trace: softwareLog)
					{
						//if these two methods are included in the current trace
						if(BasicOperators.MethodIncludedTrace(firstM, trace) && BasicOperators.MethodIncludedTrace(secondM, trace))
						{
							//each caller object 
							for(String obj: BasicOperators.ObjectSetMethod(firstM,trace))
							{
								if(BasicOperators.ObjectSetCalleeMethod(firstM, obj, secondM, trace).size()<1)
								{
									return false;
								}
							}
						}
					}
				}
				else {
					for(XTrace trace: softwareLog)
					{
						//if these two methods are included in the current trace
						if(BasicOperators.MethodIncludedTrace(firstM, trace) && BasicOperators.MethodIncludedTrace(secondM, trace))
						{
							//each caller object 
							for(String obj: BasicOperators.ObjectSetMethod(firstM,trace))
							{
								if(BasicOperators.ObjectSetCalleeMethod(firstM, obj, secondM, trace).size()!=1)
								{
									return false;
								}
							}
						}
					}
				}
			}
		}
		
		return true;
		
	}
	//for each pattern instance, we check if the temporal-level/invocation/number constraints are satisfied
		public static boolean InvocationlevelCheckingV1(Set<String> missingRoles, HashSet<Constraint> invocationConstraints, 
				XTrace invocationTrace, HashMap<String, Object> candidateInstance)
		{
			for(Constraint c: invocationConstraints)
			{			
				if((c.getType().equals("temporal")))//checking the temporal constraints
				{	
					System.out.println(c);
					
					if(missingRoles.contains(((TemporaConstraint)c).getFirstRole())||missingRoles.contains(((TemporaConstraint)c).getSecondRole()))
					{
						System.out.println("constraint is trivally hold: "+c);
						continue;
					}
					

					MethodClass firstM=(MethodClass)candidateInstance.get(((TemporaConstraint)c).getFirstRole());
					MethodClass secondM=(MethodClass)candidateInstance.get(((TemporaConstraint)c).getSecondRole());
					
					//if these two methods are included in the current trace
					if(BasicOperators.MethodIncludedTrace(firstM, invocationTrace) && BasicOperators.MethodIncludedTrace(secondM, invocationTrace))
					{
						//get the earlist occurance time of each method.
						if(BasicOperators.MethodEarliestTime(firstM, invocationTrace)-BasicOperators.MethodEarliestTime(secondM, invocationTrace)>=0)
						{
							return false;
						}
					}
					
				}
				else if ((c.getType().equals("number")))//checking the number constraints
				{
					System.out.println(c);
					if(missingRoles.contains(((NumberConstraint)c).getFirstRole())||missingRoles.contains(((NumberConstraint)c).getSecondRole()))
					{
						System.out.println("constraint is trivally hold: "+c);
						continue;
					}
					
					MethodClass firstM=(MethodClass)candidateInstance.get(((NumberConstraint)c).getFirstRole());
					MethodClass secondM=(MethodClass)candidateInstance.get(((NumberConstraint)c).getSecondRole());
					
					//if these two methods are included in the current trace
					if(BasicOperators.MethodIncludedTrace(firstM, invocationTrace) && BasicOperators.MethodIncludedTrace(secondM, invocationTrace))
					{
						//get the number of each method
						if(BasicOperators.MethodNumberIncludedTrace(firstM, invocationTrace)-BasicOperators.MethodNumberIncludedTrace(secondM, invocationTrace)!=0)
						{
							return false;
						}
					}
				}
				else{//checking the invocation constraints
					System.out.println(c);
					if(missingRoles.contains(((InvocationConstraint)c).getFirstRole())||missingRoles.contains(((InvocationConstraint)c).getSecondRole()))
					{
						System.out.println("constraint is trivally hold: "+c);
						continue;
					}
					
					MethodClass firstM=(MethodClass)candidateInstance.get(((InvocationConstraint)c).getFirstRole());
					MethodClass secondM=(MethodClass)candidateInstance.get(((InvocationConstraint)c).getSecondRole());
					if(((InvocationConstraint)c).getCardinality().equals("multiple"))//cardinality is multiple
					{	
						//if these two methods are included in the current trace
						if(BasicOperators.MethodIncludedTrace(firstM, invocationTrace) && BasicOperators.MethodIncludedTrace(secondM, invocationTrace))
						{
							//each caller object 
							for(String obj: BasicOperators.ObjectSetMethod(firstM,invocationTrace))
							{
								if(BasicOperators.ObjectSetCalleeMethod(firstM, obj, secondM, invocationTrace).size()<1)
								{
									return false;
								}
							}
						}
					}
					else {
						//if these two methods are included in the current trace
						if(BasicOperators.MethodIncludedTrace(firstM, invocationTrace) && BasicOperators.MethodIncludedTrace(secondM, invocationTrace))
						{
							//each caller object 
							for(String obj: BasicOperators.ObjectSetMethod(firstM,invocationTrace))
							{
								if(BasicOperators.ObjectSetCalleeMethod(firstM, obj, secondM, invocationTrace).size()!=1)
								{
									return false;
								}
							}
						}
						
					}
				}
			}
			
			return true;
			
		}	
	/**
	 * this method aims to extracting software event log by identifying pattern instance invocations. 
	 
	 */
	public static XLog generatingPatternInstanceEventLog(ClassClass mainRoleClass, Component2Classes com2class,XLog originalLog, XFactory factory)
	{
		// create log
		XLog componentLog =ConstructHLog.initialize(factory, com2class.getComponent());
		
		for(XTrace trace: originalLog)
		{
			XTrace tempTrace = factory.createTrace();
			for(XEvent event: trace)
			{
				// filtering the trace according to the component classes
				
				if(com2class.getClasses().contains(XSoftwareExtension.instance().extractClass(event)))
				{
					tempTrace.add(event);
				}
			}
			// identify component instances for the filtered trace
			
			// create new traces (each corresponds to one component instance)
			HashMap<String, Set<String>> componentInstance2objectset =new HashMap<String, Set<String>>(); 

//			componentInstance2objectset =SoftwareComponentInteractionBehaviorDiscoveryPlugin.InterfaceInstance2Objects(tempTrace,
//					com2class.getClasses());
			
			componentInstance2objectset= DiscoveryAndChecking.InterfaceInstance2Objects(tempTrace, mainRoleClass, com2class.getClasses());
			
			// create instance trace and add to component Log. 
			for(String comIns:componentInstance2objectset.keySet())
			{
				XTrace insTrace = factory.createTrace();
				for(XEvent e: tempTrace)
				{
					if (componentInstance2objectset.get(comIns).contains(XSoftwareExtension.instance().extractClassObject(e)))
					{
						insTrace.add(e);
					}
				}	
				componentLog.add(insTrace);
			}
			
			
		}
		return componentLog;
	}
	
	//for each trace, we identify the interface instance that refers to an invocation
	//the first step is to construct the connected graph, the second step is to make the main role (class) has only one object in each sub-graph. 
	//construct the instance for each interface
	public static HashMap<String, Set<String>> InterfaceInstance2Objects(XTrace trace, ClassClass mainRoleClass, ArrayList<String> classList)
	{
		//get the object set of the mainrole class, the assumption is that each invocation only have one object of mainrole class. 
		HashSet<String> mainRoleObjs = new HashSet<>();
		
		// we first conctruct a connected graph
		 DirectedGraph<String, DefaultEdge> directedGraph =
		            new DefaultDirectedGraph<String, DefaultEdge>
		            (DefaultEdge.class);
		 
		 // traverse through each event in the case
		 for (XEvent event :trace)
		 {
			 if(!XSoftwareExtension.instance().extractClassObject(event).equals("0"))//remove the effect of static methods
			 {
				 //generate all main role class objects
				 if(XSoftwareExtension.instance().extractClass(event).equals(mainRoleClass.getClassName())
						 && XSoftwareExtension.instance().extractPackage(event).equals(mainRoleClass.getPackageName()))
				 {
					 mainRoleObjs.add(XSoftwareExtension.instance().extractClassObject(event));
				 }
				 
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
		 
		//compute all weakly connected component
        ConnectivityInspector ci = new ConnectivityInspector(directedGraph);
        
        //Returns a list of Set s, where each set contains 
        //all vertices that are in the same maximally connected component.
        java.util.List connected = ci.connectedSets();
        HashMap<String, Set<String>> interIns2Objs = new HashMap<String, Set<String>>();
        
        for (int i=0;i<connected.size();i++)
        {
        	//check if there is more than one main role class object in each connected component
        	//get the main role class object set of the current instance/connected component.
        	HashSet<String> sharedMainRoleClassObjs = new HashSet<>();
        	sharedMainRoleClassObjs.addAll(mainRoleObjs);
        	sharedMainRoleClassObjs.retainAll((Set<String>)connected.get(i));//get the shared and store in sharedMainRoleClassObjs.
        	if(sharedMainRoleClassObjs.size()==1)
        	{
        		interIns2Objs.put(i+XConceptExtension.instance().extractName(trace), (Set<String>)connected.get(i));
        	}
        	else if (sharedMainRoleClassObjs.size()>1){
        		//for each mainrole class objs, we create a new instance object set
        		for(String mainObj: sharedMainRoleClassObjs)
        		{
        			
        			interIns2Objs.put(XConceptExtension.instance().extractName(trace),getConnectedObjects(trace,mainObj));
        		}
			}
        	else {
				System.out.println("<<<Invalid pattern instance invocation: no main role found!>>>");
			}
        }
        
        return interIns2Objs;
	}
	
	/*
	 * Given a caller object, we only consider its invoked/callee object, suitable for observer patterns. 
	 * we do not include its caller object. can be expanded for other patterns. 
	 */
	public static Set<String> getConnectedObjects(XTrace trace, String CallerObj)
	{
		HashSet<String> invocations = new HashSet<>();
		invocations.add(CallerObj);
		
		for(XEvent event: trace)
		{
			if (XSoftwareExtension.instance().extractCallerclassobject(event).equals(CallerObj))
			{
				invocations.add(XSoftwareExtension.instance().extractClassObject(event));
			}
		}
		return invocations;
	}
	
	//for each log-level constraints, we discover all missing roles. 
	//for roles whose value is not included in the log, we find the alternative using the class type hierarchy information. 
	public static HashMap<String, ArrayList<Object>> discoveryV1(Constraint lc, HashSet<String> roleSetWithValue, HashSet<String> roleSetNoValue, 
				HashMap<String, ArrayList<Object>> role2values, XLog softwareLog, ClassTypeHierarchy cth)
		{
			//i.e., if the first role does not have value and the second role has value
			if(roleSetNoValue.contains(((LogConstraint)lc).getFirstRole())&&
					roleSetWithValue.contains(((LogConstraint)lc).getSecondRole())){
				//if the constraint type is include
				if(((LogConstraint)lc).getType().equals("include"))
				{				
					if(((LogConstraint)lc).getRelation().equals("methodSet"))//method set
					{
						//get the method set of current candidate classes
						HashSet<MethodClass> mc =new HashSet<>();
//						System.out.println("Before replacing: "+role2values.get(((LogConstraint)lc).getSecondRole()));
						for(int i=0;i<role2values.get(((LogConstraint)lc).getSecondRole()).size();i++)// for each candidate value
						{
							if(BasicOperators.classIncludedInLog((ClassClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(i), softwareLog))
							{
								mc.addAll(BasicOperators.MethodSetofClass((ClassClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(i), softwareLog));
							}
						}

						
						//add the method set to the value of the first role
						for(MethodClass tempM: mc)
						{
							if(!role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempM))
							{
								role2values.get(((LogConstraint)lc).getFirstRole()).add(tempM);
							}
							
						}
					}
					else if (((LogConstraint)lc).getRelation().equals("parameterSet")) {// parameter set
						//get the parameter set of current method
						HashSet<ClassClass> pm =new HashSet<>();
//						System.out.println("Before replacing: "+role2values.get(((LogConstraint)lc).getSecondRole()).get(0));

						for(int i=0;i<role2values.get(((LogConstraint)lc).getSecondRole()).size();i++)// for each candidate value
						{
							if(BasicOperators.methodIncludedInLog((MethodClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(i), softwareLog))
							{
								pm.addAll(BasicOperators.ParameterSetofMethod((MethodClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(i), softwareLog));	
							}
						}

						//add the parameter set to the value of the first role
						for(ClassClass tempC: pm)
						{
							if(!role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempC))
							{
								role2values.get(((LogConstraint)lc).getFirstRole()).add(tempC);
							}
						}
					}
					else
					{// invokeset
						//get the invoked method set of the current method
						HashSet<MethodClass> mm =new HashSet<>();
//						System.out.println("Before replacing: "+role2values.get(((LogConstraint)lc).getSecondRole()).get(0));
						
						for(int i=0;i<role2values.get(((LogConstraint)lc).getSecondRole()).size();i++)// for each candidate value
						{
							if(BasicOperators.methodIncludedInLog((MethodClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(i), softwareLog))
							{
								mm=BasicOperators.MethodSetofMethod((MethodClass)role2values.get(((LogConstraint)lc).getSecondRole()).get(i), softwareLog);
							}
						}

						//add the invoked set to 
						for(MethodClass tempM: mm)
						{
							if(!role2values.get(((LogConstraint)lc).getFirstRole()).contains(tempM))
							{
								role2values.get(((LogConstraint)lc).getFirstRole()).add(tempM);
							}
							
						}
					}
					
				}
				
//				else
//				{// for exclude case			
//				}
			}
			return role2values;
		}//discovery
	
	//for each pattern instance, we check if the log-level constraints are satisfied
		//for missing roles, we assume they satisfy the current constrsint. 
		public static boolean LoglevelCheckingV1(Set<String> missingRoles, HashSet<Constraint> logconstraints, 
				XLog softwareLog, HashMap<String, Object> candidateInstance, ClassTypeHierarchy cth)
		{
			for(Constraint lc: logconstraints)
			{
				if(missingRoles.contains(((LogConstraint)lc).getFirstRole())||missingRoles.contains(((LogConstraint)lc).getSecondRole()))
				{
					System.out.println("constraint is trivally hold: "+lc);
					continue;
				}
				System.out.println(lc);
				
				if(((LogConstraint)lc).getType().equals("include"))
				{				
					if(((LogConstraint)lc).getRelation().equals("methodSet"))//method set
					{
						//get the method set of current class (the second role), if it is not included in the log, then use class type hierarchy
						HashSet<ClassClass> allClasses =BasicOperators.getAllTypeHierarchyClasses((ClassClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), cth);
						HashSet<MethodClass> mc= new HashSet<>();
						for(ClassClass c: allClasses)
						{
							mc.addAll(BasicOperators.MethodSetofClass(c, softwareLog));
						}

//
//						System.out.println("Role:"+((LogConstraint)lc).getFirstRole());
//						System.out.println("Role value:"+candidateInstance.get(((LogConstraint)lc).getFirstRole()));
//						System.out.println("Method Set:"+mc);
						if(mc.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							continue;
						}
						else{
							return false;
						}
					}
					else if(((LogConstraint)lc).getRelation().equals("parameterSet"))
					{
						//get the parameter set of current method
						HashSet<ClassClass> pm=null;
						if(BasicOperators.methodIncludedInLog((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), softwareLog))
						{
							pm=BasicOperators.ParameterSetofMethod((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), softwareLog);	
						}
						else{// the current method is not in the log, we need to search its type hierarchy return its parent/child class
							MethodClass alternativeMethod =BasicOperators.typeHierarchyMethod(cth, softwareLog, (MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()));
							pm=BasicOperators.ParameterSetofMethod(alternativeMethod, softwareLog);
							
						}

						HashSet<ClassClass> allParameterTypes = new HashSet<>();
						for(ClassClass c: pm)
						{
							allParameterTypes.addAll(BasicOperators.getAllTypeHierarchyClasses(c, cth));
						}

						if(allParameterTypes.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							continue;
						}
						else{
							return false;
						}
					}
					
					else {// relation is invokeSet.
						//get the invoked method set of the current method
						HashSet<MethodClass> mm=new HashSet<>();
						ClassClass callerClass = BasicOperators.MethodToClass((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()));
						String methodName = ((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole())).getMethodName();
						HashSet<ClassClass> allCallerClasses =BasicOperators.getAllTypeHierarchyClasses(callerClass, cth);
						
						for(ClassClass c: allCallerClasses)
						{
							MethodClass callerMethod = BasicOperators.ClassToMethod(c);
							callerMethod.setMethodName(methodName);
							mm.addAll(BasicOperators.MethodSetofMethod(callerMethod, softwareLog));
						}
					
						
						if(mm.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							continue;
						}
						else{
							return false;
						}
					}
				}
				else //for the exclude case
				{
					if(((LogConstraint)lc).getRelation().equals("methodSet"))//method set
					{
						//get the method set of current class (the second role)
						HashSet<MethodClass> mc=BasicOperators.MethodSetofClass((ClassClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), softwareLog);
						//if(mc.size()==0||candidateInstance.get(((LogConstraint)lc).getFirstRole())==null||mc.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						if(mc.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							return false;
						}
						else{
							continue;
						}
					}
					else if(((LogConstraint)lc).getRelation().equals("parameterSet"))
					{
						//get the parameter set of current method
						HashSet<ClassClass> pm=BasicOperators.ParameterSetofMethod((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), softwareLog);
						//if the value of the first role belongs to the class set of second role, then continue
						
						if(pm.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							return false;
						}
						else{
							continue;
						}
					}
					else {// relation is invokeSet.
						//get the invoked method set of the current method
						HashSet<MethodClass> mm=BasicOperators.MethodSetofMethod((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), softwareLog);
						//if the value of the first role belongs to the class set of second role, then continue
						if(mm.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							return false;
						}
						else{
							continue;
						}
					}
				}
			}
			return true;
		}
		
		//for each pattern instance, we check if the log-level constraints are satisfied
		//for missing roles, we assume they satisfy the current constrsint. 
		public static boolean LoglevelCheckingV2(Set<String> missingRoles, HashSet<Constraint> logconstraints, 
				XTrace invocationTrace, HashMap<String, Object> candidateInstance, ClassTypeHierarchy cth)
		{
			for(Constraint lc: logconstraints)
			{
				if(missingRoles.contains(((LogConstraint)lc).getFirstRole())||missingRoles.contains(((LogConstraint)lc).getSecondRole()))
				{
					System.out.println("constraint is trivally hold: "+lc);
					continue;
				}
				System.out.println(lc);
				
				if(((LogConstraint)lc).getType().equals("include"))
				{				
					if(((LogConstraint)lc).getRelation().equals("methodSet"))//method set
					{
						//get the method set of current class (the second role), if it is not included in the log, then use class type hierarchy
						HashSet<ClassClass> allClasses =BasicOperators.getAllTypeHierarchyClasses((ClassClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), cth);
						System.out.println("ALl classes: "+allClasses);
						System.out.println("number of event per invocation"+ invocationTrace.size());
						HashSet<MethodClass> mc= new HashSet<>();
						for(ClassClass c: allClasses)
						{
							mc.addAll(BasicOperators.MethodSetofClassPerTrace(c, invocationTrace));
							
						}
						
						System.out.println("ALl method set: "+mc);
//
//						if(!BasicOperators.methodIncludedInTrace((MethodClass)candidateInstance.get(((LogConstraint)lc).getFirstRole()), invocationTrace))// the method is not included in the log
//						{
//							MethodClass alternativeMethod =BasicOperators.typeHierarchyMethodPerTrace(cth, invocationTrace, (MethodClass)candidateInstance.get(((LogConstraint)lc).getFirstRole()));
//							System.out.println("alternative method: "+alternativeMethod);
//							
//							if(mc.contains(alternativeMethod))
//							{
//								continue;
//							}
//							return false;
//						}
//						else{
//							if(mc.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
//							{
//								continue;
//							}
//							else{
//								return false;
//							}
//						}
						
						
						if(mc.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							continue;
						}
						else{
							return false;
						}
					}
					else if(((LogConstraint)lc).getRelation().equals("parameterSet"))
					{
						//get the parameter set of current method
						HashSet<ClassClass> pm=null;
						if(BasicOperators.methodIncludedInTrace((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), invocationTrace))
						{
							pm=BasicOperators.ParameterSetofMethodPerTrace((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), invocationTrace);	
						}
						else{// the current method is not in the log, we need to search its type hierarchy return its parent/child class
							MethodClass alternativeMethod =BasicOperators.typeHierarchyMethodPerTrace(cth, invocationTrace, (MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()));
							pm=BasicOperators.ParameterSetofMethodPerTrace(alternativeMethod, invocationTrace);
							
						}

						HashSet<ClassClass> allParameterTypes = new HashSet<>();
						for(ClassClass c: pm)
						{
							allParameterTypes.addAll(BasicOperators.getAllTypeHierarchyClasses(c, cth));
						}

						if(allParameterTypes.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							continue;
						}
						else{
							return false;
						}
					}
					
					else {// relation is invokeSet.
						//get the invoked method set of the current method
						HashSet<MethodClass> mm=new HashSet<>();
						ClassClass callerClass = BasicOperators.MethodToClass((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()));
						String methodName = ((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole())).getMethodName();
						HashSet<ClassClass> allCallerClasses =BasicOperators.getAllTypeHierarchyClasses(callerClass, cth);
						
						for(ClassClass c: allCallerClasses)
						{
							MethodClass callerMethod = BasicOperators.ClassToMethod(c);
							callerMethod.setMethodName(methodName);
							mm.addAll(BasicOperators.MethodSetofMethodPerTrace(callerMethod, invocationTrace));
						}
					
						
						if(mm.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							continue;
						}
						else{
							return false;
						}
					}
				}
				else //for the exclude case
				{
					if(((LogConstraint)lc).getRelation().equals("methodSet"))//method set
					{
						//get the method set of current class (the second role)
						HashSet<MethodClass> mc=BasicOperators.MethodSetofClassPerTrace((ClassClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), invocationTrace);
						//if(mc.size()==0||candidateInstance.get(((LogConstraint)lc).getFirstRole())==null||mc.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						if(mc.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							return false;
						}
						else{
							continue;
						}
					}
					else if(((LogConstraint)lc).getRelation().equals("parameterSet"))
					{
						//get the parameter set of current method
						HashSet<ClassClass> pm=BasicOperators.ParameterSetofMethodPerTrace((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), invocationTrace);
						//if the value of the first role belongs to the class set of second role, then continue
						
						if(pm.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							return false;
						}
						else{
							continue;
						}
					}
					else {// relation is invokeSet.
						//get the invoked method set of the current method
						HashSet<MethodClass> mm=BasicOperators.MethodSetofMethodPerTrace((MethodClass)candidateInstance.get(((LogConstraint)lc).getSecondRole()), invocationTrace);
						//if the value of the first role belongs to the class set of second role, then continue
						if(mm.contains(candidateInstance.get(((LogConstraint)lc).getFirstRole())))
						{
							return false;
						}
						else{
							continue;
						}
					}
				}
			}
			return true;
		}
		
		/**
		 * this method aims to extracting software event log by identifying pattern instance invocations. 
		 
		 */
		public static XLog generatingPatternInstanceEventLogV1(HashSet<ClassClass> mainRoleClasses, Component2Classes com2class, XLog originalLog, XFactory factory)
		{
			// create log
			XLog componentLog =ConstructHLog.initialize(factory, com2class.getComponent());
			
			for(XTrace trace: originalLog)
			{
				XTrace tempTrace = factory.createTrace();
				for(XEvent event: trace)
				{
					// filtering the trace according to the component classes
					
					if(com2class.getClasses().contains(XSoftwareExtension.instance().extractClass(event)))
					{
						tempTrace.add(event);
					}
				}
				// identify component instances for the filtered trace
				
				// create new traces (each corresponds to one component instance)
				HashMap<String, Set<String>> componentInstance2objectset =new HashMap<String, Set<String>>(); 

				
				componentInstance2objectset= DiscoveryAndChecking.InterfaceInstance2ObjectsV1(tempTrace, mainRoleClasses, com2class.getClasses());
				
				// create instance trace and add to component Log. 
				for(String comIns:componentInstance2objectset.keySet())
				{
					XTrace insTrace = factory.createTrace();
					for(XEvent e: tempTrace)
					{
						if (componentInstance2objectset.get(comIns).contains(XSoftwareExtension.instance().extractClassObject(e)))
						{
							insTrace.add(e);
						}
					}	
					componentLog.add(insTrace);
				}
				
				
			}
			return componentLog;
		}
		
		//for each trace, we identify the interface instance that refers to an invocation
		//the first step is to construct the connected graph, the second step is to make the main role (class) has only one object in each sub-graph. 
		//construct the instance for each interface
		public static HashMap<String, Set<String>> InterfaceInstance2ObjectsV1(XTrace trace, HashSet<ClassClass> mainRoleClasses, ArrayList<String> classList)
		{
			//get the object set of the mainrole class, the assumption is that each invocation only have one object of mainrole class. 
			HashSet<String> mainRoleObjs = new HashSet<>();
			
			// we first conctruct a connected graph
			 DirectedGraph<String, DefaultEdge> directedGraph =
			            new DefaultDirectedGraph<String, DefaultEdge>
			            (DefaultEdge.class);
			 
			 HashSet<String> classSet= new HashSet<>();
			 for(ClassClass c: mainRoleClasses)
			 {
				 classSet.add(c.toString());
			 }
			 
			 // traverse through each event in the case
			 for (XEvent event :trace)
			 {
				 if(!XSoftwareExtension.instance().extractClassObject(event).equals("0"))//remove the effect of static methods whose object id is 0
				 {
					 //generate all main role class objects
					 if(classSet.contains( XSoftwareExtension.instance().extractPackage(event)+"."+XSoftwareExtension.instance().extractClass(event)))
//					 if(XSoftwareExtension.instance().extractClass(event).equals(mainRoleClass.getClassName())
//							 && XSoftwareExtension.instance().extractPackage(event).equals(mainRoleClass.getPackageName()))
					 {
						 mainRoleObjs.add(XSoftwareExtension.instance().extractClassObject(event));
					 }
					 
					 directedGraph.addVertex(XSoftwareExtension.instance().extractClassObject(event));
						//if the caller of this recording belongs to the component.
						if (classList.contains(XSoftwareExtension.instance().extractCallerclass(event)))
						{
							directedGraph.addVertex(XSoftwareExtension.instance().extractCallerclassobject(event));
							// add an arc from caller to callee
							directedGraph.addEdge(XSoftwareExtension.instance().extractCallerclassobject(event), 
									XSoftwareExtension.instance().extractClassObject(event));
						}
				 }
			 }	
			 
			//compute all weakly connected component
	        ConnectivityInspector ci = new ConnectivityInspector(directedGraph);
	        
	        //Returns a list of Set s, where each set contains 
	        //all vertices that are in the same maximally connected component.
	        java.util.List connected = ci.connectedSets();
	        HashMap<String, Set<String>> interIns2Objs = new HashMap<String, Set<String>>();
	        
	        int count=0;
	        for (int i=0;i<connected.size();i++)
	        {
	        	//check if there is more than one main role class object in each connected component
	        	//get the main role class object set of the current instance/connected component.
	        	HashSet<String> sharedMainRoleClassObjs = new HashSet<>();
	        	sharedMainRoleClassObjs.addAll(mainRoleObjs);
	        	sharedMainRoleClassObjs.retainAll((Set<String>)connected.get(i));//get the shared and store in sharedMainRoleClassObjs.
	        	if(sharedMainRoleClassObjs.size()==1)
	        	{
	        		interIns2Objs.put(XConceptExtension.instance().extractName(trace), (Set<String>)connected.get(i));
	        	}
	        	else if (sharedMainRoleClassObjs.size()>1){
	        		System.out.println("more than one instance in each trace!");
	        		//for each mainrole class objs, we create a new instance object set
	        		for(String mainObj: sharedMainRoleClassObjs)
	        		{
	        			interIns2Objs.put((++count)+XConceptExtension.instance().extractName(trace), getConnectedObjects(trace,mainObj));
	        		}
				}
	        	else {
					System.out.println("<<<Invalid pattern instance invocation: no main role found!>>>");
				}
	        }
	        
	        return interIns2Objs;
		}
		
		/*
		 * this class return all classes included in each pattern instance
			 * class set is not accurate, it only use the class name (string)...
			 * this set of class should be included in the log. 
			 */
		public static ArrayList<String> getClassesOfPatternInstance(HashMap<String, Object> role2vaule)
		{
			ArrayList<String> classes = new ArrayList<>();
			
			//for each role, we add its class name to classes. 
			for (String roleName : role2vaule.keySet()) 
			{
				Object obj = role2vaule.get(roleName);
				if(obj instanceof ClassClass)
				{
					//get the class set of the roles 
					if(!classes.contains(BasicOperators.extractClass(obj.toString())))
					{
						classes.add(BasicOperators.extractClass(obj.toString()));
					}
					
				}
				else if (obj instanceof MethodClass)
				{
					if(!classes.contains(BasicOperators.extractClass(BasicOperators.MethodToClass((MethodClass)obj).toString())))
					{
						classes.add(BasicOperators.extractClass(BasicOperators.MethodToClass((MethodClass)obj).toString()));
					}
				}
			}
			return classes;
		}
}
