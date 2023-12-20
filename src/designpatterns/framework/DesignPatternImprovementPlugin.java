package designpatterns.framework;
/**
 * the aim of this plug-in is to 
 * (1) add more detailed description of each pattern, e.g., the update(), register(), de_register() for the observer pattern
 * (2) check the correctness of each candidate pattern instance according to the input specifications; 
 * 
 * 2017-3-27 adding the functionality to handle incomplete event log, 
 * case 1: missing roles are not in the log. 
 * case 2: detected roles are not in the log. 
 * 
 * 2017-4-3 adding the class type hierarchy information to the plugin. 
 * And remove missing roles extension. 
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import congliu.processmining.softwareprocessmining.Component2Classes;
import designpatterns.adapterpattern.AdapterPatternClass;
import designpatterns.factorymethodpattern.FactoryMethodPatternClass;
import designpatterns.observerpattern.ObserverPatternClass;
import designpatterns.statepattern.StatePatternClass;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

@Plugin(
		name = "Design Pattern Improvement Framework",// plugin name
		
		returnLabels = {"Design Patterns"}, //reture labels
		returnTypes = {PatternSetImpl.class},//return class
		
		//input parameter labels
		parameterLabels = {"Design Pattern Candidates", "Pattern Specification", "Software event log"},
		
		userAccessible = false,
		help = "This plugin aims to improve the Design Pattern results discovered from DPD tool." 
		)

public class DesignPatternImprovementPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Design Pattern Discovery and Checking, default",
			// the number of required parameters, {0} means the first input parameter, {1} means the second input parameter, {2} means the third input parameter
			requiredParameterLabels = {0, 1, 2}
			)
	public PatternSet DiscoveryandChecking(UIPluginContext context, PatternSet patternSet, 
			DesignPatternSpecification specification, XLog softwareLog)
	{
		//the first step is to check which role is missing and which role has value. 
		//all candidate with complete roles. 
		PatternSet enhancedCandidateInstanceSet = new PatternSetImpl(); // intermediate results
		
		//for each pattern instance, 
		for(PatternClass op: patternSet.getPatternSet())
		{
			HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
			HashSet<String> roleSetWithValue = new HashSet<>();// the set of roles with value
			HashSet<String> roleSetNoValue = new HashSet<>(); // the set of roles without value
			
			ClassClass mainRoleClass=new ClassClass();
			//create the class set as ArrayList<String> classes, includes all class-level roles.
			ArrayList<String> classes = new ArrayList<>();
			
			//construct the mapping for observer pattern
			if(op.getPatternName().equals("Observer Pattern"))
			{
				role2values=ConstructRole2Values.observerPattern(op);
				
				//add the main role class, i.e. the subject
				mainRoleClass =((ObserverPatternClass)op).getSubjectClass();
			}
			else if(op.getPatternName().equals("State Pattern"))
			{
				role2values=ConstructRole2Values.statePattern(op);
				
				//add the main role class, i.e., the context
				mainRoleClass=((StatePatternClass)op).getContext();
			}
			else if(op.getPatternName().equals("(Object)Adapter Pattern"))
			{
				role2values =ConstructRole2Values.AdapterPattern(op);
				
				//add the main role class, i.e., the adapter
				mainRoleClass=((AdapterPatternClass)op).getAdapterClass();
			}
			else if(op.getPatternName().equals("Factory Method Pattern"))
			{
				role2values =ConstructRole2Values.FactoryMethodPattern(op);
				
				//add the main role class, i.e., the adapter
				mainRoleClass=((FactoryMethodPatternClass)op).getCreator();
			}
			//else....
			
			//store the missing roles, not included in the log. 
			Set<String> missingRoles = new HashSet<>();
			
			
			System.out.println("from static tools: " +role2values);
			
			//for each role, check if its value is detected from the static tools.
			for (String roleName : role2values.keySet()) 
			{
				if(role2values.get(roleName).size()>0)// have value
				{
					System.out.println("withvalue:"+roleName);
					roleSetWithValue.add(roleName);
					//check if the role value is also included in the log, limit to method roles....
					//both class and method roles should be checked with respect to the log. 
					if(!BasicOperators.roleValueIncludedinLog(role2values.get(roleName).get(0),softwareLog))
					{
						System.out.println("role value is not in the log: "+ roleName);
						missingRoles.add(roleName);
					}
					
					if(role2values.get(roleName).get(0) instanceof ClassClass)
					{
						classes.add(BasicOperators.extractClass(role2values.get(roleName).get(0).toString()));
					}
					
				}
				else{
					roleSetNoValue.add(roleName);
					System.out.println("novalue:"+roleName);
				}
			}
			
			/*
			 * class set is not accurate, it only use the class name (string)...
			 */
			System.out.println("class set: " +classes);
			
			//for each log-level constraints, we check if its first role and second role are satisfied.
			
			for(Constraint lc: specification.getLogConstraintSet())
			{
				DiscoveryAndChecking.discovery(lc, roleSetWithValue, roleSetNoValue, role2values, softwareLog);
			}// loop for each log-level constraint
			
			
			//for those role with not values detected from the log, we add explicit label. 
			for(String role: role2values.keySet())
			{
				if(role2values.get(role).size()==0)
				{
					MethodClass nullM = new MethodClass();
					nullM.setPackageName("Not");
					nullM.setClassName("In");
					nullM.setMethodName("Log");
					role2values.get(role).add(nullM);
					missingRoles.add(role);
					System.out.println("role value is not in the log: "+ role);
				}
			}

			System.out.println("after discovery: " +role2values);
			
			//get the combination of all kinds of values. 
			ArrayList<HashMap<String, Object>> result =CandidateCombination.combination(role2values);
			
			//System.out.println(result);		
			
			for(int i = 0; i<result.size(); i++)
			{	
				System.out.println(result.get(i));
				//check all constraints in the specification for each candidate pattern instance. 

				
				//refactoring the log according to invocations.
				//create the component to classes mapping, this aims to reuse the existing work
				Component2Classes c2c = new Component2Classes();
				c2c.setComponent("designpattern");
				c2c.setClasses(classes);
				
				//create factory to create Xlog, Xtrace and Xevent.
				XFactory factory = new XFactoryNaiveImpl();
				
				//invocation identification
				XLog refactoredLog=DiscoveryAndChecking.generatingPatternInstanceEventLog(mainRoleClass,c2c,softwareLog,factory);
				
				//check log-level constraints, the set of log constraints, log, candidates as a hashmap
				//for roles that do not have value in the log, we assume the current constraint is hold. 
				if(!DiscoveryAndChecking.LoglevelChecking(missingRoles,specification.getLogConstraintSet(),refactoredLog,result.get(i)))
				{
					continue;
				}
				
				//check temporal and invocation constraints
				if(!DiscoveryAndChecking.InvocationlevelChecking(missingRoles,specification.getInstanceConstraintSet(),refactoredLog,result.get(i)))
				{
					continue;
				}
				System.out.println("XXXXXXXpassed!");
				
				//create a set of complete pattern instance candidate. and add to enhancedPSet.
				if(op.getPatternName().equals("Observer Pattern"))
				{
					ObserverPatternClass opNew = new ObserverPatternClass();
					opNew.setPatternName("Observer Pattern");
					opNew.setSubjectClass((ClassClass)(result.get(i).get("Subject")));
					opNew.setListernerClass((ClassClass)(result.get(i).get("Observer")));
					opNew.setNotifyMethod((MethodClass)(result.get(i).get("notify")));
					opNew.setUpdateMethod((MethodClass)(result.get(i).get("update")));
					opNew.setRegisterMethod((MethodClass)(result.get(i).get("register")));
					opNew.setDe_registerMethod((MethodClass)(result.get(i).get("unregister")));
					opNew.setTraceNumber(softwareLog.size());
					opNew.setInvocationNumber(refactoredLog.size());
					enhancedCandidateInstanceSet.add(opNew);
				}
				else if(op.getPatternName().equals("State Pattern"))
				{
					StatePatternClass spNew = new StatePatternClass();
					spNew.setPatternName("State Pattern");
					spNew.setContext((ClassClass)result.get(i).get("Context"));
					spNew.setState((ClassClass)result.get(i).get("State"));
					spNew.setRequest((MethodClass)(result.get(i).get("request")));
					spNew.setHandle((MethodClass)(result.get(i).get("handle")));
					spNew.setTraceNumber(softwareLog.size());
					spNew.setInvocationNumber(refactoredLog.size());
					enhancedCandidateInstanceSet.add(spNew);
				}
				else if((op.getPatternName().equals("(Object)Adapter Pattern")))
				{
					AdapterPatternClass apNew = new AdapterPatternClass();
					apNew.setPatternName("(Object)Adapter Pattern");
					apNew.setAdapterClass((ClassClass)result.get(i).get("Adapter"));
					apNew.setAdapteeClass((ClassClass)result.get(i).get("Adaptee"));
					apNew.setRequestMethod((MethodClass)result.get(i).get("request"));
					apNew.setSpecificRequestMethod((MethodClass)result.get(i).get("specificRequest"));
					apNew.setTraceNumber(softwareLog.size());
					apNew.setInvocationNumber(refactoredLog.size());
					enhancedCandidateInstanceSet.add(apNew);
				}
				else if((op.getPatternName().equals("Factory Method Pattern")))
				{
					FactoryMethodPatternClass fpNew = new FactoryMethodPatternClass();
					fpNew.setPatternName("Factory Method Pattern");
					fpNew.setCreator((ClassClass)result.get(i).get("Creator"));
					fpNew.setFactoryMethod((MethodClass) result.get(i).get("Facotry Method"));
					fpNew.setTraceNumber(softwareLog.size());
					fpNew.setInvocationNumber(refactoredLog.size());
					enhancedCandidateInstanceSet.add(fpNew);
				}
				//else patterns
				
			}
			
		}//each pattern instance
		
		
		
		System.out.println("the number of instances: "+ enhancedCandidateInstanceSet.size());
		return enhancedCandidateInstanceSet;
	}//DiscoveryandChecking

	
}



