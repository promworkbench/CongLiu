package designpatterns.statepattern;

import java.util.Set;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import designpatterns.framework.PatternClass;
import designpatterns.observerpattern.ObserverPatternEnhancementPlugin;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

/**
 * the aim of this plugin is to (1) check the correctness of each candidate state patterns; 
 * (2) add more detailed description of each pattern, the handle() method.
 */

@Plugin(
		name = "State Design Pattern Enhancement",// plugin name
		
		returnLabels = {"State Design Patterns"}, //reture labels
		returnTypes = {StatePatternSet.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"State Pattern Candidates", "Software event log"},
		
		userAccessible = false,
		help = "This plugin aims to enhance the State Design Pattern instances discovered from DPD tool." 
		)
	public class StatePatternEnhancementPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "State Design Pattern Discovery, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0, 1}
			)
	
	public StatePatternSet stateEnhancement(UIPluginContext context, StatePatternSet SPSet, XLog softwareLog)
	{
		StatePatternSet enhancedSPSet = new StatePatternSet();
		
		//for each candidate state pattern instance, 
		for(PatternClass sp: SPSet.getPatternSet())
		{
			//get all information detected from the tool
			ClassClass contextC= ((StatePatternClass)sp).getContext();
			ClassClass stateC = ((StatePatternClass)sp).getState();
			MethodClass requestM= ((StatePatternClass)sp).getRequest();	
			
			//rule1: the request method should belong to the context class.
			if(!(contextC.getPackageName()+"."+contextC.getClassName()).equals(requestM.getPackageName()+"."+requestM.getClassName()))
			{
				continue;
				
			}
			
			System.out.println("rule1 passed");
			
			//rule2: to get the handle() method, it belongs to state class, and should be invoked by request() method. 
			//rule3: the handle method which belong to the state class should include a parameter of context
			//get the invoked method set of notify method, i.e. candidate update()
			Set<MethodClass> handleCandidateSet = ObserverPatternEnhancementPlugin.getInvokedMethodSet(softwareLog, requestM);
			for(MethodClass handleM: handleCandidateSet)
			{
				if((stateC.getPackageName()+"."+stateC.getClassName()).equals(handleM.getPackageName()+"."+handleM.getClassName()))// rule2
				{
					System.out.println("rule2 passed");

					//rule3: the handle() should include a parameter type of context.
					System.out.println("handle()"+handleM.getParameterSet());
//					if(handleM.getParameterSet().contains(contextC.toString()))
//					{
//						System.out.println("rule3 passed");
						StatePatternClass spNew = new StatePatternClass();		
						spNew.setPatternName("State Pattern");
						spNew.setContext(contextC);
						spNew.setState(stateC);
						spNew.setRequest(requestM);
						spNew.setHandle(handleM);
						enhancedSPSet.add(spNew);
//					}
				}
			}
		}
		
		return enhancedSPSet;
	}
}
