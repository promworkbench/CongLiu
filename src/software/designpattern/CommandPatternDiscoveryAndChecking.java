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

public class CommandPatternDiscoveryAndChecking {

	/* for command pattern, the command, receiver and execute are included while invoker, call, action are missing.
	 * the DiscoveryCompleteCommandPattern aims to find all missing roles from the execution log. 
	 */
	
	public ArrayList<HashMap<String, Object>> DiscoverCompleteCommandPattern(UIPluginContext context, PatternClass patternCandidate, XLog softwareLog, ClassTypeHierarchy cth, HashMap<String, ArrayList<Object>> role2values)
	{
		//for those role with value, we need first make sure the values are also included in the log.
		//for Command, we extend the class set to all classes with typehierarchy information
		if(!BasicOperators.classIncludedInLog((ClassClass)role2values.get("Command").get(0), softwareLog))//the class is not included in the log
		{
			context.log("The value of Command is not included in the execution log!", MessageLevel.WARNING);
		}
		//get all classes that of typehierarchy with the context class, and also included in the log.
		HashSet<ClassClass> alternativeCommandClassSet =BasicOperators.typeHierarchyClassSetInLog(cth, softwareLog, (ClassClass)role2values.get("Command").get(0));

		if(alternativeCommandClassSet.size()!=0){
			for(ClassClass cc: alternativeCommandClassSet)
			{
				if(!role2values.get("Command").contains(cc))
				{
					role2values.get("Command").add(cc);
				}
			}
		}
		
		//for Receiver, we extend the class set to all classes with typehierarchy information
		if(!BasicOperators.classIncludedInLog((ClassClass)role2values.get("Receiver").get(0), softwareLog))//the class is not included in the log
		{
			context.log("The value of Receiver is not included in the execution log!", MessageLevel.WARNING);
		}
		//get all classes that of typehierarchy with the context class, and also included in the log.
		HashSet<ClassClass> alternativeReceiverClassSet =BasicOperators.typeHierarchyClassSetInLog(cth, softwareLog, (ClassClass)role2values.get("Receiver").get(0));

		if(alternativeReceiverClassSet.size()!=0){
			for(ClassClass cc: alternativeReceiverClassSet)
			{
				if(!role2values.get("Receiver").contains(cc))
				{
					role2values.get("Receiver").add(cc);
				}
			}
		}

		//for execute
		if(!BasicOperators.methodIncludedInLog((MethodClass)role2values.get("execute").get(0), softwareLog))// the method is not included in the log
		{
			context.log("The value of execute is not included in the execution log!", MessageLevel.WARNING);
		}
		HashSet<MethodClass> alternativeMethodSet =BasicOperators.typeHierarchyMethodSetInLog(cth, softwareLog, (MethodClass)role2values.get("execute").get(0));
		
		if(alternativeMethodSet.size()!=0){
			for(MethodClass mm: alternativeMethodSet)
			{
				if(!role2values.get("execute").contains(mm))
				{
					role2values.get("execute").add(mm);
				}
			}
		}
		
		//for the action role, (1) it is a method of the receiver; (2) it is invoked by the excute method
		HashSet<MethodClass> methodSetofReceiver = new HashSet<MethodClass>();
		
		//get the method set of Receiver role 
		for(Object c: role2values.get("Receiver"))
		{
			methodSetofReceiver.addAll(BasicOperators.MethodSetofClass((ClassClass)c, softwareLog));//get all type hierarchy classes
		}
		
		//get the method set invoked by execute.
		HashSet<MethodClass> methodSetInovkedByExecute = new HashSet<MethodClass>();
		for(Object m: role2values.get("execute"))
		{
			methodSetInovkedByExecute.addAll(BasicOperators.MethodSetofMethod((MethodClass)m, softwareLog));
		}
		
		for(MethodClass m: methodSetInovkedByExecute)
		{
			if(methodSetofReceiver.contains(m)){
				if(!m.getMethodName().equals("init()"))//init() should not be included
				{
					if(!role2values.get("action").contains(m))
					{
						role2values.get("action").add(m);
					}
				}
			}
		}
				
		//for the call role, (1) it is a method of invoker but the invoker is unclear; (2) it is the caller method of execute; (3) it cannot be the caller method of action. 
		//get the caller method set of execute
		HashSet<MethodClass> methodSetCallingExecute = new HashSet<>();
		for(Object m: role2values.get("execute"))
		{
			methodSetCallingExecute.addAll(BasicOperators.CallerMethodSetofMethod((MethodClass)m, softwareLog));
		}
		//get the caller method set of action.
		HashSet<MethodClass> methodSetCallingAction = new HashSet<>();
		for(Object m: role2values.get("action"))
		{
			methodSetCallingAction.addAll(BasicOperators.CallerMethodSetofMethod((MethodClass)m, softwareLog));
		}
		for(MethodClass m: methodSetCallingExecute)// if a method belongs to the caller of execute but not be the caller of action, it is a candidate call
		{
			if(!methodSetCallingAction.contains(m))	{
				if(!role2values.get("call").contains(m))
				{
					role2values.get("call").add(m);
				}
			}
		}
		
		//for the invoker, (1) it is the class of call; (2) it cannot be command and receiver
		//get the class set of call
		HashSet<ClassClass> classSetofCall= new HashSet<>();
		for(Object m: role2values.get("call"))
		{
			classSetofCall.add(BasicOperators.MethodToClass((MethodClass)m));
		}
		for(ClassClass c: classSetofCall)
		{
			if(!role2values.get("Receiver").contains(c)
					&&!role2values.get("Command").contains(c))
			{
				if(!role2values.get("Invoker").contains(c))
				{
					role2values.get("Invoker").add(c);
				}
				
			}
		}
				
		
		//till now the command pattern candidate should complete, each role may have multiple values.  
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
		
//		for(int i = 0; i<result.size(); i++)
//		{	
//			PatternClass NewP = BasicOperators.CreatePatternInstance(patternCandidate, result.get(i), softwareLog.size(), 0);
//			discoveredCandidateInstanceSet.add(NewP);
//		}
		return result;
	}

	/*
	 * for each complete command pattern candidate, we (1) first identify its invocation; and (2) check the behavior constraints. 
	 * 2017-6-1:9:43
	 */
	public PatternSet CommandPatternInvocationConstraintsChecking(UIPluginContext context, XFactory factory, PatternClass patternCandidate, XLog softwareLog, ClassTypeHierarchy cth, ArrayList<HashMap<String, Object>> result)
	{
		if(result==null)//if there is no complete candidates discovered, return null.
		{
			return null;
		}
		
		// intermediate results to store complete but not validated candidates
		PatternSet discoveredCandidateInstanceSet = new PatternSetImpl(); 
		
		for(int i = 0; i<result.size(); i++)//each result(i) is a candidate pattern instance
		{	
			//identify the invocations for each command pattern instance,
			HashSet<XTrace> invocationTraces = commandPatternInvocation(softwareLog, factory, cth, result.get(i));
			
			int numberofValidatedInvocation = 0;//the number of validated pattern invocations. 
			//for each invocation, we check the behavioral constraints.
			for(XTrace invocation: invocationTraces)
			{
				//@cardinality constraints: check invocation level constraints, call>=1, execute>=1, and action>=1;
				if(BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("call")).getMethodName(), invocation)<1
						||BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("execute")).getMethodName(), invocation)<1
						||BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("action")).getMethodName(), invocation)<1)
				{
					context.log("instance candidate not validated:[cardinality]!", MessageLevel.WARNING);
					continue;
				}
					
				//@invocation-constraint 1: the action cannot be invoked by call method is guaranteed by the discovery part. 
				//call should invoke the execute method of exactly one command object. 
				HashSet<String> executeObjects = new HashSet<>();
				for(String o: BasicOperators.CalleeObjectSetPerTrace(((MethodClass)result.get(i).get("call")).getMethodName(), invocation))
				{
					//input sequence: callee method, caller method, caller object, invocation
					executeObjects.addAll(BasicOperators.CalleeObjectSetPerTrace(((MethodClass)result.get(i).get("execute")).getMethodName(),
							((MethodClass)result.get(i).get("call")).getMethodName(), o, invocation));
				}

				
				//@invocation-constraint 2: execute should invoke the action method of exactly one command object. 
				HashSet<String> actionObjects = new HashSet<>();
				for(String o: BasicOperators.CalleeObjectSetPerTrace(((MethodClass)result.get(i).get("execute")).getMethodName(), invocation))
				{
					//input sequence: callee method, caller method, caller object, invocation
					actionObjects.addAll(BasicOperators.CalleeObjectSetPerTrace(((MethodClass)result.get(i).get("action")).getMethodName(),
							((MethodClass)result.get(i).get("execute")).getMethodName(), o, invocation));
				}
				
				System.out.println("execute objects"+executeObjects);
				System.out.println("action objects"+actionObjects);
				if(executeObjects.size()==1 && actionObjects.size()==1)// one or more times.
				{
					numberofValidatedInvocation++;
				}			
			}
			if(numberofValidatedInvocation==0)//if there is no validated invocation, then the current candidate is not approved.
			{
				context.log("candidate not validated:[constraints: call->execute||execute->action]!", MessageLevel.WARNING);
				continue; //go to the next candidate
			}
			
			PatternClass NewP = BasicOperators.CreatePatternInstance(patternCandidate, result.get(i), softwareLog.size(), numberofValidatedInvocation);
			discoveredCandidateInstanceSet.add(NewP);
		}
		return discoveredCandidateInstanceSet;
	}
	
	
	/*
	 * invocation identification for command Pattern
	 */
	public static HashSet<XTrace> commandPatternInvocation(XLog softwareLog, XFactory factory, ClassTypeHierarchy cth, HashMap<String, Object> resulti)
	{
		//identify the invocations for each command pattern instance,
		HashSet<XTrace> invocationTraces = new HashSet<>();
		
		//the class set of invoker role.
		HashSet<ClassClass> invokerClassTypeHierarchy=BasicOperators.typeHierarchyClassSet(cth, (ClassClass)resulti.get("Invoker"));
		HashSet<String> invokerClassTypeSet = new HashSet<>();
		for(ClassClass c: invokerClassTypeHierarchy)//construct the state class type set.
		{
			invokerClassTypeSet.add(c.getPackageName()+"."+c.getClassName());
		}
		
		//the class set of Receiver role.
		HashSet<ClassClass> receiverClassTypeHierarchy=BasicOperators.typeHierarchyClassSet(cth, (ClassClass)resulti.get("Receiver"));
		HashSet<String> receiverClassTypeSet = new HashSet<>();
		for(ClassClass c: receiverClassTypeHierarchy)//construct the state class type set.
		{
			receiverClassTypeSet.add(c.getPackageName()+"."+c.getClassName());
		}
		
		
		for(XTrace trace:softwareLog)
		{
			//get the command object set for each trace.
			HashSet<String> CommandObjects =BasicOperators.ObjectSetClassPerTrace((ClassClass)resulti.get("Command"),trace);
			for(String CommandO:CommandObjects)//for each command object, we construct an invocation.
			{
				if(!CommandO.equals("0"))// exclude static case.
				{
					XTrace invocation = factory.createTrace();
					for(XEvent event: trace)
					{
						//if the callee class object is a command object, and the callee method is execute
						if(XSoftwareExtension.instance().extractClassObject(event).equals(CommandO))
						{
							if(XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("execute")).getMethodName()))
							{
								invocation.add(event);
								System.out.println("add event: "+XConceptExtension.instance().extractName(event));
							}
						}
						//else if the caller class object is the command object, the callee method is action, the callee class is of Receiver class type
						else if(XSoftwareExtension.instance().extractCallerclassobject(event).equals(CommandO))
						{
							if(XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("action")).getMethodName())
								&& receiverClassTypeSet.contains(XSoftwareExtension.instance().extractPackage(event)+"."+XSoftwareExtension.instance().extractClass(event)))
							{
								invocation.add(event);
								System.out.println("add event"+XConceptExtension.instance().extractName(event));
							}
						}
						//else if the callee method is call, the callee class is Invoker, callee object should invoke the (command object+execute),  
						else if(XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("call")).getMethodName()))
						{
							if(invokerClassTypeSet.contains(XSoftwareExtension.instance().extractPackage(event)+"."+XSoftwareExtension.instance().extractClass(event))
									&& BasicOperators.CallerObjectSetPerTrace(((MethodClass)resulti.get("execute")).getMethodName(), CommandO, trace).contains(XSoftwareExtension.instance().extractClassObject(event)))
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
