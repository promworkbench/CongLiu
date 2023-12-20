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
 * this plugin aim to re-factor the raw software execution data by XPort (from Maikel)
 * (1) add caller stuff to main();
 * (2) add object information to construct; because for some constructor, we lost the start event during the instrumentation, 
 * this is the main difference from the previous version, "SoftwareLogRefacroingPlugin.java"
 * (3) combine start & complete to one with two nano-seconds timestamps for start and complete
 * (4) XES logging on: https://svn.win.tue.nl/repos/prom/XPort/ are used.
 * 
 * 2017-6-1 new feature
 * Adding the parameter values. 
 * (1) Not all events has the parameter value.
 * (2) the start and complete events have the same parameter value information, so we use the complete event. 3
 * (3) do not add the parameter value for the main and constructors. Only for normal methods. 
 * @author cliu3
 *
 */
@Plugin(
		name = "Pre-processing XPort based Software Event Log (V2-parameter values)",// plugin name
		
		returnLabels = {"A Software Event Log"}, //return labels
		returnTypes = {XLog.class},//return class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software Event Log"},
		
		userAccessible = true,
		help = "This plugin aims to pre-process software event log collected by Maikel Leemans XPort instrumentation." 
		)
public class SoftwareLogRefactoringPluginV2 {
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
		
		int mainFlag =0;// this is to denote if the current log has main, for some test programs, no main can be found. 
		for(XTrace originalTrace: originalLog)
		{
			i++; // the execution id starts from 1.
			XTrace refatoredTrace = factory.createTrace();// create the new trace
			XConceptExtension.instance().assignName(refatoredTrace, "execution"+i);// add trace name
			
			// add the main
			String startTimenano = "";
			String endTimenano = "";
			String pacClassMethodMain ="";
			String classObjectMain = "@execution"+i; // add manual class object for main.
			int lineNumber =-1;
			int threadID = -1;
			
			for(XEvent originalEvent: originalTrace)// get the attribute of main();
			{
				
				if (XConceptExtension.instance().extractName(originalEvent).contains("main(java.lang.String[])"))
				{
					// main is found.
					mainFlag =1;
					if (XLifecycleExtension.instance().extractTransition(originalEvent).equals("start"))
						// main start event, get the start time and pac+class name
					{
						startTimenano = ((XAttributeLiteral) originalEvent.getAttributes().get("apprun:nanotime")).getValue();
						pacClassMethodMain = XConceptExtension.instance().extractName(originalEvent);
						lineNumber = Integer.parseInt(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:linenr")).getValue());
						threadID = Integer.parseInt(((XAttributeLiteral) originalEvent.getAttributes().get("apprun:threadid")).getValue());
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
				XSoftwareExtension.instance().assignCallermethod(mainEvent, "null");// the caller are null for main.
				XSoftwareExtension.instance().assignCallerclass(mainEvent, "null");
				XSoftwareExtension.instance().assignCallerclassobject(mainEvent, "null");
				XSoftwareExtension.instance().assignCallerpackage(mainEvent, "null");
				XSoftwareExtension.instance().assignStarttimenano(mainEvent, startTimenano);
				XSoftwareExtension.instance().assignEndtimenano(mainEvent, endTimenano);
				XSoftwareExtension.instance().assignLineNumber(mainEvent, lineNumber);
				XSoftwareExtension.instance().assignThreadID(mainEvent, threadID);
				XSoftwareExtension.instance().assignParameterTypeSet(mainEvent, GetMathodClassPackages.extractParameterSet(pacClassMethodMain));
				
				refatoredTrace.add(mainEvent);
			}

			
			// add constructor method calls.
			
			// this is used to store all constructor names. can be further used to detect if the caller is a constructor. 
			ArrayList<String> constructorJoinPointNames = new ArrayList<>();
			for(XEvent originalEvent: originalTrace)
			{
				if(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:etype")).getValue().equals("call_new"))
						//&& XLifecycleExtension.instance().extractTransition(originalEvent).equals("start"))
				{
					// add constructor event to trace
					XEvent constructorEvent = factory.createEvent();
					// add all constructor names to a list
					constructorJoinPointNames.add(XConceptExtension.instance().extractName(originalEvent));
					
					// check if the constructor event is start or complete, this is because sometimes start constructor event is missing. 
					if (XLifecycleExtension.instance().extractTransition(originalEvent).equals("start"))// the constructor is start
					{	
						XConceptExtension.instance().assignName(constructorEvent, "init()");
						XLifecycleExtension.instance().assignTransition(constructorEvent, "start");
						XSoftwareExtension.instance().assignClass(constructorEvent, 
						GetMathodClassPackages.extractConstructorClass(XConceptExtension.instance().extractName(originalEvent)));
						XSoftwareExtension.instance().assignPackage(constructorEvent, 
						GetMathodClassPackages.extractConstructorPackage(XConceptExtension.instance().extractName(originalEvent)));
						XSoftwareExtension.instance().assignStarttimenano(constructorEvent, 
								((XAttributeLiteral) originalEvent.getAttributes().get("apprun:nanotime")).getValue());// start timestamp
						
						XSoftwareExtension.instance().assignParameterTypeSet(constructorEvent, 
								GetMathodClassPackages.extractParameterSet(XConceptExtension.instance().extractName(originalEvent)));	
						XSoftwareExtension.instance().assignLineNumber(constructorEvent,
								Integer.parseInt(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:linenr")).getValue()));
						XSoftwareExtension.instance().assignThreadID(constructorEvent,
								Integer.parseInt(((XAttributeLiteral) originalEvent.getAttributes().get("apprun:threadid")).getValue()));
						
						
						//find its corresponding complete constructor event to add the object information
						//get the complete constructor of the current start constructor.
						XSoftwareExtension.instance().assignClassObject(constructorEvent, 
							((XAttributeLiteral) getCompleteConstructorEvent(originalTrace.indexOf(originalEvent),originalTrace,originalEvent)
									.getAttributes().get("apploc:idhashcode")).getValue());
						XSoftwareExtension.instance().assignEndtimenano(constructorEvent, 
								((XAttributeLiteral) getCompleteConstructorEvent(originalTrace.indexOf(originalEvent),originalTrace,originalEvent)
										.getAttributes().get("apprun:nanotime")).getValue());// complete timestamp
						
					}
					else{ // the constructor is complete, i.e., the start constructor event is missing
						XConceptExtension.instance().assignName(constructorEvent, "init()");
						XLifecycleExtension.instance().assignTransition(constructorEvent, "start");
						XSoftwareExtension.instance().assignClass(constructorEvent, 
						GetMathodClassPackages.extractConstructorClass(XConceptExtension.instance().extractName(originalEvent)));
						XSoftwareExtension.instance().assignPackage(constructorEvent, 
						GetMathodClassPackages.extractConstructorPackage(XConceptExtension.instance().extractName(originalEvent)));
						XSoftwareExtension.instance().assignEndtimenano(constructorEvent, 
								((XAttributeLiteral) originalEvent.getAttributes().get("apprun:nanotime")).getValue());// end timestamp
						XSoftwareExtension.instance().assignClassObject(constructorEvent, 
								((XAttributeLiteral) originalEvent.getAttributes().get("apploc:idhashcode")).getValue());
						
						XSoftwareExtension.instance().assignParameterTypeSet(constructorEvent, 
								GetMathodClassPackages.extractParameterSet(XConceptExtension.instance().extractName(originalEvent)));	
						XSoftwareExtension.instance().assignLineNumber(constructorEvent,
								Integer.parseInt(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:linenr")).getValue()));
						XSoftwareExtension.instance().assignThreadID(constructorEvent,
								Integer.parseInt(((XAttributeLiteral) originalEvent.getAttributes().get("apprun:threadid")).getValue()));
						
						//find its corresponding start constructor event (actually the first callee event) to add the start time information. 
						XSoftwareExtension.instance().assignStarttimenano(constructorEvent, 
								((XAttributeLiteral) getFirstCalleeofConstructorEvent(originalTrace.indexOf(originalEvent),originalTrace,originalEvent)
										.getAttributes().get("apprun:nanotime")).getValue());// start timestamp
					}
					
					// add caller information
					if (((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue().contains("main(java.lang.String[])"))
					{
						if (mainFlag==1)// the log contains main, i.e., pacClassMethodMain is not null;
						{
							XSoftwareExtension.instance().assignCallermethod(constructorEvent, GetMathodClassPackages.extractMethod(pacClassMethodMain));
							XSoftwareExtension.instance().assignCallerclass(constructorEvent, GetMathodClassPackages.extractClass(pacClassMethodMain));
							XSoftwareExtension.instance().assignCallerclassobject(constructorEvent, classObjectMain);
							XSoftwareExtension.instance().assignCallerpackage(constructorEvent, GetMathodClassPackages.extractPackage(pacClassMethodMain));
							//XSoftwareExtension.instance().assignCallercomponent(event, callerComponentName);
						}
						else{// pacClassMethodMain is null
							XSoftwareExtension.instance().assignCallermethod(constructorEvent, GetMathodClassPackages.extractMethod(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							XSoftwareExtension.instance().assignCallerclass(constructorEvent, GetMathodClassPackages.extractClass(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							XSoftwareExtension.instance().assignCallerclassobject(constructorEvent, classObjectMain);
							XSoftwareExtension.instance().assignCallerpackage(constructorEvent, GetMathodClassPackages.extractPackage(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
						}
						
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
					
					refatoredTrace.add(constructorEvent);
				}
			}
			
			
			// add normal method calls. 
			for(XEvent originalEvent: originalTrace)
			{
				if(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:etype")).getValue().equals("call")
						&& XLifecycleExtension.instance().extractTransition(originalEvent).equals("start")
						&& !XConceptExtension.instance().extractName(originalEvent).contains("main(java.lang.String[])"))// not main
				{
					// add normal event to trace
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
					XSoftwareExtension.instance().assignLineNumber(normalMethodEvent,
							Integer.parseInt(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:linenr")).getValue()));
					XSoftwareExtension.instance().assignThreadID(normalMethodEvent,
							Integer.parseInt(((XAttributeLiteral) originalEvent.getAttributes().get("apprun:threadid")).getValue()));
					
					
					//add parameter type and values.
					String paramTypes= GetMathodClassPackages.extractParameterSet(XConceptExtension.instance().extractName(originalEvent));
					XSoftwareExtension.instance().assignParameterTypeSet(normalMethodEvent, 
							paramTypes);	
					//add the parameter values, each value corresponds to a type. 
					String paramValues = "";
					if(paramTypes.contains(","))// more than one parameter
					{
						for(int j=1; j<=paramTypes.trim().split("\\,").length;j++)
						{
							if(j!=paramTypes.trim().split("\\,").length)// not the last one, we add a , at the end
							{
								paramValues=paramValues+((XAttributeLiteral) originalEvent.getAttributes().get("apploc:params:param"+j)).getValue()+",";
							}
							else{// for the last parameter, we do not add , at the end
								paramValues=paramValues+((XAttributeLiteral) originalEvent.getAttributes().get("apploc:params:param"+j)).getValue();
							}
							
						}
						XSoftwareExtension.instance().assignParameterValueSet(normalMethodEvent, 
								paramValues);	
					}
					else if (paramTypes.contains("."))// only one parameter
					{
						XSoftwareExtension.instance().assignParameterValueSet(normalMethodEvent, 
								((XAttributeLiteral) originalEvent.getAttributes().get("apploc:params:param1")).getValue());	
					}
					else{ //no parameter
						XSoftwareExtension.instance().assignParameterValueSet(normalMethodEvent, 
								paramValues);	
					}
								

					
					// if the caller of the current method is main()
					if (((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue().contains("main(java.lang.String[])"))
					{
						if (mainFlag==1)// the log contains main, i.e., pacClassMethodMain is not null;
						{
							XSoftwareExtension.instance().assignCallermethod(normalMethodEvent, GetMathodClassPackages.extractMethod(pacClassMethodMain));
							XSoftwareExtension.instance().assignCallerclass(normalMethodEvent, GetMathodClassPackages.extractClass(pacClassMethodMain));
							XSoftwareExtension.instance().assignCallerclassobject(normalMethodEvent, classObjectMain);
							XSoftwareExtension.instance().assignCallerpackage(normalMethodEvent, GetMathodClassPackages.extractPackage(pacClassMethodMain));
							//XSoftwareExtension.instance().assignCallercomponent(event, callerComponentName);
						}
						else{// pacClassMethodMain is null
							XSoftwareExtension.instance().assignCallermethod(normalMethodEvent, GetMathodClassPackages.extractMethod(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							XSoftwareExtension.instance().assignCallerclass(normalMethodEvent, GetMathodClassPackages.extractClass(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
							XSoftwareExtension.instance().assignCallerclassobject(normalMethodEvent, classObjectMain);
							XSoftwareExtension.instance().assignCallerpackage(normalMethodEvent, GetMathodClassPackages.extractPackage(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:caller:joinpoint")).getValue()));
						}
						
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
					
					//add the complete time by matching its complete event. 
					//because for static event, the object and name are the same, 
					//we use the first complete event after the index of current one.
					int originalEventIndex = originalTrace.indexOf(originalEvent);
					
					for (int j=originalEventIndex;j<originalTrace.size();j++)
					//for (XEvent tempEvent: originalTrace)
					{
						if ( ((XAttributeLiteral) originalTrace.get(j).getAttributes().get("apploc:idhashcode")).getValue().
								equals(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:idhashcode")).getValue())
								&&((XAttributeLiteral) originalTrace.get(j).getAttributes().get("apploc:joinpoint")).getValue().
								equals(((XAttributeLiteral) originalEvent.getAttributes().get("apploc:joinpoint")).getValue())
								&& XLifecycleExtension.instance().extractTransition(originalTrace.get(j)).equals("complete")
								&&!XConceptExtension.instance().extractName(originalTrace.get(j)).contains("main(java.lang.String[])"))
						{
							XSoftwareExtension.instance().assignEndtimenano(normalMethodEvent, 
									((XAttributeLiteral) originalTrace.get(j).getAttributes().get("apprun:nanotime")).getValue());
							break;
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
		for(int i=indexofStart+1; i<currentTrace.size();i++)
		{
			if (XConceptExtension.instance().extractName(currentTrace.get(i)).equals(XConceptExtension.instance().extractName(currentStartEvent))
					&& XLifecycleExtension.instance().extractTransition(currentTrace.get(i)).equals("complete"))
			{
				return currentTrace.get(i);
			}
		}
		return currentStartEvent;
	}
	
	//get the first callee of current constructor event 
	public static XEvent getFirstCalleeofConstructorEvent(int indexofEnd, XTrace currentTrace, XEvent currentEndEvent)
	{
		for(int i=0; i<indexofEnd;i++)
		{
			if (XConceptExtension.instance().extractName(currentEndEvent).
					equals(((XAttributeLiteral) (currentTrace.get(i)).getAttributes().get("apploc:caller:joinpoint")).getValue())
					&&((XAttributeLiteral) currentTrace.get(i).getAttributes().get("apploc:caller:joinpoint")).getValue().
					equals(((XAttributeLiteral) currentEndEvent.getAttributes().get("apploc:idhashcode")).getValue()))			
			{
				return currentTrace.get(i);
			}
		}
		return currentEndEvent;
	}
	
}
