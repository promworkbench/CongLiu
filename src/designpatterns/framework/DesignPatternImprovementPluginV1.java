package designpatterns.framework;

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
import designpatterns.adapterpattern.ClassTypeHierarchy;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

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
 * Target: some methods or classes that play a role in the candidate pattern is not really used in the execution(log). 
 * Their parent/child classes are logged. 
 * 
 * And remove missing roles extension. 
 */

@Plugin(
		name = "Design Pattern Improvement Framework V1",// plugin name
		
		returnLabels = {"Design Patterns"}, //reture labels
		returnTypes = {PatternSetImpl.class},//return class
		
		//input parameter labels
		parameterLabels = {"Design Pattern Candidates", "Pattern Specification", "Software event log", "Class Type Hierarchy"},
		
		userAccessible = false,
		help = "This plugin aims to improve the Design Pattern results discovered from DPD tool." 
		)
public class DesignPatternImprovementPluginV1 {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Design Pattern Discovery and Checking, default",
			// the number of required parameters, {0} means the first input parameter, {1} means the second input parameter, {2} means the third input parameter
			requiredParameterLabels = {0, 1, 2, 3}
			)
	public PatternSet DiscoveryandChecking(UIPluginContext context, PatternSet patternSet, 
			DesignPatternSpecification specification, XLog softwareLog, ClassTypeHierarchy cth)
	{
		//the first step is to check which role is missing and which role has value. 
		PatternSet enhancedCandidateInstanceSet = new PatternSetImpl(); // intermediate results

		//for each pattern instance, 
		for(PatternClass op: patternSet.getPatternSet())
		{
			HashSet<String> roleSetWithValue = new HashSet<>();// the set of roles with value
			HashSet<String> roleSetNoValue = new HashSet<>(); // the set of roles without value
			
			/*
			 * these methods differs from design patterns. 
			 */

			HashMap<String, ArrayList<Object>> role2values = BasicOperators.Role2Values(op);
			System.out.println("from static tools: " +role2values);
			
			//make sure, all values are included in the log, if the current value is not, we use the classTypeHierarchy to extend to its parent/child classes
			for (String roleName : role2values.keySet()) 
			{
				if(role2values.get(roleName).size()>0)
				{
					if(role2values.get(roleName).get(0) instanceof ClassClass)// the value is a class
					{
						if(!BasicOperators.classIncludedInLog((ClassClass)role2values.get(roleName).get(0), softwareLog))//the class is not included in the log
						{
							ClassClass alternativeClass =BasicOperators.typeHierarchyClass(cth, softwareLog, (ClassClass)role2values.get(roleName).get(0));
							
							//use the alternativeClass to replace the value that is not included in the log
							//role2values.get(roleName).clear();
							if(alternativeClass!=null)
							{
								role2values.get(roleName).add(alternativeClass);
							}
						}
					}
					else if(role2values.get(roleName).get(0) instanceof MethodClass)// the value is a method
					{
						if(!BasicOperators.methodIncludedInLog((MethodClass)role2values.get(roleName).get(0), softwareLog))// the method is not included in the log
						{
							MethodClass alternativeMethod =BasicOperators.typeHierarchyMethod(cth, softwareLog, (MethodClass)role2values.get(roleName).get(0));
							
							//use the alternativeMethod to replace the value that is not included in the log
							//role2values.get(roleName).clear();
							if(alternativeMethod!=null)
							{
								role2values.get(roleName).add(alternativeMethod);
							}
						}
					}
				}
				
			}
			
			System.out.println("after replacing: " +role2values);
			
						
			//store the missing roles, not included in the log. 
			Set<String> missingRoles = new HashSet<>();
		
			
			//for each role, we check if its value is detected from the static tools.
			for (String roleName : role2values.keySet()) 
			{
				if(role2values.get(roleName).size()>0)// have value
				{
					System.out.println("withvalue:"+roleName);
					roleSetWithValue.add(roleName);					
				}
				else{
					roleSetNoValue.add(roleName);
					System.out.println("novalue:"+roleName);
				}
			}
			
			
			//for each log-level constraints, we check if its first role and second role are satisfied.
			for(Constraint lc: specification.getLogConstraintSet())
			{
				// we need to use the class type hierarchy information to cope with the class/methods that not supported by the log
				DiscoveryAndChecking.discoveryV1(lc, roleSetWithValue, roleSetNoValue, role2values, softwareLog, cth);
			}// loop for each log-level constraint
			
			//for those role without values detected from the log, we add explicit label. 
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
					System.out.println("role value is not in the log after discovery: "+ role);
				}
			}

			System.out.println("role to valures after discovery: " +role2values);
			

			//create the class set as ArrayList<String> classes, includes all class-level roles.
			ArrayList<String> classes = new ArrayList<>();
			
			//for each role, we add its class name to classes. 
			for (String roleName : role2values.keySet()) 
			{
				for(Object obj: role2values.get(roleName))
				{
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
			}
			

			/*
			 * class set is not accurate, it only use the class name (string)...
			 * this set of class should be included in the log. 
			 */
			System.out.println("class set of the current pattern candidate: " +classes);
			
			// construct the main role classes according to the candidates and the pattern type. 
			ArrayList<Object> mainRoleClassSet =BasicOperators.DynamicMainRole(op, role2values);
			HashSet<ClassClass> mainRoleClasses = new HashSet<>();
			for(Object obj: mainRoleClassSet)
			{
				mainRoleClasses.add((ClassClass)obj);
			}
			
			System.out.println("main role: " +mainRoleClasses);
			
			//get the combination of all kinds of values. 
			ArrayList<HashMap<String, Object>> result =CandidateCombination.combination(role2values);
			
			
			for(int i = 0; i<result.size(); i++)//for each combined candidates
			{	
				System.out.println(result.get(i));
				//check all constraints in the specification for each candidate pattern instance. 

				//refactoring the log according to invocations, create the component to classes mapping, this aims to reuse the existing work
				Component2Classes c2c = new Component2Classes();
				c2c.setComponent("designpattern");
				c2c.setClasses(classes);//these class are 
				
				//create factory to create Xlog, Xtrace and Xevent.
				XFactory factory = new XFactoryNaiveImpl();

				//invocation identification
				XLog refactoredLog=DiscoveryAndChecking.generatingPatternInstanceEventLogV1(mainRoleClasses,c2c,softwareLog,factory);
				
				//check log-level constraints, the set of log constraints, log, candidates as a hashmap
				//for roles that do not have value in the log, we assume the current constraint is hold. 
				if(!DiscoveryAndChecking.LoglevelCheckingV1(missingRoles,specification.getLogConstraintSet(),refactoredLog, result.get(i), cth))
				{
					continue;
				}
				System.out.println("XXXXXXXLogConstraintPassed!");
				//check temporal and invocation constraints
				if(!DiscoveryAndChecking.InvocationlevelChecking(missingRoles,specification.getInstanceConstraintSet(),refactoredLog,result.get(i)))
				{
					continue;
				}
				System.out.println("XXXXXXXInvocationConstraintPassed!");
				
				//create the complete pattern instance candidate. and add to enhancedPSet.
				
				PatternClass NewP = BasicOperators.CreatePatternInstance(op, result.get(i), softwareLog.size(), refactoredLog.size());
				enhancedCandidateInstanceSet.add(NewP);
				
			}
			
		}
		
		
		System.out.println("the number of instances: "+ enhancedCandidateInstanceSet.size());
		return enhancedCandidateInstanceSet;
		
	}

}
