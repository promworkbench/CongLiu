package designpatterns.adapterpattern;

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
 * import the candidate adapter patterns from xml files to the Prom workspace. 
 * the import file is an xml-based file, we use SAX for parsing. 
 * @author cliu3
 *
 */
@Plugin(
	name = "Import Candidate Adapter Patterns (.xml)",
	parameterLabels = { "File" },
	returnLabels = {"Candidate Adapter Pattern Instances"},
	returnTypes = {AdapterPatternSet.class},
	userAccessible = false
)
@UIImportPlugin(
		description = "Import Candidate Adapter Patterns (.xml)",//show the type in window for recording files
		extensions = {"xml"}// the suffix name
)
public class ImportCandidateAdapterPatterns extends AbstractImportPlugin{

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		context.getFutureResult(0).setLabel(filename);
		
		System.out.println("candidate adapter pattern importer starts!");

		// import and parse the design pattern instance from the input stream. 
		return importer(input);
	}

	public static AdapterPatternSet importer(InputStream inputStream)
	{
		/*
		 * each adapterCandidate may contain several candidate pattern instances, 
		 * i.e., multiple request, according to our design pattern instance definition.
		 */
		HashSet<AdapterCandidate> adapters = new HashSet<>();
		SAXParser parser = null;  
        try {  
            parser = SAXParserFactory.newInstance().newSAXParser();  
           
            SAXParseXML parseXml=new SAXParseXML();  
             
            //InputStream stream=new FileInputStream("C:\\Users\\cliu3\\Desktop\\DPDOberserPattern.xml");
            parser.parse(inputStream, parseXml);  
            adapters =parseXml.getAdapters();
          
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
        AdapterPatternSet adapterset = new AdapterPatternSet();
        for(AdapterCandidate adapterC: adapters)
        {
        	ClassClass adapterClass = new ClassClass();
        	adapterClass.setClassName(ImportCandidateObserverPatterns.extractClass(adapterC.getAdapter()));
        	adapterClass.setPackageName(ImportCandidateObserverPatterns.extractPackage(adapterC.getAdapter()));
        	
        	ClassClass adapteeClass = new ClassClass();
        	adapteeClass.setClassName(ImportCandidateObserverPatterns.extractClass(adapterC.getAdaptee()));
        	adapteeClass.setPackageName(ImportCandidateObserverPatterns.extractPackage(adapterC.getAdaptee()));
        	
        	//for each request, we construct a pattern instance
        	for(String request: adapterC.getRequestSet())
        	{
        		AdapterPatternClass a = new AdapterPatternClass();
        		//set the pattern name
        		a.setPatternName("(Object)Adapter Pattern");
        		//set the adpater and the adaptee
        		a.setAdapterClass(adapterClass);
        		a.setAdapteeClass(adapteeClass);
        		
        		//create request method
        		MethodClass requestMethod = new MethodClass();
        		requestMethod.setPackageName(ImportCandidateObserverPatterns.extractPackage(adapterC.getAdapter()));
        		requestMethod.setClassName(ImportCandidateObserverPatterns.extractClass(adapterC.getAdapter()));
        		requestMethod.setMethodName(ImportCandidateObserverPatterns.extractMethod(request));
        		requestMethod.setParameterSet(ImportCandidateObserverPatterns.extractParameters(request));
        		
          		a.setRequestMethod(requestMethod);
        		adapterset.add(a);
        	}
        }
		return adapterset;
	}
        
    
}
