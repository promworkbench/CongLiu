package software.dynamic.designpattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import designpatterns.adapterpattern.ClassTypeHierarchy;
import designpatterns.framework.BasicOperators;
import designpatterns.framework.CandidateCombination;
import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;
import designpatterns.framework.PatternSetImpl;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;
import software.designpattern.ObserverPatternDiscoveryAndChecking;

public class ObserverStaticDiscovery {
	/*
	 * this method aims to discover a set of observer design pattern candidates directly from software log. 
	 */
	public ArrayList<HashMap<String, Object>> DiscoverCompleteObserverPattern(XLog softwareLog, ClassTypeHierarchy cth)
	{
		//store the final candidates that are detected by dynamic analysis from execution log. 
		 ArrayList<HashMap<String, Object>> result = new ArrayList<>();
		 
		 //for each possible Subject + Observer combination, we have one role2values
		 HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
		 //initialize the keys of the map
		 role2values.put("Subject", new ArrayList<>());
		 role2values.put("Observer", new ArrayList<>());
		 role2values.put("update", new ArrayList<>());
		 role2values.put("notify", new ArrayList<>());
		 role2values.put("register", new ArrayList<>());
		 role2values.put("unregister", new ArrayList<>());
		 
		 //define some temps
		 HashSet<MethodClass> regUnregSet = new HashSet<>();
		 HashSet<MethodClass> notifySet = new HashSet<>();
		 HashSet<MethodClass> updateSet = new HashSet<>();
		 //the method set of subjects and observers
		 HashSet<MethodClass> subjectMethods =new HashSet<>();
		 HashSet<MethodClass> observerMethods = new HashSet<>();
		 
		 int flag =0;//if a group of candidates that satisfies the structural constraints are found, we have flag =1.
		 
		 for(HashSet<ClassClass> subjects: cth.getAllCTH())// for each group of classes->candidate subject 
		 {
			 //the method set of subjects, should have >=3 methods @structural1: the subject should include as least 3 methods
			 if(BasicFunctions.MethodSetofClasses(subjects, softwareLog).size()>=3)
			 {
				 //for the observer classes
				 for(HashSet<ClassClass> observers: cth.getAllCTH())
				 {
					 if(!observers.equals(subjects))//the same class cannot be used both as subject and observers
					 {
						 flag =0;//check if the current pare of subject+observer is a candidate
						 //the method set of subjects
						 subjectMethods =BasicFunctions.MethodSetofClasses(subjects, softwareLog);
						 //the method set of observers
						 observerMethods =BasicFunctions.MethodSetofClasses(observers, softwareLog);
						 
						 regUnregSet.clear();
						 
						 //register and unregister
						 for(MethodClass regUnreg: subjectMethods)
						 {
							if(regUnreg.getMethodName().equals("init()"))
								 continue;
							//The parameter set of m @structural2: the observer class should be a parameter of reg/unreg
							for(ClassClass para: BasicOperators.ParameterSetofMethod(regUnreg, softwareLog))
							{
								if(observers.contains(para))//if a method has a parameter class that is of observer class, it may be a register/unregister class
								{
									regUnregSet.add(regUnreg);
								}
							}
						 }
						 
						if(regUnregSet.size()>=2)//@structural3: both reg and unreg should be identified
						{
							notifySet.clear();
							updateSet.clear();
							//notify->update 
							subjectMethods.removeAll(regUnregSet);//the candidate notify set is obtained by removing all reg/ung from the method set of subject
							for(MethodClass notifyM: subjectMethods) //@structural4: notify method should invoke update method, and not include a parameter of observer class
							{
								for(MethodClass updateM:BasicOperators.MethodSetofMethod(notifyM, softwareLog))
								{
									if(observerMethods.contains(updateM)){
										flag =1;
										updateSet.add(updateM);//add candidate update method
										notifySet.add(notifyM);//add candidate notify method
									}
								}
							}
						}
						
						if(flag==1)//add the subject, observer, notify, update, reg and unreg
						{
							//clear the values of each roles
							role2values.get("register").clear();
							role2values.get("unregister").clear();
							role2values.get("update").clear();
							role2values.get("notify").clear();
							role2values.get("Observer").clear();
							role2values.get("Subject").clear();

							//add subject, observer, notify, update, reg and unreg
							role2values.get("Subject").add(subjects.toArray()[0]);
							role2values.get("Observer").add(observers.toArray()[0]);
							role2values.get("update").addAll(updateSet);
							role2values.get("notify").addAll(notifySet);
							role2values.get("register").addAll(regUnregSet);
							role2values.get("unregister").addAll(regUnregSet);
							//get the combination of all kinds of values, each combination is a candidate pattern instances
							for(HashMap<String, Object> candidate: CandidateCombination.combination(role2values))
							{
								result.add(candidate);
							}
						}
		
					 }//if observer
				 }//for observers
			 }
		 }//for subjects
		 
		 return result;
		
	}


	public PatternSet ObserverPatternBehavioralConstraintsChecking(XFactory factory, XLog softwareLog, ClassTypeHierarchy cth, ArrayList<HashMap<String, Object>> result)
	{		
		// intermediate results to store complete but not validated candidates
		PatternSet discoveredCandidateInstanceSet = new PatternSetImpl(); 
		
		for(int i = 0; i<result.size(); i++)//each result(i) is a candidate pattern instance
		{			
			//identify the invocations for each observer pattern instance,
			HashSet<XTrace> invocationTraces = ObserverPatternDiscoveryAndChecking.observerPatternInvocation(softwareLog, factory, cth, result.get(i));
			
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
					continue;
				}
					
				//@invocation-constraint 1: for each observer pattern instance, an observer object should be first registered to the subject object and then unregistered. 
				//it is allowed that an observer is not registered but unregister.
				//get the observer class type 
				HashSet<ClassClass> ObserverClassTypeHierarchy = new HashSet<>();
				ObserverClassTypeHierarchy.addAll(BasicOperators.typeHierarchyClassSet(cth, (ClassClass)result.get(i).get("Observer")));
				//get the observer object set
				HashSet<String> observerObjects = ObserverPatternDiscoveryAndChecking.ObserverObjectSet(invocation,ObserverClassTypeHierarchy,
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
						break;
					}
					else// for each observer object, there exist at least one register method that take this object as input. 
					{	//for each register event in the set, there should be a unregister event in the invocation satisfying: (1) it contains a parameter value of the current observer object, and (2) executed after register event 
						int flag =1;
						for(XEvent regEvent: registerEventSet)
						{
							if(!ObserverPatternDiscoveryAndChecking.checkExistenceUnregister(regEvent, invocation, observerObj, ((MethodClass)result.get(i).get("unregister")).getMethodName()))
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
					continue;
				}
				
				
				//@invocation-constraint 2: each notify method call should invoke the update methods of currently registered observers
				//notify event set
				int validedNotifyNumber=0;
				HashSet<XEvent> notifyEventSet = BasicOperators.eventSetofMethodPerInvocation(invocation,((MethodClass)result.get(i).get("notify")).getMethodName());
				System.out.println("Number of notify events: "+notifyEventSet.size());
				if(notifyEventSet.size()==0)// no notify 
				{
					break;
				}
				else// for notify
				{
					for(XEvent notifyEvent: notifyEventSet)
					{
						//the callee object set of invoked update methods 
						HashSet<String> updateObjects =BasicOperators.calleeObjectSetofInvokedEventsPerTrace(notifyEvent,invocation);
						//the currently registered observer object set
						HashSet<String> registeredObjects=ObserverPatternDiscoveryAndChecking.currentlyRegisteredObservers(notifyEvent, observerObjects, invocation, 
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
					continue;
				}
			
				
				numberofValidatedInvocation++;
			}
			if(numberofValidatedInvocation==0)//if there is no validated invocation, then the current candidate is not approved.
			{
				continue; //go to the next candidate
			}
			
			PatternClass NewP = BasicFunctions.CreatePatternInstance("Observer Pattern", result.get(i), softwareLog.size(), numberofValidatedInvocation);
			
			discoveredCandidateInstanceSet.add(NewP);
		
		}
		return discoveredCandidateInstanceSet;
	}
}
