package congliu.processmining.softwarecomponentbehaviorNew;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.processmining.contexts.uitopia.UIPluginContext;
import org.processmining.contexts.uitopia.annotations.UITopiaVariant;
import org.processmining.framework.connections.ConnectionCannotBeObtained;
import org.processmining.framework.plugin.annotations.Plugin;
import org.processmining.framework.plugin.annotations.PluginVariant;

import congliu.processmining.objectusage.Component2Classes;
import congliu.processmining.softwareprocessmining.FileChooserConfiguration;
import congliu.processmining.softwareprocessmining.FileChooserPanel;

/**
 * this plugin aims to import the component to classes information from the file. 
 * @author cliu3
 *
 */
@Plugin(
		name = "Import External Component to Classes Configurations",// plugin name
		
		returnLabels = {"Component to Classes Configurations"}, //return labels
		returnTypes = {Component2Classes.class},//return class, a set of component to hpns 
		
		//input parameter labels, corresponding with the second parameter of main function
		parameterLabels = {"Component to Class Mapping"},
		
		userAccessible = true,
		help = "This plugin aims to import the component to class mappings."
		)
public class ImportComponent2ClassesPlugin {
	@UITopiaVariant(
	        affiliation = "TU/e", 
	        author = "Cong liu", 
	        email = "c.liu.3@tue.nl OR liucongchina@163.com"
	        )
	@PluginVariant(
			variantLabel = "Import External Component to Classes Configurations, default",
			// the number of required parameters, {0} means one input parameter
			requiredParameterLabels = {}
			)
	public Component2Classes importComponent2Classes(UIPluginContext context) throws ConnectionCannotBeObtained 
	{
		Component2Classes c2cs =new Component2Classes();
		
		//Select the input2: class2component mapping;
		FileChooserConfiguration class2componentMappingFile = new FileChooserConfiguration();
		new FileChooserPanel(class2componentMappingFile);
		
		// obtain the mapping from component to classes. 
		return component2Class(class2componentMappingFile.getFilename());
	}
	
	//read in the class to component mapping
	public static Component2Classes component2Class(String componentfiledirectory)
	{
		Component2Classes mapping = new Component2Classes();
		
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
				// each line is composed of one component name and a set of classes
				tempList = tempString.split(";");
				
				//construct the class list
				Set<String> classes = new HashSet<String>();
				for (int i=1; i<tempList.length;i++)
				{
					classes.add(tempList[i]);
				}
				
				//set the component part
				mapping.add(tempList[0],  classes);
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
