package congliu.processmining.softwareprocessmining;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.deckfour.xes.model.XLog;
import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;
import org.processmining.log.models.EventLogArray;
import org.processmining.log.models.impl.EventLogArrayImpl;

/**
 * this plugin aims to convert pre-processed program execution data
 * to software event log, each describing a component behavior
 * 
 * Input: a set of program execution traces， mapping from component to classes (domain knowledge).
 * Output: a set of software event logs.
 * 
 * The main work is to identify component instances, and construct 
 * software event logs.
 * 
 * Step1: input software execution data, and mapping from classes to component
 * step2: filtering software execution data to different subsets.
 * step3: identifying component instances, we use JGraphT to first construct a directed graph and get its weak connected sub-graph.
 * step4: construct software event log for each component. 
 * 【step4:】 we add the nesting level attribute to the software event log, it can be used to help construct HLog and set K-depth discovery. 
 * Step5: an event log array, each describing the behavior of on software component.
 * @author cliu3
 *
 */

@Plugin(
		name = "Identifying Software Event Log for each Component (support component instance)",// plugin name
		
		returnLabels = {"A set of XES Logs (Event Log Array)"}, //return labels
		returnTypes = {EventLogArray.class},//return class
		
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Software Execution Data"},
		
		userAccessible = false,
		help = "This plugin aims to convert software execution data to software event log, each describing a component behavior" 
		)

public class ProgramExecutionData2SoftwareEventLogplugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Identifying Software Event Log for each Component, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {}
			)
	public static EventLogArray formatCSV(UIPluginContext context) throws Exception 
	{
		// the final output of this plugin is a set of event logs, each describing a software component.
		EventLogArray softwareEventLogArray = new EventLogArrayImpl();
		
		//Select the inputs(1) software event data, (2) class2component mapping;
		FileChooserConfiguration softwareEventDataFile = new FileChooserConfiguration();
		FileChooserConfiguration class2componentMappingFile = new FileChooserConfiguration();
		
		new FileChooserPanel(softwareEventDataFile);
		new FileChooserPanel(class2componentMappingFile);
		
		// the file directory is: softwareEventDataFile.getFilename(), class2componentMappingFile.getFilename()
		
		// obtain the mapping from component to classes. 
		ArrayList<Component2Classes> c2c =component2Class(class2componentMappingFile.getFilename());
		
		// for each component, we identifying its software event log
		for (Component2Classes com2Class: c2c)
		{
			//obtain the software event log for each component.
			// input： (1) classes of this one specific component (2) class2component mapping; and 
			//(3)software execution data
			XLog softwareComponentLog = GeneratingSoftwareEventLog.generatingSoftwareEventLog(com2Class,c2c,softwareEventDataFile.getFilename());
			
//			//serialization the current XESlog to disk
//			try {
//				FileOutputStream fosgz = new FileOutputStream("D:\\[6]\\making examples\\software event logs\\"+
//						softwareComponentLog.getAttributes().get(XConceptExtension.KEY_NAME)+".xes"); 
//				//FileOutputStream fos = new FileOutputStream("D:\\KiekerData\\CaseStudy001\\EnrichedMethodLevelLog.xes.gz"); 
//				
//				new XesXmlSerializer().serialize(softwareComponentLog, fosgz); 
//	            // serialize to xes.gz
//				//new XesXmlGZIPSerializer().serialize(log, fosgz);
//	
//				fosgz.close();
//				
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} 
			
			//add to the final software event log list
			softwareEventLogArray.addLog(softwareComponentLog);
		}		
		
		// return the final results. 
		return softwareEventLogArray;
	}
	
	//read in the class to component mapping
	public static ArrayList<Component2Classes> component2Class(String componentfiledirectory)
	{
		ArrayList<Component2Classes> mapping = new ArrayList<Component2Classes>();
		
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(componentfiledirectory));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String tempString="";
		String [] tempList;	
		
		//read by line, and add to mappingList,
		try {
			while ((tempString = reader.readLine()) != null)
			{
				Component2Classes c2c = new Component2Classes();
				
				// each line is composed of one component name and a set of classes
				tempList = tempString.split(";");
				
				//set the component part
				c2c.setComponent(tempList[0]);
				
				//construct the class list
				ArrayList<String> classes = new ArrayList<String>();
				for (int i=1; i<tempList.length;i++)
				{
					classes.add(tempList[i]);
				}
				
				//add the class list to 
				c2c.setClasses(classes);

				mapping.add(c2c);
			}			
			//close the CSV file reader
			reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return mapping;
	}
	
}
