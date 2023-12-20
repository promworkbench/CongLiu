package software.dynamic.designpattern;

import java.util.HashMap;
import java.util.HashSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import XESSoftwareExtension.XSoftwareExtension;
import designpatterns.framework.PatternClass;
import designpatterns.observerpattern.ObserverPatternClass;
import designpatterns.statepattern.StatePatternClass;
import designpatterns.strategypattern.StrategyPatternClass;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

public class BasicFunctions {
	
	//get the method set of classes
	/*
	 * Given a set of classes, return the set of Methods according to the log. 
	 */
	public static HashSet<MethodClass> MethodSetofClasses(HashSet<ClassClass> classes, XLog log)
	{	
		HashSet<MethodClass> methodSet = new HashSet<>();
		for(ClassClass cc:classes)
		{
			for(XTrace trace: log)
			{
				for(XEvent event: trace)
				{
					if(XSoftwareExtension.instance().extractClass(event).equals(cc.getClassName())&&
							XSoftwareExtension.instance().extractPackage(event).equals(cc.getPackageName())
							&&!XConceptExtension.instance().extractName(event).equals("init()"))// we do not consider init() as role. 
					{
						MethodClass currentMethod = new MethodClass();
						currentMethod.setMethodName(XConceptExtension.instance().extractName(event));
						currentMethod.setClassName(XSoftwareExtension.instance().extractClass(event));
						currentMethod.setPackageName(XSoftwareExtension.instance().extractPackage(event));
						//currentMethod.setParameterSet(XSoftwareExtension.instance().extractParameterTypeSet(event));
						//currentMethod.setLineNumber(XSoftwareExtension.instance().extractLineNumber(event));
						methodSet.add(currentMethod);
					}				
				}
			}
		}
		
		return methodSet;
	}
	
	//pattern instance construction
		public static PatternClass CreatePatternInstance(String patternName, HashMap<String, Object>  resulti, int logNum, int InvocationNum)
		{
			PatternClass pNew;
			if(patternName.equals("Observer Pattern"))
			{
				pNew = new ObserverPatternClass();
				pNew.setPatternName(patternName);
				pNew.setTraceNumber(logNum);
				pNew.setInvocationNumber(InvocationNum);
				
				((ObserverPatternClass)pNew).setSubjectClass((ClassClass)(resulti.get("Subject")));
				((ObserverPatternClass)pNew).setListernerClass((ClassClass)(resulti.get("Observer")));
				((ObserverPatternClass)pNew).setNotifyMethod((MethodClass)(resulti.get("notify")));		
				((ObserverPatternClass)pNew).setUpdateMethod((MethodClass)(resulti.get("update")));
				((ObserverPatternClass)pNew).setRegisterMethod((MethodClass)(resulti.get("register")));
				((ObserverPatternClass)pNew).setDe_registerMethod((MethodClass)(resulti.get("unregister")));
				
				return pNew;
			}
			else if(patternName.equals("State Pattern"))
			{
				pNew = new StatePatternClass();
				pNew.setPatternName("State Pattern");
				pNew.setTraceNumber(logNum);
				pNew.setInvocationNumber(InvocationNum);
				
				((StatePatternClass)pNew).setContext((ClassClass)(resulti.get("Context")));
				((StatePatternClass)pNew).setState((ClassClass)(resulti.get("State")));
				((StatePatternClass)pNew).setRequest((MethodClass)(resulti.get("request")));		
				((StatePatternClass)pNew).setHandle((MethodClass)(resulti.get("handle")));
				((StatePatternClass)pNew).setSetState((MethodClass)(resulti.get("setState")));

				return pNew;
			}
			else if(patternName.equals("Strategy Pattern"))
			{
				pNew = new StrategyPatternClass();
				pNew.setPatternName("Strategy Pattern");
				pNew.setTraceNumber(logNum);
				pNew.setInvocationNumber(InvocationNum);
				
				((StrategyPatternClass)pNew).setContext((ClassClass)(resulti.get("Context")));
				((StrategyPatternClass)pNew).setStrategy((ClassClass)(resulti.get("Strategy")));
				((StrategyPatternClass)pNew).setContextInterface((MethodClass)(resulti.get("contextInterface")));
				((StrategyPatternClass)pNew).setAlgorithmInterface((MethodClass)(resulti.get("algorithmInterface")));
				((StrategyPatternClass)pNew).setSetStrategy((MethodClass)(resulti.get("setStrategy")));

				return pNew;
			}
//			else if((patternName.equals("(Object)Adapter Pattern")))
//			{
//				pNew = new AdapterPatternClass();
//				pNew.setPatternName("(Object)Adapter Pattern");
//				pNew.setTraceNumber(logNum);
//				pNew.setInvocationNumber(InvocationNum);
//				
//				//using the static result
//				((AdapterPatternClass)pNew).setAdapterClass(((AdapterPatternClass)op).getAdapterClass());
//				((AdapterPatternClass)pNew).setAdapteeClass(((AdapterPatternClass)op).getAdapteeClass());
//				((AdapterPatternClass)pNew).setRequestMethod(((AdapterPatternClass)op).getRequestMethod());
//				
//				//using the dynamic results
//				((AdapterPatternClass)pNew).setSpecificRequestMethod((MethodClass)resulti.get("specificRequest"));
//
//				return pNew;
//			}
//			else if((patternName.equals("Factory Method Pattern")))
//			{
//				pNew = new FactoryMethodPatternClass();
//				pNew.setPatternName("Factory Method Pattern");
//				pNew.setTraceNumber(logNum);
//				pNew.setInvocationNumber(InvocationNum);
//				
//				//using the static result
//				((FactoryMethodPatternClass)pNew).setCreator(((FactoryMethodPatternClass)op).getCreator());
//				((FactoryMethodPatternClass)pNew).setFactoryMethod(((FactoryMethodPatternClass)op).getFactoryMethod());
//
//				return pNew;
//			}
//			else if((patternName.equals("Command Pattern")))
//			{
//				pNew = new CommandPatternClass();
//				pNew.setPatternName("Command Pattern");
//				pNew.setTraceNumber(logNum);
//				pNew.setInvocationNumber(InvocationNum);
//				
//				//using the static result
//				((CommandPatternClass)pNew).setCommand(((CommandPatternClass)op).getCommand());
//				((CommandPatternClass)pNew).setReceiver(((CommandPatternClass)op).getReceiver());
//				((CommandPatternClass)pNew).setExecute(((CommandPatternClass)op).getExecute());
//				
//				//using the dynamic results
//				((CommandPatternClass)pNew).setInvoker((ClassClass)resulti.get("Invoker"));
//				((CommandPatternClass)pNew).setCall((MethodClass)resulti.get("call"));
//				((CommandPatternClass)pNew).setAction((MethodClass)resulti.get("action"));
//				
//				return pNew;
//			}
//			else if((patternName.equals("Visitor Pattern")))
//			{
//				pNew = new VisitorPatternClass();
//				pNew.setPatternName("Visitor Pattern");
//				pNew.setTraceNumber(logNum);
//				pNew.setInvocationNumber(InvocationNum);
//				
//				//using the static result
//				((VisitorPatternClass)pNew).setElement(((VisitorPatternClass)op).getElement());
//				((VisitorPatternClass)pNew).setVisitor(((VisitorPatternClass)op).getVisitor());
//				((VisitorPatternClass)pNew).setAccept(((VisitorPatternClass)op).getAccept());
//				
//				//using the dynamic results
//				((VisitorPatternClass)pNew).setVisit((MethodClass)resulti.get("visit"));
//				
//				return pNew;
//			}
			
			return null;
			//else patterns
		}
}
