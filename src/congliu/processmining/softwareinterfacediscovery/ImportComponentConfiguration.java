package congliu.processmining.softwareinterfacediscovery;

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

import observerpatterndiscovery.ClassClass;

/**
 * this import plug-in aims to import the configuration of components. 
 * 
 * example input:
 * Starter;BookstoreExample.BookstoreStarter
 * OnlineBookStore;BookstoreExample.Catalog;BookstoreExample.BookSeller;BookstoreExample.Bookstore
 * OrderAndDelivery;BookstoreExample.Order;BookstoreExample.Delivery
 * @author Cong LIU 
 */
@Plugin(
	name = "Import Component Configuration (.conf)",
	parameterLabels = { "File" },
	returnLabels = {"Imported Component Configuration" },
	returnTypes = {ComponentConfig.class },
	userAccessible = true
)
@UIImportPlugin(
		description = "Import Component Configuratio (.conf)",//show the type in window for recording files
		extensions = { "conf" }// the suffix name
)
public class ImportComponentConfiguration extends AbstractImportPlugin{
	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		context.getFutureResult(0).setLabel(filename);
		
		System.out.println("Component configuration importing starts");

		// obtain the mapping from component to classes. 
		return componentConfig(input);
	}
	
	public static ComponentConfig componentConfig(InputStream input)
	{
		ComponentConfig config = new ComponentConfig();
		
		
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
				// each line is composed of one component name and a set of classes, the component name is the first and the ca
				tempList = tempString.split(";");
				
				//construct the class list
				Set<ClassClass> classes = new HashSet<ClassClass>();
				for (int i=1; i<tempList.length;i++)
				{
					ClassClass c = new ClassClass();
					c.setClassName(extractClass(tempList[i]));
					c.setPackageName(extractPackage(tempList[i]));
					classes.add(c);
				}
				
				//set the component part
				config.add(tempList[0],  classes);
			}			
			//close the CSV file reader
			reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return config;
	}
	
	//get the class part, sample input "CH.ifa.draw.standard.StandardDrawingView"
	public static String extractClass(String s)
	{
		String args[]=s.split("\\.");	
		
		return args[args.length-1];
	}
	
	//get the package part sample input "CH.ifa.draw.standard.StandardDrawingView"
	public static String extractPackage(String s)
	{
		String args[]=s.split("\\.");	
		
		String Package = args[0];
		for (int i=1;i<args.length-1;i++)
		{
			Package=Package+"."+args[i];
		}
		return Package;
	}
}
