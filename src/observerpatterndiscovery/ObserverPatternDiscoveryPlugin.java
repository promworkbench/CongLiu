package observerpatterndiscovery;

import java.util.HashSet;
import java.util.Set;

import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import XESSoftwareExtension.XSoftwareExtension;

@Plugin(
		name = "Observer Design Pattern Discovery",// plugin name
		
		returnLabels = {"Observer Design Patterns"}, //reture labels
		returnTypes = {ObserverPatternSet.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software event log"},
		
		userAccessible = true,
		help = "This plugin aims to discover the Observer Design Patterns from a software event log." 
		)
public class ObserverPatternDiscoveryPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Observer Design Pattern Discovery, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	public ObserverPatternSet observerDiscovery(UIPluginContext context, XLog softwareLog)
	{
		ObserverPatternSet ops = new ObserverPatternSet();
		
		ObserverPatternDiscovery opd = new ObserverPatternDiscovery();
		// get the class to method set from the softwarelog
		Class2MethodSet c2m =opd.getClass2MethodSet(softwareLog);
		
		for(ClassClass c: c2m.getClassSet())
		{
			System.out.print(c+"XXX");
			for(MethodClass m: c2m.getMethodSet(c))
			{
				System.out.print(m+"X");
			}
			System.out.print(c2m.getMethodParameterSet(c));
			System.out.print("\n");
		}
		
		//for each class, check they are subject class,
		
		//<1> a subject should have at least 2 methods, i.e., addListerner(), notify(), init()?
		Set<ClassClass> candidateSubjectClasses= new HashSet<ClassClass>();
		for(ClassClass c: c2m.getClassSet())
		{
			if(c2m.getMethodSet(c).size()>=2)
			{
				candidateSubjectClasses.add(c);
			}
		}
		for(ClassClass candidateSubjectC: candidateSubjectClasses)
		{
			System.out.println("Candidate class" + candidateSubjectC);
		}
		
		// we detect observer patterns trace by trace
		for(XTrace trace : softwareLog)
		{
			System.out.println("New Trace:");
			//<2> one observer pattern can have multiple instantiations in each trace, 
			//each corresponds to different subject class object.
			for(ClassClass candidateSubjectC: candidateSubjectClasses)
			{
				Set<String> subjectObjects = new HashSet<>();
				for(XEvent event: trace)
				{
					if(candidateSubjectC.getClassName().equals(XSoftwareExtension.instance().extractClass(event)))
					{
						subjectObjects.add(XSoftwareExtension.instance().extractClassObject(event));
					}
				}
				
//				for(String obj: subjectObjects)
//				{
//					System.out.println("Candidate class objects:" + candidateSubjectC+obj);
//				}
				
				//<3> for each subject instance candidate, check its method one by one to decide the notify().
				for(String subjectO: subjectObjects)
				{
					for(MethodClass candidateNotifyM: c2m.getMethodSet(candidateSubjectC))
					{
						System.out.println("Candidate class objects notifyM: " + candidateSubjectC+subjectO+candidateNotifyM);
						
						//<3.1> get the invoked class to objects mapping.
						ClassObjectsMethodSet invokedClass2objectMethods =opd.getInvokedClassandObjects(trace, subjectO,candidateNotifyM.getMethodName());
						
						for(ClassObjectsMethod com: invokedClass2objectMethods.getAll())
						{
							System.out.println("ClassObjectsMethod: "+com);
						}
						
						//if the number of invoked method=0, then continue
						if(invokedClass2objectMethods.getSize()<1)
						{
							continue;
						}
						
						//<3.2> get the candidate listerner class, i.e., classes included in the parameter set of current class 
						Set<ClassClass> candidateListernerClasses = new HashSet<>();
						System.out.println("invoked listerner class:"+invokedClass2objectMethods.getClassSet());
						
						for(ClassClass candidateListernerC: invokedClass2objectMethods.getClassSet())
						{
							if(c2m.getMethodParameterSet(candidateSubjectC).contains(candidateListernerC.toString()))
							{
								candidateListernerClasses.add(candidateListernerC);
							}
						}
						
						System.out.println("candidate listerner class:"+candidateListernerClasses);
						
						//<3.3> the candidate class should have >1 objects. 
						if(candidateListernerClasses.size()>0)
						{
							for(ClassClass candidateListenerC: candidateListernerClasses)
							{
								if(invokedClass2objectMethods.getObjectSet(candidateListenerC).size()>=1)
								{
									//<3.4> parameter set the current method (notify method) do not include the listener class 
									if(!candidateNotifyM.getParameterSet().contains(candidateListenerC.toString()))
									{
										//create the observer pattern instance.
										ObserverPatternClass op = new ObserverPatternClass();
										op.setSubjectClass(candidateSubjectC);
										op.setListernerClass(candidateListenerC);
										op.setNotifyMethod(candidateNotifyM);
										op.setUpdateMethod(invokedClass2objectMethods.getMethod(candidateListenerC));
										// the register() and deregister() cannot be distinguished..
										Set<MethodClass> de_registerListenermethod = new HashSet<>();
										//<3.5> the register() and deregister() should be the methods of subject, 
										//and the candidate listerner class should be included in its parameters.
										for(MethodClass candidateUpdateM: c2m.getMethodSet(candidateSubjectC))
										{
											if(candidateUpdateM.getParameterSet().contains(candidateListenerC.toString()))
											{
												de_registerListenermethod.add(candidateUpdateM);
											}
										}
										op.setDeregisteringMethod(de_registerListenermethod);
										ops.add(op);
									}
								}
							}
							
						}
						
					}
				}
			}
			
		
		}

		
		return ops;
	}
}
