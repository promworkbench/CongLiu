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
import congliu.processmining.softwareprocessmining.OrderingEventsNano;
import designpatterns.adapterpattern.ClassTypeHierarchy;
import designpatterns.framework.BasicOperators;
import designpatterns.framework.CandidateCombination;
import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;
import designpatterns.framework.PatternSetImpl;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

public class StatePatternDiscoveryAndChecking {
	 
	/* for state pattern, the context, state and request are included while setState and handle are missing.
	 * the DiscoveryCompleteStatePattern aims to find all missing roles from the execution log. 
	 */
	public ArrayList<HashMap<String, Object>> DiscoverCompleteStatePattern(UIPluginContext context, PatternClass patternCandidate, XLog softwareLog, ClassTypeHierarchy cth, HashMap<String, ArrayList<Object>> role2values)
	{
		//for those role with value, we need first make sure the values are also included in the log.
		//for context, we extend the class set to all classes with typehierarchy information
		if(!BasicOperators.classIncludedInLog((ClassClass)role2values.get("Context").get(0), softwareLog))//the class is not included in the log
		{
			context.log("The value of Context is not included in the execution log!", MessageLevel.WARNING);
		}
		//get all classes that of typehierarchy with the context class, and also included in the log.
		HashSet<ClassClass> alternativeContextClassSet =BasicOperators.typeHierarchyClassSetInLog(cth, softwareLog, (ClassClass)role2values.get("Context").get(0));

		if(alternativeContextClassSet.size()!=0){
			for(ClassClass cc: alternativeContextClassSet)
			{
				if(!role2values.get("Context").contains(cc))
				{
					role2values.get("Context").add(cc);
				}
			}
		}

		//for state
		if(!BasicOperators.classIncludedInLog((ClassClass)role2values.get("State").get(0), softwareLog))//the class is not included in the log
		{
			context.log("The value of State is not included in the execution log!", MessageLevel.WARNING);
		}
		//get all classes that of typehierarchy with the context class, and also included in the log.
		HashSet<ClassClass> alternativeStateClassSet =BasicOperators.typeHierarchyClassSetInLog(cth, softwareLog, (ClassClass)role2values.get("State").get(0));

		if(alternativeStateClassSet.size()!=0){
			for(ClassClass cc: alternativeStateClassSet)
			{
				if(!role2values.get("State").contains(cc))
				{
					role2values.get("State").add(cc);
				}
			}
		}

		//for request
		if(!BasicOperators.methodIncludedInLog((MethodClass)role2values.get("request").get(0), softwareLog))// the method is not included in the log
		{
			context.log("The value of request is not included in the execution log!", MessageLevel.WARNING);
		}
		HashSet<MethodClass> alternativeMethodSet =BasicOperators.typeHierarchyMethodSetInLog(cth, softwareLog, (MethodClass)role2values.get("request").get(0));
		
		if(alternativeMethodSet.size()!=0){
			for(MethodClass mm: alternativeMethodSet)
			{
				if(!role2values.get("request").contains(mm))
				{
					role2values.get("request").add(mm);
				}
			}
		}
		
		//for the setState role, (1) it is a method of the context; (2) it should include a parameter of State type; (3) the init() should not be included
		HashSet<MethodClass> methodSetofContext = new HashSet<MethodClass>();
		//get the method set of context role 
		for(Object c: role2values.get("Context"))
		{
			methodSetofContext.addAll(BasicOperators.MethodSetofClass((ClassClass)c, softwareLog));//get all type hierarchy classes
		}
		
		//only select those with state class as an input parameter type. 
		HashSet<ClassClass> stateClassTypeHierarchy = new HashSet<>();
		for(Object stateClass: role2values.get("State"))
		{
			stateClassTypeHierarchy.addAll(BasicOperators.typeHierarchyClassSet(cth, (ClassClass)stateClass));
		}
		for(MethodClass m: methodSetofContext)
		{
			if(!m.getMethodName().equals("init()"))//init()should not be included
			{
				System.out.println(m);
				//The parameter set of m
				for(ClassClass p: BasicOperators.ParameterSetofMethod(m, softwareLog))
				{
					if(stateClassTypeHierarchy.contains(p)){//if a method has a parameter class that is of  context class, it may be a setState class
						if(!role2values.get("setState").contains(m))
						{
							role2values.get("setState").add(m);
						}
						System.out.println(m);
						break;
					}
				}
			}
		}

		//for the handle role, (1) it is a method of the state; (2) it is invoked by the request method
		HashSet<MethodClass> methodSetofState = new HashSet<MethodClass>();
		//get the method set of state role 
		for(Object c: role2values.get("State"))
		{
			methodSetofState.addAll(BasicOperators.MethodSetofClass((ClassClass)c, softwareLog));//get all type hierarchy classes
		}
		
		//get the method set invoked by request.
		HashSet<MethodClass> methodSetInovkedByRequest = new HashSet<MethodClass>();
		for(Object m: role2values.get("request"))
		{
			methodSetInovkedByRequest.addAll(BasicOperators.MethodSetofMethod((MethodClass)m, softwareLog));
		}
		
		for(MethodClass m: methodSetInovkedByRequest)
		{
			if(!m.getMethodName().equals("init()"))//init()should not be included
			{
				if(methodSetofState.contains(m)){
					if(!role2values.get("handle").contains(m))
					{
						role2values.get("handle").add(m);
					}
				}
			}
		}
		
		//till now the state pattern candidate should be complete, each role may have multiple values.  
		//if there still exist role without value, we say this candidate is invalid according to the log.  
		for(String role: role2values.keySet())
		{
			//if the role is still missing, we just return null for the current pattern instance.
			//for state pattern, the setState role can be empty
			if(role2values.get(role).size()==0){
				context.log(role+" is missing values according to the execution log for the current pattern instance!", MessageLevel.WARNING);
				return null;
			}
		}
		
		//get the combination of all kinds of values, each combination is a candidate pattern instances
		ArrayList<HashMap<String, Object>> result =CandidateCombination.combination(role2values);
		
//		for(int i = 0; i<result.size(); i++)
//		{	
//			PatternClass NewP = BasicOperators.CreatePatternInstance(patternCandidate, result.get(i), softwareLog.size(), 0);
//			discoveredCandidateInstanceSet.add(NewP);
//		}
		return result;
	}


	/*
	 * for each complete state pattern candidate, we (1) first identify its invocation; and (2) check the behavior constraints. 
	 */
	public PatternSet StatePatternInvocationConstraintsChecking(UIPluginContext context, XFactory factory, PatternClass patternCandidate, XLog softwareLog, ClassTypeHierarchy cth, ArrayList<HashMap<String, Object>> result)
	{
		if(result==null)//if there is no complete candidates discovered, return null.
		{
			return null;
		}
		
		// intermediate results that are complete candidates
		PatternSet discoveredCandidateInstanceSet = new PatternSetImpl(); 
		
		for(int i = 0; i<result.size(); i++)//each result(i) is a candidate pattern instance
		{	
			//get the state class type 
			HashSet<String> StateClassTypeHierarchy = new HashSet<>();
			for(ClassClass c:BasicOperators.typeHierarchyClassSet(cth, (ClassClass)result.get(i).get("State")))
			{
				StateClassTypeHierarchy.add(c.getPackageName()+"."+c.getClassName());
			}
			
			//get the Context class type 
			HashSet<String> ContextClassTypeHierarchy = new HashSet<>();
			for(ClassClass c: BasicOperators.typeHierarchyClassSet(cth, (ClassClass)result.get(i).get("Context")))
			{
				ContextClassTypeHierarchy.add(c.getPackageName()+"."+c.getClassName());
			}
			
			//identify the invocations for each state pattern instance,
			HashSet<XTrace> invocationTraces = statePatternInvocation(softwareLog, factory, cth, result.get(i));
			
			int numberofValidatedInvocation = 0;//the number of validated pattern invocations. 
			//for each invocation, we check the behavioral constraints.
			for(XTrace invocation: invocationTraces)
			{
				//@cardinality constraints: check invocation level constraints, request>=1 and handle >=1, and setState>=1;
				if(BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("handle")).getMethodName(), invocation)<1
						||BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("request")).getMethodName(), invocation)<1
						||BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("setState")).getMethodName(), invocation)<1)
				{
					context.log("instance candidate not validated:[cardinality]!", MessageLevel.WARNING);
					continue;
				}
					
				//@invocation-constraint 1: the state change can only be done by state or context, i.e., it can not be done by the client
				//the set of all setState (after the first request method), i.e., all state changes.
				HashSet<XEvent> setStateEvents =  setStateMethodCallSet(invocation, ((MethodClass)result.get(i).get("request")).getMethodName(),
						((MethodClass)result.get(i).get("setState")).getMethodName());
				if(setStateEvents.size()!=0)
				{
					//for each one, its caller class should be state or context.
					int validedsetState=0;
					for(XEvent setStateE: setStateEvents)
					{
						String tempCallerClass = XSoftwareExtension.instance().extractCallerpackage(setStateE)+"."+XSoftwareExtension.instance().extractCallerclass(setStateE);
						if(ContextClassTypeHierarchy.contains(tempCallerClass)
								||StateClassTypeHierarchy.contains(tempCallerClass))
						{
							validedsetState++;
						}
					}
//					System.out.println("the number of setState: "+setStateEvents.size());
//					System.out.println(validedsetState);
					if(validedsetState!=setStateEvents.size())//not all setstates are validated 
					{
						context.log("instance candidate not validated:[constraints: the state change can only be done by state or context]!", MessageLevel.WARNING);
						continue;
					}
				}
					
				
				//@invocation-constraint 2: after state change the request method should invoke the handle method of the new state object
				//an exception case is that: for the last setState, there is no need to check the request
				HashSet<XEvent> setStateEventSet = BasicOperators.eventSetofMethodPerInvocation(invocation,((MethodClass)result.get(i).get("setState")).getMethodName());
				if(setStateEventSet.size()!=0)
				{
					int validedsetState=0;
					for(XEvent setStateE: setStateEventSet)
					{	//get the index of the next setState
						int nextSetStateEventIndex=StatePatternDiscoveryAndChecking.getNextEventAfterIndexX(invocation, 
								invocation.indexOf(setStateE), ((MethodClass)result.get(i).get("setState")).getMethodName());
						if(nextSetStateEventIndex!=-1)//there exists a setStateEvent after the current one
						{
							//for each set state event, we get the request after it. 
							XEvent firstRequestEvent =StatePatternDiscoveryAndChecking.getFirstEventAfterIndexABeforeIndexB(invocation, invocation.indexOf(setStateE),
									nextSetStateEventIndex, ((MethodClass)result.get(i).get("request")).getMethodName());
							if(firstRequestEvent==null)//there do not exist such a request event
							{
								continue;
							}
							else 
							{
								//get the callee object set of the invoked handle method of request
								HashSet<String> calleeObjectofHandle =BasicOperators.calleeObjectSetofInvokedEventsPerTrace(firstRequestEvent, invocation);
								//for each setState, we get its parameter mapping
								HashMap<ClassClass, String> paraMappingofsetStateEvent = ObserverPatternDiscoveryAndChecking.constructParameterMapping(setStateE);
								for(ClassClass c:paraMappingofsetStateEvent.keySet())
								{
									if(StateClassTypeHierarchy.contains(c.toString()))
									{
										if(calleeObjectofHandle.contains(paraMappingofsetStateEvent.get(c)))
										{
											validedsetState++;
											break;
										}
										
									}
								}
							}
						}
						else//this is the last setState
						{
							validedsetState++;
						}
					}
//					System.out.println("the number of setstate: "+setStateEventSet.size());
//					System.out.println(validedsetState);
					if(validedsetState!=setStateEventSet.size())//not all setState are validated, we allow the last setState may not have handle.  
					{
						context.log("instance candidate not validated:[constraints: after state change the request method should invoke the handle method of the new state object]!", MessageLevel.WARNING);
						continue;
					}
				}
				

				numberofValidatedInvocation++;
			}
			
			if(numberofValidatedInvocation==0)//if there is no validated invocation, then the current candidate is not approved.
			{
				context.log("candidate not validated:[constraints: request->handle]!", MessageLevel.WARNING);
				continue;
			}
			
			PatternClass NewP = BasicOperators.CreatePatternInstance(patternCandidate, result.get(i), softwareLog.size(), numberofValidatedInvocation);
			discoveredCandidateInstanceSet.add(NewP);
		}
		return discoveredCandidateInstanceSet;
	}
	
	/*
	 * invocation identification for state Pattern
	 */
	public static HashSet<XTrace> statePatternInvocation(XLog softwareLog, XFactory factory, ClassTypeHierarchy cth, HashMap<String, Object> resulti)
	{
		//identify the invocations for each state pattern instance,
		HashSet<XTrace> invocationTraces = new HashSet<>();
		
		//the class set of state role.
		HashSet<ClassClass> stateClassTypeHierarchy=BasicOperators.typeHierarchyClassSet(cth, (ClassClass)resulti.get("State"));
		HashSet<String> stateClassTypeSet = new HashSet<>();
		for(ClassClass c: stateClassTypeHierarchy)//construct the state class type set.
		{
			stateClassTypeSet.add(c.getPackageName()+"."+c.getClassName());
		}

		for(XTrace trace:softwareLog)
		{
			//get the context object set for each trace.
			HashSet<String> ContextObjects =BasicOperators.ObjectSetClassPerTrace((ClassClass)resulti.get("Context"),trace);
			for(String ContextO:ContextObjects)//for each context object, we construct an invocation.
			{
				if(!ContextO.equals("0"))
				{
					XTrace invocation = factory.createTrace();
					for(XEvent event: trace)
					{
						//if the callee class object is a context object, and the callee method is setState or request
						if(XSoftwareExtension.instance().extractClassObject(event).equals(ContextO))
						{
							if(XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("request")).getMethodName())
									||XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("setState")).getMethodName()))
							{
								invocation.add(event);
//								System.out.println("add event: "+XConceptExtension.instance().extractName(event));
							}
						}
						//else if the caller class object is the context object, the callee method is handle, the callee class is of State class type
						else if(XSoftwareExtension.instance().extractCallerclassobject(event).equals(ContextO))
						{
							if(XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("handle")).getMethodName())
								&& stateClassTypeSet.contains(XSoftwareExtension.instance().extractPackage(event)+"."+XSoftwareExtension.instance().extractClass(event)))
							{
								invocation.add(event);
//								System.out.println("add event: "+XConceptExtension.instance().extractName(event));
							}
						}
					}
					
					if(invocation.size()!=0)
					{
						System.out.println("Invocation event number: "+invocation.size());
						invocationTraces.add(OrderingEventsNano.orderEventLogwithTimestamp(invocation, XSoftwareExtension.KEY_STARTTIMENANO));

					}
				}
				
			}
		}
		
		return invocationTraces;
	}
	
	
	public static HashSet<XEvent> setStateMethodCallSet(XTrace invocation, String requestName, String setStateMethodName)
	{
		HashSet<XEvent> setStateEvents = new HashSet<>();
		
		XEvent firstRequest=null;
		//get the first request event
		for(int i =0; i<invocation.size();i++)
		{
			if(XConceptExtension.instance().extractName(invocation.get(i)).equals(requestName))
			{
				firstRequest = invocation.get(i);
				break;
			}
		}
		
		if(firstRequest!=null)//get all setState events after the first request event. 
		{
			for(int i= invocation.indexOf(firstRequest)+1; i<invocation.size();i++)
			{
				if(XConceptExtension.instance().extractName(invocation.get(i)).equals(setStateMethodName))
				{
					setStateEvents.add(invocation.get(i));
				}
			}
		}
		
		return setStateEvents;
		
	}
	
	public static XEvent getFirstEventAfterEventX(XTrace invocation, XEvent eventX, String eventName)
	{
		
		for(int i =invocation.indexOf(eventX)+1;i<invocation.size();i++)
		{
			if(XConceptExtension.instance().extractName(invocation.get(i)).equals(eventName))
			{
				return invocation.get(i);
			}
		}
		return null;
	}

	public static XEvent getFirstEventAfterIndexABeforeIndexB(XTrace invocation, int A, int B, String eventName)
	{
		
		for(int i =A+1;i<B;i++)
		{
			if(XConceptExtension.instance().extractName(invocation.get(i)).equals(eventName))
			{
				return invocation.get(i);
			}
		}
		return null;
	}
	
	public static int getNextEventAfterIndexX(XTrace invocation, int X, String eventName)
	{
		
		for(int i=X+1;i<invocation.size();i++)
		{
			if(XConceptExtension.instance().extractName(invocation.get(i)).equals(eventName))
			{
				return i;
			}
		}
		return -1;
	}
}
