package congliu.processminig.XPortSoftwareEventlogTransformation;

import java.util.ArrayList;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import XESSoftwareExtension.XSoftwareExtension;
import congliu.processmining.softwareprocessmining.InitializeSoftwareEventLog;
import congliu.processmining.softwareprocessmining.OrderingEventsNano;

/**
 * this plugin aim to re-factoring the raw software execution data by XPort(from Maikel)
 * (1) add caller stuff to main();
 * 
 * (3) combine start & complete to one. with two nano-seconds
 * (4) XES logging on: https://svn.win.tue.nl/repos/prom/XPort/ are used.
 * @author cliu3
 *
 */
@Plugin(
		name = "Pre-processing XPort based Software Event Log",// plugin name
		
		returnLabels = {"A Software Event Log"}, //return labels
		returnTypes = {XLog.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software Event Log"},
		
		userAccessible = false,
		help = "This plugin aims to pre-process software event log collected by Maikel Leemans XPort instrumentation." 
		)
public class SoftwareLogRefactoingPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Pre-processing XPort based Software Event Log, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {0}
			)
	
	public static XLog preprocessing(UIPluginContext context, XLog originalLog)
	{
		return refactoring(originalLog);
	}
	
	public static XLog refactoring(XLog originalLog)
	{
		// the refactored log.
		XFactory factory = new XFactoryNaiveImpl();
		
		// input the log name and a factory.
		XLog refactoredLog =InitializeSoftwareEventLog.initialize(factory, XConceptExtension.instance().extractName(originalLog));
			
		int i =0;// add the trace id.
		
		int mainFlag =0;// this is to denote if the current log has main, for some test programs, no main is found. 
		for(XTrace originalTrace: originalLog)
		{
			i++;
			XTrace refatoredTrace = factory.createTrace();
			XConceptExtension.instance().assignName(refatoredTrace, "execution"+i);
			
			// add the main
			String startTimenano = "";
			String endTimenano = "";
			String pacClassMethodMain ="";
			String classObjectMain = "@execution"+i; // add manual class object for main.
			
			for(XEvent originalEvent: originalTrace)// get the attribute of main();
			{
				if (XConceptExtension.instance().extractName(originalEvent).contains("main(java.lang.String[])"))
				{
					// main is found.
					mainFlag =1;
					if (XLifecycleExtension.instance().extractTransition(originalEvent).equals("start"))
					{
						startTimenano = ((XAttributeLiteral) originalEvent.getAttributes().get("apprun:nanotime")).getValue();
						pacClassMethodMain = XConceptExtension.instance().extractName(originalEvent);
					}
					if (XLifecycleExtension.instance().extractTransition(originalEvent).equals("complete"))
					{
						endTimenano = ((XAttributeLiteral) originalEvent.getAttributes().get("apprun:nanotime")).getValue();
					}
				}
			}
			
			// add main event to trace
			if (mainFlag==1)
			{
				XEvent mainEvent = factory.createEvent();
				XConceptExtension.instance().assignName(mainEvent, GetMathodClassPackages.extractMethod(pacClassMethodMain));
				XLifecycleExtension.instance().assignTransition(mainEvent, "start");
				XSoftwareExtension.instance().assignClass(mainEvent, GetMathodClassPackages.extractClass(pacClassMethodMain));
				XSoftwareExtension.instance().assignPackage(mainEvent, GetMathodClassPackages.extractPackage(pacClassMethodMain));
				XSoftwareExtension.instance().assignClassObject(mainEvent, classObjectMain);
				XSoftwareExtension.instance().assignCallermethod(mainEvent, "null");
				XSoftwareExtension.instance().assignCallerclass(mainEvent, "null");
				XSoftwareExtension.instance().assignCallerclassobject(mainEvent, "null");
				XSoftwareExtension.instance().assignCallerpackage(mainEvent, "null");
				XSoftwareExtension.instance().assignStarttimenano(mainEvent, startTimenano);
				XSoftwareExtension.instance().assignEndtimenano(mainEvent, endTimenano);
				
				refatoredTrace.add(mainEvent);
			}

			
			// add constructor method calls.
			
			// this is used to store all constructor names. can be further used to detect if the caller is a constructor. 
			ArrayList<String> constructorJoinPointNames = new ArrayList<>();
			for(XEvent originalEvent: originalTrace)
			{
				if(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:etype")).getValue().equals("call_new")
						&& XLifecycleExtension.instance().extractTransition(originalEvent).equals("start"))
				{
					// add constructor event to trace
					XEvent constructorEvent = factory.createEvent();
					
					XConceptExtension.instance().assignName(constructorEvent, "init()");
					XLifecycleExtension.instance().assignTransition(constructorEvent, "start");
					XSoftwareExtension.instance().assignClass(constructorEvent, 
							GetMathodClassPackages.extractConstructorClass(XConceptExtension.instance().extractName(originalEvent)));
					XSoftwareExtension.instance().assignPackage(constructorEvent, 
							GetMathodClassPackages.extractConstructorPackage(XConceptExtension.instance().extractName(originalEvent)));
					
					constructorJoinPointNames.add(XConceptExtension.instance().extractName(originalEvent));// add all constructor names to a list
					
					// use the next event, the complete constructor to add the object information. 	
					//get the complete constructor of the current start constructor.
					XSoftwareExtension.instance().assignClassObject(constructorEvent, 
							((XAttributeLiteral) getCompleteConstructorEvent(originalTrace.indexOf(originalEvent),originalTrace,originalEvent)
									.getAttributes().get("apploc:idhashcode")).getValue());
					
					if (((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue().contains("main(java.lang.String[])"))
					{
						XSoftwareExtension.instance().assignCallermethod(constructorEvent, GetMathodClassPackages.extractMethod(pacClassMethodMain));
						XSoftwareExtension.instance().assignCallerclass(constructorEvent, GetMathodClassPackages.extractClass(pacClassMethodMain));
						XSoftwareExtension.instance().assignCallerclassobject(constructorEvent, classObjectMain);
						XSoftwareExtension.instance().assignCallerpackage(constructorEvent, GetMathodClassPackages.extractPackage(pacClassMethodMain));
						//XSoftwareExtension.instance().assignCallercomponent(event, callerComponentName);
					}
					else// if the caller of the current method is not main()
					{
						// if the caller of the current method is not a constructor.
						if(!constructorJoinPointNames.contains(((XAttributeLiteral)originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()))
						{
							XSoftwareExtension.instance().assignCallermethod(constructorEvent, 
									GetMathodClassPackages.extractMethod(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							XSoftwareExtension.instance().assignCallerclass(constructorEvent, 
									GetMathodClassPackages.extractClass(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							XSoftwareExtension.instance().assignCallerclassobject(constructorEvent, 
									((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:idhashcode")).getValue());
							XSoftwareExtension.instance().assignCallerpackage(constructorEvent, 
									GetMathodClassPackages.extractPackage(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
						}
						else // if the caller is a constructor.
						{
							XSoftwareExtension.instance().assignCallermethod(constructorEvent, "init()");
							XSoftwareExtension.instance().assignCallerclass(constructorEvent, 
									GetMathodClassPackages.extractConstructorClass(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							XSoftwareExtension.instance().assignCallerclassobject(constructorEvent, 
									((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:idhashcode")).getValue());
							XSoftwareExtension.instance().assignCallerpackage(constructorEvent, 
									GetMathodClassPackages.extractConstructorPackage(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							
						}
					}

					XSoftwareExtension.instance().assignStarttimenano(constructorEvent, 
							((XAttributeLiteral) originalEvent.getAttributes().get("apprun:nanotime")).getValue());
					XSoftwareExtension.instance().assignEndtimenano(constructorEvent, 
							((XAttributeLiteral) originalTrace.get(originalTrace.indexOf(originalEvent)+1).getAttributes().get("apprun:nanotime")).getValue());
					
					refatoredTrace.add(constructorEvent);
				}
			}
			
			
			// add normal method calls. 
			for(XEvent originalEvent: originalTrace)
			{
				if(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:etype")).getValue().equals("call")
						&& XLifecycleExtension.instance().extractTransition(originalEvent).equals("start")
						&& !XConceptExtension.instance().extractName(originalEvent).contains("main(java.lang.String[])"))
				{
					// add main event to trace
					XEvent normalMethodEvent = factory.createEvent();
					
					XConceptExtension.instance().assignName(normalMethodEvent, 
							GetMathodClassPackages.extractMethod(XConceptExtension.instance().extractName(originalEvent)));
					XLifecycleExtension.instance().assignTransition(normalMethodEvent, "start");
					XSoftwareExtension.instance().assignClass(normalMethodEvent, 
							GetMathodClassPackages.extractClass(XConceptExtension.instance().extractName(originalEvent)));
					XSoftwareExtension.instance().assignPackage(normalMethodEvent, 
							GetMathodClassPackages.extractPackage(XConceptExtension.instance().extractName(originalEvent)));
					XSoftwareExtension.instance().assignClassObject(normalMethodEvent, 
							((XAttributeLiteral) originalEvent.getAttributes().get("apploc:idhashcode")).getValue());
					//XSoftwareExtension.instance().assignComponent(event, calleeComponentName);
					
					if (((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue().contains("main(java.lang.String[])"))
					{
						XSoftwareExtension.instance().assignCallermethod(normalMethodEvent, GetMathodClassPackages.extractMethod(pacClassMethodMain));
						XSoftwareExtension.instance().assignCallerclass(normalMethodEvent, GetMathodClassPackages.extractClass(pacClassMethodMain));
						XSoftwareExtension.instance().assignCallerclassobject(normalMethodEvent, classObjectMain);
						XSoftwareExtension.instance().assignCallerpackage(normalMethodEvent, GetMathodClassPackages.extractPackage(pacClassMethodMain));
						//XSoftwareExtension.instance().assignCallercomponent(event, callerComponentName);
					}
					else// if the caller of the current method is not main()
					{
						// if the caller of the current method is not a constructor.
						if(!constructorJoinPointNames.contains(((XAttributeLiteral)originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()))
						{
							XSoftwareExtension.instance().assignCallermethod(normalMethodEvent, 
									GetMathodClassPackages.extractMethod(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							XSoftwareExtension.instance().assignCallerclass(normalMethodEvent, 
									GetMathodClassPackages.extractClass(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							XSoftwareExtension.instance().assignCallerclassobject(normalMethodEvent, 
									((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:idhashcode")).getValue());
							XSoftwareExtension.instance().assignCallerpackage(normalMethodEvent, 
									GetMathodClassPackages.extractPackage(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
						}
						else // if the caller is a constructor.
						{
							XSoftwareExtension.instance().assignCallermethod(normalMethodEvent, "init()");
							XSoftwareExtension.instance().assignCallerclass(normalMethodEvent, 
									GetMathodClassPackages.extractConstructorClass(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							XSoftwareExtension.instance().assignCallerclassobject(normalMethodEvent, 
									((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:idhashcode")).getValue());
							XSoftwareExtension.instance().assignCallerpackage(normalMethodEvent, 
									GetMathodClassPackages.extractConstructorPackage(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							
						}
						
					}
					
					XSoftwareExtension.instance().assignStarttimenano(normalMethodEvent, 
							((XAttributeLiteral) originalEvent.getAttributes().get("apprun:nanotime")).getValue());
					
					//add the complete time by matching it complete event. 
					for (XEvent tempEvent: originalTrace)
					{

						if (!XConceptExtension.instance().extractName(tempEvent).contains("main(java.lang.String[])") &&
								((XAttributeLiteral) tempEvent.getAttributes().get("apploc:idhashcode")).getValue().
								equals(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:idhashcode")).getValue()))
						{
							XSoftwareExtension.instance().assignEndtimenano(normalMethodEvent, 
									((XAttributeLiteral) tempEvent.getAttributes().get("apprun:nanotime")).getValue());
						}
					}
					
					refatoredTrace.add(normalMethodEvent);
				}
			}
			
			
			// add to the new log. 
			refactoredLog.add(refatoredTrace);
		}
		
		// order the re-factored trace using the nano-seconds attribute. 		
		return OrderingEventsNano.ordering(refactoredLog, XSoftwareExtension.KEY_STARTTIMENANO);
	}
	
	//get the complete constructor of the current start constructor.
	public static XEvent getCompleteConstructorEvent(int indexofStart, XTrace currentTrace, XEvent currentStartEvent)
	{
		XEvent completeEvent = null;
		for(int i=indexofStart+1; i<currentTrace.size();i++)
		{
			if (XConceptExtension.instance().extractName(currentTrace.get(i)).equals(XConceptExtension.instance().extractName(currentStartEvent))
					&& XLifecycleExtension.instance().extractTransition(currentTrace.get(i)).equals("complete"))
			{
				completeEvent =currentTrace.get(i);
				return completeEvent;
			}
		}
		return currentStartEvent;
	}
}
