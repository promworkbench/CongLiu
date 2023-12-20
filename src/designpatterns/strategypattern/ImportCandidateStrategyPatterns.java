package designpatterns.strategypattern;

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
 * import the candidate strategy patterns from xml files to the Prom workspace. 
 * the import file is an xml-based file, we use SAX for parsing. 
 * @author cliu3
 *
 */

@Plugin(
		name = "Import Candidate Strategy Patterns (.xml)",
		parameterLabels = { "File" },
		returnLabels = { "Candidate Strategy Pattern Instances" },
		returnTypes = {StrategyPatternSet.class},
		userAccessible = true
	)
	@UIImportPlugin(
			description = "Import Candidate Strategy Patterns (.xml)",//show the type in window for recording files
			extensions = {"xml"}// the suffix name
	)
public class ImportCandidateStrategyPatterns extends AbstractImportPlugin{
	
	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		context.getFutureResult(0).setLabel(filename);
		
		System.out.println("candidate strategy pattern importer starts!");

		// import and parse the design pattern instance from the input stream. 
		return strategyPatternImporter(input);
	}
	
	public static StrategyPatternSet strategyPatternImporter(InputStream inputStream)
	{
		HashSet<StrategyCandidate> strategies = new HashSet<>();
		SAXParser parser = null;  
        try {  
            parser = SAXParserFactory.newInstance().newSAXParser();  
           
            SAXParseXML parseXml=new SAXParseXML();  
             
            //InputStream stream=new FileInputStream("C:\\Users\\cliu3\\Desktop\\DPDOberserPattern.xml");
            parser.parse(inputStream, parseXml);  
            strategies =parseXml.getStrategies();
            
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
        StrategyPatternSet strategyset = new StrategyPatternSet();
		for(StrategyCandidate strategyC: strategies)
		{
			ClassClass contextClass = new ClassClass();
			contextClass.setClassName(ImportCandidateObserverPatterns.extractClass(strategyC.getContext()));
			contextClass.setPackageName(ImportCandidateObserverPatterns.extractPackage(strategyC.getContext()));
			
			
			ClassClass strategyClass = new ClassClass();
			strategyClass.setClassName(ImportCandidateObserverPatterns.extractClass(strategyC.getStrategy()));
			strategyClass.setPackageName(ImportCandidateObserverPatterns.extractPackage(strategyC.getStrategy()));
			
			
			//for each contextInterface, we construct a pattern instance
			for(String contextInterface: strategyC.getContextInterfaceSet())
			{
				StrategyPatternClass s = new StrategyPatternClass();
				//set pattern name
				s.setPatternName("Strategy Pattern");
				s.setContext(contextClass);
				s.setStrategy(strategyClass);
				
				//create contextInterface method, Method type
				MethodClass contextInterfaceMethod = new MethodClass();
				contextInterfaceMethod.setPackageName(ImportCandidateObserverPatterns.extractPackage(strategyC.getContext()));
				contextInterfaceMethod.setClassName(ImportCandidateObserverPatterns.extractClass(strategyC.getContext()));
				contextInterfaceMethod.setMethodName(ImportCandidateObserverPatterns.extractMethod(contextInterface));
				s.setContextInterface(contextInterfaceMethod);
				strategyset.add(s);
			}
				
		}
		return strategyset;
	}

		
}
