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

public class ObserverPatternDiscoveryAndChecking {
	/* for observer pattern, the subject, observer and notify are included while register, un-register, update are missing.
	 * the DiscoveryCompleteObserverPattern aims to find all missing roles from the execution log. 
	 */
	public ArrayList<HashMap<String, Object>> DiscoverCompleteObserverPattern(UIPluginContext context, PatternClass patternCandidate, XLog softwareLog, ClassTypeHierarchy cth, HashMap<String, ArrayList<Object>> role2values)
	{
		//for those role with value, we need first make sure the values are also included in the log.
		//for Subject, we extend the class set to all classes with typehierarchy information
		if(!BasicOperators.classIncludedInLog((ClassClass)role2values.get("Subject").get(0), softwareLog))//the class is not included in the log
		{
			context.log("The value of Subject is not included in the execution log!", MessageLevel.WARNING);
			//return null; //if there exist missing value, then this pattern is not considered anymore. 
		}
		//get all classes that of typehierarchy with the Subject class, and also included in the log.
		HashSet<ClassClass> alternativeSubjectClassSet =BasicOperators.typeHierarchyClassSetInLog(cth, softwareLog, (ClassClass)role2values.get("Subject").get(0));

		if(alternativeSubjectClassSet.size()!=0){
			for(ClassClass cc: alternativeSubjectClassSet)
			{
				if(!role2values.get("Subject").contains(cc))
				{
					role2values.get("Subject").add(cc);
				}
			}
		}
		
		//for Observer, we extend the class set to all classes with typehierarchy information
		if(!BasicOperators.classIncludedInLog((ClassClass)role2values.get("Observer").get(0), softwareLog))//the class is not included in the log
		{
			context.log("The value of Observer is not included in the execution log!", MessageLevel.WARNING);
			//return null;
		}
		
		//get all classes that of typehierarchy with the Observer class, and also included in the log.
		HashSet<ClassClass> alternativeObserverClassSet =BasicOperators.typeHierarchyClassSetInLog(cth, softwareLog, (ClassClass)role2values.get("Observer").get(0));

		if(alternativeObserverClassSet.size()!=0){
			for(ClassClass cc: alternativeObserverClassSet)
			{
				if(!role2values.get("Observer").contains(cc))
				{
					role2values.get("Observer").add(cc);
				}
			}
		}

		//for notify
		if(!BasicOperators.methodIncludedInLog((MethodClass)role2values.get("notify").get(0), softwareLog))// the method is not included in the log
		{
			context.log("The value of notify is not included in the execution log!", MessageLevel.WARNING);
			//return null;
		}
		HashSet<MethodClass> alternativeMethodSet =BasicOperators.typeHierarchyMethodSetInLog(cth, softwareLog, (MethodClass)role2values.get("notify").get(0));
		if(alternativeMethodSet.size()!=0){
			for(MethodClass mm: alternativeMethodSet)
			{
				if(!role2values.get("notify").contains(mm))
				{
					role2values.get("notify").add(mm);
				}
			}
		}
		
		//for the update role, (1) it is a method of the observer; (2) it is invoked by the notify method
		HashSet<MethodClass> methodSetofObserver = new HashSet<MethodClass>();
		
		//get the method set of Observer role 
		for(Object c: role2values.get("Observer"))
		{
			methodSetofObserver.addAll(BasicOperators.MethodSetofClass((ClassClass)c, softwareLog));//get all type hierarchy classes
		}
		
		//get the method set invoked by notify.
		HashSet<MethodClass> methodSetInovkedByNotify = new HashSet<MethodClass>();
		for(Object m: role2values.get("notify"))
		{
			methodSetInovkedByNotify.addAll(BasicOperators.MethodSetofMethod((MethodClass)m, softwareLog));
		}
		
		for(MethodClass m: methodSetInovkedByNotify)
		{
			if(methodSetofObserver.contains(m)){
				if(!m.getMethodName().equals("init()"))//init() should not be included
				{
					if(!role2values.get("update").contains(m))
					{
						role2values.get("update").add(m);
					}
				}
			}
		}
				
		//for the register(or unregister) role, (1) it is a method of Subject; (2) it should include a parameter of Observer type. 
		HashSet<MethodClass> methodSetofSubject = new HashSet<MethodClass>();
		//get the method set of subject role 
		for(Object c: role2values.get("Subject"))
		{
			methodSetofSubject.addAll(BasicOperators.MethodSetofClass((ClassClass)c, softwareLog));//get all type hierarchy classes
		}
		
		//only select those with observer class as an input parameter type. 
		HashSet<ClassClass> observerClassTypeHierarchy = new HashSet<>();
		for(Object observerClass: role2values.get("Observer"))
		{
			observerClassTypeHierarchy.addAll(BasicOperators.typeHierarchyClassSet(cth, (ClassClass)observerClass));
		}
		for(MethodClass m: methodSetofSubject)
		{
			//The parameter set of m
			for(ClassClass p: BasicOperators.ParameterSetofMethod(m, softwareLog))
			{
				if(observerClassTypeHierarchy.contains(p)){//if a method has a parameter class that is of observer class, it may be a register class
					if(!m.getMethodName().equals("init()"))//init() should not be included
					{
//						if(!role2values.get("update").contains(m))
//						{role2values.get("update").add(m);}
						if(!role2values.get("register").contains(m))
						{role2values.get("register").add(m);}
						if(!role2values.get("unregister").contains(m))
						{role2values.get("unregister").add(m);}
						break;
					}
				}
			}
		}

		
		//till now the observer pattern candidate should be complete, each role may have multiple values.  
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
		System.out.println(result);
//		for(int i = 0; i<result.size(); i++)
//		{	
//			PatternClass NewP = BasicOperators.CreatePatternInstance(patternCandidate, result.get(i), softwareLog.size(), 0);
//			discoveredCandidateInstanceSet.add(NewP);
//		}
		return result;
	}


	/*
	 * for each complete observer pattern candidate, we (1) first identify its invocation; and (2) check the behavior constraints. 
	 * 2017-6-2:4:20pm
	 */
	public PatternSet ObserverPatternInvocationConstraintsChecking(UIPluginContext context, XFactory factory, PatternClass patternCandidate, XLog softwareLog, ClassTypeHierarchy cth, ArrayList<HashMap<String, Object>> result)
	{
		
		if(result==null)//if there is no complete candidates discovered, return null.
		{
			return null;
		}
		
		// intermediate results to store complete but not validated candidates
		PatternSet discoveredCandidateInstanceSet = new PatternSetImpl(); 
		
		for(int i = 0; i<result.size(); i++)//each result(i) is a candidate pattern instance
		{	
			context.log(result.get(i).toString(), MessageLevel.WARNING);
			
			//identify the invocations for each observer pattern instance,
			HashSet<XTrace> invocationTraces = observerPatternInvocation(softwareLog, factory, cth, result.get(i));
			
			int numberofValidatedInvocation = 0;//the number of validated pattern invocations. 
			//for each invocation, we check the behavioral constraints.
			for(XTrace invocation: invocationTraces)
			{
				//check invocation level constraints, register>=1, unregister>=1, update>=1 and notify>=1;
				if(BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("notify")).getMethodName(), invocation)<1
						||BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("update")).getMethodName(), invocation)<1
						||BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("register")).getMethodName(), invocation)<1
						||BasicOperators.MethodcallNumberPerTrace(((MethodClass)result.get(i).get("unregister")).getMethodName(), invocation)<1)
				{
					context.log("instance candidate not validated:[cardinality]!", MessageLevel.WARNING);
					continue;
				}
					
				//@invocation-constraint 1: for each observer pattern instance, an observer object should be first registered to the subject object and then unregistered. 
				//it is allowed that an observer is not registered but unregister.
				//get the observer class type 
				HashSet<ClassClass> ObserverClassTypeHierarchy = new HashSet<>();
				ObserverClassTypeHierarchy.addAll(BasicOperators.typeHierarchyClassSet(cth, (ClassClass)result.get(i).get("Observer")));
				//get the observer object set
				HashSet<String> observerObjects = ObserverObjectSet(invocation,ObserverClassTypeHierarchy,
						((MethodClass)result.get(i).get("register")).getMethodName(), 
						((MethodClass)result.get(i).get("unregister")).getMethodName());
				
				System.out.println("observer obj set: "+ observerObjects);
				//@invocation-constraint 1: for each observer object, for each a register method with the observer object as input, there exist a unregister with observer object as input.
				int validedObserverObjectsNumber=0;
				for(String observerObj: observerObjects)
				{
					System.out.println("current observer obj: "+ observerObj);
					//the register event set
					HashSet<XEvent> registerEventSet=BasicOperators.getMethodCallSetwithParaObj(invocation, observerObj, ((MethodClass)result.get(i).get("register")).getMethodName());
					
					if(registerEventSet.size()==0)// there exists some observer object that are not registered but unregistered
					{
						context.log("instance candidate not validated:[no register for the observer object]!", MessageLevel.WARNING);
						break;
					}
					else// for each observer object, there exist at least one register method that take this object as input. 
					{	//for each register event in the set, there should be a unregister event in the invocation satisfying: (1) it contains a parameter value of the current observer object, and (2) executed after register event 
						int flag =1;
						for(XEvent regEvent: registerEventSet)
						{
							if(!checkExistenceUnregister(regEvent, invocation, observerObj, ((MethodClass)result.get(i).get("unregister")).getMethodName()))
							{
								flag=0;//there exist a register without unregister
							}
						}
						if(flag ==1)
						validedObserverObjectsNumber++;
					}
					
				}
				
				if(validedObserverObjectsNumber!=observerObjects.size())//if all observer objects are validated.
				{
					context.log("instance candidate not validated:[constraints: each observer object should be first register and then unregister]!", MessageLevel.WARNING);
					continue;
				}
				
				
				//@invocation-constraint 2: each notify method call should invoke the update methods of currently registered observers
				//notify event set
				int validedNotifyNumber=0;
				HashSet<XEvent> notifyEventSet = BasicOperators.eventSetofMethodPerInvocation(invocation,((MethodClass)result.get(i).get("notify")).getMethodName());
				System.out.println("Number of notify events: "+notifyEventSet.size());
				if(notifyEventSet.size()==0)// no notify 
				{
					context.log("instance candidate not validated:[no notify method in the current invocation]!", MessageLevel.WARNING);
					break;
				}
				else// for notify
				{
					for(XEvent notifyEvent: notifyEventSet)
					{
						//the callee object set of invoked update methods 
						HashSet<String> updateObjects =BasicOperators.calleeObjectSetofInvokedEventsPerTrace(notifyEvent,invocation);
						//the currently registered observer object set
						HashSet<String> registeredObjects=currentlyRegisteredObservers(notifyEvent, observerObjects, invocation, 
								((MethodClass)result.get(i).get("register")).getMethodName(), 
								((MethodClass)result.get(i).get("unregister")).getMethodName());
						System.out.println("update objects: "+updateObjects);
						System.out.println("registered objects: "+registeredObjects);
						if(registeredObjects.equals(updateObjects))
						{
							validedNotifyNumber++;//all registered observer are updated, this is a validated notify. 
						}
					}
				}
				System.out.println("Number of validated notify events: "+validedNotifyNumber);
				// only all notify methods are validated, this constraint is validated
				if(validedNotifyNumber!=notifyEventSet.size())//if all notify are validated.
				{
					context.log("instance candidate not validated:[constraints: each notify method call should invoke the update methods of currently registered observers]!", MessageLevel.WARNING);
					continue;
				}
			
				
				numberofValidatedInvocation++;
			}
			if(numberofValidatedInvocation==0)//if there is no validated invocation, then the current candidate is not approved.
			{
				context.log("candidate not validated:[constraints: observer[reg+unreg]||notify->update]!", MessageLevel.WARNING);
				continue; //go to the next candidate
			}

			PatternClass NewP = BasicOperators.CreatePatternInstance(patternCandidate, result.get(i), softwareLog.size(), numberofValidatedInvocation);
			
			discoveredCandidateInstanceSet.add(NewP);
		
		}
		return discoveredCandidateInstanceSet;
	}
	
	
	/*
	 * invocation identification for observer Pattern
	 */
	public static HashSet<XTrace> observerPatternInvocation(XLog softwareLog, XFactory factory, ClassTypeHierarchy cth, HashMap<String, Object> resulti)
	{
		//identify the invocations for each observer pattern instance,
		HashSet<XTrace> invocationTraces = new HashSet<>();
		
		//the class set of observer role.
		HashSet<ClassClass> observerClassTypeHierarchy=BasicOperators.typeHierarchyClassSet(cth, (ClassClass)resulti.get("Observer"));
		HashSet<String> observerClassTypeSet = new HashSet<>();
		for(ClassClass c: observerClassTypeHierarchy)//construct the state class type set.
		{
			observerClassTypeSet.add(c.getPackageName()+"."+c.getClassName());
		}
		
		for(XTrace trace:softwareLog)
		{
			//get the subject object set for each trace.
			HashSet<String> SubjectObjects =BasicOperators.ObjectSetClassPerTrace((ClassClass)resulti.get("Subject"),trace);
			for(String SubjectO:SubjectObjects)//for each Subject object, we construct an invocation.
			{
				if(!SubjectO.equals("0"))
				{
					XTrace invocation = factory.createTrace();
					for(XEvent event: trace)
					{
						//if the callee class object is a subject object, and the callee method is register, unregister, notify
						if(XSoftwareExtension.instance().extractClassObject(event).equals(SubjectO))
						{
							if(XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("register")).getMethodName())
									||XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("unregister")).getMethodName())
									||XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("notify")).getMethodName()))
							{
								invocation.add(event);
								//System.out.println("add event: "+XConceptExtension.instance().extractName(event));
							}
						}
						//else if the caller class object is the subject object, the callee method is update, the callee class is of observer class type
						else if(XSoftwareExtension.instance().extractCallerclassobject(event).equals(SubjectO))
						{
							if(XConceptExtension.instance().extractName(event).equals(((MethodClass)resulti.get("update")).getMethodName())
								&& observerClassTypeSet.contains(XSoftwareExtension.instance().extractPackage(event)+"."+XSoftwareExtension.instance().extractClass(event)))
							{
								invocation.add(event);
								//System.out.println("add event: "+XConceptExtension.instance().extractName(event));
							}
						}
					}
					//order the invocation trace by start time stamp. 
					
					if(invocation.size()!=0)
					invocationTraces.add(OrderingEventsNano.orderEventLogwithTimestamp(invocation, XSoftwareExtension.KEY_STARTTIMENANO));
				}
				
			}
		}
		
		return invocationTraces;
	}

	
	/*
	 * Given an invocation trace, a class typehierarchy of observer, register method and unregister method, we get a set of parameter object of this class typehierarchy
	 * 
	 */
	public static HashSet<String> ObserverObjectSet(XTrace invocation, HashSet<ClassClass> ObserverClassTypeHierarchy, String registerMethodName, String unregisterMethodName)
	{
		HashSet<String> observerObjSet = new HashSet<>();
		for(XEvent event: invocation)
		{
			/*
			 * note: it is impossible that an object is directly unregistered without registering. 
			 */
			if(XConceptExtension.instance().extractName(event).equals(registerMethodName)
					||XConceptExtension.instance().extractName(event).equals(unregisterMethodName))
			{
				//parse the parameter class set (as a  sequence), and get the value. 
				HashMap<ClassClass,String> mapping = constructParameterMapping(event);
				for(ClassClass cc: mapping.keySet())
				{
					if(ObserverClassTypeHierarchy.contains(cc))
					{
						observerObjSet.add(mapping.get(cc));
					}
				}
			}
		}
		
		return observerObjSet;
	}
	
	/*
	 * construct the mapping from parameter type (class) to object value (string)
	 */
	public static HashMap<ClassClass,String> constructParameterMapping(XEvent event)
	{
		
		HashMap<ClassClass,String> parameterType2Value = new HashMap<>();
		
		ArrayList<String> currentParameterTypeList = new ArrayList<String>();
		ArrayList<String> currentParameterValueList = new ArrayList<String>();
		
		String tempParaType = XSoftwareExtension.instance().extractParameterTypeSet(event);
		String tempParaValue = XSoftwareExtension.instance().extractParameterValueSet(event);

		if(tempParaType.contains(","))// more than one parameters for the current method
		{
			for(String paraT: tempParaType.split("\\,"))
			{
				currentParameterTypeList.add(paraT);
			}
			if(tempParaValue!=null)
			{
				for(String paraV: tempParaValue.split("\\,"))
				{
					currentParameterValueList.add(paraV);
				}
			}
			else
			{
				for(int i =0;i<currentParameterTypeList.size();i++)
				{
					currentParameterValueList.add(null);
				}
			}
			
		}
		else
		{
			if (tempParaType.contains("."))// only one parameter for the current method
			{
				currentParameterTypeList.add(tempParaType);
				currentParameterValueList.add(tempParaValue);
			}
		}
		
		if(currentParameterTypeList.size()==0)//no parameter for the current method
		{
			return parameterType2Value;
		}
		else //one or more parameter for the current method
		{
			for(int i=0;i<currentParameterTypeList.size();i++)
			{
				ClassClass tempClass = new ClassClass();
				tempClass.setClassName(BasicOperators.extractClass(currentParameterTypeList.get(i)));
				tempClass.setPackageName(BasicOperators.extractPackage(currentParameterTypeList.get(i)));
				
				parameterType2Value.put(tempClass, currentParameterValueList.get(i));
			}
			return parameterType2Value;
		}
	}
	
	/*
	 * check if these exist a unregister method call that has the same observer object parameter of register method call. 
	 */
	public static boolean checkExistenceUnregister(XEvent regEvent, XTrace invocation, String observerObj, String unregisterMethodName)
	{
		for(int n= invocation.indexOf(regEvent)+1;n<invocation.size();n++)
		{
			if(BasicOperators.getParaObjSet(invocation.get(n)).contains(observerObj)
					&& XConceptExtension.instance().extractName(invocation.get(n)).equals(unregisterMethodName))
			{
				return true;
			}
		}
		return false;
		
	}
	
	/*
	 * get the currently registered observer objects
	 */
	public static HashSet<String> currentlyRegisteredObservers(XEvent notifyEvent, HashSet<String> allObserverObjects, XTrace invocation, String registerMethodName, String unregisterMethodName)
	{
		HashSet<String> currentObservers = new HashSet<>();
		for(int i =0;i<invocation.indexOf(notifyEvent);i++)
		{
			if(XConceptExtension.instance().extractName(invocation.get(i)).equals(registerMethodName))// registered objects
			{
				for(String obj: BasicOperators.getParaObjSet(invocation.get(i)))
				{
					if(allObserverObjects.contains(obj))
						currentObservers.add(obj);
				}
			}
			else if(XConceptExtension.instance().extractName(invocation.get(i)).equals(unregisterMethodName))//unregistered objects
			{
				if(currentObservers.size()!=0)// if the current observers are empty, there is no need to remove 
				{
					for(String obj: BasicOperators.getParaObjSet(invocation.get(i)))
					{
						if(currentObservers.contains(obj))
							currentObservers.remove(obj);
					}
				}
				
			}
		}
		
		return currentObservers;
		
	}
}
