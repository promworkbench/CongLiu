package designpatterns.commandpattern;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.xml.sax.SAXException;

import designpatterns.observerpattern.ImportCandidateObserverPatterns;
import designpatterns.observerpattern.SAXParseXML;
import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;

/**
 * import the candidate command patterns from xml files to the Prom workspace. 
 * the import file is an xml-based file, we use SAX for parsing. 
 * @author cliu3
 *
 */

@Plugin(
		name = "Import Candidate Command Patterns (.xml)",
		parameterLabels = {"File"},
		returnLabels = {"Candidate Command Pattern Instances"},
		returnTypes = {CommandPatternSet.class},
		userAccessible = true
	)
	@UIImportPlugin(
			description = "Import Candidate Command Patterns (.xml)",//show the type in window for recording files
			extensions = {"xml"}// the suffix name
	)
public class ImportCandidateCommandPatterns extends AbstractImportPlugin{
	
	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		context.getFutureResult(0).setLabel(filename);
		
		System.out.println("candidate command pattern importer starts!");

		// import and parse the design pattern instance from the input stream. 
		return commandPatternImporter(input);
	}
	
	public static CommandPatternSet commandPatternImporter(InputStream inputStream)
	{
		HashSet<CommandCandidate> commands = new HashSet<>();
		SAXParser parser = null;  
        try {  
            parser = SAXParserFactory.newInstance().newSAXParser();  
           
            SAXParseXML parseXml=new SAXParseXML();  
             
            //InputStream stream=new FileInputStream("C:\\Users\\cliu3\\Desktop\\DPDOberserPattern.xml");
            parser.parse(inputStream, parseXml);  
            commands =parseXml.getCommands();
            
        } catch (ParserConfigurationException e) {  
            e.printStackTrace();  
        } catch (SAXException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
    
        
        /*
         * in the following, we parse each candidate pattern and construct the corresponding candidate pattern instances
         */
        CommandPatternSet commandset = new CommandPatternSet();
		for(CommandCandidate commandC: commands)
		{			
			//set the command class role
			ClassClass commandClass = new ClassClass();
			commandClass.setClassName(ImportCandidateObserverPatterns.extractClass(commandC.getCommand()));
			commandClass.setPackageName(ImportCandidateObserverPatterns.extractPackage(commandC.getCommand()));
			
			//set the receiver class role
			ClassClass receiverClass = new ClassClass();
			receiverClass.setClassName(ImportCandidateObserverPatterns.extractClass(commandC.getReceiver()));
			receiverClass.setPackageName(ImportCandidateObserverPatterns.extractPackage(commandC.getReceiver()));
			
			
			//for each execute, we construct a pattern instance
			for(String execute: commandC.getExecuteSet())
			{
				CommandPatternClass c = new CommandPatternClass();
				//set pattern name
				c.setPatternName("Command Pattern");
				c.setCommand(commandClass);
				c.setReceiver(receiverClass);
				
				//create execute method, Method type
				MethodClass executeMethod = new MethodClass();
				executeMethod.setPackageName(ImportCandidateObserverPatterns.extractPackage(commandC.getCommand()));
				executeMethod.setClassName(ImportCandidateObserverPatterns.extractClass(commandC.getCommand()));
				executeMethod.setMethodName(ImportCandidateObserverPatterns.extractMethod(execute));
				c.setExecute(executeMethod);
				commandset.add(c);
			}
				
		}
		return commandset;
	}

}
