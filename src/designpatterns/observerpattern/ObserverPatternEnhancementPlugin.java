package designpatterns.observerpattern;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processmining.softwareprocessmining.Un_registerMethods;
import designpatterns.framework.PatternClass;
import observerpatterndiscovery.Class2MethodSet;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;
import observerpatterndiscovery.ObserverPatternDiscovery;


/**
 * the aim of this plug-in is to (1) check the correctness of each candidate observer pattern; 
 * (2) add more detailed description of each pattern, the update(), register(),de_register().
 */

@Plugin(
		name = "Observer Design Pattern Enhancement",// plugin name
		
		returnLabels = {"Observer Design Patterns"}, //reture labels
		returnTypes = {ObserverPatternSet.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Observer Pattern Candidates", "Software event log"},
		
		userAccessible = true,
		help = "This plugin aims to enhance the Observer Design Pattern instances discovered from DPD tool." 
		)
public class ObserverPatternEnhancementPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Observer Design Pattern Discovery, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0, 1}
			)

	public ObserverPatternSet observerEnhancement(UIPluginContext context, ObserverPatternSet OPSet, XLog softwareLog)
	{
		ObserverPatternSet enhancedOPSet = new ObserverPatternSet();
		
		// get the class to method set from the software log
		ObserverPatternDiscovery opd = new ObserverPatternDiscovery();
		Class2MethodSet c2m =opd.getClass2MethodSet(softwareLog);
		
		//for each candidate observer pattern instance, 
		for(PatternClass op: OPSet.getPatternSet())
		{
			ObserverPatternClass opNew = new ObserverPatternClass();
			
			//get all information detected from the tool
			ClassClass subjectC= ((ObserverPatternClass)op).getSubjectClass();
			ClassClass listenerC = ((ObserverPatternClass)op).getListernerClass();
			MethodClass notifyM= ((ObserverPatternClass)op).getNotifyMethod();
			
			//get the method set of the subject class
			//rule1: the notify method should belong to the subject class.
			if(!(subjectC.getPackageName()+"."+subjectC.getClassName()).equals(notifyM.getPackageName()+"."+notifyM.getClassName()))
			{
				continue;
			}
			
			//rule2: for the subject, it should have more than 2 methods, register() + notify() 
			if(c2m.getClassSet().contains(subjectC))
			{
				if(c2m.getMethodSet(subjectC).size()<2)
				{
					continue;
				}
			}
			else// the subject class is not included in the current log. 
			{
				continue;
			}
			
			
			//rule3: the notify method should not include a parameter of listener interface
			if(!notifyM.getParameterSet().contains(listenerC.toString()))
			{
			}
			else{
				continue;
			}
			
			opNew.setSubjectClass(subjectC);
			opNew.setListernerClass(listenerC);
			opNew.setNotifyMethod(notifyM);
			
					
			//rule4: to get the update() method, it belongs to Listener class, and should be invoked by notify method multiple times. 
			//get the invoked method set of notify method, i.e. candidate update()
			Set<MethodClass> updateCandidateSet = getInvokedMethodSet(softwareLog, notifyM);
			
			int updateFlag =0;
			for(MethodClass updataC: updateCandidateSet)
			{
				System.out.println("Updata: "+ updataC);
				if((listenerC.getPackageName()+"."+listenerC.getClassName()).equals(updataC.getPackageName()+"."+updataC.getClassName()))
				{
					opNew.setUpdateMethod(updataC);// only one update method?
					updateFlag =1;
					break;
				}
			}
			if(updateFlag==0)// no update method is found.
			{
				continue;
			}
			
			//rule5: to get the register() and un_register() methods, 
			//get the subject object execution traces
			Un_registerMethods Un_registerMethods = getSubjectMethodSet(softwareLog, subjectC, listenerC);
			opNew.setRegisterMethod(Un_registerMethods.registerMethod);
			opNew.setDe_registerMethod(Un_registerMethods.un_registerMethod);
			enhancedOPSet.add(opNew);
		}
		
		return enhancedOPSet;
	}
	
	/*
	 * return the invoked method set of the notify()
	 */
	public static Set<MethodClass> getInvokedMethodSet(XLog log, MethodClass notify)
	{
		Set<MethodClass> methodSet = new HashSet<>();
		for(XTrace trace: log)
		{
			for(XEvent event: trace)
			{			
				if(XSoftwareExtension.instance().extractCallerpackage(event).equals(notify.getPackageName())
						&& XSoftwareExtension.instance().extractCallerclass(event).equals(notify.getClassName())
						&& XSoftwareExtension.instance().extractCallermethod(event).equals(notify.getMethodName()+"()"))
				{
					MethodClass currentMethod = new MethodClass();
					currentMethod.setMethodName(XConceptExtension.instance().extractName(event));
					currentMethod.setClassName(XSoftwareExtension.instance().extractClass(event));
					currentMethod.setPackageName(XSoftwareExtension.instance().extractPackage(event));
					currentMethod.setLineNumber(XSoftwareExtension.instance().extractLineNumber(event));
					
					//set the parameter set of the current method
					Set<String> currentParameterSet = new HashSet<String>();
					
					String tempPara = XSoftwareExtension.instance().extractParameterTypeSet(event);

					if(tempPara.length()!=0)
					{
						if(tempPara.contains(","))
						{
							for(String para: tempPara.split("\\,"))
							{
								currentParameterSet.add(para);
							}
						}
						else
						{
							currentParameterSet.add(tempPara);
						}
					}			
					
					currentMethod.setParameterSet(currentParameterSet);
					
					methodSet.add(currentMethod);
				}
			}
		}
		
		return methodSet;
	}
	
	/*
	 * get the method set of each subject object in order
	 */
	public static Un_registerMethods getSubjectMethodSet(XLog log, ClassClass subjectClass, ClassClass listenerClass)
	{
		Un_registerMethods Methodpair = new Un_registerMethods();
		//one observer pattern can have multiple instantiations in each trace, 
		//each corresponds to different subject class object.
		//get the subject object set 


		for(XTrace trace: log)
		{
			ArrayList<MethodClass> methodList = new ArrayList<>();

			for(XEvent event: trace)
			{
				if(XSoftwareExtension.instance().extractPackage(event).equals(subjectClass.getPackageName())
						&& XSoftwareExtension.instance().extractClass(event).equals(subjectClass.getClassName()))
				{
					//get the parameter set of each method
					Set<String> currentParameterSet = new HashSet<String>();
					String tempPara = XSoftwareExtension.instance().extractParameterTypeSet(event);
	
					if(tempPara.contains(","))
					{
						for(String para: tempPara.split("\\,"))
						{
							currentParameterSet.add(para);
						}
					}
					else// may be empty
					{
						currentParameterSet.add(tempPara);
					}
					
					if(currentParameterSet.contains(listenerClass.toString()))
					{
						MethodClass currentMethod = new MethodClass();
						currentMethod.setMethodName(XConceptExtension.instance().extractName(event));
						currentMethod.setClassName(subjectClass.getClassName());
						currentMethod.setPackageName(subjectClass.getPackageName());
						//currentMethod.setLineNumber(XSoftwareExtension.instance().extractLineNumber(event));
						//currentMethod.setParameterSet(currentParameterSet);
						
						if(!methodList.contains(currentMethod))
						{
							methodList.add(currentMethod);
						}
					}
				}			
			}
			
			if(methodList.size()>=2)
			{
				Methodpair.registerMethod=methodList.get(0);
				Methodpair.un_registerMethod=methodList.get(1);
				break;
			}
		}
		
		return Methodpair;
		
	}
	
}
