package congliu.processmining.softwareprocessmining;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;

import XESSoftwareExtension.XSoftwareExtension;


public class InitializeSoftwareEventLog {

	public static XLog initialize(XFactory factory, String logName)
	{
		//add the log name		
		XLog log = factory.createLog();
		log.getAttributes().put(XConceptExtension.KEY_NAME, new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, logName));
		
		//create standard extension
		XExtension conceptExtension = XConceptExtension.instance();
		//XExtension organizationalExtension = XOrganizationalExtension.instance();
		XExtension timeExtension = XTimeExtension.instance();
		XExtension lifecycleExtension=	XLifecycleExtension.instance();
		XExtension softwareExtension=	XSoftwareExtension.instance();
		
		// create extensions
		log.getExtensions().add(conceptExtension);
		log.getExtensions().add(softwareExtension);
		log.getExtensions().add(lifecycleExtension);
		log.getExtensions().add(timeExtension);
		
		// create trace level global attributes
		XAttribute xtrace = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
		log.getGlobalTraceAttributes().add(xtrace);

		// create event level global attributes
		
//		XAttribute xeventname = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
//		XAttribute xeventClass = new XAttributeLiteralImpl(XSoftwareExtension.KEY_CLASS, "DEFAULT"); 
//		XAttribute xeventPackage = new XAttributeLiteralImpl(XSoftwareExtension.KEY_PACKAGE, "DEFAULT"); 
//		XAttribute xeventClassObject = new XAttributeLiteralImpl(XSoftwareExtension.KEY_CLASSOBJ, "DEFAULT"); 
//		XAttribute xeventComponent = new XAttributeLiteralImpl(XSoftwareExtension.KEY_COMPONENT, "DEFAULT"); 
//		XAttribute xeventCallerMethod = new XAttributeLiteralImpl(XSoftwareExtension.KEY_CALLERMETHOD, "DEFAULT"); 
//		XAttribute xeventCallerClass = new XAttributeLiteralImpl(XSoftwareExtension.KEY_CALLERCLASS, "DEFAULT"); 
//		XAttribute xeventCallerPackage = new XAttributeLiteralImpl(XSoftwareExtension.KEY_CALLERPACKAGE, "DEFAULT"); 
//		XAttribute xeventCallerClassObject = new XAttributeLiteralImpl(XSoftwareExtension.KEY_CALLERCLASSOBJ, "DEFAULT"); 
//		XAttribute xeventCallerComponent = new XAttributeLiteralImpl(XSoftwareExtension.KEY_CALLERCOMPONENT, "DEFAULT"); 
//		XAttribute xeventTimeNanoStart = new XAttributeLiteralImpl(XSoftwareExtension.KEY_STARTTIMENANO, "DEFAULT"); 
//		XAttribute xeventTimeNanoEnd = new XAttributeLiteralImpl(XSoftwareExtension.KEY_ENDTIMENANO, "DEFAULT"); 
//		XAttribute xeventlifecycle = new XAttributeLiteralImpl(XLifecycleExtension.KEY_TRANSITION, "start");
		XAttribute xeventNestingLevel = new XAttributeLiteralImpl("Nesting_Level", "0");
		/**
		 * Interface type, 0: internal component event, 1: required event, 2: provided event
		 */
		XAttribute xeventInterfaceType = new XAttributeLiteralImpl("Interface_Type", "0");
		

		log.getGlobalEventAttributes().add(XConceptExtension.ATTR_NAME);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CLASS);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_PACKAGE);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CLASSOBJ);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_COMPONENT);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CALLERMETHOD);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CALLERCLASS);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CALLERPACKAGE);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CALLERCLASSOBJ);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_CALLERCOMPONENT);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_STARTTIMENANO);
		log.getGlobalEventAttributes().add(XSoftwareExtension.ATTR_ENDTIMENANO);
		log.getGlobalEventAttributes().add(XLifecycleExtension.ATTR_TRANSITION);
		log.getGlobalEventAttributes().add(xeventNestingLevel);
		log.getGlobalEventAttributes().add(xeventInterfaceType);
		
		// create classifiers based on global attribute		

		/*
		 * XEventAttributeClassifier classifierActivityObject = new XEventAttributeClassifier("Method Call Identifier Object", 
		
				"Package", "Class", XConceptExtension.KEY_NAME, "Class_Object");
		*/
		XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Method Call Identifier", 
				 XConceptExtension.KEY_NAME, XSoftwareExtension.KEY_CLASS, XSoftwareExtension.KEY_PACKAGE);
		//log.getClassifiers().add(classifierActivityObject);
		log.getClassifiers().add(classifierActivity);
		
		return log;
	}
}
