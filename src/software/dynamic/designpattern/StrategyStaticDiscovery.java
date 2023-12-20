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
import software.designpattern.StrategyPatternDiscoveryAndChecking;

public class StrategyStaticDiscovery {
	/*
	 * this method aims to discover a set of Strategy design pattern candidates directly from software log. 
	 */
	public ArrayList<HashMap<String, Object>> DiscoverCompleteStrategyPattern(XLog softwareLog, ClassTypeHierarchy cth)
	{
		//store the final candidates that are detected by dynamic analysis from execution log. 
		 ArrayList<HashMap<String, Object>> result = new ArrayList<>();
		 
		 //for each possible Context + Strategy combination, we have one role2values
		 HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
		 //initialize the keys of the map
		 role2values.put("Context", new ArrayList<>());
		 role2values.put("Strategy", new ArrayList<>());
		 role2values.put("setStrategy", new ArrayList<>());
		 role2values.put("contextInterface", new ArrayList<>());
		 role2values.put("algorithmInterface", new ArrayList<>());
		 
		 //define some temps
		 HashSet<MethodClass> setStrategySet = new HashSet<>();
		 HashSet<MethodClass> contextInterfaceSet = new HashSet<>();
		 HashSet<MethodClass> algorithmInterfaceSet = new HashSet<>();
		 //the method set of context and 
		 HashSet<MethodClass> contextMethods =new HashSet<>();
		 HashSet<MethodClass> strategyMethods = new HashSet<>();
		 int flag =0;//if a group of candidates that satisfies the structural constraints are found. 
		 
		 for(HashSet<ClassClass> contexts: cth.getAllCTH())// for each group of classes->candidate subject 
		 {
			 //the method set of contexts, should have >=2 methods @structural1: the context should include at least 2 methods
			 if(BasicFunctions.MethodSetofClasses(contexts, softwareLog).size()>=2)
			 {
				 //for the state classes
				 for(HashSet<ClassClass> strategies: cth.getAllCTH())
				 {
					 if(!strategies.equals(contexts))//the same class cannot be used both as context and strategy 
					 {
						 flag =0;//check if the current pare of Context+Strategy is a candidate
						 //the method set of contexts
						 contextMethods =BasicFunctions.MethodSetofClasses(contexts, softwareLog);
						 //the method set of states
						 strategyMethods =BasicFunctions.MethodSetofClasses(strategies, softwareLog);
						 
						 setStrategySet.clear();
						 
						 //setStrategy 
						 for(MethodClass setStrategy: contextMethods)
						 {
							if(setStrategy.getMethodName().equals("init()"))
								 continue;
							//The parameter set of m @structural2: the Strategy class should be a parameter of setStrategy
							for(ClassClass para: BasicOperators.ParameterSetofMethod(setStrategy, softwareLog))
							{
								if(strategies.contains(para))//if a method has a parameter class that is of strategy class, it may be a setState
								{
									setStrategySet.add(setStrategy);
								}
							}
						 }
						 
						if(setStrategySet.size()>=1)//@structural3: there should at lease exist a setStrategy method
						{
							contextInterfaceSet.clear();
							algorithmInterfaceSet.clear();
							//contextInterface->algorithmInterface 
							contextMethods.removeAll(setStrategySet);//the candidate contextInterface set is obtained by removing all setStrategy from the method set of context
							for(MethodClass contextInterfaceM: contextMethods) //@structural4: contextInterface method should invoke algorithmInterface method
							{
								for(MethodClass algorithmInterfaceM:BasicOperators.MethodSetofMethod(contextInterfaceM, softwareLog))
								{
									if(strategyMethods.contains(algorithmInterfaceM)){
										flag =1;
										contextInterfaceSet.add(contextInterfaceM);//add candidate request method
										algorithmInterfaceSet.add(algorithmInterfaceM);//add candidate handle method
									}
								}
							}
						}
						if(flag==1)//add the subject, observer, notify, update, reg and unreg
						{
							//clear the values of each roles
							role2values.get("Context").clear();
							role2values.get("Strategy").clear();
							role2values.get("setStrategy").clear();
							role2values.get("contextInterface").clear();
							role2values.get("algorithmInterface").clear();

							//add subject, observer, notify, update, reg and unreg
							role2values.get("Context").add(contexts.toArray()[0]);
							role2values.get("Strategy").add(strategies.toArray()[0]);
							role2values.get("setStrategy").addAll(setStrategySet);
							role2values.get("contextInterface").addAll(contextInterfaceSet);
							role2values.get("algorithmInterface").addAll(algorithmInterfaceSet);
							
							//get the combination of all kinds of values, each combination is a candidate pattern instances
							for(HashMap<String, Object> candidate: CandidateCombination.combination(role2values))
							{
								result.add(candidate);
							}
							
						}
		
					 }//if strategy
				 }//for strategies
			 }
		 }//for contexts
		 return result;
		
	}

	public PatternSet StrategyPatternInvocationConstraintsChecking(XFactory factory, XLog softwareLog, ClassTypeHierarchy cth, ArrayList<HashMap<String, Object>> result)
	{
		// intermediate results that are complete candidates
		PatternSet discoveredCandidateInstanceSet = new PatternSetImpl(); 
		
		for(int i = 0; i<result.size(); i++)//each result(i) is a candidate pattern instance
		{	
			System.out.println(result.get(i));
			//get the strategy class type 
			HashSet<String> StrategyClassTypeHierarchy = new HashSet<>();
			for(ClassClass c:BasicOperators.typeHierarchyClassSet(cth, (ClassClass)result.get(i).get("Strategy")))
			{
				StrategyClassTypeHierarchy.add(c.getPackageName()+"."+c.getClassName());
			}
			
			//get the Context class type 
			HashSet<String> ContextClassTypeHierarchy = new HashSet<>();
			for(ClassClass c: BasicOperators.typeHierarchyClassSet(cth, (ClassClass)result.get(i).get("Context")))
			{
				ContextClassTypeHierarchy.add(c.getPackageName()+"."+c.getClassName());
			}
			
			//identify the invocations for each state pattern instance,
			HashSet<XTrace> invocationTraces = StrategyPatternDiscoveryAndChecking.strategyPatternInvocation(softwareLog, factory, cth, result.get(i));
			
			int numberofValidatedInvocation = 0;//the number of validated pattern invocations. 
			//for each invocation, we check the behavioral constraints.
			for(XTrace invocation: invocationTraces)
			{
				//@cardinality constraints: check invocation level constraints, contextInterface>=1 and algorithmInterface>=1;
				//no restriction for the setStrategy
				
				if(BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("contextInterface")).getMethodName(), invocation)<1
						||BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("algorithmInterface")).getMethodName(), invocation)<1)
				{
					continue;
				}
					
				//@invocation-constraint 1: the strategy change can only be done by strategy or context
				//the set of all setstrategy
				HashSet<XEvent> setStrategyEventSet =  BasicOperators.eventSetofMethodPerInvocation(invocation,
						((MethodClass)result.get(i).get("setStrategy")).getMethodName());
				if(setStrategyEventSet.size()!=0)
				{
					//for each one, its caller class should not be Strategy or context.
					int validedsetStrategy=0;
					for(XEvent setStrategyE: setStrategyEventSet)
					{
						String tempCallerClass = XSoftwareExtension.instance().extractCallerpackage(setStrategyE)+"."+XSoftwareExtension.instance().extractCallerclass(setStrategyE);
						if(!ContextClassTypeHierarchy.contains(tempCallerClass)
								&&!StrategyClassTypeHierarchy.contains(tempCallerClass))
						{
							validedsetStrategy++;
						}
					}
					if(validedsetStrategy!=setStrategyEventSet.size())//not all setStrategy are validated 
					{
						continue;
					}
				}
				
				
				//@invocation-constraint 2: contextInterface may invoke the algorithmInterface method of exactly one strategy class objects. 
				if(setStrategyEventSet.size()!=0)
				{
					int validedsetStrategy=0;
					for(XEvent setStrategyE: setStrategyEventSet)
					{
						//get the index of the next setState
						int nextSetStrategyEventIndex=StatePatternDiscoveryAndChecking.getNextEventAfterIndexX(invocation, 
								invocation.indexOf(setStrategyE), ((MethodClass)result.get(i).get("setStrategy")).getMethodName());
						if(nextSetStrategyEventIndex!=-1)//there exists a setStrategyEvent after the current one
						{
							//for each set Strategy event, we get the contextInterface after it. 
							XEvent firstContextInterfaceEvent =StatePatternDiscoveryAndChecking.getFirstEventAfterIndexABeforeIndexB(invocation, invocation.indexOf(setStrategyE),
									nextSetStrategyEventIndex, ((MethodClass)result.get(i).get("contextInterface")).getMethodName());
							if(firstContextInterfaceEvent==null)//there do not exist such a contextInterface event
							{
								continue;
							}
							else 
							{
								//get the callee object set of the invoked handle method of request
								HashSet<String> calleeObjectofAlgorithmInterface =BasicOperators.calleeObjectSetofInvokedEventsPerTrace(firstContextInterfaceEvent, invocation);
								//for each setState, we get its parameter mapping
								HashMap<ClassClass, String> paraMappingofsetStrategyEvent = ObserverPatternDiscoveryAndChecking.constructParameterMapping(setStrategyE);
								for(ClassClass c:paraMappingofsetStrategyEvent.keySet())
								{
									if(StrategyClassTypeHierarchy.contains(c.toString()))
									{
										if(calleeObjectofAlgorithmInterface.contains(paraMappingofsetStrategyEvent.get(c)))
										{
											validedsetStrategy++;
											break;
										}
										
									}
								}
							}
						}
						else//this is the last setState
						{
							validedsetStrategy++;
						}
					}

					if(validedsetStrategy!=setStrategyEventSet.size())//not all setState are validated, we allow the last setState may not have handle.  
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
			
			PatternClass NewP = BasicFunctions.CreatePatternInstance("Strategy Pattern", result.get(i), softwareLog.size(), numberofValidatedInvocation);
			discoveredCandidateInstanceSet.add(NewP);
		}
		return discoveredCandidateInstanceSet;
	}
	
	
}
