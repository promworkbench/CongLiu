package congliu.processmining.objectusage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

/**
 * Class with the import plugin for a collection of processes
 * 
 * @author Andrea Burattin
 */
@Plugin(
	name = "Import Component to Classes Configuration (.txt)",
	parameterLabels = { "File" },
	returnLabels = { "Imported Component to Classes Configuration" },
	returnTypes = {Component2Classes.class },
	userAccessible = true
)
@UIImportPlugin(
		description = "Import Component to Classes Configuratio (.txt)",//show the type in window for recording files
		extensions = { "txt" }// the suffix name
)

public class ImportComponent2Classes extends AbstractImportPlugin{

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		context.getFutureResult(0).setLabel(filename);
		
		System.out.println("component2class configuration input starts");

		// obtain the mapping from component to classes. 
		return component2Class(input);
	}
	
	public static Component2Classes component2Class(InputStream input)
	{
		Component2Classes mapping = new Component2Classes();
		
		
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new InputStreamReader(input, "UTF-8"));
		} catch (IOException e) {
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
