package software.dynamic.designpattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import XESSoftwareExtension.XSoftwareExtension;
import designpatterns.adapterpattern.ClassTypeHierarchy;
import designpatterns.framework.BasicOperators;
import designpatterns.framework.CandidateCombination;
import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;
import designpatterns.framework.PatternSetImpl;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;
import software.designpattern.ObserverPatternDiscoveryAndChecking;
import software.designpattern.StatePatternDiscoveryAndChecking;

public class StateStaticDiscovery {
	/*
	 * this method aims to discover a set of state design pattern candidates directly from software log. 
	 */
	public ArrayList<HashMap<String, Object>> DiscoverCompleteStatePattern(XLog softwareLog, ClassTypeHierarchy cth)
	{
		//store the final candidates that are detected by dynamic analysis from execution log. 
		 ArrayList<HashMap<String, Object>> result = new ArrayList<>();
		 
		 //for each possible Context + State combination, we have one role2values
		 HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
		 //initialize the keys of the map
		 role2values.put("Context", new ArrayList<>());
		 role2values.put("State", new ArrayList<>());
		 role2values.put("setState", new ArrayList<>());
		 role2values.put("request", new ArrayList<>());
		 role2values.put("handle", new ArrayList<>());
		 
		 //define some temps
		 HashSet<MethodClass> setStateSet = new HashSet<>();
		 HashSet<MethodClass> requestSet = new HashSet<>();
		 HashSet<MethodClass> handleSet = new HashSet<>();
		 //the method set of context and state. 
		 HashSet<MethodClass> contextMethods =new HashSet<>();
		 HashSet<MethodClass> stateMethods = new HashSet<>();
		 int flag =0;//if a group of candidates that satisfies the structural constraints are found. 
		 
		 for(HashSet<ClassClass> contexts: cth.getAllCTH())// for each group of classes->candidate subject 
		 {
			 //the method set of subjects, should have >=2 methods @structural1: the context should include at least 2 methods
			 if(BasicFunctions.MethodSetofClasses(contexts, softwareLog).size()>=2)
			 {
				 //for the state classes
				 for(HashSet<ClassClass> states: cth.getAllCTH())
				 {
					 if(!states.equals(contexts))//the same class cannot be used both as context and state 
					 {
						 flag =0;//check if the current pare of Context+State is a candidate
						 //the method set of contexts
						 contextMethods =BasicFunctions.MethodSetofClasses(contexts, softwareLog);
						 //the method set of states
						 stateMethods =BasicFunctions.MethodSetofClasses(states, softwareLog);
						 
						 setStateSet.clear();
						 
						 //setState
						 for(MethodClass setState: contextMethods)
						 {
							//The parameter set of m @structural2: the state class should be a parameter of setState
							for(ClassClass para: BasicOperators.ParameterSetofMethod(setState, softwareLog))
							{
								if(states.contains(para))//if a method has a parameter class that is of state class, it may be a setState
								{
									setStateSet.add(setState);
								}
							}
						 }
						 
						if(setStateSet.size()>=1)//@structural3: there should at lease exist a setState method
						{
							requestSet.clear();
							handleSet.clear();
							//request->handle 
							contextMethods.removeAll(setStateSet);//the candidate request set is obtained by removing all setState from the method set of context
							for(MethodClass requestM: contextMethods) //@structural4: request method should invoke handle method
							{
								for(MethodClass handleM:BasicOperators.MethodSetofMethod(requestM, softwareLog))
								{
									if(stateMethods.contains(handleM)){
										flag =1;
										requestSet.add(requestM);//add candidate request method
										handleSet.add(handleM);//add candidate handle method
									}
								}
							}
						}
						if(flag==1)//add the subject, observer, notify, update, reg and unreg
						{
							//clear the values of each roles
							role2values.get("Context").clear();
							role2values.get("State").clear();
							role2values.get("setState").clear();
							role2values.get("request").clear();
							role2values.get("handle").clear();

							//add subject, observer, notify, update, reg and unreg
							role2values.get("Context").add(contexts.toArray()[0]);
							role2values.get("State").add(states.toArray()[0]);
							role2values.get("setState").addAll(setStateSet);
							role2values.get("request").addAll(requestSet);
							role2values.get("handle").addAll(handleSet);
							
							//get the combination of all kinds of values, each combination is a candidate pattern instances
							for(HashMap<String, Object> candidate: CandidateCombination.combination(role2values))
							{
								result.add(candidate);
							}
							
						}
		
					 }//if state
				 }//for states
			 }
		 }//for contexts
		 return result;
		
	}

	/*
	 * for each complete state pattern candidate, we (1) first identify its invocation; and (2) check the behavior constraints. 
	 */
	public PatternSet StatePatternBehavioralConstraintsChecking(XFactory factory, XLog softwareLog, ClassTypeHierarchy cth, ArrayList<HashMap<String, Object>> result)
	{		
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
			HashSet<XTrace> invocationTraces = StatePatternDiscoveryAndChecking.statePatternInvocation(softwareLog, factory, cth, result.get(i));
			
			int numberofValidatedInvocation = 0;//the number of validated pattern invocations. 
			//for each invocation, we check the behavioral constraints.
			for(XTrace invocation: invocationTraces)
			{
				//@cardinality constraints: check invocation level constraints, request>=1 and handle >=1, and setState>=1;
				if(BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("handle")).getMethodName(), invocation)<1
						||BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("request")).getMethodName(), invocation)<1
						||BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("setState")).getMethodName(), invocation)<1)
				{
					continue;
				}
					
				//@invocation-constraint 1: the state change can only be done by state or context
				//the set of all setState (after the first request method), i.e., all state changes.
				HashSet<XEvent> setStateEvents =  StatePatternDiscoveryAndChecking.setStateMethodCallSet(invocation, 
						((MethodClass)result.get(i).get("request")).getMethodName(),
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

					if(validedsetState!=setStateEvents.size())//not all setstates are validated 
					{
						continue;
					}
				} 
					
				
				//@invocation-constraint 2: after state change the request method should invoke the handle method of the new state object
				//an exception case is that: for the last setState, there is no need to check the request
				HashSet<XEvent> setStateEventSet = BasicOperators.eventSetofMethodPerInvocation(invocation,
						((MethodClass)result.get(i).get("setState")).getMethodName());		
				
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
					
					if(validedsetState!=setStateEventSet.size())//not all setState are validated, we allow the last setState may not have handle.  
					{
						continue;
					}
				}
				
				

				numberofValidatedInvocation++;
			}
			
			if(numberofValidatedInvocation==0)//if there is no validated invocation, then the current candidate is not approved.
			{
				continue;
			}
			
			PatternClass NewP = BasicFunctions.CreatePatternInstance("State Pattern", result.get(i), softwareLog.size(), numberofValidatedInvocation);
			discoveredCandidateInstanceSet.add(NewP);
		}
		return discoveredCandidateInstanceSet;
	}
	
}
