package designpatterns.visitorpattern;

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
 * import the candidate visitor patterns from xml files to the Prom workspace. 
 * the import file is an xml-based file, we use SAX for parsing. 
 * @author cliu3
 *
 */

@Plugin(
		name = "Import Candidate Visitor Patterns (.xml)",
		parameterLabels = { "File" },
		returnLabels = { "Candidate Visitor Pattern Instances" },
		returnTypes = {VisitorPatternSet.class},
		userAccessible = true
	)
	@UIImportPlugin(
			description = "Import Candidate Visitor Patterns (.xml)",//show the type in window for recording files
			extensions = {"xml"}// the suffix name
	)
public class ImportCandidateVisitorPatterns extends AbstractImportPlugin{
	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		context.getFutureResult(0).setLabel(filename);
		
		System.out.println("candidate visitor pattern importer starts!");

		// import and parse the design pattern instance from the input stream. 
		return visitorPatternImporter(input);
	}
	
	public static VisitorPatternSet visitorPatternImporter(InputStream inputStream)
	{
		HashSet<VisitorCandidate> visitors = new HashSet<>();
		SAXParser parser = null;  
        try {  
            parser = SAXParserFactory.newInstance().newSAXParser();  
           
            SAXParseXML parseXml=new SAXParseXML();  
             
            //InputStream stream=new FileInputStream("C:\\Users\\cliu3\\Desktop\\DPDOberserPattern.xml");
            parser.parse(inputStream, parseXml);  
            visitors =parseXml.getVisitors();
            
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
        VisitorPatternSet visitorset = new VisitorPatternSet();
		for(VisitorCandidate visitorC: visitors)
		{
			ClassClass elementClass = new ClassClass();
			elementClass.setClassName(ImportCandidateObserverPatterns.extractClass(visitorC.getElement()));
			elementClass.setPackageName(ImportCandidateObserverPatterns.extractPackage(visitorC.getElement()));
			
			
			ClassClass visitorClass = new ClassClass();
			visitorClass.setClassName(ImportCandidateObserverPatterns.extractClass(visitorC.getVisitor()));
			visitorClass.setPackageName(ImportCandidateObserverPatterns.extractPackage(visitorC.getVisitor()));
			
			
			//for each accept, we construct a pattern instance
			for(String accept: visitorC.getAcceptSet())
			{
				VisitorPatternClass v = new VisitorPatternClass();
				//set pattern name
				v.setPatternName("Visitor Pattern");
				v.setElement(elementClass);
				v.setVisitor(visitorClass);
				
				//create accept method, Method type
				MethodClass acceptMethod = new MethodClass();
				acceptMethod.setPackageName(ImportCandidateObserverPatterns.extractPackage(visitorC.getElement()));
				acceptMethod.setClassName(ImportCandidateObserverPatterns.extractClass(visitorC.getElement()));
				acceptMethod.setMethodName(ImportCandidateObserverPatterns.extractMethod(accept));
				v.setAccept(acceptMethod);
				visitorset.add(v);
			}
				
		}
		return visitorset;
	}
}
