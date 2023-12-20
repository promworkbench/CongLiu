package software.designpattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.framework.plugin.events.Logger.MessageLevel;

import XESSoftwareExtension.XSoftwareExtension;
import designpatterns.adapterpattern.ClassTypeHierarchy;
import designpatterns.framework.BasicOperators;
import designpatterns.framework.CandidateCombination;
import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;
import designpatterns.framework.PatternSetImpl;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

public class VisitorPatternDiscoveryAndChecking {
	/* for visitor pattern, the element, visitor and accept are included while visit is missing.
	 * the DiscoveryCompleteVisitorPattern aims to find all missing roles from the execution log. 
	 */
	public ArrayList<HashMap<String, Object>> DiscoverCompleteVisitorPattern(UIPluginContext context, PatternClass patternCandidate, XLog softwareLog, ClassTypeHierarchy cth, HashMap<String, ArrayList<Object>> role2values)
	{
		//for those role with value, we need first make sure the values are also included in the log.
		//for element, we extend the class set to all classes with typehierarchy information
		if(!BasicOperators.classIncludedInLog((ClassClass)role2values.get("Element").get(0), softwareLog))//the class is not included in the log
		{
			context.log("The value of Element is not included in the execution log!", MessageLevel.WARNING);
		}
		//get all classes that of typehierarchy with the element class, and also included in the log.
		HashSet<ClassClass> alternativeElementClassSet =BasicOperators.typeHierarchyClassSetInLog(cth, softwareLog, (ClassClass)role2values.get("Element").get(0));

		if(alternativeElementClassSet.size()!=0){
			for(ClassClass cc: alternativeElementClassSet)
			{
				if(!role2values.get("Element").contains(cc))
				{
					role2values.get("Element").add(cc);
				}
			}
		}

		//for visitor
		if(!BasicOperators.classIncludedInLog((ClassClass)role2values.get("Visitor").get(0), softwareLog))//the class is not included in the log
		{
			context.log("The value of Visitor is not included in the execution log!", MessageLevel.WARNING);
		}
		//get all classes that of typehierarchy with the Visitor class, and also included in the log.
		HashSet<ClassClass> alternativeVisitorClassSet =BasicOperators.typeHierarchyClassSetInLog(cth, softwareLog, (ClassClass)role2values.get("Visitor").get(0));

		if(alternativeVisitorClassSet.size()!=0){
			for(ClassClass cc: alternativeVisitorClassSet)
			{
				if(!role2values.get("Visitor").contains(cc))
				{
					role2values.get("Visitor").add(cc);
				}
			}
		}

		//for accept
		if(!BasicOperators.methodIncludedInLog((MethodClass)role2values.get("accept").get(0), softwareLog))// the method is not included in the log
		{
			context.log("The value of accept is not included in the execution log!", MessageLevel.WARNING);
		}
		HashSet<MethodClass> alternativeMethodSet =BasicOperators.typeHierarchyMethodSetInLog(cth, softwareLog, (MethodClass)role2values.get("accept").get(0));
		
		if(alternativeMethodSet.size()!=0){
			for(MethodClass mm: alternativeMethodSet)
			{
				if(!role2values.get("accept").contains(mm))
				{
					role2values.get("accept").add(mm);
				}
			}
		}
		
		//for the visit role, (1) it is a method of the Visitor; (2) it should include a parameter of Element type. 
		HashSet<MethodClass> methodSetofVisitor = new HashSet<MethodClass>();
		//get the method set of context role 
		for(Object c: role2values.get("Visitor"))
		{
			methodSetofVisitor.addAll(BasicOperators.MethodSetofClass((ClassClass)c, softwareLog));//get all type hierarchy classes
		}
		
		//only select those with Element class as an input parameter type. 
		HashSet<ClassClass> elementClassTypeHierarchy = new HashSet<>();
		for(Object elementClass: role2values.get("Element"))
		{
			elementClassTypeHierarchy.addAll(BasicOperators.typeHierarchyClassSet(cth, (ClassClass)elementClass));
		}
		for(MethodClass m: methodSetofVisitor)
		{
			System.out.println(m);
			//The parameter set of m
			for(ClassClass p: BasicOperators.ParameterSetofMethod(m, softwareLog))
			{
				if(elementClassTypeHierarchy.contains(p)){//if a method has a parameter class that is of context class, it may be a visit method
					if(!m.getMethodName().equals("init()"))//init() should not be included
					{
						if(!role2values.get("visit").contains(m))
						{
							role2values.get("visit").add(m);
						}
						break;
					}
				}
			}
		}
		
		//till now the state pattern candidate should be complete, each role may have multiple values.  
		//if there still exist role without value, we say this candidate is invalid according to the log.  
		for(String role: role2values.keySet())
		{
			//if the role is still missing, we just return null for the current pattern instance.
			if(role2values.get(role).size()==0){
				context.log(role+" is missing values according to the execution log for the current pattern instance!", MessageLevel.WARNING);
				return null;
			}
		}
		
		//get the combination of all kinds of values, each combination is a candidate pattern instances
		ArrayList<HashMap<String, Object>> result =CandidateCombination.combination(role2values);
		return result;
	}

	/*
	 * for each complete visitor pattern candidate, we (1) first identify its invocation; and (2) check the behavior constraints. 
	 */
	public PatternSet VisitorPatternInvocationConstraintsChecking(UIPluginContext context, XFactory factory, PatternClass patternCandidate, XLog softwareLog, ClassTypeHierarchy cth, ArrayList<HashMap<String, Object>> result)
	{
		if(result==null)//if there is no complete candidates discovered, return null.
		{
			return null;
		}
		
		// intermediate results that are complete candidates
		PatternSet discoveredCandidateInstanceSet = new PatternSetImpl(); 
		
		for(int i = 0; i<result.size(); i++)//each result(i) is a candidate pattern instance
		{	
			//identify the invocations for each state pattern instance,
			HashSet<XTrace> invocationTraces = visitorPatternInvocation(softwareLog, factory, cth, result.get(i));
			
			int numberofValidatedInvocation = 0;//the number of validated pattern invocations. 
			//for each invocation, we check the behavioral constraints.
			for(XTrace invocation: invocationTraces)
			{
				//@cardinality constraints: check invocation level constraints, accept>=1 and visit>=1;
				if(BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("visit")).getMethodName(), invocation)<1
						||BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("accept")).getMethodName(), invocation)<1)
				{
					context.log("instance candidate not validated:[cardinality]!", MessageLevel.WARNING);
					continue;
				}
					
				//@invocation-constraint 1: accept should invoke the visit method of exactly one Visitor class object. 
				HashSet<String> visitObjects = new HashSet<>();
				for(String o: BasicOperators.CalleeObjectSetPerTrace(((MethodClass)result.get(i).get("accept")).getMethodName(), invocation))
				{
					//input sequence: callee method, caller method, caller object, invocation
					visitObjects.addAll(BasicOperators.CalleeObjectSetPerTrace(((MethodClass)result.get(i).get("visit")).getMethodName(),
							((MethodClass)result.get(i).get("accept")).getMethodName(), o, invocation));
				}
				System.out.println("visit objects"+visitObjects);
				if(visitObjects.size()==1)// one or more times.
				{
					numberofValidatedInvocation++;
				}
			}
			
			if(numberofValidatedInvocation==0)//if there is no validated invocation, then the current candidate is not approved.
			{
				context.log("candidate not validated:[constraints: accept->visit]!", MessageLevel.WARNING);
				continue;
			}
			
			PatternClass NewP = BasicOperators.CreatePatternInstance(patternCandidate, result.get(i), softwareLog.size(), numberofValidatedInvocation);
			discoveredCandidateInstanceSet.add(NewP);
		}
		return discoveredCandidateInstanceSet;
	}
	
	/*
	 * invocation identification for visitor Pattern
	 */
	public static HashSet<XTrace> visitorPatternInvocation(XLog softwareLog, XFactory factory, ClassTypeHierarchy cth, HashMap<String, Object> resulti)
	{
		//identify the invocations for each state pattern instance,
		HashSet<XTrace> invocationTraces = new HashSet<>();
		
		//the class set of element role.
		HashSet<ClassClass> elementClassTypeHierarchy=BasicOperators.typeHierarchyClassSet(cth, (ClassClass)resulti.get("Element"));
		HashSet<String> elementClassTypeSet = new HashSet<>();
		for(ClassClass c: elementClassTypeHierarchy)//construct the element class type set.
		{
			elementClassTypeSet.add(c.getPackageName()+"."+c.getClassName());
		}
		
		for(XTrace trace:softwareLog)
		{
			//get the visitor object set for each trace. 
			HashSet<String> VisitorObjects =BasicOperators.ObjectSetClassPerTrace((ClassClass)resulti.get("Visitor"),trace);
			for(String VisitorO:VisitorObjects)//for each context object, we construct an invocation.
			{
				if(!VisitorO.equals("0"))
				{
					XTrace invocation = factory.createTrace();
					for(XEvent event: trace)
					{
						//if the callee class object is a visitor object, and the callee method is visit
						if(XSoftwareExtension.instance().extractClassObject(event).equals(VisitorO))
						{
							if(XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("visit")).getMethodName()))
							{
								invocation.add(event);
								System.out.println("add event: "+XConceptExtension.instance().extractName(event));
							}
						}
						//else if the callee method is accept, the callee class is Element, callee object should invoke the (visitor object+visit),  
						else if(XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("accept")).getMethodName()))
						{
							if(elementClassTypeSet.contains(XSoftwareExtension.instance().extractPackage(event)+"."+XSoftwareExtension.instance().extractClass(event))
									&& BasicOperators.CallerObjectSetPerTrace(((MethodClass)resulti.get("visit")).getMethodName(), VisitorO, trace).contains(XSoftwareExtension.instance().extractClassObject(event)))
							{
								invocation.add(event);
								System.out.println("add event: "+XConceptExtension.instance().extractName(event));
							}
						}
					}
					
					if(invocation.size()!=0)
					invocationTraces.add(invocation);
				}
			}
		}
		
		return invocationTraces;
	}



}
