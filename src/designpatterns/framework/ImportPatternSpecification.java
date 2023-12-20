package designpatterns.framework;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.processmining.contexts.uitopia.annotations.UIImportPlugin;
import org.processmining.framework.abstractplugins.AbstractImportPlugin;
import org.processmining.framework.plugin.PluginContext;
import org.processmining.framework.plugin.annotations.Plugin;
import org.xml.sax.SAXException;

/**
 * import the pattern specifications from xml files to the ProM workspace. 
 * the import file is an xml-based file, we use SAX for parsing. 
 * @author cliu3
 *
 */
@Plugin(
	name = "Import Design Pattern Specification (.xml)",
	parameterLabels = { "File" },
	returnLabels = { "Design Pattern Specification" },
	returnTypes = {DesignPatternSpecification.class},
	userAccessible = true
)
@UIImportPlugin(
		description = "Import Design Pattern Specification (.xml)",//show the type in window for recording files
		extensions = {"xml"}// the suffix name
)

public class ImportPatternSpecification extends AbstractImportPlugin{

	protected Object importFromStream(PluginContext context, InputStream input, String filename, long fileSizeInBytes)
			throws Exception {
		// TODO Auto-generated method stub
		context.getFutureResult(0).setLabel(filename);
		
		System.out.println("design pattern specification importer starts!");

		// import and parse the design pattern specification from the input stream. 
		return importer(input);
	}
	
	public static DesignPatternSpecification importer(InputStream inputStream)
	{

		DesignPatternSpecification designPatternSpecification = new DesignPatternSpecification();
		SAXParser parser = null;  
        try {  
            parser = SAXParserFactory.newInstance().newSAXParser();  
           
            SAXParseDesignPatternSpecification parseXml=new SAXParseDesignPatternSpecification();  
             
            //InputStream stream=new FileInputStream("C:\\Users\\cliu3\\Desktop\\DPDOberserPattern.xml");
            parser.parse(inputStream, parseXml);  
            designPatternSpecification =parseXml.getPatternSpecification();
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
        
        return designPatternSpecification;
	}
}
