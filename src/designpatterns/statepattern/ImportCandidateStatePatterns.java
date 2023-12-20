package designpatterns.statepattern;

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
 * import the candidate state patterns from xml files to the Prom workspace. 
 * the import file is an xml-based file, we use SAX for parsing. 
 * @author cliu3
 *
 */

@Plugin(
		name = "Import Candidate State Patterns (.xml)",
		parameterLabels = { "File" },
		returnLabels = { "Candidate State Pattern Instances" },
		returnTypes = {StatePatternSet.class},
		userAccessible = true
	)
	@UIImportPlugin(
			description = "Import Candidate State Patterns (.xml)",//show the type in window for recording files
			extensions = {"xml"}// the suffix name
	)
public class ImportCandidateStatePatterns extends AbstractImportPlugin{
	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		context.getFutureResult(0).setLabel(filename);
		
		System.out.println("candidate state pattern importer starts!");

		// import and parse the design pattern instance from the input stream. 
		return statePatternImporter(input);
	}
	
	public static StatePatternSet statePatternImporter(InputStream inputStream)
	{
		HashSet<StateCandidate> states = new HashSet<>();
		SAXParser parser = null;  
        try {  
            parser = SAXParserFactory.newInstance().newSAXParser();  
           
            SAXParseXML parseXml=new SAXParseXML();  
             
            //InputStream stream=new FileInputStream("C:\\Users\\cliu3\\Desktop\\DPDOberserPattern.xml");
            parser.parse(inputStream, parseXml);  
            states =parseXml.getStates();
            
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
        StatePatternSet stateset = new StatePatternSet();
		for(StateCandidate stateC: states)
		{
			
			
			ClassClass contextClass = new ClassClass();
			contextClass.setClassName(ImportCandidateObserverPatterns.extractClass(stateC.getContext()));
			contextClass.setPackageName(ImportCandidateObserverPatterns.extractPackage(stateC.getContext()));
			
			
			ClassClass stateClass = new ClassClass();
			stateClass.setClassName(ImportCandidateObserverPatterns.extractClass(stateC.getState()));
			stateClass.setPackageName(ImportCandidateObserverPatterns.extractPackage(stateC.getState()));
			
			
			//for each request, we construct a pattern instance
			for(String request: stateC.getRequestSet())
			{
				StatePatternClass s = new StatePatternClass();
				//set pattern name
				s.setPatternName("State Pattern");
				s.setContext(contextClass);
				s.setState(stateClass);
				
				//create request method, Method type
				MethodClass requestMethod = new MethodClass();
				requestMethod.setPackageName(ImportCandidateObserverPatterns.extractPackage(stateC.getContext()));
				requestMethod.setClassName(ImportCandidateObserverPatterns.extractClass(stateC.getContext()));
				requestMethod.setMethodName(ImportCandidateObserverPatterns.extractMethod(request));
				s.setRequest(requestMethod);
				stateset.add(s);
			}
				
		}
		return stateset;
	}

}
