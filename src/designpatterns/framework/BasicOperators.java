package designpatterns.framework;
/*
 * this class defines the basic operators that can be obtained from the log
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;

import XESSoftwareExtension.XSoftwareExtension;
import designpatterns.adapterpattern.AdapterPatternClass;
import designpatterns.adapterpattern.ClassTypeHierarchy;
import designpatterns.commandpattern.CommandPatternClass;
import designpatterns.factorymethodpattern.FactoryMethodPatternClass;
import designpatterns.observerpattern.ObserverPatternClass;
import designpatterns.statepattern.StatePatternClass;
import designpatterns.strategypattern.StrategyPatternClass;
import designpatterns.visitorpattern.VisitorPatternClass;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

public class BasicOperators {

	
	/*
	 * Given an event and an invocation, return the callee object of the invoked events
	 */
	public static HashSet<String> calleeObjectSetofInvokedEventsPerTrace(XEvent callerEvent, XTrace invocation)
	{
		//get the invoked event set
		HashSet<XEvent> invokedSet = new HashSet<>();
		for(XEvent event: invocation)
		{
			if(XSoftwareExtension.instance().extractCallermethod(event).equals(XConceptExtension.instance().extractName(callerEvent))
					&&XSoftwareExtension.instance().extractCallerclassobject(event).equals(XSoftwareExtension.instance().extractClassObject(callerEvent))
					&&Long.parseLong(XSoftwareExtension.instance().extractStarttimenano(callerEvent)) < Long.parseLong(XSoftwareExtension.instance().extractStarttimenano(event))
					&&Long.parseLong(XSoftwareExtension.instance().extractEndtimenano(callerEvent)) > Long.parseLong(XSoftwareExtension.instance().extractEndtimenano(event)))
			{
				invokedSet.add(event);
			}
		}
		//get the callee object of the invoked event set
		HashSet<String> objectSet = new HashSet<>();
		for(XEvent e: invokedSet)
		{
			objectSet.add(XSoftwareExtension.instance().extractClassObject(e));
		}
		
		return objectSet;
	}
	
	/*
	 * Event set of a certain method
	 */
	
	public static HashSet<XEvent> eventSetofMethodPerInvocation(XTrace invocation, String methodName)
	{
		HashSet<XEvent> eventS = new HashSet<>();
		for(XEvent e: invocation)
		{
			if(XConceptExtension.instance().extractName(e).equals(methodName))
			{
				eventS.add(e);
			}
		}
		
		return eventS;
	}
	/*
	 * Given a method call, return its parameter object
	 */
	
	public static HashSet<String> getParaObjSet(XEvent event)
	{
		HashSet<String> paraObjSet = new HashSet<>();		
		String tempParaValue = XSoftwareExtension.instance().extractParameterValueSet(event);
		
		if(tempParaValue!=null)// the tempParaValue may be null, if the current event do not have this attribute. 
		{
			if(tempParaValue.contains(","))// more than one parameters for the current method
			{
				for(String para: tempParaValue.split("\\,"))
				{
					paraObjSet.add(para);
				}
			}
			else if(!tempParaValue.isEmpty())// only one parameter for the current method 
			{
				paraObjSet.add(tempParaValue);
			}
		}
		
		
		return paraObjSet;
	}
	
	/*
	 * Given a trace/invocation, a parameter object, method name, return a set of events 
	 */
	public static HashSet<XEvent> getMethodCallSetwithParaObj(XTrace invocation, String paraObj, String methodName)
	{
		HashSet<XEvent> eventSet = new HashSet<>();	
		for(XEvent event: invocation)
		{
			if(getParaObjSet(event).contains(paraObj) && XConceptExtension.instance().extractName(event).equals(methodName))
			{
				eventSet.add(event);
			}
		}
		
		return eventSet;
	}
	
	/*
	 * Given a class, return all its type hierarchy classes, if the type hierarchy does not include such class, return this class. 
	 */
	
	public static HashSet<ClassClass> getAllTypeHierarchyClasses(ClassClass c, ClassTypeHierarchy cth)
	{
		for(HashSet<ClassClass> cc: cth.getAllCTH())
		{
			if(cc.contains(c))
			{
				return cc;
			}
		}
		
		HashSet<ClassClass> ccs = new HashSet<>();
		ccs.add(c);
		return ccs;
	}
	
	/*
	 * check if a class is included in the log
	 */
	
	public static boolean classIncludedInLog(ClassClass c, XLog log)
	{
		if(c==null)
		{
			return false;
		}
		for(XTrace trace: log)
		{
			for(XEvent event: trace)
			{
				if(c.toString().equals(XSoftwareExtension.instance().extractPackage(event)+
						"."+XSoftwareExtension.instance().extractClass(event)))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*
	 * check is a method is included in the log
	 */
	
	public static boolean methodIncludedInLog(MethodClass m, XLog log)
	{
		if(m==null)
		{
			return false;
		}
		for(XTrace trace: log)
		{
			for(XEvent event: trace)
			{
				if(m.toString().equals(XSoftwareExtension.instance().extractPackage(event)+
						"."+XSoftwareExtension.instance().extractClass(event)+"."+XConceptExtension.instance().extractName(event)))
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	/*
	 * check is a method is included in the trace
	 */
	
	public static boolean methodIncludedInTrace(MethodClass m, XTrace trace)
	{
		if(m==null)
		{
			return false;
		}
		
		for(XEvent event: trace)
		{
			if(m.toString().equals(XSoftwareExtension.instance().extractPackage(event)+
					"."+XSoftwareExtension.instance().extractClass(event)+"."+XConceptExtension.instance().extractName(event)))
			{
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * get the typehierarchyClass
	 * @param cth
	 * @param log
	 * @param c: the class that is not included in the log
	 * @return
	 */
	public static ClassClass typeHierarchyClass(ClassTypeHierarchy cth, XLog log, ClassClass c)
	{
		ArrayList<HashSet<ClassClass>> classSetList = cth.getAllCTH();
		
		for(HashSet<ClassClass> cc: classSetList)
		{
			if(cc.contains(c))
			{
				for(ClassClass candidateC: cc)
				{
					if(classIncludedInLog(candidateC,log))
					{
						return candidateC;
					}
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * get the typehierarchyClass
	 * @param cth
	 * @param log
	 * @param c: the class that is not included in the log
	 * @return a set of classes
	 */
	public static HashSet<ClassClass> typeHierarchyClassSetInLog(ClassTypeHierarchy cth, XLog log, ClassClass c)
	{
		ArrayList<HashSet<ClassClass>> classTypeHierarchyList = cth.getAllCTH();
		HashSet<ClassClass> classSet = new HashSet<>();
		
		for(HashSet<ClassClass> cc: classTypeHierarchyList)
		{
			if(cc.contains(c))
			{
				for(ClassClass candidateC: cc)
				{
					if(classIncludedInLog(candidateC,log))
					{
						classSet.add(candidateC);
					}
				}
			}
		}
		
		return classSet;
	}
	
	/**
	 * get the typehierarchyClass
	 * @param cth
	 * @param c: the class that is not included in the log
	 * @return a set of classes
	 */
	public static HashSet<ClassClass> typeHierarchyClassSet(ClassTypeHierarchy cth, ClassClass c)
	{
		ArrayList<HashSet<ClassClass>> classTypeHierarchyList = cth.getAllCTH();
		HashSet<ClassClass> classSet = new HashSet<>();
		classSet.add(c);
		
		for(HashSet<ClassClass> cc: classTypeHierarchyList)
		{
			if(cc.contains(c))
			{
				classSet.addAll(cc);
			}
		}
		
		return classSet;
	}
	
	/**
	 * 
	 * @param cth
	 * @param log
	 * @param m: the method that is not included in the log
	 * @return
	 */
	public static MethodClass typeHierarchyMethod(ClassTypeHierarchy cth, XLog log, MethodClass m)
	{
		ArrayList<HashSet<ClassClass>> classSetList = cth.getAllCTH();
		for(HashSet<ClassClass> cc: classSetList)
		{
			if(cc.contains(MethodToClass(m)))
			{
				for(ClassClass candidateC: cc)
				{
					MethodClass candidateM = ClassToMethod(candidateC);
					candidateM.setMethodName(m.getMethodName());
					if(methodIncludedInLog(candidateM,log))
					{
						return candidateM;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param cth
	 * @param log
	 * @param m: the method that is not included in the log
	 * @return a set of methods
	 */
	public static HashSet<MethodClass> typeHierarchyMethodSetInLog(ClassTypeHierarchy cth, XLog log, MethodClass m)
	{
		ArrayList<HashSet<ClassClass>> classTypeHierarchyList = cth.getAllCTH();
		HashSet<MethodClass> candidateMethodSet = new HashSet<>();
		
		for(HashSet<ClassClass> cc: classTypeHierarchyList)
		{
			if(cc.contains(MethodToClass(m)))
			{
				for(ClassClass candidateC: cc)
				{
					MethodClass candidateM = ClassToMethod(candidateC);
					candidateM.setMethodName(m.getMethodName());
					if(methodIncludedInLog(candidateM,log))
					{
						candidateMethodSet.add(candidateM);
					}
				}
			}
		}
		
		return candidateMethodSet;
	}
	
	/**
	 * 
	 * @param cth
	 * @param log
	 * @param m: the method that is not included in the trace
	 * @return
	 */
	public static MethodClass typeHierarchyMethodPerTrace(ClassTypeHierarchy cth, XTrace trace, MethodClass m)
	{
		ArrayList<HashSet<ClassClass>> classSetList = cth.getAllCTH();
		for(HashSet<ClassClass> cc: classSetList)
		{
			if(cc.contains(MethodToClass(m)))
			{
				for(ClassClass candidateC: cc)
				{
					MethodClass candidateM = ClassToMethod(candidateC);
					candidateM.setMethodName(m.getMethodName());
					if(methodIncludedInTrace(candidateM,trace))
					{
						return candidateM;
					}
				}
			}
		}
		
		return new MethodClass();
	}
	
	public static ClassClass MethodToClass(MethodClass m)
	{
		ClassClass c = new ClassClass();
		c.setClassName(m.getClassName());
		c.setPackageName(m.getPackageName());
		
		return c;
	}
	
	public static MethodClass ClassToMethod(ClassClass c)
	{
		MethodClass m = new MethodClass();
		m.setClassName(c.getClassName());
		m.setPackageName(c.getPackageName());
		
		return m;
	}
	
	/*
	 * Given a role value, check if it is included in the log
	 * 
	 */
	public static boolean roleValueIncludedinLog(Object obj, XLog log)
	{
		if(obj instanceof MethodClass)//  consider method level roles 
		{
			for(XTrace trace: log)
			{
				for(XEvent event: trace)
				{
					if(obj.toString().equals(XSoftwareExtension.instance().extractPackage(event)+
							"."+XSoftwareExtension.instance().extractClass(event)+"."+XConceptExtension.instance().extractName(event)))
					{
						return true;
					}
				}
			}
		}
		else if(obj instanceof ClassClass)//for class level roles 
		{
			for(XTrace trace: log)
			{
				for(XEvent event: trace)
				{
					if(obj.toString().equals(XSoftwareExtension.instance().extractPackage(event)+
							"."+XSoftwareExtension.instance().extractClass(event)))
					{
						return true;
					}
				}
			}

		}
					
		return false;
	}
	
	
	/*
	 * Given a class, return the set of Methods according to the log. 
	 */
	public static HashSet<MethodClass> MethodSetofClass(ClassClass cc, XLog log)
	{	
		HashSet<MethodClass> methodSet = new HashSet<>();
		for(XTrace trace: log)
		{
			for(XEvent event: trace)
			{
				if(XSoftwareExtension.instance().extractClass(event).equals(cc.getClassName())&&
						XSoftwareExtension.instance().extractPackage(event).equals(cc.getPackageName()))
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
		return methodSet;
	}
	
	/*
	 * Given a class, return the set of methods according to the trace
	 */
	
	public static HashSet<MethodClass> MethodSetofClassPerTrace(ClassClass cc, XTrace trace)
	{	
		HashSet<MethodClass> methodSet = new HashSet<>();
		
		for(XEvent event: trace)
		{
			if(XSoftwareExtension.instance().extractClass(event).equals(cc.getClassName())&&
					XSoftwareExtension.instance().extractPackage(event).equals(cc.getPackageName()))
			{
				MethodClass currentMethod = new MethodClass();
				currentMethod.setMethodName(XConceptExtension.instance().extractName(event));
				currentMethod.setClassName(XSoftwareExtension.instance().extractClass(event));
				currentMethod.setPackageName(XSoftwareExtension.instance().extractPackage(event));
				//currentMethod.setLineNumber(XSoftwareExtension.instance().extractLineNumber(event));
				methodSet.add(currentMethod);
			}				
		}
		
		return methodSet;
	}
	/*
	 * Given a method, return the parameter set according to the log. Essentially it is a set of classes
	 */
	public static HashSet<ClassClass> ParameterSetofMethod(MethodClass m, XLog log)
	{
		HashSet<ClassClass> parameterSet = new HashSet<>();
		
		for(XTrace trace: log)
		{
			for(XEvent event: trace)
			{
				if(XSoftwareExtension.instance().extractClass(event).equals(m.getClassName())&&
						XConceptExtension.instance().extractName(event).equals(m.getMethodName()))
				{
					Set<String> currentParameterSet = new HashSet<String>();
					
					String tempPara = XSoftwareExtension.instance().extractParameterTypeSet(event);
					//System.out.println(tempPara);
					if(tempPara.contains(","))
					{
						for(String para: tempPara.split("\\,"))
						{
							currentParameterSet.add(para);
						}
					}
					else
					{
						if (tempPara.contains("."))
						{
							currentParameterSet.add(tempPara);
						}
						
					}
					
					for(String para:currentParameterSet)
					{
						ClassClass tempClass = new ClassClass();
						tempClass.setClassName(extractClass(para));
						tempClass.setPackageName(extractPackage(para));
						parameterSet.add(tempClass);
					}
					return parameterSet;// return the once get 
				}
				
			}
		}
		
		
		return parameterSet;
	}
	
	/*
	 * Given a method, return the parameter set according to the trace. Essentially it is a set of classes
	 */
	public static HashSet<ClassClass> ParameterSetofMethodPerTrace(MethodClass m, XTrace trace)
	{
		HashSet<ClassClass> parameterSet = new HashSet<>();

		for(XEvent event: trace)
		{
			if(XSoftwareExtension.instance().extractClass(event).equals(m.getClassName())&&
					XConceptExtension.instance().extractName(event).equals(m.getMethodName()))
			{
				Set<String> currentParameterSet = new HashSet<String>();
				
				String tempPara = XSoftwareExtension.instance().extractParameterTypeSet(event);
				//System.out.println(tempPara);
				if(tempPara.contains(","))
				{
					for(String para: tempPara.split("\\,"))
					{
						currentParameterSet.add(para);
					}
				}
				else
				{
					if (tempPara.contains("."))
					{
						currentParameterSet.add(tempPara);
					}
					
				}
				
				for(String para:currentParameterSet)
				{
					ClassClass tempClass = new ClassClass();
					tempClass.setClassName(extractClass(para));
					tempClass.setPackageName(extractPackage(para));
					parameterSet.add(tempClass);
				}
				return parameterSet;// return the once get 
			}
			
		}
		
		return parameterSet;
	}
	
	
	/*
	 * Given a method return its invoked method set according to the log. 
	 */
	
	public static HashSet<MethodClass> MethodSetofMethod(MethodClass m, XLog log)
	{
		HashSet<MethodClass> methodSet = new HashSet<>();
		
		for(XTrace trace: log)
		{
			for(XEvent event: trace)
			{
				if(XSoftwareExtension.instance().extractCallerpackage(event).equals(m.getPackageName())&&
						XSoftwareExtension.instance().extractCallerclass(event).equals(m.getClassName())&&
						XSoftwareExtension.instance().extractCallermethod(event).equals(m.getMethodName()))
				{
					MethodClass tempMethod = new MethodClass();
					tempMethod.setPackageName(XSoftwareExtension.instance().extractPackage(event));
					tempMethod.setClassName(XSoftwareExtension.instance().extractClass(event));
					tempMethod.setMethodName(XConceptExtension.instance().extractName(event));
					
					methodSet.add(tempMethod);
				}
			}
		}
		
		return methodSet;
	}
	
	/*
	 * Given a method and a log, return its caller method set.
	 */
	
	public static HashSet<MethodClass> CallerMethodSetofMethod(MethodClass m, XLog log)
	{
		HashSet<MethodClass> methodSet = new HashSet<>();
		
		for(XTrace trace: log)
		{
			for(XEvent event: trace)
			{
				if(XSoftwareExtension.instance().extractPackage(event).equals(m.getPackageName())
						&&XSoftwareExtension.instance().extractClass(event).equals(m.getClassName())
						&&XConceptExtension.instance().extractName(event).equals(m.getMethodName()))
				{
					MethodClass tempMethod = new MethodClass();
					tempMethod.setPackageName(XSoftwareExtension.instance().extractCallerpackage(event));
					tempMethod.setClassName(XSoftwareExtension.instance().extractCallerclass(event));
					tempMethod.setMethodName(XSoftwareExtension.instance().extractCallermethod(event));
					
					methodSet.add(tempMethod);
				}
				
			}
		}

		return methodSet;
	}
	
	/*
	 * Given a method return its invoked method set according to the trace. 
	 */
	
	public static HashSet<MethodClass> MethodSetofMethodPerTrace(MethodClass m, XTrace trace)
	{
		HashSet<MethodClass> methodSet = new HashSet<>();
		
		for(XEvent event: trace)
		{
			if(XSoftwareExtension.instance().extractCallerpackage(event).equals(m.getPackageName())&&
					XSoftwareExtension.instance().extractCallerclass(event).equals(m.getClassName())&&
					XSoftwareExtension.instance().extractCallermethod(event).equals(m.getMethodName()))
			{
				
				MethodClass tempMethod = new MethodClass();
				tempMethod.setPackageName(XSoftwareExtension.instance().extractPackage(event));
				tempMethod.setClassName(XSoftwareExtension.instance().extractClass(event));
				tempMethod.setMethodName(XConceptExtension.instance().extractName(event));
				
				methodSet.add(tempMethod);
			}
		}
		
		
		return methodSet;
	}
	
	/*
	 * given a trace, checks if a method is included in the trace
	 */
	
	public static boolean MethodIncludedTrace(MethodClass m, XTrace trace)
	{
		for(XEvent event: trace)
		{
			if(XSoftwareExtension.instance().extractPackage(event).equals(m.getPackageName())
					&&XSoftwareExtension.instance().extractClass(event).equals(m.getClassName())
					&&XConceptExtension.instance().extractName(event).equals(m.getMethodName()))
			{
				return true;
			}
		}
		return false;
	}
	
	/*
	 * given a trace and method, get the earliest time of this method.
	 * 
	 */
	public static long MethodEarliestTime(MethodClass m, XTrace trace)
	{
		long earlist=Long.MAX_VALUE;
		long temp=0;
		for(XEvent event: trace)
		{
			if(XSoftwareExtension.instance().extractPackage(event).equals(m.getPackageName())
					&&XSoftwareExtension.instance().extractClass(event).equals(m.getClassName())
					&&XConceptExtension.instance().extractName(event).equals(m.getMethodName()))
			{
				temp=Long.parseLong(XSoftwareExtension.instance().extractStarttimenano(event));
				if(earlist>temp)
				{
					earlist=temp;
				}
			}
			
		}
		return earlist;		
	}
	
	/*
	 * given a method and a trace, return the number of times of this method
	 */
	public static int MethodNumberIncludedTrace(MethodClass m, XTrace trace)
	{
		int num =0;
		for(XEvent event: trace){
			if (XSoftwareExtension.instance().extractPackage(event).equals(m.getPackageName())
					&&XSoftwareExtension.instance().extractClass(event).equals(m.getClassName())
					&&XConceptExtension.instance().extractName(event).equals(m.getMethodName()))
			{
				num++;
			}
		}
		System.out.println("The number of "+m+" is "+ num);
		return num;
	}
	/**
	 *Given a trace and method, get the class object set of the method 
	 */
	public static HashSet<String> ObjectSetMethod(MethodClass m, XTrace trace)
	{
		HashSet<String> objectSet = new HashSet<>();
		for(XEvent event: trace)
		{
			if(XSoftwareExtension.instance().extractPackage(event).equals(m.getPackageName())
					&&XSoftwareExtension.instance().extractClass(event).equals(m.getClassName())
					&&XConceptExtension.instance().extractName(event).equals(m.getMethodName()))
			{
				objectSet.add(XSoftwareExtension.instance().extractClassObject(event));
			}
			
		}
		return objectSet;
	}
	
	/**
	 *Given a trace and class, get the object set of the class 
	 */
	public static HashSet<String> ObjectSetClassPerTrace(ClassClass c, XTrace trace)
	{
		HashSet<String> objectSet = new HashSet<>();
		for(XEvent event: trace)
		{
			if(XSoftwareExtension.instance().extractPackage(event).equals(c.getPackageName())
					&&XSoftwareExtension.instance().extractClass(event).equals(c.getClassName()))
			{
				objectSet.add(XSoftwareExtension.instance().extractClassObject(event));
			}
			
		}
		objectSet.remove("0");// remove the object id if it is 0/ 
		return objectSet;
	}
	
	/*
	 * Given (1) a method and its object (2) its callee method, get the object set of callee method  
	 */
	public static HashSet<String> ObjectSetCalleeMethod(MethodClass caller, String obj, MethodClass callee,  XTrace trace)
	{
		HashSet<String> objectSet = new HashSet<>();
		for(XEvent event: trace)
		{
			if(XSoftwareExtension.instance().extractCallerpackage(event).equals(caller.getPackageName())
					&&XSoftwareExtension.instance().extractCallerclass(event).equals(caller.getClassName())
					&&XSoftwareExtension.instance().extractCallermethod(event).equals(caller.getMethodName())
					&&XSoftwareExtension.instance().extractCallerclassobject(event).equals(obj)
					&&XSoftwareExtension.instance().extractPackage(event).equals(callee.getPackageName())
					&&XSoftwareExtension.instance().extractClass(event).equals(callee.getClassName())
					&&XConceptExtension.instance().extractName(event).equals(callee.getMethodName()))
					{
						objectSet.add(XSoftwareExtension.instance().extractClassObject(event));
					}
		}
		
		return objectSet;
	}
	
	/*
	 * Given (1) a callee method name (2) a trace, get the callee class object set
	 */
	
	public static HashSet<String> CalleeObjectSetPerTrace(String calleeMethodName, XTrace trace)
	{
		HashSet<String> objectSet = new HashSet<>();
		for(XEvent event: trace)
		{
			if(XConceptExtension.instance().extractName(event).equals(calleeMethodName))
			{
				objectSet.add(XSoftwareExtension.instance().extractClassObject(event));
			}
		}
		
		return objectSet;
	}
	
	/*
	 * Given (1) a caller method name (2) its object (2) its callee method, get the object set of callee  
	 */

	public static HashSet<String> CalleeObjectSetPerTrace(String calleeMethodName, String callerMethodName, String CallerObject, XTrace trace)
	{
		HashSet<String> objectSet = new HashSet<>();
		for(XEvent event: trace)
		{
			if(XConceptExtension.instance().extractName(event).equals(calleeMethodName)
					&&XSoftwareExtension.instance().extractCallermethod(event).equals(callerMethodName)
					&&XSoftwareExtension.instance().extractCallerclassobject(event).equals(CallerObject))
			{
				objectSet.add(XSoftwareExtension.instance().extractClassObject(event));
			}
		}
		
		return objectSet;
	}
	
	/*
	 * Given (1) a callee method name, (2) its object, get the caller object set
	 */
	public static HashSet<String> CallerObjectSetPerTrace(String calleeMethodname, String calleeObject, XTrace trace)
	{
		HashSet<String> objectSet = new HashSet<>();
		for(XEvent event: trace)
		{
			if(XConceptExtension.instance().extractName(event).equals(calleeMethodname)
					&&XSoftwareExtension.instance().extractClassObject(event).equals(calleeObject))
			{
				objectSet.add(XSoftwareExtension.instance().extractCallerclassobject(event));
			}
		}
		return objectSet;
	}
	
	/*
	 * Given a callee method name and a trace, return the number of times this callee method occures in the trace.
	 */
	
	public static int MethodcallNumberPerTrace(String calleeMethodName, XTrace trace)
	{
		int count =0;
		for(XEvent event: trace)
		{
			if(XConceptExtension.instance().extractName(event).equals(calleeMethodName))
			{
				count++;
			}
		}
		return count;
	}
	
	/*
	 * construct role 2 values mapping, for different design patterns 
	 */
	
	public static HashMap<String, ArrayList<Object>> Role2Values (PatternClass op)
	{
		//construct the mapping for observer pattern
		if(op.getPatternName().equals("Observer Pattern"))
		{
			return ConstructRole2Values.observerPattern(op);
		}
		else if(op.getPatternName().equals("State Pattern"))
		{
			return ConstructRole2Values.statePattern(op);
			
		}
		else if(op.getPatternName().equals("Strategy Pattern"))
		{
			return ConstructRole2Values.strategyPattern(op);
		}
		else if(op.getPatternName().equals("(Object)Adapter Pattern"))
		{
			return ConstructRole2Values.AdapterPattern(op);
		}
		else if(op.getPatternName().equals("Factory Method Pattern"))
		{
			return ConstructRole2Values.FactoryMethodPattern(op);
		}
		else if(op.getPatternName().equals("Command Pattern"))
		{
			return ConstructRole2Values.commandPattern(op);
		}
		else if(op.getPatternName().equals("Visitor Pattern"))
		{
			return ConstructRole2Values.visitorPattern(op);
		}
		
		return null;
	}
	
	/*
	 * get the main role class for different design patterns. 
	 */
	
	public static ClassClass mainRole(PatternClass op)
	{
		//construct the mapping for observer pattern
		if(op.getPatternName().equals("Observer Pattern"))
		{
			//add the main role class, i.e. the subject
			return ((ObserverPatternClass)op).getSubjectClass();
		}
		else if(op.getPatternName().equals("State Pattern"))
		{			
			//add the main role class, i.e., the context
			return ((StatePatternClass)op).getContext();
		}
		else if(op.getPatternName().equals("(Object)Adapter Pattern"))
		{			
			//add the main role class, i.e., the adapter
			return ((AdapterPatternClass)op).getAdapterClass();
		}
		else if(op.getPatternName().equals("Factory Method Pattern"))
		{			
			//add the main role class, i.e., the adapter
			return ((FactoryMethodPatternClass)op).getCreator();
		}
		return null;
	}
	
	/*
	 * get dynamic main role class set for different design patterns. 
	 */
	
	public static ArrayList<Object> DynamicMainRole(PatternClass op, HashMap<String, ArrayList<Object>> role2values)
	{
		if(op.getPatternName().equals("Observer Pattern"))
		{
			//get the main role class, i.e. the subject
			return role2values.get("Subject");
		}
		else if(op.getPatternName().equals("State Pattern"))
		{			
			//get the main role class, i.e., the context
			return role2values.get("Context");
		}
		else if(op.getPatternName().equals("(Object)Adapter Pattern"))
		{			
			//get the main role class, i.e., the adapter
			return role2values.get("Adapter");
		}
		else if(op.getPatternName().equals("Factory Method Pattern"))
		{			
			//get the main role class, i.e., the adapter
			return role2values.get("Creator");
		}
		return null;
	}
	
	//pattern instance construction
	public static PatternClass CreatePatternInstance(PatternClass op, HashMap<String, Object>  resulti, int logNum, int InvocationNum)
	{
		PatternClass pNew;
		if(op.getPatternName().equals("Observer Pattern"))
		{
			pNew = new ObserverPatternClass();
			pNew.setPatternName("Observer Pattern");
			pNew.setTraceNumber(logNum);
			pNew.setInvocationNumber(InvocationNum);
			
		
			//using the dynamic information
			((ObserverPatternClass)pNew).setSubjectClass(((ObserverPatternClass)op).getSubjectClass());
			((ObserverPatternClass)pNew).setListernerClass(((ObserverPatternClass)op).getListernerClass());
			((ObserverPatternClass)pNew).setNotifyMethod(((ObserverPatternClass)op).getNotifyMethod());
			
			//using the dynamic results
			((ObserverPatternClass)pNew).setUpdateMethod((MethodClass)(resulti.get("update")));
			((ObserverPatternClass)pNew).setRegisterMethod((MethodClass)(resulti.get("register")));
			((ObserverPatternClass)pNew).setDe_registerMethod((MethodClass)(resulti.get("unregister")));
			
			return pNew;
		}
		else if(op.getPatternName().equals("State Pattern"))
		{
			pNew = new StatePatternClass();
			pNew.setPatternName("State Pattern");
			pNew.setTraceNumber(logNum);
			pNew.setInvocationNumber(InvocationNum);
			
			//using the static result
			((StatePatternClass)pNew).setContext(((StatePatternClass)op).getContext());
			((StatePatternClass)pNew).setState(((StatePatternClass)op).getState());
			((StatePatternClass)pNew).setRequest(((StatePatternClass)op).getRequest());
			
			//using the dynamic results
			((StatePatternClass)pNew).setHandle((MethodClass)(resulti.get("handle")));
			((StatePatternClass)pNew).setSetState((MethodClass)(resulti.get("setState")));

			return pNew;
		}
		else if(op.getPatternName().equals("Strategy Pattern"))
		{
			pNew = new StrategyPatternClass();
			pNew.setPatternName("Strategy Pattern");
			pNew.setTraceNumber(logNum);
			pNew.setInvocationNumber(InvocationNum);
			
			//using the static result
			((StrategyPatternClass)pNew).setContext(((StrategyPatternClass)op).getContext());
			((StrategyPatternClass)pNew).setStrategy(((StrategyPatternClass)op).getStrategy());
			((StrategyPatternClass)pNew).setContextInterface(((StrategyPatternClass)op).getContextInterface());
			
			//using the dynamic results
			((StrategyPatternClass)pNew).setAlgorithmInterface((MethodClass)(resulti.get("algorithmInterface")));
			((StrategyPatternClass)pNew).setSetStrategy((MethodClass)(resulti.get("setStrategy")));

			return pNew;
		}
		else if((op.getPatternName().equals("(Object)Adapter Pattern")))
		{
			pNew = new AdapterPatternClass();
			pNew.setPatternName("(Object)Adapter Pattern");
			pNew.setTraceNumber(logNum);
			pNew.setInvocationNumber(InvocationNum);
			
			//using the static result
			((AdapterPatternClass)pNew).setAdapterClass(((AdapterPatternClass)op).getAdapterClass());
			((AdapterPatternClass)pNew).setAdapteeClass(((AdapterPatternClass)op).getAdapteeClass());
			((AdapterPatternClass)pNew).setRequestMethod(((AdapterPatternClass)op).getRequestMethod());
			
			//using the dynamic results
			((AdapterPatternClass)pNew).setSpecificRequestMethod((MethodClass)resulti.get("specificRequest"));

			return pNew;
		}
		else if((op.getPatternName().equals("Factory Method Pattern")))
		{
			pNew = new FactoryMethodPatternClass();
			pNew.setPatternName("Factory Method Pattern");
			pNew.setTraceNumber(logNum);
			pNew.setInvocationNumber(InvocationNum);
			
			//using the static result
			((FactoryMethodPatternClass)pNew).setCreator(((FactoryMethodPatternClass)op).getCreator());
			((FactoryMethodPatternClass)pNew).setFactoryMethod(((FactoryMethodPatternClass)op).getFactoryMethod());

			return pNew;
		}
		else if((op.getPatternName().equals("Command Pattern")))
		{
			pNew = new CommandPatternClass();
			pNew.setPatternName("Command Pattern");
			pNew.setTraceNumber(logNum);
			pNew.setInvocationNumber(InvocationNum);
			
			//using the static result
			((CommandPatternClass)pNew).setCommand(((CommandPatternClass)op).getCommand());
			((CommandPatternClass)pNew).setReceiver(((CommandPatternClass)op).getReceiver());
			((CommandPatternClass)pNew).setExecute(((CommandPatternClass)op).getExecute());
			
			//using the dynamic results
			((CommandPatternClass)pNew).setInvoker((ClassClass)resulti.get("Invoker"));
			((CommandPatternClass)pNew).setCall((MethodClass)resulti.get("call"));
			((CommandPatternClass)pNew).setAction((MethodClass)resulti.get("action"));
			
			return pNew;
		}
		else if((op.getPatternName().equals("Visitor Pattern")))
		{
			pNew = new VisitorPatternClass();
			pNew.setPatternName("Visitor Pattern");
			pNew.setTraceNumber(logNum);
			pNew.setInvocationNumber(InvocationNum);
			
			//using the static result
			((VisitorPatternClass)pNew).setElement(((VisitorPatternClass)op).getElement());
			((VisitorPatternClass)pNew).setVisitor(((VisitorPatternClass)op).getVisitor());
			((VisitorPatternClass)pNew).setAccept(((VisitorPatternClass)op).getAccept());
			
			//using the dynamic results
			((VisitorPatternClass)pNew).setVisit((MethodClass)resulti.get("visit"));
			
			return pNew;
		}
		
		return null;
		//else patterns
	}
	//get the class part, sample input "CH.ifa.draw.standard.StandardDrawingView"
	public static String extractClass(String s)
	{
		String args[]=s.split("\\.");	
		
		return args[args.length-1];
	}
	
	//get the package part sample input "CH.ifa.draw.standard.StandardDrawingView"
	public static String extractPackage(String s)
	{
		String args[]=s.split("\\.");	
		
		String Package = args[0];
		for (int i=1;i<args.length-1;i++)
		{
			Package=Package+"."+args[i];
		}
		return Package;
	}
	
}
