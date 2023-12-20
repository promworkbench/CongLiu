package software.designpattern;

import java.util.ArrayList;
import java.util.HashMap;

import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.framework.plugin.events.Logger.MessageLevel;

import designpatterns.adapterpattern.ClassTypeHierarchy;
import designpatterns.framework.BasicOperators;
import designpatterns.framework.PatternClass;
import designpatterns.framework.PatternSet;
import designpatterns.framework.PatternSetImpl;

/**
 * this plug-in aims to take as inputs: (1) software execution log; (2) class type hierarchy; and (3) candidate pattern instances (incomplete)
 * and returns a set of validated pattern instances by
 * (1) discover missing roles for the candidate pattern instances (detected from DPD tools), based on some structural constraints;
 * (2) identify pattern instance invocations;
 * (3) check behavioral and structural constraints. 
 * @author cliu3
 *
 */
@Plugin(
		name = "Design Pattern Discovery and Checking Framework",// plugin name
		
		returnLabels = {"Design Patterns"}, //reture labels
		returnTypes = {PatternSetImpl.class},//return class
		
		//input parameter labels
		parameterLabels = {"Design Pattern Candidates", "Software event log", "Class Type Hierarchy"},
		
		userAccessible = true,
		help = "This plugin aims to improve the Design Pattern results discovered from DPD tool." 
		)
public class DesignPatternDiscoveryAndCheckingPlugin {
	
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Design Pattern Discovery and Checking, default",
			// the number of required parameters, {0} means the first input parameter, {1} means the second input parameter, {2} means the third input parameter
			requiredParameterLabels = {0, 1, 2}
			)
	public PatternSet DiscoveryandChecking(UIPluginContext context, PatternSet patternSet, XLog softwareLog, ClassTypeHierarchy cth)
	{
		//the input patternSet is derived from the static tool, which may be not complete. 
		//context.log("plugin starts", MessageLevel.NORMAL);
		PatternSet completeCandidateInstanceSet = new PatternSetImpl(); // intermediate results
				
		for(PatternClass p: patternSet.getPatternSet())
		{
			context.log("pattern instance: "+p.toString(), MessageLevel.NORMAL);
					
			//these methods differs from design patterns. 
			HashMap<String, ArrayList<Object>> role2values = BasicOperators.Role2Values(p);
			
			//create factory to create Xlog, Xtrace and Xevent.
			XFactory factory = new XFactoryNaiveImpl();
			
			//Discover missing values for those roles, and for those values that are not in the log, use the class type hierarchy information.
			StatePatternDiscoveryAndChecking discoverStatePattern = new StatePatternDiscoveryAndChecking();
			StrategyPatternDiscoveryAndChecking discoverStrategyPattern = new StrategyPatternDiscoveryAndChecking();
			CommandPatternDiscoveryAndChecking discoverCommandPattern = new CommandPatternDiscoveryAndChecking();
			ObserverPatternDiscoveryAndChecking discoverObserverPattern = new ObserverPatternDiscoveryAndChecking();
			VisitorPatternDiscoveryAndChecking discoverVisitorPattern = new VisitorPatternDiscoveryAndChecking();
			PatternSet validatedPatterns;
			if(p.getPatternName().equals("State Pattern"))// for state pattern candidates
			{
				//discover missing role for the current candidate state pattern instance. 
				ArrayList<HashMap<String, Object>> result=discoverStatePattern.DiscoverCompleteStatePattern(context, p, softwareLog, cth, role2values);
				//parse through all complete candidates and check the invocation level constraints. 
				validatedPatterns =discoverStatePattern.StatePatternInvocationConstraintsChecking(context, factory, p, softwareLog, cth, result);
				if(validatedPatterns!=null)
				{
					completeCandidateInstanceSet.addPatternSet(validatedPatterns.getPatternSet());
				}
			}
			else if(p.getPatternName().equals("Strategy Pattern"))//for strategy patterns
			{
				//discover missing role for the current candidate strategy pattern instance. 
				ArrayList<HashMap<String, Object>> result=discoverStrategyPattern.DiscoverCompleteStrategyPattern(context, p, softwareLog, cth, role2values);
				//parse through all complete candidates and check the invocation level constraints. 
				validatedPatterns =discoverStrategyPattern.StrategyPatternInvocationConstraintsChecking(context, factory, p, softwareLog, cth, result);
				if(validatedPatterns!=null)
				{
					completeCandidateInstanceSet.addPatternSet(validatedPatterns.getPatternSet());
				}
			}
			else if (p.getPatternName().equals("Command Pattern"))//for command pattern candidates.
			{
				// discover missing role for the current candidate command pattern instance. 
				ArrayList<HashMap<String, Object>> result=discoverCommandPattern.DiscoverCompleteCommandPattern(context, p, softwareLog, cth, role2values);
				//parse through all complete candidates and check the invocation level constraints. 
				validatedPatterns =discoverCommandPattern.CommandPatternInvocationConstraintsChecking(context, factory, p, softwareLog, cth, result);
				if(validatedPatterns!=null)
				{
					completeCandidateInstanceSet.addPatternSet(validatedPatterns.getPatternSet());
				}
			}
			else if (p.getPatternName().equals("Observer Pattern"))//for observer pattern candidates.
			{
				// discover missing role for the current candidate observer pattern instance. 
				ArrayList<HashMap<String, Object>> result=discoverObserverPattern.DiscoverCompleteObserverPattern(context, p, softwareLog, cth, role2values);
				//parse through all complete candidates and check the invocation level constraints. 
				validatedPatterns =discoverObserverPattern.ObserverPatternInvocationConstraintsChecking(context, factory, p, softwareLog, cth, result);
				if(validatedPatterns!=null)
				{
					completeCandidateInstanceSet.addPatternSet(validatedPatterns.getPatternSet());
				}
			}
			else if (p.getPatternName().equals("Visitor Pattern"))//for observer pattern candidates.
			{
				// discover missing role for the current candidate observer pattern instance. 
				ArrayList<HashMap<String, Object>> result=discoverVisitorPattern.DiscoverCompleteVisitorPattern(context, p, softwareLog, cth, role2values);
				//parse through all complete candidates and check the invocation level constraints. 
				validatedPatterns =discoverVisitorPattern.VisitorPatternInvocationConstraintsChecking(context, factory, p, softwareLog, cth, result);
				if(validatedPatterns!=null)
				{
					completeCandidateInstanceSet.addPatternSet(validatedPatterns.getPatternSet());
				}
			}
//			else if (p.getPatternName().equals("Strategy Pattern"))
//			{
//				
//			}

		}
		
		
		return completeCandidateInstanceSet;

	}
}
