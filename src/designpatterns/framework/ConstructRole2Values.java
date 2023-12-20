package designpatterns.framework;

import java.util.ArrayList;
import java.util.HashMap;

import designpatterns.adapterpattern.AdapterPatternClass;
import designpatterns.commandpattern.CommandPatternClass;
import designpatterns.factorymethodpattern.FactoryMethodPatternClass;
import designpatterns.observerpattern.ObserverPatternClass;
import designpatterns.statepattern.StatePatternClass;
import designpatterns.strategypattern.StrategyPatternClass;
import designpatterns.visitorpattern.VisitorPatternClass;

/*
 * this class aims to construct the role to value mapping for different patterns. 
 */
public class ConstructRole2Values {

	public static HashMap<String, ArrayList<Object>> observerPattern(PatternClass op)
	{
		HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
		
		role2values.put("Subject", new ArrayList<Object>());
		if(((ObserverPatternClass)op).getSubjectClass()!=null){
			role2values.get("Subject").add(((ObserverPatternClass)op).getSubjectClass());
		}

		role2values.put("Observer", new ArrayList<Object>());
		if(((ObserverPatternClass)op).getListernerClass()!=null){
			role2values.get("Observer").add(((ObserverPatternClass)op).getListernerClass());
		}
		
		role2values.put("notify", new ArrayList<Object>());
		if(((ObserverPatternClass)op).getNotifyMethod()!=null){
			role2values.get("notify").add(((ObserverPatternClass)op).getNotifyMethod());
		}
		
		role2values.put("update", new ArrayList<Object>());
		if(((ObserverPatternClass)op).getUpdateMethod()!=null){
			role2values.get("update").add(((ObserverPatternClass)op).getUpdateMethod());
		}
		
		
		role2values.put("register", new ArrayList<Object>());
		if(((ObserverPatternClass)op).getRegisterMethod()!=null){
			role2values.get("register").add(((ObserverPatternClass)op).getRegisterMethod());
		}
		
		
		role2values.put("unregister", new ArrayList<Object>());
		if(((ObserverPatternClass)op).getDe_registerMethod()!=null){
			role2values.get("unregister").add(((ObserverPatternClass)op).getDe_registerMethod());
		}
		
		return role2values;
	}
	
	public static HashMap<String, ArrayList<Object>> statePattern(PatternClass sp)
	{
		HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
		
		role2values.put("Context", new ArrayList<Object>());
		if(((StatePatternClass)sp).getContext()!=null){
			role2values.get("Context").add(((StatePatternClass)sp).getContext());
		}

		role2values.put("State", new ArrayList<Object>());
		if(((StatePatternClass)sp).getState()!=null){
			role2values.get("State").add(((StatePatternClass)sp).getState());
		}
		
		role2values.put("setState", new ArrayList<Object>());
		if(((StatePatternClass)sp).getSetState()!=null)
		{
			role2values.get("setState").add(((StatePatternClass)sp).getSetState());
		}
		
		role2values.put("request", new ArrayList<Object>());
		if(((StatePatternClass)sp).getRequest()!=null){
			role2values.get("request").add(((StatePatternClass)sp).getRequest());
		}
		
		role2values.put("handle", new ArrayList<Object>());
		if(((StatePatternClass)sp).getHandle()!=null){
			role2values.get("handle").add(((StatePatternClass)sp).getHandle());
		}
		
		return role2values;
	}
	
	public static HashMap<String, ArrayList<Object>> strategyPattern(PatternClass sp)
	{
		HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
		
		role2values.put("Context", new ArrayList<Object>());
		if(((StrategyPatternClass)sp).getContext()!=null){
			role2values.get("Context").add(((StrategyPatternClass)sp).getContext());
		}

		role2values.put("Strategy", new ArrayList<Object>());
		if(((StrategyPatternClass)sp).getStrategy()!=null){
			role2values.get("Strategy").add(((StrategyPatternClass)sp).getStrategy());
		}

		role2values.put("setStrategy", new ArrayList<Object>());
		if(((StrategyPatternClass)sp).getSetStrategy()!=null)
		{
			role2values.get("setStrategy").add(((StrategyPatternClass)sp).getSetStrategy());
		}
		
		role2values.put("contextInterface", new ArrayList<Object>());
		if(((StrategyPatternClass)sp).getContextInterface()!=null){
			role2values.get("contextInterface").add(((StrategyPatternClass)sp).getContextInterface());
		}
		
		role2values.put("algorithmInterface", new ArrayList<Object>());
		if(((StrategyPatternClass)sp).getAlgorithmInterface()!=null){
			role2values.get("algorithmInterface").add(((StrategyPatternClass)sp).getAlgorithmInterface());
		}
		
		return role2values;
	}
	
	
	public static HashMap<String, ArrayList<Object>> AdapterPattern(PatternClass ap)
	{
		HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
		role2values.put("Adapter", new ArrayList<Object>());
		if(((AdapterPatternClass)ap).getAdapterClass()!=null){
			role2values.get("Adapter").add(((AdapterPatternClass)ap).getAdapterClass());
		}
		
		role2values.put("Adaptee", new ArrayList<Object>());
		if(((AdapterPatternClass)ap).getAdapteeClass()!=null){
			role2values.get("Adaptee").add(((AdapterPatternClass)ap).getAdapteeClass());
		}
		
		role2values.put("request", new ArrayList<Object>());
		if(((AdapterPatternClass)ap).getRequestMethod()!=null){
			role2values.get("request").add(((AdapterPatternClass)ap).getRequestMethod());
		}
		
		role2values.put("specificRequest", new ArrayList<Object>());
		if(((AdapterPatternClass)ap).getSpecificRequestMethod()!=null){
			role2values.get("specificRequest").add(((AdapterPatternClass)ap).getSpecificRequestMethod());
		}
		return role2values;
	}
	
	public static HashMap<String, ArrayList<Object>> FactoryMethodPattern(PatternClass ap)
	{
		HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
		
		/*
		 * the role name should be same with the specification. 
		 */
		role2values.put("Creator", new ArrayList<Object>());
		if(((FactoryMethodPatternClass)ap).getCreator()!=null){
			role2values.get("Creator").add(((FactoryMethodPatternClass)ap).getCreator());
		}
		
		role2values.put("factoryMethod", new ArrayList<Object>());
		if(((FactoryMethodPatternClass)ap).getFactoryMethod()!=null){
			role2values.get("factoryMethod").add(((FactoryMethodPatternClass)ap).getFactoryMethod());
		}
		
		return role2values;
	}
	
	
	public static HashMap<String, ArrayList<Object>> commandPattern(PatternClass cp)
	{
		HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
		
		role2values.put("Invoker", new ArrayList<Object>());
		if(((CommandPatternClass)cp).getInvoker()!=null){
			role2values.get("Invoker").add(((CommandPatternClass)cp).getInvoker());
		}

		role2values.put("Command", new ArrayList<Object>());
		if(((CommandPatternClass)cp).getCommand()!=null){
			role2values.get("Command").add(((CommandPatternClass)cp).getCommand());
		}
		
		role2values.put("Receiver", new ArrayList<Object>());
		if(((CommandPatternClass)cp).getReceiver()!=null){
			role2values.get("Receiver").add(((CommandPatternClass)cp).getReceiver());
		}
		
		role2values.put("call", new ArrayList<Object>());
		if(((CommandPatternClass)cp).getCall()!=null){
			role2values.get("call").add(((CommandPatternClass)cp).getCall());
		}
		
		
		role2values.put("execute", new ArrayList<Object>());
		if(((CommandPatternClass)cp).getExecute()!=null){
			role2values.get("execute").add(((CommandPatternClass)cp).getExecute());
		}
		
		
		role2values.put("action", new ArrayList<Object>());
		if(((CommandPatternClass)cp).getAction()!=null){
			role2values.get("unregister").add(((CommandPatternClass)cp).getAction());
		}
		
		return role2values;
	}
	
	public static HashMap<String, ArrayList<Object>> visitorPattern(PatternClass vp)
	{
		HashMap<String, ArrayList<Object>> role2values = new HashMap<>();
		
		role2values.put("Element", new ArrayList<Object>());
		if(((VisitorPatternClass)vp).getElement()!=null){
			role2values.get("Element").add(((VisitorPatternClass)vp).getElement());
		}

		role2values.put("Visitor", new ArrayList<Object>());
		if(((VisitorPatternClass)vp).getVisitor()!=null){
			role2values.get("Visitor").add(((VisitorPatternClass)vp).getVisitor());
		}

		role2values.put("accept", new ArrayList<Object>());
		if(((VisitorPatternClass)vp).getAccept()!=null){
			role2values.get("accept").add(((VisitorPatternClass)vp).getAccept());
		}
		
		role2values.put("visit", new ArrayList<Object>());
		if(((VisitorPatternClass)vp).getVisit()!=null){
			role2values.get("visit").add(((VisitorPatternClass)vp).getVisit());
		}
		
		return role2values;
	}
	
}
