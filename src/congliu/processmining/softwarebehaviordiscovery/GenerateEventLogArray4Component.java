package congliu.processmining.softwarebehaviordiscovery;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryNaiveImpl;
import org.deckfour.xes.model.XEvent;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.model.impl.XAttributeLiteralImpl;
import org.processmining.log.models.EventLogArray;
import org.processmining.log.models.impl.EventLogArrayImpl;

import congliu.processmining.softwareprocessmining.AddNestingLevelAttribute;
import congliu.processmining.softwareprocessmining.Component2Classes;
import congliu.processmining.softwareprocessmining.GeneratingSoftwareEventLog;
import congliu.processmining.softwareprocessmining.InitializeSoftwareEventLog;
import congliu.processmining.softwareprocessmining.OrderingEventsNano;

/**
 * this class tries to discover a set of software event log for each component. 
 * here, each component may corresponds with more than one process, 
 * we do not consider the component instance factor. 
 * In addition, methods included in each component contains caller or callee that belongs to the component, i.e., for interaction.  
 * @author cliu3
 */
public class GenerateEventLogArray4Component 
{
	public static EventLogArray generatingSoftwareEventLogArray(Component2Classes com2class, ArrayList<Component2Classes> com2classList, String softwareEventDataDirectory)
	{
		EventLogArray softwareEventLogArray =new EventLogArrayImpl();
		
		//open readin file directory. 
		File file = new File(softwareEventDataDirectory); 
		File[] filelist = file.listFiles();
		
		//we use the following CSV list to store (filtered) different program runs 
		//each csv file is the one program run data of the given component.
		//the structure of the software execution data: package name, class name, Method name, class object, start time, 
		//complete time, caller package name, caller class name, caller method name, caller class object 
				
		HashSet<ArrayList<String>> csvList = new HashSet<ArrayList<String>>();
		String tempString = "";
		String [] tempList; 
		//here each file is a program run. 
		for (File f: filelist)
        {
			ArrayList<String> csv = new ArrayList<String>();
			BufferedReader reader = null;
    		try 
    		{
    			reader = new BufferedReader(new FileReader(f));
    		} 
    		catch (FileNotFoundException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		//read by line, and add to mappingList,
    		try {
    			while ((tempString = reader.readLine()) != null)
    			{
    				if (tempString.trim().length()>0)
    				{
        				tempList = tempString.split(";");
        				
        				//if this line belongs to this component, add it to the csv
        				// here we add those caller or callee classes belongs to the component????????
        				if (com2class.getClasses().contains(tempList[1])|com2class.getClasses().contains(tempList[7]))
        				{
        					csv.add(tempString);
        					System.out.println(tempString);
        				}
    				}
    			}			
    			//close the CSV file reader
    			reader.close();
    			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    		
    		//it is possible to have an empty csv, this means that the current program run does not involves this component.  
        	csvList.add(csv);
        }

		// this method aims to add nesting level attribute to each method call. 
		// main() and method whose caller do not belong to the component will be labeled as 0
		HashSet<ArrayList<String>> csvListwithNesting=AddNestingLevelAttribute.NestingLevelIdentification(csvList, com2class);
		
		//here we suppose each component only has one software event log
		//for other discussion, we refer to future work
		//based on the nesting=0 event, we seperate to differnt logs

		
		
		
		// convert the 
		XLog softwareComponentLog =convert2softwareEventLog(com2class, com2classList, csvListwithNesting);
		
		softwareEventLogArray.addLog(softwareComponentLog);
		
		
		return softwareEventLogArray;
	}
	
	public static XLog convert2softwareEventLog(Component2Classes com2class, ArrayList<Component2Classes> com2classList, HashSet<ArrayList<String>> csvListwithNesting)
	{
		XFactory factory = new XFactoryNaiveImpl();
		// input the log name and a factory.
		XLog softwareLog =InitializeSoftwareEventLog.initialize(factory, com2class.getComponent());
		
		//each component instance is converted to a software event trace
		
		int traceID = 1;
		for (ArrayList<String> instance :csvListwithNesting)
		{
			//each instance is converted to an software trace in the log.
			XTrace tempTrace = factory.createTrace();
			tempTrace.getAttributes().put(XConceptExtension.KEY_NAME, new XAttributeLiteralImpl(XConceptExtension.KEY_NAME, "case"+traceID++));
			
			for (String recording: instance)
			{
				String[] eventAttributes = recording.split(";");
				
				// callee component is stable
				XEvent tempEvent = GeneratingSoftwareEventLog.createSoftwareEvent(factory, eventAttributes, com2class, com2classList);
				tempTrace.add(tempEvent);
			}
			
			softwareLog.add(tempTrace);
		}
		
		// ording the software event log using start time of each method. 
		return OrderingEventsNano.ordering(softwareLog, "Timestamp_Nano_Start");	
	}
	
	
}
