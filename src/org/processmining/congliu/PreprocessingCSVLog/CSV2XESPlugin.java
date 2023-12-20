package org.processmining.congliu.PreprocessingCSVLog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.deckfour.xes.classification.XEventAttributeClassifier;
import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XLifecycleExtension;
import org.deckfour.xes.extension.std.XOrganizationalExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeLiteral;
import org.deckfour.xes.model.XAttributeMap;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.deckfour.xes.model.impl.XAttributeMapImpl;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

/**
 * This class tries to convert the CSV file obtained from "LogProcessingPlugin"
 * to a XES file for further discovery algorithm use. 
 * As our current representation does not use data elements recorded, we only include those 
 * attributes like method name, class identifier, package identifier, runtime component, 
 * belonging component, interaction type, start and end time. 
 * In addition, each case has a reference to its workflow. 
 * We also try to order events according to there time stamp before serialization.   
 * @author cliu3
 *
 */

@Plugin(
		name = "CSV2XES Converter, ordered",// plugin name
		
		returnLabels = {"Formatted XES Log"}, //reture labels
		returnTypes = {XLog.class},//reture class
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"CSV RapidMiner Recording"},
		
		userAccessible = true,
		help = "This plugin aims to convert the recorded software (RapidProm) event log in CSV to XES." 
		)
public class CSV2XESPlugin {

	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "CSV2XES Converter, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {}
			)
		
	
	public XLog convert(UIPluginContext context) throws IOException
	{
		//adding a set of dialogs to select what is the input file and output file. 
		FileChooserConfiguration CSVdirectory = new FileChooserConfiguration();
//		FileChooserConfiguration XESdirectory = new FileChooserConfiguration();
		
		new FileChooserPanel(CSVdirectory);
//		new FileChooserPanel(XESdirectory);
		
		// Create a Xfactory to produce the eventlog, the XFactoryExternalStore handle the case where a created log is larger than the current main memory?
		XFactory factory = new XFactoryNaiveImpl();
		//XFactory factory = new XFactoryExternalStore.MapDBDiskSequentialAccessImpl();
		XLog log = factory.createLog();
		
		//create standard extension
		XExtension conceptExtension = XConceptExtension.instance();
		XExtension organizationalExtension = XOrganizationalExtension.instance();
		XExtension timeExtension = XTimeExtension.instance();
		XExtension lifecycleExtension=	XLifecycleExtension.instance();
		
		// create extensions
		log.getExtensions().add(conceptExtension);
		log.getExtensions().add(organizationalExtension);
		log.getExtensions().add(lifecycleExtension);
		log.getExtensions().add(timeExtension);
		
		// create trace level global attributes
		XAttribute xtrace = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
		log.getGlobalTraceAttributes().add(xtrace);

		// create event level global attributes
		XAttribute xeventname = new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "DEFAULT"); 
		XAttribute xeventresource = new XAttributeLiteralImpl(XOrganizationalExtension.KEY_RESOURCE, "C.Liu"); 
		XAttribute xeventlifecycle = new XAttributeLiteralImpl(XLifecycleExtension.KEY_TRANSITION, "complete");
		//XAttribute xeventMethod = new XAttributeLiteralImpl("Method", "DEFAULT"); 
		XAttribute xeventClass = new XAttributeLiteralImpl("Class", "DEFAULT"); 
		XAttribute xeventPackage = new XAttributeLiteralImpl("Package", "DEFAULT"); 
		XAttribute xeventRuntimeComponent = new XAttributeLiteralImpl("Runtime_Component", "DEFAULT"); 
		XAttribute xeventBelongingComponent = new XAttributeLiteralImpl("Belonging_Component", "DEFAULT"); 
		XAttribute xeventInteractionType = new XAttributeLiteralImpl("Interaction_Type", "DEFAULT"); 
		XAttribute xeventTimeNano = new XAttributeLiteralImpl("Timestamp_Nano", "DEFAULT"); 
		//XAttribute xeventEndTimeNano = new XAttributeLiteralImpl("Complete_Time_Nano", "DEFAULT"); 
		log.getGlobalEventAttributes().add(xeventname);
		log.getGlobalEventAttributes().add(xeventresource);
		log.getGlobalEventAttributes().add(xeventlifecycle);
		//log.getGlobalEventAttributes().add(xeventMethod);
		log.getGlobalEventAttributes().add(xeventClass);
		log.getGlobalEventAttributes().add(xeventPackage);
		log.getGlobalEventAttributes().add(xeventRuntimeComponent);
		log.getGlobalEventAttributes().add(xeventBelongingComponent);
		log.getGlobalEventAttributes().add(xeventInteractionType);
		log.getGlobalEventAttributes().add(xeventTimeNano);
		//log.getGlobalEventAttributes().add(xeventEndTimeNano);
		
		// create classifiers based on global attribute		
		XEventAttributeClassifier classifierActivity = new XEventAttributeClassifier("Acticity Name", XConceptExtension.KEY_NAME, "Class", "Package");
		log.getClassifiers().add(classifierActivity);
		
		// split the csvfile to different cases, according to their case id
		ArrayList<ArrayList> arrayCases = splitToCases(CSVdirectory.getFilename());
		
		// foreach case, we create a trace in the log. 
		for (ArrayList<String> cases: arrayCases) 
		{
			//cases contains the information of each trace. 
			XTrace trace = factory.createTrace();
			log.add(trace);
			//add the case name. 
			String [] caseName = cases.get(0).split(",");
			trace.getAttributes().put(XConceptExtension.KEY_NAME,
					factory.createAttributeLiteral(XConceptExtension.KEY_NAME, caseName[0], conceptExtension));
			
			for(String str: cases)
			{
				// create start and end events for each str
				XEvent eventStart= createEvent(str, factory, "start"); 				
				XEvent eventEnd= createEvent(str, factory, "complete"); 	
				// referring to the insertOrdered method of XTrace, re-write it to sort according to Timestamp_Nano
				OrderedInsert(trace, eventStart);
				OrderedInsert(trace, eventEnd);
//				trace.add(eventStart);
//				trace.add(eventEnd);
			}
		}
		
		
//		//serialization the current XES log to disk
//		try {
//			FileOutputStream fosgz = new FileOutputStream (XESdirectory.getFilename()+"\\\\formattedProMLog.xes.gz"); 
//			//FileOutputStream fos = new FileOutputStream ("D:\\KiekerData\\CaseStudy001\\EnrichedMethodLevelLog.xes.gz"); 
//			
//			//new XesXmlSerializer().serialize(log, fosgz); 
//            // serialize to xes.gz
//			new XesXmlGZIPSerializer().serialize(log, fosgz);
//
//			fosgz.close();
//			
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} 
		
		return log;
	}//covert
	
	// this function aims to split the original event log (csv) to different cases. 
	public ArrayList<ArrayList> splitToCases(String csvfile) throws IOException
	{
		ArrayList<ArrayList> list = new ArrayList<ArrayList>();
		ArrayList<String> cases = new ArrayList<String>();
		
		//open readin file
		File file = new File(csvfile); 
		// adding events to each trace
		BufferedReader reader = new BufferedReader(new FileReader(file));
		
		String tempString = "";
		String [] tempList; 
		String currentCaseName ="";
		while ((tempString = reader.readLine()) != null)
		{
			tempList = tempString.split(",");
			
			if (tempList[0].equals(currentCaseName))
			{
				cases.add(tempString);
				//System.out.println(tempString);
			}
			else
			{
				// the first case is null, then we just add the recording and change it to casename. 
				if (cases.size()>0)
				{
					ArrayList<String> tempCase = new ArrayList<String>();
					for(String s : cases)
					{
						tempCase.add(s);
					}
					list.add(tempCase);
					
					cases.clear();
				}
				cases.add(tempString);
				//System.out.println(tempString);
				currentCaseName = tempList[0];
				//System.out.println(currentCaseName);
			}		
		}//while
		// for the last case, directly add it to the list. 
		ArrayList<String> tempCase = new ArrayList<String>();
		for(String s : cases)
		{
			tempCase.add(s);
		}
		list.add(tempCase);
		
		
		for(int i =0;i<list.size();i++)
		{
			for(int j =0;j<list.get(i).size();j++)
			{
				System.out.println(list.get(i).get(j));
			}
		}
		
//		for(ArrayList<String> case2: list)
//		{
//			for (String s: case2)
//			{
//				System.out.println(s);
//			}
//		}

		return list;
	}//splitToCases
	
	
	public XEvent createEvent(String str, XFactory factory, String lifecycle)
	{
		String [] tempList = str.split(","); 
		// create event with standard extensions
		XAttributeMap attributeMap = new XAttributeMapImpl();
		// the concept is the method name here
		attributeMap.put(XConceptExtension.KEY_NAME,
				factory.createAttributeLiteral(XConceptExtension.KEY_NAME, tempList[1], XConceptExtension.instance()));
		
		attributeMap.put(XOrganizationalExtension.KEY_RESOURCE, factory.createAttributeLiteral(
				XOrganizationalExtension.KEY_RESOURCE, "C.Liu", XOrganizationalExtension.instance()));
		
		if (lifecycle.equals("start"))
		{
			attributeMap.put(XLifecycleExtension.KEY_TRANSITION, factory.createAttributeLiteral(
					XLifecycleExtension.KEY_TRANSITION, "start", XLifecycleExtension.instance()));
			// change nanoseconds to milliseconds
			Date Millidate = new Date(Long.parseLong(tempList[8])/1000000);
			
			attributeMap.put(XTimeExtension.KEY_TIMESTAMP, factory.createAttributeTimestamp(
					XTimeExtension.KEY_TIMESTAMP, Millidate, XTimeExtension.instance()));
			
			//attributeMap.put("Start_Time_Nano", new XAttributeDiscreteImpl("Start_Time_Nano", Long.parseLong(tempList[8])));
			attributeMap.put("Timestamp_Nano", new XAttributeLiteralImpl("Timestamp_Nano", tempList[8]));
		}
		else
		{
			attributeMap.put(XLifecycleExtension.KEY_TRANSITION, factory.createAttributeLiteral(
					XLifecycleExtension.KEY_TRANSITION, "complete", XLifecycleExtension.instance()));
			
			// change nanoseconds to milliseconds
			Date Millidate = new Date(Long.parseLong(tempList[9])/1000000);
			
			attributeMap.put(XTimeExtension.KEY_TIMESTAMP, factory.createAttributeTimestamp(
					XTimeExtension.KEY_TIMESTAMP, Millidate, XTimeExtension.instance()));
			
			//attributeMap.put("Complete_Time_Nano", new XAttributeDiscreteImpl("Complete_Time_Nano", Long.parseLong(tempList[9])));
			attributeMap.put("Timestamp_Nano", new XAttributeLiteralImpl("Timestamp_Nano", tempList[9]));
		} 

		// domain specific event attributes
		attributeMap.put("Class", new XAttributeLiteralImpl("Class", tempList[2]));
		attributeMap.put("Package", new XAttributeLiteralImpl("Package", tempList[3]));
		attributeMap.put("Runtime_Component", new XAttributeLiteralImpl("Runtime_Component", tempList[4]));
		attributeMap.put("Belonging_Component", new XAttributeLiteralImpl("Belonging_Component", tempList[5]));
		attributeMap.put("Interaction_Type", new XAttributeLiteralImpl("Interaction_Type", tempList[6]));
		
		XEvent event = factory.createEvent(attributeMap);
		
		return event;
	}
	
	// insert event to trace with Timestamp_Nano order
	public synchronized void OrderedInsert(XTrace trace, XEvent event)
	{
		if (trace.size() == 0) {
			// append if the current trace is empty
			trace.add(event);
			return;
		}
		
		//get the current key of timestamp in nano. 
		XAttribute currentAttr = event.getAttributes().get("Timestamp_Nano");
		
		//if event does not have this attribute, just add it
		if (currentAttr == null) {
			// append if event has no timestamp
			trace.add(event);
			return;
		}
		
		long currentValue = Long.parseLong(((XAttributeLiteral)currentAttr).getValue().toString());
		
		for (int i = (trace.size() - 1); i >= 0; i--) {
			XAttribute refTsAttr = trace.get(i).getAttributes().get("Timestamp_Nano");
			if (refTsAttr == null) {
				// trace contains events w/o timestamps, append.
				trace.add(event);
				return;
			}
			long refTsValue = Long.parseLong(((XAttributeLiteral) refTsAttr).getValue().toString());
			
			if (currentValue-refTsValue>0) {
				// insert position reached
				trace.add(i + 1, event);
				return;
			}
		}
		//insert at the beginning 
		trace.add(0, event);
		return;
	}

}
