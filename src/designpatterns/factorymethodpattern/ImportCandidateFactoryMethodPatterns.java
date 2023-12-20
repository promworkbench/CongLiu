package designpatterns.factorymethodpattern;

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
 * import the candidate factory method patterns from xml files to the Prom workspace. 
 * the import file is an xml-based file, we use SAX for parsing. 
 * @author cliu3
 *
 */
@Plugin(
	name = "Import Candidate Factory Method Patterns (.xml)",
	parameterLabels = { "File" },
	returnLabels = {"Candidate Factory Method Pattern Instances"},
	returnTypes = {FactoryMethodPatternSet.class},
	userAccessible = false
)
@UIImportPlugin(
		description = "Import Candidate Factory Method Patterns (.xml)",//show the type in window for recording files
		extensions = {"xml"}// the suffix name
)
public class ImportCandidateFactoryMethodPatterns extends AbstractImportPlugin{

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		context.getFutureResult(0).setLabel(filename);
		
		System.out.println("candidate factory method pattern importer starts!");

		// import and parse the design pattern instance from the input stream. 
		return importer(input);
	}
	
	
	public static FactoryMethodPatternSet importer(InputStream inputStream)
	{
		/*
		 * each factory method Candidate may contain several candidate pattern instances, 
		 * i.e., multiple factory methods, according to our design pattern instance definition.
		 */
		HashSet<FactoryMethodCandidate> factorys = new HashSet<>();
		SAXParser parser = null;  
        try {  
            parser = SAXParserFactory.newInstance().newSAXParser();  
           
            SAXParseXML parseXml=new SAXParseXML();  
             
            //InputStream stream=new FileInputStream("C:\\Users\\cliu3\\Desktop\\DPDOberserPattern.xml");
            parser.parse(inputStream, parseXml);  
            factorys =parseXml.getFactorys();
          
//            System.out.println(adapters);
            
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
        FactoryMethodPatternSet factorymethodset = new FactoryMethodPatternSet();
        for(FactoryMethodCandidate factoryC: factorys)
        {
        	ClassClass creatorClass = new ClassClass();
        	creatorClass.setClassName(ImportCandidateObserverPatterns.extractClass(factoryC.getCreator()));
        	creatorClass.setPackageName(ImportCandidateObserverPatterns.extractPackage(factoryC.getCreator()));
        	
            	
        	//for each factory method, we construct a pattern instance
        	for(String factoryMethod: factoryC.getFactoryMethodSet())
        	{
        		FactoryMethodPatternClass f = new FactoryMethodPatternClass();
        		//set the pattern name
        		f.setPatternName("Factory Method Pattern");
        		//set the creator role
        		f.setCreator(creatorClass);
        		
        		
        		//create factory methods
        		MethodClass factoryM = new MethodClass();
        		factoryM.setPackageName(ImportCandidateObserverPatterns.extractPackage(factoryC.getCreator()));
        		factoryM.setClassName(ImportCandidateObserverPatterns.extractClass(factoryC.getCreator()));
        		factoryM.setMethodName(ImportCandidateObserverPatterns.extractMethod(factoryMethod));
        		factoryM.setParameterSet(ImportCandidateObserverPatterns.extractParameters(factoryMethod));
        		
          		f.setFactoryMethod(factoryM);
          		factorymethodset.add(f);
        	}
        }
		return factorymethodset;
	}
}
