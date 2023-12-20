package designpatterns.adapterpattern;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;

import observerpatterndiscovery.ClassClass;

/**
 * this import plug-in aims to import the class type hierarchy of the class-level role detected by static tool. 
 * 
 * example input:
 * ObserverPattern.SMSUsers;ObserverPattern.Observer
 * ObserverPattern.CommentaryObject;ObserverPattern.Subject
 * @author Cong LIU 
 */
@Plugin(
	name = "Import Class Type Hierarchy (.cth)",
	parameterLabels = { "File" },
	returnLabels = {"Imported Class Type Hierarchy" },
	returnTypes = {ClassTypeHierarchy.class },
	userAccessible = true
)
@UIImportPlugin(
		description = "Import the class type hierarchy (.cth)",//show the type in window for recording files
		extensions = { "cth" }// the suffix name
)
public class ImportClassTypeHierarchy extends AbstractImportPlugin{
	
	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		context.getFutureResult(0).setLabel(filename);
		
		System.out.println("Import class type hierarchy starts");

		// construct the ClassTypeHierarchy 
		return construct(input);
	}
	
	public static ClassTypeHierarchy construct(InputStream input)
	{
		ClassTypeHierarchy cth = new ClassTypeHierarchy();
		
		
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
				// each line is composed of a set of classes of the same type hierarchy
				tempList = tempString.split(";");
				
				//construct the class set of the same type hierarchu
				HashSet<ClassClass> classes = new HashSet<ClassClass>();
				for (int i=0; i<tempList.length;i++)
				{
					ClassClass c = new ClassClass();
					c.setClassName(extractClass(tempList[i]));
					c.setPackageName(extractPackage(tempList[i]));
					classes.add(c);
				}
				
				//set the component part
				cth.addCTH(classes);
			}			
			//close the CSV file reader
			reader.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return cth;
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
