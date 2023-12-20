package designpatterns.framework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;

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
 * And remove missing roles extension. 
 * 
 * 2017-4-24 Refactoring the whole algorithm to discover the accurate number of invocations that support the current pattern. 
 * if the number >=1, then this pattern supported by the log. Other wise it is a false positive according to the log. 
 */

@Plugin(
		name = "Design Pattern Improvement Framework V2",// plugin name
		
		returnLabels = {"Design Patterns"}, //reture labels
		returnTypes = {PatternSetImpl.class},//return class
		
		//input parameter labels
		parameterLabels = {"Design Pattern Candidates", "Pattern Specification", "Software event log", "Class Type Hierarchy"},
		
		userAccessible = false,
		help = "This plugin aims to improve the Design Pattern results discovered from DPD tool." 
		)
public class DesignPatternImprovementPluginV2 {
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
		context.log("plugin starts", MessageLevel.NORMAL);
		PatternSet enhancedCandidateInstanceSet = new PatternSetImpl(); // intermediate results
				
		//for each candidate pattern instance, 
		for(PatternClass op: patternSet.getPatternSet())
		{
			context.log("pattern instance: "+op.toString(), MessageLevel.NORMAL);
			//the first step is to check which role is missing and which role has value. 
			HashSet<String> roleSetWithValue = new HashSet<>();// the set of roles with value
			HashSet<String> roleSetNoValue = new HashSet<>(); // the set of roles without value
			
			
			//these methods differs from design patterns. 
			HashMap<String, ArrayList<Object>> role2values = BasicOperators.Role2Values(op);
			System.out.println("role to values from static tools: " +role2values);
			
			//make sure, all values are included in the log, 
			//if the current value is not, we use the classTypeHierarchy to extend to its parent/child classes
			for (String roleName : role2values.keySet()) 
			{
				if(role2values.get(roleName).size()>0)
				{
					if(role2values.get(roleName).get(0) instanceof ClassClass)// the value is a class
					{
						if(!BasicOperators.classIncludedInLog((ClassClass)role2values.get(roleName).get(0), softwareLog))//the class is not included in the log
						{
							ClassClass alternativeClass =BasicOperators.typeHierarchyClass(cth, softwareLog, (ClassClass)role2values.get(roleName).get(0));

							if(alternativeClass!=null){
								role2values.get(roleName).add(alternativeClass);
							}
						}
					}
					else if(role2values.get(roleName).get(0) instanceof MethodClass)// the value is a method
					{
						if(!BasicOperators.methodIncludedInLog((MethodClass)role2values.get(roleName).get(0), softwareLog))// the method is not included in the log
						{
							MethodClass alternativeMethod =BasicOperators.typeHierarchyMethod(cth, softwareLog, (MethodClass)role2values.get(roleName).get(0));
							
							if(alternativeMethod!=null){
								role2values.get(roleName).add(alternativeMethod);
							}
						}
					}
				}
				
			}
			
			System.out.println("after (ClassTypeHierarchy) extention of candidates: " +role2values);
			
			//store the missing roles that are not included in the log. 
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
			
			//for those roles without values after discovery, we add explicit label. 
			for(String role: role2values.keySet())
			{
				if(role2values.get(role).size()==0){
					MethodClass nullM = new MethodClass();
					nullM.setPackageName("Not");
					nullM.setClassName("In");
					nullM.setMethodName("Log");
					role2values.get(role).add(nullM);
					missingRoles.add(role);
					System.out.println("role value is not discovered from the log: "+ role);
				}
			}

			System.out.println("role to values after discovery: " +role2values);
			
			// construct the main role classes according to the candidates and the pattern type. 
			ArrayList<Object> mainRoleClassSet =BasicOperators.DynamicMainRole(op, role2values);
			HashSet<ClassClass> mainRoleClasses = new HashSet<>();
			for(Object obj: mainRoleClassSet){
				mainRoleClasses.add((ClassClass)obj);
			}
			
			System.out.println("main role: " +mainRoleClasses);
			
			
			//get the combination of all kinds of values. 
			ArrayList<HashMap<String, Object>> result =CandidateCombination.combination(role2values);
			
			//each combination is a candidate pattern instances
			for(int i = 0; i<result.size(); i++)
			{	
				System.out.println(result.get(i));
				
				//create the class set of the current candidate instance as ArrayList<String> classes, includes all class-level roles.
				ArrayList<String> classes =DiscoveryAndChecking.getClassesOfPatternInstance(result.get(i));
				
				//refactoring the log according to invocations, create the component to classes mapping, this aims to reuse the existing work
				Component2Classes c2c = new Component2Classes();
				c2c.setComponent("designpattern"+i);
				c2c.setClasses(classes);//include all classes
				
				//create factory to create Xlog, Xtrace and Xevent.
				XFactory factory = new XFactoryNaiveImpl();

				//invocation identification
				XLog refactoredLog=DiscoveryAndChecking.generatingPatternInstanceEventLogV1(mainRoleClasses,c2c,softwareLog,factory);
				
				System.out.println("the number of traces of the refactored log: "+ refactoredLog.size());
				//check all constraints in the specification for each invocation, i.e., trace in the refactoredLog
				int invocatioNumber = 0;// the number of invocations that support the current pattern (i.e., result.get(i)). 
				for(XTrace invocationTrace: refactoredLog)
				{
					//check log-level constraints, the set of log constraints, log, candidates as a hashmap
					//for roles that do not have value in the log, we assume the current constraint is hold. 
					if(!DiscoveryAndChecking.LoglevelCheckingV2(missingRoles,specification.getLogConstraintSet(), invocationTrace, result.get(i), cth))
					{
						continue;
					}
					System.out.println("XXXXXXXLogConstraintPassed!");
					
					//check temporal and invocation constraints
					if(!DiscoveryAndChecking.InvocationlevelCheckingV1(missingRoles,specification.getInstanceConstraintSet(),invocationTrace,result.get(i)))
					{
						continue;
					}
					System.out.println("XXXXXXXInvocationConstraintPassed!");
					invocatioNumber++;
				}
				
				//create the complete pattern instance candidate. and add to enhancedPSet.
				if(invocatioNumber>0)
				{
					PatternClass NewP = BasicOperators.CreatePatternInstance(op, result.get(i), softwareLog.size(), invocatioNumber);
					enhancedCandidateInstanceSet.add(NewP);
				}
				
			}
			
			
			
		}
		
		System.out.println("the number of approved pattern instances: "+ enhancedCandidateInstanceSet.size());
		return enhancedCandidateInstanceSet;
	}

}
