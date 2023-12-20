package org.processmining.congliu.examplecodes;

import java.io.FileOutputStream;
import java.io.IOException;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.out.XesXmlSerializer;
import org.processmining.xeslite.external.XFactoryExternalStore;

/**
 * this class is a standard version to generate XES event log with basic attribute configuration
 * @author cliu3
 *
 */
public class XESGeneration {
	private XLog log;
	private XTrace trace;
	private XFactory factory;
	//standard extensions
	private XExtension conceptExtension;
	private XExtension organizationalExtension;
	private XExtension timeExtension;
	private XExtension lifecycleExtension;
	
	
	/**
	 * Creates and initializes a log. 
	*/
	
	public void initializeLog() {
		
		// Create a Xfactory to produce the eventlog, the XFactoryExternalStore handle the case where a created log is larger than the current main memory?
		factory = new XFactoryExternalStore.MapDBDiskSequentialAccessImpl();
		factory = new XFactoryNaiveImpl();
		//factory = XFactoryRegistry.instance().currentDefault();
		conceptExtension = XConceptExtension.instance();
		organizationalExtension = XOrganizationalExtension.instance();
		timeExtension = XTimeExtension.instance();
		lifecycleExtension=	XLifecycleExtension.instance();
		
		log = factory.createLog();
		
		// create extensions
		log.getExtensions().add(conceptExtension);
		log.getExtensions().add(organizationalExtension);
		log.getExtensions().add(lifecycleExtension);
		log.getExtensions().add(timeExtension);
		
		// create trace level global attributes
		XAttribute xtrace = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, ""); 
		log.getGlobalTraceAttributes().add(xtrace);


		// create event level global attributes
		XAttribute xeventname = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, ""); 
		XAttribute xeventresource = new XAttributeLiteralImpl(XOrganizationalExtension.KEY_RESOURCE, "C.Liu"); 
		XAttribute xeventlifecycle = new XAttributeLiteralImpl(XLifecycleExtension.KEY_TRANSITION, "complete");
		XAttribute xeventplugin = new XAttributeLiteralImpl("plugin", "DEFAULT"); 
		XAttribute xeventclass = new XAttributeLiteralImpl("class", "DEFAULT"); 
		XAttribute xeventpackage = new XAttributeLiteralImpl("package", "DEFAULT"); 
		log.getGlobalEventAttributes().add(xeventname);
		log.getGlobalEventAttributes().add(xeventresource);
		log.getGlobalEventAttributes().add(xeventlifecycle);
		log.getGlobalEventAttributes().add(xeventplugin);
		log.getGlobalEventAttributes().add(xeventclass);
		log.getGlobalEventAttributes().add(xeventpackage);
		
		// create classifiers based on global attribute		
		XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Activity Name", XConceptExtension.KEY_NAME);
		XEventAttributeClassifier classifierClass = new XEventAttributeClassifier("Class Name", "class");
		XEventAttributeClassifier classifierPackage = new XEventAttributeClassifier("Package Name", "package");
		XEventAttributeClassifier classifierActivityLifeCycle = new XEventAttributeClassifier("Activity Name", XConceptExtension.KEY_NAME, XLifecycleExtension.KEY_TRANSITION);
		log.getClassifiers().add(classifierActivity);
		log.getClassifiers().add(classifierClass);
		log.getClassifiers().add(classifierPackage);
		log.getClassifiers().add(classifierActivityLifeCycle);	
		
		// create trace
		trace = factory.createTrace();
		log.add(trace);
		// put attribute to trace, the final parameter of createAttributeLiteral can be omitted
		trace.getAttributes().put(XConceptExtension.KEY_NAME,
		factory.createAttributeLiteral(XConceptExtension.KEY_NAME, "Trace name", conceptExtension));

		
		// create event with standard extensions
		XAttributeMap attributeMap = new XAttributeMapImpl();
		attributeMap.put(XConceptExtension.KEY_NAME,
				factory.createAttributeLiteral(XConceptExtension.KEY_NAME, "event name", conceptExtension));
		
		attributeMap.put(XOrganizationalExtension.KEY_RESOURCE, factory.createAttributeLiteral(
				XOrganizationalExtension.KEY_RESOURCE, "event organization", organizationalExtension));
		
		attributeMap.put(XLifecycleExtension.KEY_TRANSITION, factory.createAttributeLiteral(
				XLifecycleExtension.KEY_TRANSITION, "event lifecycle", lifecycleExtension));
		
		attributeMap.put(XTimeExtension.KEY_TIMESTAMP, factory.createAttributeTimestamp(
				XTimeExtension.KEY_TIMESTAMP, System.currentTimeMillis(), timeExtension));
		
		// domain specific extension
		attributeMap.put("memory", new XAttributeLiteralImpl("memory", "123456"));
		attributeMap.put("memoryused", new XAttributeDiscreteImpl("memoryused", 123456));
	
		
		XEvent event = factory.createEvent(attributeMap);
		trace.add(event);
		
		
		//log.add(trace);
		
		
		//serialization the current XES log 
		try {
			//FileOutputStream fosgz = new FileOutputStream ("D:\\KiekerData\\CaseStudy001\\test.xes.gz"); 
			FileOutputStream fos = new FileOutputStream ("D:\\KiekerData\\CaseStudy001\\test.xes"); 
			//new FileWriter(fileName,true)
			new XesXmlSerializer().serialize(log, fos); 
//			// serialize to xes.gz
//			new XesXmlGZIPSerializer().serialize(log, fosgz);

			fos.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}
	
	public static void main (String [] args)
	{
		XESGeneration xesgeneration = new XESGeneration();
		xesgeneration.initializeLog();
	}
}
