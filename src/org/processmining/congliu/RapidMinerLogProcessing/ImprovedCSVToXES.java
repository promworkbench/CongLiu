package org.processmining.congliu.RapidMinerLogProcessing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeDiscreteImpl;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.deckfour.xes.out.XesXmlGZIPSerializer;
import org.processmining.xeslite.external.XFactoryExternalStore;

public class ImprovedCSVToXES {

	private XLog log;
	private XTrace trace;
	private XFactory factory;
	//standard extensions
	private XExtension conceptExtension;
	private XExtension organizationalExtension;
	private XExtension timeExtension;
	private XExtension lifecycleExtension;
	
	// the input file
	private  String csvfilename = null; 
	// the output file
	private  String xesfilename = null; 
	
	//constructor
	public ImprovedCSVToXES(String XESFileName, String CSVFileName)
	{
		xesfilename = XESFileName; 
		csvfilename = CSVFileName;
	}
	
	//improved conversion from CSV to XES, which can handle the case where a created log is larger than the current main memory?
	
	public void improvedConversion() throws IOException
	{
		//open readin file
		File file = new File(csvfilename); 
		File[] filelist = file.listFiles();
		BufferedWriter bw ;
		//each file is transformed to a trace
		//initialize log
		initializeXLog();
		// create trace, initialize the trace name
		
		for (File f: filelist)
        {
			trace = factory.createTrace();
			log.add(trace);
			System.out.println(f.getName());
			
			// put attribute to trace, the final parameter of createAttributeLiteral can be omitted
			trace.getAttributes().put(XConceptExtension.KEY_NAME,
			factory.createAttributeLiteral(XConceptExtension.KEY_NAME, "Case"+ parseInt(f.getName()), conceptExtension));
			
			// adding events to each trace
			BufferedReader reader = null;

    		try {
    			reader = new BufferedReader(new FileReader(f));
    		} catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		String tempString = "";
    		String [] tempList; 
//    		XEvent eventStart;
//    		XEvent eventEnd; 
    		XEvent event;
    		while ((tempString = reader.readLine()) != null)
			{
				tempList = tempString.split(";");
				
//				eventStart = createEnrichedEvent(tempList[0], tempList[1], "start", tempList[3],tempList[4],tempList[5], tempList[6], tempList[7], tempList[8], tempList[9]);
//				eventEnd = createEnrichedEvent(tempList[0], tempList[2], "complete", tempList[3], tempList[4],tempList[5], tempList[6], tempList[7], tempList[8], tempList[9]);
//				trace.add(eventStart);
//				trace.add(eventEnd);
				
				event =  createPackageLevelEvent(tempList[0],tempList[2], "complete", tempList[3], tempList[4], tempList[5], tempList[6]);
				
				trace.add(event);
			}	
    		
    		
    		//trace.clear();
        }
		
		
		//serialization the current XES log to disk
		try {
			FileOutputStream fosgz = new FileOutputStream (xesfilename); 
			//FileOutputStream fos = new FileOutputStream ("D:\\KiekerData\\CaseStudy001\\EnrichedMethodLevelLog.xes.gz"); 
			//new FileWriter(fileName,true)
			//new XesXmlSerializer().serialize(log, fosgz); 
//			// serialize to xes.gz
			new XesXmlGZIPSerializer().serialize(log, fosgz);

			fosgz.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}
	
	
	
	/*
	 * log initialization, adding global attribute (trace level and event level), and classifier
	 */

	public void initializeXLog()
	{
		
		// we choose not to use, Xlog for serialization, but to write to files case by case
//		try {
//			FileInputStream fs = new FileInputStream (xesfilename); 			
//			log = new XesXmlParser().parse(fs).get(0);
//			fs.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
		
		// Create a Xfactory to produce the eventlog, the XFactoryExternalStore handle the case where a created log is larger than the current main memory?
		factory = new XFactoryExternalStore.MapDBDiskSequentialAccessWithoutCacheImpl();//.MapDBDiskSequentialAccessImpl();
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
		XAttribute xeventresource = new XAttributeLiteralImpl(XOrganizationalExtension.KEY_RESOURCE, ""); 
		XAttribute xeventlifecycle = new XAttributeLiteralImpl(XLifecycleExtension.KEY_TRANSITION, "complete");
		//XAttribute xeventplugin = new XAttributeLiteralImpl("plugin", "DEFAULT"); 
		//XAttribute xeventclass = new XAttributeLiteralImpl("class", "DEFAULT"); 
		//XAttribute xeventpackage1 = new XAttributeLiteralImpl("package", "DEFAULT"); 
		//XAttribute xeventpackage2 = new XAttributeLiteralImpl("package2", "DEFAULT"); 
		log.getGlobalEventAttributes().add(xeventname);
		log.getGlobalEventAttributes().add(xeventresource);
		log.getGlobalEventAttributes().add(xeventlifecycle);
		//log.getGlobalEventAttributes().add(xeventplugin);
		//log.getGlobalEventAttributes().add(xeventclass);
		//log.getGlobalEventAttributes().add(xeventpackage1);
		//log.getGlobalEventAttributes().add(xeventpackage2);
		
		// create classifiers based on global attribute		
		XEventAttributeClassifier classifierName = new XEventAttributeClassifier("Package Name", XConceptExtension.KEY_NAME);
		//XEventAttributeClassifier classifierClass = new XEventAttributeClassifier("Class Name", "class");
		//XEventAttributeClassifier classifierPackage = new XEventAttributeClassifier("Package Name", "package1");
		XEventAttributeClassifier classifierLifeCycle = new XEventAttributeClassifier("Lifecycle", XConceptExtension.KEY_NAME, XLifecycleExtension.KEY_TRANSITION);
		log.getClassifiers().add(classifierName);
		//log.getClassifiers().add(classifierClass);
		//log.getClassifiers().add(classifierPackage);
		log.getClassifiers().add(classifierLifeCycle);	


	}
	
	/*
	 * create package level Xevent 
	 */
	
	public XEvent createPackageLevelEvent(String packageName, String timestamp, String lifecycle, String plugin, String usedmemory, String totalmemory, String usedcpu)
	{
	
		// create event with standard extensions
				XAttributeMap attributeMap = new XAttributeMapImpl();
				attributeMap.put(XConceptExtension.KEY_NAME,
						factory.createAttributeLiteral(XConceptExtension.KEY_NAME, packageName, conceptExtension));
				
				attributeMap.put(XOrganizationalExtension.KEY_RESOURCE, factory.createAttributeLiteral(
						XOrganizationalExtension.KEY_RESOURCE, plugin, organizationalExtension));
				
				attributeMap.put(XLifecycleExtension.KEY_TRANSITION, factory.createAttributeLiteral(
						XLifecycleExtension.KEY_TRANSITION, lifecycle, lifecycleExtension));
				
				// change nanoseconds to milliseconds
				Date Millidate = new Date(Long.parseLong(timestamp)/1000000);
				
				attributeMap.put(XTimeExtension.KEY_TIMESTAMP, factory.createAttributeTimestamp(
						XTimeExtension.KEY_TIMESTAMP, Millidate, timeExtension));
				
				// domain specific event attributes
//				attributeMap.put("plugin", new XAttributeLiteralImpl("plugin", plugin));
				attributeMap.put("cpuPercentage", new XAttributeLiteralImpl("cpuPercentage", usedcpu));
				
				attributeMap.put("usedMemory", new XAttributeDiscreteImpl("usedMemory", Long.parseLong(usedmemory)));
				attributeMap.put("totalMemory", new XAttributeDiscreteImpl("totalMemory", Long.parseLong(totalmemory)));
				
				XEvent event = factory.createEvent(attributeMap);
				
				return event;
	}
	/*
	 * create Xevent 
	 */
	
	public XEvent createEnrichedEvent(String methodname,  String timestamp, String lifecycle, String plugin, String className, String package1Name, String package2Name, String usedmemory, String totalmemory, String usedcpu)
	{
		
		// create event with standard extensions
		XAttributeMap attributeMap = new XAttributeMapImpl();
		attributeMap.put(XConceptExtension.KEY_NAME,
				factory.createAttributeLiteral(XConceptExtension.KEY_NAME, methodname, conceptExtension));
		
		attributeMap.put(XOrganizationalExtension.KEY_RESOURCE, factory.createAttributeLiteral(
				XOrganizationalExtension.KEY_RESOURCE, "C.Liu", organizationalExtension));
		
		attributeMap.put(XLifecycleExtension.KEY_TRANSITION, factory.createAttributeLiteral(
				XLifecycleExtension.KEY_TRANSITION, lifecycle, lifecycleExtension));
		
		// change nanoseconds to milliseconds
		Date Millidate = new Date(Long.parseLong(timestamp)/1000000);
		
		attributeMap.put(XTimeExtension.KEY_TIMESTAMP, factory.createAttributeTimestamp(
				XTimeExtension.KEY_TIMESTAMP, Millidate, timeExtension));
		
		// domain specific event attributes
		attributeMap.put("plugin", new XAttributeLiteralImpl("plugin", plugin));
		attributeMap.put("class", new XAttributeLiteralImpl("class", className));
		attributeMap.put("package1", new XAttributeLiteralImpl("package1", package1Name));
		attributeMap.put("package2", new XAttributeLiteralImpl("package2", package2Name));
		attributeMap.put("cpuPercentage", new XAttributeLiteralImpl("cpuPercentage", usedcpu));
		
		attributeMap.put("usedMemory", new XAttributeDiscreteImpl("usedMemory", Long.parseLong(usedmemory)));
		attributeMap.put("totalMemory", new XAttributeDiscreteImpl("totalMemory", Long.parseLong(totalmemory)));
		
		XEvent event = factory.createEvent(attributeMap);
		
		return event;
		
		/*
		 * discard the old way. 
		 */
//		XAttributeMap attMap = new XAttributeMapImpl();
//
//		//convert time
//		Date dateStart = new Date(Long.parseLong(timestamp)/1000000);  
//		
//		XLogFunctions.putLiteral(attMap, "org:resource", resource);
//		XLogFunctions.putLiteral(attMap, "concept:name", name);
//		XLogFunctions.putTimestamp(attMap, "time:timestamp", dateStart);
//		XLogFunctions.putLiteral(attMap, "lifecycle:transition", lifecycle);
//		XLogFunctions.putLiteral(attMap, "plugin", plugin);
//		XLogFunctions.putLiteral(attMap, "class", className);
//		XLogFunctions.putLiteral(attMap, "package1", package1Name);
//		XLogFunctions.putLiteral(attMap, "package2", package2Name);
//		
//		XLogFunctions.putLiteral(attMap, "usedMemory", usedmemory);
//		XLogFunctions.putLiteral(attMap, "totalMemory", totalmemory);
//		XLogFunctions.putLiteral(attMap, "cpuPercent", usedcpu);
//		
//		XEvent event = new XEventImpl(attMap);
//		
//		return event;
	}
	
	/*
	 * pase the case id from file name (String)
	 */
	public String parseInt(String string)
	{
		String result = "";
		String regEx="[^0-9]";   
		Pattern p = Pattern.compile(regEx);      
		Matcher m = p.matcher(string);
		result = m.replaceAll("").trim();
		return result;
	}
	
	
//	public static void main (String [] args) throws IOException
//	{
//		ImprovedCSVToXES icx = new ImprovedCSVToXES("D:\\KiekerData\\CaseStudy002\\ImprovedEnrichedMethodLevelLog001.xes.gz", "D:\\KiekerData\\CaseStudy002\\test10case");
//		icx.improvedConversion();
//	}
}
