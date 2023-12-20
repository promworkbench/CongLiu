package designpatterns.observerpattern;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.xml.sax.SAXException;

import observerpatterndiscovery.ClassClass;
import observerpatterndiscovery.MethodClass;


/**
 * import the candidate observer patterns from xml files to the Prom workspace. 
 * the import file is an xml-based file, we use SAX for parsing. 
 * @author cliu3
 *
 */
@Plugin(
	name = "Import Candidate Observer Patterns (.xml)",
	parameterLabels = { "File" },
	returnLabels = {"Candidate Observer Pattern Instances"},
	returnTypes = {ObserverPatternSet.class},
	userAccessible = true
)
@UIImportPlugin(
		description = "Import Candidate Observer Patterns (.xml)",//show the type in window for recording files
		extensions = {"xml"}// the suffix name
)

public class ImportCandidateObserverPatterns extends AbstractImportPlugin{

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		context.getFutureResult(0).setLabel(filename);
		
		System.out.println("candidate observer pattern importer starts!");

		// import and parse the design pattern instance from the input stream. 
		return importer(input);
	}
	
	public static ObserverPatternSet importer(InputStream inputStream)
	{
		/*
		 * each observerCandidate may contain several candidate pattern instances, 
		 * i.e., multiple notifies, according to our design pattern instance definition.
		 */
		HashSet<ObserverCandidate> observers = new HashSet<>();
		SAXParser parser = null;  
        try {  
            parser = SAXParserFactory.newInstance().newSAXParser();  
           
            SAXParseXML parseXml=new SAXParseXML();  
             
            //InputStream stream=new FileInputStream("C:\\Users\\cliu3\\Desktop\\DPDOberserPattern.xml");
            parser.parse(inputStream, parseXml);  
            observers =parseXml.getObservers();
//            for(ObserverCandidate o: observers)
//            {
//            	System.out.println("observer: "+ o.getObserver());
//            	System.out.println("subject: "+ o.getSubject());
//            	System.out.println("notifys: "+ o.getNotifySet());
//            }
          
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
        ObserverPatternSet observerset = new ObserverPatternSet();
		for(ObserverCandidate observerC: observers)
		{
			ClassClass observerClass = new ClassClass();
			observerClass.setClassName(extractClass(observerC.getObserver()));
			observerClass.setPackageName(extractPackage(observerC.getObserver()));
			ClassClass subjectClass = new ClassClass();
			subjectClass.setClassName(extractClass(observerC.getSubject()));
			subjectClass.setPackageName(extractPackage(observerC.getSubject()));
			
			//for each notify, we construct a pattern instance
			for(String notify: observerC.getNotifySet())
			{
				ObserverPatternClass o = new ObserverPatternClass();
				//set pattern name
				o.setPatternName("Observer Pattern");
				//add subject, Class type
				o.setListernerClass(observerClass);
				//add observer, Class type 
				o.setSubjectClass(subjectClass);
				
				//create notify method, Method type
				MethodClass notifyMethod = new MethodClass();
				notifyMethod.setPackageName(extractPackage(observerC.getSubject()));
				notifyMethod.setClassName(extractClass(observerC.getSubject()));
				notifyMethod.setMethodName(extractMethod(notify));
				notifyMethod.setParameterSet(extractParameters(notify));
				
				//add the notify method
				o.setNotifyMethod(notifyMethod);
				observerset.add(o);
			}
		}
		return observerset;
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
	
	//get the method part, sample input "CH.ifa.draw.standard.StandardDrawingView::moveSelection(int, int):void"
	public static String extractMethod(String s)
	{
		String args[]=s.split("\\(");	
		String args1[] =args[0].split("\\::");
		
		return args1[args1.length-1]+"()";
	}
	
	//get the parameter set, sample input "CH.ifa.draw.standard.StandardDrawingView::moveSelection(int, int):void"
	public static Set<String> extractParameters(String s)
	{
		String args[]=s.split("\\(");	
		String args1[] =args[1].split("\\)");
		String[] args2 =args1[0].split("\\,");
		return new HashSet<String>(Arrays.asList(args2));
	}
}
